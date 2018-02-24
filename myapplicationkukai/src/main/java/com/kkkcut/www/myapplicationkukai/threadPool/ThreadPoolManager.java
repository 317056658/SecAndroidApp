package com.kkkcut.www.myapplicationkukai.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/5/8.
 *  创建一个线程池管理类，设置成单例模式
 */

public class ThreadPoolManager {
    public static ExecutorService singleThreadPool;
    private  ThreadPoolManager(){

    }
    //一个核心线程的线程池
    public static ExecutorService getInstance(){
         if(singleThreadPool==null){
             singleThreadPool = Executors.newSingleThreadExecutor();
         }else {
         }
        return singleThreadPool;
    }
}

