package com.mportal.client.receiver;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.mportal.client.MportalApplication;
import com.mportal.client.activity.HomeBaseActivity;
import com.mportal.client.activity.NewsInfoActivity;
import com.mportal.client.bean.Msg;
import com.mportal.client.business.MsgController;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;

/**
 * Push消息处理receiver。请编写您需要的回调函数， 一般来说： onBind是必须的，用来处理startWork返回值；
 * onMessage用来接收透传消息； onSetTags、onDelTags、onListTags是tag相关操作的回调；
 * onNotificationClicked在通知被点击时回调； onUnbind是stopWork接口的返回值回调
 * 
 * 返回值中的errorCode，解释如下： 0 - Success 10001 - Network Problem 30600 - Internal
 * Server Error 30601 - Method Not Allowed 30602 - Request Params Not Valid
 * 30603 - Authentication Failed 30604 - Quota Use Up Payment Required 30605 -
 * Data Required Not Found 30606 - Request Time Expires Timeout 30607 - Channel
 * Token Timeout 30608 - Bind Relation Not Found 30609 - Bind Number Too Many
 * 
 * 当您遇到以上返回错误时，如果解释不了您的问题，请用同一请求的返回值requestId和errorCode联系我们追查问题。
 * 
 */
public class MyPushMessageReceiver extends PushMessageReceiver {
	
	
	
	
	
	/** TAG to Log */
	public static final String TAG = MyPushMessageReceiver.class.getSimpleName();
	
	
	
	
	/**
	 * 03-19 03:37:01.919: D/MyPushMessageReceiver(1487): onBind errorCode=0
	 * appid=5337217 userId=660223754688318456 channelId=3880482050908403871
	 * requestId=2912668001
	 * 
	 * 调用PushManager.startWork后，sdk将对push
	 * server发起绑定请求，这个过程是异步的。绑定请求的结果通过onBind返回。 如果您需要用单播推送，需要把这里获取的channel
	 * id和user id上传到应用server中，再调用server接口用channel id和user id给单个手机或者用户推送。
	 * 
	 * @param context
	 *            BroadcastReceiver的执行Context
	 * @param errorCode
	 *            绑定接口返回值，0 - 成功
	 * @param appid
	 *            应用id。errorCode非0时为null
	 * @param userId
	 *            应用user id。errorCode非0时为null
	 * @param channelId
	 *            应用channel id。errorCode非0时为null
	 * @param requestId
	 *            向服务端发起的请求id。在追查问题时有用；
	 * @return none
	 */
	@Override
	public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid=" + appid + " userId=" + userId + " channelId="
				+ channelId + " requestId=" + requestId;
		Log.d(TAG, responseString);
		MportalApplication.app.setBaiduPushUserId(userId);
		MportalApplication.commitApp(context, MportalApplication.app);
		// 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
		if (errorCode == 0) {
			Utils.setBind(context, true);
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//		updateContent(context, responseString);
	}

	/**
	 * 接收透传消息的函数。
	 * 
	 * @param context
	 *            上下文
	 * @param message
	 *            推送的消息
	 * @param customContentString
	 *            自定义内容,为空或者json字符串
	 */
	@Override
	public void onMessage(Context context, String message, String customContentString) {
		String messageString = "透传消息 message=\"" + message + "\" customContentString=" + customContentString;
		/**
		 * json示例：{title:"标题",desc:"描述",type:0,extra:{[json对象]}}
		 * {title:"",content:"最新通知",type:2,'contentType':'1','filename':'','length':'0','sender':'1430045959585',url:""}

		 * 
		 * type=0:新闻类型
		 * extra:格式：{url:"http://www.baidu.com/"}
		 * 
		 * type=1:聊天消息
		 * extra格式：{"contentType":1,"content":"内容","filename":"http://www.xxxx.com/xxxx.jpg","length","20000","sender":"xiwang"};
		 * 
		 * type=2:服务号推送
		 * extra格式:{"contentType":1,"content":"内容","filename":"http://www.xxxx.com/xxxx.jpg","length","20000","sender":"xiwang"};
		 * contentType: int 信息媒体类型 1==文本；2==图片;3==声音；4==文件
		 * filename: String 附件地址（图片，声音，文件类型会有文本类型为""）
		 * length: int 长度 （如果是图片则此处为图片或文件此处单位为byte，如果为声音此处单位为millisecond）
		 * sender: String 发送者用户名
		 * 
		 * type=3//三方推送
		 * extra格式：{url:"http://www.baidu.com/"}
		 * 
		 * 
		 */
		MsgController msgController = MsgController.getInstance(context);
		
		Log.d(TAG, messageString);
		
		
		try {
			
			JSONObject jsonO = new JSONObject(message);
			
			Msg msg = new Msg();
			msg.setTitle(jsonO.getString("title"));
			msg.setType(jsonO.getInt("type"));
			msg.setReceiverUsername(MportalApplication.user.getUsername());
			msg.setContent(jsonO.getString("content"));
			msg.setSenderId(jsonO.getString("sender"));
			msg.setFileURL(jsonO.getString("filename"));
			msg.setContentType(jsonO.getInt("contenttype"));
			msg.setInfo(jsonO.getString("msginfo"));
			msg.setSenderName(jsonO.getString("sendername"));
			if(msg.getContentType()==Msg.TYPE_CONTENT_SOUND){
				msg.setLength(jsonO.getInt("length"));
			}

			msgController.execute(msg);
			
			
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		
		

		
		
	}

	/**
	 * 接收通知点击的函数。注：推送通知被用户点击前，应用无法通过接口获取通知的内容。
	 * 
	 * @param context
	 *            上下文
	 * @param title
	 *            推送的通知的标题
	 * @param description
	 *            推送的通知的描述
	 * @param customContentString
	 *            自定义内容，为空或者json字符串
	 */
	@Override
	public void onNotificationClicked(Context context, String title, String description, String customContentString) {
		String notifyString = "通知点击 title=\"" + title + "\" description=\"" + description + "\" customContent="
				+ customContentString;
		Log.d(TAG, notifyString);

		// 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
		if (!TextUtils.isEmpty(customContentString)) {
			JSONObject customJson = null;
			try {
				customJson = new JSONObject(customContentString);
				String myvalue = null;
				if (!customJson.isNull("id")) {
					myvalue = customJson.getString("id");
				}

				LogM.log(this.getClass(), "myvalue：" + myvalue);
				Intent intent = new Intent(context, NewsInfoActivity.class);
				String[] valueArr = myvalue.split("\\|");
				LogM.log(this.getClass(), valueArr.toString());
				intent.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID, valueArr[1]);
				intent.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, "");
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
				List<RunningTaskInfo> appTask = am.getRunningTasks(1);
				if (appTask.size() > 0 && appTask.get(0).baseActivity.getPackageName().equals("com.mportal.client")) {
					context.startActivity(intent);
					return;
				} else {
					// startActivity(intent);
					HomeBaseActivity.startHomeActivity(context);
					context.startActivity(intent);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//		updateContent(context, notifyString);
	}

	/**
	 * setTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经设置成功；非0表示所有tag的设置均失败。
	 * @param successTags
	 *            设置成功的tag
	 * @param failTags
	 *            设置失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onSetTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//		updateContent(context, responseString);
	}

	/**
	 * delTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示某些tag已经删除成功；非0表示所有tag均删除失败。
	 * @param successTags
	 *            成功删除的tag
	 * @param failTags
	 *            删除失败的tag
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onDelTags(Context context, int errorCode, List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode + " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//		updateContent(context, responseString);
	}

	/**
	 * listTags() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示列举tag成功；非0表示失败。
	 * @param tags
	 *            当前应用设置的所有tag。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onListTags(Context context, int errorCode, List<String> tags, String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags=" + tags;
		Log.d(TAG, responseString);

		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//		updateContent(context, responseString);
	}

	/**
	 * PushManager.stopWork() 的回调函数。
	 * 
	 * @param context
	 *            上下文
	 * @param errorCode
	 *            错误码。0表示从云推送解绑定成功；非0表示失败。
	 * @param requestId
	 *            分配给对云推送的请求的id
	 */
	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode + " requestId = " + requestId;
		Log.d(TAG, responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			Utils.setBind(context, false);
		}
		// Demo更新界面展示代码，应用请在这里加入自己的处理逻辑
//		updateContent(context, responseString);
	}

	@Override
	public void onNotificationArrived(Context arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		System.out.println("onNotificationArrived");
	}

//	private void updateContent(Context context, String content) {
//		Log.d(TAG, "updateContent");
//		String logText = "" + Utils.logStringCache;
//
//		if (!logText.equals("")) {
//			logText += "\n";
//		}
//
//		SimpleDateFormat sDateFormat = new SimpleDateFormat("HH-mm-ss");
//		logText += sDateFormat.format(new Date()) + ": ";
//		logText += content;
//
//		Utils.logStringCache = logText;
//
//		// Intent intent = new Intent();
//		// intent.setClass(context.getApplicationContext(),
//		// PushDemoActivity.class);
//		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		// context.getApplicationContext().startActivity(intent);
//	}

}
