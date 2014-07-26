package pg.androidGames.game4.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.LinkedList;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import org.json.*;

public class DrawingView extends View implements OnTouchListener {

	private static final float FONT_SIZE = 240.0f;
	private static final int GESTURE_TYPE_MOVE = 0;
	private static final int GESTURE_TYPE_DRAW = 1;
	
	private Typeface customFont;
	private float scale;
	
	private Bitmap transpBitmap; 
	private Bitmap canvasBitmap; 
	private Canvas hintCanvas, drawCanvas;
	private Path mPath, fontPath;
	private Paint mPaint, fontPaint, animPaint;
	private Paint gridPaint;
	private LinkedList<Path> paths = new LinkedList<Path>();
	
	private float mX, mY, gX = 0, gY = 0;
	private JSONObject json;
	private JSONArray jsonPaths;
	private JSONArray jsonPath;
	
	private int characterOutlineColor = Color.BLACK;
	private int characterFillColor = Color.YELLOW;

	private char currentChar = 'A';
	private String targetGesture = "X";
	private StringBuilder currentGesture = new StringBuilder();
	
	private boolean animating = false;
	private JSONObject animJson;
	private JSONArray animPaths;
	
	private static final float DRAW_TOLERANCE = 4;
	private static final float GESTURE_TOLERANCE = 20;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DrawingView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		scale = getResources().getDisplayMetrics().density;
		
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		customFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/dnealiancursive.ttf");
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(16);

		animPaint = new Paint();
		animPaint.setTextSize((int)(FONT_SIZE * scale + 0.5f));
		animPaint.setTypeface(customFont);
		animPaint.setAntiAlias(true);
		animPaint.setStrokeJoin(Paint.Join.ROUND);
		animPaint.setStrokeMiter(4.0f);
		animPaint.setStrokeCap(Paint.Cap.ROUND);
		animPaint.setStrokeWidth(16);
		animPaint.setStyle(Paint.Style.FILL);
		animPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
		
		fontPaint = new Paint();
		fontPaint.setTextSize((int)(FONT_SIZE * scale + 0.5f));
		fontPaint.setTypeface(customFont);
		fontPaint.setAntiAlias(true);
		fontPaint.setStrokeJoin(Paint.Join.ROUND);
		fontPaint.setStrokeMiter(4.0f);
		fontPaint.setStrokeCap(Paint.Cap.ROUND);
		fontPaint.setStrokeWidth(16);

		gridPaint = new Paint();
		gridPaint.setColor(Color.GRAY);
		gridPaint.setStrokeWidth(1);
		gridPaint.setStyle(Paint.Style.STROKE);

		fontPath = new Path();
		
		hintCanvas = new Canvas();
		drawCanvas = new Canvas();

		mPath = new Path();
		paths.add(mPath);
		
		try {
			jsonPaths = new JSONArray();
			json = new JSONObject();
			json.put("paths", jsonPaths);
		} catch (JSONException e) {
			
			e.printStackTrace();
		}

		load();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		drawCanvas = new Canvas(canvasBitmap);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getHeight();
		int width = getWidth();
		
		float strokeWidth = mPaint.getStrokeWidth();
		
		drawCanvas.drawColor(Color.WHITE);

		fontPaint.setColor(characterFillColor);
		fontPaint.setStyle(Paint.Style.FILL);
		fontPaint.getTextPath(Character.toString(currentChar), 0, 1, (int)(150 * scale + 0.5f), (int)(200 * scale + 0.5f), fontPath);
		
		if (transpBitmap == null){
			transpBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			hintCanvas.setBitmap(transpBitmap);
			hintCanvas.drawColor(Color.WHITE);
			
			fontPaint.setColor(characterOutlineColor);
			fontPaint.setStyle(Paint.Style.STROKE); 
			
		    hintCanvas.drawPath(fontPath, fontPaint);
		    hintCanvas.drawPath(fontPath, animPaint);
		}
				
		/*for (int x = 50; x < width; x += 50) {
			canvas.drawLine(x, 0, x, height, gridPaint);
		}
		for (int y = 50; y < height; y += 50) {
			canvas.drawLine(0, y, width, y, gridPaint);
		}*/

		fontPaint.setColor(characterOutlineColor);
		fontPaint.setStyle(Paint.Style.STROKE);
	    drawCanvas.drawPath(fontPath, fontPaint);

		fontPaint.setColor(characterFillColor);
		fontPaint.setStyle(Paint.Style.FILL);
		drawCanvas.drawPath(fontPath, fontPaint);

		for (Path p : paths) {
			if (animating){
				mPaint.setStrokeWidth(48);
				drawCanvas.drawPath(p, mPaint);
				drawCanvas.drawBitmap(transpBitmap, 0, 0, null);
				mPaint.setStrokeWidth(strokeWidth);
			} else {
				drawCanvas.drawPath(p, mPaint);
			}
		}
		canvas.drawBitmap(canvasBitmap,0,0,null);

	}

	private void touch_start(float x, float y) {
		char gestureChar = 0;

		float dx = x - gX;
		float dy = y - gY;

		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		gX = x;
		gY = y;

		if (!animating){
			
			if (Math.abs(dx) + Math.abs(dy) > GESTURE_TOLERANCE) {

				gX = x;
				gY = y;

				gestureChar = getGestureChar(dx, dy, GESTURE_TYPE_MOVE);
				for (float s = GESTURE_TOLERANCE; s <= Math.abs(dx) + Math.abs(dy); s += GESTURE_TOLERANCE) {
					currentGesture.append(gestureChar);
				}
			}
			
			jsonPath = new JSONArray();
			jsonPaths.put(jsonPath);
			try {
				jsonPath.put(new JSONObject().put("x", x).put("y", y));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void touch_move(float x, float y, boolean isHistory) {
		char gestureChar = 0;

		float dx = x - gX;
		float dy = y - gY;

		float idx, idy;

		if (!isHistory) {
			idx = Math.abs(x - mX);
			idy = Math.abs(y - mY);

			if (idx > DRAW_TOLERANCE || idy > DRAW_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				
				if (!animating){
					try {
						jsonPath.put(new JSONObject().put("x", x).put("y", y));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				
				mX = x;
				mY = y;
			}
		}

		if (!animating && Math.abs(dx) + Math.abs(dy) > GESTURE_TOLERANCE) {

			gX = x;
			gY = y;

			gestureChar = getGestureChar(dx, dy, GESTURE_TYPE_DRAW);
			for (float s = GESTURE_TOLERANCE; s <= Math.abs(dx) + Math.abs(dy); s += GESTURE_TOLERANCE) {
				currentGesture.append(gestureChar);
			}
		}
	}

	private void touch_up() {
		int distance;
		if (!animating){
			mPath.lineTo(mX, mY);
					
			if (currentGesture.toString().equals(targetGesture)){
				Toast.makeText(getContext(), "Perfect!!!!", Toast.LENGTH_LONG).show();
			} else {
				distance = editDistance(currentGesture.toString(),targetGesture);
				if (distance < 10){
					Toast.makeText(getContext(), "Excellent!!! " + Integer.toString(distance), Toast.LENGTH_LONG).show();
				} else if (distance < 20){
					Toast.makeText(getContext(), "Very good!! " + Integer.toString(distance), Toast.LENGTH_LONG).show();
				} else if (distance < 30){
					Toast.makeText(getContext(), "Good! " + Integer.toString(distance), Toast.LENGTH_LONG).show();
				} else if (distance < 40){
					Toast.makeText(getContext(), "Not bad " + Integer.toString(distance), Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getContext(), "Can be better " + Integer.toString(distance), Toast.LENGTH_LONG).show();
				}
			}
		}
		mPath = new Path();
		paths.add(mPath);
	}
	
	private char getGestureChar(float dx, float dy, int gestureType) {
		char gestureChar;
		float tan;
		if (dx == 0) { // para evitar tangentes indeterminadas
			if (dy > 0) {
				gestureChar = 'c';
			} else {
				gestureChar = 'g';
			}
		} else {
			tan = dy / dx;
			if (dx > 0) {
				if (dy > 0) {
					if (tan < 0.41) {
						gestureChar = 'a';
					} else if (tan >= 0.41 && tan < 2.41) {
						gestureChar = 'b';
					} else {
						gestureChar = 'c';
					}
				} else {
					if (tan >= -0.41) {
						gestureChar = 'a';
					} else if (tan >= -2.41 && tan < -0.41) {
						gestureChar = 'h';
					} else {
						gestureChar = 'g';
					}
				}
			} else {
				if (dy > 0) {
					if (tan < -2.41) {
						gestureChar = 'c';
					} else if (tan >= -2.41 && tan < -0.41) {
						gestureChar = 'd';
					} else {
						gestureChar = 'e';
					}
				} else {
					if (tan < 0.41) {
						gestureChar = 'e';
					} else if (tan >= 0.41 && tan < 2.41) {
						gestureChar = 'f';
					} else {
						gestureChar = 'g';
					}
				}
			}
		}
		if (gestureType == GESTURE_TYPE_DRAW){
			gestureChar -= 32;
		}
		return gestureChar;
	}

	private int editDistance(String s, String t){
		int d[][] = new int[s.length()+1][t.length()+1];
		for (int i=0; i<s.length()+1; i++){
			d[i][0] = i;
		}
		for (int j=0; j<t.length()+1; j++){
			d[0][j] = j;
		}
		for (int j=1; j<t.length()+1; j++){
			for (int i=1; i<s.length()+1; i++){
				if (s.charAt(i-1) == t.charAt(j-1)){
					d[i][j] = d[i-1][j-1];
				} else {
					d[i][j] = Math.min(Math.min(d[i-1][j]+1, d[i][j-1]+1), d[i-1][j-1]+1);
				}
			}
		}
		return d[s.length()][t.length()];
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(x, y);
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < event.getHistorySize(); i++) {
				touch_move(event.getHistoricalX(i), event.getHistoricalY(i),
						true);
			}
			touch_move(x, y, false);
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			invalidate();
			break;
		}
		return true;
	}

	public void setPenWidth(int width) {
		mPaint.setStrokeWidth(width);
		invalidate();
	}

	public void setPenColor(int color) {
		mPaint.setColor(color);
		invalidate();
	}

	public void reset() {
		paths.clear();
		mPath = new Path();
		paths.add(mPath);
		
		currentGesture = new StringBuilder();
		gX = 0;
		gY = 0;
		
		try {
			jsonPaths = new JSONArray();
			json = new JSONObject();
			json.put("paths", jsonPaths);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		invalidate();
	}

	public void hint() {
		JSONArray storedPath;
		JSONObject point;
		try {
			Handler animHandler = new Handler();
			long animDelay = 0;
			
			for (int i=0;i<animPaths.length();i++){
				storedPath = animPaths.getJSONArray(i);
				
				for (int j=0;j<storedPath.length();j++){
					point = storedPath.getJSONObject(j);
					animDelay++;
					
					if (j==0){
						final float tempX = (float)point.getDouble("x");
						final float tempY = (float)point.getDouble("y");
						
						animHandler.postDelayed(new Runnable() { 
					         public void run() {
									animating = true;
									touch_start((float)tempX,tempY);
									invalidate();
					         } 
					    }, animDelay*25); 

					} else {
						
						final float tempX = (float)point.getDouble("x");
						final float tempY = (float)point.getDouble("y");
						
						animHandler.postDelayed(new Runnable() { 
					         public void run() {
					        	 touch_move(tempX,tempY,false);
					        	 invalidate();
					         } 
					    }, animDelay*25); 
					}
				}
				animHandler.postDelayed(new Runnable() { 
			         public void run() {
			        	 touch_up();
						 animating = false;
						 reset();
			         } 
			    }, animDelay*25);
			}
			

			
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	public void next(){
		if (currentChar == 'Z'){
			currentChar = 'a';
		} else if (currentChar == 'z'){
			currentChar = '0';
		} else if (currentChar == '9'){
			currentChar = 'A';
		} else {
			currentChar++;
		}
		transpBitmap = null;
		load();
		reset();
	}
	
	private void load(){
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			try {
				File file = new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
						"Game4Paths/" + Character.toString(currentChar) + ".json");
				if (!file.exists()){
					Toast.makeText(getContext(), "Hint for " + Character.toString(currentChar) + " is not available.", Toast.LENGTH_SHORT).show();
					return;
				}
				FileInputStream stream = new FileInputStream(file);
				String jsonStr = null;
				try {
					FileChannel fc = stream.getChannel();
					MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
							fc.size());

					jsonStr = Charset.defaultCharset().decode(bb).toString();
				} finally {
					stream.close();
				}
				animJson = new JSONObject(jsonStr);
				animPaths = animJson.getJSONArray("paths");
				targetGesture = animJson.getString("gesture");
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
		}		
	}
	
	public void save(){
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Game4Paths");
			if (file.exists() || file.mkdirs()) {
				try {
					targetGesture = currentGesture.toString();
					animJson = json;
					animPaths = animJson.getJSONArray("paths");
					json.put("gesture", targetGesture);
					Writer output = null;
					output = new BufferedWriter(new FileWriter(new File(file,Character.toString(currentChar) + ".json")));
					output.write(json.toString());
					output.close();
				} catch (Exception e) {
					Log.e("Game4-Path", e.getMessage());
				}
			} else {
				Log.e("Game4-Path", "Directory not created");
			}
		} else {
			Log.e("Game4-Path", "Media not mounted");
		}	
	}

	
}
