package com.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.adapter.MakeYearModelAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.customwidget.RecyclerViewHeader;
import com.cync.model.AssistData;

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

import static com.app.android.cync.R.id.textView;

/**
 * Created by ketul.patel on 8/1/16.
 */
public class AssistDetailFragment extends BaseContainerFragment {

    FrameLayout fmLayout;
    ArrayList<AssistData> mAssistDataList = new ArrayList<>();
    MakeYearModelAdapter mAdapter;
    TextView txtName, txtAddress, txtContactNumber, txtEmail, txtDesc,txtCategory;
    String assist_id = "";
    ArrayAdapter mAdapterMake, mAdapterModel, mAdapterYear;
    private RecyclerViewHeader recyclerHeader;
    private RecyclerView recycler;
    private RequestQueue mQueue;
    private Activity mActivity;
    private ImageView ivBack;

    public AssistDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assist_id = getArguments().getString("assist_id");
        View rootView = inflater.inflate(R.layout.fragment_assist_detail, container, false);
        mQueue = VolleySetup.getRequestQueue();


        Init(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    public void Init(View v) {


        fmLayout = (FrameLayout) v.findViewById(R.id.fmLayout);
        ivBack = (ImageView) v.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popFragment();
            }
        });
        recycler = (RecyclerView) v.findViewById(R.id.recycler_view);


        txtName = (TextView) v.findViewById(R.id.txtName);
        txtCategory = (TextView) v.findViewById(R.id.txtCategory);
        txtAddress = (TextView) v.findViewById(R.id.txtAddress);
        txtContactNumber = (TextView) v.findViewById(R.id.txtContactNumber);
        txtEmail = (TextView) v.findViewById(R.id.txtEmail);
        txtDesc = (TextView) v.findViewById(R.id.txtDesc);
        v.setFocusableInTouchMode(true);
        v.requestFocus();

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    // handle back button's click listener
                    popFragment();
                    return true;
                }
                return false;
            }
        });



        txtContactNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String phone = txtContactNumber.getText().toString();
                if(phone.length()>0) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            }
        });


        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = txtEmail.getText().toString();
                if(email.length()>0) {


                    Intent intent=new Intent(Intent.ACTION_SEND);
                    String[] recipients={email};
                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT,"Cync");

                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Send mail"));


                }
            }
        });

/*
        recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        recyclerHeader = (RecyclerViewHeader) v.findViewById(R.id.header);
        recyclerHeader.attachTo(recycler);
        mAdapter = new MakeYearModelAdapter(getActivity(), mAssistDataList);
        recycler.setAdapter(mAdapter);*/


        getDetail();


    }

    private void getDetail() {
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            Log.i("AssistDetailFragment","URL====>"+Constants.assistDetailUrl + "" + assist_id+"&user_id="+CommonClass.getUserpreference(getActivity()).user_id);
            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.GET, Constants.assistDetailUrl + "" + assist_id+"&user_id="+CommonClass.getUserpreference(getActivity()).user_id, mSuccessLisner(),
                    ErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    return requestparam;
                }
            };
            CommonClass.showLoading(getActivity());
            mQueue.add(apiRequest);
        }
    }

    private com.android.volley.Response.Listener<String> mSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {

                        if (jresObjectMain.has("result")) {
                            JSONObject resultObj = jresObjectMain.getJSONObject("result");


                            String mechanical_name,mechanic_category, address, contact_number, email,desc;
                            mechanical_name = CommonClass.getDataFromJson(resultObj, "mechanical_name");
                            address = CommonClass.getDataFromJson(resultObj, "address");
                            contact_number = CommonClass.getDataFromJson(resultObj, "contact_number");
                            email = CommonClass.getDataFromJson(resultObj, "email");
                            desc= CommonClass.getDataFromJson(resultObj, "description");
                            mechanic_category= CommonClass.getDataFromJson(resultObj, "mechanic_category");


                            txtName.setText(mechanical_name);
                            txtCategory.setText(mechanic_category);
                            txtAddress.setText(address);
                            String styledTextContactNumber = " <font color='#0000ff'><u>"+contact_number+"</u></font>";
                            txtContactNumber.setText(Html.fromHtml(styledTextContactNumber), TextView.BufferType.SPANNABLE);
                            String styledTextemail = " <font color='#0000ff'><u>"+email+"</u></font>";
                            txtEmail.setText(Html.fromHtml(styledTextemail), TextView.BufferType.SPANNABLE);




                            //txtContactNumber.setText(contact_number);
                           // txtEmail.setText(email);
                            txtDesc.setText(desc);


                            Object json = resultObj.get("make_details");
                            if (json instanceof JSONArray) {

                                JSONArray makeArray = resultObj.getJSONArray("make_details");

                                for (int i = 0; i < makeArray.length(); i++) {
                                    JSONObject mMini = makeArray.getJSONObject(i);

                                    String make_name, year, model;
                                    make_name = CommonClass.getDataFromJson(mMini, "make_name");
                                    year = CommonClass.getDataFromJson(mMini, "year");
                                    model = CommonClass.getDataFromJson(mMini, "model");


                                    mAssistDataList.add(new AssistData(make_name, year, model));
                                }

                               // mAdapter.notifyDataSetChanged();
                            }

                        }


                    } else {
                        CommonClass.ShowToast(getActivity(), message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
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
    public void onDetach() {

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onDetach();
    }


}