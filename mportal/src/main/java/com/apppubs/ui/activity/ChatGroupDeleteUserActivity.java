package com.apppubs.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.AppContext;
import com.apppubs.ui.adapter.ViewHolder;
import com.apppubs.bean.User;
import com.apppubs.util.JSONResult;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.d20.R;
import com.apppubs.ui.adapter.CommonAdapter;
import com.apppubs.constant.URLs;
import com.orm.SugarRecord;

public class ChatGroupDeleteUserActivity extends BaseActivity {

	public static final String EXTRA_STRING_CHAT_GROUP_DELETABLE_USERNAMES = "usernames";
	public static final String EXTRA_STRING_CHAT_GROUP_ID = "chat_group_id";
	
	private String mChatGroupId;
	private String mGroupUsernames;
	private String[] mIconColors = {"#F44336","#9C27B0","#3F51B5","#03A9F4","#009688","#009688","#FF9800"};
	private ListView mListView;
	private List<User> mCheckedUserList;
	private List<User> mDeletableUserList;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		mGroupUsernames = getIntent().getStringExtra(EXTRA_STRING_CHAT_GROUP_DELETABLE_USERNAMES);
		mChatGroupId = getIntent().getStringExtra(EXTRA_STRING_CHAT_GROUP_ID);
		String[] usernames = mGroupUsernames.split(",");
		StringBuilder sb = new StringBuilder();
		for(int i=-1;++i<usernames.length;){
			if(i!=0){
				sb.append(",");
			}
			sb.append("'");
			sb.append(usernames[i]);
			sb.append("'");
		}
		
		mDeletableUserList = SugarRecord.find(User.class, "username in ("+sb.toString()+")", null,null,null,null);
		
		setContentView(R.layout.act_chat_group_delete_user);
		mListView = (ListView) findViewById(R.id.chat_group_delete_user_lv);
		mListView.setEmptyView(findViewById(R.id.chat_group_delete_empty_tv));
		mListView.setAdapter(new CommonAdapter<User>(ChatGroupDeleteUserActivity.this, mDeletableUserList, R.layout.item_chat_group_delete_user) {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi") 
			@Override
			protected void fillValues(ViewHolder holder, User bean, int position) {
				TextView nameTv = holder.getView(R.id.chat_group_delete_user_name_tv);
				String truename = bean.getTrueName().trim();
				nameTv.setText(truename);
				TextView iconTv = holder.getView(R.id.chat_group_delete_user_icon_tv);
				GradientDrawable bgShape = new GradientDrawable();
				int index = Character.toLowerCase((bean.getInitials().toCharArray()[0]))-96;
				if(index<0)index=0;
				bgShape.setColor(Color.parseColor(mIconColors[index%mIconColors.length]));
				bgShape.setShape(GradientDrawable.OVAL);
				if(Build.VERSION.SDK_INT>=16){
					iconTv.setBackground(bgShape);
				}else{
					iconTv.setBackgroundDrawable(bgShape);
				}
				
				iconTv.setText(bean.getTrueName().substring(truename.length()-2, truename.length()));
				ImageView checkedIv = holder.getView(R.id.chat_group_delete_user_checkbox_checked_iv);
				checkedIv.setColorFilter(mDefaultColor, PorterDuff.Mode.SRC_ATOP);
				if(mCheckedUserList!=null&&mCheckedUserList.contains(bean)){
					checkedIv.setVisibility(View.VISIBLE);
				}else if(mCheckedUserList!=null){
					checkedIv.setVisibility(View.GONE);
				}
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(mCheckedUserList==null){
					mCheckedUserList = new ArrayList<User>();
				}
				User selectedUser = mDeletableUserList.get(position);
				if(mCheckedUserList.contains(selectedUser)){
					mCheckedUserList.remove(selectedUser);
					view.findViewById(R.id.chat_group_delete_user_checkbox_checked_iv).setVisibility(View.GONE);
				}else{
					mCheckedUserList.add(selectedUser);
					view.findViewById(R.id.chat_group_delete_user_checkbox_checked_iv).setVisibility(View.VISIBLE);
				}
				int size = mCheckedUserList.size();
				if(size>0){
					mTitleBar.setRightBtnWithText("确定("+size+")");
				}else{
					mTitleBar.setRightBtnWithText("");
				}
			}

			
		});
		mTitleBar.setRightBtnWithText("");
		mTitleBar.setRightBtnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onDeleteButtonClicked();
			}

		});
		
	}
	private void onDeleteButtonClicked() {
		if(mCheckedUserList==null||mCheckedUserList.size()==0){
			return;
		}
		new ConfirmDialog(this, new ConfirmDialog.ConfirmListener() {
			
			@Override
			public void onOkClick() {
				deleteUser();
			}
			
			@Override
			public void onCancelClick() {
				
			}
		}, "确定删除？", "取消", "确定").show();
		
	}
	
	private void deleteUser() {
		StringBuilder sb = new StringBuilder();
		for(int i=-1;++i<mCheckedUserList.size();){
			if(i!=0){
				sb.append(",");
			}
			sb.append(mCheckedUserList.get(i).getUsername());
		}
		String url = String.format(URLs.URL_CHAT_DELETE_CHAT_USER, URLs.baseURL,URLs.appCode, mChatGroupId, AppContext.getInstance(mContext).getCurrentUser().getUsername(),sb.toString());
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONResult jr = JSONResult.compile(response);
				if(jr.code ==JSONResult.RESULT_CODE_SUCCESS){
					Toast.makeText(ChatGroupDeleteUserActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					Toast.makeText(ChatGroupDeleteUserActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(ChatGroupDeleteUserActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
			}
		}));
	}
	public static void startActivity(Context context,String deletableUsernames,String chatGroupId){
		Intent intent = new Intent(context,ChatGroupDeleteUserActivity.class);
		intent.putExtra(EXTRA_STRING_CHAT_GROUP_DELETABLE_USERNAMES, deletableUsernames);
		intent.putExtra(EXTRA_STRING_CHAT_GROUP_ID, chatGroupId);
		intent.putExtra(EXTRA_STRING_TITLE, "删除群成员");
		context.startActivity(intent);
	}
}
