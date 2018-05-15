package com.apppubs.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.apppubs.constant.Constants;
import com.apppubs.util.SharedPreferenceUtils;

/**
 * Created by siger on 2018/1/19.
 */

public class DownloadAppReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long myRefId = SharedPreferenceUtils.getInstance(context).getLong(Constants
                .DEFAULT_SHARED_PREFERENCE_NAME, Constants
                .SHAREDPREFRERENCE_KEY_DOWNLOAD_REFERENCE, -1);
        long curDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if (myRefId > 0 && myRefId == curDownloadId) {
            String serviceString = Context.DOWNLOAD_SERVICE;
            DownloadManager dManager = (DownloadManager) context.getSystemService(serviceString);
            Intent install = new Intent(Intent.ACTION_VIEW);
            Uri downloadFileUri = dManager.getUriForDownloadedFile(curDownloadId);
            if (downloadFileUri != null) {
                install.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            } else {
                Toast.makeText(context, "更新失败！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
