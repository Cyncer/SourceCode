package com.backgroundTask;

/**
 * Created by ketul.patel on 21/1/16.
 */

import android.app.Activity;
import android.os.AsyncTask;

import com.app.android.cync.R;
import com.utils.CommonClass;
import com.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class UpdateGroupProfileTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = UpdateGroupProfileTask.class.getSimpleName();
//    ProgressDialog loginDialog;

    Activity mActivity;
    String message = "";
    String response = "";
    String groupId, groupName, groupImage, shareLocation;
    boolean resultdata = false;
    private Responder mResponder;

    public UpdateGroupProfileTask(Responder responder, Activity activity) {
        this.mResponder = responder;
        this.mActivity = activity;
        CommonClass.showLoading(activity);
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String URL_REG = Constants.updateGroupInfo;
        groupId = params[0];
        groupName = params[1];
        groupImage = params[2];
        shareLocation = params[3];

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (groupImage != null && groupImage.trim().length() > 0) {
            FileBody fileBody = new FileBody(new File(groupImage));
            builder.addPart("group_image", fileBody);
        } else {
//            builder.addTextBody("group_image", "");
        }
//        user_id=1&first_name=fff&last_name=fff&email=dd&image=ff
//        builder.addTextBody("user_id", CommonClass.getUserpreference(mActivity).user_id);
        builder.addTextBody("group_id", groupId);
        if (shareLocation != null && shareLocation.trim().length() > 0)
            builder.addTextBody("share_location", shareLocation);

        if (groupName != null && groupName.trim().length() > 0) {
//            groupName = CommonClass.strEncodeDecode(groupName,false);
            builder.addTextBody("group_name", groupName);
        }
        builder.addTextBody("user_id", CommonClass.getUserpreference(mActivity).user_id);
        HttpEntity entity = builder.build();
//        Log.e("UpdateGroupInfo", "" + entity.toString());
        try {
            URL_REG = URL_REG.replaceAll(" ", "%20");

//            System.out.println("Register URL doInBackGround()====" + URL_REG + groupId + " " + groupName + " " + groupImage + " " + shareLocation);
//            Log.e("Update Group", " " + URL_REG);
            URL url = new URL(URL_REG);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
            httpURLConnection.setRequestProperty("Connection", "close");

            httpURLConnection.setConnectTimeout(10000);
            httpURLConnection.setReadTimeout(10000);

            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
            httpURLConnection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());

            entity.writeTo(httpURLConnection.getOutputStream());
            System.out.println("write data === " + entity.toString() + "is stream== " + entity.isStreaming());

            int responseCode = httpURLConnection.getResponseCode();
//            System.out.println("Response Code" + responseCode);
//            Log.d("Response Code", "test" + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"), 8000);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                response = sb.toString();
            } else {
                response = "";
            }
        } catch (Exception e) {
//            Log.e("RegisterTask", "" + e.getLocalizedMessage());
            message = mActivity.getResources().getString(R.string.s_wrong);
        }
        return resultdata;
    }

    private boolean parselogindata(String response) {
//        Log.e("Json", "==> " + response);
//		Log.d("RegisterTask", "===>" + response);
        try {
            boolean Status;
            JSONObject jresObjectMain = new JSONObject(response);
            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");
        } catch (JSONException e) {
            message = mActivity.getResources().getString(R.string.s_wrong);
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        CommonClass.closeLoding(mActivity);
        if (response.length() > 0)
            resultdata = parselogindata(response);
        else {
            resultdata = false;
            message = mActivity.getResources().getString(R.string.s_wrong);
        }
        if (mResponder != null)
            mResponder.onComplete(result, message);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Responder {
        public void onComplete(boolean result, String message);
    }
}
