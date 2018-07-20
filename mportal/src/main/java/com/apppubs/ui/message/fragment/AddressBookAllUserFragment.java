package com.apppubs.ui.message.fragment;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.AppContext;
import com.apppubs.ui.widget.LetterListView;
import com.apppubs.d20.R;
import com.apppubs.ui.adbook.UserInfoActivity;
import com.apppubs.ui.fragment.BaseFragment;

public class AddressBookAllUserFragment extends BaseFragment {

	private TextView overlay;
	private LetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private ListView mCityLv;
	private OverlayThread overlayThread;
	private EditText mEditText;
	private MyAdapter adapter;
	private List<TUser> mUsers;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frg_addressbook_alluseruser, null);
		mCityLv = (ListView) view.findViewById(R.id.frg_usernames_list);
		overlay = (TextView) view.findViewById(R.id.frg_usernames_overlay);
		letterListView = (LetterListView) view.findViewById(R.id.frg_usernames_cityLetterListView);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();

		return view;
	}

	TextWatcher mTextWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String temp = mEditText.getText().toString();
			if (temp.equals("")) {
				// 填充所有的
				UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
//				mUsers = mUserBussiness.listAllUser(currentUser.getAddressbookPermissionString());
				adapter = new MyAdapter(mUsers);
				mCityLv.setAdapter(adapter);
			}
		}
	};

	public void refreshList(){
		mUsers = mUserBussiness.listAllUser();
		adapter.setData(mUsers);
		adapter.notifyDataSetChanged();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		mUsers = mUserBussiness.listAllUser();
		adapter = new MyAdapter(mUsers);
		mCityLv.setAdapter(adapter);
	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}

	}

	private class LetterListViewListener implements LetterListView.OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mCityLv.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				System.out.println("调用首字母可见");
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
			System.out.println("调用首字母不可见");
		}

	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<TUser> list;

		public MyAdapter(List<TUser> list) {

			this.inflater = LayoutInflater.from(getActivity());
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String fristM = list.get(i).getInitials();
				String currentStr = "";
				if (fristM.length() > 0) {
					currentStr = list.get(i).getInitials().substring(0, 1);
				} else {

				}

				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).getInitials() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getInitials();

					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

		}

		public void setData(List<TUser> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return mUsers.size();
		}

		@Override
		public Object getItem(int position) {
			return mUsers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_cityname_list, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.devider = convertView.findViewById(R.id.item_city_list_devider);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.name.setText(mUsers.get(position).getTrueName());
			String currentInital = mUsers.get(position).getInitials();
			String nextInitial = (position + 1) < mUsers.size() ? mUsers.get(position + 1).getInitials() : " ";
			String preInitial = (position - 1) >= 0 ? mUsers.get(position - 1).getInitials() : "";
			if (!currentInital.equals(preInitial)) {
				if(position==0){
					holder.alpha.setText("共"+mUsers.size()+"人");
				}else{
					holder.alpha.setText(currentInital);
				}
				holder.alpha.setVisibility(View.VISIBLE);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			if (!currentInital.equals(nextInitial)) {// 第一个
				holder.devider.setVisibility(View.GONE);
			} else {
				holder.devider.setVisibility(View.VISIBLE);
			}
			holder.name.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(getActivity(), UserInfoActivity.class);
					intent.putExtra(UserInfoActivity.EXTRA_STRING_USER_ID, mUsers.get(position).getUserId());
					getActivity().startActivity(intent);
				}
			});
			return convertView;
		}

		private class ViewHolder {
			private TextView alpha;
			private TextView name;
			private View devider;
		}

	}

}