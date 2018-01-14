package com.appecco.learntowrite.dialog;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private GameStructure gameStructure;
    private Progress progress;
    private int gameOrder;
    private int levelOrder;

    public static CategoryFragment newInstance (GameStructure gameStructure, Progress progress, int gameOrder, int levelOrder){
        CategoryFragment fragment = new CategoryFragment();
        fragment.setGameStructure(gameStructure);
        fragment.setProgress(progress);
        fragment.setGameOrder(gameOrder);
        fragment.setLevelOrder(levelOrder);
        return fragment;
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
                showLevelMenuDialog();
            }
        });
        String gameTag = gameStructure.findGameByOrder(gameOrder).getGameTag();
        String levelTag = gameStructure.findLevelByOrder(levelOrder).getLevelTag();
        if (progress.findGameByTag(gameTag).findLevelByTag(levelTag).getScores()[0] == -1){
            button.setEnabled(false);
            button.setAlpha(0.5f);
        }

        TextView gameNameText = view.findViewById(R.id.gameName);
        gameNameText.setText(gameStructure.findGameByOrder(gameOrder).getName());
        TextView levelNameText = view.findViewById(R.id.levelName);
        levelNameText.setText(String.format("( %s )", gameStructure.findLevelByOrder(levelOrder).getName()));
        return view;
    }

    public void showLevelMenuDialog(){
        FragmentManager fragmentManager;
        LevelMenuDialogFragment fragment = LevelMenuDialogFragment.newInstance(gameStructure,progress, gameOrder, levelOrder);
        fragmentManager = getActivity().getSupportFragmentManager();

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(getParentFragment());
        transaction.commit();


        transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, fragment).addToBackStack("LevelMenuFragment").commit();
    }

    public int getGameOrder() {
        return gameOrder;
    }

    public void setGameOrder(int gameOrder) {
        this.gameOrder = gameOrder;
    }

    public int getLevelOrder() {
        return levelOrder;
    }

    public void setLevelOrder(int levelOrder) {
        this.levelOrder = levelOrder;
    }

    public GameStructure getGameStructure() {
        return gameStructure;
    }

    public void setGameStructure(GameStructure gameStructure) {
        this.gameStructure = gameStructure;
    }

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
