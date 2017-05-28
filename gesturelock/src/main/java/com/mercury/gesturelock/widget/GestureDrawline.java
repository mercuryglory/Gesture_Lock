package com.mercury.gesturetest.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import com.mercury.gesturetest.StringUtils;
import com.mercury.gesturetest.common.AppUtil;
import com.mercury.gesturetest.common.Constant;
import com.mercury.gesturetest.entity.GesturePoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 手势密码路径绘制
 */
public class GestureDrawline extends View {
    private int mov_x;
    private int mov_y;

    private Map<String, GesturePoint> autoCheckPointMap;// 自动选中的情况点
    private boolean isDrawEnable = true; // 是否允许绘制

    /**
     * 构造函数
     */
    private int[]                                  screenDispaly;
    private Paint                                  paint;// 声明画笔
    private Canvas                                 canvas;// 画布
    private Bitmap                                 bitmap;// 位图
    private List<GesturePoint>                     list;// 装有各个view坐标的集合
    private List<Pair<GesturePoint, GesturePoint>> lineList;// 记录画过的线
    private StringBuilder                          passWordSb;
    private boolean                                isVerify;
    private String                                 passWord;
    private GestureCallBack                        callBack;
    private Handler                                mHandler;

    public GestureDrawline(Context context, List<GesturePoint> list,
                           boolean isVerify, String passWord, GestureCallBack callBack) {
        super(context);
        screenDispaly = AppUtil.getScreenDispaly(context);
        paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
        bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[0],
                Bitmap.Config.ARGB_8888); // 设置位图的宽高
        canvas = new Canvas();
        canvas.setBitmap(bitmap);// 用声明的画笔在位图上画点位

        paint.setStyle(Style.STROKE);// 设置非填充
        paint.setStrokeWidth(3);    //画笔宽度
        paint.setColor(Color.rgb(124, 163, 246));// 设置默认连线颜色
        paint.setAntiAlias(true);// 不显示锯齿

        this.list = list;
        this.lineList = new ArrayList<>();

        initAutoCheckPointMap();
        this.callBack = callBack;

        // 初始化密码缓存
        this.isVerify = isVerify;
        this.passWordSb = new StringBuilder();
        this.passWord = passWord;
        mHandler = new Handler();
    }

    private void initAutoCheckPointMap() {
        autoCheckPointMap = new HashMap<>();
        autoCheckPointMap.put("1,3", getGesturePointByNum(2));
        autoCheckPointMap.put("1,7", getGesturePointByNum(4));
        autoCheckPointMap.put("1,9", getGesturePointByNum(5));
        autoCheckPointMap.put("2,8", getGesturePointByNum(5));
        autoCheckPointMap.put("3,7", getGesturePointByNum(5));
        autoCheckPointMap.put("3,9", getGesturePointByNum(6));
        autoCheckPointMap.put("4,6", getGesturePointByNum(5));
        autoCheckPointMap.put("7,9", getGesturePointByNum(8));
    }

    private GesturePoint getGesturePointByNum(int num) {
        for (GesturePoint point : list) {
            if (point.getNum() == num) {
                return point;
            }
        }
        return null;
    }

    /**********************************************************
     * 画位图
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paint);

    }

    /**
     * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
     *
     * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
     */
    private GesturePoint getPointAt(int x, int y) {

        for (GesturePoint point : list) {
            // 先判断x
            int leftX = point.getLeftX();
            int rightX = point.getRightX();
            if (!(x >= leftX && x < rightX)) {
                // 如果为假，则跳到下一个对比
                continue;
            }

            int topY = point.getTopY();
            int bottomY = point.getBottomY();
            if (!(y >= topY && y < bottomY)) {
                // 如果为假，则跳到下一个对比
                continue;
            }

            // 如果执行到这，那么说明当前点击的点的位置在遍历到点的位置这个地方
            return point;
        }

        return null;
    }

    /**
     * 清掉屏幕上所有的线，然后画出集合里面的线
     */
    private void clearScreenAndDrawList() {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        for (Pair<GesturePoint, GesturePoint> pair : lineList) {
            // drawLine(float startX, float startY, float stopX, float stopY,
            // Paint paint)
            canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                    pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
        }
    }

    /**
     * 判断是否中间点需要选中
     *
     * @param pointStart
     * @param pointEnd
     * @return
     */
    private GesturePoint getBetweenCheckPoint(GesturePoint pointStart,
                                              GesturePoint pointEnd) {
        int startNum = pointStart.getNum();
        int endNum = pointEnd.getNum();
        String key;
        if (startNum < endNum) {
            key = startNum + "," + endNum;
        } else {
            key = endNum + "," + startNum;
        }
        return autoCheckPointMap.get(key);
    }

    /**
     * 触摸事件
     */
    private GesturePoint currentPoint;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isDrawEnable == false) {
            // 如果圆点图片呈现底片--也就是二次绘制错误，在没有清除绘制的线段的情况下，不再允许绘制线条
            return true;
        }
        paint.setColor(Color.rgb(124, 163, 246));// 设置默认连线颜色
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 鼠标按下后，获取手指点位的xy坐标
                mov_x = (int) event.getX();
                mov_y = (int) event.getY();
                // 判断当前点击的位置是处于哪个点之内
                currentPoint = getPointAt(mov_x, mov_y);
                int a = 0;
                if (currentPoint != null) {
                    currentPoint.setPointState(Constant.POINT_STATE_SELECTED);
                    passWordSb.append(currentPoint.getNum());
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // 清掉屏幕上所有的线，然后画出集合里面的线--不然的话不是一条线
                clearScreenAndDrawList();

                // 得到当前移动位置是处于哪个点内
                GesturePoint pointAt = getPointAt((int) event.getX(),
                        (int) event.getY());
                // 代表当前用户手指处于点与点之前
                if (currentPoint == null && pointAt == null) {
                    return true;
                } else {// 代表用户的手指从点与点之间移动到了点上
                    if (currentPoint == null) {// 先判断当前的point是不是为null
                        // 如果为空，那么把手指移动到的点赋值给currentPoint
                        currentPoint = pointAt;
                        // 把currentPoint这个点设置选中为true;
                        currentPoint.setPointState(Constant.POINT_STATE_SELECTED);
                        passWordSb.append(currentPoint.getNum());
                    }
                }
                if (pointAt == null || currentPoint.equals(pointAt)) {
                    // 点击移动区域不在圆的区域，或者当前点击的点与当前移动到的点的位置相同
                    // 那么以当前的点中心为起点，以手指移动位置为终点画线
                    canvas.drawLine(currentPoint.getCenterX(),
                            currentPoint.getCenterY(), event.getX(), event.getY(),
                            paint);// 画线
                } else {
                    // 如果当前点击的点与当前移动到的点的位置不同
                    // 那么以前前点的中心为起点，以手移动到的点的位置画线
                    canvas.drawLine(currentPoint.getCenterX(),
                            currentPoint.getCenterY(), pointAt.getCenterX(),
                            pointAt.getCenterY(), paint);// 画线
                    pointAt.setPointState(Constant.POINT_STATE_SELECTED);

                    // 判断是否中间点需要选中
                    //                    GesturePoint betweenPoint = getBetweenCheckPoint
                    // (currentPoint,
                    //                            pointAt);
                    //                    if (betweenPoint != null
                    //                            && Constants.POINT_STATE_SELECTED != betweenPoint
                    //                            .getPointState()) {
                    //                        // 存在中间点并且没有被选中
                    //                        Pair<GesturePoint, GesturePoint> pair1 = new Pair<>(
                    //                                currentPoint, betweenPoint);
                    //                        lineList.add(pair1);
                    //                        passWordSb.append(betweenPoint.getNum());
                    //                        Pair<GesturePoint, GesturePoint> pair2 = new Pair<>(
                    //                                betweenPoint, pointAt);
                    //                        lineList.add(pair2);
                    //                        passWordSb.append(pointAt.getNum());
                    //                        // 设置中间点选中
                    //                        betweenPoint.setPointState(Constants
                    // .POINT_STATE_SELECTED);
                    //                        // 赋值当前的point;
                    //                        currentPoint = pointAt;
                    //                    } else {
                    if (!StringUtils.hasSame(passWordSb.toString(), pointAt.getNum())) {
                        Pair<GesturePoint, GesturePoint> pair = new Pair<>(
                                currentPoint, pointAt);
                        lineList.add(pair);
                        passWordSb.append(pointAt.getNum());
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    }


                    //                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 当手指抬起的时候
                if (currentPoint != null) {
                    if (lineList.size() == 0) {
                        currentPoint.setPointState(Constant.POINT_STATE_WRONG);
                    }
                    if (isVerify) {
                        // 手势密码校验
                        // 清掉屏幕上所有的线，只画上集合里面保存的线
                        if (passWord.equals(passWordSb.toString())) {
                            // 代表用户绘制的密码手势与传入的密码相同
                            callBack.checkedSuccess();
                        } else {
                            // 用户绘制的密码与传入的密码不同。
                            callBack.checkedFail();
                        }
                    } else {
                        callBack.onGestureCodeInput(passWordSb.toString());

                    }
                }

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 校验错误/两次绘制不一致提示
     */
    private void drawEndPathTip(boolean isCorrect) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        if (isCorrect) {
            paint.setColor(Color.rgb(124, 163, 246));   //设置正确线路颜色
            for (Pair<GesturePoint, GesturePoint> pair : lineList) {
                pair.first.setPointState(Constant.POINT_STATE_SELECTED);
                pair.second.setPointState(Constant.POINT_STATE_SELECTED);
                canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                        pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
            }
        } else {
            paint.setColor(Color.rgb(255, 166, 14));// 设置错误线路颜色
            for (Pair<GesturePoint, GesturePoint> pair : lineList) {
                pair.first.setPointState(Constant.POINT_STATE_WRONG);
                pair.second.setPointState(Constant.POINT_STATE_WRONG);
                canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                        pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
            }
        }


        invalidate();
    }

    /**
     * 指定时间去清除绘制的状态
     *
     * @param delayTime 延迟执行时间
     */
    public void clearDrawlineState(long delayTime, boolean isCorrect) {
        if (delayTime > 0) {
            // 绘制错误提示路线
            isDrawEnable = false;
            drawEndPathTip(isCorrect);
        }
        new Handler().postDelayed(new clearStateRunnable(), delayTime);
    }

    /**
     * 清除绘制状态的线程
     */
    final class clearStateRunnable implements Runnable {
        public void run() {
            // 重置passWordSb
            passWordSb = new StringBuilder();
            // 清空保存点的集合
            lineList.clear();
            // 重新绘制界面
            clearScreenAndDrawList();
            for (GesturePoint p : list) {
                p.setPointState(Constant.POINT_STATE_NORMAL);
            }
            invalidate();
            isDrawEnable = true;
        }
    }

    public interface GestureCallBack {

        /**
         * 用户设置/输入了手势密码
         */
        void onGestureCodeInput(String inputCode);

        /**
         * 代表用户绘制的密码与传入的密码相同
         */
        void checkedSuccess();

        /**
         * 代表用户绘制的密码与传入的密码不相同
         */
        void checkedFail();
    }

}
