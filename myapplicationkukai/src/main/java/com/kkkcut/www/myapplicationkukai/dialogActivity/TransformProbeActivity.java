package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

/**
 * Created by Administrator on 2017/5/10.
 */

public class TransformProbeActivity extends AppCompatActivity {
    private View mDecorView;
    private ProlificSerialDriver serialDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_probe);
        serialDriver=ProlificSerialDriver.getInstance();
        mDecorView =getWindow().getDecorView();
          findViewById(R.id.iv_transform_probe).setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                int i  = serialDriver.write(Instruction.TRANSFORM_PROBE.getBytes(),Instruction.TRANSFORM_PROBE.length());
                  Log.d("大于-1", "onClick: "+i);
                  if(i>0){
                    finish();
                  }
              }
          });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
}
