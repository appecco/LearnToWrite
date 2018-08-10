package com.appecco.learntowrite.dialog;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.utils.LoadedResources;
import com.plattysoft.leonids.ParticleSystem;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.logging.Logger;

public class CharacterFinishedDialogFragment extends DialogFragment {

	private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
	private static final String PROGRESS_PARAM = "progressParam";
	private static final String GAME_ORDER_PARAM = "gameOrderParam";
	private static final String LEVEL_ORDER_PARAM = "levelOrderParam";
	private static final String CHARACTER_INDEX_PARAM = "characterIndexParam";
	private static final String SCORE_PARAM = "scoreParam";
	private static final String LEVEL_FINISHED_PARAM = "levelFinishedParam";
	private static final long PARTICLES_TIME_TO_LIVE = 2000;
	private static final int MAX_PARTICLES = 40;

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

		final ImageView imageScore = (ImageView)view.findViewById(R.id.imageScore);
		imageScore.setImageResource(scoreImages[score]);

		final ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				LoadedResources.getInstance().playSound(R.raw.button_click);
				if (gameDialogsEventsListener != null){
					gameDialogsEventsListener.onFinishedCharacterDialogCancelPressed();
				}
			}
		});

		final ImageButton retryLevelButton = (ImageButton)view.findViewById(R.id.retryLevelButton);
		retryLevelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				LoadedResources.getInstance().playSound(R.raw.button_click);
				if (gameDialogsEventsListener != null){
					gameDialogsEventsListener.onRetryCharacterSelected();
				}
			}
		});

		final ImageButton nextLevelButton = (ImageButton)view.findViewById(R.id.nextLevelButton);
		nextLevelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View view) {
				LoadedResources.getInstance().playSound(R.raw.button_click);
				if (gameDialogsEventsListener != null){
					if (levelFinished) {
						gameDialogsEventsListener.onNextLevelSelected();
					} else {
						gameDialogsEventsListener.onNextCharacterSelected();
					}
				}
			}
		});
		if (score == 0){
			nextLevelButton.setAlpha(0.5f);
			nextLevelButton.setEnabled(false);
		}

		if (score > 0) {
			LoadedResources.getInstance().playSound(R.raw.children_cheer_short);
			imageScore.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (getActivity() != null) {
						try {
							new ParticleSystem(getActivity(), MAX_PARTICLES, R.drawable.firework_particle, PARTICLES_TIME_TO_LIVE)
									.setSpeedModuleAndAngleRange(0.1f, 0.2f, 225, 315)
									.setStartTime(10)
									.oneShot(cancelButton, MAX_PARTICLES, new DecelerateInterpolator());
							new ParticleSystem(getActivity(), MAX_PARTICLES, R.drawable.firework_particle, PARTICLES_TIME_TO_LIVE)
									.setSpeedModuleAndAngleRange(0.1f, 0.2f, 225, 315)
									.setStartTime(10)
									.oneShot(retryLevelButton, MAX_PARTICLES, new DecelerateInterpolator());
							new ParticleSystem(getActivity(), MAX_PARTICLES, R.drawable.firework_particle, PARTICLES_TIME_TO_LIVE)
									.setSpeedModuleAndAngleRange(0.1f, 0.2f, 225, 315)
									.setStartTime(10)
									.oneShot(nextLevelButton, MAX_PARTICLES, new DecelerateInterpolator());
						} catch (NullPointerException ex){
							Logger.getLogger(CharacterFinishedDialogFragment.class.getName()).warning("No activity available for fireworks animation. Perhaps the user closed the app");
						}
					}
				}
			}, 25);
		}
		return view;
	}

	public void setMessageText(String messageText){
		this.messageText = messageText;
	}

}
