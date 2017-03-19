package com.apppubs.d20.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apppubs.d20.bean.Collection;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.R;
import com.orm.SugarRecord;

public class CollectionAdapter extends BaseAdapter {
	private Context context;
	private List<Collection> mList;
	public CollectionAdapter(Context context) {
		this.context = context;
		mList = SugarRecord.find(Collection.class, "TYPE = ?", Collection.TYPE_NORMAL+"");
		for(Collection c: mList){
			System.out.println(c.getTitle());
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		ViewHoder holder1 = null;
		if (convertView == null) {
			// 初始化HoderView
			holder1 = new ViewHoder();
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.save_xlvitem, null);
//			RelativeLayout lay = (RelativeLayout)convertView
//					.findViewById(R.id.saveitem_lay);
			holder1.name = (TextView) convertView.findViewById(R.id.saveitem_name);
			holder1.desc= (TextView) convertView.findViewById(R.id.saveitem_desp);
//			holder1.time= (TextView)convertView.findViewById(R.id.saveitem_time);
			convertView.setTag(holder1);
		}
		else{
			holder1=(ViewHoder) convertView.getTag();
		}
		//填充数据
		//holder1.iv.setImageResource(R.drawable.vivo);
		holder1.name.setText(mList.get(arg0).getTitle());
		if (mList.get(arg0).getContentAbs().equals("")) {
			holder1.desc.setVisibility(View.GONE);
		}else{
			holder1.desc.setVisibility(View.VISIBLE);
			String str=mList.get(arg0).getContentAbs().length()>25?(mList.get(arg0).getContentAbs().subSequence(0, 25)+"···"):mList.get(arg0).getContentAbs();
			holder1.desc.setText(str);
		}
		String time= StringUtils.getDateString(mList.get(arg0).getAddTime(), "yyyy-MM-dd HH:mm:ss");
//		holder1.time.setText(time);
		return convertView;
	}

	class ViewHoder {
		private TextView name, desc, time;
	}
	public  List<Collection> backcollection(){
		return mList;
	}
}
