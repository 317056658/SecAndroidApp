package com.kkkcut.www.myapplicationkukai.entity;

/**
 *   收藏切割记录类
 */

public class CollectCutHistory {
    private int  id;  //唯一标识
    private String step;  //步骤
    private String  keyInfo;  //钥匙信息
    private int  startFlag;


    public  CollectCutHistory(){

    }

    public  CollectCutHistory(int id,String step,String keyInfo,int startFlag){
        this.id=id;
        this.step=step;
        this.keyInfo=keyInfo;
        this.startFlag=startFlag;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(String keyInfo) {
        this.keyInfo = keyInfo;
    }
    public int getStartFlag() {
        return startFlag;
    }

    public void setStartFlag(int startFlag) {
        this.startFlag = startFlag;
    }
}
