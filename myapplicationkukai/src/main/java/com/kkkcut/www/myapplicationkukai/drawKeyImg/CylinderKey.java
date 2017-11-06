package com.kkkcut.www.myapplicationkukai.drawKeyImg;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Point;
import com.kkkcut.www.myapplicationkukai.entity.TempAnglePoint;

import java.util.ArrayList;

/**
 * 圆筒钥匙
 * Created by Administrator on 2017/7/24.
 */

public class CylinderKey extends Key{
    private KeyInfo p;
    private Path path;
    private Paint outerCirclePaint;
    private Paint innerCirclePaint;
    private Paint bluePaint;
    private Paint redPaint;
   private String[] toothDepthName;
    private   final static int OUTER_RADIUS=90;   //  外圆半径
    private int[]  circleAngles;
    private final  static   String TAG="DrawCylinderKey";
    private   float  x=300,y=200;
    private ArrayList<Point> pointList;
    private float innerCircleRadius,right;
    private String[] depthNames;
    private  ArrayList<String[]>   toothDepthNameList;
    private boolean isShowArrows=true;  //默认为true

    public CylinderKey(Context context) {
        super(context);

    }

    public CylinderKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CylinderKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDrawPatternSize(int width, int height) {

    }

    @Override
    public void setShowDrawDepthAndDepthName(boolean isDraw) {

    }


    @Override
    public void setToothDepthName(String depthName) {
        if(!TextUtils.isEmpty(depthName)) {
            toothDepthName=depthName.split(",");
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
        toothDepthNameList.add(toothDepthName);
        return toothDepthNameList;
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
        String keyNorm = "!SB";
        String toothQuantity ="";
        String toothPositionData = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int grooveWidth;
        String[] spaceGroup = p.getSpace().split(";");

        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[]   spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothCode+=",";
            }
        }
        String[] depthGroup = p.getDepth().split(";");
        toothDepthQuantity = depthGroup[0].split(",").length;//齿深数量(第一组)
        String[] depths = depthGroup[0].split(",");//齿深数据(第一组)
        for (int i = 0; i < depths.length; i++) {
            toothDepthData += (depths[i] + ",");
        }
        String[] depthNameGroup = p.getDepth_name().split(";");
        String[]  depthNames=depthNameGroup[0].split(",");
        for (int i = 0; i < depthNames.length; i++) {//齿深名称(第一组)
            //获得齿的深度名
            toothDepthName += (depthNames[i] + ",");
        }
        if(p.getGroove()==0){
            grooveWidth=400;
        }else {
            grooveWidth=p.getGroove();
        }
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + 10 + ","//  默认值
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + p.getCutDepth() + ","       // 表示加不加切
                + p.getLength() + ","//钥匙片长度
                + p.getCutDepth() + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + toothDepthName  //深度名
                + toothCode   //齿号代码  其实都是零
                + grooveWidth+","  //槽宽
                + 0 + ";"//  填充为0
                + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm = "!SB";
        String toothQuantity ="";
        String toothPositionData = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int grooveWidth;
        int knifeType=1;  //到类型
        int noseCut=0;
        String DDepth ="";
        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        String[] spaceGroup = p.getSpace().split(";");

        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[]   spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothCode+=",";
            }
        }
        String[] depthGroup = p.getDepth().split(";");
        toothDepthQuantity = depthGroup[0].split(",").length;//齿深数量(第一组)
        String[] depths = depthGroup[0].split(",");//齿深数据(第一组)
        for (int i = 0; i < depths.length; i++) {
            toothDepthData += (depths[i] + ",");
        }
        String[] depthNameGroup = p.getDepth_name().split(";");
        String[]  depthNames=depthNameGroup[0].split(",");
        for (int i = 0; i < depthNames.length; i++) {//齿深名称(第一组)
            //获得齿的深度名
            toothDepthName += (depthNames[i] + ",");
        }
        if(p.getGroove()==0){
            grooveWidth=400;
        }else {
            grooveWidth=p.getGroove();
        }

        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + 10 + ","//  默认值
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + p.getCutDepth() + ","       // 表示加不加切
                + p.getLength() + ","//钥匙片长度
                + p.getCutDepth() + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + toothDepthName  //深度名
                + toothCode   //齿号代码  其实都是零
                + grooveWidth+","  //槽宽
                + 0 + ";"//  填充为0
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //外圆
        path.addCircle(300,200,OUTER_RADIUS, Path.Direction.CW);// 顺时针
        canvas.drawPath(path,outerCirclePaint);
        path.reset();
        //内圆
        path.addCircle(300,200,innerCircleRadius, Path.Direction.CW);
        canvas.drawPath(path, innerCirclePaint);
        path.reset();
        // 绘制矩形
        path.addRect(190,195,right+14,205,Path.Direction.CW);
        //改变颜色
        outerCirclePaint.setColor(Color.parseColor("#A9A9A9"));
        canvas.drawPath(path,outerCirclePaint);
        path.reset(); //重置 清楚这个路径对象的所有点
        for (int i = 0; i < pointList.size(); i++) {
            Point point=pointList.get(i);
            if(toothDepthName[i].equals("X")){
                 canvas.drawText("X",point.getX(),point.getY()+10,bluePaint);
            }else if(toothDepthName[i].contains(".")){  //有小数的
               String[] strings =toothDepthName[i].split("\\.");
                int number=Integer.parseInt(strings[1]);
                  if(toothDepthName[i].contains("X")){   //  判断为X的
                        if(number>=5){  //x的小数大于5  就拿深度名数组第一个下标元素
                            canvas.drawText(depthNames[0],point.getX(),point.getY()+10,redPaint);
                        }else {
                            canvas.drawText("X",point.getX(),point.getY()+10,redPaint);
                        }
                  }else {
                      if(number>=5){  //x的小数大于5  就拿深度名数组第一个下标元素
                          String depthName="";
                          for (int j = 0; j <depthNames.length ; j++) {
                              //等于最后一个
                              if(strings[0].equals(depthNames[depthNames.length-1])){
                                  depthName=""+(depthNames.length+1);
                                  break;
                              }else if(strings[0].equals(depthNames[j])) {  //不等于最后一个 就拿下一个深度名
                                  depthName= depthNames[j+1];
                                  break;
                              }
                          }
                           if(depthName.isEmpty()){
                               depthName=strings[0];
                           }
                          canvas.drawText(depthName,point.getX(),point.getY()+10,redPaint);
                      }else {
                          canvas.drawText(strings[0],point.getX(),point.getY()+10, redPaint);
                      }
                  }
            }else {  //没有小数的
                String  depthName="";
                for (int j = 0; j <depthNames.length ; j++) {
                      if(toothDepthName[i].equals(depthNames[j])){
                          depthName=depthNames[j];
                          break;
                      }
                }
                if(depthName.isEmpty()){
                    depthName=toothDepthName[i];
                }
                canvas.drawText(depthName,point.getX(),point.getY()+10,bluePaint);
            }
        }
    }

    /**
     *   准备绘制钥匙的数据
     * @param p
     */
    @Override
    public void setNeededDrawAttribute(KeyInfo p) {
        pointList=new ArrayList<>();
        toothDepthNameList=new ArrayList<>();
        this.p=p;
        initPaintAndPathAttribute();//初始化路径和画笔
             String[]    spaceGroup    =p.getSpace().split(";");
             String[]  spaces= spaceGroup[0].split(",");
        circleAngles=new int[spaces.length];
          double pi=Math.PI;

          // spaces数据转为角度
        toothDepthName=new String[spaces.length];
        for (int i = 0; i < spaces.length; i++) {
            circleAngles[i]=Integer.parseInt(spaces[i])/100;
            Log.d("角度是", "setNeededDrawAttribute: "+circleAngles[i]);
            toothDepthName[i]="X";
        }
        //根据圆计算每个角度的对应位置 x和y;
        for (int i = 0; i < circleAngles.length; i++) {
            //得到每个点对应的度数
            Point point = new Point();
            TempAnglePoint tempAnglePoint = new TempAnglePoint();
            //分情况
            if (circleAngles[i] > 0 && circleAngles[i] <= 90) {
                if (circleAngles[i] == 90) {
                    point.setX(x);
                    point.setY(y-OUTER_RADIUS);
                } else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.cos(circleAngles[i]*pi/180) * OUTER_RADIUS));
                    tempAnglePoint.setY(getAbsoluteValue(Math.sin(circleAngles[i]*pi/180) * OUTER_RADIUS));
                    point.setX( x - tempAnglePoint.getX());
                    point.setY( y - tempAnglePoint.getY());
                }
            }
            else if (circleAngles[i] > 90 && circleAngles[i] <= 180) {
                if (circleAngles[i] == 180) {
                    point.setX(x+ OUTER_RADIUS);
                    point.setY(y);
                } else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.sin((circleAngles[i]-90)*pi/180) * OUTER_RADIUS));
                    tempAnglePoint.setY(getAbsoluteValue(Math.cos((circleAngles[i]-90)*pi/180) * OUTER_RADIUS));
                    point.setX( x + tempAnglePoint.getX());
                    point.setY(y - tempAnglePoint.getY());
                }
            } else if (circleAngles[i] > 180 && circleAngles[i] <= 270) {
                if (circleAngles[i] == 270) {
                    point.setX(x);
                    point.setY((y+OUTER_RADIUS)+5);
                } else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.cos((circleAngles[i]-180)*pi/180) * OUTER_RADIUS));
                    tempAnglePoint.setY(getAbsoluteValue(Math.sin((circleAngles[i]-180)*pi/180) * OUTER_RADIUS));
                    point.setX((x + tempAnglePoint.getX())+5);
                    point.setY(y + tempAnglePoint.getY());
                }
            } else {
                if(circleAngles[i]==360){
                    point.setX(x-OUTER_RADIUS);
                    point.setY(y);
                }else {
                    tempAnglePoint.setX(getAbsoluteValue(Math.cos((360-circleAngles[i])*pi/180) * OUTER_RADIUS));
                    tempAnglePoint.setY(getAbsoluteValue(Math.sin((360-circleAngles[i])*pi/180) * OUTER_RADIUS));
                    point.setX( (x - tempAnglePoint.getX())-5);
                    point.setY( (y + tempAnglePoint.getY())+2);
                }
            }
            pointList.add(point);
        }
          //计算实际钥匙的数据宽度 得到比例值
         float scaleValue= 180f/p.getWidth();
        // 计算实际钥匙的数据厚度  得到厚度是好多
        float thickness  =p.getThick()*scaleValue;
        innerCircleRadius =90-thickness;
        Log.d("内圆的半径", "setNeededDrawAttribute: "+innerCircleRadius);
        right =300-innerCircleRadius;
        //分割深度名
        String[] depthNameGroup=p.getDepth_name().split(";");
        depthNames = depthNameGroup[0].split(",");

    }



    /**
     * 无法确定计算出的cos和sin值 是正数 还是负数，所以取绝对值
     * @return
     */
    private float  getAbsoluteValue(double  value){
        return   (float) Math.abs(value);
    }
    private void initPaintAndPathAttribute() {
        path = new Path();
        outerCirclePaint = new Paint();
        innerCirclePaint = new Paint();
        bluePaint = new Paint();
        redPaint = new Paint();
        //画钥匙外圆
        outerCirclePaint.setAntiAlias(true);//去掉抗锯齿
        outerCirclePaint.setColor(Color.parseColor("#CCCCCC"));  //画笔的颜色
        outerCirclePaint.setStyle(Paint.Style.FILL);//设置画笔风格为描边
        outerCirclePaint.setStrokeWidth(1);
        //画钥匙内圆
        innerCirclePaint.setColor(Color.parseColor("#8E8E8E"));
        innerCirclePaint.setAntiAlias(true);
        innerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint.setStrokeWidth(1);
        //画蓝色字体的笔
        bluePaint.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        bluePaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        bluePaint.setFakeBoldText(true);  //设置为粗体
        bluePaint.setTextAlign(Paint.Align.CENTER);
        bluePaint.setTextSize(40);
        //画红色字体的笔
        redPaint.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        redPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        redPaint.setFakeBoldText(true);  //设置为粗体
        redPaint.setTextAlign(Paint.Align.CENTER);
        redPaint.setTextSize(40);



    }
}
