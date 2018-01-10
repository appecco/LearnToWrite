package com.appecco.learntowrite.dialog;


import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.appecco.learntowrite.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private JSONObject gameStructure;
    private JSONObject progress;
    private int gameIndex;
    private int levelIndex;

    public static CategoryFragment newInstance (JSONObject gameStructure, JSONObject progress, int gameIndex, int levelIndex){
        CategoryFragment fragment = new CategoryFragment();
        fragment.setGameStructure(gameStructure);
        fragment.setGameIndex(gameIndex);
        fragment.setLevelIndex(levelIndex);
        return fragment;
    }

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_category, container, false);
        ImageButton button = view.findViewById(R.id.categoryImageButton);
        // button.setImageIcon(?);
        // TODO: Crear íconos específicos para cada categoría (combinación de juego y nivel)
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager;
                LevelMenuDialogFragment dialog = new LevelMenuDialogFragment();
                dialog.setMessageText("Welcome to LearnToWrite!");
                // TODO: Cambiar los parámetros de LevelMenuDialogFragment para adaptarse a la nueva estructura del juego y del progreso
                // dialog.setLevels();
                // dialog.setScores();
                // TODO: Determinar si lo mejor es usar este otro Fragment Manager o cambiar LevelMenuDialogFragment a la librería support.v4
                fragmentManager = getActivity().getFragmentManager();
                dialog.show(fragmentManager, "LevelMenuDialogFragment");
            }
        });
        TextView text = view.findViewById(R.id.categoryName);
        try {
            String gameName = gameStructure.getJSONArray("games").getJSONObject(gameIndex).getString("name");
            String levelName = gameStructure.getJSONArray("levels").getJSONObject(levelIndex).getString("name");
            text.setText(gameName + " ( " + levelName + " )");
        } catch (JSONException ex){
            text.setText("Categoría ?");
        }
        return view;
    }


    public int getGameIndex() {
        return gameIndex;
    }

    public void setGameIndex(int gameIndex) {
        this.gameIndex = gameIndex;
    }

    public int getLevelIndex() {
        return levelIndex;
    }

    public void setLevelIndex(int levelIndex) {
        this.levelIndex = levelIndex;
    }

    public JSONObject getGameStructure() {
        return gameStructure;
    }

    public void setGameStructure(JSONObject gameStructure) {
        this.gameStructure = gameStructure;
    }

    public JSONObject getProgress() {
        return progress;
    }

    public void setProgress(JSONObject progress) {
        this.progress = progress;
    }
}
