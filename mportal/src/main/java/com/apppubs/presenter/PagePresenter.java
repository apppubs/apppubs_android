package com.apppubs.presenter;

import android.content.Context;
import android.text.TextUtils;

import com.apppubs.AppManager;
import com.apppubs.bean.AddressModel;
import com.apppubs.bean.page.PageModel;
import com.apppubs.bean.page.TitleBarAddressModel;
import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.model.IPageBiz;
import com.apppubs.model.PageBiz;
import com.apppubs.ui.activity.MainHandler;
import com.apppubs.ui.page.IPageView;
import com.apppubs.util.LogM;

/**
 * Created by zhangwen on 2017/9/27.
 */

public class PagePresenter {

    private Context mContext;
    private IPageBiz mPageBiz;
    private IPageView mPageView;
    private PageModel mPageModel;

    private boolean isLoading;

    public PagePresenter(Context context, IPageView view) {
        mContext = context;
        mPageBiz = new PageBiz(context);
        mPageView = view;
    }

    public void onVisible() {
        LogM.log(this.getClass(), "可以显示了");
        loadPage();
    }

    public void onCreateView() {
        mPageView.showLoading();
        loadPage();
    }

    public void onAddressSelected(AddressModel model) {
        AppManager.getInstance(mContext).saveCurrentAddress(model.getName(), model.getCode());
        mPageView.setTitleBarAddress(model.getName());
    }

    //private
    private void loadPage() {
        if (isLoading){
            return;
        }
        isLoading = isLoading;

        String pageId = mPageView.getPageId();
        mPageBiz.loadPage(pageId, new IAPCallback<PageModel>() {
            @Override
            public void onDone(final PageModel model) {
                isLoading = false;
                mPageView.hideLoading();
                if (mPageModel != null && mPageModel.equals(model)) {
                    //不需要更新
                    LogM.log(PagePresenter.class, "is equal");
                } else {
                    onDataUpdated(model);
                }
            }

            @Override
            public void onException(final APError error) {
                isLoading = false;
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mPageView.hideLoading();
                        mPageView.onError(error);
                    }
                });

            }
        });
    }

    private void onDataUpdated(final PageModel model) {
        mPageModel = model;
        saveCurAddressIfEmpty(model);
        MainHandler.getInstance().post(new Runnable() {
            @Override
            public void run() {
                mPageView.showTitleBar(model.getTitleBarModel());
                mPageView.showContentView(model.getContent());
                mPageView.hideLoading();
                //显示地址
                if (isAddressTitleBar(model)) {
                    AppManager manager = AppManager.getInstance(mContext);
                    String addressName = manager.getCurrentAddressName();
                    mPageView.setTitleBarAddress(addressName);
                }

            }
        });
    }

    private void saveCurAddressIfEmpty(PageModel model) {
        if (isAddressTitleBar(model)) {
            String addressName = AppManager.getInstance(mContext).getCurrentAddressName();
            if (TextUtils.isEmpty(addressName)) {
                TitleBarAddressModel titleBarModel = (TitleBarAddressModel)
                        model.getTitleBarModel();
                AppManager.getInstance(mContext)
                        .saveCurrentAddress(titleBarModel.getDefaultAddress(),
                                titleBarModel.getDefaultAddressCode());
            }
        }
    }

    private boolean isAddressTitleBar(PageModel model) {
        return model.getTitleBarModel() != null &&
                (model.getTitleBarModel() instanceof TitleBarAddressModel);
    }


}
