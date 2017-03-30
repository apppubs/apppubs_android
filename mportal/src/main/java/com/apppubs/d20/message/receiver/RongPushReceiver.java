package com.apppubs.d20.message.receiver;

import android.content.Context;
import android.content.Intent;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.activity.HomeBaseActivity;
import com.apppubs.d20.activity.StartUpActivity;

import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;

/**
 * Created by zhangwen on 2017/1/17.
 */

public class RongPushReceiver extends PushMessageReceiver {

    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
        return false; // 返回 false, 会弹出融云 SDK 默认通知; 返回 true, 融云 SDK 不会弹通知, 通知需要由您自定义。
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
		AppContext.getInstance(context).getApp().setPaddingUrlOnHomeActivityStartUp("apppubs://message");
		HomeBaseActivity.startHomeActivity(context);
        return true; // 返回 false, 会走融云 SDK 默认处理逻辑, 即点击该通知会打开会话列表或会话界面; 返回 true, 则由您自定义处理逻辑。
    }

}
