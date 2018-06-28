package com.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.NavigationDrawerAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.cync.model.ChatMessage;
import com.cync.model.ChatMessageNotification;
import com.cync.model.FriendDetail;
import com.cync.model.NavDrawerItem;
import com.cync.model.UserDetail;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import com.xmpp.ChatDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class FragmentDrawer extends Fragment implements NavigationDrawerAdapter.OnClickEvent {

    private static String TAG = FragmentDrawer.class.getSimpleName();

    private RecyclerView recyclerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerAdapter adapter;
    private View containerView;
    private static String[] titles = null;
    private FragmentDrawerListener drawerListener;
    ImageView ivUserIcon;
    TextView tvUsername;
    private ArrayList<FriendDetail> friendArrayList;
    ArrayList<ChatMessageNotification> lstTmp = new ArrayList<>();
    ArrayList<ChatMessageNotification> lstFinal = new ArrayList<>();
    ArrayList<String> lstFinalIDs = new ArrayList<>();
    public NavigationDrawerAdapter.OnClickEvent mOnClickEvent;

    ChatDatabase db;
    ArrayList<ChatMessage> messages = new ArrayList<ChatMessage>();
    public static int msg_count = 0;

    private RequestQueue mQueue;

    public FragmentDrawer() {

    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    public static List<NavDrawerItem> getData(int a) {
        List<NavDrawerItem> data = new ArrayList<NavDrawerItem>();
        // preparing navigation drawer items
        for (int i = 0; i < titles.length; i++) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(titles[i]);
            navItem.count = 0;

            if (a == i) {
                System.out.println("let a = " + i);
                System.out.println("let i = " + i);
                navItem.setShowNotify(true);
            } else
                navItem.setShowNotify(false);
            data.add(navItem);
        }
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOnClickEvent=this;
        // drawer labels
        titles = getActivity().getResources().getStringArray(R.array.nav_drawer_labels);
        db = new ChatDatabase(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflating view layout

        View layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        init(layout);
        setAdapterData(1);
//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                drawerListener.onDrawerItemSelected(view, position);
//                mDrawerLayout.closeDrawer(containerView);
//
//                // if(position!=5)
//                setAdapterData(position);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));

        return layout;
    }

    private void init(View view) {
        mQueue = VolleySetup.getRequestQueue();
        ivUserIcon = (ImageView) view.findViewById(R.id.ivUserIcon);
        tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        recyclerView = (RecyclerView) view.findViewById(R.id.drawerList);
    }

    public void setAdapterData(int selectedIndex) {
        setDrawerImage();
        adapter = new NavigationDrawerAdapter(getActivity(), getData(selectedIndex),mOnClickEvent);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }


    public void setDrawerImage() {
        UserDetail userDetail = CommonClass.getUserpreference(getActivity());

        if (userDetail != null) {
            String imagePath = userDetail.user_image;
            if (userDetail.first_name != null && userDetail.first_name.length() > 0) {
                String _name = userDetail.first_name;
//                _name = CommonClass.strEncodeDecode(userDetail.first_name,true);
                tvUsername.setText(userDetail.first_name);
            }


            Log.e("Picasso", "Image Path Final---------- " + imagePath);


            Log.e("setDrawerImage", "setDrawerImage " + imagePath);

            if (imagePath != null && imagePath.length() > 0) {
                Log.e("setDrawerImage", "setDrawerImage " + imagePath);
                if (imagePath.startsWith("http")) {
                    imagePath = imagePath.trim();
                    Log.e("FragmentDrawer", "IF---------- " + imagePath);
                } else {
                    Log.e("FragmentDrawer", "kk ELSE---------- " + Constants.imagBaseUrl + imagePath);
                    imagePath = Constants.imagBaseUrl + imagePath.trim();
                }
                //Log.e("Picasso", "Image Path Final---------- " + imagePath);
                if (imagePath.trim().length() > 0) {
                    Picasso.with(getActivity())
                            .load(imagePath)
                            .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                            .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                            .into(ivUserIcon);
                } else
                    ivUserIcon.setImageResource(R.drawable.no_image);
            } else
                ivUserIcon.setImageResource(R.drawable.no_image);

        }
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        getNotificationList();
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setDrawerImage();
                getActivity().invalidateOptionsMenu();
//                CommonClass.ShowToast(getActivity(), "Calculating count.... ");
                NavigationDrawerActivity.isDrawerMenuOpen = false;

//                messages.clear();
//                messages = db.GetUnreadmessageForFixUser(CommonClass.getChatUsername(getActivity()), "", "1");
//                msg_count = 0;
//                for (int i = 0; i < messages.size(); i++) {
//                    String count = messages.get(i).getType();
//                    msg_count = msg_count + Integer.parseInt(count.substring(1, count.length()));
//                }
//                if (countList != null && countList.size() > 0)
//                    msg_count = msg_count + countList.size();
//                adapter.getItem(8).count = 0;
//
//                adapter.notifyDataSetChanged();
//             if(adapter.tvNotification!=null)
//                adapter.tvNotification.setText(msg_count);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                CommonClass.closeKeyboard(getActivity());
                getActivity().invalidateOptionsMenu();
                NavigationDrawerActivity.isDrawerMenuOpen = true;
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                CommonClass.closeKeyboard(getActivity());
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        NavigationDrawerActivity.mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    //drawer is open

                } else {
//                    mDrawerToggle.syncState();

                    lstTmp = db.GetConversationMessage("cync_" + CommonClass.getUserpreference(getActivity()).user_id);
                    lstFinalIDs = db.GetConversationMessageIDS("cync_" + CommonClass.getUserpreference(getActivity()).user_id);

                    getNotificationList();
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    @Override
    public void onMainClick(View view,int position) {
        drawerListener.onDrawerItemSelected(view, position);
        mDrawerLayout.closeDrawer(containerView);

        // if(position!=5)
        setAdapterData(position);

    }

    @Override
    public void onInfoClick(int pos) {
        Toast.makeText(getActivity(), "Position  "+pos, Toast.LENGTH_SHORT).show();
    }

    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }


    }

    public interface FragmentDrawerListener {
        public void onDrawerItemSelected(View view, int position);
    }

    //--------------------------
    ArrayList<String> countList = new ArrayList<>();

    private void getNotificationList() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_friend_list, SuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
//                Log.e("getNotificationList", "Request =>" + Constants.gernalNotification + " " + requestparam);
                return requestparam;
            }
        };
//    CommonClass.showLoading(getActivity());
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> SuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("Success Listener Json", "==> " + response);
//                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                int Ncount = 0;
                int cIntCount = 0;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");

                    String ct = CommonClass.getDataFromJson(jresObjectMain, "count_notification");

                    try {

                        Ncount = Integer.parseInt(ct);
                    } catch (NumberFormatException e) {
                        Ncount = 0;
                    }


                    if (Status) {


                        String friend_id, friend_image = "", friend_first_name, friend_last_name, friend_lat, friend_email, friend_lng, friend_request;
                        friendArrayList = new ArrayList<FriendDetail>();
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
                        cIntCount = 0;
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

                            friend_image=friend_image.replace("\\/","/");
                            FriendDetail friendDetail = new FriendDetail(friend_id, friend_first_name + " " + friend_last_name, friend_image, friend_email, friend_lat, friend_lng, friend_request);

                            if (friendDetail.friendEmailId.trim().length() == 0){

                                friendDetail.type = "facebook";}
                            else{
                                friendDetail.type = "";

                            }

                            friendArrayList.add(friendDetail);




                            if (lstFinalIDs.contains(friend_id)) {

                                int eIndex = lstFinalIDs.indexOf(friend_id);
                                Log.i("ChatNotification", "OnSuccess: eIndex== " + eIndex);

                                String cCount = lstTmp.get(eIndex).count;
                                try {
                                    cIntCount = cIntCount + Integer.parseInt(cCount);
                                } catch (NumberFormatException e) {

                                }


                            }


                        }

                        String mstr=CommonClass.getFriendStringFromObject(getActivity(),friendArrayList);
                        CommonClass.setFriendDetail(getActivity(),mstr);


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


                    } else {
                        // CommonClass.ShowToast(getActivity(), message);
                    }


                    Ncount = Ncount + cIntCount;

                    Log.i(TAG, "onResponse: Count==="+Ncount);
                    adapter.getItem(8).count = Ncount;

                    adapter.notifyDataSetChanged();
                    for (int c = 0; c < Ncount; c++) {
                        countList.add("" + c);
                    }

//                    if (Status) {
//
//
//                        String isread;
//                        String notification_id, request_id, notification_name, notification_type, notification_image, notification_status, group_admin_id_group_admin_name, group_admin_image, notification_time;
//                        JSONArray jresArray = jresObjectMain.getJSONArray("data");
//                        countList = new ArrayList<String>();
//
//                        for (int i = 0; i < jresArray.length(); i++) {
//
//                            JSONObject jsonObject = jresArray.getJSONObject(i);
//                            isread = "" + CommonClass.getDataFromJson(jsonObject, "isread");
//
//                            if(isread.equalsIgnoreCase("0"))
//                            {
//                                countList.add("" + i);
//                            }
//
//                        }
//
//
//                        JSONArray jresGroupArray = jresObjectMain.getJSONArray("group_data");
//                        for (int i = 0; i < jresGroupArray.length(); i++) {
//
//                            JSONObject jsonObject = jresGroupArray.getJSONObject(i);
//                            isread = "" + CommonClass.getDataFromJson(jsonObject, "isread");
//                            if(isread.equalsIgnoreCase("0"))
//                            {
//                                countList.add("" + i);
//                            }
//                        }
//
//
//                        JSONArray jresPostArray = jresObjectMain.getJSONArray("post_notification");
//                        for (int i = 0; i < jresPostArray.length(); i++) {
//
//                            JSONObject jsonObject = jresPostArray.getJSONObject(i);
//                            isread = "" + CommonClass.getDataFromJson(jsonObject, "isread");
//                            if(isread.equalsIgnoreCase("0"))
//                            {
//                                countList.add("" + i);
//                            }
//                        }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////                        JSONArray jPostArray = jresObjectMain.getJSONArray("post_notification");
////                        for (int i = 0; i < jPostArray.length(); i++) {
////                            countList.add("" + i);
////                        }
//
//
////                        Log.i("Count Notification", "-----" + countList.size());
//                    } else {
////                        CommonClass.ShowToast(getActivity(), message);
//                    }
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
//                    CommonClass.ShowToast(getActivity(), message);
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
//                CommonClass.closeLoding(getActivity());
//                CommonClass.ShowToast(getActivity(), getActivity().getString(R.string.s_wrong));
            }
        };
    }
}