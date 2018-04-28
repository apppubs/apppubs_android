package com.apppubs.ui.fragment;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.apppubs.bean.TMenuItem;
import com.apppubs.ui.home.HomeBaseActivity;
import com.apppubs.bean.TTitleMenu;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.ui.activity.ViewCourier;
import com.orm.SugarRecord;

/**
 * 有标题栏左右菜单的fragment
 * @author zhangwen
 *
 */
public class TitleMenuFragment extends BaseFragment{

	public static final String ARGS_MENU_ID = "args_menu_id";
	
	protected String mMenuId;
	protected List<TMenuItem> mTitleMenuLeftList;//左边菜单
	protected List<TMenuItem> mTitleMenuRightList;//右边菜单
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}
	
	@Override
	public void changeActivityTitleView(TitleBar titleBar) {
		
		super.changeActivityTitleView(titleBar);
		if(titleBar==null){
			LogM.log(this.getClass(), "titleBar 为空");
			return;
		}
		Bundle args = getArguments();
		
		if(args!=null&&(mMenuId=args.getString(ARGS_MENU_ID))!=null){
			mTitleMenuLeftList = SugarRecord.findWithQuery(TMenuItem.class, "select t1.id,t1.name,t1.url,t1.iconpic from MENU_ITEM t1 join TITLE_MENU t2 on t1.id = t2.menu_id where t2.type = ? and t2.super_menu_id = ?", TTitleMenu.TYPE_LEFT+"",mMenuId);
			mTitleMenuRightList = SugarRecord.findWithQuery(TMenuItem.class, "select t1.id,t1.name,t1.url,t1.iconpic from MENU_ITEM t1 join TITLE_MENU t2 on t1.id = t2.menu_id where t2.type = ? and t2.super_menu_id = ?", TTitleMenu.TYPE_RIGHT+"",mMenuId);
		}
		
		if(mTitleMenuLeftList!=null&&mTitleMenuLeftList.size()>0){
			titleBar.addLeftBtnWithImageUrlAndClickListener(mTitleMenuLeftList.get(0).getIconpic(),mTitleMenuLeftList.get(0).getId(), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					LogM.log(this.getClass(), "点击左边菜单");
//					MenuViewCourier.openMenu(v.getTag().toString(), mHostActivity);
					ViewCourier.getInstance(mHostActivity).executeInHomeActivity(v.getTag().toString(), (HomeBaseActivity)mHostActivity);
				}
			});
		}
		
		if(mTitleMenuRightList!=null&&mTitleMenuRightList.size()>0){
			titleBar.addRightBtnWithImageUrlAndClickListener(mTitleMenuRightList.get(0).getIconpic(),mTitleMenuRightList.get(0).getId(), new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					LogM.log(this.getClass(), "点击右边菜单");
//					MenuViewCourier.openMenu(v.getTag().toString(), mHostActivity);
					ViewCourier.getInstance(mHostActivity).executeInHomeActivity(v.getTag().toString(), (HomeBaseActivity)mHostActivity);
				}
			});
		}
		

		
		
	}
	
	
}
