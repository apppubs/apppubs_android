package com.apppubs.d20.activity;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.adbook.IUserInfoViewListener;
import com.apppubs.d20.widget.ConfirmDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ResultParser;
import com.google.zxing.common.HybridBinarizer;
import com.apppubs.d20.R;
import com.sitech.oncon.barcode.camera.CameraManager;
import com.sitech.oncon.barcode.core.CaptureActivityHandler;
import com.sitech.oncon.barcode.core.FinishListener;
import com.sitech.oncon.barcode.core.InactivityTimer;
import com.sitech.oncon.barcode.core.QRCodeReader;
import com.sitech.oncon.barcode.core.RGBLuminanceSource;
import com.sitech.oncon.barcode.core.ViewfinderView;
import com.sitech.oncon.barcode.executor.ResultHandler;

public final class CaptureActivity extends BaseActivity implements OnClickListener, SurfaceHolder.Callback {

	private static final int PERMISSION_CAMERA_REQUEST_CODE = 1;

	public static final String EXTRA_NAME_BOOLEAN_NEED_SELF_RESOLVE = "self_resolve";
	public static final String EXTRA_NAME_STRING_RESULT = "result";

	private static final String TAG = CaptureActivity.class.getSimpleName();
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private TextView statusView;
	private ImageView camera;
	private Result lastResult;
	private boolean hasSurface;
	private IntentSource source;
	private Collection<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	//private Button from_gallery;
	private final int from_photo = 010;
	static final int PARSE_BARCODE_SUC = 3035;
	static final int PARSE_BARCODE_FAIL = 3036;
	String photoPath;
	ProgressDialog mProgress;
	private boolean needSelfResolve;//是否需要自己处理，如：网址提醒打开，文本则复制到内存

	// Dialog dialog;

	enum IntentSource {

		ZXING_LINK, NONE

	}

	Handler barHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PARSE_BARCODE_SUC:
				//viewfinderView.setRun(false);
				showDialog((String) msg.obj);
				break;
			case PARSE_BARCODE_FAIL://扫描失败
				//showDialog((String) msg.obj);
				if (mProgress != null && mProgress.isShowing()) {
					mProgress.dismiss();
				}
				new AlertDialog.Builder(CaptureActivity.this).setTitle("提示").setMessage("扫描失败").setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
				break;
			}
			super.handleMessage(msg);
		}

	};

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public CameraManager getCameraManager() {
		return cameraManager;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.act_capture);

		setTitle("二维码扫描");

		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
			int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
			if (permission != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(new String[]{Manifest.permission.CAMERA},PERMISSION_CAMERA_REQUEST_CODE);
			}else{
				init();
			}
		}else{
			init();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==PERMISSION_CAMERA_REQUEST_CODE){
			if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
				init();
			}else{
				Toast.makeText(this,"请在系统设置中打开相机权限",Toast.LENGTH_LONG).show();
			}
		}
	}

	private void init() {
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		cameraManager = new CameraManager(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		viewfinderView.setCameraManager(cameraManager);
		statusView = (TextView) findViewById(R.id.status_view);

		needSelfResolve = getIntent().getBooleanExtra(EXTRA_NAME_BOOLEAN_NEED_SELF_RESOLVE,true);
	}

	public void showDialog(String msg) {

		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
		}
		if (needSelfResolve){
			resolveResult(msg);
		}else{
		    Intent data = new Intent();
            data.putExtra(EXTRA_NAME_STRING_RESULT,msg);
		    setResult(RESULT_OK,data);
		    finish();
        }

	}

	public String parsLocalPic(String path) {
		String parseOk = null;
		Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF8");

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 先获取原大小
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false; // 获取新的大小
		// 缩放比
		int be = (int) (options.outHeight / (float) 200);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + "   " + h);
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader2 = new QRCodeReader();
		Result result;
		try {
			result = reader2.decode(bitmap1, hints);
			android.util.Log.i("steven", "result:" + result);
			parseOk = result.getText();

		} catch (NotFoundException e) {
			parseOk = null;
		} catch (ChecksumException e) {
			parseOk = null;
		} catch (FormatException e) {
			parseOk = null;
		}
		return parseOk;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		android.util.Log.i("steven", "data.getData()" + data);
		if (data != null) {
			mProgress = new ProgressDialog(CaptureActivity.this);
			mProgress.setMessage("正在扫描...");
			mProgress.setCancelable(false);
			mProgress.show();
			final ContentResolver resolver = getContentResolver();
			if (requestCode == from_photo) {
				if (resultCode == RESULT_OK) {
					Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
					if (cursor.moveToFirst()) {
						photoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
					}
					cursor.close();
					new Thread(new Runnable() {
						@Override
						public void run() {
							Looper.prepare();
							String result = parsLocalPic(photoPath);
							if (result != null) {
								Message m = Message.obtain();
								m.what = PARSE_BARCODE_SUC;
								m.obj = result;
								barHandler.sendMessage(m);
							} else {
								Message m = Message.obtain();
								m.what = PARSE_BARCODE_FAIL;
								m.obj = "扫描失败！";
								barHandler.sendMessage(m);
							}
							Looper.loop();
						}
					}).start();
				}

			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
			int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
			if (permission == PackageManager.PERMISSION_GRANTED) {
				resume();
			}
		}else{
			resume();
		}
	}

	private void resume() {
		handler = null;
		lastResult = null;
		resetStatusView();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		inactivityTimer.onResume();
		source = IntentSource.NONE;
		decodeFormats = null;
	}

	@Override
	protected void onPause() {

		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
			int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
			if (permission == PackageManager.PERMISSION_GRANTED) {
				pause();
			}
		}else{
			pause();
		}
		super.onPause();
	}

	private void pause() {
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		inactivityTimer.onPause();
		cameraManager.closeDriver();
		if (!hasSurface) {
			SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
			SurfaceHolder surfaceHolder = surfaceView.getHolder();
			surfaceHolder.removeCallback(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (inactivityTimer!=null){
			inactivityTimer.shutdown();
		}
		if (mProgress!= null) {
			mProgress.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
				restartPreviewAfterDelay(0L);
				return true;
			}
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			cameraManager.setTorch(false);
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			cameraManager.setTorch(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// 这里初始化界面，调用初始化相机
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (holder == null) {
			Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
		}
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	private static ParsedResult parseResult(Result rawResult) {
		return ResultParser.parseResult(rawResult);
	}

	// 解析二维码
	public void handleDecode(Result rawResult, Bitmap barcode) {
		inactivityTimer.onActivity();
		lastResult = rawResult;

		ResultHandler resultHandler = new ResultHandler(parseResult(rawResult));

		boolean fromLiveScan = barcode != null;
		if (barcode == null) {
			android.util.Log.i("steven", "rawResult.getBarcodeFormat().toString():" + rawResult.getBarcodeFormat().toString());
			android.util.Log.i("steven", "resultHandler.getType().toString():" + resultHandler.getType().toString());
			android.util.Log.i("steven", "resultHandler.getDisplayContents():" + resultHandler.getDisplayContents());
		} else {
			showDialog(resultHandler.getDisplayContents().toString());
		}
	}

	// 初始化照相机，CaptureActivityHandler解码
	private void initCamera(SurfaceHolder surfaceHolder) {
		if (surfaceHolder == null) {
			throw new IllegalStateException("No SurfaceHolder provided");
		}
		if (cameraManager.isOpen()) {
			Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
			return;
		}
		try {
			cameraManager.openDriver(surfaceHolder);
			if (handler == null) {
				handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
			}
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
		} catch (RuntimeException e) {
			Log.w(TAG, "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit();
		}
	}

	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.confirm, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void restartPreviewAfterDelay(long delayMS) {
		if (handler != null) {
			handler.sendEmptyMessageDelayed(R.id.restart_preview, delayMS);
		}
		resetStatusView();
	}

	private void resetStatusView() {
		statusView.setText(R.string.msg_default_status);
		statusView.setVisibility(View.VISIBLE);
		viewfinderView.setVisibility(View.VISIBLE);
		lastResult = null;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {

//			StringUtils.showPictures(CaptureActivity.this, from_photo);
//			break;
		default:
			break;
		}
	}

	//pragma private
	private void resolveResult(final String msg){
		if(msg.startsWith("http")){
			new ConfirmDialog(this, new ConfirmDialog.ConfirmListener() {
				@Override
				public void onOkClick() {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					Uri content_url = Uri.parse(msg);
					intent.setData(content_url);
					startActivity(intent);
				}

				@Override
				public void onCancelClick() {
					if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
						restartPreviewAfterDelay(0L);
					}
				}
			},getString(R.string.memo),String.format(getString(R.string.barcode_tow_dimen_success), msg),getString(R.string.cancel),getString(R.string.confirm)).show();
		}else{
			new ConfirmDialog(this, new ConfirmDialog.ConfirmListener() {
				@Override
				public void onOkClick() {
					//获取剪贴板管理服务
					ClipboardManager cm =(ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
					//将文本数据复制到剪贴板
					cm.setText(msg);
					//读取剪贴板数据
					//cm.getText();
					Toast.makeText(mContext,"已复制",Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onCancelClick() {
					if ((source == IntentSource.NONE || source == IntentSource.ZXING_LINK) && lastResult != null) {
						restartPreviewAfterDelay(0L);
					}
				}
			},getString(R.string.memo),String.format(getString(R.string.barcode_one_dimen_success), msg),getString(R.string.cancel),"复制").show();

		}

	}

}
