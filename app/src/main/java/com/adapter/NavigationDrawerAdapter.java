package com.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.NavDrawerItem;

import java.util.Collections;
import java.util.List;

/**
 * Created by ketul.patel on 7/1/16.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.MyViewHolder> {
    List<NavDrawerItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;
    TypedArray typedArray,typedArrayActive;
    public  OnClickEvent mOnClickEvent;

    public interface OnClickEvent{

        public void onMainClick(View mView,int pos);
        public void onInfoClick(int pos);

    }

    public NavigationDrawerAdapter(Context context, List<NavDrawerItem> data,OnClickEvent mOnClickEvent) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        typedArray = context.getResources().obtainTypedArray(R.array.nav_drawer_image);
        typedArrayActive = context.getResources().obtainTypedArray(R.array.nav_drawer_image_active);
        this.data = data;
        this.mOnClickEvent=mOnClickEvent;
    }

    public void delete(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.nav_drawer_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        NavDrawerItem current = data.get(position);
        holder.title.setText(current.getTitle());



        if(position==0 || position==1 ||position==4)
        {
            holder.imgInfo.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.imgInfo.setVisibility(View.GONE);
        }
        if(position==8) {
            holder.tvNotification.setVisibility(View.VISIBLE);
//            Log.e("Count--",""+FragmentDrawer.msg_count);
            holder.tvNotification.setText(""+current.count);
        }
        else
            holder.tvNotification.setVisibility(View.GONE);



        if (current.isShowNotify()) {
            holder.viewSelected.setVisibility(View.VISIBLE);
            holder.title.setTextColor(Color.parseColor("#FFFFFF"));
            holder.imageItem.setImageResource(typedArrayActive.getResourceId(position, -1));
        }
        else {
            holder.imageItem.setImageResource(typedArray.getResourceId(position, -1));
            holder.viewSelected.setVisibility(View.INVISIBLE);
            holder.title.setTextColor(Color.parseColor("#9A9A9A"));
        }


        holder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text_message="";

                if(position==0)
                {
                    text_message="Rider Assist is a function that matches you to relevant businesses nearby. The businesses that are matched are only those who can fix or assist with your type of ride. <b>Tap their pindrop to find out more.</b>";
                }
                else  if(position==1)
                {
                    text_message="News Feed is a place for you to post pictures and videos of your pride and joy. You can like and comment on other posts too!";
                }
                else  if(position==4)
                {
                    text_message="This is your hub, posts that you create will all sit here. You'll be able to view them any time without having to scroll endlessly through the News Feed.";
                }


                showPopupMenu(v,text_message);
               // mOnClickEvent.onInfoClick(position);
                //Toast.makeText(context, "Position  "+position, Toast.LENGTH_SHORT).show();
            }
        });

        holder.rlclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickEvent.onMainClick(v,position);
                //Toast.makeText(context, "Position  "+position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public NavDrawerItem getItem(int position){
        if(data.size()>0)
        return data.get(position);
        else
            return null;

    }
    @Override
    public int getItemCount() {
        return (null != data ? data.size() : 0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title,tvNotification;
        View viewSelected;
        ImageView imageItem,imgInfo;
        RelativeLayout rlclick;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            tvNotification = (TextView) itemView.findViewById(R.id.tvNotification);
            viewSelected = (View) itemView.findViewById(R.id.viewSelected);
            imageItem = (ImageView) itemView.findViewById(R.id.image);
            imgInfo = (ImageView) itemView.findViewById(R.id.imgInfo);
            rlclick = (RelativeLayout) itemView.findViewById(R.id.rlclick);
        }
    }


    private void showPopupMenu(View view,String text) {
        // inflate menu
        PopupWindow popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.dialog_info, null);
        popup.setContentView(layout);
        // Set content width and height
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        // Closes the popup window when touch outside of it - when looses focus
        // Clear the default translucent background

        TextView dialog_message = (TextView) layout.findViewById(R.id.dialog_message);


        dialog_message.setText(Html.fromHtml(text));

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Show anchored to button
        popup.setBackgroundDrawable(new BitmapDrawable());
        popup.showAsDropDown(view);
        popup.showAtLocation(view, Gravity.BOTTOM, 0,
                view.getBottom() - 60);
        View container = null;

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            // only for gingerbread and newer versions
            container = (View) popup.getContentView().getParent().getParent();
        } else {
            container = (View) popup.getContentView().getParent();
        }


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.6f;
        wm.updateViewLayout(container, p);
    }
}
