package com.app.android.cync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
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
 * Created by ketul.patel on 18/1/16.
 */
public class OtherUserProfileActivity extends Activity {


    private Activity mActivity;
    private ImageView ivBack, ivEdit, ivProfile;
    private TextView tvEmail, tvUsername, tvUserMake, tvUserYear, tvUserModel;
    View tmpView, tmpView1, tmpView2;
    String user_id;
    private RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_profile);
        mQueue = VolleySetup.getRequestQueue();

        user_id = getIntent().getExtras().getString("user_id");
        Init();
        getUserData();
    }


    public void Init() {

        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent output = new Intent();
                setResult(RESULT_OK, output);
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);

            }
        });

        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvUserMake = (TextView) findViewById(R.id.tvUserMake);
        tvUserYear = (TextView) findViewById(R.id.tvUserYear);
        tvUserModel = (TextView) findViewById(R.id.tvUserModel);

        tmpView = (View) findViewById(R.id.tmpView);
        tmpView1 = (View) findViewById(R.id.tmpView1);
        tmpView2 = (View) findViewById(R.id.tmpView2);

        tmpView.setVisibility(View.VISIBLE);
        tvUserMake.setVisibility(View.GONE);
        tvUserYear.setVisibility(View.GONE);
        tvUserModel.setVisibility(View.GONE);

        tmpView1.setVisibility(View.GONE);
        tmpView2.setVisibility(View.GONE);


    }


    @Override
    public void onBackPressed() {

        Intent output = new Intent();
        setResult(RESULT_OK, output);
        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }


    private void getUserData() {

        ConnectionDetector cd = new ConnectionDetector(OtherUserProfileActivity.this);
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.getuserdetails, SuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", user_id);
                Log.d("getUserData Request", "==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(OtherUserProfileActivity.this);
        mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(OtherUserProfileActivity.this, getResources().getString(R.string.check_internet));


        }
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(OtherUserProfileActivity.this);
                boolean Status;
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
                        first_name = CommonClass.getDataFromJson(jresObject, "first_name");
                        last_name = CommonClass.getDataFromJson(jresObject, "last_name");
                        login_type = CommonClass.getDataFromJson(jresObject, "login_type");
                        make = CommonClass.getDataFromJson(jresObject, "make");
                        model = CommonClass.getDataFromJson(jresObject, "model");
                        year = CommonClass.getDataFromJson(jresObject, "year");

//                        if (login_type == null || login_type.trim().length() == 0) {
//                            login_type = CommonClass.getUserpreference(OtherUserProfileActivity.this).login_type;
////                            Log.e(" - Profile", "login_type " + login_type);
//                        }
                        email = CommonClass.getDataFromJson(jresObject, "email");
                        if (jresObject.has("notification_type")) {
                            notification_type = CommonClass.getDataFromJsonBoolean(jresObject, "notification_type");
//                            Log.i("notification_type","--------"+notification_type);
                        } else
                            notification_type = CommonClass.getUserpreference(mActivity).notification;
                        UserDetail ud = new UserDetail(user_id, user_image, first_name, last_name, login_type, email, notification_type, make, model, year,true,false);

                        setUserData(ud);

                    } else {
                        CommonClass.ShowToast(OtherUserProfileActivity.this, message);
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
                CommonClass.closeLoding(OtherUserProfileActivity.this);
                CommonClass.ShowToast(OtherUserProfileActivity.this, getResources().getString(R.string.s_wrong));
            }
        };
    }

    public void setUserData(UserDetail userDetail) {

        String user_name = "";
        if (userDetail != null) {
            if (!userDetail.first_name.equalsIgnoreCase("null") && userDetail.first_name.length() > 0) {
                user_name = userDetail.first_name;
            }
            if (!userDetail.last_name.equalsIgnoreCase("null") && userDetail.last_name.length() > 0) {
                user_name = user_name + " " + userDetail.last_name;
            }
//            user_name= CommonClass.strEncodeDecode(user_name,true);
            tvUsername.setText(user_name);


            if (!userDetail.make.equalsIgnoreCase("null") && userDetail.make.length() > 0) {
                tvUserMake.setText(userDetail.make);
                tvUserMake.setVisibility(View.VISIBLE);
                tmpView1.setVisibility(View.VISIBLE);
                tmpView.setVisibility(View.GONE);
            }

            if (!userDetail.year.equalsIgnoreCase("null") && userDetail.year.length() > 0) {
                tvUserYear.setText(userDetail.year);
                tmpView2.setVisibility(View.VISIBLE);
                tvUserYear.setVisibility(View.VISIBLE);
            }

            if (!userDetail.model.equalsIgnoreCase("null") && userDetail.model.length() > 0) {
                tvUserModel.setText(userDetail.model);
                tvUserModel.setVisibility(View.VISIBLE);

            }


//            Log.e("login_type",""+userDetail.login_type);
            if (userDetail.email.trim().length() == 0){
                tvEmail.setText("Facebook User");
            userDetail.login_type = "facebook";}
            else{
                if (!userDetail.email.equalsIgnoreCase("null"))
                    tvEmail.setText(userDetail.email);
            }




            String imagePath = userDetail.user_image;
            if (imagePath != null && imagePath.length() > 0) {
                if (userDetail.login_type.equalsIgnoreCase("facebook")) {
                    if (imagePath.contains("https://") || imagePath.contains("http://"))
                        imagePath = imagePath.trim();
                    else
                        imagePath = Constants.imagBaseUrl + imagePath.trim();
                } else
                    imagePath = Constants.imagBaseUrl + imagePath.trim();

//                Log.e("imagePath", "-------" + imagePath);


                Picasso.with(OtherUserProfileActivity.this)
                        .load(imagePath)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(ivProfile);

            } else
                ivProfile.setImageResource(R.drawable.no_image);
        }
    }


}
