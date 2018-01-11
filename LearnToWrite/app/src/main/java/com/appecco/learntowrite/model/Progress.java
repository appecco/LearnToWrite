package com.appecco.learntowrite.model;

import java.io.Serializable;

public class Progress implements Serializable {

    private Game[] games;

    public Game[] getGames() {
        return games;
    }

    public void setGames(Game[] games) {
        this.games = games;
    }

    public Game findByTag(String gameTag){
        if (gameTag==null){
            return null;
        }
        for (Game game:games){
            if (gameTag.equals(game.gameTag)){
                return game;
            }
        }
        return null;
    }

    public static class Game {

        private String gameTag;
        private Level[] levels;

        public String getGameTag() {
            return gameTag;
        }

        public void setGameTag(String gameTag) {
            this.gameTag = gameTag;
        }

        public Level findByTag(String levelTag){
            if (levelTag==null){
                return null;
            }
            for (Level level:levels){
                if (levelTag.equals(level.levelTag)){
                    return level;
                }
            }
            return null;
        }

        public static class Level {

            private String levelTag;
            private int[] scores;

            public String getLevelTag() {
                return levelTag;
            }

            public void setLevelTag(String levelTag) {
                this.levelTag = levelTag;
            }

            public int[] getScores() {
                return scores;
            }

            public void setScores(int[] scores) {
                this.scores = scores;
            }
        }
    }
}
