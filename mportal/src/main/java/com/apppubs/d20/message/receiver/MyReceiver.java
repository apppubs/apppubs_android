package com.apppubs.d20.message.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.bean.Msg;
import com.apppubs.d20.model.MsgController;
import com.apppubs.d20.util.LogM;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...
                        
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
        	processCustomMessage(context, bundle);
        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

			String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
			String url = "";
			try {
				JSONObject extrasJson = new JSONObject(extras);
				url = extrasJson.optString("url");
				AppContext.getInstance(context).getApp().setPaddingUrlOnHomeActivityStartUp(url);

			} catch (Exception e) {
				LogM.log(this.getClass(), "Unexpected: extras is not a valid json");
			}

			HomeBaseActivity.startHomeActivity(context);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        	
        } else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
        	boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
        	Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
        } else {
        	Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} 
			else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		
		MsgController msgController = MsgController.getInstance(context);
		
		try {
			
			JSONObject jsonO = new JSONObject(message);
			
			Msg msg = new Msg();
			msg.setId(jsonO.getString("msgid"));
			msg.setTitle(jsonO.getString("title"));
			msg.setType(jsonO.getInt("type"));
			msg.setReceiverUsername(AppContext.getInstance(context).getCurrentUser().getUsername());
			if(TextUtils.isEmpty(jsonO.getString("content"))){
				msg.setContent(jsonO.getString("title"));
			}else{
				msg.setContent(jsonO.getString("content"));
			}
			
			msg.setSenderId(jsonO.getString("sender"));
			msg.setSenderName(jsonO.getString("sendername"));
			msg.setFileURL(jsonO.getString("filename"));
			msg.setContentType(jsonO.getInt("contentType"));
			msg.setInfo(jsonO.getString("msginfo"));
			
			
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
				msg.setSendTime(sdf.parse(jsonO.getString("responsetime")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(msg.getContentType()==Msg.TYPE_CONTENT_SOUND){
				msg.setLength(jsonO.getInt("length"));
			}

			msgController.execute(msg);
			
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
	}
}
