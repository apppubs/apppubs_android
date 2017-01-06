package com.mportal.client.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mportal.client.R;
import com.mportal.client.util.Utils;

public class LetterListView extends View
{

	OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	String[] bLong = { "#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
	String[] bShort = { "#", "A", "•", "C", "•", "E", "•", "G", "•", "I", "•", "K", "•", "M", "•", "O", "•", "Q", "•", "S", "•", "U", "•", "W", "•", "Y", "Z" };
	int choose = -1;
	Paint paint = new Paint();
	boolean showBkg = false;

	public LetterListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		
	}

	public LetterListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public LetterListView(Context context)
	{
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (showBkg)
		{
			canvas.drawColor(Color.parseColor("#20000000"));
		}

		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / bLong.length;
		for (int i = 0; i < bLong.length; i++)
		{
			paint.setColor(getContext().getResources().getColor(R.color.common_text_gray));
			paint.setAntiAlias(true);
			paint.setTextSize(Utils.dip2px(getContext(), 12));
			
			if (i == choose)
			{
				paint.setColor(Color.parseColor("#3399ff"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(bLong[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(bLong[i], xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * bLong.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			showBkg = true;
			if (oldChoose != c && listener != null)
			{
				if (c > 0 && c < bLong.length)
				{
					listener.onTouchingLetterChanged(bLong[c]);
					choose = c;
					invalidate();
				}
			}

			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c && listener != null)
			{
				if (c > 0 && c < bLong.length)
				{
					listener.onTouchingLetterChanged(bLong[c]);
					choose = c;
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			showBkg = false;
			choose = -1;
			invalidate();
			break;
		}
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		return super.onTouchEvent(event);
	}

	public void setOnTouchingLetterChangedListener(OnTouchingLetterChangedListener onTouchingLetterChangedListener)
	{
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener
	{
		public void onTouchingLetterChanged(String s);
	}

}
