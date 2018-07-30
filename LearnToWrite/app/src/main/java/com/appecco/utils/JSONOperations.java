package com.appecco.utils;

import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JSONOperations {

    public static void sort(JSONArray array, String field){
        List<JSONObject> objectList = new ArrayList<>();

        for (int i=0; i<array.length(); ){
            try {
                objectList.add(array.getJSONObject(0));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    array.remove(0);
                }
            } catch (JSONException ex){
                Log.v("JSONOperations",ex.getMessage());
            }
        }

        Collections.sort(objectList, (new Comparator<JSONObject>(){

            String field = null;

            @Override
            public int compare(JSONObject jsonObject1, JSONObject jsonObject2) {
                int order1, order2;
                try {
                    order1 = jsonObject1.getInt(field);
                    order2 = jsonObject2.getInt(field);
                    if (order1 > order2){
                        return 1;
                    }
                    if (order2 > order1) {
                        return -1;
                    }
                } catch (JSONException ex){
                    return 0;
                }
                return 0;
            }

            Comparator<JSONObject> initialize(String field){
                this.field = field;
                return this;
            }

        }).initialize(field));

        for (int i=0; i<objectList.size(); i++){
            try {
                array.put(i, objectList.get(i));
            } catch (JSONException ex){
                Log.v("JSONOperations",ex.getMessage());
            }
        }
    }
}
