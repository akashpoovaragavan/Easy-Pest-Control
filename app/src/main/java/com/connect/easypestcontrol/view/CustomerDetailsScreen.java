package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.android.volley.ServerError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.connect.easypestcontrol.controller.LeadsAdapter;
import com.connect.easypestcontrol.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDetailsScreen extends AppCompatActivity {
    ImageView back;
    TextView title;
    FloatingActionButton fb;
    String[] service_man_ls,service_man_id;
    String[] sales_man_ls,sales_man_id;
    String[] service_status_ls={"Converted","Followup","Assigned"};
    String[] lead_status_ls={"Accepted","Declined"};
    AutoCompleteTextView sales_man,service_man,lead_status,service_status,status_date,lead_status_date;
    ArrayAdapter<String> adapter_items1,adapter_items2,adapter_items3,adapter_items4;
    EditText cus_name,cus_num,cus_email,lead_source,lead_date,lead_branch,service_type,notes;
    Button submit;
    String lead_detail,lead_id,lead_sts_dt="",service,sales,ser_mn,sts,sts_dt="",nt,lead_sts,sales_id,ser_mn_id;
    String dol_user_accessToken;
    Customer customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details_screen);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title_txt);
        title.setText("Customer View");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
        lead_detail=Helper.sharedpreferences.getString("Lead_Details","");
        Log.e("Details","Show:"+lead_detail);
        String jsonDetails = "";
        Gson gson = new Gson();
        jsonDetails=lead_detail;

        Type type = new TypeToken<Customer>() {
        }.getType();

        customer=gson.fromJson(jsonDetails,type);



        fb=findViewById(R.id.create_bt);
        fb.setOnClickListener(v -> startActivity(new Intent(CustomerDetailsScreen.this,CreateNewLeads.class)));

        cus_name=findViewById(R.id.show_cus_name);
        cus_num=findViewById(R.id.show_cus_number);
        cus_email=findViewById(R.id.show_cus_email);
        lead_source=findViewById(R.id.show_lead_source);
        lead_date=findViewById(R.id.show_lead_date);
        lead_branch=findViewById(R.id.show_lead_branch);
        service_type=findViewById(R.id.service_type);
        notes=findViewById(R.id.notes);
        submit=findViewById(R.id.submit);
        sales_man=findViewById(R.id.sales_man);
        service_man=findViewById(R.id.service_man);
        lead_status=findViewById(R.id.lead_status);
        service_status=findViewById(R.id.service_status);
        status_date=findViewById(R.id.status_update_date);
        lead_status_date=findViewById(R.id.lead_status_date);

        try {
            set_values();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        sales_list();

        service_list();

        adapter_items3=new ArrayAdapter<String>(this,R.layout.list_down,service_status_ls);

        adapter_items4=new ArrayAdapter<String>(this,R.layout.list_down,lead_status_ls);


        service_status.setAdapter(adapter_items3);
        lead_status.setAdapter(adapter_items4);

        sales_man.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list=parent.getItemAtPosition(position).toString();
                 sales_id=sales_man_id[position];
                Toast.makeText(CustomerDetailsScreen.this, "Sales Man:  "+list, Toast.LENGTH_SHORT).show();
            }
        });

        service_man.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list=parent.getItemAtPosition(position).toString();
                ser_mn_id=service_man_id[position];
                Toast.makeText(CustomerDetailsScreen.this, "Service Man:  "+list, Toast.LENGTH_SHORT).show();

            }
        });

        lead_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list=parent.getItemAtPosition(position).toString();
                Toast.makeText(CustomerDetailsScreen.this, "Lead Status:  "+list, Toast.LENGTH_SHORT).show();

            }
        });

        service_status.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list=parent.getItemAtPosition(position).toString();
                Toast.makeText(CustomerDetailsScreen.this, "Service Status:  "+list, Toast.LENGTH_SHORT).show();

            }
        });

        /// Date and Time
        Calendar calendar= Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);

        status_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(CustomerDetailsScreen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month=month+1;
                        sts_dt=year+"-"+month+"-"+day;
                        String date=day+"-"+month+"-"+year;
                        status_date.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        lead_status_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(CustomerDetailsScreen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month=month+1;
                        lead_sts_dt=year+"-"+month+"-"+day;
                        String date=day+"-"+month+"-"+year;
                        lead_status_date.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        submit.setOnClickListener(v -> {
            service=service_type.getText().toString().trim();
            sales=sales_man.getText().toString().trim();
            ser_mn=service_man.getText().toString().trim();
            sts=service_status.getText().toString().trim();
            nt=notes.getText().toString().trim();
            lead_sts=lead_status.getText().toString().trim();
            Log.e("STS--",lead_sts+"...");
            Log.e("STS DT--",lead_sts_dt+"..");


            if (!lead_sts.isEmpty()&&!lead_status_date.getText().toString().isEmpty()){
                switch (sts){
                    case "Converted":
                        if(service.isEmpty() && sts.isEmpty() && sts_dt.isEmpty()){
                            Toast.makeText(CustomerDetailsScreen.this, "Please Provide Required Details...!", Toast.LENGTH_SHORT).show();
                        }else{
                            update_converted();
                        }
                        break;
                    case "Followup":
                        if(service.isEmpty() && sts.isEmpty() && sts_dt.isEmpty()){
                            Toast.makeText(CustomerDetailsScreen.this, "Please Provide Required Details...!", Toast.LENGTH_SHORT).show();
                        }else{
                            update_followUp();
                        }
                        break;
                    case "Assigned":
                        if(service.isEmpty() && sales.isEmpty() && ser_mn.isEmpty() && sts.isEmpty() && sts_dt.isEmpty()){
                            Toast.makeText(CustomerDetailsScreen.this, "Please Provide Required Details...!", Toast.LENGTH_SHORT).show();
                        }else {
                            update_assigned();
                        }
                        break;
                    default:
                        update_lead();
                        break;
                }
            }else{
                Toast.makeText(CustomerDetailsScreen.this, "Please Provide Required lead details...!", Toast.LENGTH_SHORT).show();
            }

            /*if(!lead_sts.isEmpty()&& lead_sts.equals("Accepted") && !lead_sts_dt.isEmpty()){
                if(service.isEmpty() && sales.isEmpty() && ser_mn.isEmpty() && sts.isEmpty() && sts_dt.isEmpty()){
                    Toast.makeText(CustomerDetailsScreen.this, "Please Provide Required Details...!", Toast.LENGTH_SHORT).show();
                }else {
                    update_lead();
                }
            }else {
                if (!lead_sts.isEmpty()&&!lead_sts_dt.isEmpty()){
                    update_lead();
                }else{
                    Toast.makeText(CustomerDetailsScreen.this, "Please Provide Required Details...!", Toast.LENGTH_SHORT).show();

                }
            }*/

        });
    }

    /// Set Values

    public void set_values() throws ParseException {

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



            if (!lead_sts_dt.trim().equals("") && !sts_dt.trim().equals("")) {
                lead_sts_dt=customer.getLead_status_dt();



                sts_dt = customer.getService_sts_dt();


            }

    }


    /// Sales Man List

    public void sales_list(){
        Helper.loading(CustomerDetailsScreen.this);
        JSONObject job = new JSONObject();
        RequestQueue requestQueue = Volley.newRequestQueue(CustomerDetailsScreen.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Helper.sales_list_url,
                null, response -> {
            try {
                Helper.mProgressBarHandler.hide();
                JSONObject job1 = new JSONObject(String.valueOf(response));
                JSONArray jarr = job1.getJSONArray("data");
                Log.e("Data:",job1.toString());
                if(jarr.length()!=0) {
                    sales_man_ls = new String[jarr.length()];
                    sales_man_id = new String[jarr.length()];
                    for (int v = 0; v < jarr.length(); v++) {
                        JSONObject obj = jarr.getJSONObject(v);

                        String dol_emp_id= obj.getString("_id");
                        String dol_emp_name= obj.getString("empName");

                        sales_man_ls[v]=dol_emp_name;
                        sales_man_id[v]=dol_emp_id;



                    }
                    adapter_items1=new ArrayAdapter<String >(this,R.layout.list_down,sales_man_ls);
                    sales_man.setAdapter(adapter_items1);
                }else{
                    // emptyLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(CustomerDetailsScreen.this, "NO DATA", Toast.LENGTH_SHORT).show();

                }

            }catch (Exception e){
                e.printStackTrace();
                Helper.mProgressBarHandler.hide();
            }

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
                        JSONObject obj = new JSONObject(res);
                        Log.e("res", res + "..");
                        Toast.makeText(CustomerDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
    }


    /// END

    /// Service Man List

    public void service_list(){

        RequestQueue requestQueue = Volley.newRequestQueue(CustomerDetailsScreen.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Helper.service_list_url,
                null, response -> {
            try {

                JSONObject job1 = new JSONObject(String.valueOf(response));
                JSONArray jarr = job1.getJSONArray("data");
                Log.e("Data:",job1.toString());
                if(jarr.length()!=0) {
                    service_man_ls = new String[jarr.length()];
                    service_man_id = new String[jarr.length()];
                    for (int v = 0; v < jarr.length(); v++) {
                        JSONObject obj = jarr.getJSONObject(v);

                        String dol_emp_id= obj.getString("_id");
                        String dol_emp_name= obj.getString("empName");

                        service_man_ls[v]=dol_emp_name;
                        service_man_id[v]=dol_emp_id;



                    }
                    adapter_items2=new ArrayAdapter<String>(this,R.layout.list_down,service_man_ls);
                    service_man.setAdapter(adapter_items2);
                }else{
                    // emptyLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(CustomerDetailsScreen.this, "NO DATA", Toast.LENGTH_SHORT).show();

                }

            }catch (Exception e){
                e.printStackTrace();

            }

        },error -> {
            try {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        Log.e("res", res + "..");
                        Toast.makeText(CustomerDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
    }


    /// END


    /// update Lead

    public void update_lead(){
        try {
            Helper.loading(CustomerDetailsScreen.this);
            JSONObject job = new JSONObject();

            if (lead_sts.equals("Accepted")){
                job.put("salesAccepted", "yes");


                SimpleDateFormat spf1 = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa");
                Date newDate1 = spf1.parse(sts_dt);
                spf1 = new SimpleDateFormat("yyyy-MM-dd");
                sts_dt = spf1.format(newDate1);

            }else{
                job.put("salesAccepted", "no");
            }
            switch (sts) {
                case "Converted":
                    job.put("converted", "yes");
                    job.put("convertedDate", sts_dt);
                    break;
                case "Followup":
                    job.put("followUp", "yes");
                    job.put("followUpDate", sts_dt);
                    break;
                case "Assigned":
                    job.put("assigned", "yes");
                    job.put("assignedDate", sts_dt);
                    break;
            }

            SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");
            Date newDate = spf.parse(lead_sts_dt);
            spf = new SimpleDateFormat("yyyy-MM-dd");
            lead_sts_dt = spf.format(newDate);



            job.put("id",lead_id);
            job.put("salesAcceptedDate",lead_sts_dt);
            job.put("serviceType", service);
            job.put("salesPerson", sales);
            job.put("salesPersonId", sales_id);
            job.put("servicePerson", ser_mn);
            job.put("servicePersonId", ser_mn_id);
            job.put("description", nt);
            Log.e("JOB Lead:",job.toString());

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(CustomerDetailsScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.lead_update_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(CustomerDetailsScreen.this, "Lead Successfully Updated", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CustomerDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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

    /// END


    /// update Lead

    public void update_converted(){
        try {
            Helper.loading(CustomerDetailsScreen.this);
            JSONObject job = new JSONObject();

            if (lead_sts.equals("Accepted")){
                job.put("salesAccepted", "yes");
            }else{
                job.put("salesAccepted", "no");
            }
            switch (sts) {
                case "Converted":
                    job.put("converted", "yes");
                    job.put("convertedDate", sts_dt);
                    break;
                case "Followup":
                    job.put("followUp", "yes");
                    job.put("followUpDate", sts_dt);
                    break;
                case "Assigned":
                    job.put("assigned", "yes");
                    job.put("assignedDate", sts_dt);
                    break;
            }

            job.put("id",lead_id);
            job.put("salesAcceptedDate",lead_sts_dt);
            job.put("serviceType", service);
            job.put("salesPerson", sales);
            job.put("salesPersonId", sales_id);
            job.put("servicePerson", ser_mn);
            job.put("servicePersonId", ser_mn_id);
            job.put("description", nt);
            Log.e("JOB Lead:",job.toString());

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(CustomerDetailsScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.converted_update_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(CustomerDetailsScreen.this, "Lead Successfully Updated", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CustomerDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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

    /// END


    /// update Lead

    public void update_followUp(){
        try {
            Helper.loading(CustomerDetailsScreen.this);
            JSONObject job = new JSONObject();

            if (lead_sts.equals("Accepted")){
                job.put("salesAccepted", "yes");
            }else{
                job.put("salesAccepted", "no");
            }
            switch (sts) {
                case "Converted":
                    job.put("converted", "yes");
                    job.put("convertedDate", sts_dt);
                    break;
                case "Followup":
                    job.put("followUp", "yes");
                    job.put("followUpDate", sts_dt);
                    break;
                case "Assigned":
                    job.put("assigned", "yes");
                    job.put("assignedDate", sts_dt);
                    break;
            }

            job.put("id",lead_id);
            job.put("salesAcceptedDate",lead_sts_dt);
            job.put("serviceType", service);
            job.put("salesPerson", sales);
            job.put("salesPersonId", sales_id);
            job.put("servicePerson", ser_mn);
            job.put("servicePersonId", ser_mn_id);
            job.put("description", nt);
            Log.e("JOB Lead:",job.toString());

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(CustomerDetailsScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.follow_update_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(CustomerDetailsScreen.this, "Lead Successfully Updated", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CustomerDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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

    /// END


    /// update Lead

    public void update_assigned(){
        try {
            Helper.loading(CustomerDetailsScreen.this);
            JSONObject job = new JSONObject();

            if (lead_sts.equals("Accepted")){
                job.put("salesAccepted", "yes");
            }else{
                job.put("salesAccepted", "no");
            }
            switch (sts) {
                case "Converted":
                    job.put("converted", "yes");
                    job.put("convertedDate", sts_dt);
                    break;
                case "Followup":
                    job.put("followUp", "yes");
                    job.put("followUpDate", sts_dt);
                    break;
                case "Assigned":
                    job.put("assigned", "yes");
                    job.put("assignedDate", sts_dt);
                    break;
            }

            job.put("id",lead_id);
            job.put("salesAcceptedDate",lead_sts_dt);
            job.put("serviceType", service);
            job.put("salesPerson", sales);
            job.put("salesPersonId", sales_id);
            job.put("servicePerson", ser_mn);
            job.put("servicePersonId", ser_mn_id);
            job.put("description", nt);
            Log.e("JOB Lead:",job.toString());

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(CustomerDetailsScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.assigned_update_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(CustomerDetailsScreen.this, "Lead Successfully Updated", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(CustomerDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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

    /// END


}