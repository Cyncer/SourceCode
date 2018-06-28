package com.app.android.cync;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
public class NavigationDrawerActivityTest extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private static String TAG = NavigationDrawerActivityTest.class.getSimpleName();
    public static Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private com.rey.material.widget.LinearLayout llLogout;
    private TextView mTitle;
    public static boolean isDrawerMenuOpen;
    private RequestQueue mQueue;
    android.widget.RelativeLayout nav_header_container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initId();
        // display the first navigation drawer view on app launch

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Intent serviceIntent = new Intent(NavigationDrawerActivityTest.this, XMPPService.class);
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
                new AlertDialog.Builder(NavigationDrawerActivityTest.this)
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
//                CommonClass.ShowToast(NavigationDrawerActivityTest.this,"Bas Have Bahu thayu");
            }
        });


        drawerFragment.setDrawerListener(this);


        if (getIntent() != null) {

            UserDetail userDetail = CommonClass.getUserpreference(NavigationDrawerActivityTest.this);

            if (userDetail != null && userDetail.first_name.length() > 0 && !userDetail.first_name.equalsIgnoreCase("null")) {


                String action = getIntent().getAction();
                String type = getIntent().getType();

                if (Intent.ACTION_SEND.equals(action) && type != null) {
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
                } else {
                    showFragmentInitially();

                    // Handle other intents, such as being started from the home screen
                }
            } else {
                Intent intent = new Intent(NavigationDrawerActivityTest.this, LoginActivity.class);
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
                groupId = bundle.getString("group_id", "");
                Log.e("getExtras--", "showFragmentInitially getExtras--------string=" + string);
                Log.e("getExtras--", "showFragmentInitially getExtras--------" + groupId);
                if (string != null & string.length() > 0) {
                    displayView(Integer.parseInt(string));
                    drawerFragment.setAdapterData(Integer.parseInt(string));
                } else {

                    boolean showMessage = CheckValid();


                    // Log.i("NavigationDrawerActivityTest","Kp showMessage==>"+showMessage+" Prf="+CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this));

                    if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this).equals("true") && showMessage) {
                        displayView(3);
//                        CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "false");
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "false");
//
//                            //Toast.makeText(NavigationDrawerActivityTest.this, "Don't forget to add your ride! Go to your profile to select your motorcycle  make and model.", Toast.LENGTH_LONG).show();
//
//                        }
//
//                    }, 7000);

                    } else {
                        displayView(1);
                    }
                }
            } else {


                boolean showMessage = CheckValid();
                // Log.i("NavigationDrawerActivityTest","Kp showMessage==>"+showMessage+" Prf="+CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this));

                if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this).equals("true") && showMessage) {
                    displayView(3);
                    //                   CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "false");
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "false");
//
//                            //Toast.makeText(NavigationDrawerActivityTest.this, "Don't forget to add your ride! Go to your profile to select your motorcycle  make and model.", Toast.LENGTH_LONG).show();
//
//                        }
//
//                    }, 7000);

                } else {
                    displayView(1);
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Intent mIntent = null;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {


            if (bundle.containsKey("notification")) {
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


                    // Log.i("NavigationDrawerActivityTest","Kp showMessage==>"+showMessage+" Prf="+CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this));

                    if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this).equals("true") && showMessage) {
                        displayView(3);
//                        CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "false");
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "false");
//
//                            //Toast.makeText(NavigationDrawerActivityTest.this, "Don't forget to add your ride! Go to your profile to select your motorcycle  make and model.", Toast.LENGTH_LONG).show();
//
//                        }
//
//                    }, 7000);

                    } else {
                        displayView(1);
                    }
                }

            } else {

                mIntent = intent;
                String action = intent.getAction();
                String type = intent.getType();

                if (Intent.ACTION_SEND.equals(action) && type != null) {
//            Toast.makeText(getApplicationContext(),"Toast Message "+type,Toast.LENGTH_SHORT).show();
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
            if (CommonClass.getIsFirstTimepreference(NavigationDrawerActivityTest.this).equals("true") && showMessage) {
                displayView(3);


            } else {
                displayView(1);
            }


        }


    }


    private boolean CheckValid() {

        if (CommonClass.getUserpreference(NavigationDrawerActivityTest.this).make.trim().length() > 0 && CommonClass.getUserpreference(NavigationDrawerActivityTest.this).year.trim().length() > 0 && CommonClass.getUserpreference(NavigationDrawerActivityTest.this).model.trim().length() > 0) {
            return false;
        } else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    private void displayView(int position) {
        Fragment fragment = null;
        String tag = "";

        drawerFragment.setAdapterData(position);

        switch (position) {

            case 0:

                if (mIntent == null) {

                    // Toast.makeText(getApplicationContext(),"mIntent null ",Toast.LENGTH_SHORT).show();
                    fragment = new CyncTankFragment("");
                } else {
                    fragment = new CyncTankFragment(mIntent);
                    mIntent = null;
                }
                mTitle.setText("CYNC TANK");
                tag = "CyncTankFragment";
                break;


            case 1:
                fragment = new HomeFragment();
                mTitle.setText("HOME");
                tag = "HomeFragment";
                break;
            case 2:
                fragment = new MyProfileFragment();
                mTitle.setText("MY PROFILE");
                tag = "MyProfileFragment";
                break;

            case 3:
                fragment = new CyncTankFragment("My Posts");
                mTitle.setText("MY POSTS");
                tag = "CyncTankFragmentMyPost";
                break;


            case 4:
                fragment = new FriendListFragment();
                mTitle.setText("FRIENDS LIST");
                tag = "FriendListFragment";
                break;

            case 5:
                fragment = new BlockedUserFragment();
                mTitle.setText("BLOCKED USERS");
                tag = "BlockedUserFragment";
                break;


            case 6:
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
            case 7:
                fragment = new NotificationsFragment();
                mTitle.setText("NOTIFICATIONS");
                tag = "NotificationsFragment";
                break;

            case 8:

                //  Toast.makeText(NavigationDrawerActivityTest.this,"Coming Soon",Toast.LENGTH_SHORT).show();
                fragment = new AssistFragment();
                mTitle.setText("CYNC ASSIST");
                tag = "AssistFragment";
                break;


            case 9:
                if (CommonClass.getUserpreference(NavigationDrawerActivityTest.this).login_type.equalsIgnoreCase("normal")) {
                    fragment = new ChangePasswordFragment();
                    mTitle.setText("CHANGE PASSWORD");
                    tag = "ChangePasswordFragment";
                } else {
                    new AlertDialog.Builder(NavigationDrawerActivityTest.this)
                            .setMessage("You are logged in with Facebook.")
                            .setCancelable(true)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                        /*.setNegativeButton("No", null)*/
                            .show();
//                    CommonClass.ShowToast(NavigationDrawerActivityTest.this,"Sorry you cannot change your password.");
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

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment, tag);
            fragmentTransaction.commit();
            // set the toolbar title
            getSupportActionBar().setTitle("");
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

        CyncTankFragment fragment = (CyncTankFragment) getSupportFragmentManager().findFragmentByTag("CyncTankFragment");

        if (fragment != null) {
            if (fragment.isVideoPlaying()) {
                fragment.setPortaite();
            } else {
                String tag = "";
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
//        if(tag!=null)
                if (tag.equalsIgnoreCase("OtherUser")) {
                    getSupportFragmentManager().popBackStack();
                } else if (tag.equalsIgnoreCase("InviteFriendFragment")) {
                    Intent intent = new Intent(NavigationDrawerActivityTest.this, NavigationDrawerActivityTest.class);
                    intent.putExtra("notification", "3");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else
                    alertDialog(NavigationDrawerActivityTest.this, "Do you want to exit from the app?", true);
            }
        } else {
            String tag = "";
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
//        if(tag!=null)
            if (tag.equalsIgnoreCase("OtherUser")) {
                getSupportFragmentManager().popBackStack();
            } else if (tag.equalsIgnoreCase("InviteFriendFragment")) {
                Intent intent = new Intent(NavigationDrawerActivityTest.this, NavigationDrawerActivityTest.class);
                intent.putExtra("notification", "3");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else
                alertDialog(NavigationDrawerActivityTest.this, "Do you want to exit from the app?", true);
        }
    }

    public void callLogout() {
        if (CommonClass.getUserpreference(NavigationDrawerActivityTest.this).login_type.equalsIgnoreCase("facebook")) {
//            System.out.printf("--Call Facebook------------------");
            FacebookSdk.sdkInitialize(getApplicationContext());
            LoginManager.getInstance().logOut();

        }
        CommonClass.setIsFirstTimepreference(NavigationDrawerActivityTest.this, "true");
        CommonClass.ClearUserpreference(NavigationDrawerActivityTest.this);
        CommonClass.ClearChatUserpreference(NavigationDrawerActivityTest.this);

        Intent serviceIntent = new Intent(NavigationDrawerActivityTest.this,
                XMPPService.class);
        stopService(serviceIntent);

        Intent intent = new Intent(NavigationDrawerActivityTest.this, LoginActivity.class);
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
                requestparam.put("user_id", "" + CommonClass.getUserpreference(NavigationDrawerActivityTest.this).user_id);
                Log.e("Request Logout", "logout==> " + Constants.logout + requestparam);

                return requestparam;
            }
        };
        CommonClass.showLoading(NavigationDrawerActivityTest.this);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "logout==> " + response);
                CommonClass.closeLoding(NavigationDrawerActivityTest.this);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        //  mUserListData.get(mPosition).setStatus("Pending");
                        CommonClass.ShowToast(NavigationDrawerActivityTest.this, message);
                        callLogout();
                    } else {
                        CommonClass.ShowToast(NavigationDrawerActivityTest.this, message);
                    }
                } catch (JSONException e) {
                    message = NavigationDrawerActivityTest.this.getString(R.string.s_wrong);
                    CommonClass.ShowToast(NavigationDrawerActivityTest.this, message);
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
                CommonClass.closeLoding(NavigationDrawerActivityTest.this);
                CommonClass.ShowToast(NavigationDrawerActivityTest.this, NavigationDrawerActivityTest.this.getString(R.string.s_wrong));
            }
        };
    }

    public void alertDialog(final Activity context, String steMessage, boolean flagNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(steMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if(mIntent!=null) {
                    mIntent.replaceExtras(new Bundle());
                    mIntent.setAction("");
                    mIntent.setData(null);
                    mIntent.setFlags(0);
                    mIntent=null;



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