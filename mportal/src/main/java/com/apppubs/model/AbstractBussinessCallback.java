package com.apppubs.model;

public abstract class AbstractBussinessCallback<T> implements IAPCallback<T> {

	
	public abstract void onProgressUpdate(float progress);
}
