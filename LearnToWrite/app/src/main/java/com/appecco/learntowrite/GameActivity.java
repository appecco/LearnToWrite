package com.appecco.learntowrite;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appecco.learntowrite.dialog.CategoryMenuDialogFragment;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.learntowrite.dialog.LevelDialogFragment;
import com.appecco.learntowrite.dialog.LevelMenuDialogFragment;
import com.appecco.utils.JSONOperations;
import com.appecco.utils.Settings;
import com.appecco.utils.StorageOperations;

import android.graphics.Color;
import android.net.Uri;
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

public class GameActivity extends AppCompatActivity implements CategoryMenuDialogFragment.OnFragmentInteractionListener, LevelDialogFragment.LevelDialogListener, LevelMenuDialogFragment.LevelMenuDialogListener {

    private static final String CURRENT_PROGRESS_KEY = "currentProgress";

    DrawingView viewDraw;

    private String currentGame;
    private JSONObject gameStructureJson;
    private JSONArray levelDefinitions;
    private int currentLevel;

    String currentLanguage;

    private JSONObject progress = null;
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
            gameStructureJson = StorageOperations.loadAssetsJson(this, String.format("files/gameStructure_%s.json",currentLanguage));
            JSONOperations.sort(gameStructureJson.getJSONArray("games"),"order");
            JSONOperations.sort(gameStructureJson.getJSONArray("levels"),"order");
        } catch (IOException | JSONException ex) {
            Toast.makeText(this, "The levels definition file could not be loaded", Toast.LENGTH_LONG).show();
        }

        try {
            progress = new JSONObject(StorageOperations.readPreferences(this, CURRENT_PROGRESS_KEY, null));
        } catch (Exception e){
            try {
                progress = StorageOperations.loadAssetsJson(this, "files/initialProgress.json");
            } catch (IOException ioe){
                Log.w("GameActivity","Could not load the initial progress file. " + e.getMessage());
            }
        }

        showCategoryMenuDialog();
/*
 * Sustitución de estructura inicial de 'juegos' y 'niveles'
 *

        try {
            gameStructureJson = StorageOperations.loadAssetsJson(this, "files/levels.json");
            gamesJson = gameStructureJson.getJSONArray("games");
            for (int i=0;i<gamesJson.length();i++){
                if (gamesJson.getJSONObject(i).getString("name").equals(currentGame)){
                    levelDefinitions = gamesJson.getJSONObject(i).getJSONArray("levels");
                }
            }
        } catch (IOException | JSONException ex) {
            Toast.makeText(this, "The levels definition file could not be loaded", Toast.LENGTH_LONG).show();
        }
        try {
            progress = new JSONObject(StorageOperations.readDataFromPreferencesFile(this, LEVEL_FILE, CURRENT_PROGRESS_KEY, null));
        } catch (Exception e){
            try {
                progress = StorageOperations.loadAssetsJson(this, "files/initialProgress.json");
            } catch (IOException ioe){
                Log.w("GameActivity","Could not load the initial progress file. " + e.getMessage());
            }
        }
        try {
            JSONArray games = progress.getJSONArray("games");
            currentLevel = 0;
            for (int i=0; i<games.length();i++){
                if (games.getJSONObject(i).getString("name").equals(currentGame)){
                    scores = games.getJSONObject(i).getJSONArray("scores");
                    for (int j=0;j<scores.length();j++){
                        if (scores.getInt(j)==0){
                            currentLevel = j;
                            Log.v("GameActivity","Selected level " + j);
                        }
                    }
                }
            }
        } catch (Exception e){}
        showLevelMenuDialog();
*/

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

        try {
            // TODO Set the score value to the number of stars earned for the level
            scores.put(currentLevel, 2);
            currentLevel++;
            if (levelDefinitions.length()>currentLevel){
                scores.put(currentLevel, 0);
            }
        } catch (JSONException e) {
            Log.w("GameActivity","Error while updating the level's progress. " + e.getMessage());
        }
        StorageOperations.storePreferences(this, CURRENT_PROGRESS_KEY,progress.toString());
        if (levelDefinitions.length()>currentLevel){
            showLevelDialog();
        } else {
            Toast.makeText(this, "Congratulations!!! , you have learnt all the " + currentGame, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void showLevelMenuDialog(){
        FragmentManager fragmentManager;
        LevelMenuDialogFragment dialog = new LevelMenuDialogFragment();
        dialog.setMessageText("Welcome to game " + currentGame);
        dialog.setLevels(levelDefinitions.length());
        dialog.setScores(scores);
        fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "LevelMenuDialogFragment");
    }

    public void showCategoryMenuDialog(){
        CategoryMenuDialogFragment categoryFragment = CategoryMenuDialogFragment.newInstance(gameStructureJson,progress);
        categoryFragment.setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL,0);
        categoryFragment.show(getSupportFragmentManager(),"CategoryMenuDialogFragment");
    }

    @Override
    public void onLevelMenuDialogSelection(DialogFragment dialog, int selectedLevel) {
        // TODO Cleanup commented code
        //try {
        //int characterGroup = levelDefinitions.getJSONObject(selectedLevel).getInt("characterGroup");
        currentLevel = selectedLevel;
        //viewDraw.setCharacterGroup(gameStructureJson.getJSONArray("characterGroups").getJSONArray(characterGroup));
        showLevelDialog();
        //} catch (JSONException e) {
        //	e.printStackTrace();
        //}
    }

    @Override
    public void onLevelMenuDialogCancel(DialogFragment dialog) {
        finish();
    }

    public void showLevelDialog() {
        FragmentManager fragmentManager;
        LevelDialogFragment dialog = new LevelDialogFragment();
        dialog.setMessageText("Welcome to level " + Integer.toString(currentLevel+1));
        fragmentManager = getFragmentManager();
        dialog.show(fragmentManager, "LevelDialogFragment");
    }

    @Override
    public void onLevelDialogStartLevel(DialogFragment dialog) {
        try {
            int characterGroup = Integer.parseInt(levelDefinitions.getJSONObject(currentLevel).getString("characterGroup"));
            viewDraw.setCharacterGroup(gameStructureJson.getJSONArray("characterGroups").getJSONArray(characterGroup));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(this, "Some interaction", Toast.LENGTH_LONG).show();
    }
}
