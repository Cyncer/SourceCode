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
import com.cync.model.AssistData;
import com.webservice.VolleySetup;

import java.util.ArrayList;
import java.util.List;

//import com.rey.material.widget.CheckBox;

/**
 * Created by ketul.patel on 21-01-2016.
 */
public class MakeYearModelAdapter extends RecyclerView.Adapter<MakeYearModelAdapter.ViewHolder> {

    private ArrayList<AssistData> stringList ;
    private Activity mActivity;
    public int selectedPostion = 0;
    private int count;
    private RequestQueue mQueue;
    public MakeYearModelAdapter() {

    }
//    public void clearCache() {
//        FriendListFragment.friendListAdapter.notifyDataSetChanged();
//    }

    public MakeYearModelAdapter(Activity mActivity, ArrayList<AssistData> stringList) {
        this.stringList = stringList;
        this.mActivity = mActivity;
        mQueue = VolleySetup.getRequestQueue();
        count = stringList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView MakeName,YearName,ModelName;

        public ViewHolder(final View view) {
            super(view);

            MakeName= (TextView) view.findViewById(R.id.MakeName);
            YearName= (TextView) view.findViewById(R.id.YearName);
            ModelName= (TextView) view.findViewById(R.id.ModelName);
        }
    }

    private int getCheckedItemCount() {
        int cnt = 0;
        for (int i = 0; i < stringList.size(); i++) {
                cnt++;
        }
        return cnt;
    }

    public List<AssistData> getUserDetailList() {
        return stringList;
    }

    public MakeYearModelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assist_listitem, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final AssistData fd = stringList.get(position);
//        Log.e("TAGGGGGGGGG",fd);
        if (fd != null) {
            holder.MakeName.setText(fd.make_name);
            holder.YearName.setText(fd.year);

            String model[]=fd.model.split(",");
            String modeltxt="\n";

            for(int i=0;i<model.length;i++)
            {
                modeltxt=modeltxt+model[i]+"\n";
            }

            holder.ModelName.setText(modeltxt);
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


    public ArrayList<AssistData> getCheckedItems() {

        return stringList;
    }

}


