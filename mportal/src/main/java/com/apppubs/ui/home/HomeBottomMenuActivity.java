package com.apppubs.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.constant.Actions;
import com.apppubs.presenter.HomeBottomPresenter;
import com.apppubs.presenter.HomePresenter;
import com.apppubs.ui.activity.LoginActivity;
import com.apppubs.ui.activity.ViewCourier;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.util.LogM;
import com.apppubs.ui.widget.MenuBar;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.model.Conversation;

/**
 * 区别于左右菜单的主界面此主界面为底部菜单类似zaker
 * <p>
 * Copyright (c) heaven Inc.
 * <p>
 * Original Author: zhangwen
 * <p>
 * ChangeLog:
 * 2015年1月15日 by zhangwen create
 */
public class HomeBottomMenuActivity extends HomeBaseActivity implements IHomeBottomMenuView {

    private MenuBar mMenuBar;
    private int mCurPos;
    private int mIntentCurPos;
    private int mMenuBarBtnDefaultColor;
    private BroadcastReceiver mLogoutBr;
    private IUnReadMessageObserver mMessageUnReadCountObserver = new IUnReadMessageObserver() {

        @Override
        public void onCountChanged(int i) {

            mAppContext.getApp().setmMessageUnreadNum(i);
            setMessageUnreadNum();
        }
    };

    private HomeBottomPresenter mPresenter;

    @Override
    protected void onCreate(Bundle arg0) {
        LogM.log(this.getClass(), " HomeBottomMenuActivity onCreate");
        super.onCreate(arg0);
        setContentView(R.layout.act_home_bottommenu);
        initComponent();

        mLogoutBr = new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                onLogout();
            }

            private void onLogout() {
                selectMenu(0);
            }
        };
        registerReceiver(mLogoutBr, new IntentFilter(Actions.ACTION_LOGOUT));

        RongIM.getInstance().addUnReadMessageCountChangedObserver(mMessageUnReadCountObserver, Conversation
                .ConversationType.DISCUSSION, Conversation.ConversationType.PRIVATE);

        mPresenter = new HomeBottomPresenter(this, this);
        mPresenter.onViewCreated();
    }

    @Override
    protected HomePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        LogM.log(this.getClass(), "onSaveInstanceState-->调用");
    }

    private void setMessageUnreadNum() {
        String messageMenuId = null;
        for (TMenuItem mi : mPrimaryMenuList) {
            if (mi.getUrl() != null && mi.getUrl().startsWith("apppubs://message")) {
                messageMenuId = mi.getId();
                break;
            }
        }
        if (messageMenuId != null) {
            mMenuBar.setUnreadNumForMenu(messageMenuId, mAppContext.getApp().getmMessageUnreadNum());
        }
    }

    private void initComponent() {
        mMenuBarBtnDefaultColor = getResources().getColor(R.color.menubar_default);
        mMenuBar = (MenuBar) findViewById(R.id.home_bottom_menubar);
    }

    /**
     * 选择某个菜单
     *
     * @param position
     */
    private void selectMenu(int position) {
        mIntentCurPos = position;
        TMenuItem mi = mPrimaryMenuList.get(position);
        if (ViewCourier.openLoginViewIfNeeded(mi.getUrl(), this)) {
            return;
        }
//		TMenuItem mi = miArr[position];
        mPresenter.execute(mi.getUrl());

        ImageView ivC = (ImageView) mMenuBar.getChildAt(mCurPos).findViewById(R.id.menu_buttom_iv);
        ivC.setColorFilter(mMenuBarBtnDefaultColor, Mode.SRC_ATOP);
        TextView tvC = (TextView) mMenuBar.getChildAt(mCurPos).findViewById(R.id.menu_bottom_tv);
        tvC.setTextColor(Color.parseColor("#8c8c8c"));

        ImageView iv = (ImageView) mMenuBar.getChildAt(position).findViewById(R.id.menu_buttom_iv);
        iv.setColorFilter(mThemeColor, Mode.SRC_ATOP);
        TextView tv = (TextView) mMenuBar.getChildAt(position).findViewById(R.id.menu_bottom_tv);
        tv.setTextColor(mThemeColor);

        mCurPos = position;
    }

    private Fragment mCurFrg;

    @Override
    public void changeContent(BaseFragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null || !fragments.contains(fragment)) {
            transaction.add(R.id.home_bottom_container_fgm, fragment);
        }
        if (mCurFrg != null) {
            transaction.hide(mCurFrg);
        }
        transaction.show(fragment);
        mCurFrg = fragment;
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        transaction.commitAllowingStateLoss();
    }


    @Override
    protected void setUnreadNumForMenu(String menuId, int num) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mLogoutBr != null) {
            unregisterReceiver(mLogoutBr);
        }
        LogM.log(this.getClass(), "销毁HomeBottomMenuActivity");

        RongIM.getInstance().removeUnReadMessageCountChangedObserver(mMessageUnReadCountObserver);
    }

    @Override
    protected void selectTab(int index) {
        selectMenu(index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == LoginActivity.REQUEST_CODE && resultCode == RESULT_OK) {
            selectMenu(mIntentCurPos);
        }
    }

    @Override
    public void setMenus(List<TMenuItem> menus) {
        mPrimaryMenuList = menus;
        if (menus != null && menus.size() > 0) {//判断是否有菜单如果没有提示用户配置菜单
            initMenuBar(menus);
            selectMenu(mCurPos);
        } else {
            Toast.makeText(this, "请配置菜单", Toast.LENGTH_LONG).show();
        }
    }

    private void initMenuBar(List<TMenuItem> menus) {
        int size = menus.size();

        mMenuBar.removeAllMenu();
        for (int i = -1; ++i < size; ) {
            mMenuBar.addMenuItem(menus.get(i));
        }
        mMenuBar.setOnItemClickListener(new MenuBar.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectMenu(position);
            }
        });
        mMenuBar.setVisibility(size == 1 ? View.GONE : View.VISIBLE);
    }
}
