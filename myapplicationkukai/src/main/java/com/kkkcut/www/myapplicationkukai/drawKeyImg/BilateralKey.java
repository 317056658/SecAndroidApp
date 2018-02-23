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

import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19.
 * 双边钥匙
 */

public class BilateralKey extends Key {
    private Path mPath;//画点
    private Paint mBorderPaint;//画点线的笔
    private Paint mDashedPaint; //画虚线的笔
    private Paint mColorTextPaint;//画蓝色字体的笔
    private Paint mArrowsPaint;//画箭头
    private Paint mKeyAppearanceColorPaint;//画钥匙身体颜色
    public  String[] allToothDepthName = null;//齿号的别名  代表深度
    private KeyInfo ki;
    private String[] spaceGroup;
    private String[] depthGroup,depthNameGroup;
    private ArrayList<String[]> toothCodeList;  //保存齿组名字数据的集合
    private int maxToothDistance,minToothDistance;  // 最大齿位 最小齿位
    private List<Integer>  toothDistancesListOne;
    private List<Integer>  toothDistancesListTwo;
    private String[] toothDepthNameOne, toothDepthNameTwo;  //第一组齿名，第二组齿名。
    private int halfToothWidth = 14;
    // key  value  保存深度解析好的深度位置
    private Map<String, Integer> depthPositionMapOne,depthPositionMapTwo;

    private int[] depthPositionOne, depthPositionTwo;  //画深度位子用的数组

    private float spacesScaleValue, depthScaleValue;
    private int depthPositionStartYOne, depthPositionEndYOne, depthPositionStartYTwo, depthPositionEndYTwo;
    private int excessX;
    private int[] saveSpacesDataOne;  //定义一个保存第一组int类型的spaces数组
    private int[] saveSpacesDataTwo;  //定义一个保存第二组int类型的spaces数组
    private boolean isShowArrows=true;
    private int patternWidth,patternHeight;
    private float patternBodyWidth,patternBodyHeight;
    private int patternShoulderHeightA,patternShoulderHeightB;  //肩部的宽度 和高度
    private int patternShoulderWidthA,patternShoulderWidthB;
    private int patternCuspWidth;
    private int patternExtraWidth;
    private int extraTopY;
    private int patternBodyMaxY;
    private String[] depthNameOne,depthNameTwo;  //存放深度名的数组
    private int lastToothDistanceWidth;
    private boolean isDraw=true;  //默认可以绘制



    public BilateralKey(Context context,KeyInfo ki) {
        this(context,null,ki);
    }

    public BilateralKey(Context context, AttributeSet attrs, KeyInfo ki) {
        this(context,attrs,0,ki);
    }

    public BilateralKey(Context context, AttributeSet attrs, int defStyleAttr, KeyInfo ki) {
        super(context, attrs, defStyleAttr);
           this.ki=ki;
        this.initPaintAndPath();  //初始化画笔和路径
        toothDistancesListOne=new ArrayList<>();
        toothDistancesListTwo=new ArrayList<>();
        toothCodeList =new ArrayList<>();
    }


    public void setDrawPatternSize(int width,int height) {
        this.patternWidth=width;
        this.patternHeight=height;
        patternBodyWidth=(int)(patternWidth*0.73);
        patternShoulderWidthA=(int)(patternWidth*0.1);  //百分之10
        patternShoulderWidthB=(int)(patternWidth*0.1);
        patternCuspWidth=(int)(patternWidth*0.12);   //百分之12
        patternExtraWidth =(int)(patternWidth*0.05); //百分之5
        patternBodyHeight=(int) (patternHeight*0.74);  //百分之82;
        patternShoulderHeightA=(int)(patternHeight*0.13);
        patternShoulderHeightB=(int)(patternHeight*0.13);
        extraTopY =patternShoulderHeightA+4;   //上部百分之12
        patternBodyMaxY=(int)patternBodyHeight+extraTopY;
        this.analysisSpace();
        this.analysisDepthAndDepthName();
    }

    /**
     * 解析spase
     *
     */
    private  void analysisSpace(){
       spaceGroup =ki.getSpace().split(";");
        String[] spacesOne;
        String[] spacesTwo;
             if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                 if(spaceGroup.length==1){   //齿距只有一组
                     spacesOne= spaceGroup[0].split(",");
                     spacesTwo= spaceGroup[0].split(",");
                     saveSpacesDataOne =new int[spacesOne.length];
                     saveSpacesDataTwo =new int[spacesTwo.length];
                     toothDepthNameOne=new String[spacesOne.length];  //第一组齿的深度名
                     toothDepthNameTwo=new String[spacesTwo.length];   //第二组齿的深度名
                     //转为int
                     for (int i = 0; i < saveSpacesDataOne.length ; i++) {
                         saveSpacesDataOne[i]=Integer.parseInt(spacesOne[i]);
                     }
                     for (int i = 0; i < saveSpacesDataTwo.length ; i++) {
                         saveSpacesDataTwo[i]=Integer.parseInt(spacesTwo[i]);
                     }
                     maxToothDistance= saveSpacesDataOne[saveSpacesDataOne.length-1];
                 }else {  //2组space
                     spacesOne =spaceGroup[0].split(",");
                     spacesTwo=spaceGroup[1].split(",");
                     saveSpacesDataOne =new int[spacesOne.length];
                     saveSpacesDataTwo =new int[spacesTwo.length];
                     toothDepthNameOne=new String[spacesOne.length];  //第一组齿的深度名
                     toothDepthNameTwo=new String[spacesTwo.length];   //第二组齿的深度名
                     //转为int
                     for (int i = 0; i < saveSpacesDataOne.length ; i++) {
                         saveSpacesDataOne[i]=Integer.parseInt(spacesOne[i]);
                     }
                     for (int i = 0; i < saveSpacesDataTwo.length ; i++) {
                         saveSpacesDataTwo[i]=Integer.parseInt(spacesTwo[i]);
                     }
                     if(saveSpacesDataOne[saveSpacesDataOne.length-1]>= saveSpacesDataTwo[saveSpacesDataTwo.length-1]){
                         maxToothDistance= saveSpacesDataOne[saveSpacesDataOne.length-1];
                     }else {
                         maxToothDistance= saveSpacesDataTwo[saveSpacesDataTwo.length-1];
                     }
                 }
             }else {  //前端定位
                 if(spaceGroup.length==1){   //齿距只有一组
                     spacesOne= spaceGroup[0].split(",");
                     spacesTwo= spaceGroup[0].split(",");
                     saveSpacesDataOne =new int[spacesOne.length];
                     saveSpacesDataTwo =new int[spacesTwo.length];
                     toothDepthNameOne=new String[spacesOne.length];  //第一组齿的深度名
                     toothDepthNameTwo=new String[spacesTwo.length];   //第二组齿的深度名
                     //转为int
                     for (int i = 0; i < saveSpacesDataOne.length ; i++) {
                         saveSpacesDataOne[i]=Integer.parseInt(spacesOne[i]);
                     }
                     for (int i = 0; i < saveSpacesDataTwo.length ; i++) {
                         saveSpacesDataTwo[i]=Integer.parseInt(spacesTwo[i]);
                     }
                     maxToothDistance= saveSpacesDataOne[0];
                     minToothDistance= saveSpacesDataOne[saveSpacesDataOne.length-1];
                 }else {  //齿距有2组space
                     spacesOne =spaceGroup[0].split(",");
                     spacesTwo=spaceGroup[1].split(",");
                     saveSpacesDataOne =new int[spacesOne.length];
                     saveSpacesDataTwo =new int[spacesTwo.length];
                     toothDepthNameOne=new String[spacesOne.length];  //第一组齿的深度名
                     toothDepthNameTwo=new String[spacesTwo.length];   //第二组齿的深度名
                     //转为int
                     for (int i = 0; i < saveSpacesDataOne.length ; i++) {
                         saveSpacesDataOne[i]=Integer.parseInt(spacesOne[i]);
                     }
                     for (int i = 0; i < saveSpacesDataTwo.length ; i++) {
                         saveSpacesDataTwo[i]=Integer.parseInt(spacesTwo[i]);
                     }
                     if(saveSpacesDataOne[0]>= saveSpacesDataTwo[0]){
                         maxToothDistance= saveSpacesDataOne[0];
                     }else {
                         maxToothDistance= saveSpacesDataTwo[0];
                     }
                     //获得最小的齿距
                     if(saveSpacesDataOne[saveSpacesDataOne.length-1]<= saveSpacesDataTwo[saveSpacesDataTwo.length-1]){
                         minToothDistance= saveSpacesDataOne[saveSpacesDataOne.length-1];
                     }else {
                         minToothDistance= saveSpacesDataTwo[saveSpacesDataTwo.length-1];
                     }
                 }
             }
        this.calculateDrawToothDistances(spacesOne.length,spacesTwo.length);
    }

    /**
     * 传入第一组合第二组的齿距的数量
     * 计算绘制的齿距
     * @param oneGroupCount
     * @param twoGroupCount
     */
     private void calculateDrawToothDistances(int oneGroupCount,int twoGroupCount){
         //肩部定位
         if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
             spacesScaleValue=patternBodyWidth/maxToothDistance;
             for (int i = 0; i <oneGroupCount; i++) {
                 toothDistancesListOne.add((int)(saveSpacesDataOne[i]*spacesScaleValue+ patternExtraWidth)+patternShoulderWidthA);
             }
             for (int i = 0; i <twoGroupCount; i++) {
                 toothDistancesListTwo.add((int)(saveSpacesDataTwo[i]*spacesScaleValue+ patternExtraWidth)+patternShoulderWidthB);
             }
         }else {  //前端定位
             int sumX =(int)(this.patternBodyWidth +patternShoulderWidthA)+ patternExtraWidth;
             spacesScaleValue=patternBodyWidth/maxToothDistance;
             int surplusX= sumX-(sumX - (int)(minToothDistance* spacesScaleValue));
             for (int i = 0; i <oneGroupCount; i++) {
                 toothDistancesListOne.add(sumX-(int)(saveSpacesDataOne[i]*spacesScaleValue)+surplusX);
             }
             for (int i = 0; i <twoGroupCount; i++) {
                 toothDistancesListTwo.add(sumX-(int)(saveSpacesDataTwo[i]*spacesScaleValue)+surplusX);
             }
         }
     }

    /**
     * 解析深度和深度名到绘制的深度和深度名
     */
    private  void   analysisDepthAndDepthName(){
        depthGroup = ki.getDepth().split(";");  //分割深度
        depthNameGroup=ki.getDepth_name().split(";");
        depthScaleValue =patternBodyHeight/ki.getWidth();
        depthPositionMapOne=new HashMap<>();
        depthPositionMapTwo=new HashMap<>();
        this.calculateDrawDepth();
    }
    /**
     * 计算绘制的深度
     */
    private  void calculateDrawDepth(){
        if(depthGroup.length==1){
            String[] depths=depthGroup[0].split(",");
            depthNameOne=depthNameGroup[0].split(",");
            depthNameTwo=depthNameGroup[0].split(",");
            depthPositionOne=new int[depths.length];
            depthPositionTwo=new int[depths.length];
            for (int i = 0; i < depths.length; i++) {
                depthPositionOne[i]=patternBodyMaxY-(int)(Integer.parseInt(depths[i])* depthScaleValue);
                depthPositionMapOne.put(depthNameOne[i], depthPositionOne[i]);
                //第二组深度
                depthPositionTwo[i]=(int)(Integer.parseInt(depths[i])* depthScaleValue)+extraTopY;
                depthPositionMapTwo.put(depthNameTwo[i],depthPositionTwo[i]);
            }
        }else if(depthGroup.length==2){
            //深度
            String[] depthsOneGroup=depthGroup[0].split(",");
            String[] depthsTwoGroup=depthGroup[1].split(",");
            //深度名
            depthNameOne=depthNameGroup[0].split(",");
            depthNameTwo=depthNameGroup[1].split(",");
            depthPositionOne=new int[depthsOneGroup.length];
            depthPositionTwo=new int[depthsTwoGroup.length];
            for (int i = 0; i < depthsOneGroup.length; i++) {
                depthPositionOne[i]=patternBodyMaxY-(int)(Integer.parseInt(depthsOneGroup[i])* depthScaleValue);
                depthPositionMapOne.put(depthNameOne[i], depthPositionOne[i]);
            }
            for (int i = 0; i < depthsTwoGroup.length; i++) {
                //第二组深度
                depthPositionTwo[i]=(int)(Integer.parseInt(depthsTwoGroup[i])* depthScaleValue)+extraTopY;
                depthPositionMapTwo.put(depthNameTwo[i],depthPositionTwo[i]);
            }

        }
        //得到每组深度的第一个位子和最一个位子的数据
        depthPositionStartYOne = depthPositionOne[0];
        depthPositionEndYOne = depthPositionOne[depthPositionOne.length - 1];
        depthPositionStartYTwo = depthPositionTwo[0];
        depthPositionEndYTwo = depthPositionTwo[depthPositionTwo.length - 1];
    }

    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
        this.isDraw=isDraw;
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
    }


    /**
     * 绘制钥匙图像
     * @param canvas
     */
    private void drawKeyPattern(Canvas canvas){
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){   //肩部
           mPath.moveTo(0, 4);
           int sA =(int)(patternShoulderHeightA*0.25f);
           mPath.lineTo(patternShoulderWidthA- sA,4);
           mPath.quadTo(patternShoulderWidthA,4, patternShoulderWidthA, sA +4);
           mPath.lineTo(patternShoulderWidthA,extraTopY);
           mPath.lineTo(patternShoulderWidthA+ patternExtraWidth,extraTopY);
           //根据齿深度名画图
           for (int i = 0; i < toothDepthNameOne.length; i++) {
              if(toothDepthNameOne[i].contains(".")){
                   //分割齿的深度名
                   String[] newStr = toothDepthNameOne[i].split("\\.");
                   Integer depthValue=depthPositionMapOne.get(newStr[0]);
                   float decimals=Float.parseFloat("0."+ newStr[1]);
                   float valueY;
                   int distanceY;
                   if(depthValue==null){
                       if(newStr[0].equals("X")||newStr.equals("0")){
                           depthValue=depthPositionMapOne.get(depthNameOne[0]);
                           distanceY =depthValue-extraTopY;
                           valueY =distanceY*decimals;
                           mPath.lineTo(toothDistancesListOne.get(i)- halfToothWidth,depthValue- valueY);
                           mPath.lineTo(toothDistancesListOne.get(i)+ halfToothWidth,depthValue- valueY);
                       }
                   }else {
                       valueY =0;
                       if(depthValue == depthPositionOne[depthPositionOne.length-1]){
                           //等于最后一个深度位置不计算。
                       }else {
                           for(int j = 0; j< depthPositionOne.length; j++){
                               if(depthValue ==depthPositionOne[j]){
                                   distanceY = depthPositionOne[j+1]- depthValue;
                                   valueY =distanceY*decimals;
                                   break;
                               }
                           }
                       }
                       mPath.lineTo(toothDistancesListOne.get(i)- halfToothWidth, depthValue + valueY);
                       mPath.lineTo(toothDistancesListOne.get(i)+ halfToothWidth, depthValue + valueY);
                   }
               }else {
                  Integer  depthValue=depthPositionMapOne.get(toothDepthNameOne[i]);
                   mPath.lineTo(toothDistancesListOne.get(i)- halfToothWidth, depthValue==null?extraTopY: depthPositionMapOne.get(toothDepthNameOne[i]));
                   mPath.lineTo(toothDistancesListOne.get(i)+ halfToothWidth, depthValue==null?extraTopY: depthPositionMapOne.get(toothDepthNameOne[i]));
               }
           }
           //第一组的最后一个齿距宽度
         lastToothDistanceWidth = toothDistancesListOne.get(toothDistancesListOne.size()-1)+ halfToothWidth;
           //加上多出的宽度
           if ((toothDistancesListOne.get(toothDistancesListOne.size()-1) + halfToothWidth) < (toothDistancesListTwo.get(toothDistancesListTwo.size()-1) + halfToothWidth)) {
               excessX = (toothDistancesListTwo.get(toothDistancesListTwo.size()-1) + halfToothWidth) - (toothDistancesListOne.get(toothDistancesListOne.size()-1) + halfToothWidth);
               lastToothDistanceWidth = lastToothDistanceWidth + excessX;
              mPath.lineTo(lastToothDistanceWidth,extraTopY);
           }
           int patternBodyHeightPercent46Y =(int)(patternBodyHeight*0.46)+extraTopY;
           mPath.lineTo(lastToothDistanceWidth +(patternCuspWidth*0.93f), patternBodyHeightPercent46Y);
           int patternBodyHeightPercent50Y =(int)(patternBodyHeight*0.5)+extraTopY;
           int patternBodyHeightPercent54Y =(int)(patternBodyHeight*0.54)+extraTopY;
           mPath.quadTo(lastToothDistanceWidth +patternCuspWidth, patternBodyHeightPercent50Y, lastToothDistanceWidth +(patternCuspWidth*0.93f), patternBodyHeightPercent54Y);
           mPath.lineTo(lastToothDistanceWidth,patternBodyMaxY);

           for (int i = toothDepthNameTwo.length-1; i >=0; i--) {
            if (toothDepthNameTwo[i].contains(".")) {
                   String[] newStr = toothDepthNameTwo[i].split("\\.");
                   Integer depthValue=depthPositionMapTwo.get(newStr[0]);
                   float decimals=Float.parseFloat("0."+ newStr[1]);
                   float valueY;
                   int distanceY;
                   if(depthValue==null){
                       if(newStr[0].equals("X")||newStr.equals("0")){
                           depthValue=depthPositionMapTwo.get(depthNameTwo[0]);
                           distanceY =depthValue-patternBodyMaxY;
                           valueY =distanceY*decimals;
                           //i==最后一个长度 说明 i 里面有东西
                           if(i== toothDistancesListTwo.size()-1){
                               if(toothDistancesListTwo.get(i)+ halfToothWidth< lastToothDistanceWidth){
                                   mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                                   mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                               }else{
                                   mPath.setLastPoint(lastToothDistanceWidth,depthValue-valueY);
                                   mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                               }
                           }else {
                               mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                               mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                           }
                       }
                   }else {
                       valueY =0;
                       if(depthValue == depthPositionTwo[depthPositionTwo.length-1]){
                           //等于最后一个深度位置不计算。
                       }else {
                           for(int j = 0; j< depthPositionTwo.length; j++){
                               if(depthValue ==depthPositionTwo[j]){
                                   distanceY = depthValue- depthPositionTwo[j+1];
                                   valueY =distanceY*decimals;
                                   break;
                               }
                           }
                       }
                       //i==最后一个长度 说明 i 里面有东西
                       if(i== toothDistancesListTwo.size()-1){
                           if(toothDistancesListTwo.get(i)+ halfToothWidth< lastToothDistanceWidth){
                               mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                               mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                           }else{
                               mPath.setLastPoint(lastToothDistanceWidth,depthValue-valueY);
                               mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                           }
                       }else {
                           mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                           mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                       }
                   }
               }else {
               Integer depthValue=depthPositionMapTwo.get(toothDepthNameTwo[i]);
                   if(i== toothDistancesListTwo.size()-1) {
                       if(toothDistancesListTwo.get(i)+ halfToothWidth< lastToothDistanceWidth){
                           mPath.lineTo(toothDistancesListTwo.get(i) + halfToothWidth, depthValue == null ? patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                           mPath.lineTo(toothDistancesListTwo.get(i) - halfToothWidth, depthValue == null ?patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                       }else{
                           mPath.setLastPoint(lastToothDistanceWidth, depthValue == null ? patternBodyMaxY: depthPositionMapTwo.get(toothDepthNameTwo[i]));
                           mPath.lineTo(toothDistancesListTwo.get(i) - halfToothWidth, depthValue == null ?patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                       }
                   }else {
                       mPath.lineTo(toothDistancesListTwo.get(i) + halfToothWidth, depthValue == null ? patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                       mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth, depthValue == null ? patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                   }
               }
           }
            mPath.lineTo(patternShoulderWidthB+ patternExtraWidth,patternBodyMaxY);
            int sB =(int)(patternShoulderHeightB*0.25f);
            mPath.lineTo(patternShoulderWidthB,patternBodyMaxY);
            mPath.lineTo(patternShoulderWidthB,(patternBodyMaxY+patternShoulderHeightB)-sB);
            mPath.quadTo(patternShoulderWidthB,patternShoulderHeightB+patternBodyMaxY,patternShoulderWidthB-sB,patternShoulderHeightB+patternBodyMaxY);
            mPath.lineTo(0,patternShoulderHeightB+patternBodyMaxY);
            canvas.drawPath(mPath,mBorderPaint);
            canvas.drawPath(mPath,mKeyAppearanceColorPaint);
            mPath.reset(); //重置

       }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){  //前端
            mPath.moveTo(0, extraTopY);
            mPath.lineTo(patternShoulderWidthA+ patternExtraWidth,extraTopY);
            //根据齿深度名画图
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                if(toothDepthNameOne[i].contains(".")){
                    //分割齿的深度名
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    Integer depthValue=depthPositionMapOne.get(newStr[0]);
                    float decimals=Float.parseFloat("0."+ newStr[1]);
                    float valueY;
                    int distanceY;
                    if(depthValue==null){
                        if(newStr[0].equals("X")||newStr.equals("0")){
                            depthValue=depthPositionMapOne.get(depthNameOne[0]);
                            distanceY =depthValue-extraTopY;
                            valueY =distanceY*decimals;
                            mPath.lineTo(toothDistancesListOne.get(i)- halfToothWidth,depthValue- valueY);
                            mPath.lineTo(toothDistancesListOne.get(i)+ halfToothWidth,depthValue- valueY);
                        }
                    }else {
                        valueY =0;
                        if(depthValue == depthPositionOne[depthPositionOne.length-1]){
                            //等于最后一个深度位置不计算。
                        }else {
                            for(int j = 0; j< depthPositionOne.length; j++){
                                if(depthValue ==depthPositionOne[j]){
                                    distanceY = depthPositionOne[j+1]- depthValue;
                                    valueY =distanceY*decimals;
                                    break;
                                }
                            }
                        }
                        mPath.lineTo(toothDistancesListOne.get(i)- halfToothWidth, depthValue + valueY);
                        mPath.lineTo(toothDistancesListOne.get(i)+ halfToothWidth, depthValue + valueY);
                    }
                }else {
                    Integer  depthValue=depthPositionMapOne.get(toothDepthNameOne[i]);
                    mPath.lineTo(toothDistancesListOne.get(i)- halfToothWidth, depthValue==null?extraTopY: depthPositionMapOne.get(toothDepthNameOne[i]));
                    mPath.lineTo(toothDistancesListOne.get(i)+ halfToothWidth, depthValue==null?extraTopY: depthPositionMapOne.get(toothDepthNameOne[i]));
                }
            }
            //第一组的最后一个齿距宽度
            lastToothDistanceWidth = toothDistancesListOne.get(toothDistancesListOne.size()-1)+ halfToothWidth;
            //加上多出的宽度
            if ((toothDistancesListOne.get(toothDistancesListOne.size()-1) + halfToothWidth) < (toothDistancesListTwo.get(toothDistancesListOne.size()-1) + halfToothWidth)) {
                excessX = (toothDistancesListTwo.get(toothDistancesListOne.size()-1) + halfToothWidth) - (toothDistancesListOne.get(toothDistancesListOne.size()-1) + halfToothWidth);
                lastToothDistanceWidth = lastToothDistanceWidth + excessX;
                mPath.lineTo(lastToothDistanceWidth,extraTopY);
            }
            int patternBodyHeightPercent46Y =(int)(patternBodyHeight*0.46)+extraTopY;
            mPath.lineTo(lastToothDistanceWidth +(patternCuspWidth*0.93f), patternBodyHeightPercent46Y);
            int patternBodyHeightPercent50Y =(int)(patternBodyHeight*0.5)+extraTopY;
            int patternBodyHeightPercent54Y =(int)(patternBodyHeight*0.54)+extraTopY;
            mPath.quadTo(lastToothDistanceWidth +patternCuspWidth, patternBodyHeightPercent50Y, lastToothDistanceWidth +(patternCuspWidth*0.93f), patternBodyHeightPercent54Y);
            mPath.lineTo(lastToothDistanceWidth,patternBodyMaxY);

            for (int i = toothDepthNameTwo.length-1; i >=0; i--) {
                if (toothDepthNameTwo[i].contains(".")) {
                    String[] newStr = toothDepthNameTwo[i].split("\\.");
                    Integer depthValue=depthPositionMapTwo.get(newStr[0]);
                    float decimals=Float.parseFloat("0."+ newStr[1]);
                    float valueY;
                    int distanceY;
                    if(depthValue==null){
                        if(newStr[0].equals("X")||newStr.equals("0")){
                            depthValue=depthPositionMapTwo.get(depthNameTwo[0]);
                            distanceY =depthValue-patternBodyMaxY;
                            valueY =distanceY*decimals;
                            //i==最后一个长度 说明 i 里面有东西
                            if(i== toothDistancesListTwo.size()-1){
                                if(toothDistancesListTwo.get(i)+ halfToothWidth< lastToothDistanceWidth){
                                    mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                                    mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                                }else{
                                    mPath.setLastPoint(lastToothDistanceWidth,depthValue-valueY);
                                    mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                                }
                            }else {
                                mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                                mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                            }
                        }else {

                        }
                    }else {
                        valueY =0;
                        if(depthValue == depthPositionTwo[depthPositionTwo.length-1]){
                            //等于最后一个深度位置不计算。
                        }else {
                            for(int j = 0; j< depthPositionTwo.length; j++){
                                if(depthValue ==depthPositionTwo[j]){
                                    distanceY = depthValue- depthPositionTwo[j+1];
                                    valueY =distanceY*decimals;
                                    break;
                                }
                            }
                        }
                        //i==最后一个长度 说明 i 里面有东西
                        if(i== toothDistancesListTwo.size()-1){
                            if(toothDistancesListTwo.get(i)+ halfToothWidth< lastToothDistanceWidth){
                                mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                                mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                            }else{
                                mPath.setLastPoint(lastToothDistanceWidth,depthValue-valueY);
                                mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                            }
                        }else {
                            mPath.lineTo(toothDistancesListTwo.get(i)+ halfToothWidth,depthValue-valueY);
                            mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth,depthValue-valueY);
                        }
                    }
                }else {
                    Integer depthValue=depthPositionMapTwo.get(toothDepthNameTwo[i]);
                    if(i== toothDistancesListTwo.size()-1) {
                        if(toothDistancesListTwo.get(i)+ halfToothWidth< lastToothDistanceWidth){
                            mPath.lineTo(toothDistancesListTwo.get(i) + halfToothWidth, depthValue == null ? patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                            mPath.lineTo(toothDistancesListTwo.get(i) - halfToothWidth, depthValue == null ?patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                        }else{
                            mPath.setLastPoint(lastToothDistanceWidth, depthValue == null ? patternBodyMaxY: depthPositionMapTwo.get(toothDepthNameTwo[i]));
                            mPath.lineTo(toothDistancesListTwo.get(i) - halfToothWidth, depthValue == null ?patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                        }
                    }else {
                        mPath.lineTo(toothDistancesListTwo.get(i) + halfToothWidth, depthValue == null ? patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                        mPath.lineTo(toothDistancesListTwo.get(i)- halfToothWidth, depthValue == null ? patternBodyMaxY : depthPositionMapTwo.get(toothDepthNameTwo[i]));
                    }
                }
            }
            int extraX=0;
            if(toothDistancesListTwo.get(0)- halfToothWidth<(patternShoulderWidthB+ patternExtraWidth)){
                extraX=(patternShoulderWidthB+ patternExtraWidth)-(toothDistancesListTwo.get(0)-halfToothWidth);
            }
            mPath.lineTo((patternShoulderWidthB+ patternExtraWidth)-(extraX+1),patternBodyMaxY);
            mPath.lineTo(0,patternBodyMaxY);
            canvas.drawPath(mPath,mBorderPaint);
            canvas.drawPath(mPath,mKeyAppearanceColorPaint);
            mPath.reset(); //重置
       }
       if(isDraw){
           this.drawDepthPattern(canvas);
           this.drawToothCodePattern(canvas);
       }
        mPath.reset();  //重置
        if(isShowArrows){
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                mPath.moveTo(70,20);  //100
                mPath.lineTo(100,20);//第一条  100
                mPath.lineTo(100,12);
                mPath.lineTo(110,24);//中间点  104
                mPath.lineTo(100,36);
                mPath.lineTo(100,28);
                mPath.lineTo(70,28);
                mPath.close();
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }else {
                mPath.moveTo(lastToothDistanceWidth, 30);//80
                mPath.lineTo(lastToothDistanceWidth + 10, 20);//70
                mPath.lineTo(lastToothDistanceWidth + 10, 27);//77
                mPath.lineTo(lastToothDistanceWidth + 35, 27);//77
                mPath.lineTo(lastToothDistanceWidth + 35, 33);//83
                mPath.lineTo(lastToothDistanceWidth + 10, 33);//83
                mPath.lineTo(lastToothDistanceWidth + 10, 40);//90
                mPath.lineTo(lastToothDistanceWidth, 30);//80
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }
        }
    }
    public  void setDrawToothWidth(int width){
        halfToothWidth=width/2;
    }

    /**
     * 自定义绘制锯齿状
     */
   public void  customDrawSerrated(){
       int  k=0;
       for (int i = 0; i < toothDepthNameOne.length ; i++) {
           if(depthNameOne.length>=3){
               if(i>=3) {
                       toothDepthNameOne[i] = depthNameOne[k];
                       k++;
                     if(k==3){
                         k=0;
                     }
               }else {
                   toothDepthNameOne[i] = depthNameOne[i];
               }
           }else {
               if(i>=2){
                   toothDepthNameOne[i]=depthNameOne[k];
                   k++;
                   if(k==2){
                       k=0;
                   }
               }else {
                   toothDepthNameOne[i]=depthNameOne[i];
               }
           }
       }
       k=0;
       //第二组齿的深度名
       for (int i = 0; i < toothDepthNameTwo.length ; i++) {
           if(depthNameTwo.length>=3){
               if(i>=3) {
                   toothDepthNameTwo[i] = depthNameTwo[k];
                   k++;
                   if(k==3){
                       k=0;
                   }
               }else {
                   toothDepthNameTwo[i] = depthNameTwo[i];
               }
           }else {
               if(i>=2){
                   toothDepthNameTwo[i]=depthNameTwo[k];
                   k++;
                   if(k==2){
                       k=0;
                   }
               }else {
                   toothDepthNameTwo[i]=depthNameTwo[i];
               }
           }
       }
   }

    /**
     * 绘制的齿代码图样
     */
    private void  drawToothCodePattern(Canvas canvas){
        //画第一组齿位
        for (int i = 0; i < toothDistancesListOne.size(); i++) {
            mPath.moveTo(toothDistancesListOne.get(i), depthPositionStartYOne);
            mPath.lineTo(toothDistancesListOne.get(i), depthPositionEndYOne);
            if(toothDepthNameOne[i].contains(".")){
                mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                String[] newStr=toothDepthNameOne[i].split("\\.");
                int num=Integer.parseInt(newStr[1]);
                if(depthPositionMapOne.get(newStr[0])==null){
                    if(newStr[0].equals("X")||newStr[0].equals("0")){
                        if(num>=5){
                            canvas.drawText(depthNameOne[0], toothDistancesListOne.get(i), extraTopY -10, mColorTextPaint);//画红色的字
                        }else {
                            canvas.drawText(newStr[0], toothDistancesListOne.get(i), extraTopY -10 , mColorTextPaint);//画红色的字
                        }
                    }else {
                        canvas.drawText(newStr[0], toothDistancesListOne.get(i), extraTopY -10 , mColorTextPaint);
                    }
                }else {
                    if(num>=5){
                        if(newStr[0].equals(depthNameOne[depthNameOne.length-1])){
                            canvas.drawText(newStr[0], toothDistancesListOne.get(i), extraTopY -10 , mColorTextPaint);//画红色的字
                        }else {
                            for (int j = 0; j <depthNameOne.length ; j++) {
                                if(depthNameOne[j].equals(newStr[0])){
                                    canvas.drawText(depthNameOne[j+1], toothDistancesListOne.get(i), extraTopY  -10, mColorTextPaint);//画红色的字
                                    break; //跳出循环
                                }
                            }
                        }
                    }else {
                        canvas.drawText(newStr[0], toothDistancesListOne.get(i),extraTopY -10, mColorTextPaint);//画红色的字
                    }
                }
            }else{//不包涵画蓝色的字
                mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                canvas.drawText(toothDepthNameOne[i],toothDistancesListOne.get(i), extraTopY -10, mColorTextPaint);//画蓝色的字
            }
        }
        //画第二组齿位
        for (int i = 0; i < toothDistancesListTwo.size(); i++) {
            mPath.moveTo(toothDistancesListTwo.get(i), depthPositionStartYTwo);
            mPath.lineTo(toothDistancesListTwo.get(i), depthPositionEndYTwo);
            if(toothDepthNameTwo[i].contains(".")){
                mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                String[] newStr=toothDepthNameTwo[i].split("\\.");
                int num=Integer.parseInt(newStr[1]);
                if(depthPositionMapTwo.get(newStr[0])==null){
                    if(newStr[0].equals("X")||newStr[0].equals("0")){
                        if(num>=5){
                            canvas.drawText(depthNameTwo[0], toothDistancesListTwo.get(i), patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                        }else {
                            canvas.drawText(newStr[0], toothDistancesListTwo.get(i), patternBodyMaxY+ 40, mColorTextPaint);//画红色的字
                        }
                    }else {
                        canvas.drawText(newStr[0], toothDistancesListTwo.get(i), patternBodyMaxY+ 40, mColorTextPaint);//画红色的字
                    }
                }else {
                    if(num>=5){
                        if(newStr[0].equals(depthNameTwo[depthNameTwo.length-1])){
                            canvas.drawText(newStr[0],toothDistancesListTwo.get(i), patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                        }else {
                            for (int j = 0; j <depthNameTwo.length ; j++) {
                                if(depthNameTwo[j].equals(newStr[0])){
                                    canvas.drawText(depthNameTwo[j+1], toothDistancesListTwo.get(i), patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                                    break; //跳出循环
                                }
                            }
                        }
                    }else {
                        canvas.drawText(newStr[0], toothDistancesListTwo.get(i),patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                    }
                }
            }else{//不包涵画蓝色的字
                mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                canvas.drawText(toothDepthNameTwo[i],toothDistancesListTwo.get(i), patternBodyMaxY+40, mColorTextPaint);//画蓝色的字
            }
        }
        canvas.drawPath(mPath, mDashedPaint);//画X轴方向的。
        mPath.reset();  //重置
    }

    /**
     *   绘制深度的图样
     */
    private void  drawDepthPattern(Canvas canvas){
        //画深度第一组深度的值
        for (int i = 0; i < depthPositionOne.length; i++) {
            mPath.moveTo(patternShoulderWidthA, depthPositionOne[i]);
            mPath.lineTo(lastToothDistanceWidth, depthPositionOne[i]);
        }

        //画深度第二组深度的值
        for (int i = 0; i < depthPositionTwo.length; i++) {
            mPath.moveTo(patternShoulderWidthB, depthPositionTwo[i]);
            mPath.lineTo(lastToothDistanceWidth, depthPositionTwo[i]);
        }
        canvas.drawPath(mPath, mDashedPaint);
        mPath.reset();
    }

    /**
     * 设置钥匙齿的深度名
     * @param toothCode
     */
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)){
            allToothDepthName = toothCode.split(",");
            if(spaceGroup.length==1){
                for (int i = 0; i < allToothDepthName.length ; i++) {
                    toothDepthNameOne[i]= allToothDepthName[i];
                    toothDepthNameTwo[i]= allToothDepthName[i];
                }
            }else if(spaceGroup.length==2){
                int j=0;
                for (int i = 0; i < allToothDepthName.length ; i++) {
                    if(i< toothDepthNameOne.length){
                        toothDepthNameOne[i]= allToothDepthName[i];
                    }else {
                        toothDepthNameTwo[j]= allToothDepthName[i];
                        j++;
                    }
                }
            }
        }else {  //没有为默认
            this.setToothCodeDefault();
        }
    }

    @Override
    public void redrawKey() {
        this.invalidate();//重绘
    }

    /**
     * 设置钥匙齿的数量
     * @param toothAmount
     */
    public void setToothAmount(int toothAmount) {
        //清空 绘制的齿距
        toothDistancesListOne.clear();
        toothDistancesListTwo.clear();
        toothDepthNameOne=new String[toothAmount];
        toothDepthNameTwo=new String[toothAmount];
        int [] spacesOne=new int[toothAmount];
        int [] spacesTwo =new int[toothAmount];
        String toothCode="";
        if(spaceGroup.length==1){
            for (int i = 0; i < toothAmount; i++) {
                spacesOne[i]= saveSpacesDataOne[i];
                toothDepthNameOne[i]="X";
                toothCode+="X,";
            }
            for (int i = 0; i < toothAmount; i++) {
                spacesTwo[i]= saveSpacesDataTwo[i];
                toothDepthNameTwo[i]="X";
            }
        }else if(spaceGroup.length==2){
            for (int i = 0; i < toothAmount; i++) {
                spacesOne[i]= saveSpacesDataOne[i];
                toothDepthNameOne[i]="X";
                toothCode+="X,";
            }
            for (int i = 0; i < toothAmount; i++) {
                spacesTwo[i]= saveSpacesDataTwo[i];
                toothDepthNameTwo[i]="X";
                toothCode+="X,";
            }
        }
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){  //肩部
            if(spacesOne[spacesOne.length-1]>= spacesTwo[spacesTwo.length-1]){
                maxToothDistance=spacesOne[spacesOne.length-1];
            }else {
                maxToothDistance= spacesTwo[spacesTwo.length-1];
            }
        }else {   //前端
            if(spacesOne[0]>= spacesTwo[0]){
                maxToothDistance=spacesOne[0];
            }else {
                maxToothDistance= spacesTwo[0];
            }
            //获得最小的齿距
            if(spacesOne[spacesOne.length-1]<= spacesTwo[spacesTwo.length-1]){
                minToothDistance=spacesOne[spacesOne.length-1];
            }else {
                minToothDistance= spacesTwo[spacesTwo.length-1];
            }
        }
        ki.setKeyToothCode(toothCode);
        this.calculateDrawToothDistances(spacesOne.length, spacesTwo.length);
        this.invalidate();
    }
    public int getToothAmount() {
        return saveSpacesDataOne.length;
   }

    @Override
    public ArrayList<String[]> getToothCode() {
        toothCodeList.clear();
        if(spaceGroup.length==1){
            toothCodeList.add(toothDepthNameOne);
        }else if(spaceGroup.length==2){
            toothCodeList.add(toothDepthNameOne);
            toothCodeList.add(toothDepthNameTwo);
        }
        return toothCodeList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
        this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String toothCode="";
        if(spaceGroup.length==1){
            for (int i = 0; i < toothDepthNameOne.length ; i++) {
                toothDepthNameOne[i]="X";
                toothCode+="X,";
            }
            for (int i = 0; i < toothDepthNameTwo.length ; i++) {
                toothDepthNameTwo[i]="X";
            }
        }else if(spaceGroup.length==2){
            for (int i = 0; i < toothDepthNameOne.length ; i++) {
                toothDepthNameOne[i]="X";
                toothCode+="X,";
            }
            for (int i = 0; i < toothDepthNameTwo.length ; i++) {
                toothDepthNameTwo[i]="X";
                toothCode+="X,";
            }

        }
        ki.setKeyToothCode(toothCode);
    }

    /**
     *   获得双边钥匙读钥匙指令
     * @param locatingSlot
     * @param detectionMode
     * @param isRound
     * @return
     */

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";
        String toothQuantity;
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
              //双边齿
            if(ki.getSide()==0){
                ki.setSide(2);
            }
            String[] spaceGroup = ki.getSpace().split(";");
            String[] spaces;
            toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
            for (int i = 0; i < spaceGroup.length; i++) {
                spaces = spaceGroup[i].split(",");
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
            lastToothOrExtraCut = ki.getLastBitting();
            String order = "!DR1;!BC;"
                    + keyNorm     //钥匙规范
                    + ki.getType() + "," //钥匙类型
                    + ki.getAlign() + ","//钥匙定位方式
                    + ki.getSide() + ","// 有效边
                    + ki.getWidth() + ","// 钥匙片宽度
                    + ki.getThick() + ","// 钥匙胚厚度
                    + ki.getGuide() + ","// 表示加不加切
                    + ki.getLength() + ","//钥匙片长度
                    + ki.getCutDepth() + ","//切割深度
                    + toothQuantity// 齿的数量
                    + toothPositionData//齿位置数据
                    + toothWidth//齿顶宽数据
                    + toothDepthQuantity + ","//齿深数量
                    + toothDepthData//齿深数据
                    + locatingSlot + ","  //钥匙的定位位置
                    + toothCode   //齿号代码  其实都是零
                    + ki.getNose() + ","  // 鼻部长度
                    + ki.getGroove() + ","  //槽宽
                    + toothDepthName
                    + lastToothOrExtraCut + ";"//最后齿或扩展切割
                    + "!AC;!DE" + detectionMode + "," + isRound + ";";
            return order;
    }

    @Override
    public String getCutOrder(int cutDepth,int locatingSlot,String assistClamp,String cutterDiameter,int speed, int ZDepth,int detectionMode) {
        String keyNorm ="!SB";
        String toothQuantity;
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut;
        int knifeType=1;  //到类型
        int noseCut;
        String DDepth ="0.75";
        if(TextUtils.isEmpty(assistClamp)){
            assistClamp="";
        }
        //双边齿
        if(ki.getSide()==0){
            ki.setSide(2);
        }
        if(ki.getNose()!=0){  //有鼻部就切割
            noseCut=1;  //1为切割鼻部
        }else {
            noseCut=0;  //0不切割鼻部
        }
        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothCode = ki.getKeyToothCode();  //获得实际的齿号
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
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
        lastToothOrExtraCut = ki.getLastBitting();
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide() + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getGuide() + ","// 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","//切割深度
                + toothQuantity// 齿的数量
                + toothPositionData//齿位置数据
                + toothWidth//齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData//齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码
                + ki.getNose() + ","  // 鼻部长度
                + ki.getGroove() + ","  //槽宽
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

    /**
     * 初始化路径和画笔
     */
  private  void initPaintAndPath() {
        mPath = new Path();//画点
        mBorderPaint = new Paint();//画点线的笔
        mDashedPaint = new Paint(); //画虚线的笔
        mColorTextPaint = new Paint();//画蓝色字体的笔
        mArrowsPaint = new Paint();//画红色箭头的笔
        mKeyAppearanceColorPaint = new Paint();  //画钥匙身体颜色的笔
        //画钥匙形状属性
        mBorderPaint.setAntiAlias(true);//去掉抗锯齿
        mBorderPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        mBorderPaint.setStrokeWidth(2);
        //画虚线的属性
        mDashedPaint.setAntiAlias(true);//去掉抗锯齿
        mDashedPaint.setColor(Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        mDashedPaint.setStyle(Paint.Style.STROKE);//设置画笔描边
        mDashedPaint.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        mDashedPaint.setPathEffect(effects);
        //画字体的属性
        mColorTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mColorTextPaint.setTextSize(40);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //画主体颜色的笔的属性
        mKeyAppearanceColorPaint.setColor(Color.parseColor("#BABABA"));
        mKeyAppearanceColorPaint.setAntiAlias(true);
        mKeyAppearanceColorPaint.setStyle(Paint.Style.FILL);
        mKeyAppearanceColorPaint.setStrokeWidth(1);
    }
}
