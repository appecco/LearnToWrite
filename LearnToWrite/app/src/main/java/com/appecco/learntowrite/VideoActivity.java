package com.appecco.learntowrite;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;
import android.net.Uri;

public class VideoActivity extends Activity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = (VideoView) findViewById(R.id.video_view);
        videoView.setMediaController(null);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro));

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });

        videoView.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView != null){
            videoView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoView != null){
            videoView.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoView != null){
            videoView = null;
        };
    }
}
