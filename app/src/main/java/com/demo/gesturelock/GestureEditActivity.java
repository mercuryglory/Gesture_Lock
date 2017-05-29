package com.demo.gesturelock;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercury.gesturelock.widget.GestureContentView;
import com.mercury.gesturelock.widget.GestureDrawline;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 手势密码设置界面
 */
public class GestureEditActivity extends AppCompatActivity {

    @Bind(R.id.tv_phone)
    TextView    tvPhone;
    @Bind(R.id.tv_tip)
    TextView    tvTip;
    @Bind(R.id.gesture_container)
    FrameLayout gestureContainer;
    @Bind(R.id.tv_continue)
    TextView    tvContinue;

    // 是否是第一次绘制密码锁
    private boolean mIsFirstInput  = true;
    // 初次绘制完毕密码锁，生成的密码
    private String  mFirstPassword = null;
    private GestureContentView mGestureContentView;
    private Handler            mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_edit);
        ButterKnife.bind(this);
        initData();
    }


    protected void initData() {
        mHandler = new Handler();
        setUpViews();
    }

    /**
     * 判断初次绘制完毕生成的密码判断 判断是不是为空 判断密码(连接点）数量是不是小于4
     */
    private boolean isInputPassValidate(String inputPassword) {
        if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
            return false;
        }
        return true;
    }

    private void setUpViews() {
        // 2次绘制手势密码不正确的提示语
        // 手势密码绘制区域
        /**
         * 初始化一个显示各个点的viewGroup GestureContentView(Context context, boolean
         * isVerify, String passWord, GestureCallBack callBack)
         */
        mGestureContentView = new GestureContentView(this, false, "",
                new GestureDrawline.GestureCallBack() {
                    @Override
                    public void onGestureCodeInput(String inputCode) {
                        // 验证输入的图案密码--如果密码为null。或者密码个数少于4个
                        if (!isInputPassValidate(inputCode)) {
                            inputWrong();
                            return;
                        }
                        if (mIsFirstInput) {
                            // 第一次输入密码--保存第一次输入的密码，再跟第二次比对
                            mFirstPassword = inputCode;
                            // 第一次正确的输入完毕后，保持线段1秒钟
                            mGestureContentView.clearDrawlineState(1000L, true);
                            tvTip.setText("请再次绘制手势密码");
                            tvTip.setTextColor(getResources().getColor(R.color.dark_grey));
                            // 设置可以重新设置密码锁的状态按钮
                        } else {
                            if (inputCode.equals(mFirstPassword)) {
                                mGestureContentView.clearDrawlineState(1000L, true);
                                ToastUtil.showToast(GestureEditActivity.this, "设置成功!");

                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 1000);
                            } else if (!isInputPassValidate(inputCode)) {
                                inputWrong();
                            } else {
                                tvTip.setText("两次手势密码不一致，请重新绘制");
                                tvTip.setTextColor(getResources().getColor(R.color.wrong));
                                // 左右移动动画
                                Animation shakeAnimation = AnimationUtils
                                        .loadAnimation(GestureEditActivity.this, R.anim.shake);
                                tvTip.startAnimation(shakeAnimation);
                                mGestureContentView.clearDrawlineState(1000L, false);
                                mIsFirstInput = true;
                                return;
                            }
                        }
                        mIsFirstInput = false;
                    }

                    @Override
                    public void checkedSuccess() {

                    }

                    @Override
                    public void checkedFail() {

                    }
                });
        // 设置手势解锁显示到哪个布局里面
        mGestureContentView.setParentView(gestureContainer);
    }

    private void inputWrong() {
        tvTip.setText("至少须4个以上连接点");
        tvTip.setTextColor(getResources().getColor(R.color.wrong));
        // 1秒后清除画的线段
        mGestureContentView.clearDrawlineState(1000L, false);
        mIsFirstInput = true;
        return;
    }

    @OnClick(R.id.tv_continue)
    public void onClick(View view) {
        final Dialog dialog = new Dialog(this, R.style.dialog_version_style);
        View view_dg = LayoutInflater.from(this).inflate(R.layout.dialog_gesture, null);
        TextView dgContinue = (TextView) view_dg.findViewById(R.id.dg_continue);
        TextView dgCancel = (TextView) view_dg.findViewById(R.id.dg_cancel);
        dialog.setContentView(view_dg);
        //跳过
        dgContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //                openActivity(GestureVerifyActivity.class);
                //                dialog.dismiss();

            }
        });
        //取消
        dgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

}
