package com.apppubs.ui.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.ui.fragment.ChannelFragment;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.DragSortListView;
import com.apppubs.d20.R;
import com.apppubs.bean.NewsChannel;
import com.apppubs.ui.fragment.ChannelFragmentFactory;
import com.orm.SugarRecord;

public class SortChannelBActivity extends BaseActivity {

	public static final String EXTRA_STRING_NAME_CHANNELTYPE = "channel_type";
	public static final String EXTRA_BOOLEAN_NAME = "is_sorted";

	private DragSortListView mLv;
	private MyAdapter adapter;
	private List<NewsChannel> mChannelSelectedList;
	private String mChannelTypeId;
	private boolean isSorted;// 是否

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			mNewsBiz.rerangeChannelIndex(mChannelTypeId, from, to);
			mChannelSelectedList = SugarRecord.find(NewsChannel.class, "TYPE_ID=? and DISPLAY_ORDER != 0",
					new String[] { mChannelTypeId + "" }, null, "DISPLAY_ORDER", null);
			adapter.notifyDataSetChanged();
			isSorted = true;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("频道排序");

		overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
		mChannelTypeId = getIntent().getStringExtra(EXTRA_STRING_NAME_CHANNELTYPE);
		mChannelSelectedList = SugarRecord.find(NewsChannel.class, "TYPE_ID=? and DISPLAY_ORDER != 0",
				new String[] { mChannelTypeId + "" }, null, "DISPLAY_ORDER", null);
		LogM.log(this.getClass(), "onCreate mChannelSelectedList size:" + mChannelSelectedList.size());
		setContentView(R.layout.act_sort_channel_b);
		mTitleBar.setLeftImageResource(R.drawable.close);

		mLv = (DragSortListView) findViewById(R.id.sort_channel_dsll);

		mLv.setDropListener(onDrop);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				NewsChannel nc = (NewsChannel) parent.getAdapter().getItem(position);
				ChannelFragment cfrg = ChannelFragmentFactory.getChannelFragment(nc.getShowType());
				Bundle args = new Bundle();
				args.putString(ChannelFragment.ARG_KEY, nc.getCode());
				ContainerActivity.startActivity(SortChannelBActivity.this, cfrg.getClass(), args, nc.getName());
			}
		});
		adapter = new MyAdapter();
		mLv.setAdapter(adapter);

	}

	private class ViewHolder {
		public TextView name;
		public ImageView iv;
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			return mChannelSelectedList.size();
		}

		@Override
		public Object getItem(int position) {

			return mChannelSelectedList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LogM.log(this.getClass(), "getView" + position);
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(SortChannelBActivity.this).inflate(R.layout.item_channel_sort_b, null);
				holder = new ViewHolder();
				holder.name = (TextView) convertView.findViewById(R.id.channel_sort_tv);
				holder.iv = (ImageView) convertView.findViewById(R.id.channel_sort_iv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			NewsChannel nc = mChannelSelectedList.get(position);
			holder.name.setText(nc.getName());
			mImageLoader.displayImage(nc.getPic(), holder.iv);
			return convertView;
		}

	}

	@Override
	public void finish() {

		Intent intent = getIntent();
		intent.putExtra(EXTRA_BOOLEAN_NAME, isSorted);
		setResult(RESULT_OK, intent);
		super.finish();
		overridePendingTransition(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
	}
}
