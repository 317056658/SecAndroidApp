package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 型号类
 */

public class Model {
    private int _id;
    private String name;
    private String _desc;
    private int fk_mfg;
    private String sortLetters;  //显示数据拼音的首字母

    public Model() {

    }

    public Model(int _id, String _name, String _desc, int _fk_mfg) {
        this._id = _id;
        this.name = _name;
        this._desc = _desc;
    }
    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public int get_id() {
        return _id;}

    public void set_id(int value) {
        this._id = value;}

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String get_desc() {
        return _desc;
    }

    public void set_desc(String value) {
        this.name = value;
    }

    public int getFk_mfg() {
        return fk_mfg;}

    public void setFk_mfg(int value) {
        this.fk_mfg = value;}
}
