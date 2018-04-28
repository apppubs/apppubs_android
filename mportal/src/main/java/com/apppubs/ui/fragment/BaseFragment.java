package com.apppubs.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.apppubs.AppContext;
import com.apppubs.d20.R;
import com.apppubs.model.SystemBiz;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.model.message.MsgBussiness;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BaseFragment extends Fragment implements KeyEvent.Callback {

    protected static int mDefaultColor;
    /**
     * 根元素
     */
    protected View mRootView;
    /**
     * 布局渲染工具
     */
    protected LayoutInflater mInflater;
    protected BaseActivity mHostActivity;
    protected Context mContext;
    protected ImageLoader mImageLoader;
    protected TitleBar mTitleBar;

    protected UserBussiness mUserBussiness;
    protected MsgBussiness mMsgBussiness;
    protected SystemBiz mSystemBiz;
    protected RequestQueue mRequestQueue;
    protected AppContext mAppContext;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mHostActivity = (BaseActivity) activity;
        mSystemBiz = SystemBiz.getInstance(activity);
        mMsgBussiness = MsgBussiness.getInstance(activity);
        mUserBussiness = UserBussiness.getInstance(activity);
        mRequestQueue = mHostActivity.getRequestQueue();
        mAppContext = AppContext.getInstance(mContext);
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mInflater = inflater;
        mDefaultColor = BaseActivity.mDefaultColor;
        mTitleBar = initTitleBar();
        if (mTitleBar!=null){
            LinearLayout ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams titleBarLl = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT, Utils.dip2px(mContext,50));
            ll.addView(mTitleBar,titleBarLl);
            ll.addView(initLayout(inflater, container,savedInstanceState));
            return ll;
        }
        return  initLayout(inflater, container,savedInstanceState);
    }

    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState){
        return null;
    }

    protected TitleBar initTitleBar(){
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mImageLoader = mHostActivity.getImageLoader();
        mTitleBar = mHostActivity.getTitleBar();
        changeActivityTitleView(mTitleBar);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            changeActivityTitleView(mTitleBar);
        }
    }

    /**
     * 修改TitleView属性
     * 当activity被创建后调用此方法可修改activity的titlebar
     * 当fragment重现被展示时调用此方法刷新父activity的titlebar
     */
    public void changeActivityTitleView(TitleBar titleBar) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        return false;
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int count, KeyEvent event) {

        return false;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim
				.slide_out_to_left);
    }

    /**
     * 填充某View下的某TextView
     *
     * @param rootView TextView的父view
     * @param resId    TextView 的id
     * @param content  TextView 文本内容
     */
    protected void fillTextView(View rootView, int resId, String content) {
        TextView tv = (TextView) rootView.findViewById(resId);
        tv.setText(content);

    }

    protected void fillImageView(View rootView, int resId, String uri) {
        ImageView iv = (ImageView) rootView.findViewById(resId);
        mImageLoader.displayImage(uri, iv);
    }

    protected void fillTextView(int resId, String content) {
        fillTextView(mRootView, resId, content);
    }

    protected void fillImageView(int resId, String uri) {
        fillImageView(mRootView, resId, uri);

    }

    protected void setVisibilityOfViewByResId(View rootView, int resId, int visibility) {

        View v = rootView.findViewById(resId);
        v.setVisibility(visibility);
    }

    protected void registerOnClickListener(int resid, OnClickListener listener) {
        View view = mRootView.findViewById(resid);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    protected void startActivity(Class<?> clazz) {
        Intent intent = new Intent(mHostActivity, clazz);
        startActivity(intent);
    }

    protected DisplayImageOptions getDefaultImageLoaderOptions(){
        return mHostActivity.getDefaultImageLoaderOption();
    }

    protected int getThemeColor(){
        return mAppContext.getThemeColor();
    }

}
