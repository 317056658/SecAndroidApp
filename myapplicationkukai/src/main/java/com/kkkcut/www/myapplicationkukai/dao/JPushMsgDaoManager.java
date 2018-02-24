package com.kkkcut.www.myapplicationkukai.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kkkcut.www.myapplicationkukai.entity.JMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/11.
 */

public class JPushMsgDaoManager {
    public PushMsgDAO sqliteOpenHelper;

    public JPushMsgDaoManager(Context context) {
        sqliteOpenHelper = new PushMsgDAO(context);
    }

    public void insertObject(String id,String title,String introduce, int Stata, int type, String text, String time) {
        //获得一个数据库连接
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        db.execSQL("insert into Message(MSG_id,Title,introduce,State,Type,Content,Time) values(?,?,?,?,?,?,?)", new Object[]{id,title,introduce,Stata,type,text,time});
        db.close();
    }
    public List<JMessage> queryAll() {
        List<JMessage> list = new ArrayList();
        String strSql = "select * from  Message";
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            String id=cursor.getString(cursor.getColumnIndex("MSG_id"));
            String xTitle = cursor.getString(cursor.getColumnIndex("Title"));
            String xintroduce = cursor.getString(cursor.getColumnIndex("introduce"));
            int xState = cursor.getInt(cursor.getColumnIndex("State"));
            int xType = cursor.getInt(cursor.getColumnIndex("Type"));
            String xContent = cursor.getString(cursor.getColumnIndex("Content"));
            String xTime = cursor.getString(cursor.getColumnIndex("Time"));
            JMessage msg = new JMessage(id,xTitle, xintroduce, xState, xType, xContent, xTime);
            list.add(msg);
        }
        cursor.close();
        db.close();
        return list;
    }
    public boolean query(){
        String strSql = "select * from  Message";
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        if(cursor.moveToFirst()==false){
            cursor.close();
            db.close();
            return false ;
        }
        cursor.close();
        db.close();
        return true;
    }
    public  void Delete(String time){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        db.delete("Message","Time=?",new String[]{time});
        db.close();
    }
    //删除全部
    public void DeleteAll(){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        db.delete("Message",null,null);
        db.close();
    }
    //查看消息是否已读
    public boolean queryRead(){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        String strSql = "select * from  Message where State=0";
        Cursor cursor =db.rawQuery(strSql,null);
         int  num= cursor.getCount();
        cursor.close();
        db.close();
        if(num>0){
            return true;
        }else {
            return false;
        }

    }
    public void updateState(String id){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("State",1);
        db.update("Message", values, "MSG_id= ?", new String[] {id});
        db.close();
    }
    //查一条
    public  List<JMessage> queryId(String id){
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        String strSql = "select * from  Message where MSG_id="+id;
        List<JMessage> list = new ArrayList();
        Cursor cursor =db.rawQuery(strSql,null);
             while (cursor.moveToNext()){
                 String   id1= cursor.getString(cursor.getColumnIndex("MSG_id"));
                 String xTitle = cursor.getString(cursor.getColumnIndex("Title"));
                 String xintroduce = cursor.getString(cursor.getColumnIndex("introduce"));
                 int xState = cursor.getInt(cursor.getColumnIndex("State"));
                 int xType = cursor.getInt(cursor.getColumnIndex("Type"));
                 String xContent = cursor.getString(cursor.getColumnIndex("Content"));
                 String xTime = cursor.getString(cursor.getColumnIndex("Time"));
                 JMessage msg = new JMessage(id1,xTitle, xintroduce, xState, xType, xContent, xTime);
                 list.add(msg);
             }
        cursor.close();
        db.close();
        return list;
    }
    public List<JMessage> queryAllsort() {
        List<JMessage> list = new ArrayList();
        String strSql = "select * FROM Message ORDER BY Time desc";
        SQLiteDatabase db = sqliteOpenHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            String id=cursor.getString(cursor.getColumnIndex("MSG_id"));
            String xTitle = cursor.getString(cursor.getColumnIndex("Title"));
            String xintroduce = cursor.getString(cursor.getColumnIndex("introduce"));
            int xState = cursor.getInt(cursor.getColumnIndex("State"));
            int xType = cursor.getInt(cursor.getColumnIndex("Type"));
            String xContent = cursor.getString(cursor.getColumnIndex("Content"));
            String xTime = cursor.getString(cursor.getColumnIndex("Time"));
            JMessage msg = new JMessage(id,xTitle, xintroduce, xState, xType, xContent, xTime);
            list.add(msg);
        }
        cursor.close();
        db.close();
        return list;
    }

}
