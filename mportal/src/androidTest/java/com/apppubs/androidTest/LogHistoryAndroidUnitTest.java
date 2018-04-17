package com.apppubs.androidTest;

/**
 * Created by zhangwen on 2017/7/13.
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class LogHistoryAndroidUnitTest extends InstrumentationTestCase{

	public static final String TEST_STRING = "This is a string";
	public static final long TEST_LONG = 12345678L;
	private Context mLogHistory;

	@Before
	public void createLogHistory() {
		mLogHistory = InstrumentationRegistry.getContext();
	}




	@Test
	public void testFileManager(){
		// Context of the app under test.
//		Context appContext = InstrumentationRegistry.getTargetContext();
//		FileCacheManager cacheManager = FileCacheManager.getInstance(appContext);
//		File desFile = cacheManager.fetchCache("http://127.0.0.1:49649/proxy.pac");
//		System.out.println(desFile);
//		assertEquals(desFile,appContext.getExternalCacheDir());
	}
}
