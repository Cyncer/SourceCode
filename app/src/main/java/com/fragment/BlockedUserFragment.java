package com.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.BlockedUserAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.BlockedUser;
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
import java.util.List;
import java.util.Map;

import static android.view.View.VISIBLE;


/**
 * Created by zlinux on 09/02/16.
 */
public class BlockedUserFragment extends BaseContainerFragment implements View.OnClickListener, BlockedUserAdapter.UnblockClickListner {


    int unblock_pos;


    Activity mActivity;

    int page = 0;
    private RecyclerView recyclerView;
    private BlockedUserAdapter adapter;
    private List<BlockedUser> mBlockedList;
    private RequestQueue mQueue;
    private RelativeLayout coordinatorLayout;

    LinearLayoutManager mLayoutManager;

    TextView txtNoData;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int edtPos = -1;
    boolean isApiCalling = false;


    BlockedUserAdapter.UnblockClickListner mUnblockClickListner;


    public BlockedUserFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cync_tank, container, false);
        mUnblockClickListner = this;

        InitIds(rootView);

        getData(true);

        return rootView;
    }


    private void InitIds(View v) {
        mQueue = VolleySetup.getRequestQueue();


        coordinatorLayout = (RelativeLayout) v.findViewById(R.id.llMainLayout);

        txtNoData = (TextView) v.findViewById(R.id.txtNoData);
        txtNoData.setVisibility(View.GONE);


        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mBlockedList = new ArrayList<>();

        adapter = new BlockedUserAdapter(mActivity, mBlockedList, mUnblockClickListner);

        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);


        recyclerView.setAdapter(adapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


                if (dy > 0) //check for scroll down
                {


                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {


                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            Log.v("...", "Last Item Wow ! " + loading);
                            adapter.showLoading(true);
                            // adapter.notifyDataSetChanged();
                            //Toast.makeText(getActivity(), "Last Item Wow !", Toast.LENGTH_SHORT).show();
                            getData(false);
                            //Do pagination.. i.e. fetch new data
                        }
                    } else {
                        adapter.showLoading(false);
                        //  adapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }


    private void getData(boolean show) {
        //  swipeContainer.setRefreshing(false);

        CommonClass.closeKeyboard(mActivity);
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);
            if (isApiCalling == false) {
                isApiCalling = true;
                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.blocked_friendsListUrl, SuccessLisner(),
                        mErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();

                        if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                            requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
//                    requestparam.put("latitude",CommonClass.getUserpreference(getActivity()).user_id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);
                        requestparam.put("page", "" + page);
//                        requestparam.put("post_id","2");
//                        requestparam.put("type","like");
                        return requestparam;
                    }

                };


                if (show)
                    showProgress();
                mQueue.add(apiRequest);
            }
        } else {
            if (mBlockedList.size() > 0) {
                txtNoData.setVisibility(View.GONE);
            } else
                txtNoData.setVisibility(VISIBLE);
            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));

        }
    }


    private void showProgress() {

        CommonClass.showLoading(getActivity());

    }

    private void stopProgress() {
        CommonClass.closeLoding(getActivity());
    }


    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "list==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";
                boolean hasNextpage = false;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");

                    hasNextpage = CommonClass.getDataFromJsonBoolean(jresObjectMain, "next");


                    if (page == 0) {
                        mBlockedList.clear();
                    }


                    if (Status) {


                        if (hasNextpage) {
                            page++;

                            loading = true;
                        } else {
                            loading = false;
                        }


                        if (jresObjectMain.has("data")) {


                            JSONArray jData = jresObjectMain.getJSONArray("data");
                            for (int index = 0; index < jData.length(); index++) {
                                JSONObject jsonObject = jData.getJSONObject(index);


                                String user_id, user_name, user_image;


                                user_id = CommonClass.getDataFromJson(jsonObject, "user_id");

                                user_name = CommonClass.getDataFromJson(jsonObject, "user_name");
                                user_image = CommonClass.getDataFromJson(jsonObject, "user_image");


                                mBlockedList.add(new BlockedUser(user_id, user_name, user_image));


                            }


//                            adapter.clear();
//                            // ...the data has come back, add new items to your adapter...
//                            adapter.addAll(mBlockedList);
                            if (loading)
                                adapter.showLoading(true);
                            else
                                adapter.showLoading(false);
                            adapter.notifyDataSetChanged();
                            // Now we call setRefreshing(false) to signal refresh has finished


                        }


                    } else {
                        loading = false;

                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);
                    loading = false;
                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }

                isApiCalling = false;
                if (mBlockedList.size() > 0) {
                    txtNoData.setVisibility(View.GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }


    private com.android.volley.Response.ErrorListener mErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                isApiCalling = false;
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                if (mBlockedList.size() > 0) {
                    txtNoData.setVisibility(View.GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);

                CommonClass.ShowToast(mActivity, error.getMessage());


            }
        };
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
    public void onClick(View view) {


    }


    @Override
    public void onUnblock(final int pos) {
        unblock_pos = pos;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to unblock this user?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.block_userUrl, onBlockSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            requestparam.put("friend_id", "" + mBlockedList.get(pos).user_id);
                            requestparam.put("type", "unblock");
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);


                            return requestparam;
                        }

                    };


                    showProgress();
                    mQueue.add(apiRequest);

                } else {

                    CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


                }

            }
        });
        builder.setNegativeButton("No", null);

        builder.show();


    }


    private com.android.volley.Response.Listener<String> onBlockSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "list==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";
                boolean hasNextpage = false;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        CommonClass.ShowToast(mActivity, message);
                        mBlockedList.remove(unblock_pos);
                        adapter.notifyDataSetChanged();


                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


                if (mBlockedList.size() > 0) {
                    txtNoData.setVisibility(View.GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }
}