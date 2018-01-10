package com.appecco.utils;

import android.content.Context;

import com.appecco.learntowrite.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Settings {

    public static final String CURRENT_LANGUAGE="currentLanguage";

    public static String get(Context context, String name, String defaultValue) {
        String value = StorageOperations.readPreferences(context, name, defaultValue);
        return value;
    }

    public static void set(Context context, String name, String value){
        StorageOperations.storePreferences(context, name, value);
    }

}
