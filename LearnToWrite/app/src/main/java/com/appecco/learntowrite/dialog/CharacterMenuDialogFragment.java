package com.appecco.learntowrite.dialog;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

public class CharacterMenuDialogFragment extends DialogFragment {

	private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
	private static final String PROGRESS_PARAM = "progressParam";
	private static final String GAME_ORDER_PARAM = "gameOrderParam";
	private static final String LEVEL_ORDER_PARAM = "levelOrderParam";

	GameDialogsEventsListener gameDialogsEventsListener;

	private GameStructure gameStructure;
	private Progress progress;
	private int gameOrder;
	private int levelOrder;

	public static CharacterMenuDialogFragment newInstance(GameStructure gameStructure, Progress progress, int gameOrder, int levelOrder){
		CharacterMenuDialogFragment fragment = new CharacterMenuDialogFragment();

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
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            gameDialogsEventsListener = (GameDialogsEventsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+ " must implement LevelMenuDialogListener");
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

		loadCharacterButtons(view);

		ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if (gameDialogsEventsListener != null) {
					gameDialogsEventsListener.onCharacterDialogCancelPressed();
				}
			}

		});

		return view;
	}

	public void loadCharacterButtons(){
		loadCharacterButtons(getView());
	}

	private void loadCharacterButtons(View view) {
		String gameTag = gameStructure.findGameByOrder(gameOrder).getGameTag();
		String levelTag = gameStructure.findLevelByOrder(levelOrder).getLevelTag();
		String[] characters = gameStructure.findGameByOrder(gameOrder).getCharacters();
		int[] scores = progress.findGameByTag(gameTag).findLevelByTag(levelTag).getScores();

		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.levelButtonsLayout);
		linearLayout.removeAllViewsInLayout();

		Button btn;
		Map<Integer, Drawable> levelStars = new ArrayMap<>();
		levelStars.put(-1,getResources().getDrawable(R.drawable.microstars0));
		levelStars.put(0,getResources().getDrawable(R.drawable.microstars0));
		levelStars.put(1,getResources().getDrawable(R.drawable.microstars1));
		levelStars.put(2,getResources().getDrawable(R.drawable.microstars2));
		levelStars.put(3,getResources().getDrawable(R.drawable.microstars3));

		for (int i = 0; i < Math.ceil((double)characters.length/(double)6); i++) {
			LinearLayout layout_row = new LinearLayout(view.getContext());
			layout_row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
			layout_row.setGravity(Gravity.CENTER);

			for (int j = 0; j < 6; j++) {
				if ((i*6) + j < characters.length){
					btn = new Button(getActivity());
					btn.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
					btn.setAllCaps(false);
					btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, levelStars.get(scores[(i*6)+j]) );
					btn.setText(characters[(i * 6) + j]);
					if (scores[i*6+j] == -1){
						btn.setEnabled(false);
						btn.setAlpha(0.5f);
					}

					btn.setTag((i*6) + j);
					btn.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View view) {
							int characterIndex;
							characterIndex = (int)((Button)view).getTag();

							gameDialogsEventsListener.onCharacterSelected(gameOrder, levelOrder, characterIndex);

						}

					});
					layout_row.addView(btn);
				}
			}
			linearLayout.addView(layout_row);
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    return dialog;
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

}
