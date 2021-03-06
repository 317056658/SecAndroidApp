package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 消息事件
 */

public class MessageEvent {
    public  static  final  int CHANGE_KEY_TOOTH_CODE=40;  //改变钥匙的齿码
    public  static  final  int MEASURE_TOOL_FINISH =41;  //测量工具进行中
    public  static  final  int CONFIRM_COLLECT_KEY=42;  //确定收藏钥匙
    public  static  final  int CLAMP_STATE_INDEX=43;   // 夹具状态索引
    public  String message;
    public  int resultCode;
    public  int  ClampStateIndex;

    public MessageEvent(String message,int resultCode) {
        this.message = message;
        this.resultCode=resultCode;
    }
    public  MessageEvent(int resultCode,int ClampStateIndex){
        this.resultCode=resultCode;
        this.ClampStateIndex=ClampStateIndex;
    }

}
