package com.apppubs.ui.message.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.model.message.MyFilePickerHelper;
import com.apppubs.ui.widget.widget.FileSelectionBar;

public class FilePickerChooseActivity extends BaseActivity  {

	private FileSelectionBar mSelectionBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyFilePickerHelper.getInstance(this).setSelectionBar(mSelectionBar);
	}

	private void initViews() {
		setContentView(R.layout.act_file_picker_choose);
		findViewById(R.id.file_picker_myfile_ll).setOnClickListener(this);
		findViewById(R.id.file_picker_local_ll).setOnClickListener(this);
		mSelectionBar = (FileSelectionBar) findViewById(R.id.file_picker_choose_selectionbar);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId()==R.id.file_picker_myfile_ll){
			Intent i = new Intent(this,FilePickerMyFileActivity.class);
			startActivity(i);
		}else if (v.getId()==R.id.file_picker_local_ll){
			Intent i = new Intent(this,FilePickerLocalActivity.class);
			startActivity(i);
		}

	}
}
