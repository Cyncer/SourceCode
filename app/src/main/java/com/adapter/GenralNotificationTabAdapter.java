package com.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import com.app.android.cync.CyncTankDetailsActiivty;
import com.app.android.cync.R;
import com.cync.model.Notification;
import com.fragment.GeneralTabFragment;
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
import java.util.Map;


/**
 * Created by ketul.patel on 2/2/16.
 */
public class GenralNotificationTabAdapter extends RecyclerView.Adapter<GenralNotificationTabAdapter.ViewHolder> {

    private static final String TAG = "GenralNotificationTabAdapter";
    private Activity mActivity;
    private ArrayList<Notification> notifyDetails;
    private boolean onBind;
    private int mPosition;
    private RequestQueue mQueue;
    SetFragment setText;

    public GenralNotificationTabAdapter(Activity mActivity, ArrayList<Notification> notifyDetails) {
        this.mActivity = mActivity;
        this.notifyDetails = notifyDetails;
        GeneralTabFragment generalTabFragment = new GeneralTabFragment();
        setText = (SetFragment) generalTabFragment;
        mQueue = VolleySetup.getRequestQueue();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        //        private com.rey.material.widget.CheckedImageView friendIcon;
        //private com.rey.material.widget.TextView friendIcon;

        private CardView mainCardView;
        private ImageView notifyImage;
        private TextView notifyName;
        private TextView notifyDetail;
        private TextView notifyTime;
        private ImageView acceptRequest;
        private ImageView iv_line;
        private ImageView rejectRequest;

        public ViewHolder(final View view) {
            super(view);
            mainCardView = (CardView) view.findViewById(R.id.mainCardView);
            notifyImage = (ImageView) view.findViewById(R.id.notifyImage);
            notifyName = (TextView) view.findViewById(R.id.notifyName);
            notifyDetail = (TextView) view.findViewById(R.id.notifyDetail);
            acceptRequest = (ImageView) view.findViewById(R.id.iv_yes);
            iv_line = (ImageView) view.findViewById(R.id.iv_line);
            acceptRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CommonClass.ShowToast(mActivity,""+getAdapterPosition());
                    mPosition = getAdapterPosition();
                    responseRequest(mPosition, "accept");
                }
            });
            rejectRequest = (ImageView) view.findViewById(R.id.iv_cancel);
            rejectRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    CommonClass.ShowToast(mActivity,""+getAdapterPosition());
                    mPosition = getAdapterPosition();
                    responseRequest(mPosition, "reject");
//                    responseFriendRequest(getAdapterPosition(),"reject");
                }
            });
            notifyTime = (TextView) view.findViewById(R.id.notifyTime);
        }
    }

    public void responseRequest(int position, String strResponse) {
        if (notifyDetails.get(position).notification_type.equalsIgnoreCase("Friend")) {
            if (strResponse.equals("accept"))
                alertDialog(mActivity, "If you accept friend request your location will be shared with him/her.", position, 1);
            else
                responseFriendRequest(position, strResponse);
        }
        //             responseFriendRequest(position,strResponse);
        if (notifyDetails.get(position).notification_type.equalsIgnoreCase("Group")) {
            if (strResponse.equals("accept"))
                alertDialog(mActivity, "If you accept group request your location may be shared in this group.", position, 2);
            else
                responseGroupRequest(position, strResponse);
        }
    }

    @Override
    public GenralNotificationTabAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.genral_notification_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;

    }

    @Override
    public void onBindViewHolder(GenralNotificationTabAdapter.ViewHolder holder, final int position) {
//        String imagePath = notifyDetails.get(position).notification_image;


//
        if (notifyDetails.get(position).notification_type.equalsIgnoreCase("new_post")) {

            Log.i(TAG, "onBindViewHolder: TIME==" + notifyDetails.get(position).notification_time);

            holder.notifyName.setText(notifyDetails.get(position).notification_name.trim());
            holder.notifyDetail.setText(notifyDetails.get(position).notification_status);
            holder.notifyTime.setText(notifyDetails.get(position).notification_time);
            holder.acceptRequest.setImageResource(R.drawable.accept);
            holder.rejectRequest.setImageResource(R.drawable.cancel1);
            holder.acceptRequest.setVisibility(View.GONE);
            holder.iv_line.setVisibility(View.GONE);
            holder.rejectRequest.setVisibility(View.GONE);


            if (notifyDetails.get(position).read.equalsIgnoreCase("1")) {
                holder.mainCardView.setCardBackgroundColor(Color.WHITE);
            } else {
                holder.mainCardView.setCardBackgroundColor(Color.parseColor("#ededed"));
            }

        } else {

            Log.i(TAG, "onBindViewHolder: TIME==" + notifyDetails.get(position).notification_time);
            holder.acceptRequest.setVisibility(View.VISIBLE);
            holder.rejectRequest.setVisibility(View.VISIBLE);
            holder.iv_line.setVisibility(View.VISIBLE);
            holder.notifyName.setText(notifyDetails.get(position).notification_name.trim());
            holder.notifyDetail.setText(notifyDetails.get(position).notification_type);
            holder.notifyTime.setText(notifyDetails.get(position).notification_time);
            holder.acceptRequest.setImageResource(R.drawable.accept);
            holder.rejectRequest.setImageResource(R.drawable.cancel1);

            if (notifyDetails.get(position).read.equalsIgnoreCase("1")) {
                holder.mainCardView.setCardBackgroundColor(Color.WHITE);
            } else {
                holder.mainCardView.setCardBackgroundColor(Color.parseColor("#ededed"));
            }
        }


//        holder.notifyDetail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                    CommonClass.ShowToast(mActivity,""+getAdapterPosition());
//                if (notifyDetails.get(position).notification_type.equalsIgnoreCase("new_post")) {
//
//
//                    Intent intent = new Intent(mActivity, CyncTankDetailsActiivty.class);
//
//                    intent.putExtra("post_id", "" + notifyDetails.get(position).group_admin_id_);
//                    intent.putExtra("type", "new_post");
//                    intent.putExtra("notification_id", "" + notifyDetails.get(position).notification_id);
//                    mActivity.startActivity(intent);
//
//
//                }
//            }
//        });

        holder.notifyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                    CommonClass.ShowToast(mActivity,""+getAdapterPosition());
                if (notifyDetails.get(position).notification_type.equalsIgnoreCase("new_post")) {

                    Intent intent = new Intent(mActivity, CyncTankDetailsActiivty.class);
                    intent.putExtra("post_id", "" + notifyDetails.get(position).group_admin_id_);
                    intent.putExtra("type", "new_post");
                    mActivity.startActivity(intent);

                }
            }
        });


        if (notifyDetails.get(position).notification_image != null && notifyDetails.get(position).notification_image.length() > 0) {
            if (notifyDetails.get(position).notification_image.startsWith("http")) {
                notifyDetails.get(position).notification_image = notifyDetails.get(position).notification_image.trim();
//                Log.e("GroupInfo", "IF---------- " + notifyDetails.get(position).notification_image);
            } else {
//                Log.e("GroupInfo", "ELSE---------- " + notifyDetails.get(position).notification_image);
                notifyDetails.get(position).notification_image = Constants.imagBaseUrl + notifyDetails.get(position).notification_image.trim();
            }

//            Log.e("Picasso", "Final---------- " + notifyDetails.get(position).notification_image);
            //Picasso.with(mActivity).load(notifyDetails.get(position).notification_image).into(holder.notifyImage);

            Picasso.with(mActivity)
                    .load(notifyDetails.get(position).notification_image)
                    .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(holder.notifyImage);

//            Log.e("setDrawerImage", "setDrawerImage " + notifyDetails.get(position).notification_image);
//            Log.e("Genral Notification", "onBindViewHolder " + notifyDetails.get(position).notification_image);
        } else
            holder.notifyImage.setBackgroundResource(R.drawable.no_image);
//        String str=CommonClass.strEncodeDecode(notifyDetails.get(position).notification_name.trim(),true);

    }


    public ArrayList<Notification>  getAllData()
    {
        return notifyDetails;
    }
    @Override
    public int getItemCount() {
        return notifyDetails.size();
    }

    private void responseGroupRequest(final int mPosition, final String strRequest) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.responceGroupRequest, deleteSuccessLisner(),
                deleteErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
//    user_id=11&group_id=73&status=accept
                requestparam.put("user_id", "" + CommonClass.getUserpreference(mActivity).user_id);
                requestparam.put("group_id", notifyDetails.get(mPosition).notification_id);
                requestparam.put("status", strRequest);
                Log.e(" Request", "==> " + Constants.responceGroupRequest + "--" + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(mActivity);
        mQueue.add(apiRequest);
    }

    private void responseFriendRequest(final int mPosition, final String strRequest) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.sendFriendRequest, deleteSuccessLisner(),
                deleteErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();

                requestparam.put("user_id", "" + CommonClass.getUserpreference(mActivity).user_id);
                requestparam.put("friend_id", notifyDetails.get(mPosition).notification_id);
                requestparam.put("request_id", notifyDetails.get(mPosition).request_id);
                requestparam.put("status", strRequest);
//                Log.e(" Request", "==> " + Constants.sendFriendRequest + "--" + requestparam);
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
                        //  mUserListData.get(mPosition).setStatus("Pending");
                        if (notifyDetails.get(mPosition).notification_id != null) {
                            ChatDatabase chatDatabase = ChatDatabase.getInstance(mActivity);
                            chatDatabase.deleteHistoryRecord(CommonClass.getChatUsername(mActivity), "cync_" + notifyDetails.get(mPosition).notification_id);
                        }
                        notifyDetails.remove(mPosition);
//                        notifyDetails.remove(notifyDetails.get(mPosition).notification_id);
                        CommonClass.ShowToast(mActivity, message);

                        notifyDataSetChanged();
                    } else {
                        CommonClass.ShowToast(mActivity, message);
                        notifyDataSetChanged();
                    }
                    setText.setText(notifyDetails);

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

    public interface SetFragment {
        public void setText(ArrayList<Notification> mNotifications);
    }

    public void alertDialog(final Activity context, String steMessage, final int mPosition, final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(steMessage);
        builder.setCancelable(false);
        builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (index == 1)
                    responseFriendRequest(mPosition, "accept");
                else
                    responseGroupRequest(mPosition, "accept");
            }
        });
        builder.setNegativeButton("Disagree", null);
        builder.show();
    }
}

