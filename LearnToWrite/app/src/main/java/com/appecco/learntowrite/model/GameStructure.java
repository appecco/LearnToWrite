package com.appecco.learntowrite.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by mauricio_peccorini on 12/01/2018.
 */

public class GameStructure implements Serializable {

    private HashMap<String, String> alphaFriends;
    private Game[] games;
    private Level[] levels;

    public HashMap<String, String> getAlphaFriends() {
        return alphaFriends;
    }

    public void setAlphaFriends(HashMap<String, String> alphaFriends) {
        this.alphaFriends = alphaFriends;
    }

    public Game[] getGames() {
        return games;
    }

    public void setGames(Game[] games) {
        this.games = games;
    }

    public Level[] getLevels() {
        return levels;
    }

    public void setLevels(Level[] levels) {
        this.levels = levels;
    }

    public Game findGameByTag(String gameTag) {
        if (gameTag == null) {
            return null;
        }
        for (Game game : games) {
            if (gameTag.equals(game.gameTag)) {
                return game;
            }
        }
        return null;
    }

    public Game findGameByOrder(int order) {
        for (Game game : games) {
            if (order == game.gameOrder) {
                return game;
            }
        }
        return null;
    }

    public Game nextGameByTag(String gameTag) {
        Game currentGame = findGameByTag(gameTag);
        int currentGameOrder = currentGame.getGameOrder();
        Game nextGame = findGameByOrder(currentGameOrder + 1);
        return nextGame;
    }

    public Level findLevelByTag(String levelTag) {
        if (levelTag == null) {
            return null;
        }
        for (Level level : levels) {
            if (levelTag.equals(level.levelTag)) {
                return level;
            }
        }
        return null;
    }

    public Level findLevelByOrder(int order) {
        for (Level level : levels) {
            if (order == level.levelOrder) {
                return level;
            }
        }
        return null;
    }

    public Level nextLevelByTag(String levelTag) {
        Level currentLevel = findLevelByTag(levelTag);
        int currentLevelOrder = currentLevel.getLevelOrder();
        Level nextLevel = findLevelByOrder(currentLevelOrder + 1);
        return nextLevel;
    }

    public static class Game implements Serializable {

        private String name;
        private String gameTag;
        private int gameOrder;
        private String[] characters;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGameTag() {
            return gameTag;
        }

        public void setGameTag(String gameTag) {
            this.gameTag = gameTag;
        }

        public int getGameOrder() {
            return gameOrder;
        }

        public void setGameOrder(int gameOrder) {
            this.gameOrder = gameOrder;
        }

        public String[] getCharacters() {
            return characters;
        }

        public void setCharacters(String[] characters) {
            this.characters = characters;
        }
    }

    public static class Level implements Serializable {
        private String name;
        private String levelTag;
        private int levelOrder;
        private boolean hints;
        private String contour;
        private boolean beginningMark;
        private boolean endingMark;
        private int accuracy;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLevelTag() {
            return levelTag;
        }

        public void setLevelTag(String levelTag) {
            this.levelTag = levelTag;
        }

        public int getLevelOrder() {
            return levelOrder;
        }

        public void setLevelOrder(int levelOrder) {
            this.levelOrder = levelOrder;
        }

        public boolean isHints() {
            return hints;
        }

        public void setHints(boolean hints) {
            this.hints = hints;
        }

        public String getContour() {
            return contour;
        }

        public void setContour(String contour) {
            this.contour = contour;
        }

        public boolean isBeginningMark() {
            return beginningMark;
        }

        public void setBeginningMark(boolean beginningMark) {
            this.beginningMark = beginningMark;
        }

        public boolean isEndingMark() {
            return endingMark;
        }

        public void setEndingMark(boolean endingMark) {
            this.endingMark = endingMark;
        }

        public int getAccuracy() {
            return accuracy;
        }

        public void setAccuracy(int accuracy) {
            this.accuracy = accuracy;
        }
    }
}
