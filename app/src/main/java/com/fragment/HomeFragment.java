package com.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.ChatActivity;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.app.android.cync.ShareActivity;
import com.customwidget.CircleImageView;
import com.customwidget.materialEditText;
import com.cync.model.Ride;
import com.cync.model.UserDetail;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.location.LocationDatabase;
import com.location.LocationService;
import com.rey.material.widget.Button;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.utils.GPSTracker;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.view.View.VISIBLE;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class HomeFragment extends BaseContainerFragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, OnMapReadyCallback {

    private static final int SHARE_CODE = 104;
    boolean onlyOnce = true;
    ArrayList<Ride> mRideList = new ArrayList<>();
    ArrayList<String> mRideIdList = new ArrayList<>();

    int colorList[] = {

            Color.parseColor("#004483"),
            Color.parseColor("#f80905"),
            Color.parseColor("#026c00"),
            Color.parseColor("#006c53"),
            Color.parseColor("#6c0061"),
            Color.parseColor("#695100"),
            Color.parseColor("#00a747"),
            Color.parseColor("#2800a7"),
            Color.parseColor("#74a700"),
            Color.parseColor("#008092")

    };
    boolean isTimeOk = false;
    int[] interval = {5, 15, 30, 60, 5, 15, 30, 1, 1};
    int selectedinterval;

    BroadcastReceiver updateUIReciver;
    static LocationDatabase db;
    ImageView imgPlayPause, imgFinish;

    RelativeLayout rlTrack;

    int deletedIndex = -1;
    LatLngBounds.Builder mybuilder = new LatLngBounds.Builder();
    LatLngBounds.Builder builder = new LatLngBounds.Builder();
    //-------
    private static long INTERVAL = 1000 * 20;
    private static long FASTEST_INTERVAL = 1000 * 20;
    private static final String TAG = "LocationActivity";
    //------- Google Map
    MapView mapView;
    GoogleMap googleMap;
    GPSTracker gps;
    ImageView imgMyLocation;
    double currentLat = 0.0, currentLng = 0.0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    Activity mActivity;
    LocationManager mLocationManager;

    List<LatLng> latLngList = new ArrayList<LatLng>();
    ArrayList<UserDetail> arrayList;
    boolean isGroupLocation = false, showCurrLoc = true;
    LatLng latLng;

    private RequestQueue mQueue;
    private String groupId;
    private RelativeLayout rlTitle;
    private ImageView ivBack;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    Marker markerLoginUser = null;
    ViewGroup rootViewMain;
    String ride_id = "";

    public HomeFragment() {

    }


    public HomeFragment(String ride_id) {
        // Required empty public constructor
        this.ride_id = ride_id;


    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (!isGooglePlayServicesAvailable()) {
            mActivity.finish();
        }

        showProgress();
        db = new LocationDatabase(getActivity());
        createLocationRequest();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();


        boolean showMessage = CheckValid();

//        if (CommonClass.getIsFirstTimepreference(getActivity()).equals("true") && showMessage) {
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//
//                    CommonClass.setIsFirstTimepreference(getActivity(), "false");
//
//                    Toast.makeText(getActivity(), "Don't forget to add your ride! Go to your profile to select your motorcycle  make and model.", Toast.LENGTH_LONG).show();
//
//                }
//
//            }, 7000);
//
//        }

//        if (intent != null) {
//            Uri data = intent.getData();
//            if (data != null) {
//                Log.i(TAG, "onNewIntent: received, with data: " + data.getQueryParameter("ride_id"));
//                ride_id = data.getQueryParameter("ride_id");
//
//                Log.i(TAG, "onCreate: Ride id="+ride_id);
//                intent.replaceExtras(new Bundle());
//                intent.setAction("");
//                intent.setData(null);
//                intent.setFlags(0);
//
//            }
//
//
//        }
    }

    private boolean CheckValid() {


        if (CommonClass.getUserpreference(getActivity()).make.trim().length() > 0 && CommonClass.getUserpreference(getActivity()).year.trim().length() > 0 && CommonClass.getUserpreference(getActivity()).model.trim().length() > 0) {
            return false;
        } else
            return true;


    }


    public void checkGPSon() {
        if (CommonClass.doWeHavePermisiionForFromFragment(getActivity(), this, Manifest.permission.ACCESS_FINE_LOCATION, "Needs Location Permission.", true)) {
            try {
                boolean statusOfGPS = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!statusOfGPS) {
                    gps.showSettingsAlert();
                }
                if (mGoogleApiClient == null) {
                    mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                            .addApi(LocationServices.API)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            isGroupLocation = true;
            groupId = getArguments().getString("group_id", "");
        } else {
            groupId = "";
            isGroupLocation = false;
        }
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        rootViewMain = container;
        mQueue = VolleySetup.getRequestQueue();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        Init(rootView);
        try {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);

            //googleMap = mapView.getMap();
//            googleMap.setMyLocationEnabled(true);
//            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

//            googleMap.getUiSettings().setAllGesturesEnabled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        checkGPSon();


        // Inflate the layout for this fragment

        IntentFilter filter = new IntentFilter();

        filter.addAction("com.inanny.action");

        updateUIReciver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //UI update here

//                textViewLastTrackDate.setText(""
//                        + CommonClass.getLastTrackDate(getApplicationContext()));
//                textViewLastTrackTime.setText(""
//                        + CommonClass.getLastTrackTime(getApplicationContext()));

            }
        };
        getActivity().registerReceiver(updateUIReciver, filter);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        getDataFromWeb();
    }

    private void getDataFromWeb() {
        if (CommonClass.getTrackingInterval(getActivity()) == 5000) {
            selectedinterval = 0;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 15000) {
            selectedinterval = 1;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 30000) {
            selectedinterval = 2;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 60000) {
            selectedinterval = 3;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 300000) {
            selectedinterval = 4;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 900000) {
            selectedinterval = 5;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 1800000) {
            selectedinterval = 6;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 3600000) {
            selectedinterval = 7;
        } else if (CommonClass.getTrackingInterval(getActivity()) == 86400000) {
            selectedinterval = 8;
        }


        Log.i(TAG, "onCreateView: Ride Id == " + ride_id);
        stopProgress();

        if (!isGroupLocation) {

            String ex_ridesm = CommonClass.getRideIds(getActivity());

            Log.i(TAG, "onCreateView: Total Rides   = " + ex_ridesm);


            if (CommonClass.getRideIds(getActivity()).trim().length() > 0 || ride_id.trim().length() > 0) {


                if (ride_id.trim().length() > 0) {
                    String ex_rides = CommonClass.getRideIds(getActivity());

                    Log.i(TAG, "onCreateView: Total Rides 1  = " + ex_rides);


                    String rides[] = ex_rides.split(",");
                    List<String> stringList = new ArrayList<String>(Arrays.asList(rides));
                    if (stringList.indexOf(ride_id) == -1) {

                        if (ex_rides.trim().length() > 0)
                            ex_rides = ex_rides + "," + ride_id;
                        else
                            ex_rides = ride_id;

                        Log.i(TAG, "onCreateView: Total Rides 2 = " + ex_rides);
                        CommonClass.setRideIds(getActivity(), ex_rides);
                    }
                }


                getRideDetail();
            } else {

                if (CommonClass.getUserpreference(getActivity()).view_my_rides)
                    getMyRide();
                else
                    updateMarker(new LatLng(currentLat, currentLng));
            }
        } else {
            updateMarker(new LatLng(currentLat, currentLng));
        }
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

    public void Init(View view) {
        if (isGroupLocation) {
//            mCurrentLocation=null;
            rlTitle = (RelativeLayout) view.findViewById(R.id.rlTitle);


            rlTitle.setVisibility(VISIBLE);
            ivBack = (ImageView) view.findViewById(R.id.ivBack);
            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popFragment();
                }
            });
            try {
                view.setFocusableInTouchMode(true);
                view.requestFocus();
                view.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            // handle back button's click listener
                            popFragment();
                            return true;
                        }
                        return false;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        gps = new GPSTracker(getActivity());

//        String lat = CommonClass.getPrefranceByKey_Value(getActivity(), CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lat);
//        String lng = CommonClass.getPrefranceByKey_Value(getActivity(), CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lng);
//        if (lat != null && lat.trim().length() > 0)
//            currentLat = Double.parseDouble(lat);
//        if (lng != null && lng.trim().length() > 0)
//            currentLng = Double.parseDouble(lng);
        mapView = (MapView) view.findViewById(R.id.mapview);
        currentLat = gps.getLatitude();
        currentLng = gps.getLongitude();


        rlTrack = (RelativeLayout) view.findViewById(R.id.rlTrack);
        imgMyLocation = (ImageView) view.findViewById(R.id.imgMyLocation);
        imgPlayPause = (ImageView) view.findViewById(R.id.imgPlayPause);
        imgFinish = (ImageView) view.findViewById(R.id.imgFinish);
        imgPlayPause.setOnClickListener(this);
        imgFinish.setOnClickListener(this);
        //  showPopupMenu();


        if (CommonClass.getlocationServicepreference(getActivity())
                .equalsIgnoreCase("false")) {

            Log.i(TAG, "Init: Delete Data ");


            if (CommonClass.getlocationServiceCurrentState(getActivity()) == R.drawable.btn_resume) {
                imgPlayPause.setImageResource(R.drawable.btn_resume);
                imgFinish.setVisibility(VISIBLE);

            } else {
                db.deleteAllData();
                imgPlayPause.setImageResource(R.drawable.btn_record);
            }


        } else {

            Log.i(TAG, "Init: Delete Data else ");
            if (CommonClass.getlocationServiceCurrentState(getActivity()) != 0) {
                imgPlayPause.setImageResource(CommonClass.getlocationServiceCurrentState(getActivity()));

                if (CommonClass.getlocationServiceCurrentState(getActivity()) == R.drawable.btn_resume) {

                    imgFinish.setVisibility(VISIBLE);

                }

            } else
                imgPlayPause.setImageResource(R.drawable.btn_record);

        }


        rlTrack.setVisibility(View.GONE);


        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {


                    if (googleMap != null && arrayList.size() > 0) {

                        builder.include(new LatLng(arrayList.get(0).dLat, arrayList.get(0).dLng));
//                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                            .target(new LatLng(arrayList.get(0).dLat, arrayList.get(0).dLng)).build();
//
//
//                    LatLngBounds bounds;
//                    if(mybuilder!=null)
//                    bounds= mybuilder.build();
//                    else
//                    bounds= builder.build();
//
//                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 35));


                        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                        Display display = wm.getDefaultDisplay();
                        DisplayMetrics metrics = new DisplayMetrics();
                        display.getMetrics(metrics);
                        int width = metrics.widthPixels;
                        int height = metrics.heightPixels;


                        LatLngBounds bounds;
                        if (mybuilder != null)
                            bounds = mybuilder.build();
                        else
                            bounds = builder.build();
                        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngBounds(bounds, width / 3, height / 3, 45);


                        CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                                .target(new LatLng(arrayList.get(0).dLat, arrayList.get(0).dLng));


                        cameraBuilder.zoom(15);

                        CameraPosition cameraPosition = cameraBuilder.build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                        onlyOnce = false;
                    }


                } catch (Exception e) {

                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart fired ..............");
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mapView != null)
                mapView.onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null)
            mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter("com.cync.location.data"));

        INTERVAL = 1000 * 20;
        FASTEST_INTERVAL = 1000 * 20;
        createLocationRequest();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }


        try {
            if (mapView != null)
                mapView.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();

        INTERVAL = 1000 * 40;
        FASTEST_INTERVAL = 1000 * 40;
//        createLocationRequest();
//        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
//            mGoogleApiClient.connect();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
//        }

//        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
        stopLocationUpdates();
    }
//    public static void main(String[] args) throws Exception {
//        String s = "01:19 PM";
//        Date time = null;
//        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
//        System.out.println(time = parseFormat.parse(s));
//    }
//    private String getDate(String OurDate)
//    {
//        OurDate=OurDate.substring(0,OurDate.lastIndexOf(":"));//+" "+tmp[tmp.length-1];
//        try
//        {
//            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm");
//            Date value = formatter.parse(OurDate);
//
//            SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy HH:mm");
//            dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));//this format changeable
////            dateFormatter.setTimeZone(TimeZone.getDefault());
//            OurDate = dateFormatter.format(value);
//
//            //Log.d("OurDate", OurDate);
//        }
//        catch (Exception e)
//        {
//            OurDate = "00-00-0000 00:00:00 am";
//            e.printStackTrace();
//        }
//        return OurDate;
//    }
//    public static String DateTime(String dtStart) {
//        try {
//            //----------------------
////            String dateStr = "Jul 16, 2013 12:08:59 AM";
//            Calendar cal = Calendar.getInstance();
//            TimeZone tz = cal.getTimeZone();
//            SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");
////            SimpleDateFormat dfOutput = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
//            df.setTimeZone(tz);
//            Date date = df.parse(dtStart);
//            df.setTimeZone(tz.getDefault());
//            return df.format(date);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "00-00-0000 00:00";
//        }
//        //SimpleDateFormat dateformat = new SimpleDateFormat("MM-dd/yyyy hh:mm:ss a");
//
//    }

    private synchronized void insertMarkers(final ArrayList<UserDetail> list) {
//        Log.e("insertMarkers", "" + list.size());


        try {
            if (googleMap != null)
                googleMap.clear();

            for (int i = 0; i < list.size(); i++) {
//            Log.e("Firstname", "" + list.get(i).first_name);
//                Double tLat = Double.parseDouble(list.get(i).lat.trim());
//                Double tLng = Double.parseDouble(list.get(i).lng.trim());
                if (!(list.get(i).dLat == -0.1)) {
                    LatLng tempLatLng = new LatLng(list.get(i).dLat, list.get(i).dLng);
                    final MarkerOptions options = new MarkerOptions().position(tempLatLng);
                    if (i == 0) {


                        //  if (!isGroupLocation)
                        {
                            final int finalI = i;

                            String imagePath = list.get(finalI).getUser_image();
                            if (imagePath != null && imagePath.length() > 0) {

                                if (imagePath.contains("https://") || imagePath.contains("http://"))
                                    imagePath = imagePath.trim();
                                else
                                    imagePath = Constants.imagBaseUrl + imagePath.trim();

                            }


                            final String finalImagePath = imagePath;
                            Bitmap smallMarker = null;
                            if (finalImagePath.trim().length() > 0) {
                                URL url = new URL(finalImagePath);
                                Bitmap bmp = null;
                                try {
                                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                } catch (FileNotFoundException e) {
                                    bmp = null;
                                }


                                if (bmp != null) {
                                    smallMarker = Bitmap.createScaledBitmap(bmp, 100, 100, false);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                } else {

                                    Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                            R.drawable.no_image);
                                    smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                                }
                            } else {
                                Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                        R.drawable.no_image);
                                smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            }


                            View markerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                            CircleImageView markerImageView = (CircleImageView) markerView.findViewById(R.id.imgUserImage);
                            markerImageView.setImageBitmap(smallMarker);
                            markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
                            markerView.buildDrawingCache();
                            Bitmap returnedBitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(returnedBitmap);
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            Drawable drawable = markerView.getBackground();
                            if (drawable != null)
                                drawable.draw(canvas);
                            markerView.draw(canvas);
                            options.icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap));


                        }
//                        else {
//
//
//
//                            options.icon(BitmapDescriptorFactory
//                                    .fromResource(R.drawable.select_map_pin));
//                        }
                    } else {
                        if (isGroupLocation) {


                            final int finalI = i;

                            String imagePath = list.get(finalI).getUser_image();
                            if (imagePath != null && imagePath.length() > 0) {

                                if (imagePath.contains("https://") || imagePath.contains("http://"))
                                    imagePath = imagePath.trim();
                                else {
                                    imagePath = Constants.imagBaseUrl + imagePath.trim();
                                }

                            }


                            final String finalImagePath = imagePath;

                            Log.i(TAG, "insertMarkers: finalImagePath=======" + finalImagePath);
                            Bitmap smallMarker = null;
                            if (finalImagePath.trim().length() > 0) {
                                try {
                                    URL url = new URL(finalImagePath);
                                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    smallMarker = Bitmap.createScaledBitmap(bmp, 100, 100, false);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                } catch (FileNotFoundException e) {
                                    Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                            R.drawable.no_image);
                                    smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                }


                            } else {
                                Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                        R.drawable.no_image);
                                smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            }
                            View markerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                            CircleImageView markerImageView = (CircleImageView) markerView.findViewById(R.id.imgUserImage);
                            ImageView imgBg = (ImageView) markerView.findViewById(R.id.imgBg);


                            if (list.get(i).group_is_friend) {
                                imgBg.setImageResource(R.drawable.tmp_pin_grey);
                                options.icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.tmp_pin_grey));
                            } else {

                                imgBg.setImageResource(R.drawable.tmp_pin_green);
                                options.icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.tmp_pin_green));
                            }
                            if (list.get(i).group_is_friend)
                                markerImageView.setImageBitmap(smallMarker);
                            markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
                            markerView.buildDrawingCache();
                            Bitmap returnedBitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(returnedBitmap);
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            Drawable drawable = markerView.getBackground();
                            if (drawable != null)
                                drawable.draw(canvas);
                            markerView.draw(canvas);
                            options.icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap));


                        } else {

                            final int finalI = i;

                            String imagePath = list.get(finalI).getUser_image();
                            if (imagePath != null && imagePath.length() > 0) {

                                if (imagePath.contains("https://") || imagePath.contains("http://"))
                                    imagePath = imagePath.trim();
                                else {
                                    imagePath = Constants.imagBaseUrl + imagePath.trim();
                                }

                            }


                            final String finalImagePath = imagePath;

                            Bitmap smallMarker = null;
                            if (finalImagePath.trim().length() > 0) {
                                try {
                                    URL url = new URL(finalImagePath);
                                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    smallMarker = Bitmap.createScaledBitmap(bmp, 100, 100, false);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                } catch (FileNotFoundException e) {
                                    Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                            R.drawable.no_image);
                                    smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                    options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                }
                            } else {
                                Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                        R.drawable.no_image);
                                smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            }


                            View markerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                            CircleImageView markerImageView = (CircleImageView) markerView.findViewById(R.id.imgUserImage);
                            markerImageView.setImageBitmap(smallMarker);
                            markerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            markerView.layout(0, 0, markerView.getMeasuredWidth(), markerView.getMeasuredHeight());
                            markerView.buildDrawingCache();
                            Bitmap returnedBitmap = Bitmap.createBitmap(markerView.getMeasuredWidth(), markerView.getMeasuredHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(returnedBitmap);
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            Drawable drawable = markerView.getBackground();
                            if (drawable != null)
                                drawable.draw(canvas);
                            markerView.draw(canvas);
                            options.icon(BitmapDescriptorFactory.fromBitmap(returnedBitmap));


//                            options.icon(BitmapDescriptorFactory
//                                    .fromResource(R.drawable.map_pin));
                        }
                    }
//                    String strName= CommonClass.strEncodeDecode(list.get(i).first_name,true);
                    options.title(list.get(i).first_name);
                    String str = list.get(i).update_location;
//                    String string = "";
                    Log.e("list------", "====" + list.get(i).update_location);
//                    if (str != null && str.trim().length() > 0)
//                        string = getDate(str);
//                    Log.e("string------", "====" + string);
                    if (str != null && str.trim().length() > 0)
                        options.snippet("Last Updated Time: " + str);
                    else
                        options.snippet("Last Updated Time: 00-00-0000 00:00");


                    Marker marker = googleMap.addMarker(options);


                    if (list.get(i).user_id.equalsIgnoreCase(CommonClass.getUserpreference(getActivity()).user_id)) {
                        markerLoginUser = marker;

                    }

                    mHashMap.put(marker, i);
                    builder.include(tempLatLng);

                    mybuilder = builder;
                }


                if (isGroupLocation) {
                    rlTrack.setVisibility(View.GONE);
                } else {
                    rlTrack.setVisibility(VISIBLE);
                }
            }
            stopProgress();
            if (showCurrLoc) {


                zoomMap();

//                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
//                Display display = wm.getDefaultDisplay();
//                DisplayMetrics metrics = new DisplayMetrics();
//                display.getMetrics(metrics);
//                int width = metrics.widthPixels;
//                int height = metrics.heightPixels;
//
//                final LatLngBounds bounds = builder.build();
//                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngBounds(bounds, width / 3, height / 3, 35);
//
//
//                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
//                        .target(new LatLng(list.get(0).dLat, list.get(0).dLng));
//
//
//                cameraBuilder.zoom(13);
//
//
//                CameraPosition cameraPosition = cameraBuilder.build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//                final CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(new LatLng(list.get(0).dLat, list.get(0).dLng)).build();
//                LatLngBounds bounds = builder.build();
//                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 35));
            }


            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int pos = mHashMap.get(marker);
                    UserDetail userDetail = list.get(pos);
//                Log.e("onInfoWindowClick","-----"+groupId);
                    if (groupId != null && groupId.trim().length() > 0) {
//                    Log.e("userDetail.","-----"+userDetail.group_is_friend);
                        if (userDetail.group_is_friend)
                            openChatActivity(userDetail);
                    } else
                        openChatActivity(userDetail);

                }
            });

            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {

                    // Toast.makeText(getActivity(), "" + marker.getSnippet(), Toast.LENGTH_SHORT).show();


                    if (marker.getTitle().equalsIgnoreCase("Start point") || marker.getTitle().equalsIgnoreCase("End point")) {


                        int index = -1;
                        try {
                            index = Integer.parseInt(marker.getSnippet());
                        } catch (NumberFormatException e) {

                        }
                        if (index != -1) {


                            showDetailPopupMenu(mRideList.get(index), index);

                        }

                        return true;
                    } else
                        return false;
                }
            });
            showCurrLoc = false;

            if (!isGroupLocation) {
                List<LatLng> lList = db.getLocationList();
//                if(markerLoginUser!=null)
//                lList.add(markerLoginUser.getPosition());
                drawPolyLineOnMapAll(lList, -1, "", "#000000", Color.parseColor("#000000"));


                if (mRideList.size() > 0) {
                    for (int i = 0; i < mRideList.size(); i++) {

                        List<LatLng> tmpList = mRideList.get(i).mDataList;
                        drawPolyLineOnMapAll(tmpList, i, "server", mRideList.get(i).color, mRideList.get(i).colorCode);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    public static String timeZone()
//    {
//        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault());
//        String   timeZone = new SimpleDateFormat("Z").format(calendar.getTime());
//        return timeZone.substring(0, 3) + ":"+ timeZone.substring(3, 5);
//    }


    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    private void openChatActivity(UserDetail userDetail) {
//        Log.e("if------","-----"+list.get(pos));
        if (!CommonClass.getUserpreference(mActivity).user_id.equalsIgnoreCase(userDetail.user_id) && userDetail != null) {
            Intent i = new Intent(mActivity, ChatActivity.class);
            Bundle data = new Bundle();
//                    Log.e("HOME---", "profileimage----" + userDetail.user_image);
//                    String strName= CommonClass.strEncodeDecode(userDetail.first_name,true);
            data.putString("FROM_MESSGE", "cync_" + userDetail.user_id);
            data.putString("titlename", userDetail.first_name);
            data.putString("profileimage", userDetail.user_image);
            data.putString("type", "1");
            i.putExtras(data);
            mActivity.startActivity(i);
        }
    }

    private void updateMarker(LatLng tLatLng) {
        System.out.println("update marker called " + tLatLng.latitude);
        System.out.println("update marker called " + tLatLng.longitude);
        latLng = tLatLng;
        if (isGroupLocation && groupId != null && groupId.trim().length() > 0)
            updateGroupLocation();
        else
            updateHomeFragment();
        /*if (marker != null)
        {
            marker.setPosition(position);
            CommonClass.zoomCamera(position, googleMap);
        }*/
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, mActivity, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
//        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        LocationServices.FusedLocationApi.requestLocationUpdates()

//            Log.d(TAG, "Location update started ..............: ");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
//        if (mCurrentLocation == null )
        mCurrentLocation = location;

//        int distance = (int) mCurrentLocation.distanceTo(location);
//        Log.e("Distance", "--Distance---------" + distance);
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        if(distance>=500) {
        updateMarker(new LatLng(location.getLatitude(), location.getLongitude()));
//            mCurrentLocation=null;

        updateCameraBearing(googleMap, location.getBearing());
    }

    protected void stopLocationUpdates() {
        try {
            if (mGoogleApiClient != null)
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
//            Log.d(TAG, "Location update stopped .......................");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void parseResponse(String strResponse) {
//    {"status":false,"message":"Please enter email address.","data":""}
        Log.d("Home Fragment Json", "==> " + strResponse);
        boolean Status;
        String message, update_location = "";
        try {
            JSONObject jresObjectMain = new JSONObject(strResponse);

            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");

            if (Status) {
                JSONObject jresObject = jresObjectMain.getJSONObject("data");
                if (jresObject != null) {
                    latLngList.clear();
                    latLngList.add(new LatLng(latLng.latitude, latLng.longitude));
                    System.out.println(" MY LAT LONG  " + currentLat + "  ,  " + currentLng);
                    String lat, lng, user_id, first_name, last_name, user_image;
                    boolean isFriend;
                    arrayList = new ArrayList<>();

                    double dLat, dLng;

                    user_id = jresObject.getString("user_id");

                    if (user_id.equalsIgnoreCase(CommonClass.getUserpreference(getActivity()).user_id)) {


                        try {
                            dLat = Double.parseDouble(CommonClass.getDataFromJson(jresObject, "latitude"));
                            dLng = Double.parseDouble(CommonClass.getDataFromJson(jresObject, "longitude"));

                            Log.i(TAG, "parseResponse: inside try dLat=" + dLat + " dLng=" + dLng);
                        } catch (NumberFormatException e) {
                            dLat = CommonClass.getDataFromJsonDouble(jresObject, "latitude");
                            dLng = CommonClass.getDataFromJsonDouble(jresObject, "longitude");
                        }

                        if (dLat == 0.0 || dLng == 0.0) {
                            Log.i(TAG, "parseResponse: inside if");
                            dLat = currentLat;//CommonClass.getDataFromJsonDouble(jresObject,"latitude");
                            dLng = currentLng;//CommonClass.getDataFromJsonDouble(jresObject,"longitude");
                        } else
                            Log.i(TAG, "parseResponse: inside else dLat=" + dLat + " dLng=" + dLng);


                    } else {
                        dLat = CommonClass.getDataFromJsonDouble(jresObject, "latitude");
                        dLng = CommonClass.getDataFromJsonDouble(jresObject, "longitude");
                    }
                    LatLng latLng = new LatLng(dLat, dLng);
                    first_name = jresObject.getString("first_name");
                    last_name = jresObject.getString("last_name");
                    user_image = CommonClass.getUserpreference(getActivity()).user_image;
                    if (jresObject.has("update_location"))
                        update_location = jresObject.getString("update_location");
                    else
                        update_location = "";
                    if (jresObject.has("is_friend"))
                        isFriend = jresObject.getBoolean("is_friend");
                    else
                        isFriend = false;
//                    LatLng latLng = new LatLng(lat, lng);
                    if (update_location == null && update_location.length() >= 0)
                        update_location = "00/00/0000 00:00:00 am";
                    Log.e("User location====>", "===>" + update_location);
                    UserDetail userDetail = new UserDetail(user_id, first_name + " " + last_name, latLng, user_image, isFriend, update_location);
                    arrayList.add(userDetail);
                    if (jresObject.has("user_friends_location")) {
                        JSONArray jsonArray = jresObject.getJSONArray("user_friends_location");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject latlngObj = jsonArray.getJSONObject(i);
//                            lat = latlngObj.getString("latitude");
//                            lng = latlngObj.getString("longitude");
                            user_id = latlngObj.getString("friend_id");
                            first_name = latlngObj.getString("first_name");
                            last_name = latlngObj.getString("last_name");
                            update_location = "";
                            if (latlngObj.has("is_friend"))
                                isFriend = latlngObj.getBoolean("is_friend");
                            else
                                isFriend = false;
                            if (latlngObj.has("user_image"))
                                user_image = latlngObj.getString("user_image");
                            else
                                user_image = "";
                            lat = latlngObj.getString("latitude");
                            lng = latlngObj.getString("longitude");
                            if (lat != null && lat.trim().length() > 0) {

                                try {
                                    dLat = latlngObj.getDouble("latitude");
                                } catch (JSONException e) {
                                    dLat = -0.1;
                                }
                            } else
                                dLat = -0.1;
                            if (lng != null && lng.trim().length() > 0)

                                try {
                                    dLng = latlngObj.getDouble("longitude");
                                } catch (JSONException e) {
                                    dLng = -0.1;
                                }
                            else
                                dLng = -0.1;
                            latLng = new LatLng(dLat, dLng);
                            if (latlngObj.has("update_location"))
                                update_location = latlngObj.getString("update_location");

                            if (update_location == null && update_location.length() >= 0)
                                update_location = "00/00/0000 00:00:00 am";

                            userDetail = new UserDetail(user_id, first_name + " " + last_name, latLng, user_image, isFriend, update_location);
                            arrayList.add(userDetail);
//                    latLngList.add(latLng);
                        }
                    }
                    insertMarkers(arrayList);
                } else {
                }
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
//            message = getResources().getString(R.string.s_wrong);
            e.printStackTrace();
        } catch (Exception e) {
            //  message = getResources().getString(R.string.s_wrong);
            e.printStackTrace();
        } catch (Error e2) {
//            Log.e("", "ROME parse error2: " + e2.toString());
        }
    }

    //---------- Home Fragment
    private synchronized void updateHomeFragment() {
        if (!String.valueOf(latLng.latitude).equalsIgnoreCase("0.0") || !String.valueOf(latLng.longitude).equalsIgnoreCase("0.0")) {

            ConnectionDetector cd = new ConnectionDetector(mActivity);
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {
                try {
                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.shareLocationUrl, homeSuccessLisner(),
                            homeErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();
                            requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            requestparam.put("latitude", String.valueOf(latLng.latitude));
                            requestparam.put("longitude", String.valueOf(latLng.longitude));
                            requestparam.put("timezone", "" + TimeZone.getDefault().getID());
//                    String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a").format(Calendar.getInstance().getTime());
//                    requestparam.put("datetime", formattedDate);

//                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
//                    String currentDateandTime = sdf.format(new Date());

                            Log.e("HomeFragment Request", "==> " + requestparam);
                            return requestparam;
                        }
                    };
//            CommonClass.showLoading(LoginActivity.this);
                    mQueue.add(apiRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                CommonClass.ShowToast(mActivity, mActivity.getResources().getString(R.string.check_internet));


            }
        }
    }

    private com.android.volley.Response.Listener<String> homeSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                parseResponse(response);
            }
        };
    }

    private com.android.volley.Response.ErrorListener homeErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
//                CommonClass.closeLoding(LoginActivity.this);
//                CommonClass.ShowToast(LoginActivity.this, error.getMessage());
            }
        };
    }

    //---------- Group Location
    private synchronized void updateGroupLocation() {
//        System.out.println(" CALLING SERVICE");

        if (!String.valueOf(latLng.latitude).equalsIgnoreCase("0.0") || !String.valueOf(latLng.longitude).equalsIgnoreCase("0.0")) {

            ConnectionDetector cd = new ConnectionDetector(mActivity);
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {
                try {
                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.groupLocationUpdate, homeSuccessLisner(),
                            homeErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();
                            requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            requestparam.put("latitude", String.valueOf(latLng.latitude));
                            requestparam.put("longitude", String.valueOf(latLng.longitude));
                            requestparam.put("group_id", groupId);
                            requestparam.put("timezone", "" + TimeZone.getDefault().getID());
                            //                    String formattedDate = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a").format(Calendar.getInstance().getTime());
                            //                    requestparam.put("datetime", formattedDate);
                            //                    System.out.println("URL---------------------------" + Constants.groupLocationUpdate);
                            Log.e("GroupLocation Request", "GroupLocation Request==> " + requestparam);
                            return requestparam;
                        }
                    };
                    //            CommonClass.showLoading(LoginActivity.this);
                    mQueue.add(apiRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v == imgPlayPause) {

            //Toast.makeText(mActivity, "Click", Toast.LENGTH_SHORT).show();
            if (imgPlayPause.getDrawable().getConstantState().equals
                    (getResources().getDrawable(R.drawable.btn_record).getConstantState())) {

                imgPlayPause.setEnabled(false);
                imgPlayPause.setClickable(false);
                db.deleteAllData();
                CommonClass.clearPauseInterval(getActivity());
                CommonClass.clearPauseTime(getActivity());
                CommonClass.clearDuration(getActivity());
                CommonClass.clearPauseDistance(getActivity());
                CommonClass.clearPauseDistanceInterval(getActivity());
                CommonClass.clearStartTime(getActivity());
                CommonClass.clearStopTime(getActivity());
                CommonClass.clearPastDistance(getActivity());
                CommonClass.setlocationServicepreference(getActivity(), "true");
                CommonClass.setlocationServiceCurrentState(getActivity(), R.drawable.btn_pause);
                long timeStart = SystemClock.elapsedRealtime();
                CommonClass.setStartTime(getActivity(), timeStart);
                getActivity().startService(new Intent(getActivity(), LocationService.class));
                imgPlayPause.setImageResource(R.drawable.btn_pause);

            } else if (imgPlayPause.getDrawable().getConstantState().equals
                    (getResources().getDrawable(R.drawable.btn_pause).getConstantState())) {
                CommonClass.setlocationServicepreference(
                        getActivity(), "false");
                //  getActivity().stopService(new Intent(getActivity(), LocationService.class));
                CommonClass.setlocationServiceCurrentState(getActivity(), R.drawable.btn_resume);
                imgPlayPause.setImageResource(R.drawable.btn_resume);
                imgFinish.setVisibility(VISIBLE);

                String duration = db.getDuration();
                String distance = db.getDistance();

                long last_time = 0;
                if (duration.trim().length() > 0) {
                    last_time = Long.parseLong(duration);
                }

                Log.i(TAG, "kp onClick: pause ==>  " + last_time);
                CommonClass.setDuration(getActivity(), last_time);


                long time = SystemClock.elapsedRealtime();
                CommonClass.setStartPauseInterval(getActivity(), time);

                double dis = 0;
                if (distance.trim().length() > 0)
                    dis = Double.parseDouble(distance);

                CommonClass.setStartPauseDistance(getActivity(), dis);
                //CommonClass.clearPauseInterval(getActivity());


            } else if (imgPlayPause.getDrawable().getConstantState().equals
                    (getResources().getDrawable(R.drawable.btn_resume).getConstantState())) {
                long timestop = SystemClock.elapsedRealtime();
                CommonClass.setStopPauseInterval(getActivity(), timestop);
                String distance = db.getDistance();
                double dis = 0;
                if (distance.trim().length() > 0)
                    dis = Double.parseDouble(distance);

                CommonClass.setStopPauseDistance(getActivity(), dis);
                CommonClass.setlocationServicepreference(getActivity(), "true");
                // getActivity().startService(new Intent(getActivity(), LocationService.class));
                imgPlayPause.setImageResource(R.drawable.btn_pause);
                CommonClass.setlocationServiceCurrentState(getActivity(), R.drawable.btn_pause);
                imgFinish.setVisibility(View.GONE);
                long timeNow = CommonClass.getStopPauseInterval(getActivity()) - CommonClass.getStartPauseInterval(getActivity());
                CommonClass.setPauseTime(getActivity(), timeNow);
                double disPause = CommonClass.getStopPauseDistance(getActivity()) - CommonClass.getStartPauseDistance(getActivity());
                CommonClass.setPauseDistance(getActivity(), (float) disPause);


            }

        } else if (v == imgFinish) {


            if (imgPlayPause.getDrawable().getConstantState().equals
                    (getResources().getDrawable(R.drawable.btn_resume).getConstantState())) {


                long timestop = SystemClock.elapsedRealtime();


                CommonClass.setStopPauseInterval(getActivity(), timestop);

                long timeNow = CommonClass.getStopPauseInterval(getActivity()) - CommonClass.getStartPauseInterval(getActivity());
                CommonClass.setPauseTime(getActivity(), timeNow);


                String distance = db.getDistance();
                double dis = 0;
                if (distance.trim().length() > 0)
                    dis = Double.parseDouble(distance);

                CommonClass.setStopPauseDistance(getActivity(), dis);

                double disPause = CommonClass.getStopPauseDistance(getActivity()) - CommonClass.getStartPauseDistance(getActivity());


                CommonClass.setPauseDistance(getActivity(), (float) disPause);

                getActivity().stopService(new Intent(getActivity(), LocationService.class));
            }


            CommonClass.setlocationServicepreference(
                    getActivity(), "false");

            long timeStop = SystemClock.elapsedRealtime();
            CommonClass.setStopTime(getActivity(), timeStop);


            getActivity().stopService(new Intent(getActivity(), LocationService.class));
            imgPlayPause.setImageResource(R.drawable.btn_record);
            CommonClass.setlocationServiceCurrentState(getActivity(), R.drawable.btn_record);
            imgFinish.setVisibility(View.GONE);

            String avg_speed, distance, duration, top_speed;

            avg_speed = db.getAverageSpeed();
            distance = db.getDistance();
            duration = db.getDuration();
            top_speed = db.getTopSpeed();


            double fDistance = 0.0f;

            if (distance.trim().length() > 0) {
                fDistance = Float.parseFloat(distance);
            }

            float favg_speed = 0.0f;

            if (avg_speed.trim().length() > 0) {
                favg_speed = Float.parseFloat(avg_speed);
            }


            float ftop_speed = 0.0f;

            if (top_speed.trim().length() > 0) {
                ftop_speed = Float.parseFloat(top_speed);
            }


            long time = 0;
            if (duration.trim().length() > 0) {
                time = Long.parseLong(duration);

            }


            time = time - CommonClass.getPauseTime(getActivity());


            time = CommonClass.getStopTime(getActivity()) - CommonClass.getStartTime(getActivity()) - CommonClass.getPauseTime(getActivity());

            Log.i(TAG, "lll Duration onClick: finalTime==>  " + getDurationBreakdown(time));


            duration = "" + getDurationBreakdown(time);
            float average = (float) ((fDistance * 1000 / (time / 1000)) * 3.6);


            fDistance = fDistance - CommonClass.getPauseDistance(getActivity());

            fDistance = fDistance + CommonClass.getPastDistance(getActivity());


            favg_speed = (float) ((float) ((fDistance * 1000 / (time / 1000))) * 3.6);


            avg_speed = String.format("%.2f", favg_speed);
            top_speed = String.format("%.2f", ftop_speed);


            distance = String.format("%.2f", fDistance);


            CommonClass.clearPauseInterval(getActivity());
            CommonClass.clearPauseDistanceInterval(getActivity());
            showPopupMenu(String.format("%.2f", favg_speed), distance, duration, top_speed);

        }
    }


    public static String getDurationBreakdown(long millis) {


        if (millis < 0) {

            return "1 sec";
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);

        if (days > 0) {
            sb.append(days);
            sb.append(" days ");
        }

        if (hours > 0) {
            sb.append(hours);
            sb.append(" hr ");
        }

        if (minutes > 0) {
            sb.append(minutes);
            sb.append(" min ");

        }
        if (seconds > 0) {
            sb.append(seconds);
            sb.append(" sec");
        }

        if (sb.toString().trim().length() == 0)
            return "1 sec";

        return (sb.toString());
    }

    android.app.Dialog dialogShare;
    boolean isShareClick = false;
    String ride_type = "", ride_difficulty = "";

    private void showPopupMenu(final String avg_speed, final String distance, final String duration, final String top_speed) {
        // inflate menu


        isShareClick = false;

        dialogShare = new android.app.Dialog(getActivity(), R.style.AppCompatAlertDialogStyleTrans);
        dialogShare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setCancelable(false);
        dialogShare.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogShare.setContentView(R.layout.dialog_add_ride);
        dialogShare.setCancelable(false);
        if (!dialogShare.isShowing())
            dialogShare.show();
        ride_type = "";
        ride_difficulty = "";

        final materialEditText edtRideName = (materialEditText) dialogShare.findViewById(R.id.edtRideName);
        final CheckBox chkDirt = (CheckBox) dialogShare.findViewById(R.id.chkDirt);
        final CheckBox chkSports = (CheckBox) dialogShare.findViewById(R.id.chkSports);
        final CheckBox chkCruiser = (CheckBox) dialogShare.findViewById(R.id.chkCruiser);
        final RadioButton chkBeginner = (RadioButton) dialogShare.findViewById(R.id.chkBeginner);
        final RadioButton chkIntermediate = (RadioButton) dialogShare.findViewById(R.id.chkIntermediate);
        final RadioButton chkDCruiser = (RadioButton) dialogShare.findViewById(R.id.chkDCruiser);
        TextView txtDistance = (TextView) dialogShare.findViewById(R.id.txtDistance);
        TextView txtDuration = (TextView) dialogShare.findViewById(R.id.txtDuration);
        TextView txtTopSpeed = (TextView) dialogShare.findViewById(R.id.txtTopSpeed);
        TextView txtAvgSpeed = (TextView) dialogShare.findViewById(R.id.txtAvgSpeed);
        ImageView imgClose = (ImageView) dialogShare.findViewById(R.id.imgClose);
        Button btnSave = (Button) dialogShare.findViewById(R.id.btnSave);
        Button btnShare = (Button) dialogShare.findViewById(R.id.btnShare);
        txtDistance.setText(distance + " Km");
        txtDuration.setText(duration);
        txtTopSpeed.setText(top_speed + " kph");
        txtAvgSpeed.setText(avg_speed + " kph");
//        TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
//        message.setText(rationalMessage);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  dialog.dismiss();

                ride_type = "";
                ride_difficulty = "";
                // Log.i(TAG, "onClick: Data=== " + db.getAllDataJSON());


                if (chkDirt.isChecked()) {
                    ride_type = ride_type + chkDirt.getText().toString() + ",";

                }
                if (chkSports.isChecked()) {
                    ride_type = ride_type + chkSports.getText().toString() + ",";

                }
                if (chkCruiser.isChecked()) {
                    ride_type = ride_type + chkCruiser.getText().toString() + ",";

                }


                if (ride_type.length() > 0) {
                    ride_type = ride_type.substring(0, ride_type.length() - 1);
                }


                if (chkBeginner.isChecked()) {
                    ride_difficulty = ride_difficulty + chkBeginner.getText().toString() + ",";


                }
                if (chkIntermediate.isChecked()) {
                    ride_difficulty = ride_difficulty + chkIntermediate.getText().toString() + ",";


                }
                if (chkDCruiser.isChecked()) {
                    ride_difficulty = ride_difficulty + chkDCruiser.getText().toString() + ",";


                }

                if (ride_difficulty.length() > 0) {
                    ride_difficulty = ride_difficulty.substring(0, ride_difficulty.length() - 1);
                }


                Log.i(TAG, "onClick: >> == >> " + ride_type);
                Log.i(TAG, "onClick: >> == >> " + ride_difficulty);

                boolean isValid = checkValidation(edtRideName.getText().toString(), ride_type, ride_difficulty);


                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {


                    if (isValid) {

                        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.create_rideUrl, rideSuccessLisner(),
                                rideErrorLisner()) {


                            @Override
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                HashMap<String, String> requestparam = new HashMap<>();

                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                                requestparam.put("ride_name", "" + edtRideName.getText().toString());

                                requestparam.put("ride_type", "" + ride_type);
                                requestparam.put("ride_difficulty", "" + ride_difficulty);
                                requestparam.put("distance", "" + distance);
                                requestparam.put("duration", "" + duration);
                                requestparam.put("top_speed", "" + top_speed);
                                requestparam.put("avg_speed", "" + avg_speed);
                                requestparam.put("lat_long", "" + db.getAllDataJSON());

                                return requestparam;
                            }

                        };


                        showProgress();
                        mQueue.add(apiRequest);
                    }
                } else {

                    CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));

                }


            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new AlertDialog.Builder(getActivity())
                        .setMessage("Do you want to discard this ride?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface mDialog, int id) {
                                db.deleteAllData();
                                CommonClass.clearPauseInterval(getActivity());

                                CommonClass.clearPauseTime(getActivity());
                                CommonClass.clearDuration(getActivity());
                                CommonClass.clearPauseDistance(getActivity());
                                CommonClass.clearPauseDistanceInterval(getActivity());

                                CommonClass.clearStartTime(getActivity());
                                CommonClass.clearStopTime(getActivity());
                                CommonClass.clearPastDistance(getActivity());
                                dialogShare.dismiss();

                            }
                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                            }
                        })
                        /*.setNegativeButton("No", null)*/
                        .show();


            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //    dialog.dismiss();

                ride_type = "";
                ride_difficulty = "";

                if (chkDirt.isChecked()) {
                    ride_type = ride_type + chkDirt.getText().toString() + ",";

                }
                if (chkSports.isChecked()) {
                    ride_type = ride_type + chkSports.getText().toString() + ",";

                }
                if (chkCruiser.isChecked()) {
                    ride_type = ride_type + chkCruiser.getText().toString() + ",";

                }


                if (ride_type.length() > 0) {
                    ride_type = ride_type.substring(0, ride_type.length() - 1);
                }


                if (chkBeginner.isChecked()) {
                    ride_difficulty = ride_difficulty + chkBeginner.getText().toString() + ",";


                }
                if (chkIntermediate.isChecked()) {
                    ride_difficulty = ride_difficulty + chkIntermediate.getText().toString() + ",";


                }
                if (chkDCruiser.isChecked()) {
                    ride_difficulty = ride_difficulty + chkDCruiser.getText().toString() + ",";


                }

                if (ride_difficulty.length() > 0) {
                    ride_difficulty = ride_difficulty.substring(0, ride_difficulty.length() - 1);
                }


                Log.i(TAG, "onClick: >>>>>>>>>>>> " + ride_type);
                Log.i(TAG, "onClick: >>>>>>>>>>>> " + ride_difficulty);

                boolean isValid = checkValidation(edtRideName.getText().toString(), ride_type, ride_difficulty);


                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {


                    if (isValid) {

                        isShareClick = true;
                        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.create_rideUrl, rideSuccessLisner(),
                                rideErrorLisner()) {
                            @Override
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                HashMap<String, String> requestparam = new HashMap<>();


                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                                requestparam.put("ride_name", "" + edtRideName.getText().toString());
                                requestparam.put("ride_type", "" + ride_type);
                                requestparam.put("ride_difficulty", "" + ride_difficulty);
                                requestparam.put("distance", "" + distance);
                                requestparam.put("duration", "" + duration);
                                requestparam.put("top_speed", "" + top_speed);
                                requestparam.put("avg_speed", "" + avg_speed);
                                requestparam.put("lat_long", "" + db.getAllDataJSON());

                                return requestparam;
                            }

                        };


                        showProgress();
                        mQueue.add(apiRequest);
                    }
                } else {

                    CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));

                }
            }
        });


    }


    private void showDetailPopupMenu(final Ride mRide, final int index) {
        // inflate menu


        dialogShare = new android.app.Dialog(getActivity(), R.style.AppCompatAlertDialogStyleTrans);
        dialogShare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setCancelable(false);
        dialogShare.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
//                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialogShare.setContentView(R.layout.dialog_view_ride);
        dialogShare.setCancelable(false);
        if (!dialogShare.isShowing())
            dialogShare.show();


        TextView edtRideName = (TextView) dialogShare.findViewById(R.id.txttRideName);
        TextView txtRideType = (TextView) dialogShare.findViewById(R.id.txtRideType);
        TextView txtRideDifficulty = (TextView) dialogShare.findViewById(R.id.txtRideDifficulty);
        TextView txtDistance = (TextView) dialogShare.findViewById(R.id.txtDistance);
        TextView txtDuration = (TextView) dialogShare.findViewById(R.id.txtDuration);
        TextView txtTopSpeed = (TextView) dialogShare.findViewById(R.id.txtTopSpeed);
        TextView txtAvgSpeed = (TextView) dialogShare.findViewById(R.id.txtAvgSpeed);
        ImageView imgClose = (ImageView) dialogShare.findViewById(R.id.imgClose);
        Button btnDeleteRide = (Button) dialogShare.findViewById(R.id.btnDeleteRide);
        Button btnShare = (Button) dialogShare.findViewById(R.id.btnShare);
        Button btnRide = (Button) dialogShare.findViewById(R.id.btnRide);


        String r_type = "", r_difficulty = "";


        if (mRide.ride_type.trim().length() > 0)
            r_type = mRide.ride_type.replace(",", "\n");

        if (mRide.ride_difficulty.trim().length() > 0)
            r_difficulty = mRide.ride_difficulty.replace(",", "\n");

        edtRideName.setText(mRide.ride_name);
        txtRideType.setText(r_type);
        txtRideDifficulty.setText(r_difficulty);
        txtDistance.setText(mRide.distance + " Km");
        txtDuration.setText(mRide.duration);


        if (mRide.top_speed.trim().length() > 0)
            txtTopSpeed.setText(mRide.top_speed + " kph");
        else
            txtTopSpeed.setText("-");
        txtAvgSpeed.setText(mRide.avg_speed + " kph");
//        TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
//        message.setText(rationalMessage);


        btnRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (imgPlayPause.getDrawable().getConstantState().equals
                        (getResources().getDrawable(R.drawable.btn_record).getConstantState())) {

                    if (mRide.mDataList.size() > 0) {
                        final LatLng mStart = mRide.mDataList.get(0);
                        final LatLng mEnd = mRide.mDataList.get(mRide.mDataList.size() - 1);
                        Intent mintent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + mStart.latitude + "," + mStart.longitude + "&daddr=" + mEnd.latitude + "," + mEnd.longitude + "\""));
                        startActivity(mintent);
                    }

                } else
                    Toast.makeText(getActivity(), "Please finish current ride to start new ride.", Toast.LENGTH_SHORT).show();


                // Toast.makeText(getActivity(), "Under Development !", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  dialog.dismiss();


                if (mRide.user_id.equalsIgnoreCase(CommonClass.getUserpreference(getActivity()).user_id) && mRide.status.equalsIgnoreCase("0")) {


                    Log.i(TAG, "onClick: inside api call ");

                    CommonClass.closeKeyboard(mActivity);
                    ConnectionDetector cd = new ConnectionDetector(getActivity());
                    boolean isConnected = cd.isConnectingToInternet();
                    if (isConnected) {


                        deletedIndex = index;


                        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.delete_rideUrl, deleterideSuccessLisner(),
                                rideErrorLisner()) {


                            @Override
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                HashMap<String, String> requestparam = new HashMap<>();

                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                                requestparam.put("ride_id", "" + mRide.id);


                                return requestparam;
                            }

                        };


                        showProgress();
                        mQueue.add(apiRequest);

                    } else {

                        CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));

                    }
                } else {

                    Log.i(TAG, "onClick: inside not api call ");
                    if (dialogShare != null)
                        dialogShare.dismiss();

                    String ex_rides = CommonClass.getRideIds(getActivity());


                    String rides[] = ex_rides.split(",");
                    List<String> stringList = new ArrayList<String>(Arrays.asList(rides));
                    int dIndex = stringList.indexOf(mRide.id);

                    Log.i(TAG, "onCreateView: Total Rides dIndex  = " + dIndex);

                    if (dIndex != -1) {

                        stringList.remove(dIndex);

                        String ride_data = "";
                        for (int i = 0; i < stringList.size(); i++) {

                            ride_data = ride_data + stringList.get(i) + ",";

                        }

                        if (ride_data.length() > 0)
                            ride_data = ride_data.substring(0, ride_data.length() - 1);


                        CommonClass.setRideIds(getActivity(), ride_data);

                        Log.i(TAG, "onCreateView: Total Rides 2 = " + ride_data);

                    }


                    ((NavigationDrawerActivity) getActivity()).displayView(2);
                }


            }
        });
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialogShare.dismiss();

            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShare.dismiss();


                CaptureMapScreen(mRide);

//                Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//                // Add data to the intent, the receiving app will decide
//                // what to do with it.
//                share.putExtra(Intent.EXTRA_SUBJECT, "Cync Ride");
//                share.putExtra(Intent.EXTRA_TEXT, mRide.share_ride);
//                startActivity(Intent.createChooser(share, "Share ride!"));


            }
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == SHARE_CODE && resultCode == getActivity().RESULT_OK
                && null != data) {


            String packageName = data.getExtras().getString("package");
            String name = data.getExtras().getString("name");
            String share_url = data.getExtras().getString("share_url");
            String file_path = data.getExtras().getString("file_path");
            Log.i(TAG, "onActivityResult: Package===" + packageName);


            if (packageName.equalsIgnoreCase("com.facebook.katana")) {
                ShareDialog shareDialog = new ShareDialog(getActivity());

                ShareLinkContent linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Cync Ride")
//                            .setImageUrl(Uri.parse("http://opensource.zealousys.com/cync/wp-content/uploads/1497604229.jpg"))
                        .setContentDescription(
                                "Cync Ride...")
                        .setContentUrl(Uri.parse(share_url))
                        .build();
                shareDialog.show(linkContent);  // Show facebook ShareDialog
            } else if (packageName.equalsIgnoreCase("com.app.android.cync") || packageName.equalsIgnoreCase("com.google.android.gm")) {
                //optional //internal storage
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Cync Ride");
                shareIntent.putExtra(Intent.EXTRA_TEXT, share_url);
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(file_path)));  //optional//use this when you want to send an image
                shareIntent.setPackage(packageName);
                shareIntent.setClassName(packageName, name);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share ride!"));
            } else {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Cync Ride");
                shareIntent.putExtra(Intent.EXTRA_TEXT, share_url);
                // shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(file_path)));  //optional//use this when you want to send an image
                shareIntent.setPackage(packageName);
                shareIntent.setClassName(packageName, name);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share ride!"));

            }


        }
    }


    private com.android.volley.Response.Listener<String> rideSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "Ride Create Response==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";
                boolean hasNextpage = false;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        String share_ride_url = "";

                        if (dialogShare != null)
                            dialogShare.dismiss();

                        if (jresObjectMain.has("data")) {


                            db.deleteAllData();
                            CommonClass.clearPauseInterval(getActivity());

                            CommonClass.clearPauseTime(getActivity());
                            CommonClass.clearDuration(getActivity());
                            CommonClass.clearPauseDistance(getActivity());
                            CommonClass.clearPauseDistanceInterval(getActivity());

                            CommonClass.clearStartTime(getActivity());
                            CommonClass.clearStopTime(getActivity());
                            CommonClass.clearPastDistance(getActivity());

                            String id, user_id, ride_name, ride_type, ride_difficulty, distance, duration, top_speed, avg_speed, date_added,
                                    share_ride, status;
                            ArrayList<LatLng> mDataList = new ArrayList<>();


                            JSONObject jData = jresObjectMain.getJSONObject("data");
//                            id = CommonClass.getDataFromJson(jData, "id");
//                            user_id = CommonClass.getDataFromJson(jData, "user_id");
//                            ride_name = CommonClass.getDataFromJson(jData, "ride_name");
//                            ride_type = CommonClass.getDataFromJson(jData, "ride_type");
//                            ride_difficulty = CommonClass.getDataFromJson(jData, "ride_difficulty");
//                            distance = CommonClass.getDataFromJson(jData, "distance");
//                            duration = CommonClass.getDataFromJson(jData, "duration");
//                            top_speed = CommonClass.getDataFromJson(jData, "top_speed");
//                            avg_speed = CommonClass.getDataFromJson(jData, "avg_speed");
//                            date_added = CommonClass.getDataFromJson(jData, "date_added");
//                            share_ride = CommonClass.getDataFromJson(jData, "share_ride");
//
//                            status = CommonClass.getDataFromJson(jData, "status");
//                            if (jData.has("lat_long")) {
//                                JSONArray lat_longArray = jData.getJSONArray("lat_long");
//
//                                for (int i = 0; i < lat_longArray.length(); i++) {
//
//                                    JSONObject jmini = lat_longArray.getJSONObject(i);
//
//                                    double lat, lon;
//
//                                    lat = CommonClass.getDataFromJsonDouble2(jmini, "lat");
//                                    lon = CommonClass.getDataFromJsonDouble2(jmini, "lon");
//
//
//                                    mDataList.add(new LatLng(lat, lon));
//                                }
//                            }
//
//
//                            if (CommonClass.getUserpreference(getActivity()).view_my_rides) {
//                                mRideIdList.clear();
//                                mRideList.clear();
//                                getMyRide();
//                            }
//                            mRideList.add(new Ride(id, user_id, ride_name, ride_type, ride_difficulty, distance, duration, top_speed, avg_speed, date_added,
//                                    share_ride, getRandomColor(), colorList[0], mDataList));

                            share_ride_url = CommonClass.getDataFromJson(jData, "share_ride");


                            if (isShareClick) {

                                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                                share.setType("text/plain");
                                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

                                // Add data to the intent, the receiving app will decide
                                // what to do with it.
                                share.putExtra(Intent.EXTRA_SUBJECT, "Cync Ride");
                                share.putExtra(Intent.EXTRA_TEXT, share_ride_url);

                                startActivity(Intent.createChooser(share, "Share ride!"));

                                ((NavigationDrawerActivity) getActivity()).displayView(2);

                            } else {
                                ((NavigationDrawerActivity) getActivity()).displayView(2);
                            }


                        }


                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


            }


        };
    }


    private com.android.volley.Response.Listener<String> deleterideSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "Ride Create Response==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";

                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        String share_ride_url = "";

                        if (dialogShare != null)
                            dialogShare.dismiss();


                        String ex_rides = CommonClass.getRideIds(getActivity());

                        Log.i(TAG, "onCreateView: Total Rides 1  = " + ex_rides);


                        String rides[] = ex_rides.split(",");
                        List<String> stringList = new ArrayList<String>(Arrays.asList(rides));
                        int dIndex = stringList.indexOf(mRideList.get(deletedIndex).id);
                        if (dIndex != -1) {


                            stringList.remove(dIndex);

                            String ride_data = "";
                            for (int i = 0; i < stringList.size(); i++) {

                                ride_data = ride_data + stringList.get(i) + ",";

                            }

                            if (ride_data.length() > 0)
                                ride_data = ride_data.substring(0, ride_data.length() - 1);


                            CommonClass.setRideIds(getActivity(), ride_data);

                            Log.i(TAG, "onCreateView: Total Rides 2 = " + ride_data);

                        }

                        mRideList.remove(deletedIndex);
                        mRideIdList.remove(deletedIndex);

                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


            }


        };
    }


    private com.android.volley.Response.ErrorListener rideErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getString(R.string.s_wrong));
            }
        };
    }


    private void showProgress() {

        showLoading(getActivity());

    }


    Dialog loadingD;

    public void showLoading(Context ct) {
        try {


            loadingD = new android.app.Dialog(ct, R.style.AppCompatAlertDialogStyleTrans);
            loadingD.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // dialog.setCancelable(false);
            loadingD.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
            loadingD.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            loadingD.setContentView(R.layout.loading_indicator);
            loadingD.setCancelable(false);
            loadingD.show();


            if (!loadingD.isShowing())
                loadingD.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void closeLoding(Context ct) {
        try {
            if (loadingD != null)
                loadingD.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void stopProgress() {
        closeLoding(getActivity());
    }

    private boolean checkValidation(String ride_name, String ride_type, String ride_difficulty) {


        if (TextUtils.isEmpty(ride_name)) {
            Toast.makeText(getActivity(), "Please enter ride name.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ride_type.trim().length() == 0) {
            Toast.makeText(getActivity(), "Please select ride type.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ride_difficulty.trim().length() == 0) {
            Toast.makeText(getActivity(), "Please enter ride difficulty.", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }


//    private com.android.volley.Response.Listener<String> groupLocationSuccessLisner() {
//        return new com.android.volley.Response.Listener<String>() {
//
//            private String responseMessage = "";
//
//            @Override
//            public void onResponse(String response) {
//                parseResponse(response);
//            }
//        };
//    }
//
//    private com.android.volley.Response.ErrorListener groupLocationErrorLisner() {
//        return new com.android.volley.Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
////                CommonClass.closeLoding(LoginActivity.this);
////                CommonClass.ShowToast(LoginActivity.this, error.getMessage());
//            }
//        };
//    }


    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!isGroupLocation) {

                double lati = intent.getExtras().getDouble("latitude");
                double longi = intent.getExtras().getDouble("longitude");
                long time = intent.getExtras().getLong("duration");
                time = time - CommonClass.getPauseTime(getActivity());

                if (time > 20000) {
                    imgPlayPause.setEnabled(true);
                    imgPlayPause.setClickable(true);
                }


                drawPolyLineOnMapAll(db.getLocationList(), -1, "", "#000000", Color.parseColor("#000000"));

                if (mRideList.size() > 0) {
                    for (int i = 0; i < mRideList.size(); i++) {

                        List<LatLng> tmpList = mRideList.get(i).mDataList;
                        drawPolyLineOnMapAll(tmpList, i, "server", mRideList.get(i).color, mRideList.get(i).colorCode);

                    }

                }


                if (markerLoginUser != null) {
                    markerLoginUser.setPosition(new LatLng(lati, longi));
                }


                Log.i(TAG, "onReceive: === latitude : " + lati + " longitude : " + longi + " duration=" + time + " distance = " + db.getDistance());
            }

        }
    };


    private int getColor() {
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(200 * 200 * 200);
        return darker(nextInt, 255);

//        return android.graphics.Color.HSVToColor(new float[] {generateRandomFloat(), 2f, 0});
    }


    public float generateRandomFloat() {
        Random random = new Random();

        float nfloat = random.nextFloat();
        return nfloat;
    }

    public String getRandomColor() {
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(255 * 255 * 255);

        // format it as hexadecimal string (with hashtag and leading zeros)
        String colorCode = String.format("#%06x", nextInt);
        return colorCode;
    }


    public static int darker(int color, float factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(255,
                Math.max((int) (r * factor), 0),
                Math.max((int) (g * factor), 0),
                Math.max((int) (b * factor), 0));
    }


    public void drawPolyLineOnMapAll(List<LatLng> list, int index, String from, String colorCode, int color) {


        if (list.size() > 0) {


            PolylineOptions polyOptions = new PolylineOptions();
            // polyOptions.color(Color.parseColor(colorCode));
            //polyOptions.color(getColor());
            polyOptions.color(color);


            polyOptions.width(8);

            polyOptions.addAll(list);
            Polyline polyline = googleMap.addPolyline(polyOptions);
            //polyline.setClickable(true);


            LatLng first = polyline.getPoints().get(0);
            LatLng last = polyline.getPoints().get(polyline.getPoints().size() - 1);


            final MarkerOptions optionsStart = new MarkerOptions().position(first);
            optionsStart.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_start_point));
            optionsStart.title("Start point");
            optionsStart.snippet("" + index);


            final MarkerOptions optionsEnd = new MarkerOptions().position(last);
            optionsEnd.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_end_point));
            optionsEnd.title("End point");
            optionsEnd.snippet("" + index);


            if (from.trim().length() > 0) {
                Marker mStart = googleMap.addMarker(optionsStart);
                Marker mEnd = googleMap.addMarker(optionsEnd);
            }


            for (LatLng latLng : list) {
                builder.include(latLng);
            }

            // zoomMap();

            if ((CommonClass.getlocationServicepreference(getActivity())
                    .equalsIgnoreCase("true"))) {

                for (LatLng latLng : list) {
                    builder.include(latLng);
                }


            }

            if (onlyOnce)
                zoomMap();


        }
    }


    int z = 0;

    public void zoomMap() {

        LatLngBounds bounds = builder.build();
//Change the padding as per needed


        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        googleMap.moveCamera(cu);
        googleMap.animateCamera(cu);

//        z++;
//
//        if(z==2)
//            onlyOnce=false;


    }


    public void getRideDetail() {

        CommonClass.closeKeyboard(mActivity);
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {


            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_rideUrl, rideDetailsSuccessLisner(),
                    mErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();


                    requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);

                    requestparam.put("ride_id", "" + CommonClass.getRideIds(getActivity()));


                    return requestparam;
                }

            };


            showProgress();
            mQueue.add(apiRequest);

        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));

        }
    }


    private com.android.volley.Response.Listener<String> rideDetailsSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "Ride Create Response==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";

                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        if (dialogShare != null)
                            dialogShare.dismiss();

                        if (jresObjectMain.has("data")) {
                            JSONArray dataArray = jresObjectMain.getJSONArray("data");


                            for (int x = 0; x < dataArray.length(); x++) {

                                JSONObject jData = dataArray.getJSONObject(x);

                                String id, user_id, ride_name, ride_type, ride_difficulty, distance, duration, top_speed, avg_speed, date_added,
                                        share_ride, status;
                                ArrayList<LatLng> mDataList = new ArrayList<>();

                                id = CommonClass.getDataFromJson(jData, "id");
                                user_id = CommonClass.getDataFromJson(jData, "user_id");
                                ride_name = CommonClass.getDataFromJson(jData, "ride_name");
                                ride_type = CommonClass.getDataFromJson(jData, "ride_type");
                                ride_difficulty = CommonClass.getDataFromJson(jData, "ride_difficulty");
                                distance = CommonClass.getDataFromJson(jData, "distance");
                                duration = CommonClass.getDataFromJson(jData, "duration");
                                top_speed = CommonClass.getDataFromJson(jData, "top_speed");
                                avg_speed = CommonClass.getDataFromJson(jData, "avg_speed");
                                date_added = CommonClass.getDataFromJson(jData, "date_added");
                                share_ride = CommonClass.getDataFromJson(jData, "share_ride");
                                status = CommonClass.getDataFromJson(jData, "status");

                                if (jData.has("lat_long")) {
                                    JSONArray lat_longArray = jData.getJSONArray("lat_long");

                                    for (int i = 0; i < lat_longArray.length(); i++) {

                                        JSONObject jmini = lat_longArray.getJSONObject(i);

                                        double lat, lon;

                                        lat = CommonClass.getDataFromJsonDouble2(jmini, "lat");
                                        lon = CommonClass.getDataFromJsonDouble2(jmini, "lon");
                                        mDataList.add(new LatLng(lat, lon));
                                    }
                                }
                                int colorIndex = 0;//new Random().nextInt(10 - 0 + 1) + 0;

                                Ride mRideN = new Ride(id, user_id, ride_name, ride_type, ride_difficulty, distance, duration, top_speed, avg_speed, date_added,
                                        share_ride, getRandomColor(), colorList[colorIndex], mDataList);

                                mRideN.setStatus(status);
                                mRideIdList.add(id);
                                mRideList.add(mRideN);
                            }

                        }


                    } else {


                        // CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


                if (CommonClass.getUserpreference(getActivity()).view_my_rides)
                    getMyRide();
                else
                    updateMarker(new LatLng(currentLat, currentLng));


                //updateMarker(new LatLng(currentLat, currentLng));


            }


        };
    }


    public void getMyRide() {

        if (mActivity != null) {
            CommonClass.closeKeyboard(mActivity);
            ConnectionDetector cd = new ConnectionDetector(mActivity);
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {


                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_myrideUrl, myridesSuccessLisner(),
                        mErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();


                        requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                        // requestparam.put("user_id", "960");

                        // requestparam.put("ride_id", "" + ride_id);


                        return requestparam;
                    }

                };


                showProgress();
                mQueue.add(apiRequest);

            } else {

                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));

            }
        }
    }


    private com.android.volley.Response.Listener<String> myridesSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "My Ride Response==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";

                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        if (jresObjectMain.has("data")) {


                            JSONArray dataArray = jresObjectMain.getJSONArray("data");


                            for (int x = 0; x < dataArray.length(); x++) {


                                String id, user_id, ride_name, ride_type, ride_difficulty, distance, duration, top_speed, avg_speed, date_added,
                                        share_ride, status;
                                ArrayList<LatLng> mDataList = new ArrayList<>();

                                JSONObject jData = dataArray.getJSONObject(x);

                                id = CommonClass.getDataFromJson(jData, "id");
                                user_id = CommonClass.getDataFromJson(jData, "user_id");
                                ride_name = CommonClass.getDataFromJson(jData, "ride_name");
                                ride_type = CommonClass.getDataFromJson(jData, "ride_type");
                                ride_difficulty = CommonClass.getDataFromJson(jData, "ride_difficulty");
                                distance = CommonClass.getDataFromJson(jData, "distance");
                                duration = CommonClass.getDataFromJson(jData, "duration");
                                top_speed = CommonClass.getDataFromJson(jData, "top_speed");
                                avg_speed = CommonClass.getDataFromJson(jData, "avg_speed");
                                date_added = CommonClass.getDataFromJson(jData, "date_added");
                                share_ride = CommonClass.getDataFromJson(jData, "share_ride");
                                status = CommonClass.getDataFromJson(jData, "status");

                                if (jData.has("lat_long")) {
                                    JSONArray lat_longArray = jData.getJSONArray("lat_long");

                                    for (int i = 0; i < lat_longArray.length(); i++) {

                                        JSONObject jmini = lat_longArray.getJSONObject(i);

                                        double lat, lon;

                                        lat = CommonClass.getDataFromJsonDouble2(jmini, "lat");
                                        lon = CommonClass.getDataFromJsonDouble2(jmini, "lon");


                                        mDataList.add(new LatLng(lat, lon));
                                    }
                                }

                                int colorIndex = x % 10;//new Random().nextInt(10 - 0 + 1) + 0;

                                Ride mRideN = new Ride(id, user_id, ride_name, ride_type, ride_difficulty, distance, duration, top_speed, avg_speed, date_added,
                                        share_ride, getRandomColor(), colorList[colorIndex], mDataList);

                                mRideN.setStatus(status);

                                mRideList.add(mRideN);
                                mRideIdList.add(id);
                            }


                        }


                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                    Log.i(TAG, "onResponse: ===>>>> " + mRideList.size());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }

                updateMarker(new LatLng(currentLat, currentLng));
            }


        };
    }


    private com.android.volley.Response.ErrorListener mErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getString(R.string.s_wrong));
                updateMarker(new LatLng(currentLat, currentLng));
            }
        };
    }


    private void updateCameraBearing(GoogleMap googleMap, float bearing) {
//        if (googleMap == null) return;
//        CameraPosition camPos = CameraPosition
//                .builder(
//                        googleMap.getCameraPosition() // current Camera
//                )
//                .bearing(bearing)
//                .build();
//        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPos));
    }


    public void CaptureMapScreen(final Ride mRide) {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;

                File file = new File(Environment.getExternalStorageDirectory()
                        + "/Cync/Camera/ShareRide");
                file.mkdirs();

                String imageName = "Cync_" + Calendar.getInstance().getTimeInMillis() + ".jpg";

                File image = new File(file.getAbsolutePath(), imageName);


                try {


                    FileOutputStream out = new FileOutputStream(image);

                    // above "/mnt ..... png" => is a storage path (where image will be stored) + name of image you can customize as per your Requirement

                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);


//
//                                    Intent share = new Intent(android.content.Intent.ACTION_SEND);
//                share.setType("text/plain");
//                share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
//
//                // Add data to the intent, the receiving app will decide
//                // what to do with it.
//                share.putExtra(Intent.EXTRA_SUBJECT, "Cync Ride");
//                share.putExtra(Intent.EXTRA_TEXT, mRide.share_ride);
//
//                startActivity(Intent.createChooser(share, "Share ride!"));


//                    int sdk = android.os.Build.VERSION.SDK_INT;
//                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
//                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                        clipboard.setText("" + mRide.share_ride);
//                    } else {
//                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
//                        android.content.ClipData clip = android.content.ClipData.newPlainText("text label", "" + mRide.share_ride);
//                        clipboard.setPrimaryClip(clip);
//                    }
//
//                    Toast.makeText(getActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show();


//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.putExtra(Intent.EXTRA_TITLE, "my awesome caption in the EXTRA_TITLE field");
//                    shareIntent.putExtra(Intent.EXTRA_TEXT,  mRide.share_ride);
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(image.getAbsolutePath())));  //optional//use this when you want to send an image
//                    shareIntent.setType("image/*");
//                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(Intent.createChooser(shareIntent, "Share ride!"));


                    Intent i = new Intent(getActivity(), ShareActivity.class);
                    i.putExtra("share_url", mRide.share_ride);
                    i.putExtra("file_path", image.getAbsolutePath());
                    startActivityForResult(i, SHARE_CODE);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        googleMap.snapshot(callback);

        // myMap is object of GoogleMap +> GoogleMap myMap;
        // which is initialized in onCreate() =>
        // myMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
    }


}
