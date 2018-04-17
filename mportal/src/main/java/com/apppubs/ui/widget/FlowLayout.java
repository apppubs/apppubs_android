package com.apppubs.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.apppubs.d20.R;

/**
 * 流布局 
 *
 */
public class FlowLayout extends ViewGroup {
	private int hspace = 10;
	private int vspace = 10;
//	private float offset;
	private int mColumnNum;
	public FlowLayout(Context context) {
		super(context);
		initialize(context);
	}

	public FlowLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray t = context.obtainStyledAttributes(attrs,
				R.styleable.FlowLayout, 0, 0);
		hspace = t.getDimensionPixelSize(R.styleable.FlowLayout_hspace, hspace);
		vspace = t.getDimensionPixelSize(R.styleable.FlowLayout_vspace, vspace);
		t.recycle();
		initialize(context);
	}
	public void setHspace(int hspace) {
		this.hspace = hspace;
	}
	
	public FlowLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	private void initialize(Context context) {
	}

	// This is very basic
	// doesn't take into account padding
	// You can easily modify it to account for padding
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// ********************
		// Initialize
		// ********************
		int rw = MeasureSpec.getSize(widthMeasureSpec);
		int rh = MeasureSpec.getSize(heightMeasureSpec);
		int h = 0; // current height
		int w = 0; // current width
		int h1 = 0, w1 = 0; // Current point to hook the child to
		// ********************
		// Loop through children
		// ********************
		int numOfChildren = this.getChildCount();
		int col = 0;
		int row = 1;
		for (int i = 0; i < numOfChildren; i++) {
			if(mColumnNum<++col){
				mColumnNum = col;
			}
			// ********************
			// Front of the loop
			// ********************
			View child = this.getChildAt(i);
			this.measureChild(child, widthMeasureSpec, heightMeasureSpec);
			int vw = child.getMeasuredWidth();
			int vh = child.getMeasuredHeight();
			if (w1 + vw+(col-1)*hspace +getPaddingLeft()+getPaddingRight()> rw) {
//				offset = rw-(getPaddingLeft()+getPaddingRight())-w1;
				col = 1;
				// new line: max of current width and current width position
				// when multiple lines are in play w could be maxed out
				// or in uneven sizes is the max of the right side lines
				// all lines don't have to have the same width
				// some may be larger than others
				w = Math.max(w, w1);
				// reposition the point on the next line
				w1 = 0; // start of the line
				h1 = h1 + vh; // add view height to the current height
				row++;
				
			}// ********************
				// Middle of the loop
				// ********************
			int w2 = 0, h2 = 0; // new point for the next view
			w2 = w1 + vw;
			h2 = h1;
			// latest height: current point + height of the view
			// however if the previous height is larger use that one
			h = Math.max(h, h1 + vh);
			// ********************
			// Save the current coords for the view
			// in its layout
			// ********************
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			lp.x = w1+getPaddingLeft() +(col-1)*hspace;
			lp.y = h1+getPaddingTop()+(row-1)*vspace;
			// ********************
			// Restart the loop
			// ********************
			w1 = w2;
			h1 = h2;
		}
		// ********************
		// End of for
		// ********************
		w = Math.max(w1, w);
		h = Math.max(h,h+hspace*2+getPaddingTop()+getPaddingBottom());
		// h = h;
		setMeasuredDimension(resolveSize(w, widthMeasureSpec), resolveSize(h, heightMeasureSpec));
	};

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// Call layout() on children
		int numOfChildren = this.getChildCount();
		for (int i = 0; i < numOfChildren; i++) {
			View child = this.getChildAt(i);
			LayoutParams lp = (LayoutParams) child.getLayoutParams();
			
			child.layout(lp.x, lp.y, lp.x+ child.getMeasuredWidth(), lp.y + child.getMeasuredHeight());
		}
	}// *********************************************************
		// Layout Param Support
		// *********************************************************

	@Override
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new FlowLayout.LayoutParams(getContext(), attrs);
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
		return p instanceof FlowLayout.LayoutParams;
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
			TypedArray a = c.obtainStyledAttributes(attrs,
					R.styleable.FlowLayout_Layout);
			spacing = a.getDimensionPixelSize(
					R.styleable.FlowLayout_Layout_layout_space, 0);
			a.recycle();
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
}// eof-class