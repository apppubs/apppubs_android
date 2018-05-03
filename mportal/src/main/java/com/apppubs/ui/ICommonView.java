package com.apppubs.ui;

import com.apppubs.bean.http.MyServiceNOsResult;
import com.apppubs.constant.APError;

import java.util.List;

public interface ICommonView {
    void showLoading();
    void hideLoading();
    void onError(APError error);
    void showEmptyView();
    void hideEmptyView();
}
