package com.apppubs.d20.message.fragment;

import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.R;
import com.apppubs.d20.adbook.UserInfoActivity;
import com.apppubs.d20.bean.User;

public class AddressBookUserListFragment extends BaseFragment implements OnClickListener{

	public static final String ARG_STRING_SUPER_ID = "super_id";

	private String mSuperId;
	private View mRootView;
	private ListView mLv;

	private List<User> mUserL;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Bundle args = getArguments();
		mSuperId = args.getString(ARG_STRING_SUPER_ID);
		initRootView();
		return mRootView;
	}
	
	private void initRootView() {
		mLv = new ListView(mContext);
		mLv.setSelector(R.drawable.sel_common_item);
		mLv.setDivider(null);
		mLv.setBackgroundColor(Color.WHITE);
		mRootView = mLv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		mUserL = mUserBussiness.listUser(mSuperId);

		ListAdapter adapter1 = new BaseAdapter() {
			
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				
				ViewHolder holder = null;
				if(convertView==null){
					holder = new ViewHolder();
					convertView = mInflater.inflate(R.layout.item_organization_user_lv, null);
					holder.tv = (TextView) convertView.findViewById(R.id.user_item_name_tv);
					holder.telTv = (TextView) convertView.findViewById(R.id.user_item_tel);
					holder.mobileTv = (TextView) convertView.findViewById(R.id.user_item_mobile);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				User user = mUserL.get(position);
				holder.tv.setText(user.getTrueName());
//				holder.telTv.setText(user.getWorkTEL());
				
				if(!TextUtils.isEmpty(user.getWorkTEL())&&user.getWorkTEL().length()>3&&!TextUtils.isEmpty(user.getMobile())&&user.getMobile().length()>3){
					holder.mobileTv.setText(user.getWorkTEL()+";"+user.getMobile());
				}else if(TextUtils.isEmpty(user.getWorkTEL())&&!TextUtils.isEmpty(user.getMobile())){
					holder.mobileTv.setText(user.getMobile());
				}else if(!TextUtils.isEmpty(user.getWorkTEL())&&TextUtils.isEmpty(user.getMobile())){
					holder.mobileTv.setText(user.getWorkTEL());
				}
				
				
				return convertView;
			}
			
			@Override
			public long getItemId(int position) {
				
				return position;
			}
			
			@Override
			public Object getItem(int position) {
				
				return mUserL.get(position);
			}
			
			@Override
			public int getCount() {
				
				return  mUserL.size();
			}
			
			class ViewHolder{
				TextView tv,telTv,mobileTv;
			}
		};
		
		mLv.setAdapter(adapter1);
		mLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				UserInfoActivity.startActivity(mHostActivity, mUserL.get(position).getUserId());
			}
		});
	}

	@Override
	public void onClick(View v) {
		
	}
}