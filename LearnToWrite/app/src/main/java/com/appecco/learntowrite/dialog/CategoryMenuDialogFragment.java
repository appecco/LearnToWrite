package com.appecco.learntowrite.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
//Se ha usado android.support.v4.app.DialogFragment para permitir el uso de ViewPager y FragmentStatePageAdapter que requieren un Fragment Manager de esta librería
//import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

import com.appecco.learntowrite.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CategoryMenuDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CategoryMenuDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryMenuDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
    private static final String PROGRESS_PARAM = "progressParam";

    // TODO: Rename and change types of parameters
    private JSONObject gameStructure;
    private JSONObject progress;

    private OnFragmentInteractionListener mListener;

    public CategoryMenuDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param levels Levels definition for the currently selected language.
     * @param progress Current progress in the currently selected language.
     * @return A new instance of fragment CategoryMenuDialogFragment.
     */
    public static CategoryMenuDialogFragment newInstance(JSONObject levels, JSONObject progress) {
        CategoryMenuDialogFragment fragment = new CategoryMenuDialogFragment();
        Bundle args = new Bundle();
        args.putString(GAME_STRUCTURE_PARAM, levels.toString());
        args.putString(PROGRESS_PARAM, progress.toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                gameStructure = new JSONObject(getArguments().getString(GAME_STRUCTURE_PARAM));
                progress = new JSONObject(getArguments().getString(PROGRESS_PARAM));
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

        return view;
    }

/*    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_category_menu_dialog, null);
        builder.setView(view);

        CategoriesPagerAdapter categoriesAdapter = new CategoriesPagerAdapter(getChildFragmentManager(), gameStructure, progress);
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.categoryPager);
        viewPager.setAdapter(categoriesAdapter);

        return builder.create();
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
