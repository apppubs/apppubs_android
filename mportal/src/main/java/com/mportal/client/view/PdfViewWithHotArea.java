package com.mportal.client.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.artifex.mupdfdemo.MuPDFCore;
import com.mportal.client.util.LogM;

/**
 * 可以显示pdf的view，并且识别热区。用于报纸预览阅读
 * @author sunyu
 *
 */
public class PdfViewWithHotArea extends FrameLayout{

	private Context mContext;
	private String mPdfPath;//pdf本地路径
	private MuPDFCore mCore;
	private ImageView mWindow;
	private ImageView mClickedArea;
	private Rect[] mHotAreaRects;
	private float mHotAreaBaseWidth;//hotArea基准宽度
	private float mHotAreaBaseHeight;//基准高度
	private PdfViewWithHotAreaListener mListener;
	private float mPdfRatio;//pdf的宽高比
	private float mDisplayRatio;//显示区域的宽高比
	private float mPdfDiaplayWidth,mPdfDiaplayHeight;
	private float mPdfOffsetX,mPdfOffsetY;
	private Bitmap mBitmap;
	private int mDisplayWidth,mDisplayHeight;
	public interface PdfViewWithHotAreaListener{
		
		void onHotAreaClicked(int pos);
		void onException(int errorCode);
	}
	
	public PdfViewWithHotArea(Context context) {
		this(context,null);
	}
	public PdfViewWithHotArea(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mWindow = new ImageView(context);
		mClickedArea = new ImageView(context);
		mClickedArea.setBackgroundColor(Color.parseColor("#40666666"));
		mWindow.setScaleType(ScaleType.CENTER_INSIDE);
		addView(mWindow);
		addView(mClickedArea,new LayoutParams(0, 0));
	}
	
	
	public void setHotAreas(Rect rects[]){
		mHotAreaRects = rects;

		mDisplayRatio = mDisplayWidth/(float)mDisplayHeight;
		if(mDisplayRatio>mPdfRatio){
			mPdfDiaplayHeight = mDisplayHeight;
			mPdfDiaplayWidth = mPdfDiaplayHeight*mPdfRatio;
			mPdfOffsetX = (mDisplayWidth-mPdfDiaplayWidth)/2;
		}else{
			mPdfDiaplayWidth = mDisplayHeight;
			mPdfDiaplayHeight = mPdfDiaplayWidth/mPdfRatio;
			mPdfOffsetY = (mDisplayHeight-mPdfDiaplayHeight)/2;
		}
		LogM.log(this.getClass(), "mPdfDiaplayWidth："+mPdfDiaplayWidth+"mPdfDiaplayHeight:"+mPdfDiaplayHeight+"width:"+getWidth()+"height:"+getHeight()+"offsetX:"+mPdfOffsetX+"offsetY:"+mPdfOffsetY+"mPdfRatio:"+mPdfRatio);
		//转换热区
		float scaleX = mPdfDiaplayWidth/mHotAreaBaseWidth;
		float scaleY = mPdfDiaplayHeight/mHotAreaBaseHeight;
		for(int i=-1;++i<mHotAreaRects.length;){
			Rect rect = mHotAreaRects[i];
			mHotAreaRects[i] = new Rect((int)((rect.left*scaleX)+mPdfOffsetX),(int)(rect.top*scaleY+mPdfOffsetY), (int)(rect.right*scaleX+mPdfOffsetX), (int)(rect.bottom*scaleY+mPdfOffsetY));
		}

	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mDisplayWidth = w;
		mDisplayHeight = h;
		
	}
	
	public void setListener(PdfViewWithHotAreaListener listner){
		mListener = listner;
	}
	
	/**
	 * 设置pdf路径此时开始渲染
	 * @param pdfPath
	 */
	public void setPdfPath(String pdfPath){
		mPdfPath = pdfPath;
		mCore = openFile(Uri.decode(mPdfPath));
		// 注意！！！每次打开file需要调用countPages方法才可以使用，
		if (mCore != null && mCore.countPages() == 0) {
			mCore = null;
		}
		if (mCore == null || mCore.countPages() == 0 || mCore.countPages() == -1) {
			if(mListener!=null){
				mListener.onException(0);
			}
		}else{
			
			render();
		}
		
	}
	
	public void setHotAreaBaseWidth(float width){
		this.mHotAreaBaseWidth = width;
	}
	public void setHotAreaBaseHeight(float height){
		this.mHotAreaBaseHeight = height;
	}
	/** 渲染视窗 */
	private void render() {
		if (mCore == null)
			return;
		mWindow.setVisibility(View.VISIBLE);
		PointF point = mCore.getPageSize(0);
		mPdfRatio = point.x/point.y;
		mCore.drawPage(mBitmap,0,  getWidth(), (int)(getWidth()/mPdfRatio), 0, 0, getWidth(), (int)(getWidth()/mPdfRatio),null);
		mWindow.setImageBitmap(mBitmap);
		
	}
	
	private MuPDFCore openFile(String path) {
		try {
			mCore = new MuPDFCore(mContext, path);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return mCore;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		Log.v(this.getClass().getName(), "触摸pdfview action:"+event.getAction()+"maskedAction:"+event.getActionMasked());
		int result = -1;
		if(event.getAction()==MotionEvent.ACTION_DOWN&&mHotAreaRects!=null&&(result = clickhotAreaCheck((int)event.getX(), (int)event.getY()))!=-1){
			Log.v(this.getClass().getName(), "点击到了第："+result+"个热区");
			
			mClickedArea.setVisibility(View.VISIBLE);
			LayoutParams lp = (LayoutParams) mClickedArea.getLayoutParams();
			Rect rect = mHotAreaRects[result];
			lp.width = rect.width();
			lp.height = rect.height();
			lp.setMargins(rect.left, rect.top, 0, 0);
			mClickedArea.setLayoutParams(lp);
		}
		if(event.getAction()==MotionEvent.ACTION_UP&&mHotAreaRects!=null&&(result = clickhotAreaCheck((int)event.getX(), (int)event.getY()))!=-1){
			Log.v(this.getClass().getName(), "松开时点击到了第："+result+"个热区");
		
			if(mListener!=null){
				mListener.onHotAreaClicked(result);
			}
			mClickedArea.setVisibility(View.GONE);
		}
		if(event.getAction()==MotionEvent.ACTION_CANCEL){
			mClickedArea.setVisibility(View.GONE);
		}
		
		return true;
	}
	
	/**
	 * 检测是否点击到热区
	 * @param posx
	 * @param posy
	 * @return
	 */
	private int clickhotAreaCheck(int posx,int posy){
		
		for(int i=-1;++i<mHotAreaRects.length;){
			if(mHotAreaRects[i].contains(posx, posy)){
				return i;
			}
		}
		return -1;
	}
	
	public void clean(){
		if(null!=mBitmap&&!mBitmap.isRecycled()){
			mBitmap.recycle();
			mBitmap = null;
		}
	}
	
	

}
