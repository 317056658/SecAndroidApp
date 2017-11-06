package com.kkkcut.www.myapplicationkukai.threadPoolExecutor;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/8.
 *  创建一个线程池管理类，设置成单例模式
 */

public class ThreadPoolManager {
    public static ExecutorService singleThreadPool;
    private Context context;
    private  ThreadPoolManager(Context context){
        this.context = context.getApplicationContext();
    }
    //一个核心线程的线程池
    public static ExecutorService getInstance(Context context){
         if(singleThreadPool==null){
             singleThreadPool = Executors.newSingleThreadExecutor();
             Log.d("ThreadPoolManager", "initInstance:创建线程池成功");
         }else {
             Log.d("ThreadPoolManager", "initInstance:使用已有的线程池");
         }
        return singleThreadPool;
    }
}

