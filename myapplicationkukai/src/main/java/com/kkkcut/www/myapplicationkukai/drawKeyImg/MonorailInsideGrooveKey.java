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
 * 单轨内槽
 * Created by Administrator on 2017/7/7.
 */

public class MonorailInsideGrooveKey extends Key {
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
    private int[] toothDistance1, toothDistance2;
    private int maxToothDistance;
    private String[] toothDepthNameGroup1, toothDepthNameGroup2;
    private float spacesScaleValue;
    private String[] spaceGroup;
    private double depthScaleRatio;
    private int keyWidthY, excessY;
    private int toothWidth = 14, grooveY, guide;
    private int[] depthPositionArray1, depthPositionArray2;
    private Map<String, Integer> depthDataMap1, depthDataMap2;
    private int depthStartY1, depthEndY1, depthStartY2, depthEndY2;
    private int[] toothDepthPositionY1, toothDepthPositionY2;  //保存齿的y轴的位子
    private ArrayList<String[]> toothDepthNameList;
    private int surplusX;
    private String[]  allToothDepthName;
    private boolean isShowArrows=true;  //默认为true

    public MonorailInsideGrooveKey(Context context) {
        super(context);
    }

    public MonorailInsideGrooveKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MonorailInsideGrooveKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDrawPatternSize(int width, int height) {

    }

    @Override
    public void setShowDrawDepthAndDepthName(boolean isDraw) {

    }


    @Override
    public void setNeededDrawAttribute(KeyInfo ki) {
        this.ki = ki;
        //初始化画笔属性和路径
        initPaintAndPathAttribute();
        spaceGroup = ki.getSpace().split(";");
        depthDataMap1 = new HashMap<>();
        depthDataMap2 = new HashMap<>();
        excessY = 50;
        toothDepthNameList = new ArrayList<>();
        if (ki.getAlign() == 0) {
            if (spaceGroup.length == 1) {
                String[] space = spaceGroup[0].split(",");
                toothDistance1 = new int[space.length];
                toothDepthNameGroup1 = new String[space.length];
                toothDepthPositionY1 = new int[space.length];
                for (int i = 0; i < space.length; i++) {
                    toothDistance1[i] = Integer.parseInt(space[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                //获得最大齿距
                maxToothDistance = toothDistance1[toothDistance1.length - 1];
                //计算space比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                //换算齿位
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (int) (spacesScaleValue * toothDistance1[i]) + 162;
                }
            } else if (spaceGroup.length == 2) {
                String[] space1 = spaceGroup[0].split(",");
                String[] space2 = spaceGroup[1].split(",");
                toothDistance1 = new int[space1.length];
                toothDistance2 = new int[space2.length];
                toothDepthNameGroup1 = new String[space1.length];
                toothDepthNameGroup2 = new String[space2.length];
                toothDepthPositionY1 = new int[space1.length];
                toothDepthPositionY2 = new int[space2.length];
                for (int i = 0; i < space1.length; i++) {
                    toothDistance1[i] = Integer.parseInt(space1[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                for (int i = 0; i < space2.length; i++) {
                    toothDistance2[i] = Integer.parseInt(space2[i]);
                    toothDepthNameGroup2[i] = "X";
                }
                //比出最大齿距
                if (toothDistance1[toothDistance1.length - 1] > toothDistance2[toothDistance2.length - 1]) {
                    maxToothDistance = toothDistance1[toothDistance1.length];
                } else {
                    maxToothDistance = toothDistance2[toothDistance2.length - 1];
                }
                //计算space比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                //换算第一组齿位
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (int) (spacesScaleValue * toothDistance1[i]) + 162;
                }
                //换算第二组齿位
                for (int i = 0; i < toothDistance2.length; i++) {
                    toothDistance2[i] = (int) (spacesScaleValue * toothDistance2[i]) + 162;
                }
            }
        } else if (ki.getAlign() == 1) {  //尖端定位
            if (spaceGroup.length == 1) {
                String[] space = spaceGroup[0].split(",");
                toothDistance1 = new int[space.length];
                toothDepthNameGroup1 = new String[space.length];
                toothDepthPositionY1 = new int[space.length];
                for (int i = 0; i < space.length; i++) {
                    toothDistance1[i] = Integer.parseInt(space[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                //获得最大齿距
                maxToothDistance = toothDistance1[0];
                //计算space比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                int keySumX = (int) (keyBodyWidth + 162);
                surplusX = keySumX - (keySumX - (int) (spacesScaleValue * toothDistance1[toothDistance1.length - 1]));
                //换算齿位
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (keySumX - (int) (toothDistance1[i] * spacesScaleValue)) + surplusX;
                    Log.d("换齿位是好多？", "DrawMonorailInsideGrooveKeyImg: " + toothDistance1[i]);
                }
            } else if (spaceGroup.length == 2) {
                String[] space1 = spaceGroup[0].split(",");
                String[] space2 = spaceGroup[1].split(",");
                toothDistance1 = new int[space1.length];
                toothDistance2 = new int[space2.length];
                toothDepthNameGroup1 = new String[space1.length];
                toothDepthNameGroup2 = new String[space2.length];
                //记录当前每个齿深度位置数组
                toothDepthPositionY1 = new int[space1.length];
                toothDepthPositionY2 = new int[space2.length];
                for (int i = 0; i < space1.length; i++) {
                    toothDistance1[i] = Integer.parseInt(space1[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                for (int i = 0; i < space2.length; i++) {
                    toothDistance2[i] = Integer.parseInt(space2[i]);
                    toothDepthNameGroup2[i] = "X";
                }
                //比出最大齿距
                if (toothDistance1[0] > toothDistance2[0]) {
                    maxToothDistance = toothDistance1[0];
                } else {
                    maxToothDistance = toothDistance2[0];
                }
                //计算space比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                int lastToothDistance;
                //比出最后一个齿距谁最大
                if (toothDistance1[toothDistance1.length - 1] < toothDistance2[toothDistance2.length - 1]) {
                    lastToothDistance = toothDistance1[toothDistance1.length - 1];
                } else {
                    lastToothDistance = toothDistance2[toothDistance2.length - 1];
                }
                int keySumX = (int) (keyBodyWidth + 162);
                surplusX = keySumX - (keySumX - (int) (spacesScaleValue * lastToothDistance));
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (keySumX - (int) (toothDistance1[i] * spacesScaleValue)) + surplusX;
                    Log.d("第一组齿位是好多321312？", "DrawMonorailInsideGrooveKeyImg: "+ toothDistance1[i]);
                }
                //换算第二组齿位
                for (int i = 0; i < toothDistance2.length; i++) {
                    toothDistance2[i] = (keySumX - (int) (toothDistance2[i] * spacesScaleValue)) + surplusX;
                    Log.d("第一组齿位是好多321312？", "DrawMonorailInsideGrooveKeyImg: "+ toothDistance2[i]);
                }
            }
        }
        //计算深度的比例值
        depthScaleRatio = keyBodyHeight / ki.getWidth();
        //计算钥匙的宽度
        keyWidthY = (int) (ki.getWidth() * depthScaleRatio) + excessY;

        guide = (int) (ki.getGuide() * depthScaleRatio);
        //分割深度 “;”
        String[] depthGroup = ki.getDepth().split(";");
        String[] depths = depthGroup[0].split(",");

        //分割深度名
        String[] depthNameGroup = ki.getDepth_name().split(";");
        String[] depthNameArray = depthNameGroup[0].split(",");

        if (ki.getSide() == 0) {
            depthPositionArray1 = new int[depths.length];
            for (int i = 0; i < depths.length; i++) {
                depthPositionArray1[i] = (int) (Integer.parseInt(depths[i]) * depthScaleRatio) + excessY;
                Log.d("计算深度是好多？", "DrawMonorailInsideGrooveKeyImg: " + depthPositionArray1[i]);
                //根据深度名保存深度  909
                depthDataMap1.put(depthNameArray[i], depthPositionArray1[i]);
            }
        } else if (ki.getSide() == 6) {
            //计算钥匙齿深度的位子
            depthPositionArray1 = new int[depths.length];
            for (int i = 0; i < depths.length; i++) {
                depthPositionArray1[i] = (int) (Integer.parseInt(depths[i]) * depthScaleRatio) + excessY;
                Log.d("计算深度是好多1？", "DrawMonorailInsideGrooveKeyImg: " + depthPositionArray1[i]);
                //根据深度名保存深度
                depthDataMap1.put(depthNameArray[i], depthPositionArray1[i]);
            }
            depthPositionArray2 = new int[depths.length];

            for (int i = 0; i < depths.length; i++) {
                depthPositionArray2[i] = keyWidthY - (int) (Integer.parseInt(depths[i]) * depthScaleRatio);
                Log.d("计算深度是好多2？", "DrawMonorailInsideGrooveKeyImg: " + depthPositionArray2[i]);
                //根据深度名保存深度
                depthDataMap2.put(depthNameArray[i], depthPositionArray2[i]);
            }

            depthStartY1 = depthPositionArray1[0];
            depthEndY1 = depthPositionArray1[depthPositionArray1.length - 1];
            depthStartY2 = depthPositionArray2[0];
            depthEndY2 = depthPositionArray2[depthPositionArray2.length - 1];
        } else if (ki.getSide() == 1) {
            String[] depths1 = depthGroup[0].split(",");
            depthPositionArray1 = new int[depths1.length];
            for (int i = 0; i < depths1.length; i++) {
                depthPositionArray1[i] = keyWidthY - (int) (Integer.parseInt(depths1[i]) * depthScaleRatio);
                Log.d("计算深度是好多？", "DrawMonorailInsideGrooveKeyImg: " + depthPositionArray1[i]);
                //根据深度名保存深度
                depthDataMap1.put(depthNameArray[i], depthPositionArray1[i]);
            }
        } else if (ki.getSide() == 3) {
            if (depthGroup.length == 1) {
                allToothDepthName=  new String[(toothDistance1.length+toothDistance2.length)];
                //计算钥匙齿深度的位子
                depthPositionArray1 = new int[depths.length];
                for (int i = 0; i < depths.length; i++) {
                    depthPositionArray1[i] = (int) (Integer.parseInt(depths[i]) * depthScaleRatio) + excessY;
                    Log.d("计算深度是好多1？", "DrawMonorailInsideGrooveKeyImg: " + depthPositionArray1[i]);
                    //根据深度名保存深度
                    depthDataMap1.put(depthNameArray[i], depthPositionArray1[i]);
                }
                //保存第一组每个齿开始的y的位子
                for (int i = 0; i < toothDepthPositionY1.length; i++) {
                    toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                }
                depthPositionArray2 = new int[depths.length];
                for (int i = 0; i < depths.length; i++) {
                    depthPositionArray2[i] = keyWidthY - (int) (Integer.parseInt(depths[i]) * depthScaleRatio);
                    //根据深度名保存深度
                    depthDataMap2.put(depthNameArray[i], depthPositionArray2[i]);
                }
                //保存第二组每个齿开始的y的位子
                for (int i = 0; i < toothDepthPositionY1.length; i++) {
                    toothDepthPositionY2[i] = depthPositionArray2[0] + 10;
                }

                depthStartY1 = depthPositionArray1[0];
                depthEndY1 = depthPositionArray1[depthPositionArray1.length - 1];
                depthStartY2 = depthPositionArray2[0];
                depthEndY2 = depthPositionArray2[depthPositionArray2.length - 1];
            }
        }


        if (spaceGroup.length == 1) {
            depthStartY1 = depthPositionArray1[0];
            depthEndY1 = depthPositionArray1[depthPositionArray1.length - 1];
        }
        if (ki.getGroove() != 0) {
            grooveY = (int) (ki.getGroove() * depthScaleRatio);
        } else {
            grooveY = 70;
        }
    }

    @Override
    public void setToothDepthName(String depthName) {
        if(!TextUtils.isEmpty(depthName)) {
            String[] allToothDepthName = depthName.split(",");
            if (spaceGroup.length == 1) {
                for (int i = 0; i < allToothDepthName.length; i++) {
                    toothDepthNameGroup1[i] = allToothDepthName[i];
                }
            } else if (spaceGroup.length == 2) {

                if(ki.getSide()==3){
                    int j=-1;
                    int k=-1;
                    for (int i = 0; i < allToothDepthName.length; i++) {
                        if(i%2==0){
                            j++;
                            if(j<toothDepthNameGroup2.length)
                                toothDepthNameGroup2[j]= allToothDepthName[i];
                        }else {
                            k++;
                            if(k<toothDepthNameGroup2.length)
                                toothDepthNameGroup1[j]= allToothDepthName[i];
                        }
                    }
                }else {
                    int j = 0;
                    for (int i = 0; i < allToothDepthName.length; i++) {
                        if (i < toothDepthNameGroup1.length) {
                            toothDepthNameGroup1[i] = allToothDepthName[i];
                        } else {
                            toothDepthNameGroup2[j] = allToothDepthName[i];
                            Log.d("深度的名字是:", "setDepth: " + toothDepthNameGroup2[j]);
                            j++;
                        }
                    }
                }
            }
        }

    }

    @Override
    public void redrawKey() {
        this.invalidate();
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
        if (ki.getAlign() == 0) {
            if (spaceGroup.length == 1) {
                toothDepthNameList.add(toothDepthNameGroup1);
                return toothDepthNameList;
            } else if (spaceGroup.length > 1) {
                toothDepthNameList.add(toothDepthNameGroup1);
                toothDepthNameList.add(toothDepthNameGroup2);
                return toothDepthNameList;
            }
        } else if (ki.getAlign() == 1) {
            if (spaceGroup.length == 1) {
                toothDepthNameList.add(toothDepthNameGroup1);
                return toothDepthNameList;
            } else if (spaceGroup.length == 2) {
                int j=-1;
                int k=-1;
                if(ki.getSide()==3){
                    for (int i = 0; i < allToothDepthName.length; i++) {
                            if(i%2==0){
                                j++;
                                if(j<toothDepthNameGroup2.length)
                                allToothDepthName[i]=toothDepthNameGroup2[j];
                            }else {
                                k++;
                                if(k<toothDepthNameGroup2.length)
                                allToothDepthName[i]= toothDepthNameGroup1[k];
                            }
                    }
                    toothDepthNameList.add(allToothDepthName);
                    return toothDepthNameList;
                }

            }
        }
        return null;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
            this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {

    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";   //bi
        String toothQuantity ="";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";  //齿号代码
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int  cutDepth=110;
        int groove=320;
        if(ki.getCutDepth()!=0){
            cutDepth= ki.getCutDepth();
        }
        if(ki.getGroove()!=0){
            groove= ki.getGroove();
        }
        String[] spaceGroup = ki.getSpace().split(";");
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[]  spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothCode += "0,";  //齿号
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
        lastToothOrExtraCut =(ki.getAlign()==0? ki.getExtraCut(): ki.getLastBitting());//定位方式是0启用extra_cut参数否则启用last_bitting参数
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
                + toothCode   //齿号代码  其实都是零
                + ki.getNose() + ","  // 鼻部长度
                + groove + ","  //槽宽
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
        int groove=320;
        if(ki.getGroove()!=0){
            groove= ki.getGroove();
        }
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
        lastToothOrExtraCut =(ki.getAlign()==0? ki.getExtraCut(): ki.getLastBitting());//定位方式是0启用extra_cut参数否则启用last_bitting参数
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide()+ ","// 有效边
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
                + toothCode   //齿号代码
                + ki.getNose() + ","  // 鼻部长度
                + groove + ","  //槽宽
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        Log.d("宽度是好多？", "onMeasure: " + widthSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (ki.getAlign() == 0) {
            //画肩膀定位钥匙形状
            path.moveTo(10, 20);
            path.lineTo(120, 20);
            path.lineTo(120, 50);
            //分割线-------------------------------
            path.lineTo(710 + 10, 50);
            path.quadTo(722 + 10, 50, 720 + 10, 60);
            path.lineTo(720 + 10, keyWidthY - 10);
            path.quadTo(722 + 10, keyWidthY, 710 + 10, keyWidthY);
            path.lineTo(120, keyWidthY);
            path.lineTo(120, keyWidthY + 30);
            path.lineTo(10, keyWidthY + 30);
            canvas.drawPath(path, p1);
            canvas.drawPath(path, p2);
            path.reset();
            if (ki.getSide() == 6) {
                //画第一条内槽样式
                path.moveTo(730, 60);
                path.quadTo(722 + 10, 50, 720, 50);
                int lastToothPosition = toothDistance1[toothDistance1.length - 1] + toothWidth;
                for (int i = toothDistance1.length - 1; i >= 0; i--) {
                    if (toothDepthNameGroup1[i].equals("X")) {
                        if (i == toothDistance1.length - 1) {
                            path.lineTo(lastToothPosition, depthPositionArray1[0] - 6);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 6);
                            toothDepthPositionY1[i] = depthPositionArray1[0] - 6;
                        } else {
                            path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] - 6);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 6);
                            toothDepthPositionY1[i] = depthPositionArray1[0] - 6;
                        }
                    } else if (toothDepthNameGroup1[i].contains(".")) {   //有小数的
                        //分割齿的深度名
                        String[] str = toothDepthNameGroup1[i].split("\\.");
                        if (depthDataMap1.get(str[0]) == null) {
                            if (i == toothDistance1.length - 1) {
                                path.lineTo(lastToothPosition, depthPositionArray1[0] - 6);
                                path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 6);
                                toothDepthPositionY1[i] = depthPositionArray1[0] - 6;
                            } else {
                                path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] - 6);
                                path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 6);
                                toothDepthPositionY1[i] = depthPositionArray1[0] - 6;
                            }
                        } else {
                            //获得深度的位子  y轴方向
                            int depthPosition = depthDataMap1.get(str[0]);
                            float decimals = Float.parseFloat("0." + str[1]);
                            int sumY = 0;
                            if (depthPosition == depthPositionArray1[depthPositionArray1.length - 1]) {
                                //等于最后一个深度位置不计算。
                            } else {
                                for (int j = 0; j < depthPositionArray1.length; j++) {
                                    if (depthPosition == depthPositionArray1[j]) {
                                        int distance = depthPositionArray1[j + 1] - depthPosition;
                                        sumY = (int) (distance * decimals);
                                        break;
                                    }
                                }
                            }
                            if (i == toothDistance1.length - 1) {
                                path.lineTo(lastToothPosition, depthPosition + sumY);
                                path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                                toothDepthPositionY1[i] = depthPosition + sumY;
                            } else {
                                path.lineTo(toothDistance1[i] + toothWidth, depthPosition + sumY);
                                path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                                toothDepthPositionY1[i] = depthPosition + sumY;
                            }
                        }

                    } else {   //判断除去X的整数
                        int toothDepth = depthDataMap1.get(toothDepthNameGroup1[i]) == null ? depthPositionArray1[0] - 6 : depthDataMap1.get(toothDepthNameGroup1[i]);
                        if (i == toothDistance1.length - 1) {
                            path.lineTo(lastToothPosition, toothDepth);
                            path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                            toothDepthPositionY1[i] = toothDepth;
                        } else {
                            path.lineTo(toothDistance1[i] + toothWidth, toothDepth);
                            path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                            toothDepthPositionY1[i] = toothDepth;
                        }
                    }
                }
                //画第一组圆弧
                int grooveHalfHeight = grooveY / 2;
                int firstToothPosition = toothDistance1[0] - toothWidth;
                path.quadTo(firstToothPosition - 34, toothDepthPositionY1[0] + grooveHalfHeight, firstToothPosition, toothDepthPositionY1[0] + grooveY);
                for (int i = 0; i < toothDistance1.length; i++) {
                    if (i == 0) {
                        path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] + grooveY);
                    } else {
                        path.lineTo(toothDistance1[i] - toothWidth, toothDepthPositionY1[i] + grooveY);
                        path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] + grooveY);
                    }
                }
                path.lineTo(730, 100);
                canvas.drawPath(path, p1);
                canvas.drawPath(path, p7);
                path.reset();
                //画第二条内槽样式
                path.moveTo(730, keyWidthY - 10);
                path.quadTo(722 + 10, keyWidthY, 710 + 10, keyWidthY);

                lastToothPosition = toothDistance2[toothDistance2.length - 1] + toothWidth;
                for (int i = toothDistance2.length - 1; i >= 0; i--) {
                    if (toothDepthNameGroup2[i].equals("X")) {
                        if (i == toothDistance2.length - 1) {
                            path.lineTo(lastToothPosition, depthPositionArray2[0] + 6);
                            path.lineTo(toothDistance2[i] - toothWidth, depthPositionArray2[0] + 6);
                            toothDepthPositionY2[i] = depthPositionArray2[0] + 6;
                        } else {
                            path.lineTo(toothDistance2[i] + toothWidth, depthPositionArray2[0] + 6);
                            path.lineTo(toothDistance2[i] - toothWidth, depthPositionArray2[0] + 6);
                            toothDepthPositionY2[i] = depthPositionArray2[0] + 6;
                        }
                    } else if (toothDepthNameGroup2[i].contains(".")) {   //有小数的
                        //分割齿的深度名
                        String[] str = toothDepthNameGroup2[i].split("\\.");
                        if (depthDataMap2.get(str[0]) == null) {
                            if (i == toothDistance2.length - 1) {
                                path.lineTo(lastToothPosition, depthPositionArray2[0] + 6);
                                path.lineTo(toothDistance2[i] - toothWidth, depthPositionArray2[0] + 6);
                                toothDepthPositionY2[i] = depthPositionArray2[0] + 6;
                            } else {
                                path.lineTo(toothDistance2[i] + toothWidth, depthPositionArray2[0] + 6);
                                path.lineTo(toothDistance2[i] - toothWidth, depthPositionArray2[0] + 6);
                                toothDepthPositionY2[i] = depthPositionArray2[0] + 6;
                            }
                        } else {
                            //获得深度的位子  y轴方向
                            int depthPosition = depthDataMap2.get(str[0]);
                            float decimals = Float.parseFloat("0." + str[1]);
                            int sumY = 0;
                            if (depthPosition == depthPositionArray2[depthPositionArray2.length - 1]) {
                                //等于最后一个深度位置不计算。
                            } else {
                                for (int j = 0; j < depthPositionArray2.length; j++) {
                                    if (depthPosition == depthPositionArray2[j]) {
                                        int distance = depthPositionArray2[j + 1] - depthPosition;
                                        sumY = (int) (distance * decimals);
                                        break;
                                    }
                                }
                            }
                            if (i == toothDistance2.length - 1) {
                                path.lineTo(lastToothPosition, depthPosition + sumY);
                                path.lineTo(toothDistance2[i] - toothWidth, depthPosition + sumY);
                                toothDepthPositionY2[i] = depthPosition + sumY;
                            } else {
                                path.lineTo(toothDistance2[i] + toothWidth, depthPosition + sumY);
                                path.lineTo(toothDistance2[i] - toothWidth, depthPosition + sumY);
                                toothDepthPositionY2[i] = depthPosition + sumY;
                            }
                        }
                    } else {   //判断除去X的整数
                        int toothDepth = depthDataMap2.get(toothDepthNameGroup2[i]) == null ? depthPositionArray2[0] + 6 : depthDataMap2.get(toothDepthNameGroup2[i]);
                        if (i == toothDistance2.length - 1) {
                            path.lineTo(lastToothPosition, toothDepth);
                            path.lineTo(toothDistance2[i] - toothWidth, toothDepth);
                            toothDepthPositionY2[i] = toothDepth;
                        } else {
                            path.lineTo(toothDistance2[i] + toothWidth, toothDepth);
                            path.lineTo(toothDistance2[i] - toothWidth, toothDepth);
                            toothDepthPositionY2[i] = toothDepth;
                        }
                    }
                }
                //画第二组圆弧
                grooveHalfHeight = grooveY / 2;
                firstToothPosition = toothDistance2[0] - toothWidth;
                path.quadTo(firstToothPosition - 34, toothDepthPositionY2[0] - grooveHalfHeight, firstToothPosition, toothDepthPositionY2[0] - grooveY);
                for (int i = 0; i < toothDistance2.length; i++) {
                    if (i == 0) {
                        path.lineTo(toothDistance2[i] + toothWidth, toothDepthPositionY2[i] - grooveY);
                    } else {
                        path.lineTo(toothDistance2[i] - toothWidth, toothDepthPositionY2[i] - grooveY);
                        path.lineTo(toothDistance2[i] + toothWidth, toothDepthPositionY2[i] - grooveY);
                    }
                }
                path.lineTo(730, keyWidthY - 52);

                canvas.drawPath(path, p1);
                canvas.drawPath(path, p7);
                path.reset();
                //画第一组齿的深度名
                for (int i = 0; i < toothDistance1.length; i++) {
                    if (toothDepthNameGroup1[i].equals("X")) {
                        canvas.drawText("X", toothDistance1[i], 50 - 10, p4);  //画蓝色的字
                    } else if (toothDepthNameGroup1[i].contains(".")) {
                        if (toothDepthNameGroup1[i].contains("X")) {
                            canvas.drawText("X", toothDistance1[i], 50 - 10, p6);//画红色的字
                        } else {
                            String[] s = toothDepthNameGroup1[i].split("\\.");
                            Log.d("这是几", "bilateralKey: " + s[0]);
                            canvas.drawText(s[0], toothDistance1[i], 50 - 10, p6);//画红色的字
                        }
                    } else {
                        canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], 50 - 10, p4);//画蓝色的字
                    }
                }
                //画第二组齿的深度名
                for (int i = 0; i < toothDistance2.length; i++) {
                    if (toothDepthNameGroup2[i].equals("X")) {
                        canvas.drawText("X", toothDistance2[i], depthPositionArray2[0] + 50, p4);  //画蓝色的字
                    } else if (toothDepthNameGroup2[i].contains(".")) {
                        if (toothDepthNameGroup2[i].contains("X")) {
                            canvas.drawText("X", toothDistance2[i], depthPositionArray2[0] + 50, p6);//画红色的字
                        } else {
                            String[] s = toothDepthNameGroup2[i].split("\\.");
                            Log.d("这是几", "bilateralKey: " + s[0]);
                            canvas.drawText(s[0], toothDistance2[i], depthPositionArray2[0] + 50, p6);//画红色的字
                        }
                    } else {
                        canvas.drawText(toothDepthNameGroup2[i], toothDistance2[i], depthPositionArray2[0] + 50, p4);//画蓝色的字
                    }
                }
                //画第一组深度
                for (int i = 0; i < depthPositionArray1.length; i++) {
                    path.moveTo(140, depthPositionArray1[i]);
                    path.lineTo(644, depthPositionArray1[i]);
                }
                //画第二组深度
                for (int i = 0; i < depthPositionArray2.length; i++) {
                    path.moveTo(140, depthPositionArray2[i]);
                    path.lineTo(644, depthPositionArray2[i]);
                }
                canvas.drawPath(path, p3);
                path.reset();
                //画第一组齿位的虚线
                for (int i = 0; i < toothDistance1.length; i++) {
                    path.moveTo(toothDistance1[i], depthStartY1);
                    path.lineTo(toothDistance1[i], depthEndY1);
                }
                //画第二组齿位的虚线
                for (int i = 0; i < toothDistance2.length; i++) {
                    path.moveTo(toothDistance2[i], depthStartY2);
                    path.lineTo(toothDistance2[i], depthEndY2);
                }
                canvas.drawPath(path, p3);
                path.reset();
            }
        } else if (ki.getAlign() == 1) {
            //画尖端定位钥匙形状
            path.moveTo(10, 50);
            path.lineTo(720, 50);
            path.quadTo(732, 50, 730, 60);
            path.lineTo(730, keyWidthY - 10);
            path.quadTo(732, keyWidthY, 720, keyWidthY);
            path.lineTo(10, keyWidthY);
            canvas.drawPath(path, p1);
            canvas.drawPath(path, p2);
            path.reset();

        }
        //等于0 就是A侧面
        if (ki.getSide() == 0) {
            int lastToothPosition = toothDistance1[toothDistance1.length - 1] + toothWidth;
            //画内槽样式
            path.moveTo(730, 60);
            path.quadTo(722 + 10, 50, 720, 50);
            for (int i = toothDistance1.length - 1; i >= 0; i--) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    if (i == toothDistance1.length - 1) {
                        path.lineTo(lastToothPosition, depthPositionArray1[0] + 10);
                        path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] + 10);
                        toothDepthPositionY1[i] = depthPositionArray1[0] + 10;
                    } else {
                        path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] + 10);
                        path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] + 10);
                        toothDepthPositionY1[i] = depthPositionArray1[0] + 10;
                    }
                } else if (toothDepthNameGroup1[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup1[i].split("\\.");
                    if (depthDataMap1.get(str[0]) == null) {
                        if (i == toothDistance1.length - 1) {
                            path.lineTo(lastToothPosition, depthPositionArray1[0] + 10);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] + 10);
                            toothDepthPositionY1[i] = depthPositionArray1[0] + 10;
                        } else {
                            path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] + 10);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] + 10);
                            toothDepthPositionY1[i] = depthPositionArray1[0] + 10;
                        }
                    } else {
                        //获得深度的位子  y轴方向
                        int depthPosition = depthDataMap1.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        int sumY = 0;
                        if (depthPosition == depthPositionArray1[depthPositionArray1.length - 1]) {
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionArray1.length; j++) {
                                if (depthPosition == depthPositionArray1[j]) {
                                    int distance = depthPositionArray1[j + 1] - depthPosition;
                                    sumY = (int) (distance * decimals);
                                    break;
                                }
                            }
                        }
                        if (i == toothDistance1.length - 1) {
                            path.lineTo(lastToothPosition, depthPosition + sumY);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                            toothDepthPositionY1[i] = depthPosition + sumY;
                        } else {
                            path.lineTo(toothDistance1[i] + toothWidth, depthPosition + sumY);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                            toothDepthPositionY1[i] = depthPosition + sumY;
                        }
                    }

                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap1.get(toothDepthNameGroup1[i]) == null ? depthPositionArray1[0] + 10 : depthDataMap1.get(toothDepthNameGroup1[i]);
                    if (i == toothDistance1.length - 1) {
                        path.lineTo(lastToothPosition, toothDepth);
                        path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                        toothDepthPositionY1[i] = toothDepth;
                    } else {
                        path.lineTo(toothDistance1[i] + toothWidth, toothDepth);
                        path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                        toothDepthPositionY1[i] = toothDepth;
                    }
                }
            }
            int firstToothPosition = toothDistance1[0] - toothWidth;
            int grooveHalfHeight = grooveY / 2;
            //画圆弧
            if (ki.getExtraCut() == 1) {  //  钥匙柄切割一刀
                path.lineTo(140, toothDepthPositionY1[0]);
                path.lineTo(90, 64);
                path.quadTo(32, 30, 30, 100);
                path.lineTo(120, toothDepthPositionY1[0] + grooveY);
            } else {
                path.quadTo(firstToothPosition - 40, toothDepthPositionY1[0] + grooveHalfHeight, firstToothPosition, toothDepthPositionY1[0] + grooveY);
            }

            for (int i = 0; i < toothDistance1.length; i++) {
                if (i == 0) {
                    path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] + grooveY);
                } else {
                    path.lineTo(toothDistance1[i] - toothWidth, toothDepthPositionY1[i] + grooveY);
                    path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] + grooveY);
                }
            }
            if (guide == 0) {
                path.lineTo(720, keyWidthY);
                path.quadTo(722 + 10, keyWidthY, 730, keyWidthY - 10);
            } else if (guide != 0) {
                path.lineTo(730, keyWidthY - guide);
            }
            canvas.drawPath(path, p1);
            canvas.drawPath(path, p7);
            path.reset();
            //画每个齿的深度名
            for (int i = 0; i < toothDistance1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i], depthPositionArray1[0] + 50, p4);  //画蓝色的字
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    if (toothDepthNameGroup1[i].contains("X")) {
                        canvas.drawText("X", toothDistance1[i], depthPositionArray1[0] + 50, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup1[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance1[i], depthPositionArray1[0] + 50, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], depthPositionArray1[0] + 50, p4);//画蓝色的字
                }
            }
            //画深度
            for (int i = 0; i < depthPositionArray1.length; i++) {
                path.moveTo(140, depthPositionArray1[i]);
                path.lineTo(644, depthPositionArray1[i]);
            }
            canvas.drawPath(path, p3);
            path.reset();
            //画齿位的虚线
            for (int i = 0; i < toothDistance1.length; i++) {
                path.moveTo(toothDistance1[i], depthStartY1);
                path.lineTo(toothDistance1[i], depthEndY1);
            }
            canvas.drawPath(path, p3);
            path.reset();
            //等于1 就代表B面
        } else if (ki.getSide() == 1) {
            //画钥匙的内槽
            path.moveTo(730, keyWidthY - 10);
            path.quadTo(722 + 10, keyWidthY, 710 + 10, keyWidthY);
            int lastToothPosition = toothDistance1[toothDistance1.length - 1] + toothWidth;
            for (int i = toothDistance1.length - 1; i >= 0; i--) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    if (i == toothDistance1.length - 1) {
                        path.lineTo(lastToothPosition, depthPositionArray1[0] - 10);
                        path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 10);
                        toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                    } else {
                        path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] - 10);
                        path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 10);
                        toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                    }
                } else if (toothDepthNameGroup1[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup1[i].split("\\.");
                    if (depthDataMap1.get(str[0]) == null) {
                        if (i == toothDistance1.length - 1) {
                            path.lineTo(lastToothPosition, depthPositionArray1[0] - 10);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 10);
                            toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                        } else {
                            path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] - 10);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 10);
                            toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                        }
                    } else {
                        //获得深度的位子  y轴方向
                        int depthPosition = depthDataMap1.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        int sumY = 0;
                        if (depthPosition == depthPositionArray1[depthPositionArray1.length - 1]) {
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionArray1.length; j++) {
                                if (depthPosition == depthPositionArray1[j]) {
                                    int distance = depthPositionArray1[j + 1] - depthPosition;
                                    sumY = (int) (distance * decimals);
                                    break;
                                }
                            }
                        }
                        if (i == toothDistance1.length - 1) {
                            path.lineTo(lastToothPosition, depthPosition + sumY);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                            toothDepthPositionY1[i] = depthPosition + sumY;
                        } else {
                            path.lineTo(toothDistance1[i] + toothWidth, depthPosition + sumY);
                            path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                            toothDepthPositionY1[i] = depthPosition + sumY;
                        }
                    }
                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap1.get(toothDepthNameGroup1[i]) == null ? depthPositionArray1[0] - 10 : depthDataMap1.get(toothDepthNameGroup1[i]);
                    if (i == toothDistance1.length - 1) {
                        path.lineTo(lastToothPosition, toothDepth);
                        path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                        toothDepthPositionY1[i] = toothDepth;
                    } else {
                        path.lineTo(toothDistance1[i] + toothWidth, toothDepth);
                        path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                        toothDepthPositionY1[i] = toothDepth;
                    }
                }
            }
            //画圆弧
            int grooveHalfHeight = grooveY / 2;
            int firstToothPosition = toothDistance1[0] - toothWidth;
            //画圆弧
            if (ki.getExtraCut() == 1) {  //  钥匙柄切割一刀
                path.lineTo(140, toothDepthPositionY1[0]);
                path.lineTo(90, 64);
                path.quadTo(32, 30, 30, 100);
                path.lineTo(120, toothDepthPositionY1[0] + grooveY);
            } else {
                path.quadTo(firstToothPosition - 40, toothDepthPositionY1[0] - grooveHalfHeight, firstToothPosition, toothDepthPositionY1[0] - grooveY);
            }
            for (int i = 0; i < toothDistance1.length; i++) {
                if (i == 0) {
                    path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] - grooveY);
                } else {
                    path.lineTo(toothDistance1[i] - toothWidth, toothDepthPositionY1[i] - grooveY);
                    path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] - grooveY);
                }
            }
            if (guide != 0) {
                path.lineTo(730, guide + excessY);
            } else {
                path.lineTo(720, excessY);
                path.quadTo(732, 50, 730, 60);
            }
            canvas.drawPath(path, p1);
            //画填充内槽的颜色
            canvas.drawPath(path, p7);
            //重置
            path.reset();
            //画深度虚线
            for (int i = 0; i < depthPositionArray1.length; i++) {
                path.moveTo(140, depthPositionArray1[i]);
                path.lineTo(644, depthPositionArray1[i]);
            }
            canvas.drawPath(path, p3);
            path.reset();
            //画齿位虚线
            for (int i = 0; i < toothDistance1.length; i++) {
                path.moveTo(toothDistance1[i], depthStartY1);
                path.lineTo(toothDistance1[i], depthEndY1);
            }
            canvas.drawPath(path, p3);
            path.reset();
            //画每个齿的深度名
            for (int i = 0; i < toothDepthNameGroup1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i], depthPositionArray1[0] - 30, p4);  //画蓝色的字
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    if (toothDepthNameGroup1[i].contains("X")) {
                        canvas.drawText("X", toothDistance1[i], depthPositionArray1[0] - 30, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup1[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance1[i], depthPositionArray1[0] - 30, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], depthPositionArray1[0] - 30, p4);//画蓝色的字
                }
            }
        } else if (ki.getSide() == 3) {
            if(guide!=0){
                path.moveTo(730,guide+excessY);
            }else {
                path.moveTo(730,60);
                path.quadTo(732, 50, 720,50);
                path.lineTo(690,50);
            }
            //判断第2组齿的深度位子
            for (int i = 0;  i < toothDistance2.length; i++) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    toothDepthPositionY2[i] = depthPositionArray2[0] + 10;
                } else if (toothDepthNameGroup2[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup2[i].split("\\.");
                    if (depthDataMap2.get(str[0]) == null) {
                        toothDepthPositionY2[i] = depthPositionArray2[0] + 10;
                    } else {
                        //获得深度的位子  y轴方向
                        int depthPosition = depthDataMap2.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        int sumY = 0;
                        if (depthPosition == depthPositionArray2[depthPositionArray2.length - 1]) {
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionArray2.length; j++) {
                                if (depthPosition == depthPositionArray2[j]) {
                                    int distance = depthPositionArray2[j + 1] - depthPosition;
                                    sumY = (int) (distance * decimals);
                                    break;
                                }
                            }
                        }
                        toothDepthPositionY2[i] = depthPosition + sumY;
                    }
                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap2.get(toothDepthNameGroup2[i]) == null ? depthPositionArray2[0] + 10 : depthDataMap2.get(toothDepthNameGroup2[i]);
                    toothDepthPositionY2[i] = toothDepth;
                }
            }
            //绘制内槽
            for (int i = toothDistance1.length - 1; i >= 0; i--) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] - 10);
                    path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 10);
                    Log.d("第一组的齿是好多？", "onDraw: "+(depthPositionArray1[0] - 10));
                    toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                } else if (toothDepthNameGroup1[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup1[i].split("\\.");
                    if (depthDataMap1.get(str[0]) == null) {
                        path.lineTo(toothDistance1[i] + toothWidth, depthPositionArray1[0] - 10);
                        path.lineTo(toothDistance1[i] - toothWidth, depthPositionArray1[0] - 10);
                        toothDepthPositionY1[i] = depthPositionArray1[0] - 10;
                    } else {
                        //获得深度的位子  y轴方向
                        int depthPosition = depthDataMap1.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        int sumY = 0;
                        if (depthPosition == depthPositionArray1[depthPositionArray1.length - 1]) {
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionArray1.length; j++) {
                                if (depthPosition == depthPositionArray1[j]) {
                                    int distance = depthPositionArray1[j + 1] - depthPosition;
                                    sumY = (int) (distance * decimals);
                                    break;
                                }
                            }
                        }
                        path.lineTo(toothDistance1[i] + toothWidth, depthPosition + sumY);
                        path.lineTo(toothDistance1[i] - toothWidth, depthPosition + sumY);
                        toothDepthPositionY1[i] = depthPosition + sumY;
                    }
                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap1.get(toothDepthNameGroup1[i]) == null ? depthPositionArray1[0] - 10 : depthDataMap1.get(toothDepthNameGroup1[i]);
                    path.lineTo(toothDistance1[i] + toothWidth, toothDepth);
                    path.lineTo(toothDistance1[i] - toothWidth, toothDepth);
                    toothDepthPositionY1[i] = toothDepth;
                }
                if (i < toothDistance2.length) {
                    path.lineTo(toothDistance2[i] + toothWidth, toothDepthPositionY2[i] - grooveY);
                    path.lineTo(toothDistance2[i] - toothWidth, toothDepthPositionY2[i] - grooveY);
                }
            }
            //画圆弧
            int grooveHalfHeight = grooveY / 2;
            int firstToothPosition = toothDistance2[0] - toothWidth;
            if (ki.getExtraCut() == 1) {  //  钥匙柄切割一刀
                path.lineTo(140, toothDepthPositionY1[0]);
                path.lineTo(90, 64);
                path.quadTo(32, 30, 30, 100);
                path.lineTo(120, toothDepthPositionY1[0] + grooveY);
            }else {
                path.quadTo(firstToothPosition - 40, toothDepthPositionY2[0] - grooveHalfHeight, firstToothPosition, toothDepthPositionY2[0]);
            }
            //画第二组每个齿的深度
            for (int i = 0;  i < toothDistance2.length; i++) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    if(i==0){
                        path.lineTo(toothDistance2[i] + toothWidth, depthPositionArray2[0] + 10);
                    }else {
                        path.lineTo(toothDistance2[i] - toothWidth, depthPositionArray2[0] + 10);
                        path.lineTo(toothDistance2[i] + toothWidth, depthPositionArray2[0] + 10);
                    }
                    toothDepthPositionY2[i] = depthPositionArray2[0] + 10;
                } else if (toothDepthNameGroup2[i].contains(".")) {   //有小数的
                    //分割齿的深度名
                    String[] str = toothDepthNameGroup2[i].split("\\.");
                    if (depthDataMap2.get(str[0]) == null) {
                        if(i==0){
                            path.lineTo(toothDistance2[i] + toothWidth, depthPositionArray2[0] + 10);
                        }else {
                            path.lineTo(toothDistance2[i] - toothWidth, depthPositionArray2[0] + 10);
                            path.lineTo(toothDistance2[i] + toothWidth, depthPositionArray2[0] + 10);
                        }
                    } else {
                        //获得深度的位子  y轴方向
                        int depthPosition = depthDataMap2.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        int sumY = 0;
                        if (depthPosition == depthPositionArray2[depthPositionArray2.length - 1]) {
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionArray2.length; j++) {
                                if (depthPosition == depthPositionArray2[j]) {
                                    int distance = depthPositionArray2[j + 1] - depthPosition;
                                    sumY = (int) (distance * decimals);
                                    break;
                                }
                            }
                        }
                        if(i==0){
                            path.lineTo(toothDistance2[i] + toothWidth, depthPosition + sumY);
                        }else {
                            path.lineTo(toothDistance2[i] - toothWidth, depthPosition + sumY);
                            path.lineTo(toothDistance2[i] + toothWidth, depthPosition + sumY);
                        }
                    }
                } else {   //判断除去X的整数
                    int toothDepth = depthDataMap2.get(toothDepthNameGroup2[i]) == null ? depthPositionArray2[0] +10 : depthDataMap2.get(toothDepthNameGroup2[i]);
                    if(i==0){
                        path.lineTo(toothDistance2[i] + toothWidth, toothDepth);
                    }else {
                        path.lineTo(toothDistance2[i] - toothWidth, toothDepth);
                        path.lineTo(toothDistance2[i] + toothWidth, toothDepth);
                    }
                }
                if (i < toothDistance1.length) {
                    path.lineTo(toothDistance1[i] - toothWidth, toothDepthPositionY1[i] + grooveY);
                    path.lineTo(toothDistance1[i] + toothWidth, toothDepthPositionY1[i] + grooveY);
                }
            }
            path.lineTo(690,keyWidthY);
            path.lineTo(720, keyWidthY);
            path.quadTo(732, keyWidthY, 730, keyWidthY-10);
            canvas.drawPath(path,p1);
            canvas.drawPath(path,p7);
            path.reset();
            //画第一组深度
            for (int i = 0; i < depthPositionArray1.length; i++) {
                path.moveTo(140, depthPositionArray1[i]);
                path.lineTo(644, depthPositionArray1[i]);
            }
            //画第二组深度
            for (int i = 0; i < depthPositionArray2.length; i++) {
                path.moveTo(140, depthPositionArray2[i]);
                path.lineTo(644, depthPositionArray2[i]);
            }
            canvas.drawPath(path,p3);
            path.reset();
            //画第一组齿位的虚线
            for (int i = 0; i < toothDistance1.length; i++) {
                path.moveTo(toothDistance1[i], depthStartY1);
                path.lineTo(toothDistance1[i], depthEndY1);
            }
            //画第二组齿位的虚线
            for (int i = 0; i < toothDistance2.length; i++) {
                path.moveTo(toothDistance2[i], depthStartY2);
                path.lineTo(toothDistance2[i], depthEndY2);
            }
            canvas.drawPath(path,p3);
            path.reset();
            //画第一组齿的深度名
            for (int i = 0; i < toothDepthNameGroup1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i],  40, p4);  //画蓝色的字
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    if (toothDepthNameGroup1[i].contains("X")) {
                        canvas.drawText("X", toothDistance1[i], 40, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup1[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance1[i], 40, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], 40, p4);//画蓝色的字
                }
            }
            for (int i = 0; i < toothDepthNameGroup2.length; i++) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    canvas.drawText("X", toothDistance2[i], keyWidthY + 40, p4);  //画蓝色的字
                } else if (toothDepthNameGroup2[i].contains(".")) {
                    if (toothDepthNameGroup2[i].contains("X")) {
                        canvas.drawText("X", toothDistance2[i], keyWidthY +40, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup2[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance2[i],keyWidthY+ 40, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup2[i], toothDistance2[i], keyWidthY + 40, p4);//画蓝色的字
                }
            }
        }
        if(isShowArrows){
            if(ki.getAlign()==0){
                //画红色箭头
                path.reset();
                path.moveTo(90, 20);  //100
                path.lineTo(120, 20);//第一条  100
                path.lineTo(120, 12);
                path.lineTo(130, 24);//中间点  104
                path.lineTo(120, 36);
                path.lineTo(120, 28);
                path.lineTo(90, 28);
                path.close();
                canvas.drawPath(path, p5);
                path.reset();
            }else {
                path.reset();
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
