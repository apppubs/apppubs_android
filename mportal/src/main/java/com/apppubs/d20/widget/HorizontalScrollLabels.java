package com.apppubs.d20.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.d20.util.Utils;

/**
 * Created by siger on 2018/1/18.
 * 横向滚动的可点击圆角标签
 */

public class HorizontalScrollLabels extends HorizontalScrollView implements View.OnClickListener {
    private LinearLayout mContainerLL;
    private GradientDrawable mLabelBgGd;
    private String[] mLabels;
    private HorizontalScrollLabelsListener mListener;

    public interface HorizontalScrollLabelsListener {
        void onClick(String text);
    }

    public HorizontalScrollLabels(Context context) {
        super(context);
        initView();
    }

    public HorizontalScrollLabels(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setLabels(String[] labels) {
        mLabels = labels;
        if (labels != null) {
            for (int i = -1; ++i < labels.length; ) {
                String text = labels[i];
                TextView tv = getLabel(text);
                mContainerLL.addView(tv, getLabelLayoutParams(text));
            }
        }
    }

    //private
    @NonNull
    private MarginLayoutParams getLabelLayoutParams(String text) {
        Paint paint = new Paint();
        paint.setTextSize(Utils.dip2px(getContext(), 13));
        float textWidth = paint.measureText(text);
        int width = (int) (textWidth + Utils.dip2px(getContext(), 28));

        MarginLayoutParams lp = new MarginLayoutParams(width, Utils.dip2px(getContext(),
                25));
        int dp10 = Utils.dip2px(getContext(), 10);
        int dp15 = Utils.dip2px(getContext(), 15);
        lp.leftMargin = dp10;
        lp.rightMargin = dp10;
        lp.topMargin = dp15;
        return lp;
    }

    @NonNull
    private TextView getLabel(String text) {
        TextView tv = new TextView(getContext());
        tv.setTag(text);
        tv.setText(text);
        tv.setOnClickListener(this);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        tv.setTextColor(Color.parseColor("#555555"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tv.setBackground(mLabelBgGd);
        } else {
            tv.setBackgroundDrawable(mLabelBgGd);
        }
        return tv;
    }

    public void setListener(HorizontalScrollLabelsListener listener) {
        mListener = listener;
    }

    private void initView() {
        initLabelBg();
        setHorizontalScrollBarEnabled(false);
        mContainerLL = new LinearLayout(getContext());
        mContainerLL.setOrientation(LinearLayout.HORIZONTAL);
        addView(mContainerLL);
    }

    private void initLabelBg() {
        mLabelBgGd = new GradientDrawable();// 创建drawable
        mLabelBgGd.setColor(Color.parseColor("#FFFFFF"));
        mLabelBgGd.setCornerRadius((float) (Utils.dip2px(getContext(), 15)));
        mLabelBgGd.setStroke(1, Color.parseColor("#E5E5E5"));
    }


    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick((String) v.getTag());
        }
    }
}
