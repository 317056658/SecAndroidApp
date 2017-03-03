package com.kkkcut.www.myapplicationkukai.custom;

/**
 * 工具类  防止button 按钮被疯狂点击
 */

public class Tools {
    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
