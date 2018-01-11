package com.appecco.learntowrite.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.appecco.learntowrite.model.Progress;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoriesPagerAdapter extends FragmentStatePagerAdapter {

    private JSONObject gameStructure;
    private Progress progress;

    public CategoriesPagerAdapter(FragmentManager fm, JSONObject gameStructure, Progress progress){
        super(fm);
        this.gameStructure = gameStructure;
        this.progress = progress;
    }

    @Override
    public int getCount() {
        int pageCount = 0;
        try {
            pageCount = gameStructure.getJSONArray("games").length() * gameStructure.getJSONArray("levels").length();
        } catch (JSONException ex){
            Log.v("CategoriesPagerAdapter","Invalid JSON gameStructure");
        }
        return pageCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        CategoryFragment fragment;
        int gameIndex = 0;
        int levelIndex = 0;
        try {
            gameIndex = position / gameStructure.getJSONArray("levels").length();
            levelIndex = position % gameStructure.getJSONArray("levels").length();
        } catch (JSONException ex){
            Log.v("CategoriesPagerAdapter",ex.getMessage());
        }
        fragment = CategoryFragment.newInstance(gameStructure,progress, gameIndex, levelIndex);
        return fragment;
    }
}
