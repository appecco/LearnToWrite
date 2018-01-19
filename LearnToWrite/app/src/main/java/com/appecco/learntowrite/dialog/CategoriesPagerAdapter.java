package com.appecco.learntowrite.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Toast;

import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoriesPagerAdapter extends FragmentStatePagerAdapter {

    private GameStructure gameStructure;
    private Progress progress;

    public CategoriesPagerAdapter(FragmentManager fm, GameStructure gameStructure, Progress progress){
        super(fm);
        this.gameStructure = gameStructure;
        this.progress = progress;
    }

    @Override
    public int getCount() {
        int pageCount = 0;
        pageCount = gameStructure.getGames().length * gameStructure.getLevels().length;
        return pageCount;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public Fragment getItem(int position) {
        CategoryFragment fragment;
        int gameOrder = 0;
        int levelOrder = 0;
        gameOrder = position / gameStructure.getLevels().length + 1;
        levelOrder = position % gameStructure.getLevels().length + 1;
        fragment = CategoryFragment.newInstance(gameStructure,progress, gameOrder, levelOrder);
        return fragment;
    }

    @Override
    public float getPageWidth(int position){
        return 0.4f;
    }
}
