package com.apppubs.d20.widget.menudialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.apppubs.d20.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/7/22.
 */

public class MenuDialogAdapter extends BaseAdapter {
	private List<String> mDatas = new ArrayList<String>();
	private Context mContext;

	public MenuDialogAdapter(Context context,List<String> menus){
		mContext = context;
		mDatas = menus;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		if (position>=mDatas.size()){
			return null;
		}
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.item_myfile_delete_menu_adapter,null);
		TextView tv = (TextView) view.findViewById(R.id.item_myfile_delete_menu_adapter_title_tv);
		String title = mDatas.get(position);
		tv.setText(title);
		View divider = view.findViewById(R.id.item_myfile_delete_menu_adapter_divider);
		if (position>0){
			divider.setVisibility(View.VISIBLE);
		}
		return view;
	}


}
