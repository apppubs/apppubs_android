package com.apppubs.d20.fragment;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.apppubs.d20.activity.NewsVideoInfoActivity;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.R;
import com.apppubs.d20.adapter.CommonAdapter;
import com.apppubs.d20.asytask.AsyTaskCallback;
import com.apppubs.d20.asytask.AsyTaskExecutor;
import com.apppubs.d20.bean.NewsAudioInfo;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.WebUtils;
import com.apppubs.d20.widget.commonlist.CommonListView;
import com.apppubs.d20.widget.commonlist.CommonListViewListener;

public class ChannelAudioFragment extends ChannelFragment implements AsyTaskCallback,OnClickListener {
	
	private final int REQUEST_TAG_AUDIO_LIST = 1;
	private final int TASK_TAG_UPDATE_AUDIO_TIME = 2;
	private final int TASK_TAG_STOP_AUDIO = 3;
	private final int TASK_TAG_PLAY_AUDIO = 4;
	
	private CommonListView mLv;
	private AudioAdapter mAdapter;
	public static String VEDIOINTENTINFOS = "vedioinfo";
	private List<NewsAudioInfo> mAudios;
	private SimpleDateFormat mDateFormat;
	private MediaPlayer mMediaPlayer;
	private TimerTask mTimerTask;
	private String mPlayingAudioId;
	private ImageButton mPlayingBtn;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.frg_video, null);
		init(view);

		return view;
	}

	private void init(View view) {
		mLv = (CommonListView) view.findViewById(R.id.left_vedio_xlv);
		mLv.setPullRefreshEnable(true);
		mLv.setPullLoadEnable(true);
		mLv.setOnItemClickListener(new OnItemClickListener() {
       
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Intent intent = new Intent(getActivity(), NewsVideoInfoActivity.class);
				NewsAudioInfo video = mAdapter.getItem(position-1);
				intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_ID, video.getInfoId());
				intent.putExtra(NewsVideoInfoActivity.EXTRA_STRING_NAME_CHANNELCODE, mChannelCode);
				getActivity().startActivity(intent);
			}
		});
		mLv.setCommonListViewListener(new CommonListViewListener() {

			@Override
			public void onRefresh() {
				refresh();
			}

			@Override
			public void onLoadMore() {
				load();
			}

			
		});
		refresh();
	}
	private void load() {
		AsyTaskExecutor.getInstance().startTask(REQUEST_TAG_AUDIO_LIST, this, new String[]{mCurPage+++""});
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	
	private class AudioAdapter extends CommonAdapter<NewsAudioInfo>{


		public AudioAdapter(Context context, List<NewsAudioInfo> datas, int resId) {
			super(context, datas, resId);
		}

		@Override
		protected void fillValues(com.apppubs.d20.adapter.ViewHolder holder, NewsAudioInfo bean, int position) {
			TextView titleTV = holder.getView(R.id.audio_title_tv);
			titleTV.setText(bean.getTitle());
//			
			ImageView iv = holder.getView(R.id.audio_iv);
			mImageLoader.displayImage(bean.getPicUrl(), iv);
			
			if(mDateFormat==null){
				mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			}
			TextView pubTimeTv = holder.getView(R.id.audio_pubtime_tv);
			pubTimeTv.setText(mDateFormat.format(bean.getPubTime()));
			
			TextView timeTv = holder.getView(R.id.audio_time_tv);
			ImageButton playBtn = holder.getView(R.id.audio_play_btn);
			playBtn.setOnClickListener(ChannelAudioFragment.this);
			playBtn.setTag(bean);
			playBtn.setTag(R.id.temp_id, timeTv);
			if (bean.getInfoId().equals(mPlayingAudioId)) {
//				mPlayingBtn = playBtn;
				if(mMediaPlayer.isPlaying()){
					playBtn.setImageResource(R.drawable.audio_pause);
				}else{
					playBtn.setImageResource(R.drawable.audio_play);
				}
				timeTv.setVisibility(View.VISIBLE);
			}else{
				playBtn.setImageResource(R.drawable.audio_state_initial);
				timeTv.setVisibility(View.GONE);
			}
			
		}
		
		
	}


	@Override
	public void refresh() {
		mCurPage = 0;
		load();
	}

	@Override
	public Object onExecute(Integer tag, String[] params) {
		Object result = null;
		if (tag==REQUEST_TAG_AUDIO_LIST) {
			String url = String.format(URLs.URL_AUDIO_LIST,URLs.baseURL,mChannel.getCode(),mCurPage);
				try {
					result = WebUtils.requestList(url, NewsAudioInfo.class,"resultinfo");
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}else if(tag==TASK_TAG_UPDATE_AUDIO_TIME){
			result = params[0];
		}else if(tag==TASK_TAG_STOP_AUDIO){
			
		}else if(tag==TASK_TAG_PLAY_AUDIO){
			try {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.reset(); 
				mMediaPlayer.setDataSource(params[0]);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
				mTimerTask = new TimerTask() {
					
					@Override
					public void run() {
						
						int remainSecond = (mMediaPlayer.getDuration()-mMediaPlayer.getCurrentPosition())/1000;
						if(remainSecond<1){
							AsyTaskExecutor.getInstance().startTask(TASK_TAG_STOP_AUDIO, ChannelAudioFragment.this, new String[]{""});
						}else{
							int minute =remainSecond/60;
							int second = remainSecond%60;
							AsyTaskExecutor.getInstance().startTask(TASK_TAG_UPDATE_AUDIO_TIME, ChannelAudioFragment.this, new String[]{"-"+minute+":"+second});
							System.out.println("当前时间："+"-"+minute+":"+second);
						}
						
					}
				};
				
				Timer timer = new Timer();
				timer.schedule(mTimerTask, 0,1000);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	@Override
	public void onTaskSuccess(Integer tag, Object obj) {
		if (tag==REQUEST_TAG_AUDIO_LIST) {
			mAudios = (List<NewsAudioInfo>) obj;
			if (mAdapter==null) {
				mAdapter = new AudioAdapter(mHostActivity, mAudios, R.layout.item_audio);
			}
			if (mCurPage==1) {
				mLv.setAdapter(mAdapter);
				mLv.stopRefresh();
			}else{
				mAdapter.notifyDataSetChanged();
				mLv.stopLoadMore();
			}
			
		}else if(tag==TASK_TAG_UPDATE_AUDIO_TIME){
			TextView timeTv = (TextView) mPlayingBtn.getTag(R.id.temp_id);
			System.out.println("当前文本："+timeTv.getText());
			timeTv.setText(obj.toString());
		}else if(tag==TASK_TAG_STOP_AUDIO){
			stopCurSound();
		}else if(tag==TASK_TAG_PLAY_AUDIO){
			
		}
	
	}

	@Override
	public void onTaskFail(Integer tag,Exception e) {
		
	}

	@Override
	public void onClick(View v) {
		TextView timeTv = (TextView) v.getTag(R.id.temp_id);
		timeTv.setVisibility(View.VISIBLE);
		//操作逻辑：判断当前是否有在播放的音频，若有则判断是否是当前点击的，如果是则可以暂停或者开始，如果当前点击的不是正在播放的则停止正在播放的
		NewsAudioInfo info = (NewsAudioInfo) v.getTag();
		ImageButton playingBtn = (ImageButton) v;
		
		if(info.getInfoId().equals(mPlayingAudioId)&&mMediaPlayer.isPlaying()){
			mMediaPlayer.pause();
			playingBtn.setImageResource(R.drawable.audio_play);
		}else if(info.getInfoId().equals(mPlayingAudioId)){
			mMediaPlayer.start();
			playingBtn.setImageResource(R.drawable.audio_pause);
		}else{
			stopCurSound();
			mPlayingAudioId = info.getInfoId();
			playSound(info.getAudioUrl(),timeTv);
			playingBtn.setImageResource(R.drawable.audio_pause);
		}
		mPlayingBtn = playingBtn;
		
	}
	private void stopCurSound(){
		System.out.println("停止播放mMediaPlayer:"+mMediaPlayer);
		if (mMediaPlayer!=null) {
			mMediaPlayer.stop();
			mPlayingAudioId =null;
			mPlayingBtn.setImageResource(R.drawable.audio_state_initial);
			mTimerTask.cancel();
			TextView timeTv = (TextView) mPlayingBtn.getTag(R.id.temp_id);
			timeTv.setVisibility(View.GONE);
		}
	}
	private void playSound(String src,final TextView timeTv ) {
		LogM.log(this.getClass(), "得到音频位置"+src);
		AsyTaskExecutor.getInstance().startTask(TASK_TAG_PLAY_AUDIO, this, new String[]{src});
	
	}
	
	@Override
	public void onStop() {
		super.onStop();
		stopCurSound();
		
	}
}
