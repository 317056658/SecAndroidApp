package com.kkkcut.www.myapplicationkukai.BluetoothManager;

import android.app.Application;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import cn.jpush.android.api.JPushInterface;

/**
 * 就是这儿，将我们以前一直用的默认Application给他设置成我们自己做的BltApplication
 * BltAppliaction类的作用是为了放一些全局的和一些上下文都要用到变量和方法之类的。
 */

/**
 * 理解BLE：一种近场无线网络，让可穿戴设备、只能家居、室内,即低功耗蓝牙
 * 属于一种个人近场无线网络技术，专为可穿戴设备、智能硬件、智能家居等设计
 * Android 从4.3开始支持蓝牙4.0，允许APP和BLE通信。
 */
public class BltAppliaction extends Application {
    //不管是蓝牙连接方还是服务器方，得到socket对象后都传入
    public static BluetoothSocket bluetoothSocket;

    public void onCreate() {
        super.onCreate();
        //推送
        try {
            JPushInterface.setDebugMode(true);
            JPushInterface.init(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("1AAA", "onCreate: 创建了");
    }
}
