package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.kkkcut.www.myapplicationkukai.entity.KeyBlankMfg;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Mfg;
import com.kkkcut.www.myapplicationkukai.entity.Model;
import com.kkkcut.www.myapplicationkukai.entity.Multilingual;
import com.kkkcut.www.myapplicationkukai.entity.Series;
import com.kkkcut.www.myapplicationkukai.entity.key_blank;
import com.kkkcut.www.myapplicationkukai.keyDataSelect.CharacterParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */

public class SQLiteKeyDao {
    private DatabaseHelper databaseHelper;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;


    public SQLiteKeyDao(Context context, String dataBaseName) {
        databaseHelper = new DatabaseHelper(context,dataBaseName);
    }

    /**
     * 实例化汉字转拼音类
     */
    public  void setCharacterParser(CharacterParser characterParser){
        this.characterParser = characterParser;
    }



    //模糊查询
    public void queryByLike(String name) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        //表名    要查询的列， 条件    条件参数                分组，分组后筛选，排序
        Cursor cursor = db.query("Brand", null, "name like ?", new String[]{"%" + name + "%"}, null, null, null);
        cursor.close();
        db.close();
    }

    //根据id删除
    public void deleteByid(int _id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.delete("CarKey", "_id=?", new String[]{_id + ""});
        db.close();
    }

    /**
     * 修改一条数据
     *
     * @param name 要修改的名字
     * @param _id  根据条件id
     */
    public void update(String name, int _id) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String   SQl="select ID,depth,depth_name from  profile";
        Cursor cursor  =db.rawQuery(SQl,null);
        while (cursor.moveToNext()){
             int  id     =cursor.getInt(cursor.getColumnIndex("ID"));
            String  depth =cursor.getString(cursor.getColumnIndex("depth"));
            String  depthName= cursor.getString(cursor.getColumnIndex("depth_name"));
            String[] depthArray    =depth.split(";");
            // 可能是这样;; 是0，可能是1,2,3,;;  可能是;1,2;  可能是1,2;;3,
            String[]  depthNameArray =depthName.split(";");
            for(int i=0;i<depthArray.length;i++){
                for(int j=0;j<depthArray[i].split(",").length;j++){

                }
            }

        }

    }



    /**
     * 查询钥匙坯名称
     * 钥匙坯
     * 返回钥匙坯品牌和钥匙坯型号
     */
    public String GetKeyBlankSummary(int KeyBlankID) {
        String strSql = "select key_blank_mfg.Name as blank_mfgName,key_blank.name as blankName from key_blank_mfg inner join key_blank on key_blank_mfg.ID=key_blank.fk_key_blank_mfg where key_blank.ID=%s";
        strSql = String.format(strSql, KeyBlankID);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            return cursor.getString(cursor.getColumnIndex("blank_mfgName")) + " > " + cursor.getString(cursor.getColumnIndex("blankName"));
        }
        return "";
    }


    /**
     * 根据钥匙坯名称查询数据(模糊查询)
     *
     * @param name 钥匙坯名称
     * @return 返回钥匙坯列表
     */
    public List<key_blank> KeyBlankByName(String name) {
        List<key_blank> lstkey_blank = new ArrayList();
        String strSql = "SELECT * FROM key_blank WHERE name like '%" + name + "%' ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg = cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank = new key_blank(nID, nfk_profile, strName, nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return lstkey_blank;
    }

    /**
     * 根据钥匙坯名称和钥匙坯厂商查询数据
     *
     * @param name          钥匙坯名称
     * @param KeyBlankMfgID 钥匙坯厂商ID
     * @return 返回钥匙坯列表
     */
    public List<key_blank> KeyBlankByNameANDKeyBlankName(String name, int KeyBlankMfgID) {
        List<key_blank> lstkey_blank = new ArrayList();
        String strSql = "select * from key_blank where name like '%" + name + "%' and fk_key_blank_mfg=" + String.valueOf(KeyBlankMfgID) + " order by name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg = cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank = new key_blank(nID, nfk_profile, strName, nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return lstkey_blank;
    }

    /**
     * 根据钥匙坯厂商ID查询钥匙坯信息
     *
     * @param ProfileID 厂商ID
     * @return 返回钥匙坯列表
     */
    public List<key_blank> KeyBlankByProfile(int ProfileID) {
        List<key_blank> lstkey_blank = new ArrayList();
        String strSql = "select * from key_blank where fk_profile=" + ProfileID + " ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg = cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank mdkey_blank = new key_blank(nID, nfk_profile, strName, nfk_key_blank_mfg);
            lstkey_blank.add(mdkey_blank);
        }
        return lstkey_blank;
    }

    public List<key_blank> KeyBlankByProfileAndKeyBlankMfgID(int[] ProfileIDs, int KeyBlankMfgID) {
        List<key_blank> lstkey_blank = new ArrayList();
        String strSql = "select * from key_blank where (";
        int num = 0;
        for (int i = 0; i < ProfileIDs.length; i++) {
            if (ProfileIDs[i] != 0) {
                if (num > 0) {
                    strSql = strSql + " OR ";
                }
                strSql = strSql + " fk_profile=" + String.valueOf(ProfileIDs[i]);
                num++;
            }
        }
        strSql = strSql + ") AND fk_key_blank_mfg=" + String.valueOf(KeyBlankMfgID) + " ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nfk_profile = cursor.getInt(cursor.getColumnIndex("fk_profile"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            int nfk_key_blank_mfg = cursor.getInt(cursor.getColumnIndex("fk_key_blank_mfg"));

            key_blank keyBlank = new key_blank(nID, nfk_profile, strName, nfk_key_blank_mfg);
            lstkey_blank.add(keyBlank);
        }
        return lstkey_blank;
    }

    /**
     * 根据ID查询钥匙坯厂商
     *
     * @param id 钥匙坯厂商ID
     * @return 返回钥匙坯厂商列表
     */
    public List<KeyBlankMfg> KeyBlankMfgByID(int id) {
        List<KeyBlankMfg> lstkey_blank_mfg = new ArrayList();
        String strSql = "SELECT * FROM key_blank_mfg WHERE id=" + String.valueOf(id);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));

            KeyBlankMfg mdkey_blank = new KeyBlankMfg(nID, strName, strdesc);
            lstkey_blank_mfg.add(mdkey_blank);
        }
        return lstkey_blank_mfg;
    }

    /**
     * 根据钥匙坯名称查询钥匙坯厂商(钥匙坯名称模糊查询)
     *
     * @param name 钥匙坯名称
     * @return 返回钥匙坯厂商列表
     */
    public List KeyBlankMfgByKeyBlankName(String name) {
        List list = new ArrayList();
        String strSql = "select distinct * from key_blank_mfg, (select fk_Key_blank_mfg as ID from key_blank where name like '%" + name + "%' ) as _key_blank_ where key_blank_mfg.[ID] = _key_blank_.ID order by name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strDesc = cursor.getString(cursor.getColumnIndex("desc"));
            KeyBlankMfg kbm = new KeyBlankMfg(id, strName, strDesc);
            list.add(kbm);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<KeyBlankMfg> KeyBlankMfgByName(String name) {
        List<KeyBlankMfg> lstkey_blank_mfg = new ArrayList();
        String strSql = "SELECT * FROM key_blank_mfg WHERE name like '%" + name + "%' ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));

            KeyBlankMfg mdkey_blank = new KeyBlankMfg(nID, strName, strdesc);
            lstkey_blank_mfg.add(mdkey_blank);
        }
        return lstkey_blank_mfg;
    }

    /**
     * 根据类型查询品牌信息
     *
     * @param category 品牌类型
     * @returnID 返回品牌列表
     */
    public List<Mfg> mfgByCategory(int category) {
        List<Mfg> mfgList = new ArrayList();
        String strSql = "SELECT * FROM mfg ";
        switch (category) {
            case 0:
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strDesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle = cursor.getInt(cursor.getColumnIndex("is_motorcycle")) == 1 ? true : false;
            boolean bis_dimple = cursor.getInt(cursor.getColumnIndex("is_dimple")) == 1 ? true : false;
            boolean bis_tubular = cursor.getInt(cursor.getColumnIndex("is_tubular")) == 1 ? true : false;
            boolean bis_standard = cursor.getInt(cursor.getColumnIndex("is_standard")) == 1 ? true : false;
            boolean bis_kor = cursor.getInt(cursor.getColumnIndex("is_kor")) == 1 ? true : false;
            boolean bis_chs = cursor.getInt(cursor.getColumnIndex("is_chs")) == 1 ? true : false;
            boolean bis_sec = false;//cursor.getInt(cursor.getColumnIndex("is_sec"))==1?true:false;
            Mfg mfg = new Mfg(id, type, strName, bis_visible, strDesc, bis_automobile, bis_motorcycle, bis_dimple, bis_tubular
                    , bis_standard, bis_kor, bis_chs, bis_sec);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(mfg.getName());
            // 截取第一个字符转为大写
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                mfg.setSortLetters(sortString.toUpperCase());
            } else {
                mfg.setSortLetters("#");
            }
            mfgList.add(mfg);
        }
        cursor.close();
        db.close();
        return mfgList;
    }

    public List<Mfg> MfgByID(int id) {
        List<Mfg> lstmfg = new ArrayList();
        String strSql = "SELECT * FROM mfg WHERE id=" + String.valueOf(id);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("Type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle = cursor.getInt(cursor.getColumnIndex("is_motorcycle")) == 1 ? true : false;
            boolean bis_dimple = cursor.getInt(cursor.getColumnIndex("is_dimple")) == 1 ? true : false;
            boolean bis_tubular = cursor.getInt(cursor.getColumnIndex("is_tubular")) == 1 ? true : false;
            boolean bis_standard = cursor.getInt(cursor.getColumnIndex("is_standard")) == 1 ? true : false;
            boolean bis_kor = cursor.getInt(cursor.getColumnIndex("is_kor")) == 1 ? true : false;
            boolean bis_chs = cursor.getInt(cursor.getColumnIndex("is_chs")) == 1 ? true : false;
            boolean bis_sec = cursor.getInt(cursor.getColumnIndex("is_sec")) == 1 ? true : false;

            Mfg mdmfg = new Mfg(nID, nType, strName, bis_visible, strdesc, bis_automobile, bis_motorcycle, bis_dimple, bis_tubular
                    , bis_standard, bis_kor, bis_chs, bis_sec);
            lstmfg.add(mdmfg);
        }
        return lstmfg;
    }

    public List<Mfg> MfgByNamePrefix(String prefix, int category) {
        List<Mfg> lstmfg = new ArrayList();
        String strSql = "SELECT * FROM mfg WHERE ";
        strSql = strSql + "(";
        for (int i = 0; i < prefix.length(); i++) {
            //有点问题
            if (i < (prefix.length() - 1)) {
                strSql = strSql + " name LIKE " + prefix.charAt(i) + " % OR ";
            }
        }

        strSql = strSql + ") ";
        switch (category) {
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("Type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle = cursor.getInt(cursor.getColumnIndex("is_motorcycle")) == 1 ? true : false;
            boolean bis_dimple = cursor.getInt(cursor.getColumnIndex("is_dimple")) == 1 ? true : false;
            boolean bis_tubular = cursor.getInt(cursor.getColumnIndex("is_tubular")) == 1 ? true : false;
            boolean bis_standard = cursor.getInt(cursor.getColumnIndex("is_standard")) == 1 ? true : false;
            boolean bis_kor = cursor.getInt(cursor.getColumnIndex("is_kor")) == 1 ? true : false;
            boolean bis_chs = cursor.getInt(cursor.getColumnIndex("is_chs")) == 1 ? true : false;
            boolean bis_sec = cursor.getInt(cursor.getColumnIndex("is_sec")) == 1 ? true : false;

            Mfg mdmfg = new Mfg(nID, nType, strName, bis_visible, strdesc, bis_automobile, bis_motorcycle, bis_dimple, bis_tubular
                    , bis_standard, bis_kor, bis_chs, bis_sec);
            lstmfg.add(mdmfg);
        }
        return lstmfg;
    }

    public List<Mfg> MfgByProfileType(int profile_type) {
        List<Mfg> lstmfg = new ArrayList();
        String strSql = "SELECT distinct mfg.* FROM mfg, series, (SELECT ID from profile where type=";
        strSql = strSql + String.valueOf(profile_type) + ") AS profile WHERE mfg.ID=series.fk_mfg and profile.ID=series.fk_profile  ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int nType = cursor.getInt(cursor.getColumnIndex("Type"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            boolean bis_visible = cursor.getInt(cursor.getColumnIndex("is_visible")) == 1 ? true : false;
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            boolean bis_automobile = cursor.getInt(cursor.getColumnIndex("is_automobile")) == 1 ? true : false;
            boolean bis_motorcycle = cursor.getInt(cursor.getColumnIndex("is_motorcycle")) == 1 ? true : false;
            boolean bis_dimple = cursor.getInt(cursor.getColumnIndex("is_dimple")) == 1 ? true : false;
            boolean bis_tubular = cursor.getInt(cursor.getColumnIndex("is_tubular")) == 1 ? true : false;
            boolean bis_standard = cursor.getInt(cursor.getColumnIndex("is_standard")) == 1 ? true : false;
            boolean bis_kor = cursor.getInt(cursor.getColumnIndex("is_kor")) == 1 ? true : false;
            boolean bis_chs = cursor.getInt(cursor.getColumnIndex("is_chs")) == 1 ? true : false;
            boolean bis_sec = cursor.getInt(cursor.getColumnIndex("is_sec")) == 1 ? true : false;

            Mfg mdmfg = new Mfg(nID, nType, strName, bis_visible, strdesc, bis_automobile, bis_motorcycle, bis_dimple, bis_tubular
                    , bis_standard, bis_kor, bis_chs, bis_sec);
            lstmfg.add(mdmfg);
        }
        return lstmfg;
    }

    public List<Model> ModelByID(int id) {
        List<Model> lstmodel = new ArrayList();
        String strSql = "SELECT * FROM model WHERE id=" + String.valueOf(id);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            Model mdmodel = new Model(nID, strName, strdesc, nfk_mfg);
            lstmodel.add(mdmodel);
        }
        return lstmodel;
    }

    /**
     *  根据id 查询对应的型号表的数据
     * @param id
     * @return
     */

    public List<Model> modelByMfgID(int id) {
        List<Model> list = new ArrayList();
        String strSql = "SELECT * FROM model WHERE fk_mfg=" + String.valueOf(id) + " ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int ID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strDesc = cursor.getString(cursor.getColumnIndex("desc"));
            int fk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));

            Model model = new Model(ID, strName, strDesc, fk_mfg);

            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(model.getName());
            // 截取第一个字符转为大写
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                model.setSortLetters(sortString.toUpperCase());
            } else {
                model.setSortLetters("#");
            }
            list.add(model);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<Model> ModelByMfgIDAndNamePrefix(int id_mfg, String prefix) {
        List<Model> lstmodel = new ArrayList();
        String strSql = ("SELECT * FROM model WHERE  fk_mfg=" + String.valueOf(id_mfg) + " AND ") + "(";
        for (int i = 0; i < prefix.length(); i++) {
            //有点问题
//            object obj2 = sql;
//            sql = string.Concat(new object[] { obj2, "name LIKE '", prefix[i], "%'" });
            strSql = strSql + " name LIKE " + prefix.charAt(i) + " % ";
            if (i < (prefix.length() - 1)) {
                strSql = strSql + " OR ";
            }
        }
        strSql = strSql + ") ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strdesc = cursor.getString(cursor.getColumnIndex("desc"));
            int nfk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            Model mdmodel = new Model(nID, strName, strdesc, nfk_mfg);
            lstmodel.add(mdmodel);
        }
        return lstmodel;
    }

    /**
     * id 查询   查询钥匙的Profile表所有基本数据
     * @param id
     * @return
     */

    public KeyInfo profileByID(String  id) {
        KeyInfo ki = new KeyInfo();
        String strSql = "SELECT * FROM profile WHERE id=" +id;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int nID = cursor.getInt(cursor.getColumnIndex("ID"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            int align = cursor.getInt(cursor.getColumnIndex("align"));
            int width = cursor.getInt(cursor.getColumnIndex("width"));
            int thick = cursor.getInt(cursor.getColumnIndex("thick"));
            int length = cursor.getInt(cursor.getColumnIndex("length"));
            int rowCount = cursor.getInt(cursor.getColumnIndex("row_count"));
            String face = cursor.getString(cursor.getColumnIndex("face"));
            String rowPos = cursor.getString(cursor.getColumnIndex("row_pos"));
            String strSpace = cursor.getString(cursor.getColumnIndex("space"));
            String strSpaceWidth = cursor.getString(cursor.getColumnIndex("space_width"));
            String strDepth = cursor.getString(cursor.getColumnIndex("depth"));
            String strDepthName = cursor.getString(cursor.getColumnIndex("depth_name"));
            String type_specific_info = cursor.getString(cursor.getColumnIndex("type_specific_info"));
            String desc = cursor.getString(cursor.getColumnIndex("desc"));
            String descUser = cursor.getString(cursor.getColumnIndex("desc_user"));

            ki.setId(nID);
            ki.setType(type);
            ki.setAlign(align);
            ki.setWidth(width);
            ki.setRowCount(rowCount);
            ki.setThick(thick);
            ki.setLength(length);
            ki.setFace(face);
            ki.setRow_pos(fillNoneRowData(rowCount,rowPos));
            ki.setSpace(strSpace);
            ki.setSpace_width(fillSpaceWidth(strSpace,strSpaceWidth));
            ki.setDepth(strDepth);
            ki.setDepth_name(fillNoneDepthNameData(strDepth,strDepthName));
            ki.setType_specific_info(type_specific_info);
            ki.setDesc(desc);
            ki.setDescUser(descUser);
            this.parseKeySpecificInfo(ki);
        }
        cursor.close();
        db.close();
        return ki;
    }

    /**
     * 补充没有的Row_pos数据
     * @return
     */
    private String fillNoneRowData(int size,String row){
        String[] track=row.split(";");
        String  str="";
          if(size==0){
            return "";
          }else {
              for (int i = 0; i <size ; i++) {
                  if (TextUtils.isEmpty(row) || track.length == 0) {
                      str=str+"0;";

                  } else if (track.length > i) {
                      if (TextUtils.isEmpty(track[i])) {
                          str+="0;";
                      }else {
                          str += (track[i]+";");
                      }
                  } else {
                      str += "0;";
                  }
              }
              return str;
          }
    }

    /**
     * 补充没有的深度名数据
     * @param s1
     * @param s2
     * @return
     */
    private String fillNoneDepthNameData(String s1, String s2){
        String[]     depthArray   =s1.split(";");
        String[]    depthNameArray= s2.split(";");
        String str="";
        for (int i = 0; i <depthArray.length ; i++) {
            if (TextUtils.isEmpty(s2) || depthNameArray.length == 0) {
                for (int j = 0; j < depthArray[i].split(",").length; j++) {
                    str += (1 + j) + ",";
                }
                str = str.substring(0, str.length() - 1);
                str += ";";
            } else if (depthNameArray.length > i) {
                if (TextUtils.isEmpty(depthNameArray[i])) {
                    for (int j = 0; j < depthArray[i].split(",").length; j++) {
                        str += (1 + j) + ",";
                    }
                    str = str.substring(0, str.length() - 1);
                } else {
                    str += depthNameArray[i];
                }
                str += ";";
            } else {  //证明少了长度1；2；；
                for (int j = 0; j < depthArray[i].split(",").length; j++) {
                    str += (1 + j) + ",";
                }
                str = str.substring(0, str.length() - 1);
                str += ";";
            }
        }

        return str;
    }

    /**
     * 补充 齿顶宽
     * @param space
     * @param spaceWidth
     */
  private  String fillSpaceWidth(String space, String spaceWidth){
        String[] s1= space.split(";");
        String  str="";
        if(TextUtils.isEmpty(spaceWidth)){
            for (int i=0;i<s1.length;i++){
                for(int j=0;j<s1[i].split(",").length;j++){
                    str+="0,";
                }
                str+=";";
            }
        }else {
            str=spaceWidth;
        }
        return str;
  }




//    public DataTable ProfileFromBarcode(string barcode)
//    {
//        string sql = "SELECT * FROM barcode WHERE barcode like '%" + barcode + "%' ORDER BY ID";
//        return this._Select(sql);
//    }

    public List<Series> SeriesBycode_series(int fk_profile, int year_from, int year_to) {
        List<Series> list = new ArrayList();
        String strSql = "SELECT * FROM series WHERE fk_profile=" + String.valueOf(fk_profile) + " and year_from=" + year_from + " and year_to=" + year_to + " ";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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

            Series mdseries = new Series(nID, nlock_type, nfk_mfg, nfk_model, nyear_from, nyear_to, strcode_series, strkey_blanks, strdesc, nfk_profile);
            list.add(mdseries);
        }
        return list;
    }

    public List<Series> SeriesByID(int id) {
        List<Series> lstseries = new ArrayList();
        String strSql = "SELECT * FROM series WHERE id=" + id;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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

            Series mdseries = new Series(nID, nlock_type, nfk_mfg, nfk_model, nyear_from, nyear_to, strcode_series, strkey_blanks, strdesc, nfk_profile);
            lstseries.add(mdseries);
        }
        return lstseries;
    }

    public List<Series> SeriesByMfgID(int id) {
        List<Series> lstseries = new ArrayList();
        String strSql = "SELECT * FROM series WHERE fk_mfg=" + id;
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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

            Series mdseries = new Series(nID, nlock_type, nfk_mfg, nfk_model, nyear_from, nyear_to, strcode_series, strkey_blanks, strdesc, nfk_profile);
            lstseries.add(mdseries);
        }
        return lstseries;
    }

//    public DataTable SeriesByMfgIDAndModelID(int idMfg, int idModel)
//    {
//        if (idMfg == -1)
//        {
//            return this.seriesAndProfileByModelID(idModel);
//        }
//        if (idModel == -1)
//        {
//            return this.SeriesByMfgID(idMfg);
//        }
//        string sql = "SELECT * FROM series WHERE fk_mfg=" + idMfg.ToString() + " AND fk_model=" + idModel.ToString();
//        return this._Select(sql);
//    }

    /**
     *     明天来修改
     * 根据Model id 和key种类查询钥匙的信息
     * @param id
     * @return
     */

    public List<KeyInfo> seriesAndProfileByModelID(int id,int keyCategory) {
        List<KeyInfo> list = new ArrayList();
        String strSql ="select s.year_from,s.year_to,s.code_series,s.key_blanks,p.* from series AS s inner join profile AS p " +
                "ON s.fk_profile=p.ID where  s.fk_model="+id;
        if(keyCategory==4){  //民用钥匙查询 要加的条件
            strSql =strSql+" and p.type!=6  and p.type!=8";
        }else if(keyCategory==3){  //凹点钥匙查询 要加的条件
            strSql=strSql+" and p.type=6";
        }else if(keyCategory==5){  //圆筒钥匙查询 要加的条件
            strSql=strSql+" and p.type=8";
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql,null);
        while (cursor.moveToNext()) {
             int  yearFrom=cursor.getInt(cursor.getColumnIndex("year_from"));
            int  yearTo=cursor.getInt(cursor.getColumnIndex("year_to"));
            String codeSeries=cursor.getString(cursor.getColumnIndex("code_series"));
            String keyBlanks=cursor.getString(cursor.getColumnIndex("key_blanks"));
            int mID= cursor.getInt(cursor.getColumnIndex("ID"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            int align = cursor.getInt(cursor.getColumnIndex("align"));
            int width = cursor.getInt(cursor.getColumnIndex("width"));
            int thick = cursor.getInt(cursor.getColumnIndex("thick"));
            int length = cursor.getInt(cursor.getColumnIndex("length"));
            int rowCount = cursor.getInt(cursor.getColumnIndex("row_count"));
            String face = cursor.getString(cursor.getColumnIndex("face"));
            String rowPos = cursor.getString(cursor.getColumnIndex("row_pos"));
            String strSpace = cursor.getString(cursor.getColumnIndex("space"));
            String strSpaceWidth = cursor.getString(cursor.getColumnIndex("space_width"));
            String strDepth = cursor.getString(cursor.getColumnIndex("depth"));
            String strDepthName = cursor.getString(cursor.getColumnIndex("depth_name"));
            String type_specific_info = cursor.getString(cursor.getColumnIndex("type_specific_info"));
            String desc = cursor.getString(cursor.getColumnIndex("desc"));
            String descUser = cursor.getString(cursor.getColumnIndex("desc_user"));
            KeyInfo ki = new KeyInfo();
            ki.setYearFrom(yearFrom);
            ki.setYearTo(yearTo);
            ki.setCodeSeries(codeSeries);
            ki.setKeyBlanks(keyBlanks);
            ki.setId(mID);
            ki.setType(type);
            ki.setAlign(align);
            ki.setWidth(width);
            ki.setThick(thick);
            ki.setLength(length);
            ki.setRowCount(rowCount);
            ki.setFace(face);
            ki.setRow_pos(rowPos);
            ki.setSpace(strSpace);
            ki.setSpace_width(fillSpaceWidth(strSpace,strSpaceWidth));
            ki.setDepth(strDepth);
            ki.setDepth_name(fillNoneDepthNameData(strDepth,strDepthName));
            ki.setType_specific_info(type_specific_info);
            ki.setDesc(desc);
            ki.setDescUser(descUser);
            String str = "";
            //从多少年
            yearFrom = ki.getYearFrom();
            if (yearFrom != 0) {
                str = yearFrom + "～";
            }
            //到多少年
            yearTo = ki.getYearTo();
            if (yearTo != 0) {
                str += yearTo + "";
            }
            if (yearFrom == 0 && yearFrom == 0) {
                str += ki.getCodeSeries();
            }
            if(keyCategory==4){
                str=ki.getId()+"";
            }else if(keyCategory==3){
                str=ki.getId()+"";
            }else if(keyCategory==5){
                str=ki.getId()+"";
            }
            this.parseKeySpecificInfo(ki);
            String toothCount = "[";
            if(ki.getVariableSpace()!=null){
                toothCount+=ki.getVariableSpace()+"]";
                str+=toothCount;
            } else {
                String[] group = ki.getSpace().split(";");
                for (int j = 0; j < group.length; j++) {
                    toothCount += group[j].split(",").length + "-";
                }
                toothCount = toothCount.substring(0,toothCount.length() - 1);
                toothCount += "]";
                str += toothCount;
            }


            ki.setCombinationName(str);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(ki.getCombinationName());
            // 截取第一个字符转为大写
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                ki.setSortLetters(sortString.toUpperCase());
            } else {
                ki.setSortLetters("#");
            }
            list.add(ki);
        }
        cursor.close();
        db.close();
        return list;
    }
    /**
     * 解析钥匙的特殊信息
     * @return
     */
    private void parseKeySpecificInfo(KeyInfo ki) {
        if (!TextUtils.isEmpty(ki.getType_specific_info())) {
            String[] typeSpecificInfo = ki.getType_specific_info().trim().split(";");  //分割特殊的信息
            for (int i = 0; i < typeSpecificInfo.length; i++) {
                String[] newStr = typeSpecificInfo[i].split(":");
                if (newStr[0].equals("side")) {
                    ki.setSide(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("groove")) {
                    ki.setGroove(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("cut_depth")) {
                    ki.setCutDepth(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("guide")) {
                    ki.setGuide(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("extra_cut")) {
                    ki.setExtraCut(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("nose")) {
                    ki.setNose(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("inner_cut_length")) {
                    ki.setInnerCutLength(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("last_bitting")) {
                    ki.setLastBitting(Integer.parseInt(newStr[1]));
                } else if (newStr[0].equals("variable_space")) {
                    ki.setVariableSpace(newStr[1]);
                } else if (newStr[0].equals("sibling_profile")) {
                    ki.setSiblingProfile(newStr[1]);
                }
            }
        }
    }

    public List firstQuery(String name) {

        List mlist = new ArrayList();
        String strSql = "SELECT * FROM key_blank WHERE name like '" + name + "%' ORDER BY name";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            String BlankModel = cursor.getString(cursor.getColumnIndex("name"));
            mlist.add(BlankModel);
        }
        cursor.close();
        db.close();
        return mlist;
    }

    //根据 钥匙坯的型号名字和厂商名字查询所以基础数据

    /**
     * 根据钥匙胚的型号和厂商查询钥匙的信息
     * @param modelName
     * @param firmName
     * @return
     */
    public List keyInfoByKeyBlank(String modelName, String firmName) {
        List list = new ArrayList();
        String strSql = "select  A.name as name,A.desc as desc1 ,B.name as name_1,C.*  from " +
                "key_blank_mfg as A inner join key_blank as B on A.ID=B.fk_key_blank_mfg inner join profile as C on C.ID=B.fk_profile " +
                "where   A.name='" + firmName + "'and B.name like '%" + modelName + "%' order by C.ID DESC";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            String companyName = cursor.getString(cursor.getColumnIndex("name"));
            String modelName1 = cursor.getString(cursor.getColumnIndex("name_1"));
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            int align = cursor.getInt(cursor.getColumnIndex("align"));
            int width = cursor.getInt(cursor.getColumnIndex("width"));
            int thick = cursor.getInt(cursor.getColumnIndex("thick"));
            int length = cursor.getInt(cursor.getColumnIndex("length"));
            int row_count = cursor.getInt(cursor.getColumnIndex("row_count"));
            String face = cursor.getString(cursor.getColumnIndex("face"));
            String row_pos = cursor.getString(cursor.getColumnIndex("row_pos"));
            String space = cursor.getString(cursor.getColumnIndex("space"));
            String space_width = cursor.getString(cursor.getColumnIndex("space_width"));
            String depth = cursor.getString(cursor.getColumnIndex("depth"));
            String depth_name = cursor.getString(cursor.getColumnIndex("depth_name"));
            String type_specific_info = cursor.getString(cursor.getColumnIndex("type_specific_info"));
            String desc = cursor.getString(cursor.getColumnIndex("desc1"));
            String  descUser =cursor.getString(cursor.getColumnIndex("desc_user"));
            KeyInfo ki = new KeyInfo(companyName, modelName1, id, type, align, width, thick, length, row_count, face, row_pos, space, fillSpaceWidth(space,space_width), depth, fillNoneDepthNameData(depth,depth_name), type_specific_info, desc,descUser);
                String str="";
                str+= ki.getModelName()+"("+ ki.getId()+")";
            this.parseKeySpecificInfo(ki);
            String toothCount = "[";
            if(ki.getVariableSpace()!=null){
                toothCount+= ki.getVariableSpace()+"]";
                str+=toothCount;
            } else {
                String[] group = ki.getSpace().split(";");
                for (int j = 0; j < group.length; j++) {
                    toothCount += group[j].split(",").length + "-";
                }
                toothCount = toothCount.substring(0,toothCount.length() - 1);
                toothCount += "]";
                str += toothCount;
            }
            ki.setCombinationName(str);
            list.add(ki);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 根据id查询keyBlanks的信息
     *
     * @param id
     * @return
     */
    public String queryKeyBlanks(int id) {
        String str="";
//        List<KeyBlank> list = new ArrayList<>();
        String strSql = "select A.name as name1,GROUP_CONCAT(B.name) as name2 from  key_blank_mfg A join key_blank B on A.[ID]=B.fk_key_blank_mfg where fk_profile=" + id + "  GROUP by name1";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            String companyName = cursor.getString(cursor.getColumnIndex("name1"));
            String modelName = cursor.getString(cursor.getColumnIndex("name2"));
            str+=(companyName+":"+modelName+"\n");
//            KeyBlank blanks = new KeyBlank(companyName, modelName);
//            list.add(blanks);
        }
        cursor.close();
        db.close();
        return str;
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
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



