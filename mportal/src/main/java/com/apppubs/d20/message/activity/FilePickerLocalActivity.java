package com.apppubs.d20.message.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.adapter.CommonAdapter;
import com.apppubs.d20.adapter.ViewHolder;
import com.apppubs.d20.message.model.MyFilePickerHelper;
import com.apppubs.d20.message.widget.Breadcrumb;
import com.apppubs.d20.message.widget.FileSelectionBar;
import com.apppubs.d20.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilePickerLocalActivity extends BaseActivity {

	private FileSelectionBar mSelectionBar;
	private Breadcrumb mBreadcrumb;
	private ListView mLv;
	private File mCurLocation;
	private List<File> mCurFileList;
	private CommonAdapter<File> mAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
	}

	private void initViews() {
		setContentView(R.layout.act_file_picker_local);
		setTitle("选择文件");

		mSelectionBar = (FileSelectionBar) findViewById(R.id.file_picker_local_fileselectionbar);
		mLv = (ListView) findViewById(R.id.file_picker_local_lv);
		MyFilePickerHelper.getInstance(this).setSelectionBar(mSelectionBar);

		mBreadcrumb = (Breadcrumb) findViewById(R.id.file_picker_local_breadcrumb);
		try {
			mCurLocation = FileUtils.getExternalStorageFile();
			gotoLocation(mCurLocation);
			mBreadcrumb.push("手机",mCurLocation.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		mBreadcrumb.setOnItemClickListener(new Breadcrumb.OnItemClickListener() {
			@Override
			public void onItemClick(int index, String tag) {
				gotoLocation(new File(tag));
			}
		});

		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				File curFile = mCurFileList.get(position);
				if (curFile.isDirectory()){
					gotoLocation(curFile);
					pushCurLocaltion2Breadcrumb();
				}
			}
		});
	}

	private void gotoLocation(File curFile) {
		mCurLocation = curFile;
		mCurFileList = orderByName(Arrays.asList(curFile.listFiles()));
		if (mAdapter==null){
			mAdapter = new CommonAdapter<File>(this, R.layout.item_file_picker_local) {
				@Override
				protected void fillValues(ViewHolder holder, File bean, int position) {
					TextView tv = holder.getView(R.id.file_picker_local_title_tv);
					tv.setText(bean.getName());
					ImageView iv = holder.getView(R.id.file_picker_local_selector_fl);
					if (bean.isFile()){
						iv.setVisibility(View.VISIBLE);
					}else{
						iv.setVisibility(View.GONE);
					}
					ImageView typeIv = holder.getView(R.id.file_picker_local_iv);
					typeIv.setImageResource(R.drawable.myfile_file_type_unknow);
				}
			};
			mAdapter.setData(mCurFileList);
			mLv.setAdapter(mAdapter);
		}else{
			mAdapter.setData(mCurFileList);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void pushCurLocaltion2Breadcrumb(){
		mBreadcrumb.push(mCurLocation.getName(),mCurLocation.getAbsolutePath());
	}

	public List<File> orderByName(List<File> fileList) {
		Collections.sort(fileList, new Comparator< File>() {
			@Override
			public int compare(File o1, File o2) {
				if (o1.isDirectory() && o2.isFile())
					return -1;
				if (o1.isFile() && o2.isDirectory())
					return 1;
				return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
			}
		});
		return fileList;
	}
}
