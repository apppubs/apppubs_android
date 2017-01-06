package com.mportal.client.widget;
import java.util.Calendar;

import m.framework.utils.Utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mportal.client.R;
import com.mportal.client.util.SystemUtils;

public class DateTime extends ScrollView {
	private static final String TAG = null;
	private TableLayout mCalendarTl;
	private TextView showDateTxt;// 头部显示的年月---
	private ImageView btnpre;// 上月下月的选择按钮
	private ImageView btnnext;
	private int year;
	private int month; // 0,1,..,,11
	private int srcday; // 初始日
	private int srcyear; // 初始年
	private int srcmonth; // 初始月 1，2，3。。。12
	private String mclickday = "";
	private String[] weeks = {  "SUN", "MON", "TUE", "WED", "THU","FRI", "STA" };
	private View preSelectedView = null; // 前一个选中的日期
	private SharedPreferences dateclecksp;// 点击事件的存储
	private Context mContext;
	private DateCallBack callBack;
	private int weekTitleColor = Color.BLACK;
	private int dayColor = Color.BLACK;
	private int titleColor = Color.WHITE;
	private int selectedColor = Color.TRANSPARENT;
	private boolean init = false; // 初始化标志־
	private int colWidth = 30; // 单元格宽度
	private int rowHeight = 0; // 单元格高度
	private int textSize = 12;
	private LinearLayout dayLayOut;

	private Handler mHandler;
	public static View preDataV;

	/*
	 * 最新一期的时间 SharedPreferences datesp = context.getSharedPreferences("issue",
	 * 0); int year = Integer.parseInt(datesp.getString("time_nian", null), 10);
	 * int month = Integer.parseInt(datesp.getString("time_yue", null), 10); int
	 * ri = Integer.parseInt(datesp.getString("time_ri", null), 10);
	 */
	
	public DateTime(Context context, Handler handler) {
		
		super(context);
		Calendar c = Calendar.getInstance();
		this.srcyear = c.get(Calendar.YEAR); // 获取当前年份
		this.srcmonth = c.get(Calendar.MONTH)+1;// 获取当前月份 1,2,3....12月
		this.srcday = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
		dateclecksp = context.getSharedPreferences("date", 0);
		mHandler = handler;
		mContext = context;
		setLayoutParams(new LayoutParams(ScrollView.LayoutParams.MATCH_PARENT,
				ScrollView.LayoutParams.MATCH_PARENT));

		LinearLayout mainlayout = new LinearLayout(mContext);
		addView(mainlayout);

		mainlayout.setOrientation(LinearLayout.VERTICAL);
		mainlayout.setLayoutParams(new LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		// ͷ����ʾ
		LinearLayout titleLayOut = new LinearLayout(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, Utils.dipToPx(context, 50));
		params.setMargins(0, Utils.dipToPx(context, 15), 0, 0);
		titleLayOut.setLayoutParams(params);
		titleLayOut.setOrientation(LinearLayout.HORIZONTAL);
		titleLayOut.setGravity(Gravity.CENTER);
	//	titleLayOut.setBackgroundResource(R.drawable.chinadaily_belt);
		mainlayout.addView(titleLayOut);

		mCalendarTl = new TableLayout(mContext);
		mainlayout.addView(mCalendarTl);

		showDateTxt = new TextView(mContext);
		LinearLayout.LayoutParams la = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		la.rightMargin = 20;
		la.leftMargin = 20;
		showDateTxt.setLayoutParams(la);

		this.year = srcyear;
		if (srcmonth < 1 || srcmonth > 12)
			this.month = 0;
		else
			this.month = srcmonth - 1;
//		if (day1 < 1 || day1 > 31)
//			this.srcday = 1;
//		else
//			this.srcday = day1;

	    showDateTxt.setText(this.srcmonth + "  "+ String.valueOf(this.srcyear));
		showDateTxt.setTextColor(Color.WHITE);
		showDateTxt.setTextSize(20);
		showDateTxt.setTypeface(Typeface.DEFAULT_BOLD);
		showDateTxt.setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams la1 = new LinearLayout.LayoutParams(
				Utils.dipToPx(context, 50),LayoutParams.MATCH_PARENT);
		btnpre = new ImageView(mContext);
		btnpre.setLayoutParams(la1);
		//btnpre.setImageResource(R.drawable.selector_bpre_bg);
		int paddingPx = (int)Utils.dipToPx(context, 5);
		btnpre.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
		btnnext = new ImageView(mContext);
		btnnext.setLayoutParams(la1);
		//btnnext.setImageResource(R.drawable.selector_bnext_bg);
		btnnext.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);
		dayLayOut = new LinearLayout(mContext);
		dayLayOut.setOrientation(LinearLayout.VERTICAL);
		dayLayOut.setGravity(Gravity.CENTER_VERTICAL);
		dayLayOut.addView(showDateTxt);
		dayLayOut.setLayoutParams(new LayoutParams(380, 60));

		titleLayOut.addView(btnpre);
		titleLayOut.addView(dayLayOut);
		titleLayOut.addView(btnnext);

		btnnext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (preSelectedView != null)
					preSelectedView.setBackgroundColor(Color.TRANSPARENT);
				nextMonth();
				showDateTxt.setText((month + 1) + "  "
						+ String.valueOf(year));
				generateDate();
				loadDate(1,7);//从第二行开始，第一行是星期显示
			}
		});
		btnpre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				preMonth();
				showDateTxt.setText(month + 1+ "  "
						+ String.valueOf(year));
				generateDate();
				loadDate(1,7);
			}
		});
		
		
		this.setVerticalScrollBarEnabled(false);//隐藏滚动条
		//this.setBackgroundResource(R.drawable.chinadaily_left_menu);
		this.setPadding(0, 10, 0, 0);
		this.setWidthHeightTextSize(70,70, 20); // 设置单元格宽度、高度和字体大小
		this.init(); // 设置标题、星期、日期、选中日期的颜

	}
	public DateTime(Context context) {
		super(context);
	}
	/**
	 * 初始化日期
	 * 
	 * @param titleCoclor
	 *            标题颜色
	 * @param weekTitleColor
	 *            星期颜色
	 * @param dayColor
	 *            日期颜色
	 */

	public void init() {
		if (!init) {
			showDateTxt.setTextColor(Color.WHITE);
			LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mCalendarTl.getLayoutParams();
			lp.bottomMargin = Utils.dipToPx(mContext, 20);
			generateDate();
		}

	}
	

	/**
	 * 以"周日","周一","周二","周三","周四","周五","周六"为顺序
	 * 
	 * @param weekdays
	 */
	public void setWeekTitle(String[] weekdays) {
		if (weekdays != null && weekdays.length > 0 && weekdays.length == 7)
			this.weeks = weekdays;
	}

	public int maxDay() {
		Calendar time = Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR, year);
		time.set(Calendar.MONTH, month);
		int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);// ���·ݵ�����
		return day;

	}

	// 今天的星期
	public int nowWeekDay() {
		Calendar time = Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR, year);
		time.set(Calendar.MONTH, month);
		time.set(Calendar.DATE, srcday);
		int weekday = time.get(Calendar.DAY_OF_WEEK);
        
		return weekday -1;
	}
    //一号的星期
	public int fristWeekDay() {
		Calendar time = Calendar.getInstance();
		time.clear();
		time.set(Calendar.YEAR, year);
		time.set(Calendar.MONTH, month);
		time.set(Calendar.DATE, 1);
		int weekday = time.get(Calendar.DAY_OF_WEEK);
        
		return weekday-1;
	}
	// 星期的显示
	public void generateDate() {
		
		mCalendarTl.removeAllViews();
		
		
		TableRow weekRow = new TableRow(mContext);
		weekRow.setPadding(0, 2, 0, 0);
		weekRow.setGravity(Gravity.CENTER_HORIZONTAL);
		weekRow.setBackgroundResource(R.color.dialog_bg);
		for (int i = 0; i < 7; i++) {
			TextView col1 = new TextView(mContext);
			col1.setTextSize(16);
			col1.setMinWidth(colWidth);
			col1.setMaxWidth(colWidth);
			if (rowHeight > 0)
				col1.setMinHeight(rowHeight);
			col1.setTextColor(weekTitleColor);
			col1.setText(weeks[i]);
			col1.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			weekRow.addView(col1); // 添加列
		}
		mCalendarTl.addView(weekRow);
		TableLayout.LayoutParams params = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.WRAP_CONTENT,
				TableLayout.LayoutParams.WRAP_CONTENT);

		//获得下载过的列表
		//String downloadlistnames = getDownlodenames("CD");
		String thisdatestr = "";
		String thisyearmonth = "";
		//thisyearmonth = Tools.getYearMonth(showDateTxt.getText().toString());

		int weekday = fristWeekDay();//是对的
		int maxday = maxDay();
		int count = 0;
		int crossWeeks = getWeekNumOfMonth(weekday,maxday);
		// 下方日期的显示
		for (int i = 1; i <crossWeeks+1; i++) { // 添加6行......................................
			TableRow row = new TableRow(mContext);
			row.setPadding(4, 2, 4, 2);
			row.setGravity(Gravity.CENTER_HORIZONTAL);
			row.setLayoutParams(params);
			mclickday = dateclecksp.getString("clickday", "");// 判断点击
			for (int j = 0; j < 7; j++) { // 添加1列..................................
				TextView col = new TextView(mContext);
				col.setTextColor(dayColor);
				col.setBackgroundColor(Color.TRANSPARENT);
				col.setTextSize(textSize);
				String Dtime = showDateTxt.getText().toString();
				
				if (rowHeight > 0)
					col.setMinHeight(rowHeight);
				if (i == 1) {//weekday==2
					if (weekday <= j) {
						count++;
						if (count < 10) {
							thisdatestr = thisyearmonth + "/0" + count;
						} else {
							thisdatestr = thisyearmonth + "/" + count;
						}
						col.setText(String.valueOf(count));// 初始化时的日期
						//Tools.setEverybackground(mContext,downloadlistnames , thisdatestr, col, mclickday);//设置所有情况下的背景
					}
				} else {
					if (count < maxday) {
						count++;
						if (count < 10) {
							thisdatestr = thisyearmonth + "/0" + count;
						} else {
							thisdatestr = thisyearmonth + "/" + count;
						}
						col.setText(String.valueOf(count));// 初始化时的日期
						//Tools.setEverybackground(mContext,downloadlistnames , thisdatestr, col, mclickday);//设置所有情况下的背景
					} else {
						count++;
						col.setText("");
						col.setBackgroundColor(Color.TRANSPARENT);
					}
				}

				col.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String coltext = ((TextView) v).getText().toString();
						// 可选择的显示的日期
						String Dtime = showDateTxt.getText().toString();
						// 记录点击，设置背景微透明，不可删
						if (preDataV != null) {
							preDataV.setBackgroundColor(Color.TRANSPARENT);
						}
						if (preSelectedView != null) {
							preSelectedView
									.setBackgroundColor(Color.TRANSPARENT);
						}
						
						boolean bo = false;

						if (!coltext.equals("")) { // 超过当前日期点击无效果
							//bo = Tools.isMoretoday(Dtime, coltext);
						}
						if (coltext.equals("") || bo
								|| SystemUtils.isFastDoubleClick()) {// 当点击空的日期时，直接返回
							return;
						}
						Message msg = Message.obtain(mHandler);
						msg.what = 1;
						msg.obj = getIssueName((TextView) v);
						msg.sendToTarget(); 
						if (((TextView) v).getText().toString().length() > 0) {
							preSelectedView = v;
							// 记录点击，设置背景微透明，也不可删
							if (preDataV != null) {
								preDataV.setBackgroundColor(Color.TRANSPARENT);
							}
							// 设置点击，背景色为蓝色
							//v.setBackgroundColor(getResources().getColor(R.color.date_choice));
                            
							// 处理Dtime格式,记录点击存储
//							mclickday = Utils.getFormatData(Dtime, ((TextView) v).getText().toString());
							dateclecksp.edit().putString("clickday", mclickday)
									.commit();
							// callBack.execute(v, year + "", (month + 1) + "",
							// ((TextView) v).getText().toString());
						}
					}
				});

				col.setGravity(Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL);
				row.addView(col); // 添加列
			}
			mCalendarTl.addView(row);// 添加行
		}
	}

	public void nextMonth() {
		if (month == 11) {
			year++;
			month = 0;
		} else {
			month++;
		}
	}
	/**
	 * 
	 * @param WeekdayOfFirstDay本月第一天是周几
	 * @param daysOfMonth 本月天数
	 * @return 本月跨越周数 
	 */
	private int getWeekNumOfMonth(int WeekdayOfFirstDay,int daysOfMonth){
		int weekNum = 1;//跨越周数
		int leftDays = daysOfMonth-(7-WeekdayOfFirstDay);//本月除去第一周剩余的天数
		if(leftDays%7==0){
			weekNum += leftDays/7;
		}else{
			weekNum +=leftDays/7+1;
		}
		
		return weekNum;
	}
	public void preMonth() {
		if (month == 0) {
			year--;
			month = 11;
		} else {
			month--;
		}
	}

	// 1 7
	private void loadDate(int startIndex, int endIndex) {
		int weekday = fristWeekDay();// 星期几
		int maxday = maxDay();// 一个月的最大天数
		int count = 0;

		//String downloadlistnames = getDownlodenames("CD");
		String thisdatestr = "";
		String thisyearmonth = "";
		//thisyearmonth = Tools.getYearMonth(showDateTxt.getText().toString());

		int crossWeeks = getWeekNumOfMonth(weekday,maxday);
		for (int i = startIndex; i < crossWeeks; i++) {
			TableRow row = (TableRow) mCalendarTl.getChildAt(i);
			for (int j = 0; j < 7; j++) {

				TextView col = (TextView) row.getChildAt(j);
				col.setBackgroundColor(Color.TRANSPARENT);
				mclickday = dateclecksp.getString("clickday", "");// 判断点击
				if (i == startIndex) {//第一行显示  
                       
					if (weekday <= j) {
						count++;

						if (count < 10) {
							thisdatestr = thisyearmonth + "/0" + count;
						} else {
							thisdatestr = thisyearmonth + "/" + count;
						}
						col.setMinWidth(colWidth);
						col.setMaxWidth(colWidth);
						col.setText(String.valueOf(count));
						//Tools.setEverybackground(mContext,downloadlistnames , thisdatestr, col, mclickday);//设置所有情况下的背景

					} else {
						col.setText("");
					}
				} else {
					if (count < maxday) {
						count++;
						if (count < 10) {
							thisdatestr = thisyearmonth + "/0" + count;
						} else {
							thisdatestr = thisyearmonth + "/" + count;
						}
						col.setText(String.valueOf(count));
						//Tools.setEverybackground(mContext,downloadlistnames , thisdatestr, col, mclickday);//设置所有情况下的背景
					} else {
						col.setText("");
					}
				}

			}
		}
	}

	/**
	 * 回调函数
	 * 
	 * @author Acer
	 * 
	 */
	public interface DateCallBack {
		public void execute(View v, String year, String month, String day);

	}

	public void setCallBack(DateCallBack callBack) {
		this.callBack = callBack;
	}

	/**
	 * 设置单元格的宽度，高度
	 * 
	 * @param colWidth
	 *            单元格宽度
	 * @param rowHeight
	 *            单元格高度
	 * @param textSize1
	 *            文字大小
	 */
	public void setWidthHeightTextSize(int colWidth, int rowHeight,
			int textSize1) {
		if (colWidth > 0)
			this.colWidth = colWidth;
		this.rowHeight = rowHeight;
		this.textSize = textSize1;
	}

	/**
	 * 获得下载过的列表 2014/09/25
	 */
//	public String getDownlodenames(String paptercode) {
//		PaperBussiness biness = PaperBussinessImpl.getInstance(mContext);
//		String[] names = biness.getCacheedIssueName(paptercode);
//		String downloadqilist = "";
//		if (names.length != 0) {
//			for (int i = 0; i < names.length; i++) {
//				downloadqilist = downloadqilist + names[i] + ",";
//			}
//		}
//		return downloadqilist;
//	}
    //获得下载过的期的名字列表
	private String getIssueName(TextView tv) {

		String monthS = month + 1 > 9 ? (month + 1) + "" : "0" + (month + 1);
		int dateTemp = 0;
//		Html.toHtml(contentText).toString()
		SpannableString contentText = new SpannableString(tv.getText());
		String dayS = (dateTemp = Integer.parseInt(tv.getText().toString().trim())) > 9 ? dateTemp
				+ ""
				: "0" + dateTemp;
		return year + "/" + monthS + "/" + dayS;
	}

}
