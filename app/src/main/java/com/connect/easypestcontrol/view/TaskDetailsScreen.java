package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.connect.easypestcontrol.model.Customer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TaskDetailsScreen extends AppCompatActivity {
    ImageView back;
    TextView title;
    EditText cus_name,cus_num,cus_email,lead_source,lead_branch,service_type,notes;
    AutoCompleteTextView service_sts,sales_man,service_man,status_date;
    Button submit;
    String dol_user_accessToken;
    Customer customer;
    CheckBox completed_check;
    String service_detail,lead_id,task_nt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details_screen);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title_txt);
        title.setText("Task Details");
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
        submit=findViewById(R.id.task_submit);
        sales_man=findViewById(R.id.sales_man);
        service_man=findViewById(R.id.service_man);
        status_date=findViewById(R.id.status_update_date);
        service_sts=findViewById(R.id.service_status);
        completed_check=findViewById(R.id.check3);
        notes=findViewById(R.id.task_notes);

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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task_nt=notes.getText().toString().trim();
                if (completed_check.isChecked() && !task_nt.isEmpty()){
                    update_task();
                }else{
                    Toast.makeText(TaskDetailsScreen.this, "Please complete the task...!", Toast.LENGTH_SHORT).show();
                }
            }
        });


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
        service_sts.setText(customer.getService_sts());
        status_date.setText(customer.getService_sts_dt());
        notes.setText(customer.getNotes());

    }

    public void update_task(){
        try {
            Helper.loading(TaskDetailsScreen.this);
            JSONObject job = new JSONObject();

            String completed_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Log.e("Current Date",completed_date);

            job.put("id",lead_id);
            job.put("completed","yes");
            job.put("completedDate",completed_date);
            job.put("description",task_nt);

            try{
                RequestQueue requestQueue = Volley.newRequestQueue(TaskDetailsScreen.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, Helper.task_update_url,
                        job,
                        response -> {

                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(TaskDetailsScreen.this, "Service Successfully Completed..!", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(TaskDetailsScreen.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
}