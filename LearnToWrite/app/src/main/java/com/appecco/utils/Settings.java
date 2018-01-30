package com.appecco.utils;

import android.content.Context;

import com.appecco.learntowrite.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {

    public static final String CURRENT_LANGUAGE="currentLanguage";
    public static final String MUSIC_ENABLED="musicEnabled";
    public static final String SOUND_ENABLED="soundEnabled";

    public static String get(Context context, String name, String defaultValue) {
        String value = StorageOperations.readPreferences(context, name, defaultValue);
        return value;
    }

    public static void set(Context context, String name, String value){
        StorageOperations.storePreferences(context, name, value);
    }

    public static boolean isMusicEnabled(Context context){
        return Boolean.valueOf(Settings.get(context, Settings.MUSIC_ENABLED, Boolean.toString(Boolean.TRUE)));
    }

    public static void setMusicEnabled(Context context, boolean musicEnabled){
        Settings.set(context, Settings.MUSIC_ENABLED, Boolean.toString(musicEnabled));
    }

    public static boolean isSoundEnabled(Context context){
        return Boolean.valueOf(Settings.get(context, Settings.SOUND_ENABLED, Boolean.toString(Boolean.TRUE)));
    }

    public static void setSoundEnabled(Context context, boolean soundEnabled){
        Settings.set(context, Settings.SOUND_ENABLED, Boolean.toString(soundEnabled));
    }

}
