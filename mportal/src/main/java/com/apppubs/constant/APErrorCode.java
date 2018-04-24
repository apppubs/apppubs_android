package com.apppubs.constant;

import com.apppubs.net.WMHHttpErrorCode;

/**
 * Created by siger on 2018/4/18.
 */

public enum APErrorCode {

    SUCCESS(0), //成功
    GENERAL_ERROR(1004), //通用错误
    TOKEN_EXPIRE(1001), //token过期
    REQUEST_EXPIRE(1002), //请求过期
    SIGN_ERROR(1003), //签名错误
    PARAM_ERROR(1010), //请求参数错误
    SERVER_ERROR(1020), //服务器异常
    METHOD_NOT_SUPPORT(1021), //不支持此[get/put/delete]请求方法
    USER_NAME_OR_PWD_ERROR(100001), //用户名或者密码错误
    USERNAME_NOT_EXIST(100002), //用户名不存在
    PHONE_NOT_EXIST(100003), //手机号不存在
    ORG_CODE_NOT_EXIST(100004), //机构代码不存在
    ORG_LOGIN_ADDRESS_NOT_EXIST(100005), //机构配置登录接口未配置
    SMS_SEND_ERROR(100006), //调用短信通道发送短信失败
    VERIFY_CODE_ERROR(100007), //登录验证码错误
    VERIFY_CODE_EXPIRE(100005), //登录验证码过期
    OLD_PWD_ERROR(100009), //旧密码输入失败

    /**本地错误*/
    NETWORK_ERROR(2000),//网络错误
    JSON_PARSE_ERROR(2002)//json解析错误
    ;


    private int code;

    APErrorCode(Integer code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static APErrorCode valueOf(int code) {
        for (APErrorCode item : APErrorCode.values()) {
            if (item.code == code) {
                return item;
            }
        }
        return null;
    }
}
