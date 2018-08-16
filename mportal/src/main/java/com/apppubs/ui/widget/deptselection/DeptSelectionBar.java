package com.apppubs.ui.widget.deptselection;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwen on 2017/1/7.
 */

public class DeptSelectionBar extends ViewGroup implements View.OnClickListener {

    private LinearLayout mContainerLL;
    private FrameLayout mUserIconAndHintContainerFL;
    private TextView mHintTV;
    private HorizontalScrollView mUserIconSV;
    private LinearLayout mUserIconLl;
    private Button mOkButton;

    private String mHintText = "选择部门";
    private int mMaxSelectCount;//最大选择人数
    private DeptSelectionBarListener mListener;

    private Map<String, View> mUserIconMap;

    public DeptSelectionBar(Context context) {
        super(context);
        init();
    }

    public DeptSelectionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DeptSelectionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContainerLL = new LinearLayout(getContext());
        mContainerLL.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mContainerLL, lp);

        //提示文字和滚动view容器
        mUserIconAndHintContainerFL = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
        lp1.weight = 1;
        mContainerLL.addView(mUserIconAndHintContainerFL, lp1);

        //提示文字
        mHintTV = new TextView(getContext());
        mHintTV.setText(mHintText);
        mHintTV.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        lp2.leftMargin = Utils.dip2px(getContext(), 10);
        mUserIconAndHintContainerFL.addView(mHintTV, lp2);

        //滚动view
        mUserIconSV = new HorizontalScrollView(getContext());
        FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .MATCH_PARENT);
        mUserIconAndHintContainerFL.addView(mUserIconSV, lp3);

        //头像容器
        mUserIconLl = new LinearLayout(getContext());
        mUserIconLl.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lp4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mUserIconSV.addView(mUserIconLl, lp4);


        mOkButton = new Button(getContext());
        mOkButton.setText("确定");
        mOkButton.setTextSize(12);
        int pxFor8dp = Utils.dip2px(getContext(), 8);
        mOkButton.setPadding(pxFor8dp, 0, pxFor8dp, 0);
        mOkButton.setEnabled(false);
        mOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDoneClick();
                }
            }
        });

        LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams
                .MATCH_PARENT);
        okLp.setMargins(pxFor8dp, pxFor8dp, pxFor8dp, pxFor8dp);
        mContainerLL.addView(mOkButton, okLp);

        mUserIconMap = new HashMap<String, View>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = -1; ++i < count; ) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = -1; ++i < childCount; ) {
            View child = getChildAt(i);
            child.layout(0, 0, r, b);
        }
    }

    public void setMaxSelectCount(int count) {
        mMaxSelectCount = count;
        if (count > 0) {
            mHintText = String.format("请选择1-%s人", count);
            mHintTV.setText(mHintText);
            invalidate();
        }
    }

    public void setListener(DeptSelectionBarListener onItemClickListener) {
        mListener = onItemClickListener;
    }

    public void addDept(DeptSelectionItemModel model) {
        TextView tv = new TextView(getContext());
        int pxFor8dp = Utils.dip2px(getContext(), 8);
        float fontSize = getResources().getDimension(R.dimen.item_text_size_small);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,fontSize);
        tv.setTextColor(getResources().getColor(R.color.common_text_gray));
        tv.setGravity(Gravity.CENTER);
        tv.setText(model.getName());
        tv.setBackgroundResource(R.drawable.dept_selection_item_bg);
        tv.setPadding(15,0,15,0);
//        tv.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 48);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = pxFor8dp;
        mUserIconLl.addView(tv, lp);
        tv.setTag(model.getId());
        tv.setOnClickListener(this);
        mUserIconMap.put(model.getId(), tv);
        hideOrShowHintIfNeed();
        updateOkBtn();
        mUserIconSV.post(new Runnable() {
            @Override
            public void run() {
                mUserIconSV.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    public void removeDept(String deptId) {
        View iconView = mUserIconMap.get(deptId);
        mUserIconLl.removeView(iconView);
        mUserIconMap.remove(deptId);
        hideOrShowHintIfNeed();
        updateOkBtn();
    }

    public void removeDepts(List<String> deptIds) {
        for (String deptId : deptIds) {
            removeDept(deptId);
        }
    }

    private void hideOrShowHintIfNeed() {
        if (mUserIconMap.size() > 0) {
            mHintTV.setVisibility(View.GONE);
        } else {
            mHintTV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新ok按钮
     */
    private void updateOkBtn() {
        if (mUserIconMap.size() > 0) {
            mOkButton.setEnabled(true);
            String btnText = mMaxSelectCount > 0 ? "确定(" + mUserIconMap.size() + "/" + mMaxSelectCount + ")" : "确定("
                    + mUserIconMap.size() + ")";
            mOkButton.setText(btnText);
        } else {
            mOkButton.setEnabled(false);
            mOkButton.setText("确定");
        }
    }

    @Override
    public void onClick(View v) {
        String deptId = (String) v.getTag();
        removeDept(deptId);
        if (mListener != null) {
            mListener.onItemClick(deptId);
        }
    }

    public interface DeptSelectionBarListener {
        void onItemClick(String userId);

        void onDoneClick();
    }
}
