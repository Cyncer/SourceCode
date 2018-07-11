package com.app.android.cync;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.customwidget.materialEditText;
import com.cync.model.UserDetail;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.rey.material.widget.Button;
import com.rey.material.widget.TextView;
import com.utils.CommonClass;
import com.utils.Constants;
import com.utils.GoogleLocationHelper;
import com.utils.TextValidator;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;


/**
 * Created by ketul.patel on 18/1/16.
 */
public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST_ALL = 1000;
    private static final int REQ_APP_SETTINGS = 201;

    private materialEditText login_email, login_password;
    private TextView tvForgot;
    private Button login_btn, facbook_btn, reg_btn;
    private AlertDialog.Builder builder = null;

    private RequestQueue mQueue;
    private double currentLat, currentLng;
    //-----
    // GPSTracker gps;
    private boolean isFBLogin = false;

    private String ride_id = "";

    private UserDetail ud = null;

    private CallbackManager mcallbackManager;
    private String st_email = "", st_id = "", st_name = "", st_gender = "", user_photos = "";
    private String st_firstname = "", st_lastname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //---------FB
        FacebookSdk.sdkInitialize(getApplicationContext());

        //perhaps a bit excessive
        /*FacebookSdk.addLoggingBehavior(LoggingBehavior.GRAPH_API_DEBUG_INFO);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_RAW_RESPONSES);
        */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.cync_cover, getBaseContext().getTheme()));
        } else {
            getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.cync_cover));
        }
        setContentView(R.layout.activity_login);
        mQueue = VolleySetup.getRequestQueue();

        CommonClass.updateGCMID();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("ride_id")) {
                ride_id = bundle.getString("ride_id", "");
            }
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();

        if (checkLocationPermissionGranted()) {
            GoogleLocationHelper.getGoogleLocationHelper(this).start();
        }

        requestRuntimePermission();
    }

    private void testPermission(String p) {
        boolean required = ContextCompat.checkSelfPermission(this, p) == PackageManager.PERMISSION_GRANTED;
        Log.i(TAG, "Permission:" + p + " required->" + required);
    }

    private void requestRuntimePermission() {
        if (!checkLocationPermissionGranted()
                || checkExternalStoragePermission()
                || checkPhoneStatePermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_ALL);
        }
    }

    /**
     * This will be shown when user dont give permission and uses "Do not show" tick mark at
     * runtime permission
     */
    private void showPermissionNotGivenAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Permission Required!");
        builder.setMessage("Cync requires all permissions to work properly. Please enable all permissions toggle buttons.\nApplication Settings-> App permissions -> Permission Toggles.\n\nDo you want to give permissions?");
        builder.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //no action on cancel click
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQ_APP_SETTINGS);
    }

    private boolean checkAllPermission() {
        return checkLocationPermissionGranted()
                && checkPhoneStatePermission()
                && checkExternalStoragePermission();
    }

    private boolean checkLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkExternalStoragePermission() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ALL: {
                // If request is cancelled, the result arrays are empty.

                if (checkAllPermission()) {
                    //all fine
                    //getLocationFromGPS();
                    getGoogleLocation();
                } else {
                    //request again

                    showPermissionNotGivenAlertDialog();

                    /*if (checkLocationPermissionGranted()) {
                        getGoogleLocation();
                    }*/
                }
            }
        }
    }

    private void getGoogleLocation() {
        GoogleLocationHelper.getGoogleLocationHelper(this).start();
    }

    private void init() {
        builder = null;

        login_email = (materialEditText) findViewById(R.id.login_email);
        login_password = (materialEditText) findViewById(R.id.login_password);
        login_password.setTransformationMethod(new PasswordTransformationMethod());
        login_email.setText("");
        login_password.setText("");
        tvForgot = (TextView) findViewById(R.id.tvForgot);
        tvForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (CommonClass.getUserpreference(LoginActivity.this).login_type.equalsIgnoreCase("normal")) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.putExtra("ride_id", ride_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
//                } else {
//                    CommonClass.ShowToast(LoginActivity.this, "Facebook User.");
//                }
            }
        });
        login_btn = (Button) findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkAllPermission()) {
                    showPermissionNotGivenAlertDialog();
                    return;
                }

                if (GoogleLocationHelper
                        .getGoogleLocationHelper(LoginActivity.this)
                        .getLocation() == null) {
                    GoogleLocationHelper.getGoogleLocationHelper(LoginActivity.this).start();
                    Toast.makeText(LoginActivity.this, "Getting your location...", Toast.LENGTH_LONG).show();
                    return;
                }

                Location location = GoogleLocationHelper
                        .getGoogleLocationHelper(LoginActivity.this)
                        .getLocation();

                currentLat = location.getLatitude();
                currentLng = location.getLongitude();
                final String deviceToken = CommonClass.getDeviceToken(LoginActivity.this);

                Log.i("TOKEN", "deviceToken-> " + deviceToken);

                boolean valid = ValidateTask(login_email.getText().toString().trim(), login_password.getText().toString().trim());
                if (valid) {
                    if (validLatLngToken("" + currentLat, "" + currentLng, deviceToken)) {
                        if (CommonClass.getDeviceToken(LoginActivity.this) != null && CommonClass.getDeviceToken(LoginActivity.this).trim().length() > 0) {
//                            Log.e("LoginActivity.this", "IF " + CommonClass.getDeviceToken(LoginActivity.this));
                            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.loginUrl, loginSuccessLisner(),
                                    loginErrorLisner()) {
                                @Override
                                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                    HashMap<String, String> requestparam = new HashMap<>();
                                    requestparam.put("username", login_email.getText().toString());
                                    requestparam.put("password", login_password.getText().toString());
                                    requestparam.put("device_token", deviceToken);
                                    requestparam.put("device_type", "android");
                                    requestparam.put("latitude", "" + currentLat);
                                    requestparam.put("longitude", "" + currentLng);
                                    requestparam.put("login_type", "normal");
//                            System.out.println("Login---------------------------" + requestparam);
//                                    Log.d("Login Request", "==> " + requestparam);
                                    return requestparam;
                                }
                            };
                            CommonClass.showLoading(LoginActivity.this);
                            apiRequest.setShouldCache(false);
                            mQueue.add(apiRequest);
                        } else {
//                            Log.e("LoginActivity.this", "ELSE " + CommonClass.getDeviceToken(LoginActivity.this));
                        }
                    } else {
                        CommonClass.ShowToast(LoginActivity.this, getResources().getString(R.string.s_wrong));
                    }
                }
            }
        });
        facbook_btn = (Button) findViewById(R.id.facbook_btn);
        facbook_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkAllPermission()) {
                    showPermissionNotGivenAlertDialog();
                    return;
                }

                if (GoogleLocationHelper
                        .getGoogleLocationHelper(LoginActivity.this)
                        .getLocation() == null) {
                    GoogleLocationHelper.getGoogleLocationHelper(LoginActivity.this).start();
                    Toast.makeText(LoginActivity.this, "Getting your location...", Toast.LENGTH_LONG).show();
                    return;
                }

                Location location = GoogleLocationHelper
                        .getGoogleLocationHelper(LoginActivity.this)
                        .getLocation();

                currentLat = location.getLatitude();
                currentLng = location.getLongitude();

                loginFB();
            }
        });
        reg_btn = (Button) findViewById(R.id.reg_btn);
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkAllPermission()) {
                    showPermissionNotGivenAlertDialog();
                    return;
                }

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("ride_id", ride_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                finish();
            }
        });
//         lat = CommonClass.getPrefranceByKey_Value(LoginActivity.this, CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lat);
//         lng = CommonClass.getPrefranceByKey_Value(LoginActivity.this, CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lng);
    }


    private com.android.volley.Response.Listener<String> loginSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
//                {"status":false,"message":"Please enter email address.","data":""}

                Log.d("facebook Request", "==>Call fb response== ");
                Log.d("loginSuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(LoginActivity.this);
                boolean Status, permission = false;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        String user_id, user_image = "", first_name, last_name, login_type, email, make, model, year;
                        boolean notification_type;
                        JSONObject jresObject = jresObjectMain.getJSONObject("data");
                        user_id = "" + CommonClass.getDataFromJsonInt(jresObject, "user_id");
                        user_image = CommonClass.getDataFromJson(jresObject, "user_image");
                        login_type = CommonClass.getDataFromJson(jresObject, "login_type");
                        first_name = CommonClass.getDataFromJson(jresObject, "first_name");
                        last_name = CommonClass.getDataFromJson(jresObject, "last_name");
                        email = CommonClass.getDataFromJson(jresObject, "email");
                        make = CommonClass.getDataFromJson(jresObject, "make");
                        model = CommonClass.getDataFromJson(jresObject, "model");
                        year = CommonClass.getDataFromJson(jresObject, "year");
                        if (jresObject.has("permission"))
                            permission = CommonClass.getDataFromJsonBoolean(jresObject, "permission");
                        if (jresObject.has("notification_type")) {
                            notification_type = CommonClass.getDataFromJsonBoolean(jresObject, "notification_type");
//                            Log.i("notification_type", "--------" + notification_type);
                        } else
                            notification_type = true;

                        boolean view_my_rides, hide_top_speed;

                        if (jresObject.has("view_my_rides"))
                            view_my_rides = CommonClass.getDataFromJsonBoolean(jresObject, "view_my_rides");
                        else
                            view_my_rides = true;


                        if (jresObject.has("hide_top_speed"))
                            hide_top_speed = CommonClass.getDataFromJsonBoolean(jresObject, "hide_top_speed");
                        else
                            hide_top_speed = false;


                        ud = new UserDetail(user_id, user_image, first_name, last_name, login_type, email, notification_type, make, model, year, view_my_rides, hide_top_speed);

//                        Intent serviceIntent = new Intent(getBaseContext(),
//                                XMPPService.class);
//
//                        if (CommonClass.isMyServiceRunning(XMPPService.class, LoginActivity.this))
//                            stopService(serviceIntent);
//                        startService(serviceIntent);
                        // XMPP
//                        String chat_username = CommonClass.getDataFromJson(jresObject,"chat_username");
//                        String chat_password = CommonClass.getDataFromJson(jresObject,"chat_password");
//                        CommonClass.setChatUserpreference(LoginActivity.this,chat_username,chat_password);
                        CommonClass.setChatUserpreference(LoginActivity.this, "cync_" + user_id, "cync_" + user_id);
                        if (login_type.equalsIgnoreCase("facebook")) {

                            if (permission) {
                                CommonClass.setUserpreference(LoginActivity.this, ud);
                                openNavigation();
                            } else {
                                alertDialog(LoginActivity.this, user_id);
                            }
                        } else {
                            CommonClass.setUserpreference(LoginActivity.this, ud);
                            openNavigation();
                            CommonClass.ShowToast(LoginActivity.this, message);
                        }
                    } else {
                        CommonClass.ShowToast(LoginActivity.this, message);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
//                    Log.d("", "onResponse: " + e.getMessage());
                }

            }
        };
    }

    private void openNavigation() {
        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
        intent.putExtra("notification", "");
        intent.putExtra("ride_id", ride_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private com.android.volley.Response.ErrorListener loginErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(LoginActivity.this);
                CommonClass.ShowToast(LoginActivity.this, getResources().getString(R.string.s_wrong));
            }
        };
    }

    //
    private boolean validLatLngToken(String lat, String lng, String deviceToken) {
        Log.e("validLatLngToken", "lat--- " + lat + "lng--- " + lng + "deviceToken--- " + deviceToken);
        try {
            if (lat.trim().length() > 0 && lng.trim().length() > 0 && deviceToken.trim().length() > 0) {
                return true;
            } else {
                CommonClass.ShowToast(LoginActivity.this, getResources().getString(R.string.s_wrong));
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean ValidateTask(String uemailid2, String upassword2) {
        try {
            if (uemailid2.trim().length() > 0) {
                if (TextValidator.isAValidEmail(uemailid2)) {
                    String[] domain = {".aero", ".asia", ".biz",
                            ".cat", ".com", ".coop", ".edu",
                            ".gov", ".info", ".int", ".jobs",
                            ".mil", ".mobi", ".museum", ".name",
                            ".net", ".org", ".pro", ".tel",
                            ".travel", ".ac", ".ad", ".ae", ".af",
                            ".ag", ".ai", ".al", ".am", ".an",
                            ".ao", ".aq", ".ar", ".as", ".at",
                            ".au", ".aw", ".ax", ".az", ".ba",
                            ".bb", ".bd", ".be", ".bf", ".bg",
                            ".bh", ".bi", ".bj", ".bm", ".bn",
                            ".bo", ".br", ".bs", ".bt", ".bv",
                            ".bw", ".by", ".bz", ".ca", ".cc",
                            ".cd", ".cf", ".cg", ".ch", ".ci",
                            ".ck", ".cl", ".cm", ".cn", ".co",
                            ".cr", ".cu", ".cv", ".cx", ".cy",
                            ".cz", ".de", ".dj", ".dk", ".dm",
                            ".do", ".dz", ".ec", ".ee", ".eg",
                            ".er", ".es", ".et", ".eu", ".fi",
                            ".fj", ".fk", ".fm", ".fo", ".fr",
                            ".ga", ".gb", ".gd", ".ge", ".gf",
                            ".gg", ".gh", ".gi", ".gl", ".gm",
                            ".gn", ".gp", ".gq", ".gr", ".gs",
                            ".gt", ".gu", ".gw", ".gy", ".hk",
                            ".hm", ".hn", ".hr", ".ht", ".hu",
                            ".id", ".ie", " No", ".il", ".im",
                            ".in", ".io", ".iq", ".ir", ".is",
                            ".it", ".je", ".jm", ".jo", ".jp",
                            ".ke", ".kg", ".kh", ".ki", ".km",
                            ".kn", ".kp", ".kr", ".kw", ".ky",
                            ".kz", ".la", ".lb", ".lc", ".li",
                            ".lk", ".lr", ".ls", ".lt", ".lu",
                            ".lv", ".ly", ".ma", ".mc", ".md",
                            ".me", ".mg", ".mh", ".mk", ".ml",
                            ".mm", ".mn", ".mo", ".mp", ".mq",
                            ".mr", ".ms", ".mt", ".mu", ".mv",
                            ".mw", ".mx", ".my", ".mz", ".na",
                            ".nc", ".ne", ".nf", ".ng", ".ni",
                            ".nl", ".no", ".np", ".nr", ".nu",
                            ".nz", ".om", ".pa", ".pe", ".pf",
                            ".pg", ".ph", ".pk", ".pl", ".pm",
                            ".pn", ".pr", ".ps", ".pt", ".pw",
                            ".py", ".qa", ".re", ".ro", ".rs",
                            ".ru", ".rw", ".sa", ".sb", ".sc",
                            ".sd", ".se", ".sg", ".sh", ".si",
                            ".sj", ".sk", ".sl", ".sm", ".sn",
                            ".so", ".sr", ".st", ".su", ".sv",
                            ".sy", ".sz", ".tc", ".td", ".tf",
                            ".tg", ".th", ".tj", ".tk", ".tl",
                            ".tm", ".tn", ".to", ".tp", ".tr",
                            ".tt", ".tv", ".tw", ".tz", ".ua",
                            ".ug", ".uk", ".us", ".uy", ".uz",
                            ".va", ".vc", ".ve", ".vg", ".vi",
                            ".vn", ".vu", ".wf", ".ws", ".ye",
                            ".yt", ".za", ".zm", ".zw"};
                    List<String> list = Arrays.asList(domain);
                    String tmp = uemailid2.substring(
                            uemailid2.lastIndexOf("."), uemailid2.length());
                    if (list.contains("" + tmp)) {
                        if (upassword2.trim().length() > 0) {
                            return true;
                        } else {
                            CommonClass.ShowToast(LoginActivity.this, "Please enter your password.");
                            return false;
                        }
                    } else {
                        CommonClass.ShowToast(LoginActivity.this, "Please enter valid email address.");
                        return false;
                    }
                } else {
                    CommonClass.ShowToast(LoginActivity.this, "Please enter valid email address.");
                    return false;
                }
            } else {
                CommonClass.ShowToast(LoginActivity.this, "Please enter email address.");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        if (mcallbackManager != null) {
            mcallbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);

        if (GoogleLocationHelper.checkIsGoogleLocationActivityResult(requestCode)) {
            GoogleLocationHelper.getGoogleLocationHelper(this).onActivityResult(requestCode, resultCode, data);
            return;
        }

        switch (requestCode) {
            case REQ_APP_SETTINGS: {
                GoogleLocationHelper.getGoogleLocationHelper(this).start();
                return;
            }
        }

        System.out.println("Result code is == " + requestCode);
        System.out.println("Request code is == " + requestCode);
        System.out.println("DAta is  == " + data);
//        CommonClass.ShowToast(LoginActivity.this,"requestCode"+requestCode+"  ---"+data.getDataString());
        /*if (appInstalledOrNot("com.facebook.katana")) {
//            System.out.println("if==loginFB " );
            if (!isFBLogin) {
                loginFB();
                isFBLogin = true;
            }
        } else {
//            isFBLogin = true;
//            System.out.println("else ==loginFB " );
        }*/
    }

    private void loginFB() {
        mcallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mcallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.i("Success", "FB Success");
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json1, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            Log.e("ERROR", "" + response.getError().toString());
                                        } else {
//                                            Log.e("Success", "");
                                            JSONObject json = response.getJSONObject();
                                            Log.e("Response", "Fb Data===" + json.toString());

                                            try {
                                                if (json.has("id")) {
                                                    st_id = json.getString("id");
                                                }

                                                if (json.has("name")) {
                                                    st_name = json.getString("name");
                                                }


//                                                st_gender = json.getString("gender");
                                                if (json.has("email")) {
                                                    st_email = json.getString("email");
                                                }
                                                if (json.has("first_name")) {
                                                    st_firstname = json.getString("first_name");
                                                }
                                                if (json.has("last_name")) {
                                                    st_lastname = json.getString("last_name");
                                                }
                                                if (json.has("picture")) {
                                                    JSONObject picobj = json.getJSONObject("picture");
                                                    JSONObject dataobj = picobj.getJSONObject("data");
                                                    user_photos = dataobj.getString("url");
//                                                    System.out.println("user_photos--------------------" + user_photos);
                                                    if (user_photos == null || user_photos.length() == 0)
                                                        user_photos = "";
                                                }

                                                // user_photos = "http://graph.facebook.com/" + st_id + "/picture?type=large";


                                                Log.d("facebook Request", "==>Call user_photos== " + user_photos);

                                                loginWebservice(st_id, st_name, user_photos, st_firstname, st_lastname);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,picture.type(large),first_name,last_name");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
//                        Log.d("TAG_CANCEL", "On cancel");
                        logoutFB();
                    }

                    @Override
                    public void onError(FacebookException error) {
//                        Log.d("TAG_ERROR", error.toString() + "");
                        logoutFB();
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,
                Arrays.asList("public_profile", "email"));
    }

    private void logoutFB() {
        try {
            LoginManager.getInstance().logOut();
            CommonClass.ClearUserpreference(LoginActivity.this);
            CommonClass.ClearChatUserpreference(LoginActivity.this);
//            CommonClass.ShowToast(LoginActivity.this,"Something went wrong. Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Log.d("logoutFB", "On cancel");
    }

    public void loginWebservice(final String strId, final String strName, String strImage, final String strFristname, final String strLastname) {
        final String deviceToken = CommonClass.getDeviceToken(LoginActivity.this);

        boolean valid = validLatLngToken("" + currentLat, "" + currentLng, deviceToken);
        if (valid)
            valid = validLatLngToken(strId, strFristname, strLastname);
        if (valid) {
            final String finalStrImage = Uri.encode(strImage);
            Log.d("facebook Request", "==>Call fb== ");
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.loginUrl, loginSuccessLisner(),
                    loginErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestParam = new HashMap<>();
//                requestparam.put("facebook_username", strName);
                    requestParam.put("first_name", strFristname);
                    requestParam.put("last_name", strLastname);
                    requestParam.put("facebook_id", strId);
                    requestParam.put("facebook_image", finalStrImage);
                    requestParam.put("device_token", deviceToken);
                    requestParam.put("device_type", "android");
                    requestParam.put("latitude", "" + currentLat);
                    requestParam.put("longitude", "" + currentLng);
                    requestParam.put("login_type", "facebook");
                    requestParam.put("timezone", "" + TimeZone.getDefault().getID());
                    Log.d("facebook Request", "==> " + requestParam);

                    /*for (Map.Entry<String, String> entry : requestParam.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();

                        Log.i("LoginActivity", "" + key + ":" + value);
                        // do stuff
                    }*/
                    return requestParam;
                }
            };
            CommonClass.showLoading(LoginActivity.this);
            mQueue.add(apiRequest);
        } else {
            CommonClass.ShowToast(LoginActivity.this, getResources().getString(R.string.s_wrong));
        }
    }

    @Override
    public void onBackPressed() {
        CommonClass.alertDialog(LoginActivity.this, "Do you want to exit from the app?", true);
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private com.rey.material.widget.CheckBox chkTermsCondition;
    private android.widget.TextView txtTermsCondition, txtTemp;

    public void alertDialog(final Activity context, final String user_id) {
        if (builder == null) {
            builder = new AlertDialog.Builder(context);
//        builder.setTitle("Terms and Conditions");
            builder.setCancelable(false);
            LayoutInflater inflater = context.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.terms_condition, null);
            chkTermsCondition = (com.rey.material.widget.CheckBox) dialogView.findViewById(R.id.chkTrems);
            chkTermsCondition.setVisibility(View.GONE);
            txtTemp = (android.widget.TextView) dialogView.findViewById(R.id.txtTemp);
            txtTemp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            txtTermsCondition = (android.widget.TextView) dialogView.findViewById(R.id.txtTermsCondition);
            txtTermsCondition.setText(Html.fromHtml("<u>Terms and Conditions.</u>"));
            txtTermsCondition.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            txtTermsCondition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, TermConditionActiivty.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                }
            });
            builder.setView(dialogView);
            builder.setPositiveButton("AGREE", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    CommonClass.setUserpreference(LoginActivity.this, ud);
                    updateTermsConditionFlag(user_id);
                }
            });
            builder.setNegativeButton("DISAGREE", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    builder = null;
                    logoutFB();
                    CommonClass.ShowToast(LoginActivity.this, "Please accept terms and conditions.");
                }
            });
            builder.show();
        }
    }

    private void updateTermsConditionFlag(final String mUserId) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.termcondition, termsSuccessLisner(),
                termsErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + mUserId);
                requestparam.put("term_status", "" + true);
                Log.e("getFriend List Request", "==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(LoginActivity.this);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> termsSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(LoginActivity.this);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                        intent.putExtra("notification", "");
                        intent.putExtra("ride_id", ride_id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                        finish();
                        CommonClass.ShowToast(LoginActivity.this, "Logged in successfully.");
                    } else {
                        CommonClass.ShowToast(LoginActivity.this, message);
                    }
                } catch (JSONException e) {
                    message = LoginActivity.this.getString(R.string.s_wrong);
                    CommonClass.ShowToast(LoginActivity.this, message);
                    e.printStackTrace();
                }
//                loadFragmentAgain();

            }
        };
    }

    private com.android.volley.Response.ErrorListener termsErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(LoginActivity.this);
                CommonClass.ShowToast(LoginActivity.this, getResources().getString(R.string.s_wrong));
            }
        };
    }
}
