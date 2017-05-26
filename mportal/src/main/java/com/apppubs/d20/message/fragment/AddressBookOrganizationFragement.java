package com.apppubs.d20.message.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.adapter.ViewHolder;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.fragment.BaseFragment;
import com.apppubs.d20.message.widget.Breadcrumb;
import com.apppubs.d20.widget.ConfirmDialog;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.UserInfoActivity;
import com.apppubs.d20.adapter.CommonAdapter;
import com.apppubs.d20.bean.Department;
import com.apppubs.d20.bean.User;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.message.model.UserBasicInfo;
import com.apppubs.d20.message.model.UserBussiness;
import com.apppubs.d20.message.model.UserPickerHelper;
import com.apppubs.d20.widget.AlertDialog;
import com.apppubs.d20.widget.CircleTextImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rong.imkit.RongIM;

public class AddressBookOrganizationFragement extends BaseFragment {

	private String mSuperId;
	private ListView mListView;
	private CommonAdapter<Department> mDeptAdapter;
	private CommonAdapter<User> mUserAdapter;
	private List<Department> mDepartmentList;
	private List<User> mUserList;
	private Breadcrumb mBreadcrumb;
	private TextView mHeaderNumberTV;//listview头部显示当前列表item数量的textview
	private Map<String,Integer> mListViewOffsetMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		initData();
		mRootView = initView(inflater);
		initAdapter();
		displayBySuperId(mSuperId,false);

		return mRootView;
	}


	private void initAdapter() {
		final int totalRow = mDepartmentList.size();
		final String showCountFlag = AppContext.getInstance(mContext).getAppConfig().getAdbookOrgCountFlag();
		mDeptAdapter = new CommonAdapter<Department>(mContext,mDepartmentList, R.layout.item_organization_lv) {

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

		mUserAdapter = new CommonAdapter<User>(mContext,mUserList,R.layout.item_adbook_org_user_lv) {
			@Override
			protected void fillValues(ViewHolder holder, User user, int position) {
				TextView titleTv = holder.getView(R.id.item_user_picker_user_title_tv);
				titleTv.setText(user.getTrueName());
				TextView desTv = holder.getView(R.id.item_user_picker_user_des_tv);
				if(!TextUtils.isEmpty(user.getWorkTEL())&&!TextUtils.isEmpty(user.getMobile())){
					desTv.setText("手机:"+user.getMobile()+" 电话:"+user.getWorkTEL());
				}else if(TextUtils.isEmpty(user.getWorkTEL())&&!TextUtils.isEmpty(user.getMobile())){
					desTv.setText("手机:"+user.getMobile());
				}else if(!TextUtils.isEmpty(user.getWorkTEL())&&TextUtils.isEmpty(user.getMobile())){
					desTv.setText("电话:"+user.getWorkTEL());
				}
				CircleTextImageView imageView = holder.getView(R.id.item_user_picker_user_iv);
				imageView.setTextColor(Color.WHITE);
				imageView.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
				imageView.setText(user.getTrueName());
				UserBasicInfo userBasicInfo = mUserBussiness.getCachedUserBasicInfo(user.getUserId());
				if (userBasicInfo!=null){
					if(!TextUtils.isEmpty(userBasicInfo.getAtatarUrl())){
						mImageLoader.displayImage(userBasicInfo.getAtatarUrl(),imageView);
					}
					if(mAppContext.getAppConfig().getChatFlag().equals("1")){
						TextView registerTv = holder.getView(R.id.item_user_picker_user_title1_tv);
						registerTv.setVisibility(!TextUtils.isEmpty(userBasicInfo.getAppCodeVersion())?View.GONE:View.VISIBLE);
						ImageView chatBubble = holder.getView(R.id.item_user_picker_bubble_iv);
						chatBubble.setVisibility(!TextUtils.isEmpty(userBasicInfo.getAppCodeVersion())?View.VISIBLE:View.GONE);
					}
				}else{
					TextView registerTv = holder.getView(R.id.item_user_picker_user_title1_tv);
					registerTv.setVisibility(View.GONE);
					ImageView chatBubble = holder.getView(R.id.item_user_picker_bubble_iv);
					chatBubble.setVisibility(View.GONE);
				}
			}
		};

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
				if (mAppContext.getAppConfig().getChatFlag().equals("1")&&position==0){
					prepareForCreateDiscuss();
				}else if (currentSuperIdIsLeaf()){
					User user = (User) adapterView.getAdapter().getItem(position);
					Intent intent = new Intent(getActivity(), UserInfoActivity.class);
					intent.putExtra(UserInfoActivity.EXTRA_STRING_USER_ID, user.getUserId());
					getActivity().startActivity(intent);
				}else{
					Department dep = (Department) adapterView.getAdapter().getItem(position);
					String id = dep.getDeptId();
					mBreadcrumb.push(dep.getName(),id);
					displayBySuperId(id,false);
				}
			}
		});
	}

	private void prepareForCreateDiscuss() {
		Department department = mUserBussiness.getDepartmentById(mSuperId);
		String deptName = department!=null?department.getName():"组织";
		final List<String> userIdList = mUserBussiness.getUserIdsOfCertainDepartment(mSuperId,mAppContext.getAppConfig().getChatAuthFlag()==1?true:false);
		if (userIdList==null||userIdList.size()<1){
			String message = "此部门无可会话人员";
			AlertDialog dialog = new AlertDialog(mContext,null,"无法创建讨论组！",message,"确定");
			dialog.show();
			return;
		}

		final UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
		int countOfUser = userIdList.contains(currentUser.getUserId())?userIdList.size():userIdList.size()+1;
		if (countOfUser>UserPickerHelper.MAX_SELECTED_USER_NUM){
			String message = String.format("讨论组最大人数为%d,当前部门可会话人数%d",UserPickerHelper.MAX_SELECTED_USER_NUM,countOfUser);
			AlertDialog dialog = new AlertDialog(mContext,null,"无法创建讨论组！",message,"确定");
			dialog.show();
			return;
		}
		String message = String.format("讨论组包含\"%s\"下所有可会话人员和自己共（%d）人",deptName,countOfUser);
		ConfirmDialog dialog = new ConfirmDialog(mHostActivity, new ConfirmDialog.ConfirmListener() {
            @Override
            public void onOkClick() {
				((HomeBaseActivity)mHostActivity).selectMessageFragment();
				String title = currentUser.getTrueName()+"+"+mUserBussiness.getDepartmentById(mSuperId).getName()
;				RongIM.getInstance().createDiscussion(title,userIdList,null);
            }

            @Override
            public void onCancelClick() {

            }
        },"创建讨论组？",message,"取消","创建");
		dialog.show();
	}

	private void initData() {
		mListViewOffsetMap = new HashMap<String,Integer>();
		String rootId = AppContext.getInstance(mContext).getAppConfig().getAdbookRootId();
		mSuperId =  rootId;
		mDepartmentList = mUserBussiness.listRootDepartment();
		mSuperId = UserBussiness.getInstance(mContext).getRootSuperId();
	}

	private View initView(LayoutInflater inflater) {
		mRootView = inflater.inflate(R.layout.frg_addressbook_org,null);
		mListView = (ListView) mRootView.findViewById(R.id.user_picker_lv);
		mListView.setEmptyView(mRootView.findViewById(R.id.user_picker_empty_tv));
		View header = inflater.inflate(R.layout.header_adbook_org_lv,mListView,false);
		mHeaderNumberTV = (TextView) header.findViewById(R.id.header_adbook_org_lv_text_tv);
		if(mAppContext.getAppConfig().getChatFlag().equals("1")){
			View createDiscussLl = header.findViewById(R.id.adbook_org_header_create_discuss_ll);
			createDiscussLl.setVisibility(View.VISIBLE);
		}
		mListView.addHeaderView(header);


		mBreadcrumb = (Breadcrumb) mRootView.findViewById(R.id.user_picker_bc);
		mBreadcrumb.setTextColor(Color.BLACK);
		mBreadcrumb.push("组织",mSuperId);

		mBreadcrumb.setTextColor(getResources().getColor(R.color.common_text));
		mBreadcrumb.setOnItemClickListener(new Breadcrumb.OnItemClickListener() {
			@Override
			public void onItemClick(int index,String tag) {
				displayBySuperId(tag,true);
			}
		});
		return mRootView;
	}


	public void displayBySuperId(final String superId,boolean needResume){

		//存储上一个列表的偏移量
		mListViewOffsetMap.put(mSuperId,mListView.getFirstVisiblePosition());
		if (!mUserBussiness.isLeaf(superId)) {

			mDepartmentList = mUserBussiness.listSubDepartment(superId);

			mHeaderNumberTV.setText("部门("+mDepartmentList.size()+")");

			mDeptAdapter.setData(mDepartmentList);
			mListView.setAdapter(mDeptAdapter);

		} else {
			mUserList = mUserBussiness.listUser(superId);

			mHeaderNumberTV.setText("人员("+mUserList.size()+")");

			mUserAdapter.setData(mUserList);
			mListView.setAdapter(mUserAdapter);
			List<String> userIds = new ArrayList<String>();
			for (User user : mUserList){
				userIds.add(user.getUserId());
			}
			mUserBussiness.cacheUserBasicInfoList(userIds, new BussinessCallbackCommon<List<UserBasicInfo>>() {
				@Override
				public void onDone(List<UserBasicInfo> obj) {

                    mUserAdapter.notifyDataSetChanged();

				}

				@Override
				public void onException(int excepCode) {

				}
			});
		}
		if (needResume){
			int offset = mListViewOffsetMap.get(superId)==null?0:mListViewOffsetMap.get(superId);
			mListView.setSelection(offset);
		}
		mSuperId = superId;

	}

	private boolean currentSuperIdIsLeaf(){
		return mUserBussiness.isLeaf(mSuperId);
	}

	public void refreshList(){
		displayBySuperId(mSuperId,false);
	}
}
