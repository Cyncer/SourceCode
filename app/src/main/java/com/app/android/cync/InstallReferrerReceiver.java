package com.app.android.cync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.analytics.CampaignTrackingReceiver;

public class InstallReferrerReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "ReferrerReceiver";
    public static final String REFERRER_RECEIVED = BuildConfig.APPLICATION_ID + ".action.REFERRER_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.i(LOG_TAG, "onReceive: Called method on receive");
        if ("com.android.vending.INSTALL_REFERRER".equals(action)) {
            Bundle args = intent.getExtras();

            if (args == null) {
                return;
            }
            new CampaignTrackingReceiver().onReceive(context, intent);
            for (String key : args.keySet()) {
                Log.d(LOG_TAG,"=====>>  "+ key + " = " + args.get(key));
            }

            String referrer = args.getString("referrer");


            try
            {

                int tmp=Integer.parseInt(referrer);

            }
            catch (NumberFormatException e)
            {
                referrer="";
            }


            if (!TextUtils.isEmpty(referrer)) {
                saveReferrer(context, referrer);
                LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context.getApplicationContext());
                Intent referrerReceived = new Intent(REFERRER_RECEIVED);
                referrerReceived.putExtra("referrer", referrer);
                lbm.sendBroadcast(referrerReceived);

            }
        }
    }



    public static void saveReferrer(Context context, String referrer) {
        SharedPreferences prefs = context.getSharedPreferences("referrer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("referrer", referrer);
        editor.apply();
    }

    public static String getSavedReferrer(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("referrer", Context.MODE_PRIVATE);
        return prefs.getString("referrer", null);
    }
}
