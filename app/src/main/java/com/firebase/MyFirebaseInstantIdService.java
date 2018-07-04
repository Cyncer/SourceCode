package com.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.utils.CommonClass;

public class MyFirebaseInstantIdService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstantIdService.class.getSimpleName();


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //send refreshed token to our server using webservice
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        //save to shared preferences
        CommonClass.setDeviceToken(refreshedToken);

        //send refreshedToken to our server now using background

    }
}
