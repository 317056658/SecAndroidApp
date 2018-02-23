package com.kkkcut.www.myapplicationkukai.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.entity.ConstantValue;
import com.kkkcut.www.myapplicationkukai.entity.KeyBlankMfg;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Mfg;
import com.kkkcut.www.myapplicationkukai.entity.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/2.
 *   这个用的是GreenDao框架数据库
 */

public class KeyInfoDaoManager {
    private static final String DB_NAME = "SEC1.db";
    private static KeyInfoDaoManager mInstanceHolder;
    private OriginalDatabaseOpenHelper databaseOpenHelper;

    public static KeyInfoDaoManager getInstance() {
        if (mInstanceHolder == null) {
            synchronized (KeyInfoDaoManager.class) {
                if (mInstanceHolder == null) {
                    mInstanceHolder = new KeyInfoDaoManager();
                }
            }
        }
        return mInstanceHolder;
    }

    private KeyInfoDaoManager() {
        databaseOpenHelper = new OriginalDatabaseOpenHelper(MyApplication.getContext(), DB_NAME);
    }

    /**
     * 根据分类查询制造商信息
     *
     * @param category
     */
    public List<Mfg> mfgByCategoryQuery(int category) {
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
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
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
//            boolean bis_sec = false;//cursor.getInt(cursor.getColumnIndex("is_sec"))==1?true:false;
            Mfg mfg = new Mfg(id, type, strName, bis_visible, strDesc, bis_automobile, bis_motorcycle, bis_dimple, bis_tubular
                    , bis_standard, bis_kor, bis_chs);
            mfgList.add(mfg);
        }
        cursor.close();
        db.close();
        return mfgList;
    }

    /**
     * 查询类型表  根据卡号(id)查询
     *
     * @param cardNumber
     */
    public List<Model> modelByMfgCardNumberQuery(int cardNumber) {
        List<Model> list = new ArrayList();
        String strSql = "SELECT * FROM model WHERE fk_mfg=" + String.valueOf(cardNumber) + " ORDER BY name";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int ID = cursor.getInt(cursor.getColumnIndex("ID"));
            String strName = cursor.getString(cursor.getColumnIndex("name"));
            String strDesc = cursor.getString(cursor.getColumnIndex("desc"));
            int fk_mfg = cursor.getInt(cursor.getColumnIndex("fk_mfg"));
            Model model = new Model(ID, strName, strDesc, fk_mfg);
            list.add(model);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 多表联查,查询系列表和基础信息表
     *
     * @return
     */
    public List<KeyInfo> QuerySeriesAndProfileByModelID(int id, int keyCategory) {
        String strSql = "select s.year_from,s.year_to,s.code_series,s.key_blanks,p.* from series AS s inner join profile AS p " +
                "ON s.fk_profile=p.ID where  s.fk_model=" + id;
        if (keyCategory == ConstantValue.KEY_CATEGORY_CIVIL) {  //民用钥匙查询 要加的条件
            strSql = strSql + " and p.type!=6  and p.type!=8";
        } else if (keyCategory == ConstantValue.KEY_CATEGORY_DIMPLE) {  //凹点钥匙查询 要加的条件
            strSql = strSql + " and p.type=6";
        } else if (keyCategory == ConstantValue.KEY_CATEGORY_TUBULAR) {  //圆筒钥匙查询 要加的条件
            strSql = strSql + " and p.type=8";
        }
        List<KeyInfo> list = new ArrayList();
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            int yearFrom = cursor.getInt(cursor.getColumnIndex("year_from"));
            int yearTo = cursor.getInt(cursor.getColumnIndex("year_to"));
            String codeSeries = cursor.getString(cursor.getColumnIndex("code_series"));
            String keyBlanks = cursor.getString(cursor.getColumnIndex("key_blanks"));
            int mID = cursor.getInt(cursor.getColumnIndex("ID"));
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
            ki.setCardNumber(mID);
            ki.setType(type);
            ki.setAlign(align);
            ki.setWidth(width);
            ki.setThick(thick);
            ki.setLength(length);
            ki.setRowCount(rowCount);
            ki.setFace(face);

            ki.setRow_pos(fillNoneRowData(rowCount, rowPos));
            ki.setSpace(strSpace);
            ki.setSpace_width(fillSpaceWidth(strSpace, strSpaceWidth));
            ki.setDepth(strDepth);
            ki.setDepth_name(fillNoneDepthNameData(strDepth, strDepthName));
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
            if (keyCategory == 4) {
                str = ki.getCardNumber() + "";
            } else if (keyCategory == 3) {
                str = ki.getCardNumber() + "";
            } else if (keyCategory == 5) {
                str = ki.getCardNumber() + "";
            }
            this.parseKeySpecificInfo(ki);
            String toothCount = "[";
            if (ki.getVariableSpace() != null) {
                toothCount += ki.getVariableSpace() + "]";
                str += toothCount;
            } else {
                String[] group = ki.getSpace().split(";");
                for (int j = 0; j < group.length; j++) {
                    toothCount += group[j].split(",").length + "-";
                }
                toothCount = toothCount.substring(0, toothCount.length() - 1);
                toothCount += "]";
                str += toothCount;
            }
            ki.setCombinationText(str);
            list.add(ki);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 补充没有的深度名数据
     *
     * @param s1
     * @param s2
     * @return
     */
    private String fillNoneDepthNameData(String s1, String s2) {
        String[] depthArray = s1.split(";");
        String[] depthNameArray = s2.split(";");
        String str = "";
        for (int i = 0; i < depthArray.length; i++) {
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
     *
     * @param space
     * @param spaceWidth
     */
    private String fillSpaceWidth(String space, String spaceWidth) {
        String[] s1 = space.split(";");
        String str = "";
        if (TextUtils.isEmpty(spaceWidth)) {
            for (int i = 0; i < s1.length; i++) {
                for (int j = 0; j < s1[i].split(",").length; j++) {
                    str += "0,";
                }
                str += ";";
            }
        } else {
            str = spaceWidth;
        }
        return str;
    }

    /**
     * 解析钥匙的特殊信息
     *
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
                } else if (newStr[0].equals("shape")) {
                    ki.setShape(Integer.parseInt(newStr[1]));
                }
            }
        }
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
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
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

    /**
     * id 查询   查询钥匙的Profile表所有基本数据
     *
     * @param id
     * @return
     */
    public KeyInfo profileByID(String id) {
        KeyInfo ki = new KeyInfo();
        String strSql = "SELECT * FROM profile WHERE id=" + id;
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
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
            ki.setCardNumber(nID);
            ki.setType(type);
            ki.setAlign(align);
            ki.setWidth(width);
            ki.setRowCount(rowCount);
            ki.setThick(thick);
            ki.setLength(length);
            ki.setFace(face);
            ki.setRow_pos(fillNoneRowData(rowCount, rowPos));
            ki.setSpace(strSpace);
            ki.setSpace_width(fillSpaceWidth(strSpace, strSpaceWidth));
            ki.setDepth(strDepth);
            ki.setDepth_name(fillNoneDepthNameData(strDepth, strDepthName));
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
     *
     * @return
     */
    private String fillNoneRowData(int size, String row) {
        String[] track = row.split(";");
        String str = "";
        if (size == 0) {
            return "";
        } else {
            for (int i = 0; i < size; i++) {
                if (TextUtils.isEmpty(row) || track.length == 0) {
                    str = str + "0;";

                } else if (track.length > i) {
                    if (TextUtils.isEmpty(track[i])) {
                        str += "0;";
                    } else {
                        str += (track[i] + ";");
                    }
                } else {
                    str += "0;";
                }
            }
            return str;
        }
    }

    public List firstQuery(String name) {
        List mlist = new ArrayList();
        String strSql = "SELECT * FROM key_blank WHERE name like '" + name + "%' ORDER BY name";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(strSql, null);

        while (cursor.moveToNext()) {
            String BlankModel = cursor.getString(cursor.getColumnIndex("name"));
            mlist.add(BlankModel);
        }
        cursor.close();
        db.close();
        return mlist;

    }

    /**
     * 根据钥匙胚厂商的ID查询钥匙信息
     *
     * @param id
     * @return
     */
    public List keyInfoByMfgIdQuery(int id) {
        List list = new ArrayList();
        String strSql = "select A.name as modelName,B.*   from   key_blank as A inner join profile as B on B.ID=A.fk_profile " +
                "where  A.fk_key_blank_mfg=" + id + " order by B.ID DESC";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            KeyInfo ki = new KeyInfo();
            String modelName = cursor.getString(cursor.getColumnIndex("modelName"));
            ki.setModelName(modelName);
            int cardNumber = cursor.getInt(cursor.getColumnIndex("ID"));
            ki.setCardNumber(cardNumber);
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
            int row_count = cursor.getInt(cursor.getColumnIndex("row_count"));
            ki.setRowCount(row_count);
            String face = cursor.getString(cursor.getColumnIndex("face"));
            ki.setFace(face);
            String row_pos = cursor.getString(cursor.getColumnIndex("row_pos"));

            ki.setRow_pos(fillNoneRowData(row_count, row_pos));
            String space = cursor.getString(cursor.getColumnIndex("space"));
            ki.setSpace(space);
            String space_width = cursor.getString(cursor.getColumnIndex("space_width"));

            ki.setSpace_width(fillSpaceWidth(space, space_width));

            String depth = cursor.getString(cursor.getColumnIndex("depth"));
            ki.setDepth(depth);
            String depth_name = cursor.getString(cursor.getColumnIndex("depth_name"));
            ki.setDepth_name(fillNoneDepthNameData(depth, depth_name));
            String type_specific_info = cursor.getString(cursor.getColumnIndex("type_specific_info"));
            ki.setType_specific_info(type_specific_info);
            String desc = cursor.getString(cursor.getColumnIndex("desc"));
            ki.setDesc(desc);
            String descUser = cursor.getString(cursor.getColumnIndex("desc_user"));
            ki.setDescUser(descUser);
            String str = "";
            str += ki.getModelName() + "(" + ki.getCardNumber() + ")";
            this.parseKeySpecificInfo(ki);
            String toothCount = "[";
            if (ki.getVariableSpace() != null) {
                toothCount += ki.getVariableSpace() + "]";
                str += toothCount;
            } else {
                String[] group = ki.getSpace().split(";");
                for (int j = 0; j < group.length; j++) {
                    toothCount += group[j].split(",").length + "-";
                }
                toothCount = toothCount.substring(0, toothCount.length() - 1);
                toothCount += "]";
                str += toothCount;
            }
            ki.setCombinationText(str);
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
        String str = "";
        String strSql = "select A.name as name1,GROUP_CONCAT(B.name) as name2 from  key_blank_mfg A join key_blank B on A.[ID]=B.fk_key_blank_mfg where fk_profile=" + id + "  GROUP by name1";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            String companyName = cursor.getString(cursor.getColumnIndex("name1"));
            String modelName = cursor.getString(cursor.getColumnIndex("name2"));
            str += (companyName + ":" + modelName + "\n");
//            KeyBlank blanks = new KeyBlank(companyName, modelName);
//            mList.add(blanks);
        }
        cursor.close();
        db.close();
        return str;
    }

    /**
     * 根据id查询 代码系列
     *
     * @param id
     * @return
     */
    public String queryCodeSeriesById(String id) {
        String strSql = "select code_series from series where  fk_profile=" + id + " LIMIT 1";
        SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
        String strCodeSeries = "";
        Cursor cursor = db.rawQuery(strSql, null);
        while (cursor.moveToNext()) {
            strCodeSeries = cursor.getString(cursor.getColumnIndex("code_series"));
        }
        cursor.close();
        db.close();
        return strCodeSeries;
    }

}
