package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.ChatMessageNotification;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


public class ChatNotificationAdapter extends RecyclerView.Adapter<ChatNotificationAdapter.MyViewHolder> {

    private static final String TAG = "ChatNotificationAdapter";
    public OnItemClick mOnItemClick;
    private Context mContext;
    private List<ChatMessageNotification> notificationList;

    DisplayImageOptions options;
    ImageLoader imageLoader;
    boolean showLoading = false;


    String from;

    public ChatNotificationAdapter(Activity mContext, List<ChatMessageNotification> notificationList, OnItemClick mOnItemClick, String from) {


        this.from = from;
        this.mContext = mContext;
        this.notificationList = notificationList;
        this.mOnItemClick = mOnItemClick;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)
                // .displayer(new RoundedBitmapDisplayer(250))
                .showImageForEmptyUri(R.drawable.nobnner).showImageOnFail(R.drawable.nobnner).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

    }


    public void showLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_chat_notification, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final ChatMessageNotification mNotification = notificationList.get(position);


        holder.tvUsreName.setText(mNotification.chatMessages.getReceiverName());


        holder.tvTime.setText(mNotification.chatMessages.getTimestamp());

        if(mNotification.chatMessages.type.equalsIgnoreCase("text"))
        holder.tvMessage.setText(mNotification.chatMessages.getMessage());
        else if(mNotification.chatMessages.type.equalsIgnoreCase("image"))
            holder.tvMessage.setText("Image");
        else if(mNotification.chatMessages.type.equalsIgnoreCase("video"))
            holder.tvMessage.setText("Video");

//        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                mOnItemClick.onItemClick(position);
//            }
//        });






        // holder.progressBar1.setVisibility(View.GONE);

        String pathImage = "";


        imageLoader.displayImage(mNotification.profileImage, holder.imgProfile, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                // holder.progressBar.setProgress(0);
                // holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                // holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                // holder.progressBar.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                // holder.progressBar.setProgress(Math.round(100.0f
                // * current / total));
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * Click listener for popup menu items
     * <p/>
     * class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
     * <p/>
     * public MyMenuItemClickListener() {
     * }
     *
     * @Override public boolean onMenuItemClick(MenuItem menuItem) {
     * switch (menuItem.getItemId()) {
     * case R.id.action_add_favourite:
     * Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
     * return true;
     * case R.id.action_play_next:
     * Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
     * return true;
     * default:
     * }
     * return false;
     * }
     * }
     */


    public ChatMessageNotification getItem(int position) {
        // TODO Auto-generated method stub
        return notificationList.get(position);
    }


    @Override
    public int getItemCount() {
        // return 10;
        return notificationList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        notificationList.clear();
        notifyDataSetChanged();
    }


    public ArrayList<ChatMessageNotification> getAllData() {

        return (ArrayList<ChatMessageNotification>) notificationList;
    }

    ChatMessageNotification getMatch(int position) {
        return ((ChatMessageNotification) getItem(position));
    }


    public List<ChatMessageNotification> getAll() {

        if (notificationList != null) {
            return notificationList;
        } else {
            return new ArrayList<>();
        }
    }


    public interface OnItemClick {

        void onItemClick(int pos);

        void onFavouriteClick(int pos);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsreName, tvTime, tvMessage;
        public ImageView imgProfile;



        public MyViewHolder(View view) {
            super(view);

            tvUsreName = (TextView) view.findViewById(R.id.tvUsreName);
            tvTime = (TextView) view.findViewById(R.id.tvTime);
            tvMessage = (TextView) view.findViewById(R.id.tvMessage);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);



        }
    }


}
