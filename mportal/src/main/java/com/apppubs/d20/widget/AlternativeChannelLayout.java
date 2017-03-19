package com.apppubs.d20.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

import com.apppubs.d20.R;

/**
 * 流布局 
 *
 */
public class AlternativeChannelLayout extends ViewGroup {
	
	private final int ITEM_STATE_NORMAL = 0;
	private final int ITEM_STATE_PRESSED = 1;
	
	private OnItemClickListener onItemClickListener;
	private int colCount, itemWidth = 100, itemHeight = 50,itemPadding = 10, dpi;
	private int hspace = 10;
	private int vspace = 40;
	private int dipW;//
	private float childRatio = 0.8f;
	private int lastX,lastY;
	private OnRemoveListener onRemoveListener;
	public OnRemoveListener getOnRemoveListener() {
		return onRemoveListener;
	}
	public void setOnRemoveListener(OnRemoveListener onRemoveListener) {
		this.onRemoveListener = onRemoveListener;
	}

	public interface OnRemoveListener{
		void onRemove(View view,int index);
	}
	public AlternativeChannelLayout(Context context) {
		super(context);
		initialize(context);
	}
	public AlternativeChannelLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray t = context.obtainStyledAttributes(attrs,
				R.styleable.AlternativeChannelLayout, 0, 0);
//		hspace = t.getDimensionPixelSize(R.styleable.AlternativeChannelLayout_hspace, hspace);
//		vspace = t.getDimensionPixelSize(R.styleable.AlternativeChannelLayout_vspace, vspace);
		itemHeight = t.getDimensionPixelSize(R.styleable.AlternativeChannelLayout_child_height, itemHeight);
		itemWidth = t.getDimensionPixelSize(R.styleable.AlternativeChannelLayout_child_width, itemWidth);
		itemPadding = t.getDimensionPixelSize(R.styleable.AlternativeChannelLayout_child_padding, itemPadding);
		t.recycle();
		initialize(context);
	}

	public AlternativeChannelLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void initialize(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		dpi = metrics.densityDpi;
	}
	
	private int curIndex =0;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("Alter","onTouchEvent"+event.getAction());
		
		
		Log.v("DraggableGridView","onTouch"+event.getY());
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			int i = getIndexFromCoor(lastX, lastY);
			if(i!=-1){
				curIndex = i;
				changeState( i, ITEM_STATE_PRESSED);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			int delta = lastY - (int) event.getY();
			float deltX = event.getX()-lastX;
			float deltY = event.getY()-lastY;
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			break;
		case MotionEvent.ACTION_UP:
			if(curIndex!=-1){
				changeState(curIndex, ITEM_STATE_NORMAL);
				click(curIndex);
				curIndex = -1;
			}
			break;
		}
		
		return true;
	}
	
	
	
	/**
	 * 
	 * @param index
	 * @param state STATE_NORMAL=0 STATE_PRESSED=1 STATE_DRAGGED = 2
	 */
	private void changeState(int index,int state){
		View v = getChildAt(index);
		if(state==ITEM_STATE_NORMAL){
			
			v.setBackgroundResource(R.drawable.channel_item_bg);
		}else if(state==ITEM_STATE_PRESSED){
			
			v.setBackgroundResource(R.drawable.channel_item_bg_h);
		}
	}
	
	
	private void click(int index){
		
		if(onItemClickListener!=null){
			onItemClickListener.onItemClick(null, getChildAt(index), index, index);
		}
	}
	// This is very basic
	// doesn't take into account padding
	// You can easily modify it to account for padding
	private boolean once = true;
		@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			
			
			int widthMode = MeasureSpec.getMode(widthMeasureSpec);
			int widthSize = MeasureSpec.getSize(widthMeasureSpec);
			int heightMode = MeasureSpec.getMode(heightMeasureSpec);
			int heightSize = MeasureSpec.getSize(heightMeasureSpec);
			Log.v("Alter","onMeasure: width:"+widthSize+"height:"+heightSize);
			
			int width = 0;
			int height = 0;
			if (widthMode == MeasureSpec.EXACTLY) {
				width = widthSize;
			}
			if(heightMode==MeasureSpec.EXACTLY){
				height = heightSize;
			}
	    	float w = widthSize-getPaddingLeft()-getPaddingRight();
	    	int lastW = 0;
	    	colCount = 0;
	    	w -= itemWidth;
	    	 while (w > 0)
	         {
	         	lastW = (int) w;//留下了最后一个>0的宽度也就是右边最后剩余的空白宽度，用于增加左边padding居中显示整个界面
	         	colCount++;
	         	w -= (itemWidth+itemPadding);
	         }
	    	 if(once){
	    		 setPadding(getPaddingLeft()+(int)lastW/2, getPaddingTop(), (int)lastW/2, getPaddingBottom());
	    		 once = false;
	    	 }
	    	 int count = getChildCount();
	    	 for(int i=-1;++i<count;){
	    		 
	    		 getChildAt(i).measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
	    	 }
	         setMeasuredDimension(widthSize, 500);
    	
	};

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {

		// Call layout() on children
		int numOfChildren = this.getChildCount();
		for (int i = 0; i < numOfChildren; i++) {
			Point xy = getCoorFromIndex(i);
            getChildAt(i).layout(xy.x, xy.y, xy.x + itemWidth, xy.y + itemHeight);
		}
		
	}
	
	@Override
	public void removeViewAt(int index) {
		View view = getChildAt(index);
		if(onRemoveListener!=null){
			onRemoveListener.onRemove(view,index);
		}
		super.removeViewAt(index);
		
	}
    public int getIndexFromCoor(int x, int y)
    {
        int col = getColFromCoor(x), row = getRowFromCoor(y); 
        if (col == -1 || row == -1) //touch is between columns or rows
            return -1;
        int index = row * colCount + col;
        if (index >= getChildCount())
            return -1;
        return index;
    }
    
    protected int getColFromCoor(int coor)
    {
        coor -= getPaddingLeft();
        for (int i = 0; coor > 0; i++)
        {
            if (coor < itemWidth)
                return i;
            coor -= (itemWidth + itemPadding);
        }
        return -1;
    }
    
    private int getRowFromCoor(int y){
    	 y -= getPaddingTop();
         for (int i = 0; y > 0; i++)
         {
             if (y < itemHeight)
                 return i;
             y -= (itemHeight + itemPadding);
         }
         return -1;
    }
	// *********************************************************
		// Layout Param Support
		// *********************************************************
    
    protected Point getCoorFromIndex(int index)
    {
    	if(colCount<=0)
    		return new Point(0, 0);
        int col = index % colCount;
        int row = index / colCount;
        return new Point(getPaddingLeft() + (itemWidth+itemPadding) * col,
        		getPaddingTop() + (itemHeight+itemPadding) * row);
    }
	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new AlternativeChannelLayout.LayoutParams(getContext(), attrs);
	}

	@Override
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}

	@Override
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return new LayoutParams(p);
	}

	// Override to allow type-checking of LayoutParams.
	@Override
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p instanceof AlternativeChannelLayout.LayoutParams;
	}

	// *********************************************************
	// Custom Layout Definition
	// *********************************************************
	public static class LayoutParams extends ViewGroup.MarginLayoutParams {
		public int spacing = -1;
		public int x = 0;
		public int y = 0;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
//			TypedArray a = c.obtainStyledAttributes(attrs,
//					R.styleable.AlternativeChannelLayout_Layout);
//			spacing = a.getDimensionPixelSize(
//					R.styleable.AlternativeChannelLayout_Layout_layout_space, 0);
//			a.recycle();
		}

		public LayoutParams(int width, int height) {
			super(width, height);
			spacing = 0;
		}

		public LayoutParams(ViewGroup.LayoutParams p) {
			super(p);
		}

		public LayoutParams(MarginLayoutParams source) {
			super(source);
		}
	}// eof-layout-params

	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}
	
	
}// eof-class