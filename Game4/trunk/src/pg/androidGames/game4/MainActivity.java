package pg.androidGames.game4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
 
		//DrawingView drawingView = new DrawingView(this);
		//setContentView(drawingView);
		//drawingView.requestFocus();
		
		setContentView(R.layout.activity_main);
		
		Button btnNewGame = (Button)findViewById(R.id.btnNewGame);
		btnNewGame.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View view) {
				Intent otherIntent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(otherIntent);
			}
			
		});

	}

}

