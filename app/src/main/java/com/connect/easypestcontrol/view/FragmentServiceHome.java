package com.connect.easypestcontrol.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentServiceHome extends Fragment {
    View view;
    ImageView more;
    CardView cv1,cv2,cv3,cv4;
    TextView name,number;
    TextView accepted,declined,ongoing,nextTask,missed,cancelled,completed,notCompleted;
    CircleImageView profile;
    String dol_user_role,dol_user_accessToken;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fragment_service_home,container,false);
        cv1=view.findViewById(R.id.cv1);
        cv2=view.findViewById(R.id.cv2);
        cv3=view.findViewById(R.id.cv3);
        cv4=view.findViewById(R.id.cv4);
        more=view.findViewById(R.id.more);
        name=view.findViewById(R.id.service_man_name);
        number=view.findViewById(R.id.service_man_num);
        profile=view.findViewById(R.id.vendor_img);
        accepted=view.findViewById(R.id.accepted);
        declined=view.findViewById(R.id.declined);
        ongoing=view.findViewById(R.id.ongoing);
        nextTask=view.findViewById(R.id.nextTask);
        missed=view.findViewById(R.id.missed);
        cancelled=view.findViewById(R.id.cancelled);
        completed=view.findViewById(R.id.completed);
        notCompleted=view.findViewById(R.id.notCompleted);



        Helper.sharedpreferences = getActivity().getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        dol_user_role = Helper.sharedpreferences.getString("dol_user_role","");
        dol_user_accessToken=Helper.sharedpreferences.getString("dol_user_accessToken","");
        name.setText(Helper.sharedpreferences.getString("dol_emp_name",""));
        number.setText(Helper.sharedpreferences.getString("dol_user_mobileNumber",""));
        SharedPreferences.Editor ed = Helper.sharedpreferences.edit();

        more.setOnClickListener(v -> startActivity(new Intent(getActivity(),ProfileScreen.class)));
        cv1.setOnClickListener(v -> {
            ed.putString("PDF", "Service");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportServiceScreen.class));
        });

        cv2.setOnClickListener(v ->  {
            ed.putString("PDF", "InProgress");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportServiceScreen.class));
        });
        cv3.setOnClickListener(v ->  {
            ed.putString("PDF", "Missed");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportServiceScreen.class));
        });
        cv4.setOnClickListener(v ->  {
            ed.putString("PDF", "Completed");
            ed.apply();
            startActivity(new Intent(getActivity(),ReportServiceScreen.class));
        });
        getDashboard();

        return view;
    }

    private void getDashboard() {
        // Helper.loading(getActivity());
        JSONObject job = new JSONObject();

        try {
            job.put("role","Service Engineer");

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
                            //Helper.mProgressBarHandler.hide();
                        });

                        Log.e("Dashboard", String.valueOf(response));

                        JSONObject obj = new JSONObject(response.toString());
                        String dol_user_profile = obj.getString("profile");
                        Picasso.get().load(dol_user_profile).into(profile);

                        JSONObject obj1=obj.getJSONObject("status");

                        accepted .setText(obj1.getString("accepted").trim());
                        declined.setText(obj1.getString("declined"));
                        ongoing.setText(obj1.getString("onGoing"));
                        nextTask.setText(obj1.getString("nextTask"));
                        missed.setText(obj1.getString("taskMissed"));
                        cancelled.setText(obj1.getString("cancelled"));
                        completed.setText(obj1.getString("completed"));
                        notCompleted.setText(obj1.getString("notCompleted"));

                        Log.e("ACCEPT",obj1.getString("accepted"));

                        SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
                        ed.putString("dol_user_profile", dol_user_profile );
                        ed.putString("login_role","ServiceEngineer");
                        ed.apply();

                    }catch (Exception e){
                        e.printStackTrace();
                        // Helper.mProgressBarHandler.hide();
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


}
