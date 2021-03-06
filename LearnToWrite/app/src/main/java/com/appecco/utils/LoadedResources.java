package com.appecco.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
//import android.util.ArrayMap;
import android.support.v4.util.ArrayMap;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.appecco.learntowrite.R;

import java.util.Map;

public class LoadedResources implements SettingChangedListener {

    private static final int SOUND_POOL_MAX_STREAMS = 6;
    private static final int SOUND_POOL_DEFAULT_PRIORITY = 1;
    private static final int SOUND_POOL_DEFAULT_QUALITY = 0;
    private static final float SOUND_POOL_DEFAULT_RATE = 1.0f;
    private static final float SOUND_POOL_VOLUME = 1.0f;
    private static final int SOUND_POOL_NO_LOOP = 0;

    private static LoadedResources instance = null;

    // Simple sounds
    private boolean soundEnabled;
    private boolean musicEnabled;
    private SoundPool soundPool;
    // loadedSounds: key => resourceId, value => soundPoolId
    private Map<Integer, Integer> loadedSounds = new ArrayMap<>();
    // soundsStatus: key => soundPoolId, value => loadStatus
    private Map<Integer, Integer> soundsStatus = new ArrayMap<>();

    // Animations
    private Map<Integer, Animation> animations = new ArrayMap<>();

    private LoadedResources(){

    }

    public void loadResources(Context context){
        // Settings
        soundEnabled = Settings.isSoundEnabled(context);
        musicEnabled = Settings.isMusicEnabled(context);
        Settings.getInstance().addSettingChangedListener(this);

        // Simple sounds
        soundPool = new SoundPool(SOUND_POOL_MAX_STREAMS, AudioManager.STREAM_MUSIC, SOUND_POOL_DEFAULT_QUALITY);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                soundsStatus.put(soundId, status);
            }
        });
        loadSound(context, R.raw.button_click, SOUND_POOL_DEFAULT_PRIORITY);
        loadSound(context, R.raw.good, SOUND_POOL_DEFAULT_PRIORITY);
        loadSound(context, R.raw.bad, SOUND_POOL_DEFAULT_PRIORITY);
        loadSound(context, R.raw.children_cheer_short, SOUND_POOL_DEFAULT_PRIORITY);

        // Animations
        loadAnimation(context, R.anim.box_animation);
        loadAnimation(context, R.anim.star_animation);
    }

    public void playSound(int resourceId, float leftVolume, float rightVolume, int priority, int loop, float rate){
        Integer soundPoolId;
        Integer soundStatus;
        if (soundEnabled){
            try {
                soundPoolId = loadedSounds.get(resourceId);
                if (soundPoolId != null) {
                    soundStatus = soundsStatus.get(soundPoolId);
                    if (soundStatus != null && soundStatus == 0) {
                        soundPool.play(soundPoolId, leftVolume, rightVolume, priority, loop, rate);
                    }
                }
            } catch (Exception e){
                //Ignorar
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

    public Animation getAnimation(int resourceId){
        return animations.get(resourceId);
    }

    private void loadAnimation(Context context, int resourceId){
        Animation animation = AnimationUtils.loadAnimation(context, resourceId);
        animations.put(resourceId,animation);
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
