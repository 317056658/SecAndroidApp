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
import java.util.Map;

/**
 * 双轨外槽
 * Created by Administrator on 2017/7/18.
 */

public class DualPathOuterGrooveKey extends  Key {
    private Path mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;
    private Paint mColorTextPaint;
    private Paint mArrowsPaint;
    private Paint mInsideColorPaint;
    private KeyInfo ki;
    private int[] toothDistanceOne,toothDistanceTwo;
    private int[] halfToothWidthOne,halfToothWidthTwo;
    private String[] toothDepthNameOne, toothDepthNameTwo;
    private int[] depthPositionOne, depthPositionTwo;
    //定义2个hasaMap集合  保存每组齿的深度位子
    private Map<String, Integer> depthPositionMapOne, depthPositionMapTwo;
    private int depthPositionStartYOne, depthPositionEndYOne;
    private int depthPositionStartYTwo, depthPositionEndYTwo;
    private ArrayList<String[]> toothCodeList;
    private  float spacesScaleValue;
    private double depthScaleValue;
    private String[] depthNameOne,depthNameTwo;
    private boolean isShowArrows=true;  //默认为true
    private float patternMiddleWidth;
    private int patternShoulderWidth;
    private int patternShoulderHeight;
    private int patternLocationGrooveWidth;
    private double patternBodyHeight;  //图案深度高度
    private int patternRightWidth;
    private int patternLeftWidth;
    private  boolean  isDraw=true; //默认为true
    private int  extraTopY;
    private int patternBodyMaxY;
    private int percent7_y;
    private int patternExtraCutWidth;  //图案额外切割的宽度




    public DualPathOuterGrooveKey(Context context,KeyInfo ki) {
          this(context,null,ki);
    }

    public DualPathOuterGrooveKey(Context context, AttributeSet attrs,KeyInfo ki) {
        this(context,attrs,0,ki);
    }
    public DualPathOuterGrooveKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.ki=ki;
        depthPositionMapOne =new HashMap<>();
        depthPositionMapTwo =new HashMap<>();
        toothCodeList =  new ArrayList<>();
        this.initPaintAndPath();  //初始化绘制的工具
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){   //肩部
                patternLeftWidth=(int)(width*0.16f);
                patternMiddleWidth=(int)(width*0.72f);
                patternRightWidth=(int)(width*0.11f);
                patternLocationGrooveWidth=(int)(patternLeftWidth*0.3f);
                patternShoulderWidth=(int)(patternLeftWidth*0.5f);
                patternShoulderHeight=(int)(height*0.2f);
                patternBodyHeight=(int)(height*0.66f);
                extraTopY=(int)(height*0.13f)+4;
                percent7_y =patternShoulderHeight-(int)(height*0.13f);
                patternBodyMaxY=extraTopY+(int)patternBodyHeight;
            }else {  //前端
                if(ki.getLastBitting()!=0){
                    patternLeftWidth=(int)(width*0.12f);
                    patternMiddleWidth=(int)(width*0.63f);
                    patternExtraCutWidth=(int)(width*0.14f);
                    patternRightWidth=(int)(width*0.1f);
                }else {
                    patternLeftWidth=(int)(width*0.16f);
                    patternMiddleWidth=(int)(width*0.72f);
                    patternRightWidth=(int)(width*0.11f);
                }
                patternBodyHeight=(int)(height*0.66f);
                extraTopY=(int)(height*0.13f)+4;
                patternBodyMaxY=extraTopY+(int)patternBodyHeight;
            }
        this.analysisSpaceAndSpaceWidth();
        this.analysisDepthAndDepthName();
    }

    private void analysisSpaceAndSpaceWidth(){
        String[] spaceGroup=ki.getSpace().split(";");
        String[] spaceWidthGroup=ki.getSpace_width().split(";");
        String[] spacesOne =spaceGroup[0].split(",");
        String[] spacesTwo =spaceGroup[1].split(",");
        String[] spacesWidthOne =spaceWidthGroup[0].split(",");
        String[] spacesWidthTwo =spaceWidthGroup[1].split(",");
        toothDistanceOne=new int[spacesOne.length];
        toothDistanceTwo=new int[spacesTwo.length];
        //齿的深度名
        toothDepthNameOne=new String[spacesOne.length];
        toothDepthNameTwo=new String[spacesTwo.length];
        halfToothWidthOne=new int[spacesWidthOne.length];
        halfToothWidthTwo=new int[spacesWidthTwo.length];
        this.calculateDrawToothDistanceAndToothWidth(spacesOne, spacesTwo, spacesWidthOne, spacesWidthTwo);
    }
    /**
     * 结算绘制的齿距和齿宽
     * @param spacesOne
     * @param spacesTwo
     * @param spacesWidthOne
     * @param spacesWidthTwo
     */
    private void  calculateDrawToothDistanceAndToothWidth(String[] spacesOne, String[] spacesTwo, String[] spacesWidthOne, String[] spacesWidthTwo){
        for (int i = 0; i <toothDistanceOne.length ; i++) {
            toothDistanceOne[i]=Integer.parseInt(spacesOne[i]);
        }
        for (int i = 0; i <toothDistanceTwo.length ; i++) {
            toothDistanceTwo[i]=Integer.parseInt(spacesTwo[i]);
        }
        int toothDistanceMaxLength;  //齿距的最大长度
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){  //肩部
            if(toothDistanceOne[toothDistanceOne.length-1]>=toothDistanceTwo[toothDistanceTwo.length-1]){
                toothDistanceMaxLength =toothDistanceOne[toothDistanceOne.length-1] + (toothDistanceOne[toothDistanceOne.length-1]-toothDistanceOne[toothDistanceOne.length-2]);
            }else {
                toothDistanceMaxLength =toothDistanceTwo[toothDistanceTwo.length-1] + (toothDistanceTwo[toothDistanceTwo.length-1]-toothDistanceTwo[toothDistanceTwo.length-2]);
            }
            spacesScaleValue=patternMiddleWidth/ toothDistanceMaxLength;
            // 一组有多少个齿距,就有多少个齿宽
            for (int i = 0; i <toothDistanceOne.length ; i++) {
                toothDistanceOne[i]=(int)(toothDistanceOne[i]*spacesScaleValue)+patternLeftWidth;
                halfToothWidthOne[i]=(int)((Integer.parseInt(spacesWidthOne[i])*spacesScaleValue)/2);
            }
            for (int i = 0; i <toothDistanceTwo.length ; i++) {
                toothDistanceTwo[i]=(int)(toothDistanceTwo[i]*spacesScaleValue)+patternLeftWidth;
                halfToothWidthTwo[i]=(int)((Integer.parseInt(spacesWidthTwo[i])*spacesScaleValue)/2);
            }
        }else {//前端
            if(toothDistanceOne[0]>=toothDistanceTwo[0]){
                toothDistanceMaxLength =(toothDistanceOne[0] + (toothDistanceOne[0]-toothDistanceOne[1]));
            }else {
                toothDistanceMaxLength =(toothDistanceTwo[0] + (toothDistanceTwo[0]-toothDistanceTwo[1]));
            }
            spacesScaleValue=patternMiddleWidth/ toothDistanceMaxLength;
            int sumX = (int) (patternMiddleWidth+patternLeftWidth)+patternExtraCutWidth;
            // 一组有多少个齿距,就有多少个齿宽
            for (int i = 0; i <toothDistanceOne.length ; i++) {
                toothDistanceOne[i]=sumX-(int)(toothDistanceOne[i]*spacesScaleValue);
                halfToothWidthOne[i]=(int)((Integer.parseInt(spacesWidthOne[i])*spacesScaleValue)/2);
            }
            for (int i = 0; i <toothDistanceTwo.length ; i++) {
                toothDistanceTwo[i]=sumX-(int)(toothDistanceTwo[i]*spacesScaleValue);
                halfToothWidthTwo[i]=(int)((Integer.parseInt(spacesWidthTwo[i])*spacesScaleValue)/2);
            }

        }
    }
  private void analysisDepthAndDepthName(){
      String[]  depthGroup=ki.getDepth().split(";");
      String[] depthNameGroup=ki.getDepth_name().split(";");
      String[]  depthOne=null;
      String[]  depthTwo=null;
      if(depthGroup.length==1){
          depthOne=depthGroup[0].split(",");
          depthTwo=depthGroup[0].split(",");
          depthNameOne=depthNameGroup[0].split(",");
          depthNameTwo=depthNameGroup[0].split(",");
      }else if(depthGroup.length==2){
          depthOne=depthGroup[0].split(",");
          depthTwo=depthGroup[1].split(",");
          depthNameOne=depthNameGroup[0].split(",");
          depthNameTwo=depthNameGroup[1].split(",");
      }
      depthPositionOne=new int[depthOne.length];
      depthPositionTwo=new int[depthTwo.length];
      this.calculateDrawDepth(depthOne,depthTwo);
  }

    /**
     * 计算绘制的深度
     */
  private void  calculateDrawDepth(String[]depthOne,String[] depthTwo){
      depthScaleValue=patternBodyHeight/ki.getWidth();
      //第一组深度
      for (int i = 0; i < depthPositionOne.length ; i++) {
          depthPositionOne[i]=patternBodyMaxY-(int)(Integer.parseInt(depthOne[i])* depthScaleValue);
          //根据第一组的深度名保存换算好的深度位子
          depthPositionMapOne.put(depthNameOne[i], depthPositionOne[i]);
      }
      depthPositionStartYOne = depthPositionOne[0];
      depthPositionEndYOne = depthPositionOne[depthPositionOne.length-1];

      //第二组深度
      for (int i = 0; i < depthPositionTwo.length ; i++) {
          depthPositionTwo[i]= (int)(Integer.parseInt(depthTwo[i])* depthScaleValue)+extraTopY;
          //根据第二组的深度名保存换算好的深度位子
          depthPositionMapTwo.put(depthNameTwo[i], depthPositionTwo[i]);
      }
      depthPositionStartYTwo = depthPositionTwo[0];
      depthPositionEndYTwo = depthPositionTwo[depthPositionTwo.length-1];

  }

    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
            this.isDraw=isDraw;
    }


    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
         String[]  allToothDepthName = toothCode.split(",");
            int j=-1;
            for (int i = 0; i < allToothDepthName.length; i++) {
                if(i< toothDepthNameOne.length){
                    //第一组的齿名
                    toothDepthNameOne[i]=allToothDepthName[i];
                }else {
                    j++;
                    //第二组的齿名
                    toothDepthNameTwo[j]=allToothDepthName[i];
                }
            }
        }else {
            this.setToothCodeDefault();
        }

    }

    /**
     * 重绘钥匙
     */
    @Override
    public void redrawKey() {
        //重绘
        this.invalidate();
    }

    @Override
    public void setToothAmount(int toothAmount) {

    }



    @Override
    public int getToothAmount() {
        return 0;
    }

    /**
     *  获得这把钥匙的所有齿的深度名
     * @return
     */
    @Override
    public ArrayList<String[]> getToothCode() {
        toothCodeList.clear();
        toothCodeList.add(toothDepthNameOne);
        toothCodeList.add(toothDepthNameTwo);
        return toothCodeList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
             this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String toothCode="";
        for (int i = 0; i < toothDepthNameOne.length ; i++) {
            toothDepthNameOne[i]="X";
            toothCode+="X,";
        }
        for (int j = 0; j < toothDepthNameTwo.length ; j++) {
            toothDepthNameTwo[j]="X";
            toothCode+="X,";
        }
        ki.setKeyToothCode(toothCode);
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
        int cutDepth=100;
        if(ki.getCutDepth()!=0){
            cutDepth= ki.getCutDepth();
        }

        ki.setSide(3);
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
        lastToothOrExtraCut = ki.getLastBitting();//默认值
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide() + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切   值为0
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothMark   //齿号代码  其实都是零
                + 0 + ","  // 鼻部长度
                + 0 + ","  //槽宽
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
        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        ki.setSide(3);  //设置side 为3
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
        lastToothOrExtraCut= ki.getLastBitting();
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide()+ ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码
                + 0 + ","  // 鼻部长度
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
                +detectionMode;  //切割钥匙的检测方式
        return order;
    }


    /**
     * 绘制钥匙图案
     * @param canvas
     */

  private void drawKeyPattern(Canvas canvas ){
      //图案宽度
      int   patternRightArcValue= (int)(patternBodyHeight*0.05f);

        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){  //肩部
            int patternWidth=(patternLeftWidth+patternRightWidth)+(int)patternMiddleWidth;
            int   patternBodyPercent5_y=patternRightArcValue;
                mPath.moveTo(0,4);
            int shoulderArcValue=(int)(patternShoulderWidth*0.2f);
               mPath.lineTo(patternShoulderWidth-shoulderArcValue,4);
               mPath.quadTo(patternShoulderWidth,4,patternShoulderWidth,4+shoulderArcValue);
               mPath.lineTo(patternShoulderWidth,patternShoulderHeight+4);
               mPath.lineTo(patternShoulderWidth+patternLocationGrooveWidth,patternShoulderHeight+4);
               mPath.lineTo(patternLeftWidth,extraTopY);
               mPath.lineTo(patternWidth-patternRightArcValue,extraTopY);
             mPath.quadTo(patternWidth,extraTopY,patternWidth,extraTopY+patternRightArcValue);
              mPath.lineTo(patternWidth,patternBodyMaxY-patternRightArcValue);
              mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth-patternRightArcValue,patternBodyMaxY);
              mPath.lineTo(patternLeftWidth,patternBodyMaxY);
              mPath.lineTo(patternShoulderWidth+patternLocationGrooveWidth,patternBodyMaxY- percent7_y);
              mPath.lineTo(patternShoulderWidth,patternBodyMaxY- percent7_y);
              mPath.lineTo(patternShoulderWidth,(patternBodyMaxY+(extraTopY-4))-shoulderArcValue);
             mPath.quadTo(patternShoulderWidth,patternBodyMaxY+(extraTopY-4),patternShoulderWidth-shoulderArcValue,patternBodyMaxY+(extraTopY-4));
              mPath.lineTo(0,patternBodyMaxY+(extraTopY-4));
              canvas.drawPath(mPath,mBorderPaint);
              canvas.drawPath(mPath,mInsideColorPaint);
            mPath.reset();
            mPath.moveTo(0,4);
            mPath.lineTo(patternShoulderWidth-shoulderArcValue,4);
            mPath.quadTo(patternShoulderWidth,4,patternShoulderWidth,4+shoulderArcValue);
            mPath.lineTo(patternShoulderWidth,patternShoulderHeight+4);
            mPath.lineTo(patternShoulderWidth+patternLocationGrooveWidth,patternShoulderHeight+4);
            mPath.lineTo(patternLeftWidth,extraTopY);
            //画齿距  第一组
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                if(toothDepthNameOne[i].contains(".")){
                    //分割齿的深度名
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    Integer depthValue=depthPositionMapOne.get(newStr[0]);
                    float decimals=Float.parseFloat("0." + newStr[1]);
                    float valueY;
                    int distanceY;
                    if(depthValue==null){
                        if(newStr[0].equals("X")||newStr[0].equals("0")){
                            depthValue=depthPositionMapOne.get(depthNameOne[0]);
                            distanceY = depthValue - extraTopY;
                            valueY = distanceY * decimals;
                            mPath.lineTo(toothDistanceOne[i] - halfToothWidthOne[i], extraTopY + valueY);
                            mPath.lineTo(toothDistanceOne[i] + halfToothWidthOne[i], extraTopY + valueY);
                        }
                    } else {
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
                        mPath.lineTo(toothDistanceOne[i]- halfToothWidthOne[i], depthValue + valueY);
                        mPath.lineTo(toothDistanceOne[i]+ halfToothWidthOne[i], depthValue + valueY);
                    }
                }else {
                    Integer depthValue=depthPositionMapOne.get(toothDepthNameOne[i]);
                    mPath.lineTo(toothDistanceOne[i]- halfToothWidthOne[i],depthValue==null?extraTopY: depthValue);
                    mPath.lineTo(toothDistanceOne[i]+ halfToothWidthOne[i],depthValue==null?extraTopY: depthValue);
                }
            }
            //图案的中心Y
            float patternCentre_y=(float) (patternBodyHeight/2)+extraTopY;

            mPath.lineTo(patternWidth,patternCentre_y-patternBodyPercent5_y);
            mPath.lineTo(patternWidth,patternCentre_y+patternBodyPercent5_y);
            //画齿距  第二组
            for (int i = toothDepthNameTwo.length-1; i >=0; i--) {
             if (toothDepthNameTwo[i].contains(".")) {
                    String[] newStr = toothDepthNameTwo[i].split("\\.");
                    Integer depthValue=depthPositionMapTwo.get(newStr[0]);
                 //转为小数
                    float decimals=Float.parseFloat("0."+ newStr[1]);
                    float valueY;
                    int distanceY;
                 if(depthValue==null){
                     if(newStr[0].equals("X")||newStr[0].equals("0")){
                         depthValue=depthPositionMapTwo.get(depthNameTwo[0]);
                         distanceY =patternBodyMaxY-depthValue;
                         valueY =distanceY*decimals;
                         mPath.lineTo(toothDistanceTwo[i]+halfToothWidthTwo[i],patternBodyMaxY+valueY);
                         mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],patternBodyMaxY+valueY);
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
                     mPath.lineTo(toothDistanceTwo[i]+halfToothWidthTwo[i],depthValue-valueY);
                     mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],depthValue-valueY);
                 }
                }else {
                    Integer depthValue=depthPositionMapTwo.get(toothDepthNameTwo[i]);
                    mPath.lineTo(toothDistanceTwo[i]+ halfToothWidthTwo[i],depthValue==null?patternBodyMaxY: depthValue);
                    mPath.lineTo(toothDistanceTwo[i]- halfToothWidthTwo[i],depthValue==null?patternBodyMaxY: depthValue);
                }
            }
            mPath.lineTo(patternLeftWidth,patternBodyMaxY);
            mPath.lineTo(patternShoulderWidth+patternLocationGrooveWidth,patternBodyMaxY- percent7_y);
            mPath.lineTo(patternShoulderWidth,patternBodyMaxY- percent7_y);
            mPath.lineTo(patternShoulderWidth,(patternBodyMaxY+(extraTopY-4))-shoulderArcValue);
            mPath.quadTo(patternShoulderWidth,patternBodyMaxY+(extraTopY-4),patternShoulderWidth-shoulderArcValue,patternBodyMaxY+(extraTopY-4));
            mPath.lineTo(0,patternBodyMaxY+(extraTopY-4));
        }else {     //前端
            mPath.moveTo(0, extraTopY);
            mPath.lineTo(patternLeftWidth, extraTopY);
            int patternWidth = (patternLeftWidth + patternRightWidth) + (int) (patternMiddleWidth + patternExtraCutWidth);
            mPath.lineTo(patternWidth - patternRightArcValue, extraTopY);
            mPath.quadTo(patternWidth, extraTopY, patternWidth, extraTopY + patternRightArcValue);
            mPath.lineTo(patternWidth, patternBodyMaxY - patternRightArcValue);
            mPath.quadTo(patternWidth, patternBodyMaxY, patternWidth - patternRightArcValue, patternBodyMaxY);
            mPath.lineTo(0, patternBodyMaxY);
            canvas.drawPath(mPath, mBorderPaint);
            canvas.drawPath(mPath, mInsideColorPaint);
            mPath.reset();
            //画有齿代码的样子
            mPath.moveTo(0, extraTopY);
            mPath.lineTo(patternLeftWidth, extraTopY);
            int patternBodyPercent5_y = patternRightArcValue;
            int patternExtraCutHeight = (int) (patternBodyHeight * 0.18);
            int patternExtraCutArc = (int) (patternExtraCutHeight * 0.2);
            if (ki.getLastBitting() != 0) {
                mPath.lineTo(patternLeftWidth, (extraTopY + patternExtraCutHeight) - patternExtraCutArc);
                mPath.quadTo(patternLeftWidth, extraTopY + patternExtraCutHeight, patternLeftWidth + patternExtraCutArc, extraTopY + patternExtraCutHeight);
                mPath.lineTo(patternLeftWidth + patternExtraCutWidth, extraTopY + patternExtraCutHeight);
            }
            //画齿距  第一组
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                if(toothDepthNameOne[i].contains(".")){
                    //分割齿的深度名
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    Integer depthValue = depthPositionMapOne.get(newStr[0]);
                    float decimals = Float.parseFloat("0." + newStr[1]);
                    float valueY;
                    int distanceY;
                    if (depthValue == null) {
                        if (newStr[0].equals("X") || newStr[0].equals("0")) {
                            depthValue = depthPositionMapOne.get(depthNameOne[0]);
                            distanceY = depthValue - extraTopY;
                            valueY = distanceY * decimals;
                            mPath.lineTo(toothDistanceOne[i] - halfToothWidthOne[i], extraTopY + valueY);
                            mPath.lineTo(toothDistanceOne[i] + halfToothWidthOne[i], extraTopY + valueY);
                        }
                    } else {
                        valueY = 0;
                        if (depthValue == depthPositionOne[depthPositionOne.length - 1]) {
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionOne.length; j++) {
                                if (depthValue == depthPositionOne[j]) {
                                    distanceY = depthPositionOne[j + 1] - depthValue;
                                    valueY = distanceY * decimals;
                                    break;
                                }
                            }
                        }
                        mPath.lineTo(toothDistanceOne[i] - halfToothWidthOne[i], depthValue + valueY);
                        mPath.lineTo(toothDistanceOne[i] + halfToothWidthOne[i], depthValue + valueY);
                    }
                }else {
                    Integer depthValue = depthPositionMapOne.get(toothDepthNameOne[i]);
                    mPath.lineTo(toothDistanceOne[i] - halfToothWidthOne[i], depthValue == null ? extraTopY : depthValue);
                    mPath.lineTo(toothDistanceOne[i] + halfToothWidthOne[i], depthValue == null ? extraTopY : depthValue);
                }//分割齿的深度名
            }
            //图案的中心Y
            float patternCentre_y=(float) (patternBodyHeight/2)+extraTopY;

            mPath.lineTo(patternWidth,patternCentre_y-patternBodyPercent5_y);
            mPath.lineTo(patternWidth,patternCentre_y+patternBodyPercent5_y);
            //画齿距  第二组
            for (int i = toothDepthNameTwo.length-1; i >=0; i--) {
                if (toothDepthNameTwo[i].contains(".")) {
                    String[] newStr = toothDepthNameTwo[i].split("\\.");
                    Integer depthValue=depthPositionMapTwo.get(newStr[0]);
                    //转为小数
                    float decimals=Float.parseFloat("0."+ newStr[1]);
                    float valueY;
                    int distanceY;
                    if(depthValue==null){
                        if(newStr[0].equals("X")||newStr[0].equals("0")){
                            depthValue=depthPositionMapTwo.get(depthNameTwo[0]);
                            distanceY =patternBodyMaxY-depthValue;
                            valueY =distanceY*decimals;
                            mPath.lineTo(toothDistanceTwo[i]+halfToothWidthTwo[i],patternBodyMaxY+valueY);
                            mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],patternBodyMaxY+valueY);
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
                        mPath.lineTo(toothDistanceTwo[i]+halfToothWidthTwo[i],depthValue-valueY);
                        mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],depthValue-valueY);
                    }
                }else {
                    Integer depthValue=depthPositionMapTwo.get(toothDepthNameTwo[i]);
                    mPath.lineTo(toothDistanceTwo[i]+ halfToothWidthTwo[i],depthValue==null?patternBodyMaxY: depthValue);
                    mPath.lineTo(toothDistanceTwo[i]- halfToothWidthTwo[i],depthValue==null?patternBodyMaxY: depthValue);
                }
            }
            if(ki.getLastBitting()!=0){
                mPath.lineTo(patternLeftWidth+patternExtraCutWidth,patternBodyMaxY-patternExtraCutHeight);
                mPath.lineTo(patternLeftWidth+patternExtraCutArc,patternBodyMaxY-patternExtraCutHeight);
                mPath.quadTo(patternLeftWidth,patternBodyMaxY-patternExtraCutHeight,patternLeftWidth,(patternBodyMaxY-patternExtraCutHeight)+patternExtraCutArc);
                mPath.lineTo(patternLeftWidth,patternBodyMaxY);
                mPath.lineTo(0,patternBodyMaxY);
            }else {
                mPath.lineTo(patternLeftWidth,patternBodyMaxY);
                mPath.lineTo(0,patternBodyMaxY);
            }
        }
      canvas.drawPath(mPath,mBorderPaint);
      canvas.drawPath(mPath,mKeyAppearanceColorPaint);
      mPath.reset();
        if(isDraw){
           this.drawDepthPattern(canvas);
            this.drawToothCodePattern(canvas);
        }

  }
  private void drawDepthPattern(Canvas canvas){
      int start_x=patternLeftWidth+patternExtraCutWidth;
      int end_x=start_x+(int)patternMiddleWidth;
      for (int i = 0; i <depthPositionOne.length ; i++) {
          mPath.moveTo(start_x,depthPositionOne[i]);
          mPath.lineTo(end_x,depthPositionOne[i]);
      }
      for (int i = 0; i <depthPositionTwo.length ; i++) {
          mPath.moveTo(start_x,depthPositionTwo[i]);
          mPath.lineTo(end_x,depthPositionTwo[i]);
      }
      canvas.drawPath(mPath,mDashedPaint);
      mPath.reset();
  }
  private void  drawToothCodePattern(Canvas canvas){
      //画第一组齿位
      for (int i = 0; i < toothDepthNameOne.length; i++) {
          mPath.moveTo(toothDistanceOne[i], depthPositionStartYOne);
          mPath.lineTo(toothDistanceOne[i], depthPositionEndYOne);

          if(toothDepthNameOne[i].contains(".")){
              mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
              String[] newStr=toothDepthNameOne[i].split("\\.");
              int num=Integer.parseInt(newStr[1]);
              if(depthPositionMapOne.get(newStr[0])==null){
                  if(newStr[0].equals("X")||newStr[0].equals("0")){
                      if(num>=5){
                          canvas.drawText(depthNameOne[0], toothDistanceOne[i], extraTopY -10, mColorTextPaint);//画红色的字
                      }else {
                          canvas.drawText(newStr[0], toothDistanceOne[i], extraTopY -10 , mColorTextPaint);//画红色的字
                      }
                  }
              }else {
                  if(num>=5){
                      if(newStr[0].equals(depthNameOne[depthNameOne.length-1])){
                          canvas.drawText(newStr[0], toothDistanceOne[i], extraTopY -10 , mColorTextPaint);//画红色的字
                      }else {
                          for (int j = 0; j <depthNameOne.length ; j++) {
                              if(depthNameOne[j].equals(newStr[0])){
                                  canvas.drawText(depthNameOne[j+1], toothDistanceOne[i], extraTopY  -10, mColorTextPaint);//画红色的字
                                  break; //跳出循环
                              }
                          }
                      }
                  }else {
                      canvas.drawText(newStr[0], toothDistanceOne[i],extraTopY -10, mColorTextPaint);//画红色的字
                  }
              }
          }else {//不包涵画蓝色的字
              mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
              canvas.drawText(toothDepthNameOne[i],toothDistanceOne[i], extraTopY -10, mColorTextPaint);//画蓝色的字
          }
      }
      //画第二组齿位
      for (int i = 0; i < toothDepthNameTwo.length; i++) {
          mPath.moveTo(toothDistanceTwo[i], depthPositionStartYTwo);
          mPath.lineTo(toothDistanceTwo[i], depthPositionEndYTwo);
          if(toothDepthNameTwo[i].contains(".")){
              mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
              String[] newStr=toothDepthNameTwo[i].split("\\.");
              int num=Integer.parseInt(newStr[1]);
              if(depthPositionMapTwo.get(newStr[0])==null){
                   if(newStr[0].equals("X")||newStr[0].equals("0")){
                       if(num>=5){
                           canvas.drawText(depthNameTwo[0],toothDistanceTwo[i], patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                       }else {
                           canvas.drawText(newStr[0],toothDistanceTwo[i],patternBodyMaxY+ 40, mColorTextPaint);//画红色的字
                       }
                   }else {
                       canvas.drawText(newStr[0],toothDistanceTwo[i],patternBodyMaxY+ 40, mColorTextPaint);
                   }
              }else {
                  if(num>=5){
                      if(newStr[0].equals(depthNameTwo[depthNameTwo.length-1])){
                          canvas.drawText(newStr[0],toothDistanceTwo[i], patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                      }else {
                          for (int j = 0; j <depthNameTwo.length ; j++) {
                              if(depthNameTwo[j].equals(newStr[0])){
                                  canvas.drawText(depthNameTwo[j+1], toothDistanceTwo[i], patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                                  break; //跳出循环
                              }
                          }
                      }
                  }else {
                      canvas.drawText(newStr[0], toothDistanceTwo[i],patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                  }
              }
          }else {//不包涵画蓝色的字
              mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
              canvas.drawText(toothDepthNameTwo[i],toothDistanceTwo[i], patternBodyMaxY+40, mColorTextPaint);//画蓝色的字
          }
      }
      canvas.drawPath(mPath, mDashedPaint);//画X轴方向的。
      mPath.reset();  //重置
  }

    /**
     * 自定义绘制锯齿状
     */
    public void customDrawSerrated(){
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
        if(isShowArrows){
            if(ki.getAlign()==0){
                //画红色箭头  肩部
                mPath.moveTo(90, 20);  //100
                mPath.lineTo(120, 20);//第一条  100
                mPath.lineTo(120, 12);
                mPath.lineTo(130, 24);//中间点  104
                mPath.lineTo(120, 36);
                mPath.lineTo(120, 28);
                mPath.lineTo(90, 28);
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }else if(ki.getAlign()==1){
                //画红色箭头  尖端
                mPath.moveTo(666, 30);//80
                mPath.lineTo(666 + 10, 20);//70
                mPath.lineTo(666 + 10, 27);//77
                mPath.lineTo(666 + 35, 27);//77
                mPath.lineTo(666 + 35, 33);//83
                mPath.lineTo(666 + 10, 33);//83
                mPath.lineTo(666 + 10, 40);//90
                mPath.lineTo(666, 30);//80
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);
       setMeasuredDimension(width,height);
    }
    private void initPaintAndPath() {
        mPath = new Path();
        mBorderPaint = new Paint();
        mKeyAppearanceColorPaint = new Paint();
        mDashedPaint = new Paint();
        mColorTextPaint = new Paint();
        mArrowsPaint = new Paint();
        mInsideColorPaint = new Paint();
        mInsideColorPaint = new Paint();
        //画钥匙形状属性
        mBorderPaint.setAntiAlias(true);//去掉抗锯齿
        mBorderPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        mBorderPaint.setStrokeWidth(2);
        //填充钥匙身体颜色的笔
        mKeyAppearanceColorPaint.setColor(Color.parseColor("#BABABA"));
        mKeyAppearanceColorPaint.setAntiAlias(true);
        mKeyAppearanceColorPaint.setStyle(Paint.Style.FILL);
        mKeyAppearanceColorPaint.setStrokeWidth(1);
        //画虚线的笔的属性
        mDashedPaint.setAntiAlias(true);//去掉抗锯齿
        mDashedPaint.setColor(Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        mDashedPaint.setStyle(Paint.Style.STROKE);//设置画笔描边
        mDashedPaint.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        mDashedPaint.setPathEffect(effects);
        //画蓝色字体的笔
        mColorTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mColorTextPaint.setTextSize(40);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //填充钥匙内槽颜色的笔
        mInsideColorPaint.setAntiAlias(true);//去掉锯齿
        mInsideColorPaint.setColor(Color.parseColor("#cc9900")); //黄色
        mInsideColorPaint.setStyle(Paint.Style.FILL);
        mInsideColorPaint.setStrokeWidth(1);
    }
}
