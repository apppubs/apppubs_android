package com.apppubs.ui.myfile;

import com.apppubs.constant.APError;
import com.apppubs.bean.MyFileModel;

import java.util.List;

public interface IMyFileView {

    void stopRefresh();

    void stopLoadMore();

    void setFileModels(List<MyFileModel> models);

    void setSearchFileModels(List<MyFileModel> models);

    void onError(APError error);
}
