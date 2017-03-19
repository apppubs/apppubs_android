package com.apppubs.d20.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 通用的viewholder
 * @author zhangwen
 *
 */
public class ViewHolder {
	
	private Context mContext;
	private SparseArray<View> mViews;
	private View mConvertView;
	
	public ViewHolder(Context context,View convertView,ViewGroup parent){
		
		mContext = context;
		mConvertView = convertView;
		mViews = new SparseArray<View>();
	}
	
	public static ViewHolder getViewHolder(Context context,View convertView,ViewGroup parent,int layoutResId,int pos){
		
		ViewHolder viewHolder = null;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(layoutResId, parent,false);
			viewHolder = new ViewHolder(context, convertView, parent);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		return viewHolder;
	}
	
	public <T extends View> T getView(int resId){
		
		View v = mViews.get(resId);
		if(v==null){
			v = mConvertView.findViewById(resId);
			mViews.put(resId, v);
		}	
		return (T) v;
	}
	
	public View getConvertView(){
		return mConvertView;
	}
	
	
}
