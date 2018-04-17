package com.apppubs.ui.widget.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.apppubs.model.message.UserBasicInfo;
import com.apppubs.util.Utils;
import com.apppubs.ui.widget.CircleTextImageView;
import com.apppubs.d20.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwen on 2017/1/7.
 */

public class UserSelectionBar extends ViewGroup implements View.OnClickListener{

    private LinearLayout mContainerLL;
    private FrameLayout mUserIconAndHintContainerFL;
    private TextView mHintTV;
    private HorizontalScrollView mUserIconSV;
    private LinearLayout mUserIconLl;
    private Button mOkButton;

    private String mHintText = "选择人员";
    private int mMaxSelectCount ;//最大选择人数
    private UserSelectionBarListener mListener;

    private Map<String,View> mUserIconMap;

    public UserSelectionBar(Context context) {
        super(context);
        init();
    }

    public UserSelectionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserSelectionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mContainerLL = new LinearLayout(getContext());
        mContainerLL.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        addView(mContainerLL,lp);

        //提示文字和滚动view容器
        mUserIconAndHintContainerFL = new FrameLayout(getContext());
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(0,LayoutParams.MATCH_PARENT);
        lp1.weight = 1;
        mContainerLL.addView(mUserIconAndHintContainerFL,lp1);

        //提示文字
        mHintTV = new TextView(getContext());
        mHintTV.setText(mHintText);
        mHintTV.setGravity(Gravity.CENTER_VERTICAL);
        FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        lp2.leftMargin = Utils.dip2px(getContext(),10);
        mUserIconAndHintContainerFL.addView(mHintTV,lp2);

        //滚动view
        mUserIconSV = new HorizontalScrollView(getContext());
        FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        mUserIconAndHintContainerFL.addView(mUserIconSV,lp3);

        //头像容器
        mUserIconLl = new LinearLayout(getContext());
        mUserIconLl.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams lp4 = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        mUserIconSV.addView(mUserIconLl,lp4);


        mOkButton = new Button(getContext());
        mOkButton.setText("确定");
        mOkButton.setTextSize(12);
        int pxFor8dp = Utils.dip2px(getContext(),8);
        mOkButton.setPadding(pxFor8dp,0,pxFor8dp,0);
        mOkButton.setEnabled(false);
        mOkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.onDoneClick();
                }
            }
        });

        LinearLayout.LayoutParams okLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        okLp.setMargins(pxFor8dp,pxFor8dp,pxFor8dp,pxFor8dp);
        mContainerLL.addView(mOkButton,okLp);

        mUserIconMap = new HashMap<String,View>();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i=-1;++i<count;){
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

    public void setMaxSelectCount(int count){
        mMaxSelectCount = count;
        if (count>0){
			mHintText = String.format("请选择1-%s人",count);
			mHintTV.setText(mHintText);
			invalidate();
		}
    }

    public void setListener(UserSelectionBarListener onItemClickListener){
        mListener = onItemClickListener;
    }

    public void addUser(UserBasicInfo userInfo){
        CircleTextImageView iv = new CircleTextImageView(getContext());
        ImageLoader.getInstance().displayImage(userInfo.getAtatarUrl(),iv);
        iv.setText(userInfo.getTrueName());
        iv.setTextColor(Color.WHITE);
        int pxFor8dp = Utils.dip2px(getContext(),8);
        iv.setTextSize((getHeight()-pxFor8dp*2-pxFor8dp)/3);
        iv.setFillColor(getResources().getColor(R.color.common_btn_bg_gray));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getHeight()-pxFor8dp*2,getHeight()-pxFor8dp*2);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.leftMargin = pxFor8dp;
        mUserIconLl.addView(iv,lp);
        iv.setTag(userInfo.getUserId());
        iv.setOnClickListener(this);
        mUserIconMap.put(userInfo.getUserId(),iv);
        hideOrShowHintIfNeed();
        updateOkBtn();
        mUserIconSV.post(new Runnable() {
            @Override
            public void run() {
                mUserIconSV.fullScroll(ScrollView.FOCUS_RIGHT);
            }
        });
    }

    public void addUsers(List<UserBasicInfo> users){
        for (UserBasicInfo userinfo: users){
            addUser(userinfo);
        }
    }

    public void removeUser(String userId){
        View iconView = mUserIconMap.get(userId);
        mUserIconLl.removeView(iconView);
        mUserIconMap.remove(userId);
        hideOrShowHintIfNeed();
        updateOkBtn();
    }

    public void removeUsers(List<String> userIds){
        for (String userId: userIds){
            removeUser(userId);
        }
    }

    private void hideOrShowHintIfNeed() {
        if (mUserIconMap.size()>0){
            mHintTV.setVisibility(View.GONE);
        }else{
            mHintTV.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 刷新ok按钮
     */
    private void updateOkBtn() {
        if (mUserIconMap.size()>0){
            mOkButton.setEnabled(true);
            String btnText = mMaxSelectCount>0?"确定("+mUserIconMap.size()+"/"+mMaxSelectCount+")":"确定("+mUserIconMap.size()+")";
            mOkButton.setText(btnText);
        }else{
            mOkButton.setEnabled(false);
            mOkButton.setText("确定");
        }
    }

    @Override
    public void onClick(View v) {
        String userId = (String) v.getTag();
        removeUser(userId);
        if (mListener !=null){
            mListener.onItemClick(userId);
        }
    }

    public interface UserSelectionBarListener{
        void onItemClick(String userId);
        void onDoneClick();
    }

}
