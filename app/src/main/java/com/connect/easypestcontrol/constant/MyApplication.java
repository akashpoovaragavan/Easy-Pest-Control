package com.connect.easypestcontrol.constant;


import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static Context ctx;
    @Override
    public void onCreate() {
        super.onCreate();
        ctx = this;
        VolleySingleton.handleSSLHandshake();

    }

}
