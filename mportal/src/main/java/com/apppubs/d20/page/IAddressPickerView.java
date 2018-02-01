package com.apppubs.d20.page;

import java.util.List;

/**
 * Created by siger on 2018/1/31.
 */

public interface IAddressPickerView {

    String getAddressRootId();

    void setModels(List<AddressModel> model);

    void showLoading();

    void hideLoading();

    void showError(String error);
}
