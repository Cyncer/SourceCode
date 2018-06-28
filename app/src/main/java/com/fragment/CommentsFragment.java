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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.CommentAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.CyncTankDetailsActiivty;
import com.app.android.cync.OtherUserProfileActivity;
import com.app.android.cync.R;
import com.cync.model.Comments;
import com.cync.model.CyncTank;
import com.cync.model.Tags;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by zlinux on 09/02/16.
 */
public class CommentsFragment extends BaseContainerFragment implements View.OnClickListener, CommentAdapter.OnItemClick {


    Activity mActivity;

    private static final int OPEN_OTHER_PROFILE = 12355;
    int delete_id;
    int report_id;

    int page = 0;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comments> commentList;
    private RequestQueue mQueue;
    private RelativeLayout coordinatorLayout;

    int total_comment = 0, total_like = 0;
    LinearLayoutManager mLayoutManager;

    TextView txtNoData;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int edtPos = -1;
    boolean isApiCalling = false;

    CyncTank mCyncTank=null;

    CommentAdapter.OnItemClick mOnItemClick;

    public CommentsFragment() {
        // Required empty public constructor
    }

    public CommentsFragment(CyncTank mCyncTank) {
        super();
        this.mCyncTank = mCyncTank;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    getData(true);
                }
            }, 150);


        }
    }


    public void ReloadData() {


        // commentList.get(edtPos).


        page = 0;
        getData(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        mOnItemClick = this;
        InitIds(rootView);


        return rootView;
    }


    private void InitIds(View v) {
        mQueue = VolleySetup.getRequestQueue();


        coordinatorLayout = (RelativeLayout) v.findViewById(R.id.llMainLayout);

        txtNoData = (TextView) v.findViewById(R.id.txtNoData);
        txtNoData.setVisibility(View.GONE);


        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        commentList = new ArrayList<>();

        adapter = new CommentAdapter(mActivity, commentList, mOnItemClick);

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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        // ((CyncTankDetailsActiivty) getActivity()).showKeyboard();

    }


    private void getData(boolean show) {
        //  swipeContainer.setRefreshing(false);

        CommonClass.closeKeyboard(mActivity);
        ConnectionDetector cd = new ConnectionDetector(mActivity);
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected ) {



            if (isApiCalling == false && mCyncTank!=null) {
                isApiCalling = true;
                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.like_commentUrl, SuccessLisner(),
                        mErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();

                        if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                            requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                        requestparam.put("post_id", "" + mCyncTank.id);
                        requestparam.put("type", "comment");
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);
                        requestparam.put("page", "" + page);

                        return requestparam;
                    }

                };


                if (show)
                    showProgress();
                mQueue.add(apiRequest);
            }
        } else {
            if (commentList.size() > 0) {
                txtNoData.setVisibility(View.GONE);
            } else
                txtNoData.setVisibility(VISIBLE);

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
                    JSONObject jresObjectFinal = new JSONObject(response);

                    Status = jresObjectFinal.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectFinal, "message");

                    hasNextpage = CommonClass.getDataFromJsonBoolean(jresObjectFinal, "next");


                    if (page == 0) {
                        commentList.clear();
                    }


                    if (Status) {


                        if (hasNextpage) {
                            page++;

                            loading = true;
                        } else {
                            loading = false;
                        }

                        JSONObject jresObjectMain = jresObjectFinal.getJSONObject("data");

                        try {
                            total_comment = Integer.parseInt(CommonClass.getDataFromJson(jresObjectFinal, "total_comment"));
                        } catch (NumberFormatException e) {
                            total_comment = 0;
                        }


                        try {
                            total_like = Integer.parseInt(CommonClass.getDataFromJson(jresObjectFinal, "total_like"));
                        } catch (NumberFormatException e) {
                            total_like = 0;
                        }


                        if (jresObjectMain.has("comment")) {


                            JSONArray jData = jresObjectMain.getJSONArray("comment");
                            for (int index = 0; index < jData.length(); index++) {
                                JSONObject jsonObject = jData.getJSONObject(index);


                                String id, post_id, user_id, type, text, date_added, user_name, user_image, ago, post_by;
                                ArrayList<Tags> tagList = new ArrayList<>();

                                id = CommonClass.getDataFromJson(jsonObject, "id");
                                post_id = CommonClass.getDataFromJson(jsonObject, "post_id");
                                user_id = CommonClass.getDataFromJson(jsonObject, "user_id");
                                type = CommonClass.getDataFromJson(jsonObject, "type");
                                text = CommonClass.getDataFromJson(jsonObject, "text");
                                date_added = CommonClass.getDataFromJson(jsonObject, "date_added");
                                user_name = CommonClass.getDataFromJson(jsonObject, "user_name");
                                user_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                                ago = CommonClass.getDataFromJson(jsonObject, "ago");
                                post_by = CommonClass.getDataFromJson(jsonObject, "created_by");

                                if (jsonObject.has("tag_friend")) {


                                    Object json = jsonObject.get("tag_friend");
                                    if (json instanceof JSONArray) {


                                        JSONArray tag_friendArray = jsonObject.getJSONArray("tag_friend");
                                        for (int t = 0; t < tag_friendArray.length(); t++) {
                                            String tid, tname;
                                            int startIndex, length;
                                            JSONObject jsubTag = tag_friendArray.getJSONObject(t);

                                            tid = "" + CommonClass.getDataFromJson(jsubTag, "id");
                                            tname = "" + CommonClass.getDataFromJson(jsubTag, "name");
                                            startIndex = CommonClass.getDataFromJsonInt(jsubTag, "startIndex");

                                            if (jsubTag.has("length"))
                                                length = CommonClass.getDataFromJsonInt(jsubTag, "length");
                                            else
                                                length = CommonClass.getDataFromJsonInt(jsubTag, "lenth");

                                            tagList.add(new Tags(tid, tname, startIndex, length));
                                        }

                                    }


                                }

                                commentList.add(new Comments(id, post_id, user_id, type, text, date_added, user_name, user_image, ago, post_by, tagList));


                            }

                            adapter.notifyDataSetChanged();


//                            adapter.clear();
//                            // ...the data has come back, add new items to your adapter...
//                            adapter.addAll(commentList);
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


                ((CyncTankDetailsActiivty) getActivity()).updateCount(total_comment, total_like);


                isApiCalling = false;
                if (commentList.size() > 0) {
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
                if (commentList.size() > 0) {
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
    public void onReportClick(final int pos) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to report this comment?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                report_id = pos;

                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.report_commentUrl, onReportSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            //requestparam.put("post_id", "" + mPostList.get(pos).id);
                            requestparam.put("comment_id", "" + commentList.get(pos).id);
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


    @Override
    public void onDeleteClick(final int pos) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to delete this comment?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                delete_id = pos;

                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.deletecommentUrl, onDeleteSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            //requestparam.put("post_id", "" + mPostList.get(pos).id);
                            requestparam.put("comment_id", "" + commentList.get(pos).id);
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

    boolean isClick = false;

    @Override
    public void onProfileClick(int cpos, int tpos) {


        if (!isClick) {

            Intent intent = new Intent(getActivity(), OtherUserProfileActivity.class);
            intent.putExtra("user_id", "" + adapter.getAll().get(cpos).tagList.get(tpos).id);
            startActivityForResult(intent, OPEN_OTHER_PROFILE);
            getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            isClick = true;
        }
    }


    @Override
    public void onEditClick(int pos) {


        ((CyncTankDetailsActiivty) getActivity()).editComment(commentList.get(pos));

    }


    private com.android.volley.Response.Listener<String> onDeleteSuccessLisner() {
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


                        commentList.remove(delete_id);
                        adapter.notifyDataSetChanged();

                        ((CyncTankDetailsActiivty) getActivity()).updateTabTitleOnDelete();

                        CommonClass.ShowToast(mActivity, message);
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


                if (commentList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }

    private com.android.volley.Response.Listener<String> onReportSuccessLisner() {
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


                if (commentList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_OTHER_PROFILE && resultCode == getActivity().RESULT_OK && data != null) {


            isClick = false;


        }
    }
}