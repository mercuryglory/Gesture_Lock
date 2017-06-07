package com.mercury.gesturetest.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.mercury.gesturetest.R;

import static com.mercury.gesturetest.widget.GestureView.Mode.POINT_STATE_NORMAL;

/**
 * 创建者:    wang.zhonghao
 * 创建时间:  2017/6/7
 * 描述:      ${TODO}
 */
public class GestureView extends ImageView {

    private int leftX;
    private int rightX;
    private int topY;
    private int bottomY;
    private int centerX;
    private int centerY;
    private int num;

    public GestureView(Context context, int leftX, int rightX, int topY, int bottomY, int num) {
        this(context);
        this.leftX = leftX;
        this.rightX = rightX;
        this.topY = topY;
        this.bottomY = bottomY;

        this.centerX = (leftX + rightX) / 2;
        this.centerY = (topY + bottomY) / 2;
        this.num = num;
    }

    enum Mode {
        POINT_STATE_NORMAL, POINT_STATE_SELECTED, POINT_STATE_WRONG;
    }

    private Mode currentStatus = POINT_STATE_NORMAL;

    public GestureView(Context context) {
        this(context, null);
    }

    public GestureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getLeftX() {
        return leftX;
    }

    public void setLeftX(int leftX) {
        this.leftX = leftX;
    }

    public int getRightX() {
        return rightX;
    }

    public void setRightX(int rightX) {
        this.rightX = rightX;
    }

    public int getTopY() {
        return topY;
    }

    public void setTopY(int topY) {
        this.topY = topY;
    }

    public int getBottomY() {
        return bottomY;
    }

    public void setBottomY(int bottomY) {
        this.bottomY = bottomY;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (currentStatus) {
            case POINT_STATE_NORMAL:
                this.setBackgroundResource(R.drawable.gesture_normal);
                break;
            case POINT_STATE_SELECTED:
                this.setBackgroundResource(R.drawable.gesture_pressed);
                break;
            case POINT_STATE_WRONG:
                this.setBackgroundResource(R.drawable.gesture_wrong);
                break;
            default:
                break;

        }
        //        invalidate();
    }

    public void setMode(Mode mode) {
        currentStatus = mode;
        postInvalidate();
//        invalidate();
    }

}
