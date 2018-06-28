package com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.app.android.cync.R;
import com.webservice.VolleySetup;

import java.util.ArrayList;
import java.util.List;

//import com.rey.material.widget.CheckBox;

/**
 * Created by ketul.patel on 21-01-2016.
 */
public class InviteListAdapter extends RecyclerView.Adapter<InviteListAdapter.ViewHolder> {

    private ArrayList<String> stringList ;
    private Activity mActivity;
    public int selectedPostion = 0;
    private int count;
    private RequestQueue mQueue;
    public InviteListAdapter() {

    }
//    public void clearCache() {
//        FriendListFragment.friendListAdapter.notifyDataSetChanged();
//    }

    public InviteListAdapter(Activity mActivity, ArrayList<String> stringList) {
        this.stringList = stringList;
        this.mActivity = mActivity;
        mQueue = VolleySetup.getRequestQueue();
        count = stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivDelete;
        private TextView tvEmailId;

        public ViewHolder(final View view) {
            super(view);
            ivDelete = (ImageView) view.findViewById(R.id.delete);
            tvEmailId = (TextView) view.findViewById(R.id.email);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stringList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
        }
    }

    private int getCheckedItemCount() {
        int cnt = 0;
        for (int i = 0; i < stringList.size(); i++) {
                cnt++;
        }
        return cnt;
    }

    public List<String> getUserDetailList() {
        return stringList;
    }

    public InviteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_listitem, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String fd = stringList.get(position);
//        Log.e("TAGGGGGGGGG",fd);
        if (fd != null) {
            holder.tvEmailId.setText(fd.toString());
        }
    }

    @Override
    public int getItemCount() {
        return (null != stringList ? stringList.size() : 0);
    }

    public int deleteSelectedRecord() {
        stringList.remove(selectedPostion);
        notifyItemRemoved(selectedPostion);
        return selectedPostion;
    }


    public ArrayList<String> getCheckedItems() {

        return stringList;
    }

}


