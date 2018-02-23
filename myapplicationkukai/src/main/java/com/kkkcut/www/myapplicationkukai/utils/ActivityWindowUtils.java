package com.kkkcut.www.myapplicationkukai.utils;

import android.view.Window;
import android.view.WindowManager;

/**
 * 活动窗口的工具类
 * Created by Administrator on 2017/11/30.
 */

public class ActivityWindowUtils {
    /**
     * 设置屏幕不休眠
     * @param window
     */
    public  static  void  setScreenNoDormant(Window window){
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
