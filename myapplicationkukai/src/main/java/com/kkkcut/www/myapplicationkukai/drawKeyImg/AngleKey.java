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
    private KeyInfo p;
    private Path path;
    private Paint blackPaint;
    private Paint grayPaint;
    private Paint dashedPaint;
    private Paint bluePaint;
    private Paint arrowsPaint;
    private Paint centreLinePaint;
    private Paint depthPaint;
    private Paint  yellowPaint;
    private double keyBodyHeight = 140;
    private float[] toothDistanceX;
    private float keyBodyWidth =310,spacesScaleValue;
    private String[] toothDepthName;
    private int surplusX;
    private int keyWidthY, excessY;
    private ArrayList<String[]> toothDepthNameList;
    private double  depthScaleRatio;
    private String[] allToothDepthName;
    private float keySumX;
    private float toothWidthX;
    private int[] depthPosition1,depthPosition2;
    private Map<String, Integer> depthMap1,depthMap2;
   private int centreY;
    private int startDepthY1,endDepthY1, startDepthY2,endDepthY2;
    private  final static String TAG="AngleKey";
    private boolean isShowArrows=true;  //默认为true

    public AngleKey(Context context) {
        super(context);
        depthMap1 =new HashMap<>();
        depthMap2=new HashMap<>();
        toothDepthNameList=new ArrayList<>();
    }


    public AngleKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AngleKey(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if(!TextUtils.isEmpty(depthName)){
            allToothDepthName= depthName.split(",");
            for (int i = 0; i <allToothDepthName.length ; i++) {
                toothDepthName[i]=allToothDepthName[i];
            }
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
        if(p.getAlign()==1){
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
        String[] spaceGroup = p.getSpace().split(";");
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[]  spaces = spaceGroup[i].split(",");
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
        if(p.getSide()==0){
            side="";
        }else {
            side=p.getSide()+"";
        }
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + p.getAlign() + ","//钥匙定位方式
                + side+ ","// 有效边
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切
                + p.getLength() + ","//钥匙片长度
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
        String  side;
        String DDepth="";

        if(speed==1||speed==2){
            DDepth ="0.75";
        }else if(speed==3||speed==4){
            DDepth="1";
        }
        String[] spaceGroup = p.getSpace().split(";");
        toothCode=p.getKeyToothCode();
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            String[]  spaces = spaceGroup[i].split(",");
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
        if(p.getSide()==0){
            side="";
        }else {
            side=p.getSide()+"";
        }
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + p.getAlign() + ","//钥匙定位方式
                + side+ ","// 有效边
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切
                + p.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
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
        Log.d("进来了", "onMeasure: ");
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    public void setNeededDrawAttribute(KeyInfo p) {
           this.p=p;
        //初始化画笔和路径
        initPaintAndPathAttribute();
            if(p.getAlign()==1){  //肩部定位
                //分割间隔 space
                String[] spaceGroup = p.getSpace().split(";");
                String[]    spaces   =spaceGroup[0].split(",");
                //分割深度depth
                String[]  depthGroup=p.getDepth().split(";");
                String[]  depths =depthGroup[0].split(",");
                String[] depthNameGroup=p.getDepth_name().split(";");
                String[]  depthName = depthNameGroup[0].split(",");
                //分割深度名
                if(p.getFace().equals("0")){
                    excessY=70;
                    keyBodyWidth=310;
                    keyBodyHeight=140;
                    //得到spaces比例值
                    spacesScaleValue=keyBodyWidth/(Integer.parseInt(spaces[0]));
                    keySumX=(int)keyBodyWidth+200;
                    //钥匙的宽度y位置
                    keyWidthY=(int)keyBodyHeight+excessY;
                    toothDistanceX =new float[spaces.length];
                    toothDepthName=new String[toothDistanceX.length];
                    surplusX=(int)(keySumX-(keySumX-(int)(Integer.parseInt(spaces[spaces.length-1])*spacesScaleValue)));
                    for (int i = 0; i < toothDistanceX.length ; i++) {
                        toothDistanceX[i]= keySumX-(Integer.parseInt(spaces[i])*spacesScaleValue)+surplusX;
                        toothDepthName[i]="X";
                        Log.d("齿距离2", "setNeededDrawAttribute: "+toothDistanceX[i]);
                    }
                    //齿顶宽
                    toothWidthX=(toothDistanceX[1]-toothDistanceX[0])/2;
                    toothWidthX=toothWidthX;


                    //中心线
                    centreY=((int)keyBodyHeight/2)+excessY;
                    //取最大深度 算深度比列值
                    int maxDepths=Integer.parseInt(depths[depths.length-1]);

                    depthScaleRatio=(keyBodyHeight/2)/(maxDepths+1600);
                    depthPosition1 =new int[depths.length];
                    //第一边深度
                    for (int i = 0; i < depthPosition1.length; i++) {
                        depthPosition1[i]=(int)(Integer.parseInt(depths[i])*depthScaleRatio)+excessY;
                        Log.d("深度是？1", "setNeededDrawAttribute: "+ depthPosition1[i]);
                        depthMap1.put(depthName[i],depthPosition1[i]);
                    }

                    //第二边深度
                    depthPosition2=new int[depths.length];
                    for (int i = 0; i < depthPosition2.length; i++) {
                        Log.d("深度是？", "setNeededDrawAttribute: ");
                        depthPosition2[i]=keyWidthY-(int)(Integer.parseInt(depths[i])*depthScaleRatio);
                        depthMap2.put(depthName[i],depthPosition2[i]);
                        Log.d("下边深度", "setNeededDrawAttribute: "+depthPosition2[i]);
                    }
                }else if(p.getFace().equals("2")){
                    excessY=84;
                    keyBodyWidth=380f;
                    keyBodyHeight=110;
                    //得到spaces比例值
                    spacesScaleValue=keyBodyWidth/(Integer.parseInt(spaces[0]));
                    keySumX= keyBodyWidth+200;
                    //钥匙的宽度y位置
                    keyWidthY=(int)keyBodyHeight+excessY;
                    toothDistanceX =new float[spaces.length];
                    toothDepthName=new String[toothDistanceX.length];
                    surplusX=(int)(keySumX-(keySumX-(int)(Integer.parseInt(spaces[spaces.length-1])*spacesScaleValue)));
                    for (int i = 0; i < toothDistanceX.length ; i++) {
                        toothDistanceX[i]= keySumX-(Integer.parseInt(spaces[i])*spacesScaleValue)+surplusX;
                        toothDepthName[i]="X";
                        Log.d("齿距离2", "setNeededDrawAttribute: "+toothDistanceX[i]);
                    }
                    //齿顶宽
                    toothWidthX=(toothDistanceX[1]-toothDistanceX[0])/2;
                    //中心线
                    centreY=((int)keyBodyHeight/2)+excessY;
                    //取最大深度 算深度比列值
                    int maxDepths=Integer.parseInt(depths[depths.length-1]);

                    depthScaleRatio=(keyBodyHeight/2)/(maxDepths+1600);
                    depthPosition1 =new int[depths.length];
                    //第一边深度
                    for (int i = 0; i < depthPosition1.length; i++) {
                        depthPosition1[i]=(int)(Integer.parseInt(depths[i])*depthScaleRatio)+excessY;
                        Log.d("深度是？1", "setNeededDrawAttribute: "+ depthPosition1[i]);
                        depthMap1.put(depthName[i],depthPosition1[i]);
                    }

                    //第二边深度
                    depthPosition2=new int[depths.length];
                    for (int i = 0; i < depthPosition2.length; i++) {
                        Log.d("深度是？", "setNeededDrawAttribute: ");
                        depthPosition2[i]=keyWidthY-(int)(Integer.parseInt(depths[i])*depthScaleRatio);
                        depthMap2.put(depthName[i],depthPosition2[i]);
                        Log.d("下边深度", "setNeededDrawAttribute: "+depthPosition2[i]);
                    }

                }
                //保存两边深度开始位子和结束位子
                startDepthY1=depthPosition1[0];
                endDepthY1=depthPosition1[depthPosition1.length-1];
                startDepthY2=depthPosition2[0];
                endDepthY2=depthPosition2[depthPosition2.length-1];

            }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(p.getAlign()==1){
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
            if(p.getFace().equals("0")){
                path.moveTo(20,100);
                path.lineTo(200,100);
                path.lineTo(200,70);
                path.lineTo(550,70);//310
                path.lineTo(550,84);
                path.lineTo(650,84);
                path.lineTo(660,94);
                int y=(keyWidthY-14)-10;
                path.lineTo(660,y);
                path.lineTo(650,y+10);
                path.lineTo(550,y+10);
                path.lineTo(550,keyWidthY);
                path.lineTo(200,keyWidthY);
                path.lineTo(200,keyWidthY-30);
                path.lineTo(20,keyWidthY-30);
                //画钥匙形状
                canvas.drawPath(path,blackPaint);
                //填充钥匙颜色
                canvas.drawPath(path,grayPaint);
                path.reset();
                //画中心线
                path.moveTo(20,centreY);
                path.lineTo(660,centreY);
                canvas.drawPath(path,centreLinePaint);
                path.reset();

            }else if(p.getFace().equals("2")){
                path.moveTo(20,84);
                path.lineTo(650,84);
                path.lineTo(660,94);
                path.lineTo(660,keyWidthY-10);
                path.lineTo(650,keyWidthY);
                path.lineTo(20,keyWidthY);
                //画钥匙形状
                canvas.drawPath(path,blackPaint);
                //填充钥匙颜色
                canvas.drawPath(path,grayPaint);
                path.reset();
                //画中心线
                path.moveTo(20,centreY);
                path.lineTo(660,centreY);
                canvas.drawPath(path,centreLinePaint);
                path.reset();

            }
            //根据齿的深度名 画深度
            for (int i = 0; i <toothDepthName.length ; i++) {
                if(toothDepthName[i].equals("X")){
                    //上边深度
                    path.addRect(toothDistanceX[i]-toothWidthX,excessY-10, toothDistanceX[i]+toothWidthX,excessY, Path.Direction.CW);
                    //下边深度
                    path.addRect(toothDistanceX[i]-toothWidthX,keyWidthY, toothDistanceX[i]+toothWidthX,keyWidthY+10, Path.Direction.CW);
                }else {
                    int TopDepthY= depthMap1.get(toothDepthName[i]) == null ? excessY : depthMap1.get(toothDepthName[i]);
                    int belowDepthY= depthMap2.get(toothDepthName[i]) == null ? keyWidthY : depthMap2.get(toothDepthName[i]);
                    //上边深度
                    int topY;
                    if(depthMap1.get(toothDepthName[i]) == null){
                        topY =excessY-10;
                    }else {
                        topY =excessY;
                    }
                    //下边深度
                    int bottomY;
                    if(depthMap2.get(toothDepthName[i]) == null){
                        bottomY =keyWidthY+10;
                    }else {
                        bottomY =keyWidthY;
                    }
                    //上边深度矩形
                    canvas.drawRect(toothDistanceX[i]-toothWidthX, topY, toothDistanceX[i]+toothWidthX,TopDepthY,depthPaint);
                    canvas.drawRect(toothDistanceX[i]-toothWidthX, topY, toothDistanceX[i]+toothWidthX,TopDepthY,yellowPaint);
                    //下边深度矩形
                    canvas.drawRect(toothDistanceX[i]-toothWidthX,belowDepthY, toothDistanceX[i]+toothWidthX, bottomY,depthPaint);
                    canvas.drawRect(toothDistanceX[i]-toothWidthX, belowDepthY, toothDistanceX[i]+toothWidthX, bottomY,yellowPaint);
                }
            }
            //颜色填充
            canvas.drawPath(path,yellowPaint);
            path.reset();
            for (int t = 0; t < toothDistanceX.length ; t++) {
                path.moveTo(toothDistanceX[t],excessY);
                path.lineTo(toothDistanceX[t],excessY+30);
            }
            canvas.drawPath(path,dashedPaint);
            path.reset();
            //画深度
            for (int i = 0; i <depthPosition1.length ; i++) {
                path.moveTo(220,depthPosition1[i]);
                path.lineTo(620,depthPosition1[i]);
            }
            for (int i = 0; i <depthPosition2.length ; i++) {
                path.moveTo(220,depthPosition2[i]);
                path.lineTo(620,depthPosition2[i]);
            }
            canvas.drawPath(path,dashedPaint);
            path.reset();
            //画齿位虚线
            for (int i = 0; i <toothDistanceX.length ; i++) {
                path.moveTo(toothDistanceX[i],startDepthY1);
                path.lineTo(toothDistanceX[i],endDepthY1);
                path.moveTo(toothDistanceX[i],startDepthY2);
                path.lineTo(toothDistanceX[i],endDepthY2);
            }
            canvas.drawPath(path,dashedPaint);
            path.reset();
            //画齿的深度名字
            for (int i = 0; i <toothDepthName.length ; i++) {
                canvas.drawText(toothDepthName[i],toothDistanceX[i],centreY+10,bluePaint);
            }
        }
    }
    private void initPaintAndPathAttribute() {
        path = new Path();
        blackPaint = new Paint();
        grayPaint = new Paint();
        dashedPaint = new Paint();
        bluePaint = new Paint();
        arrowsPaint = new Paint();
        centreLinePaint = new Paint();
        depthPaint =new Paint();
        yellowPaint=new Paint();
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
        bluePaint.setTextAlign(Paint.Align.CENTER);
        bluePaint.setFakeBoldText(true);  //设置为粗体
        bluePaint.setTextSize(35);
        //画红色提示箭头的属性
        arrowsPaint.setColor(Color.RED);
        arrowsPaint.setAntiAlias(true);//去掉锯齿
        arrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        arrowsPaint.setStrokeWidth(2); //设置画线的宽度

        //画钥匙中心线笔
        centreLinePaint.setAntiAlias(true);//去掉锯齿
        centreLinePaint.setColor(Color.parseColor("#666666")); //浅灰色
        centreLinePaint.setStyle(Paint.Style.STROKE);//描边
        centreLinePaint.setStrokeWidth(1);
        //画深度边框画笔
        depthPaint.setColor(Color.WHITE);  //默认白色
        depthPaint.setAntiAlias(true);//去掉锯齿
        depthPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        depthPaint.setStrokeWidth(1); //设置画线的宽度
        //黄色画笔
        yellowPaint.setColor(Color.parseColor("#cc9900"));  //黄色
        yellowPaint.setAntiAlias(true);//去掉锯齿
        yellowPaint.setStyle(Paint.Style.FILL);//设置画笔风格为填充
        yellowPaint.setStrokeWidth(1); //设置画线的宽度

    }
}
