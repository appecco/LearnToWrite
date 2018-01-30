package com.appecco.learntowrite.model;

import java.io.Serializable;

public class Progress implements Serializable {

    private Game[] games;
    private transient GameStructure gameStructure;

    public Game[] getGames() {
        return games;
    }

    public void setGames(Game[] games) {
        this.games = games;
    }

    public GameStructure getGameStructure(){
        return   this.gameStructure;
    }

    public void setGameStructure(GameStructure gameStructure){
        this.gameStructure = gameStructure;
    }

    public Game findGameByTag(String gameTag){
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

    public int getCurrentCategoryIndex(){
        int currentCategoryIndex=-1;
        for (Game game:games){
            for (Game.Level level:game.getLevels()){
                if (level.getScores()[0] != -1){
                    currentCategoryIndex++;
                } else {
                    return currentCategoryIndex;
                }
            }
        }
        return currentCategoryIndex;
    }

    public boolean updateScore(String gameTag, String levelTag, int characterIndex, int score){
        int[] scores;

        boolean levelFinished = true;

        // Obtener los scores
        scores = findGameByTag(gameTag).findLevelByTag(levelTag).getScores();

        //Verifiquemos que no se guarde un score invalido para el caracter actual aunque este haya estado mal antes (Score solo puede ser de -1 a 3)
        if (scores[characterIndex] < -1) {
            scores[characterIndex] = -1;
        }
        else if (scores[characterIndex] > 3) {
            scores[characterIndex] = 0;
        }

        // establecer el nuevo score si es mayor que el anterior
        scores[characterIndex] = (score>scores[characterIndex])?score:scores[characterIndex];

        // si se pasó el caracter, desbloquear el siguiente caracter o el siguiente nivel según corresponda
        if (score > 0) {
            if (scores.length - 1 > characterIndex) {
                levelFinished = false;
                if (scores[characterIndex + 1] == -1) {
                    // desbloquear el siguiente caracter
                    scores[characterIndex + 1] = 0;
                }
            } else {
                String nextLevelTag;
                if (gameStructure.nextLevelByTag(levelTag) != null
                        && findGameByTag(gameTag).findLevelByTag(gameStructure.nextLevelByTag(levelTag).getLevelTag()).getScores()[0] == -1) {
                    // desbloquear el siguiente nivel de dificultad
                    updateScore(gameTag, gameStructure.nextLevelByTag(levelTag).getLevelTag(), 0, 0);
                } else {
                    String firstLevelTag = gameStructure.findLevelByOrder(1).getLevelTag();
                    if (gameStructure.nextGameByTag(gameTag) != null
                            && findGameByTag(gameStructure.nextGameByTag(gameTag).getGameTag()).findLevelByTag(firstLevelTag).getScores()[0] == -1) {
                        // desbloquear el siguiente juego
                        findGameByTag(gameStructure.nextGameByTag(gameTag).getGameTag()).findLevelByTag(firstLevelTag).getScores()[0] = 0;
                    }
                }
            }
        }

        return levelFinished;
    }

    public static class Game implements Serializable {

        private String gameTag;
        private Level[] levels;

        public String getGameTag() {
            return gameTag;
        }

        public void setGameTag(String gameTag) {
            this.gameTag = gameTag;
        }

        public Level[] getLevels() {
            return levels;
        }

        public void setLevels(Level[] levels) {
            this.levels = levels;
        }

        public Level findLevelByTag(String levelTag){
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

        public static class Level implements Serializable {

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
