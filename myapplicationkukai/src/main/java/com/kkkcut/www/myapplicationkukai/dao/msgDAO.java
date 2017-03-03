package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;


/**
 * Created by Administrator on 2017/1/11.
 */

public class msgDAO extends SQLiteOpenHelper {
    public msgDAO(Context context) {
        super(context,"message1.db", null, 1);
    }
    /**
     * 数据库第一次创建的时调用，第一建表可以在这里写
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Message( _id integer primary key autoincrement,MSG_id varchar(50),Title varchar(50),introduce nvarchar(1000),State integer,Type integer,Content text,Time varchar(50))");
        Log.d(TAG, "onCreate: 数据库创建成功");
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
