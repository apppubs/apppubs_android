package com.apppubs.ui.message.activity;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.model.message.FilePickerModel;
import com.apppubs.model.message.MyFilePickerHelper;
import com.apppubs.ui.widget.widget.Breadcrumb;
import com.apppubs.ui.widget.widget.FileSelectionBar;
import com.apppubs.util.FileUtils;
import com.apppubs.util.LogM;

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
			openDirectory(mCurLocation);
			mBreadcrumb.push("手机",mCurLocation.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		mBreadcrumb.setOnItemClickListener(new Breadcrumb.OnItemClickListener() {
			@Override
			public void onItemClick(int index, String tag) {
				openDirectory(new File(tag));
			}
		});

		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				File curFile = mCurFileList.get(position);
				if (curFile.isDirectory()){
					openDirectory(curFile);
					pushCurLocaltion2Breadcrumb();
				}else{


				}
			}
		});

		MyFilePickerHelper.getInstance(this).setSelectionBar((FileSelectionBar) findViewById(R.id.file_picker_local_fileselectionbar));
	}

	private void openDirectory(File curFile) {
		mCurLocation = curFile;
		mCurFileList = orderByName(Arrays.asList(curFile.listFiles()));
		setVisibilityOfViewByResId(R.id.file_picker_local_empty_ll,mCurFileList.size()==0?View.VISIBLE:View.GONE);
		if (mAdapter==null){
			mAdapter = new CommonAdapter<File>(this, R.layout.item_file_picker_local) {
				@Override
				protected void fillValues(ViewHolder holder, File bean, int position) {
					TextView tv = holder.getView(R.id.file_picker_local_title_tv);
					tv.setText(bean.getName());
					ImageView typeIv = holder.getView(R.id.file_picker_local_iv);
					typeIv.setImageResource(getFileTypeImageResId(bean));
					View checkBtnCon  = holder.getView(R.id.file_picker_local_selector_fl);
					if (bean.isFile()){
						checkBtnCon.setVisibility(View.VISIBLE);
						checkBtnCon.setTag(bean.getAbsolutePath());
						checkBtnCon.setOnClickListener(FilePickerLocalActivity.this);
						FilePickerModel model = new FilePickerModel();
						model.setFilePath(bean.getAbsolutePath());
						model.setSize(bean.length());
						ImageView checkBtn = holder.getView(R.id.file_picker_local_selector_iv);
						checkCheckBtn(checkBtn,MyFilePickerHelper.getInstance(FilePickerLocalActivity.this).contains(model));
					}else{
						checkBtnCon.setVisibility(View.GONE);
					}
				}
				private int getFileTypeImageResId(File file){
					if (file.isDirectory()){
						return R.drawable.file_picker_local_folder;
					}else{
						String path = file.getAbsolutePath();
						String suffix = path.substring(path.lastIndexOf(".")+1);
						if ("docx".equals(suffix)||"doc".equals(suffix)) {
							return R.drawable.myfile_file_type_word;
						} else if ("xlsx".equals(suffix)||"xls".equals(suffix)) {
							return R.drawable.myfile_file_type_excel;
						} else if ("pptx".equals(suffix)||"ppt".equals(suffix)) {
							return R.drawable.myfile_file_type_ppt;
						} else if ("pdf".equals(suffix)) {
							return R.drawable.myfile_file_type_pdf;
						} else if("jpeg".equals(suffix)||"jpg".equals(suffix)||"png".equals(suffix)||"gif".equals(suffix)){
							return R.drawable.myfile_file_type_img;
						}else {
							return R.drawable.myfile_file_type_unknow;
						}
					}
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

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId()==R.id.file_picker_local_selector_fl){
			String absPath = (String) v.getTag();
			File curFile = new File(absPath);
			if (TextUtils.isEmpty(absPath)||!curFile.exists()){
				LogM.log(this.getClass(),"文件不存在");
				return;
			}
			boolean needCheck = checkFile(curFile);
			checkCheckBtn((ImageView)v.findViewById(R.id.file_picker_local_selector_iv),needCheck);
		}
	}

	private boolean checkFile(File bean) {
		FilePickerModel pickerModel = new FilePickerModel();
		pickerModel.setFilePath(bean.getAbsolutePath());
		pickerModel.setFileUrl("");
		pickerModel.setSize(bean.length());
		if(MyFilePickerHelper.getInstance(this).contains(pickerModel)){
			MyFilePickerHelper.getInstance(this).pop(pickerModel);
			return false;
		}else{
			MyFilePickerHelper.getInstance(this).put(pickerModel);
			return true;
		}
	}

	private void checkCheckBtn(ImageView checkBtnIv, boolean check) {
		if (check){
			checkBtnIv.setSelected(true);
			checkBtnIv.setColorFilter(getThemeColor(), PorterDuff.Mode.SRC_ATOP);
		}else{
			checkBtnIv.setSelected(false);
			checkBtnIv.clearColorFilter();
		}
	}
}
