package pg.androidGames.game4.dialog;

import pg.androidGames.game4.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LevelDialogFragment extends DialogFragment {
	
	Activity activity;
	String messageText;
	
	public interface LevelDialogListener{
        public void onLevelDialogStartLevel(DialogFragment dialog);
        public void onLevelDialogCancel(DialogFragment dialog);
	}
	
	LevelDialogListener listener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        try {
            listener = (LevelDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement LevelDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.level_layout, null);
	    builder.setView(view)
	           .setPositiveButton(R.string.start_level, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   listener.onLevelDialogStartLevel(LevelDialogFragment.this);
	               }
	           })
	           .setNegativeButton(R.string.cancel_level, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   listener.onLevelDialogCancel(LevelDialogFragment.this);
	               }
	           });

	    TextView messageView = (TextView)view.findViewById(R.id.levelDialogText);
		messageView.setText(messageText);
	    
	    return builder.create();
	}

	public void setMessageText(String messageText){
		this.messageText = messageText;
	}

}
