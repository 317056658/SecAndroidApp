package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2016/11/30.
 */

public class profile {
    private int _id;
    private int _type;
    private int _align;
    private int _width;
    private int _thick;
    private int _length;
    private int _row_count;
    private int _face;
    private String _row_pos;
    private String _space;
    private String _space_width;
    private String _depth;
    private String _depth_name;
    private String _type_specific_info;
    private String _desc;
    private String _desc_user;


    public profile() {

    }

    public profile(int _id, int _type, int _align, int _width
            , int _thick, int _length, int _row_count, int _face, String _row_pos, String _space
            , String _space_width, String _depth, String _depth_name, String _type_specific_info, String _desc, String _desc_user) {
        this._id = _id;
        this._type = _type;
        this._align = _align;
        this._width = _width;
        this._thick = _thick;
        this._length = _length;
        this._row_count = _row_count;
        this._row_pos = _row_pos;
        this._space = _space;
        this._space_width = _space_width;
        this._depth = _depth;
        this._depth_name = _depth_name;
        this._type_specific_info = _type_specific_info;
        this._desc = _desc;
        this._desc_user = _desc_user;
    }

    public int get_id() {
        return _id;}

    public void set_id(int value) {
        this._id = value;
    }

    public int get_type() {
        return _type;}

    public void set_type(int value) {
        this._id = value;}

    public int get_align() {
        return _align;}

    public void set_align(int value) {
        this._align = value;}

    public int get_width() {
        return _width;}

    public void set_width(int value) {
        this._width = value;}

    public int get_thick() {
        return _thick;}

    public void set_thick(int value) {
        this._thick = value;}

    public int get_length() {
        return _length;}

    public void set_length(int value) {
        this._length = value;}

    public int get_row_count() {
        return _row_count;}

    public void set_row_count(int value) {
        this._row_count = value;}

    public int get_face() {
        return _face;
    }

    public void set_face(int value) {
        this._face = value;}

    public String get_row_pos() {
        return _row_pos;
    }

    public void set_row_pos(String value) {
        this._row_pos = value;
    }

    public String get_space() {
        return _space;
    }

    public void set_space(String value) {
        this._space = value;
    }

    public String get_space_width() {
        return _space_width;
    }

    public void set_space_width(String value) {
        this._space_width = value;
    }

    public String get_depth() {
        return _depth;
    }

    public void set_depth(String value) {
        this._depth = value;
    }

    public String get_depth_name() {
        return _depth_name;
    }

    public void set_depth_name(String value) {
        this._depth_name = value;
    }

    public String get_type_specific_info() {
        return _type_specific_info;
    }

    public void set_type_specific_info(String value) {
        this._type_specific_info = value;
    }

    public String get_desc() {
        return _row_pos;
    }

    public void set_desc(String value) {
        this._desc = value;
    }

    public String get_desc_user() {
        return _desc_user;
    }

    public void set_desc_user(String value) {
        this._desc_user = value;
    }

}
