package com.mportal.client.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.activity.SubjectInfoActivity;

public class LefZhutiAdapter extends BaseAdapter {
	private Context context;
	private static final int TYPE_LEFT = 0; // item的布局标记
	private static final int TYPE_CENTER = 1;

	public LefZhutiAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getViewTypeCount() {
		return 6;
	}

	// 假如我们的数据列表是list，里面的Bean有一个属性（type）是表明这个item应该使用哪种布局的。
	// @Override
	// public int getItemViewType(int position) {
	// return list.get(position).type;
	// }
	//
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int postion, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		View v=null;
		if (postion == 0) {
//			ViewHolder1 holder1 = null;
//			if (convertView == null) {
//				// 初始化HoderView
//				holder1 = new ViewHolder1();
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				 v= inflater.inflate(R.layout.item_pic_gv,
						null);
				ImageView iv=(ImageView) v.findViewById(R.id.pic_iv);
				TextView tv=(TextView) v.findViewById(R.id.pic_tv);
				iv.setImageResource(R.drawable.icon);
				tv.setText("压缩，用于节省BITMAP内存空间--解决BUG的关键步骤  ");
//				holder1.zhuanti_title = (RelativeLayout) convertView
//						.findViewById(R.id.tupian_lay);
//				holder1.zhuanti_img = (ImageView) convertView
//						.findViewById(R.id.tupian_iv);
//				holder1.zhuanti_name = (TextView) convertView
//						.findViewById(R.id.tupian_tv);
//				convertView.setTag(holder1);
//			} else {
//				holder1 = (ViewHolder1) convertView.getTag();}
			// 填充数据
//			holder1.zhuanti_img.setImageResource(R.drawable.vedioa);
//			;
//
//			holder1.zhuanti_name.setText("压缩，用于节省BITMAP内存空间--解决BUG的关键步骤  ");
//			;
//			holder1.zhuanti_title.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					// TODO Auto-generated method stub
//					context.startActivity(new Intent(context, BodyZhuanTi.class));
//				}
//			});

		} else {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 v= inflater.inflate(R.layout.left_zhuti_xlv_item,
					null);
			 LinearLayout lay=(LinearLayout)v.findViewById(R.id.zhuti_title);
			ImageView iv=(ImageView) v.findViewById(R.id.zhuti_ima);
			TextView tv=(TextView) v.findViewById(R.id.zhuti_name);
			TextView jianjie = (TextView) v.findViewById(R.id.zhuti_jianjie);
			iv.setImageResource(R.drawable.icon);
			tv.setText("一个进程的内存可以由2个部分组成");
			jianjie.setText("一个进程的内存可以由2个部分组成：native和dalvik，，dalvik就是我们平常说的java堆，我们创建的对象是在这里面分配的，而bitmap是直接在native上分配的，对于内存的限制是 native+dalvik 不能超过最大限制。"); 
			lay.setOnClickListener(new OnClickListener() {
				
								@Override
								public void onClick(View arg0) {
									// TODO Auto-generated method stub
									context.startActivity(new Intent(context, SubjectInfoActivity.class));
								}
							});
			
//			ViewHolder holder = null;
//			if (convertView == null) {
//				// 初始化HoderView
//				holder = new ViewHolder();
//				LayoutInflater inflater = (LayoutInflater) context
//						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				convertView = inflater.inflate(R.layout.left_zhuti_xlv_item,
//						null);
//				holder.zhuanti_img = (ImageView) convertView
//						.findViewById(R.id.zhuti_ima);
//				holder.zhuanti_title = (LinearLayout) convertView
//						.findViewById(R.id.zhuti_title);
//
//				holder.zhuanti_name = (TextView) convertView
//						.findViewById(R.id.zhuti_name);
//				holder.zhuanti_jianjie = (TextView) convertView
//						.findViewById(R.id.zhuti_jianjie);
//				convertView.setTag(holder);
//			} else {
//				holder = (ViewHolder) convertView.getTag();
//				
//			}
//				// 填充数据
//				holder.zhuanti_img.setImageResource(R.drawable.vedioa);
//				;
//
//				holder.zhuanti_name.setText("压缩，用于节省BITMAP内存空间--解决BUG的关键步骤  ");
//				;
//				holder.zhuanti_jianjie
//						.setText("一个进程的内存可以由2个部分组成：native和dalvik，，dalvik就是我们平常说的java堆，我们创建的对象是在这里面分配的，而bitmap是直接在native上分配的，对于内存的限制是 native+dalvik 不能超过最大限制。");
//				;
//				holder.zhuanti_title.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View arg0) {
//						// TODO Auto-generated method stub
//						context.startActivity(new Intent(context,
//								BodyZhuanTi.class));
//					}
//				});
		}
		return v;
	}

	class ViewHolder {
		private TextView zhuanti_name, zhuanti_jianjie;
		private LinearLayout zhuanti_title;
		private ImageView zhuanti_img;
	}

	class ViewHolder1 {
		private TextView zhuanti_name;
		private RelativeLayout zhuanti_title;
		private ImageView zhuanti_img;
	}
}