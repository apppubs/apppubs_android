package com.apppubs.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.MportalApplication;
import com.apppubs.bean.Settings;
import com.apppubs.bean.http.CheckVersionResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.Actions;
import com.apppubs.constant.Constants;
import com.apppubs.d20.R;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.NewsBiz;
import com.apppubs.model.PaperBiz;
import com.apppubs.model.SystemBiz;
import com.apppubs.model.UserBiz;
import com.apppubs.model.message.MsgBussiness;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.ui.APErrorHandler;
import com.apppubs.ui.ICommonView;
import com.apppubs.ui.widget.AlertDialog;
import com.apppubs.ui.widget.ConfirmDialog;
import com.apppubs.ui.widget.TitleBar;
import com.apppubs.util.JSONResult;
import com.apppubs.util.LogM;
import com.apppubs.util.Utils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class BaseActivity extends FragmentActivity implements OnClickListener, ICommonView {

    public static final int FILECHOOSER_RESULTCODE = 1;
    public static final String EXTRA_STRING_TITLE = "extra_title";
    public static final String EXTRA_BOOLEAN_NEED_TITLEBAR = "need_titlebar";

    protected Context mContext;
    /**
     * 是否需要titlebar
     */
    private boolean isNeedTitleBar = true;
    private boolean isNeedBack = true;
    protected TitleBar mTitleBar;
    protected ImageLoader mImageLoader;
    protected MportalApplication mApp;
    protected AppContext mAppContext;

    protected NewsBiz mNewsBiz;
    protected SystemBiz mSystemBiz;
    protected PaperBiz mPaperBiz;
    protected UserBussiness mUserBussiness;
    protected MsgBussiness mMsgBussiness;

    protected int curTheme;
    protected int mThemeColor;
    private boolean mShouldInterceptBackClicked;
    private BroadcastReceiver mBr;

    public static int mDefaultColor;// 字体默认颜色

    protected RequestQueue mRequestQueue;
    private static int mActiveActivityNum;// 处于活跃状态的activity

    private ValueCallback<Uri> mUploadMessage;

    public void setUploadMessage(ValueCallback<Uri> uploadMessage) {
        mUploadMessage = uploadMessage;
    }

    private Handler mHandler;

    protected APErrorHandler mErrorHandler;

    private View mLoadingView;
    private View mEmptyView;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        LogM.log(this.getClass(), " BaseActivity onCreate");


        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };

        mHandler.sendEmptyMessage(3);

        mContext = this;
        mAppContext = AppContext.getInstance(mContext);
        isNeedTitleBar = getIntent().getBooleanExtra(EXTRA_BOOLEAN_NEED_TITLEBAR, isNeedTitleBar);

        int theme = mAppContext.getSettings().getTheme();
        switch (theme) {
            case Settings.THEME_BLUE:
                setTheme(R.style.AppThemeBlue);
                break;
            case Settings.THEME_INDIGO:
                setTheme(R.style.AppThemeIndigo);
                break;
            case Settings.THEME_RED:
                setTheme(R.style.AppThemeRed);
                break;
            case Settings.THEME_BROWN:
                setTheme(R.style.AppThemeBrown);
                break;
            default:
                setTheme(R.style.AppThemeBlue);
        }


        mThemeColor = mAppContext.getThemeColor();
        // // 横竖屏 当方向标记为2,3时，平板为横屏，为3,4时手机为横屏
        int orientationFlag = Utils.getIntegerMetaData(this, "DISPLAY_ORIENTATION");
        if (!Utils.isPad(this) && orientationFlag >= 3
                || (Utils.isPad(this) && (orientationFlag == 2 || orientationFlag == 3))) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }

        System.out.println(Utils.getIntegerMetaData(this, "DISPLAY_ORIENTATION"));

        TypedArray ta = obtainStyledAttributes(new int[]{R.attr.appDefaultColor});
        mDefaultColor = ta.getColor(0, 0xffffff);
        ta.recycle();
        getDefaultImageLoaderOption();
        mImageLoader = ImageLoader.getInstance();
        mApp = (MportalApplication) getApplication();

        mNewsBiz = NewsBiz.getInstance(mContext);
        mSystemBiz = SystemBiz.getInstance(this);
        mPaperBiz = PaperBiz.getInstance(this);
        mUserBussiness = UserBussiness.getInstance(this);
        mMsgBussiness = MsgBussiness.getInstance(this);

        mBr = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                LogM.log(this.getClass(), "onReceive finish");
                if (BaseActivity.this instanceof FirstLoginActity) {
                    return;
                }
                finish();
            }
        };
        registerReceiver(mBr, new IntentFilter(Actions.CLOSE_ALL_ACTIVITY));

        mRequestQueue = Volley.newRequestQueue(this);

        mErrorHandler = new APErrorHandler(this);
    }

    public DisplayImageOptions getDefaultImageLoaderOption() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.white)
                .showImageOnFail(R.drawable.white)
                .showImageForEmptyUri(R.drawable.white)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
//				.denyNetworkDownload(MportalApplication.systemState.getNetworkState() !=
//						ConnectivityManager.TYPE_WIFI && !mAppContext.getSettings()
//						.isAllowDowPicUse2G())
                .build();
        return options;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public Request<String> addStringRequest(String url, Listener<String> listener, ErrorListener errorListener) {
        StringRequest request = new StringRequest(url, listener, errorListener);
        return mRequestQueue.add(request);
    }

    public String getUrlCache(String url) {
        Cache.Entry entry = mRequestQueue.getCache().get(url);
        if (entry != null) {
            return new String(entry.data);
        }
        return null;
    }

    @Override
    public void setContentView(int layoutResID) {

        View content = getLayoutInflater().inflate(layoutResID, null);

        FrameLayout contentFL = new FrameLayout(mContext);
        contentFL.addView(content);
        //content中加入等待图标
        mLoadingView = initLoadingView();
        FrameLayout.LayoutParams loadingLP = getLoadingLayoutParams();
        contentFL.addView(mLoadingView, loadingLP);

        mEmptyView = initEmptyView();
        FrameLayout.LayoutParams emptyLP = new FrameLayout.LayoutParams(FrameLayout.LayoutParams
                .WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        emptyLP.gravity = Gravity.CENTER;
        contentFL.addView(mEmptyView);
        if (isNeedTitleBar) {
            super.setContentView(R.layout.act_base);
            ViewGroup vg = (ViewGroup) findViewById(R.id.title_ll);
            LinearLayout.LayoutParams contentLP = new LinearLayout.LayoutParams(LinearLayout
                    .LayoutParams.MATCH_PARENT, 0);
            contentLP.weight = 1;
            vg.addView(contentFL, contentLP);
            mTitleBar = (TitleBar) findViewById(R.id.base_tb);
            mTitleBar.setBackgroundColor(mThemeColor);
            if (isNeedBack) {
                mTitleBar.setLeftBtnClickListener(this);
                mTitleBar.setLeftImageResource(R.drawable.top_back_btn);
                String title = getIntent().getStringExtra(EXTRA_STRING_TITLE);
                if (!TextUtils.isEmpty(title)) {
                    setTitle(title);
                }
            }
        } else {
            setContentView(contentFL);
        }
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

    @Override
    public void setContentView(View view) {

        if (isNeedTitleBar) {
            super.setContentView(R.layout.act_base);
            ViewGroup vg = (ViewGroup) findViewById(R.id.title_ll);
            vg.addView(view);
            mTitleBar = (TitleBar) findViewById(R.id.base_tb);
            mTitleBar.setBackgroundColor(mThemeColor);
            if (isNeedBack) {
                mTitleBar.setLeftBtnClickListener(this);
                mTitleBar.setLeftImageResource(R.drawable.top_back_btn);
                String title = getIntent().getStringExtra(EXTRA_STRING_TITLE);
                if (!TextUtils.isEmpty(title)) {
                    setTitle(title);
                }
            }
        } else {
            super.setContentView(view);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mActiveActivityNum++;

        if (mActiveActivityNum == 1) {
            onAppActive();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {

    }

    protected void onAppActive() {

    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);

        mActiveActivityNum--;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.titlebar_left_btn:
                if (!shouldInterceptBackClick()) {
                    finish();
                }
                break;
            default:
                break;
        }
    }

    public boolean shouldInterceptBackClick() {
        return mShouldInterceptBackClicked;
    }

    public void setShouldInterceptBackClick(boolean shouldIntercept) {
        this.mShouldInterceptBackClicked = shouldIntercept;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBr);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
    }

    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    /**
     * 设置标题文字
     *
     * @param title
     */
    protected void setTitle(String title) {
        if (mTitleBar != null)
            mTitleBar.setTitle(title);
    }

    /**
     * 隐藏titlebar
     */
    public void hideTitleBar() {
        if (mTitleBar != null)
            mTitleBar.setVisibility(View.GONE);
    }

    public int getThemeColor() {
        return mThemeColor;
    }

    /**
     * 设置此activity是否需要titlebar 必须在setconview之前进行设置
     *
     * @param need
     */
    protected void setNeedTitleBar(boolean need) {
        isNeedTitleBar = need;
        if (mTitleBar != null) {
            mTitleBar.setVisibility(need ? View.VISIBLE : View.GONE);
        }
    }

    public void setNeedBack(boolean isNeedBack) {
        this.isNeedBack = isNeedBack;
    }

    protected void setVisibilityOfViewByResId(int resId, int visibility) {

        View v = findViewById(resId);
        v.setVisibility(visibility);
    }

    protected void setVisibilityOfViewByResId(View view, int resId, int visibility) {
        View v = view.findViewById(resId);
        v.setVisibility(visibility);

    }

    public static void startActivity(Context context, Class<?> clazz) {
        startActivity(context, clazz, null);
    }

    public static void startActivity(Context context, Class<?> clazz, Bundle extras) {
        Intent i = new Intent(context, clazz);
        if (extras != null) {
            i.putExtras(extras);
        }
        context.startActivity(i);
    }

    /**
     * 填充某View下的某TextView
     *
     * @param resId   TextView 的id
     * @param content TextView 文本内容
     */
    protected void fillTextView(int resId, String content) {
        TextView tv = (TextView) findViewById(resId);
        tv.setText(content);

    }

    protected void addRequest(String url, final RequestListener listener, final int requestCode) {
        mRequestQueue.add(new StringRequest(url, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                JSONResult jr = JSONResult.compile(response);
                listener.onResponse(jr, requestCode);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onException(JSONResult.RESULT_CODE_FAIL, requestCode);
            }
        }));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LogM.log(this.getClass(), "onActivityResult");
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }

        super.onActivityResult(requestCode, resultCode, intent);
    }

    public APErrorHandler getErrorHandler() {
        return mErrorHandler;
    }

    @Override
    public void showLoading() {
        mLoadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        mLoadingView.setVisibility(View.GONE);
    }

    @Override
    public void onError(APError error) {
        mErrorHandler.onError(error);
    }

    @Override
    public void showEmptyView() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
    }

    public void executeURL(String url) {
        if (url.equals("apppubs://" + Constants.APPPUBS_PROTOCOL_TYPE_CLOSE_WINDOW)) {
            finish();
        } else if (url.equals("apppubs://" + Constants.APPPUBS_PROTOCOL_TYPE_CHECK_VERSION)) {
            showLoading();
            SystemBiz.getInstance(mContext).checkUpdate(new IAPCallback<CheckVersionResult>() {
                @Override
                public void onDone(CheckVersionResult obj) {
                    showVersionUpdateAlert(obj);
                    hideLoading();
                }

                @Override
                public void onException(APError error) {
                    mErrorHandler.onError(error);
                    hideLoading();
                }
            });
        } else if (url.equals("apppubs://" + Constants.APPPUBS_PROTOCOL_TYPE_LOGOUT)) {
            onLogout();
        } else {
            ViewCourier.getInstance(mContext).openWindow(url);
        }
    }

    private void showVersionUpdateAlert(final CheckVersionResult obj) {
        if (obj.getUpdateType() < 1) {
            Toast.makeText(mContext, "当前版本为最新版本！", Toast.LENGTH_LONG).show();
        } else if (obj.getUpdateType() < 3) {
            String title = String.format("检查到有新版 %s", TextUtils.isEmpty(obj.getVersionName()) ? "" : "V" +
                    obj.getVersionName());
            ConfirmDialog dialog = new ConfirmDialog(mContext, new ConfirmDialog.ConfirmListener() {

                @Override
                public void onCancelClick() {
                }

                @Override
                public void onOkClick() {
                    AppManager.getInstance(mContext).downloadApp(obj.getDownloadURL());
                    Toast.makeText(mContext, "正在下载中，请稍候!", Toast.LENGTH_SHORT).show();
                }
            }, title, obj.getDescribe(), "下次", "更新");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        } else {
            String title = String.format("检查到有新版 %s", TextUtils.isEmpty(obj.getVersionName()) ? "" : "V" +
                    obj.getVersionName());

            AlertDialog dialog = new AlertDialog(mContext, new AlertDialog.OnOkClickListener() {
                @Override
                public void onclick() {
                    AppManager.getInstance(mContext).downloadApp(obj.getDownloadURL());
                    Toast.makeText(mContext, "正在下载中，请稍候!", Toast.LENGTH_SHORT).show();
                }
            }, title, obj.getDescribe(), "更新");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }
    }

    private void onLogout() {
        new ConfirmDialog(this,
                new ConfirmDialog.ConfirmListener() {

                    @Override
                    public void onOkClick() {
                        logout();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                }, "确定注销登陆吗？", "取消", "注销").show();
    }

    private void logout() {
        showLoading();
        UserBiz.getInstance(mContext).logout(mContext, new IAPCallback() {
            @Override
            public void onDone(Object obj) {
                hideLoading();
                BaseActivity.this.finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onException(APError error) {
                hideLoading();
                onError(error);
            }
        });

    }
}
