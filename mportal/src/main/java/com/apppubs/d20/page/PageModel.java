package com.apppubs.d20.page;

import android.content.Context;

import com.apppubs.d20.AppContext;
import com.apppubs.d20.util.Utils;
import com.apppubs.d20.widget.ColorUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.internal.Util;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PageModel {
	private String pageId;
	private  PageContentModel content;
	private TitleBarModel titleBarModel;

	public PageModel(Context context,String json){
		try {
			JSONObject pageObject = new JSONObject(json);
			if(pageObject.has("titlebar")){
				titleBarModel = new TitleBarModel(context,pageObject.getString("titlebar"));
			}
			if (pageObject.has("navbar")){
				content = new PageNavContentModel(json);
			}else{
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
}


interface PageContentModel{

}

class PageNormalContentModel implements PageContentModel{

	private JSONArray components;

	public PageNormalContentModel(String jsonArr){
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
}

class PageNavContentModel implements PageContentModel{

	private JSONObject navBar;
	private JSONArray navItems;
	private List<PageNormalContentModel> items;

	public PageNavContentModel(String json){
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
}

class TitleBarModel{

	private String type;
	private String title;
	private int bgColor;
	private String titleImgUrl;
	private String leftImgUrl;
	private String rightImgUrl;
	private String leftAction;
	private String rightAction;
	private int underlineColor;

	public TitleBarModel(Context context, String json){
		try {
			JSONObject jo = new JSONObject(json);
			type = jo.getString("titletype");
			title = jo.getString("title").replaceAll("\\$truename", AppContext.getInstance(context).getCurrentUser().getTrueName());
			bgColor = Utils.parseColor(jo.getString("bgcolor"));
			titleImgUrl = jo.getString("titleimgurl");
			leftImgUrl = jo.getString("leftbtnimgurl");
			rightImgUrl = jo.getString("rightbtnimgurl");
			leftAction = jo.getString("leftbtnurl");
			rightAction = jo.getString("rightbtnurl");
			int underColor = Utils.parseColor(jo.getString("underlinecolor"));
			if (underColor>-1){
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
}

