package com.apppubs.ui.activity;

import java.util.List;

import android.os.Bundle;

import com.apppubs.bean.Msg;
import com.apppubs.constant.APError;
import com.apppubs.model.APCallback;

/**
 * 测试的activity
 * 
 * Copyright (c) heaven Inc.
 *
 * Original Author: zhangwen
 *
 * ChangeLog:
 * 2015年3月17日 by zhangwen create
 *
 */
public class DebugActivity extends BaseActivity{

	
	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
//		
//		mMsgBussiness.sendTextMsg("fanyi", "ly", "南哥", new APCallback<Object>() {
//			
//			@Override
//			public void onException(int excepCode) {
//			}
//			
//			@Override
//			public void onDone(Object obj) {
//			}
//		});
		
		mMsgBussiness.getChatList("ly", "fanyi", new APCallback<List<Msg>>() {
			
			@Override
			public void onException(APError excepCode) {
			}
			
			@Override
			public void onDone(List<Msg> obj) {
				
				for(Msg m:obj){
					System.out.println("发送者："+m.getSenderId()+"接受者："+m.getReceiverUsername()+"发送时间"+m.getSendTime()+"发送内容："+m.getContent());
				}
			}
		});
		
		
		
		
	}
}
