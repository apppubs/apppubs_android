package com.apppubs.ui.message.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.bean.TUser;
import com.apppubs.ui.adbook.UserInfoActivity;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.d20.R;

public class AddressBookCommonlyUserListFragment extends BaseFragment {

	private View mRootView;
	private ListView mLv;

	private List<TUser> mUserL;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.frg_addressbook_user, null);
		mLv = (ListView) mRootView;
		mLv.setSelector(R.drawable.sel_common_item);
		mLv.setDivider(null);
		mLv.setBackgroundColor(Color.WHITE);
		return mRootView;
	}

	@Override
	public void onResume() {

		super.onResume();
		mUserL = mUserBussiness.listRectent();
		ListAdapter adapter = new UserListAdapter();
		mLv.setAdapter(adapter);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mUserBussiness.recordUser(mUserL.get(position).getUserId());
				UserInfoActivity.startActivity(mHostActivity, mUserL.get(position).getUserId());
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	public class UserListAdapter extends BaseAdapter {
		private List<String> tempL = new ArrayList<String>();

		public UserListAdapter() {
			for (TUser u : mUserL) {
				tempL.add(u.getTrueName());
			}
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.item_commplyuser_xlv, null);
			TextView tvUsername = (TextView) view.findViewById(R.id.item_commplyuser_name);
			System.out.println("常用联系人" + tempL.get(position));
			tvUsername.setText(tempL.get(position));
			return view;
		}

		@Override
		public int getCount() {

			return tempL.size();
		}

		@Override
		public Object getItem(int arg0) {

			return "";
		}

		@Override
		public long getItemId(int arg0) {

			return 0;
		}
	}

}