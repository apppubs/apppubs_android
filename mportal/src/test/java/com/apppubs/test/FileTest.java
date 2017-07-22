package com.apppubs.test;

import android.content.Context;
import android.os.Environment;

import com.apppubs.d20.myfile.FileCacheManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

import static org.mockito.Mockito.when;

/**
 * Created by zhangwen on 2017/7/13.
 */
@RunWith(MockitoJUnitRunner.class)
public class FileTest {

	@Mock
	Context mMockContext;
	@Mock
	Environment en;

	@Test
	public void testSDCard() {
		when(mMockContext.getExternalCacheDir()).thenReturn(new File("/"));
		System.out.print("test" + mMockContext.getExternalCacheDir().getAbsolutePath());
		try {
			URI uri = new URI("http://http://60.205.140.176:8088/wmh360/appwordfile/D20/xxhryce/新建文本文档(2).txt");
			System.out.print(uri.getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFileManager(){
		when(en.getExternalStorageState()).thenReturn("mounted");
		FileCacheManager cacheManager = FileCacheManager.getInstance();
		File desFile = cacheManager.fetchCache("http://127.0.0.1:49649/proxy.pac");
		System.out.println(desFile);
	}
}
