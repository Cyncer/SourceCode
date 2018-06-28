package com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.FriendDetail;
import com.squareup.picasso.Picasso;
import com.utils.Constants;

import java.util.ArrayList;
import java.util.List;

//import com.rey.material.widget.CheckBox;

/**
 * Created by ketul.patel on 21-01-2016.
 */
public class FriendListSuggetionAdapter extends RecyclerView.Adapter<FriendListSuggetionAdapter.ViewHolder> {


    private static final String TAG = "FriendListSuggetionAdapter";
    private List<FriendDetail> friendDetailList=new ArrayList<>();
    private List<FriendDetail> listpicOrigin=new ArrayList<>();
    private Activity mActivity;
    public int selectedPostion = 0;


    boolean temp = false;

    public FriendListSuggetionAdapter(Activity mActivity, List<FriendDetail> friendDetailList) {
        this.friendDetailList = friendDetailList;
        this.listpicOrigin = friendDetailList;
        this.mActivity = mActivity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //        private com.rey.material.widget.CheckedImageView friendIcon;
        //private com.rey.material.widget.TextView friendIcon;
        private ImageView friendIcon;
        private TextView friendName;


        public ViewHolder(final View view) {
            super(view);
            friendIcon = (ImageView) view.findViewById(R.id.friendIcon);

            friendName = (TextView) view.findViewById(R.id.friendName);

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

    public FriendListSuggetionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_suggetion, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FriendDetail fd = friendDetailList.get(position);
        if (friendDetailList.get(position) != null) {
            if (friendDetailList.get(position).friendImage != null && friendDetailList.get(position).friendImage.length() > 0) {
                if (friendDetailList.get(position).friendImage.startsWith("http")) {
                    friendDetailList.get(position).friendImage = friendDetailList.get(position).friendImage.trim();
                   // Log.e("GroupInfo", "IF---------- " + friendDetailList.get(position).friendImage);
                } else {
                   // Log.e("GroupInfo", "ELSE---------- " + friendDetailList.get(position).friendImage);
                    friendDetailList.get(position).friendImage = Constants.imagBaseUrl + friendDetailList.get(position).friendImage.trim();
                }
                //Log.e("Picasso", "Final---------- " + friendDetailList.get(position).friendImage);

                Picasso.with(mActivity)
                        .load(friendDetailList.get(position).friendImage)
                        .placeholder(R.drawable.no_image) //this is optional the image to display while the url image is downloading
                        .error(R.drawable.no_image)         //this is also optional if some error has occurred in downloading the image this image would be displayed
                        .into(holder.friendIcon);
                //Log.e("setDrawerImage", "setDrawerImage " + friendDetailList.get(position).friendImage);
            } else
                holder.friendIcon.setImageResource(R.drawable.no_image);
//            String strName= CommonClass.strEncodeDecode(friendDetailList.get(position).friendName,true);
            holder.friendName.setText(friendDetailList.get(position).friendName);
        }

    }

    @Override
    public int getItemCount() {
        return (null != friendDetailList ? friendDetailList.size() : 0);
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Log.i(TAG, "performFiltering: =="+constraint.toString());
                final FilterResults oReturn = new FilterResults();
                final List<FriendDetail> results = new ArrayList<FriendDetail>();
                if (listpicOrigin == null)
                    listpicOrigin = friendDetailList;
                if (constraint != null) {
                    if (listpicOrigin != null & listpicOrigin.size() > 0) {
                        for (final FriendDetail g : listpicOrigin) {
                            if (g.friendName.toLowerCase().contains(constraint.toString().toLowerCase()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                friendDetailList = (ArrayList<FriendDetail>) results.values;
                notifyDataSetChanged();

            }
        };

    }

}




