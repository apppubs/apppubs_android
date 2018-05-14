package com.apppubs.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.apppubs.AppContext;
import com.apppubs.constant.APError;
import com.apppubs.d20.R;
import com.apppubs.model.SystemBiz;
import com.apppubs.model.message.MsgBussiness;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

public abstract class BaseFragment extends Fragment implements KeyEvent.Callback, OnClickListener {

    public static final String ARGS_STRING_TITLE = "fragment_title";

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

    /**
     * 等待
     */
    private AVLoadingIndicatorView mLoadingView;

    /**
     * 空内容占位视图
     */
    private View mEmptyView;

    protected UserBussiness mUserBussiness;
    protected MsgBussiness mMsgBussiness;
    protected SystemBiz mSystemBiz;
    protected RequestQueue mRequestQueue;
    protected AppContext mAppContext;

    protected boolean needBack;

    private String mTitle;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && !Utils.isEmpty(args.getString(ARGS_STRING_TITLE))){
            mTitle = args.getString(ARGS_STRING_TITLE);
        }
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mInflater = inflater;

        mDefaultColor = BaseActivity.mDefaultColor;
        mTitleBar = initTitleBar();

        FrameLayout contentFL = new FrameLayout(mContext);
        contentFL.addView(initLayout(inflater, container, savedInstanceState));
        //content中加入等待图标
        mLoadingView = initLoadingView();
        FrameLayout.LayoutParams loadingLP = getLoadingLayoutParams();
        contentFL.addView(mLoadingView, loadingLP);

        mEmptyView = initEmptyView();
        FrameLayout.LayoutParams emptyLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        emptyLP.gravity = Gravity.CENTER;
        contentFL.addView(mEmptyView);

        View rootView = null;
        if (mTitleBar != null) {
            LinearLayout ll = new LinearLayout(mContext);
            ll.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams titleBarLl = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT, Utils.dip2px(mContext, 50));
            ll.addView(mTitleBar, titleBarLl);

            LinearLayout.LayoutParams contentLP = new LinearLayout.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, 0);
            contentLP.weight = 1;
            ll.addView(contentFL, contentLP);
            rootView = ll;
        } else {
            rootView = contentFL;
        }
        return rootView;
    }

    protected View initEmptyView() {
        TextView emptyView = new TextView(mContext);
        emptyView.setText(R.string.have_no_content);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R
                .dimen.item_text_size));
        emptyView.setTextColor(mContext.getResources().getColor(R.color.common_text_gray));
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setVisibility(View.GONE);
        return emptyView;
    }

    @NonNull
    private FrameLayout.LayoutParams getLoadingLayoutParams() {
        FrameLayout.LayoutParams loadingLP = new FrameLayout.LayoutParams(Utils.dip2px(mContext,
                40), Utils.dip2px(mContext, 40));
        loadingLP.gravity = Gravity.CENTER;
        return loadingLP;
    }

    private AVLoadingIndicatorView initLoadingView() {
        AVLoadingIndicatorView mLoadingView = new AVLoadingIndicatorView(mContext);
        mLoadingView.setIndicator("BallBeatIndicator");
        mLoadingView.setIndicatorColor(Color.parseColor("#C0C0C0"));
        mLoadingView.setVisibility(View.GONE);
        return mLoadingView;
    }

    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        return null;
    }

    protected TitleBar initTitleBar() {
        return null;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        mImageLoader = mHostActivity.getImageLoader();
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

    protected DisplayImageOptions getDefaultImageLoaderOptions() {
        return mHostActivity.getDefaultImageLoaderOption();
    }

    protected int getThemeColor() {
        return mAppContext.getThemeColor();
    }

    @Override
    public void onClick(View v) {
        mHostActivity.onClick(v);
    }

    public void setNeedBack(boolean need) {
        this.needBack = need;
    }

    public boolean getNeedBack() {
        return this.needBack;
    }

    public void onError(APError error) {
        mHostActivity.getErrorHandler().onError(error);
    }

    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    public void showEmptyView(){
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void hideEmptyView(){
        mEmptyView.setVisibility(View.GONE);
    }

    public void executeURL(String url) {
        mHostActivity.executeURL(url);
    }
}
