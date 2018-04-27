package com.apppubs.model;


import com.apppubs.constant.APError;

/**
 * 普通业务回调
 * @author zhangwen
 *
 * @param <T>
 */
public interface APCallback<T>{
	/**
	 * 正常返回
	 * @param obj 返回的实体对象
	 */
	void onDone(T obj);
	/**
	 * 异常时
	 * @param error 错误代码
	 */
	void onException(APError error);
	
	
}