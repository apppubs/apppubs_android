package com.apppubs.model;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSON;
import com.apppubs.AppContext;
import com.apppubs.AppManager;
import com.apppubs.bean.App;
import com.apppubs.bean.AppConfig;
import com.apppubs.bean.TCity;
import com.apppubs.bean.TCollection;
import com.apppubs.bean.Comment;
import com.apppubs.bean.TDepartment;
import com.apppubs.bean.TMenuGroup;
import com.apppubs.bean.TMsgRecord;
import com.apppubs.bean.TNewsChannel;
import com.apppubs.bean.TNewsInfo;
import com.apppubs.bean.TMenuItem;
import com.apppubs.bean.TUser;
import com.apppubs.bean.UserInfo;
import com.apppubs.bean.TUserDeptLink;
import com.apppubs.bean.http.AppInfoResult;
import com.apppubs.bean.http.CompelReadMessageResult;
import com.apppubs.bean.http.IJsonResult;
import com.apppubs.bean.http.MenusResult;
import com.apppubs.constant.APError;
import com.apppubs.constant.URLs;
import com.apppubs.d20.R;
import com.apppubs.model.message.UserBussiness;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.ui.home.CompelReadMessageModel;
import com.apppubs.util.FileUtils;
import com.apppubs.util.JSONResult;
import com.apppubs.util.LogM;
import com.apppubs.util.MathUtils;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;
import com.apppubs.util.WebUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orm.SugarRecord;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Copyright (c) heaven Inc.
 * <p>
 * Original Author: zhangwen 此类处理系统级别的业务，如初始化系统 包括初始化应用信息，菜单信息，资讯频道 ChangeLog:
 * 2015年1月4日 by zhangwen create
 */
public class SystemBiz extends BaseBiz {

    private final float PIC_RATIO = 0.56f;

    private volatile static SystemBiz mSystemBiz;

    private SystemBiz(Context context) {
        super(context);
    }

    public static SystemBiz getInstance(Context context) {
        if (mSystemBiz == null) {
            synchronized (SystemBiz.class) {
                if (mSystemBiz == null) {
                    mSystemBiz = new SystemBiz(context);
                }
            }
        }
        return mSystemBiz;

    }

    public Future<?> update(final IAPCallback<String[]> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                String updateverurl = String.format(URLs.URL_UPDATE, URLs.baseURL) + "appcode=" +
                        URLs.appCode + "&type=android&clientkey="
                        + URLs.CLIENTKEY;
                System.out.println("更新链接 ，，，" + updateverurl);
                try {
                    String verJson = WebUtils.requestWithGet(updateverurl);
                    JSONObject jsonO = new JSONObject(verJson);
                    String[] result = new String[3];
                    result[0] = jsonO.getString("appdesc");
                    result[1] = jsonO.getString("updateurl");
                    result[2] = jsonO.getString("version");
                    result[2] = "2001003";
                    sHandler.post(new OnDoneRun<String[]>(callback, result));// 与主线程的通信
                } catch (IOException | InterruptedException | JSONException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<String[]>(callback));
                }
            }
        });

        return f;
    }

    private boolean isFirstInit() {
        return mAppContext.getApp().getInitTimes() == 0;
    }

    public Future<?> initSystem(final IAPCallback<App> callback) {
        LogM.log(this.getClass(), "系统初始化");

        Runnable run = new Runnable() {

            @Override
            public void run() {
                SQLiteDatabase db = SugarRecord.getDatabase();
                try {
                    db.beginTransaction();
                    App app = init();
                    db.setTransactionSuccessful();
                    MainHandler.getInstance().post(new OnDoneRun<App>(callback, app));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isFirstInit()) {
                        clearDataBase();
                        MainHandler.getInstance().post(new OnExceptionRun<App>(callback));
                    } else {
                        MainHandler.getInstance().post(new OnDoneRun<App>(callback, mAppContext
                                .getApp()));
                    }
                } finally {
                    db.endTransaction();
                }
            }
        };
        return sDefaultExecutor.submit(run);
    }

    /**
     * 清除数据库
     */
    private void clearDataBase() {
        LogM.log(this.getClass(), "清除数据库");
        SugarRecord.deleteAll(TMenuItem.class);
        SugarRecord.deleteAll(TNewsChannel.class);
        SugarRecord.deleteAll(TCollection.class);
        SugarRecord.deleteAll(TMenuGroup.class);
        SugarRecord.deleteAll(TCity.class);

    }

    private synchronized App init() throws IOException {

        // 如果是第一次初始化，无论那init个步骤出现问题都清空数据库重新初始化
        App localApp = AppContext.getInstance(mContext).getApp();

        LogM.log(AppContext.class, "初始化:" + localApp.toString());


        //如果是新版本第一次启动
        System.out.println("当前版本，" + Utils.getVersionCode(mContext) + "上一次启动的版本：" + localApp
                .getPreWorkingVersion());
        if (AppManager.getInstant(mContext).isFirstStartupOfNewVersion()) {
            System.out.println("新版本第一次启动");
            AppContext.getInstance(mContext).resetBaseUrlAndAppCode();
            switch (localApp.getPreWorkingVersion()) {
                case 200001:
                    UserBussiness userBussiness = UserBussiness.getInstance(mContext);
                    AppContext.getInstance(mContext).clearCurrentUser();
                case 200002:
                    AppContext.getInstance(mContext).clearCurrentUser();
                case 200003:
                    //下一个需要做升级处理的版
                    break;
            }
        }

        AppInfoResult info = syncPOST("http://result.eolinker" +
                        ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=app_info", null,
                AppInfoResult.class);

        mAppContext.updateWithAppInfo(info);
        if (mAppContext.getApp().getInitTimes() == 0) {
            generateStanceDrawable(info.getName());
            generateMediumStance();
            SugarRecord.deleteAll(TUser.class);
            SugarRecord.deleteAll(TUserDeptLink.class);
            SugarRecord.deleteAll(TDepartment.class);
            SugarRecord.deleteAll(TMsgRecord.class);
        }

        mAppContext.increaseInitTimes();

        return localApp;
    }

    /**
     * 生成站位图保存在本地files文件夹下名称为stance.png 中的大小的站位图 stance_medium.png
     * 与屏幕相同的展位图stance_pic.png
     *
     * @throws FileNotFoundException
     */
    private void generateStanceDrawable(String str) throws FileNotFoundException {
        LogM.log(this.getClass(), "生成占位图");
        float width = mContext.getResources().getDimension(R.dimen.list_item_image_width);
        float height = mContext.getResources().getDimension(R.dimen.list_item_image_height);
        float textsize = mContext.getResources().getDimension(R.dimen.list_item_image_textsize);

        Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Config.RGB_565); // Load
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CACACA"));
        paint.setTextSize(textsize);
        paint.setAntiAlias(true);
        canvas.drawColor(Color.parseColor("#E6E6E6"));
        canvas.drawText(str, (width - textsize * str.length()) / 2, (height + textsize) / 2, paint);
        FileOutputStream fos = null;
        try {
            File file = new File(mContext.getFilesDir(), "stance.png");
            fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }

    private void generateMediumStance() {
        LogM.log(this.getClass(), "生成中等大小站位图站位图");
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = (int) (width * PIC_RATIO);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Config.RGB_565); // Load
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#E6E6E6"));// 绘制灰色背景
        // 绘制图标

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#CACACA"));
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);

        Resources res = mContext.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.icon);
        int bmpW = bmp.getWidth();
        int bmpH = bmp.getHeight();
        int paintX = (int) (width * 0.5 - bmpW * 0.5);
        int paintY = (int) (height * 0.5 - bmpH * 0.5);
        canvas.drawBitmap(bmp, new Rect(0, 0, bmpW, bmpH), new Rect(paintX, paintY, paintX +
                        bmpW, paintY + bmpH),
                paint);

        FileOutputStream fos = null;
        try {
            File file = new File(mContext.getFilesDir(), "stance_pic.png");
            fos = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null)
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public void loadCompelReadMessage(final IAPCallback<List<CompelReadMessageModel>> callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=alert_messages";
        Map<String, String> params = new HashMap<>();
        asyncPOST(url, params, CompelReadMessageResult.class, new
                IRQListener<CompelReadMessageResult>() {

                    @Override
                    public void onResponse(final CompelReadMessageResult jr, final APError error) {
                        if (error == null) {
                            final List<CompelReadMessageModel> models = CompelReadMessageModel
                                    .createFrom(jr);
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onDone(models);
                                }
                            });
                        } else {
                            MainHandler.getInstance().post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onException(error);
                                }
                            });
                        }
                    }
                });
    }

    public void markCompelReadMessage(String serviceArticleId, final IAPCallback<Object> callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=mark_alert_message";
        asyncPOST(url, null, new IRQStringListener() {
            @Override
            public void onResponse(String result, final APError error) {
                if (error == null) {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(null);
                        }
                    });
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }
        });
    }

    /**
     * 机器唯一标识
     */
    public String getMachineId() {
        return MathUtils.MD5("sdk=" + Build.VERSION.SDK_INT + "|" + "model=" + Build.MODEL + "|"
                + Build.SERIAL + "|"
                + Build.DEVICE);

    }


    public AppConfig syncAppConfig() throws IOException, InterruptedException {

        String result = WebUtils.requestWithGet(String.format(URLs.URL_APP_CONFIG, URLs.baseURL,
                URLs.appCode, ""));
        JSONResult jsonResult = JSONResult.compile(result);
        if (jsonResult.code != 1) {
            LogM.log(this.getClass(), "获取appconfig错误");
        } else {
            Map<String, String> resultMap = jsonResult.getResultMap();
            // service_id":"1428155175898","address_apiflag":"0","usersync_url":"",
            // "adbookversion":"5","adbookauth":"1","deptsync_url":"",
            // "address_depturl":"http://202.99.19.140:8080/wmh360/json/getaddressdata
            // .jsp?appcode=D20&flag=dept","address_encflag":"1","chatflag":"0",
            // "address_deptuserurl":"http://202.99.19.140:8080/wmh360/json/getaddressdata
            // .jsp?appcode=D20&flag=deptuser","userauth_url":"","adbookupdateflag":"0",
            // "address_userurl":"http://202.99.19.140:8080/wmh360/json/getaddressdata
            // .jsp?appcode=D20&flag=user","autologout":"1","mduserinfoflag":"0",
            // "userauth_flag":"0","userdeptsync_url":""
            App app = AppContext.getInstance(mContext).getApp();
            app.setAddressbookUserUrl(resultMap.get("address_userurl"));
            app.setAddressbookDetpUrl(resultMap.get("address_depturl"));
            app.setAddressbookDeptUserUrl(resultMap.get("address_deptuserurl"));
            app.setAddressbookNeedDecryption(Integer.parseInt(resultMap.get("address_encflag")));
            app.setAddressbookNeedPermission(Integer.parseInt(resultMap.get("adbookauth")));
            app.setDocumentReaderPageUrl(resultMap.get("document_reader_url"));
            app.setAddressbookVersion(Integer.parseInt(resultMap.get("adbookversion")));

            AppConfig appconfig = (AppConfig) jsonResult.getResultObject(AppConfig.class);
            app.setAppConfig(appconfig);

            UserInfo ui = AppContext.getInstance(mContext).getCurrentUser();
            if (!TextUtils.isEmpty(ui.getUserId()) && appconfig.getAdbookAuthFlag() == 1) {
                String url = String.format(URLs.URL_ADDRESS_PERMISSION, URLs.baseURL, URLs
                        .appCode, AppContext.getInstance(mContext).getCurrentUser().getUserId());
                String permissionResult = WebUtils.requestWithGet(url);
                JSONResult jr = JSONResult.compile(permissionResult);
                if (jr.code == 1) {
                    UserInfo userInfo = AppContext.getInstance(mContext).getCurrentUser();
                    userInfo.setAddressbookPermissionString(jr.result);
                    AppContext.getInstance(mContext).setCurrentUser(userInfo);
                }
            }

            if (!TextUtils.isEmpty(ui.getUserId()) && appconfig.getChatAuthFlag() == 1) {
                String url = String.format(URLs.URL_USER_PERMISSION, URLs.baseURL, URLs.appCode,
                        AppContext.getInstance(mContext).getCurrentUser().getUserId());
                String permissionResult = WebUtils.requestWithGet(url);
                JSONResult jr = JSONResult.compile(permissionResult);
                if (jr.code == 1) {
                    UserInfo userInfo = AppContext.getInstance(mContext).getCurrentUser();
                    userInfo.setChatPermissionString(jr.result);
                    AppContext.getInstance(mContext).setCurrentUser(userInfo);
                }
            }

            AppContext.getInstance(mContext).setAppConfig(appconfig);
            return appconfig;
        }
        return null;
    }

    /**
     * 更新appconfig，将getappconfig中的数据同步到本地的APP对象中。
     */
    public void aSyncAppConfig(final Context context, final IAPCallback<AppConfig> callback) {
        sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                try {
                    AppConfig ac = syncAppConfig();
                    sHandler.post(new OnDoneRun<AppConfig>(callback, ac));// 与主线程的通信
                } catch (Exception e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<AppConfig>(callback));// 与主线程的通信
                }

            }
        });

    }

    /**
     * 注册 修改 useridstr 用户的id emailstr passwordstr clientidstr mobilestr
     * nicknamestr
     *
     * @param usernamestr
     * @param emailstr
     * @param passwordstr
     * @param mobilestr
     * @param nicknamestr
     * @param callback
     * @return
     */
    public Future<?> postZhuce(final String usernamestr, final String emailstr, final String
            passwordstr,
                               final String mobilestr, final String nicknamestr, final
                               IAPCallback<String> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                String info = "";
                String url = String.format(URLs.URL_ZHUCE, URLs.baseURL);
                HttpClient hc = new DefaultHttpClient();
                HttpPost hp = new HttpPost(url);

                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                BasicNameValuePair bp = new BasicNameValuePair("usernamestr", usernamestr);
                BasicNameValuePair bp1 = new BasicNameValuePair("emailstr", emailstr);
                BasicNameValuePair bp2 = new BasicNameValuePair("passwordstr", passwordstr);
                BasicNameValuePair bp3 = new BasicNameValuePair("clientidstr", URLs.appCode);
                BasicNameValuePair bp4 = new BasicNameValuePair("mobilestr", mobilestr);
                BasicNameValuePair bp5 = new BasicNameValuePair("nicknamestr", nicknamestr);
                parameters.add(bp);
                parameters.add(bp1);
                parameters.add(bp2);
                parameters.add(bp3);
                parameters.add(bp4);
                parameters.add(bp5);
                HttpEntity entity;
                /*
                 * // 编码方式为HTTP.UTF_8) hp.setEntity(new
                 * UrlEncodedFormEntity(parameters, HTTP.UTF_8)); // 告知服务器端解码
                 * hp.setHeader("Content-Type",
                 * "application/x-www-form-urlencoded; charset=utf-8");
                 * HttpResponse hr = hc.execute(hp); String data =
                 * EntityUtils.toString(hr.getEntity(), "utf-8");
                 */
                try {
                    // 编码方式为HTTP.UTF_8)
                    hp.setEntity(new UrlEncodedFormEntity(parameters, HTTP.UTF_8));
                    // 告知服务器端解码
                    hp.setHeader("Content-Type", "application/x-www-form-urlencoded; " +
                            "charset=utf-8");
                    HttpResponse hr = hc.execute(hp);
                    String resurt = EntityUtils.toString(hr.getEntity(), "utf-8");
                    System.out.println("注册后的结果。。。。。。。。。。" + resurt);
                    info = resurt.trim();
                    sHandler.post(new OnDoneRun<String>(callback, info));// 与主线程的通信
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    sHandler.post(new OnExceptionRun<String>(callback));
                    e.printStackTrace();
                }

            }
        });

        return f;
    }

    public Future<?> postZhuce1(final String usernamestr, final String emailstr, final String
            passwordstr,
                                final String mobilestr, final String nicknamestr, final
                                IAPCallback<String> callback) {
        // TODO Auto-generated method stub
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String info = "";
                String requestUrl = URLs.URL_ZHUCE;
                Map<String, Object> requestParamsMap = new HashMap<String, Object>();
                requestParamsMap.put("usernamestr", usernamestr);
                requestParamsMap.put("emailstr", emailstr);
                requestParamsMap.put("passwordstr", passwordstr);
                requestParamsMap.put("clientidstr", URLs.baseURL);
                requestParamsMap.put("mobilestr", mobilestr);
                requestParamsMap.put("nicknamestr", nicknamestr);
                String resurt = "";
                resurt = WebUtils.requestWithPost(requestUrl, requestParamsMap);
                info = resurt.trim();
                sHandler.post(new OnDoneRun<String>(callback, info));// 与主线程的通信
            }
        });

        return f;
    }

    public Future<?> getCommentList(final String infoid, final int pno, final int pernum, final
    String clientkey,
                                    final IAPCallback<List<Comment>> callback) {
        // TODO Auto-generated method stub
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = String.format(URLs.URL_COMMENTLIST, URLs.baseURL) + "?infoid=" +
                            infoid + "&pno=" + pno + "&pernum=" + pernum
                            + "&clientkey=" + clientkey;
                    List<Comment> list = WebUtils.requestList(url, Comment.class, "comment");
                    sHandler.post(new OnDoneRun<List<Comment>>(callback, list));// 与主线程的通信
                } catch (IOException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<List<Comment>>(callback));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<List<Comment>>(callback));
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        });

        return f;
    }

    public Future<?> getCommentSizeZanCai(final String infoid, final IAPCallback<Comment>
            callback) {
        // TODO Auto-generated method stub
        Future<?> f = sDefaultExecutor.submit(new Runnable() {

            @Override
            public void run() {

                try {
                    String url = String.format(URLs.URL_INFOIDCOMMENTSIZE, URLs.baseURL) +
                            "?infoid=" + infoid + "&clientkey=" + URLs.CLIENTKEY;
                    String data = WebUtils.requestWithGet(url);
                    JSONObject jo = null;
                    try {
                        jo = new JSONObject(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Comment comment = new Comment();
                    try {

                        comment.setCommentnum(jo.getString("commentnum"));
                        comment.setUpnum(jo.getString("upnum"));
                        comment.setDownnum(jo.getString("downnum"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sHandler.post(new OnDoneRun<Comment>(callback, comment));// 与主线程的通信
                } catch (IOException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<Comment>(callback));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    sHandler.post(new OnExceptionRun<Comment>(callback));
                }
            }
        });

        return f;
    }

    public Future<?> getStandardDataTime(final IAPCallback<Date> callback) {
        Future<?> f = sDefaultExecutor.submit(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    url = new URL(URLs.baseURL);
                    URLConnection uc = url.openConnection();// 生成连接对象
                    uc.connect(); // 发出连接
                    long ld = uc.getDate(); // 取得网站日期时间
                    Date date = new Date(ld); // 转换为标准时间对象
                    sHandler.post(new OnDoneRun<Date>(callback, date));
                } catch (MalformedURLException e) {

                    e.printStackTrace();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }

        });
        return f;
    }

    public int getCacheSize() {
        File diskCacheDir = ImageLoader.getInstance().getDiskCache().getDirectory();
        try {
            int size = SugarRecord.sumColumn(TNewsInfo.class, "SIZE");
            size += (int) FileUtils.getFileSize(diskCacheDir);
            return size;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void clearCache(final IAPCallback<Boolean> callback) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 删除缓存图片和数据库中所有新闻信息并将所有频道的本地更新时间清除
                File diskCacheDir = ImageLoader.getInstance().getDiskCache().getDirectory();
                try {
                    FileUtils.delete(diskCacheDir);
                    SugarRecord.deleteAll(TNewsInfo.class);
                    SugarRecord.update(TNewsChannel.class, "LOCAL_LAST_UPDATE_TIME", "", null,
                            null);
                    sHandler.post(new OnDoneRun<Boolean>(callback, true));
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                }
            }

        };
        Future<?> f = sDefaultExecutor.submit(r);

    }

    public interface CheckUpdateListener {
        void onDone(VersionInfo info);
    }

    public void checkUpdate(Context context, CheckUpdateListener listener) {
        AppConfig appConfig = AppContext.getInstance(mContext).getAppConfig();
        if (appConfig != null && StringUtils.compareVersion(appConfig.getLatestVersion(),
                mAppContext.getVersionName()) > 0) {
            VersionInfo vi = new VersionInfo();
            vi.setNeedUpdate(true);
            if (appConfig.getMinSupportedVersionCode() > Utils.getVersionCode(context)) {
                vi.setNeedForceUpdate(true);
            }
            vi.setUpdateDescribe(appConfig.getLatestVersionDescribe());
            vi.setVersion(appConfig.getLatestVersion());
            vi.setUpdateUrl(appConfig.getUpdateUrl());
            vi.setNeedAlert(appConfig.getNeedVersionAlertFlag() == 1);
            listener.onDone(vi);
        } else {
            listener.onDone(new VersionInfo());

        }
    }

    /**
     * 存储/更新菜单列表到本地数据库
     *
     * @param menus
     */
    private void insertOrUpdateLocalMenus(List<TMenuItem> menus) {
        SugarRecord.deleteAll(TMenuItem.class);
        for (TMenuItem item : menus) {
            item.save();
        }
    }

    private List<TMenuItem> selectLocalMenus() {
        return SugarRecord.find(TMenuItem.class, null, null, null, null, null);
    }

    /**
     * 初始化菜单，每次初始化均会和本地菜单比对，若相同则不重新存储
     *
     * @param callback 返回是否已经更新
     */
    public void initMenus(final IAPCallback<Boolean> callback) {
        // 初始化菜单
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=menus";
        asyncPOST(url, null, MenusResult.class, new IRQListener<MenusResult>() {
            @Override
            public void onResponse(MenusResult menus, final APError error) {
                if (error == null) {
                    final List<TMenuItem> list = convert2TMenuItems(menus);
                    String netItems = JSON.toJSONString(list);
                    String localItems = JSON.toJSONString(selectLocalMenus());
                    boolean isUpdated = false;
                    if (!TextUtils.equals(netItems, localItems)) {
                        insertOrUpdateLocalMenus(list);
                        isUpdated = true;
                    } else {
                        isUpdated = false;
                    }
                    final boolean finalIsUpdated = isUpdated;
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(finalIsUpdated);
                        }
                    });
                } else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });
                }
            }

            private List<TMenuItem> convert2TMenuItems(MenusResult menus) {
                List<TMenuItem> items = new ArrayList<>();
                if (!Utils.isEmpty(menus.getItems())) {
                    for (MenusResult.MenuItem mi : menus.getItems()) {
                        items.add(TMenuItem.createFrom(mi));
                    }
                }
                return items;
            }
        });
    }

    /**
     * 获取本地存储的主菜单
     *
     * @return 主菜单列表
     */
    public List<TMenuItem> getLocalPrimaryMenus() {
        return SugarRecord.find(TMenuItem.class, "LOCATION=? and PROTECTED_FLAG = 0",
                new String[]{TMenuItem.MENU_LOCATION_PRIMARY + ""}, null, "SORT_ID", null);
    }

    public void commitPushRegisterId(final String registerId, final IAPCallback callback) {
        String url = "http://result.eolinker" +
                ".com/gN1zjDlc87a75d671a2d954f809ebcdd19e7698dc2478fa?uri=commit_push_register_id";
        Map<String, String> params = new HashMap<>();
        params.put("registerid", registerId);
        asyncPOST(url, params, new IRQStringListener() {
            @Override
            public void onResponse(final String result, final APError error) {
                if (error == null){
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onDone(result);
                        }
                    });

                }else {
                    MainHandler.getInstance().post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onException(error);
                        }
                    });

                }
            }
        });
    }

}
