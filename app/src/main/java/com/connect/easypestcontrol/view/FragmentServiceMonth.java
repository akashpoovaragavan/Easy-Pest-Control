package com.connect.easypestcontrol.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.connect.easypestcontrol.controller.ServiceAdapter;
import com.connect.easypestcontrol.controller.TaskAdapter;
import com.connect.easypestcontrol.model.Customer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentServiceMonth extends Fragment {
    View view;
    RecyclerView customer_recycler;
    ServiceAdapter serviceAdapter;
    TaskAdapter taskAdapter;
    List<Customer> customerList;

    String titletxt;
    JSONArray jsonArray1;
    String dol_user_accessToken;
    View empty_layout;
    public FragmentServiceMonth(String titletxt) {
        try {
            this.titletxt = titletxt;
            Log.e("titletxt",titletxt+".");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fragment_service_month,container,false);
        empty_layout=view.findViewById(R.id.emptyLayout);
        customer_recycler=view.findViewById(R.id.customer_recycler);
        customer_recycler.setHasFixedSize(true);
        customer_recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        Helper.sharedpreferences = getActivity().getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");

        switch (titletxt){
            case "ServiceMonth":
                service_list_month();
                break;
            case "TaskMonth":
                task_list_month();
                break;
        }




        return view;
    }


    /// Service Today

    public void service_list_month() {
        try {

            JSONObject job = new JSONObject();

            job.put("option", "month");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.serviceWork_list_url,
                    job, response -> {
                try {

                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jsonArray1 = new JSONArray();
                    jsonArray1 = job1.getJSONArray("data");
                    Log.e("Service Month", job1.toString());

                    if (jsonArray1.length() != 0) {
                        customerList= new ArrayList<>();
                        for (int v = 0; v < jsonArray1.length(); v++) {
                            JSONObject obj = jsonArray1.getJSONObject(v);
                            Customer customer = new Customer();

                            String dol_customer_id = obj.getString("_id");
                            String dol_customer_name = obj.getString("customerName");
                            String dol_mobile = obj.getString("mobileNumber");
                            String dol_email = obj.getString("email");
                            String dol_lead_source = obj.getString("leadSource");
                            String dol_lead_date = obj.getString("leadDate");
                            String dol_branch = obj.getString("branch");
                            String dol_lead_status = obj.getString("salesAccepted");
                            String dol_lead_status_date = obj.getString("salesAcceptedDate");
                            String dol_service_type = obj.getString("serviceType");
                            // String dol_service_status = obj.getString("");
                            String dol_service_status_date = obj.getString("assignedDate");
                            String dol_sales_man = obj.getString("salesPerson");
                            String dol_service_man = obj.getString("servicePerson");
                            String dol_notes = obj.getString("description");

                            customer.setId(dol_customer_id);
                            customer.setName(dol_customer_name);
                            customer.setEmail(dol_email);
                            customer.setMobile(dol_mobile);
                            customer.setLead_source(dol_lead_source);
                            customer.setLead_date(dol_lead_date);
                            customer.setLead_branch(dol_branch);
                            customer.setLead_status("Accepted");
                            customer.setLead_status_dt(dol_lead_status_date );
                            customer.setService_type(dol_service_type);
                            customer.setService_sts("Assigned");
                            customer.setService_sts_dt(dol_service_status_date);
                            customer.setSales_man(dol_sales_man);
                            customer.setService_man(dol_service_man);
                            customer.setNotes(dol_notes);
                            customerList.add(customer);

                            serviceAdapter = new ServiceAdapter(getContext(), customerList);
                            customer_recycler.setAdapter(serviceAdapter);
                            taskAdapter.notifyDataSetChanged();
                            customer_recycler.setVisibility(View.VISIBLE);
                            empty_layout.setVisibility(View.GONE);
                        }
                    } else {
                        // emptyLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }, error -> {
                try {

                    // Log.e("err",error.getMessage().toString());
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            Log.e("res", res + "..");
                            JSONObject obj = new JSONObject(res);
                            customer_recycler.setVisibility(View.GONE);
                            empty_layout.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), obj.getString("error"), Toast.LENGTH_SHORT).show();
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
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    // Basic Authentication
                    headers.put("token", dol_user_accessToken);

                    return headers;
                }
            };


            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// END

    /// Task Month

    public void task_list_month() {
        try {

            JSONObject job = new JSONObject();

            job.put("option", "month");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.task_url,
                    job, response -> {
                try {

                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jsonArray1 = new JSONArray();
                    jsonArray1 = job1.getJSONArray("data");
                    Log.e("Task Month", job1.toString());

                    if (jsonArray1.length() != 0) {
                        customerList= new ArrayList<>();
                        for (int v = 0; v < jsonArray1.length(); v++) {
                            JSONObject obj = jsonArray1.getJSONObject(v);
                            Customer customer = new Customer();

                            String dol_customer_id = obj.getString("_id");
                            String dol_customer_name = obj.getString("customerName");
                            String dol_mobile = obj.getString("mobileNumber");
                            String dol_email = obj.getString("email");
                            String dol_lead_source = obj.getString("leadSource");
                            String dol_lead_date = obj.getString("leadDate");
                            String dol_branch = obj.getString("branch");
                            String dol_lead_status = obj.getString("salesAccepted");
                            String dol_lead_status_date = obj.getString("salesAcceptedDate");
                            String dol_service_type = obj.getString("serviceType");
                            // String dol_service_status = obj.getString("");
                            String dol_service_status_date = obj.getString("serviceAcceptedDate");
                            String dol_sales_man = obj.getString("salesPerson");
                            String dol_service_man = obj.getString("servicePerson");
                            String dol_notes = obj.getString("description");

                            customer.setId(dol_customer_id);
                            customer.setName(dol_customer_name);
                            customer.setEmail(dol_email);
                            customer.setMobile(dol_mobile);
                            customer.setLead_source(dol_lead_source);
                            customer.setLead_date(dol_lead_date);
                            customer.setLead_branch(dol_branch);
                            customer.setLead_status("Accepted");
                            customer.setLead_status_dt(dol_lead_status_date );
                            customer.setService_type(dol_service_type);
                            customer.setService_sts("Accepted");
                            customer.setService_sts_dt(dol_service_status_date);
                            customer.setSales_man(dol_sales_man);
                            customer.setService_man(dol_service_man);
                            customer.setNotes(dol_notes);
                            customerList.add(customer);

                            taskAdapter = new TaskAdapter(getContext(), customerList);
                            customer_recycler.setAdapter(taskAdapter);
                            taskAdapter.notifyDataSetChanged();
                            customer_recycler.setVisibility(View.VISIBLE);
                            empty_layout.setVisibility(View.GONE);
                        }
                    } else {
                        // emptyLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }, error -> {
                try {

                    // Log.e("err",error.getMessage().toString());
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            Log.e("res", res + "..");
                            JSONObject obj = new JSONObject(res);
                            customer_recycler.setVisibility(View.GONE);
                            empty_layout.setVisibility(View.VISIBLE);
                            Toast.makeText(getActivity(), obj.getString("error"), Toast.LENGTH_SHORT).show();
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
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    // Basic Authentication
                    headers.put("token", dol_user_accessToken);

                    return headers;
                }
            };


            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                    500000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// END




    /*// Month List

    public void getService() {

        try {
            if (jsonArray1.length() != 0) {
                customerList= new ArrayList<>();
                for (int v = 0; v < jsonArray1.length(); v++) {
                    JSONObject obj = jsonArray1.getJSONObject(v);
                    Customer customer = new Customer();

                    String dol_customer_id = obj.getString("_id");
                    String dol_customer_name = obj.getString("customerName");
                    String dol_mobile = obj.getString("mobileNumber");
                    String dol_email = obj.getString("email");
                    String dol_lead_source = obj.getString("leadSource");
                    String dol_lead_date = obj.getString("leadDate");
                    String dol_branch = obj.getString("branch");
                    String dol_lead_status = obj.getString("salesAccepted");
                    String dol_lead_status_date = obj.getString("salesAcceptedDate");
                    String dol_service_type = obj.getString("serviceType");
                    // String dol_service_status = obj.getString("");
                    String dol_service_status_date = obj.getString("assignedDate");
                    String dol_sales_man = obj.getString("salesPerson");
                    String dol_service_man = obj.getString("servicePerson");
                    String dol_notes = obj.getString("description");

                    customer.setId(dol_customer_id);
                    customer.setName(dol_customer_name);
                    customer.setEmail(dol_email);
                    customer.setMobile(dol_mobile);
                    customer.setLead_source(dol_lead_source);
                    customer.setLead_date(dol_lead_date);
                    customer.setLead_branch(dol_branch);
                    customer.setLead_status("Accepted");
                    customer.setLead_status_dt(dol_lead_status_date );
                    customer.setService_type(dol_service_type);
                    customer.setService_sts("Assigned");
                    customer.setService_sts_dt(dol_service_status_date);
                    customer.setSales_man(dol_sales_man);
                    customer.setService_man(dol_service_man);
                    customer.setNotes(dol_notes);
                    customerList.add(customer);

                    serviceAdapter = new ServiceAdapter(getContext(), customerList);
                    customer_recycler.setAdapter(serviceAdapter);
                }
            } else {
                // emptyLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /// END*/
}
