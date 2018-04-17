package com.apppubs.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.apppubs.util.Utils;

/**
 * Created by siger on 2018/1/11.
 */

public class SignatureView extends View {

    private static final float STROKE_WIDTH = 5f;

    /**
     * Need to track this so the dirty region can accommodate the stroke.
     **/
    private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

    private String mHintText;
    private String mPreDrawedHintText;
    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    private Paint mTextPaint = new Paint();

    /**
     * Optimizes painting by invalidating the smallest possible area.
     */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF();

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(STROKE_WIDTH);

        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(Utils.dip2px(context, 15));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Erases the signature.
     */
    public void clear() {
        mPath.reset();
        // Repaints the entire view.
        invalidate();
    }

    public void setHintText(String hintText) {
        mHintText = hintText;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, mPaint);

        if (shouldDrawHint()) {
            drawHintText(canvas, mTextPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                // There is no end point yet, so don't waste cycles invalidating.
                return true;

            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                // Start tracking the dirty region.
                resetDirtyRect(eventX, eventY);

                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points.
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    mPath.lineTo(historicalX, historicalY);
                }

                // After replaying history, connect the line to the touch point.
                mPath.lineTo(eventX, eventY);
                break;

            default:
                return false;
        }

        // Include half the stroke width to avoid clipping.
        invalidate(
                (int) (dirtyRect.left - HALF_STROKE_WIDTH),
                (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

        lastTouchX = eventX;
        lastTouchY = eventY;

        return true;
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * points.
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    /**
     * Resets the dirty region when the motion event occurs.
     */
    private void resetDirtyRect(float eventX, float eventY) {

        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred.
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    private void drawHintText(Canvas canvas, Paint textPaint) {
        mPreDrawedHintText = mHintText;
        int sx = 0;
        int sy = 0;
        int ex = getWidth();
        int ey = getHeight();
        Rect targetRect = new Rect(sx, sy, ex, ey);
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        float width = textPaint.measureText(mHintText);
        int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        canvas.drawText(mHintText, sx + (ex - sx) / 2, baseline, textPaint);
    }

    private boolean shouldDrawHint() {
        return !TextUtils.isEmpty(mHintText) && (!mHintText.equals(mPreDrawedHintText) || mPath.isEmpty());
    }


}
