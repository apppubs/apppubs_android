package com.apppubs.d20.page;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.bean.UserInfo;
import com.apppubs.d20.constant.Constants;
import com.apppubs.d20.constant.URLs;
import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.net.WMHHttpClient;
import com.apppubs.d20.net.WMHHttpErrorCode;
import com.apppubs.d20.net.WMHRequestListener;
import com.apppubs.d20.util.JSONResult;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.util.SystemUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PageBiz implements IPageBiz {

    private Context mContext;
    private WMHHttpClient mHttpClient;

    public PageBiz(Context context) {
        mContext = context;
        mHttpClient = AppContext.getInstance(mContext).getHttpClient();
    }

    @Override
    public void loadPage(String pageId, final APResultCallback<PageModel> callback) {
        String url = getUrl(pageId);
        mHttpClient.GET(url, null, new WMHRequestListener() {
            @Override
            public void onDone(JSONResult jsonResult, @Nullable WMHHttpErrorCode errorCode) {
                if (errorCode == null) {
                    if (jsonResult.resultCode == 1) {
                        PageModel model = new PageModel(mContext, jsonResult.result);
                        PageNormalContentModel normalContent = getPageNormalContentIfExit(model);
                        try {
                            handleNormalPageContent(normalContent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        callback.onDone(model);
                    } else {
                        callback.onException(jsonResult.resultCode);
                    }
                } else {
                    callback.onException(errorCode.getValue());
                }
            }
        });

        LogM.log(this.getClass(), "请求page json：" + url);
    }

    /**
     * 处理普通类型的页面内容，主要是获取提醒数字内容
     *
     * @param normalContent 处理之前的content
     * @return 处理之后的content，其中包含获取了提醒数字的热区组件和图标列表组件
     */
    private PageNormalContentModel handleNormalPageContent(PageNormalContentModel normalContent)
            throws JSONException, IOException {
        JSONArray components = normalContent.getComponents();
        for (int i = -1; ++i < components.length(); ) {
            JSONObject jo = components.getJSONObject(i);
            handleComponent(jo);
        }

        return normalContent;
    }

    /**
     * 获取component中的badgeText
     *
     * @param jo
     * @return 如果有则返回值如果没有则返回null
     * @throws JSONException
     * @throws IOException
     */
    private JSONObject handleComponent(JSONObject jo) throws JSONException, IOException {
        String comtype = jo.getString("comtype");
        if (!TextUtils.isEmpty(comtype) && comtype.equals(Constants.PAGE_COMPONENT_ICON_LIST)) {
            JSONArray items = jo.getJSONArray("items");
            for (int i = -1; ++i < items.length(); ) {
                JSONObject item = items.getJSONObject(i);
                AppContext appContext = AppContext.getInstance(mContext);
                String rawBadgeURL = item.getString("badgeurl");
                if (!TextUtils.isEmpty(rawBadgeURL)) {
                    String badgeURL = appContext.convertUrl(rawBadgeURL);
                    OkHttpClient client = new OkHttpClient.Builder().readTimeout(5, TimeUnit
                            .SECONDS).build();
                    Request request = new Request.Builder().url(badgeURL).get().build();
                    Call call = client.newCall(request);
                    Response response = call.execute();
                    String badgeText = response.body().string();
                    item.put("badgetext", badgeText);
                }
            }
        }
        return jo;
    }

    private PageNormalContentModel getPageNormalContentIfExit(PageModel model) {
        PageContentModel content = model.getContent();
        if (content instanceof PageNormalContentModel) {
            return (PageNormalContentModel) content;
        }
        return null;
    }

    private String getUrl(String pageId) {
        UserInfo ui = AppContext.getInstance(mContext).getCurrentUser();
        String url = null;
        if (ui != null) {
            url = String.format(URLs.URL_PAGE, URLs.baseURL, URLs.appCode, pageId, ui.getUserId());
        } else {
            url = String.format(URLs.URL_PAGE, URLs.baseURL, URLs.appCode, pageId, "");
        }
        return url;
    }
}
