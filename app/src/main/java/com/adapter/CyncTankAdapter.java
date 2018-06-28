package com.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.android.cync.R;
import com.bumptech.glide.Glide;
import com.cync.model.CyncTank;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.utils.CommonClass;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;


public class CyncTankAdapter extends RecyclerView.Adapter<CyncTankAdapter.MyViewHolder> {

    public OnItemClick mOnItemClick;
    private Context mContext;
    private List<CyncTank> CyncTankList;

    DisplayImageOptions options;
    ImageLoader imageLoader;
    boolean showLoading = false;

    int clickedPos;

    public CyncTankAdapter(Activity mContext, List<CyncTank> CyncTankList, OnItemClick mOnItemClick) {


        this.mContext = mContext;
        this.CyncTankList = CyncTankList;
        this.mOnItemClick = mOnItemClick;
        System.out.println("mActivity Adapter===" + mContext);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.cyncing2)
                .showImageForEmptyUri(R.drawable.cyncing2).showImageOnFail(R.drawable.cyncing2).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

    }


    public void showLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_cync_tank, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final CyncTank mCyncTank = CyncTankList.get(position);


        String desc=mCyncTank.description;

//        try {
//            desc=URLDecoder.decode(desc, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


        desc = StringEscapeUtils.unescapeJava(desc);


        holder.txtDesc.setText(desc);
        holder.txtTime.setText(mCyncTank.ago);

        Log.i("CyncTank", "============= Size=====" + mCyncTank.tagList.size());
        SpannableString ss = new SpannableString(desc);

            for (int i = 0; i < mCyncTank.tagList.size(); i++) {


                try {



                    int start = mCyncTank.tagList.get(i).startIndex;
                    int end = mCyncTank.tagList.get(i).startIndex + mCyncTank.tagList.get(i).length;


                    Log.i("CyncTank", "============= start===" + start);
                    Log.i("CyncTank", "============= end===" + end);




                    ss.setSpan(new MyClickableSpan(position, i), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    holder.txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
                }
                catch (Exception e)
                {


                }
                //TextUtils.dumpSpans();
            }
        holder.txtDesc.setText(ss);


//        ss.setSpan(new myClickableSpan(1),0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new myClickableSpan(2),8, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        ss.setSpan(new myClickableSpan(3),16, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);




        if (mCyncTank.user_id.equalsIgnoreCase(CommonClass.getUserpreference(mContext).user_id))
            holder.imgEdit.setVisibility(View.VISIBLE);
        else
            holder.imgEdit.setVisibility(View.GONE);


        if (mCyncTank.islike) {
            Drawable img = mContext.getResources().getDrawable(R.drawable.like_fill);
            img.setBounds(0, 0, 60, 60);
            holder.btnLike.setCompoundDrawables(img, null, null, null);

        } else {
            Drawable img = mContext.getResources().getDrawable(R.drawable.like);
            img.setBounds(0, 0, 60, 60);
            holder.btnLike.setCompoundDrawables(img, null, null, null);
        }

        if (mCyncTank.comment.equalsIgnoreCase("0") || mCyncTank.comment.equalsIgnoreCase("1"))
            holder.btnComments.setText(mCyncTank.comment + " Comment");
        else
            holder.btnComments.setText(mCyncTank.comment + " Comments");


        if (mCyncTank.like.equalsIgnoreCase("0") || mCyncTank.like.equalsIgnoreCase("1"))
            holder.btnLike.setText(mCyncTank.like + " Like");
        else
            holder.btnLike.setText(mCyncTank.like + " Likes");


        if (mCyncTank.description.trim().length() == 0) {
            holder.txtDesc.setVisibility(View.GONE);
        } else {
            holder.txtDesc.setVisibility(View.VISIBLE);
        }
        holder.imgMenuOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                clickedPos = position;
                if (mCyncTank.user_id.equalsIgnoreCase(CommonClass.getUserpreference(mContext).user_id))
                    showPopupMenu(holder.imgMenuOption, 0);
                else
                    showPopupMenu(holder.imgMenuOption, 1);
            }
        });


        holder.iv_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClick.onPlayClickListner(position);
            }

        });


        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClick.onEditClick(position);
            }

        });


        holder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



//                    if(!CommonClass.getUserpreference(mContext).user_id.equalsIgnoreCase(""+mCyncTank.user_id))
//                    mOnItemClick.onProfileClickImage(position);
            }

        });

        holder.iv_thmbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (mCyncTank.type.equalsIgnoreCase("image"))
                    mOnItemClick.onImageZoom(position);
            }

        });


        holder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClick.onCommentClick(position, true);
            }

        });

        holder.btnViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClick.onCommentClick(position, false);
            }

        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnItemClick.onLikeClick(position);
            }

        });


        if (mCyncTank.user_id.equalsIgnoreCase("1")) {

            if (mCyncTank.user_image.trim().length() > 0) {
                setUImage(holder.imgProfile, mCyncTank.user_image);
//                Glide.with(mContext).load(mCyncTank.user_image)
//                        .thumbnail(0.5f)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .into(holder.imgProfile);
            }
            // setUImage(holder.imgProfile, mCyncTank.user_image);
            else
                holder.imgProfile.setImageResource(R.drawable.admin);
            holder.imgMenuOption.setVisibility(View.GONE);
            holder.rlLikeView.setVisibility(View.GONE);

            if (mCyncTank.user_name.trim().length() == 0)
                holder.txtName.setText("Cync");
            else
                holder.txtName.setText(mCyncTank.user_name);
        } else {
            setUImage(holder.imgProfile, mCyncTank.user_image);
            holder.imgMenuOption.setVisibility(View.VISIBLE);
            holder.rlLikeView.setVisibility(View.VISIBLE);
            holder.txtName.setText(mCyncTank.user_name);
        }


        if (mCyncTank.type.equalsIgnoreCase("video")) {


            final float scale = mContext.getResources().getDisplayMetrics().density;

            int dpHeightInPx = (int) (200 * scale);

            Log.i("CyncTankAdapter", "=== Video === inside if " + position);
            holder.fl_video.setVisibility(View.VISIBLE);
            holder.rlVideo.setVisibility(View.VISIBLE);
            holder.iv_play.setVisibility(View.VISIBLE);
            //  holder.iv_thmbnail.setVisibility(View.VISIBLE);
            setUImage(holder.iv_thmbnail, mCyncTank.thumbnail);
            playVideo(mCyncTank.attachment, holder, mCyncTank.thumbnail);

            ViewGroup.LayoutParams params = holder.rlVideo.getLayoutParams();
            params.height = dpHeightInPx;
            holder.rlVideo.setLayoutParams(params);

        } else if (mCyncTank.type.equalsIgnoreCase("image")) {

            Log.i("CyncTankAdapter", "=== Video === inside else if " + position);

            final float scale = mContext.getResources().getDisplayMetrics().density;

            int dpHeightInPx = (int) (300 * scale);

            //  holder.iv_thmbnail.setVisibility(View.VISIBLE);
            holder.fl_video.setVisibility(View.VISIBLE);
            holder.rlVideo.setVisibility(View.VISIBLE);
            holder.iv_play.setVisibility(View.GONE);


            ViewGroup.LayoutParams params = holder.rlVideo.getLayoutParams();
            params.height = dpHeightInPx;
            holder.rlVideo.setLayoutParams(params);


            setUImage(holder.iv_thmbnail, mCyncTank.attachment);


        } else {

            Log.i("CyncTankAdapter", "=== Video === inside else " + position);


            //  holder.iv_thmbnail.setVisibility(View.GONE);
            holder.iv_play.setVisibility(View.GONE);
            holder.fl_video.setVisibility(View.GONE);
            holder.rlVideo.setVisibility(View.GONE);
        }


    }


    public void setUImage(ImageView imgView, String url) {

        Glide.with(mContext).load(url).error(R.drawable.no_image).into(imgView);


//        imageLoader.displayImage(url, imgView, options, new SimpleImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                // holder.progressBar.setProgress(0);
//                // holder.progressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                // holder.progressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                // holder.progressBar.setVisibility(View.GONE);
//            }
//        }, new ImageLoadingProgressListener() {
//            @Override
//            public void onProgressUpdate(String imageUri, View view, int current, int total) {
//                // holder.progressBar.setProgress(Math.round(100.0f
//                // * current / total));
//            }
//        });
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void playVideo(String VideoURL, final MyViewHolder holder, String thumbnailUrl) {
        try {
            holder.fl_video.setTag(VideoURL);


            setUImage(holder.iv_thmbnail, thumbnailUrl);


//            Glide.with(mContext)
//                    .load(thumbnailUrl)
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .placeholder(R.drawable.loading)
//                    .crossFade()
//                    .into(holder.iv_thmbnail);


        } catch (Exception e) {
            Log.e("Error", e.getMessage());

        }

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
          //  ds.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ds.setColor(Color.parseColor("#0000ff"));
        }
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
                    mOnItemClick.onDeleteClick(clickedPos);


                    //Toast.makeText(mContext, "Delete Post " + clickedPos, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_report_post:
                    mOnItemClick.onReportClick(clickedPos);
                    //Toast.makeText(mContext, "Report Post", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_block_user:
                    mOnItemClick.onBlockClick(clickedPos);
                    //Toast.makeText(mContext, "Block User " + clickedPos, Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


    @Override
    public int getItemCount() {
        // return 10;
        return CyncTankList.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        CyncTankList.clear();
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


        CyncTankList.remove(position);

    }

    // Add a list of items
    public void addAll(List<CyncTank> list) {
        CyncTankList.addAll(list);
        notifyDataSetChanged();
    }


    public List<CyncTank> getAll() {

        if (CyncTankList != null) {
            return CyncTankList;
        } else {
            return new ArrayList<>();
        }
    }


    public interface OnItemClick {

        void onProfileClickImage(int pos);
        void onProfileClick(int cpos,int tpos);

        void onReportClick(int pos);

        void onBlockClick(int pos);

        void onDeleteClick(int pos);

        void onCommentClick(int pos, boolean newComment);

        void onEditClick(int pos);

        void onImageZoom(int pos);

        void onLikeClick(int pos);

        void onPlayClickListner(int pos);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        ImageView imgProfile, imgEdit, imgMenuOption, iv_thmbnail, iv_play;
        TextView txtName, txtTime, txtDesc, txtNoLike, txtNoComments;
        Button btnLike, btnComments, btnViewMore;
        public FrameLayout fl_video;
        public RelativeLayout rlVideo, rlLikeView;

        public MyViewHolder(View view) {
            super(view);


            iv_play = (ImageView) view.findViewById(R.id.iv_play);
            imgProfile = (ImageView) view.findViewById(R.id.imgProfile);
            imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
            imgMenuOption = (ImageView) view.findViewById(R.id.imgMenuOption);
            iv_thmbnail = (ImageView) view.findViewById(R.id.iv_thmbnail);
            btnLike = (Button) view.findViewById(R.id.btnLike);
            btnComments = (Button) view.findViewById(R.id.btnComments);
            btnViewMore = (Button) view.findViewById(R.id.btnViewMore);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            txtDesc = (TextView) view.findViewById(R.id.txtDesc);
            txtNoLike = (TextView) view.findViewById(R.id.txtNoLike);
            txtNoComments = (TextView) view.findViewById(R.id.txtNoComments);
            fl_video = (FrameLayout) view.findViewById(R.id.fl_video);
            rlVideo = (RelativeLayout) view.findViewById(R.id.rlVideo);
            rlLikeView = (RelativeLayout) view.findViewById(R.id.rlLikeView);

        }
    }


}