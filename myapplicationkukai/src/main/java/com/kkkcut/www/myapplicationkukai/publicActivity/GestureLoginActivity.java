package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.view.LockPatternView;

import java.util.List;

public class GestureLoginActivity extends AppCompatActivity implements View.OnClickListener {
    private LockPatternView mLockPatternView;
    private TextView mTvHintMessage;
    private TextView mBtnForgetPwd;

    private String gesturePassword;
    private static final long DELAYTIME = 600l;
    private View mDecorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_login);
        this.initViews();
    }

    private void initViews() {
        mDecorView = getWindow().getDecorView();
        mTvHintMessage = (TextView) findViewById(R.id.tv_hint_message);
        mLockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        mLockPatternView.setOnPatternListener(patternListener);
        mLockPatternView.setTactileFeedbackEnabled(true);
        mBtnForgetPwd = (TextView) findViewById(R.id.tv_forget_password);
        mBtnForgetPwd.setOnClickListener(this);
        //获得密码
        gesturePassword = SPutils.getString(getApplication(), MyApplication.GESTURE_PASSWORD, "");
    }

    public static void startGestureLoginActivity(Context context) {
        Intent intent = new Intent(context, GestureLoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forget_password:  //忘记手势密码
                MainActivity.startMainActivity(this);
                finish();
                break;
        }
    }

    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            mLockPatternView.removePostClearPatternRunnable();
        }

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            if (pattern != null) {
                String pwd = "";
                for (int i = 0; i < pattern.size(); i++) {
                    LockPatternView.Cell cell = pattern.get(i);
                    pwd += cell.getIndex();
                }
                if (gesturePassword.equals(pwd)) {
                    updateStatus(Status.CORRECT);
                } else {
                    updateStatus(Status.ERROR);
                }
            }
        }
    };

    /**
     * 更新状态
     *
     * @param status
     */
    private void updateStatus(Status status) {
        mTvHintMessage.setText(status.strId);
        mTvHintMessage.setTextColor(getResources().getColor(status.colorId));
        switch (status) {
            case DEFAULT:
                mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case ERROR:
                mLockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                mLockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CORRECT:
                mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                loginGestureSuccess();
                break;
        }
    }

    /**
     * 手势登录成功（去首页）
     */
    private void loginGestureSuccess() {
        MainActivity.startMainActivity(this);
        finish();
    }

    private enum Status {
        //默认的状态
        DEFAULT(R.string.gesture_default, R.color.grey_a5a5a5),
        //密码输入错误
        ERROR(R.string.gesture_error, R.color.red_f4333c),
        //密码输入正确
        CORRECT(R.string.gesture_correct, R.color.grey_a5a5a5);

        private Status(int strId, int colorId) {
            this.strId = strId;
            this.colorId = colorId;
        }

        private int strId;
        private int colorId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
}
