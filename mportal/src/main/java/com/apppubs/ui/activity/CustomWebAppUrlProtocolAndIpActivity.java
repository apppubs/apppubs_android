package com.apppubs.ui.activity;
/**
 * 自定义webapp的ip地址与协议
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.bean.TMenuItem;
import com.apppubs.d20.R;
import com.apppubs.util.FileUtils;
import com.orm.SugarRecord;

public class CustomWebAppUrlProtocolAndIpActivity extends BaseActivity implements OnFocusChangeListener{

	public static final String CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME = "custom_web_app_url_map";
	
	private LinearLayout mLinearLayout;
	private Map<String,String> mCustomIpMap;
	private EditText mCurFocusedEditText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Uri u = Uri.parse("sdicoa://");
//		Intent i = new Intent(Intent.ACTION_VIEW , u);
//		startActivity(i);
		setContentView(R.layout.act_custom_web_app_url_ip);
		mLinearLayout = (LinearLayout) findViewById(R.id.item_container_ll);
		mCustomIpMap = (Map<String, String>) FileUtils.readObj(mApp, CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
		if(mCustomIpMap==null){
			mCustomIpMap = new HashMap<String, String>();
		}
		setTitle("服务地址配置");
		List<TMenuItem> list = SugarRecord.find(TMenuItem.class, "is_allow_custom_ip = ?", TMenuItem.YES+"");
		for(TMenuItem mi: list){
			View item = getLayoutInflater().inflate(R.layout.item_custom_webapp_url_ip_listview, null);
			TextView menuNameItem = (TextView) item.findViewById(R.id.item_menu_name_tv);
			menuNameItem.setText(mi.getName());
			
			EditText customIpEt = (EditText) item.findViewById(R.id.item_menu_custom_ip_et);
			String text = mCustomIpMap.get(mi.getId());
			if(!TextUtils.isEmpty(text)){
				customIpEt.setText(text);
			}
			customIpEt.setOnFocusChangeListener(CustomWebAppUrlProtocolAndIpActivity.this);
		
			customIpEt.setTag(mi.getId());
			mLinearLayout.addView(item);
			
		}
	}
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(!hasFocus){
			mCustomIpMap.put(v.getTag().toString(), ((EditText)v).getText().toString());
			FileUtils.writeObj(mApp, mCustomIpMap, CUSTOM_WEB_APP_URL_SERIALIZED_FILE_NAME);
		}else{
			mCurFocusedEditText = (EditText) v;
		}
		System.out.println("焦点改变："+hasFocus);
	}
	@Override
	protected void onStop() {
		super.onStop();
		if(mCurFocusedEditText!=null){
			mCurFocusedEditText.clearFocus();
		}
	}
}
