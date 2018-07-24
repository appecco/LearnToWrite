package com.appecco.learntowrite.dialog;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.service.BackgroundMusicServiceControl;
import com.appecco.utils.LoadedResources;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.Map;

public class CharacterMenuDialogFragment extends DialogFragment {

	private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
	private static final String PROGRESS_PARAM = "progressParam";
	private static final String GAME_ORDER_PARAM = "gameOrderParam";
	private static final String LEVEL_ORDER_PARAM = "levelOrderParam";

	private static final int BUTTONS_PER_ROW = 7;

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
	        (d.getButton(Dialog.BUTTON_POSITIVE)).setEnabled(false);
	        (d.getButton(Dialog.BUTTON_NEGATIVE)).setEnabled(false);
	        (d.getButton(Dialog.BUTTON_POSITIVE)).setVisibility(View.INVISIBLE);
	        (d.getButton(Dialog.BUTTON_NEGATIVE)).setVisibility(View.INVISIBLE);
	    }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.level_menu_layout, container, false);

		loadCharacterButtons(view);

		ImageButton cancelButton = view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LoadedResources.getInstance().playSound(R.raw.button_click);
				if (gameDialogsEventsListener != null) {
					gameDialogsEventsListener.onCharacterDialogCancelPressed();
				}
			}

		});

		return view;
	}

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            BackgroundMusicServiceControl.startBackgroundMusicService(getContext(), R.raw.backgroud_sound, 50, 50);
        }
    }

	public void loadCharacterButtons(){
		final View vwView = getView();
		if (vwView != null){
			loadCharacterButtons(vwView);
		}
	}

	private void loadCharacterButtons(View view) {
		String gameTag = gameStructure.findGameByOrder(gameOrder).getGameTag();
		String levelTag = gameStructure.findLevelByOrder(levelOrder).getLevelTag();
		String[] characters = gameStructure.findGameByOrder(gameOrder).getCharacters();
		int[] scores = progress.findGameByTag(gameTag).findLevelByTag(levelTag).getScores();

		LinearLayout linearLayout = view.findViewById(R.id.levelButtonsLayout);
		linearLayout.removeAllViewsInLayout();

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int buttonWidth = displayMetrics.widthPixels / (BUTTONS_PER_ROW+1);
		int buttonHeight = (displayMetrics.heightPixels - 85) / 6;

		Button button;
		Map<Integer, Drawable> levelStars = new ArrayMap<>();
		levelStars.put(-1,resize(getResources().getDrawable(R.drawable.microstars0),buttonWidth*8/10, buttonHeight*3/10));
		levelStars.put(0,resize(getResources().getDrawable(R.drawable.microstars0),buttonWidth*8/10, buttonHeight*3/10));
		levelStars.put(1,resize(getResources().getDrawable(R.drawable.microstars1),buttonWidth*8/10, buttonHeight*3/10));
		levelStars.put(2,resize(getResources().getDrawable(R.drawable.microstars2),buttonWidth*8/10, buttonHeight*3/10));
		levelStars.put(3,resize(getResources().getDrawable(R.drawable.microstars3),buttonWidth*8/10, buttonHeight*3/10));

		for (int i = 0; i < Math.ceil((double)characters.length/(double)BUTTONS_PER_ROW); i++) {
			LinearLayout buttonRow = new LinearLayout(view.getContext());
			buttonRow.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));
			buttonRow.setGravity(Gravity.CENTER);

			for (int j = 0; j < BUTTONS_PER_ROW; j++) {
				if ((i * BUTTONS_PER_ROW) + j < characters.length){
					button = new Button(getActivity());
					button.setLayoutParams(new android.widget.LinearLayout.LayoutParams(buttonWidth, buttonHeight));
					button.setAllCaps(false);
					button.setBackground(getResources().getDrawable(R.drawable.level_button));
					button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, levelStars.get(scores[(i * BUTTONS_PER_ROW) + j]) );
					button.setText(characters[(i * BUTTONS_PER_ROW) + j]);
					button.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
					button.setTextColor(Color.YELLOW);
					button.setPadding(0,buttonHeight/11,0,buttonHeight/10);
					button.setTextSize(14 + buttonHeight / 32);
					if (scores[(i * BUTTONS_PER_ROW) + j] == -1){
						button.setEnabled(false);
						button.setAlpha(0.5f);
					}

					button.setTag((i * BUTTONS_PER_ROW) + j);
					button.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View view) {
							int characterIndex;
							characterIndex = (int)(view).getTag();

							LoadedResources.getInstance().playSound(R.raw.button_click);

							gameDialogsEventsListener.onCharacterSelected(gameOrder, levelOrder, characterIndex);

						}

					});
					buttonRow.addView(button);
				}
			}
			linearLayout.addView(buttonRow);
		}
	}

	private Drawable resize(Drawable image, int width, int height) {
		Bitmap b = ((BitmapDrawable)image).getBitmap();
		Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, height, true);
		return new BitmapDrawable(getResources(), bitmapResized);
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
