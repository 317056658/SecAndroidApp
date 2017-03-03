package com.kkkcut.www.myapplicationkukai.AutoType;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/11/24.
 */

public class PinyinComparator implements Comparator<GroupMemberBean1> {

    public int compare(GroupMemberBean1 o1, GroupMemberBean1 o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
