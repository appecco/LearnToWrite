package com.appecco.learntowrite.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BackgroundMusicServiceControl {

    public static void startBackgroundMusicService(Context context, int resourceId, int leftVolume, int rightVolume) {
        Bundle params = new Bundle();
        params.putInt(BackgroundMusicService.MUSIC_RESOURCE_ID_PARAM, resourceId);
        params.putInt(BackgroundMusicService.LEFT_VOLUME_PARAM,leftVolume);
        params.putInt(BackgroundMusicService.RIGHT_VOLUME_PARAM,rightVolume);
        sendMessage(context, BackgroundMusicService.MUSIC_START_COMMAND, params);
    }

    public static void resumeBackgroundMusic(Context context){
        sendMessage(context, BackgroundMusicService.MUSIC_RESUME_COMMAND);
    }

    public static void pauseBackgroundMusic(Context context){
        sendMessage(context, BackgroundMusicService.MUSIC_PAUSE_COMMAND);
    }

    public static void stopBackgroundMusic(Context context){
        sendMessage(context, BackgroundMusicService.MUSIC_STOP_COMMAND);
    }

    private static void sendMessage(Context context, String command){
        sendMessage(context, command, new Bundle());
    }

    private static void sendMessage(Context context, String command, Bundle params){
        Intent intent = new Intent(context, BackgroundMusicService.class);
        if (params == null){
            params = new Bundle();
        }
        params.putString(BackgroundMusicService.COMMAND_NAME_PARAM, command);
        intent.putExtras(params);
        context.startService(intent);
    }
}
