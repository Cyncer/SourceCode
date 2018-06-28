package com.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.FriendListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.cync.model.FriendDetail;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import com.xmpp.ChatDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FriendListFragment extends BaseContainerFragment {

    private Activity mActivity;
    private RequestQueue mQueue;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    //    private List<FriendListDetail> groupListDetail;
    private ArrayList<FriendDetail> friendArrayList;
    public static MenuItem addfriendIcon, deleteAllmenuLbl;
    RelativeLayout container_toolbar;
    public static com.rey.material.widget.CheckBox checkAll;
    public static boolean checkAllCheckbox;
    public static TextView select_cnt;
    public com.rey.material.widget.TextView delete_lbl;
    FriendListAdapter friendListAdapter;
    private android.widget.TextView txtNodata;
    private OnFragmentInteractionListener mListener;


    public FriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        System.out.println(" APPLYING PATCH WORK..........");

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        mQueue = VolleySetup.getRequestQueue();

        Init(view);
        getFriendList();
        return view;
    }

    private void Init(View view) {
        checkAllCheckbox = false;
        txtNodata = (TextView) view.findViewById(R.id.txtNodata);
        container_toolbar = (RelativeLayout) view.findViewById(R.id.toolbar_container);
        container_toolbar.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        checkAll = (com.rey.material.widget.CheckBox) view.findViewById(R.id.chkAll);
        select_cnt = (TextView) view.findViewById(R.id.select_cnt);
        delete_lbl = (com.rey.material.widget.TextView) view.findViewById(R.id.delete_lbl);
        delete_lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("groupFriendListAdapter;", "" + friendListAdapter.getCheckedItems());
//                CommonClass.ShowToast(mActivity, "" + groupFriendListAdapter.getCheckedItems());
                if (friendListAdapter.getCheckedItems() != null && friendListAdapter.getCheckedItems().size() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure?")
                            .setMessage("You want to delete friend from list ?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    String strFriendId=CommonClass.getCSVFromArrayList(friendListAdapter.getCheckedItems());
//                                    if (strFriendId.endsWith(","))
//                                        strFriendId = strFriendId.substring(0, strFriendId.length()-1);
//                                    Log.e("Friend-------",strFriendId);
                                    deleteFriends();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else
                    CommonClass.ShowToast(mActivity, "None of the friends selected to delete.");
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
                        loadFragmentAgain();
                        return true;
                    } else {
                        return false;
                    }
                }
                return false;
            }
        });

    }

    public void loadFragmentAgain() {
        try {
            FriendListFragment.checkAllCheckbox = false;
            container_toolbar.setVisibility(View.GONE);
            NavigationDrawerActivity.mToolbar.setVisibility(View.VISIBLE);
            friendListAdapter.notifyDataSetChanged();
            FriendListFragment friendListFragment = new FriendListFragment();
            replaceFragment(friendListFragment, false, "friendListFragment");
//                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDataInList(ArrayList<FriendDetail> friendDetail) {
        friendDetail = CommonClass.sortFriends(friendDetail);
        if (friendDetail != null && friendDetail.size() > 0) {
            txtNodata.setVisibility(View.GONE);
            friendListAdapter = new FriendListAdapter(mActivity, friendDetail, checkAll);
            mRecyclerView.setAdapter(friendListAdapter);
        } else {
            txtNodata.setVisibility(View.VISIBLE);
//            CommonClass.ShowToast(getActivity(), "No friends to display.");
        }

        System.out.println("About to start service####################");

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
                    Log.e("getFriendList Request", "Request ==> " + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    String strFriendId = "";

    private void deleteFriends() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.deleteFriends, deleteSuccessLisner(),
                deleteErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Log.e("groupFriendListAdapter;", "" + Constants.deleteFriends+""+friendListAdapter.getCheckedItems());
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                strFriendId = CommonClass.getCSVFromArrayList(friendListAdapter.getCheckedItems());
                if (strFriendId.endsWith(","))
                    strFriendId = strFriendId.substring(0, strFriendId.length() - 1);
                requestparam.put("friend_id", strFriendId);
                requestparam.put("status", "delete");
                Log.e("getFriend List Request", "==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(getActivity());
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> deleteSuccessLisner() {
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
                        if (friendListAdapter.getCheckedItems() != null) {
                            ChatDatabase chatDatabase = ChatDatabase.getInstance(getActivity());
                            for (int i = 0; i < friendListAdapter.getCheckedItems().size(); i++) {
                                chatDatabase.deleteHistoryRecord(CommonClass.getChatUsername(getActivity()), "cync_" + friendListAdapter.getCheckedItems().get(i));
                            }
                        }
                        CommonClass.ShowToast(getActivity(), message);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(getActivity(), message);
                    e.printStackTrace();
                }
                loadFragmentAgain();
            }
        };
    }

    private com.android.volley.Response.ErrorListener deleteErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), mActivity.getString(R.string.s_wrong));
            }
        };
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("Success Listener Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
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
                            friend_email = CommonClass.getDataFromJson(jsonObject, "email");
                            friend_lat = CommonClass.getDataFromJson(jsonObject, "latitude");
                            friend_lng = CommonClass.getDataFromJson(jsonObject, "longitude");
                            friend_request = CommonClass.getDataFromJson(jsonObject, "status");

                            friend_image = friend_image.replace("\\/", "/");
                            FriendDetail friendDetail = new FriendDetail(friend_id, friend_first_name + " " + friend_last_name, friend_image, friend_email, friend_lat, friend_lng, friend_request);

                            if (friendDetail.friendEmailId.trim().length() == 0) {

                                friendDetail.type = "facebook";
                            } else {
                                friendDetail.type = "";

                            }

                            friendArrayList.add(friendDetail);
                        }
                        setDataInList(friendArrayList);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(getActivity(), message);
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
                CommonClass.ShowToast(getActivity(), mActivity.getString(R.string.s_wrong));
            }
        };
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
        inflater.inflate(R.menu.menu_friendlist, menu);
        addfriendIcon = menu.findItem(R.id.action_friend);
        addfriendIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                OtherUserListFragment fragment = new OtherUserListFragment();
                if (fragment != null)
                    replaceFragment(fragment, true, "OtherUser");
                return false;
            }
        });

        deleteAllmenuLbl = menu.findItem(R.id.action_settings);
        deleteAllmenuLbl.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (friendArrayList != null && friendArrayList.size() > 0) {
                    txtNodata.setVisibility(View.GONE);
                    if (container_toolbar.getVisibility() == View.GONE) {
                        container_toolbar.setVisibility(View.VISIBLE);
                        NavigationDrawerActivity.mToolbar.setVisibility(View.GONE);
                        checkAllCheckbox = true;
                        if (friendListAdapter != null)
                            friendListAdapter.notifyDataSetChanged();
                    } else {
                        container_toolbar.setVisibility(View.GONE);
                        NavigationDrawerActivity.mToolbar.setVisibility(View.VISIBLE);
                        checkAllCheckbox = false;
                    }
                } else {
                    txtNodata.setVisibility(View.VISIBLE);
//                        CommonClass.ShowToast(mActivity, "No results found.");
                }
                return false;
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
