package com.kkkcut.www.myapplicationkukai.BluetoothService;

import android.content.Context;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.BluetoothManager.BltAppliaction;

import java.io.IOException;
import java.io.OutputStream;

import static com.kkkcut.www.myapplicationkukai.BluetoothManager.BltManager.getmBluetoothSocket;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SendSocketService {
    /**
     * 发送文本消息
     *
     * @param message
     */
    public static void sendMessage(String message,Context context) {

        if(getmBluetoothSocket()==null){
            return;
        }
        //非连接远程设备
        if (!getmBluetoothSocket().isConnected()){
            Toast.makeText(context,"请先连接蓝牙",Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream outputStream = BltAppliaction.bluetoothSocket.getOutputStream();// 获得输出流

            outputStream.write(message.getBytes());//因为outputStream 只能传输字节，所以要把字符串指令编程字节流
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
