package com.demo.gesturelock;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.demo.gesturelock.widget.GestureContentView;
import com.demo.gesturelock.widget.GestureDrawline.GestureCallBack;

/**
 * 手势密码设置界面
 */
public class GestureEditActivity extends Activity implements OnClickListener {
	// 2次绘制手势密码不正确的提示语
	private TextView mTextTip;
	// 手势密码绘制区域
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	// 重新设置手势密码
	private TextView mTextReset;
	// 是否是第一次绘制密码锁
	private boolean mIsFirstInput = true;
	// 初次绘制完毕密码锁，生成的密码
	private String mFirstPassword = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_edit);
		setUpViews();
		setUpListeners();
	}

	/**
	 * 判断初次绘制完毕生成的密码判断 判断是不是为空 判断密码数量是不是小于4
	 */
	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}

	private void setUpViews() {
		// 重新设置手势密码
		mTextReset = (TextView) findViewById(R.id.text_reset);
		// 默认不可点击
		mTextReset.setClickable(false);
		// 2次绘制手势密码不正确的提示语
		mTextTip = (TextView) findViewById(R.id.text_tip);
		// 手势密码绘制区域
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		/**
		 * 初始化一个显示各个点的viewGroup GestureContentView(Context context, boolean
		 * isVerify, String passWord, GestureCallBack callBack)
		 */
		mGestureContentView = new GestureContentView(this, false, "",
				new GestureCallBack() {
					@Override
					public void onGestureCodeInput(String inputCode) {
						// 验证输入的图案密码--如果密码为null。或者密码个数少于4个
						if (!isInputPassValidate(inputCode)) {
							mTextTip.setText(Html
									.fromHtml("<font color='#c70c1e'>最少链接4个点, 请重新输入</font>"));
							// 立刻清楚画的线段
							mGestureContentView.clearDrawlineState(0L);
							return;
						}
						if (mIsFirstInput) {
							// 第一次输入密码--保存第一次输入的密码，在进行跟第二次判断
							mFirstPassword = inputCode;
							// 第一次输入完毕后，立刻清楚画的线段
							mGestureContentView.clearDrawlineState(0L);
							// 设置可以重新设置密码锁的状态按钮
							mTextReset.setClickable(true);
							mTextReset
									.setText(getString(R.string.reset_gesture_code));
						} else {
							if (inputCode.equals(mFirstPassword)) {
								Toast.makeText(GestureEditActivity.this,
										"设置成功", Toast.LENGTH_SHORT).show();
								mGestureContentView.clearDrawlineState(0L);
								GestureEditActivity.this.finish();
							} else {
								mTextTip.setText(Html
										.fromHtml("<font color='#c70c1e'>与上一次绘制不一致，请重新绘制</font>"));
								// 左右移动动画
								Animation shakeAnimation = AnimationUtils
										.loadAnimation(
												GestureEditActivity.this,
												R.anim.shake);
								mTextTip.startAnimation(shakeAnimation);
								// 保持绘制的线，1.5秒后清除
								mGestureContentView.clearDrawlineState(1300L);
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
		mGestureContentView.setParentView(mGestureContainer);
	}

	/*****************************************************/
	private void setUpListeners() {
		mTextReset.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.text_reset:
			mIsFirstInput = true;
			mTextTip.setText(getString(R.string.set_gesture_pattern));
			break;
		default:
			break;
		}
	}

}
