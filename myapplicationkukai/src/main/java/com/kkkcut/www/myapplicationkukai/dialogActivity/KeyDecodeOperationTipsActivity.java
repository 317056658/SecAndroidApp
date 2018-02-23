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

public class KeyDecodeOperationTipsActivity extends AppCompatActivity {
    private String order;

  private ProlificSerialDriver  serialDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clamp_operation_tips);
        serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
        //可以点击外面取消窗口
        this.setFinishOnTouchOutside(true);
        ImageView  mImgHint=(ImageView)findViewById(R.id.iv_hint);
        TextView mTextTips= (TextView)findViewById(R.id.tv_tips);
        //获得传过来的意图
        Intent  intent=getIntent();
       int imgFlag =intent.getIntExtra("ImgFlag",0);
        order=intent.getStringExtra("Order");
        switch (imgFlag){
            case 2:  //2代表  代表大于5.0
                mImgHint.setImageResource(R.drawable.standardkey5mmside);
                mTextTips.setText("Fix a key in 5mm clamp.");
                break;
            case 1:  //1代表 只大于3.5
                mImgHint.setImageResource(R.drawable.standardkey3dot5mmside);
                mTextTips.setText("Fix a key in 3.5mm clamp.");
                break;
        }
        mImgHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(serialDriver==null){
                        Log.d("没有连接到设备", "onClick: ");
                    }else {
                        Log.d("读钥匙具体指令是", "onClick: "+order);
                        serialDriver.write(order.getBytes(),order.length());
                    }
                setResult(1);  //返回码也是1
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(getWindow().getDecorView());
    }
}
