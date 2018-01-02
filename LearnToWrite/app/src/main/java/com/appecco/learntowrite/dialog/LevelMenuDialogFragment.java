package com.appecco.learntowrite.dialog;

import org.json.JSONArray;
import org.json.JSONException;

import com.appecco.learntowrite.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LevelMenuDialogFragment extends DialogFragment {
	
	Activity activity;
	String messageText;
	int levels;
	JSONArray scores;
	int selectedLevel;
	
	public interface LevelMenuDialogListener{
        public void onLevelMenuDialogSelection(DialogFragment dialog, int selectedLevel);
        public void onLevelMenuDialogCancel(DialogFragment dialog);
	}
	
	LevelMenuDialogListener listener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

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
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.level_menu_layout, null);
	    builder.setView(view);
	    TextView messageView = (TextView)view.findViewById(R.id.levelDialogText);
		messageView.setText(messageText);
		LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.levelButtonsLayout);
        Button btn;

		for (int i = 0; i < Math.ceil((double)levels/(double)4); i++) {
			LinearLayout layout_row = new LinearLayout(view.getContext());
			layout_row.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT));

			for (int j = 0; j < 4; j++) {
			    if ((i*4) + j < levels){
                    btn = new Button(getActivity());
                    btn.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    try {
                        if (scores.getInt((i*4) + j) == -1){
                            btn.setText("Locked");
                            btn.setEnabled(false);
                        } else {
                            btn.setText("Level " + ((i*4) + j +1));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    btn.setTag((i*4) + j);
                    btn.setOnClickListener(new OnClickListener(){

                        @Override
                        public void onClick(View view) {
                            int level;
                            level = (int)((Button)view).getTag();
                            listener.onLevelMenuDialogSelection(LevelMenuDialogFragment.this, level);
                            getDialog().dismiss();
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
				listener.onLevelMenuDialogCancel(LevelMenuDialogFragment.this);
				getDialog().dismiss();
			}
        	
        });
	    return builder.create();
	}

	public void setMessageText(String messageText){
		this.messageText = messageText;
	}
	
	public void setLevels(int levels){
		this.levels = levels;
	}

	public void setScores(JSONArray scores) {
		this.scores = scores;
	}

}