package com.apppubs.d20.page;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by zhangwen on 2017/9/29.
 */

public class PageGridView extends FrameLayout{


	private GridLayout mGridLayout;
	public PageGridView(Context context) {
		super(context);
		initView();
	}

	public PageGridView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView(){
		mGridLayout = new GridLayout(getContext());
		mGridLayout.setBackgroundColor(Color.WHITE);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		this.addView(mGridLayout, lp1);
	}

	public void setItems(List<GrideViewItem> items){
		if (items==null||items.size()<1){
			return ;
		}
		mGridLayout.setColumnCount(items.size());
//		WindowManager wm = mHostActivity.getWindowManager();
//		int width = wm.getDefaultDisplay().getWidth();

		for (int i = -1; ++i < items.size();) {
			GrideViewItem item = items.get(i);
//			GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
//			glp.width = (width-padding*2) / gl.getColumnCount();
//			glp.setGravity(Gravity.FILL);
			RelativeLayout rl = (RelativeLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_menu_gv, null);
			View verticalLine = rl.findViewById(R.id.vertical_line);
			verticalLine.setVisibility(View.GONE);
			TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
			tv.setText(item.getTitle());
			ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
//			mImageLoader.displayImage(item.getString("picurl"), iv);
//			rl.setOnClickListener(this);
//			rl.setTag(item.getString("url"));
			mGridLayout.addView(rl);

		}
	}

	class GrideViewItem{
		private String title;
		private String picUrl;
		private String action;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPicUrl() {
			return picUrl;
		}

		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}

		public String getAction() {
			return action;
		}

		public void setAction(String action) {
			this.action = action;
		}
	}
}
