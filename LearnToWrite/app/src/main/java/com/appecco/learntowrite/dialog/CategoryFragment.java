package com.appecco.learntowrite.dialog;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.Progress;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private JSONObject gameStructure;
    private Progress progress;
    private int gameIndex;
    private int levelIndex;

    public static CategoryFragment newInstance (JSONObject gameStructure, Progress progress, int gameIndex, int levelIndex){
        CategoryFragment fragment = new CategoryFragment();
        fragment.setGameStructure(gameStructure);
        fragment.setProgress(progress);
        fragment.setGameIndex(gameIndex);
        fragment.setLevelIndex(levelIndex);
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
        try {
            String gameTag = gameStructure.getJSONArray("games").getJSONObject(gameIndex).getString("tag");
            String levelTag = gameStructure.getJSONArray("levels").getJSONObject(levelIndex).getString("tag");
            if (progress.findByTag(gameTag).findByTag(levelTag).getScores()[0] == -1){
                button.setEnabled(false);
                button.setAlpha(0.5f);
            }
        } catch (JSONException ex){
            Toast.makeText(getContext(),"Unable to determine if the level is locked",Toast.LENGTH_LONG).show();
        }

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

    public void showLevelMenuDialog(){
        FragmentManager fragmentManager;
        LevelMenuDialogFragment fragment = LevelMenuDialogFragment.newInstance(gameStructure,progress,gameIndex,levelIndex);
        fragmentManager = getActivity().getFragmentManager();
        //TODO: Esto está 'feo', solo funciona si el fragmento está dentro de otro fragmento principal en la caja de diálogo. Buscar una mejor forma, tal vez a través implementando un listener
        ((DialogFragment)getParentFragment()).getDialog().dismiss();

        // TODO: Estandarizar el uso de Activity, Dialog y Fragment. Este método de navegación entre fragmentos me parece más flexible que usando Dialog
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, fragment).addToBackStack("LevelMenuFragment").commit();
        //fragment.show(fragmentManager, "LevelMenuDialogFragment");
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

    public Progress getProgress() {
        return progress;
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
