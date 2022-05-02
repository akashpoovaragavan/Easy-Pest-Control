package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.constant.Helper;

public class SplashScreen extends AppCompatActivity {
    boolean login;
    String dol_user_role;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Helper.sharedpreferences = getSharedPreferences(Helper.MyPREFERENCES, Context.MODE_PRIVATE);
                login=Helper.sharedpreferences.getBoolean("FIRSTTIME_LOGIN",false);
                dol_user_role=Helper.sharedpreferences.getString("dol_user_role","");
                Intent intent;
                if(!login){
                    intent = new Intent(SplashScreen.this, LoginScreen.class);
                }else{
                    if (dol_user_role.equals("Service Engineer")) {
                        intent=new Intent(SplashScreen.this, HomeScreenService.class);
                    }else {
                        intent=new Intent(SplashScreen.this, HomeScreenSales.class);
                    }
                }
                startActivity(intent);
                overridePendingTransition(R.anim.right_enter,R.anim.left_out);
                finish();
            }
        },2000);


    }
}