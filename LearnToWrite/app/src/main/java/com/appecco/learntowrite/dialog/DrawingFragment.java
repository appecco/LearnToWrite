package com.appecco.learntowrite.dialog;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DrawingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DrawingFragment extends Fragment {
    private static final String CHARACTER_PARAM = "characterParam";
    private static final String SHOW_HINTS_PARAM = "showHintsParam";
    private static final String CONTOUR_TYPE_PARAM = "contourTypeParam";
    private static final String BEGINNING_MARK_PARAM = "beginningMarkParam";
    private static final String ENDING_MARK_PARAM = "endingMarkParam";

    private char character;
    private boolean showHints;
    private String contourType;
    private boolean beginningMark;
    private boolean endingMark;

    private Boolean[] score;

    private DrawingView viewDraw;
    private ImageView starView;

    GameDialogsEventsListener gameDialogsEventsListener;

    public DrawingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param character The character to be drawn by the player.
     * @param showHints Should the hint on how to draw a character be shown?
     * @param contourType Level of transparency in the contour shown to guide the player
     * @param beginningMark Should a mark indicate where to start drawing the character?
     * @param endingMark Should a mark indicate where to end drawing the character?
     * @return A new instance of fragment DrawingFragment.
     */
    public static DrawingFragment newInstance(char character, boolean showHints, String contourType, boolean beginningMark, boolean endingMark) {
        DrawingFragment fragment = new DrawingFragment();
        Bundle args = new Bundle();
        args.putChar(CHARACTER_PARAM,character);
        args.putBoolean(SHOW_HINTS_PARAM,showHints);
        args.putString(CONTOUR_TYPE_PARAM,contourType);
        args.putBoolean(BEGINNING_MARK_PARAM,beginningMark);
        args.putBoolean(ENDING_MARK_PARAM,endingMark);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            character = getArguments().getChar(CHARACTER_PARAM);
            showHints = getArguments().getBoolean(SHOW_HINTS_PARAM);
            contourType = getArguments().getString(CONTOUR_TYPE_PARAM);
            beginningMark = getArguments().getBoolean(BEGINNING_MARK_PARAM);
            endingMark = getArguments().getBoolean(ENDING_MARK_PARAM);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            gameDialogsEventsListener = (GameDialogsEventsListener) context;
        } catch (ClassCastException e) {
            throw new RuntimeException(context.toString()+ " must implement LevelDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        viewDraw = (DrawingView)view.findViewById(R.id.viewDraw);

        ImageButton btnRetry = (ImageButton)view.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                if (gameDialogsEventsListener != null){
                    gameDialogsEventsListener.onRetryCharacterSelected();
                }
                //viewDraw.reset();
                //EditText txtGesture = (EditText)findViewById(R.id.txtGesture);
                //txtGesture.setText("");
            }

        });

        ImageButton btnHint = (ImageButton)view.findViewById(R.id.btnHint);
        btnHint.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                viewDraw.hint();

            }

        });

        ImageButton btnRed = (ImageButton)view.findViewById(R.id.btnRed);
        btnRed.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                Settings.setDrawingColor(getContext(), Settings.DrawingColor.COLOR_RED);
                setupDrawingColor(view);
            }

        });

        ImageButton btnBlue = (ImageButton)view.findViewById(R.id.btnBlue);
        btnBlue.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                Settings.setDrawingColor(getContext(), Settings.DrawingColor.COLOR_BLUE);
                setupDrawingColor(view);
            }

        });

        ImageButton btnGreen = (ImageButton)view.findViewById(R.id.btnGreen);
        btnGreen.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                Settings.setDrawingColor(getContext(), Settings.DrawingColor.COLOR_GREEN);
                setupDrawingColor(view);
            }

        });

        setupDrawingColor(view);

        starView = (ImageView)view.findViewById(R.id.animated_star);

//        Funcionalidad usada para preparar los hints, NO BORRAR!!!

//        Button btnNext = (Button)view.findViewById(R.id.btnNext);
//        btnNext.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                if (character == 'z'){
//                    character = 'ñ';
//                }
//                else if (character == 'Z'){
//                    character = 'Ñ';
//                }
//                else {
//                    character++;
//                }
//                startChallenge();
//            }
//        });
//
//        Button btnSave = (Button)view.findViewById(R.id.btnSave);
//        btnSave.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                viewDraw.save();
//
//            }
//
//        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        starView.removeCallbacks(starAnimationRunnable);
    }

    private void setupDrawingColor(View view){
        ImageButton btnRed = (ImageButton)view.findViewById(R.id.btnRed);
        ImageButton btnBlue = (ImageButton)view.findViewById(R.id.btnBlue);
        ImageButton btnGreen = (ImageButton)view.findViewById(R.id.btnGreen);

        btnRed.setImageResource(R.drawable.redbutton);
        btnBlue.setImageResource(R.drawable.bluebutton);
        btnGreen.setImageResource(R.drawable.greenbutton);

        Settings.DrawingColor drawingColor = Settings.getDrawingColor(getContext());

        switch (drawingColor){
            case COLOR_RED:
                viewDraw.setPenColor(Color.argb(0xFF,0xFD,0x00,0x06));
                viewDraw.setPenColor(Color.RED);
                btnRed.setImageResource(R.drawable.red_button_selected);
                break;
            case COLOR_BLUE:
                viewDraw.setPenColor(Color.argb(0xFF,0x17,0x29,0xB0));
                btnBlue.setImageResource(R.drawable.blue_button_selected);
                break;
            case COLOR_GREEN:
                viewDraw.setPenColor(Color.argb(0xFF,0x0A,0xCF,0x00));
                btnGreen.setImageResource(R.drawable.green_button_selected);
        }
    }

    public boolean isShowHints() {
        return showHints;
    }

    public void setShowHints(boolean showHints) {
        this.showHints = showHints;
    }

    public Boolean[] getScore() {
        return score;
    }

    public void setScore(Boolean[] score) {
        this.score = score;
    }

    public void startStarAnimation(){
        starView.post(starAnimationRunnable);
    }

    public void startChallenge(){
        viewDraw.setCharacter(character);
        viewDraw.setShowHints(showHints);
        viewDraw.setContourType(contourType);
        viewDraw.setShowBeginningMark(beginningMark);
        viewDraw.setShowEndingMark(endingMark);
        viewDraw.setScore(score);

        viewDraw.startChallenge();
    }

    private final Runnable starAnimationRunnable = new Runnable(){

        @Override
        public void run() {
            Animation starAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.star_animation);
            final ImageView animatedStar = (ImageView)getView().findViewById(R.id.animated_star);
            animatedStar.setVisibility(View.VISIBLE);
            starAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    animatedStar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animatedStar.startAnimation(starAnimation);
        }
    };

}
