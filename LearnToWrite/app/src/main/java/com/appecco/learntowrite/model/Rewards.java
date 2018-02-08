package com.appecco.learntowrite.model;

import java.io.Serializable;

/**
 * Created by mauricio_peccorini on 08/02/2018.
 */

public class Rewards implements Serializable {

    private Reward[] rewards;

    public Reward[] getRewards() {
        return rewards;
    }

    public void setRewards(Reward[] rewards) {
        this.rewards = rewards;
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
