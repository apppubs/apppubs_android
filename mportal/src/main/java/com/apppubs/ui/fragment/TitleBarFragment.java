package com.apppubs.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;

import com.apppubs.d20.R;
import com.apppubs.ui.activity.ContainerActivity;
import com.apppubs.ui.myfile.FilePreviewFragment;
import com.apppubs.ui.widget.TitleBar;

public class TitleBarFragment extends BaseFragment {

    @Override
    protected TitleBar initTitleBar() {
        return getDefaultTitleBar();
    }

    private TitleBar getDefaultTitleBar() {
        TitleBar titleBar = new TitleBar(mContext);
        titleBar.setBackgroundColor(getThemeColor());
        float titleSize = mContext.getResources().getDimension(R.dimen.title_text_size);
        titleBar.setTitleTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        titleBar.setTitle(getTitle());

        if (needBack) {
            titleBar.setLeftBtnClickListener(this);
            titleBar.setLeftImageResource(R.drawable.top_back_btn);
        }
        return titleBar;
    }
}
