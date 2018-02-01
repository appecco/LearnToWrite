package com.appecco.utils;

import android.content.Context;
import java.util.Locale;

public class Settings {

    public static final String CURRENT_LANGUAGE="currentLanguage";
    public static final String MUSIC_ENABLED="musicEnabled";
    public static final String SOUND_ENABLED="soundEnabled";
    public static final String DRAWING_COLOR="drawingColor";

    public enum DrawingColor{
        COLOR_BLUE, COLOR_RED, COLOR_GREEN
    }

    public static String get(Context context, String name, String defaultValue) {
        String value = StorageOperations.readPreferences(context, name, defaultValue);
        return value;
    }

    public static void set(Context context, String name, String value){
        StorageOperations.storePreferences(context, name, value);
    }

    public static String getCurrentLanguage(Context context){
        return get(context, CURRENT_LANGUAGE, Locale.getDefault().getLanguage());
    }

    public static void setCurrentLanguage(Context context, String currentLanguage){
        set(context, CURRENT_LANGUAGE, currentLanguage);
    }

    public static boolean isMusicEnabled(Context context){
        return Boolean.valueOf(Settings.get(context, Settings.MUSIC_ENABLED, Boolean.toString(Boolean.TRUE)));
    }

    public static void setMusicEnabled(Context context, boolean musicEnabled){
        set(context, Settings.MUSIC_ENABLED, Boolean.toString(musicEnabled));
    }

    public static boolean isSoundEnabled(Context context){
        return Boolean.valueOf(get(context, Settings.SOUND_ENABLED, Boolean.toString(Boolean.TRUE)));
    }

    public static void setSoundEnabled(Context context, boolean soundEnabled){
        set(context, Settings.SOUND_ENABLED, Boolean.toString(soundEnabled));
    }

    public static DrawingColor getDrawingColor(Context context){
        return DrawingColor.valueOf(get(context, Settings.DRAWING_COLOR, DrawingColor.COLOR_BLUE.toString()));
    }

    public static void setDrawingColor(Context context, DrawingColor drawingColor){
        set(context, Settings.DRAWING_COLOR, drawingColor.toString());
    }
}
