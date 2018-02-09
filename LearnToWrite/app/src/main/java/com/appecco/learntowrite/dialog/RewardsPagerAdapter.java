package com.appecco.learntowrite.dialog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.model.Rewards;

/**
 * Created by mauricio_peccorini on 08/02/2018.
 */

public class RewardsPagerAdapter extends FragmentStatePagerAdapter {

    private Rewards rewards;

    public RewardsPagerAdapter(FragmentManager fm, Rewards rewards){
        super(fm);
        this.rewards = rewards;
    }

    @Override
    public int getCount() {
        int pageCount = 0;
        pageCount = rewards.getRewards().length;
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
        RewardFragment fragment = null;
        Rewards.Reward reward = rewards.getRewards()[position];
        fragment = RewardFragment.newInstance(rewards, reward.getTag());
        return fragment;
    }

    @Override
    public float getPageWidth(int position){
        return 0.4f;
    }
}
