package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.Clamp;
import com.kkkcut.www.myapplicationkukai.entity.ConstantValue;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class OperateTipsActivity extends AppCompatActivity implements View.OnClickListener {
    private  int hint, keyType;
    private View  mDecorView;
    private String order;
    private ProlificSerialDriver serialDriver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_cut_order);
        serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
        this.setFinishOnTouchOutside(true);
        mDecorView=getWindow().getDecorView();
        getIntentData();
        initViews();

    }
    public static  void startItselfActivityForResult(Context context,int keyType,String order,int clampHint){
                Intent intent=new Intent(context,OperateTipsActivity.class);
                intent.putExtra("keyType",keyType);
                intent.putExtra("order",order);
                intent.putExtra("hint",clampHint);
                FragmentActivity fragmentActivity =(FragmentActivity)context;
                fragmentActivity.startActivityForResult(intent,1);
    }
    private void getIntentData(){
        //获得意图
        Intent intent=getIntent();
        hint =intent.getIntExtra("hint",-1);
        keyType =intent.getIntExtra("keyType",-1);  //钥匙类型
        //获得指令
        order =intent.getStringExtra("order");
    }
    private void  initViews(){
        ImageView mImgHint =(ImageView) findViewById(R.id.iv_hint);
        mImgHint.setOnClickListener(this);
        TextView mTvOperationHint =(TextView)findViewById(R.id.tv_operation_tips);
        switch (hint){
            case ConstantValue.PROBE_CUSP:
                mImgHint.setImageResource(R.drawable.changetooltodimpledecoder);
                mTvOperationHint.setText("Is dimple decoder ready?");
                break;
            case Clamp.CLAMP_GROOVE_CHIPS:  //清楚槽内碎屑
                mImgHint.setImageResource(R.drawable.keepclampclean);
                mTvOperationHint.setText("Clean the groove from chips.");
                break;
            case Clamp.THREE_NUMBER_CLAMP_THREE_POINT_FIVE_MM_AVAILABLE:
                mImgHint.setImageResource(R.drawable.standardkey3dot5mmside);
                mTvOperationHint.setText("Fix a key in 3.5mm clamp.");
                break;
            case Clamp.THREE_NUMBER_CLAMP_FIVE_MM_AVAILABLE:
                mImgHint.setImageResource(R.drawable.standardkey5mmside);
                mTvOperationHint.setText("Fix a key in 5mm clamp.");
                break;
            case  ConstantValue.B_TYPE_CUTTER:
                mImgHint.setImageResource(R.drawable.changetooltodimplecutter);
                mTvOperationHint.setText("Is dimple decoder ready?");
                break;
        }
//        switch (keyType){
//            case KeyInfo.BILATERAL_KEY:
//                break;
//            case KeyInfo.UNILATERAL_KEY:
//                if(hint ==2){
//                    mImgHint.setImageResource(R.drawable.standardkey5mmside);
//                    mTvOperationHint.setText("Fix a key in 5mm keyType.");
//                }else if(hint ==1){
//                    mImgHint.setImageResource(R.drawable.standardkey3dot5mmside);
//                    mTvOperationHint.setText("Fix a key in 3.5mm keyType.");
//                }
//                break;
//            case  KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY:
//                break;
//            case KeyInfo.MONORAIL_OUTER_GROOVE_KEY:
//                mImgHint.setImageResource(R.drawable.keepclampclean);
//                mTvOperationHint.setText("Clean the groove from chips.");
//                break;
//            case KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY:
//                mImgHint.setImageResource(R.drawable.keepclampclean);
//                mTvOperationHint.setText("Clean the groove from chips.");
//                break;
//            case KeyInfo.MONORAIL_INSIDE_GROOVE_KEY:
//                mImgHint.setImageResource(R.drawable.keepclampclean);
//                mTvOperationHint.setText("Clean the groove from chips.");
//                break;
//            case KeyInfo.CONCAVE_DOT_KEY:
//                break;
//            case KeyInfo.ANGLE_KEY:
//                break;
//            case KeyInfo.CYLINDER_KEY:
//                break;
//            case KeyInfo.SIDE_TOOTH_KEY:
//                break;
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
          case R.id.iv_hint:   //切割钥匙
              if(serialDriver!=null){
                  serialDriver.write(order.getBytes(),order.length());
              }
              //返回
              setResult(ConstantValue.START_OPERATE);
              finish();
              break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
}
