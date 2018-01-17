package com.appecco.learntowrite;

import java.io.IOException;

import org.json.JSONArray;

import com.appecco.learntowrite.dialog.CategoryMenuDialogFragment;
import com.appecco.learntowrite.dialog.CharacterFinishedDialogFragment;
import com.appecco.learntowrite.dialog.CharacterMenuDialogFragment;
import com.appecco.learntowrite.dialog.GameDialogsEventsListener;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

public class GameActivity extends AppCompatActivity implements GameDialogsEventsListener {

    // TODO: Estandarizar el manejo de excepciones, el registro de eventos en la bitácora y las notificaciones al usuario
    // TODO: Estandarizar el uso de Activity, Dialog y Fragment. Este método de navegación entre fragmentos me parece más flexible que usando Dialog
    // TODO: Convertir este Activity y todos sus Fragments a la librería support.v4, estandarizar los imports y las declaraciones de variables
    // TODO: Estandarizar el manejo de las interacciones a través de interfaces o clases genéricas, no a veces unas y a veces las otras
    // TODO: Corregir la navegación hacia adelante y hacia atras entre GameActivity, CategoryMenuDialogFragment y CharacterMenuDialogFragment
    // TODO: Revisar valores quemados y cambiar por constantes o por variables o settings si aplica

    private static final String CURRENT_PROGRESS_KEY = "com.appecco.learntowrite.CURRENT_PROGRESS";

    DrawingView viewDraw;

    // Game: se refiere al tipo de caracteres que se está enseñando (por ejemplo: Cursivas Mayúsculas)
    // Level: se refiere al nivel de dificultad (por ejemplo: intermediate significa sin hint y con el outline ligeramente transparente
    // Character: se refiere a la letra que se está aprendiendo
    private String currentGameName;
    private int currentGameOrder;
    private int currentLevelOrder;
    private int currentCharacterIndex;

    String currentLanguage;

    private GameStructure gameStructure;
    private Progress progress = null;

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
        currentGameName = b.getString("game");

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

//        Funcionalidad usada para preparar los hints, NO BORRAR!!!

//        Button btnNext = (Button)findViewById(R.id.btnNext);
//        btnNext.setOnClickListener(new OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                currentCharacterIndex++;
//                setupLevel();
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
        String character;

        character = gameStructure.findGameByOrder(currentGameOrder).getCharacters()[currentCharacterIndex];

        // TODO: Eliminar el uso de JSON dentro de DrawingView
        JSONArray characterGroup = new JSONArray();
        for (int i=0;i<3;i++) {
            characterGroup.put(character);
        }

        // TODO: Agregarle a DrawingView el mecanismo para establecer el nivel de dificultad
        // TODO: Corregir que DrawingView está dando una calificación después de mostrar el hint al cambiar de nivel
        viewDraw.setCharacterGroup(characterGroup);

    }

    public void levelCompleted(){
        //Mostremos el Ad
        ShowInterstitialAd();

        String gameTag = gameStructure.findGameByOrder(currentGameOrder).getGameTag();
        String levelTag = gameStructure.findLevelByOrder(currentLevelOrder).getLevelTag();
        //TODO: Obtener el score correcto desde DrawingView
        int score = 2;
        boolean levelFinished = progress.updateScore(gameTag, levelTag, currentCharacterIndex, score);

        String progressData;
        Gson gson = new Gson();
        progressData = gson.toJson(progress);
        StorageOperations.storePreferences(this, CURRENT_PROGRESS_KEY + currentLanguage,progressData);

        showCharacterFinishedDialog(score, levelFinished);
    }


    public void showCategoryMenuDialog(){
        FragmentManager fragmentManager;
        CategoryMenuDialogFragment categoryFragment = CategoryMenuDialogFragment.newInstance(gameStructure,progress);
        fragmentManager = getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, categoryFragment);
        transaction.addToBackStack("CategoryMenuFragment");
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1){
            // GameActivity no puede mostrarse si no es porque se ejecutará el 'juego' de un caracter
            // en cualquier caso, si el usuario presionó 'back' habiendo un solo fragmento en el stack
            // se debe cerrar el fragmento y la actividad
            getSupportFragmentManager().popBackStack();
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCategoryDialogCancelPressed() {
        // Limpiar el stack eliminando el CategoryMenuDialogFragment
        getSupportFragmentManager().popBackStack();
        finish();
    }

    @Override
    public void onCategorySelected(int gameOrder, int levelOrder) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CharacterMenuDialogFragment fragment = CharacterMenuDialogFragment.newInstance(gameStructure,progress, gameOrder, levelOrder);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, fragment);
        transaction.addToBackStack("LevelMenuFragment");
        transaction.commit();
    }

    @Override
    public void onCharacterDialogCancelPressed() {
        // Regresar al menú de cateogrías revirtiendo la transacción que mostró el CharacterMenuDialogFragment
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCharacterSelected(int gameOrder, int levelOrder, int characterIndex) {
        // En este momento se está "simulando" el comportamiento anterior de DrawingView
        // TODO: Cambiar DrawingView para encargarse únicamente del dibujado de una letra y su calificación
        // Cuando esto se cambie, GameActivity deberá encargarse por completo del flujo del juego, sus niveles y caracteres
        //int characterGroup = Integer.parseInt(levelDefinitions.getJSONObject(currentLevel).getString("characterGroup"));
        //viewDraw.setCharacterGroup(gameStructure.getJSONArray("characterGroups").getJSONArray(characterGroup));

        // Eliminar los fragmentos CategoryMenuDialogFragment y CharacterMenuDialogFragment
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().popBackStack();

        this.currentGameOrder = gameOrder;
        this.currentLevelOrder = levelOrder;
        this.currentCharacterIndex = characterIndex;

        setupLevel();
    }

    @Override
    public void onRetryCharacterSelected() {
        getSupportFragmentManager().popBackStack();
        setupLevel();
    }

    @Override
    public void onNextCharacterSelected() {
        getSupportFragmentManager().popBackStack();
        this.currentCharacterIndex++;
        setupLevel();
    }

    @Override
    public void onFinishedCharacterDialogCancelPressed() {
        getSupportFragmentManager().popBackStack();
        finish();
    }
}
