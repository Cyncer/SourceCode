package com.location;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ApplicationController;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.utils.CommonClass;
import com.utils.GoogleLocationHelper;

public class LocationService extends Service implements GoogleLocationHelper.OnLocation {
    // private static LocationManager mLocationManager;
    private final static String TAG = "LocationService";

    private static final String NOTIFICATION_CHANNEL_ID = "1234";

    private Location lastLocation = new Location("last");
    private Data data;
    private int notificationID = 1011;
    private double currentLon = 0;
    private double currentLat = 0;
    private double lastLon = 0;
    private double lastLat = 0;
    /*private Handler h = new Handler();
    private Runnable r;*/
    private Handler locationHandle = new Handler();
    private Runnable locationRunable;
    //private GPSTrackerLD tracker;
    private LocationDatabase db;

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

        GoogleLocationHelper.getGoogleLocationHelper(this).periodicLocation(this);

        /*initHandler();*/
        initLocationHandler();
        createNotificationIcon();
        // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        // CommonClass.getTrackingInterval(getBaseContext()), 0, this);
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
                    Location location = GoogleLocationHelper.getLocationDirect();
                    if (location == null || location.getAccuracy() > 700) {
                        locationHandle.postDelayed(this, 1000);
                        return;
                    }

                        /* double lat = tracker.getLatitude();
                        double longt = tracker.getLongitude();
                        double speed = tracker.getSpeed();*/

                    /*Log.d("asd", "gps tracker = " + lat + " long " + longt);*/
                    /*Location location = tracker.getLastLocation();*/
                    currentLat = location.getLatitude();
                    currentLon = location.getLongitude();

                    if (data.isFirstTime()) {
                        lastLat = currentLat;
                        lastLon = currentLon;
                        data.setFirstTime(false);
                    } else {
                        if (lastLocation.getLatitude() == currentLat
                                && lastLocation.getLongitude() == currentLon) {
                            //both are same, no need to enter to database
                            locationHandle.postDelayed(this,
                                    CommonClass.getTrackingInterval(getBaseContext()));
                            return;
                        }
                    }

                    lastLocation.setLatitude(lastLat);
                    lastLocation.setLongitude(lastLon);
                    double distance = lastLocation.distanceTo(location);

                    data.addDistance(distance);

                    lastLat = currentLat;
                    lastLon = currentLon;
//						if (location.getAccuracy() < distance) {
//							data.addDistance(distance);
//
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
                locationHandle.postDelayed(this,
                        CommonClass.getTrackingInterval(getBaseContext()));
            }
        };
        locationHandle.postDelayed(locationRunable,
                CommonClass.getTrackingInterval(getBaseContext()));
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

        /*h.removeCallbacks(r);*/
        if (locationHandle != null && locationRunable != null) {
            locationHandle.removeCallbacks(locationRunable);
        }

        stopForeground(true);

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

        if (notificationManager != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
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
            notificationManager.notify(notificationID/*ID of notification*/, notifiBuilder.build());
        }


    }

    public void cancellAllNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null)
            mNotificationManager.cancel(notificationID);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.notification_icon : R.mipmap.ic_launcher;
    }

    /*From GoogleLocationHelper*/
    @Override
    public void onLocation(Location location) {
        if (location == null) return;
        Log.i(TAG, "Location service ->" + location.toString());
    }
}