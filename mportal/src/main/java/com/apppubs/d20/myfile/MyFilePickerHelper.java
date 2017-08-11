package com.apppubs.d20.myfile;

import android.content.Context;
import android.os.Bundle;

import com.apppubs.d20.activity.ContainerActivity;

import java.io.File;

/**
 * Created by zhangwen on 2017/8/10.
 */

public class MyFilePickerHelper {

	private static MyFilePickerHelper sHelper;

	private FilePickerListener mListener;
	private Context mContext;


	private MyFilePickerHelper(Context context){
		mContext = context;
	}

	public static MyFilePickerHelper getInstance(Context context){
		if (sHelper==null){
			synchronized (MyFilePickerHelper.class){
				if (sHelper == null){
					sHelper = new MyFilePickerHelper(context);
				}
			}
		}
		return sHelper;
	}

	public FilePickerListener getListener() {
		return mListener;
	}

	public void setListener(FilePickerListener mListener) {
		this.mListener = mListener;
	}

	public interface FilePickerListener{
		void onSelectDone(File files);
	}

	public void startSelect(FilePickerListener listener){
		mListener = listener;
		Bundle extra = new Bundle();
		extra.putInt(MyFileFragment.EXTRA_NAME_DISPLAY_MODE,MyFileFragment.EXTRA_VALUE_DISPLAY_MODE_SELECT);
		ContainerActivity.startActivity(mContext, MyFileFragment.class,extra);
	}

}
