package com.apppubs.d20.message.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.BaseActivity;

public class FilePickerLocalActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_file_picker_local);
		setTitle("选择文件");
	}
}
