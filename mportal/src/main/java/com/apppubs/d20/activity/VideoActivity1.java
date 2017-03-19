package com.apppubs.d20.activity;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.d20.util.Utils;

public class VideoActivity1 extends BaseActivity implements Callback,
		OnBufferingUpdateListener, OnCompletionListener, OnPreparedListener,
		OnTouchListener {
	private GestureDetector myGestureDetector;// 监听手势
	private SurfaceView sv;
	private SurfaceHolder holder;
	private TextView nowtime, sumtime;
	private ImageButton play, fastback, fastplay;
	private SeekBar seekbar;
	private MediaPlayer mediaplay = null;
	private PopupWindow popuWindow;
	private Context context = this;
	private View popuview;
	private MyThread mythread;
	private int width = 0;
	private int height = 0;
	private boolean isplay = true;
	private String path = null;
    private ImageView back;
	private RelativeLayout volume_layout;// 音量控制布局
	private TextView volume_text;// 音量百分比
	private ImageView volume_iv;// 音量图标
	private RelativeLayout progress_layout;// 进度图标
	private TextView progress_text;// 播放时间进度
	private ImageView progress_iv;// 快进或快退标志
	private AudioManager audiomanager;
	private int maxVolume, currentVolume;
	private static final float STEP_PROGRESS = 2f;// 设定进度滑动时的步长，避免每次滑动都改变，导致改变过快
	private static final float STEP_VOLUME = 2f;// 协调音量滑动时的步长，避免每次滑动都改变，导致改变过快
	private boolean firstScroll = false;// 每次触摸屏幕后，第一次scroll的标志
	private int FLAG = 0;// 1,调节进度，2，调节音量
	private static final int PLAY = 1;
	private static final int VOLUME = 2;
	private LinearLayout progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_video1);
		mTitleBar.setVisibility(View.GONE);
		init();
		Intent intent=getIntent();
		System.out.println("videopath.............."+path);
		holder = sv.getHolder();
		holder.addCallback(this);
		//设置SurfaceView自己不管理的缓冲区   
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);         
	
		sv.setOnTouchListener(this);
		play.setOnClickListener(this);
		fastback.setOnClickListener(this);
		fastplay.setOnClickListener(this);
		// 进度条监听
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onStopTrackingTouch(SeekBar seekBar) {
				int videoLength = seekBar.getProgress();
				// int q = mediaPlayer.getCurrentPosition();
				mediaplay.seekTo(videoLength);
				myhandler.removeMessages(0x01);
				myhandler.sendEmptyMessageDelayed(0x01, 2000);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
			}
		});

		myGestureDetector = new GestureDetector(getApplicationContext(),
				new MyGestureListener());
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return myGestureDetector.onTouchEvent(event);
	}

	private void init() {
		// 界面控制
		progress = (LinearLayout) findViewById(R.id.video_progressBar);
		back=(ImageView) findViewById(R.id.video_back);
		volume_layout = (RelativeLayout) findViewById(R.id.gesture_volume_layout);
		progress_layout = (RelativeLayout) findViewById(R.id.gesture_progress_layout);
		progress_text = (TextView) findViewById(R.id.geture_tv_progress_time);
		volume_text = (TextView) findViewById(R.id.geture_tv_volume_percentage);
		progress_iv = (ImageView) findViewById(R.id.gesture_iv_progress);
		volume_iv = (ImageView) findViewById(R.id.gesture_iv_player_volume);
		audiomanager = (AudioManager) getSystemService(AUDIO_SERVICE);
		maxVolume = audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC); // 获取系统最大音量
		currentVolume = audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值

		// 初始化
		sv = (SurfaceView) findViewById(R.id.main_sv);
		popuview = getLayoutInflater().inflate(R.layout.popuwindowvideo, null);
		nowtime = (TextView) popuview.findViewById(R.id.nowtime);
		sumtime = (TextView) popuview.findViewById(R.id.sumtime);
		play = (ImageButton) popuview.findViewById(R.id.play);
		fastback = (ImageButton) popuview.findViewById(R.id.fastback);
		fastplay = (ImageButton) popuview.findViewById(R.id.fastplay);
		seekbar = (SeekBar) popuview.findViewById(R.id.seekbar);
		popuWindow = new PopupWindow(popuview, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
	}
	public void playVideo(String puth) {
			mediaplay = new MediaPlayer();
			mediaplay.reset();
		    try {
				mediaplay.setDataSource(context, Uri.parse(path));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// setAudioStreamType设置音频流类型
			mediaplay.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaplay.prepareAsync();// 边缓冲便播放
 
			mediaplay.setDisplay(holder);
			
			mediaplay.setOnBufferingUpdateListener(this);
			mediaplay.setOnCompletionListener(this);
			mediaplay.setOnPreparedListener(this);
			
	}
   // surfacecallback方法
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
				playVideo(path);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		// TODO Auto-generated method stub
		height = mediaplay.getVideoWidth();
		width = mediaplay.getVideoHeight();
		if (width != 0 && height != 0) {
			holder.setFixedSize(width, height);// 设置视频高宽
			mediaplay.start();
			progress .setVisibility(View.GONE);
			int n = mediaplay.getDuration();
			mythread = new MyThread();
			mythread.start();
			seekbar.setMax(n);
			// 设置总时间
			n = n / 1000;
			int m = n / 60;
			int h = m / 60;
			int s = n % 60;
			m = m % 60;
			sumtime.setText(String.format("%02d:%02d:%02d", h, m, s));
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {

	}
	  @Override
	  public void finish() {
	  	// TODO Auto-generated method stub
	  	super.finish();
		if (mediaplay != null) {
			if (mediaplay.isPlaying()) {
				mediaplay.stop();
			}
			mediaplay.reset();
			mediaplay.release();
			mediaplay = null;
		}
	  }
	@Override
	protected void onPause() {
		super.onPause();
		if (mediaplay != null) {
			if (mediaplay.isPlaying()) {
				mediaplay.stop();
			}
			mediaplay.reset();
			mediaplay.release();
			mediaplay = null;
		}
		isplay = false;
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		popuWindow.dismiss();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// surfaceView的点击事件
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (!popuWindow.isShowing()) {
				popuWindow.showAtLocation(popuview, Gravity.BOTTOM, 0, 0);
				Message msg = Message.obtain();
				msg.what = 1;
				myhandler.sendEmptyMessageDelayed(0x01, 2000);
			} else
				System.out.println();
			// popuWindow.dismiss();
		}
		return false;
	} 
     
	@Override
	public void onClick(View v) {
		// imagebutton的点击事件
		switch (v.getId()) {
		case R.id.video_back:
			finish();
			break;
		case R.id.play:
			if (mediaplay.isPlaying()) {
				play.setImageResource(R.drawable.pause);
				mediaplay.pause();
			} else {
				play.setImageResource(R.drawable.play);
				mediaplay.start();
			}
			myhandler.removeMessages(0x01);
			myhandler.sendEmptyMessageDelayed(0x01, 2000);
			myhandler.sendEmptyMessage(0x02);
			break;
		case R.id.fastback:
			int i = mediaplay.getCurrentPosition() - 5000;
			mediaplay.seekTo(i);
			seekbar.setProgress(i);
			myhandler.removeMessages(0x01);
			myhandler.sendEmptyMessageDelayed(0x01, 2000);
			myhandler.sendEmptyMessage(0x02);
			break;
		case R.id.fastplay:
			int j = mediaplay.getCurrentPosition() + 5000;
			mediaplay.seekTo(j);
			seekbar.setProgress(j);
			myhandler.removeMessages(0x01);
			myhandler.sendEmptyMessageDelayed(0x01, 2000);
			myhandler.sendEmptyMessage(0x02);
			break;
		}
	}

	Handler myhandler = new Handler() {
		// 0x01代表popuwindows的消失与出现
		// 0x02代表seekbar的更新
		// 0x03代表声音的更新
		// 0x04代表快进（退）图标的消失
		// 0x05代表声音图标的消失
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0x01:
				popuWindow.dismiss();
				break;
			case 0x02:
				int num = 0;
				if (mediaplay != null) {
					num = mediaplay.getCurrentPosition();
				}
				seekbar.setProgress(num);
				num = num / 1000;
				int minute = num / 60;
				int hour = minute / 60;
				int second = num % 60;
				minute = minute % 60;
				nowtime.setText(String.format("%02d:%02d:%02d", hour, minute,
						second));
				break;
			case 0x03:
				audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,
						msg.arg1, 0);
				break;
			case 0x04:
				progress_layout.setVisibility(View.GONE);
				break;
			case 0x05:
				volume_layout.setVisibility(View.GONE);
				break;
			}
		};
	};

	class MyThread extends Thread {
		// 启动线程跟新seekbar
		@Override
		public void run() {
			while (isplay) {
				try {
					sleep(1000);
					myhandler.sendEmptyMessage(0x02);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class MyGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			firstScroll = true;// 设定是触摸屏幕后第一次scroll的标志
			// return super.onDown(e);
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			// super.onShowPress(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			// return super.onSingleTapUp(e);
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (firstScroll) {// 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
				// 横向的距离变化大则调整进度，纵向的变化大则调整音量
				if (Math.abs(distanceX) >= Math.abs(distanceY)) {
					FLAG = PLAY;
				} else {
					FLAG = VOLUME;
				}
			}
			// 如果每次触摸屏幕后第一次scroll是调节进度，那之后的scroll事件都处理音量进度，直到离开屏幕执行下一次操作
			if (FLAG == PLAY) {
				if (Math.abs(distanceX) > Math.abs(distanceY)) {// 横向移动大于纵向移动
					progress_layout.setVisibility(View.VISIBLE);// 显示图标
					if (distanceX >= Utils.dip2px(
							getApplicationContext(), STEP_PROGRESS)) {// 快退，用步长控制改变速度，可微调
						// 显示快退
						progress_iv
								.setImageResource(R.drawable.souhu_player_backward);
						if (mediaplay.getCurrentPosition() > 3 * 1000) {// 避免为负

							int i = mediaplay.getCurrentPosition() - 3000;
							mediaplay.seekTo(i);
							seekbar.setProgress(i);
						} else {
							mediaplay.seekTo(3 * 1000);
							seekbar.setProgress(3 * 1000);
						}
						myhandler.removeMessages(0x01);
						myhandler.sendEmptyMessageDelayed(0x01, 5000);
						myhandler.sendEmptyMessage(0x02);
					} else if (distanceX <= -Utils.dip2px(
							getApplicationContext(), STEP_PROGRESS)) {// 快进
						// 显示快进
						progress_iv.setImageResource(R.drawable.souhu_player_forward);
						if (mediaplay.getCurrentPosition() < mediaplay
								.getDuration() - 5 * 1000) {// 避免为负
							int i = mediaplay.getCurrentPosition() + 3000;
							mediaplay.seekTo(i);
							seekbar.setProgress(i);
						} else {
							mediaplay
									.seekTo(mediaplay.getDuration() - 3 * 1000);
							seekbar.setProgress(mediaplay.getDuration() - 3 * 1000);
						}
						myhandler.removeMessages(0x01);
						myhandler.sendEmptyMessageDelayed(0x01, 5000);
						myhandler.sendEmptyMessage(0x02);
					}
					myhandler.sendEmptyMessage(0x04);
				}
			}
			// 如果每次触摸屏幕后第一次scroll是调节音量，那之后的scroll事件都处理音量调节，直到离开屏幕执行下一次操作
			else if (FLAG == VOLUME) {
				currentVolume = audiomanager
						.getStreamVolume(AudioManager.STREAM_MUSIC); // 获取当前值
				volume_layout.setVisibility(View.VISIBLE);
				if (Math.abs(distanceY) > Math.abs(distanceX)) {// 纵向移动大于横向移动
					if (distanceY >= Utils.dip2px(
							getApplicationContext(), STEP_VOLUME)) {// 音量调大,注意横屏时的坐标体系,尽管左上角是原点，但横向向上滑动时distanceY为正
						if (currentVolume < maxVolume) {// 为避免调节过快，distanceY应大于一个设定值
							currentVolume++;
							Message msg = Message.obtain();
							msg.what = 0x03;
							msg.arg1 = currentVolume;
							myhandler.sendMessage(msg);
						}
						volume_iv
								.setImageResource(R.drawable.souhu_player_volume);
					} else if (distanceY <= -Utils.dip2px(
							getApplicationContext(), STEP_VOLUME)) {// 音量调小
						if (currentVolume > 0) {
							currentVolume--;
							if (currentVolume == 0) {// 静音，设定静音独有的图片
								volume_iv
										.setImageResource(R.drawable.souhu_player_silence);
							}
							Message msg = Message.obtain();
							msg.arg1 = currentVolume;
							msg.what = 0x03;
							myhandler.sendMessage(msg);
						}
					}
					int percentage = (currentVolume * 100) / maxVolume;
					volume_text.setText(percentage + "%");
					audiomanager.setStreamVolume(AudioManager.STREAM_MUSIC,
							currentVolume, 0);
					myhandler.sendEmptyMessage(0x05);
				}
			}

			firstScroll = false;// 第一次scroll执行完成，修改标志
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			super.onLongPress(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			// return super.onFling(e1, e2, velocityX, velocityY);
			return false;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onSingleTapConfirmed(e);
		}

	}
	
}