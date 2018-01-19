package com.apppubs.d20.widget;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class HotAreaView extends ImageView {

	private List<HotArea> mHotAreas;
	private List<TextView> mTextHotAreas;// 文本热区集合
	private HotAreaClickListener mListener;
	private Paint mPaint;
	private int mPicWidth;
	private float mPicAndViewRatio;//图片尺寸比view尺寸
	private GestureDetector mLongGestureDetector;

	public HotAreaView(Context context) {
		super(context);
		init(context);
	}

	public HotAreaView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public interface HotAreaClickListener {
		void onItemClick(int index, HotArea hotArea);
		void onItemLongClick(int index,HotArea hotArea);
	}

	private void init(Context context) {
		mTextHotAreas = new ArrayList<TextView>();
		mPaint = new Paint();

		mLongGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener() {
			public void onLongPress(MotionEvent event) {
				Log.e("", "Longpress detected");
				int result = clickhotAreaCheck((int) event.getX(), (int) event.getY());
				if (result!=-1&&mListener != null) {
					mListener.onItemLongClick(result, mHotAreas.get(result));
				}
			}

			@Override
			public boolean onSingleTapUp(MotionEvent event) {
				int result = clickhotAreaCheck((int) event.getX(), (int) event.getY());
				if (result!=-1&&mListener != null) {
					mListener.onItemClick(result, mHotAreas.get(result));
				}
				return super.onSingleTapUp(event);
			}
		});
	}

	public List<HotArea> getHotAreas() {
		return mHotAreas;
	}

	public void setHotAreas(List<HotArea> hotAreas) {
		this.mHotAreas = hotAreas;
		this.invalidate();
	}

	public int getPicWidth() {
		return mPicWidth;
	}

	public void setPicWidth(int picWidth) {
		this.mPicWidth = picWidth;
	}

	public HotAreaClickListener getListener() {
		return mListener;
	}

	public void setListener(HotAreaClickListener listener) {
		this.mListener = listener;
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v(this.getClass().getName(),
				"触摸pdfview action:" + event.getAction() + "maskedAction:" + event.getActionMasked());
		mLongGestureDetector.onTouchEvent(event);
		return true;
	}

	/**
	 * 检测是否点击到热区
	 * 
	 * @param posx
	 * @param posy
	 * @return
	 */
	private int clickhotAreaCheck(int posx, int posy) {

		for (int i = -1; ++i < mHotAreas.size();) {
			HotArea hotArea = mHotAreas.get(i);
			if (hotArea.getShape().equals(HotArea.SHAPE_RECT)) {
				String[] points = hotArea.getCoords().split(",");
				Rect rect = new Rect(Integer.parseInt(points[0]), Integer.parseInt(points[1]),
						Integer.parseInt(points[2]), Integer.parseInt(points[3]));
				if (rect.contains((int) (posx * mPicAndViewRatio), (int) (posy * mPicAndViewRatio))) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPicAndViewRatio = mPicWidth / (float) getWidth();
		if(null!=mHotAreas){
			drawHotAreas(canvas);
		}
	}

	private void drawHotAreas(Canvas canvas) {
		for(HotArea ha:mHotAreas){
			String type = ha.getType();
			if(HotArea.TYPE_TEXT.equals(type)){
				drawText(canvas, ha);
			}else if(HotArea.TYPE_IMAGE.equals(type)){
				drawBitmap(canvas, ha);
			}else if(HotArea.TYPE_BADGE.equals(ha.getType())){
				if (!TextUtils.isEmpty(ha.getText())&&!"0".equals(ha.getText())){
					drawText(canvas, ha);
				}
			}
			
		}
	}

	private void drawText(Canvas canvas, HotArea ha) {
		if(HotArea.SHAPE_CIRCLE.equals(ha.getShape())){
			mPaint.setColor(ha.getBgColor());
			mPaint.setAntiAlias(true);
			String[] coords = ha.getCoords().split(",");
			float cx = Integer.parseInt(coords[0])/mPicAndViewRatio;
			float cy = Integer.parseInt(coords[1])/mPicAndViewRatio;
			float radius = Integer.parseInt(coords[2])/mPicAndViewRatio;
			canvas.drawCircle(cx, cy,radius, mPaint);
			if(!TextUtils.isEmpty(ha.getText())){
				mPaint.setColor(ha.getTextColor());
				mPaint.setTextAlign(Paint.Align.CENTER);
				float textSize = ha.getTextSize()/mPicAndViewRatio;
				mPaint.setTextSize(textSize);//设置字体大小
				//如果设置绘制方式为居中，则指定的x坐标为文字中间位置，y坐标为文字baseline位置 y=(行高-字体高度)/2+字体高度
				FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();  
				Rect targetRect = new Rect((int)(cx-radius),(int)(cy-radius), (int)(cx+radius), (int)(cy+radius));
			    int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
				canvas.drawText(ha.getText(), cx, baseline, mPaint);
			}
		}else if(HotArea.SHAPE_RECT.equals(ha.getShape())){
			mPaint.setColor(ha.getBgColor());
			mPaint.setAntiAlias(true);
			String[] coords = ha.getCoords().split(",");
			int sx = (int) (Integer.parseInt(coords[0])/mPicAndViewRatio);
			int sy = (int) (Integer.parseInt(coords[1])/mPicAndViewRatio);
			int ex = (int) (Integer.parseInt(coords[2])/mPicAndViewRatio);
			int ey = (int) (Integer.parseInt(coords[3])/mPicAndViewRatio);
			Rect targetRect = new Rect(sx, sy, ex, ey);
			canvas.drawRect(targetRect, mPaint);
			if(!TextUtils.isEmpty(ha.getText())){
				mPaint.setColor(ha.getTextColor());
				mPaint.setTextAlign(Paint.Align.CENTER);
				float textSize = ha.getTextSize()/mPicAndViewRatio;
				mPaint.setTextSize(textSize);//设置字体大小
				//如果设置绘制方式为居中，则指定的x坐标为文字中间位置，y坐标为文字baseline位置 y=(行高-字体高度)/2+字体高度
				FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();  
			    int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
				if (HotArea.TEXT_ALIGN_LEFT.equals(ha.getTextAlign())){
					float width = mPaint.measureText(ha.getText());
					canvas.drawText(ha.getText(),sx+width/2,baseline,mPaint);
				}else if(HotArea.TEXT_ALIGN_RIGHT.equals(ha.getTextAlign())){
					float width = mPaint.measureText(ha.getText());
					canvas.drawText(ha.getText(),ex-width/2,baseline,mPaint);
				}else{
					canvas.drawText(ha.getText(),sx+(ex-sx)/2, baseline, mPaint);
				}
			}
		}
	}
	
	private void drawBitmap(Canvas canvas, HotArea ha){
		if (ha.getImage()==null){
			return;
		}
		if(HotArea.SHAPE_CIRCLE.equals(ha.getShape())){
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.TRANSPARENT);
			mPaint.setStyle(Style.STROKE);
			mPaint.setFilterBitmap(true);
			mPaint.setDither(true);
			String[] coords = ha.getCoords().split(",");
			float cx = Integer.parseInt(coords[0])/mPicAndViewRatio;
			float cy = Integer.parseInt(coords[1])/mPicAndViewRatio;
			float radius = Integer.parseInt(coords[2])/mPicAndViewRatio;
			float scaleX = radius*2/ha.getImage().getWidth();
			float scaleY = radius*2/ha.getImage().getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale(scaleX, scaleY);
			matrix.postTranslate(cx-radius, cy-radius);
			canvas.drawBitmap(ha.getImage(), matrix, null);
			mPaint.reset();
		}else if(HotArea.SHAPE_RECT.equals(ha.getShape())){
			mPaint.setAntiAlias(true);
			mPaint.setColor(Color.TRANSPARENT);
			mPaint.setStyle(Style.STROKE);
			mPaint.setFilterBitmap(true);
			mPaint.setDither(true);
			String[] coords = ha.getCoords().split(",");
			float x1 = Integer.parseInt(coords[0])/mPicAndViewRatio;
			float y1 = Integer.parseInt(coords[1])/mPicAndViewRatio;
			float x2 = Integer.parseInt(coords[2])/mPicAndViewRatio;
			float y2 = Integer.parseInt(coords[3])/mPicAndViewRatio;
			float scaleX = (x2-x1)/ha.getImage().getWidth();
			float scaleY = (y2-y1)/ha.getImage().getHeight();
			Matrix matrix = new Matrix();
			matrix.postScale(scaleX, scaleY);
			matrix.postTranslate(x1, y1);
			canvas.drawBitmap(ha.getImage(), matrix, null);
			mPaint.reset();
		}
	}
	

}
