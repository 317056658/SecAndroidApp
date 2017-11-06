package com.kkkcut.www.myapplicationkukai.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 钥匙基本信息类
 * Created by Administrator on 2016/11/30.
 */

public class KeyInfo implements Parcelable{
    public static final int  SHOULDERS_ALIGN=0; //肩部定位
    public static final int  FRONTEND_ALIGN=1;  //前端定位
    public static final int   BILATERAL_KEY=0;  // 双边钥匙
    public static final int  UNILATERAL_KEY=1; // 单边钥匙
    public static final int  DUAL_PATH_INSIDE_GROOVE_KEY=2; //双轨内槽钥匙
    public static final int  MONORAIL_OUTER_GROOVE_KEY=3; // 单轨外槽钥匙
    public static final int   DUAL_PATH_OUTER_GROOVE_KEY=4; //双轨外槽钥匙
    public static final int   MONORAIL_INSIDE_GROOVE_KEY=5;  // 单轨内槽钥匙
    public static final int  CONCAVE_DOT_KEY=6;  //凹点钥匙
    public static final int ANGLE_KEY =7;   //角度
    public static final int  CYLINDER_KEY=8;   //圆筒钥匙
    public static final int   SIDE_TOOTH_KEY=9;   //侧齿钥匙
    private int id;
    private int type;
    private int align;
    private int width;
    private int thick;
    private int length;
    private int rowCount;
    private String face;
    private String row_pos;
    private String space;
    private String space_width;
    private String depth;
    private String depth_name;
    private String type_specific_info;
    private String desc;
    private String descUser;
    private int side;
    private int guide;
    private int cutDepth;
    private int extraCut;
    private int groove;
    private int nose;
    private int innerCutLength;
    private int lastBitting;
    private String variableSpace;

    public String getVariableSpace() {
        return variableSpace;
    }

    public void setVariableSpace(String variableSpace) {
        this.variableSpace = variableSpace;
    }

    private String keyToothCode;
    private String siblingProfile;
    private String modelName;
    private String manufacturerName;
    private int  yearFrom;
    private int  yearTo;
    private String codeSeries;
    private String keyBlanks;
    private String combinationName;  //组合的名字
   private String sortLetters;  //显示数据拼音的首字母

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getCombinationName() {
        return combinationName;
    }

    public void setCombinationName(String combinationName) {
        this.combinationName = combinationName;
    }

    public int getYearFrom() {
        return yearFrom;
    }

    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }

    public int getYearTo() {
        return yearTo;
    }

    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }

    public String getCodeSeries() {
        return codeSeries;
    }

    public void setCodeSeries(String codeSeries) {
        this.codeSeries = codeSeries;
    }

    public String getKeyBlanks() {
        return keyBlanks;
    }

    public void setKeyBlanks(String keyBlanks) {
        this.keyBlanks = keyBlanks;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getSiblingProfile() {
        return siblingProfile;
    }

    public void setSiblingProfile(String siblingProfile) {
        this.siblingProfile = siblingProfile;
    }

    public String getKeyToothCode() {
        return keyToothCode;
    }

    public void setKeyToothCode(String keyToothCode) {
        this.keyToothCode = keyToothCode;
    }

    public int getLastBitting() {
        return lastBitting;
    }

    public void setLastBitting(int lastBitting) {
        this.lastBitting = lastBitting;
    }
    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getGuide() {
        return guide;
    }

    public void setGuide(int guide) {
        this.guide = guide;
    }

    public int getCutDepth() {
        return cutDepth;
    }

    public void setCutDepth(int cutDepth) {
        this.cutDepth = cutDepth;
    }

    public int getExtraCut() {
        return extraCut;
    }

    public void setExtraCut(int extraCut) {
        this.extraCut = extraCut;
    }

    public int getGroove() {
        return groove;
    }

    public void setGroove(int groove) {
        this.groove = groove;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getThick() {
        return thick;
    }

    public void setThick(int thick) {
        this.thick = thick;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getRow_pos() {
        return row_pos;
    }

    public void setRow_pos(String row_pos) {
        this.row_pos = row_pos;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getSpace_width() {
        return space_width;
    }

    public void setSpace_width(String space_width) {
        this.space_width = space_width;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getDepth_name() {
        return depth_name;
    }

    public void setDepth_name(String depth_name) {
        this.depth_name = depth_name;
    }

    public String getType_specific_info() {
        return type_specific_info;
    }

    public void setType_specific_info(String type_specific_info) {
        this.type_specific_info = type_specific_info;
    }

    public int getNose() {
        return nose;
    }

    public void setNose(int nose) {
        this.nose = nose;
    }


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDescUser() {
        return descUser;
    }

    public void setDescUser(String descUser) {
        this.descUser = descUser;
    }
    public int getInnerCutLength() {
        return innerCutLength;
    }

    public void setInnerCutLength(int innerCutLength) {
        this.innerCutLength = innerCutLength;
    }


    public static Creator<KeyInfo> getCREATOR() {
        return CREATOR;
    }

    public KeyInfo() {

    }

    public KeyInfo(String manufacturerName,String modelName,int _id, int _type, int _align, int _width
            , int _thick, int _length, int _row_count, String _face, String _row_pos, String _space
            , String _space_width, String _depth, String _depth_name, String _type_specific_info, String _desc, String _desc_user) {
        this.manufacturerName=manufacturerName;
        this.modelName=modelName;
        this.id = _id;
        this.type = _type;
        this.align = _align;
        this.width = _width;
        this.thick = _thick;
        this.length = _length;
        this.rowCount = _row_count;
        this.row_pos = _row_pos;
        this.face=_face;
        this.space = _space;
        this.space_width = _space_width;
        this.depth = _depth;
        this.depth_name = _depth_name;
        this.type_specific_info = _type_specific_info;
        this.desc = _desc;
        this.descUser = _desc_user;
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(type);
        parcel.writeInt(align);
        parcel.writeInt(width);
        parcel.writeInt(thick);
        parcel.writeInt(length);
        parcel.writeInt(rowCount);
        parcel.writeString(face);
        parcel.writeString(row_pos);
        parcel.writeString(space);
        parcel.writeString(space_width);
        parcel.writeString(depth);
        parcel.writeString(depth_name);
        parcel.writeString(type_specific_info);
        parcel.writeString(desc);
        parcel.writeString(descUser);
        parcel.writeInt(side);
        parcel.writeInt(guide);
        parcel.writeInt(cutDepth);
        parcel.writeInt(extraCut);
        parcel.writeInt(groove);
        parcel.writeInt(nose);
        parcel.writeInt(innerCutLength);
        parcel.writeInt(lastBitting);
        parcel.writeString(variableSpace);
        parcel.writeString(keyToothCode);
        parcel.writeString(siblingProfile);
        parcel.writeString(manufacturerName);
        parcel.writeString(modelName);
        parcel.writeInt(yearFrom);
        parcel.writeInt(yearTo);
        parcel.writeString(codeSeries);
        parcel.writeString(keyBlanks);
    }


    static final Parcelable.Creator<KeyInfo> CREATOR=new Parcelable.Creator<KeyInfo>() {

        @Override
        public KeyInfo createFromParcel(Parcel parcel) {
            KeyInfo ki =new KeyInfo();
            ki.setId(parcel.readInt());
            ki.setType(parcel.readInt());
            ki.setAlign(parcel.readInt());
            ki.setWidth(parcel.readInt());
            ki.setThick(parcel.readInt());
            ki.setLength(parcel.readInt());
            ki.setRowCount(parcel.readInt());
            ki.setFace(parcel.readString());
            ki.setRow_pos(parcel.readString());
            ki.setSpace(parcel.readString());
            ki.setSpace_width(parcel.readString());
            ki.setDepth(parcel.readString());
            ki.setDepth_name(parcel.readString());
            ki.setType_specific_info(parcel.readString());
            ki.setDesc(parcel.readString());
            ki.setDescUser(parcel.readString());
            ki.setSide(parcel.readInt());
            ki.setGuide(parcel.readInt());
            ki.setCutDepth(parcel.readInt());
            ki.setExtraCut(parcel.readInt());
            ki.setGroove(parcel.readInt());
            ki.setNose(parcel.readInt());
            ki.setInnerCutLength(parcel.readInt());
            ki.setLastBitting(parcel.readInt());
            ki.setVariableSpace(parcel.readString());
            ki.setKeyToothCode(parcel.readString());
            ki.setSiblingProfile(parcel.readString());
            ki.setManufacturerName(parcel.readString());
            ki.setModelName(parcel.readString());
            ki.setYearFrom(parcel.readInt());
            ki.setYearTo(parcel.readInt());
            ki.setCodeSeries(parcel.readString());
            ki.setKeyBlanks(parcel.readString());

            return ki;
        }
        @Override
        public KeyInfo[] newArray(int i) {
            return new KeyInfo[i];
        }
    };

}
