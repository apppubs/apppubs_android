package com.apppubs.d20.page;

import android.content.Context;
import android.os.Handler;

import com.apppubs.d20.model.APResultCallback;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PagePresenter {

	private Context mContext;
	private IPageBiz mPageBiz;
	private IPageView mPageView;
	private Handler mHandler = new Handler();

	public PagePresenter(Context context,IPageView view){
		mContext = context;
		mPageBiz = new PageBiz(context);
		mPageView = view;
	}

	public void onResume(){
	}

	public void onCreateView(){
		String pageId = mPageView.getPageId();
		mPageBiz.loadPage(pageId, new APResultCallback<PageModel>() {
			@Override
			public void onDone(final PageModel obj) {

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mPageView.showTitleBar(obj.getTitleBarModel());
						mPageView.showContentView(obj.getContent());
					}
				});
			}

			@Override
			public void onException(int excepCode) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mPageView.showErrorView();
					}
				});

			}
		});
	}

}
