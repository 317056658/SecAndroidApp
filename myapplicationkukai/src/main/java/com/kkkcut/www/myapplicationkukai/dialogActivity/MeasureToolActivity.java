package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.MessageEvent;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

/**
 * Created by Administrator on 2017/5/19.
 */

public class MeasureToolActivity extends AppCompatActivity implements View.OnClickListener{
    private View mDecorView;
    private int flags;
    private ProlificSerialDriver serialDriver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_decoder_dialog);
        serialDriver=ProlificSerialDriver.getInstance();
        mDecorView = getWindow().getDecorView();
        Intent   intent=getIntent();
        flags=intent.getIntExtra("Flags",0);
        initViews();
    }
    private void initViews(){
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

    @Override
    public void onClick(View v) {
       String order;
         switch (v.getId()){
             case R.id.btn_automobile_clamp://1
                 if(flags==1){   //1代表探针高度
                     order=Instruction.sendProbeHeightCalibration("1");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flags==2){
                     order=Instruction.sendCutterKnifeHeightCalibration("1");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_dimple_clamp://2
                 if(flags==1){
                     order=Instruction.sendProbeHeightCalibration("6");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flags==2){
                     order=Instruction.sendCutterKnifeHeightCalibration("6");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_single_sided_clamp:
                 if(flags==1){
                     order=Instruction.sendProbeHeightCalibration("16");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flags==2){
                     order=Instruction.sendCutterKnifeHeightCalibration("16");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_tubular_clamp:
                 if(flags==1){
                     order=Instruction.sendProbeHeightCalibration("11");
                     serialDriver.write(order.getBytes(),order.length());
                 }else if(flags==2){
                     order=Instruction.sendCutterKnifeHeightCalibration("11");
                     serialDriver.write(order.getBytes(),order.length());
                 }
                 break;
             case R.id.btn_close_window:
                   this.finish();
                    return;
         }
        EventBusUtils.post(new MessageEvent("",MessageEvent.MEASURE_TOOL_FINISH));
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
}
