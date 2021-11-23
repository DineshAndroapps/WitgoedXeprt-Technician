package com.androapp.technician.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.androapp.technician.R;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity {
    private int currentWindow = 0;
    private boolean playWhenReady = true;
    private long playbackPosition = 0;
    private SimpleExoPlayer player;
    PlayerView playerView;
    String url;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_video_player);
        url = getIntent().getStringExtra("url");
        playerView = (PlayerView) findViewById(R.id.video_view);


    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ProgressiveMediaSource.Factory(new DefaultDataSourceFactory((Context) this, "exoplayer-codelab")).createMediaSource(uri);
    }

    private void initializePlayer() {
        SimpleExoPlayer build = new SimpleExoPlayer.Builder(this).build();
        player = build;
        playerView.setPlayer(build);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING);
        Uri parse = Uri.parse(url);
        MediaSource buildMediaSource = buildMediaSource(parse);
        Log.d("TAG", "initializePlayer: " + parse);
        player.setPlayWhenReady(this.playWhenReady);
        player.seekTo(this.currentWindow, playbackPosition);
        player.prepare(buildMediaSource, false, false);
    }


    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    public void onResume() {
        super.onResume();
        hideSystemUi();
        if (Util.SDK_INT < 24 || player == null) {
            initializePlayer();
        }
    }

    private void hideSystemUi() {
        playerView.setSystemUiVisibility(4871);
    }

    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        SimpleExoPlayer simpleExoPlayer = player;
        if (simpleExoPlayer != null) {
            playWhenReady = simpleExoPlayer.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }
}
