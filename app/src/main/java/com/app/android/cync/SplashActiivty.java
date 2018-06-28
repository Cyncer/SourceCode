package com.app.android.cync;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.cync.model.UserDetail;
import com.facebook.FacebookSdk;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;

import bolts.AppLinks;


/**
 * Created by ketul.patel on 18/1/16.
 */

public class SplashActiivty extends Activity {

    private static final String TAG = "SplashActiivty";
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    String ride_id = "";
    private BroadcastReceiver mReferrerReceived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.hasExtra("referrer"))
            ride_id = intent.getStringExtra("referrer");
            try
            {

                int tmp=Integer.parseInt(ride_id);

            }
            catch (NumberFormatException e)
            {
                ride_id="";
            }

            Log.i(TAG, "onReceive: ride_id=" + ride_id);
            //Toast.makeText(SplashActiivty.this, "" + ride_id, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplication());
        lbm.unregisterReceiver(mReferrerReceived);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FacebookSdk.sdkInitialize(this);

        Uri targetUrl =  AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null && ride_id.trim().length()==0) {


            // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
            //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();

            if(targetUrl.getQueryParameterNames().contains("ride_id"))
            {
                Log.i(TAG, "onNewIntent: received, with data: " + targetUrl.getQueryParameter("ride_id"));
                ride_id = targetUrl.getQueryParameter("ride_id");
                Log.i(TAG, "onCreate: targetUrl Ride id=" + ride_id);
            }



            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }


        if (ride_id.trim().length() == 0) {
            IntentFilter filter = new IntentFilter(InstallReferrerReceiver.REFERRER_RECEIVED);
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplication());
            lbm.registerReceiver(mReferrerReceived, filter);

            SharedPreferences prefs = SplashActiivty.this.getSharedPreferences("referrer", Context.MODE_PRIVATE);
            ride_id = prefs.getString("referrer", "");
        }

        if (ride_id.trim().length() == 0) {


//            FacebookSdk.sdkInitialize(this);
//            Uri targetUrl =
//                    AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
//            if (targetUrl != null) {
//                Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
//            } else {
//                AppLinkData.fetchDeferredAppLinkData(
//                        SplashActiivty.this,
//                        new AppLinkData.CompletionHandler() {
//                            @Override
//                            public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
//                                //process applink data
//                            }
//                        });
//            }

            //Log.i(TAG, "onNewIntent: received, with data: " + getIntent().getData().getQueryParameterNames());


            if (getIntent() != null && getIntent().getData() != null && (getIntent().getData().getPath().equals("/cyncapp/share-link/"))) {


                Uri data = getIntent().getData();
                if (data != null) {
                    // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                    //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();


                    Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
                    ride_id = data.getQueryParameter("ride_id");
                    Log.i(TAG, "onCreate: Ride id=" + ride_id);


                }

            }

           else  if (getIntent() != null && getIntent().getData() != null && (getIntent().getData().getPath().equals("/cyncapp/ride-map/"))) {


                Uri data = getIntent().getData();
                if (data != null) {
                    // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                    //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();


                    Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
                    ride_id = data.getQueryParameter("ride_id");
                    Log.i(TAG, "onCreate: Ride id=" + ride_id);


                }

            }


            else if (getIntent() != null && getIntent().getData() != null && (getIntent().getData().getPath().equals("/cync/share-link/"))) {


                Uri data = getIntent().getData();
                if (data != null) {
                    // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                    //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
                    ride_id = data.getQueryParameter("ride_id");
                    Log.i(TAG, "onCreate: Ride id=" + ride_id);

                }

            }

        }

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                UserDetail userDetail = CommonClass.getUserpreference(SplashActiivty.this);
                Intent intent = null;
                ConnectionDetector cd = new ConnectionDetector(SplashActiivty.this);

                //  CommonClass.showLoading(SplashActiivty.this);


                SharedPreferences prefs = SplashActiivty.this.getSharedPreferences("referrer", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("referrer", "");
                editor.apply();


                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {
                    if (userDetail != null && userDetail.first_name.length() > 0 && !userDetail.first_name.equalsIgnoreCase("null")) {
                        intent = new Intent(SplashActiivty.this, NavigationDrawerActivity.class);
                        intent.setPackage(SplashActiivty.this.getPackageName());
                        intent.putExtra("notification", "");
                        intent.putExtra("ride_id", ride_id);
                    } else {
                        intent = new Intent(SplashActiivty.this, LoginActivity.class);
                        intent.putExtra("ride_id", ride_id);
                    }
                    if (intent != null) {

                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        // close this activity
                        finish();
                    }
                } else
                    CommonClass.ShowToast(SplashActiivty.this, "Please Check Your Internet Connection.");


            }
        }, SPLASH_TIME_OUT);

        // ATTENTION: This was auto-generated to handle app links.

            }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);


        SharedPreferences prefs = SplashActiivty.this.getSharedPreferences("referrer", Context.MODE_PRIVATE);
        ride_id = prefs.getString("referrer", "");

        if (ride_id.trim().length() == 0) {


            Bundle bundle = intent.getExtras();
            if (bundle != null) {

                Log.i(TAG, "onNewIntent: received, with data: " + intent.getData().getQueryParameterNames());


                if (intent != null && intent.getData() != null && (intent.getData().getPath().equals("/cyncapp/share-link/"))) {

                    Uri data = intent.getData();
                    if (data != null) {
                        // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                        //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
                        ride_id = data.getQueryParameter("ride_id");
                        Log.i(TAG, "onCreate: Ride id=" + ride_id);
                    }

                }
                else if (intent != null && intent.getData() != null && (intent.getData().getPath().equals("/cyncapp/ride-map/"))) {

                    Uri data = intent.getData();
                    if (data != null) {
                        // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                        //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
                        ride_id = data.getQueryParameter("ride_id");
                        Log.i(TAG, "onCreate: Ride id=" + ride_id);
                    }

                }

                else if (getIntent() != null && getIntent().getData() != null && (getIntent().getData().getPath().equals("/cync/share-link/"))) {


                    Uri data = getIntent().getData();
                    if (data != null) {
                        // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                        //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();

                        Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
                        ride_id = data.getQueryParameter("ride_id");
                        Log.i(TAG, "onCreate: Ride id=" + ride_id);


                    }

                }


            }
        }

    }


}