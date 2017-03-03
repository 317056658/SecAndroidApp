package com.kkkcut.www.myapplicationkukai.SortListView;

/**
 * Created by Administrator on 2016/11/23.
 */

public class GroupMemberBean {
    private String name;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母
    private int img;//显示图片
    private int  _id;  //id
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }


    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
