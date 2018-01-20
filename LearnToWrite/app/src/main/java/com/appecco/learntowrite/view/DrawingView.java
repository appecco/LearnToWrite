package com.appecco.learntowrite.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import org.json.*;

import com.appecco.learntowrite.GameActivity;
import com.appecco.learntowrite.R;
import com.appecco.utils.StorageOperations;

public class DrawingView extends View implements OnTouchListener {

	//Tamaño base del Font (Antes de aplicar proporcion)
	private static final float FONT_SIZE = 896.0f;
	private static final int STROKE_WIDTH = 26;
	private static final int STROKE_WIDTH_ANIM = 72;

	//Estaticos de Tipo de Gesto
	private static final int GESTURE_TYPE_MOVE = 0;
	private static final int GESTURE_TYPE_DRAW = 1;

	//Tamaño de referencia de pantalla en la que se obtuvieron los Path base
	private static final double REFERENCE_WIDTH = 1673d;
	private static final double REFERENCE_HEIGHT = 1080d;

	//Tolerancia base para los paths (Que tanto se debe mover para dibujar una nueva linea)
	private static final float DRAW_TOLERANCE = 4;
	//Tolerancia base para los gestos (Que tanto se debe mover para tomar un nuevo punto incluyendo proporcionalidad)
	private static final float GESTURE_TOLERANCE = 20;

	//Proporcionalidad
	private double PROP_TOTAL;

	//Ajuste de centrado en cada eje
	private static final boolean SAVE_ENABLED = false;
	private double CENTER_WIDTH;
	private double CENTER_HEIGHT;

	private Rect bounds = new Rect();
	private Rect anim_bounds = new Rect();

	private GameActivity activity;

	private Typeface customFont;

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

	private int characterOutlineColor = Color.BLUE;
	private int characterFillColor = Color.YELLOW;

	//private int currentCharIndex = -1;
	//private JSONArray characterGroup;
	private char currentChar = ' ';
	private String targetGesture = "X";
	private StringBuilder currentGesture = new StringBuilder();

	private boolean animating = false;
	private JSONObject animJson;
	private JSONArray animPaths;

	private int level_score;
	private boolean showHints;
	private String contourType;
	private boolean showBeginningMark;
	private boolean showEndingMark;

	private final Drawable star = getResources().getDrawable(R.drawable.star);
    private final Drawable empty_star = getResources().getDrawable(R.drawable.empty_star);

    private final DashPathEffect dashEffect = new DashPathEffect(new float[]{10, 40}, 0);

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DrawingView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		activity = (GameActivity) getContext();

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
		mPaint.setStrokeWidth(STROKE_WIDTH_ANIM);

		animPaint = new Paint();
		animPaint.setTextSize((int) (FONT_SIZE));
		animPaint.setTypeface(customFont);
		animPaint.setAntiAlias(true);
		animPaint.setStrokeJoin(Paint.Join.ROUND);
		animPaint.setStrokeMiter(4.0f);
		animPaint.setStrokeCap(Paint.Cap.ROUND);
		animPaint.setStrokeWidth(STROKE_WIDTH);
		animPaint.setStyle(Paint.Style.FILL);
		animPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

		fontPaint = new Paint();
		fontPaint.setTextSize((int) (FONT_SIZE));
		fontPaint.setTypeface(customFont);
		fontPaint.setAntiAlias(true);
		fontPaint.setStrokeJoin(Paint.Join.ROUND);
		fontPaint.setStrokeMiter(4.0f);
		fontPaint.setStrokeCap(Paint.Cap.ROUND);
		fontPaint.setStrokeWidth(STROKE_WIDTH);

		gridPaint = new Paint();
		gridPaint.setColor(Color.GRAY);
		gridPaint.setStrokeWidth(1);
		gridPaint.setStyle(Paint.Style.STROKE);

		fontPath = new Path();

		hintCanvas = new Canvas();
		drawCanvas = new Canvas();

		mPath = new Path();
		paths.add(mPath);

		level_score = 0;

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
		CalcPropCenter();
		activity.readyForChallenge();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int height = getHeight();
		int width = getWidth();

		drawCanvas.drawColor(Color.WHITE);

		fontPaint.setColor(characterFillColor);
		fontPaint.setStyle(Paint.Style.FILL);
		fontPaint.getTextPath(Character.toString(currentChar), 0, 1, (int) CENTER_WIDTH, (int) CENTER_HEIGHT, fontPath);

		if (transpBitmap == null) {
			transpBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			hintCanvas.setBitmap(transpBitmap);
			hintCanvas.drawColor(Color.WHITE);

			fontPaint.setColor(characterOutlineColor);
			fontPaint.setStyle(Paint.Style.STROKE);

			hintCanvas.drawPath(fontPath, fontPaint);
			hintCanvas.drawPath(fontPath, animPaint);
		}

		//Elijamos como se pinta el font segun el nivel de dificultad
		if (contourType.equals("full")) {
            fontPaint.setColor(characterOutlineColor);
            fontPaint.setStyle(Paint.Style.STROKE);
            drawCanvas.drawPath(fontPath, fontPaint);

            fontPaint.setColor(characterFillColor);
            fontPaint.setStyle(Paint.Style.FILL);
            drawCanvas.drawPath(fontPath, fontPaint);
        }
        else if (contourType.equals("medium")){
            fontPaint.setColor(Color.argb(50,0,0,255));
            fontPaint.setStyle(Paint.Style.STROKE);
            fontPaint.setPathEffect(dashEffect);
            fontPaint.setStrokeWidth((int)(STROKE_WIDTH/2));
            drawCanvas.drawPath(fontPath, fontPaint);
        }
        else{
            fontPaint.setColor(Color.argb(90,0,0,255));
            fontPaint.setStyle(Paint.Style.STROKE);
            drawCanvas.drawPath(fontPath, fontPaint);
        }

		for (Path p : paths) {
			if (animating) {
			    //Pintar el hint, ampliar el STROKE para asegurar que cubra el path de la letra
				mPaint.setStrokeWidth((float) (STROKE_WIDTH_ANIM / PROP_TOTAL));
				drawCanvas.drawPath(p, mPaint);
				drawCanvas.drawBitmap(transpBitmap, 0, 0, null);
//				//DEBUG
//                Log.d("DRAW", "Animating");

    		} else {
				drawCanvas.drawPath(p, mPaint);

//				//DEBUG
//                PathMeasure pm = new PathMeasure(p, false);
//                float aCoordinates[] = {0f, 0f};
//                pm.getPosTan(pm.getLength(), aCoordinates, null);
//                Log.d("DRAW", "Number of Paths: " + paths.size() + "  -   Last Point: " + Float.toString(aCoordinates[0]) + "," + Float.toString(aCoordinates[1]));
			}
		}

		canvas.drawBitmap(canvasBitmap, 0, 0, null);

		//Agreguemos las 3 estrellas segun el score del nivel se decide si empty o filled
		for (int i = 1; i <= 3; i++) {
			if (level_score >= i) {
				star.setBounds((int) ((10 + ((i - 1) * 100)) / PROP_TOTAL), (int) (10 / PROP_TOTAL), (int) ((80 + ((i - 1) * 100)) / PROP_TOTAL), (int) (80 / PROP_TOTAL));
				star.draw(canvas);
			}
			else{
				empty_star.setBounds((int) ((10 + ((i - 1) * 100)) / PROP_TOTAL), (int) (10 / PROP_TOTAL), (int) ((80 + ((i - 1) * 100)) / PROP_TOTAL), (int) (80 / PROP_TOTAL));
				empty_star.draw(canvas);
			}
		}

        if (animating){
            //Pintemos el cursor (pointing_hand)
            Drawable cursor = getResources().getDrawable(R.drawable.pointing_hand);
            cursor.setBounds((int)mX , (int)mY, (int)(mX + (150 / PROP_TOTAL)), (int)(mY + (150 / PROP_TOTAL)));
            cursor.draw(canvas);
        }

		//TODO Dependiendo del nivel hay que hacer que se dibujen puntos a lo largo del Path como guia para el dibujo
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		transpBitmap = null;
		hintCanvas.setBitmap(null);
		canvasBitmap = null;
		drawCanvas.setBitmap(null);
	}

	private void touch_start(float x, float y) {
		char gestureChar;

		//Evitar que el gesto incluya el movimiento de 0,0 al X,Y actual
		if (gX == 0 & gY == 0) {
			gX = x;
			gY = y;
		}

		//Calcular el deltaX (dx) y deltaY (dx) vs las ultimas coordenadas guardadas para el gesto (gX, gY)
		float dx = (float) ((double) (x - gX) * PROP_TOTAL);
		float dy = (float) ((double) (y - gY) * PROP_TOTAL);

		//Reiniciar el Path y asignar las variables (mX, mY son para el control del dibujo, gX, gY para el control del gesto)
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		gX = x;
		gY = y;

		//Dibujemos el punto de inicio para que si el trazo es solo de un punto se vea
        if (!SAVE_ENABLED){
            mPath.lineTo((int)(mX - (3 / PROP_TOTAL)), (int)(mY - (3 / PROP_TOTAL)));
            mPath.lineTo((int)(mX + (3 / PROP_TOTAL)), (int)(mY + (3 / PROP_TOTAL)));
        }

		if (!animating) {
			if (Math.abs(dx) + Math.abs(dy) > GESTURE_TOLERANCE) {
			    //Solamente queremos un caracter del trazo sin touch asi calculemos el caracter del gesto completo y guardemos uno solo caracter para todo
				gestureChar = getGestureChar(dx, dy, GESTURE_TYPE_MOVE);
                currentGesture.append(gestureChar);
			}

			jsonPath = new JSONArray();
			jsonPaths.put(jsonPath);
			try {
				jsonPath.put(new JSONObject().put("x", x - CENTER_WIDTH).put("y", y - CENTER_HEIGHT));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private void touch_move(float x, float y, boolean isHistory) {
		char gestureChar;

		//Evitar que se pueda realizar un trazo erroneo al iniciar un Touch mientras esta animando, eso evita que se de Touch_Start y por tanto gX y gY son 0
		if (gX == 0 & gY == 0) {
			touch_start(x, y);
			invalidate();
		}

		float dx = (float) ((double) (x - gX) * PROP_TOTAL);
		float dy = (float) ((double) (y - gY) * PROP_TOTAL);

		float idx, idy;

		if (!isHistory) {
			idx = Math.abs(x - mX);
			idy = Math.abs(y - mY);

			if (idx > DRAW_TOLERANCE || idy > DRAW_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);

				if (!animating & jsonPath != null) {
					try {
						jsonPath.put(new JSONObject().put("x", x - CENTER_WIDTH).put("y", y - CENTER_HEIGHT));
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
		int similarity;

        mPath.lineTo(mX, mY);

		if (!animating) {
			if (animPaths != null && animPaths.length() == paths.size()) {
			    //Hagamos una pausa para dar oportunidad a que se mire el trazo realizado
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //Calculemos que tan similares son los trazos, si esta habilitado el Save lo mostramos en un Toast sino se lo pasamos al GameActivity
                similarity = similarity(currentGesture.toString(), targetGesture);
				if (SAVE_ENABLED){
				    Toast.makeText(getContext(), Integer.toString((int) similarity), Toast.LENGTH_SHORT).show();
                }
                else {
                    activity.challengeCompleted(similarity);
                }
				return;
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
		if (gestureType == GESTURE_TYPE_DRAW) {
			gestureChar -= 32;
		}
		return gestureChar;
	}

	private static int similarity(String s1, String s2) {
		//Regresa la similaridad de dos cadenas de texto en un coeficiente de 0 a 100% representando que tanto hay que cambiar una cadena de texto para convertirla en la otra por LevenshteinDistance

		//Es indispensable que se llame a EditDistance con la cadena mas larga en el primer parametro
		String longer = s1, shorter = s2;
		if (s1.length() < s2.length()) {
			longer = s2;
			shorter = s1;
		}

		//Longer deberia tener la cadena mas larga, si longer es 0 ambas son 0-Lenght
		int longerLength = longer.length();
		if (longerLength == 0) {
			return 100;
		}

		return (longerLength - editDistance(longer, shorter)) * 100 / longerLength;
	}

	private static int editDistance(String s1, String s2) {
		//algoritmo LevenshteinDistance
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		if (!animating) {
			float x = event.getX();
			float y = event.getY();

			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touch_start(x, y);
					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					for (int i = 0; i < event.getHistorySize(); i++) {
						touch_move(event.getHistoricalX(i), event.getHistoricalY(i),true);
					}
					touch_move(x, y, false);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					touch_up();
					break;
			}
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
		//veamos que no estemos animando
		if (!animating) {
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
	}

	public void hint() {
		//Verifiquemos que no estemos ya animando
		if (!animating) {

			//Hacemos reset para evitar que hayan trazos previos en letras de multiple trazo
			reset();

			JSONArray storedPath;
			JSONObject point;
			if (animPaths != null) {
				try {
					Handler animHandler = new Handler();
					long animDelay = 0;

					for (int i = 0; i < animPaths.length(); i++) {
						storedPath = animPaths.getJSONArray(i);

						for (int j = 0; j < storedPath.length(); j++) {
							point = storedPath.getJSONObject(j);
							animDelay++;

							if (j == 0) {
								final float tempX = (float) ((int) ((point.getDouble("x") / PROP_TOTAL) + CENTER_WIDTH));
								final float tempY = (float) ((int) ((point.getDouble("y") / PROP_TOTAL) + CENTER_HEIGHT));

								animHandler.postDelayed(new Runnable() {
									public void run() {
										animating = true;
										touch_start(tempX, tempY);
										invalidate();
									}
								}, animDelay * 25);

							} else {

								final float tempX = (float) ((int) ((point.getDouble("x") / PROP_TOTAL) + CENTER_WIDTH));
								final float tempY = (float) ((int) ((point.getDouble("y") / PROP_TOTAL) + CENTER_HEIGHT));

								animHandler.postDelayed(new Runnable() {
									public void run() {
										touch_move(tempX, tempY, false);
										invalidate();
									}
								}, animDelay * 25);
							}
						}
						animHandler.postDelayed(new Runnable() {
							public void run() {
								touch_up();
								invalidate();
							}
						}, animDelay * 25);
					}

					//Hacemos el reset y animating=false tambien delayed para que evite que se cambie el flag antes de que termine de animar, el delay debe ser un pelito mas grande para dar tiempo que se vea la animacion del final
					animHandler.postDelayed(new Runnable() {
						public void run() {
							animating = false;
							reset();
						}
					}, animDelay * 30);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getContext(), "Hint for " + Character.toString(currentChar) + " is not available.", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void load() {
		try {
			if (currentChar != ' ') {
				String filePath;
				if (currentChar >= 'A' && currentChar <= 'Z' || currentChar == 'Ñ') {
					filePath = "files/M" + Character.toString(currentChar) + ".json";
				} else {
					filePath = "files/" + Character.toString(currentChar) + ".json";
				}
				if (StorageOperations.assetExists(getContext(), filePath)) {
					animJson = StorageOperations.loadAssetsJson(getContext(), filePath);
					animPaths = animJson.getJSONArray("paths");
					targetGesture = animJson.getString("gesture");

					//Obtener el Rect del dibujo del font original para calculo de proporciones
					try {
						String[] RectSplit = animJson.getString("bounds").split("[,]");
						anim_bounds = new Rect(Integer.parseInt(RectSplit[0]), Integer.parseInt(RectSplit[1]), Integer.parseInt(RectSplit[2]), Integer.parseInt(RectSplit[3]));
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(getContext(), "Hint for " + Character.toString(currentChar) + " is not available.", Toast.LENGTH_SHORT).show();
					animJson = null;
					animPaths = null;
					targetGesture = "X";
				}
			}
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}

		//Actualicemos las Proporciones y Centro de Dibujo para el nuevo Character
		CalcPropCenter();
	}

	public void save() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "learntowrite");
			if (file.exists() || file.mkdirs()) {
				try {
					//Hacer el targetGesture a guardar igual al gesture recien dibujado
					targetGesture = currentGesture.toString();

					//Hacer la animacion igual al gesto recien dibujado
					animJson = json;
					animPaths = animJson.getJSONArray("paths");

					//Agregar al JSON el gesto recien dibujado (Ya contiene los paths)
					json.put("gesture", targetGesture);

					//Calcular el tamaño del font segun se ha dibujado para guardarlos en el JSON y poder calcular proporcionalidad en ejecuciones futuras
					animPaint.getTextBounds(Character.toString(currentChar), 0, 1, bounds);
					json.put("bounds", Integer.toString(bounds.left) + "," + Integer.toString(bounds.top) + "," + Integer.toString(bounds.right) + "," + Integer.toString(bounds.bottom));

					Writer output;
					if (currentChar >= 'A' && currentChar <= 'Z' || currentChar == 'Ñ') {
						output = new BufferedWriter(new FileWriter(new File(file, "M" + Character.toString(currentChar) + ".json")));
					} else {
						output = new BufferedWriter(new FileWriter(new File(file, Character.toString(currentChar) + ".json")));
					}
					output.write(json.toString());
					output.close();
				} catch (Exception e) {
					Log.e("LearnToWrite", e.getMessage());
				}
			} else {
				Log.e("LearnToWrite", "Directory not created");
			}
		} else {
			Log.e("LearnToWrite", "Media not mounted");
		}
	}

	public void setCharacter(char character) {
		this.currentChar = character;
	}

	public void setShowHints(boolean showHints) {
		this.showHints = showHints;
	}

	public void setContourType(String contourType) {
		this.contourType = contourType;
	}

	public void setShowBeginningMark(boolean showBeginningMark){
    	this.showBeginningMark =showBeginningMark;
	}

    public void setShowEndingMark(boolean showEndingMark){
		this.showEndingMark = showEndingMark;
	}

	public void setScore(int score){
    	this.level_score = score;
	}

	public void startChallenge(){
		transpBitmap = null;
		animating = false;
		load();
		reset();
		if (showHints && !SAVE_ENABLED){
			hint();
		}
	}

	private void CalcPropCenter(){
        int height = getHeight();
        int width = getWidth();

        //Calcular la proporcionalidad en cada eje y obtener un promedio para ajustar segun esto el tamaño del font, no podemos ajustar independiente debido a que el dibujo del font se basa en una proporcionalidad unica para su textsize
        PROP_TOTAL = ((REFERENCE_WIDTH / width) + (REFERENCE_HEIGHT / height)) / 2;

        //Calculemos el Tamaño apropiado del font/stroke y asignemoslos
        animPaint.setTextSize((int)(FONT_SIZE / PROP_TOTAL));
        animPaint.setStrokeWidth((float)(STROKE_WIDTH / PROP_TOTAL));
        fontPaint.setTextSize((int)(FONT_SIZE / PROP_TOTAL));
        fontPaint.setStrokeWidth((float)(STROKE_WIDTH / PROP_TOTAL));
        mPaint.setStrokeWidth((float)(STROKE_WIDTH_ANIM / PROP_TOTAL));

        //Calcular el tamaño del font ya pintado para obtener la proporcion exacta segun el tamaño del font pintado y el tamaño del font de la animacion y para obtener los valores de centrado
        animPaint.getTextBounds(Character.toString(currentChar),0,1, bounds);
        if (bounds.right - bounds.left != 0 && bounds.top - bounds.bottom != 0){
            PROP_TOTAL = (((double)Math.abs(anim_bounds.right - anim_bounds.left) / (double)Math.abs(bounds.right - bounds.left)) + ((double)Math.abs(anim_bounds.top - anim_bounds.bottom) / (double)Math.abs(bounds.top - bounds.bottom))) / 2;
        }
        else{
            PROP_TOTAL = 1;
        }

        //Calcular los valores de centrado en cada eje, largo del eje entre 2 mas el centro de la letra
        CENTER_WIDTH = (width / 2) - Math.abs(((bounds.left - bounds.right)/2));
        CENTER_HEIGHT = (height / 2) + Math.abs(((bounds.top - bounds.bottom)/2)) - bounds.bottom;
    }
}
