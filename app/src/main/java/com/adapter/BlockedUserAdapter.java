package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.android.cync.R;
import com.cync.model.BlockedUser;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.List;


public class BlockedUserAdapter extends RecyclerView.Adapter<BlockedUserAdapter.MyViewHolder> {


    private Context mContext;
    private List<BlockedUser> mBlockedList;

    DisplayImageOptions options;
    ImageLoader imageLoader;
    boolean showLoading = false;
    UnblockClickListner mUnblockClickListner;

    public interface UnblockClickListner{

        public void onUnblock(int pos);
    }

    public BlockedUserAdapter(Activity mContext, List<BlockedUser> mBlockedList ,UnblockClickListner mUnblockClickListner) {



        this.mUnblockClickListner = mUnblockClickListner;
        this.mContext = mContext;
        this.mBlockedList = mBlockedList;

        System.out.println("mActivity Adapter===" + mContext);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.nobnner).showImageOnFail(R.drawable.nobnner).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

    }


    public void showLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_blocked_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final BlockedUser mBlocked = mBlockedList.get(position);


        

        holder.txtName.setText(mBlocked.user_name);
        



        setUImage(holder.imgProfile,mBlocked.user_image);


        holder.btnUnblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUnblockClickListner.onUnblock(position);
            }
        });



//
    }


    public void setUImage(ImageView imgView, String url)
    {
        imageLoader.displayImage(url, imgView, options, new SimpleImageLoadingListener() {
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




    private void showPopupMenu(View view, int type) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();

        if (type == 0)
            inflater.inflate(R.menu.menu_tank_list1, popup.getMenu());
        else
            inflater.inflate(R.menu.menu_tank_list2, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete_post:
                    Toast.makeText(mContext, "Delete Post", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_report_post:
                    Toast.makeText(mContext, "Report Post", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_block_user:
                    Toast.makeText(mContext, "Block User", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


    @Override
    public int getItemCount() {
        // return 10;
        return mBlockedList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mBlockedList.clear();
        notifyDataSetChanged();
    }


    /**
     * Showing popup menu when tapping on 3 dots
     * <p>
     * private void showPopupMenu(View view) {
     * // inflate menu
     * PopupMenu popup = new PopupMenu(mContext, view);
     * MenuInflater inflater = popup.getMenuInflater();
     * inflater.inflate(R.menu.menu_album, popup.getMenu());
     * popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
     * popup.show();
     * }
     */

    public void RemoveItem(int position) {


        mBlockedList.remove(position);

    }

    // Add a list of items
    public void addAll(List<BlockedUser> list) {
        mBlockedList.addAll(list);
        notifyDataSetChanged();
    }


    public List<BlockedUser> getAll() {

        if (mBlockedList != null) {
            return mBlockedList;
        } else {
            return new ArrayList<>();
        }
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView imgProfile;
        TextView txtName;
        Button btnUnblock;



        public MyViewHolder(View view) {
            super(view);




            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            txtName = (TextView) view.findViewById(R.id.txtName);
            btnUnblock = (Button) view.findViewById(R.id.btnUnblock);


        }
    }


}
