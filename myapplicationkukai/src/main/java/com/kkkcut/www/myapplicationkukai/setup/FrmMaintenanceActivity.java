package com.kkkcut.www.myapplicationkukai.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ProbeAndCutterMeasurementActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.TransformProbeActivity;
import com.kkkcut.www.myapplicationkukai.entity.ConstantValue;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static com.kkkcut.www.myapplicationkukai.R.id.btn_close_activity;

/**
 *
 */
public class FrmMaintenanceActivity extends AppCompatActivity implements View.OnClickListener{
   private View mDecorView;
    private  Intent mOpenMessageTipsIntent =new Intent();
    private PopupWindow pw;
    private  View rootView;
    private int flag;
    private boolean  isSpindleStart=true;   //主轴是否启动
    private  LinearLayout mClampCalibration, mLinearLayout;
    private   Button mDecoder, mCutter,mClose,mStop,mSpindle,mBtnCalibDetachableClamp;
    private Button mMillimeter,mInch;
    private String unitsType;
    private ProlificSerialDriver serialDriver;
    private HashMap<String,String> languageMap;
    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrmMaintenanceActivity activity = (FrmMaintenanceActivity) reference.get();
            if(activity != null){
                switch (msg.what){
                    case MicyocoEvent.OPERATION_FINISH:
                        if(activity.mCutter.getVisibility()==View.INVISIBLE){
                            activity.mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasureimage);
                            activity.mCutter.setVisibility(View.VISIBLE);
                            activity.mClose.setVisibility(View.VISIBLE);
                            activity.mClampCalibration.setVisibility(View.VISIBLE);
                            activity.mLinearLayout.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                        }else if(activity.mDecoder.getVisibility()==View.INVISIBLE){
                            activity.mDecoder.setVisibility(View.VISIBLE);
                            activity.mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasureimage);
                            activity.mClose.setVisibility(View.VISIBLE);
                            activity.mClampCalibration.setVisibility(View.VISIBLE);
                            activity.mLinearLayout.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case MicyocoEvent.CUT_KNIFE_SWITCHOVER_PROBE:
                        Intent   intent =new Intent(activity,TransformProbeActivity.class);
                        activity.startActivity(intent);
                        break;
                    case MicyocoEvent.USER_CANCEL_OPERATE:  //用户操作取消
                        if(activity.mCutter.getVisibility()==View.INVISIBLE){
                            activity.mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasureimage);
                            activity.mCutter.setVisibility(View.VISIBLE);
                            activity.mClose.setVisibility(View.VISIBLE);
                            activity.mClampCalibration.setVisibility(View.VISIBLE);
                            activity.mLinearLayout.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                        }else if(activity.mDecoder.getVisibility()==View.INVISIBLE) {
                            activity. mDecoder.setVisibility(View.VISIBLE);
                            activity.mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasureimage);
                            activity. mClose.setVisibility(View.VISIBLE);
                            activity.mClampCalibration.setVisibility(View.VISIBLE);
                            activity.mLinearLayout.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);

                        }
                        String data=msg.obj.toString();
                        Intent  intentException=new Intent(activity,ExceptionActivity.class);
                        intentException.putExtra("exception",data);
                        activity.startActivity(intentException);
                        break;
                }
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnCalibDetachableClamp:   //第一次校准
                Intent intent=new Intent();
                intent.setClass(this,ClampCalibrationActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_open_decoder_measure:  // 校准探针的高度
                ProbeAndCutterMeasurementActivity.
                        startItselfActivity(this,ProbeAndCutterMeasurementActivity.FlAG_PROBE_MEASUREMENT);
                break;
            case R.id.btn_stop_operate:   //停止操作
                serialDriver.write(Instruction.STOP_OPERATE.getBytes(),Instruction.STOP_OPERATE.length());
                break;
            case R.id.btn_open_cutter_measure:  //切割刀高度测量
                ProbeAndCutterMeasurementActivity.
                        startItselfActivity(this,ProbeAndCutterMeasurementActivity.FlAG_CUTTER_MEASUREMENT);
                break;
            case R.id.btn_spindle:
                if(isSpindleStart){
                    Log.d("进来了？", "onClick: ");
                    serialDriver.write(Instruction.SPINDLE_START.getBytes(),Instruction.SPINDLE_START.length());
                 serialDriver.write(Instruction.TWEET_SHORT_THREE_SOUND.getBytes(),Instruction.TWEET_SHORT_THREE_SOUND.length());
                    mSpindle.setText("OFF");
                    mSpindle.setBackgroundResource(R.drawable.frmmaintenance_spindlepressedimage);
                    isSpindleStart=false;
                }else {
                    //停止主轴+
                    serialDriver.write(Instruction.SPINDLE_END.getBytes(),Instruction.SPINDLE_END.length());
                    serialDriver.write(Instruction.TWEET_SHORT_THREE_SOUND.getBytes(),Instruction.TWEET_SHORT_THREE_SOUND.length());
                    mSpindle.setText("ON");
                    mSpindle.setBackgroundResource(R.drawable.frmmaintenance_spindleimage);
                    isSpindleStart=true;
                }
                break;
            case R.id.btn_millimeter:   //毫米
                if(!unitsType.equals("mm")){
                    mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitmmpressedimage);
                    mOpenMessageTipsIntent.setClass(this, MessageTipsActivity.class);
                    mOpenMessageTipsIntent.putExtra("Type",1);
                    mOpenMessageTipsIntent.putExtra("name","mm");
                    startActivityForResult(mOpenMessageTipsIntent,11);
                }
                break;
            case R.id.btn_inch:   //英寸
                if(!unitsType.equals("Inch")){
                    mInch.setBackgroundResource(R.drawable.frmmaintenance_unitmmpressedimage);
                    MessageTipsActivity.startMessageTipsActivityForResult(this,MessageTipsActivity.LENGTH_UNIT_TIPS);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getIntentData();
        initViews();
        initLengthUnits();
        //获得连接的串口驱动
       serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
    }
    public  static  void startFrmMaintenanceActivity(Context context,HashMap<String,String> languageMap){
        Intent intent=new Intent(context,FrmMaintenanceActivity.class);
        intent.putExtra("language",languageMap);
        context.startActivity(intent);
    }
    private void getIntentData(){
         Intent intent=getIntent();
         languageMap = (HashMap<String, String>) intent.getSerializableExtra("language");
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

    @Override
    protected void onStop() {
        super.onStop();

    }
    private void initViews(){
        //关于我们按钮
        mDecorView =  getWindow().getDecorView();
        Button setup_btn= (Button)findViewById(R.id.btnAbout);
        setup_btn.setOnClickListener(listener);
        //decoder按钮
     mDecoder= (Button)findViewById(R.id.btn_open_decoder_measure);
        mDecoder.setOnClickListener(this);
        //cutter按钮
       mCutter= (Button)findViewById(R.id.btn_open_cutter_measure);
        mCutter.setOnClickListener(this);
        //关闭按钮
        mClose=(Button)findViewById(btn_close_activity);
        mClose.setOnClickListener(listenerClose);
        //操作停止按钮
        mStop=(Button)findViewById(R.id.btn_stop_operate);
        mStop.setOnClickListener(this);
        //第一次 校准
        mBtnCalibDetachableClamp= (Button)findViewById(R.id.btnCalibDetachableClamp);
        mBtnCalibDetachableClamp.setOnClickListener(this);
        //主轴切割刀测试转动
       mSpindle =(Button)findViewById(R.id.btn_spindle);
        mSpindle.setOnClickListener(this);
           //厘米
        mMillimeter=(Button)findViewById(R.id.btn_millimeter);
        mMillimeter.setOnClickListener(this);
            //英寸
        mInch=(Button)findViewById(R.id.btn_inch);
        mInch.setOnClickListener(this);
        //去夹具校准的布局
        mClampCalibration =(LinearLayout)findViewById(R.id.ll_clamp_calibration);
        //线性布局
        mLinearLayout =(LinearLayout)findViewById(R.id.linear_layout);
        //布局3
    }

    /**
     * 初始化长度单位
     */
    private void initLengthUnits(){
         unitsType  =SPutils.getLengthUnits(this);
        if(unitsType.equals("mm")){
            mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitmmpressedimage);
        }else if(unitsType.equals("Inch")){
            mInch.setBackgroundResource(R.drawable.frmmaintenance_unitmmpressedimage);
        }
    }
    View.OnClickListener  listenerClose=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Tools.btnSound(FrmMaintenanceActivity.this);
             finish();
        }
    };

    View.OnClickListener  listener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Tools.btnSound(FrmMaintenanceActivity.this);
            Intent  intent=new Intent(FrmMaintenanceActivity.this,FrmAboutActivity.class);
            startActivity(intent);
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pw!=null){
            pw.dismiss();
        }
       mHandler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==ConstantValue.YSE_SWITCHOVER_LENGTH_UNIT){  //yes
            if(unitsType.equals("mm")){
                SPutils.setLengthUnits(getApplicationContext(),"Inch");
                mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
                unitsType="Inch";
            }else if(unitsType.equals("Inch")){
                SPutils.setLengthUnits(getApplicationContext(),"mm");
                mInch.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
                unitsType="mm";
            }
        }else if(requestCode==11&&resultCode==ConstantValue.NO_SWITCHOVER_LENGTH_UNIT){  //no
            if(unitsType.equals("mm")){
                mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
            }else if(unitsType.equals("Inch")){
                mInch.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
            }
        }else if(requestCode==1&&resultCode==ConstantValue.START_MEASURE_PROBE){
            mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasuredisabledimage);
            mCutter.setVisibility(View.INVISIBLE);
            mClose.setVisibility(View.INVISIBLE);
            mClampCalibration.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.INVISIBLE);
            mStop.setVisibility(View.VISIBLE);  //显示停止按钮
        }else if(requestCode==1&&resultCode==ConstantValue.START_MEASURE_CUTTER){
            mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasuredisabledimage);
            mDecoder.setVisibility(View.INVISIBLE);
            mClose.setVisibility(View.INVISIBLE);
            mClampCalibration.setVisibility(View.INVISIBLE);
            mLinearLayout.setVisibility(View.INVISIBLE);
            mStop.setVisibility(View.VISIBLE);  //显示停止按钮
        }
    }

}
