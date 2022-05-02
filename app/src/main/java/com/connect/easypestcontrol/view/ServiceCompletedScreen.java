package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.connect.easypestcontrol.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServiceCompletedScreen extends AppCompatActivity {
    ImageView back;
    TextView title;
    FloatingActionButton fb;
    AutoCompleteTextView sales_man,service_man,lead_status,service_status,status_date,lead_status_date;
    EditText cus_name,cus_num,cus_email,lead_source,lead_date,lead_branch,service_type,notes;
    Button download;
    String lead_detail,lead_id,lead_sts_dt,service,sales,ser_mn,sts,sts_dt,nt,lead_sts,sales_id,ser_mn_id;
    String dol_user_accessToken;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_completed_screen);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title_txt);
        title.setText("Completed Service");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
        lead_detail=Helper.sharedpreferences.getString("Completed_Details","");
        Log.e("Details","Show:"+lead_detail);
        String jsonDetails = "";
        Gson gson = new Gson();
        jsonDetails=lead_detail;

        Type type = new TypeToken<Customer>() {
        }.getType();

        customer=gson.fromJson(jsonDetails,type);

        fb=findViewById(R.id.create_bt);
        fb.setOnClickListener(v -> startActivity(new Intent(ServiceCompletedScreen.this,CreateNewLeads.class)));

        cus_name=findViewById(R.id.show_cus_name);
        cus_num=findViewById(R.id.show_cus_number);
        cus_email=findViewById(R.id.show_cus_email);
        lead_source=findViewById(R.id.show_lead_source);
        lead_date=findViewById(R.id.show_lead_date);
        lead_branch=findViewById(R.id.show_lead_branch);
        service_type=findViewById(R.id.service_type);
        notes=findViewById(R.id.notes);
        download=findViewById(R.id.download_pdf);
        sales_man=findViewById(R.id.sales_man);
        service_man=findViewById(R.id.service_man);
        lead_status=findViewById(R.id.lead_status);
        service_status=findViewById(R.id.service_status);
        status_date=findViewById(R.id.status_update_date);
        lead_status_date=findViewById(R.id.lead_status_date);

        set_values();

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download();
            }
        });

    }

    public void set_values() {
        cus_name.setText(customer.getName());
        cus_email.setText(customer.getEmail());
        cus_num.setText(customer.getMobile());
        lead_id=customer.getId();
        lead_source.setText(customer.getLead_source());
        lead_date.setText(customer.getLead_date());
        lead_branch.setText(customer.getLead_branch());
        lead_status.setText(customer.getLead_status());
        lead_status_date.setText(customer.getLead_status_dt());
        service_type.setText(customer.getService_type());
        service_status.setText(customer.getService_sts());
        status_date.setText(customer.getService_sts_dt());
        sales_man.setText(customer.getSales_man());
        service_man.setText(customer.getService_man());
        notes.setText(customer.getNotes());

    }


/*
    public void download_pdf(){
        try {
            Helper.loading(ServiceCompletedScreen.this);
            JSONObject job = new JSONObject();

            job.put("id",lead_id);
            try{
                RequestQueue requestQueue = Volley.newRequestQueue(ServiceCompletedScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.completed_pdf_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(ServiceCompletedScreen.this, "PDF Successfully Downloaded...", Toast.LENGTH_SHORT).show();
                            finish();





                        },error -> {

                    try {
                        Helper.mProgressBarHandler.hide();
                        // Log.e("err",error.getMessage().toString());
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                Log.e("res", res + "..");
                                JSONObject obj = new JSONObject(res);
                                Toast.makeText(ServiceCompletedScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        // Basic Authentication
                        headers.put("token",  dol_user_accessToken);
                        return headers;
                    }
                };




                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
*/

    class InputStreamVolleyRequest extends Request<byte[]> {
        private final Response.Listener<byte[]> mListener;
        private Map<String, String> mParams;
        private JSONObject mjsonObject;

        //create a static map for directly accessing headers
        public Map<String, String> responseHeaders ;

        public InputStreamVolleyRequest(int method, String mUrl ,Response.Listener<byte[]> listener,
                                        Response.ErrorListener errorListener, HashMap<String, String> params,Map<String,String> header) {
            // TODO Auto-generated constructor stub

            super(method, mUrl, errorListener);
            // this request would never use cache.
            setShouldCache(false);
            mListener = listener;
            mParams=params;
            responseHeaders=header;

        }


        @Override
        protected Map<String, String> getParams()
                throws com.android.volley.AuthFailureError {
            return mParams;
        };

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {

            Map<String,String> head=super.getHeaders();
            Map<String,String> head1=new HashMap<>();
            head1.putAll(head);
            head1.putAll(responseHeaders);

            return head1;
        }

        @Override
        protected void deliverResponse(byte[] response) {
            mListener.onResponse(response);
        }

        @Override
        protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {

            //Initialise local responseHeaders map with response headers received
            responseHeaders = response.headers;

            //Pass the response data here
            return Response.success( response.data, HttpHeaderParser.parseCacheHeaders(response));
        }
    }



    public void download() {
        try {
            Helper.loading(ServiceCompletedScreen.this);

            Map<String,String> head= new HashMap<>();
            head.put("token",dol_user_accessToken);

            HashMap<String,String > params=new HashMap<String,String>();
            params.put("id", lead_id);

            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, Helper.completed_pdf_url+lead_id,
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            // TODO handle the response
                            try {
                                if (response != null) {
                                    Log.d("file", " FILE"+response);
                                    Helper.mProgressBarHandler.hide();
                                    FileOutputStream outputStream;

                                    String name = lead_id + ".pdf";
                                    String url1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+name;
//"/storage/emulated/0/Android/"+ Environment.getDataDirectory().toString()+"/"+getPackageName()+"/files/File/"+name;
                                    Log.e("url",url1);
                                    outputStream = new FileOutputStream(url1);
                                  //  outputStream = openFileOutput(url1, Context.MODE_PRIVATE);
                                    outputStream.write(response);
                                    outputStream.close();
                                    Toast.makeText(ServiceCompletedScreen.this, "Download complete.", Toast.LENGTH_LONG).show();
                                    sendNotification(url1);
                                }
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                Helper.mProgressBarHandler.hide();
                                Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
// TODO handle the error
                    Helper.mProgressBarHandler.hide();
                    error.printStackTrace();
                    Log.e("IN","1");
                    try {
                        Helper.mProgressBarHandler.hide();
                        // Log.e("err",error.getMessage().toString());
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                // Now you can use any deserializer to make sense of data
                                Log.e("res", res + "..");
                                JSONObject obj = new JSONObject(res);
                                Toast.makeText(ServiceCompletedScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        Log.e("IN","2");
                        e.printStackTrace();
                    }
                }
            }, null,head);
            RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext(), new HurlStack());
            mRequestQueue.add(request);

        }catch (Exception e) {
            e.printStackTrace();
            Log.e("IN","3");
        }

    }

    public void sendNotification(String url1) {

        // Use NotificationCompat.Builder to set up our notification.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        //icon appears in device notification bar and right hand corner of notification
        builder.setSmallIcon(R.drawable.ic_notifications);

        // This intent is fired when notification is clicked
        // *****Here I want the user if clicks this notification the Download Folder should Open up*****
        Intent intent = new Intent(Intent.ACTION_VIEW);

        Uri imgUri = Uri.fromFile(new File(url1));

        intent.setDataAndType(imgUri, "file/*");

        startActivity(intent);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);

        // Large icon appears on the left of the notification
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notifications));

        // Content title, which appears in large type at the top of the notification
        builder.setContentTitle("Complete");

        // Content text, which appears in smaller text below the title
        builder.setContentText("Your Download has been completed Successfully!");

        //set the subtext
        builder.setSubText("Click to open the Downloads Folder!");
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Will display the notification in the notification bar
        notificationManager.notify(123, builder.build());

    }

}