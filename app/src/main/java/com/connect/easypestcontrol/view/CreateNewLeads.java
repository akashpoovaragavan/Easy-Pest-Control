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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateNewLeads extends AppCompatActivity {
    ImageView back;
    TextView title;
    EditText cus_name,cus_number,cus_email,lead_source,lead_date;
    AutoCompleteTextView lead_branch;
    String[] branch_ls={"Madurai","Chennai","Coimbatore","Trichy"};
    ArrayAdapter<String> adapter_branch;
    Button submit;
    String name,num,email,lead_src,lead_dt,lead_br;
    String dol_user_accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_leads);
        back=findViewById(R.id.back);
        title=findViewById(R.id.title_txt);
        title.setText("Create New Leads");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cus_name=findViewById(R.id.customer_name);
        cus_email=findViewById(R.id.customer_email);
        cus_number=findViewById(R.id.customer_number);
        lead_source=findViewById(R.id.lead_source);
        lead_date=findViewById(R.id.lead_date);
        submit=findViewById(R.id.submit_lead);

        lead_branch=findViewById(R.id.lead_branch);

        adapter_branch=new ArrayAdapter<String >(this,R.layout.list_down,branch_ls);

        lead_branch.setAdapter(adapter_branch);

        lead_branch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String list=parent.getItemAtPosition(position).toString();
                //lead_branch.setText(list);
                Toast.makeText(CreateNewLeads.this, "Branch:  "+list, Toast.LENGTH_SHORT).show();

            }
        });

        /// Date and Time
        Calendar calendar= Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH);
        final int day=calendar.get(Calendar.DAY_OF_MONTH);

        lead_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(CreateNewLeads.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month=month+1;
                         lead_dt=year+"-"+month+"-"+day;
                        String date=day+"-"+month+"-"+year;
                        lead_date.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });



        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");



        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=cus_name.getText().toString().trim();
                num=cus_number.getText().toString().trim();
                email=cus_email.getText().toString().trim();
                lead_src=lead_source.getText().toString().trim();
                lead_br=lead_branch.getText().toString().trim();


                if(name.isEmpty() && num.isEmpty() &&  email.isEmpty() && lead_src.isEmpty() && lead_dt.isEmpty() && lead_br.isEmpty()){
                    Toast.makeText(CreateNewLeads.this, "Please fill all details....!", Toast.LENGTH_SHORT).show();
                }else {
                    create_lead();
                }
            }
        });

    }

    public void create_lead(){
        try {
            Helper.loading(CreateNewLeads.this);




            JSONObject job = new JSONObject();
            job.put("customerName", name);
            job.put("mobileNumber", num);
            job.put("email", email);
            job.put("leadSource", lead_src);
            job.put("leadDate", lead_dt);
            job.put("branch", lead_br);

            try {
                RequestQueue requestQueue = Volley.newRequestQueue(CreateNewLeads.this);
                JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Helper.create_lead_url,
                        job,
                        response -> {
                            Helper.mProgressBarHandler.hide();
                            Toast.makeText(CreateNewLeads.this, "Lead Successfully Created", Toast.LENGTH_SHORT).show();
                            cus_name.setText("");
                            cus_email.setText("");
                            cus_number.setText("");
                            lead_source.setText("");
                            lead_date.setText("");
                            lead_branch.setText("");

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
                                        Toast.makeText(CreateNewLeads.this, obj.getString("error"), Toast.LENGTH_SHORT).show();
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
                        }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        // Basic Authentication
                        headers.put("token",  dol_user_accessToken);
                        return headers;
                    }
                };

                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        500000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonArrayRequest);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

}