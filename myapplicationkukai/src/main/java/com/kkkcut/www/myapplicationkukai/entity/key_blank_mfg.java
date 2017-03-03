package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 钥匙坯厂商类
 */

public class key_blank_mfg {
    private int _id;
    private  String _name;
    private  String _desc;

    public key_blank_mfg(){

    }
    public key_blank_mfg(int _id,String _name,String _desc){
        this._id=_id;
        this._name=_name;
        this._desc=_desc;
    }

    public int get_id() {
        return _id;}

    public void set_id(int value) {
        this._id = value;}


    public String get_Name() {
        return _name;
    }
    public void set_Name(String value) {
        this._name = value;
    }

    public String get_desc() {
        return _desc;
    }
    public void set_desc(String value) {
        this._name = value;
    }
}
