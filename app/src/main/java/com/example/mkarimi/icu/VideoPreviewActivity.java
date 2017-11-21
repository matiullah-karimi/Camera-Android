package com.example.mkarimi.icu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;


public class VideoPreviewActivity extends AppCompatActivity {

    private VideoView videoView;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);
        videoView = findViewById(R.id.video);
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });
        final MessageView actualResolution = findViewById(R.id.actualResolution);

        final Uri videoUri = getIntent().getParcelableExtra("video");
        path = getIntent().getStringExtra("path");

        MediaController controller = new MediaController(this);
        controller.setAnchorView(videoView);
        controller.setMediaPlayer(videoView);
        videoView.setMediaController(controller);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                actualResolution.setTitle("Actual resolution");
                actualResolution.setMessage(mp.getVideoWidth() + " x " + mp.getVideoHeight());
                ViewGroup.LayoutParams lp = videoView.getLayoutParams();
                float videoWidth = mp.getVideoWidth();
                float videoHeight = mp.getVideoHeight();
                float viewWidth = videoView.getWidth();
                lp.height = (int) (viewWidth * (videoHeight / videoWidth));
                videoView.setLayoutParams(lp);
                playVideo();
            }
        });

        findViewById(R.id.btnUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadFileHelper.uploadMultipart(VideoPreviewActivity.this, path);
                Intent intent = new Intent(VideoPreviewActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    void playVideo() {
        if (videoView.isPlaying()) return;
        videoView.start();
    }
}
