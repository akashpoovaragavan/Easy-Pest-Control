package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.github.lzyzsd.circleprogress.DonutProgress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ReportServiceScreen extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    ImageView back;
    TextView title,text1,text2;
    EditText to_date,from_date;
    DonutProgress p1,p2;
    String to_dt="",from_dt="";
    Button download_all_pdf;
    String dol_user_accessToken,pdf;
    SwipeRefreshLayout swipe_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_service_screen);
        title=findViewById(R.id.title_txt);
        title.setText("Report");
        back=findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        text1=findViewById(R.id.text1);
        text2=findViewById(R.id.text2);
        to_date=findViewById(R.id.select_to_date);
        from_date=findViewById(R.id.select_from_date);
        p1=findViewById(R.id.progress1);
        p2=findViewById(R.id.progress2);
        download_all_pdf=findViewById(R.id.download_all_pdf);
        swipe_layout=findViewById(R.id.swipelayout);
        // emptyLayout=view.findViewById(R.id.emptyLayout);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));


        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
        pdf=Helper.sharedpreferences.getString("PDF","");


        Calendar calendar= Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        to_date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(ReportServiceScreen.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year1, int month1, int day1) {
                    month1 = month1 +1;
                    String date= day1 +"-"+ month1 +"-"+ year1;
                    to_dt=year1 +"-"+ month1 +"-"+day1 ;
                    to_date.setText(date);
                }
            },year,month,day);
            datePickerDialog.show();
        });
        from_date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog=new DatePickerDialog(ReportServiceScreen.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year12, int month12, int day12) {
                    month12 = month12 +1;
                    String date= day12 +"-"+ month12 +"-"+ year12;
                    from_dt= year12 +"-"+ month12 +"-"+ day12;
                    from_date.setText(date);
                }
            },year,month,day);
            datePickerDialog.show();
        });


        set_count();


        download_all_pdf.setOnClickListener(v -> {
            String lead_url;
            Log.e("PDF","+++"+pdf);
            if (!from_dt.equals("") && !to_dt.equals("")){
                switch (pdf){
                    case "Service":
                        Log.e("PDF",pdf);
                        lead_url=Helper.service_pdf_url+"?startDate="+from_dt+"&endDate="+to_dt;
                        download_all_lead(lead_url);
                        break;
                    case "InProgress":
                        lead_url=Helper.inProcess_pdf_url+"?startDate="+from_dt+"&endDate="+to_dt;
                        download_all_lead(lead_url);
                        break;

                    case "Missed":
                        lead_url=Helper.cancelled_pdf_url+"?startDate="+from_dt+"&endDate="+to_dt;
                        download_all_lead(lead_url);
                        break;

                    case "Completed":
                        lead_url=Helper.finished_pdf_url+"?startDate="+from_dt+"&endDate="+to_dt;
                        download_all_lead(lead_url);
                        break;
                }
            }else{
                Toast.makeText(ReportServiceScreen.this, "Provide report dates..!", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void set_count(){
        switch (pdf){
            case "Service":
                service_count();
                text1.setText("Accepted");
                text2.setText("Declined");
                break;
            case "InProgress":
                inProgress_count();
                text1.setText("OnGoing");
                text2.setText("Next Task");
                break;

            case "Missed":
                missed_count();
                text1.setText("Task Missed");
                text2.setText("Cancelled");
                break;

            case "Completed":
                completed_count();
                text1.setText("Completed");
                text2.setText("Not Completed");
                break;
        }

    }

    public void download_all_lead(String myUrl) {
        try {
            Helper.loading(ReportServiceScreen.this);
            swipe_layout.setRefreshing(false);
            Map<String,String> head= new HashMap<>();
            head.put("token",dol_user_accessToken);


            Log.e("url",myUrl);
            InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, myUrl,
                    new Response.Listener<byte[]>() {
                        @Override
                        public void onResponse(byte[] response) {
                            // TODO handle the response
                            try {
                                if (response != null) {
                                    Log.d("file", " FILE"+response);
                                    Helper.mProgressBarHandler.hide();
                                    FileOutputStream outputStream;

                                    String name = from_dt+"-"+to_dt+".pdf";
                                    String url1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+name;
                                    Log.e("url",url1);
                                    outputStream = new FileOutputStream(url1);
                                    //  outputStream = openFileOutput(url1, Context.MODE_PRIVATE);
                                    outputStream.write(response);
                                    outputStream.close();
                                    Toast.makeText(ReportServiceScreen.this, "Download complete.", Toast.LENGTH_LONG).show();
                                    //sendNotification(url1);
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
                                Toast.makeText(ReportServiceScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onRefresh() {
        set_count();

    }

    class InputStreamVolleyRequest extends Request<byte[]> {
        private final Response.Listener<byte[]> mListener;
        private Map<String, String> mParams;


        //create a static map for directly accessing headers
        public Map<String, String> responseHeaders ;

        public InputStreamVolleyRequest(int method, String mUrl , Response.Listener<byte[]> listener,
                                        Response.ErrorListener errorListener, HashMap<String, String> params, Map<String,String> header) {
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

    public void service_count(){
        Helper.loading(ReportServiceScreen.this);
        swipe_layout.setRefreshing(false);
        JSONObject job = new JSONObject();

        RequestQueue requestQueue = Volley.newRequestQueue(ReportServiceScreen.this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, Helper.service_sts_url,
                job,
                response -> {
                    try{
                        Helper.mProgressBarHandler.hide();
                        JSONObject obj = new JSONObject(response.toString());
                        JSONObject obj1=obj.getJSONObject("overall");
                        p1.setDonut_progress(obj1.getString("acceptedCount").trim());
                        p2.setDonut_progress(obj1.getString("declinedCount").trim());
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
                                Log.e("res", res + "..");
                                Toast.makeText(ReportServiceScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
                Log.e("token",dol_user_accessToken+".");
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }


    public void inProgress_count(){
        Helper.loading(ReportServiceScreen.this);
        swipe_layout.setRefreshing(false);
        JSONObject job = new JSONObject();

        RequestQueue requestQueue = Volley.newRequestQueue(ReportServiceScreen.this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, Helper.inProcess_sts_url,
                job,
                response -> {
                    try{
                        Helper.mProgressBarHandler.hide();
                        JSONObject obj = new JSONObject(response.toString());
                        JSONObject obj1=obj.getJSONObject("overall");
                        p1.setDonut_progress(obj1.getString("inProgressCount").trim());
                        p2.setDonut_progress(obj1.getString("notInProgressCount").trim());
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
                                Log.e("res", res + "..");
                                Toast.makeText(ReportServiceScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
                Log.e("token",dol_user_accessToken+".");
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }



    public void missed_count(){
        Helper.loading(ReportServiceScreen.this);
        swipe_layout.setRefreshing(false);
        JSONObject job = new JSONObject();

        RequestQueue requestQueue = Volley.newRequestQueue(ReportServiceScreen.this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, Helper.cancelled_sts_url,
                job,
                response -> {
                    try{
                        Helper.mProgressBarHandler.hide();
                        JSONObject obj = new JSONObject(response.toString());
                        JSONObject obj1=obj.getJSONObject("overall");
                        p1.setDonut_progress(obj1.getString("cancelledCount").trim());
                        p2.setDonut_progress(obj1.getString("notCancelledCount").trim());
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
                                Log.e("res", res + "..");
                                Toast.makeText(ReportServiceScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
                Log.e("token",dol_user_accessToken+".");
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }


    public void completed_count(){
        Helper.loading(ReportServiceScreen.this);
        swipe_layout.setRefreshing(false);
        JSONObject job = new JSONObject();

        RequestQueue requestQueue = Volley.newRequestQueue(ReportServiceScreen.this);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, Helper.finished_sts_url,
                job,
                response -> {
                    try{
                        Helper.mProgressBarHandler.hide();
                        JSONObject obj = new JSONObject(response.toString());
                        JSONObject obj1=obj.getJSONObject("overall");
                        p1.setDonut_progress(obj1.getString("completedCount").trim());
                        p2.setDonut_progress(obj1.getString("notCompletedCount").trim());
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
                                Log.e("res", res + "..");
                                Toast.makeText(ReportServiceScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
                Log.e("token",dol_user_accessToken+".");
                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }


}