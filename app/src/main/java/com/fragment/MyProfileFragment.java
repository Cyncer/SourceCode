package com.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.squareup.picasso.Picasso;
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
public class MyProfileFragment extends BaseContainerFragment {

    private Activity mActivity;
    private ImageView ivBack, ivEdit, ivProfile;
    private TextView tvEmail, tvUsername,tvUserMake,tvUserYear,tvUserModel;
    View tmpView,tmpView1,tmpView2;
    public static MenuItem editProfileIcon, action_settings;
    private RequestQueue mQueue;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_myprofile, menu);
        editProfileIcon = menu.findItem(R.id.editProfileIcon);

        editProfileIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                EditProfileFragment fragment = new EditProfileFragment();
                if (fragment != null) {
                    replaceFragment(fragment, true, "MyProfile");
                }
                return false;
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_myprofile, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(rootView);
        getUserData();
        return rootView;
    }


    public void Init(View view) {
        ivProfile = (ImageView) view.findViewById(R.id.ivProfile);
        tvEmail = (TextView) view.findViewById(R.id.tvEmail);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        tvUserMake = (TextView) view.findViewById(R.id.tvUserMake);
        tvUserYear = (TextView) view.findViewById(R.id.tvUserYear);
        tvUserModel = (TextView) view.findViewById(R.id.tvUserModel);

        tmpView = (View) view.findViewById(R.id.tmpView);
        tmpView1 = (View) view.findViewById(R.id.tmpView1);
        tmpView2 = (View) view.findViewById(R.id.tmpView2);

        tmpView.setVisibility(View.VISIBLE);
        tvUserMake.setVisibility(View.GONE);
        tvUserYear.setVisibility(View.GONE);
        tvUserModel.setVisibility(View.GONE);

        tmpView1.setVisibility(View.GONE);
        tmpView2.setVisibility(View.GONE);
        boolean showMessage = CheckValid();
        if (CommonClass.getIsFirstTimepreference(getActivity()).equals("true") && showMessage) {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {


                    CommonClass.setIsFirstTimepreference(getActivity(), "false");

                    try {
                        Toast.makeText(getActivity(), "Don't forget to add your ride! You can select your motorcycle make and model by editing your profile.", Toast.LENGTH_LONG).show();
                    }catch (NullPointerException e)
                    {

                    }

                }

            }, 500);
        }
    }

    private boolean CheckValid() {

        if (CommonClass.getUserpreference(getActivity()).make.trim().length() > 0 && CommonClass.getUserpreference(getActivity()).year.trim().length() > 0 && CommonClass.getUserpreference(getActivity()).model.trim().length() > 0) {
            return false;
        } else
            return true;
    }

    private void getUserData() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.getuserdetails, SuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", CommonClass.getUserpreference(mActivity).user_id);
                Log.d("getUserData Request", "==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(getActivity());
        mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
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



                        String user_id,user_image="",first_name,last_name,login_type,email, make,model, year;
                        boolean notification_type;
                        JSONObject jresObject = jresObjectMain.getJSONObject("data");
                        user_id = "" + CommonClass.getDataFromJsonInt(jresObject, "user_id");
                        user_image = CommonClass.getDataFromJson(jresObject, "user_image");
                        first_name = CommonClass.getDataFromJson(jresObject, "first_name");
                        last_name = CommonClass.getDataFromJson(jresObject, "last_name");
                        login_type = CommonClass.getDataFromJson(jresObject, "login_type");
                        make= CommonClass.getDataFromJson(jresObject, "make");
                        model= CommonClass.getDataFromJson(jresObject, "model");
                        year= CommonClass.getDataFromJson(jresObject, "year");

                        if(login_type==null||login_type.trim().length()==0){
                            login_type=CommonClass.getUserpreference(getActivity()).login_type;
//                            Log.e(" - Profile", "login_type " + login_type);
                        }
                        email = CommonClass.getDataFromJson(jresObject, "email");
                        if(jresObject.has("notification_type")) {
                            notification_type = CommonClass.getDataFromJsonBoolean(jresObject, "notification_type");
//                            Log.i("notification_type","--------"+notification_type);
                        }
                        else
                            notification_type=CommonClass.getUserpreference(mActivity).notification;


                        boolean view_my_rides=true, hide_top_speed=false;

                        if(jresObject.has("view_my_rides"))
                            view_my_rides= CommonClass.getDataFromJsonBoolean(jresObject, "view_my_rides");
                        else
                            notification_type=CommonClass.getUserpreference(mActivity).view_my_rides;


                        if(jresObject.has("hide_top_speed"))
                            hide_top_speed= CommonClass.getDataFromJsonBoolean(jresObject, "hide_top_speed");
                        else
                            notification_type=CommonClass.getUserpreference(mActivity).hide_top_speed;




                        UserDetail ud = new UserDetail(user_id, user_image, first_name, last_name, login_type, email,notification_type, make,model, year,view_my_rides, hide_top_speed);
                        CommonClass.updateUserpreference( mActivity, ud);
                        setUserData(ud);

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
                CommonClass.ShowToast(getActivity(), "Something went wrong. Please try again.");
            }
        };
    }

    public void setUserData(UserDetail userDetail ) {

        String user_name="";
        if (userDetail != null) {
            if (!userDetail.first_name.equalsIgnoreCase("null")&&userDetail.first_name.length()>0) {
                user_name = userDetail.first_name;
            }
            if (!userDetail.last_name.equalsIgnoreCase("null")&&userDetail.last_name.length()>0) {
                user_name = user_name + " " + userDetail.last_name;
            }
//            user_name= CommonClass.strEncodeDecode(user_name,true);
            tvUsername.setText(user_name);





            if (!userDetail.make.equalsIgnoreCase("null")&&userDetail.make.length()>0) {
                tvUserMake.setText(userDetail.make);
                tvUserMake.setVisibility(View.VISIBLE);
                tmpView1.setVisibility(View.VISIBLE);
                tmpView.setVisibility(View.GONE);
            }

            if (!userDetail.year.equalsIgnoreCase("null")&&userDetail.year.length()>0) {
                tvUserYear.setText(userDetail.year);
                tmpView2.setVisibility(View.VISIBLE);
                tvUserYear.setVisibility(View.VISIBLE);
            }

            if (!userDetail.model.equalsIgnoreCase("null")&&userDetail.model.length()>0) {
                tvUserModel.setText(userDetail.model);
                tvUserModel.setVisibility(View.VISIBLE);

            }








//            Log.e("login_type",""+userDetail.login_type);
            if (userDetail.login_type.equalsIgnoreCase("facebook"))
                    tvEmail.setText("Facebook User");
            else {
                if (!userDetail.email.equalsIgnoreCase("null"))
                    tvEmail.setText(userDetail.email);
            }
            String imagePath = userDetail.user_image;


            if (imagePath != null && imagePath.length() > 0 ) {
                if(userDetail.login_type.equalsIgnoreCase("facebook")){
                    if(imagePath.contains("https://")||imagePath.contains("http://"))
                        imagePath=imagePath.trim();
                    else
                        imagePath = Constants.imagBaseUrl + imagePath.trim();
                }else
                    imagePath = Constants.imagBaseUrl + imagePath.trim();

//                Log.e("imagePath", "-------" + imagePath);

                Picasso.with(getActivity())
                        .load(imagePath)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(ivProfile);

            }
          else
                        ivProfile.setImageResource(R.drawable.no_image);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}