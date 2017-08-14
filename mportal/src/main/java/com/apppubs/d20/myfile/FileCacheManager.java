package com.apppubs.d20.myfile;

import java.io.File;
import java.util.List;

/**
 * Created by zhangwen on 2017/7/22.
 */


public interface FileCacheManager {

	void cacheFile(String fileUrl, CacheListener listener);

	void cacheFiles(List<String> fileUrls, CacheListener listener);

	void cancelCacheFile(String fileUrl);

	File fetchCache(String fileUrl);

	boolean removeCache(String fileUrl);

	void uploadFile(File file,CacheListener listener);
}
