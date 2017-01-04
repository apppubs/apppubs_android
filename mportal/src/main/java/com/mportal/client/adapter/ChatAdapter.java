package com.mportal.client.adapter;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.ChatPicInfoActivity;
import com.mportal.client.activity.ChatVideoInfoActivity;
import com.mportal.client.asytask.AsyTaskCallback;
import com.mportal.client.asytask.AsyTaskExecutor;
import com.mportal.client.bean.Msg;
import com.mportal.client.bean.User;
import com.mportal.client.util.LogM;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 聊天的adapter
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月25日 by zhangwen create
 * 
 */
public class ChatAdapter extends BaseAdapter implements OnClickListener ,AsyTaskCallback{

	
	private static final int TYPE_MESSAGE_LEFT_TEXT = 0x00010001;
	private static final int TYPE_MESSAGE_RIGHT_TEXT = 0x00020001;
	private static final int TYPE_MESSAGE_LEFT_IMAGE = 0x00010002;
	private static final int TYPE_MESSAGE_RIGHT_IMAGE = 0x00020002;
	private static final int TYPE_MESSAGE_LEFT_SOUND = 0x00010003;
	private static final int TYPE_MESSAGE_RIGHT_SOUND = 0x00020003;
	private static final int TYPE_MESSAGE_LEFT_VIDEO = 0x00010004;
	private static final int TYPE_MESSAGE_RIGHT_VIDEO = 0x00020004;
	
	private static final int TYPE_LOCATION_LEFT = 0x00010000;
	private static final int TYPE_LOCATION_RIGHT = 0x00020000;
	private static final int TYPE_CONTENT_TEXT = 0x00000001;
	private static final int TYPE_CONTENT_IMAGE = 0x00000002;
	private static final int TYPE_CONTENT_SOUND = 0x00000003;
	private static final int TYPE_CONTENT_VIDEO = 0x00000004;
	private static final int CONTENT_TYPE_MODE = 0x00001111;
	private static final int LOCATION_TYPE_MODE = 0x11110000; 
	
	
	private final int ASY_TASK_TAG_PLAY = 1;
	
	private Context mContext;
	private List<Msg> mList;
	private LayoutInflater mInflater;
	private User mCurUser;//当前登录的用户
	private ImageLoader mImageLoader;
	private DisplayImageOptions mImageLoaderOptions;
	private boolean isVoicePlaying;
	private MediaPlayer mMediaPlayer;
	private String mPlayingVoiceSrc;
	
	public ChatAdapter(Context context) {
		mContext = context;
		mList = new ArrayList<Msg>();
		mInflater = LayoutInflater.from(context);
		mCurUser = MportalApplication.user;//
		mImageLoader = ImageLoader.getInstance();
		Drawable drawable = Drawable.createFromPath(context.getFilesDir().getAbsolutePath()+File.separator+"stance.png");
		mImageLoaderOptions = new DisplayImageOptions.Builder()
		.showImageOnLoading(drawable)
		.showImageForEmptyUri(drawable)
		.showImageOnFail(drawable)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
	}

	public void setDate(List<Msg> list) {
		mList = list;
	}

	@Override
	public int getCount() {
		return mList.size();

	}

	@Override
	public Object getItem(int arg0) {
		System.out.println("聊天adapter  getItem");
		return "";
	}

	@Override
	public long getItemId(int arg0) {
		System.out.println("聊天adapter  getItemId");
		return 0;
	}
	public void addItem(Msg msg){
		mList.add(msg);
	}
	class ViewHolder {
		
		private ProgressBar progressBar;
		private RelativeLayout me, other;
		private LinearLayout voiceMeLl, voiceOtherLl;
		private ImageView meIconIv,otherIconIv,voiceMeIv, voiceOtherIv, sendFail, mePicIv, otherPicIv,meVideoIv,otherVideoIv;
		private TextView meContentTv, otherContentTv,otherNameTv, chatTime, voiceMeLengthTv,voiceOtherLengthTv;
		private RelativeLayout picMeLay, picOtherLay;
	}

	/**
	 * 通过信息类别来创建view
	 * @param context
	 * @param itemType
	 * @return
	 */
	private View newView(Context context, int itemType) {
		
		View contentView = null;
		ViewHolder holder = new ViewHolder();
		switch (itemType) {
		case TYPE_MESSAGE_LEFT_TEXT:
			contentView = mInflater.inflate(R.layout.item_chat_text_left, null);
			holder.otherContentTv = (TextView) contentView.findViewById(R.id.chat_content_left_tv);
			holder.otherNameTv = (TextView) contentView.findViewById(R.id.chat_content_left_name_tv);
			holder.otherIconIv = (ImageView) contentView.findViewById(R.id.chat_icon_left_iv);
			
			break;
		case TYPE_MESSAGE_RIGHT_TEXT:
			contentView = mInflater.inflate(R.layout.item_chat_text_right, null);
			holder.meContentTv = (TextView) contentView.findViewById(R.id.chat_content_right_tv);
			holder.meIconIv = (ImageView) contentView.findViewById(R.id.chat_icon_right_iv);
			break;
		case TYPE_MESSAGE_LEFT_IMAGE:
			contentView = mInflater.inflate(R.layout.item_chat_img_left, null);
			holder.otherPicIv = (ImageView) contentView.findViewById(R.id.chat_img_left_iv);
			holder.otherNameTv = (TextView) contentView.findViewById(R.id.chat_content_left_name_tv);
			break;
		case TYPE_MESSAGE_RIGHT_IMAGE:
			contentView = mInflater.inflate(R.layout.item_chat_img_right, null);
			holder.mePicIv = (ImageView) contentView.findViewById(R.id.chat_img_right_iv);
			break;
		case TYPE_MESSAGE_LEFT_SOUND:
			contentView = mInflater.inflate(R.layout.item_chat_sound_left, null);
			holder.voiceOtherLl = (LinearLayout) contentView.findViewById(R.id.chat_voice_left_ll);
			holder.voiceOtherLengthTv = (TextView) contentView.findViewById(R.id.chat_voice_length_left_tv);
			holder.otherNameTv = (TextView) contentView.findViewById(R.id.chat_content_left_name_tv);
			break;
		case TYPE_MESSAGE_RIGHT_SOUND:
			contentView = mInflater.inflate(R.layout.item_chat_sound_right, null);
			holder.voiceMeLl = (LinearLayout) contentView.findViewById(R.id.chat_voice_right_ll);
			holder.voiceMeLengthTv = (TextView) contentView.findViewById(R.id.chat_voice_length_right_tv);
			
			break;
		case TYPE_MESSAGE_RIGHT_VIDEO:
			contentView = mInflater.inflate(R.layout.item_chat_video_right, null);
			holder.meVideoIv = (ImageView) contentView.findViewById(R.id.chat_video_right_iv);
			break;
		case TYPE_MESSAGE_LEFT_VIDEO:
			contentView = mInflater.inflate(R.layout.item_chat_video_left, null);
			holder.otherVideoIv = (ImageView) contentView.findViewById(R.id.chat_video_left_iv);
			holder.otherNameTv = (TextView) contentView.findViewById(R.id.chat_content_left_name_tv);
			break;
		default:
			break;
		}
		holder.chatTime = (TextView) contentView.findViewById(R.id.chat_time_tv);
				
		if (contentView != null) {
			contentView.setTag(holder);
		}
		return contentView;
	}

	private View bindView(View convertView,int itemType){
		ViewHolder holder = (ViewHolder) convertView.getTag();
		switch (itemType) {
		case TYPE_MESSAGE_LEFT_TEXT:
			convertView = mInflater.inflate(R.layout.item_chat_text_left, null);
			holder.otherContentTv = (TextView) convertView.findViewById(R.id.chat_content_left_tv);
			holder.otherNameTv = (TextView) convertView.findViewById(R.id.chat_content_left_name_tv);
			holder.otherIconIv = (ImageView) convertView.findViewById(R.id.chat_icon_left_iv);
			break;
		case TYPE_MESSAGE_RIGHT_TEXT:
			convertView = mInflater.inflate(R.layout.item_chat_text_right, null);
			holder.meContentTv = (TextView) convertView.findViewById(R.id.chat_content_right_tv);
			holder.meIconIv = (ImageView) convertView.findViewById(R.id.chat_icon_right_iv);
			break;
		case TYPE_MESSAGE_LEFT_IMAGE:
			convertView = mInflater.inflate(R.layout.item_chat_img_left, null);
			holder.otherPicIv = (ImageView) convertView.findViewById(R.id.chat_img_left_iv);
			holder.otherNameTv = (TextView) convertView.findViewById(R.id.chat_content_left_name_tv);
			break;
		case TYPE_MESSAGE_RIGHT_IMAGE:
			convertView = mInflater.inflate(R.layout.item_chat_img_right, null);
			holder.mePicIv = (ImageView) convertView.findViewById(R.id.chat_img_right_iv);
			break;
		case TYPE_MESSAGE_LEFT_SOUND:
			convertView = mInflater.inflate(R.layout.item_chat_sound_left, null);
			holder.voiceOtherLl = (LinearLayout) convertView.findViewById(R.id.chat_voice_left_ll);
			holder.voiceOtherLengthTv = (TextView) convertView.findViewById(R.id.chat_voice_length_left_tv);
			holder.otherNameTv = (TextView) convertView.findViewById(R.id.chat_content_left_name_tv);
			break;
		case TYPE_MESSAGE_RIGHT_SOUND:
			convertView = mInflater.inflate(R.layout.item_chat_sound_right, null);
			holder.voiceMeLl = (LinearLayout) convertView.findViewById(R.id.chat_voice_right_ll);
			holder.voiceMeLengthTv = (TextView) convertView.findViewById(R.id.chat_voice_length_right_tv);
			break;
		case TYPE_MESSAGE_RIGHT_VIDEO:
			convertView = mInflater.inflate(R.layout.item_chat_video_right, null);
			holder.meVideoIv = (ImageView) convertView.findViewById(R.id.chat_video_right_iv);
			break;
		case TYPE_MESSAGE_LEFT_VIDEO:
			convertView = mInflater.inflate(R.layout.item_chat_video_left, null);
			holder.otherVideoIv = (ImageView) convertView.findViewById(R.id.chat_video_left_iv);
			holder.otherNameTv = (TextView) convertView.findViewById(R.id.chat_content_left_name_tv);
			break;
		default:
			break;
		}
		holder.chatTime = (TextView) convertView.findViewById(R.id.chat_time_tv);
		convertView.setTag(holder);
		return convertView;
	}
	
	private void fillView(ViewHolder holder,Msg msg,Msg lastMsg,int itemType,int position){
		
		switch (itemType) {
		case TYPE_MESSAGE_LEFT_TEXT:
			holder.otherContentTv.setText(msg.getContent());
			holder.otherNameTv.setText(msg.getSenderName());
			break;
		case TYPE_MESSAGE_RIGHT_TEXT:
			holder.meContentTv.setText(msg.getContent());
			break;
		case TYPE_MESSAGE_LEFT_IMAGE:
			if(msg.getPicLocation()!=null){
				mImageLoader.displayImage(msg.getPicLocation(), holder.otherPicIv,mImageLoaderOptions);
			}else{
				mImageLoader.displayImage(msg.getFileURL(), holder.otherPicIv,mImageLoaderOptions);
			}
			holder.otherPicIv.setTag(msg);
			holder.otherPicIv.setOnClickListener(this);
			holder.otherNameTv.setText(msg.getSenderName());
			break;
		case TYPE_MESSAGE_RIGHT_IMAGE:
			if(msg.getPicLocation()!=null){
				
				mImageLoader.displayImage(msg.getPicLocation(), holder.mePicIv,mImageLoaderOptions);
			}else{
				mImageLoader.displayImage(msg.getFileURL(), holder.mePicIv,mImageLoaderOptions);
			}
			holder.mePicIv.setTag(msg);
			holder.mePicIv.setOnClickListener(this);
			break;
		case TYPE_MESSAGE_LEFT_SOUND:
			holder.voiceOtherLl.setOnClickListener(this);
			holder.voiceOtherLl.setTag(msg);
			int sec  = msg.getLength()/1000;
			holder.voiceOtherLengthTv.setText(String.format("%1$d\"",sec));
			TextView blankTvOther = (TextView) holder.voiceOtherLl.findViewById(R.id.chat_voice_length_blank_left_tv);
			blankTvOther.setText(generateBlank(sec));
			holder.otherNameTv.setText(msg.getSenderName());
			break;
		case TYPE_MESSAGE_RIGHT_SOUND:
			holder.voiceMeLl.setOnClickListener(this);
			holder.voiceMeLl.setTag(msg);
			
			int second  = msg.getLength()/1000;
			holder.voiceMeLengthTv.setText(String.format("%1$d\"",second));
			TextView blankTv = (TextView) holder.voiceMeLl.findViewById(R.id.chat_voice_length_blank_right_tv);
			blankTv.setText(generateBlank(second));
			break;
		case TYPE_MESSAGE_LEFT_VIDEO:
			holder.otherVideoIv.setTag(msg);
			holder.otherVideoIv.setOnClickListener(this);
			String videoPic = msg.getFileURL().substring(0,msg.getFileURL().lastIndexOf("."))+".jpg";
			mImageLoader.displayImage(videoPic, holder.otherVideoIv);
			holder.otherNameTv.setText(msg.getSenderName());
			break;
		case TYPE_MESSAGE_RIGHT_VIDEO:
			holder.meVideoIv.setTag(msg);
			holder.meVideoIv.setOnClickListener(this);
			String mineVideoPic = msg.getFileURL().substring(0,msg.getFileURL().lastIndexOf("."))+".jpg";
			mImageLoader.displayImage(mineVideoPic, holder.meVideoIv);
			break;
		default:
			break;
		}
		
		if(lastMsg.getSendTime()==null){
			holder.chatTime.setVisibility(View.VISIBLE);
			holder.chatTime.setText(DateFormat.getDateTimeInstance().format(msg.getSendTime()));
		}else if(msg.getSendTime().getTime()-lastMsg.getSendTime().getTime()>60*1000*5){
			holder.chatTime.setText(DateFormat.getDateTimeInstance().format(msg.getSendTime()));
		}else{
			holder.chatTime.setVisibility(View.GONE);
		}
	}
	private String generateBlank(int second){
		StringBuilder sb = new StringBuilder();
		if(second<9){
			for(int i=-1;++i<second;){
				sb.append("  ");
			}
		}else{
			return "                    ";
		}
		return sb.toString();
	}
	private int getMessageType(Msg msg,String curUserName){
		
		int messageType = 0;
		
		if(curUserName.equals(msg.getSenderId())){
			//自己发的信息
			messageType += TYPE_LOCATION_RIGHT;
		}else{
			messageType += TYPE_LOCATION_LEFT;
		}
		int contentType = msg.getContentType();
		switch (contentType) {
		case Msg.TYPE_CONTENT_TEXT:
			messageType += TYPE_CONTENT_TEXT;
			break;
		case Msg.TYPE_CONTENT_IMAGE:
			messageType += TYPE_CONTENT_IMAGE;
			break;
		case Msg.TYPE_CONTENT_SOUND:
			messageType +=TYPE_CONTENT_SOUND;
			break;
		case Msg.TYPE_CONTENT_VIDEO:
			messageType += TYPE_CONTENT_VIDEO;
		default:
			break;
		}
		return messageType;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		Msg msg = mList.get(position);
		Msg lastMsg = mList.get(position>1?position-1:position);
		LogM.log(this.getClass(), "getView-->当前内容："+msg.getContent()+"当前位置："+position);
		int messageType = getMessageType(msg, mCurUser.getUsername());
		if(convertView==null){
			convertView = newView(mContext, messageType);
		}else{
			convertView = bindView(convertView, messageType);
		}
		fillView((ViewHolder)convertView.getTag(),msg,lastMsg,messageType, position);
		
		if(((ViewHolder)convertView.getTag()).otherContentTv!=null){
			LogM.log(this.getClass(), "getView-->当前左边内容："+((ViewHolder)convertView.getTag()).otherContentTv.getText());
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Msg msg = (Msg) v.getTag();
		switch (id) {
		case R.id.chat_voice_left_ll:
			LogM.log(this.getClass(), "空");
			if(msg.getFileURL()==null||msg.getFileURL().equals("")){
				Toast.makeText(mContext, "语音播放错误", Toast.LENGTH_SHORT).show();
			}else{
				playSound((msg.getFileURL()));
			}
			break;
		case R.id.chat_voice_right_ll:
			
			if(msg.getFileURL()==null||msg.getFileURL().equals("")){
				
				playSound(msg.getVoiceLocation());
			}else{
				playSound((msg.getFileURL()));
			}
			break;
		case R.id.chat_img_left_iv:
		case R.id.chat_img_right_iv:
			if(msg.getPicLocation()!=null){
				
				disPlayImage((ImageView)v,msg.getPicLocation());
			}else{
				disPlayImage((ImageView)v,msg.getFileURL());
			}
			
			break;
		case R.id.chat_video_left_iv:
		case R.id.chat_video_right_iv:
			displayVideo(msg.getFileURL());
			break;
		default:
			break;
		}
		
	}
	private void playSound(String src ) {
		LogM.log(this.getClass(), "得到音频位置"+src);
		if(isVoicePlaying&&src!=null&&src.equals(mPlayingVoiceSrc)){
			return;
		}
		
		stopSound();
		
		AsyTaskExecutor.getInstance().startTask(ASY_TASK_TAG_PLAY, this, new String[]{src});

	}
	
	private void stopSound(){
		isVoicePlaying = false;
		if(mMediaPlayer!=null){
			mMediaPlayer.stop();
		}
	}
	
	private void disPlayImage(ImageView iv,String uri){
		LogM.log(this.getClass(), "显示");
		Intent i = new Intent(mContext, ChatPicInfoActivity.class);
		i.putExtra(ChatPicInfoActivity.EXTRA_PIC_URI, uri);
		mContext.startActivity(i);
				
	}
	
	private void displayVideo(String uri){
		Intent i = new Intent(mContext,ChatVideoInfoActivity.class);
		i.putExtra(ChatVideoInfoActivity.EXTRA_STRING_VIDEO_URL, uri);
		mContext.startActivity(i);
	}

	@Override
	public Object onExecute(Integer tag, String[] params) throws Exception {
		if(tag==ASY_TASK_TAG_PLAY){
			try {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.reset(); 
				mMediaPlayer.setDataSource(params[0]);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				isVoicePlaying = true;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		
	}

	@Override
	public void onTaskFail(Integer tag, Exception e) {
		
	}
}
