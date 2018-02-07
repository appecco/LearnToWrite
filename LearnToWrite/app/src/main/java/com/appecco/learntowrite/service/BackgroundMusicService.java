package com.appecco.learntowrite.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import com.appecco.utils.Foreground;

public class BackgroundMusicService extends Service implements Foreground.Listener {

    public final static String MUSIC_RESOURCE_ID_PARAM = "musicResourceIdParam";
    public final static String LEFT_VOLUME_PARAM = "leftVolumeParam";
    public final static String RIGHT_VOLUME_PARAM = "rightVolumeParam";
    public final static String COMMAND_NAME_PARAM = "commandNameParam";
    public final static String MUSIC_START_COMMAND = "musicStartCommand";
    public final static String MUSIC_PAUSE_COMMAND = "musicPauseCommand";
    public final static String MUSIC_RESUME_COMMAND = "musicResumeCommand";
    public final static String MUSIC_STOP_COMMAND = "musicStopCommand";
    public final static String MEDIA_PLAYER_RELEASE_COMMAND = "mediaPlayerReleaseCommand";

    MediaPlayer player;
    private int currentResourceId = 0;
    private int length = 0;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Foreground.get().addListener(this);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        String command;
        int resourceId;
        int leftVolume;
        int rightVolume;
        if (bundle != null){
            command = bundle.getString(COMMAND_NAME_PARAM);
            switch (command){
                case MUSIC_START_COMMAND:
                    resourceId = bundle.getInt(MUSIC_RESOURCE_ID_PARAM);
                    leftVolume = bundle.getInt(LEFT_VOLUME_PARAM);
                    rightVolume = bundle.getInt(RIGHT_VOLUME_PARAM);
                    startMusic(resourceId,leftVolume,rightVolume);
                    break;
                case MUSIC_PAUSE_COMMAND:
                    pauseMusic();
                    break;
                case MUSIC_RESUME_COMMAND:
                    resumeMusic();
                    break;
                case MUSIC_STOP_COMMAND:
                    stopMusic();
                    break;
                case MEDIA_PLAYER_RELEASE_COMMAND:
                    releasePlayer();
                    break;
            }
        }
        return Service.START_STICKY;
    }

    public void startMusic(int resourceId, int leftVolume, int rightVolume){
        // If the service is already playing a different resource, stop it and release the player
        if (currentResourceId != 0 && currentResourceId != resourceId) {
            releasePlayer();
        }

        // Create the player only if it hasn't been created or was playing another resource
        if (player == null) {
            player = MediaPlayer.create(this, resourceId);
            player.setLooping(true);
            player.setVolume(leftVolume, rightVolume);
        }
        currentResourceId = resourceId;
        if (!player.isPlaying()) {
            player.start();
        }
    }

    public void pauseMusic(){
        if(player != null && player.isPlaying()){
            player.pause();
            length = player.getCurrentPosition();
        }
    }

    public void resumeMusic(){
        if(player != null && !player.isPlaying()){
            player.seekTo(length);
            player.start();
        }
    }

    public void stopMusic(){
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null){
            try{
                player.stop();
                player.release();
            } finally {
                player = null;
            }
        }
    }

    public IBinder onUnBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Foreground.get().removeListener(this);
        releasePlayer();
    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onBecameForeground() {
        resumeMusic();
    }

    @Override
    public void onBecameBackground() {
        pauseMusic();
    }
}
