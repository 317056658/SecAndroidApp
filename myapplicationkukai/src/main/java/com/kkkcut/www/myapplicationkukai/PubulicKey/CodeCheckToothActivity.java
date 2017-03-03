package com.kkkcut.www.myapplicationkukai.PubulicKey;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.kkkcut.www.myapplicationkukai.R;

public class CodeCheckToothActivity extends AppCompatActivity {
    EditText et;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_check_tooth);
       et=(EditText)findViewById(R.id.code_edit);
             Button   Close=(Button)findViewById(R.id.btn_close);
        Close.setOnClickListener(Closelistener);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imm.hideSoftInputFromWindow(et.getWindowToken(),0);

            }
        });

    }
    View.OnClickListener Closelistener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
             finish();
        }
    };
}
