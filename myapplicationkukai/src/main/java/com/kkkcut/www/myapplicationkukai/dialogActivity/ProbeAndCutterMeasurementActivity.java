package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.ConstantValue;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

/**
 *   探针和切割刀测量
 */

public class ProbeAndCutterMeasurementActivity extends AppCompatActivity implements View.OnClickListener{
    private View mDecorView;
    private int flag;
    private ProlificSerialDriver serialDriver;
    public   static final  int FlAG_PROBE_MEASUREMENT=1;  //探针测试
    public   static  final  int FlAG_CUTTER_MEASUREMENT=2; //刀具测量
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probe_and_cutter_measurement);
        serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
        this.getIntentData();
        initViews();
    }
    private void initViews(){
        mDecorView = getWindow().getDecorView();
        Button mBtnAutomobileClamp= (Button)findViewById(R.id.btn_automobile_clamp);
        mBtnAutomobileClamp.setOnClickListener(this);
        Button  mBtnDimpleClamp  =(Button)findViewById(R.id.btn_dimple_clamp);
        mBtnDimpleClamp.setOnClickListener(this);
        Button  mBtnSingleSidedClamp  =(Button)findViewById(R.id.btn_single_sided_clamp);
        mBtnSingleSidedClamp.setOnClickListener(this);
        Button  mBtnTubularClamp =(Button)findViewById(R.id.btn_tubular_clamp);
        mBtnTubularClamp.setOnClickListener(this);
        Button  mBtnClose =(Button)findViewById(R.id.btn_close_window);
        mBtnClose.setOnClickListener(this);
    }

    /**
     * 获得意图传过来的数据
     */
    private  void  getIntentData(){
        Intent   intent=getIntent();
        flag =intent.getIntExtra("flag",-1);
    }

    /**
     * 启动当前Activity
     */
    public  static  void startItselfActivity(Context context,int flag){
                Intent  intent=new Intent(context,ProbeAndCutterMeasurementActivity.class);
                intent.putExtra("flag",flag);
                FragmentActivity fragmentActivity  =(FragmentActivity)context;
                fragmentActivity.startActivityForResult(intent,1);

    }

    @Override
    public void onClick(View v) {
       String order;
         switch (v.getId()){
             case R.id.btn_automobile_clamp://1
                 if(flag ==FlAG_PROBE_MEASUREMENT){   //1代表探针高度
                     order=Instruction.sendProbeHeightCalibration("1");
                     if(serialDriver!=null){
                         serialDriver.write(order.getBytes(),order.length());
                     }
                 }else if(flag ==FlAG_CUTTER_MEASUREMENT){
                     order=Instruction.sendCutterKnifeHeightCalibration("1");
                     if(serialDriver!=null){
                         serialDriver.write(order.getBytes(),order.length());
                     }
                 }
                 break;
             case R.id.btn_dimple_clamp://2
                 if(flag ==FlAG_PROBE_MEASUREMENT){
                     order=Instruction.sendProbeHeightCalibration("6");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flag ==FlAG_CUTTER_MEASUREMENT){
                     order=Instruction.sendCutterKnifeHeightCalibration("6");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_single_sided_clamp:
                 if(flag ==FlAG_PROBE_MEASUREMENT){
                     order=Instruction.sendProbeHeightCalibration("16");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flag ==FlAG_CUTTER_MEASUREMENT){
                     order=Instruction.sendCutterKnifeHeightCalibration("16");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_tubular_clamp:
                 if(flag ==FlAG_PROBE_MEASUREMENT){
                     order=Instruction.sendProbeHeightCalibration("11");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flag ==FlAG_CUTTER_MEASUREMENT){
                     order=Instruction.sendCutterKnifeHeightCalibration("11");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_close_window:
                   this.finish();
                    return;
         }
         if(flag==FlAG_PROBE_MEASUREMENT){
             this.setResult(ConstantValue.START_MEASURE_PROBE);
         }else if(flag==FlAG_CUTTER_MEASUREMENT){
             this.setResult(ConstantValue.START_MEASURE_CUTTER);
         }
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
}
