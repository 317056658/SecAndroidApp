package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 型号类
 */

public class model {
    private int _id;
    private String _name;
    private String _desc;
    private int _fk_mfg;

    public model() {

    }

    public model(int _id, String _name, String _desc, int _fk_mfg) {
        this._id = _id;
        this._name = _name;
        this._desc = _desc;
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

    public int get_fk_mfg() {
        return _fk_mfg;}

    public void set_fk_mfg(int value) {
        this._fk_mfg = value;}
}
