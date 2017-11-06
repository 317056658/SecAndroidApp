package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class KeyCutOperationTipsActivity extends AppCompatActivity implements View.OnClickListener {
    private  int imgFlag,clamp;

    private View  mDecorView;
    private String order;
    private ProlificSerialDriver serialDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_cut_order);
        serialDriver=ProlificSerialDriver.getInstance();
        this.setFinishOnTouchOutside(true);
        mDecorView=getWindow().getDecorView();
        //获得意图
        Intent  intent=getIntent();
        imgFlag =intent.getIntExtra("ImgFlag",0);
        clamp=intent.getIntExtra("Clamp",1000);
        //获得切割指令
        order =intent.getStringExtra("Order");
        initViews();

    }
    private void  initViews(){
        ImageView mImgHint =(ImageView) findViewById(R.id.iv_hint);
        mImgHint.setOnClickListener(this);
        TextView mTvOperationHint =(TextView)findViewById(R.id.tv_operation_tips);
        if(imgFlag==2){
             mImgHint.setImageResource(R.drawable.standardkey5mmside);
            mTvOperationHint.setText("Fix a key in 5mm clamp.");
        }else if(imgFlag==1){
            mImgHint.setImageResource(R.drawable.standardkey3dot5mmside);
            mTvOperationHint.setText("Fix a key in 3.5mm clamp.");
        }
        if(clamp==1){
            mImgHint.setImageResource(R.drawable.keepclampclean);
            mTvOperationHint.setText("Clean the groove from chips.");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
          case R.id.iv_hint:   //切割钥匙
              if( serialDriver!=null){
                  serialDriver.write(order.getBytes(),order.length());
                  Log.e("切割钥匙指令", "onClick: "+order);
              }
              //返回
              setResult(2);
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
