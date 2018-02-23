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
 * 单轨外槽钥匙
 * Created by Administrator on 2017/7/14.
 */

public class MonorailOuterGrooveKey extends Key {
    private Path  mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;
    private Paint mColorTextPaint;
    private Paint mArrowsPaint;
    private Paint mOuterGrooveColorPaint;
    private KeyInfo ki;
    private int[] toothDistance;
    private String[] toothDepthName;
    private int guideY, noseX;
    private int[] depthPosition;
    private Map<String, Integer> depthPositionMap;   //key value对 保存绘制的深度位置
    private int depthPositionStartY, depthPositionEndY;
    private ArrayList<String[]> toothCodeList;
    private boolean isShowArrows = true;  //默认为true
    private boolean isDraw=true;
    private float patternMiddleWidth;  //图案的中间宽度
    private int patternLeftWidth, patternRightWidth;   ///图案的右边宽度,左边宽度
    private double patternHeight;   //图案的高度
    private  int extraTopY, patternMaxY;
    private   int[] halfToothWidth;
    private  int ASide=0,BSide=1;
    private String[] depthNames;
    private float spacesScaleValue;
    private  double depthScaleValue;
    private  int defaultY;
    private String[] spacesWidth;


    public MonorailOuterGrooveKey(Context context, KeyInfo ki) {
        this(context,null,ki);
    }

    public MonorailOuterGrooveKey(Context context, AttributeSet attrs,KeyInfo ki) {
        this(context,attrs,0,ki);
    }

    public MonorailOuterGrooveKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.ki=ki;
        depthPositionMap =new HashMap<>();
        toothCodeList = new ArrayList<>();
        this.initPaintAndPath();
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
        patternMiddleWidth=(int)(width*0.67);
        patternLeftWidth=(int)(width*0.2);  //百分之23
        patternRightWidth=(int)(width*0.13); //百分之77
        patternHeight=(int)(height*0.84);
        extraTopY=4+(int)(height*0.14);
        patternMaxY =extraTopY+(int)patternHeight;
        this.analysisSpaceAndSpaceWidth();
        this.analysisDepthAndDepthName();
        this.calculateDrawSpecificInfo();
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

    /**
     * 解析SpaceAndSpaceWidth
     */
    private void analysisSpaceAndSpaceWidth(){
          String[] spaceGroup=ki.getSpace().split(";");
          String[] spaceWidthGroup=ki.getSpace_width().split(";");
          String[] spaces=spaceGroup[0].split(",");
         spacesWidth=spaceWidthGroup[0].split(",");
        //齿距和齿宽数量相等
        toothDistance =new int[spaces.length];
        halfToothWidth =new int[spacesWidth.length];
        toothDepthName=new String[spaces.length];
        //全部转为int
        for (int i = 0; i < toothDistance.length ; i++) {
            toothDistance[i]=Integer.parseInt(spaces[i]);
        }
        this.calculateDrawToothDistanceAndToothWidth();
    }

    /**
     * 绘制的特殊信息
     */
    private void calculateDrawSpecificInfo(){
        //鼻部
        noseX = (int) (ki.getNose()*spacesScaleValue);
        guideY= (int)(ki.getGuide()*depthScaleValue);
    }

    /**
     * 计算绘制的齿距和齿宽
     */
    private void calculateDrawToothDistanceAndToothWidth(){
        int maxToothDistance;
          if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){   //肩部
              maxToothDistance = toothDistance[toothDistance.length - 1]+260;
              spacesScaleValue=patternMiddleWidth/maxToothDistance;
              for (int i = 0; i < toothDistance.length; i++) {
                  //计算绘制的齿距
                  toothDistance[i] = (int) (toothDistance[i] * spacesScaleValue) + patternLeftWidth;
                  //计算绘制的一半齿宽  除以2
                  halfToothWidth[i]=(int)(Integer.parseInt(spacesWidth[i])*spacesScaleValue)/2;
              }
          }else {  //前端

              maxToothDistance = toothDistance[0]+260;
              spacesScaleValue=patternMiddleWidth/maxToothDistance;
              int sumX = (int) patternMiddleWidth+patternLeftWidth;
              for (int i = 0; i < toothDistance.length; i++) {
                  toothDistance[i] = sumX - (int) (toothDistance[i] * spacesScaleValue) ;
                  halfToothWidth[i]=(int)(Integer.parseInt(spacesWidth[i])*spacesScaleValue)/2;

              }
          }
    }




    /**
     * 自定义绘制锯齿状
     */
    public  void customDrawSerrated(){
        int  k=0;
        for (int i = 0; i < toothDepthName.length ; i++) {
            if(depthNames.length>=3){
                if(i>=3) {
                    toothDepthName[i] = depthNames[k];
                    k++;
                    if(k==3){
                        k=0;
                    }
                }else {
                    toothDepthName[i] = depthNames[i];
                }
            }else {
                if(i>=2){
                    toothDepthName[i]=depthNames[k];
                    k++;
                    if(k==2){
                        k=0;
                    }
                }else {
                    toothDepthName[i]=depthNames[i];
                }
            }
        }
    }

    /**
     * 解析深度和深度名
     */
    private void  analysisDepthAndDepthName(){
        String[] depthGroup=ki.getDepth().split(";");
        String[]  depthNameGroup=ki.getDepth_name().split(";");
        String[] depths=depthGroup[0].split(",");
        depthNames=depthNameGroup[0].split(",");
        depthPosition=new int[depths.length];
        //转为int类型
        for (int i = 0; i <depthPosition.length ; i++) {
            depthPosition[i]=Integer.parseInt(depths[i]);
        }
        this.calculateDrawDepth();
    }
    private  void calculateDrawDepth(){
        //计算深度的比例值
      depthScaleValue = patternHeight / ki.getWidth();
        if(ki.getSide()==ASide){
            for (int i = 0; i < depthPosition.length; i++) {
                depthPosition[i] = (int) (depthPosition[i] * depthScaleValue) + extraTopY;
                //保存计算的深度
                depthPositionMap.put(depthNames[i], depthPosition[i]);
            }
        }else if(ki.getSide()==BSide){
            for (int i = 0; i < depthPosition.length; i++) {
                depthPosition[i] = patternMaxY - (int) (depthPosition[i]* depthScaleValue);
                //保存计算的深度
                depthPositionMap.put(depthNames[i], depthPosition[i]);
            }
        }

        //获取第一个深度的位子
        depthPositionStartY = depthPosition[0];
        //获取最后一个深度的位子
        depthPositionEndY = depthPosition[depthPosition.length - 1];
    }
    /**
     * 设置钥匙属性
     *
     * @param p
     */
    public void setNeededDrawAttribute(KeyInfo p) {
    }


    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
            String[] allToothDepthName = toothCode.split(",");
            for (int i = 0; i < allToothDepthName.length; i++) {
                toothDepthName[i] = allToothDepthName[i];
            }
        }else {
          this.setToothCodeDefault();
        }

    }

    @Override
    public void redrawKey() {
        this.invalidate();  //重绘
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
        toothCodeList.add(toothDepthName);
        return toothCodeList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
        this.isShowArrows = isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String toothCode="";
        for (int i = 0; i < toothDepthName.length; i++) {
            toothDepthName[i] = "X";
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
        String toothCode = "";
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
                + toothCode   //齿号代码  其实都是零
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
       mPath.moveTo(0,extraTopY);
        int patternWidth=patternLeftWidth+(int)patternMiddleWidth+patternRightWidth;
        int  arcX = (int)(patternHeight*0.04f);
        mPath.lineTo(patternWidth-arcX,extraTopY);
        mPath.quadTo(patternWidth,extraTopY,patternWidth,arcX+extraTopY);
        mPath.lineTo(patternWidth,patternMaxY-arcX);
        mPath.quadTo(patternWidth,patternMaxY,patternWidth-arcX,patternMaxY);
        mPath.lineTo(0,patternMaxY);
        mBorderPaint.setStrokeWidth(2f);
        canvas.drawPath(mPath, mBorderPaint);
        canvas.drawPath(mPath, mKeyAppearanceColorPaint);
        mPath.reset();
        if (isShowArrows) {
            if (ki.getAlign() == 1) {   //尖端定位
                //画尖端定位红色箭头
                mPath.moveTo(666, 20);
                mPath.lineTo(666 + 10, 10);
                mPath.lineTo(666 + 10, 17);
                mPath.lineTo(666 + 35, 17);
                mPath.lineTo(666 + 35, 23);
                mPath.lineTo(666 + 10, 23);
                mPath.lineTo(666 + 10, 30);
                mPath.lineTo(666, 20);//80
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            } else if (ki.getAlign() == 0) {  //肩部定位
                //画红色箭头
                mPath.moveTo(90, 20);  //100
                mPath.lineTo(120, 20);//第一条  100
                mPath.lineTo(120, 12);
                mPath.lineTo(130, 24);//中间点  104
                mPath.lineTo(120, 36);
                mPath.lineTo(120, 28);
                mPath.lineTo(90, 28);
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }
        }
        mPath.moveTo(patternWidth,patternMaxY-arcX);
        mPath.quadTo(patternWidth,patternMaxY,patternWidth-arcX,patternMaxY);
        if(noseX!=0){
            mPath.lineTo(patternWidth-noseX,patternMaxY);
        }
        if(ki.getSide()==ASide){
            //绘制槽
            if(guideY==0){
                defaultY=depthPosition[0]+(int)(patternHeight*0.06);
                mPath.lineTo(patternLeftWidth,patternMaxY);
                mPath.lineTo(patternLeftWidth,patternMaxY);
                for (int i = 0; i <toothDepthName.length ; i++) {
                    if(toothDepthName[i].contains(".")){
                        String[] newStr = toothDepthName[i].split("\\.");
                        Integer depthValue=depthPositionMap.get(newStr[0]);
                        float  decimals=Float.parseFloat("0."+newStr[1]);
                        float valueY;
                        int distanceY;
                        if(depthValue==null){
                            if(newStr[0].equals("X")||newStr[1].equals("0")){
                                depthValue= depthPositionMap.get(depthNames[0]);
                                distanceY =defaultY-depthValue;
                                valueY=distanceY*decimals;
                                mPath.lineTo(toothDistance[i]-halfToothWidth[i],defaultY-valueY);
                                mPath.lineTo(toothDistance[i]+halfToothWidth[i],defaultY-valueY);
                            }
                        } else {
                            valueY =0;
                            if(depthValue == depthPosition[depthPosition.length-1]){
                                //等于最后一个深度位置不计算。
                            }else {
                                for(int j = 0; j< depthPosition.length; j++){
                                    if(depthValue == depthPosition[j]){
                                        distanceY = depthValue-depthPosition[j+1];
                                        valueY =distanceY*decimals;
                                        break;
                                    }
                                }
                            }
                            mPath.lineTo(toothDistance[i]- halfToothWidth[i], depthValue - valueY);
                            mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue - valueY);
                        }
                    }else {  //不包含小数的
                        Integer depthValue=depthPositionMap.get(toothDepthName[i]);
                        mPath.lineTo(toothDistance[i]- halfToothWidth[i],depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));
                        mPath.lineTo(toothDistance[i]+ halfToothWidth[i],depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));
                    }
                }
            }else {  //guide  不等于0
                defaultY=depthPosition[0]-(int)(patternHeight*0.06);
                int guide=patternMaxY-guideY;
                mPath.lineTo(patternWidth-patternRightWidth,guide);
                mPath.lineTo(patternLeftWidth,guide);
                int arcY=(int)(patternHeight*0.2);
                int  controlX=patternLeftWidth-(int)(patternLeftWidth*0.15);
                int  controlY=guide-(arcY/2);
                mPath.quadTo(controlX,controlY,patternLeftWidth,guide-arcY);
                for (int i = 0; i <toothDepthName.length ; i++) {
                  if(toothDepthName[i].contains(".")){
                      String[] newStr = toothDepthName[i].split("\\.");
                        Integer depthValue=depthPositionMap.get(newStr[0]);
                        float  decimals=Float.parseFloat("0."+ newStr[1]);
                        float valueY;
                        int distanceY;
                        if(depthValue==null){
                            if(newStr[0].equals("X")||newStr[0].equals("0")){
                                depthValue= depthPositionMap.get(depthNames[0]);
                                distanceY =depthValue-defaultY;
                                valueY=distanceY*decimals;
                                mPath.lineTo(toothDistance[i]-halfToothWidth[i],defaultY+valueY);
                                mPath.lineTo(toothDistance[i]+halfToothWidth[i],defaultY+valueY);
                            }
                        }else {
                            valueY =0;
                            if(depthValue == depthPosition[depthPosition.length-1]){
                                //等于最后一个深度位置不计算。
                            }else {
                                for(int j = 0; j< depthPosition.length; j++){
                                    if(depthValue == depthPosition[j]){
                                        distanceY = depthPosition[j+1]-depthValue;
                                        valueY =distanceY*decimals;
                                        break;
                                    }
                                }
                            }
                            mPath.lineTo(toothDistance[i]- halfToothWidth[i], depthValue + valueY);
                            mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue + valueY);
                        }
                    }else {  //不包含小数的
                      Integer  depthValue=depthPositionMap.get(toothDepthName[i]);
                        mPath.lineTo(toothDistance[i]- halfToothWidth[i], depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));
                        mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));
                    }
                }
            }
        }else if(ki.getSide()==BSide){  //B面
            if(guideY==0){
                defaultY=depthPosition[0]-(int)(patternHeight*0.06);
                for (int i =toothDistance.length-1; i>=0 ; i--) {
                  if(toothDepthName[i].contains(".")){
                      String[] newStr = toothDepthName[i].split("\\.");
                        Integer depthValue=depthPositionMap.get(newStr[0]);
                        float  decimals=Float.parseFloat("0."+ newStr[1]);
                        float valueY;
                        int distanceY;
                      if(depthValue==null){
                          if(newStr[0].equals("0")||newStr[0].equals("X")){
                              depthValue= depthPositionMap.get(depthNames[0]);
                              distanceY =depthValue-defaultY;
                              valueY=distanceY*decimals;
                              mPath.lineTo(toothDistance[i]+halfToothWidth[i],defaultY+valueY);
                              mPath.lineTo(toothDistance[i]-halfToothWidth[i],defaultY+valueY);
                          }
                      } else {
                            valueY =0;
                            if(depthValue == depthPosition[depthPosition.length-1]){
                                //等于最后一个深度位置不计算。
                            }else {
                                for(int j = 0; j< depthPosition.length; j++){
                                    if(depthValue == depthPosition[j]){
                                        distanceY = depthPosition[j+1]-depthValue;
                                        valueY =distanceY*decimals;
                                        break;
                                    }
                                }
                            }
                            mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue + valueY);
                            mPath.lineTo(toothDistance[i]- halfToothWidth[i], depthValue + valueY);
                        }
                    }else {  //不包含小数的
                      Integer depthValue= depthPositionMap.get(toothDepthName[i]);
                        mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));
                        mPath.lineTo(toothDistance[i]- halfToothWidth[i], depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));

                    }
                }
                mPath.lineTo(patternLeftWidth,extraTopY);
            }else {   //不等于0
                defaultY=depthPosition[0]+(int)(patternHeight*0.06);
                for (int i =toothDistance.length-1; i>=0 ; i--) {
                     if(toothDepthName[i].contains(".")){
                         String[] newStr = toothDepthName[i].split("\\.");
                        Integer depthValue=depthPositionMap.get(newStr[0]);
                        float  decimals=Float.parseFloat("0."+ newStr[1]);
                        float valueY;
                        int distanceY;
                         if(depthValue==null){
                             if(newStr[0].equals("X")||newStr[0].equals("0")){
                                 depthValue= depthPositionMap.get(depthNames[0]);
                                 distanceY =defaultY-depthValue;
                                 valueY=(int)(distanceY*decimals);
                                 mPath.lineTo(toothDistance[i]+halfToothWidth[i],defaultY-valueY);
                                 mPath.lineTo(toothDistance[i]-halfToothWidth[i],defaultY-valueY);
                             }
                         } else {
                            valueY =0;
                            if(depthValue == depthPosition[depthPosition.length-1]){
                                //等于最后一个深度位置不计算。
                            }else {
                                for(int j = 0; j< depthPosition.length; j++){
                                    if(depthValue == depthPosition[j]){
                                        distanceY = depthValue-depthPosition[j+1];
                                        valueY =distanceY*decimals;
                                        break;
                                    }
                                }
                            }
                            mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue - valueY);
                            mPath.lineTo(toothDistance[i]- halfToothWidth[i], depthValue -valueY);
                        }
                    }else {  //不包含小数的
                         Integer depthValue = depthPositionMap.get(toothDepthName[i]);
                        mPath.lineTo(toothDistance[i]+ halfToothWidth[i], depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));
                        mPath.lineTo(toothDistance[i]- halfToothWidth[i],depthValue==null? defaultY: depthPositionMap.get(toothDepthName[i]));

                    }
                }
                int guide=extraTopY+guideY;
                int arcY=(int)(patternHeight*0.2);
                int x=patternLeftWidth-(int)(patternLeftWidth*0.15);
                mPath.lineTo(x,guide+arcY);
                mPath.quadTo(x-(int)(x*0.17),guide+(arcY/2),x,guide);
                mPath.lineTo(patternWidth-patternRightWidth,guide);
            }
        }
        if(noseX!=0){
            mPath.lineTo(patternWidth-noseX,extraTopY);
        }
        mPath.lineTo(patternWidth-arcX,extraTopY);
        mPath.quadTo(patternWidth,extraTopY,patternWidth,arcX+extraTopY);
        mBorderPaint.setStrokeWidth(1f);
        canvas.drawPath(mPath,mBorderPaint); //边界笔
        canvas.drawPath(mPath,mOuterGrooveColorPaint);//外槽颜色的笔
        mPath.reset();
        if(isDraw){
            this.drawDepthPattern(canvas);
            this.drawToothCodePattern(canvas);
        }

    }

    /**
     * 画深度图案
     * @param canvas
     */
    private void drawDepthPattern(Canvas canvas){
        int endX =(int)(patternLeftWidth+patternMiddleWidth);
        for (int i = 0; i <depthPosition.length ; i++) {
            mPath.moveTo(patternLeftWidth,depthPosition[i]);
            mPath.lineTo(endX,depthPosition[i]);
        }
        canvas.drawPath(mPath,mDashedPaint);
        mPath.reset();
    }
    private void drawToothCodePattern(Canvas canvas){
        int ToothCodeY;
        if(ki.getSide()==ASide){
            if(guideY==0){
                ToothCodeY=defaultY+24;
            }else {
                ToothCodeY=patternMaxY-guideY;
            }
        }else {
            ToothCodeY=extraTopY+40;
        }
        for (int i = 0; i < toothDistance.length; i++) {
            mPath.moveTo(toothDistance[i], depthPositionStartY);
            mPath.lineTo(toothDistance[i], depthPositionEndY);
            if(toothDepthName[i].contains(".")){
                mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                String[] newStr=toothDepthName[i].split("\\.");
                int num=Integer.parseInt(newStr[1]);
                if(depthPositionMap.get(newStr[0])==null){
                    if(newStr[0].equals("X")||newStr[0].equals("0")){
                        if(num>=5){
                            canvas.drawText(depthNames[0], toothDistance[i], ToothCodeY, mColorTextPaint);//画红色的字
                        }else {
                            canvas.drawText(newStr[0], toothDistance[i], ToothCodeY , mColorTextPaint);//画红色的字
                        }
                    }else {
                        canvas.drawText(newStr[0], toothDistance[i], ToothCodeY , mColorTextPaint);//画红色的字
                    }
                }else {
                    if(num>=5){
                        if(newStr[0].equals(depthNames[depthNames.length-1])){
                            canvas.drawText(newStr[0], toothDistance[i], ToothCodeY , mColorTextPaint);//画红色的字
                        }else {
                            for (int j = 0; j <depthNames.length ; j++) {
                                if(depthNames[j].equals(newStr[0])){
                                    canvas.drawText(depthNames[j+1], toothDistance[i],ToothCodeY, mColorTextPaint);//画红色的字
                                    break; //跳出循环
                                }
                            }
                        }
                    }else {
                        canvas.drawText(newStr[0], toothDistance[i],ToothCodeY, mColorTextPaint);//画红色的字
                    }
                }
            }else {
                mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                canvas.drawText(toothDepthName[i],toothDistance[i],ToothCodeY, mColorTextPaint);//画蓝色的字
            }
        }
        canvas.drawPath(mPath, mDashedPaint);//画X轴方向的。
        mPath.reset();  //重置
    }

    private void initPaintAndPath() {
        mPath = new Path();
        mBorderPaint = new Paint();
        mKeyAppearanceColorPaint = new Paint();
        mDashedPaint = new Paint();
        mColorTextPaint = new Paint();
        mArrowsPaint = new Paint();
        mOuterGrooveColorPaint = new Paint();
        //画钥匙形状属性
        mBorderPaint.setAntiAlias(true);//去掉抗锯齿
        mBorderPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        //填充钥匙身体颜色的笔
        mKeyAppearanceColorPaint.setColor(Color.parseColor("#BABABA"));
        mKeyAppearanceColorPaint.setAntiAlias(true);
        mKeyAppearanceColorPaint.setStyle(Paint.Style.FILL);
        mKeyAppearanceColorPaint.setStrokeWidth(1);
        //画虚线的笔的属性
        mDashedPaint.setAntiAlias(true);//去掉抗锯齿
        mDashedPaint.setColor( Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        mDashedPaint.setStyle(Paint.Style.STROKE);//设置画笔描边
        mDashedPaint.setStrokeWidth(1f);
        PathEffect effects = new DashPathEffect(new float[]{3f,1f,3f,1f}, 0);
        mDashedPaint.setPathEffect(effects);
        //画蓝色字体的笔
        mColorTextPaint.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        mColorTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mColorTextPaint.setTextSize(40);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //填充钥匙内槽颜色的笔
        mOuterGrooveColorPaint.setAntiAlias(true);//去掉锯齿
        mOuterGrooveColorPaint.setColor(Color.parseColor("#cc9900")); //黄色
        mOuterGrooveColorPaint.setStyle(Paint.Style.FILL);
        mOuterGrooveColorPaint.setStrokeWidth(1);
    }
}
