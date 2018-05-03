package com.apppubs.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.constant.URLs;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.home.CompelReadMessageModel;
import com.apppubs.util.JSONResult;

import java.util.List;

public class CompelMessageDialogActivity extends Activity implements View.OnClickListener{

	public static final String EXTRA_DATAS = "datas";

	private Button mPreBtn;
	private Button mNextBtn;
	private TextView mPageLabel;
	private WebView mWebView;


	private int mCurrentItem;
	private List<CompelReadMessageModel> mDatas;

	private RequestQueue mRequestQueue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		setupView();
		setupData();
		gotoItem(0);
	}

	private void init() {
		mRequestQueue = Volley.newRequestQueue(this);
	}

	private void setupData() {
		mDatas = (List<CompelReadMessageModel>) getIntent().getSerializableExtra(EXTRA_DATAS);
	}

	private void setupView() {
		setContentView(R.layout.act_compel_dialog);
		mPreBtn = (Button) findViewById(R.id.compel_dialog_pre_btn);
		mNextBtn = (Button) findViewById(R.id.compel_dialog_next_btn);
		mPageLabel = (TextView) findViewById(R.id.compel_dialog_page_tv);
		mWebView = (WebView) findViewById(R.id.compel_dialog_wv);

		mPreBtn.setOnClickListener(this);
		mNextBtn.setOnClickListener(this);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode== KeyEvent.KEYCODE_BACK){
			//屏蔽默认的返回键操作
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		if (view.getId()==R.id.compel_dialog_pre_btn){
			gotoItem(mCurrentItem-1);

		}else if(view.getId()==R.id.compel_dialog_next_btn){
			if (isLastItem(mCurrentItem)){
				finish();
			}else{
				gotoItem(mCurrentItem+1);
			}
		}

	}

	private void gotoItem(int index){
		if (mDatas==null||index<0||index>=mDatas.size()){
			return;
		}
		mCurrentItem = index;

		changeNextBtnByIndex(index);
		setPageLabel(index);
		setWebViewContent(index);
		markAsRead(index);
	}

	private void markAsRead(int index) {
		if (mDatas==null||index<0||index>=mDatas.size()){
			return;
		}
		SystemBiz biz = SystemBiz.getInstance(this);
		biz.markCompelReadMessage(mDatas.get(index).getMessageId(), new IAPCallback<Object>() {
			@Override
			public void onDone(Object obj) {}
			@Override
			public void onException(APError error) {}
		});
	}

	private void setWebViewContent(int index) {
		String content = mDatas.get(index).getmContent();
		mWebView.getSettings().setDefaultTextEncodingName("utf-8");
		mWebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
	}

	private void changeNextBtnByIndex(int index) {
		if (isLastItem(index)){
			mNextBtn.setText("关闭");
			mNextBtn.setBackgroundColor(Color.parseColor("#ff4000"));
			Drawable drawable = getResources().getDrawable(R.drawable.compel_message_alert_done_btn);
			mNextBtn.setBackgroundResource(R.drawable.compel_message_alert_done_btn);
		}else{

			mNextBtn.setBackgroundResource(R.drawable.compel_message_alert_btn);
			mNextBtn.setText("下一页");
		}
	}

	private void setPageLabel(int index) {
		mPageLabel.setText(String.format("%d/%d",index+1,mDatas.size()));
	}

	private boolean isLastItem(int index){
		if (mDatas!=null&&mDatas.size()==index+1){
			return true;
		}
		return false;
	}

}
