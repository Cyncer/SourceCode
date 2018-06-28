package com.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cync.model.User;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Converts all the user data in the file users.json to an array of {@link User} objects.
 */
public class MentionsLoaderUtils {

    private final Context context;
    private final ArrayList<User> userList=new ArrayList<>();

    public MentionsLoaderUtils(final Context context) {
        this.context = context;
         loadUsers();
    }

    /**
     * Loads users from JSON file.
     */
    private void loadUsers() {
        final Gson gson = new Gson();



        new AsyncTask<String, String, String>(){

            @Override protected String doInBackground(String... key) {




//            try {
//                HttpClient hClient = new DefaultHttpClient();
//                HttpGet hGet = new HttpGet("http://www.linkedin.com/ta/skill?query="+newText);
//
//                ResponseHandler<String> rHandler = new BasicResponseHandler();
//                data = hClient.execute(hGet, rHandler);
//                suggest = new ArrayList<String>();
//                JSONObject jobj = new JSONObject(data);
//                JSONArray jArray = jobj.getJSONArray("resultList");
//                for (int i = 0; i < jArray.length(); i++) {
//                    String SuggestKey = jArray.getJSONObject(i).getString("displayName");
//                    suggest.add(SuggestKey);
//                }
//            } catch (Exception e) {
//                Log.w("Error", e.getMessage());
//            }

                JSONArray arr = null;
                String responsestr="";

                // suggest.clear();
                String responseString = null;

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(Constants.get_friend_list);

                try {
                    AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                            new AndroidMultiPartEntity.ProgressListener() {

                                @Override
                                public void transferred(long num) {
                                    //publishProgress((int) ((num / (float) file_size) * 100));
                                }
                            });





                    // Extra parameters if you want to pass to server
                    entity.addPart("user_id", new StringBody(CommonClass.getUserpreference(context).user_id));

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
                responsestr = responseString;








                return responsestr;
            }

            @Override
            protected void onPostExecute(String responsestr) {
                super.onPostExecute(responsestr);
                Log.i("MentionsLoaderUtils","Response===="+responsestr);
                if(responsestr.trim().length()>0)
                {

                    boolean Status;
                    String message;
                    try {
                        JSONObject jresObjectMain = new JSONObject(responsestr);
                        Status = jresObjectMain.getBoolean("status");
                        message = jresObjectMain.getString("message");
                        if (Status) {
                            String friend_id, friend_image = "", friend_first_name, friend_last_name, friend_lat, friend_email, friend_lng, friend_request;
                            JSONArray jresArray = jresObjectMain.getJSONArray("data");
                            userList.clear();
                            for (int i = 0; i < jresArray.length(); i++) {

                                JSONObject jsonObject = jresArray.getJSONObject(i);
                                friend_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
                                friend_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                                friend_first_name = CommonClass.getDataFromJson(jsonObject, "first_name");
                                friend_last_name = CommonClass.getDataFromJson(jsonObject, "last_name");
                                friend_email = CommonClass.getDataFromJson(jsonObject, "email");
                                friend_lat = CommonClass.getDataFromJson(jsonObject, "latitude");
                                friend_lng = CommonClass.getDataFromJson(jsonObject, "longitude");
                                friend_request = CommonClass.getDataFromJson(jsonObject, "status");


                                String SuggestKey = friend_first_name+" "+friend_last_name;
                                userList.add(new User(friend_first_name,friend_last_name,friend_id,friend_image));



//                            JSONObject jmin=new JSONObject();
//                            jmin.put("first",friend_first_name);
//                            jmin.put("last",friend_last_name);
//                            jmin.put("picture",friend_image);
//
//
//                            jMain.put(jmin);


                            }

                        } else {

                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block

                        e.printStackTrace();
                    }

                }



            }

        }.execute();
//        try {
//            final InputStream fileReader = context.getResources().openRawResource(R.raw.users);
//            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileReader, "UTF-8"));
//            users = gson.fromJson(bufferedReader, new TypeToken<List<User>>(){}.getType());
//        } catch (IOException ex) {
//            Log.e("Mentions Sample", "Error: Failed to parse json file.");
//        }


    }

    /**
     * Search for user with name matching {@code query}.
     *
     * @return a list of users that matched {@code query}.
     */
    public List<User> searchUsers(String query) {
        final List<User> searchResults = new ArrayList<>();
        if (StringUtils.isNotBlank(query)) {
            query = query.toLowerCase(Locale.US);
            if (userList != null && !userList.isEmpty()) {
                for (User user : userList) {
                    final String firstName = user.getFirstName().toLowerCase();
                    final String lastName = user.getLastName().toLowerCase();
                    if (firstName.startsWith(query) || lastName.startsWith(query)) {
                        searchResults.add(user);
                    }
                }
            }

        }
        return searchResults;
    }

}
