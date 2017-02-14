package com.mportal.client.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.text.TextUtilsCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.bean.App;
import com.mportal.client.bean.AppConfig;
import com.mportal.client.bean.City;
import com.mportal.client.bean.Client;
import com.mportal.client.bean.Collection;
import com.mportal.client.bean.Comment;
import com.mportal.client.bean.Department;
import com.mportal.client.bean.HeadPic;
import com.mportal.client.bean.History;
import com.mportal.client.bean.MenuGroup;
import com.mportal.client.bean.MenuItem;
import com.mportal.client.bean.MsgRecord;
import com.mportal.client.bean.NewsChannel;
import com.mportal.client.bean.NewsInfo;
import com.mportal.client.bean.Paper;
import com.mportal.client.bean.ServiceNo;
import com.mportal.client.bean.TitleMenu;
import com.mportal.client.bean.User;
import com.mportal.client.bean.UserDeptLink;
import com.mportal.client.bean.WeiboInfo;
import com.mportal.client.constant.Constants;
import com.mportal.client.constant.SystemConfig;
import com.mportal.client.constant.URLs;
import com.mportal.client.message.model.UserBussiness;
import com.mportal.client.util.ACache;
import com.mportal.client.util.FileUtils;
import com.mportal.client.util.JSONUtils;
import com.mportal.client.util.JSONResult;
import com.mportal.client.util.LogM;
import com.mportal.client.util.MathUtils;
import com.mportal.client.util.Utils;
import com.mportal.client.util.WebUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orm.SugarRecord;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.IExtensionModule;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imkit.plugin.ImagePlugin;
import io.rong.imkit.widget.provider.FilePlugin;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

/**
 * 
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen 此类处理系统级别的业务，如初始化系统 包括初始化应用信息，菜单信息，资讯频道 ChangeLog:
 * 2015年1月4日 by zhangwen create
 * 
 * 
 */
public class SystemBussiness extends BaseBussiness {

	private float PIC_RATIO = 0.56f;

	public interface UpdateListenser {
		void onHaveNewVersion(String updateurl, int newVersionCode, int curVersionCode);

		void onHaveNoNewVersion(int vurVersionCode);
	}

	private Context mContext;

	private static SystemBussiness mSystemBussiness;

	private SystemBussiness(Context context) {
		mContext = context;
	}

	public static SystemBussiness getInstance(Context context) {
		if (mSystemBussiness == null) {
			synchronized (SystemBussiness.class) {
				if (mSystemBussiness == null) {
					mSystemBussiness = new SystemBussiness(context);
				}
			}
		}
		return mSystemBussiness;

	}

	public Future<?> update(final BussinessCallbackCommon<String[]> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				String updateverurl = URLs.URL_UPDATE + "appcode=" + URLs.appCode + "&type=android&clientkey="
						+ URLs.CLIENTKEY;
				System.out.println("更新链接 ，，，" + updateverurl);
				try {
					String verJson = WebUtils.requestWithGet(updateverurl);
					JSONObject jsonO = new JSONObject(verJson);
					String[] result = new String[3];
					result[0] = jsonO.getString("appdesc");
					result[1] = jsonO.getString("updateurl");
					result[2] = jsonO.getString("version");
					result[2] = "2001003";
 					sHandler.post(new OnDoneRun<String[]>(callback, result));// 与主线程的通信
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<String[]>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<String[]>(callback));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});

		return f;
	}

	public void downloadApk(String url, BussinessCallbackCommon<String> callback) {
		try {
			FileUtils.asyDownload(url, FileUtils.getAppExternalStorageFile().getAbsolutePath() + "/"
					+ SystemConfig.APP_FILE_NAME, callback);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Future<?> initSystem(final BussinessCallbackCommon<Object> callback) {
		LogM.log(this.getClass(), "系统初始化");

		Runnable run = new Runnable() {

			@Override
			public void run() {
				boolean isFirst = false;
				SQLiteDatabase db = SugarRecord.getDatabase();
				try {
					db.beginTransaction();
					init();
					db.setTransactionSuccessful();
					sHandler.post(new OnDoneRun<Object>(callback, null));
				} catch (JSONException e) {
					clearDataBase(isFirst);
					sHandler.post(new OnExceptionRun<Object>(callback));
					e.printStackTrace();
					return;

				} catch (IOException e) {
					clearDataBase(isFirst);
					sHandler.post(new OnExceptionRun<Object>(callback));
					e.printStackTrace();
					return;
				} catch (InterruptedException e) {
					clearDataBase(isFirst);
					e.printStackTrace();
					LogM.log(this.getClass(), "中断线程");
					return;
				} catch (Exception e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Object>(callback));
				} finally {
					db.endTransaction();
				}

			}

		};
		return sDefaultExecutor.submit(run);

	}

	/**
	 * 清除数据库
	 * 
	 * @param need
	 */
	private void clearDataBase(boolean need) {
		LogM.log(this.getClass(), "清除数据库");
		SugarRecord.deleteAll(MenuItem.class);
		SugarRecord.deleteAll(NewsChannel.class);
		SugarRecord.deleteAll(Collection.class);
		SugarRecord.deleteAll(MenuGroup.class);
		SugarRecord.deleteAll(City.class);

	}

	// 初始化资讯类型菜单
	private void initNewsMenu(String webAppCode, List<MenuItem> messMIList) throws JSONException, IOException,
			InterruptedException {
		// 初始化"资讯"频道
		for (MenuItem mi : messMIList) {
			// 如果此资讯菜单上次可配置则需要恢复之前已经“订阅”的频道
			// http://124.205.71.106:8080/wmh360/json/getchannellist.jsp?webappcode=A09&typeidID=1366524543362&clientkey=bb7c1386d85044ba7a7ae53f3362d634
			String url = URLs.URL_CHANNEL_LIST + "&typeidID=" + mi.getChannelTypeId() + "&webappcode=" + webAppCode;
			// 将之前的已经“订阅”的频道按序提取出，然后从服务器获取新的频道列表，根据之前的频道进行保存
			List<NewsChannel> oldChannelL = SugarRecord.find(NewsChannel.class, "TYPE_ID=? and DISPLAY_ORDER > 0",
					new String[] { mi.getChannelTypeId() + "" }, null, "DISPLAY_ORDER", null);
			List<NewsChannel> clist = WebUtils.requestList(url, NewsChannel.class, "channel");
			boolean needRefresh = oldChannelL.size() > 0;

			SugarRecord.deleteAll(NewsChannel.class, "TYPE_ID=?", mi.getChannelTypeId() + "");// 清除之前数据

			if (needRefresh) {
				for (NewsChannel nc : clist) {
					nc.setTypeId(mi.getChannelTypeId() + "");
					for (int i = -1; ++i < oldChannelL.size();) {

						if (nc.getCode().equals(oldChannelL.get(i).getCode())) {
							nc.setDisplayOrder(i + 1);
							LogM.log(this.getClass(), "setDisplayOrder 保存顺序：" + (i + 1) + "当前频道：" + nc.getName());
							break;
						}
					}
					nc.save();
				}
			} else {
				for (NewsChannel nc : clist) {
					nc.setTypeId(mi.getChannelTypeId() + "");
					nc.save();
				}
			}

			// 初始化资讯菜单下"头图"
			String churl = URLs.URL_HEAD_PIC + "&channeltypeid=" + mi.getChannelTypeId() + "&webappcode=" + webAppCode;
			List<HeadPic> chList = WebUtils.requestList(churl, HeadPic.class, "tgpic");
			for (HeadPic hp : chList) {
				hp.setChannelTypeId(mi.getChannelTypeId() + "");
				hp.save();
			}

		}

		// 处理完成之后

	}

	/**
	 * 初始化报纸
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws JSONException
	 */
	private void initPaper() throws IOException, InterruptedException, JSONException {
		LogM.log(this.getClass(), "初始化报纸");
		SugarRecord.deleteAll(Paper.class);
		String url = URLs.URL_PAPER_LIST;
		List<Paper> paperList = WebUtils.requestList(url, Paper.class, "paper");
		for (Paper p : paperList) {
			p.save();
		}

	}

	private void initMenuGroup(List<MenuItem> menuItemList) throws JSONException, IOException, InterruptedException {
		LogM.log(this.getClass(), "更新菜单布局");
		SugarRecord.deleteAll(MenuGroup.class);

		for (MenuItem mi : menuItemList) {
			List<MenuGroup> mgList = WebUtils.requestList(URLs.URL_SUBMENU_GROUP + "&id=" + mi.getId(),
					MenuGroup.class, "appsubmenus");

			if (mgList != null) {
				for (MenuGroup mg : mgList) {
					mg.setSuperId(mi.getId());
					mg.save();

				}
			}

		}

	}

	/**
	 * 启动请求,记录本次启动信息
	 */
	public void makeStartUpRequest() {

		sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					WebUtils.requestWithGet(makeLogRequestStr(mContext));
				} catch (IOException e) {

					e.printStackTrace();
				} catch (InterruptedException e) {

					e.printStackTrace();
				}
			}
		});
	}

	private synchronized void init() throws IOException, InterruptedException, JSONException {

		// 如果是第一次初始化，无论那个步骤出现问题都清空数据库重新初始化
		App localApp = MportalApplication.app;
		String orientationFlag = Utils.isScreenHorizontal(mContext)?"1":"0";
		String deviceFlag = Utils.isPad(mContext)?"4":"3";
		String screenDimenFlag = null;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenDimenFlag = dm.widthPixels+"X"+dm.heightPixels;
		//如果是新版本第一次启动
		System.out.println("当前版本，"+Utils.getVersionCode(mContext)+"上一次启动的版本："+localApp.getPreWorkingVersion());
		if(Utils.getVersionCode(mContext)>localApp.getPreWorkingVersion()){
			System.out.println("新版本第一次启动");
			if(Utils.getVersionCode(mContext)/1000%1000==1){//代码库版本为1时
				System.out.println("代码库版本为1时");
				UserBussiness userBussiness = UserBussiness.getInstance(mContext);
				User user = new User();
				MportalApplication.saveAndRefreshUser(mContext, user);
			}
		}
		String url = String.format(URLs.URL_APPINFO, MportalApplication.user.getOrgCode(),orientationFlag,deviceFlag,screenDimenFlag);
		App remoteApp = WebUtils.request(url, App.class, "app");

		if (localApp.getInitTimes() == 0) {
			localApp = remoteApp;
			localApp.setLayoutLocalScheme(localApp.getLayoutScheme());
			MportalApplication.systemSettings.setTheme(remoteApp.getDefaultTheme());
			MportalApplication.commitAndRefreshSystemSettings(MportalApplication.systemSettings, mContext);
			LogM.log(this.getClass(), "初始化颜色 appRemote.getDefaultTheme" + remoteApp.getDefaultTheme());
			generateStanceDrawable(remoteApp.getName());
			generateMediumStance();
			// 清除通讯录
			SugarRecord.deleteAll(User.class);
			SugarRecord.deleteAll(UserDeptLink.class);
			SugarRecord.deleteAll(Department.class);
			SugarRecord.deleteAll(MsgRecord.class);
			initDatabase();

		} else {
			localApp.setAllModifyUserInfo(remoteApp.getAllModifyUserInfo());
			localApp.setAllowChat(remoteApp.getAllowChat());
			localApp.setAllowRegister(remoteApp.getAllowRegister());
			localApp.setBaiduPushApiKey(remoteApp.getBaiduPushApiKey());
			localApp.setBgPicURL(remoteApp.getBgPicURL());
			localApp.setChannelupdatetime(remoteApp.getChannelUpdateTime());
			localApp.setCode(remoteApp.getCode());
			localApp.setContent(remoteApp.getContent());
			localApp.setLatestVersion(remoteApp.getLatestVersion());
			localApp.setLayoutScheme(remoteApp.getLayoutScheme());
			localApp.setLoginFlag(remoteApp.getLoginFlag());
			localApp.setLoginPicUrl(remoteApp.getLoginPicUrl());
			localApp.setMenuGroupUpdateTime(remoteApp.getMenuGroupUpdateTime());
			localApp.setMenuUpdateTime(remoteApp.getMenuUpdateTime());
			localApp.setName(remoteApp.getName());
			localApp.setStartUpPic(remoteApp.getStartUpPic());
			localApp.setWebAppCode(remoteApp.getWebAppCode());
			localApp.setNeedForceUploadAddressbook(remoteApp.getNeedForceUploadAddressbook());
			localApp.setAddressbookVersion(remoteApp.getAddressbookVersion());
			localApp.setWeatherDisplayFlag(remoteApp.getWeatherDisplayFlag());
			localApp.setPushVendorType(remoteApp.getPushVendorType());
			localApp.setDefaultServiceNoId(remoteApp.getDefaultServiceNoId());
			localApp.setWebLoginUrl(remoteApp.getWebLoginUrl());
		}

		boolean haveNewspaperMenu = false;// 是否有报纸菜单
		// 如果菜单更新了则全部初始化
		if (localApp.getMenuLocalUpdateTime() == null
				|| !localApp.getMenuLocalUpdateTime().equals(localApp.getMenuUpdateTime())) {

			// 初始化菜单,
			List<MenuItem> menuList = WebUtils.requestList(URLs.URL_MENU, MenuItem.class, "apps");

			// 清除菜单，资讯，以及头图等信息
			SugarRecord.deleteAll(MenuItem.class);
			SugarRecord.deleteAll(NewsChannel.class);
			SugarRecord.deleteAll(HeadPic.class);
			SugarRecord.deleteAll(MenuGroup.class);
			List<MenuItem> messMIList = new ArrayList<MenuItem>();
			List<MenuItem> menuGroupMiList = new ArrayList<MenuItem>();
			for (MenuItem mi : menuList) {

				mi.save();
				if (mi.getChannelTypeId() != null && !mi.getChannelTypeId().equals("")) {
					messMIList.add(mi);
				}
				if (mi.getUrl().contains("$menu")||mi.getUrl().startsWith("apppubs://menugroups")) {
					menuGroupMiList.add(mi);
				}
				if (mi.getUrl().equals(MenuItem.MENU_URL_NEWSPAPER)) {
					haveNewspaperMenu = true;
				}
			}
			LogM.log(this.getClass(), "保存菜单完成");

			initNewsMenu(localApp.getWebAppCode(), messMIList);
//			initMenuGroup(menuGroupMiList);
			initTitleMenu();

			localApp.setMenuLocalUpdateTime(localApp.getMenuUpdateTime());
			localApp.setMenuGroupLocalUpdateTime(localApp.getMenuGroupUpdateTime());

		} else if ((localApp.getChannelLocalUpdateTime() == null && localApp.getChannelUpdateTime() != null)
				|| (localApp.getChannelUpdateTime() != null && !localApp.getChannelUpdateTime().equals(
						localApp.getChannelLocalUpdateTime()))) {
			// 如果菜单没有更新但频道更新了则只初始化资讯类型也就是资讯频道
			// 如果更新之前频道是可配置的且更新后是也是可配置的则需要恢复用户“订阅”的频道
			// SugarRecord.deleteAll(NewsChannel.class);
			// SugarRecord.deleteAll(HeadPic.class);
			List<MenuItem> miL = SugarRecord.find(MenuItem.class, "url = 'app:{$news}'", null, null, null, null);
			if (miL != null && miL.size() > 0) {
				initNewsMenu(localApp.getWebAppCode(), miL);
			}
			localApp.setChannelLocalUpdateTime(localApp.getChannelUpdateTime());
		}

		if (localApp.getMenuGroupLocalUpdateTime() != null
				&& localApp.getMenuGroupLocalUpdateTime().before(localApp.getMenuGroupUpdateTime())) {
			LogM.log(this.getClass(), "需要更新");
			List<MenuItem> miList = SugarRecord.find(MenuItem.class, "URL like ? or URL like ?", new String[]{"%$menu%","apppubs://menugroups"}, null, null, null);
			initMenuGroup(miList);
			localApp.setMenuGroupLocalUpdateTime(localApp.getMenuGroupUpdateTime());
		}

		if (haveNewspaperMenu) {
			initPaper();
		}
		localApp.setStartupTimes(localApp.getInitTimes() + 1);
		MportalApplication.commitApp(mContext, localApp);
		
		//初始化appconfig,首次初始化必须使用同步方法
		
		Object appConfig = FileUtils.readObj(mContext, Constants.FILE_NAME_APP_CONFIG);
		if(appConfig==null){
			syncAppConfig();
		}else{
			AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>(){
				@Override
				protected String doInBackground(String... params) {
					try {
						syncAppConfig();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					return null;
				}
			};
			asyncTask.execute("");
			
		}
	
		
	}

	/**
	 * 初始化标题栏左右的菜单
	 * 
	 * @throws JSONException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private void initTitleMenu() throws IOException, InterruptedException, JSONException {
		SugarRecord.deleteAll(TitleMenu.class);
		List<TitleMenu> list = WebUtils.requestList(String.format(URLs.URL_TITLE_MENU, ""), TitleMenu.class,
				"resultinfo");
		SugarRecord.saveInTx(list);
	}

	/**
	 * 生成站位图保存在本地files文件夹下名称为stance.png 中的大小的站位图 stance_medium.png
	 * 与屏幕相同的展位图stance_pic.png
	 * 
	 * @throws FileNotFoundException
	 */
	private void generateStanceDrawable(String str) throws FileNotFoundException {
		LogM.log(this.getClass(), "生成占位图");
		float width = mContext.getResources().getDimension(R.dimen.list_item_image_width);
		float height = mContext.getResources().getDimension(R.dimen.list_item_image_height);
		float textsize = mContext.getResources().getDimension(R.dimen.list_item_image_textsize);

		Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Config.RGB_565); // Load
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#CACACA"));
		paint.setTextSize(textsize);
		paint.setAntiAlias(true);
		canvas.drawColor(Color.parseColor("#E6E6E6"));
		canvas.drawText(str, (width - textsize * str.length()) / 2, (height + textsize) / 2, paint);
		FileOutputStream fos = null;
		try {
			File file = new File(mContext.getFilesDir(), "stance.png");
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	private void generateMediumStance() {
		LogM.log(this.getClass(), "生成中等大小站位图站位图");
		DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
		int width = dm.widthPixels;
		int height = (int) (width * PIC_RATIO);
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565); // Load
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.parseColor("#E6E6E6"));// 绘制灰色背景
		// 绘制图标

		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#CACACA"));
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);

		Resources res = mContext.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.icon);
		int bmpW = bmp.getWidth();
		int bmpH = bmp.getHeight();
		int paintX = (int) (width * 0.5 - bmpW * 0.5);
		int paintY = (int) (height * 0.5 - bmpH * 0.5);
		canvas.drawBitmap(bmp, new Rect(0, 0, bmpW, bmpH), new Rect(paintX, paintY, paintX + bmpW, paintY + bmpH),
				paint);

		FileOutputStream fos = null;
		try {
			File file = new File(mContext.getFilesDir(), "stance_pic.png");
			fos = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, fos);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	// 构造启动日志请求url
	private String makeLogRequestStr(Context context) {
		Client mClientInfo = null;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		mClientInfo = new Client();
		mClientInfo.setmOs(Build.VERSION.RELEASE);
		mClientInfo.setmDev(Build.DEVICE);
		mClientInfo.setmLang(Locale.getDefault().getLanguage());
		mClientInfo.setmNation(Locale.getDefault().getCountry());
		mClientInfo.setmImei(tm.getDeviceId());

		mClientInfo.setmAppVersion(Utils.getVersionCode(mContext) + "");
		// mClientInfo.setmClientKey(Util.md5("CmsClient"));
		mClientInfo.setmClientKey(URLs.CLIENTKEY);
		mClientInfo.setmSms("");
		mClientInfo.setmFrom(URLs.appCode);
		mClientInfo.setmSerialNumber(getMachineId());

		StringBuilder sb = new StringBuilder(URLs.baseURL);
		sb.append("wmh360/json/getclientinfo.jsp?imei=").append(mClientInfo.getmSerialNumber());
		sb.append("&os=").append(mClientInfo.getmOs());
		sb.append("&dev=").append(mClientInfo.getmDev());
		sb.append("&lang=").append(mClientInfo.getmLang());
		sb.append("&nation=").append(mClientInfo.getmNation());
		sb.append("&sms=").append(mClientInfo.getmSms());
		sb.append("&appversion=").append(mClientInfo.getmAppVersion());
		sb.append("&clientkey=").append(mClientInfo.getmClientKey());
		sb.append("&fr=").append(mClientInfo.getmFrom());
		return sb.toString();

	}

	/**
	 * 机器唯一标识
	 */
	public String getMachineId() {
		return MathUtils.MD5("sdk=" + Build.VERSION.SDK_INT + "|" + "model=" + Build.MODEL + "|" + Build.SERIAL + "|"
				+ Build.DEVICE);

	}

	/**
	 * 初始化数据库
	 * 
	 * @return
	 */

	private final int BUFFER_SIZE = 400000;

	private void initDatabase() {

		SugarRecord.deleteAll(City.class);
		// // 打开raw中得数据库文件，获得stream流
		InputStream stream = this.mContext.getResources().openRawResource(R.raw.china_city);
		SQLiteDatabase dbMain = SugarRecord.getDatabase();
		try {

			// 将获取到的stream 流写入道data中
			File tempFile = new File(mContext.getCacheDir(), "temp.db");
			FileOutputStream outputStream = new FileOutputStream(tempFile);
			byte[] buffer = new byte[BUFFER_SIZE];
			int count = 0;
			while ((count = stream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
			stream.close();

			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(tempFile.getAbsolutePath(), null);

			Cursor cursor = db.rawQuery("SELECT * FROM T_city ORDER BY CityName", null);

			dbMain.beginTransaction();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				City city = new City();
				city.setName(cursor.getString(cursor.getColumnIndex("AllNameSort")));
				city.setNameFirstInitial(cursor.getString(cursor.getColumnIndex("CityName")));
				city.setNameInitial(cursor.getString(cursor.getColumnIndex("NameSort")));
				city.save();
			}
			cursor.close();
			db.close();
			dbMain.setTransactionSuccessful();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			dbMain.endTransaction();
		}
	}

	/**
	 * 同步服务号
	 */
	public void sycnServiceNo(final BussinessCallbackCommon<Object> callback) {
		sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				List<ServiceNo> snl = null;
				try {
					snl = WebUtils.requestList(URLs.URL_SERVICE_LIST, ServiceNo.class);

				} catch (Exception e) {
					e.printStackTrace();
					callback.onException(0);
				}
				SugarRecord.deleteAll(ServiceNo.class);
				for (ServiceNo s : snl) {
					s.save();

				}
				sHandler.post(new OnDoneRun<Object>(callback, null));// 与主线程的通信
			}
		});

	}

	public void syncAppConfig() throws IOException, InterruptedException {

		String result = WebUtils.requestWithGet(String.format(URLs.URL_APP_CONFIG,""));
		JSONResult jsonResult = JSONResult.compile(result);
		if (jsonResult.resultCode != 1) {
			LogM.log(this.getClass(), "获取appconfig错误");
		} else {
			Map<String, String> resultMap = jsonResult.getResultMap();
			// service_id":"1428155175898","address_apiflag":"0","usersync_url":"","adbookversion":"5","adbookauth":"1","deptsync_url":"","address_depturl":"http://202.99.19.140:8080/wmh360/json/getaddressdata.jsp?appcode=D20&flag=dept","address_encflag":"1","chatflag":"0","address_deptuserurl":"http://202.99.19.140:8080/wmh360/json/getaddressdata.jsp?appcode=D20&flag=deptuser","userauth_url":"","adbookupdateflag":"0","address_userurl":"http://202.99.19.140:8080/wmh360/json/getaddressdata.jsp?appcode=D20&flag=user","autologout":"1","mduserinfoflag":"0","userauth_flag":"0","userdeptsync_url":""
			App app = MportalApplication.app;
			app.setAddressbookUserUrl(resultMap.get("address_userurl"));
			app.setAddressbookDetpUrl(resultMap.get("address_depturl"));
			app.setAddressbookDeptUserUrl(resultMap.get("address_deptuserurl"));
			app.setAddressbookNeedDecryption(Integer.parseInt(resultMap.get("address_encflag")));
			app.setAddressbookNeedPermission(Integer.parseInt(resultMap.get("adbookauth")));
			app.setDocumentReaderPageUrl(resultMap.get("document_reader_url"));
			app.setAddressbookVersion(Integer.parseInt(resultMap.get("adbookversion")));

			if (app.getAddressbookNeedPermission()==App.NEED){
				String url = String.format(URLs.URL_ADDRESS_PERMISSION, MportalApplication.user.getUserId());
				String permissionResult = WebUtils.requestWithGet(url);
				JSONResult jr = JSONResult.compile(permissionResult);
				if (jr.resultCode==1){
					MportalApplication.user.setAddressbookPermissionString(jr.result);
					MportalApplication.saveAndRefreshUser(mContext, MportalApplication.user);
				}
			}

			MportalApplication.commitApp(mContext);
		}

	}

	public AppConfig getAppConfig(){
		String str = (String) FileUtils.readObj(mContext,Constants.FILE_NAME_APP_CONFIG);
		return JSONUtils.parseObjectFromJson(str,AppConfig.class);
	}

	/**
	 * 更新appconfig，将getappconfig中的数据同步到本地的APP对象中。
	 */
	public void aSyncAppConfig(final Context context, final BussinessCallbackCommon<Object> callback) {
		sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					syncAppConfig();
					sHandler.post(new OnDoneRun<Object>(callback, null));// 与主线程的通信
				} catch (Exception e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Object>(callback));// 与主线程的通信
				}

			}
		});

	}

	public Future<?> getHistoryPage(final int page, final BussinessCallbackCommon<List<History>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = URLs.URL_SERVICE_MESSAGE_INFO_LIST + "&service_id="
							+ MportalApplication.app.getDefaultServiceNoId() + "&username="
							+ MportalApplication.user.getUsername() + "&userid=" + MportalApplication.user.getUserId()
							+ "&curp=1&perp=10";
					List<History> list = WebUtils.requestList(url, History.class, "info");
					sHandler.post(new OnDoneRun<List<History>>(callback, list));// 与主线程的通信
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<History>>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<History>>(callback));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});

		return f;
	}

	public Future<?> getWeiBoInfo(final BussinessCallbackCommon<List<WeiboInfo>> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = URLs.URL_WEIBO;
					List<WeiboInfo> list = WebUtils.requestList(url, WeiboInfo.class, "weibo");
					sHandler.post(new OnDoneRun<List<WeiboInfo>>(callback, list));// 与主线程的通信
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<WeiboInfo>>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<WeiboInfo>>(callback));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});
		return f;
	}

	/**
	 * 注册 修改 useridstr 用户的id emailstr passwordstr clientidstr mobilestr
	 * nicknamestr
	 * 
	 * @param usernamestr
	 * @param emailstr
	 * @param passwordstr
	 * @param mobilestr
	 * @param nicknamestr
	 * @param callback
	 * @return
	 */
	public Future<?> postZhuce(final String usernamestr, final String emailstr, final String passwordstr,
			final String mobilestr, final String nicknamestr, final BussinessCallbackCommon<String> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				String info = "";
				String url = URLs.URL_ZHUCE;
				HttpClient hc = new DefaultHttpClient();
				HttpPost hp = new HttpPost(url);

				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				BasicNameValuePair bp = new BasicNameValuePair("usernamestr", usernamestr);
				BasicNameValuePair bp1 = new BasicNameValuePair("emailstr", emailstr);
				BasicNameValuePair bp2 = new BasicNameValuePair("passwordstr", passwordstr);
				BasicNameValuePair bp3 = new BasicNameValuePair("clientidstr", URLs.appCode);
				BasicNameValuePair bp4 = new BasicNameValuePair("mobilestr", mobilestr);
				BasicNameValuePair bp5 = new BasicNameValuePair("nicknamestr", nicknamestr);
				parameters.add(bp);
				parameters.add(bp1);
				parameters.add(bp2);
				parameters.add(bp3);
				parameters.add(bp4);
				parameters.add(bp5);
				HttpEntity entity;
				/*
				 * // 编码方式为HTTP.UTF_8) hp.setEntity(new
				 * UrlEncodedFormEntity(parameters, HTTP.UTF_8)); // 告知服务器端解码
				 * hp.setHeader("Content-Type",
				 * "application/x-www-form-urlencoded; charset=utf-8");
				 * HttpResponse hr = hc.execute(hp); String data =
				 * EntityUtils.toString(hr.getEntity(), "utf-8");
				 */
				try {
					// 编码方式为HTTP.UTF_8)
					hp.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
					// 告知服务器端解码
					hp.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					HttpResponse hr = hc.execute(hp);
					String resurt = EntityUtils.toString(hr.getEntity(), "utf-8");
					System.out.println("注册后的结果。。。。。。。。。。" + resurt);
					info = resurt.trim();
					sHandler.post(new OnDoneRun<String>(callback, info));// 与主线程的通信
				} catch (IOException e) {
					// TODO Auto-generated catch block
					sHandler.post(new OnExceptionRun<String>(callback));
					e.printStackTrace();
				}

			}
		});

		return f;
	}

	public Future<?> postZhuce1(final String usernamestr, final String emailstr, final String passwordstr,
			final String mobilestr, final String nicknamestr, final BussinessCallbackCommon<String> callback) {
		// TODO Auto-generated method stub
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String info = "";
				String requestUrl = URLs.URL_ZHUCE;
				Map<String, Object> requestParamsMap = new HashMap<String, Object>();
				requestParamsMap.put("usernamestr", usernamestr);
				requestParamsMap.put("emailstr", emailstr);
				requestParamsMap.put("passwordstr", passwordstr);
				requestParamsMap.put("clientidstr", URLs.baseURL);
				requestParamsMap.put("mobilestr", mobilestr);
				requestParamsMap.put("nicknamestr", nicknamestr);
				String resurt = "";
				resurt = WebUtils.requestWithPost(requestUrl, requestParamsMap);
				info = resurt.trim();
				sHandler.post(new OnDoneRun<String>(callback, info));// 与主线程的通信
			}
		});

		return f;
	}

	/**
	 * 修改密码 useridstr 用户的id emailstr passwordstr clientidstr mobilestr
	 * nicknamestr
	 */
	public Future<?> doModify(final String useridstr, final String emailstr, final String passwordstr,
			final String mobilestr, final String nicknamestr, final BussinessCallbackCommon<String> callback) {
		// TODO Auto-generated method stub
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String info = "";
				String url = URLs.URL_DOMODIFY;
				HttpClient hc = new DefaultHttpClient();
				HttpPost hp = new HttpPost(url);
				List<NameValuePair> parameters = new ArrayList<NameValuePair>();
				BasicNameValuePair bp = new BasicNameValuePair("useridstr", useridstr);
				BasicNameValuePair bp1 = new BasicNameValuePair("emailstr", emailstr);
				BasicNameValuePair bp2 = new BasicNameValuePair("passwordstr", passwordstr);
				BasicNameValuePair bp3 = new BasicNameValuePair("clientidstr", URLs.appCode);
				BasicNameValuePair bp4 = new BasicNameValuePair("mobilestr", mobilestr);
				BasicNameValuePair bp5 = new BasicNameValuePair("nicknamestr", nicknamestr);
				parameters.add(bp);
				parameters.add(bp1);
				parameters.add(bp2);
				parameters.add(bp3);
				parameters.add(bp4);
				parameters.add(bp5);
				HttpEntity entity;
				/*
				 * // 编码方式为HTTP.UTF_8) hp.setEntity(new
				 * UrlEncodedFormEntity(parameters, HTTP.UTF_8)); // 告知服务器端解码
				 * hp.setHeader("Content-Type",
				 * "application/x-www-form-urlencoded; charset=utf-8");
				 * HttpResponse hr = hc.execute(hp); String data =
				 * EntityUtils.toString(hr.getEntity(), "utf-8");
				 */
				try {
					// 编码方式为HTTP.UTF_8)
					hp.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
					// 告知服务器端解码
					hp.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					HttpResponse hr = hc.execute(hp);
					String resurt = EntityUtils.toString(hr.getEntity(), "utf-8");
					info = resurt.trim();
					sHandler.post(new OnDoneRun<String>(callback, info));// 与主线程的通信
				} catch (IOException e) {
					// TODO Auto-generated catch block
					sHandler.post(new OnExceptionRun<String>(callback));
					e.printStackTrace();
				}

			}
		});

		return f;

	}

	public Future<?> getCommentList(final String infoid, final int pno, final int pernum, final String clientkey,
			final BussinessCallbackCommon<List<Comment>> callback) {
		// TODO Auto-generated method stub
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = URLs.URL_COMMENTLIST + "?infoid=" + infoid + "&pno=" + pno + "&pernum=" + pernum
							+ "&clientkey=" + clientkey;
					List<Comment> list = WebUtils.requestList(url, Comment.class, "comment");
					sHandler.post(new OnDoneRun<List<Comment>>(callback, list));// 与主线程的通信
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<Comment>>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<List<Comment>>(callback));
				} catch (JSONException e) {

					e.printStackTrace();
				}
			}
		});

		return f;
	}

	public Future<?> getCommentSizeZanCai(final String infoid, final BussinessCallbackCommon<Comment> callback) {
		// TODO Auto-generated method stub
		Future<?> f = sDefaultExecutor.submit(new Runnable() {

			@Override
			public void run() {

				try {
					String url = URLs.URL_INFOIDCOMMENTSIZE + "?infoid=" + infoid + "&clientkey=" + URLs.CLIENTKEY;
					String data = WebUtils.requestWithGet(url);
					JSONObject jo = null;
					try {
						jo = new JSONObject(data);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Comment comment = new Comment();
					try {

						comment.setCommentnum(jo.getString("commentnum"));
						comment.setUpnum(jo.getString("upnum"));
						comment.setDownnum(jo.getString("downnum"));
					} catch (JSONException e) {
						e.printStackTrace();
					}
					sHandler.post(new OnDoneRun<Comment>(callback, comment));// 与主线程的通信
				} catch (IOException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Comment>(callback));
				} catch (InterruptedException e) {
					e.printStackTrace();
					sHandler.post(new OnExceptionRun<Comment>(callback));
				}
			}
		});

		return f;
	}

	public Future<?> getStandardDataTime(final BussinessCallbackCommon<Date> callback) {
		Future<?> f = sDefaultExecutor.submit(new Runnable() {
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(URLs.baseURL);
					URLConnection uc = url.openConnection();// 生成连接对象
					uc.connect(); // 发出连接
					long ld = uc.getDate(); // 取得网站日期时间
					Date date = new Date(ld); // 转换为标准时间对象
					sHandler.post(new OnDoneRun<Date>(callback, date));
				} catch (MalformedURLException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}

		});
		return f;
	}

	public int getCacheSize() {
		File diskCacheDir = ImageLoader.getInstance().getDiskCache().getDirectory();
		try {
			int size = SugarRecord.sumColumn(NewsInfo.class, "SIZE");
			size += (int) FileUtils.getFileSize(diskCacheDir);
			return size;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void clearCache(final BussinessCallbackCommon<Boolean> callback) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				// 删除缓存图片和数据库中所有新闻信息并将所有频道的本地更新时间清除
				File diskCacheDir = ImageLoader.getInstance().getDiskCache().getDirectory();
				try {
					FileUtils.delete(diskCacheDir);
					SugarRecord.deleteAll(NewsInfo.class);
					SugarRecord.update(NewsChannel.class, "LOCAL_LAST_UPDATE_TIME", "", null, null);
					sHandler.post(new OnDoneRun<Boolean>(callback, true));
				} catch (FileNotFoundException e) {

					e.printStackTrace();
				}
			}

		};
		Future<?> f = sDefaultExecutor.submit(r);

	}

	/**
	 * 刷新天气
	 */
	public void refreshWeather() {

	}

	/**
	 * 判断是否系统中是否包含某种类型的菜单
	 * 
	 * @param appURL
	 */
	public boolean containsMenuWithAppURL(String appURL) {
		List<MenuItem> menuList = SugarRecord.find(MenuItem.class, "url=?", appURL);
		if (menuList == null || menuList.isEmpty()) {
			return false;
		}
		return true;

	}

	/**
	 * 登录融云
	 */
	public void loginRC(){
		post(new Runnable() {
			@Override
			public void run() {
				String token = getRCToken();
				if (token!=null){
					loginRCWithToken(token);
				}
			}
		});

	}

	private String getRCToken(){

		String token = null;
		String userId = MportalApplication.user.getUserId();
		String username = MportalApplication.user.getUsername();
		String url = String.format(URLs.URL_RC_TOKEN,userId,username);
		try {
			String response = WebUtils.requestWithGet(url);
			JSONResult jr = JSONResult.compile(response);
			Map<String,String> map = jr.getResultMap();
			token = map.get("token");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return token;
	}

	private void loginRCWithToken(String token){
		RongIM.connect(token, new RongIMClient.ConnectCallback() {

			/**
			 * Token 错误。可以从下面两点检查 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
			 *                  2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
			 */
			@Override
			public void onTokenIncorrect() {

			}

			/**
			 * 连接融云成功
			 * @param userid 当前 token 对应的用户 id
			 */
			@Override
			public void onSuccess(String userid) {
				Log.d("LoginActivity", "--onSuccess" + userid);
				setMyExtensionModule();

			}

			/**
			 * 连接融云失败
			 * @param errorCode 错误码，可到官网 查看错误码对应的注释
			 */
			@Override
			public void onError(RongIMClient.ErrorCode errorCode) {

			}
		});
	}

	public void setMyExtensionModule() {
		List<IExtensionModule> moduleList = RongExtensionManager.getInstance().getExtensionModules();
		IExtensionModule defaultModule = null;
		if (moduleList != null) {
			for (IExtensionModule module : moduleList) {
				if (module instanceof DefaultExtensionModule) {
					defaultModule = module;
					break;
				}
			}
			if (defaultModule != null) {
				RongExtensionManager.getInstance().unregisterExtensionModule(defaultModule);
				RongExtensionManager.getInstance().registerExtensionModule(new MyExtensionModule());
			}
		}
	}


	public class MyExtensionModule extends DefaultExtensionModule {
		private FilePlugin filePlugin;
		private ImagePlugin imagePlugin;
		List<IPluginModule> pluginModules ;
		public MyExtensionModule(){
			pluginModules = new ArrayList<IPluginModule>();
			filePlugin = new FilePlugin();
			imagePlugin = new ImagePlugin();
			pluginModules.add(filePlugin);
			pluginModules.add(imagePlugin);
		}
		@Override
		public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {

			return pluginModules;
		}

	}
	public void inviteUsers(@NonNull final List<String> userIds, @NonNull final BussinessCallbackCommon callback){
		post(new Runnable() {
			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				for (String userId:userIds){
					if (sb.length()>0){
						sb.append(",");
					}
					sb.append(userId);
				}
				String url = String.format(URLs.URL_SEND_INVITE_SMS,sb.toString());
				try {
					String response = WebUtils.requestWithGet(url);
					JSONResult jr = JSONResult.compile(response);
					if (jr.resultCode==1){
						onDone(callback,null);
					}else{
						onException(callback);
					}
				} catch (Exception e) {
					e.printStackTrace();
					onException(callback);
				}
			}
		});
	}

}
