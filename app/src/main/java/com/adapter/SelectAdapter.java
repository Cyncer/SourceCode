package com.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.android.cync.R;
import com.cync.model.FriendListDetail;
import com.rey.material.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by ketul.patel on 28/1/16.
 */
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {

    private List<FriendListDetail> groupFriendList= Collections.emptyList();

    public SelectAdapter(List<FriendListDetail> groupFriendList)
    {
        this.groupFriendList = groupFriendList;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private android.widget.TextView friendIcon;
        private android.widget.TextView friendName;
        private ImageView imgDelete;

        public ViewHolder(View view) {
            super(view);


            friendIcon = (TextView) view.findViewById(R.id.friendIcon);
            friendName = (android.widget.TextView) view.findViewById(R.id.friendName);
            imgDelete = (ImageView) view.findViewById(R.id.deleteImg);

        }
    }


    @Override
    public SelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_view, parent, false);
        ViewHolder vh = new ViewHolder(view);
        view.findViewById(R.id.deleteImg);

        return vh;
    }

    @Override
    public void onBindViewHolder(SelectAdapter.ViewHolder holder, int position) {


        holder.friendIcon.setText(groupFriendList.get(position).getFriendIcon()); //Image
        holder.friendName.setText(groupFriendList.get(position).getFriendName()); //Name


//        holder.imgDelete.setImageDrawable();
      //  holder.imgDelete.setImageDrawable(groupFriendList.get(position).getDeleteIcon());
    //    holder.imgDelete.setImageResource(groupFriendList.get);

        //imageview= (ImageView)findViewById(R.id.imageView);
        //Drawable res = getResources().getDrawable(holder.imgDelete);
        //imageView.setImageDrawable(res);

  //      holder.imgDelete.setImageDrawable(holder.);
    }

    @Override
    public int getItemCount() {
        return 0;
    }


}
