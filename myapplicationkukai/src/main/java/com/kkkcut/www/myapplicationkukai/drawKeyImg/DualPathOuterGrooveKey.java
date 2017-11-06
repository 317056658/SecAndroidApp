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
 * 双轨外槽
 * Created by Administrator on 2017/7/18.
 */

public class DualPathOuterGrooveKey extends  Key {
    private Path path;
    private Paint p1;
    private Paint p2;
    private Paint p3;
    private Paint p4;
    private Paint p5;
    private Paint p6;
    private Paint p7;
    private KeyInfo p;
    private double keyBodyHeight = 190,keyBodyWidth=420;
    private int[]  toothDistance1,toothDistance2,toothWidth1,toothWidth2;
    private String[] toothDepthNameGroup1,toothDepthNameGroup2;
    private int maxToothDistance,lastToothDistance;
    private int surplusX;
    private int keyWidthY, excessY;
    private int[] depthPosition1,depthPosition2;
    //定义2个hasaMap集合  保存每组齿的深度位子
    private Map<String, Integer> depthDataMap1,depthDataMap2;
    private int depthStartY1, depthEndY1,depthStartY2,depthEndY2;
    private ArrayList<String[]> toothDepthNameList;
    private double spacesScaleValue,depthScaleRatio;
    private   String[]  allToothDepthName;
    private boolean isShowArrows=true;  //默认为true
    public DualPathOuterGrooveKey(Context context) {
        super(context);
        depthDataMap1=new HashMap<>();
        depthDataMap2=new HashMap<>();
        toothDepthNameList=new ArrayList<>();

    }

    public DualPathOuterGrooveKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DualPathOuterGrooveKey(Context context, AttributeSet attrs, int defStyleAttr) {
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
            allToothDepthName=depthName.split(",");
            int j=-1;
            for (int i = 0; i < allToothDepthName.length; i++) {
                if(i<toothDepthNameGroup1.length){
                    //第一组的齿名
                    toothDepthNameGroup1[i]=allToothDepthName[i];
                }else {
                    j++;
                    //第二组的齿名
                    toothDepthNameGroup2[j]=allToothDepthName[i];
                }
            }
        }

    }

    @Override
    public void redrawKey() {
        //重绘
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

    /**
     *  获得这把钥匙的所有齿的深度名
     * @return
     */
    @Override
    public ArrayList<String[]> getAllToothDepthName() {
        toothDepthNameList.clear();
        toothDepthNameList.add(toothDepthNameGroup1);
        toothDepthNameList.add(toothDepthNameGroup2);
        return toothDepthNameList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
             this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        for (int i = 0; i <toothDepthNameGroup1.length ; i++) {
            toothDepthNameGroup1[i]="X";
        }
        for (int j = 0; j <toothDepthNameGroup2.length ; j++) {
            toothDepthNameGroup2[j]="X";
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
        String toothMark = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int cutDepth=100;
        if(p.getCutDepth()!=0){
            cutDepth=p.getCutDepth();
        }

        p.setSide(3);
        String[] spaceGroup = p.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        for (int i = 0; i < spaceGroup.length; i++) {
            spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothMark += "0,";  //齿号
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
        lastToothOrExtraCut =p.getLastBitting();//默认值
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + p.getAlign() + ","//钥匙定位方式
                + p.getSide() + ","// 有效边
                + p.getWidth() + ","// 钥匙片宽度
                + p.getThick() + ","// 钥匙胚厚度
                + 0 + ","       // 表示加不加切   值为0
                + p.getLength() + ","//钥匙片长度
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
        p.setSide(3);  //设置side 为3
        String[] spaceGroup = p.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
        if(p.getSide()==4){//当参数为4的时候才会有下面参数
            toothQuantity=toothQuantity+spaceGroup[1].split(",").length+",";
        }
        toothCode =p.getKeyToothCode();
        for (int i = 0; i < spaceGroup.length; i++) {
            spaces = spaceGroup[i].split(",");
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
        lastToothOrExtraCut=p.getLastBitting();
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + p.getType() + "," //钥匙类型
                + p.getAlign() + ","//钥匙定位方式
                + p.getSide()+ ","// 有效边
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
     *  设置需要画的属性
     * @param p
     */

    public void setNeededDrawAttribute(KeyInfo p){
        this.p=p;
        //初始化画笔和路径的属性
        initPaintAndPathAttribute();
        //分割space
        String[]  spaceGroup  =p.getSpace().split(";");
        //分割space宽度
        String[] spaceWidthGroup=p.getSpace_width().split(";");
        String[] spaceWidth1=null;
        String[] spaceWidth2=null;
        if(spaceWidthGroup.length!=1){
            spaceWidth1=spaceWidthGroup[0].split(",");
            toothWidth1=new int[spaceWidth1.length];
            spaceWidth2=spaceWidthGroup[1].split(",");
            toothWidth2=new int[spaceWidth2.length];
        }
        //分割第一组的space 的数据
        String[]   space1= spaceGroup[0].split(",");
        toothDistance1=new int[space1.length];
        //new 第一组齿的深度名
        toothDepthNameGroup1=new String[toothDistance1.length];
        for (int i = 0; i <space1.length ; i++) {
            //转为int
            toothDistance1[i]=Integer.parseInt(space1[i]);
            toothDepthNameGroup1[i]="X";
        }
        //分割第二组的space 的数据
        String[]   space2= spaceGroup[1].split(",");
        toothDistance2=new int[space2.length];
        toothDepthNameGroup2=new String[toothDistance2.length];
        for (int i = 0; i <toothDistance2.length ; i++) {
            //转为int
            toothDistance2[i]=Integer.parseInt(space2[i]);
            toothDepthNameGroup2[i]="X";
        }
        if(p.getAlign()==0){
            //判断肩部定位的最大齿距
            if(toothDistance1[toothDistance1.length-1]>toothDistance2[toothDistance2.length-1]){
                maxToothDistance=toothDistance1[toothDistance1.length-1];
            }else {
                maxToothDistance=toothDistance2[toothDistance2.length-1];
            }
            //根据设定的钥匙宽度除以最大
            spacesScaleValue=keyBodyWidth/maxToothDistance;
            //换算第一组的齿位
            for (int i = 0; i <toothDistance1.length ; i++) {
                toothDistance1[i]=(int)(toothDistance1[i]*spacesScaleValue)+182;
                //换算每个齿宽
                toothWidth1[i]=(int)(Integer.parseInt(spaceWidth1[i])*spacesScaleValue)/2;

            }
            //换算第二组的齿位
            for (int i = 0; i <toothDistance2.length ; i++) {
                toothDistance2[i]=(int)(toothDistance2[i]*spacesScaleValue)+182;
                Log.d("第二组齿距是多少？", "setNeededDrawAttribute: "+toothDistance2[i]);
                //换算每个齿宽
                toothWidth2[i]=(int)(Integer.parseInt(spaceWidth2[i])*spacesScaleValue)/2;
            }
        }else if(p.getAlign()==1){
            //判断尖端定位的最大齿距
            if(toothDistance1[0]>toothDistance2[0]){
                maxToothDistance=toothDistance1[0];
            }else {
                maxToothDistance=toothDistance2[0];
            }
            //根据设定的钥匙宽度除以最大
            spacesScaleValue=keyBodyWidth/maxToothDistance;
            //比出最后一个齿距谁最大
            if (toothDistance1[toothDistance1.length - 1] < toothDistance2[toothDistance2.length - 1]) {
                lastToothDistance = toothDistance1[toothDistance1.length - 1];
            } else {
                lastToothDistance = toothDistance2[toothDistance2.length - 1];
            }
            int keySumX=(int)keyBodyWidth+182;
            surplusX = keySumX - (keySumX - (int) (spacesScaleValue * lastToothDistance));

            //换算第一组的齿位
            for (int i = 0; i <toothDistance1.length ; i++) {
                toothDistance1[i]=(keySumX-(int)(toothDistance1[i]*spacesScaleValue))+surplusX;
                Log.d("齿距是多少？", "setNeededDrawAttribute: "+toothDistance1[i]);
                //换算每个齿宽
                toothWidth1[i]=(int)(Integer.parseInt(spaceWidth1[i])*spacesScaleValue)/2;
            }
            //换算第二组的齿位
            for (int i = 0; i <toothDistance2.length ; i++) {
                toothDistance2[i]=(keySumX-(int)(toothDistance2[i]*spacesScaleValue))+surplusX;
                //换算每个齿宽
                toothWidth2[i]=(int)(Integer.parseInt(spaceWidth2[i])*spacesScaleValue)/2;
            }
        }
        depthScaleRatio=keyBodyHeight/p.getWidth();
        keyWidthY=(int)(p.getWidth()*depthScaleRatio)+56;
        //分割深度
        String[]  depthGroup  =p.getDepth().split(";");
        String[]  depth1=null;
        String[]  depth2=null;
        //分割深度名
        String[]  depthNameGroup=p.getDepth_name().split(";");
        String[] depthName1= null;
        String[] depthName2=null;
        excessY=56;
        depthScaleRatio=keyBodyHeight/p.getWidth();
        keyWidthY=(int)(p.getWidth()*depthScaleRatio)+56;
        Log.d("钥匙的总高度'", "setNeededDrawAttribute: "+keyWidthY);
        if(depthGroup.length==1){
            depth1=depthGroup[0].split(",");
            depth2=depthGroup[0].split(",");
            //得到每组齿的深度名
            depthName1=depthNameGroup[0].split(",");
            depthName2=depthNameGroup[0].split(",");
        }else if(depthGroup.length==2){
            depth1=depthGroup[0].split(",");
            depth2=depthGroup[1].split(",");
            //得到每组齿的深度名
            depthName1=depthNameGroup[0].split(",");
            depthName2=depthNameGroup[1].split(",");
        }
        depthPosition1=new int[depth1.length];
        depthPosition2=new int[depth2.length];
        //第一组深度
        for (int i = 0; i <depthPosition1.length ; i++) {
            depthPosition1[i]=keyWidthY-(int)(Integer.parseInt(depth1[i])*depthScaleRatio);
            Log.d("第一组深度的数据是好多？", "setNeededDrawAttribute: "+depthPosition1[i]);
            //根据第一组的深度名保存换算好的深度位子
            depthDataMap1.put(depthName1[i],depthPosition1[i]);
        }
        depthStartY1=depthPosition1[0];
        depthEndY1=depthPosition1[depthPosition1.length-1];
        //第二组深度
        for (int i = 0; i <depthPosition2.length ; i++) {
            depthPosition2[i]=(int)(Integer.parseInt(depth2[i])*depthScaleRatio)+excessY;
            Log.d("第二组深度的数据是好多？", "setNeededDrawAttribute: "+depthPosition2[i]);
            //根据第二组的深度名保存换算好的深度位子
            depthDataMap2.put(depthName2[i],depthPosition2[i]);
        }
        depthStartY2=depthPosition2[0];
        depthEndY2=depthPosition2[depthPosition2.length-1];

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("跑起来了？", "onDraw: ");
        if(isShowArrows){
            if(p.getAlign()==0){
                //画红色箭头  肩部
                path.moveTo(90, 20);  //100
                path.lineTo(120, 20);//第一条  100
                path.lineTo(120, 12);
                path.lineTo(130, 24);//中间点  104
                path.lineTo(120, 36);
                path.lineTo(120, 28);
                path.lineTo(90, 28);
                canvas.drawPath(path, p5);
                path.reset();
            }else if(p.getAlign()==1){
                //画红色箭头  尖端
                path.moveTo(666, 30);//80
                path.lineTo(666 + 10, 20);//70
                path.lineTo(666 + 10, 27);//77
                path.lineTo(666 + 35, 27);//77
                path.lineTo(666 + 35, 33);//83
                path.lineTo(666 + 10, 33);//83
                path.lineTo(666 + 10, 40);//90
                path.lineTo(666, 30);//80
                canvas.drawPath(path, p5);
                path.reset();
            }
        }
        path.moveTo(120,56);
        path.lineTo(720,56);
        path.quadTo(732,56,730,66);
        path.lineTo(730,keyWidthY-10);
        path.quadTo(732, keyWidthY+2, 720, keyWidthY);
        path.lineTo(120,keyWidthY);
        canvas.drawPath(path,p1);
        canvas.drawPath(path,p7);
        path.reset();
        //绘制特别双轨外槽钥匙
        if(p.getId()==982||p.getId()==10036||p.getId()==1341||p.getId()==10037){
            path.moveTo(10,56);
            path.lineTo(120,56);
            path.quadTo(118,depthPosition1[1]+3,130,depthPosition1[1]);
            path.lineTo(182,depthPosition1[1]);
            //绘制第一边齿和齿深
            for (int i = 0; i < toothDistance1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    path.lineTo(toothDistance1[i]-toothWidth1[i], depthPosition1[0]-8);
                    path.lineTo(toothDistance1[i]+toothWidth1[i],  depthPosition1[0]-8);
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    //分割齿的深度名
                    String[] str= toothDepthNameGroup1[i].split("\\.");
                    if(depthDataMap1.get(str[0])==null){
                        path.lineTo(toothDistance1[i]-toothWidth1[i],depthPosition1[0]-8);
                        path.lineTo(toothDistance1[i]+toothWidth1[i],depthPosition1[0]-8);
                    }else {
                        int depthData = depthDataMap1.get(str[0]);
                        float  decimals= Float.parseFloat("0."+ str[1]);
                        float  sumY=0;
                        if(depthData ==depthPosition1[depthPosition1.length-1]){
                            //等于最后一个深度不计算。
                        }else {
                            for(int j=0;j<depthPosition1.length;j++){
                                if(depthData ==depthPosition1[j]){
                                    int distance =depthPosition1[j+1]- depthData;
                                    sumY=distance*decimals;
                                    break;
                                }
                            }
                        }
                        path.lineTo(toothDistance1[i]-toothWidth1[i],depthData+sumY);
                        path.lineTo(toothDistance1[i]+toothWidth1[i],depthData+sumY);
                    }
                }else {
                    int toothDepth = depthDataMap1.get(toothDepthNameGroup1[i]) == null ?  depthPosition1[0]-8 : depthDataMap1.get(toothDepthNameGroup1[i]);
                    path.lineTo(toothDistance1[i]-toothWidth1[i],toothDepth);
                    path.lineTo(toothDistance1[i]+toothWidth1[i],toothDepth);
                }
            }
            int keyHalfY=(int)(keyBodyHeight/2)+excessY;
            path.lineTo(730,keyHalfY-5);
            path.lineTo(730,keyHalfY+5);
            //绘制第二边齿和齿深
            //绘制第二边所有齿的位子和齿深
            for (int i =toothDistance2.length-1; i >=0; i--) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    path.lineTo(toothDistance2[i] + toothWidth2[i], depthPosition2[0]+8);
                    path.lineTo(toothDistance2[i] - toothWidth2[i], depthPosition2[0]+8);
                } else if (toothDepthNameGroup2[i].contains(".")) {
                    String[] str = toothDepthNameGroup2[i].split("\\.");
                    if (depthDataMap2.get(str[0]) == null) {
                        path.lineTo(toothDistance2[i] + toothWidth2[i], depthPosition2[0]+8);
                        path.lineTo(toothDistance2[i] - toothWidth2[i], depthPosition2[0]+8);
                    } else {
                        int depthData = depthDataMap2.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        float sumY = 0;
                        if (depthData == depthPosition2[depthPosition2.length - 1]) {
                            //等于最后一个深度不计算。
                        } else {
                            for (int j = 0; j < depthPosition2.length; j++) {
                                if (depthData == depthPosition2[j]) {
                                    int distance = depthPosition2[j + 1] - depthData;
                                    sumY = distance * decimals;
                                    break;
                                }
                            }
                        }
                        path.lineTo(toothDistance2[i] + toothWidth2[i], depthData + sumY);
                        path.lineTo(toothDistance2[i] - toothWidth2[i], depthData + sumY);
                    }
                } else {
                    int toothDepth = depthDataMap2.get(toothDepthNameGroup2[i]) == null ?depthPosition2[0]+8 : depthDataMap2.get(toothDepthNameGroup2[i]);
                    path.lineTo(toothDistance2[i] + toothWidth2[i], toothDepth);
                    path.lineTo(toothDistance2[i] - toothWidth2[i], toothDepth);
                }
            }
           path.lineTo(182,depthPosition2[1]);
            path.lineTo(130,depthPosition2[1]);
            path.quadTo(117,depthPosition2[1],120,depthPosition2[1]+30);
            path.lineTo(10,depthPosition2[1]+30);
            canvas.drawPath(path,p1);
            canvas.drawPath(path,p2);
            path.reset();

            //画深度虚线
            // 第一组的深度虚线
            for (int i = 0; i < depthPosition1.length; i++) {
                path.moveTo(120, depthPosition1[i]);
                path.lineTo(644, depthPosition1[i]);
            }
            //第二组的深度虚线
            for (int i = 0; i < depthPosition2.length; i++) {
                path.moveTo(120, depthPosition2[i]);
                path.lineTo(644, depthPosition2[i]);
            }
            //画第一组齿位虚线
            for (int i = 0; i < toothDistance1.length; i++) {
                path.moveTo(toothDistance1[i], depthStartY1);
                path.lineTo(toothDistance1[i], depthEndY1);
            }
            //画第二组的齿位虚线
            for (int i = 0; i < toothDistance2.length; i++) {
                path.moveTo(toothDistance2[i], depthStartY2);
                path.lineTo(toothDistance2[i], depthEndY2);
            }
            canvas.drawPath(path,p3);
            path.reset();
            //画第一组齿的深度名
            for (int i = 0; i < toothDepthNameGroup1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i], excessY - 10, p4);  //画蓝色的字
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    if (toothDepthNameGroup1[i].contains("X")) {
                        canvas.drawText("X", toothDistance1[i], excessY  - 10, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup1[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance1[i], excessY  - 10, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], excessY- 10, p4);//画蓝色的字
                }
            }
            //画第二组齿的深度名
            for (int i = 0; i < toothDepthNameGroup2.length; i++) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    canvas.drawText("X", toothDistance2[i],keyWidthY+ 40, p4);  //画蓝色的字
                } else if (toothDepthNameGroup2[i].contains(".")) {
                    if (toothDepthNameGroup2[i].contains("X")) {
                        canvas.drawText("X", toothDistance2[i], keyWidthY  + 40, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup2[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance2[i], keyWidthY  + 40, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup2[i], toothDistance2[i], keyWidthY+ 40, p4);//画蓝色的字
                }
            }

        }else { //绘制正常标准的钥匙
            path.moveTo(10,56);
            path.lineTo(160,56);
            //绘制第一边齿和齿深
            for (int i = 0; i < toothDistance1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    path.lineTo(toothDistance1[i]-toothWidth1[i], 56);
                    path.lineTo(toothDistance1[i]+toothWidth1[i], 56);
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    //分割齿的深度名
                    String[] str= toothDepthNameGroup1[i].split("\\.");
                    if(depthDataMap1.get(str[0])==null){
                        path.lineTo(toothDistance1[i]-toothWidth1[i], 56);
                        path.lineTo(toothDistance1[i]+toothWidth1[i], 56);
                    }else {
                        int depthData = depthDataMap1.get(str[0]);
                        float  decimals= Float.parseFloat("0."+ str[1]);
                        float  sumY=0;
                        if(depthData ==depthPosition1[depthPosition1.length-1]){
                            //等于最后一个深度不计算。
                        }else {
                            for(int j=0;j<depthPosition1.length;j++){
                                if(depthData ==depthPosition1[j]){
                                    int distance =depthPosition1[j+1]- depthData;
                                    sumY=distance*decimals;
                                    break;
                                }
                            }
                        }
                        path.lineTo(toothDistance1[i]-toothWidth1[i],depthData+sumY);
                        path.lineTo(toothDistance1[i]+toothWidth1[i],depthData+sumY);
                    }
                }else {
                    int toothDepth = depthDataMap1.get(toothDepthNameGroup1[i]) == null ? 56 : depthDataMap1.get(toothDepthNameGroup1[i]);
                    path.lineTo(toothDistance1[i]-toothWidth1[i],toothDepth);
                    path.lineTo(toothDistance1[i]+toothWidth1[i],toothDepth);
                }
            }
            int keyHalfY=(int)(keyBodyHeight/2)+excessY;
            path.lineTo(730,keyHalfY-5);
            path.lineTo(730,keyHalfY+5);
            //绘制第二边齿和齿深
            //绘制第二边所有齿的位子和齿深
            for (int i =toothDistance2.length-1; i >=0; i--) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    path.lineTo(toothDistance2[i] + toothWidth2[i], keyWidthY);
                    path.lineTo(toothDistance2[i] - toothWidth2[i], keyWidthY);
                } else if (toothDepthNameGroup2[i].contains(".")) {
                    String[] str = toothDepthNameGroup2[i].split("\\.");
                    if (depthDataMap2.get(str[0]) == null) {
                        path.lineTo(toothDistance2[i] + toothWidth2[i], keyWidthY);
                        path.lineTo(toothDistance2[i] - toothWidth2[i], keyWidthY);
                    } else {
                        int depthData = depthDataMap2.get(str[0]);
                        float decimals = Float.parseFloat("0." + str[1]);
                        float sumY = 0;
                        if (depthData == depthPosition2[depthPosition2.length - 1]) {
                            //等于最后一个深度不计算。
                        } else {
                            for (int j = 0; j < depthPosition2.length; j++) {
                                if (depthData == depthPosition2[j]) {
                                    int distance = depthPosition2[j + 1] - depthData;
                                    sumY = distance * decimals;
                                    break;
                                }
                            }
                        }
                        path.lineTo(toothDistance2[i] + toothWidth2[i], depthData + sumY);
                        path.lineTo(toothDistance2[i] - toothWidth2[i], depthData + sumY);
                    }
                } else {
                    int toothDepth = depthDataMap2.get(toothDepthNameGroup2[i]) == null ? keyWidthY : depthDataMap2.get(toothDepthNameGroup2[i]);
                    path.lineTo(toothDistance2[i] + toothWidth2[i], toothDepth);
                    path.lineTo(toothDistance2[i] - toothWidth2[i], toothDepth);
                }
            }
            path.lineTo(160,keyWidthY);
            path.lineTo(10,keyWidthY);
            canvas.drawPath(path,p1);
            canvas.drawPath(path,p2);
            path.reset();

            //画深度虚线
            // 第一组的深度虚线
            for (int i = 0; i < depthPosition1.length; i++) {
                path.moveTo(120, depthPosition1[i]);
                path.lineTo(644, depthPosition1[i]);
            }
            //第二组的深度虚线
            for (int i = 0; i < depthPosition2.length; i++) {
                path.moveTo(120, depthPosition2[i]);
                path.lineTo(644, depthPosition2[i]);
            }
            //画第一组齿位虚线
            for (int i = 0; i < toothDistance1.length; i++) {
                path.moveTo(toothDistance1[i], depthStartY1);
                path.lineTo(toothDistance1[i], depthEndY1);
            }
            //画第二组的齿位虚线
            for (int i = 0; i < toothDistance2.length; i++) {
                path.moveTo(toothDistance2[i], depthStartY2);
                path.lineTo(toothDistance2[i], depthEndY2);
            }
            canvas.drawPath(path,p3);
            path.reset();
            //画第一组齿的深度名
            for (int i = 0; i < toothDepthNameGroup1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i], excessY - 10, p4);  //画蓝色的字
                } else if (toothDepthNameGroup1[i].contains(".")) {
                    if (toothDepthNameGroup1[i].contains("X")) {
                        canvas.drawText("X", toothDistance1[i], excessY  - 10, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup1[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance1[i], excessY  - 10, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], excessY- 10, p4);//画蓝色的字
                }
            }
            //画第二组齿的深度名
            for (int i = 0; i < toothDepthNameGroup2.length; i++) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    canvas.drawText("X", toothDistance2[i],keyWidthY+ 40, p4);  //画蓝色的字
                } else if (toothDepthNameGroup2[i].contains(".")) {
                    if (toothDepthNameGroup2[i].contains("X")) {
                        canvas.drawText("X", toothDistance2[i], keyWidthY  + 40, p6);//画红色的字
                    } else {
                        String[] s = toothDepthNameGroup2[i].split("\\.");
                        Log.d("这是几", "bilateralKey: " + s[0]);
                        canvas.drawText(s[0], toothDistance2[i], keyWidthY  + 40, p6);//画红色的字
                    }
                } else {
                    canvas.drawText(toothDepthNameGroup2[i], toothDistance2[i], keyWidthY+ 40, p4);//画蓝色的字
                }
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("View的宽度", "onMeasure: "+MeasureSpec.getSize(widthMeasureSpec));
        Log.d("View的高度", "onMeasure: "+MeasureSpec.getSize(heightMeasureSpec));
    }
    private void initPaintAndPathAttribute() {
        path = new Path();
        p1 = new Paint();
        p2 = new Paint();
        p3 = new Paint();
        p4 = new Paint();
        p5 = new Paint();
        p6 = new Paint();
        p7 = new Paint();
        //画钥匙形状属性
        p1.setAntiAlias(true);//去掉抗锯齿
        p1.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        p1.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        p1.setStrokeWidth(2);
        //填充钥匙身体颜色的笔
        p2.setColor(Color.parseColor("#BABABA"));
        p2.setAntiAlias(true);
        p2.setStyle(Paint.Style.FILL);
        p2.setStrokeWidth(1);
        //画虚线的笔的属性
        p3.setAntiAlias(true);//去掉抗锯齿
        p3.setColor(Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        p3.setStyle(Paint.Style.STROKE);//设置画笔描边
        p3.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        p3.setPathEffect(effects);
        //画蓝色字体的笔
        p4.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        p4.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        p4.setTextSize(40);
        //画红色提示箭头的属性
        p5.setColor(Color.RED);
        p5.setAntiAlias(true);//去掉锯齿
        p5.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        p5.setStrokeWidth(2); //设置画线的宽度
        //画红色字体的笔
        p6.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        p6.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        p6.setTextSize(40);
        //填充钥匙内槽颜色的笔
        p7.setAntiAlias(true);//去掉锯齿
        p7.setColor(Color.parseColor("#cc9900")); //黄色
        p7.setStyle(Paint.Style.FILL);
        p7.setStrokeWidth(1);
    }
}
