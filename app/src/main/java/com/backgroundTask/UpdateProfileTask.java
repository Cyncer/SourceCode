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
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class UpdateProfileTask extends AsyncTask<String, Void, Boolean> {

    private static final String TAG = UpdateProfileTask.class.getSimpleName();
//    ProgressDialog loginDialog;

    Activity mActivity;
    String message = "";
    String response = "";
    String firstname,lastname,user_image,login_type,make,year,model;
    boolean resultdata = false;
    private Responder mResponder;

    public UpdateProfileTask(Responder responder, Activity activity) {
        this.mResponder = responder;
        this.mActivity = activity;

    }

    @Override
    protected Boolean doInBackground(String... params) {

        String URL_REG = Constants.updateProfileUrl;
        firstname= params[0];
        lastname= params[1];
        user_image= params[2];
        make= params[3];
        year= params[4];
        model= params[5];


        Log.e("UpdateProfileTask", "make==>" + make);
        Log.e("UpdateProfileTask", "year==>" + year);
        Log.e("UpdateProfileTask", "model==>" + model);


        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        if(user_image.trim().length()>0) {
            FileBody fileBody = new FileBody(new File(user_image));
            builder.addPart("user_image", fileBody);
        }else {
            builder.addTextBody("user_image", "");
        }

        if(make.trim().length()>0 &&  year.trim().length()>0  && model.trim().length()>0)
        {
            builder.addTextBody("make_name", make);
            builder.addTextBody("car_year", year);
            builder.addTextBody("car_model", model);
        }




//        user_id=1&first_name=fff&last_name=fff&email=dd&image=ff
        builder.addTextBody("user_id", CommonClass.getUserpreference(mActivity).user_id);
//        if (firstname != null && groupName.trim().length() > 0) {
//            firstname = CommonClass.strEncodeDecode(firstname,false);
//            lastname = CommonClass.strEncodeDecode(lastname,false);
            builder.addTextBody("first_name", firstname);
            builder.addTextBody("last_name", lastname);
//        }

//        builder.addTextBody("device_token", CommonClass.getDeviceToken(mActivity));
//        builder.addTextBody("device_type", "android");
//        builder.addTextBody("latitude", "23.000");
//        builder.addTextBody("longitude", "23.000");
//        builder.addTextBody("login_type", "normal");

//        builder.addTextBody("login_type", "normal");
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
//            System.out.println("write data === " + entity.toString() + "is stream== " + entity.isStreaming());

            int responseCode = httpURLConnection.getResponseCode();
//            System.out.println("Response Code" + responseCode);
//            Log.d("Response Code", "test" + responseCode);

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



        }
        catch (Exception e) {
            Log.e("RegisterTask", "error==>" + e.getLocalizedMessage());
            message=mActivity.getResources().getString(R.string.s_wrong);
        }

        return resultdata;
    }

    private boolean parselogindata(String response) {
        // TODO Auto-generated method stub

        Log.e("Json", "==> " + response);
		Log.d("RegisterTask", "===>" + response);

        try
        {
            boolean Status;
            JSONObject jresObjectMain = new JSONObject(response);

            Status = jresObjectMain.getBoolean("status");
            message = jresObjectMain.getString("message");
                if (Status) {
//

                   // firstname,lastname,user_image,login_type,make,year,model

                    UserDetail ud=CommonClass.getUserpreference(mActivity);

                    ud.first_name=firstname;
                    ud.last_name=lastname;
                    ud.make=make;
                    ud.year=year;
                    ud.model=model;



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
            mResponder.onComplete(result, message);

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            CommonClass.showLoading(mActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public interface Responder {

        public void onComplete(boolean result, String message);
    }
}
