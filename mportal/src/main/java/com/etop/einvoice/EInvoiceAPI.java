package com.etop.einvoice;

import android.content.Context;
import android.telephony.TelephonyManager;

public class EInvoiceAPI {
	static {
		System.loadLibrary("AndroidVATEngine");
	}
	public native int EIKernalInit(String szSysPath,String FilePath,String CommpanyName,int nProductType,int nAultType,TelephonyManager telephonyManager,Context context);
	public native void EIKernalUnInit();
	public native int EIRecognizePhoto(byte[] ImageStreamNV21, int nLen);
	public native int EIRecognizePhotoEx(byte[] ImageStreamNV21, int nLen, int []LineX,int[]LineY);
	public native String EIGetResult(int nIndex);
	public native int EISaveRecogImg(String imgPath);
	public native int EIRecognizeImagePath(String imgPath);//导入识别
	public native int EIGetImgOrientation();//获取导入图像的方向，用于展示图像
	public native String EIGetEndTime();
	public native int EIDetectCornersNV21(byte[] streamnv21, int cols, int raws, int []LineX,int[]LineY);

}
