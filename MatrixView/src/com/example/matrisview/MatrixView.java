package com.example.matrisview;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 
 * @author Doracoin
 *
 */
public class MatrixView extends View {
	private static final String tag = "MatrixView";
	private MatrixCallback mCallback;
	private Paint backPaint;
	private Paint middlePaint;
	private Paint linePaint;
	private int select_color = Color.argb(200, 255, 255, 255);
	private int normal_color = Color.argb(100, 0, 0, 0);
	private float moving_X = -1;
	private float moving_Y = -1;
	
	private boolean isFirst = true;
	
	private float back_width = 50;
	private float select_width = 80;
	
	private int mSize = 3;
	
	private int keys_margin = 300;
	
	private Position[] position_keys ;
	private ArrayList<Integer> sequences = new ArrayList<Integer>();
	
	public MatrixView(Context context) {
		this(context, null);
	}
	
	public MatrixView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MatrixView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPaint();
		setBackgroundColor(Color.parseColor("#909090"));
	}
	
	private void initPaint(){
		if(backPaint==null){
			backPaint = new Paint();
			backPaint.setARGB(100, 0, 0, 0);
			backPaint.setStyle(Style.FILL);
			backPaint.setAntiAlias(true);
		}
		if(middlePaint==null){
			middlePaint = new Paint();
			middlePaint.setARGB(255, 255, 255, 255);
			middlePaint.setStyle(Style.FILL);
			middlePaint.setAntiAlias(true);
		}
		if(linePaint==null){
			linePaint = new Paint();
			linePaint.setColor(Color.parseColor("#505050"));
			linePaint.setStrokeWidth(10);
			linePaint.setAntiAlias(true);
		}
	}
	
	private void initPositions(int size) {
		int width = getWidth();
		int height = getHeight();
		if(width>height){
			keys_margin = height/(size+1);
		}else if (width<height){
			keys_margin = width/(size+1);
		}else {
			keys_margin = width/(size+1);
		}
		int count = size * size;
		position_keys = new Position[count];
		for (int i = 0; i < count; i++) {
			int line = (i / size);
//			int list = (i<size?i:(i%size));
			int list = i % size;
			Position p = new Position(keys_margin + keys_margin * list, keys_margin + keys_margin * line, i);
			position_keys[i] = p;
		}
	}
	
	public void setSize(int size) {
		mSize = size;
		if (size < 3) {
			mSize = 3;
		}
		if (size > 5) {
			mSize = 5;
		}
		initPositions(mSize);
		postInvalidate();
	}
	
	public int getSize(){
		return mSize;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.e(tag, "width: "+getWidth()+" height: "+getHeight());
		if(isFirst){
			isFirst = false;
			initPositions(mSize);
		}
		updateLine(canvas);
		updateFinger(canvas);
		updatePosition(canvas);
	}
	
	private void updatePosition(Canvas canvas){
		for (int i = 0; i < position_keys.length; i++) {
			if (position_keys[i].selected) {
				backPaint.setColor(select_color);
				canvas.drawCircle(position_keys[i].mX, position_keys[i].mY, select_width, backPaint);
			} else {
				backPaint.setColor(normal_color);
				canvas.drawCircle(position_keys[i].mX, position_keys[i].mY, back_width, backPaint);
			}
			canvas.drawCircle(position_keys[i].mX, position_keys[i].mY, 15, middlePaint);
		}
	}
	
	private void updateLine(Canvas canvas){
		if (sequences.size() > 1
				&& sequences.size() <= mSize * mSize) {
			for (int i = 0; i < sequences.size(); i++) {
				if (i == 0) {
					continue;
				}
				canvas.drawLine(position_keys[sequences.get(i - 1)].mX,
						position_keys[sequences.get(i - 1)].mY,
						position_keys[sequences.get(i)].mX,
						position_keys[sequences.get(i)].mY, linePaint);
			}
		}
	}
	
	private void updateFinger(Canvas canvas){
		if(sequences.size()>0){
			canvas.drawLine(position_keys[sequences.get(sequences.size()-1)].mX, position_keys[sequences.get(sequences.size()-1)].mY, 
					moving_X, moving_Y, linePaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (mCallback != null) {
				byte[] p = new byte[sequences.size()];
				for(int i=0;i<sequences.size();i++){
					p[i] = (byte) sequences.get(i).intValue();
				}
				mCallback.onFinish(p);
			}
			clear();
			return super.onTouchEvent(event);
		}
		moving_X = event.getX();
		moving_Y = event.getY();
		match(moving_X, moving_Y);
		postInvalidate();
		return true;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
//		if(View.VISIBLE==visibility){
//			int width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//
//			int height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//
//			measure(width,height);
//
//			height=getMeasuredHeight(); 
//
//			width=getMeasuredWidth();
//			
//			setSize(mSize);
//			initPaint();
//			setBackgroundColor(Color.GRAY);
//			invalidate();
//		}
	}
	
	private boolean match(float x, float y) {
		for (int i = 0; i < position_keys.length; i++) {
			if (Math.abs(x - position_keys[i].mX) <= select_width
					&& Math.abs(y - position_keys[i].mY) <= select_width) {
				if(!position_keys[i].selected){
					sequences.add(position_keys[i].number);
					position_keys[i].selected = true;
					return true;
				}
			}
		}
		return false;
	}
	
	private void clear(){
		sequences.clear();
		for(int i=0;i<position_keys.length;i++){
			position_keys[i].selected = false;
		}
		postInvalidate();
	}
	
	public void setOnFinishListener(MatrixCallback finishCallback){
		mCallback = finishCallback;
	}
	
	private class Position{
		private float mX = -1;
		private float mY = -1;
		private int number = -1;
		private boolean selected = false;
		
		private Position(){}
		
		private Position(float x, float y, int num) {
			mX = x;
			mY = y;
			number = num;
		}
	}
	
	public interface MatrixCallback{
		void onFinish(byte[] password);
	}
	
}
