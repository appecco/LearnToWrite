package com.appecco.learntowrite;

import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.appecco.learntowrite.dialog.CategoriesPagerAdapter;
import com.appecco.learntowrite.dialog.RewardFragment;
import com.appecco.learntowrite.dialog.RewardsPagerAdapter;
import com.appecco.learntowrite.model.Rewards;
import com.appecco.learntowrite.view.FixedSpeedScroller;
import com.appecco.utils.LoadedResources;
import com.appecco.utils.StorageOperations;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Field;

public class RewardsActivity extends AppCompatActivity implements RewardFragment.OnRewardPurchaseListener {

    private Rewards rewards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);

        String rewardsData = null;
        boolean rewardUnlocked;
        try {
            rewardsData = StorageOperations.loadAssetsString(this, "files/rewards.json");
        } catch (IOException ex) {
            Toast.makeText(this, "The rewards definition could not be loaded", Toast.LENGTH_LONG).show();
            Log.w("GameActivity", "Could not load the rewards definition file. " + ex.getMessage());
        }
        Gson gson = new Gson();
        rewards = gson.fromJson(rewardsData, Rewards.class);
        rewards.sortRewards();
        for (Rewards.Reward reward: rewards.getRewards()){
            rewardUnlocked = Boolean.parseBoolean(StorageOperations.readPreferences(this, reward.getTag(), "false"));
            reward.setUnlocked(rewardUnlocked);
        }

        RewardsPagerAdapter rewardsPagerAdapter = new RewardsPagerAdapter(getSupportFragmentManager(), rewards);
        ViewPager viewPager = (ViewPager)findViewById(R.id.rewardPager);
        viewPager.setAdapter(rewardsPagerAdapter);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new DecelerateInterpolator());
            scroller.setFixedDuration(1000);
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        ImageButton cancelButton = (ImageButton)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                finish();
            }
        });

    }


    @Override
    public void onRewardPurchase(Rewards.Reward reward) {
        // TODO: Agregar un cuadro de diálogo de confirmación
        // TODO: Verificar que se tenga el número suficiente de estrellas disponibles
        // TODO: Descontar el número de estrellas disponibles
        reward.setUnlocked(true);
        StorageOperations.storePreferences(this, reward.getTag(), "true");
    }
}
