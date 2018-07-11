package com.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class GoogleLocationHelper {
    private static final String TAG = GoogleLocationHelper.class.getSimpleName();

    private static GoogleLocationHelper instance = null;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    // fastest updates interval - 5 sec
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 1011;

    public Activity context;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;

    private Location mCurrentLocation;
    private long mLastUpdateTime;
    private boolean mRequestingLocationUpdates;

    private GoogleLocationHelper() {
    }

    public static GoogleLocationHelper getGoogleLocationHelper(Activity context) {
        if (instance == null)
            instance = new GoogleLocationHelper();
        instance.context = context;
        return instance;
    }

    public void start() {
        if (!checkLocationPermission()) {
            return;
        }

        if (mCurrentLocation == null ||
                (System.currentTimeMillis() - mLastUpdateTime) > 6 * 60 * 60 * 1000L) { //3 hour
            init();
            createLocationRequest();
            startLocationUpdates();
        }
    }

    public Location getLocation() {
        if (mCurrentLocation == null) {
            start();
        } else {
            return mCurrentLocation;
        }
        return null;
    }

    private void init() {
        if (context == null)
            return;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        mSettingsClient = LocationServices.getSettingsClient(context);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            // location is received
            mCurrentLocation = locationResult.getLastLocation();
            mLastUpdateTime = System.currentTimeMillis();

            CommonClass.setPrefranceByKey_Value(context,
                    CommonClass.KEY_PREFERENCE_CURRENT_LOCATION,
                    CommonClass.KEY_Current_Lat, String.valueOf(mCurrentLocation.getLatitude()));
            CommonClass.setPrefranceByKey_Value(context,
                    CommonClass.KEY_PREFERENCE_CURRENT_LOCATION,
                    CommonClass.KEY_Current_Lng, String.valueOf(mCurrentLocation.getLongitude()));

            Toast.makeText(context, "Location found successfully. You can now proceed.",
                    Toast.LENGTH_SHORT).show();
            //stop after getting location
            stopLocationUpdates();
        }
    };

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        if (!checkLocationPermission()) {
                            Log.i(TAG, "Permission Denied!! Location will not update");
                            return;
                        }

                        //Toast.makeText(context.getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
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
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(context, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
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

    private void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Toast.makeText(context, "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Location updates stopped.");
                    }
                });
    }

    /*This is onActivity Result*/
    public static boolean checkIsGoogleLocationActivityResult(int requestCode) {
        return requestCode != REQUEST_CHECK_SETTINGS;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        start();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission Denied!! Location will not update");
            return false;
        }
        return true;
    }

}
