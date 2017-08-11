package com.apppubs.d20.model;

/**
 * Created by zhangwen on 2017/8/11.
 */

public interface APProgressResultCallback<T> extends APResultCallback<T> {
	public abstract void onProgressUpdate(float progress);
}
