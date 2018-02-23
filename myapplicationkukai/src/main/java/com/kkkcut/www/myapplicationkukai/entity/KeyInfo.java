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

    private int startFlag;  //启动标志
    private String  step;  //步骤
    private int cardNumber;  //  卡号
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

    private String keyToothCode;

    private String siblingProfile;

    private String modelName;

    private String manufacturerName;

    private int  yearFrom;

    private int  yearTo;

    private String codeSeries;

    private String keyBlanks;

    private String combinationText;  //组合的文本

    private String sortLetters;  //显示数据拼音的首字母

    private boolean isVariableSpace; //是否可变space

    private  int clampStateIndex; //定义一个的夹具状态索引 保存用户的选择状态

    private int shape;  //外形


    protected KeyInfo(Parcel in) {
        startFlag=in.readInt();
        step=in.readString();
        cardNumber = in.readInt();
        type = in.readInt();
        align = in.readInt();
        width = in.readInt();
        thick = in.readInt();
        length = in.readInt();
        rowCount = in.readInt();
        face = in.readString();
        row_pos = in.readString();
        space = in.readString();
        space_width = in.readString();
        depth = in.readString();
        depth_name = in.readString();
        type_specific_info = in.readString();
        desc = in.readString();
        descUser = in.readString();
        side = in.readInt();
        guide = in.readInt();
        cutDepth = in.readInt();
        extraCut = in.readInt();
        groove = in.readInt();
        nose = in.readInt();
        innerCutLength = in.readInt();
        lastBitting = in.readInt();
        variableSpace = in.readString();
        keyToothCode = in.readString();
        siblingProfile = in.readString();
        modelName = in.readString();
        manufacturerName = in.readString();
        yearFrom = in.readInt();
        yearTo = in.readInt();
        codeSeries = in.readString();
        keyBlanks = in.readString();
        combinationText = in.readString();
        sortLetters = in.readString();
        isVariableSpace = in.readByte() != 0;
        clampStateIndex=in.readInt();
        shape=in.readInt();
    }

    public static final Creator<KeyInfo> CREATOR = new Creator<KeyInfo>() {
        @Override
        public KeyInfo createFromParcel(Parcel in) {
            return new KeyInfo(in);
        }

        @Override
        public KeyInfo[] newArray(int size) {
            return new KeyInfo[size];
        }
    };



    public KeyInfo(int cardNumber, int type, int align, int width, int thick, int length, int rowCount, String face, String row_pos,
            String space, String space_width, String depth, String depth_name, String type_specific_info, String desc,
            String descUser) {
        this.cardNumber = cardNumber;
        this.type = type;
        this.align = align;
        this.width = width;
        this.thick = thick;
        this.length = length;
        this.rowCount = rowCount;
        this.face = face;
        this.row_pos = row_pos;
        this.space = space;
        this.space_width = space_width;
        this.depth = depth;
        this.depth_name = depth_name;
        this.type_specific_info = type_specific_info;
        this.desc = desc;
        this.descUser = descUser;
    }

    public KeyInfo() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startFlag);
        dest.writeString(step);
        dest.writeInt(cardNumber);
        dest.writeInt(type);
        dest.writeInt(align);
        dest.writeInt(width);
        dest.writeInt(thick);
        dest.writeInt(length);
        dest.writeInt(rowCount);
        dest.writeString(face);
        dest.writeString(row_pos);
        dest.writeString(space);
        dest.writeString(space_width);
        dest.writeString(depth);
        dest.writeString(depth_name);
        dest.writeString(type_specific_info);
        dest.writeString(desc);
        dest.writeString(descUser);
        dest.writeInt(side);
        dest.writeInt(guide);
        dest.writeInt(cutDepth);
        dest.writeInt(extraCut);
        dest.writeInt(groove);
        dest.writeInt(nose);
        dest.writeInt(innerCutLength);
        dest.writeInt(lastBitting);
        dest.writeString(variableSpace);
        dest.writeString(keyToothCode);
        dest.writeString(siblingProfile);
        dest.writeString(modelName);
        dest.writeString(manufacturerName);
        dest.writeInt(yearFrom);
        dest.writeInt(yearTo);
        dest.writeString(codeSeries);
        dest.writeString(keyBlanks);
        dest.writeString(combinationText);
        dest.writeString(sortLetters);
        dest.writeByte((byte) (isVariableSpace ? 1 : 0));
        dest.writeInt(clampStateIndex);
        dest.writeInt(shape);
    }


    public int getStartFlag() {
        return this.startFlag;
    }


    public void setStartFlag(int startFlag) {
        this.startFlag = startFlag;
    }


    public int getCardNumber() {
        return this.cardNumber;
    }


    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }


    public int getType() {
        return this.type;
    }


    public void setType(int type) {
        this.type = type;
    }


    public int getAlign() {
        return this.align;
    }


    public void setAlign(int align) {
        this.align = align;
    }


    public int getWidth() {
        return this.width;
    }


    public void setWidth(int width) {
        this.width = width;
    }


    public int getThick() {
        return this.thick;
    }


    public void setThick(int thick) {
        this.thick = thick;
    }


    public int getLength() {
        return this.length;
    }


    public void setLength(int length) {
        this.length = length;
    }


    public int getRowCount() {
        return this.rowCount;
    }


    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }


    public String getFace() {
        return this.face;
    }


    public void setFace(String face) {
        this.face = face;
    }


    public String getRow_pos() {
        return this.row_pos;
    }


    public void setRow_pos(String row_pos) {
        this.row_pos = row_pos;
    }


    public String getSpace() {
        return this.space;
    }


    public void setSpace(String space) {
        this.space = space;
    }


    public String getSpace_width() {
        return this.space_width;
    }


    public void setSpace_width(String space_width) {
        this.space_width = space_width;
    }


    public String getDepth() {
        return this.depth;
    }


    public void setDepth(String depth) {
        this.depth = depth;
    }


    public String getDepth_name() {
        return this.depth_name;
    }


    public void setDepth_name(String depth_name) {
        this.depth_name = depth_name;
    }


    public String getType_specific_info() {
        return this.type_specific_info;
    }


    public void setType_specific_info(String type_specific_info) {
        this.type_specific_info = type_specific_info;
    }


    public String getDesc() {
        return this.desc;
    }


    public void setDesc(String desc) {
        this.desc = desc;
    }


    public String getDescUser() {
        return this.descUser;
    }


    public void setDescUser(String descUser) {
        this.descUser = descUser;
    }


    public int getCutDepth() {
        return this.cutDepth;
    }


    public void setCutDepth(int cutDepth) {
        this.cutDepth = cutDepth;
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

    public int getNose() {
        return nose;
    }

    public void setNose(int nose) {
        this.nose = nose;
    }

    public int getInnerCutLength() {
        return innerCutLength;
    }

    public void setInnerCutLength(int innerCutLength) {
        this.innerCutLength = innerCutLength;
    }

    public int getLastBitting() {
        return lastBitting;
    }

    public void setLastBitting(int lastBitting) {
        this.lastBitting = lastBitting;
    }

    public String getVariableSpace() {
        return variableSpace;
    }

    public void setVariableSpace(String variableSpace) {
        this.variableSpace = variableSpace;
    }

    public String getKeyToothCode() {
        return keyToothCode;
    }

    public void setKeyToothCode(String keyToothCode) {
        this.keyToothCode = keyToothCode;
    }

    public String getSiblingProfile() {
        return siblingProfile;
    }

    public void setSiblingProfile(String siblingProfile) {
        this.siblingProfile = siblingProfile;
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

    public String getCombinationText() {
        return combinationText;
    }

    public void setCombinationText(String combinationText) {
        this.combinationText = combinationText;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public boolean isVariableSpace() {
        return isVariableSpace;
    }

    public void setVariableSpace(boolean variableSpace) {
        isVariableSpace = variableSpace;
    }

    public int getClampStateIndex() {
        return clampStateIndex;
    }

    public void setClampStateIndex(int clampStateIndex) {
        this.clampStateIndex = clampStateIndex;
    }

    public int getShape() {
        return shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
