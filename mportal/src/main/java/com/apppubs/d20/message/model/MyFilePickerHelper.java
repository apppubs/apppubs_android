package com.apppubs.d20.message.model;

import android.content.Context;
import android.content.Intent;

import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.message.activity.FilePickerChooseActivity;

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
		Intent intent = new Intent(mContext,FilePickerChooseActivity.class);
		intent.putExtra(BaseActivity.EXTRA_STRING_TITLE,"选择文件");
		mContext.startActivity(intent);
	}

}
