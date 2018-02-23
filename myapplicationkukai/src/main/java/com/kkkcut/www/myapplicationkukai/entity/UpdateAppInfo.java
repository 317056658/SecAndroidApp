package com.kkkcut.www.myapplicationkukai.entity;


import android.os.Parcel;
import android.os.Parcelable;

public class UpdateAppInfo  implements Parcelable {

    private int Ver;
    private int State;
    private  String FileSize;  //apk大小
    private String Url; //apk下载地址
    private String Desc;
    public   UpdateAppInfo(){

    }
    protected UpdateAppInfo(Parcel in) {
        Ver = in.readInt();
        Url = in.readString();
        Desc = in.readString();
        FileSize = in.readString();
        State = in.readInt();
    }

    public static final Creator<UpdateAppInfo> CREATOR = new Creator<UpdateAppInfo>() {
        @Override
        public UpdateAppInfo createFromParcel(Parcel in) {
            return new UpdateAppInfo(in);
        }

        @Override
        public UpdateAppInfo[] newArray(int size) {
            return new UpdateAppInfo[size];
        }
    };

    public String getFileSize() {
        return FileSize;
    }

    public void setFileSize(String fileSize) {
        FileSize = fileSize;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public int getVer() {
        return Ver;
    }
    public void setVer(int ver) {
        Ver = ver;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Ver);
        dest.writeString(Url);
        dest.writeString(Desc);
        dest.writeString(FileSize);
        dest.writeInt(State);
    }
}
