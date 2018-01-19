package com.appecco.learntowrite;

import java.io.IOException;

import org.json.JSONArray;

import com.appecco.learntowrite.dialog.CategoryMenuDialogFragment;
import com.appecco.learntowrite.dialog.CharacterFinishedDialogFragment;
import com.appecco.learntowrite.dialog.CharacterMenuDialogFragment;
import com.appecco.learntowrite.dialog.DrawingFragment;
import com.appecco.learntowrite.dialog.GameDialogsEventsListener;
import com.appecco.learntowrite.dialog.GameEventsListener;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import android.media.MediaPlayer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;

public class GameActivity extends AppCompatActivity implements GameEventsListener, GameDialogsEventsListener {

    // TODO: Estandarizar el manejo de excepciones, el registro de eventos en la bitácora y las notificaciones al usuario
    // TODO: Revisar valores quemados y cambiar por constantes o por variables o settings si aplica

    private static final String CURRENT_PROGRESS_KEY = "com.appecco.learntowrite.CURRENT_PROGRESS";
    private static final int ATTEMPTS_COUNT = 3;

    // Game: se refiere al tipo de caracteres que se está enseñando (por ejemplo: Cursivas Mayúsculas)
    // Level: se refiere al nivel de dificultad (por ejemplo: intermediate significa sin hint y con el outline ligeramente transparente
    // Character: se refiere a la letra que se está aprendiendo
    private int currentGameOrder;
    private int currentLevelOrder;
    private int currentCharacterIndex;

    private int currentCharacterScore;
    private int currentAttemptIndex;

    String currentLanguage;

    private GameStructure gameStructure;
    private Progress progress = null;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Preparar Ad
        PrepareInterstitialAd();

        currentLanguage = Settings.get(this, Settings.CURRENT_LANGUAGE, "es");

        Gson gson = new Gson();

        String gameStructureData = null;
        try {
            gameStructureData = StorageOperations.loadAssetsString(this, String.format("files/gameStructure_%s.json",currentLanguage));
        } catch (IOException ex) {
            Toast.makeText(this, "The levels definition file could not be loaded", Toast.LENGTH_LONG).show();
            Log.w("GameActivity", "Could not load the levels definition file. " + ex.getMessage());
        }
        gameStructure = gson.fromJson(gameStructureData, GameStructure.class);

        String progressData = null;
        progressData = StorageOperations.readPreferences(this, CURRENT_PROGRESS_KEY + currentLanguage, null);
        if (progressData == null) {
            try {
                progressData = StorageOperations.loadAssetsString(this, String.format("files/initialProgress_%s.json", currentLanguage));
            } catch (IOException ex) {
                Toast.makeText(this, "The progress initialization file could not be loaded", Toast.LENGTH_LONG).show();
                Log.w("GameActivity", "Could not load the initial progress file. " + ex.getMessage());
            }
        }
        progress = gson.fromJson(progressData,Progress.class);
        progress.setGameStructure(gameStructure);

        showCategoryMenuDialog();

    }

    void setupLevel(){
        currentCharacterScore = 0;
        currentAttemptIndex = 0;

        showDrawingFragment();
    }

    @Override
    public void readyForChallenge(){
        setupChallenge();
    }

    void setupChallenge(){
        DrawingFragment drawingFragment = (DrawingFragment) getSupportFragmentManager().findFragmentByTag("DrawingFragment");

        if (currentAttemptIndex > 0){
            // No importa si el nivel incluye mostrar hints, estos solo se muestran en el primer intento
            drawingFragment.setShowHints(false);
        }
        drawingFragment.setScore(currentCharacterScore);
        drawingFragment.startChallenge();

    }

    @Override
    public void challengeCompleted(int score){
        GameStructure.Level level = gameStructure.findLevelByOrder(currentLevelOrder);
        if (score >= level.getAccuracy()){
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.good);
            mp.start();
            currentCharacterScore++;
        } else {
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.bad);
            mp.start();
        }
        if (currentAttemptIndex < ATTEMPTS_COUNT - 1){
            currentAttemptIndex++;
            DrawingFragment drawingFragment = (DrawingFragment) getSupportFragmentManager().findFragmentByTag("DrawingFragment");
            setupChallenge();
        } else {
            // Eliminar DrawingFragment del stack y presentar el diálogo de fin del caracter
            getSupportFragmentManager().popBackStack();
            levelCompleted();
        }
    }

    void levelCompleted(){
        //Mostremos el Ad
        ShowInterstitialAd();

        String gameTag = gameStructure.findGameByOrder(currentGameOrder).getGameTag();
        String levelTag = gameStructure.findLevelByOrder(currentLevelOrder).getLevelTag();

        boolean levelFinished = progress.updateScore(gameTag, levelTag, currentCharacterIndex, currentCharacterScore);

        String progressData;
        Gson gson = new Gson();
        progressData = gson.toJson(progress);
        StorageOperations.storePreferences(this, CURRENT_PROGRESS_KEY + currentLanguage,progressData);

        CharacterMenuDialogFragment characterMenuDialogFragment = (CharacterMenuDialogFragment) getSupportFragmentManager().findFragmentByTag("CharacterMenuDialogFragment");
        if (characterMenuDialogFragment != null){
            characterMenuDialogFragment.loadCharacterButtons();
        }

        showCharacterFinishedDialog(currentCharacterScore, levelFinished);
    }

    public void showDrawingFragment(){
        GameStructure.Level level = gameStructure.findLevelByOrder(currentLevelOrder);
        String character = gameStructure.findGameByOrder(currentGameOrder).getCharacters()[currentCharacterIndex];

        FragmentManager fragmentManager;
        DrawingFragment drawingFragment = DrawingFragment.newInstance(character.charAt(0),level.isHints(),
                level.getContour(),level.isBeginningMark(),level.isEndingMark());
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, drawingFragment,"DrawingFragment");
        transaction.addToBackStack("DrawingFragment");
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    public void showCategoryMenuDialog(){
        FragmentManager fragmentManager;
        CategoryMenuDialogFragment categoryFragment = CategoryMenuDialogFragment.newInstance(gameStructure,progress);
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, categoryFragment);
        //transaction.addToBackStack("CategoryMenuFragment");
        transaction.commit();
    }

    public void showCharacterFinishedDialog(int score, boolean levelFinished) {
        FragmentManager fragmentManager;
        CharacterFinishedDialogFragment characterFinishedFragment = CharacterFinishedDialogFragment.newInstance(gameStructure,progress, currentGameOrder, currentLevelOrder, currentCharacterIndex, score, levelFinished);
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, characterFinishedFragment);
        transaction.addToBackStack("characterFinishedFragment");
        transaction.commit();
    }


    private void PrepareInterstitialAd() {
        //Inicializar Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");

        //Precargar un Ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void ShowInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

            //Una vez mostrado el Ad preparemos el siguiente
            PrepareInterstitialAd();
        }
    }

    @Override
    public void onCategoryDialogCancelPressed() {
        // Limpiar el stack eliminando el CategoryMenuDialogFragment
        // getSupportFragmentManager().popBackStack();
        finish();
    }

    @Override
    public void onCategorySelected(int gameOrder, int levelOrder) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CharacterMenuDialogFragment fragment = CharacterMenuDialogFragment.newInstance(gameStructure,progress, gameOrder, levelOrder);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, fragment,"CharacterMenuDialogFragment");
        transaction.addToBackStack("CharacterMenuDialogFragment");
        transaction.commit();
    }

    @Override
    public void onCharacterDialogCancelPressed() {
        // Regresar al menú de cateogrías revirtiendo la transacción que mostró el CharacterMenuDialogFragment
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCharacterSelected(int gameOrder, int levelOrder, int characterIndex) {
        this.currentGameOrder = gameOrder;
        this.currentLevelOrder = levelOrder;
        this.currentCharacterIndex = characterIndex;

        setupLevel();
    }

    @Override
    public void onRetryCharacterSelected() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStack();
        setupLevel();
    }

    @Override
    public void onNextCharacterSelected() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStack();
        this.currentCharacterIndex++;
        setupLevel();
    }

    @Override
    public void onFinishedCharacterDialogCancelPressed() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStack();
        // finish(); // Solo regresar al menú de caracteres, no hasta MainActivity
    }
}
