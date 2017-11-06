package com.kkkcut.www.myapplicationkukai.entity;

import com.kkkcut.www.myapplicationkukai.R;

/**
 * Created by Administrator on 2016/11/30.
 */

public class Series {
    private int id;
    private int _lock_type;
    private int _fk_mfg;
    private int _fk_model;
    private int _year_from;
    private int _year_to;
    private String _code_series;
    private String _key_blanks;
    private String _desc;
    private int _fk_profile;
    private int  img;
    public int ge_img(){
          return img= R.drawable.ic_launcher21;
    }

    public Series() {

    }

    public Series(int _id, int _lock_type, int _fk_mfg, int _fk_model
            , int _year_from, int _year_to, String _code_series, String _key_blanks
            , String _desc, int _fk_profile) {

        this.id = _id;
        this._lock_type = _lock_type;
        this._fk_mfg = _fk_mfg;
        this._fk_model = _fk_model;
        this._year_from = _year_from;
        this._year_to = _year_to;
        this._code_series = _code_series;
        this._key_blanks = _key_blanks;
        this._desc = _desc;
        this._fk_profile = _fk_profile;
    }

    public int getId() {
        return id;}

    public void setId(int value) {
        this.id = value;}

    public int get_lock_type() {
        return _lock_type;}

    public void set_lock_type(int value) {
        this._lock_type = value;}

    public int get_fk_mfg() {
        return _fk_mfg;}

    public void set_fk_mfg(int value) {
        this._fk_mfg = value;}

    public int get_fk_model() {
        return _fk_model;}

    public void set_fk_model(int value) {
        this._fk_model = value;}

    public int get_year_from() {
        return _year_from;}

    public void set_year_from(int value) {
        this._year_from = value;}

    public int get_year_to() {
        return _lock_type;}

    public void set_year_to(int value) {
        this._lock_type = value;}

    public String get_code_series() {
        return _code_series;
    }

    public void set_code_series(String value) {
        this._code_series = value;
    }

    public String get_key_blanks() {
        return _key_blanks;
    }

    public void set_key_blanks(String value) {
        this._key_blanks = value;
    }

    public String get_desc() {
        return _desc;
    }

    public void set_desc(String value) {
        this._desc = value;
    }

    public int get_fk_profile() {
        return _fk_profile;}

    public void set_fk_profile(int value) {
        this._fk_profile = value;}
}
