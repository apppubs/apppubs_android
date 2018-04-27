package com.apppubs.model;

public abstract class AbstractBussinessCallback<T> implements APCallback<T> {

	
	public abstract void onProgressUpdate(float progress);
}
