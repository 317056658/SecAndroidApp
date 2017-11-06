package com.kkkcut.www.myapplicationkukai.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.TransformProbeActivity;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import static com.kkkcut.www.myapplicationkukai.R.id.btn_close;

/**
 *
 */
public class FrmMaintenanceActivity extends AppCompatActivity implements View.OnClickListener{
   private View mDecorView;
    private Button mBtnAutomobileClamp,mBtnDimpleClamp,mBtnSingleSidedClamp,mBtnTubularClamp,mBtnCloseWindow;
    private Intent intent=new Intent();
    private  Intent mOpenDialogIntent=new Intent();
    private PopupWindow pw;
    private  View rootView;
    private View allCannotClick;
    private int flag;
    private boolean  isSpindleStart=true;   //主轴是否启动
    private  LinearLayout mLl1, mLl2;
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
                            activity.mLl1.setVisibility(View.VISIBLE);
                            activity.mLl2.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                            activity.allCannotClick.setVisibility(View.GONE);
                        }else if(activity.mDecoder.getVisibility()==View.INVISIBLE){
                            activity.mDecoder.setVisibility(View.VISIBLE);
                            activity.mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasureimage);
                            activity.mClose.setVisibility(View.VISIBLE);
                            activity.mLl1.setVisibility(View.VISIBLE);
                            activity.mLl2.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                            activity.allCannotClick.setVisibility(View.GONE);
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
                            activity.mLl1.setVisibility(View.VISIBLE);
                            activity.mLl2.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                            activity.allCannotClick.setVisibility(View.GONE);
                        }else if(activity.mDecoder.getVisibility()==View.INVISIBLE) {
                            activity. mDecoder.setVisibility(View.VISIBLE);
                            activity.mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasureimage);
                            activity. mClose.setVisibility(View.VISIBLE);
                            activity.mLl1.setVisibility(View.VISIBLE);
                            activity. mLl2.setVisibility(View.VISIBLE);
                            activity.mStop.setVisibility(View.INVISIBLE);
                            activity.allCannotClick.setVisibility(View.GONE);
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
                intent.setClass(this,ClampCalibrationActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_start_decoder_measure:  // 校准探针的高度
                allCannotClick.setOnClickListener(null);
                allCannotClick.setVisibility(View.VISIBLE);
                flag=1;
                pw.showAtLocation(rootView,Gravity.CENTER,0,0);
                Log.d("进来了1", "onClick: ");
                break;
            case R.id.btn_stop:   //停止操作
                serialDriver.write(Instruction.STOP_OPERATE.getBytes(),Instruction.STOP_OPERATE.length());
                break;
            case R.id.btn_start_cutter_measure:  //切割刀高度测量
                allCannotClick.setOnClickListener(null);
                allCannotClick.setVisibility(View.VISIBLE);
                flag=0;
                pw.showAtLocation(rootView,Gravity.CENTER,0,0);
                break;
            case R.id.btn_spindle:
                if(isSpindleStart){
                    serialDriver.write(Instruction.SPINDLE_START.getBytes(),Instruction.SPINDLE_START.length());
                    mSpindle.setText("OFF");
                    mSpindle.setBackgroundResource(R.drawable.frmmaintenance_spindlepressedimage);
                    isSpindleStart=false;
                }else {
                    //停止主轴
                    serialDriver.write(Instruction.SPINDLE_END.getBytes(),Instruction.SPINDLE_END.length());
                    mSpindle.setText("ON");
                    mSpindle.setBackgroundResource(R.drawable.frmmaintenance_spindleimage);
                    isSpindleStart=true;
                }
                break;
            case R.id.btn_millimeter:
                if(!unitsType.equals("mm")){
                    mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitmmpressedimage);
                    mOpenDialogIntent.setClass(this, MessageTipsActivity.class);
                    mOpenDialogIntent.putExtra("Type",1);
                    mOpenDialogIntent.putExtra("name","mm");
                    startActivityForResult(mOpenDialogIntent,11);
                }
                break;
            case R.id.btn_inch:
                if(!unitsType.equals("Inch")){
                    mInch.setBackgroundResource(R.drawable.frmmaintenance_unitmmpressedimage);
                    mOpenDialogIntent.setClass(this,MessageTipsActivity.class);
                    mOpenDialogIntent.putExtra("Type",1); //1代表是长度单位打开
                    mOpenDialogIntent.putExtra("name","Inch");
                    startActivityForResult(mOpenDialogIntent,11);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        mDecorView =  getWindow().getDecorView();
        getIntentData();
        initViews();
        initLengthUnits();
        initPopupWindow();
        serialDriver=ProlificSerialDriver.getInstance();
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

    public void initPopupWindow(){
        rootView= LayoutInflater.from(this).inflate(R.layout.activity_setup,null);
        View contentView  =LayoutInflater.from(this).inflate(R.layout.popupwindow_decoder_dialog,null);
        mBtnAutomobileClamp =(Button)contentView.findViewById(R.id.btn_automobile_clamp);
        mBtnAutomobileClamp.setOnClickListener(probeAndCutterListener);
        mBtnDimpleClamp =(Button)contentView.findViewById(R.id.btn_dimple_clamp);
        mBtnDimpleClamp.setOnClickListener(probeAndCutterListener);
        mBtnSingleSidedClamp =(Button)contentView.findViewById(R.id.btn_single_sided_clamp);
        mBtnSingleSidedClamp.setOnClickListener(probeAndCutterListener);
        mBtnTubularClamp =(Button)contentView.findViewById(R.id.btn_tubular_clamp);
        mBtnTubularClamp.setOnClickListener(probeAndCutterListener);
        mBtnCloseWindow =(Button)contentView.findViewById(R.id.btn_close_window);
        mBtnCloseWindow.setOnClickListener(probeAndCutterListener);
        pw=new PopupWindow(this);
        pw.setContentView(contentView);
        pw.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        pw.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    /**
     *  4个夹具型号的探针高度测量
     */
    View.OnClickListener  probeAndCutterListener=new View.OnClickListener() {
        private  String order;
        @Override
        public void onClick(View v) {
            if(flag==1){
                switch (v.getId()){
                    case R.id.btn_automobile_clamp:   //1号夹具
                        order=Instruction.sendProbeHeightCalibration("1");
                        serialDriver.write(order.getBytes(),order.length());
                        mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasuredisabledimage);
                        mCutter.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        pw.dismiss();
                        break;
                    case R.id.btn_dimple_clamp: //2号夹具
                        order=Instruction.sendProbeHeightCalibration("6");
                        serialDriver.write(order.getBytes(),order.length());
                        mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasuredisabledimage);
                        mCutter.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        pw.dismiss();
                        break;
                    case R.id.btn_single_sided_clamp://3号夹具
                        order=Instruction.sendProbeHeightCalibration("16");
                        serialDriver.write(order.getBytes(),order.length());
                        mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasuredisabledimage);
                        mCutter.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        pw.dismiss();
                        break;
                    case R.id.btn_tubular_clamp://4号夹具
                        order=Instruction.sendProbeHeightCalibration("11");
                        serialDriver.write(order.getBytes(),order.length());
                        mDecoder.setBackgroundResource(R.drawable.frmmaintenance_startdecodermeasuredisabledimage);
                        mCutter.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        pw.dismiss();
                        break;
                    case R.id.btn_close_window:
                        pw.dismiss();
                        break;
                }
            }else {
                switch (v.getId()){
                    case R.id.btn_automobile_clamp:   // 1号夹具  切割刀的高度
                        order=Instruction.sendCutterKnifeHeightCalibration("1");
                        mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasuredisabledimage);
                        mDecoder.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        serialDriver.write(order.getBytes(),order.length());
                        pw.dismiss();
                        break;
                    case R.id.btn_dimple_clamp:  //2号夹具   切割刀的高度
                        order=Instruction.sendCutterKnifeHeightCalibration("6");
                        mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasuredisabledimage);
                        mDecoder.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        serialDriver.write(order.getBytes(),order.length());
                        pw.dismiss();
                        break;
                    case R.id.btn_single_sided_clamp:  //3号夹具  切割刀的高度
                        order=Instruction.sendCutterKnifeHeightCalibration("16");
                        serialDriver.write(order.getBytes(),order.length());
                        mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasuredisabledimage);
                        mDecoder.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        pw.dismiss();
                        break;
                    case R.id.btn_tubular_clamp: //4号夹具 切割刀的高度
                        order=Instruction.sendCutterKnifeHeightCalibration("11");
                        serialDriver.write(order.getBytes(),order.length());
                        mCutter.setBackgroundResource(R.drawable.frmmaintenance_starttoolmeasuredisabledimage);
                        mDecoder.setVisibility(View.INVISIBLE);
                        mClose.setVisibility(View.INVISIBLE);
                        mLl1.setVisibility(View.INVISIBLE);
                        mLl2.setVisibility(View.INVISIBLE);
                        mStop.setVisibility(View.VISIBLE);  //显示停止按钮
                        pw.dismiss();
                        break;
                    case R.id.btn_close_window:
                        pw.dismiss();
                        break;
                }
            }
        }
    };

    private void initViews(){
        allCannotClick = findViewById(R.id.view_bg);
        allCannotClick.setVisibility(View.GONE);
        //关于我们按钮
        Button setup_btn= (Button)findViewById(R.id.btnAbout);
        setup_btn.setOnClickListener(listener);
        //decoder按钮
     mDecoder= (Button)findViewById(R.id.btn_start_decoder_measure);
        mDecoder.setOnClickListener(this);
        //cutter按钮
       mCutter= (Button)findViewById(R.id.btn_start_cutter_measure);
        mCutter.setOnClickListener(this);
        //关闭按钮
        mClose=(Button)findViewById(btn_close);
        mClose.setOnClickListener(listenerClose);
        //操作停止按钮
        mStop=(Button)findViewById(R.id.btn_stop);
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
        //布局1
        mLl1 =(LinearLayout)findViewById(R.id.ll_01);
        //布局2
        mLl2 =(LinearLayout)findViewById(R.id.ll_02);
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
        Log.d("FrmMaintenanceActivity", "onDestroy: ");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String name   =data.getStringExtra("data");
        if(requestCode==11&&resultCode==1){  //yes
            if(name.equals("mm")){
                SPutils.setLengthUnits(null,name);
                mInch.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
                unitsType=name;
            }else if(name.equals("Inch")){
                SPutils.setLengthUnits(null,name);
                mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
                unitsType=name;
            }
        }else if(requestCode==11&&resultCode==2){  //no
            Log.d("数据是？", "onActivityResult: "+name);
            if(name.equals("mm")){
                mMillimeter.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
            }else if(name.equals("Inch")){
                mInch.setBackgroundResource(R.drawable.frmmaintenance_unitinchimage);
            }
        }
    }
}
