package com.apppubs.ui.widget.menudialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.apppubs.d20.R;

import java.util.Arrays;

/**
 * Created by zhangwen on 2017/7/22.
 */

public class MenuDialog extends Dialog {

	private String[] mMenus;
	private MenuDialogListener mListener;
	private ListView mLv;

	public interface MenuDialogListener {
		void onItemClicked(int index);
	}

	public MenuDialog(@NonNull Context context,String[] menus, MenuDialogListener listener) {
		super(context, R.style.dialog);
		mListener = listener;
		mMenus = menus;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_menu);
		initViews();
	}

	private void initViews() {
		mLv = (ListView) findViewById(R.id.dialog_menu);

		MenuDialogAdapter adapter = new MenuDialogAdapter(getContext(),Arrays.asList(mMenus));
		mLv.setAdapter(adapter);

		mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mListener.onItemClicked(position);
				dismiss();
			}
		});
	}

}
