package com.location;


import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.utils.CommonClass;
import com.utils.GoogleLocationHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

public class LocationService extends Service {
    // private static LocationManager mLocationManager;
    private final static String TAG = "LocationService";

    private static final String NOTIFICATION_CHANNEL_ID = "1234";

    private Data data;
    private static final int NOTIFICATION_ID = 1011;
    private Handler locationHandle = new Handler();
    private Runnable locationRunable;
    private LocationDatabase db;
    private PowerManager.WakeLock wl = null;
    private Location mLastLocation = new Location("last");

    public static void startService(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LocationService.class));
        } else {
            context.startService(new Intent(context, LocationService.class));
        }
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, LocationService.class));
    }

    @Override
    public void onCreate() {

        db = new LocationDatabase(ApplicationController.getInstance());

        String distance = db.getDistance();
        double fDistance = 0.0f;

        if (distance.trim().length() > 0) {
            fDistance = Float.parseFloat(distance);
        }

        CommonClass.setPastDistance(ApplicationController.getInstance(), (float) fDistance);

        Log.d(TAG, "rebindCalled last distance = " + CommonClass.getPastDistance(ApplicationController.getInstance()));
        data = new Data();
        data.setRunning(true);
        data.setFirstTime(true);
        long time = SystemClock.elapsedRealtime();
        data.setTime(time);

        //tracker = new GPSTrackerLD(getApplicationContext());
        init();

        /*initHandler();*/
        initLocationHandler();
        createNotificationIcon();
        addWakeLock();
        // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        // CommonClass.getTrackingInterval(getBaseContext()), 0, this);
    }

    @SuppressLint("WakelockTimeout")
    private void addWakeLock() {
        try {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null) {
                wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "cync_lock");
                wl.acquire();
            }
        } catch (Exception e) {
        }
    }

    private void releaseWakeLock() {
        try {
            if (wl != null) {
                wl.release();
            }
        } catch (Exception e) {
        }
    }

    /*private void initHandler() {
        h = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                if (data.isRunning()) {
                    long times = SystemClock.elapsedRealtime() - time;
                    data.setTime(times);
                }
                h.postDelayed(this, 1000);
            }
        };
        h.postDelayed(r, 1000);
    }*/

    private void initLocationHandler() {
        locationHandle = new Handler();
        locationRunable = new Runnable() {
            @Override
            public void run() {
                if (data.isRunning()) {
                    /*try {*/
                    Location location = GoogleLocationHelper.mCurrentLocation;
                    if (location == null || location.getAccuracy() > 700) {
                        locationHandle.postDelayed(this, 3000);
                        return;
                    }

                        /* double lat = tracker.getLatitude();
                        double longt = tracker.getLongitude();
                        double speed = tracker.getSpeed();*/

                    /*Log.d("asd", "gps tracker = " + lat + " long " + longt);*/
                    /*Location location = tracker.getLastLocation();*/

                    if (data.isFirstTime()) {
                        mLastLocation.setLatitude(location.getLatitude());
                        mLastLocation.setLongitude(location.getLongitude());
                        data.setFirstTime(false);
                    } else {
                        if (mLastLocation.getLatitude() == location.getLatitude()
                                && mLastLocation.getLongitude() == location.getLongitude()) {
                            //both are same, no need to enter to database
                            locationHandle.postDelayed(this, 3000);
                            return;
                        }
                    }

                    mLastLocation.setLatitude(location.getLatitude());
                    mLastLocation.setLongitude(location.getLongitude());

                    double distance = mLastLocation.distanceTo(location);

                    data.addDistance(distance);

//						if (location.getAccuracy() < distance) {
//							data.addDistance(distance);
//							lastLat = currentLat;
//							lastLon = currentLon;
//						}

                    if (location.hasSpeed()) {
                        data.setCurSpeed(location.getSpeed() * 3.6);
                    }

                    Log.d(TAG, "distance = " + data.getDistance());
                    Log.i(TAG, "kp Duration = " + data.getTime());

                    if (!CommonClass.getLocationServicePreference(
                            ApplicationController.getInstance()).equalsIgnoreCase("false")) {

                        db.INSERT_NEW_MESSAGE(CommonClass.getUserpreference(ApplicationController.getInstance()).user_id,
                                String.valueOf(location.getLatitude()),
                                String.valueOf(location.getLongitude()),
                                "",
                                CommonClass.getCurrentTimeStamp(),
                                data.getCurrentSpeedKM(),
                                "" + data.getAverageSpeedInKM(),
                                "" + data.getDistanceKm(),
                                "" + (SystemClock.elapsedRealtime() - data.getTime()));

                        writeFile("" + String.valueOf(location.getLatitude()) + "," +
                                String.valueOf(location.getLongitude()) + "\n");

                        Intent intent = new Intent("com.cync.location.data"); //FILTER is a string to identify this intent
                        intent.putExtra("latitude", location.getLatitude());
                        intent.putExtra("longitude", location.getLongitude());
                        intent.putExtra("duration", data.getTime());
                        sendBroadcast(intent);
                        Log.i(TAG, "Send broadcast for Location Update.");
                    } else {
                        cancellAllNotifications();
                        db.UpdateLastRecord("" + (SystemClock.elapsedRealtime() - data.getTime()), "" + data.getDistanceKm());
                        Log.i(TAG, "Paused location Handler");
                    }
                   /* } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
                locationHandle.postDelayed(this, 3000/*
                        CommonClass.getTrackingInterval(getBaseContext())*/);
            }
        };
        locationHandle.postDelayed(locationRunable, 3000/*
                CommonClass.getTrackingInterval(getBaseContext())*/);
    }

    public void writeFile(String mValue) {
        String filename = Environment.getExternalStorageDirectory()
                .getPath() + "/cync_log.txt";
        File file = new File(filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.write(new Date().toString() + " " + mValue);
            writer.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // If we get killed, after returning from here, restart
        locationHandle.removeCallbacks(locationRunable);
        locationHandle.postDelayed(locationRunable, 1000);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }


    /* Remove the locationlistener updates when Services is stopped */
    @Override
    public void onDestroy() {

        GoogleLocationHelper.getGoogleLocationHelper(this).onDestroy(this);

        cancellAllNotifications();

        removeLocationUpdates();

        /*h.removeCallbacks(r);*/
        if (locationHandle != null && locationRunable != null) {
            locationHandle.removeCallbacks(locationRunable);
        }

        stopForeground(true);

        releaseWakeLock();
        super.onDestroy();
    }

    private void createNotificationIcon() {

        Intent intent = new Intent(ApplicationController.getContext(),
                NavigationDrawerActivity.class);
        intent.putExtra("notification_ride", "");

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent contentIntent = PendingIntent.getActivity(
                ApplicationController.getContext(), iUniqueId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifiBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getNotificationIcon())
                .setContentTitle("Cync Ride")
                .setContentText("Your ride is in progress...")
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setOngoing(true)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null
                && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
                && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Silent", importance);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setSound(null, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            notifiBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID/*ID of notification*/, notifiBuilder.build());
        }
    }

    public void cancellAllNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequestPeriodic;
    private SettingsClient mSettingsClient;
    private LocationBroadcastForOreo locationBroadcastForOreo;

    private void init() {
        if (mFusedLocationClient == null)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (mSettingsClient == null)
            mSettingsClient = LocationServices.getSettingsClient(this);

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(GoogleLocationHelper.UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setSmallestDisplacement(5);
        mLocationRequest.setFastestInterval(GoogleLocationHelper.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationRequestPeriodic = mLocationRequest;
        LocationSettingsRequest mLocationSettingsRequestPeriodic = builder.build();

        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequestPeriodic)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        if (!GoogleLocationHelper.checkLocationPermission(LocationService.this)) {
                            Log.i(TAG, "Permission Denied!! Location will not update");
                            return;
                        }

                        Log.i(TAG, "Starting location updates, periodic");

                        //Toast.makeText(context.getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        mFusedLocationClient.requestLocationUpdates(mLocationRequestPeriodic,
                                getPendingBroadcast(LocationService.this));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                //Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public static PendingIntent getPendingBroadcast(Context context) {
        Intent intent = new Intent(context, LocationBroadcastForOreo.class);
        intent.setAction(LocationBroadcastForOreo.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void removeLocationUpdates() {
        if (mFusedLocationClient != null) {
            mFusedLocationClient
                    .removeLocationUpdates(getPendingBroadcast(LocationService.this))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "Location updates stopped. Periodic");
                        }
                    });
        }
    }
}