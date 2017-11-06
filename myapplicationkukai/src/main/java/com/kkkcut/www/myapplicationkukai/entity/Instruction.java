package com.kkkcut.www.myapplicationkukai.entity;

/**
 * 指令类
 * Created by Administrator on 2017/4/17.
 */

public class Instruction {//操作完成
    //表示操作完成
    public static  final String OPERATION_FINISH="!NP";
    //切割刀换探针
    public static  final String CUT_KNIFE_SWITCHOVER_PROBE ="!PE0,0,1;";

    //切割进行中
    public static final String CUT_CONDUCT="!PG";
    //跟踪点
    public static final String TRACE_POINT="!RP;";
    //读齿进行中
    public static final String READ_TOOTH_PROCEED="!RB";
    //暂停
    public static final String PAOSE="!PE;";
    //探针和切割刀之间的距离  发送 !TC0;!BP1;!DR1; 返回1：!PE0,0,1;  返回2：!NP
    public static  final String PROBE_AND_KNIFE_DISTANCE ="!TC0;!BP1;!DR1;";
    //切割刀转换为探针
    public static  final String  TRANSFORM_PROBE="%";
    //停止操作
    public static  final String  STOP_OPERATE="$";
    //夹具移动
    public static  final String  FIXTURE_MOVE="!SC;!BP1;";
    //主轴启动 延迟2秒，无返回值
    public static  final String  SPINDLE_START="!SR1,2000,0;!BP1";
    //主轴停止 延迟2秒，无返回值
    public static  final String  SPINDLE_END="!SR0,2000,0;!BP1";
    public static  final String  DECODE="!SB";
    //鸟叫短三声
    public static  final String TWEET_SHORT_THREE_SOUND="!BP1";

        //1到4号夹具指令
    public static String sendFixtureCalibration(String s){
        return "!CC0,"+s+";!BP1;!DR1";
    }
        //1到4号探针高度校准指令1，6，16，11
    public static String sendProbeHeightCalibration(String s){
        return "!TL0,"+s+";!BP1;!DR1";
    }
    //1到4号切割刀高度校准指令1，6，16，11
    public static String sendCutterKnifeHeightCalibration(String s){
        return "!TL1,"+s+";!BP1;!DR1;";
    }



    public  enum  ExceptionError{
        eNoError("!EX0;",0),           //没有错误
        eUserCancelled("!EX1;",1),     //用户操作取消
        eXLimitOver("!EX2;",2),        //X轴超限
        eYLimitOver("!EX3;",3),        //Y轴超限
        eZLimitOver("!EX4;",4),        //Z轴超限
        eProbeError("!EX5;",5),        //探测错误
        eCommandError("!EX6;",6),      //命令错误
        eNoTool("!EX7;",7),            //没有安装工具
        eRS232Error("!EX8;",8),        //232连接错误
        eMaterialPosError("!EX9;",9),  //钥匙太薄或者是位置不正确
        eToolError("!EX10;",10),         //工具错误（工具太长或者太短）
        eSystemReset("!EX11;",11),       //系统重置
        eCoverOpen("!EX12;",12),         //安全门打开
        eUnsupportedProfile("!EX13;",13),//选择了不支持的钥匙类型
        eWrongStopper("!EX14;",14),      //选择了错误的档块位置
        eIncomingDataError("!EX15;",15), //数据传输丢失
        eXSensorError("!EX16;",16),      //X轴传感器错误
        eYSensorError("!EX17;",17),      //Y轴传感器错误
        eZSensorError("!EX18;",18),      //Z轴传感器错误
        eClampNotCalibrated("!EX19;",19),//请设定夹具的原点
        eVersionNotMatched ("!EX20;",20); //版本不匹配
        // 成员变量
        private String name;
        private int index;

        private ExceptionError(String name, int index) {    //    必须是private的，否则编译错误
            this.name = name;
            this.index = index;
        }
       public String toString(){
           return this.name;
       }
       public int toInt(){
           return this.index;
       }

    }

}
