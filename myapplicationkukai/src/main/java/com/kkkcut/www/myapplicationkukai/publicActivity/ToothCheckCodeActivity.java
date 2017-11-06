package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;

public class ToothCheckCodeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tooth_check_code);
       TextView mTitle =(TextView)findViewById(R.id.Ttv_title);
        //设置标题
        mTitle.setText(getIntent().getStringExtra("title"));
        Button mColse  =(Button)findViewById(R.id.Tbtn_colse);
        mColse.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         Button button  =(Button)findViewById(view.getId());
          switch (view.getId()){
              case R.id.Tbtn_colse:
                  finish();
                  break;
          }
    }
}
