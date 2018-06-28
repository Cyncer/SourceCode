package com.fragment;

import android.app.Activity;
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

import com.adapter.GroupListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.cync.model.GroupListItem;
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


public class GroupListFragment extends BaseContainerFragment {

    private Activity mActivity;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<GroupListItem> groupListDetail;
    public static MenuItem addfriendIcon, deleteAllmenuLbl;

    public static com.rey.material.widget.CheckBox checkAll;
    public static TextView select_cnt;
    public com.rey.material.widget.TextView delete_lbl;
    private OnFragmentInteractionListener mListener;
    public static boolean checkAllCheckbox;
    public static boolean checkedAll = false;
    private GroupListAdapter groupListAdapter;
    private RelativeLayout container_toolbar;
    private android.widget.TextView txtNodata;
    private String groupId = "";
//    private boolean argumentsRead;

    public GroupListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            groupId = getArguments().getString("group_id", "");
        setHasOptionsMenu(true);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

//        Log.e("getArguments--", "getArguments--------" + groupId);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        View view = inflater.inflate(R.layout.fragment_friend_list, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(view);
        if (groupId != null && groupId.trim().length() > 0) {
//
        } else {
//        Log.e("GroupList-------",groupId);
            getGroupList();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (groupId != null && groupId.trim().length() > 0) {
            Bundle bundle = new Bundle();
            bundle.putString("group_id", groupId);
//            Log.e("GroupList-------",groupId);
            GroupInfoFragment fragment = new GroupInfoFragment();
            fragment.setArguments(bundle);
            replaceFragment(fragment, true, "GroupInfo");
            groupId = null;
            // setArguments(null);
        }
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
                if (groupListAdapter.getCheckedItems() != null && groupListAdapter.getCheckedItems().size() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure?")
                            .setMessage("You want to delete Group from list ?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
//                                    Log.e("groupFriendListAdapter;", "" + groupListAdapter.getCheckedItems());
                                    deleteGroups();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else
                    CommonClass.ShowToast(mActivity, "None of the groups selected to delete.");
            }
        });
        //  mRecyclerView.setChoiceMode
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
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
            GroupListFragment.checkAllCheckbox = false;
            container_toolbar.setVisibility(View.GONE);
            NavigationDrawerActivity.mToolbar.setVisibility(View.VISIBLE);
            groupListAdapter.notifyDataSetChanged();
            GroupListFragment friendListFragment = new GroupListFragment();
            replaceFragment(friendListFragment, false, "groupListFragment");
//                        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDataInList(ArrayList<GroupListItem> groupDetail) {
        groupDetail = CommonClass.sortGroup(groupDetail);
        if (groupDetail != null && groupDetail.size() > 0) {
            txtNodata.setVisibility(View.GONE);
            groupListAdapter = new GroupListAdapter(this, groupDetail, checkAll);
            mRecyclerView.setAdapter(groupListAdapter);
        } else {
            txtNodata.setVisibility(View.VISIBLE);
//            CommonClass.ShowToast(getActivity(), "No groups to display.");
        }
    }

    private void getGroupList() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_group_list, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    Log.d("getGroupList Request", "==> " + Constants.get_group_list + "" + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private void deleteGroups() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.remove_group, deleteSuccessLisner(),
                    deleteErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    String strGroupId = CommonClass.getCSVFromArrayList(groupListAdapter.getCheckedItems());
                    if (strGroupId.endsWith(","))
                        strGroupId = strGroupId.substring(0, strGroupId.length() - 1);
                    requestparam.put("group_id", strGroupId);
//                requestparam.put("status", "delete");
                    Log.e("deleteGroups Request", "==> " + Constants.remove_group + "" + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
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
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
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
                        String group_id, group_image = "", group_name;
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
                        groupListDetail = new ArrayList<GroupListItem>();
                        for (int i = 0; i < jresArray.length(); i++) {

                            JSONObject jsonObject = jresArray.getJSONObject(i);
                            group_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "group_id");
                            group_image = CommonClass.getDataFromJson(jsonObject, "group_image");
                            group_name = CommonClass.getDataFromJson(jsonObject, "group_name");

                            GroupListItem groupListDetail1 = new GroupListItem(group_id, group_name, group_image);
                            groupListDetail.add(groupListDetail1);
                        }
                        setDataInList(groupListDetail);
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
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_friendlist, menu);
        addfriendIcon = menu.findItem(R.id.action_friend);
        addfriendIcon.setIcon(R.drawable.group_add_user);
        addfriendIcon.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AddGroupFragment fragment = new AddGroupFragment();
                if (fragment != null)
                    replaceFragment(fragment, true, "Add Group");
                return false;
            }
        });
        deleteAllmenuLbl = menu.findItem(R.id.action_settings);
        deleteAllmenuLbl.setTitle("Delete group");
        deleteAllmenuLbl.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (groupListDetail != null && groupListDetail.size() > 0) {
                    if (container_toolbar.getVisibility() == View.GONE) {
                        container_toolbar.setVisibility(View.VISIBLE);
                        NavigationDrawerActivity.mToolbar.setVisibility(View.GONE);
                        checkAllCheckbox = true;
                        groupListAdapter.notifyDataSetChanged();
                    } else {
                        container_toolbar.setVisibility(View.GONE);
                        NavigationDrawerActivity.mToolbar.setVisibility(View.VISIBLE);
                        checkAllCheckbox = false;
                    }
                    txtNodata.setVisibility(View.GONE);
                } else {
//                    CommonClass.ShowToast(mActivity, "No results found.");
                    txtNodata.setVisibility(View.VISIBLE);
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
