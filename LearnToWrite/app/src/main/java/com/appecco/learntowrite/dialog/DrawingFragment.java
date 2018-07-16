package com.appecco.learntowrite.dialog;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;

import java.lang.reflect.Field;

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

    private boolean colorSelectorExpanded = false;
    private int colorSelectorWidth;

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

        ImageButton btnDrawingColor = (ImageButton)view.findViewById(R.id.btnDrawingColor);
        btnDrawingColor.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                LoadedResources.getInstance().playSound(R.raw.button_click);
                toggleExpandableColorSelector();
            }

        });

        final LinearLayout expandableColorSelector = (LinearLayout) view.findViewById(R.id.expandableColorSelector);

        expandableColorSelector.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                expandableColorSelector.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                colorSelectorWidth = expandableColorSelector.getWidth();
            }
        });


        setupColorSelector(view);

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

    private void toggleExpandableColorSelector() {
        final LinearLayout expandableColorSelector = (LinearLayout) getView().findViewById(R.id.expandableColorSelector);
        ValueAnimator animator;

        colorSelectorExpanded = !colorSelectorExpanded;

        animator = ValueAnimator.ofInt(colorSelectorExpanded?0:colorSelectorWidth,colorSelectorExpanded?colorSelectorWidth:0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = expandableColorSelector
                        .getLayoutParams();
                layoutParams.width = (int)valueAnimator.getAnimatedValue();
                expandableColorSelector.setLayoutParams(layoutParams);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // Para colocar el estado apropiado de visibilidad cuando la animación termina
                expandableColorSelector.setVisibility(colorSelectorExpanded?View.VISIBLE:View.INVISIBLE);
            }
        });

        expandableColorSelector.setVisibility(View.VISIBLE);

        animator.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        starView.removeCallbacks(starAnimationRunnable);
    }

    private void setupColorSelector(View view){
        final ImageButton btnDrawingColorSelector = (ImageButton)view.findViewById(R.id.btnDrawingColor);
        btnDrawingColorSelector.setImageDrawable(DrawableCompat.wrap(btnDrawingColorSelector.getDrawable()));
        String drawingColor = Settings.getDrawingColor(getContext());

        LinearLayout drawingColorSelector = view.findViewById(R.id.expandableColorSelector);
        ImageButton colorButton;
        LinearLayout.LayoutParams params;

        //Drawable.ConstantState circleConstantState = getResources().getDrawable(R.drawable.circle).getConstantState();

        Drawable circle = getResources().getDrawable(R.drawable.circle);
        Drawable mutableCircle;
        try {
            Field[] fields = Class.forName(getActivity().getPackageName()+".R$color").getDeclaredFields();
            for(Field field : fields) {

                final String colorName = field.getName();
                if (colorName.startsWith("drawingColor")) {
                    int color = getResources().getColor(getResources().getIdentifier(colorName, "color", getActivity().getPackageName()));
                    //mutableCircle = DrawableCompat.wrap(circle).mutate();
                    mutableCircle = DrawableCompat.wrap(circle.getConstantState().newDrawable().mutate());
                    mutableCircle.setBounds(0,0,46,46);
                    DrawableCompat.setTint(mutableCircle, color);
                    mutableCircle.invalidateSelf();

                    if (colorName.equals(drawingColor)) {
                        DrawableCompat.setTint(btnDrawingColorSelector.getDrawable(), color);
                        btnDrawingColorSelector.getDrawable().invalidateSelf();
                        viewDraw.setPenColor(color);
                    }

                    colorButton = new ImageButton(getContext());
                    colorButton.setId(View.generateViewId());
                    colorButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    colorButton.setImageDrawable(mutableCircle);
                    colorButton.setBackgroundResource(0);
                    colorButton.setPadding(15,0,0,0);

                    colorButton.setTag(color);
                    colorButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int color = (Integer) view.getTag();
                            viewDraw.setPenColor(color);
                            Settings.setDrawingColor(getContext(), colorName);
                            DrawableCompat.setTint(btnDrawingColorSelector.getDrawable(), color);
                            btnDrawingColorSelector.getDrawable().invalidateSelf();
                            toggleExpandableColorSelector();
                        }
                    });

                    params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0,0,0,0);
                    drawingColorSelector.addView(colorButton,params);
                }
            }
        } catch (ClassNotFoundException ex){
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
            final ImageView animatedStar = (ImageView)getView().findViewById(R.id.animated_star);
            animatedStar.setVisibility(View.VISIBLE);

            Animation starAnimation = LoadedResources.getInstance().getAnimation(R.anim.star_animation);
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
