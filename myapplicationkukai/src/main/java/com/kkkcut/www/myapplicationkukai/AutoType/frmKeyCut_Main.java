package com.kkkcut.www.myapplicationkukai.AutoType;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.PubulicKey.InputMainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.custom.Tools;
import com.kkkcut.www.myapplicationkukai.custom.menuPopupWindow;
import com.kkkcut.www.myapplicationkukai.entity.keyBlankInfo;

public class frmKeyCut_Main extends AppCompatActivity {
    Button imgbut;
    private View parent;
    View pop;
    menuPopupWindow  popupWindow;
    LinearLayout     fl;
    keyBlankInfo blank; //获得传过来的的数据



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audia2);
        blank = getIntent().getParcelableExtra("blank");
        String  mfgName=getIntent().getStringExtra("mfg_name");
        imgbut = (Button) findViewById(R.id.imgbut);
       TextView textView= (TextView)findViewById(R.id.tv);
        textView.setText(blank.getFirmName()+">"+mfgName+"一IC Card:"+blank.getIC_card());
//            Intent intent=getIntent();
//            int i =intent.getIntExtra("_id",-1);

        Log.d("名字", "onCreate: "+ mfgName);
       imgbut.setOnClickListener(listener);
        popupWindow=new menuPopupWindow(this,blank);
        popupWindow.setOnClickClose(new menuPopupWindow.OnClickCloseListener() {
            @Override
            public void onCloseAcivity() {
                  finish();
            }
        });
      // popupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
      // popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initBtn();//初始化按钮

    }
    public  void initBtn(){
       Button  btninput=(Button)findViewById(R.id.btnInput);
        btninput.setOnClickListener(RightClick);
        //代码查齿
       Button btncode=(Button)findViewById(R.id.btnCode);
        btncode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent=new Intent(frmKeyCut_Main.this,);
                //startActivity(intent);
            }
        });
        Button btndecode=(Button)findViewById(R.id.btnDecode);
        Button btnincode=(Button)findViewById(R.id.btnInCode);
        Button btnlacktooth=(Button)findViewById(R.id.btnLackTooth);
    }
    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Tools.isFastDoubleClick()){
                return;
            }
            //获得根布局
            View rootview = LayoutInflater.from(frmKeyCut_Main.this).inflate(R.layout.activity_audia2, null);
            popupWindow.showAtLocation(rootview,Gravity.BOTTOM, 100,0);
        }
    };

    public void retreat(View v) {
        finish();
    }

    View.OnClickListener  RightClick=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
              switch (view.getId()){
                  case  R.id.btnInput:
                      Intent  intent=new Intent(frmKeyCut_Main.this,InputMainActivity.class);
                      intent.putExtra("KBI",blank);
                      startActivity(intent);
                      break;
              }
        }
    };
}
