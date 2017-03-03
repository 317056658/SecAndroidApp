package com.kkkcut.www.myapplicationkukai.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kkkcut.www.myapplicationkukai.entity.Auto;
import com.kkkcut.www.myapplicationkukai.entity.keyBlankInfo;
import com.kkkcut.www.myapplicationkukai.entity.key_blank;
import com.kkkcut.www.myapplicationkukai.entity.key_blank_mfg;
import com.kkkcut.www.myapplicationkukai.entity.mfg;
import com.kkkcut.www.myapplicationkukai.entity.model;
import com.kkkcut.www.myapplicationkukai.entity.profile;
import com.kkkcut.www.myapplicationkukai.entity.series;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */

public class SQLiteDao {
       private   BaseDAO sqliteOpenHelper;
    public SQLiteDao(Context  context){
        sqliteOpenHelper =new BaseDAO(context);
    }
     //面向对象添加
    public void  insertObject(Auto auto){
        //获得一个数据库连接
        SQLiteDatabase db=sqliteOpenHelper.getWritableDatabase();
        ContentValues  values=new ContentValues();
        values.put("name",auto.getName());
        db.insert("",null,values);

        db.close();

    }
    //面向对象查询  查询全部
    public void  queryAll(){
          SQLiteDatabase   db  =sqliteOpenHelper.getWritableDatabase();
                   Cursor cursor=db.query("Brand",null,null,null,null,null,null);
        cursor.close();
        db.close();
    }
    //模糊查询
    public void queryByLike(String name){
        SQLiteDatabase   db  =sqliteOpenHelper.getWritableDatabase();
                    //表名    要查询的列， 条件    条件参数                分组，分组后筛选，排序
            Cursor cursor=db.query("Brand",null,"name like ?",new String[]{"%"+name+"%"},null,null,null);
                cursor.close();
               db.close();
    }
    //根据id删除
    public void deleteByid(int _id){
        SQLiteDatabase db=sqliteOpenHelper.getWritableDatabase();
                     db.delete("CarKey","_id=?",new String[]{_id+""});
                db.close();
    }

    /**
     *    修改一条数据
     * @param name   要修改的名字
      * @param _id     根据条件id
     */
    public void update(String name,int _id){
       SQLiteDatabase db =sqliteOpenHelper.getWritableDatabase();
        ContentValues  values=new ContentValues();
        values.put("name",name);
                  //表名  要修改的列名及值    条件
        db.update("CarKey",values,"_id=?",new String[]{_id+""});
        db.close();
    }
    /**
     * 查询数据库中的所以数据
     *   返回一个list的集合
     */
    public List<Auto> findAll(){
        List<Auto> autos=new ArrayList();
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor2=db.rawQuery("select * from person",null);
        Cursor cursor=db.query("Carkey",new String[]{"id","name"},null,null,null,null,null);
        while(cursor.moveToNext()){
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            String name=cursor.getString(cursor.getColumnIndex("name"));
           Auto auto=new Auto(id,name);
            autos.add(auto);
        }
        cursor.close();
        db.close();
        return autos;
    }

    /**
     * 查询钥匙坯名称
     * 钥匙坯
     * 返回钥匙坯品牌和钥匙坯型号
     */
    public String GetKeyBlankSummary(int KeyBlankID)
    {
        String strSql="select key_blank_mfg.Name as blank_mfgName,key_blank.name as blankName from key_blank_mfg inner join key_blank on key_blank_mfg.ID=key_blank.fk_key_blank_mfg where key_blank.ID=%s";
        strSql =String.format(strSql,KeyBlankID);
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
           return  cursor.getString(cursor.getColumnIndex("blank_mfgName"))+ " > "+ cursor.getString(cursor.getColumnIndex("blankName"));
        }
        return "";
    }

    /**
     *根据钥匙坯ID查询数据
     * 匙坯ID
     * @return
     */
    public List<key_blank> KeyBlankByID(int id)
    {
        List<key_blank> lstkey_blank=new ArrayList();
        String strSql = "SELECT * FROM key_blank WHERE id=" + String.valueOf(id);
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile=cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg=cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));
            key_blank mdkey_blank=new key_blank(nID,nfk_profile,strName,nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return  lstkey_blank;
    }

    /**
     * 根据钥匙坯名称查询数据(模糊查询)
     * @param name 钥匙坯名称
     * @return 返回钥匙坯列表
     */
    public List<key_blank> KeyBlankByName(String name)
    {
        List<key_blank> lstkey_blank=new ArrayList();
        String strSql = "SELECT * FROM key_blank WHERE name like '%" + name + "%' ORDER BY name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile=cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg=cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank=new key_blank(nID,nfk_profile,strName,nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return  lstkey_blank;
    }

    /**
     * 根据钥匙坯名称和钥匙坯厂商查询数据
     * @param  name 钥匙坯名称
     * @param KeyBlankMfgID 钥匙坯厂商ID
     * @return 返回钥匙坯列表
     */
    public List<key_blank> KeyBlankByNameANDKeyBlankName(String name, int KeyBlankMfgID)
    {
        List<key_blank> lstkey_blank=new ArrayList();
        String strSql = "select * from key_blank where name like '%" + name + "%' and fk_key_blank_mfg=" + String.valueOf(KeyBlankMfgID) + " order by name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile=cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg=cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank=new key_blank(nID,nfk_profile,strName,nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return  lstkey_blank;
    }

    /**
     * 根据钥匙坯厂商ID查询钥匙坯信息
     * @param ProfileID 厂商ID
     * @return 返回钥匙坯列表
     */
    public List<key_blank> KeyBlankByProfile(int ProfileID)
    {
        List<key_blank> lstkey_blank=new ArrayList();
        String strSql = "select * from key_blank where fk_profile=" + ProfileID + " ORDER BY name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile=cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg=cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank=new key_blank(nID,nfk_profile,strName,nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return  lstkey_blank;
    }

    public List<key_blank> KeyBlankByProfileAndKeyBlankMfgID(int[] ProfileIDs, int KeyBlankMfgID)
    {
        List<key_blank> lstkey_blank=new ArrayList();
        String strSql = "select * from key_blank where (";
        int num = 0;
        for (int i = 0; i < ProfileIDs.length; i++)
        {
            if (ProfileIDs[i] != 0)
            {
                if (num > 0)
                {
                    strSql = strSql + " OR ";
                }
                strSql = strSql + " fk_profile=" +String.valueOf(ProfileIDs[i]);
                num++;
            }
        }
        strSql = strSql + ") AND fk_key_blank_mfg=" + String.valueOf(KeyBlankMfgID) + " ORDER BY name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile=cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg=cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank=new key_blank(nID,nfk_profile,strName,nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return  lstkey_blank;
    }

    /**
     * 根据ID查询钥匙坯厂商
     * @param id 钥匙坯厂商ID
     * @return 返回钥匙坯厂商列表
     */
    public List<key_blank_mfg> KeyBlankMfgByID(int id)
    {
        List<key_blank_mfg> lstkey_blank_mfg=new ArrayList();
        String strSql = "SELECT * FROM key_blank_mfg WHERE id=" +String.valueOf(id);
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            String strdesc=cursor.getString(cursor.getColumnIndex("desc"));

            key_blank_mfg mdkey_blank=new key_blank_mfg(nID,strName,strdesc);
            lstkey_blank_mfg.add(mdkey_blank);
        }
        return  lstkey_blank_mfg;
    }

    /**
     * 根据钥匙坯名称查询钥匙坯厂商(钥匙坯名称模糊查询)
     * @param name 钥匙坯名称
     * @return 返回钥匙坯厂商列表
     */
    public List KeyBlankMfgByKeyBlankName(String name)
    {
        List lstkey_blank_mfg=new ArrayList();
        String strSql = "select distinct * from key_blank_mfg, (select fk_Key_blank_mfg as ID from key_blank where name like '%" + name + "%' ) as _key_blank_ where key_blank_mfg.[ID] = _key_blank_.ID order by name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            String strdesc=cursor.getString(cursor.getColumnIndex("desc"));

            key_blank_mfg mdkey_blank=new key_blank_mfg(nID,strName,strdesc);
            lstkey_blank_mfg.add(mdkey_blank);
        }
        cursor.close();
        db.close();
        return  lstkey_blank_mfg;
    }

    public List<key_blank_mfg> KeyBlankMfgByName(String name)
    {
        List<key_blank_mfg> lstkey_blank_mfg=new ArrayList();
        String strSql = "SELECT * FROM key_blank_mfg WHERE name like '%" + name + "%' ORDER BY name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);

        while(cursor.moveToNext()){
            int nID=cursor.getInt(cursor.getColumnIndex("ID"));
            String strName=cursor.getString(cursor.getColumnIndex("name"));
            String strdesc=cursor.getString(cursor.getColumnIndex("desc"));

            key_blank_mfg mdkey_blank=new key_blank_mfg(nID,strName,strdesc);
            lstkey_blank_mfg.add(mdkey_blank);
        }
        return  lstkey_blank_mfg;
    }

    /**
     *根据类型查询品牌信息
     * @param category 品牌类型
     * @returnID 返回品牌列表
     */
    public List<mfg> MfgByCategory(int category) {
        List<mfg> lstmfg = new ArrayList();
        String strSql = "SELECT * FROM mfg ";
        switch (category) {
            case 0:
//                if (!UserDB)
//                {
//                    sql = sql + "WHERE  is_visible=1 ";
//                }
//                else
//                {
//
//                }
                strSql = strSql + "WHERE  is_visible=1 ";
                break;

            case 1:
                strSql = strSql + "WHERE is_automobile=1 AND is_visible=1 ";
                break;

            case 2:
                strSql = strSql + "WHERE is_motorcycle=1 AND is_visible=1 ";
                break;

            case 3:
                strSql = strSql + "WHERE is_dimple=1 AND is_visible=1 ";
                break;

            case 4:
                strSql = strSql + "WHERE is_standard=1 AND is_visible=1 ";
                break;

            case 5:
                strSql = strSql + "WHERE is_tubular=1 AND is_visible=1 ";
                break;

            case 6:
                strSql = strSql + "WHERE is_chs=1 AND is_visible=1 ";
                break;
        }
        strSql = strSql + "ORDER BY name";
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle=cursor.getInt(cursor.getColumnIndex("is_motorcycle"))==1?true:false;
            boolean bis_dimple=cursor.getInt(cursor.getColumnIndex("is_dimple"))==1?true:false;
            boolean bis_tubular=cursor.getInt(cursor.getColumnIndex("is_tubular"))==1?true:false;
            boolean bis_standard=cursor.getInt(cursor.getColumnIndex("is_standard"))==1?true:false;
            boolean bis_kor=cursor.getInt(cursor.getColumnIndex("is_kor"))==1?true:false;
            boolean bis_chs=cursor.getInt(cursor.getColumnIndex("is_chs"))==1?true:false;
            boolean bis_sec=false;//cursor.getInt(cursor.getColumnIndex("is_sec"))==1?true:false;

            mfg mdmfg = new mfg(nID,nType, strName, bis_visible,strdesc,bis_automobile,bis_motorcycle,bis_dimple,bis_tubular
                    ,bis_standard,bis_kor,bis_chs,bis_sec);
            lstmfg.add(mdmfg);
        }
        cursor.close();
        db.close();
        return lstmfg;
    }

    public List<mfg> MfgByID(int id)
    {
        List<mfg> lstmfg = new ArrayList();
        String strSql = "SELECT * FROM mfg WHERE id=" + String.valueOf(id);
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("Type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle=cursor.getInt(cursor.getColumnIndex("is_motorcycle"))==1?true:false;
            boolean bis_dimple=cursor.getInt(cursor.getColumnIndex("is_dimple"))==1?true:false;
            boolean bis_tubular=cursor.getInt(cursor.getColumnIndex("is_tubular"))==1?true:false;
            boolean bis_standard=cursor.getInt(cursor.getColumnIndex("is_standard"))==1?true:false;
            boolean bis_kor=cursor.getInt(cursor.getColumnIndex("is_kor"))==1?true:false;
            boolean bis_chs=cursor.getInt(cursor.getColumnIndex("is_chs"))==1?true:false;
            boolean bis_sec=cursor.getInt(cursor.getColumnIndex("is_sec"))==1?true:false;

            mfg mdmfg = new mfg(nID,nType, strName, bis_visible,strdesc,bis_automobile,bis_motorcycle,bis_dimple,bis_tubular
                    ,bis_standard,bis_kor,bis_chs,bis_sec);
            lstmfg.add(mdmfg);
        }
        return lstmfg;
    }

    public List<mfg> MfgByNamePrefix(String prefix, int category)
    {
        List<mfg> lstmfg = new ArrayList();
        String strSql = "SELECT * FROM mfg WHERE ";
        strSql = strSql + "(";
        for (int i = 0; i < prefix.length(); i++)
        {
            //有点问题
            if (i < (prefix.length() - 1))
            {
                strSql = strSql + " name LIKE "+prefix.charAt(i)+" % OR ";
            }
        }

        strSql = strSql + ") ";
        switch (category)
        {
            case 0:
                strSql = strSql;
                break;
            case 1:
                strSql = strSql + " AND is_automobile=1 ";
                break;

            case 2:
                strSql = strSql + " AND is_motorcycle=1 ";
                break;

            case 3:
                strSql = strSql + " AND is_dimple=1 ";
                break;

            case 4:
                strSql = strSql + " AND is_standard=1 ";
                break;

            case 5:
                strSql = strSql + " AND is_tubular=1 ";
                break;

            case 6:
                strSql = strSql + " AND is_chs=1 ";
                break;
        }
        strSql = strSql + "AND is_visible=1 ORDER BY name";
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("Type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle=cursor.getInt(cursor.getColumnIndex("is_motorcycle"))==1?true:false;
            boolean bis_dimple=cursor.getInt(cursor.getColumnIndex("is_dimple"))==1?true:false;
            boolean bis_tubular=cursor.getInt(cursor.getColumnIndex("is_tubular"))==1?true:false;
            boolean bis_standard=cursor.getInt(cursor.getColumnIndex("is_standard"))==1?true:false;
            boolean bis_kor=cursor.getInt(cursor.getColumnIndex("is_kor"))==1?true:false;
            boolean bis_chs=cursor.getInt(cursor.getColumnIndex("is_chs"))==1?true:false;
            boolean bis_sec=cursor.getInt(cursor.getColumnIndex("is_sec"))==1?true:false;

            mfg mdmfg = new mfg(nID,nType, strName, bis_visible,strdesc,bis_automobile,bis_motorcycle,bis_dimple,bis_tubular
                    ,bis_standard,bis_kor,bis_chs,bis_sec);
            lstmfg.add(mdmfg);
        }
        return lstmfg;
    }

    public List<mfg> MfgByProfileType(int profile_type)
    {
        List<mfg> lstmfg = new ArrayList();
        String strSql = "SELECT distinct mfg.* FROM mfg, series, (SELECT ID from profile where type=";
        strSql = strSql +String.valueOf(profile_type) + ") AS profile WHERE mfg.ID=series.fk_mfg and profile.ID=series.fk_profile  ORDER BY name";
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("Type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle=cursor.getInt(cursor.getColumnIndex("is_motorcycle"))==1?true:false;
            boolean bis_dimple=cursor.getInt(cursor.getColumnIndex("is_dimple"))==1?true:false;
            boolean bis_tubular=cursor.getInt(cursor.getColumnIndex("is_tubular"))==1?true:false;
            boolean bis_standard=cursor.getInt(cursor.getColumnIndex("is_standard"))==1?true:false;
            boolean bis_kor=cursor.getInt(cursor.getColumnIndex("is_kor"))==1?true:false;
            boolean bis_chs=cursor.getInt(cursor.getColumnIndex("is_chs"))==1?true:false;
            boolean bis_sec=cursor.getInt(cursor.getColumnIndex("is_sec"))==1?true:false;

            mfg mdmfg = new mfg(nID,nType, strName, bis_visible,strdesc,bis_automobile,bis_motorcycle,bis_dimple,bis_tubular
                    ,bis_standard,bis_kor,bis_chs,bis_sec);
            lstmfg.add(mdmfg);
        }
        return lstmfg;
    }

    public List<model> ModelByID(int id)
    {
        List<model> lstmodel = new ArrayList();
        String strSql = "SELECT * FROM model WHERE id=" + String.valueOf(id);
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));

            model mdmodel = new model(nID,strName, strdesc,nfk_mfg);
            lstmodel.add(mdmodel);
        }
        return lstmodel;
    }

    public List<model> ModelByMfgID(int id)
    {
        List<model> lstmodel = new ArrayList();
        String strSql = "SELECT * FROM model WHERE fk_mfg=" + String.valueOf(id) + " ORDER BY name";
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));

            model mdmodel = new model(nID,strName, strdesc,nfk_mfg);
            lstmodel.add(mdmodel);
        }
        return lstmodel;
    }

    public List<model> ModelByMfgIDAndNamePrefix(int id_mfg, String prefix)
    {
        List<model> lstmodel = new ArrayList();
        String strSql = ("SELECT * FROM model WHERE  fk_mfg=" + String.valueOf(id_mfg) + " AND ") + "(";
        for (int i = 0; i < prefix.length(); i++)
        {
            //有点问题
//            object obj2 = sql;
//            sql = string.Concat(new object[] { obj2, "name LIKE '", prefix[i], "%'" });
            strSql = strSql + " name LIKE "+prefix.charAt(i)+" % ";
            if (i < (prefix.length() - 1))
            {
                strSql = strSql + " OR ";
            }
        }
        strSql = strSql + ") ORDER BY name";
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));

            model mdmodel = new model(nID,strName, strdesc,nfk_mfg);
            lstmodel.add(mdmodel);
        }
        return lstmodel;
    }

    public List<profile> ProfileByID(int id)
    {
        List<profile> lstprofile = new ArrayList();
        String strSql = "SELECT * FROM profile WHERE id=" + String.valueOf(id);
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int ntype = cursor.getInt(cursor.getColumnIndex("type"));
            int nalign = cursor.getInt(cursor.getColumnIndex("align"));
            int nwidth = cursor.getInt(cursor.getColumnIndex("width"));
            int nthick = cursor.getInt(cursor.getColumnIndex("thick"));
            int nlength = cursor.getInt(cursor.getColumnIndex("length"));
            int nrow_count = cursor.getInt(cursor.getColumnIndex("row_count"));
            int nface = cursor.getInt(cursor.getColumnIndex("face"));
            String strrow_pos = cursor.getString(cursor.getColumnIndex("row_pos"));
            String strspace = cursor.getString(cursor.getColumnIndex("space"));
            String strspace_width = cursor.getString(cursor.getColumnIndex("space_width"));
            String strdepth = cursor.getString(cursor.getColumnIndex("depth"));
            String strdepth_name = cursor.getString(cursor.getColumnIndex("depth_name"));
            String strtype_specific_info = cursor.getString(cursor.getColumnIndex("type_specific_info"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            String strdesc_user = cursor.getString(cursor.getColumnIndex("desc_user"));


            profile mdprofile = new profile(nID,ntype,nalign ,nwidth,nthick,nlength
                    ,nrow_count,nface,strrow_pos,strspace,strspace_width,strdepth,strdepth_name,strtype_specific_info,strdesc,strdesc_user);
            lstprofile.add(mdprofile);
        }
        return lstprofile;
    }

//    public DataTable ProfileFromBarcode(string barcode)
//    {
//        string sql = "SELECT * FROM barcode WHERE barcode like '%" + barcode + "%' ORDER BY ID";
//        return this._Select(sql);
//    }

    public List<series> SeriesBycode_series(int fk_profile, int year_from, int year_to)
    {
        List<series> lstseries = new ArrayList();
        String strSql = "SELECT * FROM series WHERE fk_profile="+String.valueOf(fk_profile)+" and year_from="+year_from+" and year_to="+year_to+" ";
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nlock_type = cursor.getInt(cursor.getColumnIndex("lock_type"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            int nfk_model = cursor.getInt(cursor.getColumnIndex("fk_model"));
            int nyear_from = cursor.getInt(cursor.getColumnIndex("year_from"));
            int nyear_to = cursor.getInt(cursor.getColumnIndex("year_to"));
            String strcode_series = cursor.getString(cursor.getColumnIndex("code_series"));
            String strkey_blanks = cursor.getString(cursor.getColumnIndex("key_blanks"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));

            series mdseries = new series(nID,nlock_type,nfk_mfg,nfk_model,nyear_from,nyear_to,strcode_series,strkey_blanks,strdesc,nfk_profile);
            lstseries.add(mdseries);
        }
        return lstseries;
    }

    public List<series> SeriesByID(int id)
    {
        List<series> lstseries = new ArrayList();
        String strSql = "SELECT * FROM series WHERE id=" + id;
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nlock_type = cursor.getInt(cursor.getColumnIndex("lock_type"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            int nfk_model = cursor.getInt(cursor.getColumnIndex("fk_model"));
            int nyear_from = cursor.getInt(cursor.getColumnIndex("year_from"));
            int nyear_to = cursor.getInt(cursor.getColumnIndex("year_to"));
            String strcode_series = cursor.getString(cursor.getColumnIndex("code_series"));
            String strkey_blanks = cursor.getString(cursor.getColumnIndex("key_blanks"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));

            series mdseries = new series(nID,nlock_type,nfk_mfg,nfk_model,nyear_from,nyear_to,strcode_series,strkey_blanks,strdesc,nfk_profile);
            lstseries.add(mdseries);
        }
        return lstseries;
    }

    public List<series> SeriesByMfgID(int id)
    {
        List<series> lstseries = new ArrayList();
        String strSql = "SELECT * FROM series WHERE fk_mfg=" + id;
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nlock_type = cursor.getInt(cursor.getColumnIndex("lock_type"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            int nfk_model = cursor.getInt(cursor.getColumnIndex("fk_model"));
            int nyear_from = cursor.getInt(cursor.getColumnIndex("year_from"));
            int nyear_to = cursor.getInt(cursor.getColumnIndex("year_to"));
            String strcode_series = cursor.getString(cursor.getColumnIndex("code_series"));
            String strkey_blanks = cursor.getString(cursor.getColumnIndex("key_blanks"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));

            series mdseries = new series(nID,nlock_type,nfk_mfg,nfk_model,nyear_from,nyear_to,strcode_series,strkey_blanks,strdesc,nfk_profile);
            lstseries.add(mdseries);
        }
        return lstseries;
    }

//    public DataTable SeriesByMfgIDAndModelID(int idMfg, int idModel)
//    {
//        if (idMfg == -1)
//        {
//            return this.SeriesByModelID(idModel);
//        }
//        if (idModel == -1)
//        {
//            return this.SeriesByMfgID(idMfg);
//        }
//        string sql = "SELECT * FROM series WHERE fk_mfg=" + idMfg.ToString() + " AND fk_model=" + idModel.ToString();
//        return this._Select(sql);
//    }

    public List<series> SeriesByModelID(int id)
    {
        List<series> lstseries = new ArrayList();
        String strSql = "SELECT * FROM series WHERE fk_model=" + id;
        SQLiteDatabase db = sqliteOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nlock_type = cursor.getInt(cursor.getColumnIndex("lock_type"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            int nfk_model = cursor.getInt(cursor.getColumnIndex("fk_model"));
            int nyear_from = cursor.getInt(cursor.getColumnIndex("year_from"));
            int nyear_to = cursor.getInt(cursor.getColumnIndex("year_to"));
            String strcode_series = cursor.getString(cursor.getColumnIndex("code_series"));
            String strkey_blanks = cursor.getString(cursor.getColumnIndex("key_blanks"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));

            series mdseries = new series(nID,nlock_type,nfk_mfg,nfk_model,nyear_from,nyear_to,strcode_series,strkey_blanks,strdesc,nfk_profile);
            lstseries.add(mdseries);
        }
        return lstseries;
    }
   public List  firstQuery(String name){

       if(name==null){
           return null;
       }
       List  mlist = new ArrayList();
       String strSql = "SELECT * FROM key_blank WHERE name like '" +name + "%' ORDER BY name";
       SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();

       Cursor cursor=db.rawQuery(strSql,null);

              while (cursor.moveToNext()){
                  String  BlankModel=cursor.getString(cursor.getColumnIndex("name"));
                  mlist.add(BlankModel);
              }
       cursor.close();
       db.close();
       return mlist;
   }
    //根据 钥匙坯的型号名字和厂商名字查询所以基础数据
    public List queryBlank(String modelName,String firmName){
        List  mlist = new ArrayList();
        String strSql = "select  A.name as name,A.desc as desc1 ,B.name as name_1,C.*  from key_blank_mfg as A inner join key_blank as B on A.ID=B.fk_key_blank_mfg inner join profile as C on C.ID=B.fk_profile where   A.name='"+firmName+"'and B.name like '"+modelName+"%' order by B.name";
        SQLiteDatabase db=sqliteOpenHelper.getReadableDatabase();
        Cursor cursor=db.rawQuery(strSql,null);
        while (cursor.moveToNext()){
            String companyName=cursor.getString(cursor.getColumnIndex("name"));
            String modelName1=cursor.getString(cursor.getColumnIndex("name_1"));
            int IC_card=cursor.getInt(cursor.getColumnIndex("ID"));
            int  type=cursor.getInt(cursor.getColumnIndex("type"));
            int align=cursor.getInt(cursor.getColumnIndex("align"));
            int width=cursor.getInt(cursor.getColumnIndex("width"));
            int thick=cursor.getInt(cursor.getColumnIndex("thick"));
            int length=cursor.getInt(cursor.getColumnIndex("length"));
            int row_count=cursor.getInt(cursor.getColumnIndex("row_count"));
            String face=cursor.getString(cursor.getColumnIndex("face"));
            String row_pos=cursor.getString(cursor.getColumnIndex("row_pos"));
            String space=cursor.getString(cursor.getColumnIndex("space"));
            String space_width=cursor.getString(cursor.getColumnIndex("space_width"));
            String depth=cursor.getString(cursor.getColumnIndex("depth"));
            String depth_name=cursor.getString(cursor.getColumnIndex("depth_name"));
            String type_specific_info=cursor.getString(cursor.getColumnIndex("type_specific_info"));
            String desc=cursor.getString(cursor.getColumnIndex("desc1"));
            keyBlankInfo  info =new keyBlankInfo(companyName,modelName1,IC_card,type,align,width,thick,length,row_count,face,row_pos,space,space_width,depth,depth_name,type_specific_info,desc);
            mlist.add(info);
        }
        cursor.close();
        db.close();
        return   mlist;

    }

}
