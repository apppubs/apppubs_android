package com.apppubs.vpn;

import android.app.Activity;
import android.content.Context;

import com.apppubs.constant.APError;
import com.apppubs.model.IAPCallback;
import com.apppubs.presenter.AbsPresenter;
import com.apppubs.util.LogM;

import java.util.ArrayList;
import java.util.List;

public class VPNConfigPresenter extends AbsPresenter<IVPNConfigView> {

    private VPNBiz mBiz;
    private List<VPNInfosResult.VPNInfoItem> mVPNInfoList;

    public VPNConfigPresenter(Context context, IVPNConfigView view) {
        super(context, view);
        mBiz = VPNBiz.getInstance(context);
    }

    public void onCreate(){
        loadData();
    }

    private void loadData() {
        mView.showLoading();
        mBiz.fetchVPNInfos(new IAPCallback<VPNInfosResult>() {
            @Override
            public void onDone(VPNInfosResult obj) {
                mVPNInfoList = obj.getItems();
                mView.hideLoading();
                showItems(obj.getItems());
            }

            @Override
            public void onException(APError error) {
                mView.hideLoading();
                mView.onError(error);
            }
        });
    }

    private void showItems(List<VPNInfosResult.VPNInfoItem> list) {
        List<VPNInfoWithPwd> viewData = new ArrayList<>();
        for (VPNInfosResult.VPNInfoItem item : list){
            VPNInfoWithPwd info = new VPNInfoWithPwd();
            info.setVpnId(item.getVpnId());
            info.setVpnName(item.getName());
            info.setVpnURL(item.getVpnURL());
            VPNPwdInfo cacheInfo = mBiz.getPwdInfo(item.getVpnId());
            if (cacheInfo!=null){
                info.setUsername(cacheInfo.getUsername());
                info.setPwd(cacheInfo.getPwd());
            }
            viewData.add(info);
        }
        mView.showItems(viewData);
    }

    public void onConfirmClicked(Activity activity,String vpnId,String username, String pwd, IAPCallback callback){
        mView.showLoading();

        VPNInfosResult.VPNInfoItem item = null;
        for (VPNInfosResult.VPNInfoItem i : mVPNInfoList){
            if (i.getVpnId().equals(vpnId)){
                item = i;
            }
        }
        VPNInfosResult.VPNInfoItem finalItem = item;
        mBiz.verifyVPN(activity,item.getVpnURL(),username,pwd,new IAPCallback(){

            @Override
            public void onDone(Object obj) {
                mView.hideLoading();
                VPNPwdInfo info = new VPNPwdInfo();
                info.setVpnId(vpnId);
                info.setUsername(username);
                info.setPwd(pwd);
                info.setVpnURL(finalItem.getVpnURL());
                mBiz.savePwdInfo(info);
                showItems(mVPNInfoList);
                mView.showMessage("验证成功！");
            }

            @Override
            public void onException(APError error) {
                mView.hideLoading();
                mView.onError(error);
            }
        });
    }

    public void onRestClicked(String vpnId){
        mBiz.clearPwdInfo(vpnId);
        showItems(mVPNInfoList);
    }

    public void onActivityResult(int requestCode, int resultCode) {
        mBiz.onActivityResult(requestCode, resultCode);
    }
}
