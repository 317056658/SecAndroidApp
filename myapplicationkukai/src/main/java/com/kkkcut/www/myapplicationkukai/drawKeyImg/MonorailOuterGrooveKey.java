package com.kkkcut.www.myapplicationkukai.drawKeyImg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 画单轨外槽
 * Created by Administrator on 2017/7/14.
 */

public class MonorailOuterGrooveKey extends Key {
    private Path path;
    private Paint p1;
    private Paint p2;
    private Paint p3;
    private Paint p4;
    private Paint p5;
    private Paint p6;
    private Paint p7;
    private KeyInfo ki;
    private float keyBodyWidth = 460f;
    private double keyBodyHeight = 190;
    private int[] toothDistance, toothDepthPositionY;
    private String[] toothDepthNameGroup;
    private int maxToothDistance;
    private int surplusX, toothWidth = 14;
    private int keyWidthY, excessY;
    private int guide, nose;
    private int[] depthPositionArray;
    private Map<String, Integer> depthDataMap;
    private int depthStartY, depthEndY;
    private ArrayList<String[]> toothDepthNameList;
    private boolean isShowArrows = true;  //默认为true

    public MonorailOuterGrooveKey(Context context) {
        super(context);
    }

    public MonorailOuterGrooveKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MonorailOuterGrooveKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDrawPatternSize(int width, int height) {

    }

    @Override
    public void setShowDrawDepthAndDepthName(boolean isDraw) {

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("本View的宽度", "onMeasure: " + MeasureSpec.getSize(widthMeasureSpec));
        Log.d("本View的高度", "onMeasure: " + MeasureSpec.getSize(heightMeasureSpec));
    }

    /**
     * 设置钥匙属性
     *
     * @param p
     */
    public void setNeededDrawAttribute(KeyInfo p) {
        //new 一个保存每组齿的深度名的集合
        toothDepthNameList = new ArrayList<>();
        //new 一个保存计算好深度数据的集合
        depthDataMap = new HashMap<>();
        this.ki = p;
        //初始化画笔属性和路径
        initPaintAndPathAttribute();
        //分割齿位
        String[] spaceGroup = p.getSpace().split(";");
        String[] spaces = spaceGroup[0].split(",");
        toothDistance = new int[spaces.length];
        toothDepthNameGroup = new String[spaces.length];
        toothDepthPositionY = new int[spaces.length];
        for (int i = 0; i < spaces.length; i++) {
            toothDistance[i] = Integer.parseInt(spaces[i]);
            toothDepthNameGroup[i] = "X";
        }
        if (p.getAlign() == 0) {
            maxToothDistance = toothDistance[toothDistance.length - 1];
        } else if (p.getAlign() == 1) {
            maxToothDistance = toothDistance[0];
        }
        //计算齿距的比例值
        float spacesScaleValue = keyBodyWidth / maxToothDistance;
        //计算深度的比例值
        double depthScaleRatio = keyBodyHeight / p.getWidth();
        //指南
        guide = (int) (p.getGuide() * depthScaleRatio);
        //鼻部
        nose = (int) (p.getNose() * spacesScaleValue);
        excessY = 50;
        //计算钥匙的宽度
        keyWidthY = (int) (p.getWidth() * depthScaleRatio) + excessY;
        String[] depthGroup = p.getDepth().split(";");
        String[] depths = depthGroup[0].split(",");
        String[] depthNameGroup = p.getDepth_name().split(";");
        String[] depthNameArray = depthNameGroup[0].split(",");
        if (p.getAlign() == 0) {
            //换算肩部定位的齿位
            for (int i = 0; i < toothDistance.length; i++) {
                toothDistance[i] = (int) (toothDistance[i] * spacesScaleValue) + 162;

            }
        } else if (p.getAlign() == 1) {
            //换算尖端定位的齿位
            int keySumX = (int) (keyBodyWidth + 162);
            surplusX = keySumX - (keySumX - (int) (spacesScaleValue * toothDistance[toothDistance.length - 1]));
            for (int i = 0; i < toothDistance.length; i++) {
                toothDistance[i] = (keySumX - (int) (toothDistance[i] * spacesScaleValue)) + surplusX;
            }
        }
        if (p.getSide() == 0) {
            //换算深度
            depthPositionArray = new int[depths.length];
            for (int i = 0; i < depthPositionArray.length; i++) {
                depthPositionArray[i] = (int) (Integer.parseInt(depths[i]) * depthScaleRatio) + excessY;
                //保存计算的深度
                depthDataMap.put(depthNameArray[i], depthPositionArray[i]);
            }

        } else if (p.getSide() == 1) {
            //换算深度
            depthPositionArray = new int[depths.length];
            for (int i = 0; i < depthPositionArray.length; i++) {
                depthPositionArray[i] = keyWidthY - (int) (Integer.parseInt(depths[i]) * depthScaleRatio);
                //保存计算的深度
                depthDataMap.put(depthNameArray[i], depthPositionArray[i]);
            }

        }
        //获取第一个深度的位子
        depthStartY = depthPositionArray[0];
        //获取最后一个深度的位子
        depthEndY = depthPositionArray[depthPositionArray.length - 1];
    }


    @Override
    public void setToothDepthName(String depthName) {
        if(!TextUtils.isEmpty(depthName)) {
            String[] allToothDepthName = depthName.split(",");
            for (int i = 0; i < allToothDepthName.length; i++) {
                toothDepthNameGroup[i] = allToothDepthName[i];
            }
        }

    }

    @Override
    public void redrawKey() {
        this.invalidate();  //重绘
    }

    @Override
    public void setKeyToothAmount(int toothAmount) {

    }

    @Override
    public String getSpace() {
        return null;
    }

    @Override
    public int getKeyToothAmount() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getAllToothDepthName() {
        toothDepthNameList.clear();
        toothDepthNameList.add(toothDepthNameGroup);
        return toothDepthNameList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
        this.isShowArrows = isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        for (int i = 0; i < toothDepthNameGroup.length; i++) {
            toothDepthNameGroup[i] = "X";
        }
    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";   //bi
        String toothQuantity ="";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothMark = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int cutDepth=100;  //默认值100
        if(ki.getCutDepth()!=0){
            cutDepth= ki.getCutDepth();
        }

        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothMark += "0,";  //齿号
            }
        }
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        for (int w = 0; w < spaceWidthGroup.length; w++) {
            String[] spacesWidth = spaceWidthGroup[w].split(",");
            for (int i = 0; i < spacesWidth.length; i++) {
                toothWidth += (spacesWidth[i] + ",");
            }
        }
        String[] depthGroup = ki.getDepth().split(";");
        toothDepthQuantity = depthGroup[0].split(",").length;//齿深数量(第一组)
        String[] depths = depthGroup[0].split(",");//齿深数据(第一组)
        for (int i = 0; i < depths.length; i++) {
            toothDepthData += (depths[i] + ",");
        }

        String[] depthNameGroup = ki.getDepth_name().split(";");
        String[]  depthNames=depthNameGroup[0].split(",");
        for (int i = 0; i < depthNames.length; i++) {//齿深名称(第一组)
            //获得齿的深度名
            toothDepthName += (depthNames[i] + ",");
        }
        lastToothOrExtraCut =0;//默认值
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide() + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getGuide() + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothMark   //齿号代码  其实都是零
                + ki.getNose() + ","  // 鼻部长度
                + ki.getGroove() + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm = "!SB";   //bi
        String toothQuantity ="";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int knifeType=1;  //到类型
        int noseCut=0;
        String DDepth="";

      //鼻部参数不等于0  就为1 表示鼻部切割
        if(ki.getNose()!=0){
            noseCut=1;
        }
        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }

        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        if(ki.getSide()==4){//当参数为4的时候才会有下面参数
            toothQuantity=toothQuantity+spaceGroup[1].split(",").length+",";
        }
        toothCode = ki.getKeyToothCode();
        for (int i = 0; i < spaceGroup.length; i++) {
            spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
            }
        }
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        for (int w = 0; w < spaceWidthGroup.length; w++) {
            String[] spacesWidth = spaceWidthGroup[w].split(",");
            for (int i = 0; i < spacesWidth.length; i++) {
                toothWidth += (spacesWidth[i] + ",");
            }
        }
        String[] depthGroup = ki.getDepth().split(";");
        toothDepthQuantity = depthGroup[0].split(",").length;//齿深数量(第一组)
        String[] depths = depthGroup[0].split(",");//齿深数据(第一组)
        for (int i = 0; i < depths.length; i++) {
            toothDepthData += (depths[i] + ",");
        }
        String[] depthNameGroup = ki.getDepth_name().split(";");
        String[]  depthNames=depthNameGroup[0].split(",");
        for (int i = 0; i < depthNames.length; i++) {//齿深名称(第一组)
            //获得齿的深度名
            toothDepthName += (depthNames[i] + ",");
        }
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide()+ ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getInnerCutLength() + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码
                + ki.getNose() + ","  // 鼻部长度
                + 0 + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                +"!AC"+assistClamp+";"  // 辅助夹具
                +"!ST"+knifeType+"," //刀类型
                +cutterDiameter+";"    //刀的规格（刀的直径）
                +"!CK"
                +speed+"," //速度
                + DDepth +","//D深度
                +ZDepth+","  //Z深度
                +noseCut+","  //鼻部切割
                +detectionMode+";";  //切割钥匙的检测方式
        return order;

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画钥匙形状
        path.moveTo(10, 50);
        path.lineTo(720, 50);
        path.quadTo(732, 50, 730, 60);
        path.lineTo(730, keyWidthY - 10);
        path.quadTo(732, keyWidthY, 720, keyWidthY);
        path.lineTo(10, keyWidthY);
        canvas.drawPath(path, p1);
        canvas.drawPath(path, p2);
        path.reset();
        if (isShowArrows) {
            if (ki.getAlign() == 1) {   //尖端定位
                //画尖端定位红色箭头
                path.moveTo(666, 30);//80
                path.lineTo(666 + 10, 20);//70
                path.lineTo(666 + 10, 27);//77
                path.lineTo(666 + 35, 27);//77
                path.lineTo(666 + 35, 33);//83
                path.lineTo(666 + 10, 33);//83
                path.lineTo(666 + 10, 40);//90
                path.lineTo(666, 30);//80
                canvas.drawPath(path, p5);
                path.reset();
            } else if (ki.getAlign() == 0) {  //肩部定位
                //画红色箭头
                path.moveTo(90, 20);  //100
                path.lineTo(120, 20);//第一条  100
                path.lineTo(120, 12);
                path.lineTo(130, 24);//中间点  104
                path.lineTo(120, 36);
                path.lineTo(120, 28);
                path.lineTo(90, 28);
                canvas.drawPath(path, p5);
                path.reset();
            }
        }
        if (ki.getSide() == 0) {
            //绘制槽
            if (guide == 0) {
                path.moveTo(730, keyWidthY - 10);
                path.quadTo(732, keyWidthY, 720, keyWidthY);
                path.lineTo(140, keyWidthY);
                path.lineTo(140, keyWidthY - 20);
                //判断齿的深度位子
                for (int i = 0; i < toothDistance.length; i++) {
                    if (toothDepthNameGroup[i].equals("X")) {
                        toothDepthPositionY[i] = depthPositionArray[0] + 10;
                    } else if (toothDepthNameGroup[i].contains(".")) {   //有小数的
                        //分割齿的深度名
                        String[] str = toothDepthNameGroup[i].split("\\.");
                        if (depthDataMap.get(str[0]) == null) {
                            toothDepthPositionY[i] = depthPositionArray[0] + 10;
                        } else {
                            //获得深度的位子  y轴方向
                            int depthPosition = depthDataMap.get(str[0]);
                            float decimals = Float.parseFloat("0." + str[1]);
                            int sumY = 0;
                            if (depthPosition == depthPositionArray[depthPositionArray.length - 1]) {
                                //等于最后一个深度位置不计算。
                            } else {
                                for (int j = 0; j < depthPositionArray.length; j++) {
                                    if (depthPosition == depthPositionArray[j]) {
                                        int distance = depthPositionArray[j + 1] - depthPosition;
                                        sumY = (int) (distance * decimals);
                                        break;
                                    }
                                }
                            }
                            toothDepthPositionY[i] = depthPosition + sumY;
                        }
                    } else {   //判断除去X的整数
                        int toothDepth = depthDataMap.get(toothDepthNameGroup[i]) == null ? depthPositionArray[0] + 10 : depthDataMap.get(toothDepthNameGroup[i]);
                        toothDepthPositionY[i] = toothDepth;
                    }
                }
            } else {
                path.moveTo(730, keyWidthY - 10);
                path.quadTo(732, keyWidthY, 720, keyWidthY);
                if (nose != 0) {
                    path.lineTo(720 - nose, keyWidthY);
                    int x = 720 - nose;
                    path.lineTo(x - 30, keyWidthY - guide);
                    path.lineTo(140, keyWidthY - guide);
                } else {
                    path.lineTo(690, keyWidthY - guide);
                    path.lineTo(140, keyWidthY - guide);
                }
                int arcY = keyWidthY - guide;
                path.quadTo(120, arcY - 20, 140, arcY - 40);
                //判断齿的深度位子
                for (int i = 0; i < toothDistance.length; i++) {
                    if (toothDepthNameGroup[i].equals("X")) {
                        toothDepthPositionY[i] = depthPositionArray[0] - 10;
                    } else if (toothDepthNameGroup[i].contains(".")) {   //有小数的
                        //分割齿的深度名
                        String[] str = toothDepthNameGroup[i].split("\\.");
                        if (depthDataMap.get(str[0]) == null) {
                            toothDepthPositionY[i] = depthPositionArray[0] - 10;
                        } else {
                            //获得深度的位子  y轴方向
                            int depthPosition = depthDataMap.get(str[0]);
                            float decimals = Float.parseFloat("0." + str[1]);
                            int sumY = 0;
                            if (depthPosition == depthPositionArray[depthPositionArray.length - 1]) {
                                //等于最后一个深度位置不计算。
                            } else {
                                for (int j = 0; j < depthPositionArray.length; j++) {
                                    if (depthPosition == depthPositionArray[j]) {
                                        int distance = depthPositionArray[j + 1] - depthPosition;
                                        sumY = (int) (distance * decimals);
                                        break;
                                    }
                                }
                            }
                            toothDepthPositionY[i] = depthPosition + sumY;
                        }
                    } else {   //判断除去X的整数
                        int toothDepth = depthDataMap.get(toothDepthNameGroup[i]) == null ? depthPositionArray[0] - 10 : depthDataMap.get(toothDepthNameGroup[i]);
                        toothDepthPositionY[i] = toothDepth;
                    }
                }
            }
            path.lineTo(toothDistance[0] - toothWidth, toothDepthPositionY[0]);
            //画每个齿的样式
            for (int i = 0; i < toothDistance.length; i++) {
                if (toothDepthNameGroup[i].equals("X")) {
                    if (i == 0) {
                        path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                    } else {
                        path.lineTo(toothDistance[i] - toothWidth, toothDepthPositionY[i]);
                        path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                    }
                } else if (toothDepthNameGroup[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup[i].split("\\.");
                    if (depthDataMap.get(str[0]) == null) {
                        if (i == 0) {
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        } else {
                            path.lineTo(toothDistance[i] - toothWidth, toothDepthPositionY[i]);
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        }
                    } else {
                        if (i == 0) {
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        } else {
                            path.lineTo(toothDistance[i] - toothWidth, toothDepthPositionY[i]);
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        }

                    }
                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap.get(toothDepthNameGroup[i]) == null ? toothDepthPositionY[i] : depthDataMap.get(toothDepthNameGroup[i]);
                    if (i == 0) {
                        path.lineTo(toothDistance[i] + toothWidth, toothDepth);
                    } else {
                        path.lineTo(toothDistance[i] - toothWidth, toothDepth);
                        path.lineTo(toothDistance[i] + toothWidth, toothDepth);
                    }
                }
            }
            if (nose != 0) {
                path.lineTo(720 - nose, excessY);
                path.quadTo(736, 45, 730, excessY + 10);
            } else {
                path.lineTo(720, excessY);
                path.quadTo(732, 50, 730, 60);
            }
            canvas.drawPath(path, p1);
            canvas.drawPath(path, p7);
            path.reset();
            //画深度虚线
            for (int i = 0; i < depthPositionArray.length; i++) {
                path.moveTo(140, depthPositionArray[i]);
                path.lineTo(644, depthPositionArray[i]);
            }
            //画每个齿位虚线
            for (int i = 0; i < toothDistance.length; i++) {
                path.moveTo(toothDistance[i], depthStartY);
                path.lineTo(toothDistance[i], depthEndY);
            }
            canvas.drawPath(path, p3);
            path.reset();
            //画每个齿的深度名
            for (int i = 0; i < toothDepthNameGroup.length; i++) {
                if (toothDepthNameGroup[i].equals("X")) {
                    canvas.drawText("X", toothDistance[i], keyWidthY - 5, p4);  //画蓝色的字
                } else if (toothDepthNameGroup[i].contains(".")) {
                    if (toothDepthNameGroup[i].contains("X")) {
                        canvas.drawText("X", toothDistance[i], keyWidthY - 5, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup[i].split("\\.");
                        canvas.drawText(s[0], toothDistance[i], keyWidthY - 5, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup[i], toothDistance[i], keyWidthY - 5, p4);//画蓝色的字
                }
            }
        } else if (ki.getSide() == 1) {
            //明天继续  23:00
            if (guide == 0) {
                path.moveTo(140, excessY);
                path.lineTo(140, excessY + 20);
                //判断齿的深度位子
                for (int i = 0; i < toothDistance.length; i++) {
                    if (toothDepthNameGroup[i].equals("X")) {
                        toothDepthPositionY[i] = depthPositionArray[0] - 10;
                    } else if (toothDepthNameGroup[i].contains(".")) {   //有小数的
                        //分割齿的深度名
                        String[] str = toothDepthNameGroup[i].split("\\.");
                        if (depthDataMap.get(str[0]) == null) {
                            toothDepthPositionY[i] = depthPositionArray[0] - 10;
                        } else {
                            //获得深度的位子  y轴方向
                            int depthPosition = depthDataMap.get(str[0]);
                            float decimals = Float.parseFloat("0." + str[1]);
                            int sumY = 0;
                            if (depthPosition == depthPositionArray[depthPositionArray.length - 1]) {
                                //等于最后一个深度位置不计算。
                            } else {
                                for (int j = 0; j < depthPositionArray.length; j++) {
                                    if (depthPosition == depthPositionArray[j]) {
                                        int distance = depthPositionArray[j + 1] - depthPosition;
                                        sumY = (int) (distance * decimals);
                                        break;
                                    }
                                }
                            }
                            toothDepthPositionY[i] = depthPosition + sumY;
                        }
                    } else {   //判断除去X的整数
                        int toothDepth = depthDataMap.get(toothDepthNameGroup[i]) == null ? depthPositionArray[0] - 10 : depthDataMap.get(toothDepthNameGroup[i]);
                        toothDepthPositionY[i] = toothDepth;
                    }
                }
            } else {
                path.moveTo(730, excessY + 10);
                path.quadTo(732, excessY, 720, excessY);
                if (nose != 0) {
                    path.lineTo(720 - nose, excessY);
                    int x = 720 - nose;
                    path.lineTo(x - 30, excessY + guide);
                    path.lineTo(140, excessY + guide);
                } else {
                    path.lineTo(690, excessY + guide);
                    path.lineTo(140, excessY + guide);
                }
                //圆弧
                int arcY = excessY + guide;
                path.quadTo(120, arcY + 20, 140, arcY + 40);
                //判断齿的深度位子
                for (int i = 0; i < toothDistance.length; i++) {
                    if (toothDepthNameGroup[i].equals("X")) {
                        toothDepthPositionY[i] = depthPositionArray[0] + 10;
                    } else if (toothDepthNameGroup[i].contains(".")) {   //有小数的
                        //分割齿的深度名
                        String[] str = toothDepthNameGroup[i].split("\\.");
                        if (depthDataMap.get(str[0]) == null) {
                            toothDepthPositionY[i] = depthPositionArray[0] + 10;
                        } else {
                            //获得深度的位子  y轴方向
                            int depthPosition = depthDataMap.get(str[0]);
                            float decimals = Float.parseFloat("0." + str[1]);
                            int sumY = 0;
                            if (depthPosition == depthPositionArray[depthPositionArray.length - 1]) {
                                //等于最后一个深度位置不计算。
                            } else {
                                for (int j = 0; j < depthPositionArray.length; j++) {
                                    if (depthPosition == depthPositionArray[j]) {
                                        int distance = depthPositionArray[j + 1] - depthPosition;
                                        sumY = (int) (distance * decimals);
                                        break;
                                    }
                                }
                            }
                            toothDepthPositionY[i] = depthPosition + sumY;
                        }
                    } else {   //判断除去X的整数
                        int toothDepth = depthDataMap.get(toothDepthNameGroup[i]) == null ? depthPositionArray[0] + 10 : depthDataMap.get(toothDepthNameGroup[i]);
                        toothDepthPositionY[i] = toothDepth;
                    }
                }
            }
            path.lineTo(toothDistance[0] - toothWidth, toothDepthPositionY[0]);
            //画每个齿的样式
            for (int i = 0; i < toothDistance.length; i++) {
                if (toothDepthNameGroup[i].equals("X")) {
                    if (i == 0) {
                        path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                    } else {
                        path.lineTo(toothDistance[i] - toothWidth, toothDepthPositionY[i]);
                        path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                    }
                } else if (toothDepthNameGroup[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup[i].split("\\.");
                    if (depthDataMap.get(str[0]) == null) {
                        if (i == 0) {
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        } else {
                            path.lineTo(toothDistance[i] - toothWidth, toothDepthPositionY[i]);
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        }
                    } else {
                        if (i == 0) {
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        } else {
                            path.lineTo(toothDistance[i] - toothWidth, toothDepthPositionY[i]);
                            path.lineTo(toothDistance[i] + toothWidth, toothDepthPositionY[i]);
                        }

                    }
                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap.get(toothDepthNameGroup[i]) == null ? toothDepthPositionY[i] : depthDataMap.get(toothDepthNameGroup[i]);
                    if (i == 0) {
                        path.lineTo(toothDistance[i] + toothWidth, toothDepth);
                    } else {
                        path.lineTo(toothDistance[i] - toothWidth, toothDepth);
                        path.lineTo(toothDistance[i] + toothWidth, toothDepth);
                    }
                }
            }
            if (nose != 0) {
                path.lineTo(720 - nose, keyWidthY);
                path.lineTo(720, keyWidthY);
                path.quadTo(732, keyWidthY, 730, keyWidthY - 10);
                path.lineTo(730, excessY + 10);
                path.quadTo(732, 50, 720, excessY);
            } else {
                path.lineTo(720, keyWidthY);
                path.quadTo(732, keyWidthY, 730, keyWidthY - 10);
                path.lineTo(730, excessY + 10);
                path.quadTo(732, 50, 720, excessY);
            }
            canvas.drawPath(path, p1);
            canvas.drawPath(path, p7);
            path.reset();
            //画深度虚线
            for (int i = 0; i < depthPositionArray.length; i++) {
                path.moveTo(140, depthPositionArray[i]);
                path.lineTo(644, depthPositionArray[i]);
            }
            //画每个齿位虚线
            for (int i = 0; i < toothDistance.length; i++) {
                path.moveTo(toothDistance[i], depthStartY);
                path.lineTo(toothDistance[i], depthEndY);
            }
            canvas.drawPath(path, p3);
            path.reset();
            //画每个齿的深度名
            for (int i = 0; i < toothDepthNameGroup.length; i++) {
                if (toothDepthNameGroup[i].equals("X")) {
                    canvas.drawText("X", toothDistance[i], excessY + 40, p4);  //画蓝色的字
                } else if (toothDepthNameGroup[i].contains(".")) {
                    if (toothDepthNameGroup[i].contains("X")) {
                        canvas.drawText("X", toothDistance[i], excessY + 40, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup[i].split("\\.");
                        canvas.drawText(s[0], toothDistance[i], excessY + 40, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup[i], toothDistance[i], excessY + 40, p4);//画蓝色的字
                }
            }
        }
    }

    private void initPaintAndPathAttribute() {
        path = new Path();
        p1 = new Paint();
        p2 = new Paint();
        p3 = new Paint();
        p4 = new Paint();
        p5 = new Paint();
        p6 = new Paint();
        p7 = new Paint();
        //画钥匙形状属性
        p1.setAntiAlias(true);//去掉抗锯齿
        p1.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        p1.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        p1.setStrokeWidth(2);
        //填充钥匙身体颜色的笔
        p2.setColor(Color.parseColor("#BABABA"));
        p2.setAntiAlias(true);
        p2.setStyle(Paint.Style.FILL);
        p2.setStrokeWidth(1);
        //画虚线的笔的属性
        p3.setAntiAlias(true);//去掉抗锯齿
        p3.setColor(Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        p3.setStyle(Paint.Style.STROKE);//设置画笔描边
        p3.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        p3.setPathEffect(effects);
        //画蓝色字体的笔
        p4.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        p4.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        p4.setTextSize(40);
        //画红色提示箭头的属性
        p5.setColor(Color.RED);
        p5.setAntiAlias(true);//去掉锯齿
        p5.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        p5.setStrokeWidth(2); //设置画线的宽度
        //画红色字体的笔
        p6.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        p6.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        p6.setTextSize(40);
        //填充钥匙内槽颜色的笔
        p7.setAntiAlias(true);//去掉锯齿
        p7.setColor(Color.parseColor("#cc9900")); //黄色
        p7.setStyle(Paint.Style.FILL);
        p7.setStrokeWidth(1);
    }
}
