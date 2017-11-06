package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

/**
 * 欢迎页
 */

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private View mDecorView;
    TextView mSkip;
    String  TAG="WelcomeActivity";
    private Context mContext;
    private String gesturePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //点击home键，再点击桌面启动图标时，
        // 系统会重启此activty，而不是直接打开之前已经打开过的activity，因此需要关闭此activity
        if(!isTaskRoot()){// 判断当前activity是不是所在任务栈的根
            finish();
            return;
        }
        setContentView(R.layout.activity_welcome);
        initViews();
        timer.start();
    }
    private void initViews(){
        mContext=this;
        mDecorView=getWindow().getDecorView();
        mSkip=  (TextView)findViewById(R.id.tv_skip);
        mSkip.setClickable(true);
        mSkip.setOnClickListener(this);
    }
    CountDownTimer timer=new CountDownTimer(4000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mSkip.setText("跳过("+millisUntilFinished/1000+")");
        }

        @Override
        public void onFinish() {
            String pwd= SPutils.getString(getApplication(),MyApplication.GESTURE_PASSWORD,"");
            if(TextUtils.isEmpty(pwd)){
                MainActivity.startMainActivity(mContext);
            }else {
                GestureLoginActivity.startGestureLoginActivity(mContext);
            }
            finish();

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_skip:  //跳过
                timer.cancel();
                String pwd= SPutils.getString(getApplication(),MyApplication.GESTURE_PASSWORD,"");
                if(TextUtils.isEmpty(pwd)){
                    MainActivity.startMainActivity(mContext);
                }else {
                    GestureLoginActivity.startGestureLoginActivity(mContext);
                }
                finish();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
