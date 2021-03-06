package com.demo.gesturelock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.mercury.gesturelock.widget.GestureContentView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 手势绘制/校验界面
 */
public class GestureVerifyActivity extends AppCompatActivity {

    @BindView(R.id.tv_phone)
    TextView           tvPhone;
    @BindView(R.id.tv_tip)
    TextView           tvTip;
    @BindView(R.id.gesture)
    GestureContentView gestureContainer;
    @BindView(R.id.tv_forgetPwd)
    TextView           tvForgetPwd;
    @BindView(R.id.tv_login)
    TextView           tvLogin;

    private int count = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_verify);
        ButterKnife.bind(this);
        setUpViews();
    }


    private void setUpViews() {
        // 手势密码 是用来校验功能的
        gestureContainer.setVerify(true);
        gestureContainer.addGestureCallBack(new GestureContentView.GestureCallBack() {
            @Override
            public void onGestureCodeInput(String inputCode) {

            }

            @Override
            public void checkedSuccess() {
                gestureContainer.clearDrawlineState(1000L, true);
                Toast.makeText(GestureVerifyActivity.this, "密码正确", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }

            @Override
            public void checkedFail() {
                count--;
                gestureContainer.clearDrawlineState(1000L, false);
                tvTip.setVisibility(View.VISIBLE);
                if (count > 0) {
                    tvTip.setText("密码错误，还可再输入" + count + "次");
                    tvTip.setTextColor(getResources().getColor(R.color.wrong));
                    // 左右移动动画
                    Animation shakeAnimation = AnimationUtils.loadAnimation
                            (GestureVerifyActivity.this, R.anim.shake);
                    tvTip.startAnimation(shakeAnimation);
                } else {
                    Toast.makeText(GestureVerifyActivity.this, "密码错误，请15分钟后再试", Toast
                            .LENGTH_SHORT).show();
                    finish();
                }
            }
        });

    }


    @OnClick({R.id.tv_forgetPwd, R.id.tv_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forgetPwd:
            case R.id.tv_login:
                ToastUtil.showToast(GestureVerifyActivity.this, "响应了");
                break;
        }
    }
}
