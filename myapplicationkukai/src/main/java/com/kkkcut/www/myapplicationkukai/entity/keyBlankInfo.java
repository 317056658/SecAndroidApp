package com.kkkcut.www.myapplicationkukai.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/2/22.
 */

public class keyBlankInfo implements Parcelable{

    private String firmName;
    private String modelName;
    private int IC_card;
    private  int type;
    private  int align;
    private  int width;
    private  int thick;
    private int length;
    private int row_count;
    private String face;
    private String row_pos;
    private String space;
    private String space_width;
    private String depth;
    private String depth_name;
    private String type_specific_info;
    private String desc;

    public keyBlankInfo(String firmName, String modelName, int IC_card, int type, int align, int width, int thick, int length, int row_count, String face, String row_pos, String space, String space_width, String depth, String depth_name, String type_specific_info, String desc) {
        this.firmName = firmName;
        this.modelName = modelName;
        this.IC_card = IC_card;
        this.type = type;
        this.align = align;
        this.width = width;
        this.thick = thick;
        this.length = length;
        this.row_count = row_count;
        this.face = face;
        this.row_pos = row_pos;
        this.space = space;
        this.space_width = space_width;
        this.depth = depth;
        this.depth_name = depth_name;
        this.type_specific_info = type_specific_info;
        this.desc = desc;
    }

    public String getFirmName() {
        return firmName;
    }
    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getIC_card() {
        return IC_card;
    }

    public void setIC_card(int IC_card) {
        this.IC_card = IC_card;
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

    public int getRow_count() {
        return row_count;
    }

    public void setRow_count(int row_count) {
        this.row_count = row_count;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public keyBlankInfo() {
    }

    public static  final Parcelable.Creator<keyBlankInfo> CREATOR=new Parcelable.Creator<keyBlankInfo>(){
       public keyBlankInfo createFromParcel(Parcel source) {
           //从Parcel容器中读取传递数据值，封装成Parcelable对象返回逻辑层。
           keyBlankInfo blank=new keyBlankInfo();
           blank.setFirmName(source.readString());
           blank.setModelName(source.readString());
           blank.setIC_card(source.readInt());
           blank.setType(source.readInt());
           blank.setAlign(source.readInt());
           blank.setWidth(source.readInt());
           blank.setThick(source.readInt());
           blank.setLength(source.readInt());
           blank.setRow_count(source.readInt());
           blank.setFace(source.readString());
           blank.setRow_pos(source.readString());
           blank.setSpace(source.readString());
           blank.setSpace_width(source.readString());
           blank.setDepth(source.readString());
           blank.setDepth_name(source.readString());
           blank.setType_specific_info(source.readString());
           blank.setDesc(source.readString());
           return blank;
       }


       public keyBlankInfo[] newArray(int size) {
           //创建一个类型为T，长度为size的数组，仅一句话（return new T[size])即可。方法是供外部类反序列化本类数组使用。
           return new keyBlankInfo[size];

       }
   };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(firmName);
        parcel.writeString(modelName);
        parcel.writeInt(IC_card);
        parcel.writeInt(type);
        parcel.writeInt(align);
        parcel.writeInt(width);
        parcel.writeInt(thick);
        parcel.writeInt(length);
        parcel.writeInt(row_count);
        parcel.writeString(face);
        parcel.writeString(row_pos);
        parcel.writeString(space);
        parcel.writeString(space_width);
        parcel.writeString(depth);
        parcel.writeString(depth_name);
        parcel.writeString(type_specific_info);
        parcel.writeString(desc);



    }
}
