package com.kkkcut.www.myapplicationkukai.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.entity.Multilingual;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/2/7.
 *   语言dao 操作管理者
 */

public class LanguageDaoManager {
    //数据库的名字
      public  static  String DB_NAME="LanguageFile.db";
    //声明单例
      private static  LanguageDaoManager mInstanceHolder;
      private  OriginalDatabaseOpenHelper databaseOpenHelper;
    /**
     * 私有类
     */
    private LanguageDaoManager(){
        databaseOpenHelper=new OriginalDatabaseOpenHelper(MyApplication.getContext(),DB_NAME);
    }
    public static LanguageDaoManager getInstanc(){
          if(mInstanceHolder==null){
                 synchronized (LanguageDaoManager.class){
                     if(mInstanceHolder==null){
                         mInstanceHolder=new LanguageDaoManager();
                     }
                 }
          }
          return mInstanceHolder;
    }
    /**
     *  查询语言
     */
    public HashMap<String,String> queryLanguageTable(String table){
        HashMap<String,String>  map=new HashMap();
        if(table.equals("0")){
            return map;
        }
        String strSql = "select * from "+table;
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()){
            String  key=cursor.getString(cursor.getColumnIndex("TableKey"));
            String value=cursor.getString(cursor.getColumnIndex("TableValue"));
            map.put(key,value);
        }
        cursor.close();
        db.close();
        return map;
    }

    /**
     *  查询语言选择表
     * @return
     */
    public ArrayList<Multilingual> queryLanguagesSelect(){
        ArrayList<Multilingual> list=new ArrayList<>();
        String strSql = "select *  from LanguageSelect";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()){
            String  tableName=cursor.getString(cursor.getColumnIndex("TableName"));
            String showLanguage=cursor.getString(cursor.getColumnIndex("LanguageDes"));
            Multilingual multilingual=new Multilingual();
            multilingual.setTableName(tableName);
            multilingual.setShowLanguage(showLanguage);
            list.add(multilingual);
        }
        cursor.close();
        db.close();
        return list;
    }
}
