package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 保存版本数据的实体类
 */
public class Version {

    private String Ver;
    private String Url="http://upgrade.kkkcut.com:8033/SECAndroidServer/APK/myapplicationkukai-v03.apk"; //apk下载地址
    private String Desc;

    public String getVer() {
        return Ver;
    }

    public void setVer(String ver) {
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
}
