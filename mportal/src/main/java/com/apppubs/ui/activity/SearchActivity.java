package com.apppubs.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.constant.URLs;
import com.apppubs.util.SystemUtils;
import com.apppubs.util.Tools;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.commonlist.CommonListView;
import com.apppubs.ui.widget.commonlist.CommonListViewListener;
import com.apppubs.d20.R;
import com.apppubs.bean.SearchInfo;

/**
 * 搜索界面
 * 
 */
public class SearchActivity extends BaseActivity implements OnItemClickListener {
	private ImageView search;
	private EditText keywordEt;
	private CommonListView mLv;
	private LinearLayout mprogress;
	private TextView title;
	private SearchAdapter adapter;
	private String word;
	private int lodemun = 2;// 上拉加载更多的页数记录
	private List<String> list = new ArrayList<String>();
	private List<SearchInfo> infos = new ArrayList<SearchInfo>();
	private List<SearchInfo> more = new ArrayList<SearchInfo>();
	private Handler myhandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mprogress.setVisibility(View.GONE);
				mLv.setVisibility(View.GONE);
				SystemUtils.showToast(SearchActivity.this, "没有找到相关内容");
				break;
			case 1:
				mLv.setVisibility(View.VISIBLE);
				mprogress.setVisibility(View.GONE);
				mLv.setAdapter(adapter);
				break;

			case 3:
				mLv.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				mLv.stopLoadMore();
				break;
			case 4:
				mLv.setVisibility(View.VISIBLE);
				mLv.stopLoadMore();
				Toast.makeText(SearchActivity.this, "没有更多了", Toast.LENGTH_SHORT)
						.show();
				;
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_search);
		init();
		keywordEt.setOnClickListener(this);
	}


	private void init() {
		setTitle("搜索");
		mprogress = (LinearLayout) findViewById(R.id.search_progress_ll);
		search = (ImageView) findViewById(R.id.search_iv_search);
		keywordEt = (EditText) findViewById(R.id.search_et_keyword);
		mLv = (CommonListView) findViewById(R.id.search_lv_values);
		mLv.setOnItemClickListener(this);
		mLv.setPullRefreshEnable(false);
		mLv.setPullLoadEnable(true);
		mLv.setCommonListViewListener(new CommonListViewListener() {
			
			@Override
			public void onRefresh() {
				// searchcanNetSend(2);
			}

			@Override
			public void onLoadMore() {
				// 联网请求上拉加载的数据
				searchcanNetSendLodeMore(lodemun);
				lodemun += 1;
			}

		});
	}

	// 联网请求上拉加载的数据
	private void searchcanNetSendLodeMore(final int lodemun) {
		if (SystemUtils.canConnectNet(SearchActivity.this)) {
			new Thread() {
				public void run() {
					String word = keywordEt.getText().toString().trim();
					adapter.getLodemore(mAppContext.getApp().getWebAppCode(), word, lodemun, 6, URLs.CLIENTKEY);
					System.out.println("打印webappcode..."+URLs.appCode);
					boolean bo = adapter.backmore();
					if (bo) {
						myhandler.sendEmptyMessage(4);
					} else {
						myhandler.sendEmptyMessage(3);
					}
				};
			}.start();

		}
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		switch (v.getId()) {
		case R.id.search_iv_search:
			// 键盘消失
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(keywordEt.getWindowToken(), 0);
			mLv.setVisibility(View.VISIBLE);
			searchcanNetSend(1);
			break;
		case R.id.search_et_keyword:
			if (keywordEt.getText().toString().trim().length() == 0) {
				mLv.setVisibility(View.GONE);
			}

			break;
		}

	}

	// 联网请求数据
	private void searchcanNetSend(final int msgwhat) {
		if (SystemUtils.canConnectNet(SearchActivity.this)) {
			if (msgwhat == 1) {
				mprogress.setVisibility(View.VISIBLE);
			} else {
				mprogress.setVisibility(View.GONE);
			}
			new Thread() {
				public void run() {
					// webappcode=A09&keyword=交通&pno=1&pernum=10&clientkey=
					word= keywordEt.getText().toString().trim();
					adapter = new SearchAdapter();
					if (adapter.isEmpty()) {
						myhandler.sendEmptyMessage(0);
					} else {
						myhandler.sendEmptyMessage(msgwhat);
					}
				};
			}.start();

		} else {
			SystemUtils.showToast(SearchActivity.this, "联网失败，请检查您的网络");
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		Utils.colseInput(SearchActivity.this);
	}

	// list的点击事件
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int postion,
			long arg3) {
		Intent i = new Intent(SearchActivity.this, NewsInfoActivity.class);

		i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, infos.get(postion-1).getInfoid());
		i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,infos.get(postion-1).getChanlcode());
		this.startActivity(i);
	}

	public class SearchAdapter extends BaseAdapter {
		private Tools json;
		public SearchAdapter() {
			json = new Tools(SearchActivity.this);
			System.out.println("搜索用到的webappcode........."+mAppContext.getApp().getWebAppCode());
			infos = json.getSearchInfos(mAppContext.getApp().getWebAppCode(),word, 1, 10, URLs.CLIENTKEY);
		}

		// 加载更多
		public void getLodemore(String webappcode, String keyword, int pno,
				int pernum, String clientkey) {
			more = json.getSearchInfos(webappcode, keyword, pno, pernum,
					clientkey);
			infos.addAll(more);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return infos.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return "";
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHoder holder1 = null;
			if (convertView == null) {
				// 初始化HoderView
				holder1 = new ViewHoder();
				LayoutInflater inflater = (LayoutInflater) SearchActivity.this
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.item_search_lv, null);

				holder1.name = (TextView) convertView
						.findViewById(R.id.searchitem_name);
				convertView.setTag(holder1);
			} else {
				holder1 = (ViewHoder) convertView.getTag();
			}
			// 填充数据
			holder1.name.setText(infos.get(position).getTopic());
			return convertView;
		}

		class ViewHoder {
			private TextView name;
		}

		public boolean backmore() {
			return more.size() == 0;
		}
	}
}
