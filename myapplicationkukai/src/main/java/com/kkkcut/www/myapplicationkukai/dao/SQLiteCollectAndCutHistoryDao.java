package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.kkkcut.www.myapplicationkukai.entity.CollectCutHistory;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作收藏和记录数据库
 * 收藏表和切割记录表的封装类
 * Created by Administrator on 2017/10/11.
 */

public class SQLiteCollectAndCutHistoryDao {
    public  static  final  String TABLE_FAVORITE="favorite";
    public  static  final  String TABLE_CUT_HISTORY="cut_history";
    private   DatabaseHelper databaseHelper=null;  //获得数据库助手
    private static SQLiteCollectAndCutHistoryDao sqLiteCollectAndCutHistoryDao=null;
    private Gson gson=null;
    private SQLiteCollectAndCutHistoryDao(Context context,String databaseName){
        databaseHelper=new DatabaseHelper(context,databaseName);
        gson=new Gson();
    }
    /**
     * 获得单例
     * @param context
     *
     * @return
     */
   public synchronized static  SQLiteCollectAndCutHistoryDao  getInstance(Context context){
        if(sqLiteCollectAndCutHistoryDao==null){
            sqLiteCollectAndCutHistoryDao=new SQLiteCollectAndCutHistoryDao(context,"Boxroom.db");
        }
        return  sqLiteCollectAndCutHistoryDao;
   }

    /**
     * 删除表中一条数据
     * @param tableName
     * @param id
     */
  public  void deleteTableSingleData(String tableName,int id){
      SQLiteDatabase db=databaseHelper.getReadableDatabase();
      //删除条件
      String whereClause = "_id=?";
      //删除条件参数
      String[] whereArgs = {String.valueOf(id)};

      db.delete(tableName,whereClause,whereArgs);
      db.close();
  }

    /**
     * 删除表中全部数据
     * @param tableName
     */
    public  void deleteTableAllData(String tableName){
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        db.delete(tableName,null,null);
        db.close();
    }

    /**
     *查询Favorite表10条数据  一次查询10条数据
     * @return
     */
   public List<CollectCutHistory> queryTableTenData(String tableName,int offset){
       List<CollectCutHistory> list=new ArrayList<>();
       SQLiteDatabase db=databaseHelper.getReadableDatabase();
        Cursor cursor  =db.rawQuery("select * from "+tableName+" order by _id DESC limit 10  OFFSET "+offset,null);
       while (cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("_id"));
           int startFlag=cursor.getInt(cursor.getColumnIndex("start_flag"));
           String step=cursor.getString(cursor.getColumnIndex("step"));
           String  keyInfo=cursor.getString(cursor.getColumnIndex("key_info"));
           CollectCutHistory  collectCutHistory=new CollectCutHistory(id,step,keyInfo,startFlag);
           list.add(collectCutHistory);
       }
       cursor.close();
       db.close();
     return  list;
   }
    /**
     * 插入数据到收藏夹表
     * @param step
     * @param ki
     */
    public  void insertDataToFavorite(String step,KeyInfo ki,int startFlag){
        String json=gson.toJson(ki,KeyInfo.class);
        SQLiteDatabase db=databaseHelper.getReadableDatabase();  //获得数据库连接
        db.execSQL("insert into favorite(start_flag,step,key_info) values(?,?,?)",new Object[]{startFlag,step,json});
        db.close();
    }
    /**
     * 插入数据到切割记录表
     * @param step
     * @param ki
     */
   public  void  insertDataToCutHistory(String step,KeyInfo ki,int startFlag){
       String json=gson.toJson(ki,KeyInfo.class);
       SQLiteDatabase db=databaseHelper.getReadableDatabase();
       db.execSQL("insert into cut_history(start_flag,step,key_info) values(?,?,?)",new Object[]{startFlag,step,json});
       db.close();
   }

    /**
     * 获得表中有多少条数据
     * @param tableName
     * @return
     */
   public   int getTableDataNum(String tableName){
       SQLiteDatabase db=databaseHelper.getReadableDatabase();
       Cursor mCursor=db.query(tableName,null,null,null,null,null,null);
        int num=mCursor.getCount();
       mCursor.close();
        return num;
   }


}
