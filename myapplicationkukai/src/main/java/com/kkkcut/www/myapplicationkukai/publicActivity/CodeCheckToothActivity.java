package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class CodeCheckToothActivity extends AppCompatActivity implements View.OnClickListener{
    private   EditText et;
   private InputMethodManager imm;
    private   Button mbtnA,mbtnB,mbtnC,mbtnD,mbtnE,mbtnF,mbtnG,mbtnH,mbtnI,mbtnJ,mbtnK,mbtnL,mbtnM,mbtnN,mbtnO,mbtnP,mbtnQ,mbtnR,mbtnS,mbtnT,
            mbtnU,mbtnV,mbtnW,mbtnX,mbtnY,mbtnZ,mbtnBar,mbtnPoint,mbtn1,mbtn2,mbtn3,mbtn4,mbtn5,mbtn6,mbtn7,mbtn8,mbtn9,mbtn_,mbtn0,mbtnComma;
    private  Button mbtnClear,mbtnNext,mbtnRemove,mbtnSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_check_tooth);
       et=(EditText)findViewById(R.id.code_edit);
        et.setLongClickable(false);
        //underline
       TextView Ctv_title=(TextView)findViewById(R.id.tv_text);
        //设置标题
        Ctv_title.setText(getIntent().getStringExtra("title"));
             Button   Close=(Button)findViewById(R.id.btn_close);
        Close.setOnClickListener(Closelistener);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
       imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et.requestFocusFromTouch();//让光标放入到点击位置
                imm.hideSoftInputFromWindow(et.getWindowToken(),0);
            }
        });

        //初始化计算机按钮
        initComputerBtn();
    }
    public   void  initComputerBtn(){
        mbtnA= (Button) findViewById(R.id.btnA);
        mbtnA.setOnClickListener(this);

        mbtnB= (Button) findViewById(R.id.btnB);
        mbtnB.setOnClickListener(this);

        mbtnC= (Button) findViewById(R.id.btnC);
        mbtnC.setOnClickListener(this);

        mbtnD= (Button) findViewById(R.id.btnD);
        mbtnD.setOnClickListener(this);

        mbtnE= (Button) findViewById(R.id.btnE);
        mbtnE.setOnClickListener(this);

        mbtnF= (Button) findViewById(R.id.btnF);
        mbtnF.setOnClickListener(this);

        mbtnG= (Button) findViewById(R.id.btnG);
        mbtnG.setOnClickListener(this);

        mbtnH= (Button) findViewById(R.id.btnH);
        mbtnH.setOnClickListener(this);

        mbtnI= (Button) findViewById(R.id.btnI);
        mbtnI.setOnClickListener(this);

        mbtnJ= (Button) findViewById(R.id.btnJ);
        mbtnJ.setOnClickListener(this);

        mbtnK= (Button) findViewById(R.id.btnK);
        mbtnK.setOnClickListener(this);

        mbtnL= (Button) findViewById(R.id.btnL);
        mbtnL.setOnClickListener(this);

        mbtnM= (Button) findViewById(R.id.btnM);
        mbtnM.setOnClickListener(this);

        mbtnN= (Button) findViewById(R.id.btnN);
        mbtnN.setOnClickListener(this);

        mbtnO= (Button) findViewById(R.id.btnO);
        mbtnO.setOnClickListener(this);

        mbtnP= (Button) findViewById(R.id.btnP);
        mbtnP.setOnClickListener(this);

        mbtnQ= (Button) findViewById(R.id.btnQ);
        mbtnQ.setOnClickListener(this);

        mbtnR= (Button) findViewById(R.id.btnR);
        mbtnR.setOnClickListener(this);

        mbtnS= (Button) findViewById(R.id.btnS);
        mbtnS.setOnClickListener(this);

        mbtnT= (Button) findViewById(R.id.btnT);
        mbtnT.setOnClickListener(this);

        mbtnU= (Button) findViewById(R.id.btnU);
        mbtnU.setOnClickListener(this);

        mbtnV= (Button) findViewById(R.id.btnV);
        mbtnV.setOnClickListener(this);

        mbtnW= (Button) findViewById(R.id.btnW);
        mbtnW.setOnClickListener(this);

        mbtnX= (Button) findViewById(R.id.btnX);
        mbtnX.setOnClickListener(this);

        mbtnY= (Button) findViewById(R.id.btnY);
        mbtnY.setOnClickListener(this);

        mbtnZ= (Button) findViewById(R.id.btnZ);
        mbtnZ.setOnClickListener(this);

        mbtnBar= (Button) findViewById(R.id.btnBar);
        mbtnBar.setOnClickListener(this);

        mbtnPoint= (Button) findViewById(R.id.btnPoint);
        mbtnPoint.setOnClickListener(this);

        mbtn1= (Button) findViewById(R.id.btn1);
        mbtn1.setOnClickListener(this);

        mbtn2= (Button) findViewById(R.id.btn_decimal2);
        mbtn2.setOnClickListener(this);

        mbtn3= (Button) findViewById(R.id.btn_decimal3);
        mbtn3.setOnClickListener(this);

        mbtn4= (Button) findViewById(R.id.btn_decimal4);
        mbtn4.setOnClickListener(this);

        mbtn5= (Button) findViewById(R.id.btn_decimal5);
        mbtn5.setOnClickListener(this);

        mbtn6= (Button) findViewById(R.id.btn_decimal6);
        mbtn6.setOnClickListener(this);

        mbtn7= (Button) findViewById(R.id.btn_decimal7);
        mbtn7.setOnClickListener(this);

        mbtn8= (Button) findViewById(R.id.btn_decimal8);
        mbtn8.setOnClickListener(this);

        mbtn9= (Button) findViewById(R.id.btn_decimal9);
        mbtn9.setOnClickListener(this);

        mbtn_= (Button) findViewById(R.id.btn_);
        mbtn_.setOnClickListener(this);

        mbtn0= (Button) findViewById(R.id.btn_decimal0);
        mbtn0.setOnClickListener(this);

        mbtnComma= (Button) findViewById(R.id.btnComma);
        mbtnComma.setOnClickListener(this);

        mbtnClear= (Button) findViewById(R.id.btnClear);
        mbtnClear.setOnClickListener(this);

        mbtnRemove= (Button) findViewById(R.id.btnRemove);
        mbtnRemove.setOnClickListener(this);

        mbtnNext= (Button) findViewById(R.id.btnNext);
        mbtnNext.setOnClickListener(this);

        mbtnSearch= (Button) findViewById(R.id.btn_search);
        mbtnSearch.setOnClickListener(this);
    }

    //返回上一层
    View.OnClickListener Closelistener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             finish();
        }
    };

    @Override
    public void onClick(View view) {
       Button btn= (Button)findViewById(view.getId());
        //按钮的声音
        Tools.btnSound(this);
        switch (view.getId()){
            case R.id.btnRemove:
                Log.d("开始坐标", "onClick: "+et.getSelectionStart());
                Log.d("结束坐标", "onClick: "+et.getSelectionEnd());
                if(et.getSelectionStart()!=0) {
                    et.getText().delete(et.getSelectionStart() - 1, et.getSelectionStart());
                }
                return;
            case R.id.btnClear:
                et.getText().clear(); //清空所有内容
                return;
            case R.id.btnNext:
//                Log.d("光标长度", "onClick: "+et.getSelectionStart());
//                Log.d("内容长度", "onClick: "+et.getText().length());
                if(et.getSelectionStart()!=et.getText().length()){
                    et.setSelection(et.getSelectionStart()+1);
                }
                return;
            case  R.id.btn_search:
                return;

       }

        et.getText().insert(et.getSelectionStart(),btn.getText());


    }
}
