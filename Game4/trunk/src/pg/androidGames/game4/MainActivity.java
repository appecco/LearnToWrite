package pg.androidGames.game4;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity {
	
	public class BackgroundSound extends AsyncTask<Void, Void, Void> {
		MediaPlayer backGroudPlayer;
		
		@Override
	    protected Void doInBackground(Void... params) {
			backGroudPlayer = MediaPlayer.create(MainActivity.this, R.raw.backgroud_sound);
	        backGroudPlayer.setLooping(true);
	        backGroudPlayer.setVolume(100,100); 
	        backGroudPlayer.start(); 
	        return null;
	    }
		
		public void stop(){
			backGroudPlayer.stop();
		}
	}
	
	BackgroundSound mBackgroundSound = new BackgroundSound();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		final MediaPlayer mPlayerClick = MediaPlayer.create(this, R.raw.button_click);
		
		setContentView(R.layout.activity_main);
		
		ImageButton btnNewGameCursive = (ImageButton)findViewById(R.id.btnNewGameCursive);
		btnNewGameCursive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				
				mPlayerClick.start();
				
				Intent otherIntent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(otherIntent);
			}
		});

		ImageButton btnNewGamePrint = (ImageButton)findViewById(R.id.btnNewGamePrint);
		btnNewGamePrint.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				mPlayerClick.start();
				
				Intent otherIntent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(otherIntent);
			}
		});
		
		ImageButton btnNewGameNumbers = (ImageButton)findViewById(R.id.btnNewGameNumbers);
		btnNewGameNumbers.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				mPlayerClick.start();
				
				Intent otherIntent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(otherIntent);
			}
		});
		
		ImageButton btnNewGameShapes = (ImageButton)findViewById(R.id.btnNewGameShapes);
		btnNewGameShapes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				mPlayerClick.start();
				
				Intent otherIntent = new Intent(MainActivity.this,GameActivity.class);
                startActivity(otherIntent);
			}
		});
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    mBackgroundSound.doInBackground();
	}
	
	@Override
	protected void onPause() {
		mBackgroundSound.stop();
		mBackgroundSound.cancel(true);
	    super.onPause();
	}

	@Override
	protected void onStop() {
		mBackgroundSound.stop();
		mBackgroundSound.cancel(true);
	    super.onStop();
	}

	@Override
	protected void onDestroy() {
		mBackgroundSound.stop();
		mBackgroundSound.cancel(true);
	    super.onDestroy();
	}
}

