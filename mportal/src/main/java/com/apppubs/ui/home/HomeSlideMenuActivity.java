package com.apppubs.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.apppubs.AppContext;
import com.apppubs.bean.TMenuItem;
import com.apppubs.bean.UserInfo;
import com.apppubs.constant.Constants;
import com.apppubs.d20.R;
import com.apppubs.presenter.HomePresenter;
import com.apppubs.presenter.HomeSlidePresenter;
import com.apppubs.ui.activity.SplashActivity;
import com.apppubs.ui.fragment.BaseFragment;
import com.apppubs.util.LogM;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 主界面
 */
public class HomeSlideMenuActivity extends HomeBaseActivity implements OnItemClickListener, IHomeSlideMenuView {

    private SlidingActivityHelper mHelper;
    private SlidingMenu mSlidingMenu;
    private ListView mLeftMenuLv;// 左边菜单
    private GridView mRightMenuGv;// 右边菜单
    private MenuLeftAdapter mLeftMenuA;
    private TextView mUsername;

    private HomeSlidePresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("HomeActivity", "主界面onCreate");
        mHelper = new SlidingActivityHelper(this);
        mHelper.onCreate(savedInstanceState);
        setContentView(R.layout.act_home_slidemenu);
        init();
        mPresenter = new HomeSlidePresenter(this, this);
        mPresenter.onViewCreated();
    }

    @Override
    protected HomePresenter getPresenter() {
        return mPresenter;
    }

    /**
     * 初始化组建
     */
    private void init() {
        mSlidingMenu = getSlidingMenu();
        // mSlidingMenu.setMenu(R.layout.menu_left);
        setBehindContentView(R.layout.menu_left);
        mSlidingMenu.setSecondaryMenu(R.layout.menu_right);
        mSlidingMenu.setBehindWidthRes(R.dimen.slidingmenu_behind_width);// 设置菜单宽度
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setFadeEnabled(true);
        mSlidingMenu.setFadeDegree(0.5f);
        mSlidingMenu.setFadingEdgeLength(200);
        mSlidingMenu.setShadowWidth(20);
        mSlidingMenu.setShadowDrawable(R.drawable.slidemenu_gradient);
        mLeftMenuLv = (ListView) findViewById(R.id.menu_left_lv);
        mLeftMenuA = new MenuLeftAdapter(this);
        mLeftMenuLv.setAdapter(mLeftMenuA);
        mLeftMenuLv.setOnItemClickListener(this);
        mRightMenuGv = (GridView) findViewById(R.id.menu_right_gv);
        mRightMenuGv.setAdapter(new MenuRightAdapter(this));
        mRightMenuGv.setOnItemClickListener(this);

        mUsername = (TextView) findViewById(R.id.menu_right_username);
    }

    @Override
    protected void onResume() {

        super.onResume();
        // 刷新右边菜单的用户名
        UserInfo user = AppContext.getInstance(mContext).getCurrentUser();
        final String username = user.getUsername();
        if (username != null && !username.equals("")) {
            mUsername.setText(username);
        } else {
            mUsername.setText("点击登录");
        }
        LinearLayout layoutLogin = (LinearLayout) findViewById(R.id.menu_right_login);
        layoutLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
    }

    public void setTouchMode(int mode) {
        mSlidingMenu.setTouchModeBehind(mode);
    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        switch (v.getId()) {
            case R.id.left_back_splash:
                // 跳回启动界面
                Intent intent1 = new Intent(HomeSlideMenuActivity.this, SplashActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                break;
            case R.id.titlebar_left_btn:// 左边侧滑菜单按钮
                mSlidingMenu.toggle();
                break;
            case R.id.left_setting:// 设置
                mViewCourier.startSettingView(this, null);
                overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
                break;
            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPostCreate(android.os.Bundle)
     */
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#findViewById(int)
     */
    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v != null)
            return v;
        return mHelper.findViewById(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
     * .Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mHelper.onSaveInstanceState(outState);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#setContentView(int)
     */
    @Override
    public void setContentView(int id) {
        super.setContentView(id);
        mHelper.registerAboveContentView(getLayoutInflater().inflate(id, null), new LayoutParams(LayoutParams
                .MATCH_PARENT, LayoutParams.MATCH_PARENT));

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#setContentView(android.view.View)
     */
    @Override
    public void setContentView(View v) {
        setContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#setContentView(android.view.View,
     * android.view.ViewGroup.LayoutParams)
     */
    @Override
    public void setContentView(View v, LayoutParams params) {
        super.setContentView(v, params);
        mHelper.registerAboveContentView(v, params);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
     * setBehindContentView(int)
     */
    public void setBehindContentView(int id) {
        setBehindContentView(getLayoutInflater().inflate(id, null));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
     * setBehindContentView(android.view.View)
     */
    public void setBehindContentView(View v) {
        setBehindContentView(v, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
     * setBehindContentView(android.view.View,
     * android.view.ViewGroup.LayoutParams)
     */
    public void setBehindContentView(View v, LayoutParams params) {
        mHelper.setBehindContentView(v, params);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#getSlidingMenu
     * ()
     */
    public SlidingMenu getSlidingMenu() {
        return mHelper.getSlidingMenu();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showAbove()
     */
    public void showContent() {
        mHelper.showContent();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showBehind()
     */
    public void showMenu() {
        mHelper.showMenu();
        LogM.log(this.getClass(), "打开菜单");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#showSecondaryMenu
     * ()
     */
    public void showSecondaryMenu() {
        mHelper.showSecondaryMenu();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivityBase#
     * setSlidingActionBarEnabled(boolean)
     */
    public void setSlidingActionBarEnabled(boolean b) {
        mHelper.setSlidingActionBarEnabled(b);
    }

    /**
     * 切换主界面Fragment
     *
     * @param fragment
     */
    private Fragment mCurFrg;

    @Override
    public void changeContent(BaseFragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null || !fragments.contains(fragment)) {
            transaction.add(R.id.home_container_fgm, fragment);
            transaction.addToBackStack(null);
        }
        if (mCurFrg != null) {
            transaction.hide(mCurFrg);
        }
        transaction.show(fragment);
        mCurFrg = fragment;
        transaction.commit();
    }

    @Override
    protected void setUnreadNumForMenu(String menuId, int num) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
        if (parent.getId() == R.id.menu_left_lv) {// 如果是左边菜单被点击
            for (int i = -1; ++i < mLeftMenuLv.getChildCount(); ) {
                ViewGroup item = (ViewGroup) mLeftMenuLv.getChildAt(i);
                item.setBackgroundColor(Color.TRANSPARENT);
            }
            view.setBackgroundColor(Color.parseColor("#30000000"));
            LogM.log(this.getClass(), "onItemClick" + position);

            mPresenter.onMenuSelected(((TMenuItem) view.getTag()).getUrl());

        } else {
//			mViewCourier.executeInHomeActivity((TMenuItem) view.getTag(),this);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (mSlidingMenu.isMenuShowing()) {
                mHelper.showContent();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void selectTab(int index) {
        LogM.log(this.getClass(), "selectTab 此方法暂未实现！！！！");
    }

    @Override
    public void setMenus(List<TMenuItem> menus) {
        mPrimaryMenuList = menus;
        mLeftMenuA.notifyDataSetChanged();
        mPresenter.onMenuSelected(menus.get(0).getUrl());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("HomeActivity", "主界面onDestory");
    }

    @Override
    public void hideMenu() {
        mSlidingMenu.showContent();
    }

    @Override
    public void executeURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.equals("apppubs://" + Constants.APPPUBS_PROTOCOL_TYPE_OPEN_SLIDE_MENU)) {
            showMenu();
        } else {
            super.executeURL(url);
        }
    }

    public class MenuLeftAdapter extends BaseAdapter {

        private Context context;
        private ImageLoader mImageLoader;
        private int mItemHeight;

        private boolean needCenterLayout = true;//是否需要菜单居中,如果菜单字数相同则居中，否则居左

        public MenuLeftAdapter(Context context) {

            this.context = context;
            mImageLoader = ImageLoader.getInstance();
            mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.menu_left_item_height);
            Log.v("MenuLeftAdapter", "MenuLeftAdapter初始化");
            int lenTemp = 0;
            for (TMenuItem mi : mPrimaryMenuList) {
                if (lenTemp == 0) {
                    lenTemp = mi.getName().length();
                } else if (lenTemp != mi.getName().length()) {
                    needCenterLayout = false;
                }
            }
        }

        @Override
        public int getCount() {
            return mPrimaryMenuList.size();
        }

        @Override
        public Object getItem(int pos) {
            if (mPrimaryMenuList.size() == 0)
                return null;
            return mPrimaryMenuList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup container) {
            Log.v("MenuLeftAdapter", "MenuLeftAdapter getView pos:" + pos);
            TMenuItem mi = mPrimaryMenuList.get(pos);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(needCenterLayout ? R.layout.item_menu_left_align_center : R.layout
                    .item_menu_left, null);

            AbsListView.LayoutParams param = new AbsListView.LayoutParams(-1, mItemHeight);
            convertView.setLayoutParams(param);
            convertView.setTag(mi);
            ImageView iconIv = (ImageView) convertView.findViewById(R.id.left_gv_iv);
            TextView titleTv = (TextView) convertView.findViewById(R.id.left_gv_tv);
            // 填充数据
            mImageLoader.displayImage(mi.getIconpic(), iconIv);
            titleTv.setText(mi.getName());
            if (!needCenterLayout && mi.getName().length() > 6) {//如果是左边对齐，而且字数大于6则缩小字体
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            } else {
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            }
            if (pos == 0) {
                // convertView.setBackgroundResource(R.drawable.menu_list_item_bg);
                convertView.setBackgroundColor(Color.parseColor("#30000000"));
                // titleTv.setTextColor(context.getResources().getColor(R.color.text_menu_left_h));
            }
            return convertView;
        }

    }

    public class MenuRightAdapter extends BaseAdapter {

        private Context context;
        private int mItemWidth;// item宽度，
        private int mItemHeight;// item高度
        // 填充数据
        private ImageLoader mImageloader;

        public MenuRightAdapter(Context context) {
            this.context = context;
            mImageloader = ImageLoader.getInstance();

            mItemWidth = context.getResources().getDimensionPixelSize(R.dimen.menu_right_gv_item_width);
            mItemHeight = context.getResources().getDimensionPixelSize(R.dimen.menu_right_gv_item_height);
        }

        @Override
        public int getCount() {
            return mSecondaryMenuList.size();
        }

        @Override
        public Object getItem(int pos) {
            return mSecondaryMenuList.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return pos;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            TMenuItem mi = mSecondaryMenuList.get(position);
            convertView = inflater.inflate(R.layout.item_menu_right_gv, null);
            convertView.setTag(mi);// 在操作此item时可已取出利用
            AbsListView.LayoutParams param = new AbsListView.LayoutParams(mItemWidth, mItemHeight);
            convertView.setLayoutParams(param);
            ImageView iconIv = (ImageView) convertView.findViewById(R.id.right_gv_iv);
            TextView titleTv = (TextView) convertView.findViewById(R.id.channels_gv_tv);

            // 填充数据
            if (mi.getIconpic() != null) {
                iconIv.setVisibility(View.VISIBLE);
                mImageloader.displayImage(mi.getIconpic(), iconIv);
            } else {
                iconIv.setVisibility(View.GONE);
            }
            titleTv.setText(mi.getName());

            return convertView;
        }

    }

}
