package com.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.ChatNotificationAdapter;
import com.adapter.RecyclerItemClickListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.ChatMessageNotification;
import com.cync.model.FriendDetail;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import com.xmpp.ChatDatabase;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatNotificationFragment extends Fragment implements View.OnClickListener, ChatNotificationAdapter.OnItemClick {
    private static final int OPEN_CHAT = 3;

    ChatNotificationAdapter.OnItemClick mOnItemClick;
    ProgressDialog loginDialog;
    Activity mActivity;

    int page = 1;
    private RecyclerView recyclerView;
    private ChatNotificationAdapter adapter;
    private List<FriendDetail> mMatchList;
    private RelativeLayout coordinatorLayout;
    private SwipeRefreshLayout swipeContainer;
    LinearLayoutManager mLayoutManager;
    TextView txtNoData;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int edtPos = -1;
    boolean isApiCalling = false;
    LinearLayout btnDeleteLayout;
    Button btnCancel, btnDelete;
    boolean isDeletable = false;
    static ChatDatabase db;
    ArrayList<ChatMessageNotification> lstTmp = new ArrayList<>();
    ArrayList<ChatMessageNotification> lstFinal = new ArrayList<>();
    ArrayList<String> lstFinalIDs = new ArrayList<>();
    private RequestQueue mQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_chat_notifications, container, false);
        db = new ChatDatabase(getActivity());

        InitIds(v);


        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                page = 1;
                getData(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.black,
                R.color.black,
                R.color.black,
                R.color.black);


        return v;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //   CommonClass.isMain = true;
            getData(true);
        } else {

            // CommonClass.isMain = false;
        }
    }


//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.favourite: {
//                Intent intent = new Intent(getActivity(), FavouriteActivity.class);
//                startActivityForResult(intent, OPEN_FAV);
//                getActivity().overridePendingTransition(R.anim.fb_slide_in_from_right, R.anim.fb_forward);
//                return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }


    private void InitIds(View v) {

        mQueue = VolleySetup.getRequestQueue();

        coordinatorLayout = (RelativeLayout) v.findViewById(R.id.coordinatorLayout);
        txtNoData = (TextView) v.findViewById(R.id.txtNoData);

        btnDeleteLayout = (LinearLayout) v.findViewById(R.id.btnDeleteLayout);
        btnCancel = (Button) v.findViewById(R.id.btnCancel);
        btnDelete = (Button) v.findViewById(R.id.btnDelete);
        btnCancel.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        txtNoData.setVisibility(View.GONE);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        mOnItemClick = this;
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mMatchList = new ArrayList<>();

        adapter = new ChatNotificationAdapter(mActivity, lstFinal, mOnItemClick, "my FriendDetail");

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


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click


//                        Intent intent = new Intent(getActivity(),
//                                FinalChatActivity.class);
//                        intent.putExtra("TO_ID", "" + adapter.getItem(position).chatMessages.user_id);
//
//                        intent.putExtra("TO_NAME", "" + uDetail.user_id);
//                        intent.putExtra("titlename", "" + uDetail.first_name+" "+uDetail.last_name);
//                        intent.putExtra("type", "dating");
//                        startActivity(intent);
//                        getActivity().overridePendingTransition(R.anim.fb_slide_in_from_right, R.anim.fb_forward);


//                        Intent intent = new Intent(getActivity(), FinalChatActivity.class);
//                        intent.putExtra("FROM_MESSGE", lstFinal.get(position).chatMessages.receiverName);
//                        intent.putExtra("titlename", lstFinal.get(position).chatMessages.name);
//                        intent.putExtra("type", lstFinal.get(position).chatMessages.type);
//                       // intent.putExtra("profileimage", uDetail.profile_image_url);
//                        startActivityForResult(intent,OPEN_CHAT);
//                        getActivity().overridePendingTransition(R.anim.fb_slide_in_from_right, R.anim.fb_forward);


                    }
                })
        );


    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        System.out.println("mActivity===" + mActivity);
    }


    private void getData(boolean show) {
        //  swipeContainer.setRefreshing(false);
//        lstTmp = db.GetConversationMessage(Constants.App_Name + CommonClass.getUserpreference(getActivity()).user_id);
//        lstFinalIDs = db.GetConversationMessageIDS(Constants.App_Name + CommonClass.getUserpreference(getActivity()).user_id);

        CommonClass.closeKeyboard(mActivity);
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_friend_list, SuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
//                Log.e("getFriendList Request", "==> " + requestparam);
                    return requestparam;
                }
            };

            mQueue.add(apiRequest);

        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }

    }

    @Override
    public void onClick(View v) {


    }


    private void showProgress() {

        if (loginDialog == null) {
            loginDialog = new ProgressDialog(getActivity());
            loginDialog.setCancelable(false);
        }

        try {
            loginDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void stopProgress() {
        if (loginDialog != null && loginDialog.isShowing())
            loginDialog.dismiss();
    }


    @Override
    public void onItemClick(int pos) {

//        String data = FriendDetail.getStringFromObject(getActivity(), mMatchList.get(pos));
//        Intent i = new Intent(getActivity(), OfferDetailActivity.class);
//        i.putExtra("data", data);
//        startActivityForResult(i, OPEN_DETAIL);
//        getActivity().overridePendingTransition(R.anim.fb_slide_in_from_right, R.anim.fb_forward);


    }

    @Override
    public void onFavouriteClick(int pos) {

        edtPos = pos;


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == OPEN_CHAT && resultCode == getActivity().RESULT_OK && data != null) {

            boolean isChange = data.getBooleanExtra("isChange", false);


            if (isChange) {

                page = 1;
                getData(true);


            }

        }


    }


    public void refrehsList(Chat chat, Message message) {


        Log.i("ChatNotification", "refrehsList: Chat Notification Fragment");
        page = 1;
        getData(true);


    }


    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("Success Listener Json", "==> " + response);

//                boolean Status;
//                String message;
//                try
//                {
//                    JSONObject jresObjectMain = new JSONObject(response);
//                    Status = jresObjectMain.getBoolean("status");
//                    message = jresObjectMain.getString("message");
//                    if (Status)
//                    {
//                        String friend_id;
//                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
//                        friendArrayList = new ArrayList<String>();
//                        for (int i = 0; i < jresArray.length(); i++) {
//
//                            JSONObject jsonObject = jresArray.getJSONObject(i);
//                            friend_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
//                            System.out.println("************ cync_"+friend_id);
//                            friendArrayList.add("cync_"+friend_id);
//                        }
//
//                        GetMessages getMessages = new GetMessages();
//                        getMessages.execute();
//
//                    }
//                    else
//                    {
//                        // CommonClass.ShowToast(getActivity(), message);
//                    }
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    message = getResources().getString(R.string.s_wrong);
//                    CommonClass.ShowToast(getActivity(), message);
//                    e.printStackTrace();
//                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getActivity().getString(R.string.s_wrong));
            }
        };
    }

}


