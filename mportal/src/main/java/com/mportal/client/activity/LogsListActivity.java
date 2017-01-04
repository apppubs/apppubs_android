package com.mportal.client.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.adapter.ViewHolder;
import com.mportal.client.fragment.FilePreviewFragment;

public class LogsListActivity extends BaseActivity{

	private ListView mListView;
	private CommonAdapter<File> mAdapter;
	private List<File> mFileList;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mListView = new ListView(this);
		
		mAdapter = new CommonAdapter<File>(this,android.R.layout.simple_list_item_1) {
			
			@Override
			protected void fillValues(ViewHolder holder, File bean, int position) {
				TextView tv = holder.getView(android.R.id.text1);
				tv.setText(bean.getName());
			}
		};
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle args = new Bundle();
				args.putString(FilePreviewFragment.ARGS_STRING_FILE_LOCAL_PATH,mFileList.get(position).getAbsolutePath());
				args.putBoolean(FilePreviewFragment.ARGS_BOOLEAN_SHARE_2_QQ, true);
				ContainerActivity.startActivity(LogsListActivity.this, FilePreviewFragment.class, args, "文件预览");
			}
		});
		File[] files = getExternalFilesDir("logs").listFiles();
		mFileList = new ArrayList<File>();
		Collections.addAll(mFileList, files);
		mAdapter.sestData(mFileList);
		mListView.setAdapter(mAdapter);
		
		setContentView(mListView);
	}
}
