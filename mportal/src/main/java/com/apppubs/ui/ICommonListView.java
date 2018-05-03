package com.apppubs.ui;

import java.util.List;

public interface ICommonListView<T> extends ICommonView{

    void setDatas(List<T> datas);
}
