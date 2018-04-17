package com.apppubs.presenter;

import android.content.Context;

/**
 * Created by siger on 2018/3/13.
 */

public abstract class AbsPresenter<T> {

    protected T mView;
    protected Context mContext;

    public AbsPresenter(Context context, T view) {
        mContext = context;
        mView = view;
    }
}
