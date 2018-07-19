package com.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationResult;
import com.utils.GoogleLocationHelper;

public class LocationBroadcastForOreo extends BroadcastReceiver {
    private static final String TAG = "LocationBroadForOreo";
    static final String ACTION_PROCESS_UPDATES = "com.app.android.cync.PROCESS_LOC_UPDATES";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    GoogleLocationHelper.mCurrentLocation = result.getLastLocation();
                    GoogleLocationHelper.mLastUpdateTime = System.currentTimeMillis();
                    Log.i(TAG, "OREO Location->" + GoogleLocationHelper.mCurrentLocation);
                }
            }
        }
    }
}
