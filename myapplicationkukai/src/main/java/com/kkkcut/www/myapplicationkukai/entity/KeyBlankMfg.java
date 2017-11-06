package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 钥匙坯厂商类
 */

public class KeyBlankMfg {
    private int id;
    private  String name;
    private  String desc;

    public KeyBlankMfg(){

    }
    public KeyBlankMfg(int _id, String _name, String _desc){
        this.id =_id;
        this.name =_name;
        this.desc =_desc;
    }

    public int getId() {
        return id;}

    public void setId(int value) {
        this.id = value;}


    public String getName() {
        return name;
    }
    public void setName(String value) {
        this.name = value;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String value) {
        this.name = value;
    }
}
