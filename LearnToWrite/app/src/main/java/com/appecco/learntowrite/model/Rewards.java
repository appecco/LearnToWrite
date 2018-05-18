package com.appecco.learntowrite.model;

import android.content.Context;

import com.appecco.learntowrite.RewardsActivity;
import com.appecco.utils.StorageOperations;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class Rewards implements Serializable {

    private final static String EARNED_STARS_KEY = "earnedStarsKey";
    private final static String SPENT_STARS_KEY = "spentStarsKey";

    private Reward[] rewards;

    public Reward[] getRewards() {
        return rewards;
    }

    public void setRewards(Reward[] rewards) {
        this.rewards = rewards;
    }

    public Reward findByTag(String rewardTag){
        if (rewardTag == null){
            return null;
        }
        for (Reward reward: rewards){
            if (rewardTag.equals(reward.getTag())){
                return reward;
            }
        }
        return null;
    }

    public void sortRewards(){
        Arrays.sort(rewards, 0, rewards.length - 1, new Comparator<Reward>() {
            @Override
            public int compare(Reward reward1, Reward reward2) {
                return Integer.compare(reward1.getCost(), reward2.getCost());
            }
        });
    }

    public void updateStatus(Context context) {
        boolean rewardUnlocked;
        for (Rewards.Reward reward: rewards){
            rewardUnlocked = Boolean.parseBoolean(StorageOperations.readPreferences(context, reward.getTag(), "false"));
            reward.setUnlocked(rewardUnlocked);
        }
        sortRewards();
    }

    public int getAvailableStars(Context context){
        int earnedStars, spentStars;
        earnedStars = Integer.parseInt(StorageOperations.readPreferences(context, EARNED_STARS_KEY,"0"));
        spentStars = Integer.parseInt(StorageOperations.readPreferences(context, SPENT_STARS_KEY,"0"));
        return earnedStars - spentStars;
    }

    public int getEarnedStars(Context context){
        int earnedStars;
        earnedStars = Integer.parseInt(StorageOperations.readPreferences(context, EARNED_STARS_KEY,"0"));
        return earnedStars;
    }

    public void addEarnedStars(Context context, int stars){
        int earnedStars;
        earnedStars = Integer.parseInt(StorageOperations.readPreferences(context, EARNED_STARS_KEY,"0"));
        earnedStars += stars;
        StorageOperations.storePreferences(context, EARNED_STARS_KEY, Integer.toString(earnedStars));
    }

    public void addSpentStars(Context context, int stars){
        int spentStars;
        spentStars = Integer.parseInt(StorageOperations.readPreferences(context, SPENT_STARS_KEY,"0"));
        spentStars += stars;
        StorageOperations.storePreferences(context, SPENT_STARS_KEY, Integer.toString(spentStars));
    }

    public static class Reward implements Serializable{
        private String tag;
        private String type;
        private String resourceName;
        private int cost;
        private transient boolean unlocked;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public int getCost() {
            return cost;
        }

        public void setCost(int cost) {
            this.cost = cost;
        }

        public boolean isUnlocked() {
            return unlocked;
        }

        public void setUnlocked(boolean unlocked) {
            this.unlocked = unlocked;
        }
    }
}
