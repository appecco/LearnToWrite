package com.appecco.learntowrite;

import java.io.IOException;

import org.json.JSONArray;

import com.appecco.learntowrite.dialog.CategoryMenuDialogFragment;
import com.appecco.learntowrite.dialog.CharacterFinishedDialogFragment;
import com.appecco.learntowrite.dialog.CharacterIntroDialogFragment;
import com.appecco.learntowrite.dialog.CharacterMenuDialogFragment;
import com.appecco.learntowrite.dialog.DrawingFragment;
import com.appecco.learntowrite.dialog.GameDialogsEventsListener;
import com.appecco.learntowrite.dialog.GameEventsListener;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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

    private static final int MAX_SOUND_POOL_STREAMS = 4;
    private static final int DEFAULT_SOUND_POOL_PRIORITY = 1;
    private static final int DEFAULT_SOUND_POOL_QUALITY = 0;
    private static final float DEFAULT_SOUND_POOL_RATE = 1.0f;
    private static final float SOUND_POOL_VOLUME = 1.0f;
    private static final int SOUND_POOL_NO_LOOP = 0;

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

    private MediaPlayer mp;
    private SoundPool soundPool;
    private int goodSoundId;
    private int badSoundId;
    private boolean goodSoundLoaded;
    private boolean badSoundLoaded;

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

        prepareSoundResources();

        showCategoryMenuDialog();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        prepareSoundResources();
    }

    void prepareSoundResources(){
        soundPool = new SoundPool(MAX_SOUND_POOL_STREAMS, AudioManager.STREAM_MUSIC, DEFAULT_SOUND_POOL_QUALITY);
        goodSoundId = soundPool.load(this, R.raw.good,DEFAULT_SOUND_POOL_PRIORITY);
        badSoundId = soundPool.load(this, R.raw.bad,DEFAULT_SOUND_POOL_PRIORITY);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
                if (status == 0){
                    if (soundId == goodSoundId){
                        goodSoundLoaded = true;
                    }
                    if (soundId == badSoundId){
                        badSoundLoaded = true;
                    }
                }
            }
        });
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
    public void readyForHint(){
        CharacterIntroDialogFragment characterIntroDialogFragment = (CharacterIntroDialogFragment)getSupportFragmentManager().findFragmentByTag("CharacterIntroDialogFragment");
        characterIntroDialogFragment.startHint();
    }

    @Override
    public void challengeCompleted(int score){
        GameStructure.Level level = gameStructure.findLevelByOrder(currentLevelOrder);
        if (score >= level.getAccuracy()){
//            mp = MediaPlayer.create(this, R.raw.good);
//            mp.start();
            if (goodSoundLoaded) {
                soundPool.play(goodSoundId, SOUND_POOL_VOLUME, SOUND_POOL_VOLUME, DEFAULT_SOUND_POOL_PRIORITY, SOUND_POOL_NO_LOOP, DEFAULT_SOUND_POOL_RATE);
            }
            currentCharacterScore++;
        } else {
//            mp = MediaPlayer.create(this, R.raw.bad);
//            mp.start();
            if (badSoundLoaded) {
                soundPool.play(badSoundId, SOUND_POOL_VOLUME, SOUND_POOL_VOLUME, DEFAULT_SOUND_POOL_PRIORITY, SOUND_POOL_NO_LOOP, DEFAULT_SOUND_POOL_RATE);
            }
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

    @Override
    protected void onStop(){
        super.onStop();
//        if (mp !=null){
//            mp.release();
//            mp = null;
//        }
        soundPool.release();
        soundPool = null;
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

        CategoryMenuDialogFragment categoryMenuDialogFragment = (CategoryMenuDialogFragment) getSupportFragmentManager().findFragmentByTag("CategoryMenuDialogFragment");
        if (categoryMenuDialogFragment != null){
            categoryMenuDialogFragment.loadCategoryButtons();
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
        transaction.add(android.R.id.content, categoryFragment,"CategoryMenuDialogFragment");
        //transaction.addToBackStack("CategoryMenuFragment");
        transaction.commit();
    }

    public void showCharacterFinishedDialog(int score, boolean levelFinished) {
        FragmentManager fragmentManager;
        CharacterFinishedDialogFragment characterFinishedFragment = CharacterFinishedDialogFragment.newInstance(gameStructure,progress, currentGameOrder, currentLevelOrder, currentCharacterIndex, score, levelFinished);
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, characterFinishedFragment, "CharacterFinishedDialogFragment");
        transaction.addToBackStack("CharacterFinishedDialogFragment");
        transaction.commit();
    }

    public void showCharacterIntroDialog(){
        FragmentManager fragmentManager;
        CharacterIntroDialogFragment characterIntroFragment = CharacterIntroDialogFragment.newInstance(gameStructure,progress, currentGameOrder, currentLevelOrder, currentCharacterIndex);
        fragmentManager = getSupportFragmentManager();

        characterIntroFragment.setGameStructure(gameStructure);
        characterIntroFragment.setProgress(progress);
        characterIntroFragment.setGameOrder(currentGameOrder);
        characterIntroFragment.setLevelOrder(currentLevelOrder);
        characterIntroFragment.setCharacterIndex(currentCharacterIndex);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, characterIntroFragment, "CharacterIntroDialogFragment");
        transaction.addToBackStack("CharacterIntroDialogFragment");
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

        showCharacterIntroDialog();
    }

    @Override
    public void onCancelCharacterSelected() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onStartCharacterSelected() {
        // Eliminar el fragmento de introducción del caracter del stack para que no regrese a él luego de finalizar el caracter
        getSupportFragmentManager().popBackStack();
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
