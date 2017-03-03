package com.kkkcut.www.myapplicationkukai.PubulicKey;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.keyBlankInfo;

public class InputMainActivity extends AppCompatActivity {
    EditText et;
    InputMethodManager imm;
    keyBlankInfo KBI;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_main);
        LinearLayout ll=(LinearLayout)findViewById(R.id.ll);
        //启动Activity  默认不启动系统输入法
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
         et= (EditText) findViewById(R.id.input_et);

        imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        KBI =getIntent().getParcelableExtra("KBI");
        String[]  array=KBI.getSpace().split(";");
        String num="";
        for (int i=0;i<array.length;i++){
               for(int j=0;j<array[i].split(",").length;j++){
                      num+="X";
                   if(num.length()==array[i].split(",").length){
                       num=num+"-";
                   }
               }
        }

        et.setOnTouchListener(new View.OnTouchListener() {
                                  @Override
                                  public boolean onTouch(View view, MotionEvent motionEvent) {

                                      return false;
                                  }
                              });
                et.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        Editable editable = et.getText();
                          Selection.setSelection(editable,et.getSelectionStart()-1,et.getSelectionStart());
                        imm.hideSoftInputFromWindow(et.getWindowToken(),0);
                        //Selection.setSelection(et.getText(),0,3);
                        et.setHighlightColor(Color.BLUE);
                        Log.d("开始位子", "onClick: " + et.getSelectionStart());
                        Log.d("光标", "onClick: " + et.getSelectionEnd());
                    }
                });

          //减去最后一个字符 得到多少xxxx-xxxx
        num=num.substring(0,num.length()-1);

        et.setText(num);

        Selection.setSelection(et.getText(),0,1);
        et.setHighlightColor(Color.BLUE);
        //动态加载布局
        MountView();
    }
    public void MountView(){
         String[]  Depth=KBI.getDepth().split(";");
        if(KBI.getDepth_name()==null){
            for (int i=0;i<Depth.length;i++){

            }
        }
          String[]  Depth_name= KBI.getDepth_name().split(";");

    }
}
