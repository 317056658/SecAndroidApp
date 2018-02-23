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
 * 凹点钥匙
 * Created by Administrator on 2017/7/19.
 */

public class ConcaveDotKey extends Key {
    private Path  mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;
    private Paint mColorTextPaint;
    private Paint arrowsPaint;
    private Paint bigCirclePaint;
    private Paint smallCirclePaint;
    private KeyInfo ki;
    private ArrayList<String[]> toothCodeList;
    private List<Map<String, Integer>> bigCircleRadiusList;
    private List<Map<String, Integer>> smallCircleRadiusList;
    private boolean isDrawSidePattern, isRowEquality;
    private boolean isShowArrows=true;  //默认为true
    private int patternLeftWidth;
    private float patternRightWidth;
    private double patternBodyHeight;
    private int patternLeftShoulderHeight;
    private int extraTopY,patternBodyMaxY;
    private int[][] toothDistanceX;    //二维数组
    private int[] toothOrbitY;  //齿轨道
    private String[][] toothDepthName;  //二维数组  齿的深度名
    private int interval_y;  //间隔y
    private String[] rowPosGroup;
    private int patternSideHeight;
    private int[]  toothDepthNameYPosition;
    private  double toothOrbitScaleValue;
    private  String[][] twoDepthName;  //存放深度名的二维数据
    private  boolean  isOnlyDrawSidePattern;
    private boolean  isDrawToothCode=true;  //是否绘制齿代码

    public ConcaveDotKey(Context context,KeyInfo ki) {
         this(context,null,ki);
    }


    public ConcaveDotKey(Context context, AttributeSet attrs,KeyInfo ki) {
        this(context,attrs,0,ki);
    }

    public ConcaveDotKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.ki=ki;
        this.initPaintAndPath();
        bigCircleRadiusList =new ArrayList<>();
        smallCircleRadiusList =new ArrayList<>();
        toothCodeList = new ArrayList<>();
    }
    /**

     *  设置只绘制侧面图案大小
     */
    @Override
    public   void setOnlyDrawSidePatternSize(int width, int height){
            patternLeftWidth=(int)(width*0.1);
            patternRightWidth=(int)(width*0.9f);
            patternSideHeight=(int)(height*0.74);
            extraTopY=(int)(height*0.26f);
            isOnlyDrawSidePattern=true;
            toothOrbitY=new int[1];
            this.calculateDrawOnlyDrawSideToothDepthNamePosition();
            this.analysisSpace();
            this.calculateDrawToothDistance();
    }

    /**
     * 解析行位置数据转为int
     */
    private void  analysisRowPosDataToInt(){
        rowPosGroup = ki.getRow_pos().split(";");  //分割rowPos数据
        toothOrbitY=new int[rowPosGroup.length];  //齿轨
        for (int i = 0; i <rowPosGroup.length ; i++) {
            toothOrbitY[i]=Integer.parseInt(rowPosGroup[i]);
            if(toothOrbitY[i]<0){
                isDrawSidePattern=true;    //绘制
            }
        }
        int value=0;
        for (int i = 0; i <toothOrbitY.length ; i++) {
                  if(value==toothOrbitY[i]){
                      isRowEquality=true;
                  }else {
                      value=toothOrbitY[i];
                  }
        }
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
        //这个方法必须放在最前面
        this.analysisRowPosDataToInt();
            //定义绘制图案大小的数据
            if(isDrawSidePattern){
                patternRightWidth=(int)(width*0.9f);
                patternLeftWidth=(int)(width*0.1f);
                patternBodyHeight=(int)(height*0.58f);
                patternLeftShoulderHeight=(int)(height*0.1f);
                extraTopY=4+patternLeftShoulderHeight;
                interval_y =(int)(height*0.07f);
                patternBodyMaxY=(int)(patternBodyHeight +extraTopY);
                patternSideHeight=(int)(height*0.15f);
            }else {
                patternRightWidth=(int)(width*0.9f);
                patternLeftWidth=(int)(width*0.1f);
                patternBodyHeight=(int)(height*0.76f);
                patternLeftShoulderHeight=(int)(height*0.12f);
                extraTopY=4+patternLeftShoulderHeight;
                patternBodyMaxY=(int)(patternBodyHeight +extraTopY);
            }
        this.analysisSpace();
        this.calculateDrawToothDistance();
        this.calculateDrawToothOrbitPosition();
        // 计算绘制齿代码字体 y坐标
        this.calculateDrawToothDepthNamePosition();
    }

    private  void analysisSpace(){
        String[] spaceGroup = ki.getSpace().split(";");
            //全部转为int类型
            toothDistanceX =new int[ki.getRowCount()][];
        //初始化齿的深度的数量
        toothDepthName=new String[ki.getRowCount()][];

            for (int i = 0; i < toothDistanceX.length; i++) {
               String[] space=spaceGroup[i].split(",");
                toothDistanceX[i]=new int[space.length];
                toothDepthName[i]=new String[space.length];
                for (int j = 0; j < space.length; j++) {
                    toothDistanceX[i][j]=Integer.parseInt(space[j]);
                }
            }
    }


    /**
     * 计算绘制只有侧面齿深度名位置
     */
   private void    calculateDrawOnlyDrawSideToothDepthNamePosition(){
       Paint.FontMetricsInt fm = mColorTextPaint.getFontMetricsInt();
       int  sideY=extraTopY+(patternSideHeight/2);
       int top=sideY+fm.top;
       int bottom=sideY+fm.bottom;
       toothDepthNameYPosition=new int[1];
       toothDepthNameYPosition[0]=(sideY+(sideY-(bottom-(bottom-top)/2)));
       toothOrbitY[0]=sideY;
   }
    /**
     * 计算绘制的齿的深度名 y  位置
     */
    private  void calculateDrawToothDepthNamePosition(){
        Paint.FontMetricsInt fm = mColorTextPaint.getFontMetricsInt();

        //侧面Y的中心点
        int  sideY=(patternBodyMaxY+interval_y)+patternLeftShoulderHeight+(patternSideHeight/2);
        int sideTop=sideY+fm.top;
        int sideBottom=sideY+fm.bottom;
        toothDepthNameYPosition=new int[ki.getRowCount()];
        for (int i = 0; i < rowPosGroup.length; i++) {
            int top=toothOrbitY[i]+fm.top;
            int bottom=toothOrbitY[i]+fm.bottom;
            if(Integer.parseInt(rowPosGroup[i])<0){  //<0就是代表侧面
                toothDepthNameYPosition[i]= (sideY+(sideY-(sideBottom-(sideBottom-sideTop)/2)));
            }else {
                toothDepthNameYPosition[i]=(int)(Integer.parseInt(rowPosGroup[i])*toothOrbitScaleValue)+extraTopY;
                toothDepthNameYPosition[i]=(toothDepthNameYPosition[i]+( toothDepthNameYPosition[i]-(bottom-(bottom-top)/2)));
            }
        }
    }

    /**
     * 计算绘制的齿的轨道位置
     *
     */
    private void  calculateDrawToothOrbitPosition(){
        //绘制齿轨的比例值
      toothOrbitScaleValue=patternBodyHeight/ki.getWidth();
        //   把行的位置转为int
        for (int i = 0; i < toothOrbitY.length; i++) {
            if(toothOrbitY[i]<0){  //<0就是代表侧面
                toothOrbitY[i]=(patternBodyMaxY+interval_y)+patternLeftShoulderHeight+(patternSideHeight/2);
            }else {
                toothOrbitY[i]=(int)(toothOrbitY[i]*toothOrbitScaleValue)+extraTopY;
            }
        }
    }
    /**
     * 计算绘制的齿距
     * @param
     */
    private void calculateDrawToothDistance(){
        int maxLength=0;
        float spacesScaleValue;
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){   //肩膀
            int  max=0;
            //比出哪组的齿距是最大值  得到齿距的最大长度
            for (int i = 0; i <toothDistanceX.length ; i++) {
                        if(max<toothDistanceX[i][toothDistanceX[i].length-1]){
                            max=toothDistanceX[i][toothDistanceX[i].length-1];
                            maxLength =max+(max-toothDistanceX[i][toothDistanceX[i].length-2]);
                        }
            }
            spacesScaleValue=patternRightWidth/maxLength;

            for (int i = 0; i < toothDistanceX.length; i++) {
                for (int j = 0; j <toothDistanceX[i].length ; j++) {
                    toothDistanceX[i][j]=(int)(toothDistanceX[i][j]*spacesScaleValue)+patternLeftWidth;
                }
            }
        }else {     //前端
            int  max=0;
            //比出哪组的齿距是最大值  得到齿距的最大长度
            for (int i = 0; i <toothDistanceX.length ; i++) {
                if(max<toothDistanceX[i][0]){
                    max=toothDistanceX[i][0];
                    maxLength =max+(max-toothDistanceX[i][1]);
                }
            }
            spacesScaleValue=patternRightWidth/maxLength;
            //获得图案的宽度
            int sumX=(int)patternRightWidth+patternLeftWidth;
            for (int i = 0; i < toothDistanceX.length; i++) {
                for (int j = 0; j <toothDistanceX[i].length ; j++) {
                    toothDistanceX[i][j]=sumX-(int)(toothDistanceX[i][j]*spacesScaleValue);
                }
            }
        }
    }

    /**
     * 设置绘制大圆和内圆的半径
     */
    @Override
    public  void setDrawBigCircleAndInnerCircleSize(int bigCircleRadius, int smallCircleRadius){
        String[] depthGroup=ki.getDepth().split(";");
            String[] depthNameGroup=ki.getDepth_name().split(";");
        String[] depth;
        String[] depthName;
        Map<String, Integer> bigCircleRadiusMap;
        Map<String, Integer> smallCircleRadiusMap;
        twoDepthName=new String[ki.getRowCount()][];
        int bigRadius;  //16  大圆的半径
        int smallRadius;//6  //小圆的半径
                for (int i = 0; i < ki.getRowCount(); i++) {
                    bigRadius = bigCircleRadius;
                    smallRadius = smallCircleRadius;
                    bigCircleRadiusMap=new HashMap<>();
                    smallCircleRadiusMap=new HashMap<>();
                        if(depthGroup.length<(i+1)){
                            depth=depthGroup[depthGroup.length-1].split(",");
                            depthName=depthNameGroup[depthGroup.length-1].split(",");
                            twoDepthName[i]=new String[depthName.length];
                            for (int j = 0; j <depth.length ; j++) {
                                twoDepthName[i][j]=depthName[j];
                                int num=Integer.parseInt(depth[j]);
                                if(num==0){
                                    bigCircleRadiusMap.put(depthName[j],0);
                                    smallCircleRadiusMap.put(depthName[j],0);
                                }else {
                                    bigCircleRadiusMap.put(depthName[j], bigRadius);
                                    smallCircleRadiusMap.put(depthName[j], smallRadius);
                                    bigRadius = bigRadius +4;
                                    smallRadius = smallRadius+2;
                                }
                            }
                            bigCircleRadiusList.add(bigCircleRadiusMap);
                            smallCircleRadiusList.add(smallCircleRadiusMap);
                         }else {
                            depth=depthGroup[i].split(",");
                            depthName=depthNameGroup[i].split(",");
                            twoDepthName[i]=new String[depthName.length];
                            for (int j = 0; j <depth.length ; j++) {
                                int num=Integer.parseInt(depth[j]);
                                twoDepthName[i][j]=depthName[j];
                                if(num==0){
                                    bigCircleRadiusMap.put(depthName[j],0);
                                    smallCircleRadiusMap.put(depthName[j],0);
                                }else {
                                    bigCircleRadiusMap.put(depthName[j], bigRadius);
                                    smallCircleRadiusMap.put(depthName[j], smallRadius);
                                    bigRadius = bigRadius +4;
                                    smallRadius = smallRadius+2;
                                }
                            }
                            bigCircleRadiusList.add(bigCircleRadiusMap);
                            smallCircleRadiusList.add(smallCircleRadiusMap);
                        }
                }
    }

    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
    }

    /**
     * 自定义绘制的圆凹槽
     */
    public void customDrawCircleGroove(){
        for (int i = 0; i <toothDepthName.length ; i++) {
            for (int j = 0; j < toothDepthName[i].length; j++) {
                toothDepthName[i][j]=twoDepthName[i][0];
            }
        }
    }
    /**
     *  设置钥匙齿的深度名
     * @param toothCode
     */
    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
         String[]   allToothDepthName = toothCode.split(",");
            int k=0;
            for (int i = 0; i <toothDepthName.length; i++) {
                for (int j = 0; j <toothDepthName[i].length ; j++) {
                    toothDepthName[i][j]=allToothDepthName[k];
                    k++;
                }
            }
        }else {
            this.setToothCodeDefault();
        }
    }

    @Override
    public void redrawKey() {
        this.invalidate();
    }
    @Override
    public void setToothAmount(int toothAmount) {
        if(ki.getRowCount()==1){
            String toothCode="";
            boolean isExistValue;
            Map<String,Integer> bigCircleMap =bigCircleRadiusList.get(0);
            //判读0能不能找到数据
            if(bigCircleMap.get("0")==null){
                isExistValue=false;
            }else {
                isExistValue=true;
            }
            toothDistanceX[0]=new int[toothAmount];
            toothDepthName[0]=new  String[toothAmount];
            String[] spaceGroup = ki.getSpace().split(";");
            String[] spaces=spaceGroup[0].split(",");
            for (int i = 0; i < toothAmount; i++) {
                //转为int类型
                toothDepthName[0][i]="X";
                toothDistanceX[0][i]=Integer.parseInt(spaces[i]);
                    if(isExistValue){
                        toothCode+="X,";
                    }else {
                        toothCode+="0,";
                    }
            }
            ki.setKeyToothCode(toothCode);
            this.calculateDrawToothDistance();
            this.invalidate(); //重绘
        }
    }



    @Override
    public int getToothAmount() {
        if(ki.getRowCount()==1){
            return toothDistanceX[0].length;
        }else {
            return 0;
        }
    }

    @Override
    public ArrayList<String[]> getToothCode() {
        toothCodeList.clear();
        for (int i = 0; i <toothDepthName.length; i++) {
            toothCodeList.add(toothDepthName[i]);
        }
        return toothCodeList;
    }

    /**
     * 绘制钥匙图案
     * @param canvas
     */
    private void drawKeyPattern(Canvas canvas){
        int  patternWidth=patternLeftWidth+(int)patternRightWidth;
        int   patternRightArcValue= (int)(patternBodyHeight*0.05f);
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
            if(isOnlyDrawSidePattern){  //只有侧面的
                patternRightArcValue= (int)(patternSideHeight*0.05f);
                mPath.moveTo(0,extraTopY);
                mPath.lineTo(patternWidth-patternRightArcValue,extraTopY);
                mPath.quadTo(patternWidth,extraTopY,patternWidth, extraTopY+patternRightArcValue);
                mPath.lineTo(patternWidth,(extraTopY+patternSideHeight)-patternRightArcValue);
                mPath.quadTo(patternWidth,extraTopY+patternSideHeight,patternWidth-patternRightArcValue,extraTopY+patternSideHeight);
                mPath.lineTo(0,extraTopY+patternSideHeight);
                canvas.drawPath(mPath,mBorderPaint);
                canvas.drawPath(mPath,mKeyAppearanceColorPaint);
                mPath.reset();
                mPath.moveTo(patternLeftWidth,extraTopY);
                mPath.lineTo(patternLeftWidth,extraTopY+patternSideHeight);
                canvas.drawPath(mPath,mBorderPaint);
                mPath.reset();
            }else{
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
                canvas.drawPath(mPath,mBorderPaint);
                canvas.drawPath(mPath,mKeyAppearanceColorPaint);
                mPath.reset();
            }
        }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
            mPath.moveTo(0,extraTopY);
            mPath.lineTo(patternWidth-patternRightArcValue,extraTopY);
            mPath.quadTo(patternWidth,extraTopY,patternWidth,extraTopY+patternRightArcValue);
            mPath.lineTo(patternWidth,patternBodyMaxY-patternRightArcValue);
            mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth-patternRightArcValue,patternBodyMaxY);
            mPath.lineTo(0,patternBodyMaxY);
            canvas.drawPath(mPath,mBorderPaint);
            canvas.drawPath(mPath,mKeyAppearanceColorPaint);
            mPath.reset();
        }
        if(isDrawSidePattern){
            mPath.moveTo(0,(patternBodyMaxY+patternLeftShoulderHeight)+interval_y);
            mPath.lineTo(patternWidth-patternRightArcValue,(patternBodyMaxY+patternLeftShoulderHeight)+interval_y);
            mPath.quadTo(patternWidth,(patternBodyMaxY+patternLeftShoulderHeight)+interval_y,patternWidth,
                    (patternBodyMaxY+patternLeftShoulderHeight)+(interval_y+patternRightArcValue));
            mPath.lineTo(patternWidth,(patternBodyMaxY+patternLeftShoulderHeight)+((interval_y+patternSideHeight)-patternRightArcValue));
            mPath.quadTo(patternWidth,(patternBodyMaxY+patternLeftShoulderHeight)+(interval_y+patternSideHeight),patternWidth-patternRightArcValue,
                    (patternBodyMaxY+patternLeftShoulderHeight)+(interval_y+patternSideHeight));
            mPath.lineTo(0,(patternBodyMaxY+patternLeftShoulderHeight)+(interval_y+patternSideHeight));
            canvas.drawPath(mPath,mBorderPaint);
            canvas.drawPath(mPath,mKeyAppearanceColorPaint);
            mPath.reset();
            mPath.moveTo(patternLeftWidth,(patternBodyMaxY+patternLeftShoulderHeight)+interval_y);
            mPath.lineTo(patternLeftWidth,(patternBodyMaxY+patternLeftShoulderHeight)+(interval_y+patternSideHeight));
            canvas.drawPath(mPath,mBorderPaint);
            mPath.reset();
        }
            for (int i = 0; i <toothDepthName.length ; i++) {
                Map<String,Integer> bigCircleMap =bigCircleRadiusList.get(i);
                Map<String,Integer> smallCircleMap =smallCircleRadiusList.get(i);
                for (int j = 0; j < toothDepthName[i].length; j++) {
                    if(toothDepthName[i][j].contains(".")){
                        String[] newStr=toothDepthName[i][j].split("\\.");
                        int integer =Integer.parseInt(newStr[1]);
                        if(newStr[0].equals("X")&&newStr[0].equals("0")){
                            //明天继续
                            if(integer >=5){
                                //大圆
                                canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],bigCircleMap.get(twoDepthName[i][0]),bigCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],smallCircleMap.get(twoDepthName[i][0]),smallCirclePaint);
                            }
                        }else {
                            //等于最后一个
                            if(newStr[0].equals(twoDepthName[i][twoDepthName[i].length-1])){
                                //大圆
                                canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],bigCircleMap.get(newStr[0]),bigCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],smallCircleMap.get(newStr[0]),smallCirclePaint);
                            }else {
                                String  str="";
                                if(integer>=5){
                                    for (int k = 0; k < twoDepthName[i].length; k++) {
                                        if(newStr[0].equals(twoDepthName[i][j])){
                                            str=twoDepthName[i][j+1];
                                            break;
                                        }
                                    }
                                    //大圆
                                    canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],bigCircleMap.get(str),bigCirclePaint);
                                    //小圆
                                    canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],smallCircleMap.get(str),smallCirclePaint);
                                }else {
                                    //大圆
                                    canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],bigCircleMap.get(newStr[0]),bigCirclePaint);
                                    //小圆
                                    canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],smallCircleMap.get(newStr[0]),smallCirclePaint);
                                }
                            }
                        }
                    }else if (smallCircleMap.get(toothDepthName[i][j])!=null) {
                        //大圆
                        canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],bigCircleMap.get(toothDepthName[i][j]),bigCirclePaint);
                        //小圆
                        canvas.drawCircle(toothDistanceX[i][j],toothOrbitY[i],smallCircleMap.get(toothDepthName[i][j]),smallCirclePaint);
                    }
                }
            }
        this.drawToothOrbitPattern(canvas);
        if(isDrawToothCode){
            this.drawToothCodePattern(canvas);
        }
    }

    /**
     * 绘制的齿代码图案
     */
    private  void drawToothCodePattern(Canvas canvas){
        for (int i = 0; i < toothDepthName.length; i++) {
            for (int j = 0; j <toothDepthName[i].length ; j++) {
                Map<String,Integer> bigCircleMap =bigCircleRadiusList.get(i);
                        if(toothDepthName[i][j].contains(".")){
                            String[] newStr=toothDepthName[i][j].split("\\.");
                            int integer=Integer.parseInt(newStr[1]);
                            mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                            if(newStr[0].equals("X")||newStr.equals("0")) {
                                if (integer >= 5) {
                                    canvas.drawText(twoDepthName[i][0],toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                                }else {
                                    canvas.drawText(newStr[0],toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                                }
                            }else {
                                //等于最后一个
                                if(newStr[0].equals(twoDepthName[i][twoDepthName[i].length-1])){
                                    canvas.drawText(newStr[0],toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                                }else {
                                    String  str="";
                                    if(integer>=5){
                                        for (int k = 0; k < twoDepthName[i].length; k++) {
                                            if(newStr[0].equals(twoDepthName[i][j])){
                                                str=twoDepthName[i][j+1];
                                                break;
                                            }
                                        }
                                        canvas.drawText(str,toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                                    }else {
                                        canvas.drawText(newStr[0],toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                                    }
                                }
                            }
                        }else if(bigCircleMap.get(toothDepthName[i][j])==null){
                            mColorTextPaint.setColor(Color.parseColor("#FF000080"));
                            canvas.drawText("X",toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                        }else {
                            mColorTextPaint.setColor(Color.parseColor("#FF000080"));
                            canvas.drawText(toothDepthName[i][j],toothDistanceX[i][j],toothDepthNameYPosition[i],mColorTextPaint);
                        }
            }
        }
    }
    /**
     * 绘制齿轨道图案
     * @param canvas
     */
    private void drawToothOrbitPattern(Canvas canvas){
        for (int i = 0; i < toothOrbitY.length ; i++) {
               mPath.moveTo(patternLeftWidth, toothOrbitY[i]);
               mPath.lineTo(patternLeftWidth+patternRightWidth, toothOrbitY[i]);
        }
         canvas.drawPath(mPath,mDashedPaint);
         mPath.reset();
    }

    /**
     * 设置绘制齿代码
     */
    public  void setDrawToothCode(boolean isDrawToothCode){
        this.isDrawToothCode=isDrawToothCode;
    }
    @Override
    public void setShowArrows(boolean isShowArrows) {
                this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String toothCode="";
        boolean isNone;  //是否有不有这个数据
        for (int i = 0; i <toothDepthName.length ; i++) {
            Map<String,Integer>  map=bigCircleRadiusList.get(i);
            if(map.get("0")==null){
                isNone=false;
            }else {
                isNone=true;
            }
            for (int j = 0; j < toothDepthName[i].length; j++) {
                if(isNone==false){
                    toothCode+="0,";
                }else {
                    toothCode+=",";
                }
                toothDepthName[i][j]="X";
            }
        }

        ki.setKeyToothCode(toothCode);
    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        if(ki.getThick()==0){
            ki.setThick(210);
        }
        String keyNorm = "!SB";
        String toothCode;
        String[]  rowPosition = ki.getRow_pos().split(";");
        int rowCount=ki.getRowCount();
        String[] spaceGroup = ki.getSpace().split(";");
        toothCode= ki.getKeyToothCode().substring(0,ki.getKeyToothCode().length()-1);  //获得实际齿号
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        String[] depthGroup = ki.getDepth().split(";");
        String[] depthNameGroup = ki.getDepth_name().split(";");
        String order = "!DR1;!BC;" + keyNorm; //钥匙规范
        order=order + ki.getType() + "," ;//钥匙类型
        order=order + 10+",";//默认值
        order=order + ki.getAlign() + ","; //钥匙定位方式
        order=order + rowCount + ",";    //轴的的数量
        order=order + ki.getWidth() + ",";//钥匙宽度
        order=order + ki.getThick() + ",";//钥匙厚度
        order=order + 0+ ",";  //表示加不加切
        order=order + ki.getLength()  + ",";
        order=order + ki.getCutDepth() + ",";
        for (int i = 0; i <rowCount ; i++) {//通过轴的数量遍历数据
            order=order + rowPosition[i] + ",";//轴位置
            String[] spaces=spaceGroup[i].split(",");
            order=order + spaces.length + ",";  //钥匙齿数
            for (int j = 0; j <spaces.length ; j++) {
                order=order + spaces[j] + ",";  //钥匙的齿距数据
            }
            String[] spaceWidth=  spaceWidthGroup[i].split(",");
            for (int k = 0; k <spaces.length ; k++) {  //齿顶宽(和齿距对应)
                order=order + spaceWidth[k] + ",";
            }
            for (int l = 0; l < spaces.length; l++) {  //额外切割
                order=order + ki.getExtraCut() + ",";
            }
            String[]  depths=depthGroup[i].split(",");
            order=order + depths.length + ",";     //齿深数量
            for (int z = 0; z <depths.length ; z++) {
                order=order + depths[z] + ",";   //齿深数据
            }
            String[]  depthNames=depthNameGroup[i].split(",");
            for (int n = 0; n < depths.length; n++) {  //齿深名称
                order=order +(depthNames[n].charAt(0)+",");
            }
        }
        order=order + locatingSlot+ ",";
        order=order + toothCode+";";  //中间最后一段  第20段
                order=order + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        if(ki.getThick()==0){
            ki.setThick(210);
        }
        String keyNorm = "!SB";
        String toothCode;
        int noseCut=0;
        String DDepth="";
        if(speed==1||speed==2){
            DDepth ="0.25";
        }else if(speed==3||speed==4){
            DDepth="0.4";
        }
        String[]  rowPosition = ki.getRow_pos().split(";");
        int rowCount=ki.getRowCount();
        String[] spaceGroup = ki.getSpace().split(";");
        toothCode= ki.getKeyToothCode();  //获得实际齿号
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        String[] depthGroup = ki.getDepth().split(";");
        String[] depthNameGroup = ki.getDepth_name().split(";");
        String order = "!DR1;!BC;" + keyNorm; //钥匙规范
        order=order + ki.getType() + "," ;//钥匙类型
        order=order + 10+",";//默认值
        order=order + ki.getAlign() + ","; //钥匙定位方式
        order=order + rowCount + ",";    //轴的的数量
        order=order + ki.getWidth() + ",";//钥匙宽度
        order=order + ki.getThick() + ",";//钥匙厚度
        order=order + 0+ ",";  //表示加不加切
        order=order + ki.getLength()  + ",";
        order=order + cutDepth + ",";
        for (int i = 0; i <rowCount ; i++) {//通过轴的数量遍历数据
            order=order + rowPosition[i] + ",";//轴位置
            String[] spaces=spaceGroup[i].split(",");
            order=order + spaces.length + ",";  //钥匙齿数
            for (int j = 0; j <spaces.length ; j++) {
                order=order + spaces[j] + ",";  //钥匙的齿距数据
            }
            String[] spaceWidth=  spaceWidthGroup[i].split(",");
            for (int k = 0; k <spaces.length ; k++) {  //齿顶宽(和齿距对应)
                order=order + spaceWidth[k] + ",";
            }
            for (int l = 0; l < spaces.length; l++) {  //额外切割
                order=order + ki.getExtraCut() + ",";
            }
            String[]  depths=depthGroup[i].split(",");
            order=order + depths.length + ",";     //齿深数量
            for (int z = 0; z <depths.length ; z++) {
                order=order + depths[z] + ",";   //齿深数据
            }
            String[]  depthNames=depthNameGroup[i].split(",");
            for (int n = 0; n < depths.length; n++) {  //齿深名称
                order=order +(depthNames[n].charAt(0)+",");
            }
        }
        order=order + locatingSlot+ ",";
        order=order + toothCode+";";  //中间最后一段  第20段
        order=order +"!AC"+assistClamp+";"  // 辅助夹具
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
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
        if(isShowArrows){
            if (ki.getAlign() == 0) {
                //画红色箭头  肩部
                mPath.moveTo(90, 20);  //100
                mPath.lineTo(120, 20);//第一条  100
                mPath.lineTo(120, 12);
                mPath.lineTo(130, 24);//中间点  104
                mPath.lineTo(120, 36);
                mPath.lineTo(120, 28);
                mPath.lineTo(90, 28);
                canvas.drawPath(mPath, arrowsPaint);
                mPath.reset();
            } else if (ki.getAlign() == 1) {
                //画红色箭头  尖端
                mPath.moveTo(666, 30);//80
                mPath.lineTo(666 + 10, 20);//70
                mPath.lineTo(666 + 10, 27);//77
                mPath.lineTo(666 + 35, 27);//77
                mPath.lineTo(666 + 35, 33);//83
                mPath.lineTo(666 + 10, 33);//83
                mPath.lineTo(666 + 10, 40);//90
                mPath.lineTo(666, 30);//80
                canvas.drawPath(mPath, arrowsPaint);
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
        arrowsPaint = new Paint();
        bigCirclePaint = new Paint();
        smallCirclePaint = new Paint();
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
        mColorTextPaint.setTextSize(27);
        //画红色提示箭头的属性
        arrowsPaint.setColor(Color.RED);
        arrowsPaint.setAntiAlias(true);//去掉锯齿
        arrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        arrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //画大圆的笔
        bigCirclePaint.setAntiAlias(true);//去掉锯齿
        bigCirclePaint.setColor(Color.parseColor("#FFD700")); //黄色
        bigCirclePaint.setStyle(Paint.Style.FILL);
        bigCirclePaint.setStrokeWidth(1);
        //画小圆的笔
        smallCirclePaint.setAntiAlias(true);//去掉锯齿
        smallCirclePaint.setColor(Color.parseColor("#cc9900")); //黄色
        smallCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint.setStrokeWidth(1);
    }
}
