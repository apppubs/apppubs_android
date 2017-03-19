package com.apppubs.d20.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.d20.R;

public class SurveyAdapter extends BaseAdapter{
	private Context context;

	public SurveyAdapter(Context context) {
		this.context = context;
	}

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
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHoder holder1 = null;
		if (convertView == null) {
			// 初始化HoderView
			holder1 = new ViewHoder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.save_xlvitem, null);
//			RelativeLayout lay = (RelativeLayout)convertView
//					.findViewById(R.id.saveitem_lay);
//			
//			holder1.iv = (ImageView)convertView.findViewById(R.id.saveitem_iv);
//			holder1.classiv= (ImageView)convertView.findViewById(R.id.saveitem_flag);
//			holder1.classiv.setVisibility(View.GONE);
//			
//			holder1.name = (TextView) convertView.findViewById(R.id.saveitem_name);
//			holder1.desc= (TextView) convertView.findViewById(R.id.saveitem_desp);
//			holder1.comment= (TextView)convertView.findViewById(R.id.saveitem_comment);
			convertView.setTag(holder1);
		}
		else{
			holder1=(ViewHoder) convertView.getTag();
		}
		//填充数据
		holder1.iv.setImageResource(R.drawable.icon);
		holder1.name.setText("阿门SEO干嘛饿哦");
		holder1.desc.setText("啥地方了萨芬的拉升的v目录是多么V领的买噶实打实大概");
		holder1.comment.setText("3评论");
		return convertView;
	}

	class ViewHoder {
		private ImageView iv,classiv;
		private TextView name, desc, comment;
	}

}
