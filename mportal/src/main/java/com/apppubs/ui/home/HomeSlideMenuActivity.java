package com.apppubs.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.bean.App;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.Weather;
import com.apppubs.presenter.HomePresenter;
import com.apppubs.presenter.HomeSlidePresenter;
import com.apppubs.ui.activity.SplashActivity;
import com.apppubs.ui.activity.ViewCourier;
import com.apppubs.ui.activity.WeatherActivity;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.model.IAPCallback;
import com.apppubs.service.DownloadAppService;
import com.apppubs.util.LogM;
import com.apppubs.util.ServiceUtils;
import com.apppubs.util.StringUtils;
import com.apppubs.util.SystemUtils;
import com.apppubs.util.Tools;
import com.apppubs.util.Utils;
import com.apppubs.util.WeatherUtils;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.TitleBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 主界面
 * 
 */
public class HomeSlideMenuActivity extends HomeBaseActivity implements OnItemClickListener, AMapLocationListener, Runnable, IHomeSlideMenuView {

	private SlidingActivityHelper mHelper;
	private SlidingMenu mSlidingMenu;
	private SharedPreferences getShare;// 天气信息
	private ArrayList<Weather> mWeatherInfos;
	private ListView mLeftMenuLv;// 左边菜单
	private GridView mRightMenuGv;// 右边菜单
	private ListAdapter mLeftMenuA;
	private TextView mUsername;
	private ImageView mWetherpic;
	private TextView mWeatherTempTv;
	private TextView mWeatherCityName;
	private RelativeLayout mWeather;
	private String mcityname;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private TextView versionTV;
	private ImageView newVersion;
	private RelativeLayout updateApp;// 版本更新
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (mWeatherInfos == null || mWeatherInfos.size() == 0) {
					break;
				}
				Weather weather = mWeatherInfos.get(0);
				mWetherpic.setImageResource(WeatherUtils.solvedWeather(weather.getWeather()));
				mWeatherTempTv.setText(StringUtils.getTemp(weather.getTemp()));
				mWeatherCityName.setText(mcityname);
				break;

			default:
				break;
			}

		};
	};

	private HomeSlidePresenter mPresenter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNeedTitleBar(false);
		Log.v("HomeActivity", "主界面onCreate");
		mHelper = new SlidingActivityHelper(this);
		mHelper.onCreate(savedInstanceState);
		setContentView(R.layout.act_home_slidemenu);
		init();
		initState();
		mPresenter = new HomeSlidePresenter(this, this);
		mPresenter.onViewCreated();
	}

	@Override
	protected HomePresenter getPresenter() {
		return mPresenter;
	}

	/** 初始化组建 */
	private void init() {
		mWeather = (RelativeLayout) findViewById(R.id.menu_left_weather);
		getShare = getSharedPreferences(WeatherActivity.WEATHRECITYNAMESF, MODE_PRIVATE);
		mSlidingMenu = getSlidingMenu();
		// mSlidingMenu.setMenu(R.layout.menu_left);
		setBehindContentView(R.layout.menu_left);
		mSlidingMenu.setSecondaryMenu(R.layout.menu_right);
		mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);// 设置菜单宽度
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setFadeEnabled(true);
		mSlidingMenu.setFadeDegree(0.5f);
		mSlidingMenu.setFadingEdgeLength(200);
		mSlidingMenu.setShadowWidth(20);
		mSlidingMenu.setShadowDrawable(R.drawable.slidemenu_gradient);
		mLeftMenuLv = (ListView) findViewById(R.id.menu_left_lv);
		mWetherpic = (ImageView) findViewById(R.id.menu_left_weather_pic);
		mWeatherTempTv = (TextView) findViewById(R.id.menu_left_weather_temp);
		mWeatherCityName = (TextView) findViewById(R.id.menu_left_weather_cityname);
		mLeftMenuA = new MenuLeftAdapter(this);
		mLeftMenuLv.setAdapter(mLeftMenuA);
		mLeftMenuLv.setOnItemClickListener(this);
		mRightMenuGv = (GridView) findViewById(R.id.menu_right_gv);
		versionTV = (TextView) findViewById(R.id.menu_right_curversion);
		newVersion = (ImageView) findViewById(R.id.menu_right_newcursion);
		updateApp = (RelativeLayout) findViewById(R.id.menu_right_update);
		mRightMenuGv.setAdapter(new MenuRightAdapter(this));
		mRightMenuGv.setOnItemClickListener(this);
		mTitleBar = (TitleBar) findViewById(R.id.home_tb);

		mUsername = (TextView) findViewById(R.id.menu_right_username);
	}

	/**
	 * 初始化状态
	 */
	public void initState() {
		// 将第一个界面填充
//		mViewCourier.executeInHomeActivity((TMenuItem) mLeftMenuA.getItem(0),this);
		// mTitleBar.setTitle(App.listAll(App.class).get(0).getName());
		mTitleBar.setLeftBtnClickListener(this);
//		mTitleBar.setRightBtnClickListener(this);

		if (mAppContext.getApp().getLayoutLocalScheme() == App.STYLE_SLIDE_MENU) {
			versionTV.setText("当前版本:" + "V" +  mAppContext.getVersionName());
		}
		if(mAppContext.getApp().getWeatherDisplayFlag()==1){
			// 天气信息
			String temp = getShare.getString(WeatherActivity.WEATHERTEMP, "");
			mcityname = getShare.getString(WeatherActivity.WEATHERCITYNAME, "");
			String wetherpic = getShare.getString(WeatherActivity.WEATHERPIC, "");
			
			if (temp.equals("") || mcityname.equals("") || wetherpic.equals("")) {

				/*
				 * * mAMapLocManager.setGpsEnable(false);// ======= /*
				 * aMapLocManager = LocationManagerProxy.getInstance(this);
				 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
				 * API定位采用GPS和网络混合定位方式 <<<<<<< .mine
				 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
				 */
				
				handler.postDelayed(this, 20000);// 设置超过12秒还没有定位到就停止定位
			} else {
				
				mWetherpic.setImageResource(WeatherUtils.solvedWeather(wetherpic));
				mWeatherTempTv.setText(temp);
				System.out.println("打印保存的温度显示，，，1" + temp);
				mWeatherCityName.setText(mcityname);
			}
			
		}else{
			setVisibilityOfViewByResId(R.id.menu_left_weather, View.GONE);
		}
		
		checkVersion();
		
	}

	private void checkVersion() {
		boolean bo = mAppContext.getApp().getLatestVersion() > Utils.getVersionCode(HomeSlideMenuActivity.this);
		System.out.println("打印保存的版本号。。。。" + mAppContext.getApp().getWebAppCode());
		if (bo) {
			newVersion.setVisibility(View.VISIBLE);
			updateApp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// 更新服务正在运行

					mSystemBiz.update(new IAPCallback<String[]>() {

						@Override
						public void onException(APError excepCode) {

						}

						@Override
						public void onDone(final String obj[]) {
							ConfirmDialog dialog = new ConfirmDialog( HomeSlideMenuActivity.this,
									new ConfirmDialog.ConfirmListener() {

										@Override
										public void onCancelClick() {
										}

										@Override
										public void onOkClick() {
											if (ServiceUtils.isServiceRunning(HomeSlideMenuActivity.this,
													DownloadAppService.serviceName)) {
												Toast.makeText(HomeSlideMenuActivity.this, "升级服务已经启动,无需再次启动", Toast.LENGTH_LONG)
														.show();
											} else {
												AppManager.getInstant(mContext).downloadApp(obj[1]);
//												Intent it = new Intent(HomeSlideMenuActivity.this,
//														DownloadAppService.class);
//												it.putExtra(DownloadAppService.SERVICRINTENTURL, obj[1]);
//												it.putExtra(DownloadAppService.SERVACESHARENAME, 0);
//												startService(it);
												System.out.println("启动服务。。。。。。。。。。。。。。");
											}
										}
									}, "检查到有新版本更新",obj[0], "下次", "升级");
							dialog.show();
						}
					});
				}

			});
		} else {
			newVersion.setVisibility(View.GONE);
			updateApp.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Toast.makeText(HomeSlideMenuActivity.this, "当前已是最新版本", Toast.LENGTH_LONG).show();
				}
			});
		}

	}


	@Override
	public TitleBar getTitleBar() {

		return mTitleBar;
	}

	@Override
	protected void onResume() {

		super.onResume();
		// 刷新右边菜单的用户名
		UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
		final String username = user.getUsername();
		if (username != null && !username.equals("")) {
			mUsername.setText(username);
		} else {
			mUsername.setText("点击登录");
		}
		LinearLayout layoutLogin = (LinearLayout) findViewById(R.id.menu_right_login);
		layoutLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				mViewCourier.execute(ViewCourier.ACTION_USER_CENTER);
			}
		});


	}

	public void setTouchMode(int mode) {
		mSlidingMenu.setTouchModeBehind(mode);
	}

	/**
	 * 获取天气消息
	 */
	public void getWeather() {
		// 能否联网
		boolean bo = SystemUtils.canConnectNet(getApplication());
		if (bo) {
			new Thread() {
				@Override
				public void run() {
					super.run();
					try {
						sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					mWeatherInfos = Tools.getWeatherList(HomeSlideMenuActivity.this, mcityname);
					handler.sendEmptyMessage(0);
				}
			}.start();

		} else {
			Toast.makeText(getApplication(), getResources().getString(R.string.network_faile), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.left_back_splash:
			// 跳回启动界面
			Intent intent1 = new Intent(HomeSlideMenuActivity.this, SplashActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
			break;
		case R.id.titlebar_left_btn:// 左边侧滑菜单按钮
			mSlidingMenu.toggle();
			break;
//		case R.id.titlebar_right_btn:// 右边侧滑菜单按钮
//			if (mSlidingMenu.isMenuShowing()) {
//
//				mSlidingMenu.toggle();
//			} else {
//				mSlidingMenu.showSecondaryMenu();
//			}
//			break;
		case R.id.left_setting:// 设置
			mViewCourier.startSettingView(this, null);
			overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
			break;
		case R.id.menu_left_weather:
			intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mHelper.onPostCreate(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#findViewById(int)
	 */
	@Override
	public View findViewById(int id) {
		View v = super.findViewById(id);
		if (v != null)
			return v;
		return mHelper.findViewById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
	 * .Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mHelper.onSaveInstanceState(outState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(int)
	 */
	@Override
	public void setContentView(int id) {
		setContentView(getLayoutInflater().inflate(id, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(android.view.View)
	 */
	@Override
	public void setContentView(View v) {
		setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#setContentView(android.view.View,
	 * android.view.ViewGroup.LayoutParams)
	 */
	@Override
	public void setContentView(View v, LayoutParams params) {
		super.setContentView(v, params);
		mHelper.registerAboveContentView(v, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
	 * setBehindContentView(int)
	 */
	public void setBehindContentView(int id) {
		setBehindContentView(getLayoutInflater().inflate(id, null));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
	 * setBehindContentView(android.view.View)
	 */
	public void setBehindContentView(View v) {
		setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
	 * setBehindContentView(android.view.View,
	 * android.view.ViewGroup.LayoutParams)
	 */
	public void setBehindContentView(View v, LayoutParams params) {
		mHelper.setBehindContentView(v, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#getSlidingMenu
	 * ()
	 */
	public SlidingMenu getSlidingMenu() {
		return mHelper.getSlidingMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#toggle()
	 */
	public void toggle() {
		mHelper.toggle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showAbove()
	 */
	public void showContent() {
		mHelper.showContent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showBehind()
	 */
	public void showMenu() {
		mHelper.showMenu();
		LogM.log(this.getClass(), "打开菜单");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showSecondaryMenu
	 * ()
	 */
	public void showSecondaryMenu() {
		mHelper.showSecondaryMenu();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
	 * setSlidingActionBarEnabled(boolean)
	 */
	public void setSlidingActionBarEnabled(boolean b) {
		mHelper.setSlidingActionBarEnabled(b);
	}

	// /* (non-Javadoc)
	// * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	// */
	// @Override
	// public boolean onKeyUp(int keyCode, KeyEvent event) {
	// boolean b = mHelper.onKeyUp(keyCode, event);
	// if (b) return b;
	// return super.onKeyUp(keyCode, event);
	// }

	/**
	 * 切换主界面Fragment
	 * 
	 * @param fragment
	 */
	private Fragment mCurFrg;

	@Override
	public void changeContent(Fragment fragment) {
		mTitleBar.removeLeft2ndView();
		mTitleBar.removeRight2ndView();
		mTitleBar.removeRightView();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments==null||!fragments.contains(fragment)) {
			transaction.add(R.id.home_container_fgm, fragment);
			transaction.addToBackStack(null);
		}
		if (mCurFrg != null) {

			transaction.hide(mCurFrg);
		}
		transaction.show(fragment);
		mCurFrg = fragment;
		transaction.commit();
		mCurFrg.onAttach(this);

		
//		fragment.changeActivityTitleView(mTitleBar);
	}
	@Override
	protected void setUnreadNumForMenu(String menuId, int num) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		if (parent.getId() == R.id.menu_left_lv) {// 如果是左边菜单被点击
			for (int i = -1; ++i < mLeftMenuLv.getChildCount();) {
				ViewGroup item = (ViewGroup) mLeftMenuLv.getChildAt(i);
				item.setBackgroundColor(Color.TRANSPARENT);
			}
			LogM.log(this.getClass(), "onItemClick" + position);
			mSlidingMenu.toggle();

//			ViewGroup item = (ViewGroup) mLeftMenuLv.getChildAt(position);
			view.setBackgroundColor(Color.parseColor("#30000000"));
			https://192.168.1.140/svn/wmh/trunk/Mportal
			view.postDelayed(new Runnable() {

				@Override
				public void run() {
//					mViewCourier.executeInHomeActivity((TMenuItem) view.getTag(),HomeSlideMenuActivity.this);
				}
			}, 300);

		} else {
//			mViewCourier.executeInHomeActivity((TMenuItem) view.getTag(),this);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if (mSlidingMenu.isMenuShowing()) {
				mHelper.showContent();
				return true;
			}
//			if(mCurFrg!=null&&mCurFrg.onKeyDown(keyCode, event)){
//				return true;
//			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void selectTab(int index) {
		LogM.log(this.getClass(),"selectTab 此方法暂未实现！！！！");
	}

	@Override
	public void setMenus(List<TMenuItem> menus) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v("HomeActivity", "主界面onDestory");
	}


	public class MenuLeftAdapter extends BaseAdapter {

		private Context context;
		private ImageLoader mImageLoader;
		private int mItemHeight;

		private boolean needCenterLayout = true;//是否需要菜单居中,如果菜单字数相同则居中，否则居左
		public MenuLeftAdapter(Context context) {

			this.context = context;
			mImageLoader = ImageLoader.getInstance();
			mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.menu_left_item_height);
			Log.v("MenuLeftAdapter", "MenuLeftAdapter初始化");
			int lenTemp = 0;
			for(TMenuItem mi:mPrimaryMenuList){
				if(lenTemp==0){
					lenTemp = mi.getName().length();
				}else if(lenTemp!=mi.getName().length()){
					needCenterLayout = false;
				}
			}
		}

		@Override
		public int getCount() {
			return mPrimaryMenuList.size();
		}

		@Override
		public Object getItem(int pos) {
			if (mPrimaryMenuList.size() == 0)
				return null;
			return mPrimaryMenuList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int pos, View convertView, ViewGroup container) {
			Log.v("MenuLeftAdapter", "MenuLeftAdapter getView pos:" + pos);
			TMenuItem mi = mPrimaryMenuList.get(pos);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(needCenterLayout?R.layout.item_menu_left_align_center:R.layout.item_menu_left, null);
			
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(-1, mItemHeight);
			convertView.setLayoutParams(param);
			convertView.setTag(mi);
			ImageView iconIv = (ImageView) convertView.findViewById(R.id.left_gv_iv);
			TextView titleTv = (TextView) convertView.findViewById(R.id.left_gv_tv);
			// 填充数据
			mImageLoader.displayImage(mi.getIconpic(), iconIv);
			titleTv.setText(mi.getName());
			if(!needCenterLayout&&mi.getName().length()>6){//如果是左边对齐，而且字数大于6则缩小字体
				titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);			
			}else{
				titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);	
			}
			if (pos == 0) {
				// convertView.setBackgroundResource(R.drawable.menu_list_item_bg);
				convertView.setBackgroundColor(Color.parseColor("#30000000"));
				// titleTv.setTextColor(context.getResources().getColor(R.color.text_menu_left_h));
			}
			return convertView;
		}

	}

	public class MenuRightAdapter extends BaseAdapter {

		private Context context;
		private int mItemWidth;// item宽度，
		private int mItemHeight;// item高度
		// 填充数据
		private ImageLoader mImageloader;

		public MenuRightAdapter(Context context) {
			this.context = context;
			mImageloader = ImageLoader.getInstance();

			mItemWidth = context.getResources().getDimensionPixelSize(R.dimen.menu_right_gv_item_width);
			mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.menu_right_gv_item_height);
		}

		@Override
		public int getCount() {
			return mSecondaryMenuList.size();
		}

		@Override
		public Object getItem(int pos) {
			return mSecondaryMenuList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup container) {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
			TMenuItem mi = mSecondaryMenuList.get(position);
			convertView = inflater.inflate(R.layout.item_menu_right_gv, null);
			convertView.setTag(mi);// 在操作此item时可已取出利用
			AbsListView.LayoutParams param = new AbsListView.LayoutParams(mItemWidth, mItemHeight);
			convertView.setLayoutParams(param);
			ImageView iconIv = (ImageView) convertView.findViewById(R.id.right_gv_iv);
			TextView titleTv = (TextView) convertView.findViewById(R.id.channels_gv_tv);

			// 填充数据
			if (mi.getIconpic() != null) {
				iconIv.setVisibility(View.VISIBLE);
				mImageloader.displayImage(mi.getIconpic(), iconIv);
			} else {
				iconIv.setVisibility(View.GONE);
			}
			titleTv.setText(mi.getName());

			return convertView;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();// 停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
	}


	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")" + "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:" + convertToTime(location.getTime()) + "\n城市编码:"
					+ cityCode + "\n位置描述:" + desc + "\n省:" + location.getProvince() + "\n市:" + location.getCity()
					+ "\n区(县):" + location.getDistrict() + "\n区域编码:" + location.getAdCode());
			mcityname = location.getCity();
			getWeather();
			stopLocation();// 停止定位
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			stopLocation();// 销毁掉定位
		}
	}

	public static void show(Context context, String info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static void show(Context context, int info) {
		Toast.makeText(context, info, Toast.LENGTH_LONG).show();
	}

	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}
}
