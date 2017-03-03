package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Administrator on 2016/11/24.
 */

public class BaseDAO extends SQLiteOpenHelper {
    private  Context myContext;
    protected SQLiteDatabase db;


    public static final String DB_NAME = "SEC1.db"; //保存的数据库文件名
    public BaseDAO(Context context) {
        super(context,DB_NAME , null, 1);
        this.myContext = context;
    }


    /**
     * 数据库第一次创建的时调用，第一建表可以在这里写
     * @param db
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
              // db.execSQL("create table prerson( _id integer primary key autoincrement,name varchar(50))");
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
