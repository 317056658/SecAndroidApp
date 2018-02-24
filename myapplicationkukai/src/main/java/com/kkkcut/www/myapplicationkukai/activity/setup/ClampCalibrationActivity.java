package com.kkkcut.www.myapplicationkukai.activity.setup;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.TransformProbeActivity;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.utils.ActivityWindowUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.lang.ref.WeakReference;

import static com.kkkcut.www.myapplicationkukai.entity.Instruction.STOP_OPERATE;

/**
 * 夹具校准界面
 */

public class ClampCalibrationActivity extends AppCompatActivity implements View.OnClickListener{
  private View mDecorView;
    private    Button mBtnStart, mBtnClampMove, mBtnCloseActivity, mBtnStopOperate, mBtnManualSet;
    private PopupWindow pw;
    private PopupWindow pw1;
    private PopupWindow pw2;
    private PopupWindow pw3;
    private PopupWindow inversionPopupWindow;
    private  View   rootView;
     private LinearLayout mLlClamp1, mLlClamp2, mLlClamp3, mLlClamp4;
   private ImageView mIvCalibrationStartTips, mIvClamp1CalibrationSend, mIvClamp2CalibrationSend, mIvClamp3CalibrationSend;
    private ImageView mIvCalibrateClamp1, mIvCalibrateClamp2, mIvCalibrateClamp3, mIvCalibrateClamp4;
    private TextView mTvCalibrationWait;
    private RelativeLayout mRlCutterDistance;
    private int token;  //令牌
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }
        Intent intentException=new Intent();
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ClampCalibrationActivity activity = (ClampCalibrationActivity) reference.get();
            if(activity != null) {
                String data = "";
                if (msg.obj != null) {
                    data = msg.obj.toString();
                }
                switch (msg.what) {
                    case MicyocoEvent.CUT_KNIFE_SWITCHOVER_PROBE://代表探针和切割刀的距离
                        Intent intent = new Intent(activity, TransformProbeActivity.class);
                        activity.startActivity(intent);
                        break;
                    case MicyocoEvent.OPERATION_FINISH://操作完成
                        if (activity.mTvCalibrationWait.getVisibility() == View.VISIBLE) {  //探针和切割刀校准完成
                            Log.d("进来starts？", "onMessageEventMain: ");
                            activity.mBtnStart.setBackgroundResource(R.drawable.btncalibprobecutterdistanceimage);
                            activity.mBtnStart.setText("Start");
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.mTvCalibrationWait.setVisibility(View.INVISIBLE);
                        } else if (activity.token == 1) {   //1号夹具
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mIvCalibrateClamp1.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.token = 0;
                        } else if (activity.token == 2) {
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp2.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.token = 0;
                        } else if (activity.token == 3) {
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp3.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.token = 0;
                        } else if (activity.token == 4) {
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mIvCalibrateClamp4.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.token = 0;
                        }
                        break;
                    case MicyocoEvent.USER_CANCEL_OPERATE://操作取消
                        if (activity.mTvCalibrationWait.getVisibility() == View.VISIBLE) {
                            activity.mBtnStart.setBackgroundResource(R.drawable.btncalibprobecutterdistanceimage);
                            activity.mBtnStart.setText("Start");
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.mTvCalibrationWait.setVisibility(View.INVISIBLE);
                        } else if (activity.token == 1) {   //1号夹具
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mIvCalibrateClamp1.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.token = 0;
                        } else if (activity.token == 2) {  //2
                            Log.d("来的撒", "handleMessage: ");
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mIvCalibrateClamp2.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.token = 0;
                        } else if (activity.token == 3) {
                            activity.mIvCalibrateClamp3.setClickable(true);
                            activity.mIvCalibrateClamp3.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp1.setClickable(true);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp2.setClickable(true);
                            activity.mLlClamp4.setVisibility(View.VISIBLE);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.mBtnStart.setClickable(true);
                            activity.mBtnClampMove.setClickable(true);
                            activity.mBtnCloseActivity.setClickable(true);
                            activity.mBtnManualSet.setClickable(true);
                            activity.token = 0;
                        } else if (activity.token == 4) {
                            activity.mRlCutterDistance.setVisibility(View.VISIBLE);
                            activity.mLlClamp1.setVisibility(View.VISIBLE);
                            activity.mLlClamp2.setVisibility(View.VISIBLE);
                            activity.mLlClamp3.setVisibility(View.VISIBLE);
                            activity.mBtnClampMove.setVisibility(View.VISIBLE);
                            activity.mBtnManualSet.setVisibility(View.VISIBLE);
                            activity.mBtnCloseActivity.setVisibility(View.VISIBLE);
                            activity.mBtnStopOperate.setVisibility(View.INVISIBLE);
                            activity.mIvCalibrateClamp4.setClickable(true);
                            activity.mIvCalibrateClamp4.setImageResource(R.drawable.btncalibleftclamp1image);
                            activity.token = 0;
                        }
                        intentException.setClass(activity,ExceptionActivity.class);
                        intentException.putExtra("exception", data);
                        activity.startActivity(intentException);
                        break;
                    case MicyocoEvent.SAFETYGATE_OPEN:  //安全们打开
                        intentException.setClass(activity,ExceptionActivity.class);
                        intentException.putExtra("exception", data);
                        activity.startActivity(intentException);
                        break;
                }

            }
        }
    }
    private ProlificSerialDriver serialDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保健当前屏幕永不休眠
        ActivityWindowUtils.setScreenNoDormant(getWindow());
        setContentView(R.layout.activity_clamp_calibration);
        mDecorView =getWindow().getDecorView();
        initViews();//初始化按键
        initPopupWindow();//初始化PopupWindow窗口
        serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(serialDriver!=null){
            serialDriver.setHandler(mHandler);
        }
    }
    private PowerManager.WakeLock mWakeLock;
    private void acquireWakeLock() {
        if(mWakeLock == null) {
            PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                    this.getClass().getCanonicalName());
            mWakeLock.acquire();

        }

    }

    private void releaseWakeLock() {
        if(mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }


    private  void  initViews(){
        mBtnClampMove =(Button)findViewById(R.id.btn_move);
        mBtnClampMove.setOnClickListener(this);
        mBtnStopOperate =(Button)findViewById(R.id.btn_stop_operate);
        mBtnStopOperate.setVisibility(View.INVISIBLE);
        mBtnStopOperate.setOnClickListener(this);
        mBtnManualSet =(Button)findViewById(R.id.btn_manual_set);

        mBtnCloseActivity =(Button)findViewById(R.id.btn_close_activity);
        mBtnCloseActivity.setOnClickListener(this);
        mBtnStart =(Button)findViewById(R.id.btn_start);
        mBtnStart.setOnClickListener(this);
        mRlCutterDistance =(RelativeLayout)findViewById(R.id.rl_0);
        mLlClamp1 = (LinearLayout)findViewById(R.id.ll_fixture1);
        mLlClamp2 = (LinearLayout)findViewById(R.id.ll_fixture2);
        mLlClamp3 = (LinearLayout)findViewById(R.id.ll_fixture3);
        mLlClamp4 = (LinearLayout)findViewById(R.id.ll_fixture4);

        mIvCalibrateClamp1 =(ImageView)findViewById(R.id.iv_calibrate_fixture1);
        mIvCalibrateClamp1.setOnClickListener(this);
        mIvCalibrateClamp2 =(ImageView)findViewById(R.id.iv_calibrate_fixture2);
        mIvCalibrateClamp2.setOnClickListener(this);
        mIvCalibrateClamp3 =(ImageView)findViewById(R.id.iv_calibrate_fixture3);
        mIvCalibrateClamp3.setOnClickListener(this);
        mIvCalibrateClamp4 =(ImageView)findViewById(R.id.iv_calibrate_fixture4);
        mIvCalibrateClamp4.setOnClickListener(this);

        mTvCalibrationWait =(TextView) findViewById(R.id.tv_calibration_wait);
        mTvCalibrationWait.setVisibility(View.INVISIBLE);

    }
    private  void initPopupWindow(){
        rootView =LayoutInflater.from(this).inflate(R.layout.activity_clamp_calibration,null);
        // 获得内容布局
          View contentView= LayoutInflater.from(this).inflate(R.layout.popupwindow_first_calibration_block_hint,null);
        mIvCalibrationStartTips =(ImageView)contentView.findViewById(R.id.iv_first_fixture_Calibration_block);
        pw=new PopupWindow();
        pw.setContentView(contentView);
        pw.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //Start 的pop
        mIvCalibrationStartTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBtnClampMove.setVisibility(View.INVISIBLE);
                mBtnManualSet.setVisibility(View.INVISIBLE);
                mBtnCloseActivity.setVisibility(View.INVISIBLE);
                mLlClamp1.setVisibility(View.INVISIBLE);
                mLlClamp2.setVisibility(View.INVISIBLE);
                mLlClamp3.setVisibility(View.INVISIBLE);
                mLlClamp4.setVisibility(View.INVISIBLE);
                //探针和切割刀之间的距离  发送 !TC0;!BP1;!DR1; 返回1：!PE0,0,1;  返回2：!NP
             int res   = serialDriver.write(Instruction.PROBE_AND_KNIFE_DISTANCE.getBytes(),Instruction.PROBE_AND_KNIFE_DISTANCE.length());
               if(res>0){
                   Log.d("命令发送成功", "onClick: 15=="+res);
               }else {
                   Log.d("命令发送失败", "onClick: 15=="+res);
               }
                pw.dismiss();
                mBtnStart.setBackgroundResource(R.drawable.btncalibleftclamp1disabledimage);
                mBtnStart.setText("");
                mBtnStopOperate.setVisibility(View.VISIBLE);
                mTvCalibrationWait.setVisibility(View.VISIBLE);
            }
        });
        //1号 的pop
        pw1=new PopupWindow(this);
        View  contentView1  =LayoutInflater.from(this).inflate(R.layout.popupwindow_fixture1_calibration,null);
        pw1.setContentView(contentView1); //设置内容布局
        pw1.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw1.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw1.setBackgroundDrawable(new ColorDrawable());
        mIvClamp1CalibrationSend = (ImageView)contentView1.findViewById(R.id.iv_firstFixture1Calibration);
        mIvClamp1CalibrationSend.setOnClickListener(this);

        //2号 的pop
        pw2=new PopupWindow(this);
        View  contentView2  =LayoutInflater.from(this).inflate(R.layout.popupwindow_fixture2_calibration,null);
        pw2.setContentView(contentView2); //设置内容布局
        pw2.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw2.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw2.setBackgroundDrawable(new ColorDrawable());
        mIvClamp2CalibrationSend =(ImageView)contentView2.findViewById(R.id.iv_firstFixture2Calibration);
        mIvClamp2CalibrationSend.setOnClickListener(this);

        //3号 的pop
        pw3=new PopupWindow(this);
        View  contentView3  =LayoutInflater.from(this).inflate(R.layout.popupwindow_fixture3_calibration,null);
        pw3.setContentView(contentView3); //设置内容布局
        pw3.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw3.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw3.setBackgroundDrawable(new ColorDrawable());
        mIvClamp3CalibrationSend =(ImageView)contentView3.findViewById(R.id.iv_firstFixture3Calibration);
        mIvClamp3CalibrationSend.setOnClickListener(this);
            //把探针换成切割的PopupWindow提示操作
      View   transformProbeView =LayoutInflater.from(this).inflate(R.layout.activity_transform_probe,null);
        inversionPopupWindow =new PopupWindow(this);
        inversionPopupWindow.setContentView(transformProbeView);
        inversionPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        inversionPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        inversionPopupWindow.setBackgroundDrawable(new ColorDrawable());
        transformProbeView.findViewById(R.id.iv_transform_probe).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialDriver.write(Instruction.TRANSFORM_PROBE.getBytes(),Instruction.TRANSFORM_PROBE.length());
                inversionPopupWindow.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }


    @Override
    public void onClick(View view) {
        String order;
          switch (view.getId()){
              case  R.id.btn_start:
                      mBtnStart.setBackgroundResource(R.drawable.btncalibprobecutterdistancepressedimage);
                      mBtnStart.setClickable(false);
                      mBtnClampMove.setClickable(false);
                      mBtnCloseActivity.setClickable(false);
                      mBtnManualSet.setClickable(false);
                      mIvCalibrateClamp1.setClickable(false);
                      mIvCalibrateClamp2.setClickable(false);
                      mIvCalibrateClamp3.setClickable(false);
                      mIvCalibrateClamp4.setClickable(false);
                      pw.showAtLocation(rootView, Gravity.CENTER,0,0);//显示PopupWindow
                  break;
              case R.id.btn_close_activity:  //关闭当前Activity，回到主Activity
                     finish();
                  break;
              case R.id.btn_stop_operate:  //取消操作
                  serialDriver.write(STOP_OPERATE.getBytes(),STOP_OPERATE.length());
                  break;
              case R.id.iv_calibrate_fixture1:  //一号夹具开始校准事件
                  mIvCalibrateClamp1.setImageResource(R.drawable.btncalibleftclamp1disabledimage);
                  mIvCalibrateClamp1.setClickable(false);
                  mIvCalibrateClamp2.setClickable(false);
                  mIvCalibrateClamp3.setClickable(false);
                  mIvCalibrateClamp4.setClickable(false);
                  mBtnStart.setClickable(false);
                  mBtnClampMove.setClickable(false);
                  mBtnManualSet.setClickable(false);
                  mBtnCloseActivity.setClickable(false);
                  pw1.showAtLocation(rootView, Gravity.CENTER,0,0);
                  token=1;
                  break;
              case R.id.iv_calibrate_fixture2://二号夹具开始校准事件
                  mIvCalibrateClamp2.setImageResource(R.drawable.btncalibleftclamp1disabledimage);
                  mIvCalibrateClamp1.setClickable(false);
                  mIvCalibrateClamp2.setClickable(false);
                  mIvCalibrateClamp3.setClickable(false);
                  mIvCalibrateClamp4.setClickable(false);
                  mBtnStart.setClickable(false);
                  mBtnClampMove.setClickable(false);
                  mBtnCloseActivity.setClickable(false);
                  mBtnManualSet.setClickable(false);
                  mBtnCloseActivity.setClickable(false);
                  pw2.showAtLocation(rootView,Gravity.CENTER,0,0);
                  token=2;
                  break;
              case R.id.iv_calibrate_fixture3://三号夹具开始校准事件
                  Log.d("3", "onClick: ");
                  mIvCalibrateClamp3.setImageResource(R.drawable.btncalibleftclamp1disabledimage);
                  mIvCalibrateClamp1.setClickable(false);
                  mIvCalibrateClamp2.setClickable(false);
                  mIvCalibrateClamp3.setClickable(false);
                  mIvCalibrateClamp4.setClickable(false);
                  mBtnStart.setClickable(false);
                  mBtnClampMove.setClickable(false);
                  mBtnCloseActivity.setClickable(false);
                  mBtnManualSet.setClickable(false);
                  pw3.showAtLocation(rootView, Gravity.CENTER,0,0);
                  token=3;
                  break;
              case R.id.iv_calibrate_fixture4://发指令4号夹具校准事件
                  mIvCalibrateClamp4.setImageResource(R.drawable.btncalibleftclamp1disabledimage);
                  mIvCalibrateClamp4.setClickable(false);
                  order =Instruction.sendFixtureCalibration("3");
                  serialDriver.write(order.getBytes(), order.length());
                  mRlCutterDistance.setVisibility(View.INVISIBLE);
                  mLlClamp1.setVisibility(View.INVISIBLE);
                  mLlClamp2.setVisibility(View.INVISIBLE);
                  mLlClamp3.setVisibility(View.INVISIBLE);
                  mBtnClampMove.setVisibility(View.INVISIBLE);
                  mBtnManualSet.setVisibility(View.INVISIBLE);
                  mBtnCloseActivity.setVisibility(View.INVISIBLE);
                  mBtnStopOperate.setVisibility(View.VISIBLE);
                  token=4;
                  break;
              case R.id.btn_move:
                  serialDriver.write(Instruction.FIXTURE_MOVE.getBytes(),Instruction.FIXTURE_MOVE.length());
                  break;
              case R.id.iv_firstFixture1Calibration://发指令1号夹具校准
                  order =Instruction.sendFixtureCalibration("0");
                  serialDriver.write(order.getBytes(), order.length());
                  pw1.dismiss();
                  mRlCutterDistance.setVisibility(View.INVISIBLE);
                  mLlClamp2.setVisibility(View.INVISIBLE);
                  mLlClamp3.setVisibility(View.INVISIBLE);
                  mLlClamp4.setVisibility(View.INVISIBLE);
                  mBtnClampMove.setVisibility(View.INVISIBLE);
                  mBtnManualSet.setVisibility(View.INVISIBLE);
                  mBtnCloseActivity.setVisibility(View.INVISIBLE);
                  mBtnStopOperate.setVisibility(View.VISIBLE);
                  break;
              case R.id.iv_firstFixture2Calibration://发指令2号夹具校准
                  order =Instruction.sendFixtureCalibration("1");
                  serialDriver.write(order.getBytes(), order.length());
                  pw2.dismiss();
                  mRlCutterDistance.setVisibility(View.INVISIBLE);
                  mLlClamp1.setVisibility(View.INVISIBLE);
                  mLlClamp3.setVisibility(View.INVISIBLE);
                  mLlClamp4.setVisibility(View.INVISIBLE);
                  mBtnClampMove.setVisibility(View.INVISIBLE);
                  mBtnManualSet.setVisibility(View.INVISIBLE);
                  mBtnCloseActivity.setVisibility(View.INVISIBLE);
                  mBtnStopOperate.setVisibility(View.VISIBLE);
                  break;
              case R.id.iv_firstFixture3Calibration://发指令3号夹具校准
                  order =Instruction.sendFixtureCalibration("2");
                  serialDriver.write(order.getBytes(), order.length());
                  pw3.dismiss();
                  mRlCutterDistance.setVisibility(View.INVISIBLE);
                  mLlClamp1.setVisibility(View.INVISIBLE);
                  mLlClamp2.setVisibility(View.INVISIBLE);
                  mLlClamp4.setVisibility(View.INVISIBLE);
                  mBtnClampMove.setVisibility(View.INVISIBLE);
                  mBtnManualSet.setVisibility(View.INVISIBLE);
                  mBtnCloseActivity.setVisibility(View.INVISIBLE);
                  mBtnStopOperate.setVisibility(View.VISIBLE);
                  break;
          }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pw.isShowing())
        pw.dismiss();
        else if(pw1.isShowing())
        pw1.dismiss();
        else if(pw2.isShowing())
        pw2.dismiss();
        else if(pw3.isShowing())
        pw3.dismiss();
        mHandler.removeCallbacksAndMessages(null);
    }
}
