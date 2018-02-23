package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 品牌类
 */

public class Mfg {
    private int id;
    private  int type;
    private  String name;
    private boolean isVisible;
    private  String desc;
    private boolean isAutomobile;
    private boolean isMotorcycle;
    private boolean isDimple;
    private boolean isTubular;
    private boolean isStandard;
    private boolean isKor;
    private boolean isChina;
    private String sortLetters;  //显示数据拼音的首字母
    private String showName;  //显示的名字
    public Mfg(int id, int type, String name, boolean isVisible, String desc, boolean isAutomobile,
            boolean isMotorcycle, boolean isDimple, boolean isTubular, boolean isStandard, boolean isKor,
            boolean isChina) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.isVisible = isVisible;
        this.desc = desc;
        this.isAutomobile = isAutomobile;
        this.isMotorcycle = isMotorcycle;
        this.isDimple = isDimple;
        this.isTubular = isTubular;
        this.isStandard = isStandard;
        this.isKor = isKor;
        this.isChina = isChina;
    }

    public Mfg() {
    }
    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getIsVisible() {
        return this.isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getIsAutomobile() {
        return this.isAutomobile;
    }

    public void setIsAutomobile(boolean isAutomobile) {
        this.isAutomobile = isAutomobile;
    }

    public boolean getIsMotorcycle() {
        return this.isMotorcycle;
    }

    public void setIsMotorcycle(boolean isMotorcycle) {
        this.isMotorcycle = isMotorcycle;
    }

    public boolean getIsDimple() {
        return this.isDimple;
    }

    public void setIsDimple(boolean isDimple) {
        this.isDimple = isDimple;
    }

    public boolean getIsTubular() {
        return this.isTubular;
    }

    public void setIsTubular(boolean isTubular) {
        this.isTubular = isTubular;
    }

    public boolean getIsStandard() {
        return this.isStandard;
    }

    public void setIsStandard(boolean isStandard) {
        this.isStandard = isStandard;
    }

    public boolean getIsKor() {
        return this.isKor;
    }

    public void setIsKor(boolean isKor) {
        this.isKor = isKor;
    }

    public boolean getIsChina() {
        return this.isChina;
    }

    public void setIsChina(boolean isChina) {
        this.isChina = isChina;
    }
}
