package com.apppubs.d20.widget.commonlist;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;

public abstract class CommonAbsListView extends AbsListView{

	public CommonAbsListView(Context context) {
		super(context);
	}
	public CommonAbsListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public abstract void addHeaderView(View view);
	public abstract void addFooterView(View view);

}
