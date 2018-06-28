package com.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.customwidget.materialEditText;
import com.rey.material.widget.Button;
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
public class ChangePasswordFragment extends BaseContainerFragment {

    Activity mActivity;
    materialEditText old_password, new_password, confirm_password;
    Button update_btn;
    private RequestQueue mQueue;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueue = VolleySetup.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);
        init(rootView);
        return rootView;
    }

    public void init(View view) {
        old_password = (materialEditText) view.findViewById(R.id.old_password);
        old_password.setTransformationMethod(new PasswordTransformationMethod());
        new_password = (materialEditText) view.findViewById(R.id.new_password);
        new_password.setTransformationMethod(new PasswordTransformationMethod());
        confirm_password = (materialEditText) view.findViewById(R.id.confirm_password);
        confirm_password.setTransformationMethod(new PasswordTransformationMethod());
        update_btn = (Button) view.findViewById(R.id.update_btn);
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = ValidationTask(old_password.getText().toString().trim(), new_password.getText().toString().trim(), confirm_password.getText().toString().trim());
                if (valid) {
                    ConnectionDetector cd = new ConnectionDetector(getActivity());
                    boolean isConnected = cd.isConnectingToInternet();
                    if (isConnected) {
                        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.changePassword, SuccessLisner(),
                                ErrorLisner()) {
                            @Override
                            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                                HashMap<String, String> requestparam = new HashMap<>();
                                requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                                requestparam.put("new_password", new_password.getText().toString());
                                requestparam.put("old_password", old_password.getText().toString());
                                return requestparam;
                            }
                        };
                        CommonClass.showLoading(mActivity);
                        mQueue.add(apiRequest);
                    } else {

                        CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


                    }
                }
            }
        });
    }


    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.e("Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
//                        callLogout();
                        old_password.setText("");
                        new_password.setText("");
                        confirm_password.setText("");
                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }
                CommonClass.ShowToast(getActivity(), message);
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity);
                CommonClass.ShowToast(getActivity(), mActivity.getString(R.string.s_wrong));
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

    private boolean ValidationTask(String oldPassword, String newPassword, String confirmPassword) {
        if (oldPassword.trim().length() > 0) {
            if (newPassword.trim().length() > 0) {

                if (newPassword.trim().length() >= 6) {

                    if (confirmPassword.trim().length() > 0) {
                        if (newPassword.equals(confirmPassword)) {
//                            System.out.printf("-----confirmPassword-------------------------------------");
                            if (oldPassword.equals(newPassword)) {
//                                System.out.printf("-----oldPassword-------------------------------------");
                                CommonClass.ShowToast(mActivity, "Old password can not be the same as new password.");
                                return false;
                            } else {
                                return true;
                            }
                        } else {
                            CommonClass.ShowToast(mActivity, "Password and confirm password do not match.");
                            return false;
                        }
                    } else {
                        CommonClass.ShowToast(mActivity, "Password and confirm password do not match.");
                        return false;
                    }
                } else {
                    CommonClass.ShowToast(mActivity, "Password should contain minimum 6 characters.");
                    return false;
                }
            } else {
                CommonClass.ShowToast(mActivity, "Please enter your new password.");
                return false;
            }
        } else {
            CommonClass.ShowToast(mActivity, "Please enter your old password.");
            return false;
        }

    }
//    public void callLogout() {
//        if (CommonClass.getUserpreference(mActivity).login_type.equalsIgnoreCase("facebook")) {
////            System.out.printf("--Call Facebook------------------");
//            FacebookSdk.sdkInitialize(mActivity);
//            LoginManager.getInstance().logOut();
//        }
//        SharedPreferences sp = ApplicationController.getContext().getSharedPreferences("userpref", ApplicationController.getContext().MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.clear();
//        editor.commit();
//        Intent intent = new Intent(mActivity, LoginActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//        mActivity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
//
//    }
}