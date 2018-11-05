package com.apppubs.ui.webapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.amap.api.location.AMapLocation;
import com.apppubs.AppManager;
import com.apppubs.bean.TMenuItem;
import com.apppubs.constant.Constants;
import com.apppubs.d20.R;
import com.apppubs.jsbridge.BridgeHandler;
import com.apppubs.jsbridge.BridgeWebView;
import com.apppubs.jsbridge.CallBackFunction;
import com.apppubs.jsbridge.DefaultHandler;
import com.apppubs.model.SystemBiz;
import com.apppubs.presenter.WebAppPresenter;
import com.apppubs.ui.activity.CaptureActivity;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.fragment.TitleBarFragment;
import com.apppubs.ui.home.HomeBaseActivity;
import com.apppubs.ui.imageselector.MultiImageSelectorActivity;
import com.apppubs.ui.myfile.FilePreviewFragment;
import com.apppubs.ui.widget.HorizontalScrollLabels;
import com.apppubs.ui.widget.ProgressHUD;
import com.apppubs.ui.widget.ProgressWebView;
import com.apppubs.ui.widget.ProgressWebView.ProgressWebViewListener;
import com.apppubs.ui.widget.SegmentedGroup;
import com.apppubs.ui.widget.SignatureView;
import com.apppubs.util.Base64;
import com.apppubs.util.BitmapUtils;
import com.apppubs.util.LocationManager;
import com.apppubs.util.LogM;
import com.apppubs.util.SystemUtils;
import com.apppubs.util.Utils;
import com.apppubs.vpn.VPNBiz;
import com.apppubs.vpn.VPNViewCourierHelper;
import com.etop.VATDetectLine.activity.VatRecogActivity;
import com.jelly.mango.ImageSelectListener;
import com.jelly.mango.Mango;
import com.jelly.mango.MultiplexImage;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebAppFragment extends TitleBarFragment implements OnClickListener, IWebAppView {

    public static final String ARGUMENT_INT_MENUBARTYPE = "menu_bar_type";
    public static final String ARGUMENT_STRING_URL = "url";
    public static final String ARGUMENT_STRING_MORE_MENUS = "more_menus";
    public static final String ARGUMENT_STRING_NEED_TITLEBAR = "fragment_title_bar";
    public static final String ARGUMENT_BOOLEAN_NEED_TITLE_BAR_ARROW = "arg_need_title_bar_arrow";

    public static final int REQUEST_CODE_PICTURES = 100;
    public static final int REQUEST_CODE_QRCODE = 101;//二维码扫描接过

    private final String JS_MENU_ITEM_REFRESH = "menu_item_refresh";

    private static final int SDK_PAY_FLAG = 1;

    private static final int REQUEST_CODE_OCR = 103;

    private String mUrl;
    private String mPreviousURL;
    private String mApppubsMiddleURL;//中间url，访问中间url会重定向到目标url
    private String mMoreMenusStr;
    private boolean isNeedActionBarArrow;

    private ProgressWebView mWebView;
    private ImageView mTitleBarArrow;
    private View mRootView;
    private WebSettings mSettings;

    private PopupWindow mPopWin;
    private ProgressHUD mProgressHUD;

    boolean isCloseButtonAdded;
    private CallBackFunction mTmpHandelCallbackFunction;

    private String mOnloadingText = "载入中 ···";

    private WebAppPresenter mPresenter;

    //pop signature
    private PopupWindow mSignaturePopWin;
    private SegmentedGroup mSignatureSegmentedGroup;
    private SignatureView mSignatureSignatureView;
    private EditText mSignatureET;
    private Listener mListener;

    public interface Listener {
        void onLinkClicked(String url);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    JSONObject json = new JSONObject((Map<String, String>) msg.obj);
                    mTmpHandelCallbackFunction.onCallBack(json.toString());
                    break;
                }

                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogM.log(this.getClass(), "WebAppFragment-->onAttach");
        Bundle args = getArguments();
        initURLs(args.getString(ARGUMENT_STRING_URL));
        mMoreMenusStr = args.getString(ARGUMENT_STRING_MORE_MENUS);
        isNeedTitleBar = args.getBoolean(ARGUMENT_STRING_NEED_TITLEBAR, isNeedTitleBar);
        if (TextUtils.isEmpty(mMoreMenusStr)) {
            mMoreMenusStr = "0";
        }
        isNeedActionBarArrow = args.getBoolean(ARGUMENT_BOOLEAN_NEED_TITLE_BAR_ARROW, isNeedActionBarArrow);
        mHostActivity.setShouldInterceptBackClick(true);
    }

    private void initURLs(String url) {
        mUrl = url;
        mPreviousURL = extractPreviousFromUrl(mUrl);
        mApppubsMiddleURL = convertUrl(mUrl);
    }

    /**
     * 转换服务器传来的url
     *
     * @param url 转化之前url
     * @return 转换之后的url
     */
    private String convertUrl(String url) {
        try {
            url = URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String myURL = mAppContext.getLocalBaseURL() + Constants.API_ENTRY + "?apiName=" + Constants.API_NAME_HTTP +
                "&redirectURL=" + url + "&username=" +
                mAppContext.getCurrentUser().getUsername() + "&token=" + mAppContext.getCurrentUser().getToken();
        return myURL;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        mRootView = inflater.inflate(R.layout.frg_webapp, null);
        initComponent(mRootView);
        initStates();
        initPresenter();
        mPresenter.onCreateView();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 如果没有标题，则标题为“载入中。。。”
        if (mTitleBar != null && TextUtils.isEmpty(mTitleBar.getTitle())) {
            mTitleBar.setTitle(mOnloadingText);
        }

        if (!(mHostActivity instanceof HomeBaseActivity) && mTitleBar != null) {
            mTitleBar.setLeftBtnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    webviewGoBack();
                }
            });
        }

        if (mTitleBar != null) {
            mTitleBar.reset();
            if (mHostActivity instanceof HomeBaseActivity) {
                isCloseButtonAdded = true;// 避免出现关闭
            }


            if (mMoreMenusStr != null && !mMoreMenusStr.equals("") && mMoreMenusStr.split(",")
                    .length > 1) {
                mTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable.title_more,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                openMenu();
                            }
                        });
            } else if (mMoreMenusStr != null && !mMoreMenusStr.equals("") && mMoreMenusStr.split
                    (",").length == 1) {
                if (mMoreMenusStr.equals(TMenuItem.WEB_APP_MENU_REFRESH + "")) {
                    mTitleBar.addRightBtnWithImageResourceIdAndClickListener(R.drawable
                            .titlebar_refresh, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            refresh();
                        }
                    });
                } else if (mMoreMenusStr.equals(TMenuItem.WEB_APP_MENU_SHARE + "")) {
                    mTitleBar.addRightBtnWithTextAndClickListener("分享", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                        }
                    });
                } else if (mMoreMenusStr.equals(TMenuItem.WEB_APP_MENU_OPEN_WITH_BROWSER + "")) {
                    mTitleBar.addRightBtnWithTextAndClickListener("浏览器打开", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            openInBrowser();
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            webviewGoBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initPresenter() {
        mPresenter = new WebAppPresenter(getContext(), this);
    }

    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
    private void initComponent(View v) {
        if (isNeedActionBarArrow) {
            mTitleBar.setLeftBtnWithImageResourceId(R.drawable.close);
        }
        mWebView = (ProgressWebView) mRootView.findViewById(R.id.webapp_wb);
        mWebView.setInitialScale(1);
        mWebView.setHostActivity(mHostActivity);

        mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);
        mSettings.setAppCacheEnabled(true);
        mSettings.setBuiltInZoomControls(true);
        mSettings.setUseWideViewPort(true);
        mSettings.setSupportZoom(true);
        mSettings.setDomStorageEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= 11) {

            mSettings.setDisplayZoomControls(false);

        }
        mWebView.setDefaultHandler(new DefaultHandler());
        mWebView.setListener(new ProgressWebViewListener() {

            @Override
            public void onURLClicked(String url) {
                initURLs(url);
                if (mListener != null) {
                    mListener.onLinkClicked(url);
                }
            }

            @Override
            public void onReceiveTitle(String title) {
                // 之前如果没有设置过标题则将网页的title当做标题
                if (mTitleBar != null && !TextUtils.isEmpty(title)) {
                    mTitleBar.setTitle(title);
                }

            }

            @Override
            public void onFinished() {
                if (mTitleBar != null && mTitleBar.getTitle().equals(mOnloadingText)) {
                    mTitleBar.setTitle("");
                }
            }
        });

        mWebView.registerHandler("hideMenuItems", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                System.out.println("hideMenuItems" + data);
                if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(mMoreMenusStr) && data
                        .contains(JS_MENU_ITEM_REFRESH)) {
                    mMoreMenusStr = mMoreMenusStr.replaceAll("0", "").replaceAll(",0", "")
                            .replaceAll("0", "");
                }
                mWebView.post(new Runnable() {

                    @Override
                    public void run() {
                        changeActivityTitleView(mTitleBar);
                    }
                });
            }
        });
        mWebView.registerHandler("closeWindow", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {

                mHostActivity.finish();
                function.onCallBack("请求结束");
            }
        });
        mWebView.registerHandler("openWindow", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                mHostActivity.executeURL(data);
                function.onCallBack("请求结束");
            }
        });
        //选择图片
        mWebView.registerHandler("chooseImage", new BridgeHandler() {


            @Override
            public void handler(String data, CallBackFunction function) {
                mTmpHandelCallbackFunction = function;
                Intent intent = new Intent(mHostActivity, MultiImageSelectorActivity.class);
                startActivityForResult(intent, REQUEST_CODE_PICTURES);
            }
        });
        //获取设备id
        mWebView.registerHandler("getDeviceId", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack(AppManager.getInstance(mContext).getMachineId());
            }

        });

        //切换app
        mWebView.registerHandler("changeApp", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                final String[] strArr = data.split(",");

                mWebView.post(new Runnable() {

                    @Override
                    public void run() {
                        AppManager.getInstance(mContext).showChangeDialog(mContext, strArr[0],
                                strArr[1]);
                        changeActivityTitleView(mTitleBar);
                    }
                });
            }
        });

        //支付宝支付
        mWebView.registerHandler("alipay", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                mTmpHandelCallbackFunction = function;
                final String[] strArr = data.split(",");
                LogM.log(this.getClass(), "支付宝进行宝支付");
                try {
                    JSONObject jo = new JSONObject(data);
                    awakenAlipay(jo.getString("orderstr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mWebView.post(new Runnable() {

                    @Override
                    public void run() {

                    }
                });
            }
        });

        //微信支付
        mWebView.registerHandler("wxpay", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                mTmpHandelCallbackFunction = function;
                LogM.log(this.getClass(), "微信支付");
                try {
                    awakeWxpay(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mWebView.registerHandler("amap", new BridgeHandler() {
            @Override
            public void handler(String data, final CallBackFunction function) {
                System.out.println("amap点击");
                LocationManager manager = LocationManager.getInstance(mContext);
                manager.setListener(new LocationManager.LocationListener() {
                    @Override
                    public void onLocationChanded(AMapLocation location) {
                        String data = "{\"latitude\":" + location.getLatitude() + "," +
                                "\"longtitude\":" + location.getLongitude() + "}";
                        System.out.println(data);
                        function.onCallBack(data);
                    }
                });
                manager.requestLocation();
            }
        });

        mTitleBarArrow = (ImageView) mRootView.findViewById(R.id.webapp_arrow_iv);
        mTitleBarArrow.setVisibility(isNeedActionBarArrow ? View.VISIBLE : View.GONE);
        mTitleBarArrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) v;
                iv.setImageResource(toggleTitleBar() ? R.drawable.header_arrow_up : R.drawable.header_arrow_down);
            }
        });
    }

    private void awakenAlipay(final String orderStr) {

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
                Map<String, String> result = alipay.payV2(orderStr, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void awakeWxpay(String content) throws JSONException {
        JSONObject json = new JSONObject(content);
        if (null != json && !json.has("retcode")) {
            PayReq req = new PayReq();
            //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
            req.appId = json.getString("appid");
            req.partnerId = json.getString("partnerid");
            req.prepayId = json.getString("prepayid");
            req.nonceStr = json.getString("noncestr");
            req.timeStamp = json.getString("timestamp");
            req.packageValue = json.getString("package");
            req.sign = json.getString("sign");
            req.extData = "app data"; // optional
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//            IWXAPI api = WXAPIFactory.createWXAPI(mContext, req.appId);
            Log.e("WebAppFragment", "checkArgs=" + req.checkArgs());
            SystemBiz.getInstance(mContext).getWxApi().sendReq(req);
        } else {
            Log.d("PAY_GET", "返回错误" + json.getString("retmsg"));
        }
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void openMenu() {

        View menuPop = LayoutInflater.from(mHostActivity).inflate(R.layout.pop_web_menu, null);

        mPopWin = new PopupWindow(menuPop, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
                .LayoutParams.WRAP_CONTENT);
        mPopWin.setFocusable(true);
        mPopWin.setOutsideTouchable(true);
        mPopWin.setBackgroundDrawable(new BitmapDrawable());
        mPopWin.showAsDropDown(mTitleBar.getRightView());

        String[] arr = mMoreMenusStr.split(",");
        for (String s : arr) {

            int menuIntValue = Integer.parseInt(s);
            switch (menuIntValue) {
                case TMenuItem.WEB_APP_MENU_REFRESH:
                    setVisibilityOfViewByResId(menuPop, R.id.pop_ref_ll, View.VISIBLE);
                    break;
                case TMenuItem.WEB_APP_MENU_OPEN_WITH_BROWSER:
                    setVisibilityOfViewByResId(menuPop, R.id.pop_browser_ll, View.VISIBLE);
                    break;
                case TMenuItem.WEB_APP_MENU_SHARE:
                    setVisibilityOfViewByResId(menuPop, R.id.pop_share_ll, View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        View refV = menuPop.findViewById(R.id.pop_ref_ll);
        View brsV = menuPop.findViewById(R.id.pop_browser_ll);
        refV.setOnClickListener(this);
        brsV.setOnClickListener(this);
    }

    private void initStates() {

        mWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        String mimetype,
                                        long contentLength) {

                String downloadUrl = url;
                CookieManager cookieManager = CookieManager.getInstance();
                String cookieStr = cookieManager.getCookie(url);
                if (!TextUtils.isEmpty(cookieStr)) {
                    if (url.contains("?")) {
                        downloadUrl += "&" + cookieStr;
                    } else {
                        downloadUrl += "?" + cookieStr;
                    }
                }
                Bundle args = new Bundle();


                String fileName = fetchFileName(contentDisposition, url);
                if (!TextUtils.isEmpty(fileName)) {
                    args.putString(FilePreviewFragment.ARGS_FILE_NAME, fileName);
                }
                args.putString(FilePreviewFragment.ARGS_STRING_URL, url);
                args.putString(ContainerActivity.EXTRA_STRING_TITLE, "文件预览");
                args.putString(FilePreviewFragment.ARGS_STRING_MIME_TYPE, mimetype);
                ContainerActivity.startContainerActivity(getContext(), FilePreviewFragment.class,
                        args);

            }

            private String fetchFileName(String contentDisposition, String url) {

                if (!TextUtils.isEmpty(contentDisposition)) {
                    Pattern pattern = Pattern.compile("filename=\"(.*?)\"", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(contentDisposition);
                    while (matcher.find()) {
                        String fileName = null;
                        if (!TextUtils.isEmpty(fileName = matcher.group(1))) {
                            try {
                                fileName = URLDecoder.decode(fileName, "utf-8");
                                return fileName;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        URL urlO = new URL(url);
                        if (!TextUtils.isEmpty(urlO.getFile())) {
                            return urlO.getFile().substring(urlO.getFile().lastIndexOf("/") + 1);
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        });

        loadUrl();
    }

    private void loadUrl() {
        mWebView.loadUrl(mApppubsMiddleURL, SystemBiz.getInstance(mContext).getCommonHeader());
    }

    public void webviewGoBack() {
        if (isNeedActionBarArrow) {
            mHostActivity.finish();
        } else {
            if (!Utils.isEmpty(mPreviousURL)) {
                if (mPreviousURL.equals("main")) {
                    mHostActivity.finish();
                } else {
                    mWebView.loadUrl(mPreviousURL);
                }
            } else {
                if (mWebView.canGoBack()) {
                    if (!isCloseButtonAdded) {
                        addClose();
                    }
                    mWebView.goBack();
                } else {
                    mHostActivity.finish();
                }
            }
        }
    }

    private String extractPreviousFromUrl(String url) {

        Pattern pattern = Pattern.compile("previousurl=([^&?]*)");
        Matcher m = pattern.matcher(url);
        while (m.find()) {
            try {
                return URLDecoder.decode(m.group(1), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void addClose() {
        // 当主界面跳转时判断此button是否需要，如果需要才显示，判断需要的方式则为此button是否被加载过
        // 根据此fragment嵌入的activity不同，显示不同的样式
        mTitleBar.addLeftBtnWithImageResourceIdAndClickListener(R.drawable.close_circle, new
                OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        mHostActivity.finish();

                    }
                });
    }

    public void refresh() {

        if (SystemUtils.canConnectNet(mHostActivity)) {
            mWebView.reload();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.err_msg_network_faile), Toast.LENGTH_LONG).show();
        }
    }

    public void openInBrowser() {
        // 判断联网请求数据
        if (SystemUtils.canConnectNet(mHostActivity)) {
            Uri uri = Uri.parse(mWebView.getUrl());
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.err_msg_network_faile), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.pop_ref_ll:
                this.refresh();
                mPopWin.dismiss();
                break;
            case R.id.pop_browser_ll:
                this.openInBrowser();
                mPopWin.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (VPNViewCourierHelper.getInstance(mContext).needVPN()){
            VPNBiz.getInstance(mContext).addCounter();
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("NewApi")
    @Override
    public void onStop() {
        super.onStop();
//		mWebView.stopLoading();
        if (VPNViewCourierHelper.getInstance(mContext).needVPN()){
            VPNBiz.getInstance(mContext).reduceCounter();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogM.log(this.getClass(), "onActivityResult");


        if (requestCode == REQUEST_CODE_PICTURES && resultCode == Activity.RESULT_OK) {
            mProgressHUD = ProgressHUD.show(mContext, null, true, false, null);

            List<String> selectPath = data.getStringArrayListExtra(MultiImageSelectorActivity
                    .EXTRA_RESULT);
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (String p : selectPath) {
                Bitmap bitmap = BitmapUtils.convertToBitmap(p, 800, 800);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String base64 = Base64.encode(byteArray);
                if (sb.length() > 1) {
                    sb.append(",\"" + base64 + "\"");
                } else {
                    sb.append("\"" + base64 + "\"");
                }
                bitmap = null;
                try {
                    if (stream != null) {
                        stream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sb.append("]");
            mTmpHandelCallbackFunction.onCallBack(sb.toString());
            mProgressHUD.hide();
        } else if (requestCode == REQUEST_CODE_QRCODE && resultCode == Activity.RESULT_OK) {
            String result = data.getStringExtra(CaptureActivity.EXTRA_NAME_STRING_RESULT);
            mPresenter.onQRCodeDone(result);
        } else if(requestCode == REQUEST_CODE_OCR && resultCode == Activity.RESULT_OK){
            List result = data.getStringArrayListExtra(VatRecogActivity.EXTRA_RESULT_STRING_LIST);
            mPresenter.onOCRComplete(result);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
        mWebView.cancelNetworkError();
    }

    //IWebAppView
    @Override
    public BridgeWebView getBridgeWebView() {
        return mWebView;
    }

    @Override
    public void showSignaturePanel(JSONObject jsonObject) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.pop_signature, null);
        RadioButton inputSegmentBtn = (RadioButton) contentView.findViewById(R.id
                .pop_signature_segmented_button1);
        RadioButton handwritingBtn = (RadioButton) contentView.findViewById(R.id
                .pop_signature_segmented_button2);
        Button doneBtn = (Button) contentView.findViewById(R.id.pop_signature_done_btn);
        mSignatureSegmentedGroup = (SegmentedGroup) contentView.findViewById(R.id
                .pop_signature_segment_sg);
        mSignatureET = (EditText) contentView.findViewById(R.id.pop_signature_et);
        mSignatureSignatureView = (SignatureView) contentView.findViewById(R.id.pop_signature_sv);

        HorizontalScrollLabels hsl = (HorizontalScrollLabels) contentView.findViewById(R.id
                .pop_signature_hsl);
        hsl.setListener(new HorizontalScrollLabels.HorizontalScrollLabelsListener() {
            @Override
            public void onClick(String text) {
                mSignatureET.setText(mSignatureET.getText() + text);
            }
        });
        mSignatureSegmentedGroup.setOnCheckedChangeListener(new RadioGroup
                .OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.pop_signature_segmented_button2) {
                    mSignatureET.setVisibility(View.GONE);
                    mSignatureSignatureView.setVisibility(View.VISIBLE);
                } else {
                    mSignatureET.setVisibility(View.VISIBLE);
                    mSignatureSignatureView.setVisibility(View.GONE);
                }
            }
        });
        try {
            inputSegmentBtn.setText(jsonObject.getString("inputTitle"));
            handwritingBtn.setText(jsonObject.getString("handwritingTitle"));
            hsl.setLabels(getCommonWords(jsonObject));
            mSignatureET.setHint(jsonObject.getString("handwritingDes"));
            mSignatureET.setText(jsonObject.getString("defaultTxt"));
            mSignatureSignatureView.setHintText(jsonObject.getString("handwritingDes"));
            doneBtn.setText(jsonObject.getString("doneTxt"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Button clearBtn = (Button) contentView.findViewById(R.id.pop_signature_clear_btn);
        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSignatureSignatureView.getVisibility() == View.VISIBLE) {
                    mSignatureSignatureView.clear();
                } else {
                    mSignatureET.setText("");
                }

            }
        });

        doneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("success", true);
                    JSONObject result = new JSONObject();
                    int type = mSignatureSignatureView.getVisibility() == View.VISIBLE ? 1 : 0;
                    result.put("type", type);
                    if (type == 0) {
                        result.put("text", mSignatureET.getText());
                        result.put("image", "");
                    } else {
                        result.put("text", "");
                        result.put("image", base64Bitmap(getCacheBitmapFromView
                                (mSignatureSignatureView)));
                    }
                    jo.put("result", result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPresenter.onSignatureDone(jo.toString());

            }
        });
        mSignaturePopWin = new PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSignaturePopWin.setOutsideTouchable(true);
        mSignaturePopWin.setFocusable(true);
        mSignaturePopWin.setAnimationStyle(R.style.popwin_channel_anim_style);
        mSignaturePopWin.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                light();
            }
        });
        mSignaturePopWin.showAtLocation(mWebView, Gravity.BOTTOM, 0, 0);
        dim();
    }

    @Override
    public void hideSignaturePanel() {
        mSignaturePopWin.dismiss();
    }

    @Override
    public void showImages(List<MultiplexImage> images) {

        Mango.setImages(images);
        Mango.setPosition(0);
        Mango.setImageSelectListener(new ImageSelectListener() {
            @Override
            public void select(int index) {
                Log.d("Mango", "select: " + index);
            }
        });
        try {
            Mango.open(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showScanQRCode(boolean needSelfResolve) {
        Intent intent = new Intent(mContext, CaptureActivity.class);
        intent.putExtra(CaptureActivity.EXTRA_NAME_BOOLEAN_NEED_SELF_RESOLVE, needSelfResolve);
        startActivityForResult(intent, REQUEST_CODE_QRCODE);
    }

    @Override
    public void checkUpdate() {
        executeURL("apppubs://" + Constants.APPPUBS_PROTOCOL_TYPE_CHECK_VERSION);
    }

    @Override
    public void startOCR(int type) {
        Intent intent = new Intent(getContext(),VatRecogActivity.class);
        intent.putExtra(VatRecogActivity.EXTRA_INT_TYPE,type);
        startActivityForResult(intent,REQUEST_CODE_OCR);
    }

    private void dim() {
        Utils.setBackgroundAlpha(getActivity(), 0.7f);
    }

    private void light() {
        Utils.setBackgroundAlpha(getActivity(), 1f);
    }

    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    private String base64Bitmap(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String base64 = Base64.encode(byteArray);
        return base64;
    }

    @NonNull
    private String[] getCommonWords(JSONObject jsonObject) throws JSONException {
        JSONArray ja = jsonObject.getJSONArray("commonWords");
        String[] labels = new String[ja.length()];
        for (int i = -1; ++i < ja.length(); ) {
            labels[i] = ja.getString(i);
        }
        return labels;
    }
}
