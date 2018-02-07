package com.appecco.learntowrite.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.appecco.learntowrite.R;
import com.appecco.learntowrite.model.GameStructure;
import com.appecco.learntowrite.model.Progress;
import com.appecco.learntowrite.view.DrawingView;
import com.appecco.utils.LoadedResources;
import com.appecco.utils.Settings;

public class CharacterIntroDialogFragment extends DialogFragment {

	private static final String GAME_STRUCTURE_PARAM = "gameStructureParam";
	private static final String PROGRESS_PARAM = "progressParam";
	private static final String GAME_ORDER_PARAM = "gameOrderParam";
	private static final String LEVEL_ORDER_PARAM = "levelOrderParam";
	private static final String CHARACTER_INDEX_PARAM = "characterIndexParam";

	GameDialogsEventsListener gameDialogsEventsListener;
	boolean fragmentStarting = true;

	private GameStructure gameStructure;
	private Progress progress;
	private int gameOrder;
	private int levelOrder;
	private int characterIndex;

    private MediaPlayer mLetterSound;

	private String character, currentLanguage;

//    public class LetterSound extends AsyncTask<Void, Void, Void> {
//        MediaPlayer backGroudPlayer;
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            backGroudPlayer = MediaPlayer.create(getActivity(), R.raw.a_es);
//            backGroudPlayer.setLooping(false);
//            backGroudPlayer.setVolume(100,100);
//            backGroudPlayer.start();
//            return null;
//        }
//
//        public void stop(){
//            backGroudPlayer.stop();
//        }
//
//    }
//
//    LetterSound mLetterSound = new LetterSound();
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mLetterSound.doInBackground();
//    }
//
//    @Override
//    public void onPause() {
//        mLetterSound.stop();
//        mLetterSound.cancel(true);
//        super.onPause();
//    }
//
//    @Override
//    public void onStop() {
//        mLetterSound.stop();
//        mLetterSound.cancel(true);
//        super.onStop();
//    }
//
//    @Override
//    public void onDestroy() {
//        mLetterSound.stop();
//        mLetterSound.cancel(true);
//        super.onDestroy();
//    }

	public static CharacterIntroDialogFragment newInstance(GameStructure gameStructure, Progress progress, int gameOrder, int levelOrder, int characterIndex){
		CharacterIntroDialogFragment fragment = new CharacterIntroDialogFragment();

		Bundle args = new Bundle();
		args.putSerializable(GAME_STRUCTURE_PARAM, gameStructure);
		args.putSerializable(PROGRESS_PARAM, progress);
		args.putInt(GAME_ORDER_PARAM, gameOrder);
		args.putInt(LEVEL_ORDER_PARAM, levelOrder);
		args.putInt(CHARACTER_INDEX_PARAM, characterIndex);
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
		}

        character = gameStructure.findGameByOrder(gameOrder).getCharacters()[characterIndex];
        currentLanguage = Settings.getCurrentLanguage(getActivity());
        if (Settings.isSoundEnabled(getActivity())) {
            Resources res = this.getContext().getResources();
            // Se maneja el caso especial de la 'ñ' que no puede incluirse como nombre de un asset
			// Se maneja el caso especial de los numeros ya que el nombre del asset no puede empezar con un numero
			String fileName = String.format("%s_%s",character.toLowerCase(), currentLanguage).replace("ñ","n1");
			if (character.matches("\\b\\d")){
				fileName = "num" + fileName;
			}
			int soundId = res.getIdentifier(fileName, "raw", this.getContext().getPackageName());
            if (soundId != 0){
                mLetterSound = MediaPlayer.create(getActivity(), soundId);
            }
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

	    if (mLetterSound != null) {
			mLetterSound.start();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_character_intro_dialog, container, false);

//		TextView titleText = (TextView) view.findViewById(R.id.characterIntroTitle);
//		String gameName = gameStructure.findGameByOrder(gameOrder).getName();
//		String levelName = gameStructure.findLevelByOrder(levelOrder).getName();
//		titleText.setText(String.format("%s ( %s )",gameName,levelName));

		ImageView alphaFriendImage = (ImageView)view.findViewById(R.id.alphafriendImage);
		// Se maneja el caso especial de la 'ñ' que no puede incluirse como nombre de un asset
		String alphaResourceName = String.format("alpha_%s_%s", character.toLowerCase(), currentLanguage).replace("ñ","n1");
		Resources contextResources = getActivity().getResources();
		int alphaResourceId = contextResources.getIdentifier(alphaResourceName, "drawable", getActivity().getPackageName());
		if (alphaResourceId != 0) {
			alphaFriendImage.setImageResource(alphaResourceId);
		} else {
			alphaFriendImage.setImageResource(R.drawable.shapes_icon);
		}

		DrawingView drawingView = (DrawingView)view.findViewById(R.id.hintDrawingView);
		drawingView.setShowHints(true);
		drawingView.setContourType("full");
		drawingView.setShowBeginningMark(false);
		drawingView.setShowEndingMark(false);
		drawingView.setCharacter(gameStructure.findGameByOrder(gameOrder).getCharacters()[characterIndex].charAt(0));

		drawingView.setGameDialogsEventsListener(gameDialogsEventsListener);

		ImageButton cancelButton = (ImageButton)view.findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LoadedResources.getInstance().playSound(R.raw.button_click);
				if (gameDialogsEventsListener != null) {

					// Para evitar que se envíe de nuevo el evento al finalizar el hint en DrawingView
					DrawingView drawingView = (DrawingView)CharacterIntroDialogFragment.this.getView().findViewById(R.id.hintDrawingView);
					drawingView.setGameDialogsEventsListener(null);

					gameDialogsEventsListener.onCancelCharacterSelected();
				}
			}

		});

		ImageButton startButton = (ImageButton)view.findViewById(R.id.startLevelButton);
		startButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				LoadedResources.getInstance().playSound(R.raw.button_click);
				if (gameDialogsEventsListener != null) {

					// Para evitar que se envíe de nuevo el evento al finalizar el hint en DrawingView
					DrawingView drawingView = (DrawingView)CharacterIntroDialogFragment.this.getView().findViewById(R.id.hintDrawingView);
					drawingView.setGameDialogsEventsListener(null);

					gameDialogsEventsListener.onStartCharacterSelected();
				}
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

	public void startHint(){
		DrawingView drawingView = (DrawingView)getView().findViewById(R.id.hintDrawingView);
		drawingView.load();
		drawingView.hint();
	}

	@Override
	public void onResume() {
		super.onResume();
		DrawingView drawingView = (DrawingView)getView().findViewById(R.id.hintDrawingView);
		drawingView.setGameDialogsEventsListener(gameDialogsEventsListener);
		if (!fragmentStarting) {
			drawingView.hint();
		}
		fragmentStarting = false;
	}

	@Override
	public void onPause() {
		super.onPause();
		DrawingView drawingView = (DrawingView)getView().findViewById(R.id.hintDrawingView);
		drawingView.setGameDialogsEventsListener(null);
	}

	@Override
    public void onStop(){
        super.onStop();
        if (mLetterSound !=null){
            mLetterSound.release();
            mLetterSound = null;
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

	public int getCharacterIndex() {
		return characterIndex;
	}

	public void setCharacterIndex(int characterIndex) {
		this.characterIndex = characterIndex;
	}

}
