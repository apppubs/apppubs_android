package com.apppubs.d20.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.apppubs.d20.bean.NewsInfo;
import com.apppubs.d20.myfile.FilePreviewFragment;
import com.apppubs.d20.webapp.WebAppFragment;

/**
 * 信息正文的基础类
 * @author zhangwen
 *
 */
public abstract class  NewsInfoBaseActivity extends BaseActivity{

	
	/**
	 * 
	 * @param context
	 * @param type
	 * @param params 参数，如果是普通信息，视频新闻，音频新闻则传入channelCode和infoId，如果是图片新闻则传入channelCode,
	 * 如果是专题，则传入专题的url，如果是url类型则传入url地址。
	 */
	public static void startInfoActivity(Context context,String type,String... params){
		if(type.equals(NewsInfo.NEWS_TYPE_NORAML)){
			Intent i = new Intent(context,NewsInfoActivity.class);
			i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,  params[0]);
			i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID,params[1]);
			context.startActivity(i);
		}else if(type.equals(NewsInfo.NEWS_TYPE_PICTURE)){
			Intent intent = new Intent(context,NewsPictureInfoActivity.class);
			intent.putExtra(NewsPictureInfoActivity.EXTRA_STRING_NAME_ID,params[1]);
			context.startActivity(intent);
		}else if(type.equals(NewsInfo.NEWS_TYPE_VIDEO)){
			Intent intent = new Intent(context,NewsVideoInfoActivity.class);
			intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_ID,params[1]);
			intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,  params[0]);
			context.startActivity(intent);
		}else if(type.equals(NewsInfo.NEWS_TYPE_AUDIO)){
			Intent intent = new Intent(context,NewsAudioInfoActivity.class);
			intent.putExtra(NewsAudioInfoActivity.EXTRA_STRING_NAME_ID,params[1]);
			intent.putExtra(NewsAudioInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,  params[0]);
			context.startActivity(intent);
		}else if(type.equals(NewsInfo.NEWS_TYPE_SPECIALS)){
			Bundle bundle = new Bundle();
			bundle.putString(WebAppFragment.ARGUMENT_STRING_URL,params[0]);
			ContainerActivity.startActivity(context, WebAppFragment.class,bundle);
		}else if(type.equals(NewsInfo.NEWS_TYPE_URL)){
			Bundle extras = new Bundle();
			extras.putString(WebAppFragment.ARGUMENT_STRING_URL, params[0]);
			ContainerActivity.startActivity(context, WebAppFragment.class,extras);
		}else if(type.equals(NewsInfo.NEWS_TYPE_FILE)){
			Bundle args = new Bundle();
			args.putString(FilePreviewFragment.ARGS_STRING_URL, params[0]);
			ContainerActivity.startActivity(context, FilePreviewFragment.class, args, "文件预览");
		}
	}
	
}
