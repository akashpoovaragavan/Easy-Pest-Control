package com.connect.easypestcontrol.view;

import android.content.Context;
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
import com.connect.easypestcontrol.controller.TabSelectedAdapter;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FragmentService extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    TextView title;
    ViewPager viewPager;
    TabLayout tabLayout;
    View view;
    String dol_user_accessToken;
   // JSONArray jarr1,jarr2,jarr3;
   SwipeRefreshLayout swipe_layout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fragment_service,container,false);
        title=view.findViewById(R.id.title_txt);
        title.setText("Service");
        viewPager=view.findViewById(R.id.view_pager);
        tabLayout=view.findViewById(R.id.tablayout);

        swipe_layout=view.findViewById(R.id.swipelayout);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));


        Helper.sharedpreferences = getActivity().getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
/*
        service_list_today();
        service_list_week();
        service_list_month();
        setUpViewPager(viewPager);*/

        return view;
    }

  /*  /// Service Today

    public void service_list_today() {
        try {
            Helper.loading(getActivity());
            JSONObject job = new JSONObject();

            job.put("option", "today");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.serviceWork_list_url,
                    job, response -> {
                try {
                    Helper.mProgressBarHandler.hide();
                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jarr1 = new JSONArray();
                    jarr1 = job1.getJSONArray("data");
                    Log.e("Service Today", job1.toString());
                    //setUpViewPager(viewPager);

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

    /// Service Today

    public void service_list_week() {
        try {
            Helper.loading(getActivity());
            JSONObject job = new JSONObject();

            job.put("option", "week");

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Helper.serviceWork_list_url,
                    job, response -> {
                try {
                    Helper.mProgressBarHandler.hide();
                    JSONObject job1 = new JSONObject(String.valueOf(response));
                    jarr2 = new JSONArray();
                    jarr2 = job1.getJSONArray("data");
                    Log.e("Service Week", job1.toString());
                    //setUpViewPager(viewPager);

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
                    jarr3 = new JSONArray();
                    jarr3 = job1.getJSONArray("data");
                    Log.e("Service Month", job1.toString());
                    setUpViewPager(viewPager);

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

    /// END*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        swipe_layout.setRefreshing(false);
        TabSelectedAdapter selectedAdapter=new TabSelectedAdapter(getChildFragmentManager());
        selectedAdapter.addFragments(new FragmentServiceToday("ServiceToday"),"Today");
        selectedAdapter.addFragments(new FragmentServiceWeek("ServiceWeek"),"Week");
        selectedAdapter.addFragments(new FragmentServiceMonth("ServiceMonth"),"Month");
        viewPager.setAdapter(selectedAdapter);

    }

    @Override
    public void onRefresh() {
        setUpViewPager(viewPager);
    }
}
