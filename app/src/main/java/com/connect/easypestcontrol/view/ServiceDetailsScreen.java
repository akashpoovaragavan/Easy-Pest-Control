package com.connect.easypestcontrol.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.connect.easypestcontrol.model.Customer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ServiceDetailsScreen extends AppCompatActivity implements LocationListener {
    LocationManager locationManager;
    ImageView back;
    TextView title;
    EditText cus_name,cus_num,cus_email,lead_source,lead_branch,service_type;
    Button submit;
    //CheckBox reached,in_progress,completed;
    String[] service_status_ls={"Accepted","Declined"};
    AutoCompleteTextView service_sts,sales_man,service_man,status_date;
    ArrayAdapter<String> adapter_items1;
    String dol_user_accessToken;
    private boolean check;
    Customer customer;
    String service_detail,lead_id,sts_dt,ser_sts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details_screen);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title_txt);
        title.setText("Service");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cus_name=findViewById(R.id.show_cus_name);
        cus_num=findViewById(R.id.show_cus_number);
        cus_email=findViewById(R.id.show_cus_email);
        lead_source=findViewById(R.id.show_lead_source);
        lead_branch=findViewById(R.id.show_lead_branch);
        service_type=findViewById(R.id.service_type);
        submit=findViewById(R.id.submit_service);
        sales_man=findViewById(R.id.sales_man);
        service_man=findViewById(R.id.service_man);
        status_date=findViewById(R.id.status_update_date);
        service_sts=findViewById(R.id.service_status);

        /*reached=findViewById(R.id.check1);
        in_progress=findViewById(R.id.check2);
        completed=findViewById(R.id.check3);
*/

        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
        service_detail=Helper.sharedpreferences.getString("Service_Details","");
        Log.e("Details","Show:"+service_detail);
        String jsonDetails = "";
        Gson gson = new Gson();
        jsonDetails=service_detail;

        Type type = new TypeToken<Customer>() {
        }.getType();

        customer=gson.fromJson(jsonDetails,type);

        set_values();

        adapter_items1=new ArrayAdapter<String>(this,R.layout.list_down,service_status_ls);
        service_sts.setAdapter(adapter_items1);

        service_sts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list=parent.getItemAtPosition(position).toString();
                Toast.makeText(ServiceDetailsScreen.this, "Service Status:  "+list, Toast.LENGTH_SHORT).show();

            }
        });

        /// Date and Time
        Calendar calendar= Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);

        status_date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(ServiceDetailsScreen.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year1, int month1, int day1) {
                    month1 = month1 +1;
                    sts_dt= year1 +"-"+ month1 +"-"+ day1;
                    String date= day1 +"-"+ month1 +"-"+ year1;
                    status_date.setText(date);
                }
            },year,month,day);
            datePickerDialog.show();
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ser_sts=service_sts.getText().toString().trim();
                if (ser_sts.isEmpty() && sts_dt.isEmpty()){
                    Toast.makeText(ServiceDetailsScreen.this, "Please provide required details..!", Toast.LENGTH_SHORT).show();
                }else{
                    update_service();
                }

            }
        });



       /* if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

*/
/*
        completed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                locationEnabled();
                getLocation();

               */
/* if(check){
                    if (completed.isChecked()){
                        add_alert();
                    }
                }else{
                    locationEnabled();
                    getLocation();
                }*//*


            }
        });
*/
    }



    /// Set Values

    public void set_values() {
        cus_name.setText(customer.getName());
        cus_email.setText(customer.getEmail());
        cus_num.setText(customer.getMobile());
        lead_id=customer.getId();
        lead_source.setText(customer.getLead_source());
        lead_branch.setText(customer.getLead_branch());
        service_type.setText(customer.getService_type());
        sales_man.setText(customer.getSales_man());
        service_man.setText(customer.getService_man());

    }



    public void update_service(){
        try {
            Helper.loading(ServiceDetailsScreen.this);
            JSONObject job = new JSONObject();

            if (ser_sts.equals("Accepted")){
                job.put("serviceAccepted", "yes");
            }else{
                job.put("serviceAccepted", "no");
            }
            job.put("id",lead_id);
            job.put("serviceAcceptedDate",sts_dt);

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(ServiceDetailsScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.serviceWork_update_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(ServiceDetailsScreen.this, "Service Successfully Updated", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ServiceDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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


   /* public void add_alert(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(ServiceDetailsScreen.this);
        View view=getLayoutInflater().inflate(R.layout.notes,null);
        TextView finish=view.findViewById(R.id.finish);
        alert.setView(view);
        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }*/

   /* public void add_signature(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(ServiceDetailsScreen.this);
        View view=getLayoutInflater().inflate(R.layout.signature,null);
        TextView verify=view.findViewById(R.id.verify);
        alert.setView(view);
        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void add_Qrcode(){
        final AlertDialog.Builder alert=new AlertDialog.Builder(ServiceDetailsScreen.this);
        View view=getLayoutInflater().inflate(R.layout.qr_code,null);
        TextView verify=view.findViewById(R.id.verify);
        ImageView qr=view.findViewById(R.id.qr_view);
        alert.setView(view);
        final AlertDialog alertDialog=alert.create();
        alertDialog.setCanceledOnTouchOutside(true);
        MultiFormatWriter writer= new MultiFormatWriter();
        try {
            BitMatrix matrix=writer.encode("BarCode", BarcodeFormat.QR_CODE,350,350);
            BarcodeEncoder encoder=new BarcodeEncoder();
            Bitmap bitmap=encoder.createBitmap(matrix);
            qr.setImageBitmap(bitmap);
        }catch (WriterException e) {
            e.printStackTrace();
        }
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }*/
    /// LOCATION
    private void locationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!gps_enabled && !network_enabled) {
            new android.app.AlertDialog.Builder(ServiceDetailsScreen.this)
                    .setTitle("Enable GPS Service")
                    .setMessage("We need your GPS location to show Near Services around you.")
                    .setCancelable(false)
                    .setPositiveButton("Enable", (paramDialogInterface, paramInt) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, (LocationListener) this);
            check=true;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        try {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            Toast.makeText(ServiceDetailsScreen.this, "Location:"+address, Toast.LENGTH_SHORT).show();
            Log.e("city: ",address+"..");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
    /// END

}