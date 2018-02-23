package com.kkkcut.www.myapplicationkukai.entity;



/**
 * 类型类
 */

public class Model {
    private int id;
    private String name;
    private String desc;
    private int fkMfg;
    private String sortLetters;  //显示数据拼音的首字母
    private String showName;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public Model(int id, String name, String desc, int fkMfg) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.fkMfg = fkMfg;
    }

    public Model() {
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFkMfg() {
        return this.fkMfg;
    }

    public void setFkMfg(int fkMfg) {
        this.fkMfg = fkMfg;
    }



}
