package com.connect.easypestcontrol.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.connect.easypestcontrol.constant.VolleySingleton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    View view;
    ImageView more;
    CardView cv1,cv2,cv3,cv4;
    TextView accepted,declined,converted,not_converted,followup,missed,assigned,not_assigned;
    TextView name,number;
    CircleImageView profile;
    String dol_user_role,dol_user_accessToken;
    SwipeRefreshLayout swipe_layout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fragment_home,container,false);
        cv1=view.findViewById(R.id.cv1);
        cv2=view.findViewById(R.id.cv2);
        cv3=view.findViewById(R.id.cv3);
        cv4=view.findViewById(R.id.cv4);
        more=view.findViewById(R.id.more);
        accepted=view.findViewById(R.id.accepted);
        declined=view.findViewById(R.id.declined);
        converted=view.findViewById(R.id.converted);
        not_converted=view.findViewById(R.id.not_converted);
        followup=view.findViewById(R.id.followup);
        missed=view.findViewById(R.id.missed);
        assigned=view.findViewById(R.id.assigned);
        not_assigned=view.findViewById(R.id.not_assigned);
        name=view.findViewById(R.id.name);
        number=view.findViewById(R.id.number);
        profile=view.findViewById(R.id.vendor_img);

        swipe_layout=view.findViewById(R.id.swipelayout);
       // emptyLayout=view.findViewById(R.id.emptyLayout);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));



        Helper.sharedpreferences = getActivity().getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_role = Helper.sharedpreferences.getString("dol_user_role","");
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
        name.setText(Helper.sharedpreferences.getString("dol_emp_name",""));
        number.setText(Helper.sharedpreferences.getString("dol_user_mobileNumber",""));

        getDashboard();
        SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
        
        more.setOnClickListener(v -> startActivity(new Intent(getActivity(),ProfileScreen.class)));
        cv1.setOnClickListener(v -> {
            ed.putString("PDF", "Lead");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportScreen.class));
        });

        cv2.setOnClickListener(v ->  {
            ed.putString("PDF", "Converted");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportScreen.class));
        });
        cv3.setOnClickListener(v ->  {
            ed.putString("PDF", "Follow");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportScreen.class));
        });
        cv4.setOnClickListener(v ->  {
            ed.putString("PDF", "Assign");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportScreen.class));
        });
        return view;
    }





    private void getDashboard() {
      // Helper.loading(getActivity());
        swipe_layout.setRefreshing(false);
        JSONObject job = new JSONObject();

        try {
            job.put("role","Sales Man");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("job", String.valueOf(job));
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Helper.dashboard_url,
                job,
                response -> {
                    try{
                        getActivity().runOnUiThread(() -> {
                           // Helper.mProgressBarHandler.hide();
                        });

                            Log.e("Dashboard", String.valueOf(response));

                            JSONObject obj = new JSONObject(response.toString());
                            String dol_user_profile = obj.getString("profile");
                            Picasso.get().load(dol_user_profile).into(profile);

                            JSONObject obj1=obj.getJSONObject("status");

                            accepted .setText(obj1.getString("accepted").trim());
                            declined.setText(obj1.getString("declined"));
                            converted.setText(obj1.getString("converted"));
                            not_converted.setText(obj1.getString("notConverted"));
                            followup.setText(obj1.getString("followUp"));
                            missed.setText(obj1.getString("missed"));
                            assigned.setText(obj1.getString("assigned"));
                            not_assigned.setText(obj1.getString("notAssigned"));

                            SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
                            ed.putString("dol_user_profile", dol_user_profile );
                            ed.putString("login_role","Sales");
                            ed.apply();

                    }catch (Exception e){
                        e.printStackTrace();
                      //  Helper.mProgressBarHandler.hide();
                    }
                    },
                    error -> {
                        try {
                           // Helper.mProgressBarHandler.hide();
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("err ",error.getMessage());
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

    @Override
    public void onRefresh() {
        getDashboard();
    }
}
