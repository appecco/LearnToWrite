package pg.androidGames.game4.view;

import java.util.LinkedList;

import pg.androidGames.game4.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
	private LinkedList<Path> paths = new LinkedList<Path>();
	private EditText txtGesture;
	
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
		mPaint.setStrokeWidth(8);
		mCanvas = new Canvas();
		mPath = new Path();
		paths.add(mPath);
		
		txtGesture = (EditText)findViewById(R.id.txtGesture);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		for (Path p : paths) {
			canvas.drawPath(p, mPaint);
		}
	}

	private float mX, mY;
	private static final float TOUCH_TOLERANCE = 40;

	private void touch_start(float x, float y) {
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touch_move(float x, float y) {
		char gestureChar = 0;
		
		float idx = Math.abs(x - mX);
		float idy = Math.abs(y - mY);
		float dx = x - mX;
		float dy = y - mY;
		
		if (idx < TOUCH_TOLERANCE && idy < TOUCH_TOLERANCE) {
			return;
		}
		
		mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
		mX = x;
		mY = y;
		
		if (dx >= TOUCH_TOLERANCE){
			if (dy >= TOUCH_TOLERANCE){
				gestureChar = 'a';
			} else if (dy <= -1 * TOUCH_TOLERANCE){
				gestureChar = 'g';
			} else {
				gestureChar = 'h';
			}
		} else if (dx <= -1 * TOUCH_TOLERANCE){
			if (dy >= TOUCH_TOLERANCE){
				gestureChar = 'c';
			} else if (dy <= -1 * TOUCH_TOLERANCE){
				gestureChar = 'e';
			} else {
				gestureChar = 'd';
			}
		} else {
			if (dy >= TOUCH_TOLERANCE){
				gestureChar = 'b';
			} else if (dy <= -1 * TOUCH_TOLERANCE){
				gestureChar = 'f';
			}
		}
		try {
			txtGesture.getText().append(gestureChar);
		} catch (Exception e){
			Log.e("Game4", e.getMessage());
		}
	}

	private void touch_up() {
		mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mCanvas.drawPath(mPath, mPaint);
		// kill this so we don't double draw
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
