package com.apppubs.vpn;

import com.apppubs.ui.ICommonView;

import java.util.List;

public interface IVPNConfigView extends ICommonView{
    void showItems(List<VPNInfoWithPwd> items);
    void setReadmeURL(String readmeURL);
}
