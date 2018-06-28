package com.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.adapter.OtherUserListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.rey.material.widget.TextView;
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

/**
 * Created by ketul.patel on 4/2/16.
 */
public class OtherUserListFragment extends BaseContainerFragment {

    public static ArrayList<UserDetail> mUserList;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private OtherUserListAdapter otherUserListAdapter;
    private TextView invite_lbl;
    private ImageView ivOtherBack, friend_search;
    private com.rey.material.widget.EditText search_friend;
    private Activity mActivity;
    private RequestQueue mQueue;
    private android.widget.TextView txtNodata;
    private String search_name = "";

    public OtherUserListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        if(savedInstanceState!=null)
        Bundle bundle = this.getArguments();
        if (bundle != null)
            search_name = bundle.getString("search_name", "");
        else
            search_name = "";
//        Log.e("search_name--","search_name--"+search_name);
        View rootView = inflater.inflate(R.layout.fragment_all_user_list, container, false);
        mQueue = VolleySetup.getRequestQueue();
        Init(rootView);
        getAllUserList();
        return rootView;
    }

    public void Init(View view) {
        txtNodata = (android.widget.TextView) view.findViewById(R.id.txtNodata);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        search_friend = (com.rey.material.widget.EditText) view.findViewById(R.id.search_friend);
        search_friend.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("") || s.toString().length() == 0) {
//                    otherUserListAdapter = new OtherUserListAdapter(mActivity, mUserList);
//                    mRecyclerView.setAdapter(otherUserListAdapter);
                    //setDataInList(mUserList);
                    setDataInList(new ArrayList<UserDetail>());
                    txtNodata.setVisibility(View.GONE);
                }

            }

        });
        invite_lbl = (TextView) view.findViewById(R.id.invite_lbl);
        invite_lbl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new InviteFriendFragment(), true, "InviteFriendFragment");
            }
        });
        ivOtherBack = (ImageView) view.findViewById(R.id.ivOtherBack);
        ivOtherBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
//                CommonClass.ShowToast(mActivity,"Back Call.");
            }
        });
        friend_search = (ImageView) view.findViewById(R.id.friend_search);
        friend_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strSearch = search_friend.getText().toString().trim();
//                performSearch(strSearch);
                if (performSearch(strSearch) != null) {
//                    Log.e("setOnClickListener", "" + performSearch(strSearch).size());

                    ArrayList<UserDetail> mtemp = performSearch(strSearch);

                    if (mtemp.size() < mUserList.size())
                        setDataInList(performSearch(strSearch));

//                    if(performSearch(strSearch).size()>0) {
//                        txtNodata.setVisibility(View.GONE);
//                        otherUserListAdapter = new OtherUserListAdapter(mActivity, performSearch(strSearch));
//                        mRecyclerView.setAdapter(otherUserListAdapter);
//                    }else{
//                        txtNodata.setVisibility(View.VISIBLE);
//                    }
                }
            }
        });
        search_friend.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(android.widget.TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String strSearch = search_friend.getText().toString().trim();
//                    performSearch(strSearch);
                    if (performSearch(strSearch) != null) {
//                        Log.e("setOn", "" + performSearch(strSearch).size());

                        ArrayList<UserDetail> mtemp = performSearch(strSearch);

                        Log.i("OtherUserListFragment", "=====> Search size=" + mtemp.size());
                        Log.i("OtherUserListFragment", "=====> mUserList size=" + mUserList.size());

                        if (mtemp.size() < mUserList.size())
                            setDataInList(performSearch(strSearch));
//                        if(performSearch(strSearch).size()>0) {
//                            txtNodata.setVisibility(View.GONE);
//                            otherUserListAdapter = new OtherUserListAdapter(mActivity, performSearch(strSearch));
//                            mRecyclerView.setAdapter(otherUserListAdapter);
//                        }else{
//                            txtNodata.setVisibility(View.VISIBLE);
//                        }
                    }
                    return true;
                }

                return false;
            }
        });
//            view.setFocusableInTouchMode(true);
//            view.requestFocus();
//            view.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    Log.e("Key Back Press", "keyCode: " + keyCode);
//
//                        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                            //  Log.i(tag, "onKey Back listener is working!!!");
//                           popFragment();
//                            return true;
//                        } else {
//                            return false;
//                        }
//                }
//            });
    }

    @Override
    public void onResume() {
        super.onResume();
        String temp = search_name;
        if (temp != null && temp.length() > 0)
            search_friend.setText(temp);
//        Log.e("if--","search_name--"+search_name);
    }

    @Override
    public void onStart() {
        super.onStart();
        String temp = search_name;
        if (temp != null && temp.length() > 0)
            search_friend.setText(temp);
//        Log.e("if--","search_name--"+search_name);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String temp = search_name;
        if (temp != null && temp.length() > 0)
            search_friend.setText(temp);
//        Log.e("if--","search_name--"+search_name);
    }

    private void setEditText() {

    }

    private ArrayList<UserDetail> performSearch(String query) {


        ArrayList<UserDetail> moviesFiltered = new ArrayList<UserDetail>();

        if (query != null) {
            if (mUserList != null && mUserList.size() > 0) {
                for (UserDetail g : mUserList) {


                    if ((g.first_name.replace(" ", "").toLowerCase() + g.first_name.replace(" ", "").toLowerCase()).contains(query.toString().toLowerCase().replace(" ", "")))
                        moviesFiltered.add(g);
                }
            }

        }
//        // First we split the query so that we're able
//        // to search word by word (in lower case).
//        String[] queryByWords = query.toLowerCase().split("\\s+");
//
//        // Empty list to fill with matches.
//        ArrayList<UserDetail> moviesFiltered = new ArrayList<UserDetail>();
//
//
//
//        // Go through initial releases and perform search.
//        for (UserDetail movie : mUserList) {
//
//            // Content to search through (in lower case).
//            String content = (
//                    movie.first_name +" "+movie.last_name
//            ).toLowerCase();
//
//            for (String word : queryByWords) {
//
//                Log.i("OtherUserListFragment", "======>" + word);
//
//                // There is a match only if all of the words are contained.
//                int numberOfMatches = queryByWords.length;
//
//                // All query words have to be contained,
//                // otherwise the release is filtered out.
//                if (content.contains(word)) {
//                    numberOfMatches--;
//                } else {
//                    break;
//                }
//                // They all match.
//                if (numberOfMatches == 0) {
//                    moviesFiltered.add(movie);
//                }
//            }
//        }
        return moviesFiltered;
    }

    private void getAllUserList() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.getAllUsers, getAllUserSuccessListener(),
                    getAllUserErrorListener()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {

                    HashMap<String, String> requestparam = new HashMap<>();
                    requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                    Log.e("getAllUserList", "==> " + Constants.getAllUsers + requestparam);
                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


        }
    }

    private com.android.volley.Response.Listener<String> getAllUserSuccessListener() {
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
                        String user_id, user_image = "", user_name, user_email, status;
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
                        mUserList = new ArrayList<UserDetail>();
                        for (int i = 0; i < jresArray.length(); i++) {
                            JSONObject jsonObject = jresArray.getJSONObject(i);
                            user_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
                            user_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                            String strLastname = "";
                            if (CommonClass.getDataFromJson(jsonObject, "last_name") != null && CommonClass.getDataFromJson(jsonObject, "last_name").length() > 0)
                                strLastname = CommonClass.getDataFromJson(jsonObject, "last_name");
                            user_name = CommonClass.getDataFromJson(jsonObject, "first_name") + " " + strLastname;
                            user_email = CommonClass.getDataFromJson(jsonObject, "user_email");
//                            status= jsonObject.getString("status");
                            status = CommonClass.getDataFromJson(jsonObject, "status");
//                            Log.e("status", "-------" + jsonObject.getString("status"));


                            UserDetail userDetail = new UserDetail(user_id, user_name, user_image, user_email, status);
                            if (user_email.trim().length() == 0) {

                                userDetail.login_type = "facebook";
                            } else {
                                userDetail.login_type = "";

                            }
                            mUserList.add(userDetail);
                        }


                        if (search_name.trim().length() > 0) {
                            setDataInList(mUserList);
                            setDataInList(performSearch(search_name));
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
        };
    }

    private com.android.volley.Response.ErrorListener getAllUserErrorListener() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(getActivity());
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
    }

    private void setDataInList(ArrayList<UserDetail> mDetail) {
        mDetail = CommonClass.sortUserDetails(mDetail);
        if (mDetail != null && mDetail.size() > 0) {
            txtNodata.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            otherUserListAdapter = new OtherUserListAdapter(mActivity, mDetail);
            mRecyclerView.setAdapter(otherUserListAdapter);
        } else {
            txtNodata.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
//            CommonClass.ShowToast(getActivity(), "No user to display.");
        }
    }


}
