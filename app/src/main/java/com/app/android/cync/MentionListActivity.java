package com.app.android.cync;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.adapter.FriendListSuggetionAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.cync.model.FriendDetail;
import com.mentions_package.edit.User;
import com.utils.CommonClass;
import com.utils.Constants;
import com.utils.RecyclerItemClickListener;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Thanks on 2016/11/8.
 */

public class MentionListActivity extends Activity {


    private RequestQueue mQueue;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ImageView ivBack;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<FriendDetail> friendArrayList = new ArrayList<>();

    FriendListSuggetionAdapter mFriendListSuggetionAdapter;
    TextView txtNodata;

    EditText edtSearch;
    ArrayList<String> mMentionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_frien_list_suggetion);
        mQueue = VolleySetup.getRequestQueue();

        Intent intent = getIntent();
        mMentionList = CommonClass.getMentionListFromString(MentionListActivity.this, intent.getExtras().getString("mention"));

        InitId();


    }

    private void InitId() {

        edtSearch = (EditText) findViewById(R.id.edtSearch);
        txtNodata = (TextView) findViewById(R.id.txtNodata);
        String mstr = CommonClass.getFriendDetail(MentionListActivity.this);
        friendArrayList = CommonClass.getFriendObjectListFromString(MentionListActivity.this, mstr);

        friendArrayList=CommonClass.sortFriends(friendArrayList);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(MentionListActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriendListSuggetionAdapter = new FriendListSuggetionAdapter(MentionListActivity.this, friendArrayList);
        mRecyclerView.setAdapter(mFriendListSuggetionAdapter);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
            }
        });

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(MentionListActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click


                        FriendDetail mFriend = mFriendListSuggetionAdapter.getFriendDetailList().get(position);
                        User user = new User(mFriend.friendId, mFriend.friendName+" ");


//                        Mention mMention = new Mention(mFriend.friendId, "" + mFriend.friendName);

                        if (mMentionList.indexOf(mFriend.friendId)==-1) {
                            Intent intent = new Intent();
                            intent.putExtra("mention", user);
                            setResult(RESULT_OK, intent);
                            onBackPressed();
                        }else {
                            Toast.makeText(MentionListActivity.this, "This user is already tagged.", Toast.LENGTH_SHORT).show();
                        }

//                        Mention mMention = new Mention(mFriend.friendId, "" + mFriend.friendName);
//
//                        if (mMentionList.indexOf(mMention.relId)==-1) {
//                            Intent intent = new Intent();
//                            intent.putExtra("mention", mMention);
//                            setResult(RESULT_OK, intent);
//                            onBackPressed();
//                        } else {
//                            Toast.makeText(MentionListActivity.this, "This user is already tagged.", Toast.LENGTH_SHORT).show();
//                        }
                    }
                })
        );

        edtSearch = (EditText) findViewById(R.id.edtSearch);

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub


                if (TextUtils.isEmpty(s.toString())) {
                    mFriendListSuggetionAdapter.getFilter().filter("");
                } else {
                    mFriendListSuggetionAdapter.getFilter().filter(s.toString());
                }


            }
        });

    }

    private void getFriendList() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_friend_list, SuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(MentionListActivity.this).user_id);
                Log.e("getFriendList Request", "Request ==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(MentionListActivity.this);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("Success Listener Json", "==> " + response);
                CommonClass.closeLoding(MentionListActivity.this);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        String friend_id, friend_image = "", friend_first_name, friend_last_name, friend_lat, friend_email, friend_lng, friend_request;
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");

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

                            FriendDetail friendDetail = new FriendDetail(friend_id, friend_first_name + " " + friend_last_name, friend_image, friend_email, friend_lat, friend_lng, friend_request);
                            friendArrayList.add(friendDetail);
                        }

                        mFriendListSuggetionAdapter.notifyDataSetChanged();
                    } else {
                        CommonClass.ShowToast(MentionListActivity.this, message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(MentionListActivity.this, message);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CommonClass.closeLoding(MentionListActivity.this);
                CommonClass.ShowToast(MentionListActivity.this, getString(R.string.s_wrong));
            }
        };
    }
}
