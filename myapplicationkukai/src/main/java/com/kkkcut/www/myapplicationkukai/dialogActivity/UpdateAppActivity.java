package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.entity.UpdateAppInfo;
import com.kkkcut.www.myapplicationkukai.service.AppDownloadService;
import com.kkkcut.www.myapplicationkukai.utils.ActivityWindowUtils;
import com.kkkcut.www.myapplicationkukai.utils.CacheActivityUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.view.ProgressBarWithPercent;
import com.qiangxi.checkupdatelibrary.callback.DownloadCallback;
import com.qiangxi.checkupdatelibrary.http.HttpRequest;
import com.qiangxi.checkupdatelibrary.utils.ApplicationUtil;
import com.qiangxi.checkupdatelibrary.utils.NetWorkUtil;

import java.io.File;

public class UpdateAppActivity extends AppCompatActivity implements View.OnClickListener{
    private UpdateAppInfo updateAppInfo;
    private long  timeRange;  //时间范围
    private ProgressBarWithPercent  mProgressBarWithPercent;
    private Button mBtnUpdateApp,mBtnCancelUpdate;
    private TextView mTextHint;
    private MyApplication application;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWindowUtils.setScreenNoDormant(getWindow());
        setContentView(R.layout.activity_update_app_acitivity);
        CacheActivityUtils.addActivity(this);
        this.getIntentData();
        this.initViews();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //隐藏底部导航栏
        Tools.hideBottomUIMenu(getWindow().getDecorView());
    }

    /**
     * 初始化View
     */
    private void initViews(){
        TextView    appSize =(TextView)findViewById(R.id.tv_app_size);
        appSize.setText("APP大小:"+updateAppInfo.getFileSize());
        TextView    contentDescribe =(TextView)findViewById(R.id.tv_content_describe);
        contentDescribe.setText(updateAppInfo.getDesc());

        mBtnUpdateApp= (Button) findViewById(R.id.btn_update);
        mBtnUpdateApp.setOnClickListener(this);
        mBtnCancelUpdate = (Button) findViewById(R.id.btn_cancel_update);
        mBtnCancelUpdate.setOnClickListener(this);
        mTextHint =(TextView) findViewById(R.id.tv_text_hint);   //文本提示
        mTextHint.setOnClickListener(this);
        mProgressBarWithPercent=(ProgressBarWithPercent)findViewById(R.id.progressBarWithPercent);
        if(updateAppInfo.getState()==0){
            mProgressBarWithPercent.setVisibility(View.GONE);   //彻底隐藏
        }else if(updateAppInfo.getState()==1){
            mBtnCancelUpdate.setText("退出应用");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(updateAppInfo.getState()==1){
            if(keyCode==KeyEvent.KEYCODE_BACK){
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获得意图数据
     */
    private void   getIntentData(){
        Intent  intent=getIntent();
        updateAppInfo=intent.getParcelableExtra("updateAppInfo");
    }
    /**
     * 启动当前Activity
     * @param context
     * @param updateAppInfo
     */
    public static void  startItselfActivity(Context context, UpdateAppInfo updateAppInfo){
        Intent intent=new Intent(context,UpdateAppActivity.class);
        intent.putExtra("updateAppInfo",updateAppInfo);
        context.startActivity(intent);
    }

    /**
     * 下载
     */
    private void download() {
        HttpRequest.download(updateAppInfo.getUrl(),Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib","myapplicationkukai-v2.apk",new DownloadCallback() {
            @Override
            public void onDownloadSuccess(File file) {
                mTextHint.setVisibility(View.GONE);    //隐藏
                mProgressBarWithPercent.setVisibility(View.INVISIBLE);
                mBtnCancelUpdate.setVisibility(View.VISIBLE);
                mBtnUpdateApp.setVisibility(View.VISIBLE);
                ApplicationUtil.installApk(UpdateAppActivity.this, file);

            }

            @Override
            public void onProgress(long  currentProgress, long totalProgress) {
                mProgressBarWithPercent.setProgress((int)currentProgress);
                mProgressBarWithPercent.setMax((int)totalProgress);
            }

            @Override
            public void onDownloadFailure(String s) {   //下载失败
                    Toast.makeText(UpdateAppActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
          switch (v.getId()){
              case  R.id.btn_update:   //更新app
                  //防抖动,两次点击间隔小于1s都return;
                  if(updateAppInfo.getState()==0){
                      if (System.currentTimeMillis() - timeRange < 1000) {
                          return;
                      }
                      timeRange = System.currentTimeMillis();
                      if (!NetWorkUtil.hasNetConnection(this)) {
                          Toast.makeText(this, "当前无网络连接", Toast.LENGTH_SHORT).show();
                          return;
                      }
                      Intent intent = new Intent();
                      intent.putExtra("downloadUrl", updateAppInfo.getUrl());
                      intent.putExtra("filePath", Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib");
                      intent.putExtra("fileName", "myapplicationkukai-v2.apk");
                      intent.putExtra("iconResId", R.mipmap.pic01);
                      intent.putExtra("isShowProgress", true);
                      intent.putExtra("appName", "SEC");
                      intent.setClass(this, AppDownloadService.class);
                      startService(intent);
                      Toast.makeText(this, "正在后台为您下载", Toast.LENGTH_SHORT).show();
                      finish();
                  }else if(updateAppInfo.getState()==1){   //强制更新
                      //防抖动,两次点击间隔小于500ms都return;
                      if (System.currentTimeMillis() - timeRange < 500) {
                          return;
                      }
                      timeRange = System.currentTimeMillis();
                      if (!NetWorkUtil.hasNetConnection(this)) {
                          Toast.makeText(this, "当前无网络连接", Toast.LENGTH_SHORT).show();
                          return;
                      }
                      if ("点击安装".equals(mBtnUpdateApp.getText().toString().trim())) {
                          File file = new File(Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib", "myapplicationkukai-v2.apk");
                          if (file.exists()) {
                              ApplicationUtil.installApk(this, file);
                          } else {
                              download();
                          }
                          return;
                      }else {
                          mBtnUpdateApp.setVisibility(View.INVISIBLE);
                          mBtnCancelUpdate.setVisibility(View.INVISIBLE);
                          download();
                          mTextHint.setVisibility(View.VISIBLE);
                      }
                  }
                  break;
              case R.id.btn_cancel_update:  //取消更新app
                  if(updateAppInfo.getState()==1){    //退出应用
                      CacheActivityUtils.finishAllActivity();
                  }else if(updateAppInfo.getState()==0){
                      finish();
                  }
                  break;
          }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheActivityUtils.finishSingleActivity(this);   // 删除这个
    }
}
