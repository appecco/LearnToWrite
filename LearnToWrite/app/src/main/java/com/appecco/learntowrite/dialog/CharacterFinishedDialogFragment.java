package com.appecco.learntowrite.dialog;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CharacterFinishedDialogFragment extends DialogFragment {

	private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
	private static final String PROGRESS_PARAM = "progressParam";
	private static final String GAME_ORDER_PARAM = "gameOrderParam";
	private static final String LEVEL_ORDER_PARAM = "levelOrderParam";
	private static final String CHARACTER_INDEX_PARAM = "characterIndexParam";
	private static final String SCORE_PARAM = "scoreParam";
	private static final String LEVEL_FINISHED_PARAM = "levelFinishedParam";

	Activity activity;
	String messageText;

	GameStructure gameStructure;
	Progress progress;
	int gameOrder;
	int levelOrder;
	int characterIndex;
	int score;
	boolean levelFinished;

	GameDialogsEventsListener gameDialogsEventsListener;

	public static CharacterFinishedDialogFragment newInstance(GameStructure gameStructure, Progress progress,
															  int gameOrder, int levelOrder,
															  int characterIndex, int score,
															  boolean levelFinished){
		CharacterFinishedDialogFragment fragment = new CharacterFinishedDialogFragment();

		Bundle args = new Bundle();
		args.putSerializable(GAME_STRUCTURE_PARAM, gameStructure);
		args.putSerializable(PROGRESS_PARAM, progress);
		args.putInt(GAME_ORDER_PARAM, gameOrder);
		args.putInt(LEVEL_ORDER_PARAM, levelOrder);
		args.putInt(CHARACTER_INDEX_PARAM, characterIndex);
		args.putInt(SCORE_PARAM, score);
		args.putBoolean(LEVEL_FINISHED_PARAM, levelFinished);
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
			characterIndex = getArguments().getInt(CHARACTER_INDEX_PARAM);
			score = getArguments().getInt(SCORE_PARAM);
			levelFinished = getArguments().getBoolean(LEVEL_FINISHED_PARAM);
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
		View view = inflater.inflate(R.layout.level_layout, container, false);

		int scoreImages[] = {R.drawable.stars0,R.drawable.stars1,R.drawable.stars2,R.drawable.stars3};

		ImageView imageScore = (ImageView)view.findViewById(R.id.imageScore);
		imageScore.setImageResource(scoreImages[score]);

		ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				if (gameDialogsEventsListener != null){
					gameDialogsEventsListener.onFinishedCharacterDialogCancelPressed();
				}
			}
		});

		ImageButton retryLevelButton = (ImageButton)view.findViewById(R.id.retryLevelButton);
		retryLevelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				dismissFragment();
				if (gameDialogsEventsListener != null){
					gameDialogsEventsListener.onRetryCharacterSelected();
				}
			}
		});

		ImageButton nextLevelButton = (ImageButton)view.findViewById(R.id.nextLevelButton);
		nextLevelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				if (gameDialogsEventsListener != null){
					gameDialogsEventsListener.onNextCharacterSelected();
				}
			}
		});
		if (score == 0 || levelFinished){
			nextLevelButton.setAlpha(0.5f);
			nextLevelButton.setEnabled(false);
		}

		TextView messageText = (TextView)view.findViewById(R.id.levelDialogText);
		if (levelFinished){
			// TODO: Internacionalizar y agregar el nombre del nivel
			messageText.setText(R.string.level_finished_congratulations);
		} else {
			if (score == 0) {
				messageText.setText(
						String.format("Your score on %s is %d. You should try this again!",
								gameStructure.findGameByOrder(gameOrder).getCharacters()[characterIndex],
								score));
			} else if (score > 0 && score < 3) {
				messageText.setText(
						String.format("Your score on %s is %d. That's good but can be better!",
								gameStructure.findGameByOrder(gameOrder).getCharacters()[characterIndex],
								score));
			} else if (score == 3){
				messageText.setText(
						String.format("Your have passed %s with flying colors. Great Job!",
								gameStructure.findGameByOrder(gameOrder).getCharacters()[characterIndex]));
			}
		}

		return view;
	}

	private void dismissFragment(){
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.remove(this);
		transaction.commit();
	}

	public void setMessageText(String messageText){
		this.messageText = messageText;
	}

}
