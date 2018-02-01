package com.appecco.learntowrite.dialog;

//Se ha usado android.support.v4.app.DialogFragment para permitir el uso de ViewPager y FragmentStatePageAdapter que requieren un Fragment Manager de esta librería
//import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.support.v4.app.DialogFragment;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.view.FixedSpeedScroller;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryMenuDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryMenuDialogFragment extends DialogFragment {

    private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
    private static final String PROGRESS_PARAM = "progressParam";

    private Progress progress;
    private GameStructure gameStructure;

    private GameDialogsEventsListener gameDialogsEventsListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameStructure Levels definition for the currently selected language.
     * @param progress Current progress in the currently selected language.
     * @return A new instance of fragment CategoryMenuDialogFragment.
     */
    public static CategoryMenuDialogFragment newInstance(GameStructure gameStructure, Progress progress) {
        CategoryMenuDialogFragment fragment = new CategoryMenuDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(GAME_STRUCTURE_PARAM, gameStructure);
        args.putSerializable(PROGRESS_PARAM, progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameStructure = (GameStructure)(getArguments().getSerializable(GAME_STRUCTURE_PARAM));
            progress = (Progress)(getArguments().getSerializable(PROGRESS_PARAM));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_menu_dialog, null);

        CategoriesPagerAdapter categoriesAdapter = new CategoriesPagerAdapter(getChildFragmentManager(), gameStructure, progress);
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.categoryPager);
        viewPager.setAdapter(categoriesAdapter);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(viewPager.getContext(), new DecelerateInterpolator());
            scroller.setFixedDuration(2000);
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }

        ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if (gameDialogsEventsListener != null) {
                    gameDialogsEventsListener.onCategoryDialogCancelPressed();
                }
            }
        });

        return view;
    }

    public void loadCategoryButtons(){
        ViewPager viewPager = (ViewPager)getView().findViewById(R.id.categoryPager);
        PagerAdapter pagerAdapter = viewPager.getAdapter();
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GameDialogsEventsListener) {
            gameDialogsEventsListener = (GameDialogsEventsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement CategoryMenuDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewPager viewPager = (ViewPager)getView().findViewById(R.id.categoryPager);
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewPager viewPager = (ViewPager)getView().findViewById(R.id.categoryPager);
                viewPager.setCurrentItem(progress.getCurrentCategoryIndex(),true);

            }
        },1250);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        gameDialogsEventsListener = null;
    }

}
