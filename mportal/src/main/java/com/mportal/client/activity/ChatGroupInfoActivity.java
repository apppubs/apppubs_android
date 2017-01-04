package com.mportal.client.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.JSONResult;
import com.mportal.client.util.Utils;
import com.mportal.client.view.ConfirmDialog;
import com.mportal.client.view.ConfirmDialog.ConfirmListener;
import com.mportal.client.view.FlowLayout;

public class ChatGroupInfoActivity extends BaseActivity{

	public static final String EXTRA_CHAT_GROUP_ID = "group_id";
	public static final String ACTION_CLOSE = "close_ChatGroupInfoActivity";
	
	private final int MAX_GROUP_NAME_LENGTH = 30;
	private final String[] mIconColors = {"#F44336","#9C27B0","#3F51B5","#03A9F4","#009688","#009688","#FF9800"};

	private String mChatGroupId;
	private String mChatGroupName;
	private String mUsernames;
	private boolean loadSuccess = false;//标记详情是否加载完毕，只有加载完毕才可以进行其他操作
	
	private FlowLayout usersFl;
	private BroadcastReceiver mCloseBR;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mChatGroupId = getIntent().getStringExtra(EXTRA_CHAT_GROUP_ID);
		
		setContentView(R.layout.act_chat_group_info);
		setTitle("聊天详情");
		
		usersFl = (FlowLayout) findViewById(R.id.chat_group_info_users_fl);
		

		
		mCloseBR = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};
		registerReceiver(mCloseBR, new IntentFilter(ACTION_CLOSE));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		String url = String.format(URLs.URL_CHAT_GROUD_INFO, mChatGroupId,MportalApplication.user.getUsername());
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {
			
			@Override
			public void onResponse(String response) {
					JSONResult jr = JSONResult.compile(response);
					try {
						mChatGroupName = jr.getResultMap().get("group_name");
						fillTextView(R.id.chat_group_info_group_name_tv, TextUtils.isEmpty(mChatGroupName)?"未设置":mChatGroupName);
						String groupUsersInfoStr = jr.getResultMap().get("group_userinfo");
						JSONArray ja = new JSONArray(groupUsersInfoStr);
						fillTextView(R.id.chat_group_info_total_num_tv, "共"+ja.length()+"人");
						usersFl.removeAllViews();
						StringBuilder usernamesSB = new StringBuilder();
						for(int i=-1;++i<ja.length();){
							JSONObject userJsonO = ja.getJSONObject(i);
							TextView tv = new TextView(ChatGroupInfoActivity.this);
							String truename = userJsonO.getString("truename");
							tv.setText(truename);
							tv.setBackgroundColor(Color.parseColor(mIconColors[i%mIconColors.length]));
							int paddLeftRight = Utils.dip2px(ChatGroupInfoActivity.this, 5);
							int paddTopBottom = Utils.dip2px(ChatGroupInfoActivity.this, 3);
							tv.setTextColor(Color.WHITE);
							tv.setPadding(paddLeftRight, paddTopBottom, paddLeftRight, paddTopBottom);
							ViewGroup.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
							
							usersFl.addView(tv,lp);
							if(i!=0){
								usernamesSB.append(",");
							}
							usernamesSB.append(userJsonO.getString("username"));
						}
						mUsernames = usernamesSB.toString();
						if(MportalApplication.user.getUsername().equals(jr.getResultMap().get("group_creator"))){
							setVisibilityOfViewByResId(R.id.chat_group_info_delete_user_rl, View.VISIBLE);
						}
						loadSuccess = true;
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					
			}
		}, new ErrorListener() {
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(ChatGroupInfoActivity.this, "网络故障", Toast.LENGTH_SHORT).show();
			}
		}));
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(!loadSuccess){
			return;
		}
		
		switch (v.getId()) {
		case R.id.chat_group_info_add_user_rl:
			Intent addUserIntent = new Intent(this,ChatNewGroupChatOrAddUserActivity.class);
			addUserIntent.putExtra(ChatNewGroupChatOrAddUserActivity.EXTRA_PRESELECTED_USERNAME_LIST, mUsernames);
			addUserIntent.putExtra(ChatNewGroupChatOrAddUserActivity.EXTRA_INT_MODE, ChatNewGroupChatOrAddUserActivity.MODE_ADD);
			addUserIntent.putExtra(ChatNewGroupChatOrAddUserActivity.EXTRA_STRING_CHAT_GROUP, mChatGroupId);
			startActivity(addUserIntent);
			break;
		case R.id.chat_group_info_exit_tv:
			onExitClicked();
			break;
		case R.id.chat_group_info_group_name_rl:
			onGroupNameLabelClicked();
			break;
		case R.id.chat_group_info_delete_user_rl:
			onDeleteUserClicked();
			break;
		default:
			break;
		}
	}

	private void onDeleteUserClicked() {
		String deletableUsernames = mUsernames.replaceAll(MportalApplication.user.getUsername()+",", "").replaceAll(","+MportalApplication.user.getUsername(), "").replaceAll(MportalApplication.user.getUsername(), "");
		ChatGroupDeleteUserActivity.startActivity(this, deletableUsernames,mChatGroupId);
	}

	private void onExitClicked() {
		new ConfirmDialog(this,new ConfirmListener() {
			
			@Override
			public void onOkClick() {
				exitGroupChat();
			}
			
			@Override
			public void onCancelClick() {
				
			}
		}, "是否退出此群?", "退出后则不能接受此群的消息!", "取消", "退出").show();
	}

	private void exitGroupChat() {
		String url = String.format(URLs.URL_CHAT_EXIT_CHAT_GROUP, mChatGroupId,MportalApplication.user.getUsername());
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				JSONResult jr = JSONResult.compile(response);
				if(jr.resultCode==JSONResult.RESULT_CODE_SUCCESS){
					finish();
					Intent closeChatActivityIntent = new Intent(ChatActivity.ACTION_CLOSE_CHAT_ACTIVITY);
					sendBroadcast(closeChatActivityIntent);
				}else{
					Toast.makeText(ChatGroupInfoActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(ChatGroupInfoActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
			}
		}));
	}

	private void onGroupNameLabelClicked() {
		android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		input.setText(mChatGroupName);
		input.setSelection(mChatGroupName.length());
		alert.setView(input);
		alert.setTitle("修改群名称");
		alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		        String value = input.getText().toString().trim();
		        changeGroupName(value);
		    }

		});

		alert.setNegativeButton("取消",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int whichButton) {
		                dialog.cancel();
		            }
		        });
		alert.show();
	}
	
	private void changeGroupName(final String newGroupName) {
		if(TextUtils.isEmpty(newGroupName)){
			Toast.makeText(this, "请填写组名", Toast.LENGTH_SHORT).show();
		}else if(newGroupName.length()>MAX_GROUP_NAME_LENGTH){
			Toast.makeText(this, "组名长度不得长于30字符", Toast.LENGTH_SHORT).show();
		}else{
			
			String url = null;
			try {
				url = String.format(URLs.URL_CHAT_CHANGE_GROUP_NAME, mChatGroupId,URLEncoder.encode(newGroupName,"utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			mRequestQueue.add(new StringRequest(url, new Listener<String>() {
				
				@Override
				public void onResponse(String error) {
					Toast.makeText(ChatGroupInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
					mChatGroupName = newGroupName;
					fillTextView(R.id.chat_group_info_group_name_tv, mChatGroupName);
				}
			}, new ErrorListener() {
				
				@Override
				public void onErrorResponse(VolleyError error) {
					Toast.makeText(ChatGroupInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
				}
			}));
		}
	}
	@Override
	public void finish() {
		super.finish();
		unregisterReceiver(mCloseBR);
	}
}
