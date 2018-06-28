package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.cync.R;
import com.cync.model.Comments;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.utils.CommonClass;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {


    private Context mContext;
    private List<Comments> mCommentList;
    private List<Comments> listpicOrigin;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    boolean showLoading = false;
    OnItemClick mOnItemClick;
    int clickedPos;

    public interface OnItemClick {

        void onReportClick(int pos);

        void onDeleteClick(int pos);
        void onProfileClick(int cpos,int tpos);



        void onEditClick(int pos);



    }



    public CommentAdapter(Activity mContext, List<Comments> mCommentList,OnItemClick mOnItemClick ) {


        listpicOrigin = mCommentList;
        this.mContext = mContext;
        this.mCommentList = mCommentList;
        this.mOnItemClick=mOnItemClick;

        System.out.println("mActivity Adapter===" + mContext);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.no_image).showImageOnFail(R.drawable.no_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

    }


    public void showLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_comments, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Comments mComment = mCommentList.get(position);

        String cmt=mComment.text;

//        try {
//            cmt= URLDecoder.decode(cmt, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        cmt = StringEscapeUtils.unescapeJava(cmt);

        SpannableString ss = new SpannableString(cmt);

        for (int i = 0; i < mComment.tagList.size(); i++) {


            int start = mComment.tagList.get(i).startIndex;
            int end = mComment.tagList.get(i).startIndex + mComment.tagList.get(i).length;


            Log.i("CyncTank", "============= start===" + start);
            Log.i("CyncTank", "============= end===" + end);

            ss.setSpan(new MyClickableSpan(position, i), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            holder.txtComment.setMovementMethod(LinkMovementMethod.getInstance());
            //TextUtils.dumpSpans();
        }
        holder.txtComment.setText(ss);



        holder.txtName.setText(mComment.user_name);
       // holder.txtComment.setText(cmt);
        holder.txtTime.setText(mComment.ago);


        holder.imgCLick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPos=position;

                if(mComment.post_by.equalsIgnoreCase(CommonClass.getUserpreference(mContext).user_id))
                {

                    if(mComment.user_id.equalsIgnoreCase(CommonClass.getUserpreference(mContext).user_id))
                    {

                        showPopupMenu(holder.imgCLick, 0);
                    }
                    else {
                        showPopupMenu(holder.imgCLick, 1);
                    }

                }
                else
                {
                    if(mComment.user_id.equalsIgnoreCase(CommonClass.getUserpreference(mContext).user_id))
                    {

                        showPopupMenu(holder.imgCLick, 0);
                    }
                    else {
                        showPopupMenu(holder.imgCLick, 2);
                    }
                }


            }
        });



        setUImage(holder.imgProfile,mComment.user_image);

//
    }

    private class MyClickableSpan extends ClickableSpan {
        int cpos;
        int pos;

        private MyClickableSpan(int cpos,int position) {
            this.cpos = cpos;
            this.pos = position;
        }
        @Override
        public void onClick(final View widget) {

            Log.i("CyncFragment","Call OnProfile Click");
            mOnItemClick.onProfileClick(cpos,pos);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            //ds.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ds.setColor(Color.parseColor("#0000ff"));
        }
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
            inflater.inflate(R.menu.menu_comment_mine, popup.getMenu());
        else if (type == 1)
            inflater.inflate(R.menu.menu_comment_otheron_mypost, popup.getMenu());
        else
            inflater.inflate(R.menu.menu_comment_otherpost_other, popup.getMenu());
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
                case R.id.action_edit_comment:
                    mOnItemClick.onEditClick(clickedPos);
                   // Toast.makeText(mContext, "Edit Comment", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_report_comment:
                    mOnItemClick.onReportClick(clickedPos);
                   // Toast.makeText(mContext, "Report Comment", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_delete_comment:
                    mOnItemClick.onDeleteClick(clickedPos);
                    //Toast.makeText(mContext, "Delete comment", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


    @Override
    public int getItemCount() {
        // return 10;
        return mCommentList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        mCommentList.clear();
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


        mCommentList.remove(position);

    }

    // Add a list of items
    public void addAll(List<Comments> list) {
        mCommentList.addAll(list);
        notifyDataSetChanged();
    }


    public List<Comments> getAll() {

        if (mCommentList != null) {
            return mCommentList;
        } else {
            return new ArrayList<>();
        }
    }




    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView imgProfile,imgCLick;
        TextView txtName, txtTime, txtComment;
        RelativeLayout rlTmp;


        public MyViewHolder(View view) {
            super(view);




            rlTmp = (RelativeLayout) view.findViewById(R.id.rlTmp);
            imgCLick = (ImageView) view.findViewById(R.id.imgCLick);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            txtComment = (TextView) view.findViewById(R.id.txtComment);


        }
    }


}
