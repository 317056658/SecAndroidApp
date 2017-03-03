package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2016/11/24.
 */

public class Auto {
    private  String name;
    private int _id;
    public Auto(){

    }
    public Auto(int _id,String name){
          this._id=_id;
        this.name=name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
