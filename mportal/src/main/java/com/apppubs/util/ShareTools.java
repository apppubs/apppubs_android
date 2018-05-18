package com.apppubs.util;

import android.content.Context;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.apppubs.d20.R;

public class ShareTools {
	
	private Context context;

	public ShareTools(Context context) {
		this.context = context;
	}

	public void showShare(String title,String url,String picpath) {
		if (SystemUtils.canConnectNet(context)) {
			ShareSDK.initSDK(context);
			OnekeyShare oks = new OnekeyShare();
			// 关闭sso授权
			oks.disableSSOWhenAuthorize();

			// 分享时Notification的图标和文字
//			oks.setNotification(R.drawable.icon, context.getString(R.string.app_name));
			// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
			oks.setTitle(context.getString(R.string.share));
			// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
			oks.setTitleUrl("http://sharesdk.cn");
			// text是分享文本，所有平台都需要这个字段
			oks.setText(title+"  详情请点击："+url);
			// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
			oks.setImagePath(picpath);
			// url仅在微信（包括好友和朋友圈）中使用
			oks.setUrl("http://sharesdk.cn");
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用
			oks.setComment("我是测试评论文本");
			// site是分享此内容的网站名称，仅在QQ空间使用
			oks.setSite(context.getString(R.string.app_name));
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用
			oks.setSiteUrl("http://sharesdk.cn");

			// 启动分享GUI
			oks.show(context);
		} else {
			Toast.makeText(context, context.getString(R.string.err_msg_network_faile), 2000);
		}

	}

}
