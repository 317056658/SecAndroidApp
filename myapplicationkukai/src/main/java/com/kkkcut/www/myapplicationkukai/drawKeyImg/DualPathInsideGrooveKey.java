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
 * 双轨内槽钥匙
 * Created by Administrator on 2017/7/17.
 */

public class DualPathInsideGrooveKey extends Key{
    private Path mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;
    private Paint mColorTextPaint;  //颜色字体画笔
    private Paint mArrowsPaint;
    private Paint mInsideGrooveColorPaint;
    private KeyInfo ki;
    private float[] toothDistanceOne, toothDistanceTwo;
    private String[] toothDepthNameOne, toothDepthNameTwo;
    private int  innerCutLengthX;
    private int initDepthPositionOne, initDepthPositionTwo;
    private int[] depthPositionOne, depthPositionTwo;
    //定义2个hasaMap集合  保存每组齿的深度位子
    private Map<String, Integer> depthPositionMapOne, depthPositionMapTwo;
    private int depthPositionStartYOne, depthPositionEndYOne, depthPositionStartYTwo, depthPositionEndYTwo;
    private ArrayList<String[]> toothCodeList;
    private float spacesScaleValue;
    private double depthScaleValue;
    private  boolean isShowArrows=true;
    private boolean  isDraw=true;
    private float patternMiddleWidth;
    private double patternBodyHeight;
    private int patternLeftWidth, patternRightWidth;   ///图案的右边宽度,左边宽度
    private int patternLeftShoulderHeight;  //图案的左边肩部高度
    private String[] depthNameOne,depthNameTwo;
    private int[] halfToothWidthOne,halfToothWidthTwo;
    private int patternBodyMaxY;
    private  int extraTopY;
    private int noseX;
    private int patternExtraWidth;
    private Context mContext;


    public DualPathInsideGrooveKey(Context context,KeyInfo ki) {
        this(context,null,ki);
    }

    public DualPathInsideGrooveKey(Context context, AttributeSet attrs,KeyInfo ki) {
       this(context,attrs,0,ki);
    }
    public DualPathInsideGrooveKey(Context context, AttributeSet attrs,int defStyleAttr,KeyInfo ki) {
        super(context,attrs,defStyleAttr);
        this.ki=ki;
        mContext=context;
        depthPositionMapOne =new HashMap<>();
        depthPositionMapTwo =new HashMap<>();
        toothCodeList =new ArrayList<>();
        this.initPaintAndPath();
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
        patternMiddleWidth=(int)(width*0.67f);
        patternLeftWidth=(int)(width*0.15f);
        patternExtraWidth=(int)(width*0.05f);
        patternRightWidth=(int)(width*0.13f);
        patternLeftShoulderHeight=(int)(height*0.13f);
        patternBodyHeight =(int)(height*0.68f);
        extraTopY=4+patternLeftShoulderHeight;
        patternBodyMaxY =(int)(patternBodyHeight +extraTopY);
        this.analysisSpaceAndSpaceWidth();
        this.analysisDepthAndDepthName();
        this.calculateDrawSpecificInfo();
    }

    /**
     * 计算绘制的特殊信息
     */
    private void calculateDrawSpecificInfo(){
        noseX=(int)(ki.getNose()*spacesScaleValue);
        //计算有不有尾部长度
        innerCutLengthX =(int)(ki.getInnerCutLength()*spacesScaleValue);
    }

    /**
     * 解析 Space和 SpaceWidth
     */
    private void analysisSpaceAndSpaceWidth(){
        String[] spaceGroup=ki.getSpace().split(";");
        String[] spaceWidthGroup=ki.getSpace_width().split(";");
        String[] spaceOne=spaceGroup[0].split(",");
        String[] spaceTwo=spaceGroup[1].split(",");
        String[] spaceWidthOne=spaceWidthGroup[0].split(",");
        String[] spaceWidthTwo=spaceWidthGroup[1].split(",");
        toothDistanceOne=new float[spaceOne.length];
        toothDistanceTwo=new float[spaceTwo.length];
        //齿的深度名
        toothDepthNameOne=new String[spaceOne.length];
        toothDepthNameTwo=new String[spaceTwo.length];
        halfToothWidthOne=new int[spaceWidthOne.length];
        halfToothWidthTwo=new int[spaceWidthTwo.length];
        for (int i = 0; i <toothDistanceOne.length ; i++) {
            toothDistanceOne[i]=Float.parseFloat(spaceOne[i]); //转为float
            halfToothWidthOne[i]=Integer.parseInt(spaceWidthOne[i]);        }
        for (int i = 0; i <toothDistanceTwo.length ; i++) {
            toothDistanceTwo[i]=Float.parseFloat(spaceTwo[i]);
            halfToothWidthTwo[i]=Integer.parseInt(spaceWidthTwo[i]);
        }
        this.calculateDrawToothDistanceAndToothWidth();

    }

    /**
     * 计算绘制的齿距和齿宽
     */
    private void calculateDrawToothDistanceAndToothWidth(){
        int length;  //长度
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){  //肩部
                 if(toothDistanceOne[toothDistanceOne.length-1]>=toothDistanceTwo[toothDistanceTwo.length-1]){
                     length=(int)(toothDistanceOne[toothDistanceOne.length-1] + (toothDistanceOne[toothDistanceOne.length-1]-toothDistanceOne[toothDistanceOne.length-2]));
                 }else {
                     length=(int)(toothDistanceTwo[toothDistanceTwo.length-1] + (toothDistanceTwo[toothDistanceTwo.length-1]-toothDistanceTwo[toothDistanceTwo.length-2]));
                 }
                 spacesScaleValue=patternMiddleWidth/length;
                // 一组有多少个齿距,就有多少个齿宽
                for (int i = 0; i <toothDistanceOne.length ; i++) {
                    toothDistanceOne[i]=(toothDistanceOne[i]*spacesScaleValue)+(patternLeftWidth+patternExtraWidth);
                    halfToothWidthOne[i]=((int)(halfToothWidthOne[i]*spacesScaleValue)/2);
                }
                for (int i = 0; i <toothDistanceTwo.length ; i++) {
                    toothDistanceTwo[i]=(toothDistanceTwo[i]*spacesScaleValue)+(patternLeftWidth+patternExtraWidth);
                    halfToothWidthTwo[i]=((int)(halfToothWidthTwo[i]*spacesScaleValue)/2);
                }
            }else {//前端
                if(toothDistanceOne[0]>=toothDistanceTwo[0]){
                    length=(int)(toothDistanceOne[0] + (toothDistanceOne[0]-toothDistanceOne[1]));
                }else {
                    length=(int)(toothDistanceTwo[0] + (toothDistanceTwo[0]-toothDistanceTwo[1]));
                }
                spacesScaleValue=patternMiddleWidth/length;
                int sumX = (int) (patternMiddleWidth+patternLeftWidth)+patternExtraWidth;
                // 一组有多少个齿距,就有多少个齿宽
                for (int i = 0; i <toothDistanceOne.length ; i++) {
                    toothDistanceOne[i]=sumX-(toothDistanceOne[i]*spacesScaleValue);
                    halfToothWidthOne[i]=((int)(halfToothWidthOne[i]*spacesScaleValue)/2);
                }
                for (int i = 0; i <toothDistanceTwo.length ; i++) {
                    toothDistanceTwo[i]=sumX-(toothDistanceTwo[i]*spacesScaleValue);
                    halfToothWidthTwo[i]=((int)(halfToothWidthTwo[i]*spacesScaleValue)/2);
                }

            }
    }

    /**
     * 解析深度和深度名
     */
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
        //转为int
        for (int i = 0; i < depthPositionOne.length; i++) {
            depthPositionOne[i]=Integer.parseInt(depthOne[i]);
        }
        for (int i = 0; i < depthPositionTwo.length; i++) {
            depthPositionTwo[i]=Integer.parseInt(depthTwo[i]);
        }
        this.calculateDrawDepth();
    }

    /**
     * 计算绘制的深度
     */
    private void calculateDrawDepth(){
        depthScaleValue=patternBodyHeight/ki.getWidth();
        //第一组深度
        for (int i = 0; i < depthPositionOne.length ; i++) {
            depthPositionOne[i]=(int)(depthPositionOne[i]* depthScaleValue)+extraTopY;
            //根据第一组的深度名保存换算好的深度位子
            depthPositionMapOne.put(depthNameOne[i], depthPositionOne[i]);
        }
        depthPositionStartYOne = depthPositionOne[0];
        depthPositionEndYOne = depthPositionOne[depthPositionOne.length-1];
        //第二组深度
        for (int i = 0; i < depthPositionTwo.length ; i++) {
            depthPositionTwo[i]= patternBodyMaxY -(int)(depthPositionTwo[i]* depthScaleValue);
            //根据第二组的深度名保存换算好的深度位子
            depthPositionMapTwo.put(depthNameTwo[i], depthPositionTwo[i]);
        }
        depthPositionStartYTwo = depthPositionTwo[0];
        depthPositionEndYTwo = depthPositionTwo[depthPositionTwo.length-1];
       // 附加的Y
        int additionalY=(int)(patternBodyHeight *0.07);
        //判断第一组深度,第一位深度 是最大 还是最小
        if(depthPositionOne[0]> depthPositionOne[depthPositionOne.length-1]){
            initDepthPositionOne = depthPositionOne[0]+additionalY;

        }else {
            if(depthPositionOne[0]-additionalY<extraTopY){
                initDepthPositionOne=extraTopY;
            }else {
                initDepthPositionOne = depthPositionOne[0]-additionalY;
            }
        }
        //判断第二组深度,第一位深度 是最大 还是最小
        if(depthPositionTwo[0]< depthPositionTwo[depthPositionTwo.length-1]){
            initDepthPositionTwo = depthPositionTwo[0]-additionalY;
        }else {
            if(depthPositionTwo[0]+additionalY>patternBodyMaxY){
                initDepthPositionTwo=patternBodyMaxY;
            }else {
                initDepthPositionTwo = depthPositionTwo[0]+additionalY;
            }
        }
    }

    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
        this.isDraw=isDraw;
    }
    /**
     * 获得返回所有齿的深度名
     * @param toothCode
     */
    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
            // 分割  获得全部齿的深度名
           String[] newStr = toothCode.split(",");
            int j=-1;
            for (int i = 0; i < newStr.length ; i++) {
                if(i< toothDepthNameOne.length){
                    toothDepthNameOne[i]= newStr[i];
                }else {
                    j++;
                    toothDepthNameTwo[j]= newStr[i];
                }
            }
        }else {
            for (int i = 0; i <toothDepthNameOne.length ; i++) {
                toothDepthNameOne[i]="X";
            }
            for (int i = 0; i <toothDepthNameTwo.length ; i++) {
                toothDepthNameTwo[i]="X";
            }
        }

    }

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

    /**
     * 默认值 是X
     */
    @Override
    public void setToothCodeDefault() {
        for (int i = 0; i <toothDepthNameOne.length ; i++) {
            toothDepthNameOne[i]="X";
        }
        for (int i = 0; i <toothDepthNameTwo.length ; i++) {
            toothDepthNameTwo[i]="X";
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
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int side;
        int nose;
        if(ki.getNose()==0){
            nose=180;
        }else {
            nose= ki.getNose();
        }
        if(ki.getSide()==0){
            side=3;
        }else {
            side= ki.getSide();
        }
        if(ki.getCutDepth()==0){  //等于0 就设置默认值为100
           ki.setCutDepth(100);
        }
        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        if(ki.getSide()==4){
            toothQuantity=toothQuantity+spaceGroup[1].split(",").length+",";
        }
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
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + side + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getInnerCutLength()+ ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + ki.getCutDepth() + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码  其实都是零
                + nose + ","  // 鼻部长度
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
        int noseCut;
        String DDepth="";
        int side;
        int nose;
        if(ki.getNose()==0){
            nose=180;
        }else {
            nose= ki.getNose();
        }
        if(ki.getSide()==0){
            side=3;
        }else {
            side= ki.getSide();
        }
            noseCut=1;  //1为切割鼻部

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
                + side+ ","// 有效边
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
                + nose + ","  // 鼻部长度
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

    /**
     * 绘制钥匙图案
     * @param canvas
     */
    private void  drawKeyPattern(Canvas canvas){
        int  patternWidth=patternLeftWidth+(int)((patternMiddleWidth+patternRightWidth)+patternExtraWidth);
        int   patternRightArcValue= (int)(patternBodyHeight*0.05f);
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){   //肩部
            mPath.moveTo(0,4);
            int shoulderArcValue =(int)(patternLeftShoulderHeight*0.25f);
            mPath.lineTo(patternLeftWidth- shoulderArcValue,4);
            mPath.quadTo(patternLeftWidth,4,patternLeftWidth, shoulderArcValue +4);
            mPath.lineTo(patternLeftWidth,extraTopY);
            mPath.lineTo(patternWidth-patternRightArcValue,extraTopY);
            mPath.quadTo(patternWidth,extraTopY,patternWidth,patternRightArcValue+extraTopY);
            mPath.lineTo(patternWidth,patternBodyMaxY-patternRightArcValue);
            mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth-patternRightArcValue,patternBodyMaxY);
            mPath.lineTo(patternLeftWidth,patternBodyMaxY);
            mPath.lineTo(patternLeftWidth,(patternBodyMaxY+patternLeftShoulderHeight)-shoulderArcValue);
            mPath.quadTo(patternLeftWidth,patternBodyMaxY+patternLeftShoulderHeight,patternLeftWidth-shoulderArcValue,patternBodyMaxY+patternLeftShoulderHeight);
            mPath.lineTo(0,patternBodyMaxY+patternLeftShoulderHeight);
        }else {     //前端
            mPath.moveTo(0,extraTopY);
            mPath.lineTo(patternWidth-patternRightArcValue,extraTopY);
            mPath.quadTo(patternWidth,extraTopY,patternWidth,extraTopY+patternRightArcValue);
            mPath.lineTo(patternWidth,patternBodyMaxY-patternRightArcValue);
            mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth-patternRightArcValue,patternBodyMaxY);
            mPath.lineTo(0,patternBodyMaxY);
        }

        canvas.drawPath(mPath, mBorderPaint);
        canvas.drawPath(mPath, mKeyAppearanceColorPaint);
        mPath.reset();
        mPath.moveTo(patternWidth,extraTopY+patternRightArcValue);
        mPath.quadTo(patternWidth,extraTopY,patternWidth-patternRightArcValue,extraTopY);
        mPath.lineTo((patternWidth-patternRightArcValue)-noseX,extraTopY);
        for (int i = toothDistanceOne.length-1; i >=0; i--) {
            if (toothDepthNameOne[i].equals("X")) {
               mPath.lineTo(toothDistanceOne[i]+halfToothWidthOne[i],initDepthPositionOne);
                mPath.lineTo(toothDistanceOne[i]-halfToothWidthOne[i],initDepthPositionOne);
            } else if (toothDepthNameOne[i].contains(".")) {
                //分割齿的深度名
                String[] newStr = toothDepthNameOne[i].split("\\.");
                int depthValue;
                float decimals;
                float valueY;
                int distanceY;
                if(newStr[0].equals("X")||depthPositionMapOne.get(newStr[0])==null){
                    depthValue=depthPositionMapOne.get(depthNameOne[0]);
                    decimals=Float.parseFloat("0."+ newStr[1]);
                    distanceY =depthValue-initDepthPositionOne;
                    valueY =distanceY*decimals;
                    mPath.lineTo(toothDistanceOne[i]+halfToothWidthOne[i],initDepthPositionOne+ valueY);
                    mPath.lineTo(toothDistanceOne[i]- halfToothWidthOne[i],initDepthPositionOne+ valueY);
                }else {
                    depthValue= depthPositionMapOne.get(newStr[0]);
                      decimals= Float.parseFloat("0."+ newStr[1]);
                    valueY=0;
                    if(depthValue == depthPositionOne[depthPositionOne.length-1]){
                        //等于最后一个深度不计算。
                    }else {
                        for(int j = 0; j< depthPositionOne.length; j++){
                            if(depthValue == depthPositionOne[j]){
                                distanceY = depthPositionOne[j+1]- depthValue;
                                valueY=distanceY*decimals;
                                break;
                            }
                        }
                    }
                    mPath.lineTo(toothDistanceOne[i]+halfToothWidthOne[i],depthValue+valueY);
                    mPath.lineTo(toothDistanceOne[i]-halfToothWidthOne[i],depthValue+valueY);
                }
            }else {
                mPath.lineTo(toothDistanceOne[i]+ halfToothWidthOne[i], depthPositionMapOne.get(toothDepthNameOne[i])==null?initDepthPositionOne: depthPositionMapOne.get(toothDepthNameOne[i]));
                mPath.lineTo(toothDistanceOne[i]- halfToothWidthOne[i], depthPositionMapOne.get(toothDepthNameOne[i])==null?initDepthPositionOne: depthPositionMapOne.get(toothDepthNameOne[i]));
            }
        }
       int patternCentreY=extraTopY+(int)(patternBodyHeight/2);
        //百分之5
        int percent5Y=(int)(patternBodyHeight*0.06);
        if(innerCutLengthX<=0){
            mPath.lineTo(patternLeftWidth+patternExtraWidth,patternCentreY-percent5Y);
            mPath.quadTo((patternLeftWidth+patternExtraWidth)-6,patternCentreY,patternLeftWidth+patternExtraWidth,patternCentreY+percent5Y);
        }else {
            int x=patternLeftWidth-innerCutLengthX;
            mPath.lineTo(x,patternCentreY-percent5Y);
            mPath.quadTo(x-9,patternCentreY,x,patternCentreY+percent5Y);
        }
        //计算第二组的
        for (int i = 0; i < toothDistanceTwo.length ; i++) {
            if (toothDepthNameTwo[i].equals("X")) {
                mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],initDepthPositionTwo);
                mPath.lineTo(toothDistanceTwo[i]+halfToothWidthTwo[i],initDepthPositionTwo);
            } else if (toothDepthNameTwo[i].contains(".")) {
                //分割齿的深度名
                String[] newStr = toothDepthNameTwo[i].split("\\.");
                int depthValue;
                float decimals;
                float valueY;
                int distanceY;
                if(newStr[0].equals("X")){
                    depthValue=depthPositionMapTwo.get(depthNameTwo[0]);
                    decimals=Float.parseFloat("0."+ newStr[1]);
                    distanceY =depthValue-initDepthPositionTwo;
                    valueY =distanceY*decimals;
                    mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],initDepthPositionTwo+ valueY);
                    mPath.lineTo(toothDistanceTwo[i]+ halfToothWidthTwo[i],initDepthPositionTwo+ valueY);
                }else  if(depthPositionMapOne.get(newStr[0])==null){
                    mPath.lineTo(toothDistanceOne[i]-halfToothWidthOne[i],initDepthPositionTwo);
                    mPath.lineTo(toothDistanceOne[i]+halfToothWidthOne[i],initDepthPositionTwo);
                }else {
                    depthValue= depthPositionMapTwo.get(newStr[0]);
                    decimals= Float.parseFloat("0."+ newStr[1]);
                    valueY=0;
                    if(depthValue == depthPositionTwo[depthPositionTwo.length-1]){
                        //等于最后一个深度不计算。
                    }else {
                        for(int j = 0; j< depthPositionTwo.length; j++){
                            if(depthValue == depthPositionTwo[j]){
                                distanceY = depthPositionTwo[j+1]- depthValue;
                                valueY=distanceY*decimals;
                                break;
                            }
                        }
                    }
                    mPath.lineTo(toothDistanceTwo[i]-halfToothWidthTwo[i],depthValue+valueY);
                    mPath.lineTo(toothDistanceTwo[i]+halfToothWidthTwo[i],depthValue+valueY);
                }
            }else {
                Integer depthValue=depthPositionMapTwo.get(toothDepthNameTwo[i]);
                mPath.lineTo(toothDistanceTwo[i]- halfToothWidthTwo[i],depthValue==null?initDepthPositionTwo: depthValue);
                mPath.lineTo(toothDistanceTwo[i]+ halfToothWidthTwo[i],depthValue==null?initDepthPositionTwo: depthValue);
            }
        }
        mPath.lineTo((patternWidth-patternRightArcValue)-noseX,patternBodyMaxY);
        mPath.lineTo(patternWidth-patternRightArcValue,patternBodyMaxY);
        mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth,(patternBodyMaxY-patternRightArcValue));
        canvas.drawPath(mPath,mBorderPaint);
        canvas.drawPath(mPath,mInsideGrooveColorPaint);  //内槽颜色
        mPath.reset();
        if(isDraw){
            this.drawDepthPattern(canvas);
            this.drawToothCodePattern(canvas);
        }
    }
    /**
     * 绘制深度图案
     * @param canvas
     */
    private  void drawDepthPattern(Canvas canvas){
        int start_x=patternLeftWidth+patternExtraWidth;
        int end_x=(patternLeftWidth+patternExtraWidth)+(int)patternMiddleWidth;
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
    /**
     * 绘制齿代码图案
     * @param canvas
     */
    private  void  drawToothCodePattern(Canvas canvas){
        //画第一组齿位
        for (int i = 0; i < toothDepthNameOne.length; i++) {
            mPath.moveTo(toothDistanceOne[i], depthPositionStartYOne);
            mPath.lineTo(toothDistanceOne[i], depthPositionEndYOne);
            if(toothDepthNameOne[i].contains(".")){
                mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                String[] newStr=toothDepthNameOne[i].split("\\.");
                if(newStr[0].equals("X")){
                    if(Integer.parseInt(newStr[1])>=5){
                        canvas.drawText(depthNameOne[0], toothDistanceOne[i], extraTopY -10, mColorTextPaint);//画红色的字
                    }else {
                        canvas.drawText(newStr[0], toothDistanceOne[i], extraTopY -10 , mColorTextPaint);//画红色的字
                    }
                }else {
                    if(Integer.parseInt(newStr[1])>=5){
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
            }else if(toothDepthNameOne.equals("X")||!toothDepthNameOne[i].contains(".")){//不包涵画蓝色的字
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
                if(newStr[0].equals("X")){
                    if(Integer.parseInt(newStr[1])>=5){
                        canvas.drawText(depthNameTwo[0],toothDistanceTwo[i], patternBodyMaxY + 40, mColorTextPaint);//画红色的字
                    }else {
                        canvas.drawText(newStr[0],toothDistanceTwo[i],patternBodyMaxY+ 40, mColorTextPaint);//画红色的字
                    }
                }else {
                    if(Integer.parseInt(newStr[1])>=5){
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
            }else if(toothDepthNameTwo.equals("X")||!toothDepthNameTwo[i].contains(".")){//不包涵画蓝色的字
                mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                canvas.drawText(toothDepthNameTwo[i],toothDistanceTwo[i], patternBodyMaxY+40, mColorTextPaint);//画蓝色的字
            }
        }
        canvas.drawPath(mPath, mDashedPaint);//画X轴方向的。
        mPath.reset();  //重置
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
        if(isShowArrows){  //为false就不显示提示箭头
            if(ki.getAlign()==0){
                //画红色箭头
                mPath.moveTo(110, 20);  //110
                mPath.lineTo(140, 20);//第一条  140
                mPath.lineTo(140, 12);
                mPath.lineTo(150, 24);//中间点  150
                mPath.lineTo(140, 36);
                mPath.lineTo(140, 28);
                mPath.lineTo(110, 28);
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }else if(ki.getAlign()==1){
                //画红色箭头
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
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }
    private void initPaintAndPath() {
        mPath = new Path();
        mBorderPaint = new Paint();
        mKeyAppearanceColorPaint = new Paint();
        mDashedPaint = new Paint();
        mColorTextPaint = new Paint();
        mArrowsPaint = new Paint();
        mInsideGrooveColorPaint = new Paint();
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
        mInsideGrooveColorPaint.setAntiAlias(true);//去掉锯齿
        mInsideGrooveColorPaint.setColor(Color.parseColor("#cc9900")); //黄色
        mInsideGrooveColorPaint.setStyle(Paint.Style.FILL);
        mInsideGrooveColorPaint.setStrokeWidth(1);
    }
}
