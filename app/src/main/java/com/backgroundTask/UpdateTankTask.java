package com.backgroundTask;

/**
 * Created by ketul.patel on 21/1/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.app.android.cync.R;
import com.customwidget.CircularProgressBar;
import com.utils.AndroidMultiPartEntity;
import com.utils.CommonClass;
import com.utils.Constants;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class UpdateTankTask extends AsyncTask<String, Integer, Boolean> {

    private static final String TAG = UpdateTankTask.class.getSimpleName();
    //    ProgressDialog loginDialog;
    ProgressDialog mDialog;
    Activity mActivity;
    String message = "";
    String response = "";
    int file_size;
    String strImage, strVideo, strDesc, remove_attechment, post_id,tag_friend,tag_friend_id;
    boolean resultdata = false;
    private Responder mResponder;

    CircularProgressBar mCircularProgressBar;

    public UpdateTankTask(UpdateTankTask.Responder responder, Activity activity, String strImage, String strVideo,String tag_friend,String tag_friend_id) {
        this.mResponder = responder;
        this.mActivity = activity;
        this.strImage = strImage;
        this.strVideo = strVideo;
        this.tag_friend = tag_friend;
        this.tag_friend_id = tag_friend_id;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String URL_REG = Constants.update_postUrl;


        strDesc = params[0];
        strImage = params[1];
        strVideo = params[2];
        remove_attechment = params[3];
        post_id = params[4];
        tag_friend = params[5];
        tag_friend_id = params[6];


//        try {
//            strDesc= URLEncoder.encode(strDesc, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        strDesc =  StringEscapeUtils.escapeJava(strDesc);


//        if(remove_attechment.trim().length()==0)
//            remove_attechment="false";

        Log.e("UpdateTankTask", "strDesc==>" + strDesc);
        Log.e("UpdateTankTask", "strImage==>" + strImage);
        Log.e("UpdateTankTask", "strVideo==>" + strVideo);
        Log.e("UpdateTankTask", "remove_attechment==>" + remove_attechment);
        Log.e("UpdateTankTask", "post_id==>" + post_id);
        Log.e("UpdateTankTask", "user_id==>" + CommonClass.getUserpreference(mActivity).user_id);
        Log.e("UpdateTankTask", "tag_friend==>" +tag_friend);
        Log.e("UpdateTankTask", "tag_friend_id==>" +tag_friend_id);


        String responseString = null;

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(URL_REG);

        try {
            AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                    new AndroidMultiPartEntity.ProgressListener() {

                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) file_size) * 100));
                        }
                    });

            File sourceFile = null;

            if (strImage.trim().length() > 0) {

                sourceFile = new File(strImage);


            } else if (strVideo.trim().length() > 0) {

                sourceFile = new File(strVideo);

            }


            // Adding file data to http body
            if (strImage.trim().length() > 0 || strVideo.trim().length() > 0)
                entity.addPart("post_attachment[]", (ContentBody) new FileBody(sourceFile));

            // Extra parameters if you want to pass to server
            entity.addPart("description",
                    new StringBody(strDesc));
            entity.addPart("user_id", new StringBody(CommonClass.getUserpreference(mActivity).user_id));
            entity.addPart("post_id", new StringBody(post_id));
            entity.addPart("tag_friend", new StringBody(tag_friend));
            entity.addPart("tag_friend_id", new StringBody(tag_friend_id));
            if (remove_attechment.trim().length() > 0)
                entity.addPart("remove_attechment", new StringBody(remove_attechment));


            //        builder.addTextBody("post_id", post_id);
//
//        if (remove_attechment.trim().length() > 0)
//        builder.addTextBody("remove_attechment", remove_attechment);
//        builder.addTextBody("description", strDesc);
//        builder.addTextBody("user_id", CommonClass.getUserpreference(mActivity).user_id);


            file_size = (int) entity.getContentLength();
            httppost.setEntity(entity);

            // Making server call
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            responseString = "";
        } catch (IOException e) {
            responseString = "";
        }
        response = responseString;


//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        if (strImage.trim().length() > 0) {
//            FileBody fileBody = new FileBody(new File(strImage));
//            builder.addPart("post_attachment[]", fileBody);
//        } else if (strVideo.trim().length() > 0) {
//            FileBody fileBody = new FileBody(new File(strVideo));
//            builder.addPart("post_attachment[]", fileBody);
//        }
//        builder.addTextBody("post_id", post_id);
//
//        if (remove_attechment.trim().length() > 0)
//        builder.addTextBody("remove_attechment", remove_attechment);
//        builder.addTextBody("description", strDesc);
//        builder.addTextBody("user_id", CommonClass.getUserpreference(mActivity).user_id);
//
//        HttpEntity entity = builder.build();
//        try {
//            URL_REG = URL_REG.replaceAll(" ", "%20");
//
//            System.out.println("Register URL doInBackGround()====" + URL_REG);
//            URL url = new URL(URL_REG);
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestProperty("Accept", "application/json");
//            httpURLConnection.setRequestProperty("Connection", "close");
//
//            httpURLConnection.setConnectTimeout(50000);
//            httpURLConnection.setReadTimeout(50000);
//
//            httpURLConnection.setDoInput(true);
//            httpURLConnection.setRequestMethod("POST");
//            httpURLConnection.setDoOutput(true);
//            httpURLConnection.addRequestProperty("Content-length", entity.getContentLength() + "");
//            httpURLConnection.addRequestProperty(entity.getContentType().getName(), entity.getContentType().getValue());
//
//            entity.writeTo(httpURLConnection.getOutputStream());
////            System.out.println("write data === " + entity.toString() + "is stream== " + entity.isStreaming());
//
//            int responseCode = httpURLConnection.getResponseCode();
////            System.out.println("Response Code" + responseCode);
////            Log.d("Response Code", "test" + responseCode);
//
////			if(responseCode == HttpURLConnection.HTTP_OK){
////				String line;
////				BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
////				while((line=br.readLine())!=null){
////					response += line;
////				}
////
////			}else{
////				Log.d("Else code test", response);
////				response="";
////			}
//
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"), 8000);
//                StringBuilder sb = new StringBuilder();
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//                reader.close();
//                response = sb.toString();
//            } else {
//                response = "";
//            }
//
//
//        } catch (Exception e) {
//            Log.e("RegisterTask", "error==>" + e.getLocalizedMessage());
//            message = mActivity.getResources().getString(R.string.s_wrong);
//        }

        return resultdata;
    }

    private boolean parselogindata(String response) {
        // TODO Auto-generated method stub

        Log.e("Json", "==> " + response);
        Log.d("RegisterTask", "===>" + response);

        try {
            boolean Status;
            JSONObject jresObjectMain = new JSONObject(response);

            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");
            if (Status) {
//

                return true;

            } else {
                return false;

            }

        } catch (JSONException e) {
            message = mActivity.getResources().getString(R.string.s_wrong);
            e.printStackTrace();

        }
        return false;
    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);

//        if (mDialog != null)
//            mDialog.dismiss();


        if (strImage.trim().length() > 0 || strVideo.trim().length() > 0) {


            CommonClass.closeLodingpercentage(mActivity);
        } else {

            CommonClass.closeLoding(mActivity);

        }

        if (response.length() > 0)
            resultdata = parselogindata(response);
        else {
            resultdata = false;
            message = mActivity.getResources().getString(R.string.s_wrong);
        }

        if (mResponder != null)
            mResponder.onComplete(resultdata, message);

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {


            if (strImage.trim().length() > 0 || strVideo.trim().length() > 0) {


                mCircularProgressBar = CommonClass.showLoadingPercentage(mActivity);

                if (strImage.trim().length() > 0) {


                    CommonClass.setTextLoading("Uploading image...");

                } else if (strVideo.trim().length() > 0) {


                    CommonClass.setTextLoading("Uploading Video...");

                }

            } else {

                CommonClass.showLoading(mActivity);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface Responder {

        public void onComplete(boolean result, String message);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {


        if (strImage.trim().length() > 0 || strVideo.trim().length() > 0) {


            if (mCircularProgressBar != null) {
                mCircularProgressBar.setProgress((int) (progress[0]));
                mCircularProgressBar.showProgressText(true);

            }

        } else {


        }


    }

}
