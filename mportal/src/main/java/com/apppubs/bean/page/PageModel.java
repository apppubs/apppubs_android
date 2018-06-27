package com.apppubs.bean.page;

import android.content.Context;

import com.apppubs.AppContext;
import com.apppubs.constant.Constants;
import com.apppubs.util.StringUtils;
import com.apppubs.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PageModel {
    private String pageId;
    private PageContentModel content;
    private TitleBarModel titleBarModel;

    public PageModel(Context context, String json) {
        try {
            JSONObject pageObject = new JSONObject(json);
            if (pageObject.has("titleBar")) {
                titleBarModel = TitleBarModel.buildTitleBarModel(context,
                        pageObject.getString("titleBar"));
            }
            if (pageObject.has("navBar")) {
                content = new PageNavContentModel(json);
            } else {
                content = new PageNormalContentModel(pageObject.getString("components"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getPageId() {
        return pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public PageContentModel getContent() {
        return content;
    }

    public void setContent(PageContentModel content) {
        this.content = content;
    }

    public TitleBarModel getTitleBarModel() {
        return titleBarModel;
    }

    public void setTitleBarModel(TitleBarModel titleBarModel) {
        this.titleBarModel = titleBarModel;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PageModel)) {
            return false;
        }
        PageModel des = (PageModel) o;
        if (!Utils.compare(this.pageId, des.getPageId())
                || !Utils.compare(this.content, des.getContent())
                || !Utils.compare(this.titleBarModel, des.getTitleBarModel())) {
            return false;
        }

        return true;
    }
}


