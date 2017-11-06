package com.kkkcut.www.myapplicationkukai.keyDataSelect;

import com.kkkcut.www.myapplicationkukai.entity.Mfg;

import java.util.Comparator;

/**
 * Mfg类 拼音比较仪
 * Created by Administrator on 2016/11/23.
 */

public class MfgPinyinComparator implements Comparator<Mfg>{
    @Override
    public int compare(Mfg o1, Mfg o2) {
        //改变listview排序方式    @在前面就是 a-z排序前，#在前面 就是 #排前面
        if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")){
            return -1;
        } else if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());

        }

    }
}
