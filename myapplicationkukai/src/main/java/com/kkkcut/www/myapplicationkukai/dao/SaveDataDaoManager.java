package com.kkkcut.www.myapplicationkukai.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.saveData.KeyInformationBoxroomActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/30.
 *  //保存数据dao 管理者
 */

public class SaveDataDaoManager {
    private static final String DB_NAME="SaveData.db";
    public  static  final  String TABLE_FAVORITE="favorite";
    public  static  final  String TABLE_CUT_HISTORY="cut_history";
    private static SaveDataDaoManager mInstanceHolder=null;
    private OriginalDatabaseOpenHelper databaseOpenHelper;
    public static SaveDataDaoManager getInstance(){
        if(mInstanceHolder ==null){
            synchronized (SaveDataDaoManager.class){
                if(mInstanceHolder ==null){
                    mInstanceHolder =new SaveDataDaoManager();
                    Log.d("进来了？", "getInstance: ");
                }
            }
        }
        return mInstanceHolder;
    }
    private SaveDataDaoManager(){
        // 打开数据库和创建数据库
        databaseOpenHelper=new OriginalDatabaseOpenHelper(MyApplication.getContext(),DB_NAME);
    }
    /**
     * 删除表中一条数据
     * @param tableName
     * @param cardNumber
     */
    public  void deleteTableSingleData(String tableName,int cardNumber){
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        //删除条件
        String whereClause = "cardNumber=?";
        //删除条件参数
        String[] whereArgs = {String.valueOf(cardNumber)};

        db.delete(tableName,whereClause,whereArgs);
        db.close();
    }

    /**
     *  删除第一条数据   根据id删除
     *  @param tableName
     */
    public void  deleteTableFirstData(String tableName){
        //获得数据库连接
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        db.execSQL("delete from "+tableName+" where _id = (select _id from "+tableName+" Limit 1)");
        db.close();
    }

    /**
     * 删除表中全部数据
     * @param tableName
     */
    public  void deleteTableAllData(String tableName){
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        db.delete(tableName,null,null);
        db.close();
    }

    /**
     *查询Favorite表10条数据  一次查询10条数据
     * @return
     */
    public List<KeyInfo> queryTableTenData(String tableName, int offset){
        List<KeyInfo> list=new ArrayList<>();
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        Cursor cursor  =db.rawQuery("select * from "+tableName+" order by _id DESC limit 10  OFFSET "+offset,null);
        while (cursor.moveToNext()){
            KeyInfo ki=new KeyInfo();
            int id = cursor.getInt(cursor.getColumnIndex("card_number"));
            ki.setCardNumber(id);
            int startFlag=cursor.getInt(cursor.getColumnIndex("start_flag"));
            ki.setStartFlag(startFlag);
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            ki.setType(type);
            int align = cursor.getInt(cursor.getColumnIndex("align"));
            ki.setAlign(align);
            int width = cursor.getInt(cursor.getColumnIndex("width"));
            ki.setWidth(width);
            int thick = cursor.getInt(cursor.getColumnIndex("thick"));
            ki.setThick(thick);
            int length = cursor.getInt(cursor.getColumnIndex("length"));
            ki.setLength(length);
            int rowCount = cursor.getInt(cursor.getColumnIndex("row_count"));
            ki.setRowCount(rowCount);
            int  yearFrom=cursor.getInt(cursor.getColumnIndex("year_from"));
            ki.setYearFrom(yearFrom);
            int  yearTo=cursor.getInt(cursor.getColumnIndex("year_to"));
            ki.setYearTo(yearTo);
            String codeSeries=cursor.getString(cursor.getColumnIndex("code_series"));
            ki.setCodeSeries(codeSeries);
            String keyBlanks=cursor.getString(cursor.getColumnIndex("key_blanks"));
            ki.setKeyBlanks(keyBlanks);
            String face = cursor.getString(cursor.getColumnIndex("face"));
            ki.setFace(face);
            String rowPos = cursor.getString(cursor.getColumnIndex("row_pos"));
            ki.setRow_pos(rowPos);
            String strSpace = cursor.getString(cursor.getColumnIndex("space"));
            ki.setSpace(strSpace);
            String strSpaceWidth = cursor.getString(cursor.getColumnIndex("space_width"));
            ki.setSpace_width(strSpaceWidth);
            String strDepth = cursor.getString(cursor.getColumnIndex("depth"));
            ki.setDepth(strDepth);
            String strDepthName = cursor.getString(cursor.getColumnIndex("depth_name"));
            ki.setDepth_name(strDepthName);
            String type_specific_info = cursor.getString(cursor.getColumnIndex("type_specific_info"));
            ki.setType_specific_info(type_specific_info);
            String desc = cursor.getString(cursor.getColumnIndex("desc"));
            ki.setDesc(desc);
            String descUser = cursor.getString(cursor.getColumnIndex("desc_user"));
            ki.setDescUser(descUser);
            String toothCode=cursor.getString(cursor.getColumnIndex("tooth_code"));
            ki.setKeyToothCode(toothCode);
              String  step=cursor.getString(cursor.getColumnIndex("step"));
            ki.setStep(step);
            list.add(ki);
        }
        cursor.close();
        db.close();
        return  list;
    }
    /**
     * 插入数据到收藏夹表
     * @param
     * @param ki
     */
    public  void insertDataToFavorite(KeyInfo ki){
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();  //获得数据库连接
        db.execSQL("insert into favorite(" +
                "card_number," +  //1
                "step,"+
                "start_flag," +  //2
                "type," +      //3
                "align," +    //4
                "width," +   //5
                "thick," +   //6
                "length," +   //7
                "row_count," +   //8
                "face," +  //9
                "row_pos," +  //10
                "space," +   //11
                "space_width," +  //12
                "depth," +  //13
                "depth_name," + //14
                "type_specific_info," +  //15
                "desc," +   //16
                "desc_user," +   //17
                "tooth_code)" +   //18
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{ki.getCardNumber(),ki.getStep(),ki.getStartFlag(),ki.getType(),
                ki.getAlign() ,ki.getWidth(),ki.getThick(),ki.getLength(),ki.getRowCount(),ki.getFace(),ki.getRow_pos(),ki.getSpace(),
                ki.getSpace_width(),ki.getDepth(),ki.getDepth_name(),ki.getType_specific_info(),ki.getDesc(),ki.getDescUser(),ki.getKeyToothCode()
        });
        db.close();
    }
    /**
     * 插入数据到切割记录表
     *
     * @param ki
     */
    public  void  insertDataToCutHistory(KeyInfo ki){
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        db.execSQL("insert into cut_history(" +
                "card_number," +  //1
                 "step,"+
                "start_flag," +  //2
                "type," +      //3
                "align," +    //4
                "width," +   //5
                "thick," +   //6
                "length," +   //7
                "row_count," +   //8
                "face," +  //9
                "row_pos," +  //10
                "space," +   //11
                "space_width," +  //12
                "depth," +  //13
                "depth_name," + //14
                "type_specific_info," +  //15
                "desc," +   //16
                "desc_user," +   //17
                "tooth_code)" +   //18) "
                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",new Object[]{ki.getCardNumber(),ki.getStep(),ki.getStartFlag(),ki.getType(),
               ki.getAlign() ,ki.getWidth(),ki.getThick(),ki.getLength(),ki.getRowCount(),ki.getFace(),ki.getRow_pos(),ki.getSpace(),
                ki.getSpace_width(),ki.getDepth(),ki.getDepth_name(),ki.getType_specific_info(),ki.getDesc(),ki.getDescUser(),ki.getKeyToothCode()
               });
        db.close();
    }

    /**
     * 获得表中有多少条数据
     * @param tableName
     * @return
     */
    public   int getTableDataNum(String tableName){
        SQLiteDatabase db=databaseOpenHelper.getReadableDatabase();
        Cursor mCursor=db.query(tableName,null,null,null,null,null,null);
        int num=mCursor.getCount();
        mCursor.close();
        return num;
    }

    /**
     *  保存数据到切割记录
     */
    public static void saveDataToCutHistory(KeyInfo ki){
        if(mInstanceHolder!=null){
            int dataCount=mInstanceHolder.getTableDataNum(SaveDataDaoManager.TABLE_CUT_HISTORY);
            if(dataCount< KeyInformationBoxroomActivity.CUT_HISTORY_DATA_MAX){
                mInstanceHolder.insertDataToCutHistory(ki);
            }else {
                // 删除第一条 在插入
                mInstanceHolder.deleteTableFirstData(SaveDataDaoManager.TABLE_CUT_HISTORY);
                mInstanceHolder.insertDataToCutHistory(ki);
            }
        }else {
            getInstance();
        }
    }
}
