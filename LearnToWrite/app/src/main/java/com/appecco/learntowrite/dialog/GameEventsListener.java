package com.appecco.learntowrite.dialog;

/**
 * Created by mauricio_peccorini on 18/01/2018.
 */

public interface GameEventsListener {

    void readyForChallenge();
    void readyForHint();
    void challengeCompleted(int score);

}
