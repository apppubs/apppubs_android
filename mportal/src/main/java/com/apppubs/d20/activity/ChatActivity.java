package com.apppubs.d20.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.Msg;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.model.MsgController;
import com.apppubs.d20.constant.Actions;
import com.apppubs.d20.exception.ESUnavailableException;
import com.apppubs.d20.util.SharedPreferenceUtils;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.widget.MyEditText;
import com.apppubs.d20.MportalApplication;
import com.apppubs.d20.R;
import com.apppubs.d20.adapter.ChatAdapter;
import com.apppubs.d20.model.BussinessCallbackCommon;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.util.FileUtils;
import com.apppubs.d20.widget.FlowLayout;
import com.apppubs.d20.widget.ProgressHUD;
import com.apppubs.lame.MP3Recorder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ChatActivity extends BaseActivity implements OnClickListener {

	public static final String EXTRA_STRING_OTHER_USERNAME = "other_user_name";
	public static final String EXTRA_STRING_CHAT_ID = "chat_id";
	public static final String EXTRA_INT_CHAT_TYPE = "chat_type";

	public static final int CHAT_TYPE_SINGLE = 1;
	public static final int CHAT_TYPE_GROUP = 2;

	public static final String ACTION_CLOSE_CHAT_ACTIVITY = "close_chat_activity";

	private final int REQUEST_CODE_GET_VIDEO = 4;

	public String mTarget = "";
	private ListView mListView;
	private ChatAdapter mChatAdapter;
	private ImageView mMoreIv;
	private MyEditText mChatEt;
	private TextView sendBtn;
	private ImageView mVoiceIv;
	private boolean mVoiceMode = true;
	private List<Msg> infos = new ArrayList<Msg>();
	private boolean mMorePannelOpen;
	private String mPicPath;
	private String mLastDateS;// 聊天的记录时间；
	private View mMoreView;// 更多面板
	private FlowLayout mMorePannelFl;// 添加图片，照相
	private List<LinearLayout> linLays = new ArrayList<LinearLayout>();
	private RelativeLayout mRECingLayRl;// 显示声音录制
	private TextView mRECTv;// 录制声音按钮
	// private User mOtherUser;
	private String TAG = "ChatAcitivty";
	private ImageView mVoiceChangpic;// 录音时图片更换
	private ImageView mClickedIv;// 播放时图片更换
	private ImageView mSendFail;// 发送失败标记
	private TextView mChatTime;// 聊天时间的标记
	// private MediaRecorder mMediaRecorder;//
	private MP3Recorder mRecorder;
	private String mSoundPath;
	// 设置录音保存路径
	private static String mFileName;
	private MediaPlayer mMediaPlayer;// 播放录音
	/** 手机震动API */
	private ToneGenerator mToneGenerator;
	/**
	 * 更新话筒状态
	 * 
	 */
	private int BASE = 1;
	private int SPACE = 500;// 间隔取样时间

	private BroadcastReceiver mMsgReceiver;
	private BroadcastReceiver mCloseReceiver;

	private String mChatGroupId;
	private int mChatType = CHAT_TYPE_SINGLE;

	private DisplayImageOptions mImageLoaderOptions;

	private final Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:// 录音时图片更换
				updateMicStatus();
				break;
			case 1:// 播放时图片更换
				updateMicStatus1(mMediaPlayer);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 更新话筒状态
	 * 
	 */
	int[] picids = { R.drawable.mport_me_recordlearn1, R.drawable.mport_me_recordlearn2,
			R.drawable.mport_me_recordlearn3 };
	int i;

	private void updateMicStatus1(MediaPlayer mMediaPlayer) {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			if (i < 2) {
				i++;
			} else {
				i = 0;
			}
			mClickedIv.setImageResource(picids[i]);
			mHandler.sendEmptyMessageDelayed(1, SPACE);
		} else {
			System.out.println("播放结束后，更换为原始图片");
			mClickedIv.setImageResource(R.drawable.mport_me_haverecord);
			System.out.println("播放结束后，更换为原始图片   ddd");
		}
	}

	private void updateMicStatus() {
		int[] voicePics = { R.drawable.oy, R.drawable.oz, R.drawable.p0, R.drawable.p1, R.drawable.p2, R.drawable.p3,
				R.drawable.p4 };
		if (mRecorder != null) {

			double db = mRecorder.getVolume();
			System.out.println("当前分贝：" + db);
			if (db < 40.0 && db > 20.0) {
				mVoiceChangpic.setImageResource(voicePics[1]);
			} else if (db > 40.0 && db < 50.0) {
				mVoiceChangpic.setImageResource(voicePics[2]);
			} else if (db > 50.0 && db < 60.0) {
				mVoiceChangpic.setImageResource(voicePics[3]);
			} else if (db > 60.0 && db < 70.0) {
				mVoiceChangpic.setImageResource(voicePics[4]);
			} else if (db > 70.0 && db < 80.0) {
				mVoiceChangpic.setImageResource(voicePics[5]);
			} else if (db > 80.0 && db < 100.0) {
				mVoiceChangpic.setImageResource(voicePics[6]);
			}
			Log.d(TAG, "分贝值：" + db);
			mHandler.sendEmptyMessageDelayed(0, SPACE);
		}
	}

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.act_chat);
		initView();
		initLinsner();
		Intent intent = getIntent();
		String otherUserName = intent.getStringExtra(EXTRA_STRING_OTHER_USERNAME);
		mChatGroupId = intent.getStringExtra(EXTRA_STRING_CHAT_ID);
		mChatType = intent.getIntExtra(EXTRA_INT_CHAT_TYPE, CHAT_TYPE_SINGLE);

		MsgController.getInstance(this).cancelNotificationBySenderId(mChatGroupId);
		registerReceiver();
		initData();
	}

	/**
	 * 此activity设置为了singleTop当用户和A聊天时，B来了信息，用户点击通知时会调用次方法
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String otherUserName = intent.getStringExtra(EXTRA_STRING_OTHER_USERNAME);
		mChatGroupId = intent.getStringExtra(EXTRA_STRING_CHAT_ID);
		mChatType = intent.getIntExtra(EXTRA_INT_CHAT_TYPE, CHAT_TYPE_SINGLE);
		MsgController.getInstance(this).cancelNotificationBySenderId(mChatGroupId);
		setTitle(intent.getStringExtra(EXTRA_STRING_TITLE));
		initData();
	}

	@Override
	protected void onResume() {
		MsgController.getInstance(this).setCurChatGroupId(mChatGroupId);
		super.onResume();
	}

	private void initData() {
		mChatAdapter = new ChatAdapter(this);
		mListView.setAdapter(mChatAdapter);
		MsgController.getInstance(this).setCurChatGroupId(mChatGroupId);
		Map<String,String> map = (Map<String, String>) MportalApplication.readObj(this, MportalApplication.MSG_DELETED_CHAT_GROUP_MAP);
		String deleteDateStr = "";
		if(map!=null&&map.get(mChatGroupId)!=null){
			deleteDateStr = map.get(mChatGroupId);
		}
		UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
		mMsgBussiness.getChatGroupChatList(currentUser.getUsername(), mChatGroupId,deleteDateStr,
				new BussinessCallbackCommon<List<Msg>>() {

					@Override
					public void onException(int excepCode) {

					}

					@Override
					public void onDone(List<Msg> obj) {
						infos = obj;
						System.out.println("获得的聊天信息 。。。。" + infos.toString());
						mChatAdapter.setDate(obj);
						mChatAdapter.notifyDataSetChanged();
					}
				});

		cleanUnreadNum();
		if (mChatType == CHAT_TYPE_GROUP) {
			mTitleBar.setRightBtnImageResourceId(mChatType == CHAT_TYPE_GROUP ? R.drawable.chat_users
					: R.drawable.chat_user);
			mTitleBar.setRightBtnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ChatActivity.this, ChatGroupInfoActivity.class);
					intent.putExtra(ChatGroupInfoActivity.EXTRA_CHAT_GROUP_ID, mChatGroupId);
					startActivity(intent);
				}
			});
		}
	}

	private void registerReceiver() {
		mMsgReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				Msg msg = (Msg) intent.getExtras().getSerializable(Actions.EXTRA_MSG);
				mChatAdapter.addItem(msg);
				mChatAdapter.notifyDataSetChanged();
				mListView.setSelection(mChatAdapter.getCount() - 1);
			}

		};
		mCloseReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				finish();
			}
		};

		this.registerReceiver(mMsgReceiver, new IntentFilter(Actions.ACTION_MESSAGE));
		this.registerReceiver(mCloseReceiver, new IntentFilter(ACTION_CLOSE_CHAT_ACTIVITY));
	}

	private void cleanUnreadNum() {
		if (!TextUtils.isEmpty(mChatGroupId)) {
			UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
			String url = String.format(URLs.URL_CLEAR_UNREAD_NUM_FOR_SERVICE_NO_AND_CHAT, mChatGroupId,
					currentUser.getUsername());
			mRequestQueue.add(new StringRequest(url, new Listener<String>() {

				@Override
				public void onResponse(String arg0) {

				}
			}, new ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError arg0) {

				}
			}));
		}
	}

	private void initLinsner() {
		mVoiceIv.setOnClickListener(this);
		// 发送
		sendBtn.setOnClickListener(this);
		mRECTv.setOnTouchListener(new OnTouchListener() {

			float starty = 0;
			float endY = 0;

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					readyOperation();// 给予客户端震动提示
					starty = event.getRawY();
					mRECingLayRl.setVisibility(View.VISIBLE);
					// mMediaRecorder = startVoice();
					try {
						File dir = FileUtils.getAppExternalFilesStorageFile();
						if (!dir.exists()) {
							dir.mkdirs();
						}
						mSoundPath = dir + "/" + UUID.randomUUID() + ".mp3";
					} catch (ESUnavailableException e1) {
						e1.printStackTrace();
					}
					mRecorder = new MP3Recorder(new File(mSoundPath));
					try {
						mRecorder.start();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// 更新你相应的UI
					mHandler.sendEmptyMessageDelayed(0, 500);
					break;
				case MotionEvent.ACTION_UP:
					endY = event.getRawY();
					mRECingLayRl.setVisibility(View.GONE);
					// stopVoice();
					mRecorder.stop();
					mRecorder = null;
					if (starty - endY > 30) {// 手指上滑,取消发送
						Toast.makeText(ChatActivity.this, "取消发送", Toast.LENGTH_SHORT).show();
					} else {
						// 设置要播放的文件
						try {
							mMediaPlayer = new MediaPlayer();
							File file = new File(mSoundPath);
							FileInputStream fis = new FileInputStream(file);
							mMediaPlayer.setDataSource(fis.getFD());
							mMediaPlayer.prepare();
							final int mVoiceDuration = mMediaPlayer.getDuration();// 即为时长
							fis.close();
							// 是ms;
							if (mVoiceDuration < 1000) {
								Toast.makeText(ChatActivity.this, "时间过短", Toast.LENGTH_SHORT).show();
							} else {
								/*
								 * mSystemBussiness.getStandardDataTime(new
								 * BussinessCallbackCommon<Date>() {
								 * 
								 * @Override public void onException(int
								 * excepCode) { //
								 * infos.get(position).getSendTime() }
								 * 
								 * @Override public void onDone(Date obj) { //
								 * obj } });
								 */
								Msg info = new Msg();
								UserInfo currentUser = AppContext.getInstance(mContext).getCurrentUser();
								info.setSenderId(currentUser.getUsername());
								info.setContentType(Msg.TYPE_CONTENT_SOUND);
								info.setLength(mVoiceDuration);
								info.setVoiceLocation(mSoundPath);
								info.setSendTime(new Date());
								infos.add(info);
								mChatAdapter.notifyDataSetChanged();
								mListView.setSelection(infos.size() - 1);
								// if(mChatType==CHAT_TYPE_SINGLE){
								// mMsgBussiness.sendSoundMsg(MportalApplication.user.getUsername(),
								// mOtherUser.getUsername(),
								// mFileName,mVoiceDuration);
								// }else{
								// }
								mMsgBussiness.sendGroupSoundMsg(currentUser.getUsername(), mChatGroupId,
										mSoundPath, mVoiceDuration);
							}

						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (IllegalStateException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					break;
				default:
					break;
				}
				return false;
			}
		});

	}

	/** 开始录音 */
	// public MediaRecorder startVoice() {
	// String path = Environment.getExternalStorageDirectory().getPath();
	// // "/sdcard/MyVoiceForder/Record/";
	// // 设置录音保存路径
	// mFileName = path+"/Record/" + UUID.randomUUID().toString() + ".arm";
	// String state = android.os.Environment.getExternalStorageState();
	// if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
	// }
	// File directory = new File(mFileName).getParentFile();
	// if (!directory.exists() && !directory.mkdirs()) {
	// }
	// mMediaRecorder = new MediaRecorder();
	// mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	// mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	// mMediaRecorder.setOutputFile(mFileName);
	// mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	// try {
	// mMediaRecorder.prepare();
	// } catch (IOException e) {
	// }
	// mMediaRecorder.start();
	// return mMediaRecorder;
	// }
	//
	// /** 停止录音 */
	// public void stopVoice() {
	// mMediaRecorder.stop();
	// mMediaRecorder.release();
	// mMediaRecorder = null;
	// }

	private void initView() {
		mVoiceChangpic = (ImageView) findViewById(R.id.chat_voice_ing_changpic);
		mRECingLayRl = (RelativeLayout) findViewById(R.id.chat_voice_lay_rl);
		mRECTv = (TextView) findViewById(R.id.chat_count_voice_tv);
		mMoreView = findViewById(R.id.chat_more_fl);
		mMorePannelFl = (FlowLayout) findViewById(R.id.chat_more_flow);
		mListView = (ListView) findViewById(R.id.chat_lv);
		mMoreIv = (ImageView) findViewById(R.id.chat_more_iv);
		mChatEt = (MyEditText) findViewById(R.id.chat_content_et);
		sendBtn = (TextView) findViewById(R.id.chat_content_send);
		mVoiceIv = (ImageView) findViewById(R.id.chat_voice_iv);

		int[] moreIds = { R.drawable.add_from_camera, R.drawable.aa8, R.drawable.aad };
		// int[] moreIds = { R.drawable.add_from_camera, R.drawable.aa8,
		// R.drawable.aa7, R.drawable.aa3, R.drawable.aad };
		String moreStr[] = { "相机", "图片", "发视频" };
		// String moreStr[] = { "相机", "图片", "位置", "文件", "发视频" };
		for (int i = 0; i < moreIds.length; i++) {
			LinearLayout ll = (LinearLayout) getLayoutInflater().inflate(R.layout.item_chat_moregrid, null);
			ImageView iv = (ImageView) ll.findViewById(R.id.gridImage);
			TextView tv = (TextView) ll.findViewById(R.id.gridImage_tv);
			iv.setImageResource(moreIds[i]);
			tv.setText(moreStr[i]);
			linLays.add(ll);
			mMorePannelFl.addView(ll);
			switch (i) {
			case 0:
				ll.setId(R.id.chat_more_take_pic);
				break;
			case 1:
				ll.setId(R.id.chat_more_pic);
				break;
			case 2:
				ll.setId(R.id.chat_more_send_video);
				break;
			default:
				break;
			}
			ll.setOnClickListener(this);

		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.chat_more_iv:
			mRECingLayRl.setVisibility(View.GONE);
			mChatEt.setVisibility(View.VISIBLE);
			if (!mVoiceMode) {
				// mVoiceIv.setImageResource(R.drawable.chat_keyboard);
				closeSoftWindows();
			}
			if (mMorePannelOpen) {
				mMorePannelOpen = false;
				mMorePannelFl.setVisibility(View.GONE);
				// mMoreIv.setImageResource(R.drawable.chat_sound);
				opSoftInputFromWindow();
			} else {// 换原始图片
				closeSoftWindows();
				mMorePannelOpen = true;
				mMorePannelFl.setVisibility(View.VISIBLE);
				// mMoreIv.setImageResource(R.drawable.chat_sound);
			}

			break;
		case R.id.chat_voice_iv:
			if (mMorePannelOpen) {
				mMorePannelOpen = false;
				mMorePannelFl.setVisibility(View.GONE);
				// mMoreIv.setImageResource(R.drawable.chat_sound);
				closeSoftWindows();
			}
			if (mVoiceMode) {// 换图片 录音操作
				mVoiceMode = false;
				mVoiceIv.setImageResource(R.drawable.chat_keyboard);
				mRECTv.setVisibility(View.VISIBLE);
				mChatEt.setVisibility(View.GONE);
				closeSoftWindows();
			} else {
				mRECTv.setVisibility(View.GONE);
				mChatEt.setVisibility(View.VISIBLE);
				mVoiceMode = true;
				mVoiceIv.setImageResource(R.drawable.chat_sound);
				opSoftInputFromWindow();
				mChatEt.requestFocus();
			}
			break;

		case R.id.chat_content_send:
			final String count = mChatEt.getText().toString();
			if (!TextUtils.isEmpty(count)) {
				mSystemBussiness.getStandardDataTime(new BussinessCallbackCommon<Date>() {

					@Override
					public void onException(int excepCode) {
					}

					@Override
					public void onDone(Date obj) {
						Msg info = new Msg();
						info.setContent(count);
						info.setContentType(Msg.TYPE_CONTENT_TEXT);
						info.setSendTime(obj);
						info.setSenderId(AppContext.getInstance(mContext).getCurrentUser().getUsername());
						infos.add(info);
						mChatAdapter.notifyDataSetChanged();
						mListView.setSelection(infos.size() - 1);
						mChatEt.setText("");
						// mProgressBar.setVisibility(View.VISIBLE);
						String groupId = mChatGroupId == null ? "" : mChatGroupId;
						// String receiverUsername =
						// mChatType==CHAT_TYPE_SINGLE?mOtherUser.getUsername():"";
						// String receiverUsername =
						// mChatType==CHAT_TYPE_SINGLE?mOtherUser.getUsername():"";
						mMsgBussiness.sendTextMsg(AppContext.getInstance(mContext).getCurrentUser().getUsername(), "", groupId,
								info.getContent(), new BussinessCallbackCommon<Object>() {

									@Override
									public void onException(int excepCode) {
										mSendFail.setVisibility(View.VISIBLE);
									}

									@Override
									public void onDone(Object obj) {
										System.out.println("聊天文本显示的发送结果。。。。" + obj);
									}
								});
					}
				});

			}
			break;
		case R.id.chat_content_et:
			if (mMorePannelOpen) {
				mMorePannelFl.setVisibility(View.GONE);
			}
			break;
		case R.id.chat_more_take_pic:
			// 调用系统相机
			// Intent camare = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// String path = getFilesDir().getAbsolutePath();
			// path += "/"+UUID.randomUUID().toString()+".jpg";
			// File file = new File( path );
			// Uri outputFileUri = Uri.fromFile( file );
			// camare.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			// startActivityForResult(camare, 2);
			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// Ensure that there's a camera activity to handle the intent
			if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
				// Create the File where the photo should go
				File photoFile = null;
				try {
					photoFile = createImageFile();
				} catch (IOException ex) {
					// Error occurred while creating the File
				}
				// Continue only if the File was successfully created
				if (photoFile != null) {
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
					startActivityForResult(takePictureIntent, 2);
				}
			}
			break;
		case R.id.chat_more_pic:
			// 调用本地相册
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_PICK);

			intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, 1);
			break;
		case 2:// 位置

			break;
		case 3:// 文件
			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
			innerIntent.setType("*/*");
			Intent wrapperIntent = Intent.createChooser(innerIntent, null);
			startActivityForResult(wrapperIntent, 3);
			break;
		case R.id.chat_more_send_video:
			// 添加视频
			Intent innerVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			innerVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
			// innerVideo.setType("video/*");
			// innerVideo.setAction(Intent.ACTION_GET_CONTENT);
			Intent wrapperVideo = Intent.createChooser(innerVideo, null);
			// startActivityForResult(wrapperVideo, REQUEST_CODE_GET_VIDEO);
			startActivityForResult(innerVideo, REQUEST_CODE_GET_VIDEO);
			break;
		default:
			break;
		}
	}

	String mCurrentPhotoPath;

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mCurrentPhotoPath = "file:/" + image.getAbsolutePath();
		return image;
	}

	// 关闭软键盘
	private void closeSoftWindows() {
		if (getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	/**
	 * 软键盘，如果有，就隐藏，如没有，就显示软键盘
	 */
	private void opSoftInputFromWindow() {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK && mMorePannelOpen) {
			mMorePannelFl.setVisibility(View.GONE);
			mMorePannelOpen = false;
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (!mMorePannelOpen) {
			mMorePannelOpen = true;
			mMorePannelFl.setVisibility(View.GONE);
			// mMoreIv.setImageResource(R.drawable.chat_sound);
			closeSoftWindows();
		}

		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				// 本地相册
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				cursor.moveToFirst();
				mPicPath = cursor.getString(columnIndex);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();
				Log.d("picturePath", picturePath);
				Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
				Bitmap smallBitmap = reduce(bitmap, 960, 1280, true);
				String smallImagePath = getFilesDir().toString() + "/" + UUID.randomUUID().toString() + ".jpg";
				FileOutputStream out = null;
				try {
					out = new FileOutputStream(smallImagePath);
					smallBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
					out.flush();
					out.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				Msg msg = new Msg();
				msg.setPicLocation("file://" + smallImagePath);
				msg.setContentType(Msg.TYPE_CONTENT_IMAGE);
				msg.setSenderId(AppContext.getInstance(mContext).getCurrentUser().getUsername());
				msg.setSendTime(new Date());
				// msg.setReceiverUsername(mOtherUser.getUsername());
				infos.add(msg);

				mChatAdapter.notifyDataSetChanged();
				mListView.setSelection(infos.size() - 1);
				mMsgBussiness.sendGroupPicMsg(AppContext.getInstance(mContext).getCurrentUser().getUsername(), mChatGroupId, smallImagePath);
				break;
			case 2:
				// 相机拍摄
				String sdStatus = Environment.getExternalStorageState();
				if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
					Log.i("TestFile", "SD card is not avaiable/writeable right now.");
					return;
				}
				
				String pic = mCurrentPhotoPath;
				Bitmap cameraBit = BitmapFactory.decodeFile(pic.replace("file:/", ""));
				Bitmap cameraBitSmall = reduce(cameraBit, 960, 1280, true);
				String cameraBitSmallPath = getFilesDir().toString() + "/" + UUID.randomUUID().toString() + ".jpg";
				FileOutputStream cameraOut = null;
				try {
					out = new FileOutputStream(cameraBitSmallPath);
					cameraBitSmall.compress(Bitmap.CompressFormat.JPEG, 80, out);
					out.flush();
					out.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (cameraOut != null) {
						try {
							cameraOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				Msg info = new Msg();
				info.setSenderId(AppContext.getInstance(mContext).getCurrentUser().getUsername());
				info.setContentType(Msg.TYPE_CONTENT_IMAGE);
				info.setPicLocation( pic);
				info.setSendTime(new Date());
				infos.add(info);
				mChatAdapter.notifyDataSetChanged();
				mListView.setSelection(infos.size() - 1);
				// if(mChatType==CHAT_TYPE_SINGLE){
				// mMsgBussiness.sendPicMsg(MportalApplication.user.getUsername(),mOtherUser.getUsername(),mPicPath);
				// }else{
				// mMsgBussiness.sendGroupPicMsg(MportalApplication.user.getUsername(),mChatGroupId,mPicPath);
				// }
				mMsgBussiness.sendGroupPicMsg(AppContext.getInstance(mContext).getCurrentUser().getUsername(), mChatGroupId, cameraBitSmallPath);
				break;
			case 3:// 文件
				System.out.println("打印文件传送返回来的..........." + data.getDataString());
				// 打印文件传送返回来的...........file:///storage/emulated/0/LEWA/music/mp3/%E9%99%88%E5%A5%95%E8%BF%85-Get_A_Life_%E6%BC%94%E5%94%B1%E4%BC%9A-%E4%BA%BA%E6%9D%A5%E4%BA%BA%E5%BE%80.mp3

				break;

			case REQUEST_CODE_GET_VIDEO:// 视频
				System.out.println("打印视频传送返回来的..........." + data.getDataString());
				// 本地相册
				Uri selectedVideo = data.getData();
				String[] videofilePathColumn = { MediaColumns.DATA };
				Cursor videoCursor = getContentResolver().query(selectedVideo, videofilePathColumn, null, null, null);
				int videoColumnIndex = videoCursor.getColumnIndex(videofilePathColumn[0]);
				videoCursor.moveToFirst();
				int ci = videoCursor.getColumnIndexOrThrow(videofilePathColumn[0]);
				videoCursor.moveToFirst();
				String videoPath = videoCursor.getString(ci);
				videoCursor.close();
				if (TextUtils.isEmpty(videoPath)) {
					Toast.makeText(this, "无法发送视频", Toast.LENGTH_SHORT).show();
					return;
				}
				// bitmap = BitmapFactory.decodeFile(picturePath);
				// // return
				// putBackpic(bitmap);
				// String picName = System.currentTimeMillis()+".p";
				// Msg msg = new Msg();
				// msg.setPicLocation("file:///"+picturePath);
				// msg.setContentType(Msg.TYPE_CONTENT_IMAGE);
				// msg.setSenderUsername(MportalApplication.user.getUsername());
				// msg.setReceiverUsername(mOtherUser.getUsername());
				// infos.add(msg);
				//
				// mChatAdapter.notifyDataSetChanged();
				// mListView.setSelection(infos.size() - 1);

				ProgressHUD.show(this);
				// if(mChatType==CHAT_TYPE_SINGLE){
				//
				// mMsgBussiness.sendVideoMsg(MportalApplication.user.getUsername(),mOtherUser.getUsername(),videoPath,new
				// BussinessCallbackCommon<Object>() {
				//
				// @Override
				// public void onException(int excepCode) {
				//
				// }
				//
				// @Override
				// public void onDone(Object obj) {
				//
				//
				// mMsgBussiness.getChatList(mOtherUser.getUsername(),
				// MportalApplication.user.getUsername(),
				// new BussinessCallbackCommon<List<Msg>>() {
				//
				// @Override
				// public void onException(int excepCode) {
				//
				// }
				//
				// @Override
				// public void onDone(List<Msg> obj) {
				// ProgressHUD.dismissProgressHUDInThisContext(ChatActivity.this);
				// infos = obj;
				// System.out.println("获得的聊天信息 。。。。" + infos.toString());
				// mChatAdapter.setDate(obj);
				// mChatAdapter.notifyDataSetChanged();
				// }
				// });
				//
				//
				//
				// }
				// });
				// }else{
				// }
				mMsgBussiness.sendGroupVideoMsg(AppContext.getInstance(mContext).getCurrentUser().getUsername(), mChatGroupId, videoPath,
						new BussinessCallbackCommon<Object>() {

							@Override
							public void onException(int excepCode) {

							}

							@Override
							public void onDone(Object obj) {
								Map<String,String> map = (Map<String, String>) MportalApplication.readObj(ChatActivity.this, MportalApplication.MSG_DELETED_CHAT_GROUP_MAP);
								String deleteDateStr = "";
								if(map!=null){
									deleteDateStr = map.get(mChatGroupId);
								}
								mMsgBussiness.getChatGroupChatList(AppContext.getInstance(mContext).getCurrentUser().getUsername(), mChatGroupId,deleteDateStr,
										new BussinessCallbackCommon<List<Msg>>() {

											@Override
											public void onException(int excepCode) {

											}

											@Override
											public void onDone(List<Msg> obj) {
												ProgressHUD.dismissProgressHUDInThisContext(ChatActivity.this);
												infos = obj;
												System.out.println("获得的聊天信息 。。。。" + infos.toString());
												mChatAdapter.setDate(obj);
												mChatAdapter.notifyDataSetChanged();
											}
										});

							}
						});
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 压缩图片
	 * 
	 * @param bitmap
	 *            源图片
	 * @param width
	 *            想要的宽度
	 * @param height
	 *            想要的高度
	 * @param isAdjust
	 *            是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
	 * @return Bitmap
	 */
	public Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {
		// 如果想要的宽度和高度都比源图片小，就不压缩了，直接返回原图
		if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
			return bitmap;
		}
		// 根据想要的尺寸精确计算压缩比例, 方法详解：public BigDecimal divide(BigDecimal divisor,
		// int scale, int roundingMode);
		// scale表示要保留的小数位, roundingMode表示如何处理多余的小数位，BigDecimal.ROUND_DOWN表示自动舍弃
		float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN)
				.floatValue();
		if (isAdjust) {// 如果想自动调整比例，不至于图片会拉伸
			sx = (sx < sy ? sx : sy);
			sy = sx;// 哪个比例小一点，就用哪个比例
		}
		Matrix matrix = new Matrix();
		matrix.postScale(sx, sy);// 调用api中的方法进行压缩，就大功告成了
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	}

	private void putBackpic(final Bitmap bitmap) {
		mSystemBussiness.getStandardDataTime(new BussinessCallbackCommon<Date>() {

			@Override
			public void onException(int excepCode) {
				// infos.get(position).getSendTime()
			}

			@Override
			public void onDone(Date obj) {
				// obj
				Msg info = new Msg();
				info.setSenderId(AppContext.getInstance(mContext).getCurrentUser().getUsername());
				info.setContentType(2);// 图片
				info.setSendTime(obj);
				info.setPicLocation(mPicPath);
				infos.add(info);
				mChatAdapter.notifyDataSetChanged();
				mListView.setSelection(infos.size() - 1);
			}
		});

	}

	/**
	 * 给予客户端震动提示
	 */
	protected void readyOperation() {
		/** 计算当前录音时长 */
		long computationTime = -1L;
		computationTime = -1L;
		Toast mRecordTipsToast = null;
		playTone(ToneGenerator.TONE_PROP_BEEP, 200);
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				stopTone();
			}
		}, 200);
		/** 按键振动时长 */
		// public static final int TONE_LENGTH_MS = 200;
		vibrate(50L);
	}

	/**
	 * 播放提示音
	 * 
	 * @param tone
	 * @param durationMs
	 */
	public void playTone(int tone, int durationMs) {
		synchronized (mToneGeneratorLock) {
			initToneGenerator();
			if (mToneGenerator == null) {
				return;
			}

			// Start the new tone (will stop any playing tone)
			mToneGenerator.startTone(tone, durationMs);
		}
	}

	private Object mToneGeneratorLock = new Object();
	// 初始化 /**音量值*/
	private static final float TONE_RELATIVE_VOLUME = 100.0F;

	private void initToneGenerator() {
		AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (mToneGenerator == null) {
			try {
				int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
				int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int volume = (int) (TONE_RELATIVE_VOLUME * (streamVolume / streamMaxVolume));
				mToneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, volume);

			} catch (RuntimeException e) {
				mToneGenerator = null;
			}
		}
	}

	/**
	 * 停止播放声音
	 */
	public void stopTone() {
		if (mToneGenerator != null)
			mToneGenerator.stopTone();
	}

	/**
	 * 手机震动
	 * 
	 * @param milliseconds
	 */
	public synchronized void vibrate(long milliseconds) {
		Vibrator mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		if (mVibrator == null) {
			return;
		}
		mVibrator.vibrate(milliseconds);
	}

	/**
	 * 音频播放
	 */
	private void onPlayVoice(boolean start) {
		if (start) {
			startPlaying();
		} else {
			stopPlaying();
		}
	}

	// 停止播放
	private void stopPlaying() {
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void startPlaying() {

	}

	public static void startActivity(Context context, String otherUsername, String chatId, int chatType, String title) {
		Intent i = new Intent(context, ChatActivity.class);
		i.putExtra(EXTRA_STRING_OTHER_USERNAME, otherUsername);
		i.putExtra(EXTRA_STRING_CHAT_ID, chatId);
		i.putExtra(EXTRA_INT_CHAT_TYPE, chatType);
		if (title != null) {
			i.putExtra(EXTRA_STRING_TITLE, title);
		}
		context.startActivity(i);
	}

	public static void startActivity(Context context, String otherUsername, String chatId, int chatType) {
		startActivity(context, otherUsername, chatId, chatType, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		cleanUnreadNum();
		//取消删除标志
		String deletedIdsStr = SharedPreferenceUtils.getInstance(this).getString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants.SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS, "");
		List<String> deletedIdsList = StringUtils.str2ArrayList(deletedIdsStr, ",");
		deletedIdsList.remove(mChatGroupId);
		SharedPreferenceUtils.getInstance(this).putString(Constants.DEFAULT_SHARED_PREFERENCE_NAME, Constants.SHAREDPREFERENCE_KEY_DElETED_CHAT_IDS, StringUtils.array2Str(deletedIdsList, ","));
	}

	@Override
	protected void onStop() {
		super.onStop();
		MsgController.getInstance(this).setCurChatGroupId(null);
	}

	@Override
	public void finish() {
		// 停止播放
		if (mMediaPlayer != null) {
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		unregisterReceiver(mMsgReceiver);
		unregisterReceiver(mCloseReceiver);
	}

}
