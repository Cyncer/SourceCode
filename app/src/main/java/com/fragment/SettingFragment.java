
package com.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.rey.material.widget.Switch;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class SettingFragment extends BaseContainerFragment {

    Activity mActivity;
    private RequestQueue mQueue;

    public SettingFragment() {
        // Required empty public constructor
    }

    private Switch swNotification, swLocation, swViewRides, swHideTopSpeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = VolleySetup.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        Init(rootView);
        return rootView;
    }


    private com.android.volley.Response.Listener<String> locationSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.e("Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message, user_id;
                String location_type;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        JSONObject jresObject = jresObjectMain.getJSONObject("result");
                        user_id = "" + CommonClass.getDataFromJsonInt(jresObject, "user_id");
                        location_type = CommonClass.getDataFromJson(jresObject, "location_type");
//                        UserDetail ud = CommonClass.getUserpreference(getActivity());
//                        UserDetail tud = new UserDetail(ud.user_id, ud.user_image, ud.first_name, ud.last_name, ud.login_type, ud.email, notification_type);
//                        CommonClass.setUserpreference(getActivity(), tud);
                        if (location_type.equalsIgnoreCase("on"))
                            swLocation.setChecked(true);
                        else
                            swLocation.setChecked(false);

//                        Log.e("-------",""+ud.user_id+" "+ud.user_image+" "+ud.first_name+" "+ud.last_name+" "+ ud.login_type+" "+ ud.email+" "+ notification_type);
                    } else {
                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }
                CommonClass.ShowToast(mActivity, message);
            }
        };
    }

    private com.android.volley.Response.Listener<String> getSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.e("Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message, user_id, location_type;
                boolean notification_type, view_my_rides, hide_top_speed;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        JSONObject jresObject = jresObjectMain.getJSONObject("result");

                        notification_type = CommonClass.getDataFromJsonBoolean(jresObject, "notification_type");
                        location_type = CommonClass.getDataFromJson(jresObject, "display_location");
                        view_my_rides = CommonClass.getDataFromJsonBoolean(jresObject, "view_my_rides");
                        hide_top_speed = CommonClass.getDataFromJsonBoolean(jresObject, "hide_top_speed");


                        UserDetail ud = CommonClass.getUserpreference(getActivity());
                        UserDetail tud = new UserDetail(ud.user_id, ud.user_image, ud.first_name, ud.last_name, ud.login_type, ud.email, notification_type, view_my_rides, hide_top_speed);
                        CommonClass.setUserpreference(getActivity(), tud);
                        swNotification.setChecked(notification_type);
                        swViewRides.setChecked(view_my_rides);
                        swHideTopSpeed.setChecked(hide_top_speed);

                        if (location_type.equalsIgnoreCase("on"))
                            swLocation.setChecked(true);
                        else
                            swLocation.setChecked(false);


//                        Log.e("-------",""+ud.user_id+" "+ud.user_image+" "+ud.first_name+" "+ud.last_name+" "+ ud.login_type+" "+ ud.email+" "+ notification_type);
                    } else {
                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(mActivity, message);
                    e.printStackTrace();
                }

                setChangeListner();

            }
        };
    }


    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.e("Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message, user_id;
                boolean notification_type, view_my_rides, hide_top_speed;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {


                        JSONObject jresObject = null;

                        if(jresObjectMain.has("result"))
                        {
                            jresObject=  jresObjectMain.getJSONObject("result");
                        }
                        else
                            jresObject=jresObjectMain.getJSONObject("data");

                        user_id = "" + CommonClass.getDataFromJsonInt(jresObject, "user_id");
                        notification_type = CommonClass.getDataFromJsonBoolean(jresObject, "notification_type");

                        view_my_rides = CommonClass.getDataFromJsonBoolean(jresObject, "view_my_rides");
                        hide_top_speed = CommonClass.getDataFromJsonBoolean(jresObject, "hide_top_speed");


                        UserDetail ud = CommonClass.getUserpreference(getActivity());
                        UserDetail tud = new UserDetail(ud.user_id, ud.user_image, ud.first_name, ud.last_name, ud.login_type, ud.email, notification_type, view_my_rides, hide_top_speed);
                        CommonClass.setUserpreference(getActivity(), tud);

                        if (isChanged.equalsIgnoreCase("notification"))
                            swNotification.setChecked(notification_type);
                        else if (isChanged.equalsIgnoreCase("viewRides"))
                            swViewRides.setChecked(view_my_rides);
                        else if (isChanged.equalsIgnoreCase("hide_speed"))
                            swHideTopSpeed.setChecked(hide_top_speed);


//                        Log.e("-------",""+ud.user_id+" "+ud.user_image+" "+ud.first_name+" "+ud.last_name+" "+ ud.login_type+" "+ ud.email+" "+ notification_type);
                    } else {
                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }
                CommonClass.ShowToast(mActivity, message);
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity);
                CommonClass.ShowToast(mActivity, getResources().getString(R.string.s_wrong));
            }
        };
    }

    private com.android.volley.Response.ErrorListener getErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setChangeListner();
                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity);
                CommonClass.ShowToast(mActivity, getResources().getString(R.string.s_wrong));
            }
        };
    }


    private void updateLocationSetting(final String swRespose) {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.update_settingUrl, locationSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", CommonClass.getUserpreference(mActivity).user_id);
                    requestparam.put("display_location", "" + swRespose);
//                Log.i("updateSetting", "Notification Type" + requestparam);
                    Log.e("updateSetting", "==> " + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(mActivity);
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }


    private void getSetting() {

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_settingUrl, getSuccessLisner(),
                    getErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", CommonClass.getUserpreference(mActivity).user_id);

                    return requestparam;
                }
            };
            CommonClass.showLoading(mActivity);
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }


    private void updateSetting(final boolean swRespose, final boolean swViewRides, final boolean swTopSpeed,String type) {


        String url=Constants.getAllNotification;

        if(type.trim().length()>0)
        {
            url=Constants.update_settingUrl;
        }

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST,url, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", CommonClass.getUserpreference(mActivity).user_id);
                    requestparam.put("notification_type", "" + swRespose);
                    requestparam.put("view_my_rides", "" + swViewRides);
                    requestparam.put("hide_top_speed", "" + swTopSpeed);
//                Log.i("updateSetting", "Notification Type" + requestparam);
                    Log.e("updateSetting", "==> " + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(mActivity);
            mQueue.add(apiRequest);

        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private void Init(View view) {
        swNotification = (Switch) view.findViewById(R.id.swNotification);
        swLocation = (Switch) view.findViewById(R.id.swLocation);
        swViewRides = (Switch) view.findViewById(R.id.swViewRides);
        swHideTopSpeed = (Switch) view.findViewById(R.id.swHideTopSpeed);


        //Log.e("-----------",""+CommonClass.getUserpreference(getActivity()).notification);
        // CommonClass.ShowToast(mActivity,"Notification type"+CommonClass.getUserpreference(getActivity()).notification);
//        swLocation.setChecked(CommonClass.getUserpreference(getActivity()).notification);
//        swNotification.setChecked(CommonClass.getUserpreference(getActivity()).notification);

        getSetting();

    }


    String isChanged = "";

    public void setChangeListner() {
        swNotification.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                Log.e("", "==>        " + checked);
                isChanged = "notification";
                updateSetting(checked, swViewRides.isChecked(), swHideTopSpeed.isChecked(),"");
//                if (checked)
//                    CommonClass.ShowToast(mActivity, "Notifications turned ON.");
//                else
//                    CommonClass.ShowToast(mActivity, "Notifications turned OFF.");
            }
        });

        swViewRides.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                isChanged = "viewRides";
                Log.e("", "==>        " + checked);
                updateSetting(swNotification.isChecked(), checked, swHideTopSpeed.isChecked(),"update");
//                if (checked)
//                    CommonClass.ShowToast(mActivity, "Notifications turned ON.");
//                else
//                    CommonClass.ShowToast(mActivity, "Notifications turned OFF.");
            }
        });


        swHideTopSpeed.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                Log.e("", "==>        " + checked);
                isChanged = "hide_speed";
                updateSetting(swNotification.isChecked(), swViewRides.isChecked(), checked,"update");
//                if (checked)
//                    CommonClass.ShowToast(mActivity, "Notifications turned ON.");
//                else
//                    CommonClass.ShowToast(mActivity, "Notifications turned OFF.");
            }
        });


        swLocation.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                Log.e("", "==>        " + checked);

                if (checked)
                    updateLocationSetting("on");
                else
                    updateLocationSetting("off");
//                if (checked)
//                    CommonClass.ShowToast(mActivity, "Notifications turned ON.");
//                else
//                    CommonClass.ShowToast(mActivity, "Notifications turned OFF.");
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

