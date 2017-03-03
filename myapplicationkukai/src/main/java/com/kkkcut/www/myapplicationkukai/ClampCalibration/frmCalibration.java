package com.kkkcut.www.myapplicationkukai.ClampCalibration;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.kkkcut.www.myapplicationkukai.BluetoothService.SendSocketService;
import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;

public class frmCalibration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clamp_calibration);
        Button btn_move=(Button)findViewById(R.id.btn_move);
        Button  btnClose=(Button)findViewById(R.id.btnClose);
        btnClose.setOnClickListener(close);

         btn_move.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 String message = "!SC;!BP1;";
                 SendSocketService.sendMessage(message,frmCalibration.this);
             }
         });
    }
    View.OnClickListener close=new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            Intent intent=new Intent(frmCalibration.this, MainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };
}
