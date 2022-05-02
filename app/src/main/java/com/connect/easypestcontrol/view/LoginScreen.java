package com.connect.easypestcontrol.view;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class LoginScreen extends AppCompatActivity {
    ImageButton login;
    EditText username,password;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getpermission();

        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);

        login=findViewById(R.id.login);
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
       /* username.setText("todaytest");
        password.setText("todaytest");*/



        login.setOnClickListener(v -> {
            String log_user=username.getText().toString().trim();
            if (!isConnected()) {
                showInternetDialog();
            }else {
                if (log_user.isEmpty()){
                    username.setError("Username required");
                    username.requestFocus();
                }else {
                    logincheck(log_user);
                   /* LongOperation lo=new LongOperation();
                    lo.execute("");*/
                }
            }
        });
    }


    //// Permissions
    private void getpermission() {
        Dexter.withContext(LoginScreen.this).withPermissions(Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
    /// End

    //// Login checks

    public void logincheck(String usernames) {
        try {
            final String username = usernames;
            final String passwords = password.getText().toString().trim();

            Helper.loading(LoginScreen.this);

            JSONObject job = new JSONObject();
            job.put("username",username);
            job.put("password",passwords);
            Log.e("JOB:",job.toString());


            try {
                //Helper.loading(LoginScreen.this);
                RequestQueue requestQueue = Volley.newRequestQueue(LoginScreen.this);
                JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Helper.login_url,
                        job,
                        response -> {
                            try{
                                Helper.mProgressBarHandler.hide();
                                Log.e("res ",response.toString());
                                JSONObject obj = new JSONObject(response.toString());
                                Log.e("jobs ",obj.toString());
                                if(!obj.isNull("error")) {
                                    if (obj.getString("error").equals("Please enter a valid username") || response.toString().equals("Please enter a valid password")) {
                                        Toast.makeText(LoginScreen.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else{
                                    String dol_user_name        = obj.getString("username");
                                    String dol_emp_name         = obj.getString("empName");
                                    String dol_user_mobileNumber       = obj.getString("mobileNumber");

                                    String dol_user_accessToken   = obj.getString("accessToken");

                                    JSONObject obj1=obj.getJSONObject("roleManagement");
                                    String dol_user_role = obj1.getString("role");

                                    JSONArray obj2=obj.getJSONArray("branch");
                                    String dol_user_branch          = obj2.get(0).toString();

                                    SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
                                    ed.putString("dol_user_name", dol_user_name);
                                    ed.putString("dol_emp_name", dol_emp_name);
                                    ed.putString("dol_user_mobileNumber", dol_user_mobileNumber);
                                    ed.putString("dol_user_accessToken", dol_user_accessToken);
                                    ed.putString("dol_user_role",dol_user_role);
                                    ed.putString("dol_user_branch",dol_user_branch);

                                    ed.putBoolean("FIRSTTIME_LOGIN", true);
                                    ed.apply();

                                    if (dol_user_role.equals("Service Engineer")) {
                                        startActivity(new Intent(LoginScreen.this, HomeScreenService.class));
                                    }else {
                                        startActivity(new Intent(LoginScreen.this, HomeScreenSales.class));
                                    }
                                    finish();
                                    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_out_left);
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                                Helper.mProgressBarHandler.hide();
                            }
                        },
                        error -> {
                            try {
                                Helper.mProgressBarHandler.hide();
                               // Log.e("err",error.getMessage().toString());
                                NetworkResponse response = error.networkResponse;
                                if (error instanceof ServerError && response != null) {
                                    try {
                                        String res = new String(response.data,
                                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                        // Now you can use any deserializer to make sense of data
                                        JSONObject obj = new JSONObject(res);
                                        Log.e("res",res+"..");
                                        Toast.makeText(LoginScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                                    } catch (UnsupportedEncodingException e1) {
                                        // Couldn't properly decode data to string
                                        e1.printStackTrace();
                                    } catch (JSONException e2) {
                                        // returned data is not JSONObject?
                                        e2.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );

                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonArrayRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }


            /*StringRequest stringRequest = new StringRequest(Request.Method.POST,Helper.login_url,
                    response -> {
                        Helper.mProgressBarHandler.hide();
                        Log.e("logincheck--", response);
                        try {
                            if(response.toString().trim().equals("INVALID")) {
                                Toast.makeText(LoginScreen.this, "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Helper.mProgressBarHandler.hide();
                        }

                    },
                    error -> {
                        try {
                            Toast.makeText(LoginScreen.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            Helper.mProgressBarHandler.hide();
                            Log.e("err", error.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", passwords);
                    //params.put("token", token);
                    Log.e("par",params.toString());
                    return params;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                   // headers.put("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYyMzJlYjYzMzVlZTEwOTJhMmQ2YWFhNSIsInJvbGVNYW5hZ2VtZW50Ijp7InJvbGUiOiJCb3RoIn0sImlhdCI6MTY0ODQ1Mjk3NywiZXhwIjoxNjc5OTg4OTc3fQ.rDFhxnrO41usuSSrxgzmdxgl3288iJ9ArCiFRs1drc0");
                    return headers;
                }
            };
            VolleySingleton.getInstance(LoginScreen.this).addToRequestQueue(stringRequest);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /// End
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }



    //// Show no internet
    private void showInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.no_internet_dialog,null);
        builder.setCancelable(false);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        view.findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    alertDialog.show();
                } else {
                    alertDialog.dismiss();
                }
            }
        });

        alertDialog.show();
    }
    /// check network
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }
    ////end


/*
    public class LongOperation extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            String current="";

            try {
                URL url;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL(Helper.login_url);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    InputStreamReader isw = new InputStreamReader(in);
                    int data = isw.read();
                    while (data != -1) {
                        current += (char) data;
                        data = isw.read();
                        System.out.print(current);
                    }
                    Log.d("datalength",""+current.length());
                    // return the data to onPostExecute method
                    return current;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return current;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("data", s.toString());
        }
    }
*/

}