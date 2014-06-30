package pg.androidGames.game4;

import pg.androidGames.game4.view.DrawingView;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
 
		DrawingView drawingView = new DrawingView(this);
		setContentView(drawingView);
		drawingView.requestFocus();

	}

}

