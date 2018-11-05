package com.etop.VATDetectLine.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.apppubs.d20.R;
import com.apppubs.ui.activity.BaseActivity;
import com.apppubs.ui.widget.TitleBar;
import com.etop.VATDetectLine.utils.VatConstantUtil;
import com.etop.VATDetectLine.utils.VatImgFileNameUtil;
import com.etop.VATDetectLine.utils.VatStreamUtil;
import com.etop.einvoice.EInvoiceAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ocr识别
 */
public class VatRecogActivity extends BaseActivity {

    public static final int TYPE_CAMERA = 0;
    public static final int TYPE_GALLERY = 1;

    public static final String EXTRA_INT_TYPE = "extra_int_type";
    public static final String EXTRA_RESULT_STRING_LIST = "list_result";

    private final int REQUEST_CODE_TAKE_PHOTO = 1;
    private final int REQUEST_CODE_GET_IMG_FROM_GALLERY = 2;

    private ArrayList<String> mResultList = null;
    private Bitmap compsBmp;
    private String fileImgPath;
    private File tempImgFile;
    private String imgFile;

    private int type;

    private EInvoiceAPI eiapi = null;
    private String UserID = VatConstantUtil.getUserId();
    private ProgressDialog progress;

    private LinearLayout mContainerLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initInput();
        initOCR();
        startGetImg();
    }

    private void initView() {
        setContentView(R.layout.act_vat_recog);
        mContainerLL = (LinearLayout) findViewById(R.id.act_vat_recog_ll);
        setTitle("识别结果");
        TitleBar titlebar = getTitleBar();
        titlebar.setRightBtnWithText("完成");
        titlebar.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putStringArrayListExtra(EXTRA_RESULT_STRING_LIST, mResultList);
                VatRecogActivity.this.setResult(RESULT_OK, intent);
                VatRecogActivity.this.finish();
            }
        });
    }

    private void startGetImg() {
        if (type == TYPE_CAMERA) {
            String state = Environment.getExternalStorageState(); //拿到sdcard是否可用的状态码
            if (state.equals(Environment.MEDIA_MOUNTED)) {   //如果可用
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                File fileDir = new File(Environment.getExternalStorageDirectory()
                        + "/alpha/VinCode/");
                if (!fileDir.isDirectory() || !fileDir.exists())
                    fileDir.mkdirs();

                tempImgFile = new File(Environment.getExternalStorageDirectory()
                        + "/alpha/VinCode/", "temp.jpg");
                Uri imageUri = Uri.fromFile(tempImgFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
            } else {
                Toast.makeText(this, "sdcard不可用", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Intent selectIntent = new Intent(Intent.ACTION_PICK);
            selectIntent.setType("image/*");
            if (selectIntent.resolveActivity(this.getPackageManager()) != null) {
                startActivityForResult(selectIntent, REQUEST_CODE_GET_IMG_FROM_GALLERY);
            }
        }
    }

    private void initInput() {
        Intent intent = getIntent();
        type = intent.getIntExtra(EXTRA_INT_TYPE, TYPE_CAMERA);
    }

    private void initOCR() {
        //1.设置授权名称
        VatConstantUtil.setUserId("1D06FE3623544DC2D3A0");
        //2.将授权文件写入到手机目录中，初始化核心必要操作
        try {
            VatStreamUtil.copyDataBase(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始化识别核心
        if (eiapi == null) {
            eiapi = new EInvoiceAPI();
            String cacheDir = (this.getExternalCacheDir()).getPath();
            String FilePath = cacheDir + "/" + UserID + ".lic";
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            int nRet = eiapi.EIKernalInit("", FilePath, UserID, 21, 0x02, telephonyManager, this);
            if (nRet != 0) {
                Toast.makeText(getApplicationContext(), "激活失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) { //防止没有返回结果
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(tempImgFile.getPath(), options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = calculateSampleSize(options);
                compsBmp = BitmapFactory.decodeFile(tempImgFile.getPath(), options);

                FileOutputStream fos = null;
                imgFile = Environment.getExternalStorageDirectory()
                        + "/alpha/VinCode/" + VatImgFileNameUtil.pictureName("VAT") + ".jpg";
                try {
                    fos = new FileOutputStream(imgFile);
                    compsBmp.compress(Bitmap.CompressFormat.JPEG, 30, fos);// 把数据写入文件
                    compsBmp.recycle();
                    tempImgFile.delete();
                    compsBmp = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                tempImgFile.delete();
                finish();
                return;
            }

            try {
                progress = ProgressDialog.show(this, "", "正在识别...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //导入图像识别接口
                        final int nRet = eiapi.EIRecognizeImagePath(imgFile);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 0 ：代表识别成功
                                if (nRet == 0) {
                                    for (int i = 1; i < 18; i++) {
                                        //将识别完成后，获取识别结果，添加到集合中
                                        mResultList.add(eiapi.EIGetResult(i));
                                    }
                                    String fplx = eiapi.EIGetResult(18);
                                    if (fplx.equals("0")) {
                                        mResultList.add("解析失败");
                                    } else if (fplx.equals("1")) {
                                        mResultList.add("专票");
                                    } else if (fplx.equals("4")) {
                                        mResultList.add("普票");
                                    } else if (fplx.equals("10")) {
                                        mResultList.add("电子普通发票");
                                    }
                                    displayResult(mResultList);
                                } else {
                                    Toast.makeText(VatRecogActivity.this,"识别失败，ErrorCode：" + nRet,Toast.LENGTH_LONG).show();
                                    VatRecogActivity.this.finish();
                                }
                                if (progress != null) progress.dismiss();
                            }
                        });

                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_GET_IMG_FROM_GALLERY) {
            if (data == null) {
                this.finish();
                return;
            }
            Uri imageFileUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(imageFileUri, filePathColumn, null, null, null);
            String filePath = "";
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();
            } else {
                filePath = imageFileUri.getPath();
            }
            fileImgPath = filePath;
            mResultList = new ArrayList<>();
            if (eiapi != null) {
                progress = ProgressDialog.show(VatRecogActivity.this, "", "正在识别...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //导入图像识别接口
                        final int nRet = eiapi.EIRecognizeImagePath(fileImgPath);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 0 ：代表识别成功
                                if (nRet == 0) {
                                    for (int i = 1; i < 18; i++) {
                                        //将识别完成后，获取识别结果，添加到集合中
                                        mResultList.add(eiapi.EIGetResult(i));
                                    }
                                    String fplx = eiapi.EIGetResult(18);
                                    if (fplx.equals("0")) {
                                        mResultList.add("解析失败");
                                    } else if (fplx.equals("1")) {
                                        mResultList.add("专票");
                                    } else if (fplx.equals("4")) {
                                        mResultList.add("普票");
                                    } else if (fplx.equals("10")) {
                                        mResultList.add("电子普通发票");
                                    }
                                    Intent intent = new Intent();
                                    intent.putStringArrayListExtra(EXTRA_RESULT_STRING_LIST, mResultList);
                                    displayResult(mResultList);
                                } else {
                                    Toast.makeText(VatRecogActivity.this,"识别失败，ErrorCode：" + nRet,Toast.LENGTH_LONG).show();
                                    VatRecogActivity.this.finish();
                                }
                                if (progress != null) progress.dismiss();
                            }
                        });

                    }
                }).start();
            }

        }
    }

    private void displayResult(List<String> resultList) {
        String strField[] = {"发票代码 ：", "发票号码 ：", "开票日期 ：", "购方识别号 ：", "销方识别号 ：",
                "价税合计 ：", "金额合计 ：", "税额合计 ：", "校验码 ：", "购方名称 ：", "销方名称 ："
                , "大写金额 ：", "货物名称 ：", "货物税率 ：", "打印发票代码 ：", "打印发票号码 ：", "发票联次 ：", "发票类型 ："};
        for (int i = -1; ++i < strField.length; ) {
            View item = LayoutInflater.from(this).inflate(R.layout.item_act_vat_recog_ll, null);
            TextView tv = (TextView) item.findViewById(R.id.item_act_vat_recog_tv);
            tv.setText(strField[i]);
            EditText et = (EditText) item.findViewById(R.id.item_act_vat_recog_et);
            et.setText(resultList.get(i));
            et.setTag(i);
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mResultList.set(Integer.parseInt(et.getTag().toString()), s.toString());
                }
            });
            mContainerLL.addView(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progress != null) progress.dismiss();
        if (eiapi != null) {
            eiapi.EIKernalUnInit();
            eiapi = null;
        }
    }


    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }
}
