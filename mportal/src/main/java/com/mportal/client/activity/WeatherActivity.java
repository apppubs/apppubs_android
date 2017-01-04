package com.mportal.client.activity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mportal.client.R;
import com.mportal.client.bean.Weather;
import com.mportal.client.constant.Actions;
import com.mportal.client.fragment.BaseFragment;
import com.mportal.client.util.StringUtils;
import com.mportal.client.util.SystemUtils;
import com.mportal.client.util.Tools;
import com.mportal.client.util.WeatherUtils;
import com.mportal.client.view.AlertDialog;
import com.mportal.client.view.AlertDialog.OnOkClickListener;
import com.mportal.client.view.TitleBar;

/**
 * 天气
 * 
 */
public class WeatherActivity extends BaseActivity {

	public static final String ACTION_REFRESH = "jason.broadcast.action.WeatherActivity";

	private ArrayList<Weather> mWeatherInfos;
	private String cityName;
	private ImageView tomorrow_image, houtian_iamge, dahoutian_iamge;
	private ImageView mGifView;
	private LinearLayout progress;
	private TitleBar mTitle;
	private TextView today_time;
	private TextView today_temp, today_info;
	private TextView tomorrow_time, tomorrow_temp, tomorrow_weather;
	private TextView houtian_time, houtian_temp, houtian_weather;
	private TextView dahoutian_time, dahoutian_temp, dahoutian_weather;
	public static String WEATHRECITYNAMESF = "cityNameWeater";
	public static String WEATHERCITYNAME = "weacityName";
	public static String WEATHERSAVE = "weacitySaveList";// 保存的天气信息
	private ProgressDialog mProgressDialog = null;
	public boolean isHaveWeather = true;// 是否存有天气信息
	public static String WEATHERTEMP = "today_temp";
	public static String WEATHERPIC = "weatherpic";
	public static String WEATHE = "weatherlist";
	private SharedPreferences getShare;
	private int i;// 标志是否第一次进来；
	private String weatherCount;// 保存的天气信息
	private HomeBaseActivity activity;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progress.setVisibility(View.GONE);
			switch (msg.what) {
			case 0:
				if (mWeatherInfos.size() > 0) {
					mTitle.setTitle(mWeatherInfos.get(1).getCityName());
					setTodayWearther();
					setTomorrowWearther();
					setHoutianWearther();
					setDahoutianWearther();
					activity.refreshWether(WeatherActivity.this,mWeatherInfos);
					
				} else {
					Dialog d = new AlertDialog(WeatherActivity.this, new OnOkClickListener() {

						@Override
						public void onclick() {

						}
					}, "数据异常", "没有此处的天气信息!", "确定");
					d.setCancelable(false);
					d.setCanceledOnTouchOutside(false);
					d.show();
				}
				break;
			case 1:
				setTodayWearther();
				setTomorrowWearther();
				setHoutianWearther();
				setDahoutianWearther();
				break;
			default:
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setNeedTitleBar(false);
		setContentView(R.layout.act_weather);
		init();
		// mWeatherInfos=MportalApplication.weathers;
		// if (mWeatherInfos.size()>0&&i==0) {
		// isHaveWeather=false;
		// System.out.println("查询保存的天气信息"+MportalApplication.weathers.get(0).toString());
		// i++;
		// setTodayWearther();
		// setTomorrowWearther();
		// setHoutianWearther();
		// setDahoutianWearther();
		// }
	}

	@Override
	protected void onResume() {
		super.onResume();
		cityName = getShare.getString(WEATHERCITYNAME, "");
		if (cityName.equals("")) {
			cityName = "北京";
		}
		// 读取用户配置的城市信息


		// 能否联网
		boolean bo = SystemUtils.canConnectNet(getApplication());
		if (bo) {
			progress.setVisibility(View.VISIBLE);
			new Thread() {
				@Override
				public void run() {
					super.run();
					mWeatherInfos = Tools.getWeatherList(WeatherActivity.this, cityName);

					handler.sendEmptyMessage(0);
				}
			}.start();

		} else {
			Toast.makeText(getApplication(), getResources().getString(R.string.network_faile), 2000).show();
		}
	}

	private void turnWeatrher(String content) {
		// TODO Auto-generated method stub
		try {
			if (content.indexOf("results") > 0) {
				JSONObject jo = new JSONObject(content);
				JSONArray array = jo.getJSONArray("results");
				for (int j = 0; j < array.length(); j++) {
					JSONObject jo1 = array.getJSONObject(j);
					String cityName = jo1.getString("currentCity");
					JSONArray array2 = jo1.getJSONArray("weather_data");
					for (int i = 0; i < array2.length(); i++) {
						Weather info = new Weather();
						JSONObject jo2 = array2.getJSONObject(i);
						info.setCityName(cityName);
						info.setData(jo2.getString("date"));
						info.setWeather(jo2.getString("weather"));
						info.setWind(jo2.getString("wind"));
						info.setTemp(jo2.getString("temperature"));
						mWeatherInfos.add(info);
					}
				}
				handler.sendEmptyMessage(1);
			}

		} catch (Exception e) {
			System.out.println("天气错误" + e.getMessage());
			e.printStackTrace();
		}
	}

	private void init() {
		activity=new HomeBaseActivity() {
			
			@Override
			protected void changeContent(BaseFragment fragment) {
			}

			@Override
			protected void setUnreadNumForMenu(String menuId, int num) {
				// TODO Auto-generated method stub
				
			}
		};
		getShare = getSharedPreferences(WEATHRECITYNAMESF, Context.MODE_PRIVATE);
		progress = (LinearLayout) findViewById(R.id.weather_progress_ll);
		mGifView = (ImageView) findViewById(R.id.weather_today_image);
		mTitle = (TitleBar) findViewById(R.id.weather_wb);
		today_time = (TextView) findViewById(R.id.weather_today_time);
		tomorrow_image = (ImageView) findViewById(R.id.weather_tomorrow_image);
		houtian_iamge = (ImageView) findViewById(R.id.weather_houtian_image);
		dahoutian_iamge = (ImageView) findViewById(R.id.weather_dahoutian_image);
		today_temp = (TextView) findViewById(R.id.weather_today_temp);
		today_info = (TextView) findViewById(R.id.weather_today_info);
		tomorrow_time = (TextView) findViewById(R.id.weather_tomorrow_time);
		tomorrow_temp = (TextView) findViewById(R.id.weather_tomorrow_temp);
		tomorrow_weather = (TextView) findViewById(R.id.weather_tomorrow_weather);
		houtian_time = (TextView) findViewById(R.id.weather_houtian_time);
		houtian_temp = (TextView) findViewById(R.id.weather_houtian_temp);
		houtian_weather = (TextView) findViewById(R.id.weather_houtian_weather);
		dahoutian_time = (TextView) findViewById(R.id.weather_dahoutian_time);
		dahoutian_temp = (TextView) findViewById(R.id.weather_dahoutian_temp);
		dahoutian_weather = (TextView) findViewById(R.id.weather_dahoutian_weather);
		// int gifWidth = getResources().getDimensionPixelSize(
		// R.dimen.weather_gif_width);
		// int gifHeight = getResources().getDimensionPixelSize(
		// R.dimen.weather_gif_height);
		// mGifView.setShowDimension(gifWidth, gifHeight);
		// gif.setGifImage(R.drawable.biz_pc_plugin_weather_null);
		// 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
		// mGifView.setGifImageType(GifImageType.COVER);

		mTitle.setLeftBtnClickListener(this);
		mTitle.setRightBtnClickListener(this);
		// 设置加载方式：先加载后显示、边加载边显示、只显示第一帧再显示
		// mGifView.setGifImageType(GifImageType.COVER);
	}

	// 在dismiss 之前调用gifView.ClearAnimation（）就没问题
	public void setTodayWearther() {
		Weather w = mWeatherInfos.get(0);
		
		Intent intent = new Intent();
		intent.setAction(Actions.REFRESH_WEATHER);
		sendBroadcast(intent);
		int i = WeatherUtils.solvedWeather(w.getWeather());
		// shishitianqi.setText(str.getData().substring(
		// str.getData().indexOf("(") + 1, str.getData().indexOf(")")));
		// mGifView.setGifImage(i);
		mGifView.setImageResource(i);
		if (w.getData().contains("(")) {
			today_time
					.setText(StringUtils.getWeek(w.getData()) + "  " + w.getData().substring(2, w.getData().indexOf("(")));
		} else {
			today_time.setText(StringUtils.getWeek(w.getData()));
		}

		today_temp.setText(w.getTemp() + "  " + StringUtils.getNowTemp(w.getData()));
		today_info.setText(w.getWeather() + "  " + w.getWind());

		Editor editor = getShare.edit();
		editor.putString(WeatherActivity.WEATHERCITYNAME, cityName);
		editor.putString(WeatherActivity.WEATHERTEMP, StringUtils.getTemp(w.getTemp()));
		editor.putString(WeatherActivity.WEATHERPIC, w.getWeather());
		editor.commit();
	}

	public void setTomorrowWearther() {
		Weather str = mWeatherInfos.get(1);
		tomorrow_image.setImageResource(WeatherUtils.solvedWeather(str.getWeather()));
		tomorrow_time.setText(StringUtils.getWeek(str.getData()));
		tomorrow_temp.setText(str.getTemp());
		tomorrow_weather.setText(str.getWeather() + "  " + str.getWind());
	}

	public void setHoutianWearther() {
		Weather str = mWeatherInfos.get(2);
		houtian_iamge.setImageResource(WeatherUtils.solvedWeather(str.getWeather()));
		houtian_time.setText(StringUtils.getWeek(str.getData()));
		houtian_temp.setText(str.getTemp());
		houtian_weather.setText(str.getWeather() + "  " + str.getWind());
	}

	public void setDahoutianWearther() {
		Weather str = mWeatherInfos.get(3);
		dahoutian_iamge.setImageResource(WeatherUtils.solvedWeather(str.getWeather()));
		dahoutian_time.setText(StringUtils.getWeek(str.getData()));
		dahoutian_temp.setText(str.getTemp());
		dahoutian_weather.setText(str.getWeather() + "  " + str.getWind());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.titlebar_right_btn:
			Intent it = new Intent(WeatherActivity.this, WeatherCitySelectActivity.class);
			it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(it);

			break;
		default:
			break;
		}
	}

}
