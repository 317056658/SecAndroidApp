package com.kkkcut.www.myapplicationkukai.BluetoothService;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

import static com.kkkcut.www.myapplicationkukai.BluetoothManager.BltAppliaction.bluetoothSocket;

/**
 * Created by Administrator on 2016/12/30.
 */

public class readThread  extends Thread {
    Handler  handler;
    public  readThread(Handler handler){
           this.handler=handler;
    }
    public void run() {
        InputStream is = null;
        if(bluetoothSocket==null){
            return;
        }
        try {
            is = bluetoothSocket.getInputStream();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (true) {
            byte[] buffer;
            while (true) {
                try {
                    buffer=new byte[1024];
                    if(is.available()>0 == false){
                        continue;
                    }else{
                        Thread.sleep(500);
                    }

                    is.read(buffer);
                    String s=new String(buffer);
                    Message mgs=new Message();
                    mgs.obj=s;
                    handler.sendMessage(mgs);

                } catch (Exception e) {
                    try {
                        is.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
