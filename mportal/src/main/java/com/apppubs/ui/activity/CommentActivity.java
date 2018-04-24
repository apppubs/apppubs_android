package com.apppubs.ui.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.bean.Comment;
import com.apppubs.bean.NewsInfo;
import com.apppubs.util.LogM;
import com.apppubs.util.Tools;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.d20.R;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.model.APResultCallback;
import com.apppubs.constant.URLs;
import com.apppubs.util.SystemUtils;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.orm.SugarRecord;

public class CommentActivity extends BaseActivity {
	
	public static final String EXTRA_STRING_NAME_ID = "id";
	
	public static String NEWSTYPESTRING = "newstype";
	private LinearLayout normal;
	private EditText mEditText;
	private TextView sendTv;
	private String mInfoId;
	private CharSequence mCommentTemp;
	private LinearLayout mProgress;
	private LinearLayout mEmptyLl;
	private CommonListView mLv;
	private CommonAdapter<Comment> mCommentAdapter;
	private int NEWSTYPE;
	private int mCurPage = 1;
	private List<Comment> mInfos = new ArrayList<Comment>();
	private String mIP;// IP地址
	private SimpleDateFormat mDateFormate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
	
	/**
	 * 标准时间
	 */
	private Date mStandardDateTime;
	private Handler myhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 0:
				mEditText.setText("");
				// 强制弹出
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
				switch (msg.arg1) {

				case 1:
					Toast.makeText(getApplication(), "提交成功", Toast.LENGTH_SHORT).show();
					switch (NEWSTYPE) {
					case 0:
						// SugarRecord.find(NewsInfo.class, "INFOID",
						// whereArgs);
						SugarRecord.updateById(NewsInfo.class, mInfoId, "IS_COLLECTED", 1 + "");
						break;
					}
					mCurPage = 1;
					getPage(1);// 提交成功， 下拉帅新
					break;
				case 2:
					Toast.makeText(getApplication(), "提交失败", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.act_newsinfo_comment);
		Intent i = getIntent();
		mInfoId = i.getStringExtra(EXTRA_STRING_NAME_ID);
		NEWSTYPE = i.getIntExtra(NEWSTYPESTRING, 0);
		init();
		getPage(0);

	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void init() {
		
		setTitle("评论");
		mProgress = (LinearLayout) findViewById(R.id.comment_progress_ll);
		mEmptyLl = (LinearLayout) findViewById(R.id.comment_nullshow_ll);
		mLv = (CommonListView) findViewById(R.id.commment_xlv);
		mLv.setPullRefreshEnable(true);
		mLv.setPullLoadEnable(true);
		normal = (LinearLayout) findViewById(R.id.comment_hint);
		mEditText = (EditText) findViewById(R.id.commment_count);
		sendTv = (TextView) findViewById(R.id.comment_send);
		sendTv.setOnClickListener(this);
		mLv.setCommonListViewListener(new CommonListViewListener() {
			
			@Override
			public void onRefresh() {
				mCurPage = 1;
				getPage(1);

			}

			@Override
			public void onLoadMore() {
				getPage(0);
			}
		});
		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				mCommentTemp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				if (mCommentTemp.length() == 0) {
					sendTv.setTextColor(getResources().getColor(R.color.common_text_gray));
				} else {
					sendTv.setTextColor(mDefaultColor);
				}
			}
		});
		
		mCommentAdapter = new CommonAdapter<Comment>(this,mInfos,R.layout.item_comment_xlv) {
			
			@Override
			protected void fillValues(ViewHolder holder, Comment comment, int position) {
				
				ImageView pic = holder.getView(R.id.comment_item_pic);
				TextView userNameTv = holder.getView(R.id.comment_item_username);
				TextView contentTv = holder.getView(R.id.comment_item_content);
				TextView timeTv = holder.getView(R.id.comment_item_time);
				
				userNameTv.setText(comment.getCname());
				contentTv.setText(mInfos.get(position).getContent());
				String timeS = mDateFormate.format(comment.getCdate());
				timeTv.setText(timeS);
			}
		};
		
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.comment_hint:
			normal.setVisibility(View.GONE);
			mEditText.setVisibility(View.VISIBLE);
			mEditText.requestFocus();
			InputMethodManager imm = (InputMethodManager) mEditText.getContext().getSystemService(INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
			break;
		case R.id.commment_count:
			break;
		case R.id.comment_send:
			if (SystemUtils.canConnectNet(getApplication())) {
				sendComment();
			} else {
				Toast.makeText(getApplication(), R.string.network_faile, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	// 提交评论
	private void sendComment() {
		final Tools json = new Tools(getApplication());
		if (mEditText.getText().toString().trim().length() == 0) {
			Toast.makeText(getApplication(), "说点什么吧...", Toast.LENGTH_SHORT).show();
		} else {
			if (!SystemUtils.getWIFiAddress(this).equals("")) {
				mIP = SystemUtils.getWIFiAddress(this);
				new Thread() {

					public void run() {
						String trueName = AppContext.getInstance(mContext).getCurrentUser().getTrueName();
						
						String i  = json.submmitCommment(mInfoId, mIP, mEditText.getText().toString(), URLs.CLIENTKEY, AppContext.getInstance(mContext).getCurrentUser().getUserId(),trueName==null?"":trueName);
						Message msg = Message.obtain();
						msg.what = 0;
						if (i.equals("")) {
							msg.arg1 = 2;// 提交失败
						} else {
							msg.arg1 = 1;// 提交成功
						}
						myhandler.sendMessage(msg);
					};
				}.start();
			} else if (!SystemUtils.getLocalIpAddress().equals("")) {
				mIP = SystemUtils.getLocalIpAddress();
				new Thread() {

					public void run() {
						String trueName = AppContext.getInstance(mContext).getCurrentUser().getTrueName();
						String i = json.submmitCommment(mInfoId, mIP, mEditText.getText().toString(), URLs.CLIENTKEY, AppContext.getInstance(mContext).getCurrentUser().getUserId(),trueName);
						Message msg = Message.obtain();
						msg.what = 0;
						if (i.equals("")) {
							msg.arg1 = 2;// 提交失败
						} else {
							msg.arg1 = 1;// 提交成功
						}
						myhandler.sendMessage(msg);
					};
				}.start();
			} else {
				Toast.makeText(getApplication(), getResources().getString(R.string.network_faile), Toast.LENGTH_LONG).show();
			}

		}
	}

	private void getPage(final int page) {

		mSystemBiz.getStandardDataTime(new APResultCallback<Date>() {

			@Override
			public void onException(int excepCode) {
			}

			@Override
			public void onDone(Date obj) {
				
				mStandardDateTime = obj;
				LogM.log(this.getClass(), "当前服务器时间：" + mStandardDateTime.toString());
				mSystemBiz.getCommentList(mInfoId, mCurPage, 10, URLs.CLIENTKEY, new APResultCallback<List<Comment>>() {

					@Override
					public void onException(int excepCode) {

					}

					@Override
					public void onDone(List<Comment> obj) {
						mProgress.setVisibility(View.GONE);
						fillPageToList(page, obj);
					}
	
				});

			}
		});

	}
	
	//将某一页的数据填充到列表中
	private void fillPageToList(final int page, List<Comment> obj) {
		
		if (mCurPage == 1) {
			mInfos = obj;
			mCommentAdapter.setData(mInfos);
			if (mInfos.size() == 0) {
				mEmptyLl.setVisibility(View.VISIBLE);
				
			} else {
				mEmptyLl.setVisibility(View.GONE);
				if (page == 1) {// 下拉帅新
					mLv.stopRefresh();
				}
				mLv.setAdapter(mCommentAdapter);
			}
		} else {// lodemore
			if (obj.size() == 0) {
				Toast.makeText(CommentActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
			}else{
				mInfos.addAll(obj);
				mCommentAdapter.notifyDataSetChanged();
			}
			mLv.stopLoadMore();
		}
		mCurPage++;
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
}