package com.appecco.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.ArrayMap;

import com.appecco.learntowrite.R;

import java.util.Map;

public class LoadedResources implements SettingChangedListener {

    public static final int SOUND_POOL_MAX_STREAMS = 6;
    public static final int SOUND_POOL_DEFAULT_PRIORITY = 1;
    public static final int SOUND_POOL_DEFAULT_QUALITY = 0;
    public static final float SOUND_POOL_DEFAULT_RATE = 1.0f;
    public static final float SOUND_POOL_VOLUME = 1.0f;
    public static final int SOUND_POOL_NO_LOOP = 0;

    private static LoadedResources instance = null;

    private boolean soundEnabled;
    private boolean musicEnabled;
    private SoundPool soundPool;
    // loadedSounds: key => resourceId, value => soundPoolId
    private Map<Integer, Integer> loadedSounds = new ArrayMap<Integer, Integer>();
    // soundsStatus: key => soundPoolId, value => loadStatus
    private Map<Integer, Integer> soundsStatus = new ArrayMap<Integer, Integer>();

    private LoadedResources(){

    }

    public void loadResources(Context context){
        soundPool = new SoundPool(SOUND_POOL_MAX_STREAMS, AudioManager.STREAM_MUSIC, SOUND_POOL_DEFAULT_QUALITY);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                soundsStatus.put(soundId, status);
            }
        });

        loadSound(context, R.raw.good, SOUND_POOL_DEFAULT_PRIORITY);
        loadSound(context, R.raw.bad, SOUND_POOL_DEFAULT_PRIORITY);
        loadSound(context, R.raw.button_click, SOUND_POOL_DEFAULT_PRIORITY);

        soundEnabled = Settings.isSoundEnabled(context);
        musicEnabled = Settings.isMusicEnabled(context);
        Settings.getInstance().addSettingChangedListener(this);
    }

    public void playSound(int resourceId, float leftVolume, float rightVolume, int priority, int loop, float rate){
        int soundPoolId;
        int soundStatus;
        if (soundEnabled){
            try{
                soundPoolId = loadedSounds.get(resourceId);
                soundStatus = soundsStatus.get(soundPoolId);
                if (soundStatus == 0){
                    soundPool.play(soundPoolId, leftVolume, rightVolume, priority, loop, rate);
                }
            }
            catch (Exception e){

            }
        }
    }

    public void playSound(int resourceId){
           playSound(resourceId, SOUND_POOL_VOLUME, SOUND_POOL_VOLUME, SOUND_POOL_DEFAULT_PRIORITY, SOUND_POOL_NO_LOOP, SOUND_POOL_DEFAULT_RATE);
    }

    private void loadSound(Context context, int resourceId, int defaultPriority){
        int soundPoolId = soundPool.load(context, resourceId, defaultPriority);
        loadedSounds.put(resourceId,soundPoolId);
    }

    @Override
    public void onSettingChanged(String setting, String newValue) {
        switch (setting){
            case Settings.SOUND_ENABLED:
                soundEnabled = Boolean.parseBoolean(newValue);
                break;
            case Settings.MUSIC_ENABLED:
                musicEnabled = Boolean.parseBoolean(newValue);
                break;
        }
    }

    public static synchronized LoadedResources getInstance(){
        if (instance == null){
            instance = new LoadedResources();
        }
        return instance;
    }

    public void releaseResources() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
