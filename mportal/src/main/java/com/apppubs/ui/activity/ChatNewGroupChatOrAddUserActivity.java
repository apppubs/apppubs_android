package com.apppubs.ui.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.AppContext;
import com.apppubs.bean.TUser;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.constant.URLs;
import com.apppubs.util.JSONResult;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.d20.R;

public class ChatNewGroupChatOrAddUserActivity extends BaseActivity {

	private final String TAG_VALUE_VIEW_TYPE_USER_ICON = "user_icon";
	
	public static final String EXTRA_PRESELECTED_USERNAME_LIST = "preselected_user_list";
	public static final String EXTRA_INT_MODE = "mode";
	public static final String EXTRA_STRING_CHAT_GROUP="chat_group";
	
	public static final int MODE_NEW = 0;
	public static final int MODE_ADD = 1;
	
	private ListView mListView;
	private CommonAdapter<TUser> mListAdapter;
	private List<TUser> mUserList;
	private List<TUser> mFilteredUserList;
	private List<TUser> mCheckedUserList;
	
	private String mPreSelectedUsernames;
	private int mMode;
	private String mChatGroupId;
	
	private HorizontalScrollView mCheckedUsersContainerHSV;
	private LinearLayout mCheckedusersContainerLL;
	private SearchView mSearchView;
	
	
	private String[] mIconColors = {"#F44336","#9C27B0","#3F51B5","#03A9F4","#009688","#009688","#FF9800"};
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_chat_new_group_chat);
		
		mPreSelectedUsernames =  getIntent().getStringExtra(EXTRA_PRESELECTED_USERNAME_LIST);
		mMode = getIntent().getIntExtra(EXTRA_INT_MODE, 0);
		mChatGroupId = getIntent().getStringExtra(EXTRA_STRING_CHAT_GROUP);
		initView();
		fillData();
	}
	private void initView() {
		if(mMode==MODE_ADD){
			setTitle("添加群成员");
			fillTextView(R.id.chat_new_group_chat_explain_tv, "添加1-"+(999-mPreSelectedUsernames.split(",").length)+"人");
		}else{
			setTitle("发起群聊天 ");
		}
		mListView = (ListView) findViewById(R.id.chat_new_group_chat_usernames_list);
		mCheckedUsersContainerHSV = (HorizontalScrollView) findViewById(R.id.chat_new_group_chat_users_container_hsv);
		mCheckedusersContainerLL = (LinearLayout) findViewById(R.id.chat_new_group_chat_users_container_ll);
		mSearchView = (SearchView) findViewById(R.id.chat_new_group_chat_sv);
		mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String queryString) {
				System.out.println("onQueryTextSubmit:"+queryString);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String queryString) {
				System.out.println("onQueryTextChange:"+queryString);
				if(TextUtils.isEmpty(queryString)){
					mFilteredUserList = mUserList;
				}else{
					mFilteredUserList = searchUser(queryString);
				}
				mListAdapter.setData(mFilteredUserList);
				mListAdapter.notifyDataSetChanged();
				return false;
			}

			
		});
		
	}
	private List<TUser> searchUser(String queryString) {
		List<TUser> filteredUserList = new ArrayList<TUser>();
		for(TUser user:mUserList){
			if(user.getTrueName().contains(queryString)){
				filteredUserList.add(user);
			}
		}
		return filteredUserList;
	}
	private void fillData() {
		mUserList = mUserBussiness.listAllUser();
		mFilteredUserList = new ArrayList<TUser>();
		for(TUser user:mUserList){
			mFilteredUserList.add(user);
		}
		mListAdapter= new CommonAdapter<TUser>(this,mFilteredUserList,R.layout.item_chat_new_group_chat) {
			
			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") 
			@Override
			protected void fillValues(ViewHolder holder, TUser bean, int position) {
				TextView nameTv = holder.getView(R.id.chat_new_group_chat_name_tv);
				nameTv.setText(bean.getTrueName());
				ImageView checkedIv = holder.getView(R.id.chat_new_group_chat_checkbox_checked_iv);
				checkedIv.setColorFilter(mDefaultColor,Mode.SRC_ATOP);
				if(mCheckedUserList!=null&&!mCheckedUserList.contains(bean)){
					checkedIv.setVisibility(View.GONE);
				}else if(mCheckedUserList!=null&&mCheckedUserList.contains(bean)){
					checkedIv.setVisibility(View.VISIBLE);
				}
				ImageView precheckedIv = holder.getView(R.id.chat_new_group_chat_checkbox_prechecked_iv);
				precheckedIv.setColorFilter(Color.GRAY,Mode.SRC_ATOP);
				if(mPreSelectedUsernames!=null&&mPreSelectedUsernames.contains(bean.getUsername())){
					precheckedIv.setVisibility(View.VISIBLE);
				}else{
					precheckedIv.setVisibility(View.GONE);
					
				}
				TextView iconTv = holder.getView(R.id.chat_new_group_chat_icon_tv);
				GradientDrawable bgShape = new GradientDrawable();
				int index = getIndexOfLetterInAlphabet(bean.getInitials().toCharArray()[0]);
				bgShape.setColor(Color.parseColor(mIconColors[index%mIconColors.length]));
				bgShape.setShape(GradientDrawable.OVAL);
				if(Build.VERSION.SDK_INT>=16){
					iconTv.setBackground(bgShape);
				}else{
					iconTv.setBackgroundDrawable(bgShape);
				}
				String trueName = bean.getTrueName().trim();
				iconTv.setText(trueName.length()>2?trueName.substring(trueName.length()-2):trueName);
				
			}
		
		};
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(mCheckedUserList==null){
					mCheckedUserList = new ArrayList<TUser>();
				}
				TUser selectedUser = mFilteredUserList.get(position);
				if(mPreSelectedUsernames!=null&&mPreSelectedUsernames.contains(selectedUser.getUsername())){
					
				}else if(mCheckedUserList.contains(selectedUser)){
					mCheckedUserList.remove(selectedUser);
					view.findViewById(R.id.chat_new_group_chat_checkbox_checked_iv).setVisibility(View.GONE);
					removeUserIconFromContainer(selectedUser.getUserId());
				}else{
					mCheckedUserList.add(selectedUser);
					view.findViewById(R.id.chat_new_group_chat_checkbox_checked_iv).setVisibility(View.VISIBLE);
					addUserIcon2Container(selectedUser);
				}
				
			}

			
		});
		mCheckedUserList = new ArrayList<TUser>();
		mListView.setAdapter(mListAdapter);
		
		updateOkButton();
	}
	
	private void uncheckedUserByUserId(String userid){
		for(TUser user:mCheckedUserList){
			if(user.getUserId().equals(userid)){
				mCheckedUserList.remove(user);
				break;
			}
		}
		mListAdapter.notifyDataSetChanged();
		removeUserIconFromContainer(userid);
		
	}
	private void removeUserIconFromContainer(String userId) {
		if(mCheckedUserList.size()==0){
			mCheckedUsersContainerHSV.setVisibility(View.GONE);
		}
		View selectedView = mCheckedusersContainerLL.findViewWithTag(userId);
		mCheckedusersContainerLL.removeView(selectedView);
		updateOkButton();
	}
	
	
	private void addUserIcon2Container(TUser selectedUser) {
		mCheckedUsersContainerHSV.setVisibility(View.VISIBLE);
		
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(Utils.dip2px(this, 30), Utils.dip2px(this, 30));
		ll.setMargins(Utils.dip2px(this, 10), 0, 0, 0);
		ll.gravity = Gravity.CENTER_VERTICAL;
		mCheckedUsersContainerHSV.smoothScrollTo(mCheckedusersContainerLL.getWidth(), 0);
		TextView tv = makeIconTv(selectedUser.getTrueName(), selectedUser.getInitials());
		tv.setTag(R.id.temp_id, TAG_VALUE_VIEW_TYPE_USER_ICON);
		tv.setTag(selectedUser.getUserId());
		tv.setOnClickListener(this);
		mCheckedusersContainerLL.addView(tv,ll);
		updateOkButton();
	}
	
	private void updateOkButton(){
		Button creationBtn = (Button) findViewById(R.id.chat_new_group_chat_create_btn);
		int curUsersNum = mPreSelectedUsernames!=null?mPreSelectedUsernames.split(",").length+mCheckedUserList.size():mCheckedUserList.size();
		creationBtn.setText("确定("+curUsersNum+"/999)");
		if(mCheckedUserList.size()<1){
			creationBtn.setEnabled(false);
		}else{
			creationBtn.setEnabled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi") 
	private TextView makeIconTv(String name,String pinyinFirstLetter){
		TextView tv = new TextView(this);
		GradientDrawable bgShape = new GradientDrawable();
		int index = getIndexOfLetterInAlphabet(pinyinFirstLetter.toCharArray()[0]);
		bgShape.setColor(Color.parseColor(mIconColors[index%mIconColors.length]));
		bgShape.setShape(GradientDrawable.OVAL);
		if(Build.VERSION.SDK_INT>=16){
			tv.setBackground(bgShape);
		}else{
			tv.setBackgroundDrawable(bgShape);
		}
		String trueName = name.trim();
		tv.setText(trueName.length()>2?trueName.substring(trueName.length()-2):trueName);
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
		return tv;
	}
	private int getIndexOfLetterInAlphabet(Character letter){
		int result = Character.toLowerCase(letter)-96;
		return result<0?0:result;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		Object viewType = v.getTag(R.id.temp_id);
		if(viewType!=null&&viewType.equals(TAG_VALUE_VIEW_TYPE_USER_ICON)){
			Object userId = v.getTag();
			uncheckedUserByUserId(userId.toString());
		}else if(v.getId()==R.id.chat_new_group_chat_create_btn){
			if(mMode==MODE_NEW){
				newChat();
			}else{
				addUser2Group();
			}
		}
	}
	
	private void addUser2Group() {
		StringBuilder sb = new StringBuilder();
		sb.append(AppContext.getInstance(mContext).getCurrentUser().getUsername());
		for(int i=-1;++i<mCheckedUserList.size();){
			sb.append(",");
			sb.append(mCheckedUserList.get(i).getUsername());
		}
		String url = null;
		try {
			url = String.format(URLs.URL_CHAT_ADD_CHAT_USER, URLs.baseURL,URLs.appCode,mChatGroupId,URLEncoder.encode(sb.toString(), "utf-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ProgressHUD.dismissProgressHUDInThisContext(ChatNewGroupChatOrAddUserActivity.this);
				JSONResult jr = JSONResult.compile(response);
				if(jr.code ==JSONResult.RESULT_CODE_SUCCESS){
					finish();
					Intent closeChatInfoActivityIntent = new Intent(ChatGroupInfoActivity.ACTION_CLOSE);
					sendBroadcast(closeChatInfoActivityIntent);
					Toast.makeText(ChatNewGroupChatOrAddUserActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressHUD.dismissProgressHUDInThisContext(ChatNewGroupChatOrAddUserActivity.this);
			}
		}));
	}
	private void newChat() {
		ProgressHUD.show(this);
		StringBuilder sb = new StringBuilder();
		sb.append(AppContext.getInstance(mContext).getCurrentUser().getUsername());
		for(int i=-1;++i<mCheckedUserList.size();){
			sb.append(",");
			sb.append(mCheckedUserList.get(i).getUsername());
		}
		String url = null;
		try {
			url = String.format(URLs.URL_CHAT_CREATE_CHAT,URLs.baseURL,URLs.appCode, AppContext.getInstance(mContext).getCurrentUser().getUsername(),URLEncoder.encode(sb.toString(),"utf-8"),"2");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringRequest request = new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				ProgressHUD.dismissProgressHUDInThisContext(ChatNewGroupChatOrAddUserActivity.this);
				JSONResult jr = JSONResult.compile(response);
				if(jr.code ==JSONResult.RESULT_CODE_SUCCESS){
					String groupId = (String)jr.getResultMap().get("groupid");
					ChatActivity.startActivity(ChatNewGroupChatOrAddUserActivity.this, "",groupId,ChatActivity.CHAT_TYPE_GROUP);
					finish();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				ProgressHUD.dismissProgressHUDInThisContext(ChatNewGroupChatOrAddUserActivity.this);
			}
		});
		request.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
		mRequestQueue.add(request);
	}
	
}
