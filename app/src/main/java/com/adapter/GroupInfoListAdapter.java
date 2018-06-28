package com.adapter;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.R;
import com.cync.model.FriendListDetail;
import com.cync.model.UserDetail;
import com.fragment.BaseContainerFragment;
import com.fragment.GroupInfoFragment;
import com.fragment.OtherUserListFragment;
import com.squareup.picasso.Picasso;
import com.utils.CommonClass;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.rey.material.widget.CheckBox;

/**
 * Created by ketul.patel on 21-01-2016.
 */
public class GroupInfoListAdapter extends RecyclerView.Adapter<GroupInfoListAdapter.ViewHolder> {

    private List<UserDetail> userDetailList = Collections.emptyList();
    private BaseContainerFragment mActivity;
    public int selectedPostion = 0;
    private int count;
    //    CheckBox checkAll;
    private RequestQueue mQueue;
    boolean isAllChecked;
    boolean temp = false;
    private String groupId;
    boolean isAdmin;
    int mPosition;

    public GroupInfoListAdapter() {

    }
//    public void clearCache() {
//        FriendListFragment.friendListAdapter.notifyDataSetChanged();
//    }

    public GroupInfoListAdapter(BaseContainerFragment mActivity, List<UserDetail> userDetailList, /*CheckBox checkAll,*/ String groupId, boolean isAdmin) {
        this.userDetailList = userDetailList;
        this.mActivity = mActivity;
//        this.checkAll = checkAll;
        this.isAdmin = isAdmin;
        this.groupId = groupId;
        mQueue = VolleySetup.getRequestQueue();
        count = userDetailList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //        private com.rey.material.widget.CheckedImageView friendIcon;
        //private com.rey.material.widget.TextView friendIcon;
        private ImageView friendIcon, ivImage;
        private TextView friendName, userType;
        private com.rey.material.widget.TextView tvDelete/*,tvAdd*/;
        //        private android.widget.RelativeLayout cardView;
        private CardView cvDelete;
        public FriendListDetail friendList;
//        public CheckBox chkSelected;

        public ViewHolder(final View view) {
            super(view);
            friendIcon = (ImageView) view.findViewById(R.id.friendIcon);
            userType = (TextView) view.findViewById(R.id.userType);
//            chkSelected = (CheckBox) view.findViewById(R.id.chkSelected);
            friendName = (TextView) view.findViewById(R.id.friendName);
            tvDelete = (com.rey.material.widget.TextView) view.findViewById(R.id.tvDelete);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OtherUserListFragment fragment = new OtherUserListFragment();
                    if (fragment != null) {
                        Bundle bundle = new Bundle();
                        System.out.println("ViewHolder.onClick"+userDetailList.get(getAdapterPosition()).first_name + " " + userDetailList.get(getAdapterPosition()).last_name);
                        bundle.putString("search_name", userDetailList.get(getAdapterPosition()).first_name+ " " + userDetailList.get(getAdapterPosition()).last_name);
                        fragment.setArguments(bundle);
                        mActivity.replaceFragment(fragment, true, "OtherUser");
                    }
                }
            });
//            tvAdd = (com.rey.material.widget.TextView) view.findViewById(R.id.tvAdd);
            cvDelete= (CardView) view.findViewById(R.id.cvDelete);
            GroupInfoFragment.tvCountMember.setText(/*String.format("%02d", getCheckedItemCount())+*/"  " + String.format("%02d", userDetailList.size()) + "  ");
//

            //----------------
            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPosition = getAdapterPosition();

                    deleteGroupMember();
                }
            });
//            tvAdd.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mPosition = getAdapterPosition();
//                    deleteGroupMember();
//                }
//            });
//            }
        }

    }

    private int getCheckedItemCount() {
        int cnt = 0;
        for (int i = 0; i < userDetailList.size(); i++) {
            if (userDetailList.get(i).isSelected())
                cnt++;
        }
        return cnt;
    }

    public List<UserDetail> getUserDetailList() {
        return userDetailList;
    }

    public GroupInfoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_info_listview_item, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final UserDetail userDetail = userDetailList.get(position);
        if (userDetailList.get(position) != null) {
            if (userDetailList.get(position).user_image != null && userDetailList.get(position).user_image.length() > 0) {
                if (userDetailList.get(position).user_image.startsWith("http")) {
                    userDetailList.get(position).user_image = userDetailList.get(position).user_image.trim();
                } else {
                    userDetailList.get(position).user_image = Constants.imagBaseUrl + userDetailList.get(position).user_image.trim();
                }


                Picasso.with(mActivity.getActivity())
                        .load(userDetailList.get(position).user_image)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(holder.friendIcon);

            } else
                holder.friendIcon.setImageResource(R.drawable.no_image);
            holder.friendName.setText(userDetailList.get(position).first_name + " " + userDetailList.get(position).last_name);
        }
        //Log.e("GroupLocation",""+userDetailList.get(position).group_location);


        if (userDetailList.get(position).user_type.equalsIgnoreCase("admin")) {
            holder.userType.setVisibility(View.VISIBLE);
        } else {
            holder.userType.setVisibility(View.GONE);
        }
        if (isAdmin)
            holder.tvDelete.setVisibility(View.VISIBLE);
        else {
            holder.tvDelete.setVisibility(View.GONE);
            holder.cvDelete.setVisibility(View.GONE);
            if (userDetailList.get(position).group_is_friend) {
                holder.ivImage.setVisibility(View.GONE);
            } else {
                holder.ivImage.setVisibility(View.VISIBLE);
            }
        }
        //Log.e("isSelected()"+userDetailList.get(position).isSelected," ----"+userDetailList.get(position).isSelected());

//            holder.cardView.setBackgroundColor(mActivity.getResources().getColor(R.color.unfriend_color));
        if (userDetailList.get(position).user_id.equalsIgnoreCase(CommonClass.getUserpreference(mActivity.getActivity()).user_id)) {
            holder.tvDelete.setVisibility(View.GONE);
            holder.ivImage.setVisibility(View.GONE);
            holder.cvDelete.setVisibility(View.GONE);
//            holder.tvAdd.setVisibility(View.GONE);
//            holder.cardView.setBackgroundColor(mActivity.getActivity().getResources().getColor(R.color.white));
        }
        //holder.chkSelected.setChecked(userDetailList.get(position).isSelected());
    }

    @Override
    public int getItemCount() {
        return (null != userDetailList ? userDetailList.size() : 0);
    }

    public int deleteSelectedRecord() {
        userDetailList.remove(selectedPostion);
        notifyItemRemoved(selectedPostion);
        return selectedPostion;
    }

    public interface GetSelectedItem {
        public void getSelectedItem(ArrayList<String> strFriendId);
    }

    public ArrayList<String> getCheckedItems() {
        ArrayList<String> mTempArry = new ArrayList<String>();

        for (int i = 0; i < userDetailList.size(); i++) {
            if (/*userDetailList.get(i).isSelected()&&*/!(userDetailList.get(i).user_id.equalsIgnoreCase(CommonClass.getUserpreference(mActivity.getActivity()).user_id))) {
                mTempArry.add(userDetailList.get(i).user_id);
            }
        }
        return mTempArry;
    }


    private void deleteGroupMember() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.delete_group_member, deleteGroupMemberSuccessLisner(),
                deleteGroupMemberErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(mActivity.getActivity()).user_id);
                requestparam.put("group_id", groupId);
                requestparam.put("friend_id", userDetailList.get(mPosition).user_id);
//                Log.e("deleteGroups Request", "==> " + Constants.remove_group + "" + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(mActivity.getActivity());
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> deleteGroupMemberSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(mActivity.getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        CommonClass.ShowToast(mActivity.getActivity(), message);
                    } else {
                        CommonClass.ShowToast(mActivity.getActivity(), message);
                    }
                    userDetailList.remove(mPosition);
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    message = mActivity.getActivity().getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(mActivity.getActivity(), message);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener deleteGroupMemberErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("Json Error", "==> " + error.getMessage());
                CommonClass.closeLoding(mActivity.getActivity());
                CommonClass.ShowToast(mActivity.getActivity(), mActivity.getString(R.string.s_wrong));
            }
        };
    }

    //------------------------
    private void sendRequestFriend(final int mPosition) {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.sendFriendRequest, deleteSuccessLisner(),
                deleteErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(mActivity.getActivity()).user_id);
                requestparam.put("friend_id", userDetailList.get(mPosition).user_id);
//                Log.e("getFriend List Request", "==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(mActivity.getActivity());
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> deleteSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {
            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
//                Log.d("SuccessLisner Json", "==> " + response);
                CommonClass.closeLoding(mActivity.getActivity());
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        userDetailList.get(mPosition).setStatus("Pending");
                        CommonClass.ShowToast(mActivity.getActivity(), message);
                    } else {
                        CommonClass.ShowToast(mActivity.getActivity(), message);
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    message = mActivity.getActivity().getString(R.string.s_wrong);
                    CommonClass.ShowToast(mActivity.getActivity(), message);
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
                CommonClass.closeLoding(mActivity.getActivity());
                CommonClass.ShowToast(mActivity.getActivity(), mActivity.getString(R.string.s_wrong));
            }
        };
    }
}


