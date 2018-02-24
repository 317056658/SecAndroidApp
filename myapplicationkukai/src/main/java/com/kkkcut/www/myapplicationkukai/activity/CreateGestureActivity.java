package com.kkkcut.www.myapplicationkukai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.view.LockPatternIndicator;
import com.kkkcut.www.myapplicationkukai.view.LockPatternView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CreateGestureActivity extends AppCompatActivity implements View.OnClickListener{
    private LockPatternView mLockPatternView;
    private TextView mTvMessage;
    private LockPatternIndicator mLockPatternIndicator;
    private TextView mTvResetPwd;
    private List<LockPatternView.Cell> mChosenPattern = null;
    private static final long DELAYTIME = 600L;
    private HashMap<String,String>  languageMap;
    private View mDecorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gesture);
        this.initViews();
        this.getIntentData();
    }
    public  static  void startCreateGestureActivity(Context context,HashMap<String,String>  languageMap){
        Intent  intent=new Intent(context,CreateGestureActivity.class);
        intent.putExtra("language",languageMap);
        context.startActivity(intent);
    }
    private void getIntentData(){
         Intent intent=getIntent();
         languageMap = (HashMap<String, String>) intent.getSerializableExtra("language");
    }

    /**
     * 初始化View
     */
    private void initViews() {
        mDecorView=getWindow().getDecorView();
       Button mBtnBack= (Button)findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mLockPatternIndicator = (LockPatternIndicator) findViewById(R.id.lockPatterIndicator);
        mLockPatternView = (LockPatternView) findViewById(R.id.lockPatternView);
        mLockPatternView.setTactileFeedbackEnabled(true);  //开启震动模式
        mLockPatternView.setOnPatternListener(patternListener);
        mTvMessage = (TextView) findViewById(R.id.tv_message);
        mTvResetPwd = (TextView) findViewById(R.id.tv_reset_pwd);
        mTvResetPwd.setOnClickListener(this);
    }
    /**
     * 手势监听
     */
    private LockPatternView.OnPatternListener patternListener = new LockPatternView.OnPatternListener() {

        @Override
        public void onPatternStart() {
            mLockPatternView.removePostClearPatternRunnable();
            //updateStatus(Status.DEFAULT, null);
            mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
        }

        /**
         *  设置手势图案完成！！
         * @param pattern
         */

        @Override
        public void onPatternComplete(List<LockPatternView.Cell> pattern) {
            //Log.e(TAG, "--onPatternDetected--");
            if(mChosenPattern == null && pattern.size() >= 4) {
                mChosenPattern = new ArrayList<>(pattern);
                updateStatus(Status.CORRECT, pattern);
            } else if (mChosenPattern == null && pattern.size() < 4) {
                updateStatus(Status.LESSERROR, pattern);
            } else if (mChosenPattern != null) {
                if (mChosenPattern.equals(pattern)) {      //密码设置结束
                    updateStatus(Status.CONFIRMCORRECT, pattern);
                } else {
                    updateStatus(Status.CONFIRMERROR, pattern);
                }
            }
        }
    };
    /**
     * 更新状态
     * @param status
     * @param pattern
     */
    private void updateStatus(Status status, List<LockPatternView.Cell> pattern) {
        mTvMessage.setTextColor(getResources().getColor(status.colorId));
        mTvMessage.setText(status.strId);
        switch (status) {
            case DEFAULT:
                mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CORRECT:
                updateLockPatternIndicator();
                mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case LESSERROR:
                mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                break;
            case CONFIRMERROR:
                mLockPatternView.setPattern(LockPatternView.DisplayMode.ERROR);
                mLockPatternView.postClearPatternRunnable(DELAYTIME);
                break;
            case CONFIRMCORRECT:
                saveChosenPattern(pattern);
                mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                setLockPatternSuccess();
                break;
        }
    }

    /**
     * 更新 Indicator
     */
    private void updateLockPatternIndicator() {
        if (mChosenPattern == null)
            return;
        mLockPatternIndicator.setIndicator(mChosenPattern);
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case R.id.tv_reset_pwd:   //重新设置密码
                  mChosenPattern = null;
                  mLockPatternIndicator.setDefaultIndicator();
                  updateStatus(Status.DEFAULT, null);
                  mLockPatternView.setPattern(LockPatternView.DisplayMode.DEFAULT);
                  break;
              case R.id.btn_back:
                  finish();
                  break;
          }
    }
    /**
     * 成功设置了手势密码(跳到首页)
     */

    private void setLockPatternSuccess() {
        Toast.makeText(this, "create gesture success", Toast.LENGTH_SHORT).show();
              finish();

    }

    /**
     * 保存手势密码
     */
    private void saveChosenPattern(List<LockPatternView.Cell> cells) {
        String pwd="";
        for (int i = 0; i <cells.size() ; i++) {
              LockPatternView.Cell  cell=cells.get(i);
            pwd+=cell.getIndex();
        }

        SPutils.saveString(getApplication(), MyApplication.GESTURE_PASSWORD,pwd);
    }

    private enum Status {
        //默认的状态，刚开始的时候（初始化状态）
        DEFAULT(R.string.create_gesture_default, R.color.grey_a5a5a5),
        //第一次记录成功
        CORRECT(R.string.create_gesture_correct, R.color.grey_a5a5a5),
        //连接的点数小于4（二次确认的时候就不再提示连接的点数小于4，而是提示确认错误）
        LESSERROR(R.string.create_gesture_less_error, R.color.red_f4333c),
        //二次确认错误
        CONFIRMERROR(R.string.create_gesture_confirm_error, R.color.red_f4333c),
        //二次确认正确
        CONFIRMCORRECT(R.string.create_gesture_confirm_correct, R.color.grey_a5a5a5);

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
