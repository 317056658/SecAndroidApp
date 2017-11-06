package com.kkkcut.www.myapplicationkukai.keyDataSelect;

import com.kkkcut.www.myapplicationkukai.entity.Model;

import java.util.Comparator;

/**
 * Created by Administrator on 2017/9/18.
 */

public class ModelPinyinComparator implements Comparator<Model> {
    @Override
    public int compare(Model o1, Model o2) {
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
