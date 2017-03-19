package com.apppubs.d20.fragment;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.activity.PaperIssueActivity;
import com.apppubs.d20.bean.Paper;
import com.apppubs.d20.bean.PaperIssue;
import com.apppubs.d20.business.BussinessCallbackCommon;
import com.apppubs.d20.business.PaperBussiness;
import com.apppubs.d20.widget.commonlist.CommonListView;
import com.apppubs.d20.widget.commonlist.CommonListViewListener;
import com.orm.SugarRecord;

public class PaperIssueListFragment extends BaseFragment {

	public static final String ARG_PAPERCODE = "paper_code";

	private PaperBussiness mPaperBussiness;
	private CommonListView xlv;
	private List<PaperIssue> mIssuelist;
	private String mPaperCode;
	private ViewHoder viewhoder;
	private int mCurPos = 1;
	private LinearLayout progress;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mRootView = inflater.inflate(R.layout.frg_paper, null);
		xlv = (CommonListView) mRootView.findViewById(R.id.frg_peter__xlv);
		init();
		load();

		xlv.setCommonListViewListener(new CommonListViewListener() {

			@Override
			public void onRefresh() {
				load();
			}

			@Override
			public void onLoadMore() {

			}
		});

		xlv.setPullLoadEnable(false);
		return mRootView;
	}


	private void init() {
		progress = (LinearLayout) mRootView.findViewById(R.id.frg_peter_progress_ll);
		mPaperCode = getArguments().getString(ARG_PAPERCODE);
		mPaperBussiness = PaperBussiness.getInstance();
	}

	private void load() {
		mPaperBussiness.getPaperIssueList(mPaperCode, mCurPos, new BussinessCallbackCommon<List<PaperIssue>>() {

			@Override
			public void onException(int excepCode) {
			}

			@Override
			public void onDone(List<PaperIssue> obj) {
				progress.setVisibility(View.GONE);
				mIssuelist = obj;
				xlv.setAdapter(new ListView1Adapter());
				xlv.stopRefresh();
			}
		});
	}

	public class IssueAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mIssuelist.size();
		}

		@Override
		public Object getItem(int pos) {
			return mIssuelist.get(pos);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int pos, View convertView, ViewGroup arg2) {
			viewhoder = null;
			if (convertView == null) {
				// 初始化HoderView
				viewhoder = new ViewHoder();
				convertView = LayoutInflater.from(mHostActivity).inflate(R.layout.item_paper_gv, null);
				viewhoder.frg = (FrameLayout) convertView.findViewById(R.id.item_pepter_frg);
				viewhoder.qiPic = (ImageView) convertView.findViewById(R.id.item_pepter_iv);
				// viewhoder.lodedown=(ImageView)
				// convertView.findViewById(R.id.item_papter_lode);
				viewhoder.qiname = (TextView) convertView.findViewById(R.id.item_pepter_qi);
				convertView.setTag(viewhoder);
			} else {
				viewhoder = (ViewHoder) convertView.getTag();
			}
			// 填充数据
			PaperIssue pi = mIssuelist.get(pos);
			mImageLoader.displayImage(pi.getCover(), viewhoder.qiPic);
			viewhoder.qiname.setText(pi.getName());
			viewhoder.frg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(mHostActivity, PaperIssueActivity.class);
					PaperIssue pi = mIssuelist.get(pos);
					Paper paper = SugarRecord.findByProperty(Paper.class, "paper_code", pi.getPaperCode());
					i.putExtra(PaperIssueActivity.EXTRA_NAME_ISSUE_ID, pi.getId());
					i.putExtra(PaperIssueActivity.EXTRA_STRING_TITLE, paper.getName() + " (" + pi.getName() + ")");
					startActivity(i);
				}
			});

			return convertView;
		}

	}

	class ViewHoder {
		private FrameLayout frg;
		private TextView qiname;
		private ImageView qiPic, lodedown;
	}

	/**
	 * xlv的填充只有一个gridview
	 * 
	 * @author sunyu
	 * 
	 */

	class ListView1Adapter extends BaseAdapter {

		@Override
		public int getCount() {
			return 1;
		}

		@Override
		public Object getItem(int position) {
			return "";
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = mInflater.inflate(R.layout.item_paper_xlv, parent, false);
			GridView gv = (GridView) view.findViewById(R.id.listview_item_gridview);
			gv.setAdapter(new IssueAdapter());
			return view;
		}

	}
}