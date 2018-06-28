package com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapter.ChatHistoryAdapter2;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.ChatActivity;
import com.app.android.cync.R;
import com.customwidget.DividerItemDecoration;
import com.cync.model.ChatMessage;
import com.cync.model.ChatMessageNotification;
import com.cync.model.DataObject;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import com.xmpp.ChatDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ketul.patel on 6/2/16.
 */
public class ChatTabFragment extends BaseContainerFragment {

    private static final String TAG = "ChatTabFragment";
    ArrayList<ChatMessageNotification> lstTmp = new ArrayList<>();
    ArrayList<ChatMessageNotification> lstFinal = new ArrayList<>();
    ArrayList<String> lstFinalIDs = new ArrayList<>();


    RecyclerView chat_recycler_view;
    private RecyclerView.LayoutManager mLayoutManager;

    ChatHistoryAdapter2 mAdapter;
    int finalUnreadcount = 0;
    private RequestQueue mQueue;
    private ArrayList<String> friendArrayList;
    ChatDatabase db;
    ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
    private android.widget.TextView txtNodata;

    public ChatTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_list, container, false);

        chat_recycler_view = (RecyclerView) rootView.findViewById(R.id.recycler_list);
        txtNodata = (android.widget.TextView) rootView.findViewById(R.id.txtNodata);
        chat_recycler_view.setHasFixedSize(true);
        mQueue = VolleySetup.getRequestQueue();
        mLayoutManager = new LinearLayoutManager(getActivity());
        chat_recycler_view.setLayoutManager(mLayoutManager);


        Log.i(TAG, "onCreateView: Current user==" + CommonClass.getUserpreference(getActivity()).user_id);
        db = new ChatDatabase(getActivity());


        mAdapter = new ChatHistoryAdapter2(getActivity(), lstFinal);

        chat_recycler_view.setAdapter(mAdapter);

        chat_recycler_view.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        // getFriendList();

//        lstTmp = db.GetConversationMessage("cync_" + CommonClass.getUserpreference(getActivity()).user_id);
//        lstFinalIDs = db.GetConversationMessageIDS("cync_" + CommonClass.getUserpreference(getActivity()).user_id);
//
//        getFriendList();

        System.out.println("******** ON CREATE VIEW OF CHAT TAB" + lstFinalIDs.size());
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
                    //Do something after 100ms
                    lstTmp = db.GetConversationMessage("cync_" + CommonClass.getUserpreference(getActivity()).user_id);
                    lstFinalIDs = db.GetConversationMessageIDS("cync_" + CommonClass.getUserpreference(getActivity()).user_id);

                    getFriendList();
                }
            }, 100);


        } else {
            // fragment is no longer visible
        }
    }

    private ArrayList<DataObject> getDataSet() {
        ArrayList results = new ArrayList<DataObject>();
        for (int index = 0; index < 20; index++) {
            DataObject obj = new DataObject("Some Primary Text " + index,
                    "Secondary " + index);
            results.add(index, obj);
        }
        return results;
    }

    @Override
    public void onResume() {
        System.out.println("******** ON RESUME VIEW OF CHAT TAB");

        super.onResume();
        ((ChatHistoryAdapter2) mAdapter).setOnItemClickListener(new ChatHistoryAdapter2.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {

//                String id = messages.get(position).getReceiverName();
//                String name = CommonClass.getFriendNameChat(getActivity(), id);
//                String img = CommonClass.getFriendImageChat(getActivity(), id);
//
//
//                Intent i = new Intent(getActivity(), ChatActivity.class);
//                Bundle data = new Bundle();
//
//                data.putString("FROM_MESSGE", id);
//                data.putString("titlename", name);
//                data.putString("profileimage", img);
//                data.putString("type", "1");
//                i.putExtras(data);
//                startActivity(i);

                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("FROM_MESSGE", lstFinal.get(position).chatMessages.receiverName);
                intent.putExtra("titlename", lstFinal.get(position).name);
                intent.putExtra("type", "1");
                intent.putExtra("profileimage", lstFinal.get(position).profileImage);
                startActivity(intent);


            }
        });


    }


    class GetMessages extends AsyncTask<String, Void, String> {

        ArrayList<ChatMessage> chatrecordlist = new ArrayList<ChatMessage>();

        protected void onPreExecute() {
            super.onPreExecute();
            chatrecordlist.clear();
            messages.clear();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("true")) {
                txtNodata.setVisibility(View.GONE);
            } else {
                txtNodata.setVisibility(View.VISIBLE);
//                CommonClass.ShowToast(getActivity(),"No unread messages.");
            }


            Log.i("ChatCount", "==============Count=========" + messages.size());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(String... strings) {
            chatrecordlist.addAll(db.GetUnreadmessageForFixUser(CommonClass.getChatUsername(getActivity()), "", "1"));


            for (int i = 0; i < chatrecordlist.size(); i++) {
                if (friendArrayList.contains(chatrecordlist.get(i).getReceiverName())) {
                    messages.add(chatrecordlist.get(i));


                }
            }


            if (messages.size() > 0) {

                for (int i = 0; i < messages.size(); i++) {


                    String count = messages.get(i).getType();


                    finalUnreadcount = finalUnreadcount + Integer.parseInt(count.substring(1, count.length()));

                }
                Log.i("ChatTab", "================>C==========" + finalUnreadcount);


                final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Collections.sort(messages, new Comparator<ChatMessage>() {
                    public int compare(ChatMessage o1, ChatMessage o2) {
                        try {
                            Date d1 = format.parse(o1.getTimestamp());
                            Date d2 = format.parse(o2.getTimestamp());
                            return d2.compareTo(d1);
                        } catch (ParseException pe) {
                            pe.printStackTrace();
                            return 0;
                        }
                    }
                });
                return "true";
            } else {
                return "false";
            }

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
//                Log.e("getFriendList Request", "==> " + requestparam);
                return requestparam;
            }
        };

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
                Log.d("Success Listener Json", "Friend List ==> " + response);

                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        lstFinal.clear();
                        String friend_id, user_image, first_name, last_name;
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
                        friendArrayList = new ArrayList<String>();
                        for (int i = 0; i < jresArray.length(); i++) {

                            JSONObject jsonObject = jresArray.getJSONObject(i);
                            friend_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
                            user_image = "" + CommonClass.getDataFromJson(jsonObject, "user_image");
                            first_name = "" + CommonClass.getDataFromJson(jsonObject, "first_name");
                            last_name = "" + CommonClass.getDataFromJson(jsonObject, "last_name");
                            System.out.println("************ cync_" + friend_id);

                            if (lstFinalIDs.contains(friend_id)) {

                                int eIndex = lstFinalIDs.indexOf(friend_id);
                                Log.i("ChatNotification", "OnSuccess: eIndex== " + eIndex);

                                ChatMessageNotification ehm = lstTmp.get(eIndex);
                                ehm.profileImage = user_image;
                                ehm.name = (first_name + " " + last_name).trim();
                                lstFinal.add(ehm);

                            }


                            friendArrayList.add("cync_" + friend_id);
                        }


                        Collections.sort(lstFinal, new Comparator<ChatMessageNotification>() {
                            public int compare(ChatMessageNotification obj1, ChatMessageNotification obj2) {
                                // ## Ascending order
                                return obj1.chatMessages.getTimestamp().compareToIgnoreCase(obj2.chatMessages.getTimestamp()); // To compare string values
                                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                // ## Descending order
                                //return obj2.notification_time.compareToIgnoreCase(obj1.notification_time); // To compare string values
                                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                            }
                        });
                        Collections.reverse(lstFinal);


                        if (lstFinal.size() == 0) {
                            txtNodata.setVisibility(View.GONE);
                        } else {
                            txtNodata.setVisibility(View.VISIBLE);
//                CommonClass.ShowToast(getActivity(),"No unread messages.");
                        }

                        mAdapter.notifyDataSetChanged();

//                        GetMessages getMessages = new GetMessages();
//                        getMessages.execute();

                    } else {
                        // CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(getActivity(), message);
                    e.printStackTrace();
                }


                if (lstFinal.size() > 0) {
                    txtNodata.setVisibility(View.GONE);

                } else {
                    txtNodata.setVisibility(View.VISIBLE);
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
                CommonClass.ShowToast(getActivity(), getActivity().getString(R.string.s_wrong));
            }
        };
    }

}
