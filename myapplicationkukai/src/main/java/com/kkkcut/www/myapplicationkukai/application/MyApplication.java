package com.kkkcut.www.myapplicationkukai.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.StrictMode;

import com.bandeng.MyEventBusIndex;
import com.kkkcut.www.myapplicationkukai.dao.SaveDataDaoManager;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.utils.VersionUtils;
import com.kkkcut.www.myapplicationkukai.utils.logDocument.LogUtils;

import org.greenrobot.eventbus.EventBus;

import cn.jpush.android.api.JPushInterface;

/**
 * 就是这儿，将我们以前一直用的默认Application给他设置成我们自己做的BluetoothApplication
 * BltAppliaction类的作用是为了放一些全局的和一些上下文都要用到变量和方法之类的。
 */

/**
 * 理解BLE：一种近场无线网络，让可穿戴设备、只能家居、室内,即低功耗蓝牙
 * 属于一种个人近场无线网络技术，专为可穿戴设备、智能硬件、智能家居等设计
 * Android 从4.3开始支持蓝牙4.0，允许APP和BLE通信。
 */
public class MyApplication extends Application {
    public static  String GESTURE_PASSWORD="gesturePassword";  //
    //图片状态
    private static Context mContext;
    public static KeyInfo   ki;         //缓存钥匙信息
    public static String stepText;  //缓存步骤文本
    public static int startFlag;
    private  boolean DEBUG_MODE=true;  //严苛模式
    public   static  final boolean  isCreation=true;
    public void onCreate() {
        super.onCreate();
        //推送
        try {
            JPushInterface.setDebugMode(true);
             JPushInterface.init(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext=this;
        // 初始化数据库
        SaveDataDaoManager.getInstance();
        // 启用EventBus3.0加速功能
   EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();

            if(DEBUG_MODE){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectAll()
                                .detectDiskReads()// 侦测磁盘读
                                .detectDiskWrites()// 侦测磁盘写
//                        .detectNetwork()    // 侦测网络操作
                                .detectCustomSlowCalls()//侦测自定义的耗时操作
                                .penaltyLog()
                                .build());

                        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                                .detectAll()
                                .detectLeakedSqlLiteObjects()//泄露的Sqlite对象
                                .detectLeakedClosableObjects()//未关闭的Closable对象泄露
                                .detectLeakedRegistrationObjects()//广播或者服务等未注销导致泄漏
                                .detectActivityLeaks()//侦测Activity（活动）泄露
                                .penaltyLog()
                                .build());
                    }
                }).start();
            }
        try {
            //获取包管理者对象
            PackageManager pm = getPackageManager();
                //获取包的详细信息
                PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
                //获取版本号和版本名称
                System.out.println("版本号："+info.versionCode);
                System.out.println("版本名称："+info.versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
         intLog();
    }
    public static Context getContext() {
        return mContext;
    }
    private void intLog(){

        //用可以作为外部缓存的路径,卸载app时，会自动删除文件
        LogUtils.setLogDir(getExternalCacheDir().getAbsolutePath());

        if(VersionUtils.VersionStatusName.equals("beta")){
            LogUtils.setLogLevel(LogUtils.LogLevel.DEBUG);
        }else {
            // 为了保护隐私和保证log的整洁，正式版上只打比warn高的log，即warn, error和assert
            LogUtils.setLogLevel(LogUtils.LogLevel.WARN);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }
}
