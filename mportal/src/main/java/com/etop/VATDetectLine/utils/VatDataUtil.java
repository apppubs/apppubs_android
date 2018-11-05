package com.etop.VATDetectLine.utils;
public class VatDataUtil {
    private static byte[] myData;

    public static byte[] getData() {
        return myData;
    }

    public static void setData(byte[] takeData){
        myData = takeData;
    }
}
