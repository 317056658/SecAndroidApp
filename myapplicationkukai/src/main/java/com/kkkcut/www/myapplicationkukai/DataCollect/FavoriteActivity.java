package com.kkkcut.www.myapplicationkukai.DataCollect;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.BluetoothService.SendSocketService;
import com.kkkcut.www.myapplicationkukai.BluetoothService.readThread;
import com.kkkcut.www.myapplicationkukai.R;

public class FavoriteActivity extends AppCompatActivity {
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
                String data  =String.valueOf(msg.obj);
            Toast.makeText(FavoriteActivity.this,data, Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Button btn_move = (Button) findViewById(R.id.btn_move);
        btn_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = "$";
                SendSocketService.sendMessage(message, FavoriteActivity.this);

            }
        });
        //开启子线程监听返回的数据
        readThread read=new readThread(handler);
        read.start();
    }


}