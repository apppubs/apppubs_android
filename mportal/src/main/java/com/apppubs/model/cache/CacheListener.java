package com.apppubs.model.cache;

/**
 * Created by zhangwen on 2017/7/22.
 */

public interface CacheListener{
	void onException(FileCacheErrorCode errorCode);
	void onDone(String localPath);
	void onProgress(float progress,long totalBytesExpectedToRead);
}