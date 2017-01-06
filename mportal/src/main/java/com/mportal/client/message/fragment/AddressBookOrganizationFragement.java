package com.mportal.client.message.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.ContainerActivity;
import com.mportal.client.adapter.CommonAdapter;
import com.mportal.client.adapter.ViewHolder;
import com.mportal.client.bean.App;
import com.mportal.client.bean.Department;
import com.mportal.client.constant.Constants;
import com.mportal.client.fragment.BaseFragment;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.LogM;

public class AddressBookOrganizationFragement extends BaseFragment {

	public static final String ARG_STRING_SUPER_ID = "super_id";

	private String mSuperId;
	private ListView depLv;
	private CommonAdapter<Department> adapter;
	private List<Department> mDepartmentList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		mSuperId = getArguments().getString(ARG_STRING_SUPER_ID);
		initRootView();

		return mRootView;
	}

	private void initRootView() {
		
		depLv = new ListView(mContext);
		depLv.setSelector(R.drawable.sel_common_item);
		depLv.setDivider(null);
		depLv.setBackgroundColor(getResources().getColor(R.color.window_color));
		mRootView = depLv;
	}
	
	public void refreshList(){
		mDepartmentList = mUserBussiness.listSubDepartment(mSuperId);
		adapter.notifyDataSetChanged(mDepartmentList);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		
		mDepartmentList = new ArrayList<Department>();
		final int totalRow = mDepartmentList.size();
		String tempFlag = "0";
		
		String appConfig = (String) FileUtils.readObj(mContext, Constants.FILE_NAME_APP_CONFIG);
		try {
			JSONObject appConfigJO = new JSONObject(appConfig);
			 tempFlag = appConfigJO.getString(Constants.APP_CONFIG_PARAM_ORG_SHOW_COUNT_FLAG);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		final String showCountFlag = tempFlag;
		adapter = new CommonAdapter<Department>(mContext,mDepartmentList,R.layout.item_organization_lv) {

			@Override
			protected void fillValues(ViewHolder holder, Department bean, int position) {
				
				((TextView)holder.getView(R.id.org_item_name_tv)).setText(bean.getName());
				TextView detailTv = ((TextView) holder.getView(R.id.org_item_count_tv)); 
				if(showCountFlag.equals("1")){
					detailTv.setVisibility(View.VISIBLE);
					detailTv.setText("共" + bean.getTotalNum() + "人");
				}else{
					detailTv.setVisibility(View.GONE);
				}
				View line = holder.getView(R.id.line);
				View longerLine = holder.getView(R.id.line_longer);
				//最后一行的宽度是全屏的.隐藏item里的线
				if((totalRow-1)==position){
					line.setVisibility(View.GONE);
					longerLine.setVisibility(View.VISIBLE);
				}else{
					line.setVisibility(View.VISIBLE);
					longerLine.setVisibility(View.GONE);
				}
			}
		};
		
		depLv.setAdapter(adapter);
		depLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
				Department dep = mDepartmentList.get(position);
				String id = dep.getDeptId();
				if (!mUserBussiness.isLeaf(id)) {

					Bundle b = new Bundle();
					b.putString(AddressBookOrganizationFragement.ARG_STRING_SUPER_ID, id);
					ContainerActivity.startActivity(mHostActivity, AddressBookOrganizationFragement.class, b,
							dep.getName());
				} else {
					LogM.log(this.getClass(), "是叶子节点");
					Bundle b = new Bundle();
					b.putString(AddressBookUserListFragment.ARG_STRING_SUPER_ID, id);
					if (dep.getTotalNum() == 0) {
						Toast.makeText(getActivity(), "没有联系人", Toast.LENGTH_SHORT).show();
					} else {
						ContainerActivity.startActivity(mHostActivity, AddressBookUserListFragment.class, b,
								dep.getName());
					}
				}
			}
		});
		
		refreshList();
	}
}
