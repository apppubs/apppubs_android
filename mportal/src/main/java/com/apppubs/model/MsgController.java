package com.apppubs.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.SparseIntArray;

import com.apppubs.bean.Msg;
import com.apppubs.constant.Actions;
import com.apppubs.constant.URLs;
import com.apppubs.AppContext;
import com.apppubs.ui.webapp.WebAppFragment;
import com.apppubs.ui.start.StartUpActivity;
import com.apppubs.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.activity.ChatActivity;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.activity.HomeBaseActivity;
import com.apppubs.ui.activity.NewsInfoActivity;
import com.apppubs.ui.activity.WebAppActivity;
import com.apppubs.util.SharedPreferenceUtils;

/**
 * 消息控制器，msg跳转
 * 
 * 每个人或者服务号均只能创建一个通知，新的聊天消息通知覆盖旧的信息，有某一个人的多条未读信息时在通知栏标明。
 * 同理一个服务号只能创建一个通知。
 * 聊天的通知通过用户名（转换为int）唯一标志，服务号通过服务号id来标志
 */
public class MsgController {

	private Context mContext;
	private String mCurTargetChatGroupId;// 当前聊天的对方的用户名
	private boolean isMsgListVisiable;//信息列表是否可见
	/**
	 * 存放发送这用户名和通知id对应数字的map，每个username对应一个整数，此整数作为notification的id唯一标志notification
	 */
	private Map<String,Integer> mMsgSenderusernameAndNotificationIdMap;
	
	/**
	 * 存放每个通知的未读数量
	 */
	private SparseIntArray mUnreadNumArr;

	private MsgController(Context context) {
		mContext = context;
		mMsgSenderusernameAndNotificationIdMap = new HashMap<String, Integer>();
		mUnreadNumArr = new SparseIntArray();
	}

	private static MsgController sMsgController;

	public static MsgController getInstance(Context context) {

		if (sMsgController == null) {
			sMsgController = new MsgController(context);
		}
		return sMsgController;
	}

	@SuppressLint("NewApi")
	public void execute(final Msg msg) {

		//当前界面为聊天界面，且聊天人恰好是新消息发送者时不显示通知只进行声音提醒和消息刷新。
		if (mCurTargetChatGroupId != null && mCurTargetChatGroupId.equals(msg.getSenderId())) {
			Intent i = new Intent(Actions.ACTION_MESSAGE);
			Bundle b = new Bundle();
			i.putExtras(b);
			i.putExtra(Actions.EXTRA_MSG, msg);
			mContext.sendBroadcast(i);

			MediaPlayer mp = new MediaPlayer();
			try {
				mp.setDataSource(mContext, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
				mp.prepare();
				mp.setLooping(false);
				mp.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (AppContext.getInstance(mContext).getSettings().isNeedPushNotification()) {
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

			PendingIntent pi = PendingIntent.getActivities(mContext, 1,
					makeIntentStack(mContext, msg.getInfo(), msg.getType(), msg.getSenderId(),msg.getSenderName()),
					PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
			// 当用户名和通知id对不存在时创建一个通知id，存在时增加通知的未读数
			if (!mMsgSenderusernameAndNotificationIdMap.containsKey(msg.getSenderId())) {
				int notificationId = mMsgSenderusernameAndNotificationIdMap.size();// 将当前map包含的键值对数量当做通知的id
				mMsgSenderusernameAndNotificationIdMap.put(msg.getSenderId(), notificationId);
				mUnreadNumArr.put(notificationId, 1);
			} else {
				int curUnReadNum = mUnreadNumArr.get(mMsgSenderusernameAndNotificationIdMap.get(msg.getSenderId()));
				mUnreadNumArr.put(mMsgSenderusernameAndNotificationIdMap.get(msg.getSenderId()), ++curUnReadNum);
			}
			// 通知为阅读数大于1时给予数量提示
			int notificationId = mMsgSenderusernameAndNotificationIdMap.get(msg.getSenderId());
			int unReadNum = mUnreadNumArr.get(notificationId);
			if (unReadNum > 1) {
				Notification notification = builder.setContentIntent(pi).setSmallIcon(R.drawable.icon)
						.setContentTitle(msg.getSenderName())
						.setContentText("[" + unReadNum + "条] " + msg.getContent()).setAutoCancel(true)
						.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).build();
				notificationManager.notify(notificationId, notification);
			} else {
				Notification notification = builder.setContentIntent(pi).setSmallIcon(R.drawable.icon)
						.setContentTitle(msg.getSenderName()).setContentText(msg.getContent()).setAutoCancel(true)
						.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).build();
				notificationManager.notify(notificationId, notification);
			}
		}
		
		//当前的界面在信息列表界面时刷新信息列表
		if(isMsgListVisiable){
			Intent i = new Intent(Actions.ACTION_REFRESH_CHAT_RECORD_LIST);
			mContext.sendBroadcast(i);
		}
		int type = msg.getType();
		if (type == Msg.TYPE_CHAT) {
			
		} else if (type == Msg.TYPE_SYSTEM || type == Msg.TYPE_THIRD_PARTY) {
		} else if (type == Msg.TYPE_CMS) {
		} else if (type == Msg.TYPE_THIRD_PARTY) {

		}

	}

	public String getCurTargetChatGroupId() {
		return mCurTargetChatGroupId;
	}

	public void setCurChatGroupId(String curTargetUsername) {
		this.mCurTargetChatGroupId = curTargetUsername;
	}
	
	public boolean isMsgListVisiable() {
		return isMsgListVisiable;
	}

	public void setMsgListVisiable(boolean isMsgListVisiable) {
		this.isMsgListVisiable = isMsgListVisiable;
	}

	/**
	 * 通过用户名或者服务号id来清空通知
	 * @param username
	 */
	public void cancelNotificationBySenderId(String sender){
		if(mMsgSenderusernameAndNotificationIdMap.containsKey(sender)){
			int notificationId = mMsgSenderusernameAndNotificationIdMap.get(sender);
			NotificationManager notificationManager = (NotificationManager) mContext
					.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(notificationId);
			mUnreadNumArr.put(notificationId, 0);
		}
		
	}
	
	protected static boolean isTopActivity(Activity activity) {
		String packageName = "com.apppubs.d20";
		ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		if (tasksInfo.size() > 0) {
			System.out.println("---------------包名-----------" + tasksInfo.get(0).topActivity.getPackageName());
			// 应用程序位于堆栈的顶层
			if (packageName.equals(tasksInfo.get(0).topActivity.getPackageName())) {
				return true;
			}
		}
		return false;
	}

	private boolean isRunningForeground(Context context) {
		boolean isRunningForeground = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String currentPackageName = cn.getPackageName();
		if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName())) {
			isRunningForeground = true;
		}
		return isRunningForeground;
	}

	@SuppressLint("NewApi")
	Intent[] makeIntentStack(Context context, String msgInfo, int messageType,String sender,String title) {
		
		LogM.log(this.getClass(), "makeIntentStack:" + msgInfo);
		Intent[] intents = null;
		if(messageType==Msg.TYPE_CMS){
			if (isRunningForeground(mContext)) {
				intents = new Intent[1];
				intents[0] = new Intent(context, NewsInfoActivity.class);
				String[] arr = msgInfo.split("\\|");
				Bundle extras = new Bundle();
				extras.putString(NewsInfoActivity.EXTRA_STRING_NAME_ID, arr[0]);
				extras.putString(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, arr[1]);
				intents[0].putExtras(extras);
			} else {
				intents = new Intent[2];
				intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
						StartUpActivity.class));

				intents[1] = new Intent(context, NewsInfoActivity.class);
				String[] arr = msgInfo.split("\\|");
				Bundle extras = new Bundle();
				extras.putString(NewsInfoActivity.EXTRA_STRING_NAME_ID, arr[0]);
				extras.putString(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, arr[1]);
				intents[1].putExtras(extras);

			}
		}else if(messageType==Msg.TYPE_SYSTEM){
			if (isRunningForeground(mContext)) {
				intents = new Intent[1];
				intents[0] = new Intent(context, WebAppActivity.class);
				Bundle extras = new Bundle();
				String url = String.format(URLs.URL_SERVICEINFO,URLs.baseURL,URLs.appCode) + "&serviceinfo_id=" + msgInfo + "&service_id="
				+ sender;
				extras.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
				extras.putBoolean(WebAppActivity.EXTRA_NAME_BOOL_NEED_CLEAR_SERVERCE_NO_UNREAD_NUM, true);
				extras.putString(WebAppActivity.EXTRA_NAME_STRING_SERVICE_NO_INFO_ID, msgInfo);
				intents[0].putExtras(extras);
				
			} else {
				intents = new Intent[2];
				intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
						StartUpActivity.class));

				intents[1] =  new Intent(context, WebAppActivity.class);
				Bundle extras = new Bundle();
				String url = String.format(URLs.URL_SERVICEINFO, URLs.baseURL,URLs.appCode) + "&serviceinfo_id=" + msgInfo + "&service_id="+ sender;
				extras.putString(WebAppFragment.ARGUMENT_STRING_URL, url);
				intents[1].putExtras(extras);

			}
		}else if(messageType==Msg.TYPE_THIRD_PARTY){
			if (isRunningForeground(mContext)) {
				intents = new Intent[1];
				Intent intent = new Intent(context,ContainerActivity.class);
				intent.putExtra(ContainerActivity.EXTRA_FRAGMENT_CLASS_NAME, WebAppFragment.class.getName());
				intent.putExtra(WebAppFragment.ARGUMENT_STRING_URL, msgInfo);
				intents[0] = intent;
				
			} else {
				intents = new Intent[2];
				intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
						StartUpActivity.class));
				Intent intent = new Intent(context,ContainerActivity.class);
				intent.putExtra(ContainerActivity.EXTRA_FRAGMENT_CLASS_NAME, WebAppFragment.class.getName());
				intent.putExtra(WebAppFragment.ARGUMENT_STRING_URL, msgInfo);
				intents[1] = intent;

			}
		}else if(messageType==Msg.TYPE_CHAT){
			if (SharedPreferenceUtils.getInstance(mContext).getBoolean(HomeBaseActivity.MPORTAL_PREFERENCE_NAME, HomeBaseActivity.MPORTAL_PREFERENCE_APP_RUNNING_KEY, false)) {
				intents = new Intent[1];
				Intent intent = new Intent(context,ChatActivity.class);
				intent.putExtra(ChatActivity.EXTRA_STRING_CHAT_ID, sender);
				intents[0] = intent;
				
			} else {
				intents = new Intent[2];
				intents[0] = Intent.makeRestartActivityTask(new ComponentName(context,
						StartUpActivity.class));
				intents[0].putExtra(BaseActivity.EXTRA_STRING_TITLE, title);
				Intent intent = new Intent(context,ChatActivity.class);
				intent.putExtra(ChatActivity.EXTRA_STRING_CHAT_ID, sender);
				intents[1] = intent;

			}
		}
		
		return intents;
	}

}
