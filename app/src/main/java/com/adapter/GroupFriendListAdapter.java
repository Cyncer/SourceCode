package com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.FriendDetail;
import com.cync.model.FriendListDetail;
import com.fragment.GroupFriendListFragment;
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
public class GroupFriendListAdapter extends RecyclerView.Adapter<GroupFriendListAdapter.ViewHolder> {

    private List<FriendDetail> friendDetailList = Collections.emptyList();
    private Activity mActivity;
    public int selectedPostion = 0;
    private int count;
    boolean isAllChecked;
    boolean temp = false;

    public GroupFriendListAdapter() {

    }
//    public void clearCache() {
//        FriendListFragment.friendListAdapter.notifyDataSetChanged();
//    }

    public GroupFriendListAdapter(Activity mActivity, List<FriendDetail> friendDetailList) {
        this.friendDetailList = friendDetailList;
        this.mActivity = mActivity;
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
            chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView != null) {
                        friendDetailList.get(getAdapterPosition()).setSelected(isChecked);
                        GroupFriendListFragment.select_cnt.setText(getCheckedItemCount() + " " + "SELECTED");
                    }
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

    public List<FriendDetail> getGroupFriendDetailList() {
        return friendDetailList;
    }

    public GroupFriendListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_group_list_view_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FriendDetail fd = friendDetailList.get(position);
        if (friendDetailList.get(position) != null) {
//            String imagePath = friendDetailList.get(position).friendImage;
          //  Log.e("GroupInfo", "IF---------- " + fd.friendImage);
            if (fd.friendImage != null && fd.friendImage.length() > 0) {
//                if(CommonClass.getUserpreference(mActivity).login_type.equalsIgnoreCase("facebook")){
//                    if(friendDetailList.get(position).friendImage.contains("https://")||friendDetailList.get(position).friendImage.contains("http://"))
//                        friendDetailList.get(position).friendImage=friendDetailList.get(position).friendImage.trim();
//                    else
//                        friendDetailList.get(position).friendImage = Constants.imagBaseUrl + friendDetailList.get(position).friendImage.trim();
//                }else
//                    friendDetailList.get(position).friendImage = Constants.imagBaseUrl + friendDetailList.get(position).friendImage.trim();
//
//                Picasso.with(mActivity).load(friendDetailList.get(position).friendImage).into(holder.friendIcon);
//                Log.e("Genral Notification", "onBindViewHolder " + friendDetailList.get(position).friendImage);
                if (fd.friendImage.startsWith("http")) {
//                    if(userDetailList.get(position).user_image.contains("https://")||userDetailList.get(position).user_image.contains("http://")) {
                    fd.friendImage = friendDetailList.get(position).friendImage.trim();
                  //  Log.e("GroupInfo", "IF---------- " + friendDetailList.get(position).friendImage);
                } else {
                   // Log.e("GroupInfo", "ELSE---------- " + friendDetailList.get(position).friendImage);
                    friendDetailList.get(position).friendImage = Constants.imagBaseUrl + friendDetailList.get(position).friendImage.trim();
                }

               // Log.e("Picasso", "Final---------- " + friendDetailList.get(position).friendImage);
                Picasso.with(mActivity)
                        .load(friendDetailList.get(position).friendImage)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(holder.friendIcon);

                // Log.e("setDrawerImage", "setDrawerImage " + friendDetailList.get(position).friendImage);
            } else
                holder.friendIcon.setImageResource(R.drawable.no_image);
//            Log.e("friendDetailList", "-------" + imagePath);
//            if (imagePath != null && imagePath.length() > 0) {
//                imagePath = Constants.imagBaseUrl + imagePath.trim();
//                Log.e("friendDetailList", "-------" + imagePath);
//                Picasso.with(mActivity).load(imagePath).into(holder.friendIcon);
//            } else
//                holder.friendIcon.setImageResource(R.drawable.no_image);
//            String strName= CommonClass.strEncodeDecode(friendDetailList.get(position).friendName,true);
            holder.friendName.setText(friendDetailList.get(position).friendName);
        }
        holder.chkSelected.setChecked(fd.isSelected());
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

    public interface GetSelectedItem {
        public void getSelectedItem(ArrayList<String> strFriendId);
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


