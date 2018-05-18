package com.apppubs.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.apppubs.AppContext;
import com.apppubs.constant.APError;
import com.apppubs.constant.APErrorCode;
import com.apppubs.constant.Actions;
import com.apppubs.d20.R;
import com.apppubs.model.UserBiz;
import com.apppubs.ui.activity.FirstLoginActity;

public class APErrorHandler {

    private Context mContext;

    public APErrorHandler(Context context) {
        mContext = context;
    }

    public void onError(APError error) {
        if (error.getCode() == APErrorCode.TOKEN_EXPIRE) {
            Toast.makeText(mContext, mContext.getString(R.string.err_msg_token_expire), Toast.LENGTH_LONG).show();

            AppContext.getInstance(mContext).clearCurrentUser();

            Intent closeI = new Intent(Actions.CLOSE_ALL_ACTIVITY);
            mContext.sendBroadcast(closeI);
            Intent loginIntent = new Intent(mContext, FirstLoginActity.class);
            mContext.startActivity(loginIntent);

        } else if (error.getCode() == APErrorCode.NETWORK_ERROR) {
            Toast.makeText(mContext, mContext.getString(R.string.err_msg_network_faile), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, error.getMsg(), Toast.LENGTH_LONG).show();
        }
    }


}
