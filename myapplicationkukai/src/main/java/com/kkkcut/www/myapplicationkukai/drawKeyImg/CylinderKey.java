package com.kkkcut.www.myapplicationkukai.drawKeyImg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Point;
import com.kkkcut.www.myapplicationkukai.entity.TempAnglePoint;

import java.util.ArrayList;

/**
 * 圆筒钥匙
 * Created by Administrator on 2017/7/24.
 */

public class CylinderKey extends Key{
    private KeyInfo ki;
    private Path mPath;
    private Paint mPatternColorPaint;
    private Paint mBluePaint;
    private Paint mColorTextPaint;
   private String[] toothDepthName;
    private final  static   String TAG="DrawCylinderKey";
    private   float  x=300,y=200;
    private ArrayList<Point> pointList;
    private float innerCircleRadius;
    private String[] depthName;
    private  ArrayList<String[]> toothCodeList;
    private int circleOrigin_x, circleOrigin_y;
    private int outerCircleRadius;
    private  float thicknessScaleValue;
    private int rectRight;
    private String  strDepthNames;
    private boolean  isDrawCircle;
    private float circleRadius;   //半径
    public CylinderKey(Context context,KeyInfo ki) {
        this(context,null,ki);
    }
    public CylinderKey(Context context, AttributeSet attrs,KeyInfo ki) {
        this(context,attrs,0,ki);
    }
    public CylinderKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.ki=ki;
        toothCodeList=new ArrayList<>();
        this.initPaintAndPath();
    }
    /**
     * 设置图案大小
     * @param width
     * @param height
     */

    @Override
    public void setDrawPatternSize(int width, int height) {
        circleOrigin_x =width/2;
        circleOrigin_y =height/2;
        x=circleOrigin_x;
        y= circleOrigin_y;
        outerCircleRadius=(int)(circleOrigin_y *0.84f);
        float patternHeight=height;  //转为float
        this.analysisThick(patternHeight);
        this.analysisSpace();
        this.analysisDepthName();
    }

    /**
     * 解析厚度
     * @param patternHeight
     */
    private void analysisThick(float patternHeight){
        thicknessScaleValue=patternHeight/ki.getWidth();
        //计算内圆的半径
        innerCircleRadius=outerCircleRadius-(int)(ki.getThick()*thicknessScaleValue);
        rectRight =(int)(circleOrigin_x-innerCircleRadius)+(int)(innerCircleRadius*0.2f);
    }
    /**
     * 解析深度名
     */
    private void   analysisDepthName(){
        String[] depthNameGroup=ki.getDepth_name().split(";");
        depthName=depthNameGroup[0].split(",");
        for (int i = 0; i <depthName.length ; i++) {
            strDepthNames+=depthName[i];     //把深度名全部转为一个字符串
        }
    }
    /**
     * 解析space
     */
    private void analysisSpace(){
        String[]    spaceGroup    =ki.getSpace().split(";");
        String[]  spaces= spaceGroup[0].split(",");
       int[]   circleAngle =new int[spaces.length];
        toothDepthName=new String[spaces.length];
        // spaces数据转为角度
        for (int i = 0; i < spaces.length; i++) {
            circleAngle[i]=Integer.parseInt(spaces[i])/100;
        }
        this.calculateAnglePointPosition(circleAngle);
    }
    /**
     * 计算角度 点的位置
     */
    private void calculateAnglePointPosition(int[] circleAngle){
        double pi=Math.PI;
        pointList=new ArrayList<>();
        //根据圆计算每个角度的对应位置 x和y;
        for (int i = 0; i < circleAngle.length; i++) {
            //得到每个点对应的度数
            Point point = new Point();
            TempAnglePoint tempAnglePoint = new TempAnglePoint();
            //分情况
            if (circleAngle[i] > 0 && circleAngle[i] <= 90) {
                if (circleAngle[i] == 90) {
                    point.setX(x);
                    point.setY(y-outerCircleRadius);
                } else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.cos(circleAngle[i]*pi/180) * outerCircleRadius));
                    tempAnglePoint.setY(getAbsoluteValue(Math.sin(circleAngle[i]*pi/180) * outerCircleRadius));
                    point.setX(x - tempAnglePoint.getX());
                    point.setY(y - tempAnglePoint.getY());
                }
            }
            else if (circleAngle[i] > 90 && circleAngle[i] <= 180) {
                if (circleAngle[i] == 180) {
                    point.setX(x+ outerCircleRadius);
                    point.setY(y);
                } else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.sin((circleAngle[i]-90)*pi/180) * outerCircleRadius));
                    tempAnglePoint.setY(getAbsoluteValue(Math.cos((circleAngle[i]-90)*pi/180) * outerCircleRadius));
                    point.setX(x + tempAnglePoint.getX());
                    point.setY(y - tempAnglePoint.getY());
                }
            } else if (circleAngle[i] > 180 && circleAngle[i] <= 270) {
                if (circleAngle[i] == 270) {
                    point.setX(x);
                    point.setY((y+outerCircleRadius));
                } else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.cos((circleAngle[i]-180)*pi/180) * outerCircleRadius));
                    tempAnglePoint.setY(getAbsoluteValue(Math.sin((circleAngle[i]-180)*pi/180) * outerCircleRadius));
                    point.setX((x + tempAnglePoint.getX()));
                    point.setY(y + tempAnglePoint.getY());
                }
            } else {
                if(circleAngle[i]==360){
                    point.setX(x-outerCircleRadius);
                    point.setY(y);
                }else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.cos((360- circleAngle[i])*pi/180) * outerCircleRadius));
                    tempAnglePoint.setY(getAbsoluteValue(Math.sin((360- circleAngle[i])*pi/180) * outerCircleRadius));
                    point.setX( (x - tempAnglePoint.getX()));
                    point.setY( (y + tempAnglePoint.getY()));
                }
            }
            pointList.add(point);
        }
    }
    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
    }
    @Override
    public void setToothCode(String toothCode) {
        if(!TextUtils.isEmpty(toothCode)) {
            toothDepthName= toothCode.split(",");
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
        String[] spaceGroup = ki.getSpace().split(";");
        String[] depthGroup=ki.getDepth().split(";");
        String[] depthNameGroup=ki.getDepth_name().split(";");

        String order = "!DR1;!BC;" + keyNorm; //钥匙规范
        order=order + ki.getType() + "," ;//钥匙类型
        order=order + 10+",";//默认值
        order=order + ki.getWidth()+ ",";    //钥匙的宽度
        order=order + ki.getThick() + ",";   //钥匙的厚度
        order=order + ki.getCutDepth()+ ","; //切割深度
        order=order +spaceGroup[0].split(",").length+",";  //space的长度
        order=order +spaceGroup[0]+",";   //
        order=order +depthGroup[0].split(",").length+"," ;  //
        order=order + depthGroup[0]+",";
        order=order + depthNameGroup[0]+",";    //深度名
        order=order +   ki.getKeyToothCode();
        order=order+"0,";
        order=order  +"0;";
        order=order + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        //明天继续切割指令
        String keyNorm = "!SB";
        int knifeType=1;  //到类型
        int noseCut=0;
        String DDepth ="";
        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        String[] spaceGroup = ki.getSpace().split(";");
        String[] depthGroup=ki.getDepth().split(";");
        String[] depthNameGroup=ki.getDepth_name().split(";");

        String order = "!DR1;!BC;" + keyNorm
                    + ki.getType() + "," //钥匙类型
                     + 10+","//默认值
                     + ki.getWidth()+ ","    //钥匙的宽度
                    + ki.getThick() + "," //钥匙的厚度
                   + ki.getCutDepth()+ ","//切割深度
                   +spaceGroup[0].split(",").length+"," //space的长度
                   + spaceGroup[0]+","   //
                  +depthGroup[0].split(",").length+","   //
                  +depthGroup[0]+","
                  +depthNameGroup[0]+","   //深度名
                  +ki.getKeyToothCode()
                  +"0,"
                  +"0;"
                  +"!AC"+assistClamp+";"  // 辅助夹具
                  +"!ST"+knifeType+"," //刀类型
                  +cutterDiameter+";"    //刀的规格（刀的直径）
                  +"!CK"
                  +speed+"," //速度
                  +DDepth +","//D深度
                  +ZDepth+","  //Z深度
                  +noseCut+","  //鼻部切割
                  +detectionMode+";";  //切割钥匙的检测方式;
        return order;
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
         //保存View设置的大小
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }

    /**
     * 绘制钥匙的图案
     * @param canvas
     */
    private  void drawKeyPattern(Canvas canvas){
        //外圆
        mPath.addCircle(circleOrigin_x, circleOrigin_y,outerCircleRadius, Path.Direction.CW);
        mPatternColorPaint.setColor(Color.parseColor("#CCCCCC"));  //画笔的颜色
        canvas.drawPath(mPath, mPatternColorPaint);
        mPath.reset();
        //内圆
        mPath.addCircle(circleOrigin_x, circleOrigin_y,innerCircleRadius, Path.Direction.CW);
        mPatternColorPaint.setColor(Color.parseColor("#A9A9A9"));  // 设置画笔的颜色
        canvas.drawPath(mPath, mPatternColorPaint);
        mPath.reset();
        mPatternColorPaint.setColor(Color.parseColor("#CCCCCC"));  //画笔的颜色
        mPath.addRect(3,circleOrigin_y-(int)(innerCircleRadius*0.1f), rectRight,circleOrigin_y+(int)(innerCircleRadius*0.1f),Path.Direction.CW);
        canvas.drawPath(mPath, mPatternColorPaint);
        mPath.reset();
        if(isDrawCircle){   //是否绘制为圆
            mPatternColorPaint.setColor(Color.RED);
            for (int i = 0; i < pointList.size(); i++) {
                Point point=pointList.get(i);
                mPath.addCircle(point.getX(), point.getY(),circleRadius, Path.Direction.CW);
            }
            mPatternColorPaint.setColor(Color.parseColor("#FF000080"));
            canvas.drawPath(mPath,mPatternColorPaint);
            mPath.reset();
        }else {
            for (int i = 0; i < toothDepthName.length; i++) {
                Point point=pointList.get(i);
                if(toothDepthName[i].contains(".")){  //有小数的
                    mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                    String[] newStr =toothDepthName[i].split("\\.");
                    int num =Integer.parseInt(newStr[1]);
                    if(strDepthNames.contains(newStr[0])==false){
                        if(newStr[0].equals("X")||newStr[0].equals("0")){
                            if(num >=5){  //x的小数大于5  就拿深度名数组第一个下标元素
                                canvas.drawText(depthName[0],point.getX(),point.getY()+10, mColorTextPaint);
                            }else {
                                canvas.drawText("X",point.getX(),point.getY()+10, mColorTextPaint);
                            }
                        }else {
                            canvas.drawText(newStr[0],point.getX(),point.getY()+10, mColorTextPaint);
                        }
                    }else {
                        String depthName="";
                        if(num >=5){  //x的小数大于5  就拿深度名数组第一个下标元素
                            if(newStr[0].equals(this.depthName[this.depthName.length-1])){
                                canvas.drawText(newStr[0],point.getX(),point.getY()+10, mColorTextPaint);
                            }else {
                                for (int j = 0; j < this.depthName.length ; j++) {
                                    if(newStr[0].equals(this.depthName[j])) {  //不等于最后一个 就拿下一个深度名
                                        depthName= this.depthName[j+1];
                                        break;
                                    }
                                }
                            }
                            canvas.drawText(depthName,point.getX(),point.getY()+10, mColorTextPaint);
                        }else {   //不大于等于5
                            canvas.drawText(newStr[0],point.getX(),point.getY()+10, mColorTextPaint);
                        }
                    }
                } else {
                    mColorTextPaint.setColor(Color.parseColor("#FF000080"));
                    canvas.drawText(toothDepthName[i],point.getX(),point.getY()+10, mColorTextPaint);
                }
            }
        }
    }

    /**
     * 设置 绘制齿代码为圆形
     */
    public  void  setDrawToothCodeForCirclePattern(boolean isDrawCircle,int circleRadius){
          this.circleRadius=circleRadius;
          this.isDrawCircle=isDrawCircle;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
    }
    /**
     * 无法确定计算出的cos和sin值 是正数 还是负数，所以取绝对值
     * @return
     */
    private float  getAbsoluteValue(double  value){
        return   (float) Math.abs(value);
    }
    private void initPaintAndPath() {
        mPath = new Path();
        mPatternColorPaint = new Paint();
        mBluePaint = new Paint();
        mColorTextPaint = new Paint();
        mPatternColorPaint.setAntiAlias(true);//去掉抗锯齿
        mPatternColorPaint.setStyle(Paint.Style.FILL);//设置画笔风格为描边
        mPatternColorPaint.setStrokeWidth(1);
        //画红色字体的笔
        mColorTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mColorTextPaint.setFakeBoldText(true);  //设置为粗体
        mColorTextPaint.setTextAlign(Paint.Align.CENTER);
        mColorTextPaint.setTextSize(34);
    }
}