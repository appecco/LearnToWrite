package com.appecco.learntowrite;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appecco.learntowrite.view.DrawingView;
import com.appecco.learntowrite.dialog.LevelDialogFragment;
import com.appecco.learntowrite.dialog.LevelMenuDialogFragment;
import com.appecco.utils.StorageOperations;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class GameActivity extends Activity implements LevelDialogFragment.LevelDialogListener, LevelMenuDialogFragment.LevelMenuDialogListener {

    private static final String LEVEL_FILE = "level";
    private static final String CURRENT_PROGRESS_KEY = "currentProgress";
    private static final String SETTINGS_FILE="settings";

    DrawingView viewDraw;

    private String currentGame;
    private JSONObject levelsJson;
    private JSONArray levelDefinitions;
    private int currentLevel;

    private JSONObject progress = null;
    private JSONArray scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        JSONArray gamesJson;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        viewDraw = (DrawingView)findViewById(R.id.viewDraw);

        Bundle b = getIntent().getExtras();
        currentGame = b.getString("game");

        Button btnRetry = (Button)findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.reset();
                EditText txtGesture = (EditText)findViewById(R.id.txtGesture);
                txtGesture.setText("");
            }

        });

        Button btnHint = (Button)findViewById(R.id.btnHint);
        btnHint.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.hint();

            }

        });

        Button btnNext = (Button)findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.next();

            }

        });

        Button btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.save();

            }

        });

        try {
            levelsJson = StorageOperations.loadAssetsJson(this, "files/levels.json");
            gamesJson = levelsJson.getJSONArray("games");
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
    }

    public void onRadPenWidthClick(View view) {

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radPenWide:
                if (checked)
                    viewDraw.setPenWidth(36);
                break;
            case R.id.radPenMedium:
                if (checked)
                    viewDraw.setPenWidth(24);
                break;
            case R.id.radPenThin:
                if (checked)
                    viewDraw.setPenWidth(16);
                break;
        }
    }

    public void onRadPenColorClick(View view){

        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.radColorBlue:
                if (checked)
                    viewDraw.setPenColor(Color.BLUE);
                break;
            case R.id.radColorRed:
                if (checked)
                    viewDraw.setPenColor(Color.RED);
                break;
            case R.id.radColorGreen:
                if (checked)
                    viewDraw.setPenColor(Color.GREEN);
                break;
            case R.id.radColorYellow:
                if (checked)
                    viewDraw.setPenColor(Color.YELLOW);
                break;
            case R.id.radColorOrange:
                if (checked)
                    viewDraw.setPenColor(Color.rgb(255, 140, 0));
                break;
        }
    }

    public void levelCompleted(){
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
        //StorageOperations.saveDataToPreferencesFile(this, LEVEL_FILE, new String[] {CURRENT_PROGRESS_KEY,Integer.toString(currentLevel)});
        StorageOperations.saveDataToPreferencesFile(this, LEVEL_FILE, new String[] {CURRENT_PROGRESS_KEY,progress.toString()});
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

    @Override
    public void onLevelMenuDialogSelection(DialogFragment dialog, int selectedLevel) {
        // TODO Cleanup commented code
        //try {
        //int characterGroup = levelDefinitions.getJSONObject(selectedLevel).getInt("characterGroup");
        currentLevel = selectedLevel;
        //viewDraw.setCharacterGroup(levelsJson.getJSONArray("characterGroups").getJSONArray(characterGroup));
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
            viewDraw.setCharacterGroup(levelsJson.getJSONArray("characterGroups").getJSONArray(characterGroup));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLevelDialogCancel(DialogFragment dialog) {
        finish();
    }

}
