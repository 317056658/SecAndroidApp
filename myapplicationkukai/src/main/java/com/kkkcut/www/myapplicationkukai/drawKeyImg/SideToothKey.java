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
 * 侧齿钥匙
 * Created by Administrator on 2017/7/26.
 */

public class SideToothKey extends Key {
    private KeyInfo p;
    private Path path;
    private Paint blackPaint;
    private Paint grayPaint;
    private Paint dashedPaint;
    private Paint bluePaint;
    private Paint arrowsPaint;
    private Paint redPaint;
    private String[] spaceGroup;
    private int[] toothDistanceX,depthPositionY;
    private  float keyBodyHeightY = 100, keyBodyWidthX = 360;
    private  float spacesScaleValue, depthScaleRatio;
    private int keySumX,toothWidth;
    private String[] toothDepthName;
    private int[] toothWidthX,bottomDepthY;
    private int excessY,maxY;
   private  int  depthStartY, depthEndY;
    private Map<String, Integer> depthDataMap;
   private String[]  depthName;
    private ArrayList<String[]> toothDepthNameList;
    private boolean isShowArrows=true;  //默认为true


    public SideToothKey(Context context) {
        super(context);
        depthDataMap =new HashMap<>();
        toothDepthNameList=new ArrayList<>();

    }

    public SideToothKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SideToothKey(Context context, AttributeSet attrs, int defStyleAttr) {
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
            //设置深度名
            String[]  keyToothDepthName= depthName.split(",");
            for (int i = 0; i <keyToothDepthName.length ; i++) {
                toothDepthName[i]=keyToothDepthName[i];
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
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        String[] spaceGroup = p.getSpace().split(";");

        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[] spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothCode += "0,";  //齿号
            }
        }
        String[] spaceWidthGroup = p.getSpace_width().split(";");
        for (int w = 0; w < spaceWidthGroup.length; w++) {
            String[] spacesWidth = spaceWidthGroup[w].split(",");
            for (int i = 0; i < spacesWidth.length; i++) {
                toothWidth += (spacesWidth[i] + ",");
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
        lastToothOrExtraCut = p.getLastBitting();
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + p.getAlign() + ","//钥匙定位方式
                + 0 + ","// 有效边
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + p.getGuide() + ","       // 表示加不加切
                + p.getLength() + ","//钥匙片长度
                + p.getCutDepth() + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码  其实都是零
                + p.getNose() + ","  // 鼻部长度
                + p.getGroove() + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm = "!SB";
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
        String  DDepth="";
        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        String[] spaceGroup = p.getSpace().split(";");
        toothCode=p.getKeyToothCode();  //获得实际的齿号
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[] spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)

            }
        }
        String[] spaceWidthGroup = p.getSpace_width().split(";");
        for (int w = 0; w < spaceWidthGroup.length; w++) {
            String[] spacesWidth = spaceWidthGroup[w].split(",");
            for (int i = 0; i < spacesWidth.length; i++) {
                toothWidth += (spacesWidth[i] + ",");
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

        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + p.getAlign() + ","//钥匙定位方式
                + 0 + ","// 有效边
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切
                + p.getLength() + ","//钥匙片长度
                + 0+ ","   //切割深度
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
                + lastToothOrExtraCut + ";"
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
    public void setNeededDrawAttribute(KeyInfo p) {
       this.p=p;
        //初始化路径和画笔
        initPaintAndPathAttribute();
        // 分割space 间隔
        spaceGroup = p.getSpace().split(";");
        String[]   space=spaceGroup[0].split(",");

        //根据space数组的长度 new一个齿距数组
        toothDistanceX=new int[space.length];
        //得到spaces比例值
        spacesScaleValue=keyBodyWidthX/Integer.parseInt(space[0]);

        keySumX=320+(int)keyBodyWidthX;
        toothDepthName=new String[space.length];
          //分割齿宽
        toothWidth=34;
          String[] spaceWidthGroup=p.getSpace_width().split(";");
            String[]  spaceWidth =spaceWidthGroup[0].split(",");
        toothWidthX=new int[spaceWidth.length];
        //换算齿宽
        for (int i = 0; i <spaceWidth.length ; i++) {
           int width = (int)(Integer.parseInt(spaceWidth[i])*spacesScaleValue);
            if(width>toothWidth){
                toothWidthX[i]=toothWidth/2;
            }else {
                toothWidthX[i]=width/2;
            }
        }
        //换算齿距
        for (int i = 0; i <toothDistanceX.length ; i++) {
            toothDistanceX[i]= (keySumX-Math.round(Integer.parseInt(space[i])*spacesScaleValue));
            Log.d("换算齿距X是好多？'", "setNeededDrawAttribute: "+toothDistanceX[i]);
            //初始化齿的深度名 都为X;
            toothDepthName[i]="X";
        }
        bottomDepthY=new int[toothDistanceX.length];
        //分割深度
        String[]  depthGroup =p.getDepth().split(";");
        String[]  depth=depthGroup[0].split(",");
        //分割深度名
        String[]  depthNameGroup= p.getDepth_name().split(";");
          depthName=depthNameGroup[0].split(",");
        depthScaleRatio=keyBodyHeightY/p.getWidth();
        depthPositionY=new int[depth.length];
        maxY=250;
        excessY=100;
        int keySumY=100+excessY;
        for (int i = 0; i <depthPositionY.length ; i++) {
           depthPositionY[i]=keySumY-Math.round(Integer.parseInt(depth[i])*depthScaleRatio);
            depthDataMap.put(depthName[i],depthPositionY[i]);
        }

        depthStartY=depthPositionY[0];
        depthEndY=depthPositionY[depthPositionY.length-1];
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(60,150);
        path.lineTo(320-toothWidthX[0],150);//110
        for (int i = 0; i <toothDepthName.length ; i++) {
            if(toothDepthName[i].equals("X")){
                path.lineTo(toothDistanceX[i]-toothWidthX[i],excessY);
                path.lineTo(toothDistanceX[i]+toothWidthX[i],excessY);
                bottomDepthY[i]=0;
            }else if(toothDepthName[i].contains(".")){
                //分割齿的深度名
                String[] strName = toothDepthName[i].split("\\.");
                int number =Integer.parseInt(strName[1]);
                float decimalsF;
                if(toothDepthName[i].contains("X")){  //只要x带小数 就不变。
                        //根据齿名找深度
                        String toothName = depthName[0];
                        int depthY=depthDataMap.get(toothName);
                        depthY=depthY-excessY;
                        decimalsF = Float.parseFloat("0." + strName[1]);
                       int  y  =(int)(depthY*decimalsF);
                          if(y==0){
                              depthY=excessY;
                          }else {
                              depthY=excessY+y;
                          }
                        path.lineTo(toothDistanceX[i] - toothWidthX[i],depthY);
                        path.lineTo(toothDistanceX[i] + toothWidthX[i],depthY);
                        bottomDepthY[i] = y;
                }else {
                    //分割齿的深度名
                    if (depthDataMap.get(strName[0]) == null) {
                        path.lineTo(toothDistanceX[i]-toothWidthX[i],excessY);
                        path.lineTo(toothDistanceX[i]+toothWidthX[i],excessY);
                        bottomDepthY[i]=0;
                    } else {
                        //获得深度的位子  y轴方向
                        int depthY = depthDataMap.get(strName[0]);
                        if (depthY == depthPositionY[depthPositionY.length - 1]) {
                            //等于最后一个深度位置不计算，不计算小数。
                            path.lineTo(toothDistanceX[i]-toothWidthX[i],depthY);
                            path.lineTo(toothDistanceX[i]+toothWidthX[i],depthY);
                            bottomDepthY[i]=depthY-excessY;
                        } else {
                           decimalsF = Float.parseFloat("0." + strName[1]);
                            int distance=0;
                            int sumY = 0;
                            for (int j = 0; j <depthPositionY.length; j++) {
                                if (depthY == depthPositionY[j]) {
                                     distance = depthPositionY[j + 1] - depthY;
                                    sumY = (int) (distance * decimalsF);
                                    break;
                                }
                            }
                            bottomDepthY[i]=(depthY-excessY)+sumY;
                            depthY = depthY + sumY;
                            path.lineTo(toothDistanceX[i]-toothWidthX[i],depthY);
                            path.lineTo(toothDistanceX[i]+toothWidthX[i],depthY);

                        }
                    }
                }
            }else {
                if(depthDataMap.get(toothDepthName[i])==null){  //等于null  就给底部变化设置为默认值
                    path.lineTo(toothDistanceX[i]-toothWidthX[i],excessY);
                    path.lineTo(toothDistanceX[i]+toothWidthX[i],excessY);
                    bottomDepthY[i]=0;
                }else {
                    int  depthY = depthDataMap.get(toothDepthName[i]);
                    path.lineTo(toothDistanceX[i]-toothWidthX[i],depthY);
                    path.lineTo(toothDistanceX[i]+toothWidthX[i],depthY);
                    bottomDepthY[i]=depthY-excessY;
                }
            }
        }
        int lastToothPosition=toothDistanceX[toothDistanceX.length-1]+toothWidthX[toothWidthX.length-1];
        path.lineTo(lastToothPosition,excessY+26);
        path.lineTo(keySumX,excessY+26);
        path.lineTo(keySumX,150);
        path.lineTo(keySumX+90,150);
        path.lineTo(keySumX+100,160);
        path.lineTo(keySumX+100,maxY-10);
        path.lineTo(keySumX+90,maxY);
        path.lineTo(lastToothPosition,maxY);
        for (int i = toothWidthX.length-1; i>=0 ; i--) {
                path.lineTo(toothDistanceX[i]+toothWidthX[i],220+bottomDepthY[i]);
                path.lineTo(toothDistanceX[i]-toothWidthX[i],220+bottomDepthY[i]);
        }
        path.lineTo(320-toothWidthX[0],maxY);
        path.lineTo(60,maxY);
        canvas.drawPath(path,blackPaint); //画钥匙形状
         canvas.drawPath(path,grayPaint); //填充钥匙形状颜色
        path.reset();
        //画深度
        for (int i = 0; i <depthPositionY.length ; i++) {
                path.moveTo(260,depthPositionY[i]);
                path.lineTo(700,depthPositionY[i]);
        }
        //画齿位虚线
        for (int i = 0; i <toothDistanceX.length ; i++) {
               path.moveTo(toothDistanceX[i],depthStartY);
               path.lineTo(toothDistanceX[i],depthEndY);
        }
        canvas.drawPath(path,dashedPaint);
        path.reset();
        //画钥匙齿的深度名
        for (int i = 0; i <toothDepthName.length ; i++) {
            if(toothDepthName[i].contains(".")){   //有小数的
                String[] split = toothDepthName[i].split("\\.");
                int number=Integer.parseInt(split[1]);
                if(toothDepthName[i].contains("X")){  //只要x带小数 就不变。
                    if(number>=5){
                      canvas.drawText(depthName[0],toothDistanceX[i],210,redPaint);
                    }else {
                        canvas.drawText(split[0],toothDistanceX[i],210,redPaint);
                    }
                }else {
                    if(number>=5){
                       if(!split[0].equals(depthName[depthName.length-1])){
                           for (int j = 0; j <depthName.length ; j++) {
                                 if(split[0].equals(depthName[j])){
                                     canvas.drawText(depthName[j+1],toothDistanceX[i],210,redPaint);
                                     break;
                                 }
                           }
                       }else {
                           canvas.drawText(split[0],toothDistanceX[i],210,redPaint);
                       }
                    }else {  //小于5的
                        canvas.drawText(split[0],toothDistanceX[i],210,redPaint);
                    }
                }
            }else {  //没有小数的深度名   画蓝色字体
                canvas.drawText(toothDepthName[i],toothDistanceX[i],210,bluePaint);  //
            }
        }
        //画红色箭头  尖端
        path.moveTo(666, 30);//80
        path.lineTo(666 + 10, 20);//70
        path.lineTo(666 + 10, 27);//77
        path.lineTo(666 + 35, 27);//77
        path.lineTo(666 + 35, 33);//83
        path.lineTo(666 + 10, 33);//83
        path.lineTo(666 + 10, 40);//90
        path.lineTo(666, 30);//80
        canvas.drawPath(path, arrowsPaint);
        path.reset();
    }
    private void initPaintAndPathAttribute() {
        path = new Path();
        blackPaint = new Paint();
        grayPaint = new Paint();
        dashedPaint = new Paint();
        bluePaint = new Paint();
        arrowsPaint = new Paint();
        redPaint=new Paint();
        //画钥匙形状属性
        blackPaint.setAntiAlias(true);//去掉抗锯齿
        blackPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        blackPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        blackPaint.setStrokeWidth(2);
        //填充钥匙身体颜色的笔
        grayPaint.setColor(Color.parseColor("#BABABA"));
        grayPaint.setAntiAlias(true);
        grayPaint.setStyle(Paint.Style.FILL);
        grayPaint.setStrokeWidth(1);
        //画虚线的笔的属性
        dashedPaint.setAntiAlias(true);//去掉抗锯齿
        dashedPaint.setColor(Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        dashedPaint.setStyle(Paint.Style.STROKE);//设置画笔描边
        dashedPaint.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        dashedPaint.setPathEffect(effects);
        //画蓝色字体的笔
        bluePaint.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        bluePaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        bluePaint.setFakeBoldText(true);  //设置为粗体
        bluePaint.setTextSize(35);
        //画红色提示箭头的属性
        arrowsPaint.setColor(Color.RED);
        arrowsPaint.setAntiAlias(true);//去掉锯齿
        arrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        arrowsPaint.setStrokeWidth(1); //设置画线的宽度
        //画红色字体的笔
        redPaint.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        redPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        redPaint.setFakeBoldText(true);  //设置为粗体
        redPaint.setTextSize(35);
    }
}
