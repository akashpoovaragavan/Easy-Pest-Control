package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileScreen extends AppCompatActivity {
    ImageView back;
    TextView title,name,number,role,branch,logout;
    LinearLayout switch_acc;
    CircleImageView profile,pro1;
    String login_role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        title=findViewById(R.id.title_txt);
        back=findViewById(R.id.back);
        title.setText("Profile");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        name=findViewById(R.id.employ_name);
        number=findViewById(R.id.emp_number);
        role=findViewById(R.id.emp_role);
        branch=findViewById(R.id.emp_branch);
        profile=findViewById(R.id.profile_pic);
        pro1=findViewById(R.id.pro1);
        switch_acc=findViewById(R.id.switch_acc);
        logout=findViewById(R.id.logout);
        Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
        name.setText(Helper.sharedpreferences.getString("dol_emp_name",""));
        number.setText(Helper.sharedpreferences.getString("dol_user_mobileNumber",""));
        role.setText(Helper.sharedpreferences.getString("dol_user_role",""));
        branch.setText(Helper.sharedpreferences.getString("dol_user_branch",""));
        login_role=Helper.sharedpreferences.getString("login_role","");
        if (Helper.sharedpreferences.getString("dol_user_profile",null)!=null  ) {
            Picasso.get().load(Helper.sharedpreferences.getString("dol_user_profile", null)).into(profile);
            Picasso.get().load(Helper.sharedpreferences.getString("dol_user_profile", null)).into(pro1);
        }
        if (role.getText().toString().equals("Both")){
            switch_acc.setVisibility(View.VISIBLE);
            role.setText("Sales and Service");
        }else{
            switch_acc.setVisibility(View.GONE);
        }
        switch_acc.setOnClickListener(v -> {
            if(login_role.equals("Sales")) {
                startActivity(new Intent(ProfileScreen.this, HomeScreenService.class));
            }else{
                startActivity(new Intent(ProfileScreen.this, HomeScreenSales.class));
            }
            finish();
        });
        logout.setOnClickListener(v -> {
            SharedPreferences.Editor ed = Helper.sharedpreferences.edit();
            ed.clear();
            ed.apply();
            startActivity(new Intent(ProfileScreen.this, LoginScreen.class));
            finish();
        });
    }
}