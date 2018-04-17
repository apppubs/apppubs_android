package com.apppubs.model.myfile;

import java.io.File;
import java.util.List;

/**
 * Created by zhangwen on 2017/7/22.
 */


public interface FileCacheManager {

	/**
	 * @deprecated void cacheFile(String fileUrl, String fileName,CacheListener listener) 代替
	 * @param fileUrl
	 * @param listener
	 */
	@Deprecated
	void cacheFile(String fileUrl, CacheListener listener);

	void cacheFile(String fileUrl, String fileName,CacheListener listener);

	void cacheFiles(List<String> fileUrls, CacheListener listener);

	void cancelCacheFile(String fileUrl);

	File fetchCache(String fileUrl);

	boolean removeCache(String fileUrl);

	void uploadFile(File file,CacheListener listener);
}
