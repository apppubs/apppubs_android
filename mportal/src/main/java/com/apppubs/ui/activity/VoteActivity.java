package com.apppubs.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.d20.R;
import com.apppubs.ui.adapter.LefZhutiAdapter;

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
