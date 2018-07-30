package com.appecco.learntowrite.dialog;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;

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

    private Button button;

    private GameDialogsEventsListener gameDialogsEventsListener;
    private Runnable boxAnimationRunnable;

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

        final View view =  inflater.inflate(R.layout.fragment_category, container, false);

        String gameTag = gameStructure.findGameByOrder(gameOrder).getGameTag();
        String levelTag = gameStructure.findLevelByOrder(levelOrder).getLevelTag();

        button = (Button)view.findViewById(R.id.categoryImageButton);
        String imageResourceName = String.format("%s_%s",gameTag.toLowerCase(), levelTag.toLowerCase());
        Resources contextResources = getActivity().getResources();
        int imageResourceId = contextResources.getIdentifier(imageResourceName, "drawable", getActivity().getPackageName());
        if (imageResourceId != 0) {
            button.setBackground(getContext().getResources().getDrawable(imageResourceId));
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        button.getLayoutParams().width = displayMetrics.widthPixels/4;
        button.getLayoutParams().height = displayMetrics.widthPixels*5/24;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                gameDialogsEventsListener.onCategorySelected(gameOrder, levelOrder);
            }
        });
        if (progress.findGameByTag(gameTag).findLevelByTag(levelTag).getScores()[0] == -1){
            button.setEnabled(false);
            button.setAlpha(0.5f);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        boxAnimationRunnable = new Runnable(){

            @Override
            public void run() {
                try {
                    Animation boxAnimation = LoadedResources.getInstance().getAnimation(R.anim.box_animation);
                    View fView = getView();
                    if (boxAnimation != null && fView != null) {
                        Button categoryButton = fView.findViewById(R.id.categoryImageButton);
                        categoryButton.startAnimation(boxAnimation);
                    }
                } catch (Exception e) {
                    //Ignorar
                }
            }
        };
        button.postDelayed(boxAnimationRunnable,250);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        button.removeCallbacks(boxAnimationRunnable);
        button = null;
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
