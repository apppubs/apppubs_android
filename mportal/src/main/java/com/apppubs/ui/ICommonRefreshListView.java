package com.apppubs.ui;

public interface ICommonRefreshListView<T> extends ICommonListView<T> {
    void stopRefresh();
    void stopLoadMore();
    void haveLoadAll();
}
