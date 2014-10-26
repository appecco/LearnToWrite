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
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
		
	}
	
	LevelDialogListener listener;
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (LevelDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LevelDialogListener");
        }
    }
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    LayoutInflater inflater = getActivity().getLayoutInflater();

	    View view = inflater.inflate(R.layout.level_layout, null);
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    builder.setView(view)
	    // Add action buttons
	           .setPositiveButton(R.string.start_level, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   listener.onDialogPositiveClick(LevelDialogFragment.this);
	               }
	           })
	           .setNegativeButton(R.string.cancel_level, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int id) {
	                   listener.onDialogNegativeClick(LevelDialogFragment.this);
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
