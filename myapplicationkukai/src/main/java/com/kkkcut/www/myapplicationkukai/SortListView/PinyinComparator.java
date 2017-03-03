package com.kkkcut.www.myapplicationkukai.SortListView;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/11/23.
 */

public class PinyinComparator implements Comparator<GroupMemberBean> {

    public int compare(GroupMemberBean o1, GroupMemberBean o2) {
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
