package com.apppubs.d20.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.apppubs.d20.R;

/**
 * Created by zhangwen on 2017/2/24.
 * 讨论组用户详情，头像信息包含未激活标志
 */

public class UserIconImageView extends CircleTextImageView {

    private final String UNACTIVATED = "未激活";

    private Paint mActiveTextPaint = new Paint();
    private Paint mActiveBgPaint = new Paint();

    private boolean needNonactivated;


    public UserIconImageView(Context context) {
        super(context);
        init();
    }

    public UserIconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UserIconImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init(){
        mActiveTextPaint.setColor(Color.WHITE);
        mActiveTextPaint.setAntiAlias(true);
        mActiveBgPaint.setStyle(Paint.Style.FILL);
        mActiveBgPaint.setAntiAlias(true);
        mActiveBgPaint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (needNonactivated){
            RectF rect = new RectF(0,(float)(getHeight()*0.7),getWidth(),getHeight());
            canvas.drawRoundRect(rect,(float)(getWidth()*0.1),(float)(getWidth()*0.1), mActiveBgPaint);
            //未激活文字
            mActiveTextPaint.setTextSize((float)(getWidth()*0.15));
            Paint.FontMetricsInt fm = mActiveTextPaint.getFontMetricsInt();
            float width = mActiveTextPaint.measureText(UNACTIVATED);

            canvas.drawText(UNACTIVATED,
                    getWidth() / 2 - width / 2,
                    getHeight() / 2 - fm.descent + (fm.bottom - fm.top) / 2+(float)(getHeight()*0.35), mActiveTextPaint);
        }
    }

    public void setNeedNonactivated(boolean need){
        needNonactivated = need;
        invalidate();
    }
}
