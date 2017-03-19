package com.apppubs.d20.business;

public abstract class AbstractBussinessCallback<T> implements BussinessCallbackCommon<T> {

	
	public abstract void onProgressUpdate(float progress);
}
