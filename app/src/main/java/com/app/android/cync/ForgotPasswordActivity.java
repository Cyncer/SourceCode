package com.app.android.cync;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.customwidget.materialEditText;
import com.rey.material.widget.Button;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.utils.TextValidator;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ketul.patel on 18/1/16.
 */
public class ForgotPasswordActivity extends Activity {

    materialEditText rg_email;
    Button submit_btn;
    private ImageView ivBack;
    private RequestQueue mQueue;
    String ride_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        mQueue = VolleySetup.getRequestQueue();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {


            if(bundle.containsKey("ride_id"))
            {
                ride_id = bundle.getString("ride_id", "");
            }
        }
        init();
    }

    private void init() {
        rg_email = (materialEditText) findViewById(R.id.rg_email);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = ValidationTask(rg_email.getText().toString().trim());
                if (valid) {
//                    Intent i = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
//                    startActivity(i);

                    ConnectionDetector cd = new ConnectionDetector(ForgotPasswordActivity.this);
                    boolean isConnected = cd.isConnectingToInternet();
                    if (isConnected) {
                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.forgotpasswordUrl, loginSuccessLisner(),
                            loginErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();
                            requestparam.put("email_address", rg_email.getText().toString());
                            return requestparam;
                        }
                    };
                    CommonClass.showLoading(ForgotPasswordActivity.this);
                    mQueue.add(apiRequest);
                    } else {

                        CommonClass.ShowToast(ForgotPasswordActivity.this, getResources().getString(R.string.check_internet));


                    }
                }
            }
        });
        ivBack= (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                intent.putExtra("ride_id", ride_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        intent.putExtra("ride_id", ride_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
        finish();
       // super.onBackPressed();
    }
    private com.android.volley.Response.Listener<String> loginSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub

//                {"status":false,"message":"Please enter email address.","data":""}
//                Log.e("Json", "==> " + response);
                CommonClass.closeLoding(ForgotPasswordActivity.this);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
//

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("ride_id", ride_id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                        finish();
//                        overridePendingTransition(R.anim.fb_slide_in_from_right, R.anim.fb_forward);
                        finish();
                    } else {


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    e.printStackTrace();

                }

                CommonClass.ShowToast(ForgotPasswordActivity.this, message);
            }
        };
    }

    private com.android.volley.Response.ErrorListener loginErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(ForgotPasswordActivity.this);
                CommonClass.ShowToast(ForgotPasswordActivity.this, getResources().getString(R.string.s_wrong));
            }
        };
    }


    private boolean ValidationTask(String uemailid2) {
        // TODO Auto-generated method stub

        if (!TextUtils.isEmpty(uemailid2)) {

            if (TextValidator.isAValidEmail(uemailid2)) {
                String[] domain = {".aero", ".asia", ".biz", ".cat", ".com", ".coop", ".edu", ".gov", ".info", ".int", ".jobs", ".mil", ".mobi", ".museum", ".name", ".net", ".org", ".pro", ".tel", ".travel", ".ac", ".ad", ".ae", ".af", ".ag", ".ai", ".al", ".am", ".an", ".ao", ".aq", ".ar", ".as", ".at", ".au", ".aw", ".ax", ".az", ".ba", ".bb", ".bd", ".be", ".bf", ".bg", ".bh", ".bi", ".bj", ".bm", ".bn", ".bo", ".br", ".bs", ".bt", ".bv", ".bw", ".by", ".bz", ".ca", ".cc", ".cd", ".cf", ".cg", ".ch", ".ci", ".ck", ".cl", ".cm", ".cn", ".co", ".cr", ".cu", ".cv", ".cx", ".cy", ".cz", ".de", ".dj", ".dk", ".dm", ".do", ".dz", ".ec", ".ee", ".eg", ".er", ".es", ".et", ".eu", ".fi", ".fj", ".fk", ".fm", ".fo", ".fr", ".ga", ".gb", ".gd", ".ge", ".gf", ".gg", ".gh", ".gi", ".gl", ".gm", ".gn", ".gp", ".gq", ".gr", ".gs",
                        ".gt", ".gu", ".gw", ".gy", ".hk", ".hm", ".hn", ".hr", ".ht", ".hu", ".id", ".ie", " No", ".il", ".im", ".in", ".io", ".iq", ".ir", ".is", ".it", ".je", ".jm", ".jo", ".jp", ".ke", ".kg", ".kh", ".ki", ".km", ".kn", ".kp", ".kr", ".kw", ".ky", ".kz", ".la", ".lb", ".lc", ".li", ".lk", ".lr", ".ls", ".lt", ".lu", ".lv", ".ly", ".ma", ".mc", ".md", ".me", ".mg", ".mh", ".mk", ".ml", ".mm", ".mn", ".mo", ".mp", ".mq", ".mr", ".ms", ".mt", ".mu", ".mv", ".mw", ".mx", ".my", ".mz", ".na", ".nc", ".ne", ".nf", ".ng", ".ni", ".nl", ".no", ".np", ".nr", ".nu", ".nz", ".om", ".pa", ".pe", ".pf", ".pg", ".ph", ".pk", ".pl", ".pm", ".pn", ".pr", ".ps", ".pt", ".pw", ".py", ".qa", ".re", ".ro", ".rs", ".ru", ".rw", ".sa", ".sb", ".sc", ".sd", ".se", ".sg", ".sh", ".si", ".sj", ".sk", ".sl", ".sm", ".sn", ".so", ".sr", ".st", ".su", ".sv", ".sy", ".sz", ".tc", ".td", ".tf", ".tg", ".th", ".tj", ".tk", ".tl", ".tm", ".tn", ".to", ".tp", ".tr", ".tt", ".tv", ".tw", ".tz", ".ua", ".ug", ".uk", ".us", ".uy", ".uz", ".va", ".vc", ".ve", ".vg", ".vi", ".vn", ".vu", ".wf", ".ws", ".ye", ".yt", ".za", ".zm", ".zw"};
                List<String> list = Arrays.asList(domain);

                String tmp = uemailid2.substring(uemailid2.lastIndexOf("."), uemailid2.length());
                if (list.contains("" + tmp)) {
                    return true;
                } else {
                    CommonClass.ShowToast(ForgotPasswordActivity.this,"Please enter valid email address.");
                }
            } else {
                CommonClass.ShowToast(ForgotPasswordActivity.this,"Please enter valid email address.");
            }
        } else {
            CommonClass.ShowToast(ForgotPasswordActivity.this,"Please enter email address.");
        }

        return false;
    }

}
