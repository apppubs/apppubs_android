package com.apppubs.d20.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.apppubs.d20.util.LogM;
import com.apppubs.d20.MportalApplication;

public class ConnectionChangeReceiver extends BroadcastReceiver {   
    @Override   
    public void onReceive( Context context, Intent intent ) {
    	LogM.log(this.getClass(), "网络状态改变");
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();   
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);   
		if (activeNetInfo != null) {
//			Toast.makeText(context, "当前网络状态 : " + activeNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
			MportalApplication.systemState.setNetworkState(activeNetInfo.getType());

		}

		if (mobNetInfo != null) {
//			Toast.makeText(context, "Mobile Network Type : " + mobNetInfo.getTypeName(), Toast.LENGTH_SHORT).show();
		}
	} 
}
