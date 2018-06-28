package com.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adapter.CyncTankAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.app.android.cync.CyncTankDetailsActiivty;
import com.app.android.cync.R;
import com.backgroundTask.UpdateTankTask;
import com.backgroundTask.UploadTankTask;
import com.cync.model.CyncTank;
import com.cync.model.Tags;
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
import com.utils.PermissionUtils;
import com.videocompress.CompressListener;
import com.videocompress.Compressor;
import com.videocompress.InitListener;
import com.webservice.AsyncUtil;
import com.webservice.VolleySetup;
import com.webservice.VolleyStringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * Created by zlinux on 09/02/16.
 */
public class CyncTankFragment_copy extends BaseContainerFragment implements View.OnClickListener, CyncTankAdapter.OnItemClick, PlaybackControlView.OnFullScreenClick {
    private SwipeRefreshLayout swipeContainer;
    private final String TAG = getClass().getSimpleName();
    UploadTankTask mUploadTankTask;
    UpdateTankTask mUpdateTankTask;
    private Compressor mCompressor;
    private String currentOutputVideoPath;
    ListView mViewPopup;

    int delete_id;
    int edited_id;
    int liked_id;
    int report_id;
    private PlaybackControlView.OnFullScreenClick mOnFullScreenClick;
    Dialog dialog;
    private static final int PERMISSION_CAMERA_CODE = 0x0;
    private static final int CAMERA_CODE = 101, GALLERY_CODE = 102, VIDEO_CAMERA_CODE = 201, VIDEO_GALLERY_CODE = 202;
    private static final int CROP_FROM_CAMERA = 2;
    String finalimagepath = "", videoPath = "", remove_attechment = "";
    private Uri mImageCaptureUri, cameraUri, cropUri;
    boolean isManageShare = false;

    FrameLayout fullscreen_video;
    TextView txtCount;
    Button btnPost;


    private static final int OPEN_DETAIL = 3;

    public CyncTankAdapter.OnItemClick mOnItemClick;
    private SimpleExoPlayerView playerView;
    LinearLayout llAddNew;
    Activity mActivity;
    private ImageView iv_thumbnail;
    int page = 0;
    private RecyclerView recyclerView;
    private CyncTankAdapter adapter;
    private List<CyncTank> mPostList;
    private RequestQueue mQueue;
    private RelativeLayout coordinatorLayout;
    private FrameLayout playerLayout;
    private LinearLayout linearLayout;
    private ViewGroup viewGroup;
    LinearLayoutManager mLayoutManager;
    SimpleExoPlayer player;
    TextView txtNoData;
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    int edtPos = -1;
    boolean isApiCalling = false;
    private FrameLayout videoLayout;
    FrameLayout.LayoutParams params;
    Button btnAddPhoto, btnAddVideo;
    EditText edtPostText;
    ImageView imgPreview, imgplayButton, imgDeleteImgVd;
    DisplayImageOptions options;
    ImageLoader imageLoader;
    String from = "";
    String heyfrom = "";

    Intent intent;

    public CyncTankFragment_copy(String from) {
        // Required empty public constructor
        this.heyfrom = from;
    }


    public CyncTankFragment_copy(Intent intent) {
        // Required empty public constructor
        this.intent = intent;
    }

    private void createVideoView() {
        mOnFullScreenClick = this;
        playerLayout = new FrameLayout(getActivity());
        playerView = new SimpleExoPlayerView(getActivity());
//        player = new EMVideoView(getActivity());
//        progress = new ProgressView(getActivity());
        linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        viewGroup = (ViewGroup) playerView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        linearLayout.addView(playerView);


    }


    private void setVideoView(FrameLayout videoLayout) {
        String path = videoLayout.getTag().toString();

        if (videoLayout.getTag() != null && !TextUtils.isEmpty(videoLayout.getTag().toString())) {
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


                    //if (getScreenOrientation()!= 1)
                    {
                        ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(linearLayout);
                        }


                        params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);

                        videoLayout.addView(linearLayout, params);

                        fullscreen_video.setVisibility(GONE);

                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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

    public static MenuItem addNewTank;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (from.trim().length() == 0)
        setHasOptionsMenu(true);

        mCompressor = new Compressor(getActivity());
        mCompressor.loadBinary(new InitListener() {
            @Override
            public void onLoadSuccess() {
                Log.v(TAG, "====>> load library succeed");

            }

            @Override
            public void onLoadFail(String reason) {
                Log.i(TAG, "====>> load library fail:" + reason);

            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_cync_tank, menu);
        addNewTank = menu.findItem(R.id.addNewTank);


        if (mViewPopup.getVisibility() == VISIBLE) {
            addNewTank.setIcon(getResources().getDrawable(R.drawable.ic_close));
        } else {
            addNewTank.setIcon(getResources().getDrawable(R.drawable.edit_icon));
        }


        addNewTank.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                CommonClass.closeKeyboard(mActivity);
                if (llAddNew.getVisibility() == GONE) {
                    llAddNew.setVisibility(VISIBLE);
                    mViewPopup.setVisibility(VISIBLE);
                    addNewTank.setIcon(getResources().getDrawable(R.drawable.ic_close));

                } else {
                    llAddNew.setVisibility(GONE);
                    mViewPopup.setVisibility(GONE);
                    addNewTank.setIcon(getResources().getDrawable(R.drawable.edit_icon));
                    finalimagepath = "";
                    videoPath = "";
                    edtPostText.setText("");

                    btnPost.setText("POST");
                    imgplayButton.setVisibility(GONE);
                    imgPreview.setVisibility(View.GONE);
                    imgDeleteImgVd.setVisibility(View.GONE);

                    //recyclerView.startSc();
                }


                return false;
            }
        });

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_cync_tank, container, false);


        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.cyncing)
                .showImageForEmptyUri(R.drawable.nobnner).showImageOnFail(R.drawable.nobnner).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();


        InitIds(rootView);
        createVideoView();
        isManageShare = true;
        getData(true);


        return rootView;
    }


    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            // Update UI to reflect text being shared

            edtPostText.setText(sharedText);
            //Toast.makeText(getActivity(), "Text Share", Toast.LENGTH_SHORT).show();

            finalimagepath = "";


            videoPath = "";

            imgplayButton.setVisibility(GONE);
            imgPreview.setVisibility(View.GONE);
            imgDeleteImgVd.setVisibility(View.GONE);
            llAddNew.setVisibility(VISIBLE);
            mViewPopup.setVisibility(VISIBLE);
            if (addNewTank != null)
                addNewTank.setIcon(getResources().getDrawable(R.drawable.ic_close));


            getActivity().getIntent().replaceExtras(new Bundle());
            getActivity().getIntent().setAction("");
            getActivity().getIntent().setData(null);
            getActivity().getIntent().setFlags(0);
        }


    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared


            Bitmap mBitmap = resize(rotateBitmap(getPath(imageUri), getBitmap(imageUri)), 1500, 1500);

            //remove_attechment = "true";
            finalimagepath = SaveImage(mBitmap);


            videoPath = "";
            imgPreview.setImageBitmap(mBitmap);
            imgplayButton.setVisibility(GONE);
            imgPreview.setVisibility(View.VISIBLE);
            imgDeleteImgVd.setVisibility(View.VISIBLE);
            llAddNew.setVisibility(VISIBLE);
            mViewPopup.setVisibility(VISIBLE);
            if (addNewTank != null)
                addNewTank.setIcon(getResources().getDrawable(R.drawable.ic_close));


            // Toast.makeText(getActivity(), "Image Share", Toast.LENGTH_SHORT).show();
            getActivity().getIntent().replaceExtras(new Bundle());
            getActivity().getIntent().setAction("");
            getActivity().getIntent().setData(null);
            getActivity().getIntent().setFlags(0);
        }
    }

    void handleSendVideo(Intent intent) {
        Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (videoUri != null) {
            // Update UI to reflect image being shared


            videoPath = getPath(videoUri);


            File file = new File(videoPath);
            int file_size = Integer.parseInt(String.valueOf(file.length() / (1024 * 1024)));
            MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(videoPath));
            int duration = mp.getDuration();
            mp.release();
/*convert millis to appropriate time*/

            String time = String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes(duration),
                    TimeUnit.MILLISECONDS.toSeconds(duration) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


            if (TimeUnit.MILLISECONDS.toSeconds(duration) > 60) {
                Toast.makeText(getActivity(), "Cant upload video of duration greater than 60 seconds", Toast.LENGTH_SHORT).show();
            } else {


                //mImageCaptureUri.getPath();
                imgplayButton.setVisibility(View.VISIBLE);
                Log.i("CyncTankFragment", "Video Path====" + getPath(videoUri));
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                imgPreview.setImageBitmap(thumb);
                imgPreview.setVisibility(VISIBLE);
                imgDeleteImgVd.setVisibility(View.VISIBLE);
                finalimagepath = "";
                llAddNew.setVisibility(VISIBLE);
                mViewPopup.setVisibility(VISIBLE);
                if (addNewTank != null)
                    addNewTank.setIcon(getResources().getDrawable(R.drawable.ic_close));


                if (file_size > 10) {
                    Log.i("CyncTankFragment", "Video Time===>" + TimeUnit.MILLISECONDS.toSeconds(duration) + "Size==" + file_size);


                    Calendar calendar = Calendar.getInstance();

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int date = calendar.get(Calendar.DATE);

                    int hour = calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    File Outfile = new File(Environment.getExternalStorageDirectory()
                            + "/Cync/Camera/Video");
                    Outfile.mkdirs();

                    String vName = "Compress_" + date + "_" + month + "_" + year + "_" + hour + "_"
                            + minute + "_" + second + ".mp4";

                    File videoOut = new File(Outfile.getAbsolutePath(), vName);


                    currentOutputVideoPath = videoOut.getAbsolutePath();
//                    final String cmd = "-y -i " + videoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
//                            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k -s 640x480 -aspect 16:9 " + currentOutputVideoPath;


                    //final String cmd = "-y -i /sdcard/videokit/in.mp4 -strict experimental -s 160×120 -r 25 -vcodec mpeg4 -b 150k -ab 48000 -ac 2 -ar 22050 /sdcard/videokit/out.mp4";

                    final String cmd = "-y -i " + videoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k " + currentOutputVideoPath;


                    File mfile = new File(currentOutputVideoPath);
                    if (mfile.exists()) {
                        mfile.delete();
                    }


                    execCommand(cmd);


                }


            }


            intent.replaceExtras(new Bundle());
            intent.setAction("");
            intent.setData(null);
            intent.setFlags(0);
        }
    }


    private void InitIds(View v) {
        mQueue = VolleySetup.getRequestQueue();
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
//        int badgeCount = 10;
//        ShortcutBadger.applyCount(getActivity(), badgeCount); //for 1.1.4+

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                page = 0;
                getData(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimary_black,
                R.color.colorPrimary_black,
                R.color.colorPrimary_black,
                R.color.colorPrimary_black);


        fullscreen_video = (FrameLayout) v.findViewById(R.id.fullscreen_video);
        mViewPopup = (ListView) v.findViewById(R.id.mViewPopup);
        btnPost = (Button) v.findViewById(R.id.btnPost);
        txtCount = (TextView) v.findViewById(R.id.txtCount);
        edtPostText = (EditText) v.findViewById(R.id.edtPostText);
        btnAddPhoto = (Button) v.findViewById(R.id.btnAddPhoto);
        btnAddVideo = (Button) v.findViewById(R.id.btnAddVideo);
        imgPreview = (ImageView) v.findViewById(R.id.imgPreview);
        imgplayButton = (ImageView) v.findViewById(R.id.imgplayButton);
        imgDeleteImgVd = (ImageView) v.findViewById(R.id.imgDeleteImgVd);


        btnPost.setOnClickListener(this);
        imgDeleteImgVd.setOnClickListener(this);
        imgplayButton.setOnClickListener(this);
        btnAddPhoto.setOnClickListener(this);
        btnAddVideo.setOnClickListener(this);

        coordinatorLayout = (RelativeLayout) v.findViewById(R.id.llMainLayout);
        llAddNew = (LinearLayout) v.findViewById(R.id.llAddNew);
        txtNoData = (TextView) v.findViewById(R.id.txtNoData);
        txtNoData.setVisibility(GONE);

        mOnItemClick = this;
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mPostList = new ArrayList<>();

        adapter = new CyncTankAdapter(mActivity, mPostList, mOnItemClick);

        mLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);


        recyclerView.setAdapter(adapter);


        edtPostText.addTextChangedListener(mTextEditorWatcher);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                Log.i("CyncTankFragment", "dy====>" + dy);

                if (dy > 0) //check for scroll down
                {


                    //  stop player


                    if (player != null) {
                        player.stop();

                        setPortaite();

                    }


                    if (videoLayout != null && linearLayout != null) {

                        ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
                        if (viewGroup != null) {
                            viewGroup.removeView(linearLayout);
                        }


                        params = new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT,
                                FrameLayout.LayoutParams.MATCH_PARENT);

                        videoLayout.addView(linearLayout, params);

                        fullscreen_video.setVisibility(GONE);

                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


                        if (viewGroup != null) {
                            viewGroup.removeView(linearLayout);
                        }


                        if (iv_thumbnail != null)
                            iv_thumbnail.setVisibility(View.VISIBLE);
                    }


                    // ----


                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if (loading) {


                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {

                            Log.v("...", "Last Item Wow ! " + loading);
                            adapter.showLoading(true);
                            // adapter.notifyDataSetChanged();
                            //Toast.makeText(getActivity(), "Last Item Wow !", Toast.LENGTH_SHORT).show();
                            getData(false);
                            //Do pagination.. i.e. fetch new data
                        }
                    } else {
                        adapter.showLoading(false);
                        //  adapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //This sets a textview to the current length
            txtCount.setText(String.valueOf(s.length()) + "/250");
        }

        public void afterTextChanged(Editable s) {
        }
    };

    private void getData(boolean show) {
        //  swipeContainer.setRefreshing(false);

        CommonClass.closeKeyboard(mActivity);
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {

            System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);
            System.out.println("mActivity===" + heyfrom);
            if (isApiCalling == false) {
                isApiCalling = true;
                VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.getpostUrl, SuccessLisner(),
                        mErrorLisner()) {
                    @Override
                    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                        HashMap<String, String> requestparam = new HashMap<>();


                        requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                        if (heyfrom.trim().length() == 0)
                            requestparam.put("type", "post");
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);
                        requestparam.put("page", "" + page);

                        return requestparam;
                    }

                };


                if (show)
                    showProgress();
                mQueue.add(apiRequest);
            }
        } else {
            swipeContainer.setRefreshing(false);
            if (mPostList.size() > 0) {
                txtNoData.setVisibility(GONE);
            } else
                txtNoData.setVisibility(VISIBLE);

        }
    }


    private void showProgress() {

        CommonClass.showLoading(getActivity());

    }

    private void stopProgress() {
        CommonClass.closeLoding(getActivity());
    }


    private com.android.volley.Response.Listener<String> SuccessLisner() {
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

                    hasNextpage = CommonClass.getDataFromJsonBoolean(jresObjectMain, "next");


                    if (page == 0) {
                        mPostList.clear();
                    }


                    if (Status) {


                        if (hasNextpage) {
                            page++;

                            loading = true;
                        } else {
                            loading = false;
                        }


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


                                mPostList.add(new CyncTank(id, user_id, title, description, type, date_added, ago, user_image, user_name, comment, like, attachment, islike, thumbnail, tagList));


                            }


//                            adapter.clear();
//                            // ...the data has come back, add new items to your adapter...
//                            adapter.addAll(mPostList);
                            if (loading)
                                adapter.showLoading(true);
                            else
                                adapter.showLoading(false);
                            adapter.notifyDataSetChanged();
                            // Now we call setRefreshing(false) to signal refresh has finished


                        }


                    } else {
                        loading = false;

                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);
                    loading = false;
                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }

                isApiCalling = false;
                if (mPostList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);

                Log.e("getExtras--", "onNewIntent getExtras----position----manage share");

                if (isManageShare)
                    manageShare();
                swipeContainer.setRefreshing(false);
            }


        };
    }

    private void manageShare() {

        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {

                    handleSendText(intent); // Handle text being sent
                } else if (type.startsWith("image/")) {
                    handleSendImage(intent); // Handle single image being sent
                } else if (type.startsWith("video/")) {
                    handleSendVideo(intent); // Handle multiple images being sent
                }
            } else {
                // Handle other intents, such as being started from the home screen
            }
        }


    }


    private com.android.volley.Response.ErrorListener mErrorLisner() {
        return new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeContainer.setRefreshing(false);

                isApiCalling = false;
                Log.e("Json Error", "==> " + error.getMessage());
                stopProgress();
                if (mPostList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);

                CommonClass.ShowToast(mActivity, error.getMessage());

                manageShare();

            }
        };
    }


    @Override
    public void onDeleteClick(final int pos) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to delete this post?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                delete_id = pos;

                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.delete_postUrl, onDeleteSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            requestparam.put("post_id", "" + mPostList.get(pos).id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);


                            return requestparam;
                        }

                    };


                    showProgress();
                    mQueue.add(apiRequest);

                } else {

                    CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


                }

            }
        });
        builder.setNegativeButton("No", null);

        builder.show();


    }

    private com.android.volley.Response.Listener<String> onLikeUnlikeSuccessLisner() {
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


                        JSONObject data = jresObjectMain.getJSONObject("data");

                        if (data.has("like")) {
                            int like = data.getInt("like");
                            mPostList.get(liked_id).like = "" + like;
                        } else {
                            if (mPostList.get(liked_id).islike) {
                                mPostList.get(liked_id).like = "" + (Integer.parseInt(mPostList.get(liked_id).like) - 1);
                            } else {
                                mPostList.get(liked_id).like = "" + (Integer.parseInt(mPostList.get(liked_id).like) + 1);
                            }
                        }


                        mPostList.get(liked_id).islike = !mPostList.get(liked_id).islike;
                        adapter.notifyDataSetChanged();

                        //CommonClass.ShowToast(mActivity, message);
                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


                if (mPostList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }


    private com.android.volley.Response.Listener<String> onDeleteSuccessLisner() {
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


                        mPostList.remove(delete_id);
                        adapter.notifyDataSetChanged();

                        CommonClass.ShowToast(mActivity, message);
                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


                if (mPostList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }


    private com.android.volley.Response.Listener<String> onReportSuccessLisner() {
        return new com.android.volley.Response.Listener<String>() {

            private String responseMessage = "";

            @Override
            public void onResponse(String response) {
                // TODO Auto-generated method stub
                Log.e("Json", "list==> " + response.trim());
                stopProgress();
                boolean Status;
                String message = "";

                try {
                    JSONObject jresObjectMain = new JSONObject(response);

                    Status = jresObjectMain.getBoolean("status");
                    message = CommonClass.getDataFromJson(jresObjectMain, "message");


                    if (Status) {


                        CommonClass.ShowToast(mActivity, message);
                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


                if (mPostList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
    }

    @Override
    public void onCommentClick(int pos, boolean newComment) {

        String data = CyncTank.getStringFromObject(getActivity(), mPostList.get(pos));
        Intent i = new Intent(getActivity(), CyncTankDetailsActiivty.class);
        i.putExtra("data", data);
        i.putExtra("newComment", newComment);
        startActivityForResult(i, OPEN_DETAIL);

    }

    @Override
    public void onEditClick(int pos) {

        edited_id = pos;

        if (llAddNew.getVisibility() == GONE) {
            llAddNew.setVisibility(VISIBLE);
            mViewPopup.setVisibility(VISIBLE);
            addNewTank.setIcon(getResources().getDrawable(R.drawable.ic_close));
        }

        edtPostText.setText(mPostList.get(edited_id).description);

        btnPost.setText("Update");


        if (mPostList.get(edited_id).type.equalsIgnoreCase("video")) {

            imgPreview.setVisibility(View.VISIBLE);
            imgplayButton.setVisibility(View.VISIBLE);
            imgDeleteImgVd.setVisibility(View.VISIBLE);

            setUImage(imgPreview, mPostList.get(edited_id).thumbnail);


        } else if (mPostList.get(edited_id).type.equalsIgnoreCase("image")) {


            imgPreview.setVisibility(View.VISIBLE);
            imgDeleteImgVd.setVisibility(View.VISIBLE);

            setUImage(imgPreview, mPostList.get(edited_id).attachment);


        }


    }

    @Override
    public void onImageZoom(int pos) {

        CommonClass.OpenZoomImage(getActivity(), mPostList.get(pos).attachment, R.drawable.cyncing);

    }

    @Override
    public void onLikeClick(final int pos) {


        liked_id = pos;
        CommonClass.closeKeyboard(mActivity);
        ConnectionDetector cd = new ConnectionDetector(getActivity());
        boolean isConnected = cd.isConnectingToInternet();
        if (isConnected) {


            VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.addcommentUrl, onLikeUnlikeSuccessLisner(),
                    mErrorLisner()) {
                @Override
                protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                    HashMap<String, String> requestparam = new HashMap<>();

                    if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                        requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                    requestparam.put("post_id", "" + mPostList.get(pos).id);

                    if (mPostList.get(pos).islike)
                        requestparam.put("type", "unlike");
                    else
                        requestparam.put("type", "like");

                    return requestparam;
                }

            };


            showProgress();
            mQueue.add(apiRequest);

        } else {

            CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


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

    @Override
    public void onProfileClickImage(int pos) {

    }

    @Override
    public void onProfileClick(int cpos, int tpos) {

    }

    @Override
    public void onReportClick(final int pos) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to report this post?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                report_id = pos;

                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.report_postUrl, onReportSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            requestparam.put("post_id", "" + mPostList.get(pos).id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);


                            return requestparam;
                        }

                    };


                    showProgress();
                    mQueue.add(apiRequest);

                } else {

                    CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


                }

            }
        });
        builder.setNegativeButton("No", null);

        builder.show();


    }

    @Override
    public void onBlockClick(final int pos) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to block this user?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                CommonClass.closeKeyboard(mActivity);
                ConnectionDetector cd = new ConnectionDetector(getActivity());
                boolean isConnected = cd.isConnectingToInternet();
                if (isConnected) {

                    System.out.println("mActivity===" + CommonClass.getUserpreference(getActivity()).user_id);

                    VolleyStringRequest apiRequest = new VolleyStringRequest(Request.Method.POST, Constants.block_userUrl, onBlockSuccessLisner(),
                            mErrorLisner()) {
                        @Override
                        protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                            HashMap<String, String> requestparam = new HashMap<>();

                            if (CommonClass.getUserpreference(getActivity()).user_id.trim().length() > 0)
                                requestparam.put("user_id", CommonClass.getUserpreference(getActivity()).user_id);
                            requestparam.put("friend_id", "" + mPostList.get(pos).user_id);
//                    requestparam.put("longitude",CommonClass.getUserpreference(getActivity()).user_id);


                            return requestparam;
                        }

                    };


                    showProgress();
                    mQueue.add(apiRequest);

                } else {

                    CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));


                }

            }
        });
        builder.setNegativeButton("No", null);

        builder.show();

    }


    private com.android.volley.Response.Listener<String> onBlockSuccessLisner() {
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

                        CommonClass.ShowToast(mActivity, message);
                        page = 0;
                        getData(true);


                    } else {


                        CommonClass.ShowToast(mActivity, message);


                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    if (message.trim().length() == 0)
                        message = getResources().getString(R.string.s_wrong);

                    e.printStackTrace();
                    CommonClass.ShowToast(mActivity, message);


                }


                if (mPostList.size() > 0) {
                    txtNoData.setVisibility(GONE);
                } else
                    txtNoData.setVisibility(VISIBLE);


            }
        };
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

            setPortaite();

        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public void onPlayClickListner(int pos) {

        try {

//            Log.i("CyncTankFragment","onPlayClickListner pos====>"+pos);

            ConnectionDetector cd = new ConnectionDetector(getActivity());
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {

                if (iv_thumbnail != null)
                    iv_thumbnail.setVisibility(VISIBLE);

                View view = recyclerView.getLayoutManager().findViewByPosition(pos);
                if (view != null)
                    videoLayout = (FrameLayout) view.findViewById(R.id.fl_video);

                iv_thumbnail = (ImageView) view.findViewById(R.id.iv_thmbnail);
                iv_thumbnail.setVisibility(GONE);
                //removing view form main layout
                ViewGroup mainGroup = (ViewGroup) playerLayout.getParent();
                if (mainGroup != null) {


                    Log.i("CyncTankFragment", "onPlayClickListner remove View====>" + pos);


                    mainGroup.removeView(playerLayout);

                }
                setVideoView(videoLayout);


            } else
                Toast.makeText(getActivity(), "No internet connection.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onClick(View view) {

        if (view == btnAddPhoto) {
            from = "photo";
            selectImage();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                // only for gingerbread and newer versions
//
//
//                String PERMISSIONS[] = new String[]{
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                        Manifest.permission.CAMERA
//                };
//
//
//                if (PermissionUtils.hasSelfPermission(getActivity(), PERMISSIONS)) {
//                    selectImage();
//                } else {
//                    requestPermissions(PERMISSIONS, PERMISSION_CAMERA_CODE);
//                }
//
//
//            } else {
//                selectImage();
//            }
        } else if (view == btnPost) {


            ConnectionDetector cd = new ConnectionDetector(getActivity());
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {


                if (btnPost.getText().toString().equalsIgnoreCase("Post")) {

                    if (edtPostText.getText().toString().length() > 0 || finalimagepath.trim().length() > 0 || videoPath.trim().length() > 0) {

                        mUploadTankTask = new UploadTankTask(mUploadTankTaskResponder, getActivity(), finalimagepath, videoPath, "", "");
                        AsyncUtil.execute(mUploadTankTask, edtPostText.getText().toString(), finalimagepath, videoPath, "");
                    }
                } else {

//                    if (edtPostText.getText().toString().length() > 0) {
//
//                        mUpdateTankTask = new UpdateTankTask(mUpdateTankTaskResponder, getActivity());
//                        AsyncUtil.execute(mUpdateTankTask, edtPostText.getText().toString(), finalimagepath, videoPath, remove_attechment, mPostList.get(edited_id).id);
//                    } else if (finalimagepath.trim().length() > 0 || videoPath.trim().length() > 0) {
//                        mUpdateTankTask = new UpdateTankTask(mUpdateTankTaskResponder, getActivity());
//                        AsyncUtil.execute(mUpdateTankTask, edtPostText.getText().toString(), finalimagepath, videoPath, remove_attechment, mPostList.get(edited_id).id);
//                    } else if (remove_attechment.equalsIgnoreCase("true")) {
//                        mUpdateTankTask = new UpdateTankTask(mUpdateTankTaskResponder, getActivity());
//                        AsyncUtil.execute(mUpdateTankTask, edtPostText.getText().toString(), finalimagepath, videoPath, remove_attechment, mPostList.get(edited_id).id);
//                    }
//


                    if (edtPostText.getText().toString().length() > 0 && finalimagepath.trim().length() > 0 && videoPath.trim().length() > 0 && remove_attechment.trim().length() == 0) {

                    } else {

                        if (remove_attechment.equalsIgnoreCase("true")) {
                            if (edtPostText.getText().toString().length() > 0 || finalimagepath.trim().length() > 0 || videoPath.trim().length() > 0) {
                                mUpdateTankTask = new UpdateTankTask(mUpdateTankTaskResponder, getActivity(), finalimagepath, videoPath, "", "");
                                AsyncUtil.execute(mUpdateTankTask, edtPostText.getText().toString(), finalimagepath, videoPath, remove_attechment, mPostList.get(edited_id).id, "");

                            }
                        } else {
                            mUpdateTankTask = new UpdateTankTask(mUpdateTankTaskResponder, getActivity(), finalimagepath, videoPath, "", "");
                            AsyncUtil.execute(mUpdateTankTask, edtPostText.getText().toString(), finalimagepath, videoPath, remove_attechment, mPostList.get(edited_id).id, "");

                        }


                    }

                }


//                else
//                {
//                    CommonClass.ShowToast(getActivity(), "Please write your comment.");
//
//                }
            } else {
                CommonClass.ShowToast(getActivity(), getResources().getString(R.string.check_internet));
            }
        } else if (view == btnAddVideo) {
            from = "video";
            selectImage();
        } else if (view == imgplayButton) {

            if (videoPath.trim().length() > 0) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(
                        Uri.parse("file://" + videoPath),
                        "video/*");

                startActivity(intent);
            }
        } else if (view == imgDeleteImgVd) {


            remove_attechment = "true";
            finalimagepath = "";
            videoPath = "";


            imgPreview.setImageDrawable(null);

            imgPreview.setVisibility(GONE);
            imgplayButton.setVisibility(GONE);
            imgDeleteImgVd.setVisibility(GONE);
        }

    }


    UploadTankTask.Responder mUploadTankTaskResponder = new UploadTankTask.Responder() {

        @Override
        public void onComplete(boolean result, final String message) {
            if (result) {
                isManageShare = false;
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("UI thread", "I am the UI thread");

                        llAddNew.setVisibility(GONE);
                        mViewPopup.setVisibility(GONE);
                        addNewTank.setIcon(getResources().getDrawable(R.drawable.edit_icon));
                        finalimagepath = "";
                        videoPath = "";
                        edtPostText.setText("");

                        btnPost.setText("POST");
                        imgplayButton.setVisibility(GONE);
                        imgPreview.setVisibility(View.GONE);
                        imgDeleteImgVd.setVisibility(View.GONE);

                        page = 0;
                        getData(true);
                        CommonClass.ShowToast(getActivity(), message);
                    }
                });


            } else {
                CommonClass.ShowToast(getActivity(), message);
            }

        }
    };

    UpdateTankTask.Responder mUpdateTankTaskResponder = new UpdateTankTask.Responder() {

        @Override
        public void onComplete(boolean result, final String message) {
            if (result) {

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("UI thread", "I am the UI thread");

                        llAddNew.setVisibility(GONE);
                        mViewPopup.setVisibility(GONE);
                        addNewTank.setIcon(getResources().getDrawable(R.drawable.edit_icon));
                        finalimagepath = "";
                        videoPath = "";
                        edtPostText.setText("");

                        btnPost.setText("POST");
                        imgplayButton.setVisibility(GONE);
                        imgPreview.setVisibility(View.GONE);
                        imgDeleteImgVd.setVisibility(View.GONE);

                        page = 0;
                        getData(true);
                        CommonClass.ShowToast(getActivity(), message);
                    }
                });


            } else {
                CommonClass.ShowToast(getActivity(), message);
            }

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {


            case PERMISSION_CAMERA_CODE:


                if (PermissionUtils.verifyAllPermissions(grantResults)) {
                    selectImage();

                } else {
                    Toast.makeText(getActivity(), "Permission Not Granted", Toast.LENGTH_SHORT).show();
                }

                break;


        }
    }


    public void selectImage() {
        CommonClass.closeKeyboard(getActivity());

        dialog = new Dialog(getActivity(), R.style.AppCompatAlertDialogStyleNew);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.stockAnimation;
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog.setContentView(R.layout.alert_dialog_image_picker);
        dialog.show();


        Button txtlabel = (Button) dialog.findViewById(R.id.txtlabel);
        Button buttonGallery = (Button) dialog.findViewById(R.id.buttonGallery);
        Button buttonCamera = (Button) dialog.findViewById(R.id.buttonCamera);
        Button buttonimgCancel = (Button) dialog.findViewById(R.id.buttonimgCancel);

        txtlabel.setText("ADD PHOTO FROM");

        if (from.equalsIgnoreCase("video")) {
            txtlabel.setText("ADD VIDEO FROM");
        }


        buttonimgCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();


            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();


                if (from.equalsIgnoreCase("photo"))
                    openCamera();
                else
                    openVideoCamera();


            }
        });


        buttonGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();


                if (from.equalsIgnoreCase("photo")) {

                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, GALLERY_CODE);

                } else {

                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, VIDEO_GALLERY_CODE);

                }


            }
        });
    }


    public void openVideoCamera() {
        PackageManager pm = getActivity().getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {


            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int date = calendar.get(Calendar.DATE);

            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Cync/Camera/Video");
            file.mkdirs();

            String imageName = date + "_" + month + "_" + year + "_" + hour + "_"
                    + minute + "_" + second + ".mp4";

            File image = new File(file.getAbsolutePath(), imageName);


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                cameraUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", image);
//            } else {
//                cameraUri = Uri.fromFile(image);
//            }


            cameraUri = Uri.fromFile(image);


            Intent takePictureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            takePictureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
            takePictureIntent.putExtra("EXTRA_VIDEO_QUALITY", 0);
            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);

            startActivityForResult(takePictureIntent,
                    VIDEO_CAMERA_CODE);


        } else {
            Toast.makeText(getActivity(),
                    "Device has no camera!", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCamera() {
        PackageManager pm = getActivity().getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {


            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int date = calendar.get(Calendar.DATE);

            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            File file = new File(Environment.getExternalStorageDirectory()
                    + "/Cync/Camera/Images");
            file.mkdirs();

            String imageName = date + "_" + month + "_" + year + "_" + hour + "_"
                    + minute + "_" + second + ".jpg";

            File image = new File(file.getAbsolutePath(), imageName);


//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                cameraUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", image);
//            } else {
//                cameraUri = Uri.fromFile(image);
//            }


            cameraUri = Uri.fromFile(image);


            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);

            startActivityForResult(takePictureIntent,
                    CAMERA_CODE);


        } else {
            Toast.makeText(getActivity(),
                    "Device has no camera!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == GALLERY_CODE && resultCode == RESULT_OK
                    && null != data) {


                mImageCaptureUri = data.getData();

                //String finalimagepath = mImageCaptureUri.getPath();

                //   mFinalBitmap=resize(getBitmap(mImageCaptureUri), 1500, 1500);

                Bitmap mBitmap = resize(rotateBitmap(getPath(mImageCaptureUri), getBitmap(mImageCaptureUri)), 1500, 1500);
                remove_attechment = "";

                finalimagepath = SaveImage(mBitmap);


                videoPath = "";
                imgPreview.setImageBitmap(mBitmap);
                imgplayButton.setVisibility(GONE);
                imgPreview.setVisibility(View.VISIBLE);
                imgDeleteImgVd.setVisibility(View.VISIBLE);


                //  performCrop(mImageCaptureUri);

            } else if (requestCode == CROP_FROM_CAMERA && resultCode == RESULT_OK) {
                remove_attechment = "";
                finalimagepath = cropUri.getPath();
                videoPath = "";


            } else if (requestCode == CROP_FROM_CAMERA && resultCode == RESULT_CANCELED) {
                remove_attechment = "";
                String finalimagepath = "";


            } else if (requestCode == CAMERA_CODE
                    && resultCode == RESULT_OK) {

                // String finalimagepath = cameraUri.getPath();
                remove_attechment = "";

                Bitmap mBitmap = resize(rotateBitmap(cameraUri.getPath(), getBitmap(cameraUri)), 1500, 1500);
                imgPreview.setImageBitmap(mBitmap);
                imgPreview.setVisibility(VISIBLE);
                imgDeleteImgVd.setVisibility(VISIBLE);
                imgplayButton.setVisibility(GONE);
                finalimagepath = SaveImage(mBitmap);
                videoPath = "";


                //performCrop(cameraUri);
            } else if (requestCode == VIDEO_CAMERA_CODE
                    && resultCode == RESULT_OK) {


                remove_attechment = "";
                imgplayButton.setVisibility(View.VISIBLE);
                videoPath = cameraUri.getPath();
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath,
                        MediaStore.Images.Thumbnails.MINI_KIND);
                imgPreview.setImageBitmap(thumb);
                imgPreview.setVisibility(VISIBLE);
                imgDeleteImgVd.setVisibility(VISIBLE);
                finalimagepath = "";


                File file = new File(videoPath);
                int file_size = Integer.parseInt(String.valueOf(file.length() / (1024 * 1024)));


                MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(videoPath));
                int duration = mp.getDuration();
                mp.release();
/*convert millis to appropriate time*/

                String time = String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

                if (file_size > 10) {


                    Log.i("CyncTankFragment", "Video Time===>" + TimeUnit.MILLISECONDS.toSeconds(duration) + "Size==" + file_size);


                    Calendar calendar = Calendar.getInstance();

                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int date = calendar.get(Calendar.DATE);

                    int hour = calendar.get(Calendar.HOUR);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);

                    File Outfile = new File(Environment.getExternalStorageDirectory()
                            + "/Cync/Camera/Video");
                    Outfile.mkdirs();

                    String vName = "Compress_" + date + "_" + month + "_" + year + "_" + hour + "_"
                            + minute + "_" + second + ".mp4";

                    File videoOut = new File(Outfile.getAbsolutePath(), vName);


                    currentOutputVideoPath = videoOut.getAbsolutePath();


                    String cmd = "-y -i " + videoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                            "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k " + currentOutputVideoPath;

                    File mfile = new File(currentOutputVideoPath);
                    if (mfile.exists()) {
                        mfile.delete();
                    }
                    execCommand(cmd);

                }


            } else if (requestCode == VIDEO_GALLERY_CODE && resultCode == RESULT_OK
                    && null != data) {

                mImageCaptureUri = data.getData();

                videoPath = getPath(mImageCaptureUri);


                File file = new File(videoPath);
                int file_size = Integer.parseInt(String.valueOf(file.length() / (1024 * 1024)));


                MediaPlayer mp = MediaPlayer.create(getActivity(), Uri.parse(videoPath));
                int duration = mp.getDuration();
                mp.release();
/*convert millis to appropriate time*/

                String time = String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(duration),
                        TimeUnit.MILLISECONDS.toSeconds(duration) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));


                if (TimeUnit.MILLISECONDS.toSeconds(duration) > 60) {
                    Toast.makeText(getActivity(), "Cant upload video of duration greater than 60 seconds", Toast.LENGTH_SHORT).show();
                } else {


                    remove_attechment = "";
                    imgplayButton.setVisibility(View.VISIBLE);
                    Log.i("CyncTankFragment", "Video Path====" + getPath(mImageCaptureUri));
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    imgPreview.setImageBitmap(thumb);
                    imgPreview.setVisibility(VISIBLE);
                    imgDeleteImgVd.setVisibility(View.VISIBLE);
                    finalimagepath = "";


                    if (file_size > 10) {
                        Log.i("CyncTankFragment", "Video Time===>" + TimeUnit.MILLISECONDS.toSeconds(duration) + "Size==" + file_size);


                        Calendar calendar = Calendar.getInstance();

                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int date = calendar.get(Calendar.DATE);

                        int hour = calendar.get(Calendar.HOUR);
                        int minute = calendar.get(Calendar.MINUTE);
                        int second = calendar.get(Calendar.SECOND);

                        File Outfile = new File(Environment.getExternalStorageDirectory()
                                + "/Cync/Camera/Video");
                        Outfile.mkdirs();

                        String vName = "Compress_" + date + "_" + month + "_" + year + "_" + hour + "_"
                                + minute + "_" + second + ".mp4";

                        File videoOut = new File(Outfile.getAbsolutePath(), vName);


                        currentOutputVideoPath = videoOut.getAbsolutePath();


                        final String cmd = "-y -i " + videoPath + " -strict -2 -vcodec libx264 -preset ultrafast " +
                                "-crf 24 -acodec aac -ar 44100 -ac 2 -b:a 96k " + currentOutputVideoPath;


                        File mfile = new File(currentOutputVideoPath);
                        if (mfile.exists()) {
                            mfile.delete();
                        }


                        execCommand(cmd);


                    }


                }


                //mImageCaptureUri.getPath();

            } else if (requestCode == OPEN_DETAIL && resultCode == getActivity().RESULT_OK && data != null) {

                boolean isChange = data.getBooleanExtra("isChange", false);


                if (isChange) {


                    page = 0;
                    getData(true);


                }

            }
        } catch (NullPointerException e) {
            CommonClass.ShowToast(getActivity(), "Please try again.");
        }

    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public Bitmap rotateBitmap(String path, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }


    }


    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }

            Log.i("UploadActivity", "Final======Height==" + finalHeight);
            Log.i("UploadActivity", "Final======Width==" + finalWidth);
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }


    private String SaveImage(Bitmap finalBitmap) {


        File file = new File(Environment.getExternalStorageDirectory()
                + "/Cync/Camera/Images");
        file.mkdirs();

        String imageName = "Cync_" + Calendar.getInstance().getTimeInMillis() + ".jpg";

        File image = new File(file.getAbsolutePath(), imageName);


        if (image.exists()) image.delete();
        try {
            FileOutputStream out = new FileOutputStream(image);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return image.getAbsolutePath();
    }


    private Bitmap getBitmap(Uri path) {

        ContentResolver mContentResolver = getActivity().getContentResolver();
        Uri uri = path;
        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 1200000; // 1.2MP
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("TAG", "scale = " + scale + ", orig-width: " + o.outWidth
                    + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = mContentResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("TAG", "1th scale operation dimenions - width: " + width
                        + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("TAG", "bitmap size - width: " + b.getWidth() + ", height: "
                    + b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e("TAG", e.getMessage(), e);
            return null;
        }
    }


    @Override
    public void onFullScreenClick() {


        if (fullscreen_video.getVisibility() == View.GONE)        // 1 for Configuration.ORIENTATION_PORTRAIT
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
            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            videoLayout.addView(linearLayout, params);


            fullscreen_video.setVisibility(GONE);

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }


//        Toast.makeText(getActivity(), "FullScreen Click", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(getActivity(), FullScreenVideoActivity.class);
//        startActivity(intent);


    }


    public int getScreenOrientation() {
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
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


    public boolean isVideoPlaying() {

        if (fullscreen_video.getVisibility() == VISIBLE) {
            return true;
        } else
            return false;
    }


    public void setPortaite() {

        if (videoLayout != null) {
            ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(linearLayout);
            }


            params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT);

            videoLayout.addView(linearLayout, params);


            fullscreen_video.setVisibility(GONE);

            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        }
    }


    private void execCommand(String cmd) {

        Log.i(TAG, "success cmd===" + cmd);
        File mFile = new File(currentOutputVideoPath);
        if (mFile.exists()) {
            mFile.delete();

        }


        String[] command = cmd.split(" ");

        //  String[] cmdArray={cmd};

        CommonClass.showLoadingCompress(getActivity());
        mCompressor.execCommand(command, new CompressListener() {
            @Override
            public void onExecSuccess(String message) {
                Log.i(TAG, "success " + message);

                videoPath = currentOutputVideoPath;
                CommonClass.closeLoding(getActivity());
            }

            @Override
            public void onExecFail(String reason) {
                Log.i(TAG, "fail " + reason);
                //CommonClass.closeLoding(getActivity());
                CommonClass.closeLoding(getActivity());
            }

            @Override
            public void onExecProgress(String message) {
                Log.i(TAG, "progress== " + message);


            }
        });
    }

}