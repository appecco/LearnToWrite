package com.appecco.learntowrite.dialog;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
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

    private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
    private static final String PROGRESS_PARAM = "progressParam";
    private static final String GAME_ORDER_PARAM = "gameOrderParam";
    private static final String LEVEL_ORDER_PARAM = "levelOrderParam";

    private GameStructure gameStructure;
    private Progress progress;
    private int gameOrder;
    private int levelOrder;

    private GameDialogsEventsListener gameDialogsEventsListener;

    public static CategoryFragment newInstance (GameStructure gameStructure, Progress progress, int gameOrder, int levelOrder){
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putSerializable(GAME_STRUCTURE_PARAM, gameStructure);
        args.putSerializable(PROGRESS_PARAM, progress);
        args.putInt(GAME_ORDER_PARAM, gameOrder);
        args.putInt(LEVEL_ORDER_PARAM, levelOrder);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameStructure = (GameStructure)(getArguments().getSerializable(GAME_STRUCTURE_PARAM));
            progress = (Progress)(getArguments().getSerializable(PROGRESS_PARAM));
            gameOrder = getArguments().getInt(GAME_ORDER_PARAM);
            levelOrder = getArguments().getInt(LEVEL_ORDER_PARAM);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            gameDialogsEventsListener = (GameDialogsEventsListener) context;
        } catch (ClassCastException ex){
            throw new RuntimeException(context.toString() + " must implement CategoryMenuDialogListener");
        }
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
                gameDialogsEventsListener.onCategorySelected(gameOrder, levelOrder);
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
