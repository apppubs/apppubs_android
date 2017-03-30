package com.apppubs.d20.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.MsgRecord;
import com.apppubs.d20.model.MsgController;
import com.apppubs.d20.util.SharedPreferenceUtils;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.activity.ChatActivity;
import com.apppubs.d20.activity.ChatNewGroupChatOrAddUserActivity;
import com.apppubs.d20.activity.ContainerActivity;
import com.apppubs.d20.bean.App;
import com.apppubs.d20.constant.Actions;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.message.fragment.AddressBookFragement;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.widget.TitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.orm.SugarRecord;

/**
 * 消息记录列表界面
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月18日 by zhangwen create
 * 
 */
public class MsgRecordListFragment extends BaseFragment implements OnClickListener {

	
	
	private ListView mLv;
	private TextView mEmptyTv;
	private MyAdapter mAdapter;
	private List<MsgRecord> mMsgRecordL;
	private PopupWindow mMenuPW;
	private DisplayImageOptions mDisplayImageOptions;
	private SimpleDateFormat mSimpleDateFormat;
	private BroadcastReceiver mRefreshBR;
	private Date mCurResponseTime;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mDisplayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.user)
				.showImageForEmptyUri(R.drawable.user).showImageOnFail(R.drawable.user).cacheInMemory(true)
				.cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

		initRootView();
		fill();
		mRefreshBR = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				refreshList();
			}
		};
		return mRootView;
	}

	private void initRootView() {
		FrameLayout rootView = new FrameLayout(mContext);
		mEmptyTv = new TextView(mContext);
		mEmptyTv.setText("还没有消息!");
		mEmptyTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		mEmptyTv.setTextColor(getResources().getColor(R.color.common_text_gray));
		FrameLayout.LayoutParams emptyTvLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		emptyTvLp.gravity = Gravity.CENTER;
		mLv = new ListView(mContext);
		// mLv = (SwipeListView) mInflater.inflate(R.layout.frg_msgrecord,
		// null);
		mLv.setDivider(null);
		mLv.setSelector(R.drawable.sel_common_item);
		mLv.setBackgroundColor(getResources().getColor(R.color.window_color));
		
		rootView.addView(mLv);
		rootView.addView(mEmptyTv,emptyTvLp);
		
		mRootView = rootView;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			refreshList();
			MsgController.getInstance(mContext).setMsgListVisiable(true);
		}else{
			MsgController.getInstance(mContext).setMsgListVisiable(false);
		}
	}
	@Override
	public void changeActivityTitleView(TitleBar titleBar) {

		super.changeActivityTitleView(titleBar);
		if (titleBar == null) {
			return;
		}
		titleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.plus, new OnClickListener() {

			@Override
			public void onClick(View v) {
				View menuPop = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_msg_record_menu, null);

				mMenuPW = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				mMenuPW.setFocusable(true);
				mMenuPW.setOutsideTouchable(true);
				mMenuPW.setBackgroundDrawable(new BitmapDrawable());
				mMenuPW.showAsDropDown(mTitleBar.getRightView());
				if (mAppContext.getApp().getAllowChat() == App.ALLOW_CHAT_FALSE) {
					// 当没有聊天功能时隐藏新建聊天
					setVisibilityOfViewByResId(menuPop, R.id.pop_msg_record_add_chat_ll, View.GONE);
					setVisibilityOfViewByResId(menuPop, R.id.pop_msg_record_add_group_chat_ll, View.GONE);
				}

				View addChatV = menuPop.findViewById(R.id.pop_msg_record_add_chat_ll);
				View addServiceV = menuPop.findViewById(R.id.pop_msg_record_add_service_ll);
				View addGrougChatV = menuPop.findViewById(R.id.pop_msg_record_add_group_chat_ll);
				addChatV.setOnClickListener(MsgRecordListFragment.this);
				addServiceV.setOnClickListener(MsgRecordListFragment.this);
				addGrougChatV.setOnClickListener(MsgRecordListFragment.this);
			}
		});
		
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		refreshList();
		MsgController.getInstance(mContext).setMsgListVisiable(true);
		mHostActivity.registerReceiver(mRefreshBR, new IntentFilter(Actions.ACTION_REFRESH_CHAT_RECORD_LIST));
	}
	@Override
	public void onStop() {
		super.onStop();
		mHostActivity.unregisterReceiver(mRefreshBR);
	}
	private void refreshList() {
		String url = String.format(URLs.URL_SERVICE_NO_FOR_USER, AppContext.getInstance(mContext).getCurrentUser().getUsername());
		mRequestQueue.add(new StringRequest(url,new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONResult jr = JSONResult.compile(response);
				mCurResponseTime = jr.responseTime;
				if(jr.resultCode==1){
					SugarRecord.deleteAll(MsgRecord.class);
					String deletedIdsStr = SharedPreferenceUtils.getInstance(mContext).getString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants.SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS, "");
					List<String> deletedIdsList = StringUtils.str2ArrayList(deletedIdsStr, ",");
					try {
						JSONArray ja = new JSONArray(jr.result);
						for(int i=-1;++i<ja.length();){
							JSONObject serviceNO = ja.getJSONObject(i);
							if(deletedIdsList.contains(serviceNO.getString("service_id"))){
								if(serviceNO.getInt("newpush_unreadnum")==0){
									continue;
								}else{
									deletedIdsList.remove(serviceNO.getString("service_id"));
									SharedPreferenceUtils.getInstance(mContext).putString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants.SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS, StringUtils.array2Str(deletedIdsList, ","));
								}
							}

							
							MsgRecord msgRecord = new MsgRecord();
							msgRecord.setSourceUsernameOrId(serviceNO.getString("service_id"));
							msgRecord.setTitle(serviceNO.getString("service_name"));
							msgRecord.setIcon(serviceNO.getString("service_picurl"));
							msgRecord.setSubTitle(serviceNO.getString("newpush_title"));
							try{
								msgRecord.setUpdateTime(mSimpleDateFormat.parse(serviceNO.getString("service_updatetime")));
							}catch(ParseException e){
								e.printStackTrace();
								msgRecord.setUpdateTime(new Date());
							}
							msgRecord.setUnreadNum(serviceNO.getInt("newpush_unreadnum"));
							if(serviceNO.getInt("service_flag")==4){
								msgRecord.setType(MsgRecord.TYPE_CHAT);
							}else{
								msgRecord.setType(MsgRecord.TYPE_SERVICE);
							}
							msgRecord.save();
						}
						
						mMsgRecordL = mMsgBussiness.listMsgRecord();
						fill();
					} catch (JSONException e) {
						e.printStackTrace();
					} 
					
				}
				
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				
			}
		}));
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	private void fill() {
		
		mMsgRecordL = mMsgBussiness.listMsgRecord();
		
		if(mMsgRecordL==null||mMsgRecordL.size()==0){
			
			mEmptyTv.setVisibility(View.VISIBLE);
			return ;
		}else{
			mEmptyTv.setVisibility(View.GONE);
		}
		if(mAdapter==null){
			
			mAdapter = new MyAdapter();
			mLv.setAdapter(mAdapter);
			mLv.setOnItemClickListener(new OnItemClickListener() {
				
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					final MsgRecord mr = mMsgRecordL.get(position);
					if (mr.getType() == MsgRecord.TYPE_CHAT) {
						
//					User otherU = mUserBussiness.getUserByUsername(mr.getSourceUsernameOrId());
//					User otherU = mUserBussiness.getUserByUserId(mr.getSourceUsernameOrId());
						String url = String.format(URLs.URL_CHAT_GROUD_INFO, mr.getSourceUsernameOrId(), AppContext.getInstance(mContext).getCurrentUser().getUsername());
						mRequestQueue.add(new StringRequest(url, new Listener<String>() {
							
							@Override
							public void onResponse(String response) {
								JSONResult jr = JSONResult.compile(response);
								if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
									String groupType = (String)jr.getResultMap().get("group_type");
									if(groupType.equals("1")){
                                        ChatActivity.startActivity(mHostActivity, "",mr.getSourceUsernameOrId(),ChatActivity.CHAT_TYPE_SINGLE,mr.getTitle());
                                    }else{
                                        ChatActivity.startActivity(mHostActivity, "",mr.getSourceUsernameOrId(),ChatActivity.CHAT_TYPE_GROUP,mr.getTitle());
                                    }

								}
							}
						}, new ErrorListener() {
							
							@Override
							public void onErrorResponse(VolleyError arg0) {
								
							}
						}));
						
						
						
					} else if (mr.getType() == MsgRecord.TYPE_SERVICE) {
						Bundle b = new Bundle();
						b.putString(ServiceNoInfoFragment.ARGS_STRING_SERVICE_NO_ID, mr.getSourceUsernameOrId());
						ContainerActivity.startActivity(mHostActivity, ServiceNoInfoListFragement.class, b, mr.getTitle());
						mMsgBussiness.cleanUnread(mr.getSourceUsernameOrId());
					}
					
				}
			});
			mLv.setOnItemLongClickListener(new OnItemLongClickListener() {
				
				@Override
				public boolean onItemLongClick(AdapterView<?> adapter, View view, int pos, long id) {
					System.out.println("长按");
					return true;
				}
			});
		}else{
			mAdapter.notifyDataSetChanged();
		}
	}

	private class MyAdapter extends BaseSwipeAdapter {

		SimpleDateFormat sdf;

		public MyAdapter() {
			sdf = new SimpleDateFormat("HH:mm:ss",Locale.CHINA);
		}

		@Override
		public int getCount() {

			return mMsgRecordL.size();
		}

		@Override
		public Object getItem(int position) {

			return mMsgRecordL.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public void fillValues(final int pos, View convertView) {
			MsgRecord record = mMsgRecordL.get(pos);
			TextView titleTv = null;
			TextView subTitleTv = null;
			ImageView iconIv = null;
			TextView updateTimeTv = null;
			TextView unreadTv = null;
			if (record.getType() == MsgRecord.TYPE_SERVICE) {
				titleTv = (TextView) convertView.findViewById(R.id.message_item_title_tv1);
				subTitleTv = (TextView) convertView.findViewById(R.id.message_item_des_tv1);
				iconIv = (ImageView) convertView.findViewById(R.id.message_item_iv1);
				updateTimeTv = (TextView) convertView.findViewById(R.id.message_item_time_tv1);
				unreadTv = (TextView) convertView.findViewById(R.id.msg_record_item_unread_tv1);
				convertView.findViewById(R.id.swipe).setVisibility(View.GONE);
				convertView.findViewById(R.id.swipe1).setVisibility(View.VISIBLE);
				
			} else {
				titleTv = (TextView) convertView.findViewById(R.id.message_item_title_tv);
				subTitleTv = (TextView) convertView.findViewById(R.id.message_item_des_tv);
				iconIv = (ImageView) convertView.findViewById(R.id.message_item_iv);
				updateTimeTv = (TextView) convertView.findViewById(R.id.message_item_time_tv);
				unreadTv = (TextView) convertView.findViewById(R.id.msg_record_item_unread_tv);
				convertView.findViewById(R.id.swipe).setVisibility(View.VISIBLE);
				convertView.findViewById(R.id.swipe1).setVisibility(View.GONE);
				
				final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(getSwipeLayoutResourceId(pos));
				
				convertView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						swipeLayout.close(false);
						String deletedIdsStr = SharedPreferenceUtils.getInstance(mContext).getString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants.SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS, "");
						List<String> arrList = StringUtils.str2ArrayList(deletedIdsStr, ",");
						arrList.add( mMsgRecordL.get(pos).getSourceUsernameOrId());
						String ids = StringUtils.array2Str(arrList, ",");
						SharedPreferenceUtils.getInstance(mContext).putString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants.SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS, ids);
						
						Object obj =  MportalApplication.readObj(mContext, MportalApplication.MSG_DELETED_CHAT_GROUP_MAP);
						Map<String,String> map = (Map<String, String>) (obj!=null?obj:new HashMap<String,String>());
						map.put(mMsgRecordL.get(pos).getSourceUsernameOrId(),mSimpleDateFormat.format(new Date()));
						MportalApplication.writeObj(mContext, map, MportalApplication.MSG_DELETED_CHAT_GROUP_MAP);
						
						String url = String.format(URLs.URL_CLEAR_UNREAD_NUM_FOR_SERVICE_NO_AND_CHAT, mMsgRecordL.get(pos).getSourceUsernameOrId(),
								AppContext.getInstance(mContext).getCurrentUser().getUsername());
						mRequestQueue.add(new StringRequest(url, new Listener<String>() {

							@Override
							public void onResponse(String arg0) {

							}
						}, new ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError arg0) {

							}
						}));
						
						SugarRecord.deleteAll(MsgRecord.class, "id = ? ", mMsgRecordL.get(pos).getId());
						mMsgRecordL = mMsgBussiness.listMsgRecord();
						mAdapter.notifyDataSetChanged();
						if(mMsgRecordL==null||mMsgRecordL.size()==0){
							
							mEmptyTv.setVisibility(View.VISIBLE);
							return;//如果没有消息，直接返回
						}else{
							mEmptyTv.setVisibility(View.GONE);
							
						}
						
						
					}
				});
			}
			
			titleTv.setText(record.getTitle());
			subTitleTv.setText(record.getSubTitle());
			mImageLoader.displayImage(record.getIcon(), iconIv,mDisplayImageOptions);
			updateTimeTv.setText(StringUtils.getFormattedTime(record.getUpdateTime(), mCurResponseTime==null?new Date():mCurResponseTime));
			int unreadNum = record.getUnreadNum();
			if (unreadNum > 0) {
				unreadTv.setVisibility(View.VISIBLE);
				unreadTv.setText(record.getUnreadNum() + "");
			} else {
				unreadTv.setVisibility(View.GONE);
			}
			
			
		}

		@Override
		public View generateView(final int pos, ViewGroup parent) {

			View v = LayoutInflater.from(mContext).inflate(R.layout.item_msg_record_lv, null);
			return v;

		}

		@Override
		public int getSwipeLayoutResourceId(int arg0) {
			return R.id.swipe;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_msg_record_add_service_ll:
			ContainerActivity.startActivity(mContext, ServiceNoSubscribeFragment.class, null, "添加服务号");
//			ContainerActivity.startActivity(mContext, ServiceNoListOfMineFragment.class, null, "我关注的服务号");
			mMenuPW.dismiss();
			break;
		case R.id.pop_msg_record_add_chat_ll:
			
			Log.e(this.getClass().getName(), "此处需要，增加AddressBookFragement的参数");
			Bundle args = new Bundle();
			ContainerActivity.startActivity(mContext, AddressBookFragement.class, null, "开始聊天");
			
			mMenuPW.dismiss();
			break;
		case R.id.pop_msg_record_add_group_chat_ll:
			Intent chatNewGroupIntent = new Intent(mHostActivity,ChatNewGroupChatOrAddUserActivity.class);
			chatNewGroupIntent.putExtra(ChatNewGroupChatOrAddUserActivity.EXTRA_PRESELECTED_USERNAME_LIST, AppContext.getInstance(mContext).getCurrentUser().getUsername());
			startActivity(chatNewGroupIntent);
			mMenuPW.dismiss();
			break;
		default:
			break;
		}
	}

}
