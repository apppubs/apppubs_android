package com.apppubs.presenter;

import com.apppubs.ui.activity.MainHandler;
import com.apppubs.bean.AddressModel;
import com.apppubs.model.AddressPickerBiz;
import com.apppubs.ui.page.IAddressPickerView;

import java.util.List;

/**
 * Created by siger on 2018/1/31.
 */

public class AddressPickerPresenter {

    private IAddressPickerView mView;
    private AddressPickerBiz mPickerBiz;

    public AddressPickerPresenter(IAddressPickerView view) {
        mView = view;
        mPickerBiz = new AddressPickerBiz();
    }

    public void onCreateView() {
        mView.showLoading();
        mPickerBiz.getAddressList(mView.getAddressRootId(), new AddressPickerBiz
                .AddressPickerListener() {


            @Override
            public void onDone(final List<AddressModel> models) {
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mView.hideLoading();
                        mView.setModels(models);
                    }
                });

            }

            @Override
            public void onFailure(final Exception e) {
                MainHandler.getInstance().post(new Runnable() {
                    @Override
                    public void run() {
                        mView.showError(e.getMessage());
                    }
                });

            }
        });
    }
}
