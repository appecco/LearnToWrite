package com.appecco.learntowrite.dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.Progress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LevelMenuDialogFragment extends DialogFragment {
	
	int levels;
	JSONArray scores;
	int selectedLevel;

	LevelMenuDialogListener listener;

	private JSONObject gameStructure;
	private Progress progress;
	private int gameIndex;
	private int levelIndex;

	public static LevelMenuDialogFragment newInstance(JSONObject gameStructure, Progress progress, int gameIndex, int levelIndex){
		LevelMenuDialogFragment fragment = new LevelMenuDialogFragment();
		fragment.setGameStructure(gameStructure);
		fragment.setProgress(progress);
		fragment.setGameIndex(gameIndex);
		fragment.setLevelIndex(levelIndex);
		return fragment;
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (LevelMenuDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement LevelMenuDialogListener");
        }
    }
	
	@Override
	public void onStart() {
		super.onStart();
	    AlertDialog d = (AlertDialog) getDialog();
	    if (d != null) {
	        ((Button)d.getButton(Dialog.BUTTON_POSITIVE)).setEnabled(false);
	        ((Button)d.getButton(Dialog.BUTTON_NEGATIVE)).setEnabled(false);
	        ((Button)d.getButton(Dialog.BUTTON_POSITIVE)).setVisibility(View.INVISIBLE);
	        ((Button)d.getButton(Dialog.BUTTON_NEGATIVE)).setVisibility(View.INVISIBLE);
	    }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.level_menu_layout, container, false);

		TextView titleText = (TextView) view.findViewById(R.id.levelDialogText);
		try {
			String gameName = gameStructure.getJSONArray("games").getJSONObject(gameIndex).getString("name");
			String levelName = gameStructure.getJSONArray("levels").getJSONObject(levelIndex).getString("name");
			titleText.setText(gameName + " ( " + levelName + " )");
		} catch (JSONException ex){
			titleText.setText("Nivel?");
		}

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.levelButtonsLayout);
		JSONArray characters = null;
		try {
			characters = gameStructure.getJSONArray("games").getJSONObject(gameIndex).getJSONArray("characters");
		} catch (JSONException ex) {
			Toast.makeText(null, "Unable to load the elements in the current level",Toast.LENGTH_LONG).show();
			onCancelButtonPressed();
		}
		Button btn;

		for (int i = 0; i < Math.ceil((double)characters.length()/(double)6); i++) {
			LinearLayout layout_row = new LinearLayout(view.getContext());
			layout_row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

			for (int j = 0; j < 6; j++) {
				if ((i*6) + j < characters.length()){
					btn = new Button(getActivity());
					btn.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					try {
						btn.setAllCaps(false);
						btn.setText(characters.getString((i * 6) + j));
					} catch (JSONException ex){
						btn.setText("?");
					}
					try {
						String gameTag = gameStructure.getJSONArray("games").getJSONObject(gameIndex).getString("tag");
						String levelTag = gameStructure.getJSONArray("levels").getJSONObject(levelIndex).getString("tag");
						if (progress.findByTag(gameTag).findByTag(levelTag).getScores()[i*6+j] == -1){
							btn.setEnabled(false);
							btn.setAlpha(0.5f);
						}
					} catch (JSONException ex){
						Toast.makeText(getActivity(),"Unable to determine if the level is locked",Toast.LENGTH_LONG).show();
					}
					btn.setTag((i*6) + j);
					btn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View view) {
							int characterIndex;
							characterIndex = (int)((Button)view).getTag();
							listener.onLevelMenuDialogSelection(gameIndex, levelIndex, characterIndex);

							FragmentManager fragmentManager;
							fragmentManager = getFragmentManager();
							FragmentTransaction transaction = fragmentManager.beginTransaction();
							transaction.remove(LevelMenuDialogFragment.this);
							transaction.commit();

//							getDialog().dismiss();

						}

					});
					layout_row.addView(btn);
				}
			}
			linearLayout.addView(layout_row);
		}

		btn = (Button)view.findViewById(R.id.btnLevelMenuCancel);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				onCancelButtonPressed();
			}

		});

		return view;
	}


	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    return dialog;
	}

	void onCancelButtonPressed(){
		listener.onLevelMenuDialogCancel(LevelMenuDialogFragment.this);
		FragmentManager fragmentManager;
		fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.remove(LevelMenuDialogFragment.this);
		transaction.commit();

//			getDialog().dismiss();
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

	public interface LevelMenuDialogListener{
		void onLevelMenuDialogSelection(int gameIndex, int levelIndex, int characterIndex);
		//void onLevelMenuDialogSelection(DialogFragment dialog, int selectedLevel);
		void onLevelMenuDialogCancel(DialogFragment dialog);
	}

}
