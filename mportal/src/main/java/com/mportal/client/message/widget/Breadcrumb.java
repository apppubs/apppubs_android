package com.mportal.client.message.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hp.hpl.sparta.Text;
import com.mportal.client.R;
import com.mportal.client.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangwen on 2017/1/5.
 */

public class Breadcrumb extends ViewGroup {

    private List<View> mPathList;
    private List<View> mArrowList;
    private HorizontalScrollView mScrollView;
    private LinearLayout mContainerLL;
    private int mHighlightColor;
    private int mTextColor;
    private OnItemClickListener onItemClickListener;

    public Breadcrumb(Context context) {
        super(context);
        init();
    }

    public Breadcrumb(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Breadcrumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Breadcrumb(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        mPathList = new ArrayList<View>();
        mArrowList = new ArrayList<View>();
        mScrollView = new HorizontalScrollView(getContext());
        mScrollView.setHorizontalScrollBarEnabled(false);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(mScrollView,lp);

        mContainerLL = new LinearLayout(getContext());
        mContainerLL.setOrientation(LinearLayout.HORIZONTAL);
        int dp10px = Utils.dip2px(getContext(),10);
        mContainerLL.setPadding(dp10px,0,dp10px,0);
        ViewGroup.LayoutParams containerLp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        mScrollView.addView(mContainerLL,containerLp);

        mTextColor = Color.BLACK;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for (int i=-1;++i<childCount;){
            View child = getChildAt(i);
            child.measure(widthMeasureSpec,heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i=-1;++i<childCount;){
            View child = getChildAt(i);
            child.layout(0,0,r,b);
        }
    }

    public void push(String str, final String tag){

        if (mPathList.size()>0){
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.array_right_thin);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT);
            mContainerLL.addView(imageView,lp1);
            mArrowList.add(imageView);
        }
        TextView tv = new TextView(getContext());
        tv.setText(str);
        tv.setTag(tag);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(mTextColor);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContainerLL.addView(tv,lp);
        final int index = mPathList.size();
        tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    pop2index(index);
                    onItemClickListener.onItemClick(index,tag);
                }
            }
        });
        mPathList.add(tv);
        post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });

    }

    private void pop2index(int index){
        if (mPathList.size()>(index+1)){
            List<View> paddingRemovePathList = new ArrayList<View>();
            List<View> paddingRemoveArrowList = new ArrayList<View>();
            int size = mPathList.size();
            for (int i=index+1;i<size;i++){
                paddingRemovePathList.add(mPathList.get(i));
                mContainerLL.removeView(mPathList.get(i));
                if (i>=1){
                    mContainerLL.removeView(mArrowList.get(i-1));
                    paddingRemoveArrowList.add(mArrowList.get(i-1));
                }
            }
            mPathList.removeAll(paddingRemovePathList);
            mArrowList.removeAll(paddingRemoveArrowList);
        }
    }

    public void setTextColor(int color){
        mTextColor = color;
        for (View view : mPathList){
            TextView tv = (TextView) view;
            tv.setTextColor(mTextColor);
        }
    }

    public interface OnItemClickListener{
        abstract void onItemClick(int index,String tag);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }



}
