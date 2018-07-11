package com.utils;

/**
 * Created by ketul.patel on 21/1/16.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ApplicationController;
import com.app.android.cync.R;
import com.customwidget.CircularProgressBar;
import com.customwidget.TouchImageView;
import com.cync.model.FriendDetail;
import com.cync.model.GroupListItem;
import com.cync.model.UserDetail;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rey.material.app.Dialog;
import com.rey.material.widget.Button;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class CommonClass {
    private static final String TAG = "CommonClass";
    public static boolean isForground = false;
    public static String KEY_USER_DATA = "User_data";
    public static String OTHER_USER_DATA = "other_User_data";
    public static String DEVICE_TOKEN = "device_token";
    private static Context context = ApplicationController.getContext();

    public static String KEY_CHAT_USERNAME = "chat_username";
    public static String KEY_CHAT_USERPASSWORD = "chat_userpassword";
    public static String KEY_CHAT_ON_OFF = "chat_on_off";

    public static String KEY_PREFERENCE_CURRENT_LOCATION = "current_location";
    public static String KEY_Current_Lat = "lat";
    public static String KEY_Current_Lng = "lng";
    public static boolean fromFriendList = true;

    public static String KEY_isFirstTime = "isfirsttime";
    public static String KEY_RIDE_ID = "ride_ids";

    public static void setIsFirstTimepreference(Context ct, String ud) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("isfirsttime",
                ApplicationController.getContext().MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(KEY_isFirstTime, ud);

        peditor.commit();

    }

    public static String getIsFirstTimepreference(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("isfirsttime",
                ct.MODE_PRIVATE);

        return sp.getString(KEY_isFirstTime, "true");

    }

    public static void setChatUserpreference(Context ct, String chatusername,
                                             String chatuserpassword) {
        SharedPreferences sp = ct.getSharedPreferences("chatuserpref",
                ct.MODE_PRIVATE);

        SharedPreferences.Editor peditor = sp.edit();
//        String strName= CommonClass.strEncodeDecode(chatusername,true);
        peditor.putString(KEY_CHAT_USERNAME, chatusername);

        peditor.putString(KEY_CHAT_USERPASSWORD, chatuserpassword);

        peditor.commit();
    }

    public static String getChatUsername(Context ct) {
        SharedPreferences sp = ct.getSharedPreferences("chatuserpref",
                ct.MODE_PRIVATE);

        String chat_username = sp.getString(KEY_CHAT_USERNAME, "null");
        return chat_username;

    }


    public static void setFriendsChat(Context ct, String id, String name, String img) {


        String key_name = id + "_name";
        String key_img = id + "_img";

        SharedPreferences sp = ct.getSharedPreferences("chatfriendpref",
                ct.MODE_PRIVATE);

        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(id, id);
        peditor.putString(key_name, name);
        peditor.putString(key_img, img);

        peditor.commit();


    }


    public static String getFriendNameChat(Context ct, String id) {


        String key_name = id + "_name";

        SharedPreferences sp = ct.getSharedPreferences("chatfriendpref",
                ct.MODE_PRIVATE);


        String name = sp.getString(key_name, "");

        return name;


    }


    public static String getFriendImageChat(Context ct, String id) {


        String key_name = id + "_img";

        SharedPreferences sp = ct.getSharedPreferences("chatfriendpref",
                ct.MODE_PRIVATE);


        String name = sp.getString(key_name, "");

        return name;


    }


    public static void setChatPref(Context ct, boolean isChaton) {
        SharedPreferences sp = ct.getSharedPreferences("chatonoff",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putBoolean(KEY_CHAT_ON_OFF, isChaton);

        peditor.commit();
    }

    public static boolean getChatPref(Context ct) {
        SharedPreferences sp = ct.getSharedPreferences("chatonoff",
                ct.MODE_PRIVATE);

        boolean ischaton = sp.getBoolean(KEY_CHAT_ON_OFF, false);
        return ischaton;

    }


    public static void setTimer(int time) {
        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("myTime",
                ApplicationController.getContext().MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putInt("Time", time);

        peditor.commit();
    }

    public static int getTimer() {
        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("myTime",
                ApplicationController.getContext().MODE_PRIVATE);

        int mTime = sp.getInt("Time", 0);
        return mTime;

    }


    public static boolean isMyServiceRunning(Class<?> serviceClass, Context mContext) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void ShowToast(Context ct, String message) {
        try {
            if (message.trim().length() != 0)
                Toast.makeText(ct, message, Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
//            Log.d("", "Exception---" + e.getLocalizedMessage());
        }
    }

    public static void closeKeyboard(Activity mActivity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) mActivity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            if (mActivity.getCurrentFocus().getWindowToken() != null) {
                inputManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

            }
        } catch (NullPointerException e) {

        }
    }


    public static void log(String s) {
//        Log.d("COMMON CLASS", s);
    }


    public static void updateGCMID() {
        // TODO Auto-generated method stub
       /* GCMRegistrar.unregister(getApplicationContext());
        GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);*/
        if (new ConnectionDetector(ApplicationController.getContext()).isConnectingToInternet()) {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        CommonClass.setDeviceToken(FirebaseInstanceId.getInstance().getToken());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            CommonClass.ShowToast(ApplicationController.getContext(), "No Internet Connection");
        }
    }


    public static ArrayAdapter setSpinner(com.rey.material.widget.Spinner sp, ArrayList<String> list, int DrawableLeft, String Hint) {
        if (Hint.trim().length() > 0)
            list.add(0, Hint);
        ArrayAdapter<String> karant_adapter = new ArrayAdapter<String>(context, R.layout.spiner_item_layout, list);
        sp.setAdapter(karant_adapter);
        karant_adapter.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
        TextView tv = (TextView) sp.getSelectedView();
        tv.setCompoundDrawablesWithIntrinsicBounds(DrawableLeft, 0, 0, 0);

        return karant_adapter;
    }


    public static String getDataFromJson(JSONObject jobj, String key) {


        try {
            if (jobj.has(key)) {
                return jobj.getString(key);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static Boolean getDataFromJsonBoolean(JSONObject jobj, String key) {
        try {
            if (jobj.has(key)) {
                return jobj.getBoolean(key);
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public static int getDataFromJsonInt(JSONObject jobj, String key) {
        try {
            if (jobj.has(key)) {
                return jobj.getInt(key);
            } else {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static double getDataFromJsonDouble(JSONObject jobj, String key) {
        try {
            if (jobj.has(key)) {
                return jobj.getInt(key);
            } else {
                return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }


    public static double getDataFromJsonDouble2(JSONObject jobj, String key) {
        try {
            if (jobj.has(key)) {
                return jobj.getDouble(key);
            } else {
                return 0.0;
            }
        } catch (Exception e) {
            return 0.0;
        }
    }


//public static String strEncodeDecode(String str,boolean isDecode){
//    try {
//        if(isDecode)
//            str = URLDecoder.decode(str,"UTF-8");
//        else
//            str = URLEncoder.encode(str,"UTF-8");
//    }catch (Exception e)
//    {
//        e.printStackTrace();
//    }
//    return str;
//}

    public static void setUserpreference(Context ct, UserDetail ud) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("userpref", ApplicationController.getContext().MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        String udata = getStringDateUserFromObjectUserDetail(ApplicationController.getContext(), ud);

        peditor.putString(KEY_USER_DATA, udata);

        peditor.commit();

    }


    public static void updateUserpreference(Context ct, UserDetail ud) {
        // TODO Auto-generated method stub
//        Log.e("UserDetail","UserDetail--"+ud.first_name+"-"+ud.user_image);
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("userpref", ApplicationController.getContext().MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        String udata = getStringDateUserFromObjectUserDetail(ApplicationController.getContext(), ud);

        peditor.putString(KEY_USER_DATA, udata);

        peditor.apply();

    }

    public static UserDetail getUserpreference(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("userpref", ApplicationController.getContext().MODE_PRIVATE);

        String tmp = sp.getString(KEY_USER_DATA, "null");

        UserDetail mUserDetail = null;
        if (tmp.equalsIgnoreCase("null")) {
            mUserDetail = new UserDetail("null", "null", "null", "null", "null", "null", true, true, false);

        } else {
            mUserDetail = getObjectDateUserFRomStringUserDetail(ApplicationController.getContext(), sp.getString(KEY_USER_DATA, "null"));
        }

        return mUserDetail;

    }


    public static String getCSVFromArrayList(ArrayList<String> ids) {
        String csv = "";
        if (ids != null && ids.size() > 0) {
            String idList = ids.toString();
            csv = idList.substring(1, idList.length() - 1).replace(", ", ",");
        }
        return csv;
    }

    public static String getPrefranceByKey_Value(Context ct, String prefrence, String key) {
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences(prefrence, ct.MODE_PRIVATE);

        return sp.getString(key, "0");

    }

    public static void setPrefranceByKey_Value(Context ct, String prefrence, String key, String value) {
        if (ct == null) return;
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences(prefrence, ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(key, value);
        peditor.commit();
    }


    public static void zoomCamera(LatLng location, GoogleMap googleMap) {
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(location,
                14);
        // googleMap.moveCamera(yourLocation);
        googleMap.animateCamera(yourLocation);

    }


    public static void setDeviceToken(String dt) {
        if (dt == null || dt.isEmpty()) return;

        SharedPreferences sp = ApplicationController.getContext()
                .getSharedPreferences("device_token", Context.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(DEVICE_TOKEN, dt);
        peditor.commit();

    }

    public static String getDeviceToken(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("device_token", ct.MODE_PRIVATE);
        return sp.getString(DEVICE_TOKEN, "");

    }


    public static void setOtherImage(Context ct, String img) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("otherpref", ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();


        peditor.putString(OTHER_USER_DATA, img);

        peditor.commit();

    }

    public static String getOtherImage(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("otherpref", ct.MODE_PRIVATE);


        return sp.getString(OTHER_USER_DATA, "");

    }


    public static String getStringDateUserFromObjectUserDetail(Context ct, UserDetail mUd) {

        Gson gson = new Gson();
        String json = gson.toJson(mUd);
        return json;
    }

    public static UserDetail getObjectDateUserFRomStringUserDetail(Context ct, String json1) {

        Type type = new TypeToken<UserDetail>() {
        }.getType();
        UserDetail inpList = new Gson().fromJson(json1, type);

        return inpList;
    }

//    public static void ClearUserpreference(Context ct) {
//        SharedPreferences sp = ct.getSharedPreferences("userpref", ct.MODE_PRIVATE);
//        SharedPreferences.Editor peditor = sp.edit();
//        peditor.putString(KEY_USER_DATA, "");
//
//        peditor.commit();
//
//    }


    public static void ClearUserpreference(Context ct) {
        SharedPreferences sp = ct.getSharedPreferences("userpref", ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(KEY_USER_DATA, "");
        peditor.commit();
    }


    public static void ClearChatUserpreference(Context ct) {
        SharedPreferences sp = ct.getSharedPreferences("chatuserpref", ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(KEY_CHAT_USERNAME, "");
        peditor.putString(KEY_CHAT_USERPASSWORD, "");
        peditor.commit();
    }

    public static String getCurrentTimeStamp() {
        try {

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find
            // todays
            // date

            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    public static void OpenZoomImage(Activity activity, String url, int tmpImage) {
        final android.app.Dialog dialog;
        DisplayImageOptions options;
        ImageLoader imageLoader;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(activity));
        options = new DisplayImageOptions.Builder().showImageOnLoading(tmpImage)
                .showImageForEmptyUri(tmpImage).showImageOnFail(tmpImage).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        dialog = new android.app.Dialog(activity, R.style.AppCompatAlertDialogStyleTrans);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog.setContentView(R.layout.zoom_image_layout);
        dialog.setCancelable(false);
        dialog.show();


        TouchImageView imgParcelImage = (TouchImageView) dialog.findViewById(R.id.touchImageView1);


        ImageView buttonClose = (ImageView) dialog.findViewById(R.id.buttonClose);

        imageLoader.displayImage(url, imgParcelImage, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // holder.progressBar.setProgress(0);
                // holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // holder.progressBar.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                // holder.progressBar.setProgress(Math.round(100.0f
                // * current / total));
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();


            }
        });


    }


    public static android.app.Dialog dialog;
    public static android.app.Dialog percentage;


    public static TextView txtLoadingText;

    public static void setTextLoading(String text) {
        if (txtLoadingText != null)
            txtLoadingText.setText(text);
    }

    public static CircularProgressBar showLoadingPercentage(Context ct) {

        CircularProgressBar loadingIndicator = null;

        try {


            percentage = new android.app.Dialog(ct, R.style.AppCompatAlertDialogStyleTrans);
            percentage.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // dialog.setCancelable(false);
            percentage.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
            percentage.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            percentage.setContentView(R.layout.loading_percentage);
            percentage.setCancelable(false);
            percentage.show();


            txtLoadingText = (TextView) percentage.findViewById(R.id.txtLoadingText);
            loadingIndicator = (CircularProgressBar) percentage.findViewById(R.id.circularProgress);
            loadingIndicator.setProgressColor(Color.parseColor("#000000"));
            loadingIndicator.setTextColor(Color.parseColor("#000000"));

            percentage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadingIndicator;
    }


    public static void showLoading(Context ct) {
        try {


            dialog = new android.app.Dialog(ct, R.style.AppCompatAlertDialogStyleTrans);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            dialog.setContentView(R.layout.loading_indicator);
            dialog.setCancelable(false);
            dialog.show();


//            ImageView loadingIndicator = (ImageView) dialog.findViewById(R.id.loadingIndicator);
//            ImageView gear1 = (ImageView) dialog.findViewById(R.id.gear1);
//            ImageView gear2 = (ImageView) dialog.findViewById(R.id.gear2);
//            ImageView gear3 = (ImageView) dialog.findViewById(R.id.gear3);
//
//            RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            rotate.setDuration(1500);
//            //rotate.setRepeatMode(1);
//            rotate.setRepeatCount(Animation.INFINITE);
//            rotate.setInterpolator(new LinearInterpolator());
//            loadingIndicator.startAnimation(rotate);
//
//
//            RotateAnimation rotate1 = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            rotate1.setDuration(1500);
//            //rotate.setRepeatMode(1);
//            rotate1.setRepeatCount(Animation.INFINITE);
//            rotate1.setInterpolator(new LinearInterpolator());
//            gear1.startAnimation(rotate);
//
//
//            RotateAnimation rotate2 = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            rotate2.setDuration(1500);
//
//            rotate2.setRepeatCount(Animation.INFINITE);
//            rotate2.setInterpolator(new LinearInterpolator());
//            gear2.startAnimation(rotate2);
//
//
//            RotateAnimation rotate3 = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//            rotate3.setDuration(1500);
//            //rotate.setRepeatMode(1);
//            rotate3.setRepeatCount(Animation.INFINITE);
//            rotate3.setInterpolator(new LinearInterpolator());
//            gear3.startAnimation(rotate3);


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showLoadingCompress(Context ct) {
        try {


            dialog = new android.app.Dialog(ct, R.style.AppCompatAlertDialogStyleTrans);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // dialog.setCancelable(false);
            dialog.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            dialog.setContentView(R.layout.loading_indicator2);
            dialog.setCancelable(false);
            dialog.show();


            ImageView loadingIndicator = (ImageView) dialog.findViewById(R.id.loadingIndicator);
            ImageView gear1 = (ImageView) dialog.findViewById(R.id.gear1);
            ImageView gear2 = (ImageView) dialog.findViewById(R.id.gear2);
            ImageView gear3 = (ImageView) dialog.findViewById(R.id.gear3);

            RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(1500);
            //rotate.setRepeatMode(1);
            rotate.setRepeatCount(Animation.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
            loadingIndicator.startAnimation(rotate);


            RotateAnimation rotate1 = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate1.setDuration(1500);
            //rotate.setRepeatMode(1);
            rotate1.setRepeatCount(Animation.INFINITE);
            rotate1.setInterpolator(new LinearInterpolator());
            gear1.startAnimation(rotate);


            RotateAnimation rotate2 = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate2.setDuration(1500);

            rotate2.setRepeatCount(Animation.INFINITE);
            rotate2.setInterpolator(new LinearInterpolator());
            gear2.startAnimation(rotate2);


            RotateAnimation rotate3 = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate3.setDuration(1500);
            //rotate.setRepeatMode(1);
            rotate3.setRepeatCount(Animation.INFINITE);
            rotate3.setInterpolator(new LinearInterpolator());
            gear3.startAnimation(rotate3);


//


//
//            Animation a = AnimationUtils.loadAnimation(ct, R.anim.progress_anim);
//            a.setDuration(2000);
//            loadingIndicator.startAnimation(a);


            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void closeLoding(Context ct) {
        try {
            if (dialog != null)
                dialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void closeLodingpercentage(Context ct) {
        try {
            if (percentage != null)
                percentage.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    public static boolean doWeHavePermisiionFor(Activity activity, String Permision) {
        int hasWriteContactsPermission = activity.checkSelfPermission(Permision);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Permision},
                    55);
            return false;
        }
        return true;

    }

    @TargetApi(Build.VERSION_CODES.M)
    public static boolean doWeHavePermisiionForFromFragment(final FragmentActivity activity, final Fragment fragment, final String Permision, final String rationalMessage, boolean askIfNotGranted) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteContactsPermission = activity.checkSelfPermission(Permision);
            if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
                if (askIfNotGranted) {
                    if (!activity.shouldShowRequestPermissionRationale(Permision)) {
                        final Dialog dialog = new Dialog(activity);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_confirmation);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        Button okBtn = (Button) dialog.findViewById(R.id.dialog_ok_btn);
                        Button cancelBtn = (Button) dialog.findViewById(R.id.dialog_cancel_btn);
                        TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
                        message.setText(rationalMessage);
                        okBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                intent.setData(uri);
                                fragment.startActivityForResult(intent, 420);
                                dialog.dismiss();
                            }
                        });
                        cancelBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }

                    fragment.requestPermissions(new String[]{Permision},
                            55);
                }

                return false;
            }
        }
        return true;

    }

    public static ArrayList<GroupListItem> sortGroup(ArrayList<GroupListItem> groups) {
        Collections.sort(groups, new CustomComparatorGroup());
        return groups;
    }

    public static class CustomComparatorGroup implements Comparator<GroupListItem> {
        @Override
        public int compare(GroupListItem o1, GroupListItem o2) {
            return o1.group_name.compareToIgnoreCase(o2.group_name);
        }
    }

    public static ArrayList<FriendDetail> sortFriends(ArrayList<FriendDetail> friends) {

        if (friends != null && friends.size() > 0) {
            Collections.sort(friends, new CustomComparatorFriend());
        }
        return friends;
    }

    public static class CustomComparatorFriend implements Comparator<FriendDetail> {
        @Override
        public int compare(FriendDetail o1, FriendDetail o2) {
            return o1.friendName.compareToIgnoreCase(o2.friendName);
        }
    }

    public static ArrayList<UserDetail> sortUserDetails(ArrayList<UserDetail> userDetails) {
        Collections.sort(userDetails, new CustomComparator());
        return userDetails;
    }

    public static class CustomComparator implements Comparator<UserDetail> {
        @Override
        public int compare(UserDetail o1, UserDetail o2) {
            return o1.first_name.compareToIgnoreCase(o2.first_name);
        }
    }

    public static void alertDialog(final Activity context, String steMessage, boolean flagNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(steMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.finish();

            }
        });
        if (flagNo) {
            builder.setNegativeButton("No", null);
        }
        builder.show();
    }
//    public static Bitmap getBitmap(Uri path ,Activity mActivity) {
//
//        ContentResolver mContentResolver = mActivity.getContentResolver();
//        Uri uri = path;
//        InputStream in = null;
//        try {
//            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
//            in = mContentResolver.openInputStream(uri);
//
//            // Decode image size
//            BitmapFactory.Options o = new BitmapFactory.Options();
//            o.inJustDecodeBounds = true;
//            BitmapFactory.decodeStream(in, null, o);
//            in.close();
//
//            int scale = 1;
//            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
//                scale++;
//            }
//            Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
//                    + ", orig-height: " + o.outHeight);
//
//            Bitmap b = null;
//            in = mContentResolver.openInputStream(uri);
//            if (scale > 1) {
//                scale--;
//                // scale to max possible inSampleSize that still yields an image
//                // larger than target
//                o = new BitmapFactory.Options();
//                o.inSampleSize = scale;
//                b = BitmapFactory.decodeStream(in, null, o);
//
//                // resize to desired dimensions
//                int height = b.getHeight();
//                int width = b.getWidth();
//                Log.d("TAG", "1th scale operation dimenions - width: " + width
//                        + ", height: " + height);
//
//                double y = Math.sqrt(IMAGE_MAX_SIZE
//                        / (((double) width) / height));
//                double x = (y / height) * width;
//
//                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
//                        (int) y, true);
//                b.recycle();
//                b = scaledBitmap;
//
//                System.gc();
//            } else {
//                b = BitmapFactory.decodeStream(in);
//            }
//            in.close();
//
//            Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
//                    + b.getHeight());
//            return b;
//        } catch (IOException e) {
//            Log.e("TAG", e.getMessage(), e);
//            return null;
//        }
//    }


    public static ArrayList<String> getMentionListFromString(Context ct, String json1) {

        Type type = new TypeToken<List<String>>() {
        }.getType();
        ArrayList<String> inpList = new Gson().fromJson(json1, type);
        return inpList;
    }


    public static String getMentionFromObject(Context ct, ArrayList<String> object) {

        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }


    public static String getFriendStringFromObject(Context ct, ArrayList<FriendDetail> object) {

        SharedPreferences mPrefs = ct.getSharedPreferences("friendpref", ct.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }


    public static ArrayList<FriendDetail> getFriendObjectListFromString(Context ct, String json1) {

        Type type = new TypeToken<List<FriendDetail>>() {
        }.getType();
        ArrayList<FriendDetail> inpList = new Gson().fromJson(json1, type);
        return inpList;
    }


    public static void setFriendDetail(Context ct, String dt) {

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("friendpref", ct.MODE_PRIVATE);

        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString("KEY_FRIEND_LIST", dt);

        peditor.commit();

    }

    public static String getFriendDetail(Context ct) {
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("friendpref", ct.MODE_PRIVATE);

        return sp.getString("KEY_FRIEND_LIST", "");

    }


    public static String isFirstTime = "true";
    public static String KEY_SERVICE_START = "isServiceStart";
    public static String KEY_SERVICE_STATE = "service_state";
    public static String KEY_TRACKING_INTERVAL = "tracking_interval";
    public static String KEY_START_PAUSE_INTERVAL = "start_pause_interval";
    public static String KEY_STOP_PAUSE_INTERVAL = "stop_pause_interval";
    public static String KEY_PAUSE_TIME = "pause_time";
    public static String KEY_DURATION = "duration";
    public static String KEY_USER_AUTH_ID = "user_auth_id";
    public static String KEY_LAST_TRACKING_DATE = "tracking_last_date";
    public static String KEY_LAST_TRACKING_TIME = "tracking_last_time";


    public static String KEY_START_PAUSE_DISTANCE = "start_pause_distance";
    public static String KEY_STOP_PAUSE_DISTANCE = "stop_pause_distance";
    public static String KEY_PAUSE_DISTANCE = "pause_distance";
    public static String KEY_PASTE_DISTANCE = "past_distance";

    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

    public static Date GetUTCdatetimeAsDate() {
        // note: doesn't check for null
        return StringDateToDate(GetUTCdatetimeAsString());
    }

    public static String GetUTCdatetimeAsString() {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate) {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try {
            dateToReturn = (Date) dateFormat.parse(StrDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateToReturn;
    }


    public static void setlocationServiceCurrentState(Context ct,
                                                      int state) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("state",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putInt(KEY_SERVICE_STATE, state);

        peditor.commit();

    }

    public static int getlocationServiceCurrentState(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("state",
                ct.MODE_PRIVATE);

        int tmp = sp.getInt(KEY_SERVICE_STATE, 0);

        return tmp;

    }


    public static void setlocationServicepreference(Context ct,
                                                    String isServiceStart) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("isServiceStart",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putString(KEY_SERVICE_START, isServiceStart);

        peditor.commit();

    }

    public static String getlocationServicepreference(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("isServiceStart",
                ct.MODE_PRIVATE);

        String tmp = sp.getString(KEY_SERVICE_START, "false");

        return tmp;

    }

    //
    public static void ClearlocationServicepreference(Context ct) {
        SharedPreferences sp = ct.getSharedPreferences("isServiceStart",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(KEY_SERVICE_START, "false");

        peditor.commit();

    }

    public static void setTrackingInterval(Context ct, int times) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("tracking_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putInt(KEY_TRACKING_INTERVAL, times);

        peditor.commit();

    }

    public static int getTrackingInterval(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("tracking_time",
                ct.MODE_PRIVATE);

        int tmp = sp.getInt(KEY_TRACKING_INTERVAL, 3000);

        return tmp;

    }


    public static void setStartPauseInterval(Context ct, long times) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_START_PAUSE_INTERVAL, times);

        peditor.commit();

    }

    public static void setStopPauseInterval(Context ct, long times) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_STOP_PAUSE_INTERVAL, times);

        peditor.commit();

    }


    public static long getStartPauseInterval(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_time",
                ct.MODE_PRIVATE);

        long tmp = sp.getLong(KEY_START_PAUSE_INTERVAL, 0);

        return tmp;

    }

    public static long getStopPauseInterval(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_time",
                ct.MODE_PRIVATE);

        long tmp = sp.getLong(KEY_STOP_PAUSE_INTERVAL, 0);

        return tmp;

    }

    public static void clearPauseInterval(Context ct) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_START_PAUSE_INTERVAL, 0);
        peditor.putLong(KEY_STOP_PAUSE_INTERVAL, 0);

        peditor.commit();

    }


    public static void setPauseTime(Context ct, long times) {
        // TODO Auto-generated method stub


        Log.i(TAG, "kkkkk setPauseTime: " + getPauseTime(ct));
        Log.i(TAG, "kkkkk setPauseTime: " + times);
        long finalTime = getPauseTime(ct) + times;
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_pause_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_PAUSE_TIME, finalTime);

        peditor.commit();

    }

    public static long getPauseTime(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_pause_time",
                ct.MODE_PRIVATE);

        long tmp = sp.getLong(KEY_PAUSE_TIME, 0);

        return tmp;

    }


    public static void clearPauseTime(Context ct) {
        // TODO Auto-generated method stub

        Log.i(TAG, "kkkkk clearPauseTime: ");


        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_pause_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_PAUSE_TIME, 0);

        peditor.commit();

    }


    public static void setDuration(Context ct, long times) {
        // TODO Auto-generated method stub


        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("duration",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_DURATION, times);

        peditor.commit();

    }

    public static long getDuration(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("duration",
                ct.MODE_PRIVATE);

        long tmp = sp.getLong(KEY_DURATION, 0);

        return tmp;

    }


    public static void clearDuration(Context ct) {
        // TODO Auto-generated method stub

        Log.i(TAG, "kkkkk clearPauseTime: ");


        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("duration",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_DURATION, 0);

        peditor.commit();

    }


    public static void clearTrackingInterval(Context ct) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("tracking_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putInt(KEY_TRACKING_INTERVAL, 5000);

        peditor.commit();

    }


    public static double getStartPauseDistance(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_distance",
                ct.MODE_PRIVATE);

        double tmp = sp.getFloat(KEY_START_PAUSE_DISTANCE, 0);

        return tmp;

    }

    public static double getStopPauseDistance(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_distance",
                ct.MODE_PRIVATE);

        double tmp = sp.getFloat(KEY_STOP_PAUSE_DISTANCE, 0);

        return tmp;

    }

    public static void clearPauseDistanceInterval(Context ct) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_START_PAUSE_DISTANCE, 0);
        peditor.putFloat(KEY_STOP_PAUSE_DISTANCE, 0);

        peditor.commit();

    }


    public static void setStartPauseDistance(Context ct, double dis) {
        // TODO Auto-generated method stub

        Log.i(TAG, ">>> Distance: setStartPauseDistance = " + dis);
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_START_PAUSE_DISTANCE, (float) dis);

        peditor.commit();


    }

    public static void setStopPauseDistance(Context ct, double dis) {
        // TODO Auto-generated method stub

        Log.i(TAG, ">>> Distance: setStopPauseDistance = " + dis);

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("pause_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_STOP_PAUSE_DISTANCE, (float) dis);

        peditor.commit();


    }


    public static void setPauseDistance(Context ct, float dis) {
        // TODO Auto-generated method stub


        double finalDis = getPauseDistance(ct) + dis;
        Log.i(TAG, ">>> Distance: setPauseDistance = " + finalDis);

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_pause_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_PAUSE_DISTANCE, (float) finalDis);


        peditor.commit();

    }

    public static double getPauseDistance(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_pause_distance",
                ct.MODE_PRIVATE);

        double tmp = sp.getFloat(KEY_PAUSE_DISTANCE, 0.0f);


        Log.i(TAG, ">>> Distance: getPauseDistance = " + tmp);

        return tmp;

    }


    public static void clearPauseDistance(Context ct) {
        // TODO Auto-generated method stub


        Log.i(TAG, ">>> Distance: clearPauseDistance = ");

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_pause_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_PAUSE_DISTANCE, 0);

        peditor.commit();

    }


    public static String KEY_START_TIME = "start_time";
    public static String KEY_STOP_TIME = "stop_time";

    public static void setStartTime(Context ct, long times) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("start_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_START_TIME, times);

        peditor.commit();

    }

    public static long getStartTime(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("start_time",
                ct.MODE_PRIVATE);
        long tmp = sp.getLong(KEY_START_TIME, 0);
        return tmp;

    }


    public static void setStopTime(Context ct, long times) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("stop_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_STOP_TIME, times);

        peditor.commit();

    }

    public static long getStopTime(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("stop_time",
                ct.MODE_PRIVATE);

        long tmp = sp.getLong(KEY_STOP_TIME, 0);

        return tmp;

    }


    public static void clearStartTime(Context ct) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("start_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putLong(KEY_START_TIME, 0);


        peditor.commit();

    }

    public static void clearStopTime(Context ct) {
        // TODO Auto-generated method stub

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("start_time",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();


        peditor.putLong(KEY_STOP_TIME, 0);

        peditor.commit();

    }


    public static void setPastDistance(Context ct, float dis) {
        // TODO Auto-generated method stub


        double finalDis = getPastDistance(ct) + dis;
        Log.i(TAG, ">>> Distance: setPauseDistance = " + finalDis);

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_past_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_PASTE_DISTANCE, (float) finalDis);


        peditor.commit();

    }

    public static double getPastDistance(Context ct) {
        // TODO Auto-generated method stub
        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_past_distance",
                ct.MODE_PRIVATE);

        double tmp = sp.getFloat(KEY_PASTE_DISTANCE, 0.0f);


        Log.i(TAG, ">>> Distance: getPauseDistance = " + tmp);

        return tmp;

    }


    public static void clearPastDistance(Context ct) {
        // TODO Auto-generated method stub


        Log.i(TAG, ">>> Distance: clearPauseDistance = ");

        @SuppressWarnings({"static-access", "deprecation"})
        SharedPreferences sp = ct.getSharedPreferences("r_past_distance",
                ct.MODE_PRIVATE);
        SharedPreferences.Editor peditor = sp.edit();

        peditor.putFloat(KEY_PASTE_DISTANCE, 0);

        peditor.commit();

    }


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public static void setRideIds(Context ct, String ride_ids) {


        SharedPreferences sp = ct.getSharedPreferences("ridepref",
                ct.MODE_PRIVATE);

        SharedPreferences.Editor peditor = sp.edit();
        peditor.putString(KEY_RIDE_ID, ride_ids);


        peditor.commit();


    }


    public static String getRideIds(Context ct) {


        SharedPreferences sp = ct.getSharedPreferences("ridepref",
                ct.MODE_PRIVATE);
        String name = sp.getString(KEY_RIDE_ID, "");

        return name;


    }


}