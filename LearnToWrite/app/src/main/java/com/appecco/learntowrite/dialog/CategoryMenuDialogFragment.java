package com.appecco.learntowrite.dialog;

//Se ha usado android.support.v4.app.DialogFragment para permitir el uso de ViewPager y FragmentStatePageAdapter que requieren un Fragment Manager de esta librería
//import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.Progress;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryMenuInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryMenuDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryMenuDialogFragment extends DialogFragment {
    private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
    private static final String PROGRESS_PARAM = "progressParam";

    private JSONObject gameStructure;
    private Progress progress;

    private CategoryMenuInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameStructure Levels definition for the currently selected language.
     * @param progress Current progress in the currently selected language.
     * @return A new instance of fragment CategoryMenuDialogFragment.
     */
    public static CategoryMenuDialogFragment newInstance(JSONObject gameStructure, Progress progress) {
        CategoryMenuDialogFragment fragment = new CategoryMenuDialogFragment();
        Bundle args = new Bundle();
        args.putString(GAME_STRUCTURE_PARAM, gameStructure.toString());
        args.putSerializable(PROGRESS_PARAM, progress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                gameStructure = new JSONObject(getArguments().getString(GAME_STRUCTURE_PARAM));
                progress = (Progress)(getArguments().getSerializable(PROGRESS_PARAM));
            } catch (JSONException ex){
                Toast.makeText(getActivity().getApplicationContext(),"Unable to load the game gameStructure definition and current progress", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_menu_dialog, null);

        CategoriesPagerAdapter categoriesAdapter = new CategoriesPagerAdapter(getChildFragmentManager(), gameStructure, progress);
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.categoryPager);
        viewPager.setAdapter(categoriesAdapter);

        ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onCancelButtonPressed();
            }
        });

        return view;
    }

    public void onCancelButtonPressed() {
        if (mListener != null) {
            mListener.onCategoryDialogCancel();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CategoryMenuInteractionListener) {
            mListener = (CategoryMenuInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CategoryMenuInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface CategoryMenuInteractionListener {
        void onCategoryDialogCancel();
    }
}
