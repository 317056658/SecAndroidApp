package com.kkkcut.www.myapplicationkukai.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kkkcut.www.myapplicationkukai.application.MyApplication;

/**
 * Created by Administrator on 2018/2/8.
 * 代码齿号 dao 管理者
 */

public class CodeToothNumberDaoManager {
    public  static   String  DB_NAME="code.db";
    private  static  CodeToothNumberDaoManager mInstanceHolder;
    private OriginalDatabaseOpenHelper  databaseOpenHelper;

    private   CodeToothNumberDaoManager(){
        databaseOpenHelper=new OriginalDatabaseOpenHelper(MyApplication.getContext(),DB_NAME);
    }

    /**
     * 获得实例
     * @return
     */
    public  static   CodeToothNumberDaoManager  getInstance(){
           if(mInstanceHolder==null){
                 synchronized (CodeToothNumberDaoManager.class){
                     if(mInstanceHolder==null){
                         mInstanceHolder=new CodeToothNumberDaoManager();
                     }
                 }
           }
           return  mInstanceHolder;
    }

    /**
     *  查询齿号，传入code
     * @param code
     * @return
     */
    public  String queryBittingByCode(String code ){
        //获得数据库连接
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        String  strSql="select bitting from BittingCode where Code=?";
        String bitting="";
        Cursor cursor =db.rawQuery(strSql,new String[]{
                code});
          while (cursor.moveToNext()){
              bitting=cursor.getString(cursor.getColumnIndex("bitting"));
          }
        cursor.close();
        db.close();
       return  bitting;
    }
}
