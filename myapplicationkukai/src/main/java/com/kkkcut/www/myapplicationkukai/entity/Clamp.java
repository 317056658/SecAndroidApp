package com.kkkcut.www.myapplicationkukai.entity;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Clamp {
    /**
     * 一号夹具 定位槽二的距离
     */
    public  static  final  int ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE=2150;
    /**
     * 一号夹具 定位槽三的距离
     */
    public  static  final  int ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC=2450;
    /**
     * 一号夹具 定位槽四的距离
     */
    public  static  final  int ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC=2750;
    /**
     * 一号夹具 定位槽五的距离
     */
    public  static  final  int ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC=3250;
    /**
     *  三号夹具可用的边5.0
     */
    public  static  final  float THREE_NUMBER_CLAMP_AVAILABLE_FIVE_MM=5.0f;
    /**
     *  三号夹具可用的边3.5
     */
    public  static  final  float THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM =3.5f;
    /**
     *  清楚槽的碎屑
     */
    public  static  final  int   CLAMP_GROOVE_CHIPS=101;
    /**
     * 三号夹具 3.5MM可用
     */
    public  static  final  int   THREE_NUMBER_CLAMP_THREE_POINT_FIVE_MM_AVAILABLE=102;
    /**
     * 三号夹具 5MM可用
     */
    public  static  final  int   THREE_NUMBER_CLAMP_FIVE_MM_AVAILABLE=103;


    private int  imgHint;
    private int  locationSlot;
    private int clampType;
    public int getClampType() {
        return clampType;
    }

    public void setClampType(int clampType) {
        this.clampType = clampType;
    }

    public int getImgHint() {
        return imgHint;
    }

    public void setImgHint(int imgHint) {
        this.imgHint = imgHint;
    }

    public int getLocationSlot() {
        return locationSlot;
    }

    public void setLocationSlot(int locationSlot) {
        this.locationSlot = locationSlot;
    }
}
