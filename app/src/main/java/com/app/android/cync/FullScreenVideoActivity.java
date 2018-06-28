package com.app.android.cync;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.utils.ConnectionDetector;

import static com.ApplicationController.getContext;

/**
 * Created by zlinux on 10/2/17.
 */

/**
 * An activity that plays media using {@link SimpleExoPlayer}.
 */
public class FullScreenVideoActivity extends Activity implements PlaybackControlView.OnFullScreenClick {

    private SimpleExoPlayerView playerView;
    private PlaybackControlView.OnFullScreenClick mOnFullScreenClick;
    private FrameLayout playerLayout;
    private LinearLayout linearLayout;
    private ViewGroup viewGroup;
    Activity mActivity;
    SimpleExoPlayer player;
    FrameLayout.LayoutParams params;
    FrameLayout videoLayout;
    private ImageView ivBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        mActivity = FullScreenVideoActivity.this;
        playerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        videoLayout = (FrameLayout) findViewById(R.id.root);


        createVideoView();


        try {

//            Log.i("CyncTankFragment","onPlayClickListner pos====>"+pos);

            ConnectionDetector cd = new ConnectionDetector(mActivity);
            boolean isConnected = cd.isConnectingToInternet();
            if (isConnected) {


                ViewGroup mainGroup = (ViewGroup) playerLayout.getParent();
                if (mainGroup != null) {


                    mainGroup.removeView(playerLayout);

                }
                setVideoView(videoLayout);


            } else
                Toast.makeText(mActivity, "No internet connection.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createVideoView() {
        mOnFullScreenClick = this;
        playerLayout = new FrameLayout(mActivity);

//        player = new EMVideoView(mActivity);
//        progress = new ProgressView(mActivity);
        linearLayout = new LinearLayout(mActivity);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        viewGroup = (ViewGroup) playerView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViews();
        }
        linearLayout.addView(playerView);
    }


    private void setVideoView(FrameLayout videoLayout) {
        String path = "http://demo.zealousys.com:81/mimocare/wp-content/uploads/2016/07/346728036.mp4";//videoLayout.getTag().toString();

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
                Util.getUserAgent(getContext(), "CYNC"), defaultBandwidth);
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

                    ViewGroup viewGroup = (ViewGroup) linearLayout.getParent();
                    if (viewGroup != null) {
                        viewGroup.removeView(linearLayout);
                    }


//                    if (iv_thumbnail != null)
//                        iv_thumbnail.setVisibility(View.VISIBLE);

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

    @Override
    public void onFullScreenClick() {

    }

    @Override
    public void onBackPressed() {

        finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }
}
