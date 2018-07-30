package com.appecco.learntowrite;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;
import android.net.Uri;

import com.appecco.utils.Settings;

public class VideoActivity extends Activity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.video_view);
        videoView.setMediaController(null);
        videoView.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.intro));

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (Settings.isSoundEnabled(VideoActivity.this)){
                    mediaPlayer.setVolume(100,100);
                } else {
                    mediaPlayer.setVolume(0,0);
                }

                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finish();
            }
        });
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
            if (!videoView.isPlaying()) {
                videoView.resume();
            }
        }
    }

}
