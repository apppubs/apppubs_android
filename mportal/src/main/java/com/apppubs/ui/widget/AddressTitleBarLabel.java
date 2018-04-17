package com.apppubs.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apppubs.d20.R;
import com.apppubs.util.Utils;

/**
 * Created by siger on 2018/1/31.
 */

public class AddressTitleBarLabel extends LinearLayout {

    private String mText;
    private ImageView mImageView;
    private TextView mTextView;

    public AddressTitleBarLabel(Context context) {
        super(context);
        init(context);
    }

    public void setText(String text){
        mText = text;
        mTextView.setText(text);
    }

    //private
    private void init(Context context){
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setPadding(Utils.dip2px(getContext(),10),0,Utils.dip2px(getContext(),2),0);
        addView(mTextView);
        mImageView = new ImageView(context);
        mImageView.setImageResource(R.drawable.arrow_down);
        addView(mImageView);

        setOrientation(LinearLayout.HORIZONTAL);

        LayoutParams textViewLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        mTextView.setLayoutParams(textViewLP);

        LayoutParams imageViewLP = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        imageViewLP.gravity = Gravity.CENTER;
        mImageView.setLayoutParams(imageViewLP);
    }
}
