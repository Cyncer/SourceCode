package com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.GenralNotificationTabAdapter;
import com.adapter.GenralNotificationTabAdapter.SetFragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.CyncTankDetailsActiivty;
import com.app.android.cync.R;
import com.cync.model.Notification;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.utils.RecyclerItemClickListener;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by ketul.patel on 6/2/16.
 */
public class GeneralTabFragment extends BaseContainerFragment implements SetFragment {
    private static final int OPEN_DETAIL = 3;
    private Activity mActivity;
    private RequestQueue mQueue;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public static android.widget.TextView txtNodata;
    //    private List<FriendListDetail> groupListDetail;
    private ArrayList<Notification> notificationArrayList;
    GenralNotificationTabAdapter mAdapter;

    public GeneralTabFragment() {
        // Required empty public constructor
    }

    public static MenuItem menuClear;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_notification, menu);
        menuClear = menu.findItem(R.id.action_clear);
        menuClear.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                new AlertDialog.Builder(getActivity())
                        .setMessage("Do you want to clear all notifications?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                sendRequestLogout();
                                deleteNotifications();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return false;
            }
        });


    }


    private void deleteNotifications() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.clear_Notification, deleteSuccessLisner(),
                    deleteErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
//                Log.e("groupFriendListAdapter;", "" + Constants.deleteFriends+""+friendListAdapter.getCheckedItems());
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("timezone", "" + TimeZone.getDefault().getID());

                    Log.e("deleteNotifications  Request", "==> " + requestparam);
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
                        getNotificationList();
                        CommonClass.ShowToast(getActivity(), message);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(getActivity(), message);
                    e.printStackTrace();
                }

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recycler_list, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(view);

        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // load data here

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getNotificationList();
                }
            }, 100);


        } else {
            // fragment is no longer visible
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == OPEN_DETAIL && resultCode == getActivity().RESULT_OK && data != null) {

            Log.i("GeneralTabFragment", "onActivityResult: Hey");
            boolean isChange = data.getBooleanExtra("isChange", false);


            if (isChange) {


                getNotificationList();


            }

        }


    }

    private void Init(View view) {
        txtNodata = (TextView) view.findViewById(R.id.txtNodata);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click


                        if (mAdapter.getAllData().get(position).notification_type.equalsIgnoreCase("new_post")) {

                            Intent intent = new Intent(getActivity(), CyncTankDetailsActiivty.class);
                            intent.putExtra("from", "notification");
                            intent.putExtra("post_id", "" + mAdapter.getAllData().get(position).group_admin_id_);
                            intent.putExtra("type", "new_post");
                            intent.putExtra("notification_id", "" + mAdapter.getAllData().get(position).notification_id);
                            startActivityForResult(intent, OPEN_DETAIL);

                        }
                    }
                })
        );


    }

    private void getNotificationList() {

        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.gernalNotification, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("timezone", "" + TimeZone.getDefault().getID());
                    Log.e("getNotificationList", "Request =>" + Constants.gernalNotification + " " + requestparam);
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
                Log.d("Success Listener Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        String notification_id, request_id, notification_name, notification_type, notification_image, notification_status, group_admin_id_group_admin_name, group_admin_image, notification_time, created_at, isread;
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
                        notificationArrayList = new ArrayList<Notification>();
                        for (int i = 0; i < jresArray.length(); i++) {

                            JSONObject jsonObject = jresArray.getJSONObject(i);
                            notification_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "friend_id");
                            notification_image = CommonClass.getDataFromJson(jsonObject, "friend_image");
                            notification_name = CommonClass.getDataFromJson(jsonObject, "friend_name");
                            isread = CommonClass.getDataFromJson(jsonObject, "isread");
                            notification_type = "Friend";
                            notification_status = CommonClass.getDataFromJson(jsonObject, "status");
                            notification_time = CommonClass.getDataFromJson(jsonObject, "notification_time");
                            request_id = CommonClass.getDataFromJson(jsonObject, "request_id");
                            created_at = CommonClass.getDataFromJson(jsonObject, "created_at");
                            Notification notification = new Notification(notification_id, notification_name, notification_type, notification_image, notification_status, notification_time, request_id, created_at);
                            notification.read = isread;
                            notificationArrayList.add(notification);
                        }

                        String admin_id, admin_name;
                        JSONArray jresGroupArray = jresObjectMain.getJSONArray("group_data");
                        for (int i = 0; i < jresGroupArray.length(); i++) {

                            JSONObject jsonObject = jresGroupArray.getJSONObject(i);
                            notification_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "group_id");
                            notification_image = CommonClass.getDataFromJson(jsonObject, "group_image");
                            notification_name = CommonClass.getDataFromJson(jsonObject, "group_name");
                            notification_type = "Group";
                            isread = CommonClass.getDataFromJson(jsonObject, "isread");
                            notification_status = CommonClass.getDataFromJson(jsonObject, "status");
                            notification_time = CommonClass.getDataFromJson(jsonObject, "notification_time");
                            admin_id = CommonClass.getDataFromJson(jsonObject, "admin_id");
                            admin_name = CommonClass.getDataFromJson(jsonObject, "admin_name");
                            request_id = CommonClass.getDataFromJson(jsonObject, "request_id");
                            created_at = CommonClass.getDataFromJson(jsonObject, "created_at");
                            Notification notification = new Notification(notification_id, notification_name, notification_type, notification_image, notification_status, notification_time, admin_id, admin_name, request_id, created_at);
                            notification.read = isread;
                            notificationArrayList.add(notification);
                        }


                        JSONArray jresPostArray = jresObjectMain.getJSONArray("post_notification");
                        for (int i = 0; i < jresPostArray.length(); i++) {

                            JSONObject jsonObject = jresPostArray.getJSONObject(i);
                            notification_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "id");
                            notification_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                            notification_name = CommonClass.getDataFromJson(jsonObject, "user_name");
                            notification_type = "new_post";
                            isread = CommonClass.getDataFromJson(jsonObject, "isread");
                            notification_status = CommonClass.getDataFromJson(jsonObject, "notification_message");
                            notification_time = CommonClass.getDataFromJson(jsonObject, "notification_time");
                            admin_id = CommonClass.getDataFromJson(jsonObject, "type_id");
                            admin_name = CommonClass.getDataFromJson(jsonObject, "admin_name");
                            request_id = CommonClass.getDataFromJson(jsonObject, "request_id");
                            created_at = CommonClass.getDataFromJson(jsonObject, "created_at");
                            Notification notification = new Notification(notification_id, notification_name, notification_type, notification_image, notification_status, notification_time, admin_id, admin_name, request_id, created_at);
                            notification.read = isread;
                            notificationArrayList.add(notification);
                        }


//                        Collections.sort(notificationArrayList, new Comparator<Notification>(){
//                            public int compare(Notification obj1, Notification obj2) {
//                                // ## Ascending order
//                               //return obj1.created_at.compareToIgnoreCase(obj2.created_at); // To compare string values
//                                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values
//
//                                // ## Descending order
//                                  return obj2.created_at.compareToIgnoreCase(obj1.created_at); // To compare string values
//                                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
//                            }
//                        });

                        // Collections.reverse(notificationArrayList);


                        setDataInList(notificationArrayList);
                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
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

    @Override
    public void onResume() {
        super.onResume();

    }

    private void setDataInList(ArrayList<Notification> notifyDetail) {
        try {
            Collections.sort(notifyDetail, new CustomComparator());
            Collections.reverse(notifyDetail);
            if (notifyDetail != null && notifyDetail.size() > 0) {
                txtNodata.setVisibility(View.GONE);
                if (mRecyclerView != null) {
                    mAdapter = new GenralNotificationTabAdapter(mActivity, notifyDetail);
                    mRecyclerView.setAdapter(mAdapter);
                }
            } else {
                notificationArrayList.clear();
                txtNodata.setVisibility(View.VISIBLE);
                mAdapter = new GenralNotificationTabAdapter(mActivity, notifyDetail);
                mRecyclerView.setAdapter(mAdapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public void setText(ArrayList<Notification> mNotifications) {
        if (txtNodata != null)
            setDataInList(mNotifications);
        else
            System.out.println("------------------------GeneralTabFragment.setText");
    }


    public class CustomComparator implements Comparator<Notification> {
        @Override
        public int compare(Notification o1, Notification o2) {
            return o1.created_at.compareToIgnoreCase(o2.created_at);// && o1.notification_name.compareToIgnoreCase(o2.notification_name);
        }
    }

}
