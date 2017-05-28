package com.mercury.gesturetest.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mercury.gesturetest.R;
import com.mercury.gesturetest.common.AppUtil;
import com.mercury.gesturetest.entity.GesturePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * 手势密码容器类
 */
public class GestureContentView extends ViewGroup {
    private GestureDrawline gestureDrawline;

    /************************************************************************
     * 包含9个ImageView的容器，初始化
     *
     * @param context
     * @param isVerify
     * 是否为校验手势密码
     * @param passWord
     * 用户传入密码
     * @param callBack
     * 手势绘制完毕的回调
     */
    private int[]              screenDispaly;
    // 将屏幕宽度分成3份
    private int                blockWidth;
    // 9个点位的集合
    private List<GesturePoint> list;
    // 环境
    private Context            context;
    // 是否需要校验密码
    private boolean            isVerify;

    public GestureContentView(Context context, boolean isVerify,
                              String passWord, GestureDrawline.GestureCallBack callBack) {
        super(context);
        // 获取屏幕宽度
        screenDispaly = AppUtil.getScreenDispaly(context);
        // 获取屏幕宽度的1/3
        blockWidth = screenDispaly[0] / 3;
        this.list = new ArrayList<>();
        this.context = context;
        this.isVerify = isVerify;
        // 添加9个图标
        addChild();
        // 初始化一个可以画线的view
        gestureDrawline = new GestureDrawline(context, list, isVerify,
                passWord, callBack);
    }

    // 用来计算2个圆心之间的一半距离大小
    private int baseNum = 4;
    private int extra   = 20;

    private void addChild() {
        int radius = blockWidth / baseNum;
        for (int i = 0; i < 9; i++) {
            ImageView image = new ImageView(context);
            image.setBackgroundResource(R.drawable.gesture_normal);
            this.addView(image);
            invalidate();
            // 第几行---012 0 ，345 1， 678 2--
            int row = i / 3;
            // 第几列---012 012 ， 345 012 ， 678 012
            int col = i % 3;
            // 定义点的每个属性
            int leftX;
            int rightX;
            if (col == 0) {
                leftX = blockWidth - radius * 2 - extra;
                rightX = blockWidth - extra;
            } else if (col == 2) {
                leftX = blockWidth * col + extra;
                rightX = blockWidth * col + radius * 2 + extra;
            } else {
                leftX = screenDispaly[0] / 2 - radius;
                rightX = screenDispaly[0] / 2 + radius;
            }
            //			int leftX = col * blockWidth + blockWidth / baseNum;
            int topY = row * (blockWidth - radius);
            //			int rightX = col * blockWidth + blockWidth - blockWidth / baseNum;
            int bottomY = row * (blockWidth - radius) + radius * 2 ;
            // 构建圆点对象
            GesturePoint p = new GesturePoint(leftX, rightX, topY, bottomY,
                    image, i + 1);
            // 添加9个圆点图标
            this.list.add(p);
        }
    }

    /**
     * 设置手势解锁显示到哪个布局里面
     */

    public void setParentView(ViewGroup parent) {
        // 得到屏幕的宽度
        int width = screenDispaly[0];
        int height = screenDispaly[1];
        // 设置手势锁的宽度高度--以屏幕的宽为基准
        LayoutParams layoutParams = new LayoutParams(width, height);
        // 设置手势锁的宽度高度--以屏幕的宽为基准
        this.setLayoutParams(layoutParams);
        // 将线路绘制也做同样的操作
        gestureDrawline.setLayoutParams(layoutParams);
        parent.addView(gestureDrawline);
        parent.addView(this);
    }

    /**************************************
     * 绘制圆点位操作
     ****************************************/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int radius = blockWidth / baseNum;
        // 循环获取里面的每一个圆点位
        for (int i = 0; i < getChildCount(); i++) {
            // 第几行
            int row = i / 3;
            // 第几列
            int col = i % 3;
            // 获取对应的圆点位
            View v = getChildAt(i);
            // 进行圆点位的绘制操作
            int leftX;
            int rightX;
            if (col == 0) {
                leftX = blockWidth - radius * 2 - extra;
                rightX = blockWidth - extra;
            } else if (col == 2) {
                leftX = blockWidth * col + extra;
                rightX = blockWidth * col + radius * 2 + extra;
            } else {
                leftX = screenDispaly[0] / 2 - radius;
                rightX = screenDispaly[0] / 2 + radius;
            }
            //			int leftX = col * blockWidth + blockWidth / baseNum;
            int topY = row * (blockWidth - radius);
            //			int rightX = col * blockWidth + blockWidth - blockWidth / baseNum;
            int bottomY = row * (blockWidth - radius) + radius * 2;
            v.layout(leftX, topY, rightX, bottomY);

            //			v.layout(col * blockWidth + blockWidth / baseNum, row * blockWidth
            //					+ blockWidth / baseNum, col * blockWidth + blockWidth
            //					- blockWidth / baseNum, row * blockWidth + blockWidth
            //					- blockWidth / baseNum);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 遍历设置每个子view的大小
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 对外暴露一个方法---用于清楚密码锁上的线段 保留路径delayTime时间长
     *
     * @param delayTime
     */
    public void clearDrawlineState(long delayTime, boolean isCorrect) {
        gestureDrawline.clearDrawlineState(delayTime, isCorrect);
    }

}
