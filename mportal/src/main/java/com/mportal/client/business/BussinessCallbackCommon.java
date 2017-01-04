package com.mportal.client.business;



/**
 * 普通业务回调
 * @author zhangwen
 *
 * @param <T>
 */
public interface BussinessCallbackCommon<T>{
	/**
	 *异常类型
	 */
	int EXCEPTION_COMMON = 0;//通用异常
	int EXCEPTION_PARSE = 1;//解析错误
	int EXCEPTION_DOWNLOAD = 2;//下载错误
	/**
	 * 正常返回
	 * @param obj 返回的实体对象
	 */
	void onDone(T obj);
	/**
	 * 异常时
	 * @param errCode 错误代码
	 */
	void onException(int excepCode);
	
	
}