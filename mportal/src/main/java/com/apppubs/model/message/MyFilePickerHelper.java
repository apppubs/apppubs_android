package com.apppubs.model.message;

import android.content.Context;
import android.content.Intent;

import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.message.activity.FilePickerChooseActivity;
import com.apppubs.ui.widget.widget.FileSelectionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/8/10.
 */

public class MyFilePickerHelper {

	private static MyFilePickerHelper sHelper;

	private FilePickerListener mListener;
	private Context mContext;
	private FileSelectionBar mSelectionBar;
	private List<FilePickerModel> mDatas;


	private MyFilePickerHelper(Context context){
		mContext = context;
		mDatas = new ArrayList<FilePickerModel>();
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

	public void clear(){
		mDatas.clear();
	}

	public FilePickerListener getListener() {
		return mListener;
	}

	public void setListener(FilePickerListener mListener) {
		this.mListener = mListener;
	}

	public interface FilePickerListener{
		void onSelectDone(List<FilePickerModel> files);
	}

	public void startSelect(FilePickerListener listener){
		mListener = listener;
		Intent intent = new Intent(mContext,FilePickerChooseActivity.class);
		intent.putExtra(BaseActivity.EXTRA_STRING_TITLE,"选择文件");
		mContext.startActivity(intent);
	}

	public void setSelectionBar(FileSelectionBar selectionBar){
		mSelectionBar = selectionBar;
		mSelectionBar.setDatas(mDatas);
		mSelectionBar.setListener(new FileSelectionBar.FileSelectionBarListener() {
			@Override
			public void onOkClick() {
				mListener.onSelectDone(mDatas);
			}
		});
	}

	public void put(FilePickerModel model){
		if (!contains(model)){
//			if(null==model.getFilePath()){
//				model.setFilePath(AppContext.getInstance(mContext).getCacheManager().fetchCache(model.getFileUrl()).getAbsolutePath());
//			}
			mDatas.add(model);
			mSelectionBar.put(model);
		}
	}

	public FilePickerModel pop(FilePickerModel model){
		FilePickerModel targetModel = null;
		for (FilePickerModel m:mDatas){
			if (model.equals(m)){
				targetModel = m;
			}
		}
		if (targetModel!=null){
			mDatas.remove(targetModel);
			mSelectionBar.pop(model);
		}
		return targetModel;
	}

	public boolean contains(FilePickerModel model){
		if (null==model){
			return false;
		}
		for (FilePickerModel m:mDatas){
			if (model.equals(m)){
				return true;
			}
		}
		return false;
	}

	public List<FilePickerModel> getSelectionModels(){
		return mDatas;
	}

}
