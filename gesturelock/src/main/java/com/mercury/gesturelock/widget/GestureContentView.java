package com.mercury.gesturelock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.mercury.gesturelock.common.AppUtil;
import com.mercury.gesturelock.common.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mercury.gesturelock.widget.GestureView.Mode.POINT_STATE_SELECTED;


/**
 * 手势密码容器类
 */
public class GestureContentView extends ViewGroup {

    /**
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
    private int[]                          screenDispaly;
    // 将屏幕宽度分成3份
    private int                            blockWidth;
    // 9个点位的集合
    private List<GestureView> list;

    // 是否需要校验密码
    private boolean isVerify;
    private Context context;
    //校验密码
    /**
     * 构造函数
     */
    private Paint                                  paint;// 声明画笔
    private Canvas                                 canvas;// 画布
    private Bitmap                                 bitmap;// 位图
    private List<Pair<GestureView, GestureView>>   lineList;// 记录画过的线
    private StringBuilder                          passWordSb;
    private GestureCallBack        callBack;

    private String passWord = "12369";


    public GestureContentView(Context context) {
        this(context, null);
    }

    public GestureContentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureContentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // 获取屏幕宽度
        screenDispaly = AppUtil.getScreenDispaly(context);
        // 获取屏幕宽度的1/3
        blockWidth = screenDispaly[0] / 3;
        this.list = new ArrayList<>();
        this.lineList = new ArrayList<>();

        screenDispaly = AppUtil.getScreenDispaly(context);
        paint = new Paint(Paint.DITHER_FLAG);// 创建一个画笔
        bitmap = Bitmap.createBitmap(screenDispaly[0], screenDispaly[1],
                Bitmap.Config.ARGB_8888); // 设置位图的宽高
        canvas = new Canvas();
        canvas.setBitmap(bitmap);// 用声明的画笔在位图上画点位

        paint.setStyle(Paint.Style.STROKE);// 设置非填充
        paint.setStrokeWidth(3);    //画笔宽度
        paint.setColor(Color.rgb (124, 163, 246));// 设置默认连线颜色
        paint.setAntiAlias(true);// 不显示锯齿

        initAutoCheckPointMap();

        // 初始化手势密码操作生成的密码
        this.passWordSb = new StringBuilder();

        // 添加9个锁位
        addChild(context);

    }


    // 用来计算2个圆心之间的一半距离大小
    private int baseNum = 4;
    private int extra   = 20;

    private void addChild(Context context) {
        int radius = blockWidth / baseNum;
        for (int i = 0; i < 9; i++) {

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
            int topY = row * (blockWidth - radius);
            int bottomY = row * (blockWidth - radius) + radius * 2;
            // 构建圆点对象
            GestureView image = new GestureView(context, leftX, rightX, topY, bottomY, i + 1);
            // 添加9个圆点图标
            this.list.add(image);
            this.addView(image);

        }

        // 得到屏幕的宽度
        int width = screenDispaly[0];
        int height = screenDispaly[1];
        // 设置手势锁的宽度高度--以屏幕的宽为基准
        LayoutParams layoutParams = new LayoutParams(width, height);
        // 设置手势锁的宽度高度--以屏幕的宽为基准
        this.setLayoutParams(layoutParams);

    }


    /**
     * Created by wang.zhonghao on 2017/6/8
     * descript:  摆放手势锁圆点的位置,已适配屏幕
     */
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
            int topY = row * (blockWidth - radius);
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

    private int mov_x;
    private int mov_y;

    private Map<String, GestureView> autoCheckPointMap;// 自动选中的情况点
    private boolean isDrawEnable = true; // 是否允许绘制


    public void addGestureCallBack(GestureCallBack callBack) {
        this.callBack = callBack;
    }

    public void setVerify(boolean isVerify) {
        this.isVerify = isVerify;
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

    private GestureView getGesturePointByNum(int num) {
        for (GestureView point : list) {
            if (point.getNum() == num) {
                return point;
            }
        }
        return null;
    }


    /**
     * Created by wang.zhonghao on 2017/6/8
     * descript:  更新线段的绘制
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, paint);

    }

    /**
     * 通过点的位置去集合里面查找这个点是包含在哪个Point里面的
     *
     * @return 如果没有找到，则返回null，代表用户当前移动的地方属于点与点之间
     */
    private GestureView getPointAt(int x, int y) {

        for (GestureView point : list) {
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
        for (Pair<GestureView, GestureView> pair : lineList) {
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
    private GestureView getBetweenCheckPoint(GestureView pointStart,
                                             GestureView pointEnd) {
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
    private GestureView currentPoint;

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
                if (currentPoint != null) {
                    currentPoint.setMode(POINT_STATE_SELECTED);
                    passWordSb.append(currentPoint.getNum());
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // 清掉屏幕上所有的线，然后画出集合里面的线--不然的话不是一条线
                clearScreenAndDrawList();

                // 得到当前移动位置是处于哪个点内
                GestureView pointAt = getPointAt((int) event.getX(),
                        (int) event.getY());
                // 代表当前用户手指处于点与点之前
                if (currentPoint == null && pointAt == null) {
                    return true;
                } else {// 代表用户的手指从点与点之间移动到了点上
                    if (currentPoint == null) {// 先判断当前的point是不是为null
                        // 如果为空，那么把手指移动到的点赋值给currentPoint
                        currentPoint = pointAt;
                        // 把currentPoint这个点设置选中为true;
                        currentPoint.setMode(POINT_STATE_SELECTED);
                        passWordSb.append(currentPoint.getNum());
                    }
                }

                if (pointAt == null || currentPoint.equals(pointAt)) {
                    // 点击移动区域不在圆的区域，或者当前点击的点与当前移动到的点的位置相同
                    // 那么以当前的点中心为起点，以手指移动位置为终点画线

                    //                    Log.e("left", currentPoint.getLeftX() + "");
                    //                    Log.e("right", currentPoint.getRightX() + "");
                    //                    Log.e("top", currentPoint.getTopY() + "");
                    //                    Log.e("bottom", currentPoint.getBottomY() + "");
                    //                    Log.e("eventX", event.getX() + "");
                    //                    Log.e("eventY", event.getY() + "");
                    canvas.drawLine(currentPoint.getCenterX(),
                            currentPoint.getCenterY(), event.getX(), event.getY(),
                            paint);// 画线
                } else {
                    // 如果当前点击的点与当前移动到的点的位置不同
                    // 那么以前前点的中心为起点，以手移动到的点的位置画线
                    canvas.drawLine(currentPoint.getCenterX(),
                            currentPoint.getCenterY(), pointAt.getCenterX(),
                            pointAt.getCenterY(), paint);// 画线
                    pointAt.setMode(POINT_STATE_SELECTED);

                    if (!StringUtils.hasSame(passWordSb.toString(), pointAt.getNum())) {
                        Pair<GestureView, GestureView> pair = new Pair<>(
                                currentPoint, pointAt);
                        lineList.add(pair);
                        passWordSb.append(pointAt.getNum());
                        // 赋值当前的point;
                        currentPoint = pointAt;
                    }

                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:// 当手指抬起的时候
                if (currentPoint != null) {
                    if (lineList.size() == 0) {
                        currentPoint.setMode(GestureView.Mode.POINT_STATE_WRONG);
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
            for (Pair<GestureView, GestureView> pair : lineList) {
                pair.first.setMode(POINT_STATE_SELECTED);
                pair.second.setMode(POINT_STATE_SELECTED);
                canvas.drawLine(pair.first.getCenterX(), pair.first.getCenterY(),
                        pair.second.getCenterX(), pair.second.getCenterY(), paint);// 画线
            }
        } else {
            paint.setColor(Color.rgb(255, 166, 14));// 设置错误线路颜色
            for (Pair<GestureView, GestureView> pair : lineList) {
                pair.first.setMode(GestureView.Mode.POINT_STATE_WRONG);
                pair.second.setMode(GestureView.Mode.POINT_STATE_WRONG);
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
            for (GestureView view : list) {
                view.setMode(GestureView.Mode.POINT_STATE_NORMAL);
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
