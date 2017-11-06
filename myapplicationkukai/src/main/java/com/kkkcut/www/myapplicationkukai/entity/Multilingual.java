package com.kkkcut.www.myapplicationkukai.entity;

/**  多语言类
 * Created by Administrator on 2017/5/25.
 */

public class Multilingual {
    private String tableName;
    private  String  showLanguage;
    private  boolean  Checked;

    public boolean isChecked() {
        return Checked;
    }
    public void setChecked(boolean checked) {
        Checked = checked;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getShowLanguage() {
        return showLanguage;
    }

    public void setShowLanguage(String showLanguage) {
        this.showLanguage = showLanguage;
    }





}
