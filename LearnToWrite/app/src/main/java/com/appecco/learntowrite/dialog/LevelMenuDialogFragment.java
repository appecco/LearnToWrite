package com.appecco.learntowrite.dialog;

import org.json.JSONArray;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LevelMenuDialogFragment extends DialogFragment {

	private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
	private static final String PROGRESS_PARAM = "progressParam";
	private static final String GAME_ORDER_PARAM = "gameOrderParam";
	private static final String LEVEL_ORDER_PARAM = "levelOrderParam";

	LevelMenuDialogListener levelMenuDialogListener;

	private GameStructure gameStructure;
	private Progress progress;
	private int gameOrder;
	private int levelOrder;

	public static LevelMenuDialogFragment newInstance(GameStructure gameStructure, Progress progress, int gameOrder, int levelOrder){
		LevelMenuDialogFragment fragment = new LevelMenuDialogFragment();

		Bundle args = new Bundle();
		args.putSerializable(GAME_STRUCTURE_PARAM, gameStructure);
		args.putSerializable(PROGRESS_PARAM, progress);
		args.putInt(GAME_ORDER_PARAM, gameOrder);
		args.putInt(LEVEL_ORDER_PARAM, levelOrder);
		fragment.setArguments(args);

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			gameStructure = (GameStructure)(getArguments().getSerializable(GAME_STRUCTURE_PARAM));
			progress = (Progress)(getArguments().getSerializable(PROGRESS_PARAM));
			gameOrder = getArguments().getInt(GAME_ORDER_PARAM);
			levelOrder = getArguments().getInt(LEVEL_ORDER_PARAM);
		}
	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            levelMenuDialogListener = (LevelMenuDialogListener) activity;
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
		String gameName = gameStructure.findGameByOrder(gameOrder).getName();
		String levelName = gameStructure.findLevelByOrder(levelOrder).getName();
		titleText.setText(String.format("%s ( %s )",gameName,levelName));

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.levelButtonsLayout);
		String[] characters = null;
		characters = gameStructure.findGameByOrder(gameOrder).getCharacters();

		Button btn;

		for (int i = 0; i < Math.ceil((double)characters.length/(double)6); i++) {
			LinearLayout layout_row = new LinearLayout(view.getContext());
			layout_row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

			for (int j = 0; j < 6; j++) {
				if ((i*6) + j < characters.length){
					btn = new Button(getActivity());
					btn.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					btn.setAllCaps(false);
					btn.setText(characters[(i * 6) + j]);

					String gameTag = gameStructure.findGameByOrder(gameOrder).getGameTag();
					String levelTag = gameStructure.findLevelByOrder(levelOrder).getLevelTag();
					if (progress.findGameByTag(gameTag).findLevelByTag(levelTag).getScores()[i*6+j] == -1){
						btn.setEnabled(false);
						btn.setAlpha(0.5f);
					}

					btn.setTag((i*6) + j);
					btn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View view) {
							int characterIndex;
							characterIndex = (int)((Button)view).getTag();

							levelMenuDialogListener.onLevelMenuDialogSelection(gameOrder, levelOrder, characterIndex);

							FragmentManager fragmentManager;
							fragmentManager = getFragmentManager();
							FragmentTransaction transaction = fragmentManager.beginTransaction();
							transaction.remove(LevelMenuDialogFragment.this);
							transaction.commit();

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

	private void dismissFragment(){
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.remove(this);
		transaction.commit();
	}

	void onCancelButtonPressed(){
		dismissFragment();
		if (levelMenuDialogListener != null) {
			levelMenuDialogListener.onLevelMenuDialogCancel(LevelMenuDialogFragment.this);
		}
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

	public interface LevelMenuDialogListener{
		void onLevelMenuDialogSelection(int gameIndex, int levelIndex, int characterIndex);
		void onLevelMenuDialogCancel(DialogFragment dialog);
	}

}
