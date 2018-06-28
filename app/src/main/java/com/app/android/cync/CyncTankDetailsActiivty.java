package com.app.android.cync;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.FriendListSuggetionAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.cync.model.Comments;
import com.cync.model.CyncTank;
import com.cync.model.FriendDetail;
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
import com.mentions_package.edit.MentionEditText;
import com.mentions_package.edit.User;
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

import org.apache.commons.lang3.StringEscapeUtils;
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

public class CyncTankDetailsActiivty extends AppCompatActivity implements View.OnClickListener, PlaybackControlView.OnFullScreenClick {

    int dstart, dend;
    LinearLayout mentions_list_layout;
    private FriendListSuggetionAdapter mFriendListSuggetionAdapter;
    LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManagerFriend;
    private ArrayList<FriendDetail> friendArrayList = new ArrayList<>();
    boolean isFirstTimeLoad = true;
    String nTagText = "";
    private static final String TAG = "CyncTankDetailsActiivty";


    RecyclerView mentionsList;
    ArrayList<Tags> upDateTag = new ArrayList<>();


    SimpleExoPlayer player;
    boolean isChange = false;
    private PlaybackControlView.OnFullScreenClick mOnFullScreenClick;
    Toolbar toolbar;
    TextView toolbar_title;
    ImageView ivBack;
    private static final int OPEN_OTHER_PROFILE = 12355, MENTION_CODE = 330;
    ;
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
    MentionEditText edtCommentText;
    Button btnPost;
    ViewPagerAdapter adapter;
    private RequestQueue mQueue;
    int comment_count = 0, like_count = 0;
    TabLayout tabLayout;
    String type = "";
    ViewPager viewPager;
    CollapsingToolbarLayout collapsingToolbarLayout;
    String comt = "", notification_id = "", from = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cync_tank_details);
        mQueue = VolleySetup.getRequestQueue();

        InitId();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {


            if (bundle.containsKey("from")) {

                from = bundle.getString("from");


            }

            if (bundle.containsKey("notification_id")) {

                notification_id = bundle.getString("notification_id");


            }


            if (bundle.containsKey("post_id")) {
                String post_id = bundle.getString("post_id");
                type = bundle.getString("type");


                newComment = true;
                 getPostData(post_id);

            } else if (bundle.containsKey("data")) {
                String detail = bundle.getString("data");
                newComment = bundle.getBoolean("newComment");

                mCyncTank = CyncTank.getObjectListFromString(CyncTankDetailsActiivty.this, detail);

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
        // getFriendList();

    }

//    public void showKeyboard()
//    {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(edtCommentText, InputMethodManager.SHOW_IMPLICIT);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//    }

    private void InitId() {


        mentions_list_layout = (LinearLayout) findViewById(R.id.mentions_list_layout);
        mRecyclerView = (RecyclerView) findViewById(R.id.mentions_list);


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(CyncTankDetailsActiivty.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mFriendListSuggetionAdapter = new FriendListSuggetionAdapter(CyncTankDetailsActiivty.this, friendArrayList);
        mRecyclerView.setAdapter(mFriendListSuggetionAdapter);


        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder().showImageOnLoading(loading)
                .showImageForEmptyUri(R.drawable.no_image).showImageOnFail(R.drawable.no_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        txtName = (TextView) findViewById(R.id.txtName);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDesc = (TextView) findViewById(R.id.txtDesc);
        edtCommentText = (MentionEditText) findViewById(R.id.edtCommentText);

        edtCommentText.bringToFront();

//        edtCommentText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
//        edtCommentText.setOnMentionListener(new MentionEditText.OnMentionListener() {
//            @Override
//            public void onMention(int start,int end ,String src) {
//
//
//                Log.i(TAG, "onMention: start==" + src);
//                Intent intent = new Intent(CyncTankDetailsActiivty.this, MentionListActivity.class);
//                startActivityForResult(intent, MENTION_CODE);
//
//            }
//        });


        edtCommentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(this);
        edtCommentText.addTextChangedListener(mTextEditorWatcher);
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
                    CommonClass.OpenZoomImage(CyncTankDetailsActiivty.this, mCyncTank.attachment, loading);
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

                                    if (newComment) {
                                        rlNewComment.setVisibility(VISIBLE);

                                    }

                                    edtCommentText.requestFocus();
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                                    break;
                                case 1:
                                    rlNewComment.setVisibility(GONE);
                                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

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

                    String desc = mCyncTank.description;


                    //desc = URLDecoder.decode(desc, "UTF-8");

                    desc = StringEscapeUtils.unescapeJava(desc);


                    SpannableString ss = new SpannableString(desc);

                    for (int i = 0; i < mCyncTank.tagList.size(); i++) {


                        int start = mCyncTank.tagList.get(i).startIndex;
                        int end = mCyncTank.tagList.get(i).startIndex + mCyncTank.tagList.get(i).length;


                        Log.i("CyncTank", "============= start===" + start);
                        Log.i("CyncTank", "============= end===" + end);

                        ss.setSpan(new MyClickableSpan(i), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                        txtDesc.setMovementMethod(LinkMovementMethod.getInstance());
                        //TextUtils.dumpSpans();
                    }
                    txtDesc.setText(ss);


                    setUImage(imgProfile, mCyncTank.user_image);

                    if (mCyncTank.type.equalsIgnoreCase("video")) {

                        final float scale = getResources().getDisplayMetrics().density;

                        int dpHeightInPx = (int) (200 * scale);

                        ViewGroup.LayoutParams params = fl_video.getLayoutParams();
                        params.height = dpHeightInPx;
                        fl_video.setLayoutParams(params);

                        fl_video.setVisibility(View.VISIBLE);
                        iv_play.setVisibility(View.VISIBLE);
                        iv_thumbnail.setVisibility(View.VISIBLE);

                        setUImage(iv_thumbnail, mCyncTank.thumbnail);
                        //playVideo(mCyncTank.attachment, holder, mCyncTank.user_image);

                    } else if (mCyncTank.type.equalsIgnoreCase("image")) {


                        final float scale = getResources().getDisplayMetrics().density;

                        int dpHeightInPx = (int) (300 * scale);

                        ViewGroup.LayoutParams params = fl_video.getLayoutParams();
                        params.height = dpHeightInPx;
                        fl_video.setLayoutParams(params);

                        iv_thumbnail.setVisibility(View.VISIBLE);

                        fl_video.setVisibility(View.VISIBLE);
                        iv_play.setVisibility(GONE);

                        setUImage(iv_thumbnail, mCyncTank.attachment);


                    } else {


                        iv_thumbnail.setVisibility(GONE);
                        iv_play.setVisibility(GONE);

                        final float scale = getResources().getDisplayMetrics().density;

                        int dpHeightInPx = (int) (25 * scale);

                        ViewGroup.LayoutParams params = fl_video.getLayoutParams();
                        params.height = dpHeightInPx;
                        fl_video.setLayoutParams(params);
                        fl_video.setBackgroundColor(Color.parseColor("#ffffff"));
                        fl_video.setVisibility(VISIBLE);
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
        playerLayout = new FrameLayout(CyncTankDetailsActiivty.this);
        playerView = new SimpleExoPlayerView(CyncTankDetailsActiivty.this);
//        player = new EMVideoView(CyncTankDetailsActiivty.this);
//        progress = new ProgressView(CyncTankDetailsActiivty.this);
        linearLayout = new LinearLayout(CyncTankDetailsActiivty.this);
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


                    // if (getScreenOrientation() == 2)
                    {
                        ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(linearLayout);
                        }


                        params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);


                        fullscreen_video.addView(linearLayout, params);

                        fullscreen_video.setVisibility(GONE);

                        CyncTankDetailsActiivty.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ((AppCompatActivity) CyncTankDetailsActiivty.this).getSupportActionBar().show();
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
                    viewPager.setCurrentItem(1);
                else {
                    viewPager.setCurrentItem(0);
                    edtCommentText.requestFocus();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        }, 150);



    }

    String tag_friend = "", tag_friend_id = "";


    public int nthLastIndexOf(String str, String c, int n) {
        if (str == null || n < 1)
            return -1;
        int pos = str.length();
        while (n-- > 0 && pos != -1)
            pos = str.lastIndexOf(c, pos - 1);
        return pos;
    }


    @Override
    public void onClick(View view) {

        if (view == ivBack) {

            Intent output = new Intent();
            if (from.equalsIgnoreCase("notification"))
                output.putExtra("isChange", true);
            else
                output.putExtra("isChange", isChange);
            setResult(RESULT_OK, output);

            finish();

        } else if (view == btnPost) {

            CommonClass.closeKeyboard(CyncTankDetailsActiivty.this);
            ConnectionDetector cd = new ConnectionDetector(CyncTankDetailsActiivty.this);
            boolean isConnected = cd.isConnectingToInternet();

            if (edtCommentText.getText().toString().trim().length() > 0) {
                if (isConnected) {


                    String edtTextStr = edtCommentText.getText().toString();

                    JSONArray jMain = new JSONArray();


                    for (int i = 0; i < edtCommentText.getTagList().size(); i++) {

                        edtTextStr = edtTextStr.replace("@" + edtCommentText.getTagList().get(i).name, "" + edtCommentText.getTagList().get(i).name);
                        //int startIndex =nthLastIndexOf(edtTextStr, edtCommentText.getTagList().get(i).name, (i+1));
                        if (edtTextStr.contains(edtCommentText.getTagList().get(i).name)) {
                            int startIndex = edtTextStr.indexOf(edtCommentText.getTagList().get(i).name);


                            int length = edtCommentText.getTagList().get(i).name.length();

                            Log.i(TAG, "---- Data------Text " + edtTextStr);
                            Log.i(TAG, "---- Data------startIndex " + startIndex);
                            Log.i(TAG, "---- Data------length " + length);


                            try {
                                tag_friend_id = tag_friend_id + edtCommentText.getTagList().get(i).id + ",";

                                JSONObject jobj = new JSONObject();
                                jobj.put("name", edtCommentText.getTagList().get(i).name);
                                jobj.put("id", edtCommentText.getTagList().get(i).id);
                                jobj.put("startIndex", startIndex);
                                jobj.put("length", length);

                                jMain.put(jobj);

                            } catch (JSONException e) {

                            }
                        }

                    }


                    Log.i("JSON", "JSON string==" + jMain.toString());
                    tag_friend = jMain.toString();


                    if (tag_friend_id.length() > 0) {
                        tag_friend_id = tag_friend_id.substring(0, tag_friend_id.length() - 1);
                    }


                    String url = Constants.addcommentUrl;
                    comt = edtCommentText.getText().toString().trim();


//                    try {
//                        comt = URLEncoder.encode(edtCommentText.getText().toString().trim(), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }

                    comt = StringEscapeUtils.escapeJava(edtTextStr);

                    if (btnPost.getText().toString().equalsIgnoreCase("Update"))
                        url = Constants.edit_commentUrl;

                    System.out.println("CyncTankDetailsActiivty.this===" + tag_friend_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, url, onCommentSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id);
                            requestparam.put("post_id", "" + mCyncTank.id);
                            requestparam.put("type", "comment");
                            requestparam.put("tag_friend", tag_friend);
                            requestparam.put("tag_friend_id", tag_friend_id);

                            if (btnPost.getText().toString().equalsIgnoreCase("Update"))
                                requestparam.put("comment_id", "" + mOldComments.id);
                            requestparam.put("text", "" + comt);
//                    requestparam.put("longitude",CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id);


                            return requestparam;
                        }

                    };


                    showProgress();
                    mQueue.add(apiRequest);

                } else {

                    CommonClass.ShowToast(CyncTankDetailsActiivty.this, getResources().getString(R.string.check_internet));


                }
            } else {

                CommonClass.ShowToast(CyncTankDetailsActiivty.this, "Please write your comment.");


            }

        } else if (view == iv_play && mCyncTank != null) {


            try {
                fl_video.setTag("" + mCyncTank.attachment);

                ConnectionDetector cd = new ConnectionDetector(CyncTankDetailsActiivty.this);
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
                    Toast.makeText(CyncTankDetailsActiivty.this, "No internet connection.", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {

                Toast.makeText(CyncTankDetailsActiivty.this, "Error " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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


                CommonClass.ShowToast(CyncTankDetailsActiivty.this, error.getMessage());


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
                        CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);


                    } else {


                        CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);


                }


            }
        };
    }


    private void showProgress() {

        CommonClass.showLoading(CyncTankDetailsActiivty.this);

    }

    private void stopProgress() {
        CommonClass.closeLoding(CyncTankDetailsActiivty.this);
    }


    @Override
    public void onFullScreenClick() {


        CommonClass.closeKeyboard(CyncTankDetailsActiivty.this);


        if (fullscreen_video.getVisibility() == View.GONE)    // 1 for Configuration.ORIENTATION_PORTRAIT
        {                          // 2 for Configuration.ORIENTATION_LANDSCAPE
            //your code             // 0 for Configuration.ORIENTATION_SQUARE


            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            fullscreen_video.addView(linearLayout, params);

            fullscreen_video.setVisibility(VISIBLE);
            ((AppCompatActivity) CyncTankDetailsActiivty.this).getSupportActionBar().hide();
            CyncTankDetailsActiivty.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            fl_video.addView(linearLayout, params);

            fullscreen_video.setVisibility(GONE);

            CyncTankDetailsActiivty.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((AppCompatActivity) CyncTankDetailsActiivty.this).getSupportActionBar().show();
        }


//        Toast.makeText(CyncTankDetailsActiivty.this, "FullScreen Click", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(CyncTankDetailsActiivty.this, FullScreenVideoActivity.class);
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


        if (fullscreen_video.getVisibility() == VISIBLE) {
            setPortaite();
        } else {
            Intent output = new Intent();
            if (from.equalsIgnoreCase("notification"))
                output.putExtra("isChange", true);
            else
                output.putExtra("isChange", isChange);
            setResult(RESULT_OK, output);

            finish();
        }


//        if (getScreenOrientation() == 2)        // 1 for Configuration.ORIENTATION_PORTRAIT
//        {
//            setPortaite();
//        } else {
//            Intent output = new Intent();
//            output.putExtra("isChange", isChange);
//            setResult(RESULT_OK, output);
//
//            finish();
//
//        }


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
        comment_count = comment_count - 1;


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


        String desc = this.mOldComments.text;

//        try {
//            desc = URLDecoder.decode(desc, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }


        desc = StringEscapeUtils.unescapeJava(desc);
        String tmp = desc;
        upDateTag = (ArrayList<Tags>) this.mOldComments.tagList.clone();


        for (int i = 0; i < upDateTag.size(); i++) {


            String t = tmp.substring(0, tmp.indexOf(upDateTag.get(i).name));
            t=t.replace("  "," ");
            if (i == 1) {

                Log.i(TAG, "onEditClick: ======= " + tmp);
                Log.i(TAG, "onEditClick: ======= " + upDateTag.get(i).startIndex);
                Log.i(TAG, "onEditClick: ======= " + upDateTag.get(i).length);

            }


            String subtmp = tmp.substring(tmp.indexOf(upDateTag.get(i).name) + upDateTag.get(i).length);
            edtCommentText.append(t.trim());
//            Mention m = new Mention(upDateTag.get(i).id, upDateTag.get(i).name);
//            edtCommentText.appendMention(m);

            User user = new User(upDateTag.get(i).id, upDateTag.get(i).name.trim() + " ");
            // Mention m = new Mention(upDateTag.get(i).id, upDateTag.get(i).name);
            edtCommentText.insertWithId(user, user.getUserId().toString());

            tmp = subtmp;


        }
        edtCommentText.append(tmp.trim());


        btnPost.setText("UPDATE");
        edtCommentText.setSelection(edtCommentText.getText().length());


        edtCommentText.requestFocus();
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtCommentText, InputMethodManager.SHOW_IMPLICIT);

    }


    private void getPostData(final String post_id) {
        //  swipeContainer.setRefreshing(false);

        CommonClass.closeKeyboard(CyncTankDetailsActiivty.this);
        ConnectionDetector cd = new ConnectionDetector(CyncTankDetailsActiivty.this);
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            System.out.println("CyncTankDetailsActiivty.this===" + CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id);
            System.out.println("CyncTankDetailsActiivty.this===" + post_id);

            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.post_detailUrl, onPostDataSuccessLisner(),
                    mErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();


                    requestparam.put("user_id", CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id);

                    if (notification_id.trim().length() > 0)
                        requestparam.put("notification_id", notification_id);
                    requestparam.put("post_id", post_id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id);


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
                System.out.println("CyncTankDetailsActiivty.this===" + response.trim());

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
                                if (jsonObject.has("tag_friend")) {


                                    Object json = jsonObject.get("tag_friend");
                                    if (json instanceof JSONArray) {


                                        JSONArray tag_friendArray = jsonObject.getJSONArray("tag_friend");
                                        for (int t = 0; t < tag_friendArray.length(); t++) {
                                            String tid, tname;
                                            int startIndex, length;
                                            JSONObject jsubTag = tag_friendArray.getJSONObject(t);

                                            tid = "" + CommonClass.getDataFromJson(jsubTag, "id");
                                            tname = "" + CommonClass.getDataFromJson(jsubTag, "name");
                                            startIndex = CommonClass.getDataFromJsonInt(jsubTag, "startIndex");
                                            if (jsubTag.has("length"))
                                                length = CommonClass.getDataFromJsonInt(jsubTag, "length");
                                            else
                                                length = CommonClass.getDataFromJsonInt(jsubTag, "lenth");


                                            tagList.add(new Tags(tid, tname, startIndex, length));
                                        }

                                    }


                                }
                                mCyncTank = new CyncTank(id, user_id, title, description, type, date_added, ago, user_image, user_name, comment, like, attachment, islike, thumbnail, tagList);


                            }


                        }

                        if (mCyncTank != null)
                            setData();


                    } else {


                        CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);


                }


            }
        };
    }

    boolean isClick = false;

    private class MyClickableSpan extends ClickableSpan {

        int pos;

        private MyClickableSpan(int position) {

            this.pos = position;
        }

        @Override
        public void onClick(final View widget) {

            Log.i("CyncFragment", "Call OnProfile Click");
            //  mOnItemClick.onProfileClick(cpos,pos);

            if (!isClick && mCyncTank != null) {


                Intent intent = new Intent(CyncTankDetailsActiivty.this, OtherUserProfileActivity.class);
                intent.putExtra("user_id", "" + mCyncTank.tagList.get(pos).id);
                startActivityForResult(intent, OPEN_OTHER_PROFILE);
                overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                isClick = true;
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            // ds.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            ds.setColor(Color.parseColor("#0000ff"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MENTION_CODE && resultCode == RESULT_OK
                && null != data) {

            User user = (User) data.getSerializableExtra("mention");
            if (edtCommentText.getText().toString().length() + (user.getUserName()).length() < 250) {

                if (edtCommentText.getText().toString().length() + (user.getUserName()).length() < 250) {
                    edtCommentText.insertWithId(user, user.getUserId().toString());
                }
            } else {
                Toast.makeText(CyncTankDetailsActiivty.this, " You cannot add more than 250 characters.", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == OPEN_OTHER_PROFILE && resultCode == RESULT_OK && data != null) {


            isClick = false;


        }
    }


    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {


            if (count == 1 && !TextUtils.isEmpty(s)) {
                char mentionChar = s.toString().charAt(start);
                int selectionStart = edtCommentText.getSelectionStart();
                if (mentionChar == '@') {

                    Intent intent = new Intent(CyncTankDetailsActiivty.this, MentionListActivity.class);
                    String data = CommonClass.getMentionFromObject(CyncTankDetailsActiivty.this, edtCommentText.getTagListID());
                    intent.putExtra("mention", data);
                    startActivityForResult(intent, MENTION_CODE);
                    edtCommentText.getText().delete(selectionStart - 1, selectionStart);

                }
//                else if (mentionChar == '#') {
//                    startActivityForResult(TagList.getIntent(mMainActivity), REQUEST_TAG_APPEND);
//                    mMentionedittext.getText().delete(selectionStart - 1, selectionStart);
//                }
            }

        }

        public void afterTextChanged(Editable s) {
        }
    };


    private void getFriendList() {
        VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.get_friend_list, friendSuccessLisner(),
                ErrorLisner()) {
            @Override
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                HashMap<String, String> requestparam = new HashMap<>();
                requestparam.put("user_id", "" + CommonClass.getUserpreference(CyncTankDetailsActiivty.this).user_id);
                Log.e("getFriendList Request", "Request ==> " + requestparam);
                return requestparam;
            }
        };
        CommonClass.showLoading(CyncTankDetailsActiivty.this);
        mQueue.add(apiRequest);
    }

    private com.android.volley.Response.Listener<String> friendSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                Log.d("Success Listener Json", "==> " + response);
                CommonClass.closeLoding(CyncTankDetailsActiivty.this);
                boolean Status;
                String message;
                try {
                    JSONObject jresObjectMain = new JSONObject(response);
                    Status = jresObjectMain.getBoolean("status");
                    message = jresObjectMain.getString("message");
                    if (Status) {
                        String friend_id, friend_image = "", friend_first_name, friend_last_name, friend_lat, friend_email, friend_lng, friend_request;
                        JSONArray jresArray = jresObjectMain.getJSONArray("data");

                        for (int i = 0; i < jresArray.length(); i++) {

                            JSONObject jsonObject = jresArray.getJSONObject(i);
                            friend_id = "" + CommonClass.getDataFromJsonInt(jsonObject, "user_id");
                            friend_image = CommonClass.getDataFromJson(jsonObject, "user_image");
                            friend_first_name = CommonClass.getDataFromJson(jsonObject, "first_name");
                            friend_last_name = CommonClass.getDataFromJson(jsonObject, "last_name");
                            friend_email = CommonClass.getDataFromJson(jsonObject, "email");
                            friend_lat = CommonClass.getDataFromJson(jsonObject, "latitude");
                            friend_lng = CommonClass.getDataFromJson(jsonObject, "longitude");
                            friend_request = CommonClass.getDataFromJson(jsonObject, "status");

                            FriendDetail friendDetail = new FriendDetail(friend_id, friend_first_name + " " + friend_last_name, friend_image, friend_email, friend_lat, friend_lng, friend_request);
                            friendArrayList.add(friendDetail);
                        }

                        mFriendListSuggetionAdapter.notifyDataSetChanged();
                    } else {
                        //CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    message = getResources().getString(R.string.s_wrong);
                    CommonClass.ShowToast(CyncTankDetailsActiivty.this, message);
                    e.printStackTrace();
                }
            }
        };
    }

    private com.android.volley.Response.ErrorListener ErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                CommonClass.closeLoding(CyncTankDetailsActiivty.this);
                CommonClass.ShowToast(CyncTankDetailsActiivty.this, getString(R.string.s_wrong));
            }
        };
    }
}