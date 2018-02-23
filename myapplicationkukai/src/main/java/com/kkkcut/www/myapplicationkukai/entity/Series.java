package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2016/11/30.
 */

public class Series {
    private int id;
    private int lockType;
    private int fkMfg;
    private int fkModel;
    private int yearFrom;
    private int yearTo;
    private String codeSeries;
    private String keyBlanks;
    private int fkProfile;
    public Series(int id, int lockType, int fkMfg, int fkModel, int yearFrom,
            int yearTo, String codeSeries, String keyBlanks, int fkProfile) {
        this.id = id;
        this.lockType = lockType;
        this.fkMfg = fkMfg;
        this.fkModel = fkModel;
        this.yearFrom = yearFrom;
        this.yearTo = yearTo;
        this.codeSeries = codeSeries;
        this.keyBlanks = keyBlanks;
        this.fkProfile = fkProfile;
    }
    public Series() {
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getLockType() {
        return this.lockType;
    }
    public void setLockType(int lockType) {
        this.lockType = lockType;
    }
    public int getFkMfg() {
        return this.fkMfg;
    }
    public void setFkMfg(int fkMfg) {
        this.fkMfg = fkMfg;
    }
    public int getFkModel() {
        return this.fkModel;
    }
    public void setFkModel(int fkModel) {
        this.fkModel = fkModel;
    }
    public int getYearFrom() {
        return this.yearFrom;
    }
    public void setYearFrom(int yearFrom) {
        this.yearFrom = yearFrom;
    }
    public int getYearTo() {
        return this.yearTo;
    }
    public void setYearTo(int yearTo) {
        this.yearTo = yearTo;
    }
    public String getCodeSeries() {
        return this.codeSeries;
    }
    public void setCodeSeries(String codeSeries) {
        this.codeSeries = codeSeries;
    }
    public String getKeyBlanks() {
        return this.keyBlanks;
    }
    public void setKeyBlanks(String keyBlanks) {
        this.keyBlanks = keyBlanks;
    }
    public int getFkProfile() {
        return this.fkProfile;
    }
    public void setFkProfile(int fkProfile) {
        this.fkProfile = fkProfile;
    }

   

}
