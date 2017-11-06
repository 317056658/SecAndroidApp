package com.kkkcut.www.myapplicationkukai.entity;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/10/19.
 */

public class LanguageEvent {
    private HashMap    languageMap;
    public  LanguageEvent(HashMap languageMap){
        this.languageMap=languageMap;
    }
    public HashMap getLanguageMap() {
        return languageMap;
    }

    public void setLanguageMap(HashMap languageMap) {
        this.languageMap = languageMap;
    }
}
