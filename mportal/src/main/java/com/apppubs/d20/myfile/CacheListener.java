package com.apppubs.d20.myfile;

/**
 * Created by zhangwen on 2017/7/22.
 */

public interface CacheListener{
	void onException(FileCacheErrorCode errorCode);
	void onDone(String fileUrl);
	void onProgress(float progress,long totalBytesExpectedToRead);
}