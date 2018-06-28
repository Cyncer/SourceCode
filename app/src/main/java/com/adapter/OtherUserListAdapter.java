package com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.UserDetail;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;
import com.xmpp.ChatDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ketul.patel on 4/2/16.
 */
public class OtherUserListAdapter extends RecyclerView.Adapter<OtherUserListAdapter.ViewHolder> {
    ArrayList<UserDetail> mUserListData;
    Activity mActivity;
    int mPosition;
    private int lastPosition = -1;
    private RequestQueue mQueue;
    public OtherUserListAdapter( Activity mActivity,ArrayList<UserDetail> mUserListData) {
        this.mUserListData = mUserListData;
        this.mActivity = mActivity;
        mQueue = VolleySetup.getRequestQueue();
        lastPosition= mUserListData.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        //        private com.rey.material.widget.CheckedImageView friendIcon;
        //private com.rey.material.widget.TextView friendIcon;
        private ImageView ivUserIcon;
        private android.widget.TextView tvUsername,tvAdded;
        public UserDetail userDetail;
        public ViewHolder(final View view) {
            super(view);
            ivUserIcon = (ImageView) view.findViewById(R.id.ivUserIcon);
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);
            tvAdded = (TextView) view.findViewById(R.id.tvAdded);
            tvAdded.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CommonClass.ShowToast(mActivity,""+getAdapterPosition());
                    mPosition=  getAdapterPosition();
                    if(mUserListData.get(mPosition).status.equalsIgnoreCase("accept")||mUserListData.get(mPosition).status.equalsIgnoreCase("pending")) {

                    }else {
                        sendRequestFriend(getAdapterPosition());
                    }
                }
            });
        }
    }
    View view;
    public List<UserDetail> getFriendDetailList() {
        return mUserListData;
    }
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.other_list_view_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserDetail userDetail = mUserListData.get(position);
        if (userDetail != null) {
//            String strName= CommonClass.strEncodeDecode(mUserListData.get(position).first_name,true);
            holder.tvUsername.setText(mUserListData.get(position).first_name);
//            String imagePath = mUserListData.get(position).user_image;
//            Log.e("Usetr Image", "---------- pos " + position + " image = " + userDetail.user_image);
            if (userDetail.user_image != null && userDetail.user_image.length() > 0) {
//                if(CommonClass.getUserpreference(mActivity).login_type.equalsIgnoreCase("facebook")){
//                    if(mUserListData.get(position).user_image.contains("https://")||mUserListData.get(position).user_image.contains("http://"))
//                        mUserListData.get(position).user_image=mUserListData.get(position).user_image.trim();
//                    else
//                        mUserListData.get(position).user_image = Constants.imagBaseUrl + mUserListData.get(position).user_image.trim();
//                }else
//                    mUserListData.get(position).user_image = Constants.imagBaseUrl + mUserListData.get(position).user_image.trim();
//
//                Picasso.with(mActivity).load(mUserListData.get(position).user_image).into(holder.ivUserIcon);
//                Log.e("setDrawerImage", "setDrawerImage " + mUserListData.get(position).user_image);
//                if (mUserListData.get(position).user_image.startsWith("http")) {
////                    if(userDetailList.get(position).user_image.contains("https://")||userDetailList.get(position).user_image.contains("http://")) {
//                    mUserListData.get(position).user_image = mUserListData.get(position).user_image.trim();
////                    Log.e("GroupInfo", "IF---------- " + mUserListData.get(position).user_image);
//                } else {
////                    Log.e("GroupInfo", "ELSE---------- " + mUserListData.get(position).user_image);
//                    mUserListData.get(position).user_image = Constants.imagBaseUrl + mUserListData.get(position).user_image.trim();
//                }

//                Log.e("Picasso", "Final---------- " + mUserListData.get(position).user_image);








                String imagePath = userDetail.user_image;

                Log.e("imagePath", "Other User -------" + imagePath);


                if (imagePath != null && imagePath.length() > 0 ) {
                    if(userDetail.login_type.equalsIgnoreCase("facebook")){
                        if(imagePath.contains("https://")||imagePath.contains("http://"))
                            imagePath=imagePath.trim();
                        else
                            imagePath = Constants.imagBaseUrl + imagePath.trim();
                    }else
                        imagePath = Constants.imagBaseUrl + imagePath.trim();

                 Log.e("imagePath", "Other User -------" + imagePath);

                    Picasso.with(mActivity)
                            .load(imagePath)
                            .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                            .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                            .into(holder.ivUserIcon);

                }
                else
                    holder.ivUserIcon.setImageResource(R.drawable.no_image);











//                Picasso.with(mActivity)
//                        .load(mUserListData.get(position).user_image)
//                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
//                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
//                        .into(holder.ivUserIcon);


//                Log.e("setDrawerImage", "setDrawerImage " + mUserListData.get(position).user_image);
            } else
            {
                holder.ivUserIcon.setImageResource(R.drawable.no_image);
             }
//            if (imagePath != null && imagePath.length() > 0) {
//                imagePath = Constants.imagBaseUrl + imagePath.trim();
////                Log.e("friendDetailList", "-------" + imagePath);
//                Picasso.with(mActivity).load(imagePath).into(holder.ivUserIcon);
//            } else
//                holder.ivUserIcon.setImageResource(R.drawable.no_image);

            if(mUserListData.get(position).status!=null&&mUserListData.get(position).status.trim().length()>0){

//               Log.e("mUserListData", "--IF-----" + mUserListData.get(position).status);
               if(mUserListData.get(position).status.equalsIgnoreCase("accept")){
                   holder.tvAdded.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary_black));
                   holder.tvAdded.setTextColor(mActivity.getResources().getColor(R.color.white));
                   holder.tvAdded.setText("  Added  ");
               }else if(mUserListData.get(position).status.equalsIgnoreCase("pending")){
                   holder.tvAdded.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary_black));
                   holder.tvAdded.setTextColor(mActivity.getResources().getColor(R.color.white));
                   holder.tvAdded.setText(" Pending ");
               }else if(mUserListData.get(position).status.equalsIgnoreCase("reject")){
                   holder.tvAdded.setText("   Add   ");
               }
           }else
           {
//               Log.e("mUserListData", "--ELSE-----" + mUserListData.get(position).status);
               holder.tvAdded.setBackgroundColor(mActivity.getResources().getColor(R.color.white));
               holder.tvAdded.setText("   Add   ");
               holder.tvAdded.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary_black));
           }
            holder.tvAdded.setTag(position);
//            Animation animation = AnimationUtils.loadAnimation(mActivity, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);

//            view.startAnimation(animation);
//            Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.up_from_bottom);
//            animation.setStartOffset(position*500);
//            if(view!=null)
//            view.startAnimation(animation);

            lastPosition= position;
        }
    }
    @Override
    public int getItemCount() {
        return (null != mUserListData ? mUserListData.size() : 0);
    }
//    public int deleteSelectedRecord() {
//
//        friendDetailList.remove(selectedPostion);
//        notifyItemRemoved(selectedPostion);
//        return selectedPostion;
//    }

    private void sendRequestFriend(final int mPosition) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.sendFriendRequest, deleteSuccessLisner(),
                deleteErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" +  CommonClass.getUserpreference(mActivity).user_id);
                requestparam.put("friend_id",mUserListData.get(mPosition).user_id);
                Log.e("getFriend List Request", "==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(mActivity);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> deleteSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";
            @Override
            public void onResponse(String response) {
//                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(mActivity);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        mUserListData.get(mPosition).setStatus("Pending");
                        CommonClass.ShowToast(mActivity, message);
                   if(mUserListData.get(mPosition).user_id!=null) {
                       ChatDatabase chatDatabase = ChatDatabase.getInstance(mActivity);
                       chatDatabase.deleteHistoryRecord(CommonClass.getChatUsername(mActivity), "cync_" + mUserListData.get(mPosition).user_id);
                   }

                    } else {
                        CommonClass.ShowToast(mActivity, message);
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    message = mActivity.getString(R.string.s_wrong);
                    CommonClass.ShowToast(mActivity, message);
                    e.printStackTrace();
                }
//                loadFragmentAgain();
            }
        };
    }

    private com.android.volley.Response.ErrorListener deleteErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity);
                CommonClass.ShowToast(mActivity, mActivity.getString(R.string.s_wrong));
            }
        };
    }

}
