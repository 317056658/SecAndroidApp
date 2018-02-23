package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Administrator on 2016/11/24.
 * 原生数据库打开助手
 */

public class OriginalDatabaseOpenHelper extends SQLiteOpenHelper {
    public OriginalDatabaseOpenHelper(Context context, String dataBaseName) {
        super(context,dataBaseName, null, 1);
    }
    /**
     * 数据库第一次创建的时调用，第一建表可以在这里写
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("执行了？", "onCreate: ");
        //创建收藏表
        db.execSQL(
                "create table favorite(" +
                        "_id integer primary key autoincrement," +  // 为主键 自动增长
                        "card_number integer," +
                        "step nvarchar,"+
                        "start_flag integer," +
                        "key_blanks nvarchar,"+
                        "year_from integer,"+
                        "year_to integer,"+
                        "code_series nvarchar,"+
                        "type integer," +
                        "align integer," +
                        "width integer," +
                        "thick integer," +
                        "length integer," +
                        "row_count integer," +
                        "face varchar," +
                        "row_pos varchar," +
                        "space varchar," +
                        "space_width varchar," +
                        "depth varchar," +
                        "depth_name varchar," +
                        "type_specific_info varchar," +
                        "desc varchar," +
                        "desc_user varchar," +
                        "tooth_code varchar)" );  //收藏甲表
        //创建切割记录表
        db.execSQL("create table cut_history(" +
                "_id integer primary key autoincrement," +  // 为主键 自动增长
                 "start_flag integer,"+
                "step nvarchar,"+
                "key_blanks nvarchar,"+
                "year_from integer,"+
                "year_to integer,"+
                "code_series nvarchar,"+
                "card_number integer," +
                "type integer," +
                "align integer," +
                "width integer," +
                "thick integer," +
                "length integer," +
                "row_count integer," +
                "face varchar," +
                "row_pos varchar," +
                "space varchar," +
                "space_width varchar," +
                "depth varchar," +
                "depth_name varchar," +
                "type_specific_info varchar," +
                "desc varchar," +
                "desc_user varchar," +
                "tooth_code varchar)");  //收藏甲表");//切割记录表
    }

    /**
     * 数据库版本升级会调用此方法
     *
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
