package com.mportal.client.message.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangwen on 2017/1/5.
 */

public class Breadcrumb extends ViewGroup {


    public Breadcrumb(Context context) {
        super(context);
    }

    public Breadcrumb(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Breadcrumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Breadcrumb(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }


}
