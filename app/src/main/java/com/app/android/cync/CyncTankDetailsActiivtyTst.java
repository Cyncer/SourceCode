package com.app.android.cync;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.cync.model.Comments;
import com.cync.model.CyncTank;
import com.cync.model.Tags;
import com.fragment.CommentsFragment;
import com.fragment.LikesFragment;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.utils.CommonClass;
import com.utils.ConnectionDetector;
import com.utils.Constants;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.ApplicationController.getContext;
import static com.app.android.cync.R.drawable.loading;


/**
 * Created by ketul.patel on 18/1/16.
 */

public class CyncTankDetailsActiivtyTst extends AppCompatActivity implements View.OnClickListener, PlaybackControlView.OnFullScreenClick {
    SimpleExoPlayer player;
    boolean isChange = false;
    private PlaybackControlView.OnFullScreenClick mOnFullScreenClick;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView ivBack;

    Comments mOldComments = null;
    FrameLayout.LayoutParams params;
    private FrameLayout playerLayout;
    FrameLayout fl_video, fullscreen_video;
    private LinearLayout linearLayout;
    private ViewGroup viewGroup;

    private ImageView iv_thumbnail, iv_play, imgProfile;
    private SimpleExoPlayerView playerView;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    CyncTank mCyncTank = null;
    boolean newComment = false;
    RelativeLayout rlNewComment;
    TextView txtName, txtTime, txtDesc;
    EditText edtCommentText;
    Button btnPost;
    ViewPagerAdapter adapter;
    private RequestQueue mQueue;
    int comment_count = 0, like_count = 0;
    TabLayout tabLayout;
    String type = "";
    ViewPager viewPager;
    CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cync_tank_details);
        mQueue = VolleySetup.getRequestQueue();

        InitId();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {


            if (bundle.containsKey("post_id")) {
                String post_id = bundle.getString("post_id");
                type = bundle.getString("type");


                newComment = true;
                getPostData(post_id);

            } else if (bundle.containsKey("data")) {
                String detail = bundle.getString("data");
                newComment = bundle.getBoolean("newComment");

                mCyncTank = CyncTank.getObjectListFromString(CyncTankDetailsActiivtyTst.this, detail);

                try {
                    comment_count = Integer.parseInt(mCyncTank.comment);
                    like_count = Integer.parseInt(mCyncTank.like);

                } catch (NumberFormatException e) {
                    comment_count = 0;
                    like_count = 0;
                }

                setData();
            }


        } else {

            mCyncTank = null;
        }


        createVideoView();


    }

    private void InitId() {

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder().showImageOnLoading(loading)
                .showImageForEmptyUri(R.drawable.nobnner).showImageOnFail(R.drawable.nobnner).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        txtName = (TextView) findViewById(R.id.txtName);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        edtCommentText = (EditText) findViewById(R.id.edtCommentText);
        btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(this);

        rlNewComment = (RelativeLayout) findViewById(R.id.rlNewComment);



        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        iv_thumbnail = (ImageView) findViewById(R.id.iv_thmbnail);
        fl_video = (FrameLayout) findViewById(R.id.fl_video);

        fullscreen_video = (FrameLayout) findViewById(R.id.fullscreen_video);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        iv_play.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        //setUpToolBar();
        viewPager = (ViewPager) findViewById(R.id.htab_viewpager);
        ImageView header = (ImageView) findViewById(R.id.header);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.bike);


        iv_thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCyncTank != null && mCyncTank.type.equalsIgnoreCase("image"))
                    CommonClass.OpenZoomImage(CyncTankDetailsActiivtyTst.this, mCyncTank.attachment, loading);
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.htab_tabs);


    }


    private void setData() {

        if (mCyncTank != null) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    if (newComment) {
                        rlNewComment.setVisibility(VISIBLE);
                    } else {
                        rlNewComment.setVisibility(GONE);
                    }

                    setupViewPager(viewPager);
                    tabLayout.setupWithViewPager(viewPager);







                    collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);
                    collapsingToolbarLayout.setTitleEnabled(false);


                    tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {

                            viewPager.setCurrentItem(tab.getPosition());

                            switch (tab.getPosition()) {
                                case 0:

                                    if (newComment)
                                        rlNewComment.setVisibility(VISIBLE);

                                    break;
                                case 1:
                                    rlNewComment.setVisibility(GONE);

                                    break;

                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {

                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {

                        }
                    });


                    txtName.setText(mCyncTank.user_name);
                    txtTime.setText(mCyncTank.ago);
                    txtDesc.setText(mCyncTank.description);
                    setUImage(imgProfile, mCyncTank.user_image);

                    if (mCyncTank.type.equalsIgnoreCase("video")) {


                        fl_video.setVisibility(View.VISIBLE);
                        iv_play.setVisibility(View.VISIBLE);
                        iv_thumbnail.setVisibility(View.VISIBLE);

                        setUImage(iv_thumbnail, mCyncTank.thumbnail);
                        //playVideo(mCyncTank.attachment, holder, mCyncTank.user_image);

                    } else if (mCyncTank.type.equalsIgnoreCase("image")) {


                        iv_thumbnail.setVisibility(View.VISIBLE);

                        fl_video.setVisibility(View.VISIBLE);
                        iv_play.setVisibility(GONE);

                        setUImage(iv_thumbnail, mCyncTank.attachment);


                    } else {


                        iv_thumbnail.setVisibility(GONE);
                        iv_play.setVisibility(GONE);
                        fl_video.setVisibility(GONE);
                    }
                }
            }, 10);
        }
    }


    public void setUImage(ImageView imgView, String url) {
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


    private void createVideoView() {
        mOnFullScreenClick = this;
        playerLayout = new FrameLayout(CyncTankDetailsActiivtyTst.this);
        playerView = new SimpleExoPlayerView(CyncTankDetailsActiivtyTst.this);
//        player = new EMVideoView(CyncTankDetailsActiivtyTst.this);
//        progress = new ProgressView(CyncTankDetailsActiivtyTst.this);
        linearLayout = new LinearLayout(CyncTankDetailsActiivtyTst.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        viewGroup = (ViewGroup) playerView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        linearLayout.addView(playerView);
    }


    private void setVideoView(FrameLayout videoLayout) {
        String path = videoLayout.getTag().toString();

        if (path != null && !TextUtils.isEmpty(path)) {
            //adding videoview in layout
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }
            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            videoLayout.addView(linearLayout, params);

            setup(playerView, Uri.parse(path));
            playerView.requestFocus();
            playerView.showController();
        }
    }


    private void setup(SimpleExoPlayerView playerView, Uri videoUri) {
        // 1. Create a default TrackSelector
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

// 2. Create a default LoadControl
        LoadControl loadControl = new DefaultLoadControl();

// 3. Create the player
        if (player != null) {
            player.stop();
        }

        player =
                ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        playerView.setPlayer(player);

        DefaultBandwidthMeter defaultBandwidth = new DefaultBandwidthMeter();
// Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), "Cync"), defaultBandwidth);
// Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
// This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(videoUri,
                dataSourceFactory, extractorsFactory, null, null);

// Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);

        playerView.setMFullScreenClickListener(mOnFullScreenClick);

        player.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {


            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == ExoPlayer.STATE_ENDED) {


                    if (getScreenOrientation() == 2) {
                        ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(linearLayout);
                        }


                        params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);

                        fullscreen_video.addView(linearLayout, params);

                        fullscreen_video.setVisibility(GONE);

                        CyncTankDetailsActiivtyTst.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ((AppCompatActivity) CyncTankDetailsActiivtyTst.this).getSupportActionBar().show();
                    }


                    ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(linearLayout);
                    }


                    if (iv_thumbnail != null)
                        iv_thumbnail.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }
        });


    }


    private void setupViewPager(final ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new CommentsFragment(mCyncTank), "COMMENTS (" + comment_count + ")");
        adapter.addFrag(new LikesFragment(mCyncTank), "LIKES (" + like_count + ")");

        viewPager.setAdapter(adapter);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (type.equalsIgnoreCase("like"))
                    viewPager.setCurrentItem(0);
                else
                    viewPager.setCurrentItem(0);
            }
        },150);
    }


    @Override
    public void onClick(View view) {

        if (view == ivBack) {

            Intent output = new Intent();
            output.putExtra("isChange", isChange);
            setResult(RESULT_OK, output);

            finish();

        } else if (view == btnPost) {

            CommonClass.closeKeyboard(CyncTankDetailsActiivtyTst.this);
            ConnectionDetector cd = new ConnectionDetector(CyncTankDetailsActiivtyTst.this);
            boolean isConnected = cd.isConnectingToInternet();

            if (edtCommentText.getText().toString().trim().length() > 0) {
                if (isConnected) {


                    String url = Constants.addcommentUrl;

                    if (btnPost.getText().toString().equalsIgnoreCase("Update"))
                        url = Constants.edit_commentUrl;

                    System.out.println("CyncTankDetailsActiivtyTst.this===" + CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, url, onCommentSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id);
                            requestparam.put("post_id", "" + mCyncTank.id);
                            requestparam.put("type", "comment");
                            if (btnPost.getText().toString().equalsIgnoreCase("Update"))
                                requestparam.put("comment_id", "" + mOldComments.id);
                            requestparam.put("text", "" + edtCommentText.getText().toString().trim());
//                    requestparam.put("longitude",CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id);


                            return requestparam;
                        }

                    };


                    showProgress();
                    mQueue.add(apiRequest);

                } else {

                    CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, getResources().getString(R.string.check_internet));


                }
            } else {

                CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, "Please write your comment.");


            }

        } else if (view == iv_play && mCyncTank != null) {


            try {
                fl_video.setTag("" + mCyncTank.attachment);

                ConnectionDetector cd = new ConnectionDetector(CyncTankDetailsActiivtyTst.this);
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    if (iv_thumbnail != null)
                        iv_thumbnail.setVisibility(VISIBLE);

                    iv_thumbnail.setVisibility(GONE);

                    ViewGroup mainGroup = (ViewGroup) playerLayout.getParent();
                    if (mainGroup != null) {


                        mainGroup.removeView(playerLayout);

                    }
                    setVideoView(fl_video);


                } else
                    Toast.makeText(CyncTankDetailsActiivtyTst.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

                Toast.makeText(CyncTankDetailsActiivtyTst.this, "Error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }
        }
    }


    private com.android.volley.Response.ErrorListener mErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();


                CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, error.getMessage());


            }
        };
    }

    private com.android.volley.Response.Listener<String> onCommentSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "list==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";
                boolean hasNextpage = false;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        if (btnPost.getText().toString().equalsIgnoreCase("POST")) {
                            updateTabTitle();
                            rlNewComment.setVisibility(VISIBLE);
                        }


                        edtCommentText.setText("");
                        mOldComments = null;
                        btnPost.setText("POST");

                        CommentsFragment fragment = (CommentsFragment) adapter.getItem(0);


                        fragment.ReloadData();
                        CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, message);


                    } else {


                        CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, message);


                }


            }
        };
    }


    private void showProgress() {

        CommonClass.showLoading(CyncTankDetailsActiivtyTst.this);

    }

    private void stopProgress() {
        CommonClass.closeLoding(CyncTankDetailsActiivtyTst.this);
    }


    @Override
    public void onFullScreenClick() {


        if (getScreenOrientation() == 1)        // 1 for Configuration.ORIENTATION_PORTRAIT
        {                          // 2 for Configuration.ORIENTATION_LANDSCAPE
            //your code             // 0 for Configuration.ORIENTATION_SQUARE


            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            fullscreen_video.addView(linearLayout, params);

            fullscreen_video.setVisibility(VISIBLE);
            ((AppCompatActivity) CyncTankDetailsActiivtyTst.this).getSupportActionBar().hide();
            CyncTankDetailsActiivtyTst.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (getScreenOrientation() == 2) {
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            fl_video.addView(linearLayout, params);

            fullscreen_video.setVisibility(GONE);

            CyncTankDetailsActiivtyTst.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((AppCompatActivity) CyncTankDetailsActiivtyTst.this).getSupportActionBar().show();
        }


//        Toast.makeText(CyncTankDetailsActiivtyTst.this, "FullScreen Click", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(CyncTankDetailsActiivtyTst.this, FullScreenVideoActivity.class);
//        startActivity(intent);


    }


    public void setPortaite() {

        if (fl_video != null) {
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            fl_video.addView(linearLayout, params);

            fullscreen_video.setVisibility(GONE);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            getSupportActionBar().show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            if (iv_thumbnail != null)
                iv_thumbnail.setVisibility(VISIBLE);
        }


    }

    @Override
    public void onPause() {
        super.onPause();


        if (player != null) {
            player.stop();
        }
        if (getScreenOrientation() == 2)        // 1 for Configuration.ORIENTATION_PORTRAIT
        {
            setPortaite();
        }
//        setPortaite();
    }


    @Override
    public void onBackPressed() {

        if (getScreenOrientation() == 2)        // 1 for Configuration.ORIENTATION_PORTRAIT
        {
            setPortaite();
        } else {
            Intent output = new Intent();
            output.putExtra("isChange", isChange);
            setResult(RESULT_OK, output);

            finish();

        }


    }

    public int getScreenOrientation() {
        Display getOrient = getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        if (getOrient.getWidth() == getOrient.getHeight()) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (getOrient.getWidth() < getOrient.getHeight()) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);


        }

        public int getItemPosition(Object item) {
            return POSITION_NONE;
        }


        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {


            if (position == 0) {
                // if position is zero, set the title to RECEIVED.
                return "COMMENTS (" + comment_count + ")";
            } else {
                // if position is 1, set the title to SENT.
                return "LIKES (" + like_count + ")";
            }

        }
    }


    public void updateTabTitleOnDelete() {

        isChange = true;
        comment_count = comment_count-1;


        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(adapter.getPageTitle(i));
            }
        }


    }


    public void updateCount(int cCount, int lCount) {


        comment_count = cCount;
        like_count = lCount;


        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(adapter.getPageTitle(i));
            }
        }
    }

    public void updateTabTitle() {

        isChange = true;
        comment_count = comment_count + 1;


        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setText(adapter.getPageTitle(i));
            }
        }
    }


    public void editComment(Comments mOldComments) {
        this.mOldComments = mOldComments;

        rlNewComment.setVisibility(VISIBLE);
        edtCommentText.setText(mOldComments.text);
        edtCommentText.setSelection(edtCommentText.getText().length());
        btnPost.setText("UPDATE");

    }


    private void getPostData(final String post_id) {
        //  swipeContainer.setRefreshing(false);

        CommonClass.closeKeyboard(CyncTankDetailsActiivtyTst.this);
        ConnectionDetector cd = new ConnectionDetector(CyncTankDetailsActiivtyTst.this);
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            System.out.println("CyncTankDetailsActiivtyTst.this===" + CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id);
            System.out.println("CyncTankDetailsActiivtyTst.this===" + post_id);

            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.post_detailUrl, onPostDataSuccessLisner(),
                    mErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();


                    requestparam.put("user_id", CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id);

                    requestparam.put("post_id", post_id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(CyncTankDetailsActiivtyTst.this).user_id);


                    return requestparam;
                }

            };


            showProgress();
            mQueue.add(apiRequest);

        } else {


        }

    }


    private com.android.volley.Response.Listener<String> onPostDataSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                stopProgress();
                boolean Status;
                String message = "";
                boolean hasNextpage = false;
                System.out.println("CyncTankDetailsActiivtyTst.this===" + response.trim());

                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        if (jresObjectMain.has("data")) {


                            JSONArray jData = jresObjectMain.getJSONArray("data");
                            for (int index = 0; index < jData.length(); index++) {
                                JSONObject jsonObject = jData.getJSONObject(index);


                                String id, user_id, title, description, type, date_added, ago, user_image, user_name, comment, like, attachment = "", thumbnail;
                                boolean islike;
                                islike = Boolean.parseBoolean(CommonClass.getDataFromJson(jsonObject, "islike"));

                                ArrayList<Tags> tagList = new ArrayList<>();
                                id = CommonClass.getDataFromJson(jsonObject, "id");
                                user_id = CommonClass.getDataFromJson(jsonObject, "user_id");
                                title = CommonClass.getDataFromJson(jsonObject, "title");
                                description = CommonClass.getDataFromJson(jsonObject, "description");
                                type = CommonClass.getDataFromJson(jsonObject, "type");
                                date_added = CommonClass.getDataFromJson(jsonObject, "date_added");
                                ago = CommonClass.getDataFromJson(jsonObject, "ago");
                                user_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                                user_name = CommonClass.getDataFromJson(jsonObject, "user_name");
                                comment = "" + CommonClass.getDataFromJsonInt(jsonObject, "comment");
                                like = "" + CommonClass.getDataFromJsonInt(jsonObject, "like");
                                thumbnail = "" + CommonClass.getDataFromJson(jsonObject, "thumbnail");


                                if (jsonObject.has("attachment")) {
                                    JSONArray attachmentArray = jsonObject.getJSONArray("attachment");
                                    for (int in = 0; in < attachmentArray.length(); in++) {
                                        attachment = attachmentArray.getString(0);
                                    }


                                }


                                mCyncTank = new CyncTank(id, user_id, title, description, type, date_added, ago, user_image, user_name, comment, like, attachment, islike, thumbnail,tagList);


                            }


                        }

                        if (mCyncTank != null)
                            setData();


                    } else {


                        CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(CyncTankDetailsActiivtyTst.this, message);


                }


            }
        };
    }

}