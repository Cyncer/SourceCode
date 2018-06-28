package com.webservice;

/**
 * Created by ketul.patel on 21/1/16.
 */
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class VolleyStringRequest extends StringRequest {

    private int mStatusCode;
    private String msg;
    private ErrorListener mErrorListener;

    public VolleyStringRequest(int method, String url,
                               Listener<String> listener, ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        setShouldCache(false);
        mErrorListener = errorListener;
    }

    @Override
    public Request<?> setRetryPolicy(RetryPolicy retryPolicy) {
        // TODO Auto-generated method stub
        retryPolicy = new DefaultRetryPolicy(50000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        setShouldCache(false);
        return super.setRetryPolicy(retryPolicy);
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;

        return super.parseNetworkResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        // TODO Auto-generated method stub

        Log.e("Deliver Error", "True");

        mErrorListener.onErrorResponse(error);
        super.parseNetworkError(error);
    }

    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError) {
        if (volleyError.networkResponse != null
                && volleyError.networkResponse.data != null) {
            Log.e("status Code", " code "
                    + volleyError.networkResponse.statusCode);

            try {
                JSONObject jsonobject = new JSONObject(new String(
                        volleyError.networkResponse.data));

                VolleyError error = new VolleyError(
                        jsonobject.optString("message"));
                volleyError = error;
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {

            VolleyError error = new VolleyError(VolleySetup.ErrorMessage);
            volleyError = error;
        }

        return volleyError;
    }
}
