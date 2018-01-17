package com.apppubs.d20.page;

import android.content.Context;
import android.os.Handler;

import com.apppubs.d20.model.APResultCallback;
import com.apppubs.d20.util.LogM;
import com.apppubs.d20.util.Utils;

import okhttp3.internal.Util;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PagePresenter {

	private Context mContext;
	private IPageBiz mPageBiz;
	private IPageView mPageView;
	private Handler mHandler = new Handler();
	private PageModel mPageModel;

	public PagePresenter(Context context,IPageView view){
		mContext = context;
		mPageBiz = new PageBiz(context);
		mPageView = view;
	}

	public void onVisiable(){
		LogM.log(this.getClass(),"可以显示了");
		loadPage();
	}

	public void onCreateView(){
		loadPage();
	}

	private void loadPage() {
		String pageId = mPageView.getPageId();
		mPageBiz.loadPage(pageId, new APResultCallback<PageModel>() {
			@Override
			public void onDone(final PageModel model) {
				if (mPageModel!=null&&mPageModel.equals(model)){
					//不需要更新
					LogM.log(PagePresenter.class,"is equal");
				}else{
					mPageModel = model;

					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mPageView.showTitleBar(mPageModel.getTitleBarModel());
							mPageView.showContentView(mPageModel.getContent());
						}
					});
				}
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
