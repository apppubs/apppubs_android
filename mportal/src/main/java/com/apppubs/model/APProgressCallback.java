package com.apppubs.model;

/**
 * Created by zhangwen on 2017/8/11.
 */

public interface APProgressCallback<T> extends IAPCallback<T> {
	public abstract void onProgressUpdate(float progress);
}
