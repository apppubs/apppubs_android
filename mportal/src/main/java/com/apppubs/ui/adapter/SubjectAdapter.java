package com.apppubs.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.ui.news.NewsInfoActivity;

public class SubjectAdapter extends BaseAdapter {
	private Context context;

	public SubjectAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 10;
	}

	@Override
	public Object getItem(int arg0) {
		return "";
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int postion, View arg1, ViewGroup arg2) {
		View v = null;
		if (postion == 0) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(R.drawable.icon);
			v=iv;
		} else if (postion == 1) {
			TextView tv = new TextView(context);
			tv.setText("SVN专区");
		//	tv.setBackgroundColor(context.getResources().getColor(R.color.gray));
			tv.setPadding(8, 10, 8, 4);;
			v=tv;
		} else if (postion == 7) {
			TextView tv = new TextView(context);
			tv.setText("最新行情");
			//tv.setBackgroundColor(context.getResources().getColor(R.color.gray));
			tv.setPadding(8, 10, 8, 4);;
			v=tv;
		} else {
			LayoutInflater inf = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inf.inflate(R.layout.left_zhuti_xlv_item, null);
			LinearLayout lay = (LinearLayout) v.findViewById(R.id.zhuti_title);
			ImageView iv = (ImageView) v.findViewById(R.id.zhuti_ima);
			TextView tv = (TextView) v.findViewById(R.id.zhuti_name);
			TextView jianjie = (TextView) v.findViewById(R.id.zhuti_jianjie);
			iv.setImageResource(R.drawable.icon);
			tv.setText("一个进程的内存可以由2个部分组成");
			jianjie.setText("一个进程的内存可以由2个部分组成：native和dalvik，，dalvik就是我们平常说的java堆，我们创建的对象是在这里面分配的，而bitmap是直接在native上分配的，对于内存的限制是 native+dalvik 不能超过最大限制。");
			lay.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					context.startActivity(new Intent(context,
							NewsInfoActivity.class));
				}
			});
		}

		return v;
	}

}
