package com.kkkcut.www.myapplicationkukai.utils;

import com.google.gson.Gson;

/**
 * Gson解析
 * Created by Administrator on 2017/10/17.
 */

public class GsonUtils {
    private   GsonUtils(){

    }
    public  static class Holder{
        private static Gson  gson=new Gson();
        public  static Gson getInstance(){
            return gson;
        }
    }
}
