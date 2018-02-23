package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 单片机事件类
 * Created by Administrator on 2017/8/11.
 */

public class MicyocoEvent {
    public static final int OPERATION_FINISH=30;  //操作完成
    public static final int CUT_KNIFE_SWITCHOVER_PROBE=31; //切割刀转换为探针
    public static final int READ_TOOTH_DATA_BACK=32;  //  读齿数据返回
    public static  final int  CUT_DATA_BACK=33;  //切割数据返回
    public static final int NO_ERROR=0;  //没有错误
    public static final int USER_CANCEL_OPERATE=1;  //用户取消操作
    public static final int X_LIMIT_OVER=2;     //X轴超限
    public static final int Y_LIMIT_OVER=3; //Y轴超限
    public static final int Z_LIMIT_OVER=4; //Z轴超限
    public static final int PROBE_ERROR=5;  //探测错误
    public static final int COMMAND_ERROR=6;  //命令错误
    public static  final int NO_TOOL=7;   //没有安装工具
    public static  final  int RS232_ERROR=8;   //232连接错误
    public static  final  int  MATERIAL_OR_POSITION_ERROR=9; //钥匙太薄或者是位置不正确
    public static  final  int TOOL_ERROR=10; //工具错误
    public static  final  int SYSTEM_RESET=11;//系统重置
    public static  final  int SAFETYGATE_OPEN=12;//安全门打开
    public static  final  int UNSUPPORTED_PROFILE=13;//选择了不支持的钥匙类型
    public static  final  int WRONG_STOPPER=14;  //选择了错误的档块位置
    public static  final  int INCOMING_DATA_ERROR=15;//数据传输丢失
    public static   final int X_SENSOR_ERROR=16; //X轴传感器错误
    public static   final int Y_SENSOR_ERROR=17;//Y轴传感器错误
    public static   final int Z_SENSOR_ERROR=18; ///Z轴传感器错误
    public static   final int CLAMP_NOT_CALIBRATED=19;//请设定夹具的原点
    public static  final  int VERSION_NOT_MATCHED=20; //版本不匹配
    public static   final int EXCEPTION_ERROR=110;  //异常错误
    private String msg;
    private int  what;
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }
}
