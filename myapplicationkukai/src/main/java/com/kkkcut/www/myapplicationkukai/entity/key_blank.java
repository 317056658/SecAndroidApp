package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 钥匙坯类型类
 */

public class key_blank {
    private int _id;
    private int _fk_profile;
    private String _name;
    private int _fk_key_blank_mfg;

    public key_blank() {

    }

    public key_blank(int _id, int _fk_profile, String _name, int _fk_key_blank_mfg) {
        this._id = _id;
        this._fk_profile = _fk_profile;
        this._name = _name;
        this._fk_key_blank_mfg = _fk_key_blank_mfg;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int value) {
        this._id = value;
    }

    public int get_fk_profile() {
        return _fk_profile;
    }

    public void set_fk_profile(int value) {
        this._fk_profile = value;
    }

    public String get_Name() {
        return _name;
    }

    public void set_Name(String value) {
        this._name = value;
    }

    public int get_fk_key_blank_mfg() {
        return _fk_key_blank_mfg;
    }

    public void set_fk_key_blank_mfg(int value) {
        this._fk_key_blank_mfg = value;
    }
}
