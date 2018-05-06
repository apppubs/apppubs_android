package com.apppubs.ui.news;

import com.apppubs.bean.http.ArticleResult;
import com.apppubs.ui.ICommonDataView;
import com.apppubs.ui.ICommonView;

public interface INewsDefaultInfoView extends ICommonView {

    void setData(ArticleResult result);
}
