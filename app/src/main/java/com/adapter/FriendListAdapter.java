package com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.cync.ChatActivity;
import com.app.android.cync.R;
import com.cync.model.FriendDetail;
import com.cync.model.FriendListDetail;
import com.fragment.FriendListFragment;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;
import com.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.rey.material.widget.CheckBox;

/**
 * Created by ketul.patel on 21-01-2016.
 */
public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {

    private List<FriendDetail> friendDetailList = Collections.emptyList();
    private Activity mActivity;
    public int selectedPostion = 0;
    private int count;
    com.rey.material.widget.CheckBox checkAll;
    boolean isAllChecked;
    boolean temp = false;

    public FriendListAdapter() {

    }
//    public void clearCache() {
//        FriendListFragment.friendListAdapter.notifyDataSetChanged();
//    }

    public FriendListAdapter(Activity mActivity, List<FriendDetail> friendDetailList, com.rey.material.widget.CheckBox checkAll) {
        this.friendDetailList = friendDetailList;
        this.mActivity = mActivity;
        this.checkAll = checkAll;
        count = friendDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //        private com.rey.material.widget.CheckedImageView friendIcon;
        //private com.rey.material.widget.TextView friendIcon;
        private ImageView friendIcon;
        private android.widget.TextView friendName;
        public FriendListDetail friendList;
        public CheckBox chkSelected;
        public ViewHolder(final View view) {
            super(view);
            friendIcon = (ImageView) view.findViewById(R.id.friendIcon);
            chkSelected = (com.rey.material.widget.CheckBox) view.findViewById(R.id.chkSelected);
            friendName = (TextView) view.findViewById(R.id.friendName);
            checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    Log.d("all", "onCheckedChanged: temp = " + temp);
                    if (!temp) {
                        for (int i = 0; i < friendDetailList.size(); i++) {
                            friendDetailList.get(i).setSelected(isChecked);
                        }
                        FriendListFragment.select_cnt.setText(getCheckedItemCount() + " " + "SELECTED");
                        notifyDataSetChanged();
                    }
                    temp = false;
                }
            });
            chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView != null) {
                        friendDetailList.get(getAdapterPosition()).setSelected(isChecked);
                        int checkedItemCount = getCheckedItemCount();
                        if (getItemCount() == checkedItemCount) {
                            if (!checkAll.isChecked()) {
                                temp = true;
//                          Log.d("all", "chkSelected: temp = " + temp);
                                checkAll.setChecked(true);
                            }
                        } else {
                            if (checkAll.isChecked()) {
                                temp = true;
//                          Log.d("all", "chkSelected: temp = " + temp);
                                checkAll.setChecked(false);
                            }
                        }
                        FriendListFragment.select_cnt.setText(getCheckedItemCount() + " " + "SELECTED");
                    }
                }
            });
//            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   Intent i = new Intent(mActivity, ChatActivity.class);
                    Bundle data = new Bundle();
//                    String strName= CommonClass.strEncodeDecode(friendDetailList.get(getAdapterPosition()).friendName,true);
                    data.putString("FROM_MESSGE", "cync_" + friendDetailList.get(getAdapterPosition()).friendId);
                    data.putString("titlename", friendDetailList.get(getAdapterPosition()).friendName);
                    data.putString("profileimage", friendDetailList.get(getAdapterPosition()).friendImage);
                    data.putString("type", "1");
                    i.putExtras(data);
                    mActivity.startActivity(i);
                }
            });
        }


    }

    private int getCheckedItemCount() {
        int cnt = 0;
        for (int i = 0; i < friendDetailList.size(); i++) {
            if (friendDetailList.get(i).isSelected())
                cnt++;
        }

        return cnt;
    }

    public List<FriendDetail> getFriendDetailList() {
        return friendDetailList;
    }

    public FriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FriendDetail fd = friendDetailList.get(position);
        if (friendDetailList.get(position) != null) {
            if (friendDetailList.get(position).friendImage != null && friendDetailList.get(position).friendImage.length() > 0) {
//                if (friendDetailList.get(position).friendImage.startsWith("http")) {
//                    friendDetailList.get(position).friendImage = friendDetailList.get(position).friendImage.trim();
//                   // Log.e("GroupInfo", "IF---------- " + friendDetailList.get(position).friendImage);
//                } else {
//                   // Log.e("GroupInfo", "ELSE---------- " + friendDetailList.get(position).friendImage);
//                    friendDetailList.get(position).friendImage = Constants.imagBaseUrl + friendDetailList.get(position).friendImage.trim();
//                }
                //Log.e("Picasso", "Final---------- " + friendDetailList.get(position).friendImage);





                


                String imagePath = friendDetailList.get(position).friendImage;
                if (imagePath != null && imagePath.length() > 0 ) {
                    if(friendDetailList.get(position).type.equalsIgnoreCase("facebook")){
                        if(imagePath.contains("https://")||imagePath.contains("http://"))
                            imagePath=imagePath.trim();
                        else
                            imagePath = Constants.imagBaseUrl + imagePath.trim();
                    }else
                        imagePath = Constants.imagBaseUrl + imagePath.trim();

               Log.e("imagePath", "-------" + imagePath);

                    Picasso.with(mActivity)
                            .load(imagePath)
                            .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                            .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                            .into(holder.friendIcon);

                }
                else
                    holder.friendIcon.setImageResource(R.drawable.no_image);




                //Log.e("setDrawerImage", "setDrawerImage " + friendDetailList.get(position).friendImage);
            } else
                holder.friendIcon.setImageResource(R.drawable.no_image);
//            String strName= CommonClass.strEncodeDecode(friendDetailList.get(position).friendName,true);
            holder.friendName.setText(friendDetailList.get(position).friendName);
        }
        holder.chkSelected.setChecked(fd.isSelected());
        if (FriendListFragment.checkAllCheckbox) {
            holder.chkSelected.setVisibility(View.VISIBLE);
        } else {
            holder.chkSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != friendDetailList ? friendDetailList.size() : 0);
    }

    public int deleteSelectedRecord() {
        friendDetailList.remove(selectedPostion);
        notifyItemRemoved(selectedPostion);
        return selectedPostion;
    }
    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for (int i = 0; i < friendDetailList.size(); i++) {
            if (friendDetailList.get(i).isSelected()) {
                mTempArry.add(friendDetailList.get(i).friendId);
            }
        }
        return mTempArry;
    }
}




