package com.backgroundTask;

/**
 * Created by ketul.patel on 21/1/16.
 */

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.utils.CommonClass;
import com.utils.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimeZone;


public class RegisterTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = RegisterTask.class.getSimpleName();
//    ProgressDialog loginDialog;


    Activity mActivity;
    String message = "";
    String response = "";
    String email,password,firstname,lastname, device_token,device_type,user_image,login_type,lat,lng;
    boolean resultdata = false;
    private Responder mResponder;

    public RegisterTask(Responder responder, Activity activity) {
        this.mResponder = responder;
        this.mActivity = activity;
        CommonClass.showLoading(activity);
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String URL_REG = Constants.registerUrl;
        email= params[0];
        password= params[1];
        firstname= params[2];
        lastname= params[3];
        user_image= params[4];
        lat = CommonClass.getPrefranceByKey_Value(mActivity, CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lat);
        lng = CommonClass.getPrefranceByKey_Value(mActivity, CommonClass.KEY_PREFERENCE_CURRENT_LOCATION, CommonClass.KEY_Current_Lng);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if(user_image.trim().length()>0) {
            FileBody fileBody = new FileBody(new File(user_image));
            builder.addPart("user_image", fileBody);
        }
        builder.addTextBody("email", email);
//        password = CommonClass.strEncodeDecode(password,false);
        builder.addTextBody("password", password);
//        firstname = CommonClass.strEncodeDecode(firstname,false);
        builder.addTextBody("first_name", firstname);
//        lastname = CommonClass.strEncodeDecode(lastname,false);
        builder.addTextBody("last_name", lastname);
        builder.addTextBody("api_token", CommonClass.getDeviceToken(mActivity));
        builder.addTextBody("device_type", "android");
        builder.addTextBody("latitude", lat);
        builder.addTextBody("longitude", lng);
        builder.addTextBody("login_type", "normal");
        builder.addTextBody("timezone", ""+TimeZone.getDefault().getID());
//        Log.e("Parameter","email"+email+"password"+password+"firstname"+firstname+"lastname"+lastname+"api_token"+CommonClass.getDeviceToken(mActivity)+
//                "device_type"+"android"+"latitude"+lat+"longitude"+lng+"login_type"+"normal");
        HttpEntity entity = builder.build();
        try
        {
            URL_REG = URL_REG.replaceAll(" ", "%20");

            System.out.println("Register URL doInBackGround()====" + URL_REG);
            URL url=new URL(URL_REG);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Accept", "application/json");
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
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            entity.writeTo(bytes);
            String content = bytes.toString();
//            Log.e("content--------",""+content);
            int responseCode = httpURLConnection.getResponseCode();
//            System.out.println("Response Code" + responseCode);
            Log.d("Response Code", "test" + responseCode);

//			if(responseCode == HttpURLConnection.HTTP_OK){
//				String line;
//				BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//				while((line=br.readLine())!=null){
//					response += line;
//				}
//
//			}else{
//				Log.d("Else code test", response);
//				response="";
//			}

            if(responseCode == HttpURLConnection.HTTP_OK){

                BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"), 8000);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                response=sb.toString();
            }else{
                response="";
            }
            //
            //
            Log.e("response", "" + response);

        }
        catch (Exception e) {
//            Log.e("RegisterTask", "" + e.getLocalizedMessage());
            e.printStackTrace();
            message=mActivity.getResources().getString(R.string.s_wrong);
        }

        return resultdata;
    }
    boolean Status;
    private boolean parselogindata(String response) {
        // TODO Auto-generated method stub

        Log.e("Json", "==> " + response);

        try
        {

            JSONObject jresObjectMain = new JSONObject(response);

            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");


                if (Status) {
//
                    String user_id,user_image="",first_name,last_name,login_type,email,make,model,year;
                    boolean notification_type;
                    JSONObject jresObject = jresObjectMain.getJSONObject("data");
                    user_id = "" + CommonClass.getDataFromJsonInt(jresObject, "user_id");
                    user_image = CommonClass.getDataFromJson(jresObject, "user_image");
                    first_name = CommonClass.getDataFromJson(jresObject, "first_name");
                    last_name = CommonClass.getDataFromJson(jresObject, "last_name");
                    login_type = CommonClass.getDataFromJson(jresObject, "login_type");
                    email = CommonClass.getDataFromJson(jresObject, "email");
                    make = CommonClass.getDataFromJson(jresObject, "make");
                    model = CommonClass.getDataFromJson(jresObject, "model");
                    year = CommonClass.getDataFromJson(jresObject, "year");
                    if(jresObject.has("notification_type"))
                        notification_type= CommonClass.getDataFromJsonBoolean(jresObject, "notification_type");
                    else
                        notification_type=true;

                    boolean view_my_rides, hide_top_speed;

                    if(jresObject.has("view_my_rides"))
                        view_my_rides= CommonClass.getDataFromJsonBoolean(jresObject, "view_my_rides");
                    else
                        view_my_rides=true;


                    if(jresObject.has("hide_top_speed"))
                        hide_top_speed= CommonClass.getDataFromJsonBoolean(jresObject, "hide_top_speed");
                    else
                        hide_top_speed=false;


//                    String chat_username = CommonClass.getDataFromJson(jresObject,"chat_username");
//                    String chat_password = CommonClass.getDataFromJson(jresObject,"chat_password");
//                    CommonClass.setChatUserpreference(mActivity,chat_username,chat_password);
                    CommonClass.setChatUserpreference(mActivity,"cync_"+user_id,"cync_"+user_id);
                    UserDetail ud = new UserDetail(user_id, user_image, first_name, last_name, login_type, email,notification_type,make,model,year,view_my_rides, hide_top_speed);
                    CommonClass.setUserpreference( mActivity, ud);
                    return true;

                } else {


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
        CommonClass.closeLoding(mActivity);
        if(response.length()>0)
            resultdata = parselogindata(response);
        else
        {
            resultdata=false;
            message=mActivity.getResources().getString(R.string.s_wrong);
        }
        if (mResponder != null)
            mResponder.onComplete(Status, message);

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
