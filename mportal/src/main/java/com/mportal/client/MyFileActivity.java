package com.mportal.client;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artifex.mupdfdemo.MuPDFActivity;
import com.mportal.client.activity.BaseActivity;
import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.adapter.ViewHolder;
import com.mportal.client.bean.LocalFile;
import com.mportal.client.util.FileUtils;
import com.mportal.client.view.ConfirmDialog;
import com.mportal.client.view.ConfirmDialog.ConfirmListener;
import com.orm.SugarRecord;

public class MyFileActivity extends BaseActivity implements OnClickListener {

	
	private ListView mLv;
	private CommonAdapter<LocalFile> mAdapter;
	private List<LocalFile> mLocalFileList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_my_file);
		mLv = (ListView) findViewById(R.id.my_file_lv);
		mLocalFileList = SugarRecord.listAll(LocalFile.class);
		mAdapter = new CommonAdapter<LocalFile>(this,mLocalFileList,R.layout.item_my_file) {

			@Override
			protected void fillValues(ViewHolder holder, LocalFile bean, int position) {
				TextView nameTv = holder.getView(R.id.my_file_name_tv);
				nameTv.setText(bean.getName());
				TextView deleteTv = holder.getView(R.id.my_file_delete_tv);
				deleteTv.setTag(R.id.temp_id, position);
				deleteTv.setOnClickListener(MyFileActivity.this);
			}
		};
		mLv.setAdapter(mAdapter);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Uri uri = Uri.parse(mLocalFileList.get(position).getPath());
				Intent intent = new Intent(MyFileActivity.this,MuPDFActivity.class);
				intent.putExtra(MuPDFActivity.EXTRA_BOOLEAN_EDITABLE, true);
				intent.setAction(Intent.ACTION_VIEW);
				intent.setData(uri);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onClick(final View v) {
		super.onClick(v);
		if (v.getTag(R.id.temp_id)==null) {
			return;
		}
		new ConfirmDialog(this, new ConfirmListener() {
			
			@Override
			public void onOkClick() {
				Integer pos = (Integer) v.getTag(R.id.temp_id);
				deleteLocalFile(mLocalFileList.get(pos));
				mLocalFileList = SugarRecord.listAll(LocalFile.class);
				mAdapter.notifyDataSetChanged(mLocalFileList);
			}
			
			@Override
			public void onCancelClick() {
				
			}
		}, "确定删除？", "取消", "确定").show();
	}
	
	
	private void deleteLocalFile(LocalFile localFile){
		
		SugarRecord.deleteAll(LocalFile.class, "id=?", localFile.getId());
		try {
			FileUtils.delete(localFile.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Toast.makeText(MyFileActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
	}
	
}
