package com.backgroundTask;

/**
 * Created by ketul.patel on 21/1/16.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.app.android.cync.R;
import com.fragment.AddGroupFragment;
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



public class CreateGroupTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = CreateGroupTask.class.getSimpleName();
    Activity mActivity;
    String message = "";
    String response = "";
    String user_id,friend_id,group_name,group_image;
    boolean resultdata = false;
    private Responder mResponder;

    public CreateGroupTask(Responder responder, Activity activity) {
        this.mResponder = responder;
        this.mActivity = activity;
        CommonClass.showLoading(activity);
    }

    @Override
    protected Boolean doInBackground(String... params) {
//        user_id=34&group_name=zealous&group_image=&friends_id=22,23
        String URL_REG = Constants.create_group;
        friend_id= params[0];
        group_name= params[1];
        group_image= params[2];
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if(group_image.trim().length()>0) {
            FileBody fileBody = new FileBody(new File(group_image));
            builder.addPart("group_image", fileBody);
        }
        builder.addTextBody("user_id", CommonClass.getUserpreference(mActivity).user_id);
        builder.addTextBody("friends_id", friend_id);
//        group_name = CommonClass.strEncodeDecode(group_name,false);
        builder.addTextBody("group_name", group_name);
        HttpEntity entity = builder.build();
        try
        {
//            URL_REG = URL_REG.replaceAll(" ", "%20");


//            String encodedUrl = URLEncoder.encode(URL_REG,"UTF-8");
//            URL url=new URL(encodedUrl);

            System.out.println("Create Group URL doInBackGround()====" + URL_REG+""+entity.toString());
            URL url=new URL(URL_REG);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
//            httpURLConnection.setRequestProperty("content-type", "application/json;  charset=utf-8");
            httpURLConnection.setRequestProperty("Connection", "close");

            httpURLConnection.setConnectTimeout(50000);
            httpURLConnection.setReadTimeout(50000);

            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
            httpURLConnection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
            entity.writeTo(httpURLConnection.getOutputStream());
            System.out.println("write data === " + entity.toString() + "is stream== " + entity.isStreaming());
            int responseCode = httpURLConnection.getResponseCode();
//            System.out.println("Response Code" + responseCode);
//            Log.e("Response Code", "test" + responseCode);

            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"), 8000);
                StringBuilder sb = new StringBuilder();
                if(reader!=null){
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
               }
                response=sb.toString();
            }else{
                response="";
            }
//            Log.e("Create Group",":-- "+response);

        }
        catch (Exception e) {
            Log.e("CrateGroupTask", "" + e.getLocalizedMessage());
            message=mActivity.getResources().getString(R.string.s_wrong);
        }
        return resultdata;
    }

    private boolean parselogindata(String response) {

//        Log.e("Json", "==> " + response);
//		 Log.d("SuccessLisner Json", "==> " + response);
//        {"status":true,"message":"Group created successfully.","data":""}

        boolean Status;
        String message;
        try {
            JSONObject jresObjectMain = new JSONObject(response);
            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");
            if (Status) {
                CommonClass.ShowToast(mActivity, message);
            } else {
                CommonClass.ShowToast(mActivity, message);
            }
        } catch (JSONException e) {
            message = mActivity.getResources().getString(R.string.s_wrong);
            CommonClass.ShowToast(mActivity, message);
            e.printStackTrace();
        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        CommonClass.closeLoding(mActivity);
        AddGroupFragment.groupImagePath="";
        AddGroupFragment.groupName="";
         if(response.length()>0)
            resultdata = parselogindata(response);
            else
            {
                resultdata=false;
                message=mActivity.getResources().getString(R.string.s_wrong);
            }
        if (mResponder != null)
            mResponder.onComplete(result, message);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


        try {
//            loginDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public interface Responder {

        public void onComplete(boolean result, String message);
    }
}
