package com.kkkcut.www.myapplicationkukai.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.kkkcut.www.myapplicationkukai.R;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类  防止button 按钮被疯狂点击
 */
public class Tools {
    private static long lastClickTime;
   private static SoundPool sp=new  SoundPool(10, AudioManager.STREAM_SYSTEM, 5);;//定义一个SoundPool
    private static int music;//声音一个整型用load（）；来设置suondID

    /**
     * 防止button被疯狂点击  2秒一次
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 2000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 删除多余的零
     */
    public static  String removeUnnecessaryZero(double d){

        String s = BigDecimal.valueOf(d).stripTrailingZeros().toPlainString();
        return s;
    }

    /**
     *  隐藏底部虚拟导航栏，并且全屏
     * @param decorView
     */

    public static void hideBottomUIMenu(View decorView) {

        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            decorView.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            int uiOptions =
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    |View.SYSTEM_UI_FLAG_IMMERSIVE;
            decorView.setSystemUiVisibility(uiOptions);

        }

    }

    /**
     * 按钮的声音方法
     */
    public static void btnSound(Context context){
        music= sp.load(context, R.raw.glass_01,1);
        sp.play(music, 1, 1, 0, 0, 1);
    }
    /**
     * 去掉换行符
     */
    public static String  clearNewline(String s){
         return  s.replaceAll("\r|\n| ","");
    }
    public static int   extractNumber(String s){
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(s);
        m.find();
        return Integer.parseInt(m.group());
    }
    /**
     *  禁止EditText弹出软件盘，光标依然正常显示。
     */
    public static void disableShowSoftInput(EditText et) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            et.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(et, false);
            } catch (Exception e) {
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(et, false);
            } catch (Exception e) {
            }
        }
    }
    /**
     * 禁止EditText弹出剪切板
     */
     public static  void disableShearPlate(EditText et){
         et.setLongClickable(false);
         et.setTextIsSelectable(false);
         et.setCustomSelectionActionModeCallback(new android.view.ActionMode.Callback() {
             @Override
             public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
                 return false;
             }

             @Override
             public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
                 return false;
             }

             @Override
             public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
                 return false;
             }

             @Override
             public void onDestroyActionMode(android.view.ActionMode mode) {

             }
         });
     }


}
