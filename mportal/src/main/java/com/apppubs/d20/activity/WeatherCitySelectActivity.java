package com.apppubs.d20.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.apppubs.d20.widget.LetterListView;
import com.apppubs.d20.R;
import com.apppubs.d20.bean.City;
import com.orm.SugarRecord;

/**
 * 城市列表
 * 
 * 
 */
public class WeatherCitySelectActivity extends BaseActivity implements AMapLocationListener, Runnable {
	
	private MyAdapter mAdapter;
	private ListView mCityLv;
	private TextView overlay, myLocation;
	private LetterListView letterListView;
	private HashMap<String, Integer> alphaIndexer;// 存放存在的汉语拼音首字母和与之对应的列表位置
	private String[] sections;// 存放存在的汉语拼音首字母
	private Handler handler;
	private OverlayThread overlayThread;
	private List<City> mCityList;
	private ImageView mSearchBtn;
	private EditText et;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private boolean isDingwei = true;
	private String cityName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_city_list);
		setTitle("城市选择");
		init();
		
		// 定位到的城市
		// getLoadCity();
	}

	private void getLoadCity() {
		// TODO Auto-generated method stub
	}

	private void init() {
	
		// myLocation=(TextView) findViewById(R.id.weather_gps);
		// myLocation.setOnClickListener(this);
		mSearchBtn = (ImageView) findViewById(R.id.city_search_btn);
		et = (EditText) findViewById(R.id.et);
		overlay = (TextView) findViewById(R.id.overlay);
		mSearchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = et.getText().toString().trim();
				mCityList = getSelectCityNames(content);
				mAdapter.setData(mCityList);
				mAdapter.notifyDataSetChanged();
			}
		});

		mCityLv = (ListView) findViewById(R.id.city_list);
		letterListView = (LetterListView) findViewById(R.id.cityLetterListView);
		mCityList  = SugarRecord.listAll(City.class);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());
		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		// initOverlay();
		mAdapter = new MyAdapter(this, mCityList);
		mCityLv.setAdapter(mAdapter);
	}

	private List<City> getSelectCityNames(String con) {
		// 判断查询的内容是不是汉字
		Pattern p_str = Pattern.compile("[\\u4e00-\\u9fa5]+");
		Matcher m = p_str.matcher(con);
		if (m.find() && m.group(0).equals(con)) {
			mCityList = SugarRecord.find(City.class, "NAME like ?", new String[]{con+"%"},null,"NAME",null);
		} else {
			mCityList = SugarRecord.find(City.class, "NAME_INITIAL like ?", new String[]{con+"%"},null,"NAME",null);
		}
		return mCityList;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		// case R.id.weather_gps:
		// aMapLocManager = LocationManagerProxy.getInstance(this);
		// // /*
		// // * mAMapLocManager.setGpsEnable(false);//
		// // * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		// // * API定位采用GPS和网络混合定位方式
		// // * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		// // */
		// // aMapLocManager.requestLocationUpdates(
		// // LocationProviderProxy.AMapNetwork, 2000, 10, this);
		// // handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
		//
		// mTitleBar.setTitle(cityName);
		// // 保存用户配置信息（这里为用户选择的城市代号信息）
		// SharedPreferences userAreaInfo = getSharedPreferences(
		// WeatherActivity.WEATHRECITYNAMESF, Context.MODE_PRIVATE);
		// Editor editor = userAreaInfo.edit();
		// editor.putString(WeatherActivity.WEATHERCITYNAME, cityName);
		// editor.commit();
		// finish();
		// break;

		default:
			break;
		}
	}

	private class MyAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<City> list;

		public MyAdapter(Context context, List<City> list) {

			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				// 当前汉语拼音首字母
				// getAlpha(list.get(i));
				String currentStr = list.get(i).getNameFirstInitial();
				// 上一个汉语拼音首字母，如果不存在为“ ”
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).getNameFirstInitial() : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getNameFirstInitial();
					alphaIndexer.put(name, i);
					sections[i] = name;
				}
			}

		}

		public void setData(List<City> list){
			this.list = list;
		}
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_cityname_list, null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.name = (TextView) convertView.findViewById(R.id.name);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.name.setText(list.get(position).getName());
			String currentStr = list.get(position).getNameFirstInitial();
			String previewStr = (position - 1) >= 0 ? list.get(position - 1).getNameFirstInitial() : " ";
			
			holder.name.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					City cityModel = (City) mCityLv.getAdapter().getItem(position);
					String cityName = cityModel.getName();
					mTitleBar.setTitle(cityName);
					// 保存用户配置信息（这里为用户选择的城市代号信息）
					SharedPreferences userAreaInfo = getSharedPreferences(WeatherActivity.WEATHRECITYNAMESF, MODE_PRIVATE);
					Editor editor = userAreaInfo.edit();
					editor.putString(WeatherActivity.WEATHERCITYNAME, cityName);
					editor.commit();
					finish();
				}
			});
			
			if (!previewStr.equals(currentStr)) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(currentStr);
			} else {
				holder.alpha.setVisibility(View.GONE);
			}
			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView name;
		}

	}

	// 初始化汉语拼音首字母弹出提示框
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.overlay, null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class LetterListViewListener implements LetterListView.OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(final String s) {
			if (alphaIndexer.get(s) != null) {
				int position = alphaIndexer.get(s);
				mCityLv.setSelection(position);
				overlay.setText(sections[position]);
				overlay.setVisibility(View.VISIBLE);
				handler.removeCallbacks(overlayThread);
				// 延迟一秒后执行，让overlay为不可见
				handler.postDelayed(overlayThread, 1500);
			}
		}

	}

	// 设置overlay不可见
	private class OverlayThread implements Runnable {

		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
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
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")" + "\n精    度    :" + location.getAccuracy() + "米" + "\n定位方式:"
					+ location.getProvider() + "\n定位时间:" + convertToTime(location.getTime()) + "\n城市编码:" + cityCode + "\n位置描述:"
					+ desc + "\n省:" + location.getProvince() + "\n市:" + location.getCity() + "\n区(县):" + location.getDistrict()
					+ "\n区域编码:" + location.getAdCode());
			cityName = location.getCity();
			isDingwei = false;
			myLocation.setText("定位到的城市：" + cityName);
			stopLocation();
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			myLocation.setText("定位超时");
			stopLocation();// 销毁掉定位
		}
	}

	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}

}