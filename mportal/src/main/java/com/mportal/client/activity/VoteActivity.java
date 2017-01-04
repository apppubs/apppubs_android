package com.mportal.client.activity;

import android.os.Bundle;
import android.view.View;

import com.mportal.client.R;
import com.mportal.client.adapter.LefZhutiAdapter;
import com.mportal.client.view.commonlist.CommonListView;
/**
 * 投票界面
 *
 */
public class VoteActivity extends BaseActivity  {
	private CommonListView xlv;
    private LefZhutiAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_vote);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		xlv = (CommonListView) findViewById(R.id.vote_xlv);
		adapter=new LefZhutiAdapter(getApplication());
		xlv.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.vote_back:
			finish();
			break;
		default:
			break;
		}
	}
}
