package com.apppubs.d20.activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.apppubs.d20.adapter.SurveyAdapter;
import com.apppubs.d20.widget.TitleBar;
import com.apppubs.d20.widget.commonlist.CommonListView;
import com.apppubs.d20.R;

/**
 * 调查
 * 
 * @author sunyu
 * 
 */
public class SurveyActivity extends BaseActivity {
    private CommonListView xlv;
    private TitleBar mTitleBar;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_survey);
		init();
		}

		private void init() {
			mTitleBar = (TitleBar) findViewById(R.id.survey_tb);
			mTitleBar.setLeftBtnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			xlv=(CommonListView) findViewById(R.id.survey_xlv);
			xlv.setPullRefreshEnable(true);
			xlv.setPullLoadEnable(true);
			xlv.setAdapter(new SurveyAdapter(getApplication()));
		}

}
