package com.connect.easypestcontrol.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

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
import com.connect.easypestcontrol.controller.TabSelectedAdapter;
import com.connect.easypestcontrol.model.Customer;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FragmentLeads  extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    TextView title;
    RecyclerView customer_recycler;
    LeadsAdapter leadsAdapter;
    List<Customer> items;
    FloatingActionButton fb;
    View view;
    String dol_user_accessToken;
    View empty_layout;
    SwipeRefreshLayout swipe_layout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
         view =  inflater.inflate(R.layout.fragment_leads,container,false);
         title=view.findViewById(R.id.title_txt);
         title.setText("Today Leads");
        swipe_layout=view.findViewById(R.id.swipelayout);
        empty_layout=view.findViewById(R.id.emptyLayout);

        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));

        fb=view.findViewById(R.id.add_bt);
        fb.setOnClickListener(v -> startActivity(new Intent(getActivity(),CreateNewLeads.class)));


        Helper.sharedpreferences = getActivity().getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");

        customer_recycler=view.findViewById(R.id.customer_recycler);
        customer_recycler.setHasFixedSize(true);
        customer_recycler.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        customer_recycler.setNestedScrollingEnabled(false);



        getLeads();

        return  view;
    }



    public void getLeads(){

        Helper.loading(getActivity());
        swipe_layout.setRefreshing(false);
        JSONObject job = new JSONObject();
        try {
          //  job.put("accessToken",dol_user_accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Helper.lead_list_url,
                job, response -> {
            try {
                Helper.mProgressBarHandler.hide();
                JSONObject job1 = new JSONObject(String.valueOf(response));
                JSONArray jarr = job1.getJSONArray("data");
                Log.e("Data:",job1.toString());
                if(jarr.length()!=0) {
                    items = new ArrayList<>();
                    for (int v = 0; v < jarr.length(); v++) {
                        JSONObject obj = jarr.getJSONObject(v);
                        Customer customer=new Customer();

                        String dol_customer_id= obj.getString("_id");
                        String dol_customer_name= obj.getString("customerName");
                        String dol_mobile= obj.getString("mobileNumber");
                        String dol_email=obj.getString("email");
                        String dol_lead_source= obj.getString("leadSource");
                        String dol_lead_date= obj.getString("leadDate");
                        String dol_branch= obj.getString("branch");

                        customer.setId(dol_customer_id);
                        customer.setName(dol_customer_name);
                        customer.setEmail(dol_email);
                        customer.setMobile(dol_mobile);
                        customer.setLead_source(dol_lead_source);
                        customer.setLead_date(dol_lead_date);
                        customer.setLead_branch(dol_branch);
                        items.add(customer);

                        leadsAdapter=new LeadsAdapter(getContext(),items);
                        customer_recycler.setAdapter(leadsAdapter);
                        leadsAdapter.notifyDataSetChanged();
                        customer_recycler.setVisibility(View.VISIBLE);
                        empty_layout.setVisibility(View.GONE);

                    }
                }else{
                    customer_recycler.setVisibility(View.GONE);
                    empty_layout.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity(), "NO DATA", Toast.LENGTH_SHORT).show();

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
                                Log.e("res", res + "..");
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




        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                500000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onRefresh() {
        getLeads();
    }
}
