package pg.androidGames.game4;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pg.androidGames.game4.view.DrawingView;
import pg.androidGames.utils.StorageOperations;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class GameActivity extends Activity {
	
	private static final String LEVEL_FILE = "level";
	private static final String CURRENT_LEVEL_KEY = "currentLevel";
	private static final String SETTINGS_FILE="settings";
	
	DrawingView viewDraw;
	
	private String currentGame;
	private JSONObject levelsJson;
	private JSONArray levelDefinitions;
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		JSONArray gamesJson;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		viewDraw = (DrawingView)findViewById(R.id.viewDraw);
		
		Bundle b = getIntent().getExtras();
		currentGame = b.getString("game");
		
		Button btnRetry = (Button)findViewById(R.id.btnRetry);
		btnRetry.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				viewDraw.reset();
				EditText txtGesture = (EditText)findViewById(R.id.txtGesture);
				txtGesture.setText("");
			}
			
		});
		
		Button btnHint = (Button)findViewById(R.id.btnHint);
		btnHint.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				viewDraw.hint();
				
			}
			
		});

		Button btnNext = (Button)findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				viewDraw.next();
				
			}
			
		});

		Button btnSave = (Button)findViewById(R.id.btnSave);
		btnSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				viewDraw.save();
				
			}
			
		});

		try {
			levelsJson = StorageOperations.loadAssetsJson(this, "files/levels.json");
			gamesJson = levelsJson.getJSONArray("games");
			for (int i=0;i<gamesJson.length();i++){
				if (gamesJson.getJSONObject(i).getString("name").equals(currentGame)){
					levelDefinitions = gamesJson.getJSONObject(i).getJSONArray("levels");
				}
			}
		} catch (IOException | JSONException ex) {
			Toast.makeText(this, "The levels definition file could not be loaded", Toast.LENGTH_LONG).show();
		}
		try {
			currentLevel = Integer.parseInt(StorageOperations.readDataFromPreferencesFile(this, LEVEL_FILE, CURRENT_LEVEL_KEY,"1"));
		} catch (Exception e){
			currentLevel = 1;
		}
		try {
			int characterGroup = Integer.parseInt(levelDefinitions.getJSONObject(currentLevel-1).getString("characterGroup"));
			viewDraw.setCharacterGroup(levelsJson.getJSONArray("characterGroups").getJSONArray(characterGroup));
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
	
	public void levelCompleted(){
		currentLevel++;
		if (levelDefinitions.length()>currentLevel){
			Toast.makeText(this, "Congratulations, you have reached level " + Integer.toString(currentLevel), Toast.LENGTH_LONG).show();
			try {
				int characterGroup = Integer.parseInt(levelDefinitions.getJSONObject(currentLevel-1).getString("characterGroup"));
				viewDraw.setCharacterGroup(levelsJson.getJSONArray("characterGroups").getJSONArray(characterGroup));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(this, "Congratulations!!! , you have learnt all the " + currentGame, Toast.LENGTH_LONG).show();
			finish();
		}
	}
}
