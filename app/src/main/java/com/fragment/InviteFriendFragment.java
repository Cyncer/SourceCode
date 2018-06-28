package com.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.adapter.InviteListAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.NavigationDrawerActivity;
import com.app.android.cync.R;
import com.rey.material.widget.Button;
import com.rey.material.widget.EditText;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.utils.TextValidator;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class InviteFriendFragment extends BaseContainerFragment {
    //    TextView txtLabel;
    private  Activity mActivity;
    EditText etEmailAddress;
    private ImageView ivBack,imageViewProfile;
    private Button btnInvite,btnAdd;
    public  String emailAddress;
    private RequestQueue mQueue;
    ArrayList<String> strEmail;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private InviteListAdapter inviteListAdapter;
    public InviteFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        mQueue = VolleySetup.getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.invite_fragment, container, false);
        Init(rootView);
        return rootView;
    }

    public void Init(View view) {
        strEmail=new ArrayList<>();

        etEmailAddress=(EditText) view.findViewById(R.id.etEmailAddress);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, NavigationDrawerActivity.class);
                intent.putExtra("notification", "5");
                startActivity(intent);
                //mActivity.finish();
            }
        });
        btnInvite=(Button) view.findViewById(R.id.btnInvite);
        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inviteListAdapter!=null) {
                    if (inviteListAdapter.getCheckedItems() != null && inviteListAdapter.getCheckedItems().size() > 0) {
                        String strEmails = CommonClass.getCSVFromArrayList(inviteListAdapter.getCheckedItems());
                        if (strEmails != null && strEmails.trim().length() > 0) {
                            getInviteFriendList(strEmails);
                        } else
                            CommonClass.ShowToast(mActivity, "Please add email address.");
                    } else
                        CommonClass.ShowToast(mActivity, "Please add email address.");
                } else
                    CommonClass.ShowToast(mActivity, "Please add email address.");
            }
        });
        btnAdd=(Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAddress=etEmailAddress.getText().toString().trim();
                boolean flagDuplicat=true;
                if(emailAddress!=null&& emailAddress.trim().length()>0) {
                    boolean valid = ValidationTask(emailAddress);
                    if(valid) {
                        if(strEmail!=null&&strEmail.size()>0)
                            flagDuplicat= checkInList(emailAddress);
                        if(flagDuplicat) {
                            etEmailAddress.setText("");
                            strEmail.add(emailAddress);
                            if (strEmail != null ) {
                                inviteListAdapter = new InviteListAdapter(mActivity, strEmail);
                                mRecyclerView.setAdapter(inviteListAdapter);
                            }
                        }else
                            CommonClass.ShowToast(mActivity,"Email address already added.");
                    }
                }else
                    CommonClass.ShowToast(mActivity,"Please enter email address.");

            }
        });
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
//                    // handle back button's click listener
//                    Intent intent = new Intent(mActivity, NavigationDrawerActivity.class);
//                    intent.putExtra("notification", "2");
//                    startActivity(intent);
//                    mActivity.finish();
//                    return true;
//                }
//                return false;
//            }
//        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

                return false;

            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    mRecyclerView.setNestedScrollingEnabled(false);
                } else {
                    mRecyclerView.setNestedScrollingEnabled(true);
                }
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

    }

    private boolean checkInList(String stremail){
        for (String strElement:strEmail) {
            if(strElement.equalsIgnoreCase(stremail)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity=activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void getInviteFriendList(final String mEmail) {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.inviteFriend, SuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(getActivity()).user_id);
                requestparam.put("emails",mEmail);
//                Log.d("getFriend List Request", "==> " + requestparam);
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
//                Log.e("Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                    }
                    Intent intent = new Intent(mActivity, NavigationDrawerActivity.class);
                    intent.putExtra("notification", "5");
                    startActivity(intent);
                    //mActivity.finish();
                } catch (JSONException e) {
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();
                }
                CommonClass.ShowToast(getActivity(), message);
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity);
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.s_wrong));
            }
        };
    }

    private boolean ValidationTask(String uemailid2) {
        if (!TextUtils.isEmpty(uemailid2)) {

            if (TextValidator.isAValidEmail(uemailid2)) {
                String[] domain = {".aero", ".asia", ".biz", ".cat", ".com", ".coop", ".edu", ".gov", ".info", ".int", ".jobs", ".mil", ".mobi", ".museum", ".name", ".net", ".org", ".pro", ".tel", ".travel", ".ac", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am", ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au", ".aw", ".ax", ".az", ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm", ".bn", ".bo", ".br", ".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca", ".cc", ".cd", ".cf", ".cg", ".ch", ".ci", ".ck", ".cl", ".cm", ".cn", ".co", ".cr", ".cu", ".cv", ".cx", ".cy", ".cz", ".de", ".dj", ".dk", ".dm", ".do", ".dz", ".ec", ".ee", ".eg", ".er", ".es", ".et", ".eu", ".fi", ".fj", ".fk", ".fm", ".fo", ".fr", ".ga", ".gb", ".gd", ".ge", ".gf", ".gg", ".gh", ".gi", ".gl", ".gm", ".gn", ".gp", ".gq", ".gr", ".gs",
                        ".gt", ".gu", ".gw", ".gy", ".hk", ".hm", ".hn", ".hr", ".ht", ".hu", ".id", ".ie", " No", ".il", ".im", ".in", ".io", ".iq", ".ir", ".is", ".it", ".je", ".jm", ".jo", ".jp", ".ke", ".kg", ".kh", ".ki", ".km", ".kn", ".kp", ".kr", ".kw", ".ky", ".kz", ".la", ".lb", ".lc", ".li", ".lk", ".lr", ".ls", ".lt", ".lu", ".lv", ".ly", ".ma", ".mc", ".md", ".me", ".mg", ".mh", ".mk", ".ml", ".mm", ".mn", ".mo", ".mp", ".mq", ".mr", ".ms", ".mt", ".mu", ".mv", ".mw", ".mx", ".my", ".mz", ".na", ".nc", ".ne", ".nf", ".ng", ".ni", ".nl", ".no", ".np", ".nr", ".nu", ".nz", ".om", ".pa", ".pe", ".pf", ".pg", ".ph", ".pk", ".pl", ".pm", ".pn", ".pr", ".ps", ".pt", ".pw", ".py", ".qa", ".re", ".ro", ".rs", ".ru", ".rw", ".sa", ".sb", ".sc", ".sd", ".se", ".sg", ".sh", ".si", ".sj", ".sk", ".sl", ".sm", ".sn", ".so", ".sr", ".st", ".su", ".sv", ".sy", ".sz", ".tc", ".td", ".tf", ".tg", ".th", ".tj", ".tk", ".tl", ".tm", ".tn", ".to", ".tp", ".tr", ".tt", ".tv", ".tw", ".tz", ".ua", ".ug", ".uk", ".us", ".uy", ".uz", ".va", ".vc", ".ve", ".vg", ".vi", ".vn", ".vu", ".wf", ".ws", ".ye", ".yt", ".za", ".zm", ".zw"};
                List<String> list = Arrays.asList(domain);

                String tmp = uemailid2.substring(uemailid2.lastIndexOf("."), uemailid2.length());
                if (list.contains("" + tmp)) {
                    return true;
                } else {
                    CommonClass.ShowToast(mActivity,"Please enter valid email address.");
                }
            } else {
                CommonClass.ShowToast(mActivity,"Please enter valid email address.");
            }
        } else {
            CommonClass.ShowToast(mActivity,"Please enter email address.");
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}