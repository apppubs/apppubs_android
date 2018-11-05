package com.etop.VATDetectLine.utils;

import android.os.Environment;

public class VatConstantUtil {
    public static final String PATH = Environment.getExternalStorageDirectory() + "/Alpha/other/";
    public static final String ImgPATH = Environment.getExternalStorageDirectory() + "/Alpha/VAT/";
    private static String UserId = "";
    public static String getUserId() {
        return UserId;
    }
    public static void setUserId(String userId) {
        UserId = userId;
    }
}
