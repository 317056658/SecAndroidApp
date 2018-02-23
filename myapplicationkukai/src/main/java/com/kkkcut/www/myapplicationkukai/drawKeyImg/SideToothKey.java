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
 * 侧齿钥匙
 * Created by Administrator on 2017/7/26.
 */

public class SideToothKey extends Key {
    private KeyInfo ki;
    private Path mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;
    private Paint mColorTextPaint;
    private Paint mArrowsPaint;
    private int[] toothDistanceX,depthPositionY;
    private  float spacesScaleValue;
    private  double depthScaleRatio;
    private String[] toothDepthName;
    private int[] halfToothWidth;
    private float[] bottomDepthY;
    private Map<String, Integer> depthPositionMap;
    private String[]  depthName;
    private ArrayList<String[]> toothCodeList;
    private boolean isShowArrows=true;  //默认为true
    private int patternLeftWidth,patternRightWidth;
    private float patternMiddleWidth;
    private double patternTopHeight;
    private int patternBodyHeight;  //图案的身体高度
    private int extraTopY;  //额外宽度y
    private int patternHeightPercent15;
    private  boolean isDraw=true;
    private int depthPositionStartY,depthPositionEndY;
    private int patternMaxY;


    public SideToothKey(Context context,KeyInfo ki) {
        this(context,null,ki);
    }

    public SideToothKey(Context context, AttributeSet attrs, KeyInfo ki) {
        this(context,attrs,0,ki);
    }


    public SideToothKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.initPaintAndPath();
        this.ki=ki;
        depthPositionMap =new HashMap<>();
        toothCodeList =new ArrayList<>();
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
        patternLeftWidth=(int)(width*0.3f);
        patternMiddleWidth=(int)(width*0.55f);
        patternRightWidth=(int)(width*0.15f);
        extraTopY=(int)(height*0.04f);
        patternBodyHeight=(int)(height*0.66f);
        patternTopHeight=(int)(height*0.44f);
        patternHeightPercent15=(int)(height*0.15f);
        patternMaxY=extraTopY+(patternHeightPercent15+patternBodyHeight);
        this.analysisSpaceAndSpaceWidth();
        this.analysisDepthAndDepthName();
    }

    /**
     *解析space 和space宽度
     */
    private void analysisSpaceAndSpaceWidth(){
            String[] spaceGroup=ki.getSpace().split(";");
            String[] spaceWidthGroup=ki.getSpace_width().split(";");
            String[] space =spaceGroup[0].split(",");
            String[] spaceWidth=spaceWidthGroup[0].split(",");
          int [] spacesInt=new int[space.length];
          int [] spaceWidthInt=new int[spaceWidth.length];
        toothDepthName=new String[space.length];
        toothDistanceX=new int[space.length];
        //底下的深度位置
        bottomDepthY=new float[space.length];
        halfToothWidth =new int[spaceWidth.length];
        //转为int类型
        for (int i = 0; i < space.length ; i++) {
            spacesInt[i]=Integer.parseInt(space[i]);
            spaceWidthInt[i]=Integer.parseInt(spaceWidth[i]);
        }
        this.calculateDrawToothDistanceAndToothWidth(spacesInt,spaceWidthInt);
    }

    /**
     * 计算绘制的齿距和齿宽
     */
    private void calculateDrawToothDistanceAndToothWidth(int[] spacesInt,int[] spaceWidthInt){
        if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
            spacesScaleValue=patternMiddleWidth/spacesInt[0];
            // 齿距和齿宽的数组 是一样的长度
            int sumX=(int)(patternLeftWidth+patternMiddleWidth);
            for (int i = 0; i <spacesInt.length ; i++) {
                toothDistanceX[i]=sumX-(int)(spacesInt[i]*spacesScaleValue);
                halfToothWidth[i]=(int)(spaceWidthInt[i]*spacesScaleValue)/2;
            }
            // 判读齿宽
            for (int i = 0; i <toothDistanceX.length ; i++) {
                if(i==toothDistanceX.length-1){
                    int num=(toothDistanceX[i]-toothDistanceX[i-1])/2;
                    if(halfToothWidth[i]>num){
                        halfToothWidth[i]=num;
                    }
                }else {
                    int num=(toothDistanceX[i+1]-toothDistanceX[i])/2;
                    if(halfToothWidth[i]>num){
                        halfToothWidth[i]=num;
                    }
                }
            }
        }
    }

    /**
     * 解析深度和深度名
     */
    private void  analysisDepthAndDepthName(){
             String[]  depthGroup=ki.getDepth().split(";");
             String[]  depthNameGroup=ki.getDepth_name().split(";");
             String[]    depth=depthGroup[0].split(",");
            depthName=depthNameGroup[0].split(",");
      this.calculateDrawDepth(depth);
    }

    /**
     * 自定义锯齿状
     */
     public  void customDrawSerrated(){
         int  k=0;
         int j=1;
         for (int i = 0; i < toothDepthName.length ; i++) {
             if(i< depthName.length){
                 if(i==2){
                     toothDepthName[i]="X";
                 }else if(i== toothDepthName.length-1) {
                     toothDepthName[i]= depthName[k];
                 }else{
                     toothDepthName[i]= depthName[i];
                 }
             }else {
                 if(depthName.length==2){
                     toothDepthName[i]= depthName[depthName.length-j];
                     j++;
                 }else {
                     toothDepthName[i]= depthName[0];
                 }
             }
         }
     }

    /**
     * 计算绘制的深度
     */
    private void calculateDrawDepth(String[] depth){
        depthPositionY=new int[depth.length];
        depthScaleRatio=patternTopHeight/ki.getWidth();    //得到比列值
        int sumY=(int)(patternTopHeight+extraTopY);
        for (int i = 0; i < depth.length; i++) {
            depthPositionY[i]=sumY-(int)(Integer.parseInt(depth[i])*depthScaleRatio);
            depthPositionMap.put(depthName[i],depthPositionY[i]);
        }
        depthPositionStartY=depthPositionY[0];
        depthPositionEndY=depthPositionY[depthPositionY.length-1];
    }
    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
           this.isDraw=isDraw;
    }

    /**
     *绘制钥匙图案
     */
    private void  drawKeyPattern(Canvas canvas) {
        mPath.moveTo(0, patternHeightPercent15+extraTopY);
        mPath.lineTo(patternLeftWidth - halfToothWidth[0], patternHeightPercent15+extraTopY);
        for (int i = 0; i < toothDepthName.length; i++) {
            //分割齿的深度名
            if(toothDepthName[i].contains(".")){
                String[] newStr = toothDepthName[i].split("\\.");
                float decimals = Float.parseFloat("0." + newStr[1]);//转为小数
                float valueY = 0;
                int distanceY;
                Integer depthValue = depthPositionMap.get(newStr[0]);
                if (depthValue == null) {
                    if (newStr[0].equals("X") || newStr[0].equals("0")) {
                        depthValue = depthPositionMap.get(depthName[0]);
                        distanceY = depthValue - extraTopY;
                        valueY = distanceY * decimals;
                        bottomDepthY[i]=valueY;
                        mPath.lineTo(toothDistanceX[i] - halfToothWidth[i], depthValue - valueY);
                        mPath.lineTo(toothDistanceX[i] + halfToothWidth[i], depthValue - valueY);
                    }
                }else {
                        if (depthValue == depthPositionY[depthPositionY.length - 1]) {
                            mPath.lineTo(toothDistanceX[i] - halfToothWidth[i], depthValue);
                            mPath.lineTo(toothDistanceX[i] + halfToothWidth[i], depthValue);
                            bottomDepthY[i]=depthValue-extraTopY;
                            //等于最后一个深度位置不计算。
                        } else {
                            for (int j = 0; j < depthPositionY.length; j++) {
                                if (depthValue == depthPositionY[j]) {
                                    distanceY = depthPositionY[j + 1] - depthValue;
                                    valueY = distanceY * decimals;
                                    break;
                                }
                            }
                            bottomDepthY[i]=(depthValue-extraTopY)+valueY;
                            mPath.lineTo(toothDistanceX[i] - halfToothWidth[i], depthValue + valueY);
                            mPath.lineTo(toothDistanceX[i] + halfToothWidth[i], depthValue + valueY);
                        }
                    }

            }else{
                Integer    depthValue = depthPositionMap.get(toothDepthName[i]) == null ? extraTopY : depthPositionMap.get(toothDepthName[i]);
                if(depthValue!=null){
                    bottomDepthY[i]=depthValue-extraTopY;
                }
                mPath.lineTo(toothDistanceX[i] - halfToothWidth[i], depthValue);
                mPath.lineTo(toothDistanceX[i] + halfToothWidth[i], depthValue);
            }
        }
        mPath.lineTo(toothDistanceX[toothDistanceX.length-1]+halfToothWidth[halfToothWidth.length-1],extraTopY+(int)(patternTopHeight*0.14));

        mPath.lineTo(patternLeftWidth+patternMiddleWidth,extraTopY+(int)(patternTopHeight*0.14));
        mPath.lineTo(patternLeftWidth+patternMiddleWidth, patternHeightPercent15+extraTopY);
        int patternWidth=patternLeftWidth+(int)patternMiddleWidth+patternRightWidth;
        int rightArc=(int)(patternBodyHeight*0.1f);
        mPath.lineTo(patternWidth-rightArc,extraTopY+patternHeightPercent15);
        mPath.quadTo(patternWidth,extraTopY+patternHeightPercent15,patternWidth,(extraTopY+patternHeightPercent15)+rightArc);
        mPath.lineTo(patternWidth,patternMaxY-rightArc);
        mPath.quadTo(patternWidth,patternMaxY,patternWidth-rightArc,patternMaxY);
        mPath.lineTo(toothDistanceX[toothDistanceX.length-1]+halfToothWidth[toothDistanceX.length-1],patternMaxY);
        int  bottomY=patternMaxY-(int)(patternBodyHeight*0.3f);
        mPath.lineTo(toothDistanceX[toothDistanceX.length-1]+halfToothWidth[toothDistanceX.length-1],bottomY);
        //下面齿的变化
        for (int i = toothDistanceX.length-1; i >=0; i--) {
            mPath.lineTo(toothDistanceX[i]+halfToothWidth[i],bottomY+bottomDepthY[i]);
            mPath.lineTo(toothDistanceX[i]-halfToothWidth[i],bottomY+bottomDepthY[i]);
        }
        mPath.lineTo(patternLeftWidth-halfToothWidth[0],patternMaxY);
        mPath.lineTo(0,patternMaxY);
        canvas.drawPath(mPath,mBorderPaint);
        canvas.drawPath(mPath,mKeyAppearanceColorPaint);
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
      int start_x=patternLeftWidth-(int)(patternLeftWidth*0.14);
      int end_y= (int) (patternLeftWidth+patternMiddleWidth);
      for (int i = 0; i <depthPositionY.length ; i++) {
            mPath.moveTo(start_x,depthPositionY[i]);
            mPath.lineTo(end_y,depthPositionY[i]);
      }
      canvas.drawPath(mPath,mDashedPaint);
      mPath.reset();
  }
    /**
     * 绘制齿代码
     * @param canvas
     */
  private void drawToothCodePattern(Canvas canvas){
      //画齿位
      for (int i = 0; i < toothDistanceX.length ; i++) {
          mPath.moveTo(toothDistanceX[i], depthPositionStartY);
          mPath.lineTo(toothDistanceX[i], depthPositionEndY);
      }
      canvas.drawPath(mPath, mDashedPaint);
      mPath.reset();
      //画齿名的颜色
      for (int i = 0; i < toothDepthName.length ; i++) {
          if(toothDepthName[i].contains(".")){
              mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
              String[] newStr = toothDepthName[i].split("\\.");
              int num=Integer.parseInt(newStr[1]);  //转为int
              if(depthPositionMap.get(newStr[0])== null){
                  if(newStr[0].equals("X")||newStr[0].equals("0")){
                      if(num>=5){
                          canvas.drawText(depthName[0],toothDistanceX[i],depthPositionEndY +50,mColorTextPaint);
                      }else {
                          canvas.drawText("X",toothDistanceX[i],depthPositionEndY +50,mColorTextPaint);
                      }
                  }else {
                      canvas.drawText(newStr[0],toothDistanceX[i],depthPositionEndY +50,mColorTextPaint);
                  }
              }else {
                  if(num>=5){
                      if(newStr[0].equals(depthName[depthName.length-1])){
                          canvas.drawText(newStr[0], toothDistanceX[i], depthPositionEndY + 50, mColorTextPaint);//画红色的字
                      }else {
                          for (int j = 0; j < depthName.length ; j++) {
                              if(depthName[j].equals(newStr[0])){
                                  canvas.drawText(depthName[j+1], toothDistanceX[i], depthPositionEndY + 50, mColorTextPaint);//画红色的字
                                  break; //跳出循环
                              }
                          }
                      }
                  }else {
                      canvas.drawText(newStr[0], toothDistanceX[i], depthPositionEndY + 50, mColorTextPaint);//画红色的字
                  }
              }
          }else {
              mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
              canvas.drawText(toothDepthName[i], toothDistanceX[i], depthPositionEndY + 50, mColorTextPaint);//画红色的字
          }
      }
  }



    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
            //设置深度名
            String[]  keyToothDepthName= toothCode.split(",");
            for (int i = 0; i <keyToothDepthName.length ; i++) {
                toothDepthName[i]=keyToothDepthName[i];
            }
        }else {
            this.setToothCodeDefault();  //设置toothCode为默认
        }
    }

    @Override
    public void redrawKey() {
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
        toothCodeList.add(toothDepthName);
        return toothCodeList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
             this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String toothCode="";
        for (int i = 0; i <toothDepthName.length ; i++) {
            toothDepthName[i]="X";
            toothCode+="X,";
        }
        ki.setKeyToothCode(toothCode);
    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";
        String toothQuantity ="";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity ;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        String[] spaceGroup = ki.getSpace().split(";");

        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[] spaces = spaceGroup[i].split(",");
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
                + 0 + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getGuide() + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + ki.getCutDepth() + ","   //切割深度
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
        String keyNorm = "!SB";
        int knifeType=1;  //到类型
        int noseCut=0;
        String  DDepth="";
        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        String[] depthGroup = ki.getDepth().split(";");
        String[] depthNameGroup = ki.getDepth_name().split(";");

        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + 0 + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + 0+ ","   //切割深度
                + spaceGroup[0].split(",").length+","   // 齿的数量
                + spaceGroup[0]+","     //齿位置数据
                + spaceWidthGroup[0]+","   //齿顶宽数据
                + depthGroup[0].split(",").length + ","//齿深数量
                + depthGroup[0]+","      //齿深数据
                + locatingSlot+ ","  //钥匙的定位位置
                + ki.getKeyToothCode()   //齿号代码  其实都是零
                + 0 + ","  // 鼻部长度
                + 0 + ","  //槽宽
                + depthNameGroup[0]+","
                + 0 + ";"
                +"!AC"+assistClamp+";"  // 辅助夹具
                +"!ST"+knifeType+"," //刀类型
                +cutterDiameter+";"    //刀的规格（刀的直径）
                +"!CK"
                +speed+"," //速度
                + DDepth +","//D深度
                +ZDepth+","  //Z深度
                +noseCut+","  //鼻部切割
                +detectionMode+";";  //切割钥匙的检测方式      //最后齿或扩展切割
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
            if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
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
    private void initPaintAndPath() {
        mPath = new Path();
        mBorderPaint = new Paint();
        mKeyAppearanceColorPaint = new Paint();
        mDashedPaint = new Paint();
        mColorTextPaint = new Paint();
        mArrowsPaint = new Paint();
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
        mColorTextPaint.setFakeBoldText(true);  //设置为粗体
        mColorTextPaint.setTextSize(35);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(1); //设置画线的宽度
    }
}
