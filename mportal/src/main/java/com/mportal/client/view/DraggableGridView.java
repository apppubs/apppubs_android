//TO DO:
//
// - improve timer performance (especially on Eee Pad)
// - improve child rearranging

package com.mportal.client.view;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;

import com.mportal.client.R;
import com.mportal.client.util.LogM;

public class DraggableGridView extends ViewGroup implements View.OnTouchListener, View.OnClickListener, View.OnLongClickListener {
	
	private final int ITEM_STATE_NORMAL = 0;
	private final int ITEM_STATE_PRESSED = 1;
	private final int ITEM_STATE_DRAGGED = 2;
	//layout vars
	private float childRatio = .8f;
	
    protected int colCount, itemWidth, itemHeight,itemPadding, dpi, scroll = 0;
    protected float lastDelta = 0;
    protected Handler handler = new Handler();
    //dragging vars
    protected int dragged = -1, lastX = -1, lastY = -1, lastTarget = -1;
    protected boolean enabled = true, touching = false;
    //anim vars
    public static int animT = 150;
    protected ArrayList<Integer> newPositions = new ArrayList<Integer>();
    //listeners
    protected OnRearrangeListener onRearrangeListener;
    protected OnClickListener secondaryOnClickListener;
    private OnItemClickListener onItemClickListener;
    
    //CONSTRUCTOR AND HELPERS
    public DraggableGridView (Context context, AttributeSet attrs) {
        super(context, attrs);
        
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DraggableGridView);
        itemHeight = (int) ta.getDimension(R.styleable.DraggableGridView_item_height, 10f);
        itemWidth = (int) ta.getDimension(R.styleable.DraggableGridView_item_width, 50f);
        itemPadding = (int) ta.getDimension(R.styleable.DraggableGridView_item_padding, 10f);
        ta.recycle();
        setListeners();
//        handler.removeCallbacks(updateTask);
//        handler.postAtTime(updateTask, SystemClock.uptimeMillis() + 500);
        setChildrenDrawingOrderEnabled(true);

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		dpi = metrics.densityDpi;
    }
    protected void setListeners()
    {
    	setOnTouchListener(this);
    	super.setOnClickListener(this);
        setOnLongClickListener(this);
    }
    @Override
    public void setOnClickListener(OnClickListener l) {
    	secondaryOnClickListener = l;
    }
//    protected Runnable updateTask = new Runnable() {
//		public void run()
//        {
//            if (dragged != -1)
//            {
//            	if (lastY < itemPadding * 3 && scroll > 0)
//            		scroll -= 20;
//            	else if (lastY > getBottom() - getTop() - (itemPadding * 3) && scroll < getMaxScroll())
//            		scroll += 20;
//            }
//            else if (lastDelta != 0 && !touching)
//            {
//            	scroll += lastDelta;
//            	lastDelta *= .9;
//            	if (Math.abs(lastDelta) < .25)
//            		lastDelta = 0;
//            }
////            clampScroll();
////            layout( getLeft(), getTop(), getRight(), getBottom());
//            handler.postDelayed(this, 25);
//        }
//    };
    
    @Override
    public void addView(View child) {
    	
    	super.addView(child);
    	newPositions.add(-1);
    	if(onRearrangeListener!=null)
    		onRearrangeListener.onAdd();
    };
    @Override
    public void removeViewAt(int index) {
    	View view = getChildAt(index);
    	if(onRearrangeListener!=null)
    		onRearrangeListener.onRemove(view,index);
    	super.removeViewAt(index);
    	newPositions.remove(index);
    };
    
    private int spaceWidth = 0;//右边空白的尺寸
    private boolean once = true;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		
		int width = 0;
		int height;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		}
    	float w = widthSize-getPaddingLeft()-getPaddingRight();
    	System.out.println(width);
    	int lastW = 0;
    	colCount = 0;
    	w -= itemWidth;
    	 while (w > 0)
         {
         	lastW = (int) w;//留下了最后一个>0的宽度也就是右边最后剩余的空白宽度，用于增加左边padding居中显示整个界面
         	colCount++;
         	w -= (itemWidth+itemPadding);
         }
    	 int count = getChildCount();
    	 for(int i=-1;++i<count;){
    		 
    		 getChildAt(i).measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
    	 }
    	 if(once){
    		 setPadding(getPaddingLeft()+(int)lastW/2, getPaddingTop(), (int)lastW/2, getPaddingBottom());
    		 once = false;
    	 }
         setMeasuredDimension(width, heightSize);
         Log.v("Drag","onMeasure lastW/2:"+lastW/2);
    }
    
    //LAYOUT
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    	
    	
        for (int i = 0; i < getChildCount(); i++)
        	if (i != dragged)
        	{
	            Point xy = getCoorFromIndex(i);
	            getChildAt(i).layout(xy.x, xy.y, xy.x + itemWidth, xy.y + itemHeight);
	            newPositions.add(-1);
        	}
        
    }
    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
    	if (dragged == -1)
    		return i;
    	else if (i == childCount - 1)
    		return dragged;
    	else if (i >= dragged)
    		return i + 1;
    	return i;
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
    protected int getTargetFromCoor(int x, int y)
    {
        if (getColFromCoor(y + scroll) == -1) //touch is between rows
            return -1;
        //if (getIndexFromCoor(x, y) != -1) //touch on top of another visual
            //return -1;
        
        int leftPos = getIndexFromCoor(x - (itemWidth / 4), y);
        int rightPos = getIndexFromCoor(x + (itemWidth / 4), y);
        if (leftPos == -1 && rightPos == -1) //touch is in the middle of nowhere
            return -1;
        if (leftPos == rightPos) //touch is in the middle of a visual
        	return -1;
        
        int target = -1;
        if (rightPos > -1)
            target = rightPos;
        else if (leftPos > -1)
            target = leftPos + 1;
        if (dragged < target)
            return target - 1;
        
        //Toast.makeText(getContext(), "Target: " + target + ".", Toast.LENGTH_SHORT).show();
        return target;
    }
    protected Point getCoorFromIndex(int index)
    {
        int col = index % colCount;
        int row = index / colCount;
        return new Point(getPaddingLeft() + (itemWidth+itemPadding) * col,
        		getPaddingTop() + (itemHeight+itemPadding) * row);
    }
    public int getIndexOf(View child)
    {
    	for (int i = 0; i < getChildCount(); i++)
    		if (getChildAt(i) == child)
    			return i;
    	return -1;
    }
    
    //EVENT HANDLERS
    public void onClick(View view) {
    	if (enabled)
    	{
    		if (secondaryOnClickListener != null)
    			secondaryOnClickListener.onClick(view);
    		if (onItemClickListener != null && getLastIndex() != -1)
    			onItemClickListener.onItemClick(null, getChildAt(getLastIndex()), getLastIndex(), getLastIndex() / colCount);
    	}
    }
    public boolean onLongClick(View view)
    {
    	if (!enabled)
    		return false;
        int index = getLastIndex();
        if (index != -1)
        {
            dragged = index;
            changeState(index,ITEM_STATE_DRAGGED);
            return true;
        }
        return false;
    }
    int curIndex = -1;
	public boolean onTouch(View view, MotionEvent event)
    {
		Log.v("DraggableGridView","onTouch"+event.getY());
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			enabled = true;
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			touching = true;
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
			if (dragged != -1) {
				// change draw location of dragged visual
				Point center = move(dragged,deltX,deltY);

				// check for new target hover
				int target = getTargetFromCoor(center.x, center.y);
				if (lastTarget != target) {
					if (target != -1) {
						animateGap(target);
						lastTarget = target;
					}
				}
			} else {
//				scroll += delta;
				// clampScroll();
				if (Math.abs(delta) > 2)
					enabled = false;
//				layout(getLeft(), getTop(), getRight(), getBottom());
				requestLayout();
			}
			lastX = (int) event.getX();
			lastY = (int) event.getY();
			lastDelta = delta;
			break;
		case MotionEvent.ACTION_UP:
			LogM.log(this.getClass(), "MotionEvent.ACTION_UP dragged"+dragged);
			if (dragged != -1) {
				changeState(dragged, ITEM_STATE_NORMAL);
				View v = getChildAt(dragged);
				if (lastTarget != -1)
					reorderChildren();
				else {
					Point xy = getCoorFromIndex(dragged);
					v.layout(xy.x, xy.y, xy.x + itemWidth, xy.y + itemHeight);
				}
				v.clearAnimation();
//				if (v instanceof T)
//				((TextView) v).setAlpha(255);
				lastTarget = -1;
				dragged = -1;
			}
			if(curIndex!=-1){
				
				changeState(curIndex, ITEM_STATE_NORMAL);
				curIndex = -1;
			}
			touching = false;
			break;
		}
		if (dragged != -1)
			return true;
		return false;
    }
    
	/**
	 * 移动某一个子元素，返回当前元素的中心点坐标
	 * @param index
	 * @param dx
	 * @param dy
	 * @return
	 */
	@SuppressLint("NewApi")
	private Point move(int index,float dx,float dy){
		View movedV = getChildAt(index);
		float newX = movedV.getX()+dx;
		float newY = movedV.getY()+dy;
		float width = itemWidth * 3 / 2;
		float height = itemHeight * 3 / 2;
		movedV.layout((int)newX,(int)newY,(int)(newX+width) ,(int)(newY+height));
		Point p = new Point((int)(newX+width/2), (int)(newY+height/2));
		return p;
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
		}else if(state==ITEM_STATE_DRAGGED){
	    	int x = getCoorFromIndex(dragged).x + itemWidth / 2, y = getCoorFromIndex(dragged).y + itemHeight / 2;
	        int l = x - (3 * itemWidth / 4), t = y - (3 * itemHeight / 4);
	    	v.layout(l, t, l + (itemWidth * 3 / 2), t + (itemHeight * 3 / 2));
	    	AnimationSet animSet = new AnimationSet(true);
			ScaleAnimation scale = new ScaleAnimation(.667f, 1, .667f, 1, itemWidth * 3 / 4, itemHeight * 3 / 4);
			scale.setDuration(animT);
			AlphaAnimation alpha = new AlphaAnimation(1, .5f);
			alpha.setDuration(animT);

			animSet.addAnimation(scale);
			animSet.addAnimation(alpha);
			animSet.setFillEnabled(true);
			animSet.setFillAfter(true);
			
			v.clearAnimation();
			v.startAnimation(animSet);
		}
	}
    protected void animateGap(int target)
    {
    	for (int i = 0; i < getChildCount(); i++)
    	{
    		View v = getChildAt(i);
    		if (i == dragged)
	    		continue;
    		int newPos = i;
    		if (dragged < target && i >= dragged + 1 && i <= target)
    			newPos--;
    		else if (target < dragged && i >= target && i < dragged)
    			newPos++;
    		
    		//animate
    		int oldPos = i;
    		if (newPositions.get(i) != -1)
    			oldPos = newPositions.get(i);
    		if (oldPos == newPos)
    			continue;
    		
    		Point oldXY = getCoorFromIndex(oldPos);
    		Point newXY = getCoorFromIndex(newPos);
    		Point oldOffset = new Point(oldXY.x - v.getLeft(), oldXY.y - v.getTop());
    		Point newOffset = new Point(newXY.x - v.getLeft(), newXY.y - v.getTop());
    		
    		TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, oldOffset.x,
																  Animation.ABSOLUTE, newOffset.x,
																  Animation.ABSOLUTE, oldOffset.y,
																  Animation.ABSOLUTE, newOffset.y);
			translate.setDuration(animT);
			translate.setFillEnabled(true);
			translate.setFillAfter(true);
			v.clearAnimation();
			v.startAnimation(translate);
    		
			newPositions.set(i, newPos);
    	}
    }
	protected void reorderChildren()
    {
        //FIGURE OUT HOW TO REORDER CHILDREN WITHOUT REMOVING THEM ALL AND RECONSTRUCTING THE LIST!!!
    	if (onRearrangeListener != null)
    		onRearrangeListener.onRearrange(dragged, lastTarget);
        ArrayList<View> children = new ArrayList<View>();
        for (int i = 0; i < getChildCount(); i++)
        {
        	getChildAt(i).clearAnimation();
            children.add(getChildAt(i));
        }
        removeAllViews();
        while (dragged != lastTarget)
            if (lastTarget == children.size()) // dragged and dropped to the right of the last element
            {
                children.add(children.remove(dragged));
                dragged = lastTarget;
            }
            else if (dragged < lastTarget) // shift to the right
            {
                Collections.swap(children, dragged, dragged + 1);
                dragged++;
            }
            else if (dragged > lastTarget) // shift to the left
            {
                Collections.swap(children, dragged, dragged - 1);
                dragged--;
            }
        for (int i = 0; i < children.size(); i++)
        {
        	newPositions.set(i, -1);
            addView(children.get(i));
        }
//        layout( getLeft(), getTop(), getRight(), getBottom());
        requestLayout();
    }
    public void scrollToTop()
    {
    	scroll = 0;
    }
    public void scrollToBottom()
    {
    	scroll = Integer.MAX_VALUE;
//    	clampScroll();
    }
//    protected void clampScroll()
//    {
//    	int stretch = 3, overreach = getHeight() / 2;
//    	int max = getMaxScroll();
//    	max = Math.max(max, 0);
//    	
//    	if (scroll < -overreach)
//    	{
//    		scroll = -overreach;
//    		lastDelta = 0;
//    	}
//    	else if (scroll > max + overreach)
//    	{
//    		scroll = max + overreach;
//    		lastDelta = 0;
//    	}
//    	else if (scroll < 0)
//    	{
//	    	if (scroll >= -stretch)
//	    		scroll = 0;
//	    	else if (!touching)
//	    		scroll -= scroll / stretch;
//    	}
//    	else if (scroll > max)
//    	{
//    		if (scroll <= max + stretch)
//    			scroll = max;
//    		else if (!touching)
//    			scroll += (max - scroll) / stretch;
//    	}
//    }
    protected int getMaxScroll()
    {
    	int rowCount = (int)Math.ceil((double)getChildCount()/colCount), max = rowCount * itemWidth + (rowCount + 1) * itemPadding - getHeight();
    	return max;
    }
    public int getLastIndex()
    {
    	return getIndexFromCoor(lastX, lastY);
    }
    
    //OTHER METHODS
    public void setOnRearrangeListener(OnRearrangeListener l)
    {
    	this.onRearrangeListener = l;
    }
    public void setOnItemClickListener(OnItemClickListener l)
    {
    	this.onItemClickListener = l;
    }
    
	
	public interface OnRearrangeListener {
		
		public abstract void onRearrange(int oldIndex, int newIndex);
		public void onRemove(View view,int index);
		public void onAdd();
}

}