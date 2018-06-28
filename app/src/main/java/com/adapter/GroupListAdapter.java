package com.adapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.GroupListItem;
import com.fragment.BaseContainerFragment;
import com.fragment.GroupInfoFragment;
import com.fragment.GroupListFragment;
import com.rey.material.widget.CheckBox;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.rey.material.widget.CheckBox;

/**
 * Created by ketul.patel on 21-01-2016.
 */
public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.ViewHolder> {

    private List<GroupListItem> groupListDetails = Collections.emptyList();
    private BaseContainerFragment mActivity;
    public int selectedPostion = 0;
    private int count;
    com.rey.material.widget.CheckBox checkAll;
    boolean isAllChecked;
    boolean temp = false;

    public GroupListAdapter() {

    }
//    public void clearCache() {
//        FriendListFragment.friendListAdapter.notifyDataSetChanged();
//    }

    public GroupListAdapter(BaseContainerFragment mActivity, List<GroupListItem> groupListDetails, com.rey.material.widget.CheckBox checkAll) {
        this.groupListDetails = groupListDetails;
        this.mActivity = mActivity;
        this.checkAll = checkAll;
        count = groupListDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //        private com.rey.material.widget.CheckedImageView friendIcon;
        //private com.rey.material.widget.TextView friendIcon;
        private ImageView friendIcon;
        private android.widget.TextView friendName;
        public CheckBox chkSelected;

        public ViewHolder(final View view) {
            super(view);
            friendIcon = (ImageView) view.findViewById(R.id.friendIcon);
            chkSelected = (com.rey.material.widget.CheckBox) view.findViewById(R.id.chkSelected);
            friendName = (TextView) view.findViewById(R.id.friendName);
            checkAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   // Log.d("all", "onCheckedChanged: temp = " + temp);
                    if (!temp) {
                        for (int i = 0; i < groupListDetails.size(); i++) {
                            groupListDetails.get(i).setSelected(isChecked);
                        }
                        GroupListFragment.select_cnt.setText(getCheckedItemCount() + " " + "SELECTED");
                        notifyDataSetChanged();
                    }
                    temp = false;
                }
            });
            chkSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView != null) {
                        groupListDetails.get(getAdapterPosition()).setSelected(isChecked);
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
                        GroupListFragment.select_cnt.setText(getCheckedItemCount() + " " + "SELECTED");
                    }
                }
            });
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    GroupListFragment fragment=new GroupListFragment();
//                    GroupInfoFragment eventFragment =new GroupInfoFragment();
//                    CommonClass.ShowToast(mActivity.getActivity(), "onClick：" + getPosition());
//                    if(new GroupInfoFragment() !=null)
                    if(groupListDetails.get(getAdapterPosition()).group_id!=null&&groupListDetails.get(getAdapterPosition()).group_id.trim().length()>0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("group_id", groupListDetails.get(getAdapterPosition()).group_id);
                        //set Fragmentclass Arguments
                        GroupInfoFragment fragment = new GroupInfoFragment();
                        fragment.setArguments(bundle);
                        mActivity.replaceFragment(fragment, true, "GroupInfo");
                    }else{
                        CommonClass.ShowToast(mActivity.getActivity(),"No Group data.");
                    }
                }
            });
        }
    }

    private int getCheckedItemCount() {
        int cnt = 0;
        for (int i = 0; i < groupListDetails.size(); i++) {
            if (groupListDetails.get(i).isSelected())
                cnt++;
        }
        return cnt;
    }

    public List<GroupListItem> getFriendDetailList() {
        return groupListDetails;
    }

    public GroupListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int itemPosition = mRecyclerView.getChildPosition(view);
//                String item = mList.get(itemPosition);
//                Toast.makeText(mContext, item, Toast.LENGTH_LONG).show();
//            }
//        });
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final GroupListItem fd = groupListDetails.get(position);
        if (groupListDetails.get(position) != null) {
//            String imagePath = groupListDetails.get(position).group_image;
            if (groupListDetails.get(position).group_image != null && groupListDetails.get(position).group_image.length() > 0) {
//                if(CommonClass.getUserpreference(mActivity.getActivity()).login_type.equalsIgnoreCase("facebook")){
//                    if(groupListDetails.get(position).group_image.contains("https://")||groupListDetails.get(position).group_image.contains("http://"))
//                        groupListDetails.get(position).group_image=groupListDetails.get(position).group_image.trim();
//                    else
//                        groupListDetails.get(position).group_image = Constants.imagBaseUrl + groupListDetails.get(position).group_image.trim();
//                }else
//                    groupListDetails.get(position).group_image = Constants.imagBaseUrl + groupListDetails.get(position).group_image.trim();
//
//                Picasso.with(mActivity.getActivity()).load(groupListDetails.get(position).group_image).into(holder.friendIcon);
//                Log.e("setDrawerImage", "setDrawerImage " + groupListDetails.get(position).group_image);
                if (groupListDetails.get(position).group_image.startsWith("http")) {
//                    if(userDetailList.get(position).user_image.contains("https://")||userDetailList.get(position).user_image.contains("http://")) {
                    groupListDetails.get(position).group_image = groupListDetails.get(position).group_image.trim();
//                    Log.e("GroupInfo", "IF---------- " + groupListDetails.get(position).group_image);
                } else {
//                    Log.e("GroupInfo", "ELSE---------- " + groupListDetails.get(position).group_image);
                    groupListDetails.get(position).group_image = Constants.imagBaseUrl + groupListDetails.get(position).group_image.trim();
                }

//                Log.e("Picasso", "Final---------- " + groupListDetails.get(position).group_image);

                Picasso.with(mActivity.getActivity())
                        .load(groupListDetails.get(position).group_image)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(holder.friendIcon);
                // Log.e("setDrawerImage", "setDrawerImage " + groupListDetails.get(position).group_image);
            } else
                holder.friendIcon.setImageResource(R.drawable.no_image);
//            if (imagePath != null && imagePath.length() > 0) {
//                imagePath = Constants.imagBaseUrl + imagePath.trim();
////                Log.e("friendDetailList", "-------" + imagePath);
//                Picasso.with(mActivity.getActivity()).load(imagePath).into(holder.friendIcon);
//            } else
//                holder.friendIcon.setImageResource(R.drawable.no_image);

//            try {
//                group_name = URLDecoder.decode(groupListDetails.get(position).group_name,"UTF-8");
//            }catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//          String group_name = CommonClass.strEncodeDecode(groupListDetails.get(position).group_name,true);
            holder.friendName.setText(groupListDetails.get(position).group_name);
        }
        holder.chkSelected.setChecked(fd.isSelected());
        if (GroupListFragment.checkAllCheckbox) {
            holder.chkSelected.setVisibility(View.VISIBLE);
        } else {
            holder.chkSelected.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != groupListDetails ? groupListDetails.size() : 0);
    }

    public int deleteSelectedRecord() {
        groupListDetails.remove(selectedPostion);
        notifyItemRemoved(selectedPostion);
        return selectedPostion;
    }

    public interface GetSelectedItem {
        public void getSelectedItem(ArrayList<String> strFriendId);
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();
        for (int i = 0; i < groupListDetails.size(); i++) {
            if (groupListDetails.get(i).isSelected()) {
                mTempArry.add(groupListDetails.get(i).group_id);
            }
        }
        return mTempArry;
    }
}


