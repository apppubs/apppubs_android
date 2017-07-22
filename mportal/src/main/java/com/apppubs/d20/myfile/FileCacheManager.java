package com.apppubs.d20.myfile;

import android.text.TextUtils;

import com.apppubs.d20.exception.ESUnavailableException;

import java.io.File;

/**
 * Created by zhangwen on 2017/7/22.
 */


public interface FileCacheManager {

	void cacheFile(String fileUrl, CacheListener listener);

	void cancelCacheFile(String fileUrl);

	File fetchCache(String fileUrl);

	boolean removeCache(String fileUrl);
}
