package com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.GroupFriendListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.backgroundTask.CreateGroupTask;
import com.cync.model.FriendDetail;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.AsyncUtil;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GroupFriendListFragment extends BaseContainerFragment {

    private Activity mActivity;
    private RequestQueue mQueue;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //    private List<FriendListDetail> groupListDetail;
    private ArrayList<FriendDetail> friendArrayList;
    public static MenuItem addfriendIcon, deleteAllmenuLbl;
    RelativeLayout container_toolbar;
    public static TextView select_cnt;
    public com.rey.material.widget.TextView done_lbl;
    GroupFriendListAdapter groupFriendListAdapter;
    private String groupId;
    private OnFragmentInteractionListener mListener;
    private ImageView ivCancel;
    private CreateGroupTask createGroupTask;
    boolean isFromGroupInfo;
    private android.widget.TextView txtNodata;

    public GroupFriendListFragment() {
        // Required empty public constructor
    }
//    public GroupFriendListFragment(boolean isFromGroupInfo) {
//        // Required empty public constructor
//        this.isFromGroupInfo=isFromGroupInfo;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        View view = inflater.inflate(R.layout.fragment_group_friend_list, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(view);
        if (getArguments() != null) {
            groupId = getArguments().getString("group_id", "");
            if (groupId != null && groupId.trim().length() > 0)
                getGroupFriendList(groupId);
        } else
            getFriendList();
        return view;
    }

    private void Init(View view) {
        txtNodata = (TextView) view.findViewById(R.id.txtNodata);
        container_toolbar = (RelativeLayout) view.findViewById(R.id.toolbar_container);
        container_toolbar.setVisibility(View.VISIBLE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        select_cnt = (TextView) view.findViewById(R.id.select_cnt);
        ivCancel = (ImageView) view.findViewById(R.id.ivCancel);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CommonClass.ShowToast(mActivity, "Cancel Call");
//                popFragment();
                if (groupId != null && groupId.trim().length() > 0) {
                    popFragment();
                } else {
                    Intent intent = new Intent(mActivity, NavigationDrawerActivity.class);
                    intent.setPackage(mActivity.getPackageName());
                    intent.putExtra("notification", "7");
//                    CommonClass.setPrefranceByKey_Value(mActivity,"Notification","notify","3");
                    startActivity(intent);
                    //mActivity.finish();
                }

            }
        });
        done_lbl = (com.rey.material.widget.TextView) view.findViewById(R.id.delete_lbl);
        done_lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    Log.e("groupFriendListAdapter;", "done_lbl-------------" );
                    if (groupFriendListAdapter.getCheckedItems() != null && groupFriendListAdapter.getCheckedItems().size() > 0) {
                        if (groupId != null && groupId.trim().length() > 0) {
                            addGroupMember();
                        } else {
                            requestGroupMember();
                        }
                    } else
                        CommonClass.ShowToast(mActivity, "No friend selected.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //  mRecyclerView.setChoiceMode
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Log.i(tag, "keyCode: " + keyCode);

                if (container_toolbar.getVisibility() == View.VISIBLE) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //  Log.i(tag, "onKey Back listener is working!!!");
//                        loadFragmentAgain();
                        Intent intent = new Intent(mActivity, NavigationDrawerActivity.class);
                        intent.setPackage(mActivity.getPackageName());
                        intent.putExtra("notification", "4");
                        startActivity(intent);
//                        getActivity().finish();
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });
    }

    private void setDataInList(ArrayList<FriendDetail> friendDetail) {
        friendDetail = CommonClass.sortFriends(friendDetail);
        if (friendDetail != null && friendDetail.size() > 0) {
            txtNodata.setVisibility(View.GONE);
            groupFriendListAdapter = new GroupFriendListAdapter(mActivity, friendDetail);
            mRecyclerView.setAdapter(groupFriendListAdapter);
        } else {
            txtNodata.setVisibility(View.VISIBLE);
//            CommonClass.ShowToast(getActivity(), "No friends to display.");
        }
    }

    private void getFriendList() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_friend_list, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
//                Log.d("getFriend List Request", "==> " + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private void getGroupFriendList(final String mGroupId) {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.getGroupFrienList, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("group_id", mGroupId);
//                Log.d("getFriend List Request", "==> " + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private void requestGroupMember() {
        ConnectionDetector cd = new ConnectionDetector(mActivity);
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            //    email,password,full_name,contact_no, address,avatar(file field),device_token,device_type
            if (AddGroupFragment.groupImagePath == null || AddGroupFragment.groupImagePath.trim().length() == 0)
                AddGroupFragment.groupImagePath = "";
            createGroupTask = new CreateGroupTask(mResponder, mActivity);
            AsyncUtil.execute(createGroupTask, CommonClass.getCSVFromArrayList(groupFriendListAdapter.getCheckedItems()), AddGroupFragment.groupName, AddGroupFragment.groupImagePath);
        }
    }

    CreateGroupTask.Responder mResponder = new CreateGroupTask.Responder() {

        @Override
        public void onComplete(boolean result, String message) {
            if (result) {
                CommonClass.ShowToast(mActivity, "mResponder " + message);
            } else {
                CommonClass.ShowToast(mActivity, message);
            }
            Intent intent = new Intent(mActivity, NavigationDrawerActivity.class);
            intent.putExtra("notification", "7");
            startActivity(intent);
            // getActivity().finish();
        }
    };

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("Success Listener Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                parseResponse(response);
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

    private void parseResponse(String strResponse) {
        boolean Status;
        String message;
        try {
            JSONObject jresObjectMain = new JSONObject(strResponse);
            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");
            if (Status) {
                String friend_id, friend_image = "", friend_first_name, friend_last_name, friend_lat, friend_email, friend_lng, friend_request;
                JSONArray jresArray = jresObjectMain.getJSONArray("data");
                friendArrayList = new ArrayList<FriendDetail>();
                for (int i = 0; i < jresArray.length(); i++) {

                    JSONObject jsonObject = jresArray.getJSONObject(i);
                    friend_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
                    friend_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                    friend_first_name = CommonClass.getDataFromJson(jsonObject, "first_name");
                    friend_last_name = CommonClass.getDataFromJson(jsonObject, "last_name");
                    if (jsonObject.has("email"))
                        friend_email = CommonClass.getDataFromJson(jsonObject, "email");
                    else
                        friend_email = "";
                    friend_lat = CommonClass.getDataFromJson(jsonObject, "latitude");
                    friend_lng = CommonClass.getDataFromJson(jsonObject, "longitude");
                    if (jsonObject.has("status"))
                        friend_request = CommonClass.getDataFromJson(jsonObject, "status");
                    else
                        friend_request = "";
                    FriendDetail friendDetail = new FriendDetail(friend_id, friend_first_name + " " + friend_last_name, friend_image, friend_email, friend_lat, friend_lng, friend_request);
                    friendArrayList.add(friendDetail);
                }
                setDataInList(friendArrayList);
            } else {
                CommonClass.ShowToast(getActivity(), message);
            }
        } catch (JSONException e) {
            message = getResources().getString(R.string.s_wrong);
            CommonClass.ShowToast(getActivity(), message);
            e.printStackTrace();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //-----------------
    private void addGroupMember() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.add_group_member, addGroupMemberSuccessLisner(),
                addGroupMemberErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(mActivity).user_id);
                requestparam.put("group_id", groupId);
                requestparam.put("friend_id", CommonClass.getCSVFromArrayList(groupFriendListAdapter.getCheckedItems()));
//                Log.e("add_group Request", "==> "+ Constants.remove_group+""+ requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(mActivity);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> addGroupMemberSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        CommonClass.ShowToast(mActivity, message);
                    } else {
                        CommonClass.ShowToast(mActivity, message);
                    }


                    popFragment();
                } catch (JSONException e) {
                    message = mActivity.getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(mActivity, message);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener addGroupMemberErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity);
                CommonClass.ShowToast(mActivity, getResources().getString(R.string.s_wrong));
            }
        };
    }
}
