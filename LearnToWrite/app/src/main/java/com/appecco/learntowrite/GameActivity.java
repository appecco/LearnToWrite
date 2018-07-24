package com.appecco.learntowrite;

import java.io.IOException;

import com.appecco.learntowrite.dialog.CategoryMenuDialogFragment;
import com.appecco.learntowrite.dialog.CharacterFinishedDialogFragment;
import com.appecco.learntowrite.dialog.CharacterIntroDialogFragment;
import com.appecco.learntowrite.dialog.CharacterMenuDialogFragment;
import com.appecco.learntowrite.dialog.DrawingFragment;
import com.appecco.learntowrite.dialog.GameDialogsEventsListener;
import com.appecco.learntowrite.dialog.GameEventsListener;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.service.BackgroundMusicServiceControl;
import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;
import com.tjeannin.apprate.AppRate;

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

    private Boolean currentCharacterScore[];
    private int currentAttemptIndex;

    String currentLanguage;

    private GameStructure gameStructure;
    private Progress progress = null;

    private InterstitialAd mInterstitialAd;
    private int counterToAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Preparar Ad
        PrepareInterstitialAd();

        currentLanguage = Settings.getCurrentLanguage(this);

        Gson gson = new Gson();

        String gameStructureData = null;
        try {
            gameStructureData = StorageOperations.loadAssetsString(this, String.format("files/gameStructure_%s.json",currentLanguage));
        } catch (IOException ex) {
            Toast.makeText(this, "The levels definition file could not be loaded", Toast.LENGTH_LONG).show();
            Log.w("GameActivity", "Could not load the levels definition file. " + ex.getMessage());
        }
        gameStructure = gson.fromJson(gameStructureData, GameStructure.class);

        String progressData = StorageOperations.readPreferences(this, CURRENT_PROGRESS_KEY + currentLanguage, null);
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

        // Este código solo se debe usar en tiempo de depuración para desbloquear todos los niveles
//        int scores[];
//        for (Progress.Game game: progress.getGames()){
//            for (Progress.Game.Level level: game.getLevels()){
//                scores = level.getScores();
//                for (int i=0; i<scores.length;i++){
//                    scores[i] = 3;
//                }
//            }
//        }

        prepareSoundResources();

        showCategoryMenuDialog();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        prepareSoundResources();
    }

    void prepareSoundResources(){
//        if (Settings.isSoundEnabled(GameActivity.this)) {
//
//        }
    }

    void setupLevel(){
        currentCharacterScore = new Boolean[3];
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
        DrawingFragment drawingFragment = (DrawingFragment) getSupportFragmentManager().findFragmentByTag("DrawingFragment");
        GameStructure.Level level = gameStructure.findLevelByOrder(currentLevelOrder);
        if (score >= level.getAccuracy()){
            LoadedResources.getInstance().playSound(R.raw.good);
            currentCharacterScore[currentAttemptIndex] = true;
        } else {
            LoadedResources.getInstance().playSound(R.raw.bad);
            currentCharacterScore[currentAttemptIndex] = false;
        }
        if (currentAttemptIndex < ATTEMPTS_COUNT - 1){
            if (currentCharacterScore[currentAttemptIndex]) {
                drawingFragment.startStarAnimation();
            }
            currentAttemptIndex++;
            setupChallenge();
        } else {
            // Eliminar DrawingFragment del stack y presentar el diálogo de fin del caracter
            getSupportFragmentManager().popBackStackImmediate();
            processChallengeCompleted();
        }
    }

    void processChallengeCompleted(){
        int scoreValue = 0;
        for (int i=0; i<ATTEMPTS_COUNT; i++){
            if (currentCharacterScore[i] != null && currentCharacterScore[i]){
                scoreValue++;
            }
        }

        String gameTag = gameStructure.findGameByOrder(currentGameOrder).getGameTag();
        String levelTag = gameStructure.findLevelByOrder(currentLevelOrder).getLevelTag();

        boolean levelFinished = progress.updateScore(gameTag, levelTag, currentCharacterIndex, scoreValue);

        String progressData;
        Gson gson = new Gson();
        progressData = gson.toJson(progress);
        StorageOperations.storePreferences(this, CURRENT_PROGRESS_KEY + currentLanguage,progressData);

        CharacterMenuDialogFragment characterMenuDialogFragment = (CharacterMenuDialogFragment) getSupportFragmentManager().findFragmentByTag("CharacterMenuDialogFragment");
        if (characterMenuDialogFragment != null){
            characterMenuDialogFragment.setGameOrder(currentGameOrder);
            characterMenuDialogFragment.setLevelOrder(currentLevelOrder);
            characterMenuDialogFragment.loadCharacterButtons();
        }

        CategoryMenuDialogFragment categoryMenuDialogFragment = (CategoryMenuDialogFragment) getSupportFragmentManager().findFragmentByTag("CategoryMenuDialogFragment");
        if (categoryMenuDialogFragment != null){
            categoryMenuDialogFragment.loadCategoryButtons();
        }

        showCharacterFinishedDialog(scoreValue, levelFinished);
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

    public void showCharacterIntroDialog(){
        CharacterIntroDialogFragment characterIntroFragment = CharacterIntroDialogFragment.newInstance(gameStructure,progress, currentGameOrder, currentLevelOrder, currentCharacterIndex);
        characterIntroFragment.setGameStructure(gameStructure);
        characterIntroFragment.setProgress(progress);
        characterIntroFragment.setGameOrder(currentGameOrder);
        characterIntroFragment.setLevelOrder(currentLevelOrder);
        characterIntroFragment.setCharacterIndex(currentCharacterIndex);
        showGameRelatedFragment(characterIntroFragment,"CharacterIntroDialogFragment");
    }

    public void showDrawingFragment(){
        GameStructure.Level level = gameStructure.findLevelByOrder(currentLevelOrder);
        String character = gameStructure.findGameByOrder(currentGameOrder).getCharacters()[currentCharacterIndex];

        int charactersBeforeAd = 5 - currentLevelOrder;
        if (counterToAd >= charactersBeforeAd){
            counterToAd = 0;
            //Mostremos el Ad
            ShowInterstitialAd();
        } else {
            counterToAd++;

            new AppRate(this)
                    .setShowIfAppHasCrashed(false)
                    .setMinDaysUntilPrompt(5)
                    .setMinLaunchesUntilPrompt(3)
                    .init();

//      Usar esta alternativa solo para pruebas, mostraría el diálogo SIEMPRE
//            new AppRate(this)
//                    .init();
        }

        DrawingFragment drawingFragment = DrawingFragment.newInstance(character.charAt(0),level.isHints(),
                level.getContour(),level.isBeginningMark(),level.isEndingMark());
        showGameRelatedFragment(drawingFragment,"DrawingFragment");
    }

    public void showCharacterFinishedDialog(int score, boolean levelFinished) {
        CharacterFinishedDialogFragment characterFinishedFragment = CharacterFinishedDialogFragment.newInstance(gameStructure,progress, currentGameOrder, currentLevelOrder, currentCharacterIndex, score, levelFinished);
        showGameRelatedFragment(characterFinishedFragment,"CharacterFinishedDialogFragment");
    }

    private void showGameRelatedFragment(Fragment fragment, String tag){
        FragmentManager fragmentManager;
        fragmentManager = getSupportFragmentManager();

        BackgroundMusicServiceControl.startBackgroundMusicService(this, R.raw.drawing_background_sound, 50, 50);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        Fragment characterMenuDialogFragment = fragmentManager.findFragmentByTag("CharacterMenuDialogFragment");
        if (characterMenuDialogFragment != null && !characterMenuDialogFragment.isHidden()){
            transaction.hide(characterMenuDialogFragment);
        }

        transaction.add(android.R.id.content, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        fragmentManager.executePendingTransactions();
    }

    private void PrepareInterstitialAd() {
        //Inicializar Interstitial Ads
        mInterstitialAd = new InterstitialAd(this);
        //Ad Unit de Prueba
        //mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        //Ad Unit de Learn To Write: Cursive
        mInterstitialAd.setAdUnitId("ca-app-pub-1507251474990125/7111273261");

        //Precargar un Ad
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void ShowInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }

        //Ya sea que se haya podido mostrar un Ad o no hay que preparar nuevamente ya que si no habia un Ad en esta oportunidad queremos que prepare un para el siguiente intento
        PrepareInterstitialAd();
    }

    @Override
    public void onCategoryDialogCancelPressed() {
        finish();
    }

    @Override
    public void onCategorySelected(int gameOrder, int levelOrder) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment categoryMenuDialogFragment = fragmentManager.findFragmentByTag("CategoryMenuDialogFragment");
        CharacterMenuDialogFragment fragment = CharacterMenuDialogFragment.newInstance(gameStructure,progress, gameOrder, levelOrder);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.hide(categoryMenuDialogFragment);
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
        getSupportFragmentManager().popBackStackImmediate();
        setupLevel();
    }

    @Override
    public void onRetryCharacterSelected() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStackImmediate();
        setupLevel();
    }

    @Override
    public void onNextCharacterSelected() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStackImmediate();
        this.currentCharacterIndex++;
        //setupLevel();
        showCharacterIntroDialog();
    }

    @Override
    public void onNextLevelSelected() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStackImmediate();
        this.currentCharacterIndex=0;
        if (gameStructure.getLevels().length > currentLevelOrder){
            currentLevelOrder++;
        } else {
            currentLevelOrder = 1;
            if (gameStructure.getGames().length > currentGameOrder){
                currentGameOrder++;
            } else {
                // El juego ha finalizado, ciclar al primer juego
                currentGameOrder = 1;
            }
        }

        CharacterMenuDialogFragment characterMenuDialogFragment = (CharacterMenuDialogFragment) getSupportFragmentManager().findFragmentByTag("CharacterMenuDialogFragment");
        if (characterMenuDialogFragment != null){
            characterMenuDialogFragment.setGameOrder(currentGameOrder);
            characterMenuDialogFragment.setLevelOrder(currentLevelOrder);
            characterMenuDialogFragment.loadCharacterButtons();
        }

        CategoryMenuDialogFragment categoryMenuDialogFragment = (CategoryMenuDialogFragment) getSupportFragmentManager().findFragmentByTag("CategoryMenuDialogFragment");
        if (categoryMenuDialogFragment != null){
            categoryMenuDialogFragment.loadCategoryButtons();
        }

        //setupLevel();
        showCharacterIntroDialog();
    }

    @Override
    public void onFinishedCharacterDialogCancelPressed() {
        // Eliminar el fragmento de fin de caracter
        getSupportFragmentManager().popBackStack();
        // finish(); // Solo regresar al menú de caracteres, no hasta MainActivity
    }
}
