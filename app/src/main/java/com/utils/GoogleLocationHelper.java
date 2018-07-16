package com.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GoogleLocationHelper {
    private static final String TAG = GoogleLocationHelper.class.getSimpleName();

    //private static GoogleLocationHelper instance = null;

    private static HashMap<Context, GoogleLocationHelper> helperHashMap = null;

    // location updates interval - 5sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3000;
    // fastest updates interval - 3 sec
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 3000;

    private static final int REQUEST_CHECK_SETTINGS_SINGLE = 1011;
    private static final int REQUEST_CHECK_SETTINGS_PERIODIC = 1012;

    public Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsClient mSettingsClient;

    private static Location mCurrentLocation;
    private long mLastUpdateTime;
    private boolean mRequestingLocationUpdates;

    //add location callbacks
    private HashMap<OnLocation, OnLocation> callBacksSingle = new HashMap<>();
    private HashMap<OnLocation, OnLocation> callBacksPeriodic = new HashMap<>();

    private GoogleLocationHelper() {
    }

    public static GoogleLocationHelper getGoogleLocationHelper(Context context) {
        if (helperHashMap == null)
            helperHashMap = new HashMap<>();

        if (helperHashMap.get(context) == null) {
            GoogleLocationHelper instance = new GoogleLocationHelper();
            helperHashMap.put(context, instance);
        }

        helperHashMap.get(context).context = context;
        return helperHashMap.get(context);
    }

    private void start() {
        if (!checkLocationPermission()) {
            return;
        }

        if (mCurrentLocation == null ||
                (System.currentTimeMillis() - mLastUpdateTime) > 6 * 60 * 60 * 1000L) { //3 hour
            init();
            createLocationRequest(true);
            startLocationUpdates(false);
        }
    }

    public void singleLocation(OnLocation onLocationSingle) {
        if (!checkLocationPermission()) {
            return;
        }

        if (mCurrentLocation == null ||
                (System.currentTimeMillis() - mLastUpdateTime) > 6 * 60 * 60 * 1000L) { //3 hour
            if (onLocationSingle != null)
                callBacksSingle.put(onLocationSingle, onLocationSingle);
            init();
            createLocationRequest(false);
            startLocationUpdates(false);
        } else {
            if (onLocationSingle != null) {
                onLocationSingle.onLocation(mCurrentLocation);
            }
        }
    }

    public void periodicLocation(OnLocation onLocation) {
        if (!checkLocationPermission()) {
            return;
        }

        if (onLocation != null)
            callBacksPeriodic.put(onLocation, onLocation);

        init();
        createLocationRequest(true);
        startLocationUpdates(true);
    }

    public static Location getLocationDirect() {
        return mCurrentLocation;
    }

    private Location getLocation() {
        if (mCurrentLocation == null) {
            singleLocation(null);
        } else {
            return mCurrentLocation;
        }
        return null;
    }

    private void init() {
        if (context == null)
            return;

        if (mFusedLocationClient == null)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        if (mSettingsClient == null)
            mSettingsClient = LocationServices.getSettingsClient(context);
    }

    private void createLocationRequest(boolean high) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        if (high)
            mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(high ? LocationRequest.PRIORITY_HIGH_ACCURACY
                : LocationRequest.PRIORITY_LOW_POWER);

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

            Log.i(TAG, "Location found, periodic" + mCurrentLocation);


            Iterator it = callBacksPeriodic.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                if (pair.getValue() != null) {
                    ((OnLocation) pair.getValue()).onLocation(locationResult.getLastLocation());
                } else {
                    it.remove();
                }
            }

            if (callBacksPeriodic.size() == 0) {
                stopLocationUpdates(true);
            }
        }
    };

    private LocationCallback mLocationCallbackSingle = new LocationCallback() {
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

            Log.i(TAG, "Location found ->" + mCurrentLocation.toString());

            Iterator it = callBacksSingle.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue());
                if (pair.getValue() != null) {
                    ((OnLocation) pair.getValue()).onLocation(locationResult.getLastLocation());
                }
                //notify and remove call backs
                it.remove();
            }

            //stop after getting single location
            stopLocationUpdates(false);
        }
    };


    private void startLocationUpdates(final boolean periodic) {
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

                        Log.i(TAG, "Starting location updates, periodic?" + periodic);

                        //Toast.makeText(context.getApplicationContext(), "Started location updates!", Toast.LENGTH_SHORT).show();
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                !periodic ? mLocationCallbackSingle : mLocationCallback, Looper.myLooper());
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

                                    if (context instanceof Activity) {
                                        rae.startResolutionForResult((Activity) context, !periodic ? REQUEST_CHECK_SETTINGS_SINGLE
                                                : REQUEST_CHECK_SETTINGS_PERIODIC);
                                    }
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

    private void stopLocationUpdates(final boolean periodic) {
        // Removing location updates

        if (((periodic && mLocationCallback != null) || !periodic && mLocationCallbackSingle != null)
                && mFusedLocationClient != null) {
            mFusedLocationClient
                    .removeLocationUpdates(periodic ? mLocationCallback : mLocationCallbackSingle)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i(TAG, "Location updates stopped. Periodic? " + periodic);
                        }
                    });
        }
    }

    public void onDestroy(Context activity) {
        stopLocationUpdates(true);
        stopLocationUpdates(false);
        if (helperHashMap != null && activity != null) {
            helperHashMap.remove(activity);
        }
    }

    /*This is onActivity Result*/
    public static boolean checkIsGoogleLocationActivityResult(int requestCode) {
        return requestCode == REQUEST_CHECK_SETTINGS_SINGLE || requestCode == REQUEST_CHECK_SETTINGS_PERIODIC;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS_SINGLE:
            case REQUEST_CHECK_SETTINGS_PERIODIC:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationUpdates() gets called in onResume again.
                        if (requestCode == REQUEST_CHECK_SETTINGS_SINGLE) {
                            singleLocation(null);
                        } else {
                            periodicLocation(null);
                        }
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

    public static interface OnLocation {
        void onLocation(Location location);
    }

}
