package com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.ChatMessageNotification;
import com.squareup.picasso.Picasso;
import com.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatHistoryAdapter2 extends RecyclerView
        .Adapter<ChatHistoryAdapter2
        .DataObjectHolder> {
    private static String LOG_TAG = "ChatHistoryAdapter";
    private ArrayList<ChatMessageNotification> mDataset;
    private static MyClickListener myClickListener;
    Activity mActivity;
    Date date;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView chathistory_friendname;
        TextView dateTime;
        ImageView chathistory_profile;
        TextView chathistory_msg;
        TextView chathistory_msgcount;
        TextView chathistory_time;
        FrameLayout frame_msgcount;


        public DataObjectHolder(View itemView) {
            super(itemView);
           /* label = (TextView) itemView.findViewById(R.id.textView);
            dateTime = (TextView) itemView.findViewById(R.id.textView2);*/
            chathistory_profile = (ImageView) itemView.findViewById(R.id.chathistory_profile_iv);
            chathistory_friendname = (TextView) itemView.findViewById(R.id.chathistory_friendname_tv);
            chathistory_msg = (TextView) itemView.findViewById(R.id.chathistory_msg_tv);
            chathistory_msgcount = (TextView) itemView.findViewById(R.id.chathistory_msgcount_tv);
            chathistory_time = (TextView) itemView.findViewById(R.id.chathistory_time_tv);
            frame_msgcount = (FrameLayout) itemView.findViewById(R.id.frame_msgcount);


            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public ChatHistoryAdapter2(Activity mActivity, ArrayList<ChatMessageNotification> myDataset) {

        mDataset = myDataset;
        this.mActivity = mActivity;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_history_item, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        String id = mDataset.get(position).chatMessages.getReceiverName();

        String imagePath = mDataset.get(position).profileImage;

        if (imagePath != null && imagePath.length() > 0) {
            if (imagePath.startsWith("http")) {
                imagePath = imagePath.trim();
//                Log.e("GroupInfo", "IF---------- " + imagePath);
            } else {
//                Log.e("GroupInfo", "ELSE---------- " + imagePath);
                imagePath = Constants.imagBaseUrl + imagePath.trim();
            }
            Log.e("Picasso", "Image Final---------- " + imagePath);


            Picasso.with(mActivity)
                    .load(imagePath)
                    .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                    .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                    .into(holder.chathistory_profile);
        }

        holder.chathistory_friendname.setText(mDataset.get(position).name);
        holder.chathistory_msg.setText(mDataset.get(position).chatMessages.getMessage());

        String count = mDataset.get(position).count;
        holder.chathistory_msgcount.setText(count);

        int tCount = Integer.parseInt(count);

        if (tCount == 0) {
            holder.frame_msgcount.setVisibility(View.INVISIBLE);

        }
        String dtStart = "2010-10-15T09:27:37Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(mDataset.get(position).chatMessages.getTimestamp());

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long token_date = date.getTime();

        long curr_date = new Date().getTime();

        long diff = curr_date - token_date;

        long seconds = diff / 1000;
        long totalminutes = seconds / 60;
        int hours = (int) (totalminutes / 60);
        int days = hours / 24;


        int min = (int) totalminutes % 60;
        int actual_hours = (int) hours % 24;

        System.out.println("DAYS " + days + " HOURS " + hours + "TOTAL MINUTES " + totalminutes + "  ACTUAL MINUTES " + min);


        String time = "";

        time = min + " minutes ago";
        if (days >= 1) {
            time = days + " days " + actual_hours + " hours " + min + " minutes";
        } else if (hours >= 1) {
            time = hours + " hours " + min + " minutes ago";
        }

        holder.chathistory_time.setText(time);
    }

    public void addItem(ChatMessageNotification dataObj, int index) {
        mDataset.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}