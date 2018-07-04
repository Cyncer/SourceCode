package com.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.customwidget.CircleImageView;
import com.cync.model.Assist;
import com.cync.model.UserDetail;
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
import com.rey.material.widget.Spinner;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.utils.GPSTracker;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

;
;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class AssistFragment extends BaseContainerFragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    //-------
    private static final long INTERVAL = 1000 * 20;
    private static final long FASTEST_INTERVAL = 1000 * 20;
    private static final String TAG = "LocationActivity";
    String tmpCategory = "", tmpMake = "", tmpYear = "", tmpModel = "";
    boolean isFirstCategory = true, isFirstMake = true, isFirstYear = true, isFirstModel = true;
    ArrayAdapter mAdapterCategory, mAdapterMake, mAdapterModel, mAdapterYear;

    boolean mySettings = false;
    Spinner spinnerCategory, spinnerMake, spinnerYear, spinnerModel;
    ArrayList<String> listYear = new ArrayList<>();
    ArrayList<String> listModel = new ArrayList<>();
    ArrayList<String> listMake = new ArrayList<>();
    ArrayList<String> listMakeId = new ArrayList<>();

    ArrayList<String> listCategory = new ArrayList<>();
    LatLngBounds.Builder mybuilder;
    //------- Google Map
    MapView mapView;
    GoogleMap googleMap;
    GPSTracker gps;
    ImageView imgMyLocation;
    ImageView imgFilter, imgMyFilter;
    double currentLat = 0.0, currentLng = 0.0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    Activity mActivity;
    LocationManager mLocationManager;
    ArrayList<Assist> arrayList = new ArrayList<>();
    boolean showCurrLoc = true;
    LatLng latLng;
    private HashMap<Marker, Integer> mHashMap = new HashMap<Marker, Integer>();
    private RequestQueue mQueue;


    public AssistFragment() {
        // Required empty public constructor
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
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!isGooglePlayServicesAvailable()) {
            mActivity.finish();
        }
        createLocationRequest();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_assist, container, false);

        mQueue = VolleySetup.getRequestQueue();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();
        Init(rootView);
        try {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
            // googleMap = mapView.getMap();
//            googleMap.setMyLocationEnabled(true);
//            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            //googleMap.getUiSettings().setAllGesturesEnabled(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        checkGPSon();

        // updateMarker(new LatLng(currentLat, currentLng));
        // Inflate the layout for this fragment

        UserDetail udm = CommonClass.getUserpreference(getActivity());
        if (udm.make.trim().length() != 0) {
            tmpMake = udm.make;
            tmpYear = udm.year;
            tmpModel = udm.model;
        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        this.googleMap = gMap;
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        updateMarker(new LatLng(currentLat, currentLng));

        //time to call webservice to get data
        getData();
    }

    private void getData() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            try {


                String url = Constants.assistUrl;
                url = url + "?mechanics_category=" + tmpCategory;

                // "make_name="+tmpMake+"&make_year="+tmpYear+"&make_model="+tmpModel
                if (tmpMake.trim().length() > 0) {
                    url = url + "&make_name=" + tmpMake;
                    if (tmpYear.trim().length() > 0) {
                        url = url + "&make_year=" + tmpYear;

                        if (tmpYear.trim().length() > 0) {
                            url = url + "&make_model=" + tmpModel;
                        }
                    }
                }

                Log.i("AssistFragment", "URL===>" + url);

                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, url, mSuccessLisner(),
                        mErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();

                        return requestparam;
                    }
                };
                CommonClass.showLoading(getActivity());
                mQueue.add(apiRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private com.android.volley.Response.Listener<String> mSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.d("Home Fragment Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message, update_location = "";
                try {

                    if (googleMap != null)
                        googleMap.clear();


                    JSONObject jresObjectMain = new JSONObject(response.trim());

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        arrayList.clear();
                        arrayList.add(new Assist("-1", "" + CommonClass.getUserpreference(getActivity()).first_name + " " + CommonClass.getUserpreference(getActivity()).last_name, "", "", "" + currentLat, "" + currentLng, "", "", ""));

                        if (jresObjectMain.has("result")) {
                            JSONArray resultArray = jresObjectMain.getJSONArray("result");

                            for (int index = 0; index < resultArray.length(); index++) {

                                JSONObject jmini = resultArray.getJSONObject(index);
                                String id, name, address, contact_number, lat, lng, make_name, year, model;

                                id = CommonClass.getDataFromJson(jmini, "id");
                                name = CommonClass.getDataFromJson(jmini, "name");
                                address = CommonClass.getDataFromJson(jmini, "address");
                                contact_number = CommonClass.getDataFromJson(jmini, "contact_number");
                                lat = CommonClass.getDataFromJson(jmini, "lat");
                                lng = CommonClass.getDataFromJson(jmini, "long");
                                make_name = CommonClass.getDataFromJson(jmini, "make_name");
                                year = CommonClass.getDataFromJson(jmini, "year");
                                model = CommonClass.getDataFromJson(jmini, "model");


                                arrayList.add(new Assist(id, name, address, contact_number, lat, lng, make_name, year, model));
                            }
                        }

                        Log.d("Home Fragment Json", "==> " + arrayList.size());
                        insertMarkers(arrayList);

                    } else {
                        CommonClass.ShowToast(getActivity(), message);
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


        };
    }

    private com.android.volley.Response.ErrorListener mErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
//                CommonClass.ShowToast(LoginActivity.this, error.getMessage());
            }
        };
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

        mapView = (MapView) view.findViewById(R.id.mapview);
        currentLat = gps.getLatitude();
        currentLng = gps.getLongitude();


        Log.i("AssistFragment", "URL >==" + currentLat + " " + currentLng);


        imgMyLocation = (ImageView) view.findViewById(R.id.imgMyLocation);
        imgFilter = (ImageView) view.findViewById(R.id.imgFilter);
        imgMyFilter = (ImageView) view.findViewById(R.id.imgMyFilter);

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialog();
                //
            }
        });


        imgMyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                openDialog();

                if (mAdapterMake != null) {
                    mAdapterMake.notifyDataSetChanged();
                    spinnerMake.setSelection(0);

                }
                if (mAdapterCategory != null) {
                    mAdapterCategory.notifyDataSetChanged();
                    spinnerCategory.setSelection(0);

                }
                if (mAdapterModel != null) {
                    mAdapterModel.notifyDataSetChanged();
                    spinnerModel.setSelection(0);

                }

                if (mAdapterYear != null) {
                    mAdapterYear.notifyDataSetChanged();
                    spinnerYear.setSelection(0);

                }

                isFirstCategory = true;
                isFirstMake = true;
                isFirstYear = true;
                isFirstModel = true;
                UserDetail udm = CommonClass.getUserpreference(getActivity());

                if (udm.make.trim().length() != 0) {

                    int index = listMake.indexOf(udm.make);
                    if (index != -1)
                        spinnerMake.setSelection(index);
                }


//                fgfg
//                getData();
            }
        });

        imgMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (arrayList.get(0).id.equalsIgnoreCase("-1")) {

                    final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if (googleMap != null && arrayList.size() > 0) {


                        if (arrayList.get(0).lat.trim().length() > 0 && arrayList.get(0).lng.trim().length() > 0) {


                            try {


                                builder.include(new LatLng(Double.parseDouble(arrayList.get(0).lat), Double.parseDouble(arrayList.get(0).lng)));


                            } catch (NumberFormatException e) {

                            }
                        }

//
//
// CameraPosition cameraPosition = new CameraPosition.Builder()
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

                        if (arrayList.get(0).lat.trim().length() > 0 && arrayList.get(0).lng.trim().length() > 0) {


                            CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                                    .target(new LatLng(Double.parseDouble(arrayList.get(0).lat), Double.parseDouble(arrayList.get(0).lng)));


                            cameraBuilder.zoom(googleMap.getCameraPosition().zoom);

                            CameraPosition cameraPosition = cameraBuilder.build();
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
                                public void onCancel() {
                                }

                                public void onFinish() {


                                    CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                                            .target(new LatLng(Double.parseDouble(arrayList.get(0).lat), Double.parseDouble(arrayList.get(0).lng)));


                                    cameraBuilder.zoom(googleMap.getCameraPosition().zoom);

                                    CameraPosition cameraPosition = cameraBuilder.build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                }
                            });
                        }


                    }
                }
            }
        });


    }

    private void openDialog() {

//        isFirstMake = true;
//        isFirstYear = true;
//        isFirstModel = true;

        mAdapterCategory = null;
        mAdapterMake = null;
        mAdapterYear = null;
        mAdapterModel = null;

        final Dialog dialog;

        dialog = new Dialog(getActivity(), R.style.AppCompatAlertDialogStyleNew);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog.setContentView(R.layout.popup_content);
        dialog.setCancelable(true);
        dialog.show();

        spinnerCategory = (Spinner) dialog.findViewById(R.id.spinnerCategory);
        spinnerMake = (Spinner) dialog.findViewById(R.id.spinnerMake);
        spinnerYear = (Spinner) dialog.findViewById(R.id.spinnerYear);
        spinnerModel = (Spinner) dialog.findViewById(R.id.spinnerModel);
        Button buttonCancel = (Button) dialog.findViewById(R.id.buttonCancel);
        Button buttonOk = (Button) dialog.findViewById(R.id.buttonOk);


//        if(listMake.size()>0) {
//
//
//                mAdapterMake = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listMake);
//                spinnerMake.setAdapter(mAdapterMake);
//                mAdapterMake.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
//                android.widget.TextView tv = (android.widget.TextView) spinnerMake.getSelectedView();
//                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
//
//
//
//                if (tmpMake.trim().length() != 0) {
//
//                    int index = listMake.indexOf(tmpMake);
//                    if (index != -1) {
//                        spinnerMake.setSelection(index);
//                        if(listYear.size()>0)
//                        {
//
//
//
//                            if (tmpYear.trim().length() != 0) {
//
//
//
//                                int indexY = listYear.indexOf(tmpYear);
//                                if (indexY != -1) {
//                                    {
//                                        mAdapterYear = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listYear);
//                                        spinnerYear.setAdapter(mAdapterYear);
//                                        mAdapterYear.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
//                                        android.widget.TextView tvY = (android.widget.TextView) spinnerYear.getSelectedView();
//                                        tvY.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
//
//                                        spinnerYear.setSelection(indexY);
//                                    }
//
//                                    if (listModel.size() > 0) {
//
//
//
//
//                                        if (tmpYear.trim().length() != 0) {
//
//
//
//
//                                            int indexM = listModel.indexOf(tmpModel);
//                                            if (indexM != -1) {
//                                                mAdapterModel = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listModel);
//                                                spinnerModel.setAdapter(mAdapterModel);
//                                                mAdapterModel.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
//                                                android.widget.TextView tM = (android.widget.TextView) spinnerModel.getSelectedView();
//                                                tM.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
//
//                                                spinnerYear.setSelection(indexM);
//                                            }
//                                        }
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                }
//
//
//
//
//        }else
        getMake();


        spinnerMake.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {


                listYear.clear();
                listYear.add("Select Year");
                if (mAdapterYear != null) {
                    mAdapterYear.notifyDataSetChanged();


                }

                listModel.clear();
                listModel.add("Select Model");
                if (mAdapterModel != null) {
                    mAdapterModel.notifyDataSetChanged();


                }


                if (position > 0) {


                    getYear();

                }

            }
        });


        spinnerCategory.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {


            }
        });


        spinnerYear.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {

//                listModel.clear();
//                listModel.add("Select Model");
                if (mAdapterModel != null) {
                    mAdapterModel.notifyDataSetChanged();
                    spinnerModel.setSelection(0);

                }

                if (position > 0) {


                    getModel();

                }


            }
        });


        spinnerModel.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {


            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                UserDetail udm = CommonClass.getUserpreference(getActivity());

                tmpMake = udm.make;
                tmpYear = udm.year;
                tmpModel = udm.model;
                dialog.dismiss();
                getData();
            }


        });

        buttonOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                tmpCategory = spinnerCategory.getSelectedItem().toString();


                if (spinnerMake.getSelectedItemPosition() == 0) {
                    tmpMake = "";
                    tmpModel = "";
                    tmpYear = "";

                } else if (spinnerYear.getSelectedItemPosition() == 0) {
                    tmpMake = spinnerMake.getSelectedItem().toString();
                    tmpModel = "";
                    tmpYear = "";

                } else if (spinnerModel.getSelectedItemPosition() == 0) {
                    tmpMake = spinnerMake.getSelectedItem().toString();

                    tmpYear = spinnerYear.getSelectedItem().toString();
                    tmpModel = "";


                } else {
                    tmpMake = spinnerMake.getSelectedItem().toString();
                    tmpModel = spinnerModel.getSelectedItem().toString();
                    tmpYear = spinnerYear.getSelectedItem().toString();
                }


                dialog.dismiss();
                getData();

            }


        });
    }


    private void getModel() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            Log.i("EditProfileFramgment", "URL >==" + Constants.getModel + listMakeId.get(spinnerMake.getSelectedItemPosition()));
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.getModel + listMakeId.get(spinnerMake.getSelectedItemPosition()) + "&year=" + listYear.get(spinnerYear.getSelectedItemPosition()), modelSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        }
    }


    private void getYear() {

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            Log.i("EditProfileFramgment", "URL >==" + Constants.getYear + listMakeId.get(spinnerMake.getSelectedItemPosition()));
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.getYear + listMakeId.get(spinnerMake.getSelectedItemPosition()), yearSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        }
    }

    private void getMake() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.getMake, makeSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        }
    }

    private com.android.volley.Response.Listener<String> makeSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if (jresObjectMain.has("mechanic_category")) {
                            listCategory.clear();
                            JSONArray catArray = jresObjectMain.getJSONArray("mechanic_category");

                            for (int i = 0; i < catArray.length(); i++) {

                                if (i == 0 && tmpCategory.trim().length() == 0) {
                                    tmpCategory = "" + catArray.getString(i);
                                }
                                listCategory.add("" + catArray.getString(i));


                            }


                        }


                        if (jresObjectMain.has("result")) {
                            JSONArray resultArray = jresObjectMain.getJSONArray("result");
                            listMake.clear();
                            listMakeId.clear();


                            listMake.add("Select Make");
                            listMakeId.add("-1");

                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jsonObject = resultArray.getJSONObject(i);

                                listMake.add("" + CommonClass.getDataFromJson(jsonObject, "make_name"));
                                listMakeId.add("" + CommonClass.getDataFromJsonInt(jsonObject, "term_id"));
                            }


                            if (mAdapterCategory == null) {
                                mAdapterCategory = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listCategory);
                                spinnerCategory.setAdapter(mAdapterCategory);
                                mAdapterCategory.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerCategory.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
                            } else {
                                mAdapterCategory.notifyDataSetChanged();
                            }


                            if (mAdapterMake == null) {
                                mAdapterMake = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listMake);
                                spinnerMake.setAdapter(mAdapterMake);
                                mAdapterMake.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerMake.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
                            } else {
                                mAdapterMake.notifyDataSetChanged();
                            }

                            if (tmpMake.trim().length() != 0) {

                                int index = listMake.indexOf(tmpMake);
                                if (index != -1)
                                    spinnerMake.setSelection(index);
                            }

                            Log.i("AssistFragment", "tmpCategory==" + tmpCategory);

                            if (tmpCategory.trim().length() != 0) {

                                int index = listCategory.indexOf(tmpCategory);
                                if (index != -1)
                                    spinnerCategory.setSelection(index);
                            }


                        }


                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }

            }
        };
    }


    private com.android.volley.Response.Listener<String> yearSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if (jresObjectMain.has("result")) {
                            JSONArray resultArray = jresObjectMain.getJSONArray("result");
                            listYear.clear();
                            listYear.add("Select Year");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jsonObject = resultArray.getJSONObject(i);

                                listYear.add("" + CommonClass.getDataFromJson(jsonObject, "year"));

                            }


                            Log.i("AssistFragment", "==Condition== if > " + isFirstYear);


                            if (mAdapterYear == null) {
                                mAdapterYear = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listYear);
                                spinnerYear.setAdapter(mAdapterYear);
                                mAdapterYear.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerYear.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
                            } else {
                                mAdapterYear.notifyDataSetChanged();
                            }

                            if (tmpYear.trim().length() != 0) {

                                int index = listYear.indexOf(tmpYear);
                                if (index != -1)
                                    spinnerYear.setSelection(index);
                            }


                        }

                        spinnerYear.setVisibility(View.VISIBLE);

                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }

            }
        };
    }

    private com.android.volley.Response.Listener<String> modelSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if (jresObjectMain.has("result")) {
                            JSONArray resultArray = jresObjectMain.getJSONArray("result");

                            listModel.clear();
                            listModel.add("Select Model");
                            for (int i = 0; i < resultArray.length(); i++) {
                                JSONObject jsonObject = resultArray.getJSONObject(i);

                                listModel.add("" + CommonClass.getDataFromJson(jsonObject, "model_name"));

                            }


                            if (mAdapterModel == null) {

                                mAdapterModel = new ArrayAdapter<String>(getActivity(), R.layout.spiner_item_layout, listModel);
                                spinnerModel.setAdapter(mAdapterModel);
                                mAdapterModel.setDropDownViewResource(R.layout.spiner_dropdown_item_layout);
                                android.widget.TextView tv = (android.widget.TextView) spinnerModel.getSelectedView();
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.drop_down_arrow, 0, 0, 0);
                            } else
                                mAdapterModel.notifyDataSetChanged();


                            if (tmpModel.trim().length() != 0) {

                                int index = listModel.indexOf(tmpModel);
                                if (index != -1)
                                    spinnerModel.setSelection(index);
                            }


                        }

                        spinnerModel.setVisibility(View.VISIBLE);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
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
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
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
//        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    private synchronized void insertMarkers(final ArrayList<Assist> list) {
//        Log.e("insertMarkers", "" + list.size());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        try {
            if (googleMap != null)
                googleMap.clear();

            for (int i = 0; i < list.size(); i++) {


                if (list.get(i).lat.trim().length() > 0 && list.get(i).lng.trim().length() > 0) {


                    try {
                        LatLng tempLatLng = new LatLng(Double.parseDouble(list.get(i).lat), Double.parseDouble(list.get(i).lng));
                        final MarkerOptions options = new MarkerOptions().position(tempLatLng);
                        Bitmap smallMarker = null;
                        if (list.get(i).id.equalsIgnoreCase("-1")) {

                            options.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.select_map_pin));

                            UserDetail userDetail = CommonClass.getUserpreference(getActivity());
                            String imagePath = userDetail.user_image;

                            if (imagePath != null && imagePath.length() > 0) {

                                if (imagePath.startsWith("http")) {
                                    imagePath = imagePath.trim();

                                } else {
                                    Log.e("FragmentDrawer", "kk ELSE---------- " + Constants.imagBaseUrl + imagePath);
                                    imagePath = Constants.imagBaseUrl + imagePath.trim();
                                }
                                //Log.e("Picasso", "Image Path Final---------- " + imagePath);
                                if (imagePath.trim().length() > 0) {

                                    try {
                                        URL url = new URL(imagePath);
                                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        smallMarker = Bitmap.createScaledBitmap(bmp, 100, 100, false);
                                        // options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
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
                                    //options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                }

                            } else {
                                Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                        R.drawable.no_image);
                                smallMarker = Bitmap.createScaledBitmap(icon, 100, 100, false);
                                // options.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                            }


                        } else {

                            options.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.map_pin));
                        }


                        View markerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                        CircleImageView markerImageView = (CircleImageView) markerView.findViewById(R.id.imgUserImage);
                        ImageView imgBg = (ImageView) markerView.findViewById(R.id.imgBg);
                        if (!list.get(i).id.equalsIgnoreCase("-1")) {
                            imgBg.setImageResource(R.drawable.tmp_pin_grey);
                            options.icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.tmp_pin_grey));
                        }
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


                        options.title(list.get(i).name);
                        String str = list.get(i).address;
                        options.snippet("" + str);


                        Log.i("AssistFragment", "======>>>" + i);
                        Marker marker = googleMap.addMarker(options);

                        builder.include(marker.getPosition());
                        mHashMap.put(marker, i);

                        mybuilder = builder;


                    } catch (NumberFormatException e) {

                        Log.i("AssistFragment", "======>>>" + e.getLocalizedMessage());
                    }
                }

            }


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

            if (arrayList.get(0).lat.trim().length() > 0 && arrayList.get(0).lng.trim().length() > 0) {


                CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble(arrayList.get(0).lat), Double.parseDouble(arrayList.get(0).lng)));


                cameraBuilder.zoom(googleMap.getCameraPosition().zoom);

                CameraPosition cameraPosition = cameraBuilder.build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.animateCamera(yourLocation, new GoogleMap.CancelableCallback() {
                    public void onCancel() {
                    }

                    public void onFinish() {


                        CameraPosition.Builder cameraBuilder = new CameraPosition.Builder()
                                .target(new LatLng(Double.parseDouble(arrayList.get(0).lat), Double.parseDouble(arrayList.get(0).lng)));


                        cameraBuilder.zoom(googleMap.getCameraPosition().zoom);

                        CameraPosition cameraPosition = cameraBuilder.build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    }
                });
            }


//            LatLngBounds bounds = builder.build();
//            int padding = 0; // offset from edges of the map in pixels
//            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//            googleMap.animateCamera(cu);


            googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {

                    int pos = mHashMap.get(marker);
                    Assist ad = list.get(pos);

                    if (!ad.id.equalsIgnoreCase("-1")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("assist_id", "" + ad.id);


                        AssistDetailFragment fragment = new AssistDetailFragment();
                        fragment.setArguments(bundle);
                        if (fragment != null) {
                            replaceFragment(fragment, true, "AssistDetail");
                        }
                    }

                }
            });
            showCurrLoc = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void updateMarker(LatLng tLatLng) {

        latLng = tLatLng;

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
//        Log.d(TAG, "Firing onLocationChanged..............................................");
//        if (mCurrentLocation == null )
        mCurrentLocation = location;

//        int distance = (int) mCurrentLocation.distanceTo(location);
//        Log.e("Distance", "--Distance---------" + distance);
//        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//        if(distance>=500) {
        updateMarker(new LatLng(location.getLatitude(), location.getLongitude()));
//            mCurrentLocation=null;

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


}