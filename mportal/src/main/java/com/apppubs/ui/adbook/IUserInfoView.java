package com.apppubs.ui.adbook;

import android.widget.ImageView;

import com.apppubs.bean.TUser;
import com.apppubs.ui.ICommonView;

/**
 * Created by zhangwen on 2017/10/24.
 */

public interface IUserInfoView extends ICommonView {


    public static final int START_CHAT_BTN = 1;
    public static final int MOBILE_PHONE_BTN = 2;
    public static final int TEL_PHONE_BTN = 3;
    public static final int EMAIL_BTN = 4;
    public static final int INVITE_BTN = 5;
    public static final int RE_SEND_INVITE_BTN = 6;
    public static final int ADD_CONTACT_BTN = 7;

    public static final int BOTTOM_BTN_TYPE_NONE = 0;
    public static final int BOTTOM_BTN_TYPE_NORMAL = 1;
    public static final int BOTTOM_BTN_TYPE_ACTIVE = 2;
    public static final int BOTTOM_BTN_TYPE_ACTIVED = 3;

    void setBottomBtnType(int type);

    String getUserId();


    void setUser(TUser user);

    void setDepartmentStr(String departmentStr);

    void showIcon(String iconURL, String name, boolean needZoom);

}

