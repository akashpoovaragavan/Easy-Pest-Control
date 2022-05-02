package com.connect.easypestcontrol.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.connect.easypestcontrol.controller.CustomerAdapter;
import com.connect.easypestcontrol.controller.LeadsAdapter;
import com.connect.easypestcontrol.model.Customer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FragmentToday extends Fragment {
    RecyclerView customer_recycler;
    CustomerAdapter customerAdapter;
    List<Customer> items;
    FloatingActionButton fb;
    View view;
    String dol_user_accessToken;
    View empty_layout;



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
     String titletxt;
    JSONArray jsonArray1;
    public FragmentToday(String titletxt) {
        try {
            this.titletxt = titletxt;
           // this.jsonArray1=jsonArray1;
            Log.e("titletxt",titletxt+".");
            //Log.e("today",jsonArray1.length()+".");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO: Rename and change types and number of parameters
   /* public static FragmentToday newInstance(String param1, JSONArray param2) {
        FragmentToday fragment = new FragmentToday(titletxt,jsonArray1);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
       // args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_today, container, false);
        fb=view.findViewById(R.id.add_bt);
        empty_layout=view.findViewById(R.id.emptyLayout);
        if(titletxt.equals("leads"))
            fb.setVisibility(View.VISIBLE);
        else
            fb.setVisibility(View.GONE);

        fb.setOnClickListener(v -> startActivity(new Intent(getActivity(),CreateNewLeads.class)));

        Helper.sharedpreferences = getActivity().getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");


        customer_recycler=view.findViewById(R.id.customer_recycler);
        customer_recycler.setHasFixedSize(true);
        customer_recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        switch (titletxt){
            case "Converted":
                converted_list_today();
                break;

            case "FollowUp":
                //getFollow();
                follow_list_today();
                break;

            case "Assigned":
                //getAssign();
                assigned_list_today();
                break;
        }

        return view;
    }



    /// Converted Today

    public void converted_list_today() {
        try {
            Helper.loading(getActivity());
            JSONObject job = new JSONObject();

            job.put("option", "today");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.converted_list_url,
                    job, response -> {
                try {
                    Helper.mProgressBarHandler.hide();
                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jsonArray1 = new JSONArray();
                    jsonArray1 = job1.getJSONArray("data");
                    Log.e("Converted Today", job1.toString());
                    if (jsonArray1.length() != 0) {
                        items = new ArrayList<>();
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
                            String dol_service_status_date = obj.getString("convertedDate");
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
                            customer.setService_sts("Converted");
                            customer.setService_sts_dt(dol_service_status_date);
                            customer.setSales_man(dol_sales_man);
                            customer.setService_man(dol_service_man);
                            customer.setNotes(dol_notes);
                            items.add(customer);

                            customerAdapter = new CustomerAdapter(getContext(), items);
                            customer_recycler.setAdapter(customerAdapter);
                            customerAdapter.notifyDataSetChanged();
                            customer_recycler.setVisibility(View.VISIBLE);
                            empty_layout.setVisibility(View.GONE);
                        }
                    } else {
                        // emptyLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Helper.mProgressBarHandler.hide();
                }

            }, error -> {
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


    /// Follow Up Today

    public void follow_list_today() {
        try {
            Helper.loading(getActivity());
            JSONObject job = new JSONObject();

            job.put("option", "today");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.follow_list_url,
                    job, response -> {
                try {
                    Helper.mProgressBarHandler.hide();
                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jsonArray1 = new JSONArray();
                    jsonArray1 = job1.getJSONArray("data");
                    Log.e("Follow Today", job1.toString());

                    if (jsonArray1.length() != 0) {
                        items = new ArrayList<>();
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
                            String dol_service_status_date = obj.getString("followUpDate");
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
                            customer.setService_sts("Follow Up");
                            customer.setService_sts_dt(dol_service_status_date);
                            customer.setSales_man(dol_sales_man);
                            customer.setService_man(dol_service_man);
                            customer.setNotes(dol_notes);
                            items.add(customer);

                            customerAdapter = new CustomerAdapter(getContext(), items);
                            customer_recycler.setAdapter(customerAdapter);
                            customerAdapter.notifyDataSetChanged();
                            customer_recycler.setVisibility(View.VISIBLE);
                            empty_layout.setVisibility(View.GONE);
                        }
                    } else {
                        // emptyLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Helper.mProgressBarHandler.hide();
                }

            }, error -> {
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

    /// Assigned Today

    public void  assigned_list_today() {
        try {
            Helper.loading(getActivity());
            JSONObject job = new JSONObject();

            job.put("option", "today");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.assigned_list_url,
                    job, response -> {
                try {
                    Helper.mProgressBarHandler.hide();
                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jsonArray1 = new JSONArray();
                    jsonArray1 = job1.getJSONArray("data");
                    Log.e("Assigned Today", job1.toString());

                    if (jsonArray1.length() != 0) {
                        items = new ArrayList<>();
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
                            items.add(customer);

                            customerAdapter = new CustomerAdapter(getContext(), items);
                            customer_recycler.setAdapter(customerAdapter);
                            customerAdapter.notifyDataSetChanged();
                            customer_recycler.setVisibility(View.VISIBLE);
                            empty_layout.setVisibility(View.GONE);
                        }
                    } else {
                        // emptyLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Helper.mProgressBarHandler.hide();
                }

            }, error -> {
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



    /*// Today List

    public void getConverted() {

        try {
            if (jsonArray1.length() != 0) {
                items = new ArrayList<>();
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
                    String dol_service_status_date = obj.getString("convertedDate");
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
                    customer.setService_sts("Converted");
                    customer.setService_sts_dt(dol_service_status_date);
                    customer.setSales_man(dol_sales_man);
                    customer.setService_man(dol_service_man);
                    customer.setNotes(dol_notes);
                    items.add(customer);

                    customerAdapter = new CustomerAdapter(getContext(), items);
                    customer_recycler.setAdapter(customerAdapter);
                }
            } else {
                // emptyLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Helper.mProgressBarHandler.hide();
        }
    }

    /// END

    // Today List

    public void getFollow() {

        try {
            if (jsonArray1.length() != 0) {
                items = new ArrayList<>();
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
                    String dol_service_status_date = obj.getString("convertedDate");
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
                    customer.setService_sts("Follow Up");
                    customer.setService_sts_dt(dol_service_status_date);
                    customer.setSales_man(dol_sales_man);
                    customer.setService_man(dol_service_man);
                    customer.setNotes(dol_notes);
                    items.add(customer);

                    customerAdapter = new CustomerAdapter(getContext(), items);
                    customer_recycler.setAdapter(customerAdapter);
                }
            } else {
                // emptyLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Helper.mProgressBarHandler.hide();
        }
    }

    /// END


    // Today List

    public void getAssign() {

        try {
            if (jsonArray1.length() != 0) {
                items = new ArrayList<>();
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
                    String dol_service_status_date = obj.getString("convertedDate");
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
                    items.add(customer);

                    customerAdapter = new CustomerAdapter(getContext(), items);
                    customer_recycler.setAdapter(customerAdapter);
                }
            } else {
                // emptyLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Helper.mProgressBarHandler.hide();
        }
    }

    /// END
*/

}