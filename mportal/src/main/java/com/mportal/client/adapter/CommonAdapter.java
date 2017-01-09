package com.mportal.client.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 通用的adapter
 * 
 * @author hezheng
 * 
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

	protected List<T> mDatas;
	protected Context mContext;
	protected int mResId;

	public CommonAdapter(Context context, List<T> datas, int resId) {
		mDatas = datas;
		mContext = context;
		mResId = resId;
	}

	public CommonAdapter(Context context, int resId) {
		mContext = context;
		mResId = resId;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	public void setData(List<T> datas) {
		mDatas = datas;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	public void notifyDataSetChanged(List<T> datas){
		mDatas = datas;
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = ViewHolder.getViewHolder(mContext, convertView, parent, mResId, position);
		fillValues(holder, getItem(position), position);
		return holder.getConvertView();
	}

	protected abstract void fillValues(ViewHolder holder, T bean, int position);

}
