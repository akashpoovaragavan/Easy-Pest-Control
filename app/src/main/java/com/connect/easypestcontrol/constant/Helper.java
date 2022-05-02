package com.connect.easypestcontrol.constant;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class Helper {
    public static ProgressBarHandler mProgressBarHandler;
    public static SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "easypestcontrol" ;
    public static String onback = "";

    /**************************API***********************************/
    public static String login_url              = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/auth";
    public static String dashboard_url          = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/dashboard";
    public static String lead_list_url          = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/lead";
    public static String create_lead_url        = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/lead/create";
    public static String lead_update_url        = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/lead/update";
    public static String sales_list_url         = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/lead/salesMan/list";
    public static String service_list_url       = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/lead/serviceEngineer/list";
    public static String converted_list_url     = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/converted";
    public static String converted_update_url   = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/converted/update";
    public static String follow_list_url        = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/followUp";
    public static String follow_update_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/followUp/update";
    public static String assigned_list_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/assigned";
    public static String assigned_update_url    = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/assigned/update";
    public static String serviceWork_list_url   = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/service";
    public static String serviceWork_update_url = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/service/update";
    public static String task_url               = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/task";
    public static String task_update_url        = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/task/update";
    public static String completed_list_url     = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/completed";
    public static String lead_pdf_url           = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/lead";
    public static String lead_sts_url           = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/lead/stats";
    public static String converted_pdf_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/converted";
    public static String converted_sts_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/converted/stats";
    public static String follow_pdf_url         = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/followUp";
    public static String follow_sts_url         = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/followUp/stats";
    public static String assign_pdf_url         = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/assigned";
    public static String assign_sts_url         = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/assigned/stats";
    public static String completed_pdf_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/completed/individual/";
    public static String service_pdf_url        = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/service";
    public static String service_sts_url        = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/service/stats";
    public static String inProcess_sts_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/inProgress/stats";
    public static String inProcess_pdf_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/inProgress";
    public static String cancelled_sts_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/cancelled/stats";
    public static String cancelled_pdf_url      = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/cancelled";
    public static String finished_pdf_url       = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/completed";
    public static String finished_sts_url       = "https://xyyg28gpl5.execute-api.us-east-1.amazonaws.com/api/mobile/invoice/completed/stats";


    public static void loading(Activity activity) {
        mProgressBarHandler = new ProgressBarHandler(activity); // In onCreate
        mProgressBarHandler.show(); // To show the progress bar
    }
    public static class ProgressBarHandler {

        private Context mContext;
        private ProgressBar mProgressBar;
        public ProgressBarHandler(Activity context) {
            mContext = context;
            ViewGroup layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();
            mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleLarge);
            mProgressBar.setIndeterminate(true);
            RelativeLayout.LayoutParams params = new
                    RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            RelativeLayout rl = new RelativeLayout(context);
            rl.setGravity(Gravity.CENTER);
            rl.addView(mProgressBar);
            layout.addView(rl, params);
            hide();
        }
        public void show() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        public void hide() {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
