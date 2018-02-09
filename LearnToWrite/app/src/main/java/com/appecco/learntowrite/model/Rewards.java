package com.appecco.learntowrite.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

public class Rewards implements Serializable {

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
