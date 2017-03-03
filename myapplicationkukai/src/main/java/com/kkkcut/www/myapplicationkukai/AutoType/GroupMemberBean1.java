package com.kkkcut.www.myapplicationkukai.AutoType;

/**
 * Created by Administrator on 2016/11/24.
 */

public class GroupMemberBean1 {
    private String name;   //显示的数据
    private String sortLetters;  //显示数据拼音的首字母
    private int img;//显示图片
    private int _id;

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
