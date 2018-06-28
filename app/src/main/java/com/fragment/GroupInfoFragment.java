package com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adapter.GroupInfoListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.rey.material.widget.Button;
import com.rey.material.widget.CompoundButton;
import com.rey.material.widget.Switch;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupInfoFragment extends BaseContainerFragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ImageView ivUserImage, edit_group, ivAddGroupMember, line;
    private TextView tvGroupname;
    public static TextView tvCountMember;
    private Switch swShareLocation;
    //    private CheckBox chkMember;
    private Button btnSave, btnSeeMap, btnExitGroup;
    private Activity mActivity;
    private RequestQueue mQueue;
    private GroupInfoListAdapter groupInfoListAdapter;
    private String groupId;
    private ArrayList<UserDetail> userDetailArrayList;
    private String group_id, group_image = "", group_name;
    public static boolean switchLocation = false;
    public boolean isAdmin, isCreated = false;
    private ImageView ivBack;
    private CardView cvSharelocation;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        mQueue = VolleySetup.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            groupId = getArguments().getString("group_id", "");
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);
        isCreated = true;
        init(view);
        if (groupId != null && groupId.trim().length() > 0)
            getGroupInfo();
        else
            CommonClass.ShowToast(mActivity, "No result found.");
        this.setGroupMapButton(swShareLocation.isChecked());
        return view;
    }

    private void init(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.group_recycler_view);
        cvSharelocation = (CardView) view.findViewById(R.id.cvSharelocation);
        mRecyclerView.setHasFixedSize(true);
        ivAddGroupMember = (ImageView) view.findViewById(R.id.ivAddGroupMember);
        ivAddGroupMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("group_id", groupId);
                GroupFriendListFragment fragment = new GroupFriendListFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment, true, "GroupFriendListFragment");
            }
        });
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
//                GroupListFragment fragment = new GroupListFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("group_id", "");
//                fragment.setArguments(bundle);
//                replaceFragment(fragment, false, "GroupInfo");
//                GroupListFragment fragment = new GroupListFragment();
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.container_body, fragment, "GroupList");
//                fragmentTransaction.commit();
            }
        });
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ivUserImage = (ImageView) view.findViewById(R.id.ivUserImage);
        edit_group = (ImageView) view.findViewById(R.id.edit_group);
        line = (ImageView) view.findViewById(R.id.line);
        edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("group_id", groupId);
                bundle.putString("group_name", group_name);
                bundle.putString("group_image", group_image);

                //set Fragmentclass Arguments
                UpdateGroupFragment fragment = new UpdateGroupFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment, true, "UpdateGroupFragment");
            }
        });

        tvGroupname = (TextView) view.findViewById(R.id.tvGroupname);
        tvCountMember = (TextView) view.findViewById(R.id.tvCountMember);
        swShareLocation = (Switch) view.findViewById(R.id.swShareLocation);
        swShareLocation.setVisibility(View.GONE);
        btnExitGroup = (Button) view.findViewById(R.id.btnExitGroup);
        btnExitGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Are you sure?")
                        .setMessage("You want to exit this group?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteGroups();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        btnSeeMap = (Button) view.findViewById(R.id.btnSeeMap);
        btnSeeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("group_id", groupId);
                HomeFragment fragment = new HomeFragment();
                fragment.setArguments(bundle);
                replaceFragment(fragment, true, "HomeFragment");
            }
        });
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAdmin)
                    CommonClass.ShowToast(mActivity, "Please share group location to view group map.");
                else
                    CommonClass.ShowToast(mActivity, "Group location is not shared.");
            }
        });
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    popFragment();
//                    replaceFragment(new GroupListFragment(), true, "GroupListFragment");
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        isCreated = false;
    }

    private void getGroupInfo() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.getGroupInfo, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("group_id", groupId);
                    Log.e("getFriendList Request", "==> " + requestparam);
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
                Log.d("Success Listener Json", "Group Info==> " + response.trim());
                CommonClass.closeLoding(getActivity());
                parseJsonResponse(response);
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

    private void setData(ArrayList<UserDetail> userDetailList) {
        for (UserDetail element : userDetailList) {
            if (element.user_id.equalsIgnoreCase(CommonClass.getUserpreference(mActivity).user_id)) {
                if (element.share_location.equalsIgnoreCase("off")) {
                    swShareLocation.setChecked(false);
                    switchLocation = false;
                } else if (element.share_location.equalsIgnoreCase("on")) {
                    swShareLocation.setChecked(true);
                    switchLocation = true;
                }
            }
        }
//        Log.e("group_name", "-------" + group_name);
        tvGroupname.setText(group_name);
        String formatted = String.format("%02d", userDetailList.size());
        GroupInfoFragment.tvCountMember.setText("  " + formatted);
//        Log.e("group_image", "-------" + group_image);
        if (group_image != null && group_image.length() > 0) {
            String imagePath = Constants.imagBaseUrl + group_image.trim();
//                 Log.e("imagBaseUrl", "-------" + imagePath);


            Picasso.with(mActivity)
                    .load(imagePath)
                    .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(ivUserImage);

        } else
            ivUserImage.setImageResource(R.drawable.no_image);

        boolean isAdmin = false;
        if (userDetailList != null && userDetailList.size() > 0)
            if (userDetailList.get(0).user_type.equalsIgnoreCase("admin"))
                if (userDetailList.get(0).user_id.equalsIgnoreCase(CommonClass.getUserpreference(mActivity).user_id)) {
                    edit_group.setVisibility(View.VISIBLE);
                    line.setVisibility(View.VISIBLE);
                    cvSharelocation.setVisibility(View.VISIBLE);
                    swShareLocation.setVisibility(View.VISIBLE);
                    final ArrayList<UserDetail> userDetails = userDetailList;


                    swShareLocation.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(Switch view, boolean checked) {

                            if (!isCreated) {
                                if (checked) {
                                    switchLocation = true;
//                                        if (groupInfoListAdapter != null)
//                                            groupInfoListAdapter.notifyDataSetChanged();
                                } else {
                                    switchLocation = false;
//                                        if (groupInfoListAdapter != null)
//                                            groupInfoListAdapter.notifyDataSetChanged();
                                }
                                if (userDetails.size() > 1) {
                                    updateGroupLocationFlag();
                                    swShareLocation.setChecked(switchLocation);
                                    setGroupMapButton(switchLocation);
//                                    Log.e("updateGroupLocationFlag", "updateGroupLocationFlag");
                                } else {
                                    CommonClass.ShowToast(getActivity(), "No members for sharing group location.");
                                    swShareLocation.setChecked(false);
                                }
                            }
                        }
                    });


//                    swShareLocation.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            if (!isCreated) {
//                                if (swShareLocation.isChecked()) {
//                                    switchLocation = true;
////                                        if (groupInfoListAdapter != null)
////                                            groupInfoListAdapter.notifyDataSetChanged();
//                                } else {
//                                    switchLocation = false;
////                                        if (groupInfoListAdapter != null)
////                                            groupInfoListAdapter.notifyDataSetChanged();
//                                }
//                                if (userDetails.size() > 1) {
//                                    updateGroupLocationFlag();
//                                    swShareLocation.setChecked(switchLocation);
//                                    setGroupMapButton(switchLocation);
////                                    Log.e("updateGroupLocationFlag", "updateGroupLocationFlag");
//                                } else {
//                                    CommonClass.ShowToast(getActivity(), "No members for sharing group location.");
//                                    swShareLocation.setChecked(false);
//                                }
//                            }
//                        }
//                    });
                    isAdmin = true;
                } else {


                    setGroupMapButton(userDetailList.get(0).isSelected);
                    edit_group.setVisibility(View.INVISIBLE);
                    line.setVisibility(View.INVISIBLE);
                    cvSharelocation.setVisibility(View.GONE);
                    isAdmin = false;
                }
        swShareLocation.setChecked(userDetailList.get(0).isSelected);
        setGroupMapButton(userDetailList.get(0).isSelected);
        userDetailList = CommonClass.sortUserDetails(userDetailList);
        if (userDetailList != null && userDetailList.size() > 0) {
            groupInfoListAdapter = new GroupInfoListAdapter(this, userDetailList, /*chkMember,*/group_id, isAdmin);
            mRecyclerView.setAdapter(groupInfoListAdapter);
        } else {
            CommonClass.ShowToast(getActivity(), "No result found.");
        }

    }

    private void setGroupMapButton(boolean isChecked) {
        if (isChecked) {
            btnSeeMap.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.GONE);
//            btnSeeMap.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.colorPrimary_black));
//            System.out.println("GroupInfo======== IF ========================" + isChecked);
        } else {
            btnSeeMap.setVisibility(View.GONE);
            btnSave.setVisibility(View.VISIBLE);
//            btnSeeMap.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.grey_colorRipple));
//            System.out.println("GroupInfo========ELSE ========================" + isChecked);
        }
        btnSeeMap.setClickable(isChecked);
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void deleteGroups() {

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.remove_group, deleteGroupSuccessLisner(),
                    deleteGroupErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("group_id", groupId);
//                requestparam.put("status", "delete");
//                Log.e("deleteGroups Request", "==> " + Constants.remove_group + "" + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private com.android.volley.Response.Listener<String> deleteGroupSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        CommonClass.ShowToast(getActivity(), message);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                    popFragment();
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(getActivity(), message);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener deleteGroupErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
    }

    //------------------
    public void updateGroupLocationFlag() {
        if (groupInfoListAdapter != null) {
            if (groupInfoListAdapter.getCheckedItems().size() > 0) {
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {
                    String shareGroupLocation = "";
                    if (swShareLocation.isChecked())
                        shareGroupLocation = "on";
                    else
                        shareGroupLocation = "off";

                    String strMemberId = CommonClass.getCSVFromArrayList(groupInfoListAdapter.getCheckedItems());
                    if (strMemberId.endsWith(",")) {
                        strMemberId = strMemberId.replaceAll(", $", "");
                    }
//                    Log.e("strMemberId", "--------" + strMemberId);
                    if (strMemberId != null && strMemberId.length() > 0)
                        updateShareGrouplocation(shareGroupLocation, strMemberId);

                }
            } else
                CommonClass.ShowToast(mActivity, "");
        }
    }

    //----------------
    private void updateShareGrouplocation(final String strLocation, final String strMemberId) {

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.updateGroupInfo, locationGroupSuccessLisner(),
                    locationGroupErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("group_id", groupId);
                    requestparam.put("share_location", strLocation);
                    requestparam.put("share_members_id", strMemberId);

//                Log.e("Location Request", "==> " + Constants.updateGroupInfo + "" + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private com.android.volley.Response.Listener<String> locationGroupSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        CommonClass.ShowToast(getActivity(), message);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
//                    popFragment();
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(getActivity(), message);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener locationGroupErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
    }

    private void parseJsonResponse(String strResponse) {
        boolean Status;
        String message;
//        Log.e("parseJsonResponse", "==> " + strResponse);
        try {
            JSONObject jresObjectMain = new JSONObject(strResponse);
            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");
            if (Status) {
                isAdmin = false;
                String user_id, user_image = "", first_name, last_name, lat, lng, share_location, status;
                boolean groupLocation, isFriend;
                JSONObject jresObj = jresObjectMain.getJSONObject("data");
                if (jresObj != null) {
                    group_id = "" + CommonClass.getDataFromJsonInt(jresObj, "group_id");
                    group_image = CommonClass.getDataFromJson(jresObj, "group_image");
//
                    group_name = CommonClass.getDataFromJson(jresObj, "group_name");

                    if (userDetailArrayList != null)
                        userDetailArrayList.clear();

                    userDetailArrayList = new ArrayList<UserDetail>();
                    JSONArray jAdmin = jresObj.getJSONArray("admin");
                    JSONObject jsonAdmin = jAdmin.getJSONObject(0);
                    if (jsonAdmin != null) {
                        user_id = "" + CommonClass.getDataFromJsonInt(jsonAdmin, "user_id");
                        user_image = CommonClass.getDataFromJson(jsonAdmin, "user_image");
                        first_name = CommonClass.getDataFromJson(jsonAdmin, "first_name");
                        last_name = "" + CommonClass.getDataFromJson(jsonAdmin, "last_name");
                        share_location = CommonClass.getDataFromJson(jsonAdmin, "share_location");
                        lat = CommonClass.getDataFromJson(jsonAdmin, "latitude");
                        lng = CommonClass.getDataFromJson(jsonAdmin, "longitude");
                        isFriend = true;
                        status = CommonClass.getDataFromJson(jsonAdmin, "status");

                        if (share_location.equalsIgnoreCase("on")) {
                            groupLocation = true;
                        } else {
                            groupLocation = false;
                        }
                        // groupLocation = CommonClass.getDataFromJsonBoolean(jsonAdmin, "group_location");
//                        Log.e("Paser ADMIN", "ADMIN " + groupLocation);
                        UserDetail userDetail = new UserDetail(user_id, user_image, first_name, last_name, share_location, "admin", lat, lng, groupLocation, isFriend, status);
                        userDetail.setSelected(groupLocation);
                        userDetailArrayList.add(userDetail);
                        if (CommonClass.getUserpreference(mActivity).user_id.equalsIgnoreCase(user_id)) {
                            isAdmin = true;
                            ivAddGroupMember.setVisibility(View.VISIBLE);
                        } else {
                            isAdmin = false;
                            ivAddGroupMember.setVisibility(View.GONE);
                        }

                    }
                    JSONArray jresArray = jresObj.getJSONArray("member");
                    for (int i = 0; i < jresArray.length(); i++) {
                        JSONObject jsonObject = jresArray.getJSONObject(i);
                        user_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
                        user_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                        first_name = CommonClass.getDataFromJson(jsonObject, "first_name");
                        last_name = "" + CommonClass.getDataFromJson(jsonObject, "last_name");
                        share_location = CommonClass.getDataFromJson(jsonObject, "share_location");
                        lat = CommonClass.getDataFromJson(jsonObject, "latitude");
                        lng = CommonClass.getDataFromJson(jsonObject, "longitude");
                        groupLocation = CommonClass.getDataFromJsonBoolean(jsonObject, "group_location");
                        isFriend = CommonClass.getDataFromJsonBoolean(jsonObject, "is_friend");
//                        Log.e("Paser Member", "Member " + groupLocation);
                        status = CommonClass.getDataFromJson(jsonAdmin, "status");
                        UserDetail userDetail = new UserDetail(user_id, user_image, first_name, last_name, share_location, "member", lat, lng, groupLocation, isFriend, status);
//                        userDetail.setSelected(groupLocation);
                        userDetailArrayList.add(userDetail);
                    }
                    setData(userDetailArrayList);
                }
            } else {
                CommonClass.ShowToast(getActivity(), message);
            }

        } catch (JSONException e) {
            message = getResources().getString(R.string.s_wrong);
            CommonClass.ShowToast(getActivity(), message);
            e.printStackTrace();
        }
    }


}
