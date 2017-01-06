package com.mportal.client.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListView extends ListView {
  /**
   * 用于嵌套在由滑动的控件里面
   * @param context
   * @param attrs
   */
	public MyListView(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public MyListView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

	}

	public MyListView(Context context) {

		super(context);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		      int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, 

		                 MeasureSpec.AT_MOST); 
	       super.onMeasure(widthMeasureSpec, expandSpec); 

	}
}
