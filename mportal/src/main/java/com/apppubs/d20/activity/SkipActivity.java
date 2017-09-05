package com.apppubs.d20.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.apppubs.d20.start.StartUpActivity;

public class SkipActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(this,StartUpActivity.class);
		startActivity(intent);
		finish();
	}
	
}
