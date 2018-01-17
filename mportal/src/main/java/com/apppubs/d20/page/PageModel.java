package com.apppubs.d20.page;

import android.content.Context;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.util.StringUtils;
import com.apppubs.d20.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.loc.g.t;

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
            if (pageObject.has("titlebar")) {
                titleBarModel = new TitleBarModel(context, pageObject.getString("titlebar"));
            }
            if (pageObject.has("navbar")) {
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


interface PageContentModel {

}

class PageNormalContentModel implements PageContentModel {

    private JSONArray components;

    public PageNormalContentModel(String jsonArr) {
        try {
            components = new JSONArray(jsonArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getComponents() {
        return components;
    }

    public void setComponents(JSONArray components) {
        this.components = components;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PageNormalContentModel)) {
            return false;
        }
        PageNormalContentModel des = (PageNormalContentModel) o;
        if ((this.components == null && des.getComponents() != null)
                || (this.components != null && des.getComponents() == null)) {
            return false;
        }
        if ((this.components != null && des.getComponents() != null)) {
            if (!StringUtils.equals(this.components.toString(), des.getComponents().toString())) {
                return false;
            }
        }
        return true;
    }
}

class PageNavContentModel implements PageContentModel {

    private JSONObject navBar;
    private JSONArray navItems;
    private List<PageNormalContentModel> items;

    public PageNavContentModel(String json) {
        try {
            navBar = new JSONObject(json).getJSONObject("navbar");
            navItems = navBar.getJSONArray("items");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray getNavItems() {
        return navItems;
    }

    public void setNavItems(JSONArray navItems) {
        this.navItems = navItems;
    }

    public List<PageNormalContentModel> getItems() {
        return items;
    }

    public void setItems(List<PageNormalContentModel> items) {
        this.items = items;
    }

    public JSONObject getNavBar() {
        return navBar;
    }

    public void setNavBar(JSONObject navBar) {
        this.navBar = navBar;
    }

    @Override
    public boolean equals(Object o) {
        return true;
    }
}

class TitleBarModel {

    private String mJson;
    private String type;
    private String title;
    private int bgColor;
    private String titleImgUrl;
    private String leftImgUrl;
    private String rightImgUrl;
    private String leftAction;
    private String rightAction;
    private int underlineColor;

    public TitleBarModel(Context context, String json) {
        mJson = json;
        try {
            JSONObject jo = new JSONObject(json);
            type = jo.getString("titletype");
            title = jo.getString("title").replaceAll("\\$truename", AppContext.getInstance
                    (context).getCurrentUser().getTrueName());
            bgColor = Utils.parseColor(jo.getString("bgcolor"));
            titleImgUrl = jo.getString("titleimgurl");
            leftImgUrl = jo.getString("leftbtnimgurl");
            rightImgUrl = jo.getString("rightbtnimgurl");
            leftAction = jo.getString("leftbtnurl");
            rightAction = jo.getString("rightbtnurl");
            int underColor = Utils.parseColor(jo.getString("underlinecolor"));
            if (underColor > -1) {
                underlineColor = underColor;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getTitleImgUrl() {
        return titleImgUrl;
    }

    public void setTitleImgUrl(String titleImgUrl) {
        this.titleImgUrl = titleImgUrl;
    }

    public String getLeftImgUrl() {
        return leftImgUrl;
    }

    public void setLeftImgUrl(String leftImgUrl) {
        this.leftImgUrl = leftImgUrl;
    }

    public String getRightImgUrl() {
        return rightImgUrl;
    }

    public void setRightImgUrl(String rightImgUrl) {
        this.rightImgUrl = rightImgUrl;
    }

    public String getLeftAction() {
        return leftAction;
    }

    public void setLeftAction(String leftAction) {
        this.leftAction = leftAction;
    }

    public String getRightAction() {
        return rightAction;
    }

    public void setRightAction(String rightAction) {
        this.rightAction = rightAction;
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
    }

    @Override
    public String toString() {
        return mJson;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TitleBarModel)) {
            return false;
        }
        TitleBarModel des = (TitleBarModel) o;
        if (!StringUtils.equals(mJson, des.toString())) {
            return false;
        }
        return true;
    }
}

class GridViewModel {
    private Integer column;
    private Integer maxRow;
    private List<GridViewItem> items;

    public GridViewModel(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            this.column = jo.getInt("column");
            this.maxRow = jo.getInt("maxrow");
            JSONArray items = jo.getJSONArray("items");
            List<GridViewItem> list = new ArrayList<GridViewItem>();
            for (int i = -1; ++i < items.length(); ) {
                GridViewItem item = new GridViewItem(items.getString(i));
                list.add(item);
            }
            this.items = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getMaxRow() {
        return maxRow;
    }

    public void setMaxRow(Integer maxRow) {
        this.maxRow = maxRow;
    }

    public List<GridViewItem> getItems() {
        return items;
    }

    public void setItems(List<GridViewItem> items) {
        this.items = items;
    }

    public List<GridViewItem> getItemsForPage(int pageIndex) {
        List<GridViewItem> list = new ArrayList<GridViewItem>();
        int pageSize = maxRow * column;
        int pageStartIndex = pageSize * pageIndex;
        if (items.size() > pageStartIndex + pageSize) {
            list.addAll(items.subList(pageStartIndex, pageStartIndex + pageSize));
        } else if (items.size() > pageStartIndex) {
            list.addAll(items.subList(pageStartIndex, items.size()));
        } else {
            //do nothing
        }
        return list;
    }

    public int getTotalPage() {
        return items.size() % (maxRow * column) == 0 ? items.size() / (maxRow * column) : items
                .size() / (maxRow * column) + 1;
    }

    public int getRealMaxRow() {
        if (items.size() <= maxRow * column) {
            return items.size() % column == 0 ? items.size() / column : items.size() / column + 1;
        } else {
            return maxRow;
        }
    }
}

class GridViewItem {
    private String title;
    private String picUrl;
    private String action;
    private String badgeURL;
    private String badgeTxt;

    public GridViewItem(String jsonStr) {
        try {
            JSONObject jo = new JSONObject(jsonStr);
            this.title = jo.getString("title");
            this.picUrl = jo.getString("picurl");
            this.action = jo.getString("url");
            this.badgeURL = jo.getString("badgeurl");
            this.badgeTxt = jo.has("badgetext") ? jo.getString("badgetext") : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getBadgeURL() {
        return badgeURL;
    }

    public void setBadgeURL(String badgeURL) {
        this.badgeURL = badgeURL;
    }

    public String getBadgeTxt() {
        return badgeTxt;
    }

    public void setBadgeTxt(String badgeTxt) {
        this.badgeTxt = badgeTxt;
    }
}

