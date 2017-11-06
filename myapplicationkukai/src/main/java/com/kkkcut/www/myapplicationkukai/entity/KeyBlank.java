package com.kkkcut.www.myapplicationkukai.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class KeyBlank {
    private String mfgName;  //厂家名字
    private String  modelName;  //型号名
    private List<String> modelNameList;  //存放型号名字的集合

    public List<String> getModelNameList() {
        return modelNameList;
    }

    public void setModelNameList(List<String> modelNameList) {
        this.modelNameList = modelNameList;
    }
    public  KeyBlank(){

    }

    public KeyBlank(String mfgName, String modelName) {
        this.mfgName = mfgName;
        this.modelName = modelName;
    }

    public String getMfgName() {
        return mfgName;
    }

    public void setMfgName(String mfgName) {
        this.mfgName = mfgName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


}
