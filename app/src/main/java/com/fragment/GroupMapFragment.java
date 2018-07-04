package com.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
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
import com.utils.CommonClass;
import com.utils.Constants;
import com.utils.GPSTracker;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class GroupMapFragment extends BaseContainerFragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    LatLngBounds.Builder mybuilder = new LatLngBounds.Builder();
    //------- Google Map
    MapView mapView;
    GoogleMap googleMap;
    GPSTracker gps;
    private Marker marker;
    ImageView imgMyLocation;
    double currentLat = 0.0, currentLng = 0.0;
    //-------
    private static final long INTERVAL = 1000 * 20;
    private static final long FASTEST_INTERVAL = 1000 * 20;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    private static final String TAG = "LocationActivity";
    Activity mActivity;
    LocationManager mLocationManager;
    private RequestQueue mQueue;
    List<LatLng> latLngList = new ArrayList<LatLng>();

    public GroupMapFragment() {
        // Required empty public constructor
    }

    //-------
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    //--------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        if (!isGooglePlayServicesAvailable()) {
            mActivity.finish();
        }
        createLocationRequest();
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

            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(rootView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // googleMap = mapView.getMap();
//        googleMap.setMyLocationEnabled(true);
//        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        //googleMap.getUiSettings().setAllGesturesEnabled(true);
        checkGPSon();
        //addMarker();
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        addMarker();
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
        gps = new GPSTracker(getActivity());

        String lat = CommonClass.getPrefranceByKey_Value(getActivity(), CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lat);
        String lng = CommonClass.getPrefranceByKey_Value(getActivity(), CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lng);
        if (lat != null && lat.trim().length() > 0)
            currentLat = Double.parseDouble(lat);
        if (lng != null && lng.trim().length() > 0)
            currentLng = Double.parseDouble(lng);
        mapView = (MapView) view.findViewById(R.id.mapview);
        currentLat = gps.getLatitude();
        currentLng = gps.getLongitude();

        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                if (googleMap != null) {

                    builder.include(new LatLng(currentLat, currentLng));
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
                    CameraUpdate yourLocation = CameraUpdateFactory.newLatLngBounds(bounds, width / 3, height / 3, 35);


                    CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                            .target(new LatLng(currentLat, currentLng));


                    cameraBuilder.zoom(googleMap.getCameraPosition().zoom);

                    CameraPosition cameraPosition = cameraBuilder.build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
                        public void onCancel() {
                        }

                        public void onFinish() {


                            CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                                    .target(new LatLng(currentLat, currentLng));


                            cameraBuilder.zoom(googleMap.getCameraPosition().zoom);

                            CameraPosition cameraPosition = cameraBuilder.build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                        }
                    });


                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
        mapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
//        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    private void insertMarkers(List<LatLng> list) {

        googleMap.clear();


        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < list.size(); i++) {

            final MarkerOptions options = new MarkerOptions().position(list.get(i));

            googleMap.addMarker(options);

            builder.include(list.get(i));
        }

        LatLngBounds bounds = builder.build();

        mybuilder = builder;

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

    }

    private void addMarker() {
        // map_title.setText(strText);
        MarkerOptions markerOptions = new MarkerOptions().position(
                new LatLng(currentLat, currentLng)).icon(BitmapDescriptorFactory
                .fromResource(R.drawable.map_pin));
        markerOptions.title(CommonClass.getUserpreference(getActivity()).first_name + " " + CommonClass.getUserpreference(getActivity()).last_name);
        marker = googleMap.addMarker(markerOptions);

        CommonClass.zoomCamera(new LatLng(currentLat, currentLng),
                googleMap);
        System.out.println("add marker called");


    }

    private void updateMarker(LatLng position) {
//        System.out.println("update marker called");
        latLng = position;
        callService();
        /*if (marker != null)
        {
            marker.setPosition(position);
            CommonClass.zoomCamera(position, googleMap);
        }*/
    }

    LatLng latLng;

    private synchronized void callService() {
        System.out.println(" CALLING SERVICE");
        LatLng templatLng = latLng;
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.shareLocationUrl, loginSuccessLisner(),
                loginErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                requestparam.put("latitude", String.valueOf(latLng.latitude));
                requestparam.put("longitude", String.valueOf(latLng.longitude));

//                            System.out.println("Login---------------------------" + requestparam);
//                Log.d("Login Request", "==> " + requestparam);
                return requestparam;
            }
        };
//            CommonClass.showLoading(LoginActivity.this);
        mQueue.add(apiRequest);
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
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
//        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
//                mGoogleApiClient);
//        LocationServices.FusedLocationApi.requestLocationUpdates()

//        Log.d(TAG, "Location update started ..............: ");
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
//        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;

        currentLat = mCurrentLocation.getLatitude();
        currentLng = mCurrentLocation.getLongitude();

//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
//        Log.d(TAG, "Location update stopped .......................");
    }

    private com.android.volley.Response.Listener<String> loginSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub

//                {"status":false,"message":"Please enter email address.","data":""}

                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        String user_id, user_image = "", first_name, last_name, login_type, email;
                        JSONObject jresObject = jresObjectMain.getJSONObject("data");
                        JSONArray jsonArray = jresObject.getJSONArray("user_friends_location");
                        latLngList.clear();
                        latLngList.add(new LatLng(latLng.latitude, latLng.longitude));
                        System.out.println(" MY LAT LONG  " + currentLat + "  ,  " + currentLng);
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject latlngObj = jsonArray.getJSONObject(i);
                            double lat = latlngObj.getDouble("latitude");
                            double lng = latlngObj.getDouble("longitude");
                            LatLng latLng = new LatLng(lat, lng);

                            latLngList.add(latLng);

                        }

                        insertMarkers(latLngList);
//
//
                    } else {
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener loginErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
//                CommonClass.closeLoding(LoginActivity.this);
//                CommonClass.ShowToast(LoginActivity.this, error.getMessage());
            }
        };
    }


}