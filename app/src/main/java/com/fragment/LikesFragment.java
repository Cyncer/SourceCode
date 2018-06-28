package com.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adapter.LikesAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.CyncTankDetailsActiivty;
import com.app.android.cync.R;
import com.cync.model.CyncTank;
import com.cync.model.Likes;
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
public class LikesFragment extends BaseContainerFragment implements View.OnClickListener {


    Activity mActivity;

    int page = 0;
    private RecyclerView recyclerView;
    private LikesAdapter adapter;
    private List<Likes> likeList;
    private RequestQueue mQueue;
    private RelativeLayout coordinatorLayout;

    LinearLayoutManager mLayoutManager;

    TextView txtNoData;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int edtPos = -1;
    boolean isApiCalling = false;
    int total_comment = 0, total_like = 0;

    CyncTank mCyncTank;


    public LikesFragment() {
        // Required empty public constructor
    }

    public LikesFragment(CyncTank mCyncTank) {
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
            }, 0);


        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_likes, container, false);


        InitIds(rootView);


        return rootView;
    }


    private void InitIds(View v) {
        mQueue = VolleySetup.getRequestQueue();


        coordinatorLayout = (RelativeLayout) v.findViewById(R.id.llMainLayout);

        txtNoData = (TextView) v.findViewById(R.id.txtNoData);
        txtNoData.setVisibility(View.GONE);


        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        likeList = new ArrayList<>();

        adapter = new LikesAdapter(mActivity, likeList);

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

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

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
                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.like_commentUrl, SuccessLisner(),
                        mErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();

                        if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                            requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
//                    requestparam.put("latitude",CommonClass.getUserpreference(getActivity()).user_id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);
                        requestparam.put("page", "" + page);
                        requestparam.put("post_id", "" + mCyncTank.id);
                        requestparam.put("type", "like");
                        return requestparam;
                    }

                };


                if (show)
                    showProgress();
                mQueue.add(apiRequest);
            }
        } else {
            if (likeList.size() > 0) {
                txtNoData.setVisibility(View.GONE);
            } else
                txtNoData.setVisibility(VISIBLE);

        }
    }


    private void showProgress() {

        //CommonClass.showLoading(getActivity());

    }

    private void stopProgress() {
      //  CommonClass.closeLoding(getActivity());
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
                        likeList.clear();
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


                        if (jresObjectMain.has("like")) {


                            JSONArray jData = jresObjectMain.getJSONArray("like");
                            for (int index = 0; index < jData.length(); index++) {
                                JSONObject jsonObject = jData.getJSONObject(index);


                                String id, post_id, user_id, type, text, date_added, user_name, user_image, ago;


                                id = CommonClass.getDataFromJson(jsonObject, "id");
                                post_id = CommonClass.getDataFromJson(jsonObject, "post_id");
                                user_id = CommonClass.getDataFromJson(jsonObject, "user_id");
                                type = CommonClass.getDataFromJson(jsonObject, "type");
                                text = CommonClass.getDataFromJson(jsonObject, "text");
                                date_added = CommonClass.getDataFromJson(jsonObject, "date_added");
                                user_name = CommonClass.getDataFromJson(jsonObject, "user_name");
                                user_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                                ago = CommonClass.getDataFromJson(jsonObject, "ago");


                                likeList.add(new Likes(id, post_id, user_id, type, text, date_added, user_name, user_image, ago));


                            }


                            ((CyncTankDetailsActiivty) getActivity()).updateCount(total_comment, total_like);


//                            adapter.clear();
//                            // ...the data has come back, add new items to your adapter...
//                            adapter.addAll(likeList);
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
                if (likeList.size() > 0) {
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
                if (likeList.size() > 0) {
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


}