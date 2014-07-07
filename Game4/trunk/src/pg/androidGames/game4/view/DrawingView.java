package pg.androidGames.game4.view;

import java.util.LinkedList;

import pg.androidGames.game4.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.text.method.Touch;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

public class DrawingView extends View implements OnTouchListener {

	private Canvas mCanvas;
	private Path mPath;
	private Paint mPaint;
	private Paint gridPaint;
	private LinkedList<Path> paths = new LinkedList<Path>();
	private EditText txtGesture;
	private EditText DeltaX, DeltaY;
	private float mX, mY, gX, gY;
	
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
		setFocusable(true);
		setFocusableInTouchMode(true);

		this.setOnTouchListener(this);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(16);
		
		gridPaint = new Paint();
		gridPaint.setColor(Color.GRAY);
		gridPaint.setStrokeWidth(1);
		gridPaint.setStyle(Paint.Style.STROKE);
		
		mCanvas = new Canvas();
		mPath = new Path();
		paths.add(mPath);

	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		int height = getHeight();
		int width = getWidth();
		
		for (int x=50; x<width; x+=50){
			canvas.drawLine(x, 0, x, height, gridPaint);
		}
		for (int y=50; y<height; y+=50){
			canvas.drawLine(0,y,width,y,gridPaint);
		}
		for (Path p : paths) {
			canvas.drawPath(p, mPaint);
		}
	}

	private void touch_start(float x, float y) {
		txtGesture = (EditText)((android.app.Activity)getContext()).findViewById(R.id.txtGesture);
		
		DeltaX = (EditText)((android.app.Activity)getContext()).findViewById(R.id.DeltaX);
		DeltaY = (EditText)((android.app.Activity)getContext()).findViewById(R.id.DeltaY);
		
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
		gX = x;
		gY = y;
	}

	private void touch_move(float x, float y) {
		char gestureChar = 0;
		
		float idx = Math.abs(x - mX);
		float idy = Math.abs(y - mY);
		
		float dx = x - gX;
		float dy = y - gY;
		
		float tan;
		
		DeltaX.setText(Float.toString(dx));
		DeltaY.setText(Float.toString(dy));

		if (idx > DRAW_TOLERANCE || idy > DRAW_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
		
		
		if (Math.abs(dx) + Math.abs(dy) > GESTURE_TOLERANCE) {
			gX = x;
			gY = y;
			
			if (dx == 0){ // para evitar tangentes indeterminadas
				if (dy > 0){
					gestureChar = 'c';
				} else {
					gestureChar = 'g';
				}
			} else {
				tan = dy / dx;
				if (dx > 0) {
					if (dy > 0){
						if (tan < 0.41){
							gestureChar = 'a';
						} else if (tan >= 0.41 && tan < 2.41){
							gestureChar = 'b';
						} else {
							gestureChar = 'c';
						}
					} else {
						if (tan >= -0.41){
							gestureChar = 'a';
						} else if (tan >= -2.41 && tan < -0.41){
							gestureChar = 'h';
						} else {
							gestureChar = 'g';
						}
					}
				} else {
					if (dy > 0){
						if (tan < -2.41){
							gestureChar = 'c';
						} else if (tan >= -2.41 && tan < -0.41){
							gestureChar = 'd';
						} else {
							gestureChar = 'e';
						}
					} else {
						if (tan < 0.41){
							gestureChar = 'e';
						} else if (tan >= 0.41 && tan < 2.41){
							gestureChar = 'f';
						} else {
							gestureChar = 'g';
						}
					}
				}
			}

			txtGesture.getText().append(gestureChar);
			
		}
		
		
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		
		mPath = new Path();
		paths.add(mPath);
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
			touch_move(x, y);
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

	public void reset(){
		paths.clear();
		mPath = new Path();
		paths.add(mPath);
		invalidate();
	}
}
