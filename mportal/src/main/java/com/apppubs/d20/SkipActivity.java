package com.apppubs.d20;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.apppubs.d20.activity.NewsInfoBaseActivity;
import com.apppubs.d20.constant.Constants;

public class SkipActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String uri =  getIntent().getData().toString();
		String reg = "apppubsnews:\\/\\/[^,]*,[^,]*($|,[^,]*)";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(uri);
		if(matcher.matches()){
			String params = uri.replace(Constants.CUSTOM_SCHEMA_APPPUBS_NEWS+"://", "");
			String[] paramsArr = params.split(",");
			if(paramsArr.length>2){
				NewsInfoBaseActivity.startInfoActivity(this, paramsArr[0], paramsArr[1],paramsArr[2]);
			}else{
				NewsInfoBaseActivity.startInfoActivity(this, paramsArr[0], paramsArr[1]);
			}
			
			finish();
		}else{
			Toast.makeText(this, "自定义协议格式错误", Toast.LENGTH_LONG).show();
		}
		
		
				
	}
}
