package com.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.app.android.cync.R;
import com.utils.CommonClass;


public class BootTimeReceiver extends BroadcastReceiver {

    LocationDatabase db;

    @Override
    public void onReceive(Context ctxt, Intent i) {

        db = new LocationDatabase(ctxt);


        CommonClass.setLocationServiceStartStopPreference(
                ctxt, "false");
        CommonClass.setLocationServiceCurrentState(ctxt, R.drawable.btn_record);
        db.deleteAllData();
        CommonClass.clearPauseInterval(ctxt);

        CommonClass.clearPauseTime(ctxt);
        CommonClass.clearDuration(ctxt);
        CommonClass.clearPauseDistance(ctxt);
        CommonClass.clearPauseDistanceInterval(ctxt);
        CommonClass.clearStartTime(ctxt);
        CommonClass.clearStopTime(ctxt);
        CommonClass.clearPastDistance(ctxt);

//        Intent intent = new Intent(ctxt, LocationService.class);
//        ctxt.startService(intent);

    }

}
