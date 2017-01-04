package com.mportal.client.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.mportal.client.MportalApplication;
import com.mportal.client.R;
import com.mportal.client.activity.ContainerActivity;
import com.mportal.client.activity.NewsInfoActivity;
import com.mportal.client.bean.HeadPic;
import com.mportal.client.bean.MenuGroup;
import com.mportal.client.bean.MenuItem;
import com.mportal.client.constant.Actions;
import com.mportal.client.constant.Constants;
import com.mportal.client.constant.SystemConfig;
import com.mportal.client.constant.URLs;
import com.mportal.client.util.GsonUtils;
import com.mportal.client.util.LogM;
import com.mportal.client.util.Utils;
import com.mportal.client.util.WebUtils;
import com.mportal.client.view.ConfirmDialog;
import com.mportal.client.view.SlidePicView;
import com.mportal.client.view.SlidePicView.SlidePicItem;
import com.orm.SugarRecord;

/**
 * 菜单列表
 * 
 * Copyright (c) heaven Inc.
 * 
 * Original Author: zhangwen
 * 
 * ChangeLog: 2015年3月5日 by zhangwen create
 * 
 */
public class MenuGroupsFragment extends HomeFragment implements OnClickListener{

	public static final String ARGS_SUPER_ID = "super_id";
	
	private final int BLOCK_COLUMN = 4;// 普通方块排列列数
	private final int BIG_BLOCK_COLUMN = 3;// 大方块排列列数
	/**
	 * 父id
	 */
	private String mSuperId;
	private LinearLayout mContainerLl;
	private Map<String, MenuItem> mMenuMap;
	private List<MenuGroup> mMenuGroupList;
	private int mDividerColor;//分割线颜色 
	private Map<String,TextView> mBadgeMap;//menuiten.id-》徽章textview
	private BroadcastReceiver mRefreshBadgeBR;//刷新徽章的广播接收器
	
	private String mTempResponseMenu;
	
	private String mMenuCache;
	private String mMenuGroupCache;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mSuperId = args.getString(ARGS_SUPER_ID);
		
		mDividerColor = getResources().getColor(R.color.common_divider);
		mRefreshBadgeBR = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				refreshBadge();
			}
		};
		mBadgeMap = new HashMap<String, TextView>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		initRootView();
		return mRootView;
	}

	/**
	 * 初始化rootview
	 */
	private void initRootView() {
		ScrollView sv = new ScrollView(mContext);
		sv.setBackgroundResource(R.color.window_color);
		mContainerLl = new LinearLayout(mContext);
		mContainerLl.setOrientation(LinearLayout.VERTICAL);
		FrameLayout.LayoutParams llLp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		sv.addView(mContainerLl, llLp);
		mRootView = sv;
		
		
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		loadCache();
		loadRemoteMenuData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		mHostActivity.registerReceiver(mRefreshBadgeBR, new IntentFilter(Actions.ACTION_REFRESH_BADGE));
		//每次显示均要刷新徽章
		refreshBadge();
	}
	@Override
	public void onStop() {
		super.onStop();
		mHostActivity.unregisterReceiver(mRefreshBadgeBR);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(!hidden){
			loadRemoteMenuData();
		}
	}
	
	private void loadRemoteMenuData() {
		String url = String.format(URLs.URL_MENUS, mMenuId);
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {

			@Override
			public void onResponse(String response) {
				mTempResponseMenu  = response;
				loadRemoteMenugroup();
			}
		}, null));
	}
	
	private void loadRemoteMenugroup(){
		String url = URLs.URL_SUBMENU_GROUP + "&id=" +mMenuId;
		mRequestQueue.add(new StringRequest(url, new Listener<String>() {
			@Override
			public void onResponse(String response) {
				//本地缓存和远程不同则重现刷新否则不刷新
				if(!(response!=null&&response.equals(mMenuGroupCache)&&mTempResponseMenu!=null&&mTempResponseMenu.equals(mMenuCache))){
					mMenuGroupCache = response;
					mMenuCache = mTempResponseMenu;
					parseMenuResponse(mTempResponseMenu);
					parseMenuGroupData(response);
					refreshContainerLl();
				}
				refreshBadge();
			}
		}, null));
	}
	
	private void loadCache(){
		if((mMenuCache=getMenuCache())!=null&&(mMenuGroupCache=getMenuGroupCache())!=null){
			parseMenuResponse(mMenuCache);
			parseMenuGroupData(mMenuGroupCache);
			refreshContainerLl();
		}
	}
	
	private String getMenuCache(){
		String url = String.format(URLs.URL_MENUS, mMenuId);
		String cache = null;
		if(mRequestQueue.getCache().get(url)!=null){
			cache = new String(mRequestQueue.getCache().get(url).data);
		}
		return cache;
	}
	
	private String getMenuGroupCache(){
		String menuGroupUrl = URLs.URL_SUBMENU_GROUP + "&id=" +mMenuId;
		String cache = null;
		if(mRequestQueue.getCache().get(menuGroupUrl)!=null){
			cache = new String(mRequestQueue.getCache().get(menuGroupUrl).data);
		}
		return cache;
	}
	
	/**
	 * 填充container ll
	 */
	private void refreshContainerLl(){
		mContainerLl.removeAllViews();
		int size = mMenuGroupList.size();
		for (int i = -1; ++i < size;) {

			MenuGroup mg = mMenuGroupList.get(i);
			addBlank(mg.getMarginTop());
			
			if (mg.getDividerTopFlag()==MenuGroup.DIVIDER_TRUE) {
				addDivider();
			}
			
			List<MenuItem> menuGroupMenuList = new ArrayList<MenuItem>();
			String[] ids = mg.getMenuIds().split(",");
			for(int j=-1;++j<ids.length;){
				MenuItem mi = null;
				if((mi=mMenuMap.get(ids[j]))!=null){
					menuGroupMenuList.add(mi);
				}
				
			}
			
			
			
			if (mg.getStyle() == MenuGroup.STYLE_LIST) {

				
				for (int j=-1;++j<menuGroupMenuList.size();) {
					
					MenuItem mi = menuGroupMenuList.get(j);
					if(mi.getUrl().contains("app:{$widget_webapp}")){
						addWiget(mi);
						continue;//跳过此次循环
					}
					
					if(mi.getUrl().contains(Constants.MENU_URI_SLIDING_PIC)){
						
						addSlidePic(mi.getChannelTypeId());
						continue;//跳过此次循环
					}
					
					
					RelativeLayout rl = (RelativeLayout) mInflater.inflate(R.layout.item_menu_lv, null);
					TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
					tv.setText(mi.getName());
					ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
					setVisibilityOfViewByResId(rl, R.id.menu_reddot, View.GONE);
					if(!TextUtils.isEmpty(mi.getBadgeURL())){
						mBadgeMap.put(mi.getId(), (TextView)rl.findViewById(R.id.menu_reddot));
//						setViewVisibilityByRemoteBadgeURL((TextView)rl.findViewById(R.id.menu_reddot),mi.getBadgeURL());
					}
					
					
					mImageLoader.displayImage(mi.getIconpic(), iv);
					
					//如果是电话格式则不需要小箭头
					if(mi.getUrl().startsWith("tel:")){
						setVisibilityOfViewByResId(rl, R.id.arrow, View.GONE);
					}
					mContainerLl.addView(rl);
					rl.setTag(mi.getId());
					rl.setOnClickListener(this);
					//此记录不是最后一条而且内部允许划线
					if(mg.getDividerInternalFlag()==MenuGroup.DIVIDER_TRUE&&j!=menuGroupMenuList.size()-1){
						View line = new View(mHostActivity);
						LayoutParams lp1 = new LayoutParams(Utils.dip2px(mContext, 20), 1);
						line.setBackgroundColor(Color.parseColor("#FFFFFF"));
						mContainerLl.addView(line, lp1);
					}
					
				}
			} else if (mg.getStyle() == MenuGroup.STYLE_GRID && menuGroupMenuList.size() == 2) {

				
				// 如果是横排显示且此组菜单仅有两个时使用图片和名称同行显示的方式显示
				
				LinearLayout doubleMenuLl = (LinearLayout) mInflater.inflate(R.layout.item_menu_double_lv, null);
				MenuItem mi1 = menuGroupMenuList.get(0);
				MenuItem mi2 = menuGroupMenuList.get(1);

				LinearLayout ll1 = (LinearLayout) doubleMenuLl.findViewById(R.id.menu_ll1);
				LinearLayout ll2 = (LinearLayout) doubleMenuLl.findViewById(R.id.menu_ll2);

				TextView tv1 = (TextView) doubleMenuLl.findViewById(R.id.menu_tv1);
				ImageView iv1 = (ImageView) doubleMenuLl.findViewById(R.id.menu_iv1);
				TextView tv2 = (TextView) doubleMenuLl.findViewById(R.id.menu_tv2);
				ImageView iv2 = (ImageView) doubleMenuLl.findViewById(R.id.menu_iv2);

				tv1.setText(mi1.getName());
				mImageLoader.displayImage(mi1.getIconpic(), iv1);

				tv2.setText(mi2.getName());
				mImageLoader.displayImage(mi2.getIconpic(), iv2);
				
				ll1.setTag(mi1.getId());
				ll2.setTag(mi2.getId());
				ll1.setOnClickListener(this);
				ll2.setOnClickListener(this);

				mContainerLl.addView(doubleMenuLl);
			}else if (mg.getStyle() == MenuGroup.STYLE_GRID) {

				
				GridLayout gl = new GridLayout(mHostActivity);
				gl.setBackgroundColor(Color.WHITE);
				LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				gl.setColumnCount(menuGroupMenuList.size()==3?BLOCK_COLUMN-1:BLOCK_COLUMN);
				WindowManager wm = mHostActivity.getWindowManager();
				int width = wm.getDefaultDisplay().getWidth();
				
				for (int j = -1; ++j < menuGroupMenuList.size();) {

					MenuItem mi = menuGroupMenuList.get(j);
					GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
					glp.width = width / gl.getColumnCount();
					glp.setGravity(Gravity.FILL);
					RelativeLayout rl = (RelativeLayout) mInflater.inflate(R.layout.item_menu_gv, null);
					View verticalLine = rl.findViewById(R.id.vertical_line);
					verticalLine.setVisibility(mg.getDividerInternalFlag()==MenuGroup.DIVIDER_TRUE?View.VISIBLE:View.GONE);
					TextView tv = (TextView) rl.findViewById(R.id.menu_tv);
					tv.setText(mi.getName());
					ImageView iv = (ImageView) rl.findViewById(R.id.menu_iv);
					mImageLoader.displayImage(mi.getIconpic(), iv);
					rl.setTag(mi.getId());
					rl.setOnClickListener(this);
					gl.addView(rl, glp);
					
					if(!TextUtils.isEmpty(mi.getBadgeURL())){
						mBadgeMap.put(mi.getId(), (TextView)rl.findViewById(R.id.menu_reddot));
//						setViewVisibilityByRemoteBadgeURL((TextView)rl.findViewById(R.id.menu_reddot),mi.getBadgeURL());
					}
				}
				mContainerLl.addView(gl, lp1);
			} else if (mg.getStyle() == MenuGroup.STYLE_BIG_GRID_WITH_SLIDEPIC) {


				int itemNum = menuGroupMenuList.size();
				boolean haveSlidePic = false;
				GridLayout gl = new GridLayout(mHostActivity);
				gl.setBackgroundColor(Color.WHITE);
				gl.setColumnCount(BIG_BLOCK_COLUMN);
				WindowManager wm = mHostActivity.getWindowManager();
				int width = wm.getDefaultDisplay().getWidth();
				width = width - gl.getPaddingLeft() - gl.getPaddingRight();

				for (int j = -1; ++j < menuGroupMenuList.size();) {

					MenuItem mi = menuGroupMenuList.get(j);
					// 第一个菜单如果是滚动图类型的话先加上一个滚动图
					if (j == 0 && mi.getUrl().contains("$slidingpic")) {

						SlidePicView spv = new SlidePicView(mContext);
						List<HeadPic> picList = SugarRecord.listAll(HeadPic.class);
						final List<SlidePicItem> list = new ArrayList<SlidePicItem>();
						for (HeadPic hp : picList) {
							SlidePicItem sp = new SlidePicItem();
							sp.picURL = hp.getPicURL();
							sp.title = hp.getTopic();
							sp.infoId = String.valueOf(hp.getInfoid());
							list.add(sp);
						}
						spv.setData(list);
						mContainerLl.addView(spv);

						haveSlidePic = true;
						--itemNum;// 绘制方块边线时需要去掉此item
						continue;// ,其他操作均跳过
					}
					View itemView = LayoutInflater.from(mHostActivity).inflate(R.layout.item_channels_b, null);
					
					if(mg.getDividerInternalFlag()==MenuGroup.DIVIDER_TRUE){
						
						int maxRow = itemNum%BIG_BLOCK_COLUMN==0? itemNum / BIG_BLOCK_COLUMN:itemNum/BIG_BLOCK_COLUMN+1;// 最大行
						int curPos = haveSlidePic ? j - 1 : j;
						
						if (curPos % BIG_BLOCK_COLUMN == BIG_BLOCK_COLUMN - 1 && curPos / BIG_BLOCK_COLUMN == maxRow-1) {// 最右下角的方块
							itemView.setPadding(0, 0, 0, 0);
						} else if (curPos % BIG_BLOCK_COLUMN == BIG_BLOCK_COLUMN - 1) {// 右边的方块
							itemView.setPadding(0, 0, 0, 1);
						} else if (curPos / BIG_BLOCK_COLUMN == maxRow-1) {//最下面的一行
							itemView.setPadding(0, 0, 1, 0);
						} else {
							itemView.setPadding(0, 0, 1, 1);
						}
					}
			
					
					TextView tv = (TextView) itemView.findViewById(R.id.channels_gv_tv);
					ImageView iv = (ImageView) itemView.findViewById(R.id.channels_gv_iv);
					tv.setText(mi.getName());
					tv.setSingleLine();
					mImageLoader.displayImage(mi.getIconpic(), iv);
					itemView.setTag(mi.getId());
					itemView.setOnClickListener(MenuGroupsFragment.this);
					GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
					glp.width = width / gl.getColumnCount();
					glp.setGravity(Gravity.FILL);

					gl.addView(itemView, glp);
					
					if(!TextUtils.isEmpty(mi.getBadgeURL())){
						mBadgeMap.put(mi.getId(), (TextView)itemView.findViewById(R.id.menu_reddot));
//						setViewVisibilityByRemoteBadgeURL((TextView)rl.findViewById(R.id.menu_reddot),mi.getBadgeURL());
					}
					

				}
				LinearLayout.LayoutParams hgvLp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);

				mContainerLl.addView(gl, hgvLp);
			}
			if(mg.getDividerBottomFlag()==MenuGroup.DIVIDER_TRUE){
				addDivider();
			}
			addBlank(mg.getMarginBottom());
		}
		
	}

	/**
	 * 增加滚动图片
	 * @param channelTypeId :此滚动图对应的频道id
	 */
	private void addSlidePic(String channelTypeId) {
		final SlidePicView mSlidePicView = new SlidePicView(mHostActivity,SlidePicView.STYLE_UNDER_PIC,0.56f);
		mSlidePicView.setTitleTextSize(getResources().getDimension(R.dimen.slide_pic_title_text_size));

		mSlidePicView.setOnItemClickListener(new SlidePicView.OnItemClickListener() {

			@Override
			public void onClick(int pos,SlidePicItem item) {
				if(!TextUtils.isEmpty(item.linkType)&&item.linkType.equals("http")){
					Bundle bundle = new Bundle();
					bundle.putString(WebAppFragment.ARGUMENT_STRING_URL, item.linkValue);
					ContainerActivity.startActivity(mContext, WebAppFragment.class,bundle,"正文");
				}else if(!TextUtils.isEmpty(item.linkType)&&item.linkType.equals("info")){
					String[] linkValueArr = item.linkValue.split(",");
					Intent i = new Intent(mHostActivity,NewsInfoActivity.class);
					
					i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_ID,linkValueArr[1]);
					i.putExtra(NewsInfoActivity.EXTRA_STRING_NAME_CHANNELCODE,  linkValueArr[0]);
					mHostActivity.startActivity(i);
					mHostActivity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					
				}
				
			}
		});
		mSlidePicView.setBackgroundColor(0xffffffff);
		
		mContainerLl.addView(mSlidePicView);
		
		new AsyncTask<String, Integer, List<SlidePicItem>>(){

			@Override
			protected List<SlidePicItem> doInBackground(String... params) {
				List<SlidePicItem> slidPicList = new ArrayList<SlidePicItem>();
				try {
					List<Map<String,Object>> list = WebUtils.requestMapList(params[0], "sliding");
					for(Map<String,Object> map : list){
						SlidePicItem  spi = new SlidePicItem();
						spi.picURL = (String) map.get("picurl");
						spi.title = (String) map.get("title");
						spi.linkType = (String)map.get("linktype");
						spi.linkValue = (String)map.get("linkvalue");
						slidPicList.add(spi);
						System.out.println("picurl增加。。。："+spi.picURL);
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return slidPicList;
			}
			
			protected void onPostExecute(java.util.List<SlidePicItem> result) {
				mSlidePicView.setData(result);
				
			};
			
		}.execute(String.format(URLs.URL_PROMOTION_PIC_LIST, channelTypeId));
		
	}

	/**
	 * 增加webview插件
	 * @param mi
	 */
	private void addWiget(MenuItem mi) {
		final WebView wb = new WebView(mContext);
		
		mContainerLl.addView(wb,new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(mContext, 130)));
		wb.setWebViewClient(new WebViewClient(){
			
			@Override
			public boolean shouldOverrideUrlLoading(
					WebView view, String url) {
				if(url.contains("app:logout")){
					
					new ConfirmDialog(mHostActivity,
							new ConfirmDialog.ConfirmListener() {

								@Override
								public void onOkClick() {
									mHostActivity.sendBroadcast(new Intent(Actions.ACTION_LOGOUT));
								}

								@Override
								public void onCancelClick() {

								}
							}, "确定注销登陆吗？", "取消", "注销").show();
					
					
					
			
					
					return true;
				}
				wb.loadUrl(url);
				
				return true;
			}
		});
		WebSettings mSettings = wb.getSettings();
		mSettings.setJavaScriptEnabled(true);
		mSettings.setAppCacheEnabled(false);
//		mSettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		mSettings.setBuiltInZoomControls(false);
		mSettings.setUseWideViewPort(true);
		String url = mi.getUrl().split(",")[1];
		url = SystemConfig.convertUrl(url);
		wb.loadUrl(url);
		System.out.println("插件：url："+url);
	}
	/**
	 * 增加20dp的空白行
	 */
	private void addBlank(int height) {
		View v = new View(mContext);
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dip2px(mHostActivity, height));
		mContainerLl.addView(v, lp);
	}

	/**
	 * 增加水平分割线
	 */
	private void addDivider() {
		View lineV = new View(mContext);
		LayoutParams lineLp = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		lineV.setBackgroundColor(mDividerColor);
		mContainerLl.addView(lineV, lineLp);
	}

	@Override
	public void onClick(View v) {
		String menuId = (String) v.getTag();
		mViewController.executeInHomeActivity(mMenuMap.get(menuId),mHostActivity);
	}
	
	/**
	 * 刷新此界面上的徽章
	 */
	private void refreshBadge(){
		for(String s:mBadgeMap.keySet()){
			MenuItem mi = mMenuMap.get(s);
			String url = SystemConfig.convertUrl(mi.getBadgeURL());
			setViewVisibilityByRemoteBadgeURL(mBadgeMap.get(s), url);
		}
		
	}
	/**
	 * 通过url得到的数字，显示到某个textview上
	 * @param v
	 * @param url
	 */
	private void setViewVisibilityByRemoteBadgeURL(final TextView v,String url){
		new AsyncTask<String, Integer, Integer>() {

			@Override
			protected Integer doInBackground(String... arg) {
				LogM.log(this.getClass(), "arg:"+arg[0]);
				Integer result = 0;
				try {
					
					String numStr = WebUtils.requestWithGet(arg[0]);
					if(!TextUtils.isEmpty(numStr)){
						result = Integer.parseInt(numStr);
					}
					
				}catch(Exception e){
					e.printStackTrace();
					result = 0;
				}
				return result;
			}
			@Override
			protected void onPostExecute(Integer result) {
				
				if(result==0){
					v.setVisibility(View.GONE);
					return;
				}else{
					v.setVisibility(View.VISIBLE);
					if(result>99){
						v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
						v.setText("99+");
					}else{
						v.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
						v.setText(result+"");
					}
					
				}
				
			}
		}.execute(url);
		
	}


	private void parseMenuGroupData(String response) {
		List<MenuGroup> mgList = new ArrayList<MenuGroup>();
		try {
			JSONObject jo;
			jo = new JSONObject(response);
			JSONArray ja = jo.getJSONArray("appsubmenus");
			for(int i=-1;++i<ja.length();){
				MenuGroup mg = GsonUtils.getGson().fromJson(ja.getString(i),MenuGroup.class);
				mgList.add(mg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mMenuGroupList = mgList;
	}

	private void parseMenuResponse(String response) {
		List<MenuItem> menuList = new ArrayList<MenuItem>();
		try {
			JSONObject jo = new JSONObject(response);
			JSONArray ja = jo.getJSONArray("apps");
			for(int i=-1;++i<ja.length();){
				MenuItem mi = GsonUtils.getGson().fromJson( ja.getString(i), MenuItem.class) ;
				menuList.add(mi);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mMenuMap = new HashMap<String, MenuItem>();
		for(MenuItem mi:menuList){
			String menuPower = MportalApplication.user.getMenuPower();
			if(mi.getProtectedFlag()==0||(!TextUtils.isEmpty(menuPower)&&menuPower.indexOf(mi.getId())!=-1)){
				mMenuMap.put(mi.getId(), mi);
			}
		}
	}

	
}
