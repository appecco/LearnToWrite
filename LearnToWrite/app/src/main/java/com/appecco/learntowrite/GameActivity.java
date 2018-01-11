package com.appecco.learntowrite;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appecco.learntowrite.dialog.CategoryMenuDialogFragment;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.learntowrite.dialog.LevelDialogFragment;
import com.appecco.learntowrite.dialog.LevelMenuDialogFragment;
import com.appecco.utils.JSONOperations;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;
import com.google.gson.Gson;

public class GameActivity extends AppCompatActivity implements CategoryMenuDialogFragment.CategoryMenuInteractionListener, LevelDialogFragment.LevelDialogListener, LevelMenuDialogFragment.LevelMenuDialogListener {

    private static final String CURRENT_PROGRESS_KEY = "com.appecco.learntowrite.CURRENT_PROGRESS";

    DrawingView viewDraw;

    private JSONObject gameStructure;
    private JSONArray levelDefinitions;

    // Game: se refiere al tipo de caracteres que se está enseñando (por ejemplo: Cursivas Mayúsculas)
    // Level: se refiere al nivel de dificultad (por ejemplo: intermediate significa sin hint y con el outline ligeramente transparente
    // Character: se refiere a la letra que se está aprendiendo
    private String currentGame;
    private int currentGameIndex;
    private int currentLevelIndex;
    private int currentCharacterIndex;

    String currentLanguage;

    private Progress progress = null;
    private JSONArray scores;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSONArray gamesJson;
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        setContentView(R.layout.activity_game);

        //Preparar Ad
        PrepareInterstitialAd();

        viewDraw = (DrawingView)findViewById(R.id.viewDraw);

        Bundle b = getIntent().getExtras();
        currentGame = b.getString("game");

        ImageButton btnRetry = (ImageButton)findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.reset();
                //EditText txtGesture = (EditText)findViewById(R.id.txtGesture);
                //txtGesture.setText("");
            }

        });

        ImageButton btnHint = (ImageButton)findViewById(R.id.btnHint);
        btnHint.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.hint();

            }

        });

        ImageButton btnRed = (ImageButton)findViewById(R.id.btnRed);
        btnRed.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.setPenColor(Color.RED);

            }

        });

        ImageButton btnBlue = (ImageButton)findViewById(R.id.btnBlue);
        btnBlue.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.setPenColor(Color.BLUE);

            }

        });

        ImageButton btnGreen = (ImageButton)findViewById(R.id.btnGreen);
        btnGreen.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.setPenColor(Color.GREEN);

            }

        });


//        Button btnNext = (Button)findViewById(R.id.btnNext);
//        btnNext.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                viewDraw.next();
//
//            }
//
//        });
//
//        Button btnSave = (Button)findViewById(R.id.btnSave);
//        btnSave.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                viewDraw.save();
//
//            }
//
//        });

        currentLanguage = Settings.get(this, Settings.CURRENT_LANGUAGE, "es");

        try {
            //TODO: Cargar la estructura del juego a POJOs para evitar tener que usar índices de arreglos y capturar JSONExceptions en todos lados
            gameStructure = StorageOperations.loadAssetsJson(this, String.format("files/gameStructure_%s.json",currentLanguage));
            JSONOperations.sort(gameStructure.getJSONArray("games"),"order");
            JSONOperations.sort(gameStructure.getJSONArray("levels"),"order");
        } catch (IOException | JSONException ex) {
            Toast.makeText(this, "The levels definition file could not be loaded", Toast.LENGTH_LONG).show();
        }

        String progressData = null;
        progressData = StorageOperations.readPreferences(this, CURRENT_PROGRESS_KEY + currentLanguage, null);
        if (progressData == null) {
            try {
                progressData = StorageOperations.loadAssetsString(this, String.format("files/initialProgress_%s.json", currentLanguage));
            } catch (IOException ioe) {
                Log.w("GameActivity", "Could not load the initial progress file. " + ioe.getMessage());
            }
        }
        Gson gson = new Gson();
        progress = gson.fromJson(progressData,Progress.class);

        showCategoryMenuDialog();

    }

//    public void onRadPenWidthClick(View view) {
//
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch(view.getId()) {
//            case R.id.radPenWide:
//                if (checked)
//                    viewDraw.setPenWidth(36);
//                break;
//            case R.id.radPenMedium:
//                if (checked)
//                    viewDraw.setPenWidth(24);
//                break;
//            case R.id.radPenThin:
//                if (checked)
//                    viewDraw.setPenWidth(16);
//                break;
//        }
//    }

//    public void onRadPenColorClick(View view){
//
//        boolean checked = ((RadioButton) view).isChecked();
//
//        switch(view.getId()) {
//            case R.id.radColorBlue:
//                if (checked)
//                    viewDraw.setPenColor(Color.BLUE);
//                break;
//            case R.id.radColorRed:
//                if (checked)
//                    viewDraw.setPenColor(Color.RED);
//                break;
//            case R.id.radColorGreen:
//                if (checked)
//                    viewDraw.setPenColor(Color.GREEN);
//                break;
//            case R.id.radColorYellow:
//                if (checked)
//                    viewDraw.setPenColor(Color.YELLOW);
//                break;
//            case R.id.radColorOrange:
//                if (checked)
//                    viewDraw.setPenColor(Color.rgb(255, 140, 0));
//                break;
//        }
//    }

    public void levelCompleted(){
        //Mostremos el Ad
        ShowInterstitialAd();

        int[] scores;
        try {
            String gameTag = gameStructure.getJSONArray("games").getJSONObject(currentGameIndex).getString("tag");
            String levelTag = gameStructure.getJSONArray("levels").getJSONObject(currentLevelIndex).getString("tag");
            scores = progress.findByTag(gameTag).findByTag(levelTag).getScores();
            //TODO: Obtener el score correcto desde DrawingView
            scores[currentCharacterIndex] = 2;
            if (scores.length - 1 > currentCharacterIndex
                    && scores[currentCharacterIndex+1] == -1){
                // desbloquear el siguiente caracter
                scores[currentCharacterIndex+1] = 0;
            } else {
                if (progress.findByTag(gameTag).getLevels().length - 1 > currentLevelIndex
                    && progress.findByTag(gameTag).getLevels()[currentLevelIndex+1].getScores()[0] == -1){
                    // desbloquear el siguiente nivel de dificultad
                    progress.findByTag(gameTag).getLevels()[currentLevelIndex+1].getScores()[0] = 0;
                } else {
                    if (progress.getGames().length - 1 > currentGameIndex
                        && progress.getGames()[currentGameIndex+1].getLevels()[0].getScores()[0] == -1){
                        // desbloquear el siguiente juego
                        progress.getGames()[currentGameIndex+1].getLevels()[0].getScores()[0] = 0;
                    }

                }
            }
        } catch (JSONException ex){
            Toast.makeText(this,"Unable set the updated progress",Toast.LENGTH_LONG).show();
        }

        String progressData;
        Gson gson = new Gson();
        progressData = gson.toJson(progress);
        StorageOperations.storePreferences(this, CURRENT_PROGRESS_KEY + currentLanguage,progressData);

        int gameLength;
        try {
            gameLength = gameStructure.getJSONArray("games").getJSONObject(currentGameIndex).getJSONArray("characters").length();
        } catch (JSONException ex){
            gameLength = 1;
        }

        //TODO: Mostrar el dialogo de resultado del caracter o fin de nivel y las opciones de continuar o reintentar
        if (gameLength > currentCharacterIndex){
            this.currentCharacterIndex++;
            setupLevel();
        } else {
            Toast.makeText(this, "Congratulations!!! , you have learnt all the " + currentGame, Toast.LENGTH_LONG).show();
            finish();
        }

    }

    public void showCategoryMenuDialog(){
        CategoryMenuDialogFragment categoryFragment = CategoryMenuDialogFragment.newInstance(gameStructure,progress);
        categoryFragment.setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL,0);
        categoryFragment.show(getSupportFragmentManager(),"CategoryMenuDialogFragment");
    }

    void setupLevel(){
        String character;

        try {
            character = gameStructure.getJSONArray("games").getJSONObject(currentGameIndex).getJSONArray("characters").getString(currentCharacterIndex);
        } catch (JSONException ex){
            character = "?";
        }

        JSONArray characterGroup = new JSONArray();
        for (int i=0;i<3;i++) {
            characterGroup.put(character);
        }

        // TODO: Agregarle a DrawingView el mecanismo para establecer el nivel de dificultad
        viewDraw.setCharacterGroup(characterGroup);

    }

    @Override
    public void onLevelMenuDialogSelection(int gameIndex, int levelIndex, int characterIndex) {
        //currentLevel = selectedLevel;

        // El diálogo del nivel se va a mostrar solo cuando se haya finalizado el nivel
        // indicando el resultado y permitiendo pasar al siguiente o intentar de nuevo
        // showLevelDialog();

        // En este momento se está "simulando" el comportamiento anterior de DrawingView
        // TODO: Cambiar DrawingView para encargarse únicamente del dibujado de una letra y su calificación
        // Cuando esto se cambie, GameActivity deberá encargarse por completo del flujo del juego, sus niveles y caracteres
        //int characterGroup = Integer.parseInt(levelDefinitions.getJSONObject(currentLevel).getString("characterGroup"));
        //viewDraw.setCharacterGroup(gameStructure.getJSONArray("characterGroups").getJSONArray(characterGroup));

        this.currentGameIndex = gameIndex;
        this.currentLevelIndex = levelIndex;
        this.currentCharacterIndex = characterIndex;

        setupLevel();

    }

    @Override
    public void onLevelMenuDialogCancel(DialogFragment dialog) {
        finish();
    }

    public void showLevelDialog() {
        FragmentManager fragmentManager;
        LevelDialogFragment dialog = new LevelDialogFragment();
        //dialog.setMessageText("Welcome to level " + Integer.toString(currentLevel+1));
        //TODO: Cambiar el título del nivel por la letra que se está aprendiendo y su respectivo Alphafriend
        fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "LevelDialogFragment");
    }

    @Override
    public void onLevelDialogStartLevel(DialogFragment dialog) {
        //TODO: Cambiar esta interacción por "retry level" y agregar la lógica correspondiente
/*
        try {
            int characterGroup = Integer.parseInt(levelDefinitions.getJSONObject(currentLevel).getString("characterGroup"));
            viewDraw.setCharacterGroup(gameStructure.getJSONArray("characterGroups").getJSONArray(characterGroup));
        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    public void onLevelDialogCancel(DialogFragment dialog) {
        finish();
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
    public void onCategoryDialogCancel() {
        finish();
    }
}
