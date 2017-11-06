package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Administrator on 2016/11/24.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
  //  public static final String DB_NAME = "SEC1.db"; //保存的数据库文件名

    public DatabaseHelper(Context context, String dataBaseName) {
        super(context,dataBaseName,null,1);
    }
    /**
     * 数据库第一次创建的时调用，第一建表可以在这里写
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
      Log.d("调用了？+++++++", "onCreate: ");
      db.execSQL("create table favorite(_id integer primary key autoincrement,start_flag integer,step varchar(300),key_info varchar(800))");  //收藏甲表
      db.execSQL("create table cut_history(_id integer primary key autoincrement,start_flag integer,step varchar(300),key_info varchar(800))");//切割记录表
    }
    /**
     * 数据库版本升级会调用此方法
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
