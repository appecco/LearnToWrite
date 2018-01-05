package com.appecco.utils;

import android.content.Context;

import com.appecco.learntowrite.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mauricio_peccorini on 04/01/2018.
 */

public class Settings {

    public static final String CURRENT_LANGUAGE="currentLanguage";

    private static final String SETTINGS_FILE="settings";

    public static String get(String name, String defaultValue) {
        String value = defaultValue;
        value = StorageOperations.readDataFromPreferencesFile(MainActivity.context, SETTINGS_FILE, name, defaultValue);
        return value;
    }

    public static void set(String name, String value){
        StorageOperations.saveDataToPreferencesFile(MainActivity.context, SETTINGS_FILE, new String[] {name, value});
    }

}
