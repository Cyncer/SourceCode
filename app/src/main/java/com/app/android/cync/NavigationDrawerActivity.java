package com.app.android.cync;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.cync.model.UserDetail;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.fragment.AssistFragment;
import com.fragment.BlockedUserFragment;
import com.fragment.ChangePasswordFragment;
import com.fragment.CyncTankFragment;
import com.fragment.FragmentDrawer;
import com.fragment.FriendListFragment;
import com.fragment.GroupListFragment;
import com.fragment.HomeFragment;
import com.fragment.MyProfileFragment;
import com.fragment.NotificationsFragment;
import com.fragment.SettingFragment;
import com.rey.material.widget.LinearLayout;
import com.utils.CommonClass;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import com.xmpp.XMPPService;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ketul.patel on 7/1/16.
 */

public class NavigationDrawerActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = NavigationDrawerActivity.class.getSimpleName();
    public static Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private com.rey.material.widget.LinearLayout llLogout;
    private TextView mTitle;
    public static boolean isDrawerMenuOpen;
    private RequestQueue mQueue;
    android.widget.RelativeLayout nav_header_container;
    String ride_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = NavigationDrawerActivity.this.getSharedPreferences("referrer", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("referrer", "");
        editor.apply();
        initId();
        // display the first navigation drawer view on app launch
        Bundle mbundle = getIntent().getExtras();
        if (mbundle != null) {


            if (mbundle.containsKey("ride_id")) {
                ride_id = mbundle.getString("ride_id", "");
            }

        }

        Log.i(TAG, "onCreate: Ride id=" + ride_id);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent serviceIntent = new Intent(NavigationDrawerActivity.this, XMPPService.class);
        if (isMyServiceRunning(XMPPService.class))
            stopService(serviceIntent);
        startService(serviceIntent);


    }

    private String groupId = "";

    private void initId() {
        mQueue = VolleySetup.getRequestQueue();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        isDrawerMenuOpen = false;
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);


        llLogout = (LinearLayout) findViewById(R.id.llLogout);
        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(NavigationDrawerActivity.this)
                        .setMessage("Do you want to logout?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sendRequestLogout();

                            }
                        })
                        /*.setNegativeButton("No", null)*/
                        .show();
//
            }
        });
        nav_header_container = (RelativeLayout) findViewById(R.id.nav_header_container);
        nav_header_container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });


        drawerFragment.setDrawerListener(this);


        if (getIntent() != null) {

            UserDetail userDetail = CommonClass.getUserpreference(NavigationDrawerActivity.this);

            if (userDetail != null && userDetail.first_name.length() > 0 && !userDetail.first_name.equalsIgnoreCase("null")) {


                String action = getIntent().getAction();
                String type = getIntent().getType();

                if (getIntent().getExtras().containsKey("notification_ride"))
                {
                    displayView(2);
                }
                else if (ride_id.trim().length() > 0) {

                    displayView(2);
                } else if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if ("text/plain".equals(type)) {
                        mIntent = getIntent();
                        displayView(1);
                        // Handle text being sent
                    } else if (type.startsWith("image/")) {
                        // Handle single image being sent
                        mIntent = getIntent();
                        displayView(1);
                    } else if (type.startsWith("video/")) {
                        mIntent = getIntent();
                        displayView(1);
                        // Handle multiple images being sent
                    }
                } else if (getIntent() != null && getIntent().getData() != null && (getIntent().getData().getPath().equals("/cyncapp/share-link/"))) {

                    mIntentApp = getIntent();
                    Uri data = getIntent().getData();
                    if (data != null) {


                        // Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                        //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();
                        displayView(2);
                    }

                } else {
                    showFragmentInitially();

                    // Handle other intents, such as being started from the home screen
                }
            } else {
                Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                // close this activity
                finish();
            }
        } else {
            showFragmentInitially();
        }


    }


    public void showFragmentInitially() {
        Bundle bundle = getIntent().getExtras();
        try {
            String string = "";
            if (bundle != null) {
                string = bundle.getString("notification", "1");


                if (bundle.containsKey("ride_id")) {

                    ride_id = bundle.getString("ride_id", "");
                }

                groupId = bundle.getString("group_id", "");


                if (ride_id.trim().length() > 0) {

                    displayView(2);
                } else if (string != null & string.length() > 0) {
                    displayView(Integer.parseInt(string));
                    drawerFragment.setAdapterData(Integer.parseInt(string));
                } else {

                    boolean showMessage = CheckValid();


                    // Log.i("NavigationDrawerActivity","Kp showMessage==>"+showMessage+" Prf="+CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this));

                    if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this).equals("true") && showMessage) {
                        displayView(3);


                    } else {
                        displayView(1);
                    }
                }
            } else {


                boolean showMessage = CheckValid();
                // Log.i("NavigationDrawerActivity","Kp showMessage==>"+showMessage+" Prf="+CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this));

                if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this).equals("true") && showMessage) {
                    displayView(3);


                } else {
                    displayView(1);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Intent mIntent = null;
    Intent mIntentApp = null;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {


            if (bundle.containsKey("notification_ride"))
            {
                displayView(2);
            }
            else if (bundle.containsKey("ride_id") && bundle.getString("ride_id", "").trim().length() > 0) {
                ride_id = bundle.getString("ride_id", "");

                Log.i(TAG, "onNewIntent: Ride id=" + ride_id);

                if (ride_id.trim().length() > 0) {

                    displayView(2);
                }




            } else if (bundle.containsKey("notification")) {
                String string = "";
                string = bundle.getString("notification", "1");


                groupId = bundle.getString("group_id", "");

                Log.e("getExtras--", " onNewIntent getExtras--------string=" + string);
                Log.e("getExtras--", "onNewIntent getExtras--------" + groupId);

                if (string != null & string.length() > 0) {
                    displayView(Integer.parseInt(string));
                    drawerFragment.setAdapterData(Integer.parseInt(string));
                } else {

                    boolean showMessage = CheckValid();


                    // Log.i("NavigationDrawerActivity","Kp showMessage==>"+showMessage+" Prf="+CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this));

                    if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this).equals("true") && showMessage) {
                        displayView(3);


                    } else {
                        displayView(1);
                    }
                }

            } else if (intent != null && intent.getData() != null && (intent.getData().getPath().equals("/cyncapp/share-link/"))) {

                mIntentApp = intent;
                Uri data = intent.getData();
                if (data != null) {



                    //Log.i(TAG, "onNewIntent: received, with data: "+ data.getQueryParameter("id"));
                    //Toast.makeText(NavigationDrawerActivity.this, ""+ data.getQueryParameter("id"), Toast.LENGTH_SHORT).show();
                    displayView(2);

                }

            } else {

                mIntent = intent;
                String action = intent.getAction();
                String type = intent.getType();

                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    // Toast.makeText(getApplicationContext(),"Toast Message "+type,Toast.LENGTH_SHORT).show();
                    displayView(1);
//            if ("text/plain".equals(type)) {
//
//                handleSendText(intent); // Handle text being sent
//            } else if (type.startsWith("image/")) {
//                handleSendImage(intent); // Handle single image being sent
//            }
//            else  if (type.startsWith("video/")) {
//                handleSendVideo(intent); // Handle multiple images being sent
//            }
                } else {
                    displayView(1);
                }

            }


        } else {

            boolean showMessage = CheckValid();
            if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivity.this).equals("true") && showMessage) {
                displayView(3);


            } else {
                displayView(1);
            }


        }


    }


    private boolean CheckValid() {

        if (CommonClass.getUserpreference(NavigationDrawerActivity.this).make.trim().length() > 0 && CommonClass.getUserpreference(NavigationDrawerActivity.this).year.trim().length() > 0 && CommonClass.getUserpreference(NavigationDrawerActivity.this).model.trim().length() > 0) {
            return false;
        } else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (menu != null) {
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    /*XMPP*/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    public void displayView(int position) {
        Fragment fragment = null;
        String tag = "";
        drawerFragment.setAdapterData(position);

//        switch (position) {
//
//            case 0:
//
//                if (mIntent == null) {
//
//                    // Toast.makeText(getApplicationContext(),"mIntent null ",Toast.LENGTH_SHORT).show();
//                    fragment = new CyncTankFragment("");
//                } else {
//                    fragment = new CyncTankFragment(mIntent);
//                    mIntent = null;
//                }
//                mTitle.setText("CYNC TANK");
//                tag = "CyncTankFragment";
//                break;
//
//
//            case 1:
//                fragment = new HomeFragment();
//                mTitle.setText("MAP");
//                tag = "HomeFragment";
//                break;
//            case 2:
//                fragment = new MyProfileFragment();
//                mTitle.setText("MY PROFILE");
//                tag = "MyProfileFragment";
//                break;
//
//            case 3:
//                fragment = new CyncTankFragment("My Posts");
//                mTitle.setText("MY POSTS");
//                tag = "CyncTankFragmentMyPost";
//                break;
//
//
//            case 4:
//                fragment = new FriendListFragment();
//                mTitle.setText("FRIENDS LIST");
//                tag = "FriendListFragment";
//                break;
//
//            case 5:
//                fragment = new BlockedUserFragment();
//                mTitle.setText("BLOCKED USERS");
//                tag = "BlockedUserFragment";
//                break;
//
//
//            case 6:
//                fragment = new GroupListFragment();
//                mTitle.setText("MY GROUPS");
//                tag = "GroupListFragment";
//                Bundle bundle = new Bundle();
//                if (groupId != null && groupId.trim().length() > 0) {
//                    bundle.putString("group_id", groupId);
//                    Log.e("GroupListFragment--", "--------" + groupId);
//                    fragment.setArguments(bundle);
//                    groupId = null;
//                }
//
//                break;
//            case 7:
//                fragment = new NotificationsFragment();
//                mTitle.setText("NOTIFICATIONS");
//                tag = "NotificationsFragment";
//                break;
//
//            case 8:
//
//                //  Toast.makeText(NavigationDrawerActivity.this,"Coming Soon",Toast.LENGTH_SHORT).show();
//                fragment = new AssistFragment();
//                mTitle.setText("CYNC ASSIST");
//                tag = "AssistFragment";
//                break;
//
//
//            case 9:
//                if (CommonClass.getUserpreference(NavigationDrawerActivity.this).login_type.equalsIgnoreCase("normal")) {
//                    fragment = new ChangePasswordFragment();
//                    mTitle.setText("CHANGE PASSWORD");
//                    tag = "ChangePasswordFragment";
//                } else {
//                    new AlertDialog.Builder(NavigationDrawerActivity.this)
//                            .setMessage("You are logged in with Facebook.")
//                            .setCancelable(true)
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                }
//                            })
//                        /*.setNegativeButton("No", null)*/
//                            .show();
//                    CommonClass.ShowToast(NavigationDrawerActivity.this,"Sorry you cannot change your password.");
//                }
//                break;
//            case 10:
//                fragment = new SettingFragment();
//                mTitle.setText("SETTINGS");
//                tag = "SettingFragment";
//                break;
//            default:
//                break;
//        }

        getSupportActionBar().setTitle("");
        switch (position) {

            case 0:

                fragment = new AssistFragment();
                mTitle.setText("RIDER ASSIST");
                tag = "AssistFragment";
                break;


            case 1:
                if (mIntent == null) {

                    // Toast.makeText(getApplicationContext(),"mIntent null ",Toast.LENGTH_SHORT).show();
                    fragment = new CyncTankFragment("");
                } else {
                    fragment = new CyncTankFragment(mIntent);
                    mIntent = null;
                }
                mTitle.setText("NEWS FEED");
                tag = "CyncTankFragment";
                break;

            case 2:
                mTitle.setText("MAP");
                tag = "HomeFragment";
                fragment = new HomeFragment(ride_id);
                mIntentApp = null;
                ride_id="";
                break;

            case 3:
                fragment = new MyProfileFragment();
                mTitle.setText("MY PROFILE");
                tag = "MyProfileFragment";
                break;

            case 4:
                fragment = new CyncTankFragment("My Posts");
                mTitle.setText("MY GARAGE");
                tag = "CyncTankFragmentMyPost";
                break;

            case 5:
                fragment = new FriendListFragment();
                mTitle.setText("FRIENDS LIST");
                tag = "FriendListFragment";
                break;

            case 6:
                fragment = new BlockedUserFragment();
                mTitle.setText("BLOCKED USERS");
                tag = "BlockedUserFragment";
                break;


            case 7:
                fragment = new GroupListFragment();
                mTitle.setText("MY GROUPS");
                tag = "GroupListFragment";
                Bundle bundle = new Bundle();
                if (groupId != null && groupId.trim().length() > 0) {
                    bundle.putString("group_id", groupId);
                    Log.e("GroupListFragment--", "--------" + groupId);
                    fragment.setArguments(bundle);
                    groupId = null;
                }

                break;
            case 8:
                fragment = new NotificationsFragment();
                mTitle.setText("NOTIFICATIONS");
                tag = "NotificationsFragment";
                break;


            case 9:
                if (CommonClass.getUserpreference(NavigationDrawerActivity.this).login_type.equalsIgnoreCase("normal")) {
                    fragment = new ChangePasswordFragment();
                    mTitle.setText("CHANGE PASSWORD");
                    tag = "ChangePasswordFragment";
                } else {

                    new AlertDialog.Builder(NavigationDrawerActivity.this)
                            .setMessage("You are logged in with Facebook.")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                        /*.setNegativeButton("No", null)*/
                            .show();
//                    CommonClass.ShowToast(NavigationDrawerActivity.this,"Sorry you cannot change your password.");
                }
                break;
            case 10:
                fragment = new SettingFragment();
                mTitle.setText("SETTINGS");
                tag = "SettingFragment";
                break;
            default:
                break;
        }

        int delayTime = 0;

        if (position == 2)
            delayTime = 500;

        if (fragment != null) {
            final Handler handler = new Handler();
            final Fragment finalFragment = fragment;
            final String finalTag = tag;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms

                    ride_id="";
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, finalFragment, finalTag);
                    fragmentTransaction.commit();
                    // set the toolbar title

                }
            }, delayTime);

        }
    }

    public int getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    @Override
    public void onBackPressed() {
        Log.i("TAG", "======OnBackPressed=====>>>>");
        CyncTankFragment fragment = (CyncTankFragment) getSupportFragmentManager().findFragmentByTag("CyncTankFragment");

        if (fragment != null) {
            if (fragment.isVideoPlaying()) {
                fragment.setPortaite();
            } else {
                String tag = "";
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();

                Log.i("Navigation", "===========>>>>" + tag);
//        if(tag!=null)
                if (tag.equalsIgnoreCase("OtherUser")) {
                    getSupportFragmentManager().popBackStack();
                } else if (tag.equalsIgnoreCase("InviteFriendFragment")) {
                    displayView(5);
//                    Intent intent = new Intent(NavigationDrawerActivity.this, NavigationDrawerActivity.class);
//                    intent.putExtra("notification", "4");
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                    //finish();
                } else
                    alertDialog(NavigationDrawerActivity.this, "Do you want to exit from the app?", true);
            }
        } else {

            String tag = "";
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
//        if(tag!=null)

            Log.i("Navigation", "======ffdfd=====>>>>" + tag);


            if (tag.equalsIgnoreCase("OtherUser")) {
                getSupportFragmentManager().popBackStack();
            } else if (tag.equalsIgnoreCase("InviteFriendFragment")) {

                displayView(5);
//                Intent intent = new Intent(NavigationDrawerActivity.this, NavigationDrawerActivity.class);
//                intent.putExtra("notification", "4");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
                //finish();
            } else
                alertDialog(NavigationDrawerActivity.this, "Do you want to exit from the app?", true);
        }
    }

    public void callLogout() {

        if (CommonClass.getUserpreference(NavigationDrawerActivity.this).login_type.equalsIgnoreCase("facebook")) {
//            System.out.printf("--Call Facebook------------------");
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();

        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        CommonClass.setRideIds(NavigationDrawerActivity.this,"");
        CommonClass.setIsFirstTimepreference(NavigationDrawerActivity.this, "true");
        CommonClass.ClearUserpreference(NavigationDrawerActivity.this);
        CommonClass.ClearChatUserpreference(NavigationDrawerActivity.this);

        Intent serviceIntent = new Intent(NavigationDrawerActivity.this,
                XMPPService.class);
        stopService(serviceIntent);

        Intent intent = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
        finish();
    }

    private void sendRequestLogout() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.logout, SuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(NavigationDrawerActivity.this).user_id);
                Log.e("Request Logout", "logout==> " + Constants.logout + requestparam);

                return requestparam;
            }
        };
        CommonClass.showLoading(NavigationDrawerActivity.this);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "logout==> " + response);
                CommonClass.closeLoding(NavigationDrawerActivity.this);
                boolean Status;
                String message;
                try {

                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        //  mUserListData.get(mPosition).setStatus("Pending");
                        CommonClass.ShowToast(NavigationDrawerActivity.this, message);
                        callLogout();
                    } else {
                        CommonClass.ShowToast(NavigationDrawerActivity.this, message);
                    }
                } catch (JSONException e) {
                    message = NavigationDrawerActivity.this.getString(R.string.s_wrong);
                    CommonClass.ShowToast(NavigationDrawerActivity.this, message);
                    e.printStackTrace();
                }
//                loadFragmentAgain();
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(NavigationDrawerActivity.this);
                CommonClass.ShowToast(NavigationDrawerActivity.this, NavigationDrawerActivity.this.getString(R.string.s_wrong));
            }
        };
    }


    public void alertDialog(final Activity context, String steMessage, boolean flagNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(steMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (mIntent != null) {
                    mIntent.replaceExtras(new Bundle());
                    mIntent.setAction("");
                    mIntent.setData(null);
                    mIntent.setFlags(0);
                    mIntent = null;


                }

                getIntent().replaceExtras(new Bundle());
                getIntent().setAction("");
                getIntent().setData(null);
                getIntent().setFlags(0);

                context.finish();
//                System.exit(0);
//
//                Intent intent = new Intent();
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(intent);
//                finish();


            }
        });
        if (flagNo) {
            builder.setNegativeButton("No", null);
        }
        builder.show();
    }
}