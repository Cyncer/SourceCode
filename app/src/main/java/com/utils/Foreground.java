package com.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.ApplicationController;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Usage:
 * <p/>
 * 1. Get the Foreground Singleton, passing a Context or Application object unless you
 * are sure that the Singleton has definitely already been initialised elsewhere.
 * <p/>
 * 2.a) Perform a direct, synchronous check: Foreground.isForeground() / .isBackground()
 * <p/>
 * or
 * <p/>
 * 2.b) Register to be notified (useful in Service or other non-UI components):
 * <p/>
 * Foreground.Listener myListener = new Foreground.Listener(){
 * public void onBecameForeground(){
 * // ... whatever you want to do
 * }
 * public void onBecameBackground(){
 * // ... whatever you want to do
 * }
 * }
 * <p/>
 * public void onCreate(){
 * super.onCreate();
 * Foreground.get(this).addListener(listener);
 * }
 * <p/>
 * public void onDestroy(){
 * super.onCreate();
 * Foreground.get(this).removeListener(listener);
 * }
 */
public class Foreground implements Application.ActivityLifecycleCallbacks {

    public static final long CHECK_DELAY = 500;
    public static final String TAG = Foreground.class.getName();
    private static RequestQueue mQueue;
    private static Foreground instance;
    long start;
    long end;
    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private Runnable check;

    /**
     * Its not strictly necessary to use this method - _usually_ invoking
     * get with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in test harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @param application
     * @return an initialised Foreground instance
     */
    public static Foreground init(Application application) {
        if (instance == null) {
            instance = new Foreground();
            application.registerActivityLifecycleCallbacks(instance);
            mQueue = VolleySetup.getRequestQueue();
        }
        return instance;
    }

    public static Foreground get(Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException(
                    "Foreground is not initialised and " +
                            "cannot obtain the Application object");
        }
        return instance;
    }

    public static Foreground get() {
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke " +
                            "at least once with parameterised init/get");
        }
        return instance;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            Log.i(TAG, "went foreground");
            if (CommonClass.getTimer() > 0)
                CallApi();


            start = Calendar.getInstance().getTimeInMillis();


            for (Listener l : listeners) {
                try {
                    l.onBecameForeground();

                } catch (Exception exc) {
                    Log.e(TAG, "Listener threw exception!", exc);
                }
            }
        } else {
            Log.i(TAG, "still foreground");
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        handler.postDelayed(check = new Runnable() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;

                    end = Calendar.getInstance().getTimeInMillis();
                    Log.i(TAG, "went background== start=" + start);
                    Log.i(TAG, "went background== end=" + end);
                    Log.i(TAG, "went background== result=" + (int) ((end - start) / 1000) + " seconds");

                    int old = (int) ((end - start) / 1000);
                    int prefTime = CommonClass.getTimer();
                    CommonClass.setTimer(prefTime + old);

                    Log.i(TAG, "went background== Total=" + CommonClass.getTimer());
                    start = Calendar.getInstance().getTimeInMillis();

                    if (CommonClass.getTimer() > 0)
                        CallApi();

                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground();


                        } catch (Exception exc) {
                            Log.e(TAG, "Listener threw exception!", exc);
                        }
                    }
                } else {
                    Log.i(TAG, "still foreground");
                }
            }
        }, CHECK_DELAY);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    private void CallApi() {


        UserDetail userDetail = CommonClass.getUserpreference(ApplicationController.getContext());
        if (userDetail != null && userDetail.first_name.length() > 0 && !userDetail.first_name.equalsIgnoreCase("null"))
        {

            ConnectionDetector cd = new ConnectionDetector(ApplicationController.getContext());
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {
                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.updateTime + "user_id=" + CommonClass.getUserpreference(ApplicationController.getContext()).user_id + "&seconds=" + CommonClass.getTimer(), modelSuccessLisner(),
                        ErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();

                        return requestparam;
                    }
                };

                mQueue.add(apiRequest);

            }
        }

    }

    private com.android.volley.Response.Listener<String> modelSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);

                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {


                        CommonClass.setTimer(0);


                    } else {

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = ApplicationController.getContext().getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }


            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());


            }
        };
    }

    public interface Listener {

        void onBecameForeground();

        void onBecameBackground();

    }


}
