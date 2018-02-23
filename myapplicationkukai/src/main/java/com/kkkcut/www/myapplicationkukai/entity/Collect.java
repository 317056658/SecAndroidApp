package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2018/1/30.
 *  收藏类
 */

public class Collect {
    private long  id;  //唯一标识
    private String step;  //步骤
    private String  keyInfo;  //钥匙信息
    private int  startFlag;  //启动标志
    public Collect(long id, String step, String keyInfo, int startFlag) {
        this.id = id;
        this.step = step;
        this.keyInfo = keyInfo;
        this.startFlag = startFlag;
    }
    public Collect() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getStep() {
        return this.step;
    }
    public void setStep(String step) {
        this.step = step;
    }
    public String getKeyInfo() {
        return this.keyInfo;
    }
    public void setKeyInfo(String keyInfo) {
        this.keyInfo = keyInfo;
    }
    public int getStartFlag() {
        return this.startFlag;
    }
    public void setStartFlag(int startFlag) {
        this.startFlag = startFlag;
    }
}
