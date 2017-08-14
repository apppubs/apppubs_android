package com.apppubs.d20.message.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.BaseActivity;
import com.apppubs.d20.message.model.MyFilePickerHelper;
import com.apppubs.d20.message.widget.FileSelectionBar;
import com.apppubs.d20.myfile.MyFileFragment;

import java.util.List;

/**
 * Created by zhangwen on 2017/8/14.
 */

public class FilePickerMyFileActivity extends BaseActivity {

	private FileSelectionBar mSelectionBar;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initViews();
	}

	private void initViews() {
		setContentView(R.layout.act_picker_myfile);
		setTitle("选择文件");
		setupFragment();
		mSelectionBar = (FileSelectionBar) findViewById(R.id.picker_myfile_selectionbar);
		MyFilePickerHelper.getInstance(this).setSelectionBar(mSelectionBar);
	}

	private void setupFragment() {
		MyFileFragment fragment = new MyFileFragment();
		Bundle args = new Bundle();
		args.putInt(MyFileFragment.EXTRA_NAME_DISPLAY_MODE,MyFileFragment.EXTRA_VALUE_DISPLAY_MODE_SELECT);
		fragment.setArguments(args);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments == null || !fragments.contains(fragment)) {
			transaction.remove(fragment);
			transaction.add(R.id.picker_myfile_frag_container, fragment);
		}
		transaction.show(fragment);
		transaction.commit();
	}
}
