package com.backgroundTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.app.android.cync.R;
import com.utils.CommonClass;
import com.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zlinux on 21/2/17.
 */

public class BackgroundUploader extends AsyncTask<String, Integer, Boolean> implements DialogInterface.OnCancelListener {

    private ProgressDialog progressDialog;
    private String url;

    private static final String TAG = BackgroundUploader.class.getSimpleName();
    //    ProgressDialog loginDialog;
    Activity mActivity;
    String message = "";
    String response = "";
    String strImage, strVideo, strDesc;
    boolean resultdata = false;
    private Responder mResponder;
    File file;


    public interface Responder {

        public void onComplete(boolean result, String message);
    }


    public BackgroundUploader(Responder responder, Activity activity,String strImage,String strVideo) {
        this.mResponder = responder;
        this.mActivity = activity;


        this.strImage = strImage;
        this.strVideo = strVideo;
    }


    @Override
    protected void onPreExecute() {

        if (strImage.trim().length() > 0)
            file = new File(strImage);
        if (strVideo.trim().length() > 0)
            file = new File(strVideo);

        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.setMax((int) file.length());
        progressDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {


        String URL_REG = Constants.create_postUrl;

        strDesc = params[0];
        strImage = params[1];
        strVideo = params[2];

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String mboundary = "*****";

        HttpURLConnection.setFollowRedirects(false);
        HttpURLConnection connection = null;
        String fileName = file.getName();
        try {
            connection = (HttpURLConnection) new URL(URL_REG).openConnection();
            connection.setRequestMethod("POST");
            String boundary = "---------------------------boundary";
            String tail = "\r\n--" + boundary + "--\r\n";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setDoOutput(true);

            String metadataPart = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"metadata\"\r\n\r\n"
                    + "" + "\r\n";

            String fileHeader1 = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"post_attachment[]\"; filename=\""
                    + fileName + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n"
                    + "Content-Transfer-Encoding: binary\r\n";

            long fileLength = file.length() + tail.length();
            String fileHeader2 = "Content-length: " + fileLength + "\r\n";
            String fileHeader = fileHeader1 + fileHeader2 + "\r\n";
            String stringData = metadataPart + fileHeader;

            long requestLength = stringData.length() + fileLength;
            connection.setRequestProperty("Content-length", "" + requestLength);
            connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(stringData);



            String description_key = "description";
            String description_value = strDesc;
            try {
                out.writeBytes("Content-Disposition: form-data; name=\""
                        + description_key + "\"" + lineEnd
                        + "Content-Type: application/json" + lineEnd);
                out.writeBytes(lineEnd);
                out.writeBytes(description_value);
                out.writeBytes(lineEnd);
            } catch (IOException ioe) {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }



            String user_key = "user_id";
            String user_value =  CommonClass.getUserpreference(mActivity).user_id;
            try {
                out.writeBytes("Content-Disposition: form-data; name=\""
                        + user_key + "\"" + lineEnd
                        + "Content-Type: application/json" + lineEnd);


                out.writeBytes(lineEnd);
                out.writeBytes(user_value);
                out.writeBytes(lineEnd);
            } catch (IOException ioe) {
                Log.e("Debug", "error: " + ioe.getMessage(), ioe);
            }



            out.writeBytes(twoHyphens + mboundary + twoHyphens + lineEnd);
            out.flush();







            int progress = 0;
            int bytesRead = 0;
            byte buf[] = new byte[1024];
            BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(file));
            while ((bytesRead = bufInput.read(buf)) != -1) {
                // write output
                out.write(buf, 0, bytesRead);
                out.flush();
                progress += bytesRead;
                // update progress bar
                publishProgress(progress);
            }

            // Write closing boundary and close stream
            out.writeBytes(tail);
            out.flush();
            out.close();

            // Get server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            response = builder.toString();
        } catch (Exception e) {
            // Exception
            Log.i("BackgroundUploader","Exception==="+e.getLocalizedMessage());
        } finally {
            if (connection != null) connection.disconnect();
        }



        Log.i("BackgroundUploader","Response==="+response);


        return resultdata;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressDialog.setProgress((int) (progress[0]));
    }



    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if(progressDialog!=null)
            progressDialog.dismiss();
        CommonClass.closeLoding(mActivity);
        if (response.length() > 0)
            resultdata = parselogindata(response);
        else {
            resultdata = false;
            message = mActivity.getResources().getString(R.string.s_wrong);
        }

        if (mResponder != null)
            mResponder.onComplete(resultdata, message);

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
    public void onCancel(DialogInterface dialog) {
        cancel(true);
        dialog.dismiss();
    }
}