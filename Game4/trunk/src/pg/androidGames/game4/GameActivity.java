package pg.androidGames.game4;

import pg.androidGames.game4.view.DrawingView;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class GameActivity extends Activity {
	
	DrawingView viewDraw;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		viewDraw = (DrawingView)findViewById(R.id.viewDraw);
		
		Button btnRetry = (Button)findViewById(R.id.btnRetry);
		btnRetry.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				viewDraw.reset();
				EditText txtGesture = (EditText)findViewById(R.id.txtGesture);
				txtGesture.setText("");
			}
			
		});
	}

	public void onRadPenWidthClick(View view) {
				
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    switch(view.getId()) {
	        case R.id.radPenWide:
	            if (checked)
	                viewDraw.setPenWidth(36);
	            break;
	        case R.id.radPenMedium:
	            if (checked)
	                viewDraw.setPenWidth(24);
	            break;
	        case R.id.radPenThin:
	            if (checked)
	                viewDraw.setPenWidth(16);
	            break;
	    }
	}
	
	public void onRadPenColorClick(View view){
		
	    boolean checked = ((RadioButton) view).isChecked();
	    
	    switch(view.getId()) {
	        case R.id.radColorBlue:
	            if (checked)
	                viewDraw.setPenColor(Color.BLUE);
	            break;
	        case R.id.radColorRed:
	            if (checked)
	                viewDraw.setPenColor(Color.RED);
	            break;
	        case R.id.radColorGreen:
	            if (checked)
	                viewDraw.setPenColor(Color.GREEN);
	            break;
	        case R.id.radColorYellow:
	            if (checked)
	                viewDraw.setPenColor(Color.YELLOW);
	            break;
	        case R.id.radColorOrange:
	            if (checked)
	                viewDraw.setPenColor(Color.rgb(255, 140, 0));
	            break;
	    }
	}
}
