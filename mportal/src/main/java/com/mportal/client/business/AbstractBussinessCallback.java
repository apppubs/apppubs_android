package com.mportal.client.business;

public abstract class AbstractBussinessCallback<T> implements BussinessCallbackCommon<T> {

	
	public abstract void onProgressUpdate(float progress);
}
