package com.appecco.learntowrite.dialog;

/**
 * Created by mauricio_peccorini on 15/01/2018.
 */

public interface GameDialogsEventsListener {

    void onCategoryDialogCancelPressed();
    void onCategorySelected(int gameOrder, int levelOrder);
    void onCharacterDialogCancelPressed();
    void onCharacterSelected(int gameOrder, int levelOrder, int characterIndex);
    void onCancelCharacterSelected();
    void onStartCharacterSelected();
    void onRetryCharacterSelected();
    void onNextCharacterSelected();
    void onFinishedCharacterDialogCancelPressed();
}
