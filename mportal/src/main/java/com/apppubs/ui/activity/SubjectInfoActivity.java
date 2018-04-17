package com.apppubs.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.ui.adapter.SubjectAdapter;
import com.apppubs.d20.R;

public class SubjectInfoActivity extends BaseActivity  {
	private ImageView back;
	private TextView tv;
	private ListView lv;
	private SubjectAdapter adapter;
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	 	// TODO Auto-generated method stub
	 	super.onCreate(savedInstanceState);
	 	setContentView(R.layout.act_zhuanti_info);
	 	back=(ImageView) findViewById(R.id.bodyzhuanti_back);
	 	tv=(TextView) findViewById(R.id.bodyzhuanti_title);
	 	lv=(ListView) findViewById(R.id.bodyzhuanti_lv);
	 	adapter=new SubjectAdapter(SubjectInfoActivity.this);
	 	lv.setAdapter(adapter);
	 	back.setOnClickListener(this);
	    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bodyzhuanti_back:
			finish();
			break;

		default:
			break;
		}
	}
}
