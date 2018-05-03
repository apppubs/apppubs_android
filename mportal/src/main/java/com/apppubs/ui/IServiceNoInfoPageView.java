package com.apppubs.ui;

import com.apppubs.bean.http.ServiceNOInfoPageResult;

public interface IServiceNoInfoPageView extends ICommonListView<ServiceNOInfoPageResult.Items>{

    void hideRefreshAndLoadMore();
}
