package com.apppubs.d20.model;

public abstract class AbstractBussinessCallback<T> implements APResultCallback<T> {

	
	public abstract void onProgressUpdate(float progress);
}
