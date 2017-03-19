package com.apppubs.d20.widget;

import android.graphics.Bitmap;

public class HotArea {

	public static final String TYPE_DEFAULT = "0";
	public static final String TYPE_TEXT = "1";
	public static final String TYPE_IMAGE = "2";

	public static final String IMAGE_DISPLAY_MODE_CENTER_INSIDE = "centerinside";
	public static final String IMAGE_DISPLAY_MODE_CENTER_CROP = "centercorp";
	public static final String IMAGE_DISPLAY_MODE_FITXY = "fitxy";

	public static final String TEXT_ALIGN_CENTER = "center";
	public static final String TEXT_ALIGN_LEFT = "left";
	public static final String TEXT_ALIGN_RIGHT = "right";

	public static final String SHAPE_DEFAULT = "";
	public static final String SHAPE_RECT = "rect";
	public static final String SHAPE_CIRCLE = "circle";
	public static final String SHAPE_POLY = "poly";

	private String type;
	private String coords;
	private String url;
	private String shape;
	private String textUrl;
	private int textSize;
	private String textAlign;
	private int textColor;
	private String text;
	private int bgColor;
	private String imageUrl;
	private String imageDisplayMode;
	private Bitmap image;

	public HotArea() {
		super();
	}

	public HotArea(String shape, String coords, String url) {
		this.shape = shape;
		this.coords = coords;
		this.url = url;
	}

	public String getCoords() {
		return coords;
	}

	public void setCoords(String coords) {
		this.coords = coords;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShape() {
		return shape;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTextUrl() {
		return textUrl;
	}

	public void setTextUrl(String textUrl) {
		this.textUrl = textUrl;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public String getTextAlign() {
		return textAlign;
	}

	public void setTextAlign(String textAlign) {
		this.textAlign = textAlign;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getImageDisplayMode() {
		return imageDisplayMode;
	}

	public void setImageDisplayMode(String imageDisplayMode) {
		this.imageDisplayMode = imageDisplayMode;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	
}