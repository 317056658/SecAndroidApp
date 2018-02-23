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
 * 角度钥匙
 * Created by Administrator on 2017/7/24.
 */

public class AngleKey extends Key {
    private KeyInfo ki;
    private Path mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;  //虚线画笔
    private Paint mColorTextPaint;
    private Paint mArrowsPaint;
    private Paint centreLinePaint;
    private Paint mDepthBorderPaint;
    private Paint mYellowPaint;

    private float spacesScaleValue;
    private String[] toothDepthName;

    private ArrayList<String[]> toothDepthNameList;
    private String[] allToothDepthName;

    private int[] depthPositionOne,depthPositionTwo;  //深度位置一 和二；
    private Map<String,Integer> depthMapOne, depthMapTwo;

    private  final static String TAG="AngleKey";
    private boolean isShowArrows=true;  //默认为true
    private int patternLeftWidth,patternRightWidth;
    private float patternMiddleWidth;
    private int[] toothDistance;
    private int depthPositionStartYOne, depthPositionEndYOne, depthPositionStartYTwo, depthPositionEndYTwo;
    private double patternMiddleHeight;  //图案中间高度
    private int extraTopY; //额外上面Y
    private int patternMaxY;   //
    private int eachToothWidth;
   private String[] depthName;  //深度名数组
    private boolean  isDraw=true;

    public AngleKey(Context context,KeyInfo ki) {
         this(context,null,ki);
    }


    public AngleKey(Context context, AttributeSet attrs,KeyInfo ki) {
        this(context,attrs,0,ki);
    }

    public AngleKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        depthMapOne =new HashMap<>();
        depthMapTwo =new HashMap<>();
        toothDepthNameList=new ArrayList<>();
        this.ki=ki;
        this.initPaintAndPath();
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
        patternLeftWidth=(int)(width*0.15f);
        patternRightWidth=(int)(width*0.15f);
        patternMiddleWidth=(int)(width*0.7f);
        extraTopY=(int)(height*0.1f)+4;
        patternMiddleHeight=(int)(height*0.88f);
        patternMaxY=(int)(extraTopY+patternMiddleHeight);
        this.analysisSpace();
    }

    private void   analysisSpace(){
            String[] spaceGroup=ki.getSpace().split(";");
            String[] space=spaceGroup[0].split(",");
        toothDistance=new int[space.length];
        toothDepthName=new String[space.length];
        //转为int
        for (int i = 0; i <space.length ; i++) {
            toothDistance[i]=Integer.parseInt(space[i]);
        }
        this.calculateDrawToothDistance();
        this.analysisDepthAndDepthName();
    }

    /**
     * 计算绘制的齿距
     */
    private  void    calculateDrawToothDistance(){
           if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
               //计算出最大长度
                 int maxLength=toothDistance[0]+(toothDistance[0]-toothDistance[1]);
               spacesScaleValue=patternMiddleWidth/maxLength;
               int sumX=(int)(patternLeftWidth+patternMiddleWidth);
               for (int i = 0; i <toothDistance.length ; i++) {
                   toothDistance[i]=sumX-(int)(toothDistance[i]*spacesScaleValue);
               }
               //每个齿的宽度
               eachToothWidth =(toothDistance[1]-toothDistance[0])/2;

           }
    }

    /**
     * 解析深度和深度名
     */
    private void  analysisDepthAndDepthName(){
         String[]  depthGroup=ki.getDepth().split(";");
         String[]  depthNameGroup=ki.getDepth_name().split(";");
         String[] depth=depthGroup[0].split(",");
         depthName=depthNameGroup[0].split(",");
        int[]  depthInt=new int[depth.length];  //
        //转为int
        for (int i = 0; i < depth.length; i++) {
            depthInt[i]=Integer.parseInt(depth[i]);
        }
        this.calculateDrawDepth(depthInt,depthName);
    }

    /**
     * 计算绘制的深度
     */
    private void calculateDrawDepth(int[] depthInt,String[] depthName){
        int maxValue =0;   //最大深度值
        for (int i = 0; i <depthInt.length ; i++) {
              if(maxValue <depthInt[i]){
                  maxValue =depthInt[i];
              }

        }
        double depthScaleRatio=(patternMiddleHeight/2)/(maxValue+1600);
        depthPositionOne=new int[depthInt.length];   //深度位置  1
        depthPositionTwo=new int[depthInt.length];   //深度位置  2
        //计算第一边的深度
        for (int i = 0; i <depthInt.length ; i++) {
            depthPositionOne[i]=(int)(depthInt[i]*depthScaleRatio)+extraTopY;
            //key value 对 保证计算好的深度
            depthMapOne.put(depthName[i],depthPositionOne[i]);
        }
        depthPositionStartYOne=depthPositionOne[0];
        depthPositionEndYOne=depthPositionOne[depthPositionOne.length-1];
        //计算第二边的深度
        for (int i = 0; i <depthInt.length ; i++) {
            depthPositionTwo[i]=patternMaxY-(int)(depthInt[i]*depthScaleRatio);
            //key value 对 保证计算好的深度
            depthMapTwo.put(depthName[i],depthPositionTwo[i]);
        }
        depthPositionStartYTwo=depthPositionTwo[0];
        depthPositionEndYTwo=depthPositionTwo[depthPositionOne.length-1];
    }

    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
          this.isDraw=isDraw;
    }


    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
            allToothDepthName = toothCode.split(",");
            for (int i = 0; i < allToothDepthName.length; i++) {
                toothDepthName[i] = allToothDepthName[i];
            }
        }else {
            setToothCodeDefault();
        }
    }

    @Override
    public void redrawKey() {
            this.invalidate();  //重绘
    }

    @Override
    public void setToothAmount(int toothAmount) {

    }

    /**
     * 自定义绘制的齿状
     */
     public  void customDrawSerrated(){
         int k=0;
         for (int i = 0; i <toothDepthName.length ; i++) {
                 if(depthName.length>=3){
                     toothDepthName[i]=depthName[k+1];
                     k++;
                     if(k==2){
                         k=0;
                     }
                 }else {
                     toothDepthName[i]=depthName[0];
                 }
         }
     }

    @Override
    public int getToothAmount() {
        return 0;
    }

    @Override
    public ArrayList<String[]> getToothCode() {
        toothDepthNameList.clear();
        if(ki.getAlign()==1){
            toothDepthNameList.add(toothDepthName);
        }
        return toothDepthNameList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
            this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String  toothCode="";
        for (int i = 0; i <toothDepthName.length ; i++) {
            toothDepthName[i]="X";
            toothCode+="0,";
        }
        ki.setKeyToothCode(toothCode);

    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";
        String toothQuantity ="";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        String  side;
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
        if(ki.getSide()==0){
            side="";
        }else {
            side= ki.getSide()+"";
        }
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + side+ ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + 0 + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码  其实都是零
                + 0 + ","  // 鼻部长度
                + 0 + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm = "!SB";
        String toothPositionData = "";
        String toothWidth = "";
        String toothDepthData = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int knifeType=1;  //到类型
        int noseCut=0;

        String DDepth="";

        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        String[] spaceGroup = ki.getSpace().split(";");
        for (int i = 0; i < spaceGroup.length; i++) {
            String[]  spaces = spaceGroup[i].split(",");
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
                + ki.getWidth() + ","// 钥匙宽度
                + ki.getThick() + ","// 钥匙厚度
                + 0 + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                +0+ ","   //切割深度
                + spaceGroup[0].split(",").length+","  // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + depthGroup[0].split(",").length + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + ki.getKeyToothCode()   //齿号代码  其实都是零
                + 0 + ","  // 鼻部长度
                + 0 + ","  //槽宽
                + toothDepthName
                + 0 + ";"//最后齿或扩展切割
                +"!AC"+assistClamp+";"  // 辅助夹具
                +"!ST"+knifeType+"," //刀类型
                +cutterDiameter+";"    //刀的规格（刀的直径）
                +"!CK"
                +speed+"," //速度
                + DDepth +","//D深度
                +ZDepth+","  //Z深度
                +noseCut+","  //鼻部切割
                +detectionMode+";";  //切割钥匙的检测方式;
               return order;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }
    /**
     * 绘制本钥匙的图案
     * @param canvas
     */
   private  void drawKeyPattern(Canvas canvas){
       int patternWidth=patternLeftWidth+(int)(patternMiddleWidth+patternRightWidth);
         if(ki.getFace().equals("0")){
             mPath.moveTo(0,extraTopY+(int)(patternMiddleHeight*0.2f));
             mPath.lineTo(patternLeftWidth,extraTopY+(int)(patternMiddleHeight*0.2f));
             mPath.lineTo(patternLeftWidth,extraTopY);
             mPath.lineTo(patternLeftWidth+patternMiddleWidth,extraTopY);
             mPath.lineTo(patternLeftWidth+patternMiddleWidth,extraTopY+(int)(patternMiddleHeight*0.1f));
             mPath.lineTo(patternWidth-(int)(patternRightWidth*0.12),extraTopY+(int)(patternMiddleHeight*0.1f));

             mPath.lineTo(patternWidth,extraTopY+(int)(patternMiddleHeight*0.15f));
             mPath.lineTo(patternWidth,patternMaxY-(int)(patternMiddleHeight*0.15f));

             mPath.lineTo(patternWidth-(int)(patternRightWidth*0.12),patternMaxY-(int)(patternMiddleHeight*0.1f));
             mPath.lineTo(patternLeftWidth+patternMiddleWidth,patternMaxY-(int)(patternMiddleHeight*0.1f));
             mPath.lineTo(patternLeftWidth+patternMiddleWidth,patternMaxY);
             mPath.lineTo(patternLeftWidth,patternMaxY);
             mPath.lineTo(patternLeftWidth,patternMaxY-(int)(patternMiddleHeight*0.2f));
             mPath.lineTo(0,patternMaxY-(int)(patternMiddleHeight*0.2f));
             canvas.drawPath(mPath, mBorderPaint);
             canvas.drawPath(mPath,mKeyAppearanceColorPaint);
             mPath.reset();
             mPath.moveTo(0,extraTopY+(int)(patternMiddleHeight/2));
             mPath.lineTo(patternWidth,extraTopY+(int)(patternMiddleHeight/2));
             canvas.drawPath(mPath, mBorderPaint);
             mPath.reset();
         }else if(ki.getFace().equals("2")){
             mPath.moveTo(0,extraTopY);
             mPath.lineTo(patternWidth-(int)(patternMiddleHeight*0.15f),extraTopY);
             mPath.lineTo(patternWidth,extraTopY+(int)(patternMiddleHeight*0.15f));
             mPath.lineTo(patternWidth,patternMaxY-(int)(patternMiddleHeight*0.15f));
             mPath.lineTo(patternWidth-(int)(patternMiddleHeight*0.15f),patternMaxY);
             mPath.lineTo(0,patternMaxY);
             canvas.drawPath(mPath, mBorderPaint);
             canvas.drawPath(mPath,mKeyAppearanceColorPaint);
             mPath.reset();
             mPath.moveTo(0,extraTopY+(int)(patternMiddleHeight/2));
             mPath.lineTo(patternWidth,extraTopY+(int)(patternMiddleHeight/2));
             canvas.drawPath(mPath, mBorderPaint);
             mPath.reset();
         }
       for (int i = 0; i <toothDepthName.length ; i++) {
               if(toothDepthName[i].contains(".")){
                   String[] newStr=toothDepthName[i].split("\\.");
                   int distanceY;
                   float valueY=0;
                   float decimals=Float.parseFloat("0."+newStr[1]);
                   Integer depthValue=depthMapOne.get(newStr[0]);
                   if(depthValue==null){
                             depthValue=depthMapOne.get(depthName[0]);
                            valueY=(depthValue-extraTopY)*decimals;
                       Log.d(TAG, "drawKey深度: "+(depthValue+valueY));
                       mPath.addRect(toothDistance[i]-eachToothWidth,extraTopY, toothDistance[i]+eachToothWidth,depthValue+valueY, Path.Direction.CW);
                     }else {
                       if(depthValue != depthPositionOne[depthPositionOne.length-1]) {
                           for (int j = 0; j < depthPositionOne.length; j++) {
                               if (depthValue == depthPositionOne[j]) {
                                   distanceY = depthPositionOne[j + 1] - depthValue;
                                   valueY = distanceY * decimals;
                                   break;
                               }
                           }
                       }
                       mPath.addRect(toothDistance[i]-eachToothWidth,extraTopY, toothDistance[i]+eachToothWidth,depthValue+valueY, Path.Direction.CW);
                     }
               }else  if(depthMapOne.get(toothDepthName[i])!=null){
                   int  depthValue=depthMapOne.get(toothDepthName[i]);
                  mPath.addRect(toothDistance[i]-eachToothWidth,extraTopY, toothDistance[i]+eachToothWidth,depthValue, Path.Direction.CW);
               }
       }
       for (int i = 0; i <toothDepthName.length ; i++) {
           if(toothDepthName[i].contains(".")){
               String[] newStr=toothDepthName[i].split("\\.");
               int distanceY;
               float valueY=0;
               float decimals=Float.parseFloat("0."+newStr[1]);
               Integer depthValue=depthMapTwo.get(newStr[0]);
               if(depthValue==null){
                   depthValue=depthMapTwo.get(depthName[0]);
                   valueY=(patternMaxY-depthValue)*decimals;
                   mPath.addRect(toothDistance[i]-eachToothWidth,depthValue+valueY, toothDistance[i]+eachToothWidth,patternMaxY, Path.Direction.CW);
               }else {
                   if(depthValue != depthPositionTwo[depthPositionTwo.length-1]) {
                       for (int j = 0; j < depthPositionTwo.length; j++) {
                           if (depthValue == depthPositionTwo[j]) {
                               distanceY = depthValue-depthPositionTwo[j + 1];
                               valueY = distanceY * decimals;
                               break;
                           }
                       }
                   }
                   mPath.addRect(toothDistance[i]-eachToothWidth,depthValue-valueY, toothDistance[i]+eachToothWidth,patternMaxY, Path.Direction.CW);
               }
           }else  if(depthMapTwo.get(toothDepthName[i])!=null){
               int  depthValue=depthMapTwo.get(toothDepthName[i]);
               mPath.addRect(toothDistance[i]-eachToothWidth,depthValue, toothDistance[i]+eachToothWidth,patternMaxY, Path.Direction.CW);
           }
       }
       canvas.drawPath(mPath,mDepthBorderPaint);
       canvas.drawPath(mPath,mYellowPaint);
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
   private void  drawDepthPattern(Canvas canvas){
       for (int i = 0; i <depthPositionOne.length ; i++) {
             mPath.moveTo(patternLeftWidth,depthPositionOne[i]);
           mPath.lineTo(patternLeftWidth+patternMiddleWidth,depthPositionOne[i]);
       }
       for (int i = 0; i <depthPositionTwo.length ; i++) {
           mPath.moveTo(patternLeftWidth,depthPositionTwo[i]);
           mPath.lineTo(patternLeftWidth+patternMiddleWidth,depthPositionTwo[i]);
       }
       canvas.drawPath(mPath,mDashedPaint);
       mPath.reset();
   }

    /**
     * 绘制齿代码
     * @param canvas
     */
   private  void drawToothCodePattern(Canvas canvas) {
       int  patternCentreY=extraTopY+(int)(patternMiddleHeight/2);
       for (int i = 0; i < toothDistance.length; i++) {
           mPath.moveTo(toothDistance[i], depthPositionStartYOne);
           mPath.lineTo(toothDistance[i], depthPositionEndYOne);
           mPath.moveTo(toothDistance[i], depthPositionStartYTwo);
           mPath.lineTo(toothDistance[i], depthPositionEndYTwo);
           if (toothDepthName[i].contains(".")) {
               mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
               String[] newStr = toothDepthName[i].split("\\.");
               int num=Integer.parseInt(newStr[1]);
               if (depthMapOne.get(newStr[0])== null) {
                    if(newStr[0].equals("X")||newStr[0].equals("0")){
                        if(num>=5){
                            canvas.drawText(depthName[0],toothDistance[i],patternCentreY+10,mColorTextPaint);
                        }else {
                            canvas.drawText("X",toothDistance[i],patternCentreY+10,mColorTextPaint);
                        }
                    }else {
                        canvas.drawText(newStr[0],toothDistance[i],patternCentreY+10,mColorTextPaint);
                    }
               } else {
                   if(num>=5){
                       if(!newStr[0].equals(depthName.length-1)){
                           for (int j = 0; j <depthName.length ; j++) {
                               if(newStr.equals(depthName[i])){
                                   canvas.drawText(depthName[i+1],toothDistance[i],patternCentreY+10,mColorTextPaint);
                                   break;
                               }
                           }
                       }else {
                           canvas.drawText(newStr[0],toothDistance[i],patternCentreY+10,mColorTextPaint);
                       }
                   }else {
                       canvas.drawText(newStr[0],toothDistance[i],patternCentreY+10,mColorTextPaint);
                   }
               }
           } else  {
               mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
               canvas.drawText(toothDepthName[i],toothDistance[i],patternCentreY+10,mColorTextPaint);
           }
       }
       //画齿的位置虚线
       canvas.drawPath(mPath,mDashedPaint);
       mPath.reset();
   }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
        if(isShowArrows){
            if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
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
    private void initPaintAndPath() {
        mPath = new Path();
        mBorderPaint = new Paint();
        mKeyAppearanceColorPaint = new Paint();
        mDashedPaint = new Paint();
        mColorTextPaint = new Paint();
        mArrowsPaint = new Paint();
        centreLinePaint = new Paint();
        mDepthBorderPaint =new Paint();
        mYellowPaint =new Paint();
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
        mColorTextPaint.setTextAlign(Paint.Align.CENTER);
        mColorTextPaint.setFakeBoldText(true);  //设置为粗体
        mColorTextPaint.setTextSize(35);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //画深度边框画笔
        mDepthBorderPaint.setColor(Color.WHITE);  //默认白色
        mDepthBorderPaint.setAntiAlias(true);//去掉锯齿
        mDepthBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        mDepthBorderPaint.setStrokeWidth(2); //设置画线的宽度
        //黄色画笔
        mYellowPaint.setColor(Color.parseColor("#cc9900"));  //黄色
        mYellowPaint.setAntiAlias(true);//去掉锯齿
        mYellowPaint.setStyle(Paint.Style.FILL);//设置画笔风格为填充
        mYellowPaint.setStrokeWidth(1); //设置画线的宽度
    }
}
