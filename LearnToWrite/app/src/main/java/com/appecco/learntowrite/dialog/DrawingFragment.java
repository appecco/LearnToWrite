package com.appecco.learntowrite.dialog;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.view.DrawingView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_drawing, container, false);

        viewDraw = (DrawingView)view.findViewById(R.id.viewDraw);

        ImageButton btnRetry = (ImageButton)view.findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.reset();
                //EditText txtGesture = (EditText)findViewById(R.id.txtGesture);
                //txtGesture.setText("");
            }

        });

        ImageButton btnHint = (ImageButton)view.findViewById(R.id.btnHint);
        btnHint.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.hint();

            }

        });

        ImageButton btnRed = (ImageButton)view.findViewById(R.id.btnRed);
        btnRed.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.setPenColor(Color.RED);

            }

        });

        ImageButton btnBlue = (ImageButton)view.findViewById(R.id.btnBlue);
        btnBlue.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View arg0) {
                viewDraw.setPenColor(Color.BLUE);

            }

        });

        ImageButton btnGreen = (ImageButton)view.findViewById(R.id.btnGreen);
        btnGreen.setOnClickListener(new View.OnClickListener(){

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

        return view;
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

    public void startChallenge(){
        viewDraw.setCharacter(character);
        viewDraw.setShowHints(showHints);
        viewDraw.setContourType(contourType);
        viewDraw.setShowBeginningMark(beginningMark);
        viewDraw.setShowEndingMark(endingMark);
        viewDraw.setScore(score);

        viewDraw.startChallenge();
    }
}
