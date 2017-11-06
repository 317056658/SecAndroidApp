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
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/19.
 * 双边钥匙
 */

public class BilateralKey extends Key {
    private Path mPath;//画点
    private Paint mBorderPaint;//画点线的笔
    private Paint mDashedPaint; //画虚线的笔
    private Paint mTextColorPaint;//画蓝色字体的笔
    private Paint p4; //画红色字体的笔
    private Paint mArrowsPaint;//画箭头
    private Paint mKeyAppearanceColorPaint;//画钥匙身体颜色
    public  String[] allToothDepthName = null;//齿号的别名  代表深度
    private KeyInfo ki;
    private String[] spaceGroup;
    private String[] depthGroup,depthNameGroup;
    private ArrayList<String[]> toothNameGroupList;  //保存齿组名字数据的集合
    private int maxToothPosition;  // 最大齿位
    private int[] toothDistancesOne, toothDistancesTwo;  //存储齿位的数据数据
    private List<Integer>  toothDistancesListOne;
    private List<Integer>  toothDistancesListTwo;
    private String[] toothDepthNameOne, toothDepthNameTwo;  //第一组齿名，第二组齿名。
    private int toothWidth = 14;
    private   Map<String, Integer> depthMap1;
   private Map<String, Integer> depthMap2;
    private int[] depthPositionArray1, depthPositionArray2;  //画深度位子用的数组
    private int keyHalfWidthY;
    private float spacesScaleValue,depthScaleRatio;
    private float keyBodyWidth;
    private float keyWidthY, keySumY;
    private int depthStartY1, depthEndY1, depthStartY2, depthEndY2;
    int excessX;
    int excessY=60;
    private List<Integer> spaceDataList1;  //定义一个保存int类型的space集合1
    private List<Integer> spaceDataList2;  //定义一个保
    private int[]  spaceDataArray1,spaceDataArray2;//存int类型的space集合 12
    private  int surplusX;
    private boolean isShowArrows=true;
    private int patternWidth,patternHeight;
    private  final int  AGROUP=1 ;


    public BilateralKey(Context context,KeyInfo ki) {
        this(context,null,ki);
    }

    public BilateralKey(Context context, AttributeSet attrs, KeyInfo ki) {
        this(context,attrs,0,ki);
    }

    public BilateralKey(Context context, AttributeSet attrs, int defStyleAttr, KeyInfo ki) {
        super(context, attrs, defStyleAttr);
           this.ki=ki;
        this.initPaintAndPath();  //初始化画笔和路径
        toothDistancesListOne=new ArrayList<>();
        toothDistancesListTwo=new ArrayList<>();
    }

    public void setNeededDrawAttribute(KeyInfo p){
        initPaintAndPath();//初始化画笔属性和路径
        spaceDataList1=new ArrayList<>();
        spaceDataList2=new ArrayList<>();
        keyBodyWidth=487f;
        this.ki = p;
        toothNameGroupList = new ArrayList<>();
        //分割数据得到齿组
        spaceGroup = p.getSpace().split(";");
        depthMap1 = new HashMap<>();
        depthMap2 = new HashMap<>();
        //分割深度数据得到深度数据的组数
        depthGroup = p.getDepth().split(";");
        //分割深度名
        depthNameGroup=p.getDepth_name().split(";");

        if (p.getAlign() == 0) {
            //获得肩部定位的最大齿位数据
            if (spaceGroup.length > 1) {
                String[] Spaces1 = spaceGroup[0].split(",");
                String[] Spaces2 = spaceGroup[1].split(",");
                toothDepthNameOne =new String[Spaces1.length];
                toothDepthNameTwo =new String[Spaces2.length];
                toothDistancesOne =new int[Spaces1.length];
                toothDistancesTwo =new int[Spaces2.length];
                if (Integer.parseInt(Spaces1[Spaces1.length - 1]) > Integer.parseInt(Spaces2[Spaces2.length - 1])) {
                    maxToothPosition = Integer.parseInt(Spaces1[Spaces1.length - 1]);
                } else {
                    maxToothPosition = Integer.parseInt(Spaces2[Spaces2.length - 1]);
                }
                for (int i = 0; i < Spaces1.length; i++) {
                    // 把齿位数据转为int
                    toothDistancesOne[i] = Integer.parseInt(Spaces1[i]);
                    spaceDataList1.add(Integer.parseInt(Spaces1[i]));
                    toothDepthNameOne[i]="X";
                }
                for (int i = 0; i < Spaces2.length; i++) {
                    // 把齿位数据转为int
                    toothDistancesTwo[i] = Integer.parseInt(Spaces2[i]);
                    spaceDataList2.add(Integer.parseInt(Spaces2[i]));
                    toothDepthNameTwo[i]="X";
                }

            } else {
                String[] Spaces = spaceGroup[0].split(",");
                toothDistancesOne = new int[Spaces.length];
                toothDistancesTwo = new int[Spaces.length];
                toothDepthNameOne =new String[Spaces.length];
                toothDepthNameTwo =new String[Spaces.length];

                for (int i = 0; i < Spaces.length; i++) {
                    toothDepthNameOne[i]="X";
                    toothDepthNameTwo[i]="X";
                    // 把齿位数据转为int
                    toothDistancesOne[i] = Integer.parseInt(Spaces[i]);
                    toothDistancesTwo[i] = Integer.parseInt(Spaces[i]);
                    spaceDataList1.add(Integer.parseInt(Spaces[i]));
                    spaceDataList2.add(Integer.parseInt(Spaces[i]));
                }
                maxToothPosition = spaceDataList1.get(spaceDataList1.size()-1);
            }
        } else if (p.getAlign() == 1) {
            //获得尖端定位的最大齿位数据
            if (spaceGroup.length > 1) {
                String[] Spaces1 = spaceGroup[0].split(",");
                String[] Spaces2 = spaceGroup[1].split(",");
                toothDepthNameOne =new String[Spaces1.length];
                toothDepthNameTwo =new String[Spaces2.length];
                toothDistancesOne =new int[Spaces1.length];
                toothDistancesTwo =new int[Spaces2.length];
                if (Integer.parseInt(Spaces1[0]) > Integer.parseInt(Spaces2[0])) {
                    maxToothPosition = Integer.parseInt(Spaces1[0]);
                } else {
                    maxToothPosition = Integer.parseInt(Spaces2[0]);
                }
                for (int i = 0; i < Spaces1.length; i++) {
                    spaceDataList1.add(Integer.parseInt(Spaces1[i]));
                    // 把齿位数据转为int
                    toothDistancesOne[i] = Integer.parseInt(Spaces1[i]);
                    //初始化每个齿的深度名
                    toothDepthNameOne[i]="X";
                }
                for (int i = 0; i < Spaces2.length; i++) {
                    spaceDataList2.add(Integer.parseInt(Spaces2[i]));
                    // 把齿位数据转为int
                    toothDistancesTwo[i] = Integer.parseInt(Spaces2[i]);
                    toothDepthNameTwo[i]="X";
                }
            } else {
                String[] Spaces = spaceGroup[0].split(",");
                maxToothPosition = Integer.parseInt(Spaces[0]);

                toothDepthNameOne =new String[Spaces.length];
                toothDepthNameTwo =new String[Spaces.length];


                toothDistancesOne = new int[Spaces.length];
                toothDistancesTwo = new int[Spaces.length];
                for (int i = 0; i < Spaces.length; i++) {
                    toothDepthNameOne[i]="X";
                    toothDepthNameTwo[i]="X";
                    // 把齿位数据转为int
                    toothDistancesOne[i] = Integer.parseInt(Spaces[i]);
                    toothDistancesTwo[i] = Integer.parseInt(Spaces[i]);
                    spaceDataList1.add(Integer.parseInt(Spaces[i]));
                    spaceDataList2.add(Integer.parseInt(Spaces[i]));
                }
            }
        }
        //计算space数据的比例值
        spacesScaleValue = keyBodyWidth/maxToothPosition;
        if(p.getAlign()==0){
            //换算第一组齿位的数据
            for (int i = 0; i < toothDistancesOne.length; i++) {
                int toothPosition= (int)(toothDistancesOne[i]*spacesScaleValue)+132;
                //一组的换算好的齿位
                toothDistancesOne[i]= toothPosition;
            }
            //换算第二组的所有齿位的数据
            for (int i = 0; i < toothDistancesTwo.length ; i++) {
                int toothPosition= (int)(toothDistancesTwo[i]*spacesScaleValue)+132;
                //存储第二组的换算好的齿位
                toothDistancesTwo[i]=toothPosition;
            }
        }else if(p.getAlign()==1){
            int keySumX=(int)(keyBodyWidth+132);
            int minToothPosition=0;

            if(toothDistancesOne[toothDistancesOne.length-1]< toothDistancesTwo[toothDistancesTwo.length-1]){
                minToothPosition= toothDistancesOne[toothDistancesOne.length-1];
            }else {
                minToothPosition= toothDistancesTwo[toothDistancesTwo.length-1];
            }
            surplusX=keySumX- (keySumX- (int)(spacesScaleValue* minToothPosition));
            //换算第一组齿位的数据
            for (int i = 0; i < toothDistancesOne.length; i++) {
                int toothPosition= (keySumX-(int)(toothDistancesOne[i]*spacesScaleValue))+surplusX;
                //存储第一组的换算好的齿位
                toothDistancesOne[i]= toothPosition;
            }
            //换算第二组的所有齿位的数据
            for (int i = 0; i < toothDistancesTwo.length ; i++) {
                int toothPosition=(keySumX-(int)(toothDistancesTwo[i]*spacesScaleValue))+surplusX;
                //存储第二组的换算好的齿位
                toothDistancesTwo[i]=toothPosition;
            }

        }

        //计算钥匙深度比例值
        depthScaleRatio=210f/p.getWidth();
        //计算钥匙的宽度
        keyWidthY =p.getWidth()*depthScaleRatio;
        //钥匙的总宽度
        keySumY=keyWidthY+excessY;
        //计算钥匙的一半宽度
        keyHalfWidthY =(int)(keyWidthY /2)+excessY;

        if (depthGroup.length == 1) {
            //分割深度得到具体数据
            String[] depths = depthGroup[0].split(",");
            //分割深度名，得到深度名
            String[] depthNameArray = depthNameGroup[0].split(",");
            depthPositionArray1 = new int[depths.length];
            depthPositionArray2 = new int[depths.length];
            int eachDepth=0;
            for (int i = 0; i < depths.length; i++) {
                //换算每个深度的位子
                eachDepth = (int) (Integer.parseInt(depths[i]) * depthScaleRatio);
                //保存第二组深度的位子
                depthPositionArray2[i] = eachDepth + 60;
                depthMap2.put(depthNameArray[i], eachDepth + 60);
                //保存第一组深度的位子
                eachDepth = (int) keySumY - eachDepth;
                depthMap1.put(depthNameArray[i], eachDepth);
                depthPositionArray1[i] = eachDepth;
            }
        } else if (depthGroup.length == 2) {
            //分割第一组深度名，得到深度第一组所有的深度名
            String[] depthNameArray1 = depthNameGroup[0].split(",");
            //分割第一组深度数据
            String[] depths1 = depthGroup[0].split(",");
            depthPositionArray1 = new int[depths1.length];
            //换算第一组齿的深度
            int eachDepth = 0;
            for (int i = 0; i < depthPositionArray1.length; i++) {
                eachDepth = (int) (Integer.parseInt(depths1[i]) * depthScaleRatio);
                //保存第一组深度的位子
                eachDepth = (int) keySumY - eachDepth;
                depthPositionArray1[i] = eachDepth;
                depthMap1.put(depthNameArray1[i], eachDepth);
            }
            //分割第二组深度名，得到深度第一组所有的深度名
            String[] depthNameArray2 = depthNameGroup[1].split(",");
            //分割第二组深度数据
            String[] depths2 =depthGroup[1].split(",");
            depthPositionArray2=new int[depths2.length];
            //换算第二组齿的深度
            for (int i = 0; i < depthPositionArray2.length; i++) {
                //换算每个深度的位子
                eachDepth= (int)(Integer.parseInt(depths2[i]) * depthScaleRatio);
                //保存第二组齿的深度的位子
                depthPositionArray2[i] = eachDepth + 60;
                depthMap2.put(depthNameArray2[i], eachDepth + 60);
            }
        }
        //得到每组深度的第一个位子和最一个位子的数据
        depthStartY1 = depthPositionArray1[0];
        depthEndY1 = depthPositionArray1[depthPositionArray1.length - 1];
        depthStartY2 = depthPositionArray2[0];
        depthEndY2 = depthPositionArray2[depthPositionArray2.length - 1];

    }

    public BilateralKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setDrawPatternSize(int width, int height) {
        this.patternWidth=width;
        this.patternHeight=height;

    }

    /**
     * 解析spase到绘制的齿距
     */
    private  void analysisToothDistances(){
       spaceGroup =ki.getSpace().split(";");
        String[] spacesOne;
        String[] spacesTwo;
                if(spaceGroup.length==AGROUP){   //齿距只有一组
                    spacesOne= spaceGroup[0].split(",");
                    spacesTwo= spaceGroup[0].split(",");
                    //转为int类型
                    for (int i = 0; i <spacesOne.length ; i++) {
                         toothDistancesListOne.add(Integer.parseInt(spacesOne[i]));
                          toothDistancesListTwo.add(Integer.parseInt(spacesTwo[i]));
                    }
                    toothDepthNameOne=new String[spacesOne.length];  //第一组
                    toothDepthNameTwo=new String[spacesTwo.length];   //第二组
                    drawToothCode();
                }else {

                }
    }

    @Override
    public void setShowDrawDepthAndDepthName(boolean isDraw) {

    }

    /**
     * 绘制的齿码
     */
    private void  drawToothCode(){
        if(TextUtils.isEmpty(ki.getKeyToothCode())){
                for (int i = 0; i <toothDepthNameOne.length ; i++) {   // 第一组
                    toothDepthNameOne[i]="X";
                }
                for (int i = 0; i <toothDepthNameTwo.length ; i++) {    //第二组
                    toothDepthNameTwo[i]="X";
                }
        }else {
         String[]   toothCode = ki.getKeyToothCode().split(",");
            if(spaceGroup.length==AGROUP){  //只有一组
                for (int i = 0; i <toothDepthNameOne.length ; i++) {   // 第一组
                    toothDepthNameOne[i]=toothCode[i];
                }
                for (int i = 0; i <toothDepthNameTwo.length ; i++) {    //第二组
                    toothDepthNameTwo[i]=toothCode[i];
                }
            }else {     //有2组
                int j=0;
                for (int i = 0; i <toothDepthNameOne.length ; i++) {   //
                    toothDepthNameOne[i]=toothCode[j];
                    j++;
                }
                for (int i = 0; i <toothDepthNameTwo.length; i++) {
                    toothDepthNameTwo[i]=toothCode[j];
                    j++;
                }
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("固定宽度", "onMeasure: " + widthSize);
        Log.d("固定高度", "onMeasure: " + heightSize);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bilateralKey(canvas);
    }

    /**
     * 设置钥匙齿的深度名
     * @param depthName
     */
    public void setToothDepthName(String depthName) {
        if(!TextUtils.isEmpty(depthName)){
            allToothDepthName = depthName.split(",");
            if(spaceGroup.length==1){
                for (int i = 0; i < allToothDepthName.length ; i++) {
                    toothDepthNameOne[i]= allToothDepthName[i];
                    toothDepthNameTwo[i]= allToothDepthName[i];
                }
            }else if(spaceGroup.length==2){
                int j=0;
                for (int i = 0; i < allToothDepthName.length ; i++) {
                    if(i< toothDepthNameOne.length){
                        toothDepthNameOne[i]= allToothDepthName[i];
                    }else {
                        toothDepthNameTwo[j]= allToothDepthName[i];
                        j++;
                    }
                }
            }
        }

    }

    @Override
    public void redrawKey() {
        this.invalidate();//重绘
    }

    /**
     * 设置钥匙齿的数量
     * @param toothAmount
     */
    public void setKeyToothAmount(int toothAmount) {
        toothDistancesOne =new int[toothAmount];
        toothDistancesTwo =new int[toothAmount];
        //重新new 齿的深度名数组
        toothDepthNameOne =new String[toothAmount];
        toothDepthNameTwo =new String[toothAmount];
        spaceDataArray1=new int[toothAmount];
        spaceDataArray2=new int[toothAmount];
        for (int i = 0; i < toothDistancesOne.length; i++) {
            toothDistancesOne[i]= spaceDataList1.get(i);
            spaceDataArray1[i]=spaceDataList1.get(i);
            toothDepthNameOne[i]="X";
        }
        for (int i = 0; i < toothDistancesTwo.length; i++) {
            toothDistancesTwo[i]= spaceDataList2.get(i);
            spaceDataArray2[i]=spaceDataList2.get(i);
            toothDepthNameTwo[i]="X";
        }
        if(ki.getAlign() == 0){
            if (spaceGroup.length == 2) {
                if (toothDistancesOne[toothDistancesOne.length-1] > toothDistancesTwo[toothDistancesTwo.length-1]) {
                    maxToothPosition = toothDistancesOne[toothDistancesOne.length-1];
                } else {
                    maxToothPosition = toothDistancesTwo[toothDistancesTwo.length-1];
                }
            }else {
                maxToothPosition= toothDistancesOne[toothDistancesOne.length-1];
            }
        }else if(ki.getAlign()==1){
            if (spaceGroup.length ==2) {
                if(toothDistancesOne[0] > toothDistancesTwo[0]){
                    maxToothPosition= toothDistancesOne[0];
                }else {
                    maxToothPosition= toothDistancesTwo[0];
                }
            }else {
                maxToothPosition= toothDistancesOne[0];
            }
        }
        //计算space的比例值
        spacesScaleValue = keyBodyWidth/maxToothPosition;
        if(ki.getAlign()==0){
            //换算第一组齿位的数据
            for (int i = 0; i < toothDistancesOne.length; i++) {
                int toothPosition= (int)(toothDistancesOne[i]*spacesScaleValue)+132;
                //一组的换算好的齿位
                toothDistancesOne[i]= toothPosition;

            }
            //换算第二组的所有齿位的数据
            for (int i = 0; i < toothDistancesTwo.length ; i++) {
                int toothPosition= (int)(toothDistancesTwo[i]*spacesScaleValue)+132;
                //存储第二组的换算好的齿位
                toothDistancesTwo[i]=toothPosition;

            }
        }else if(ki.getAlign()==1){
            int keySumX=(int)(keyBodyWidth+132);
            int minToothPosition=0;

            if(toothDistancesOne[toothDistancesOne.length-1]< toothDistancesTwo[toothDistancesTwo.length-1]){
                minToothPosition= toothDistancesOne[toothDistancesOne.length-1];
            }else {
                minToothPosition= toothDistancesTwo[toothDistancesTwo.length-1];
            }
            surplusX=keySumX- (keySumX- (int)(spacesScaleValue* minToothPosition));
            //换算第一组齿位的数据
            for (int i = 0; i < toothDistancesOne.length; i++) {
                int toothPosition= keySumX-(int)(toothDistancesOne[i]*spacesScaleValue)+surplusX;
                //存储第一组的换算好的齿位
                toothDistancesOne[i]= toothPosition;
            }
            //换算第二组的所有齿位的数据
            for (int i = 0; i < toothDistancesTwo.length ; i++) {
                int toothPosition=(keySumX-(int)(toothDistancesTwo[i]*spacesScaleValue))+surplusX;
                //存储第二组的换算好的齿位
                toothDistancesTwo[i]=toothPosition;
            }
        }
        //重绘
        this.invalidate();

    }

    @Override
    public String getSpace() {
        String space="";
        if(spaceGroup.length==1){
            for (int i = 0; i <spaceDataArray1.length ; i++) {
                if(i==(spaceDataArray1.length-1)){
                    space+=spaceDataArray1[i];
                }else {
                    space+=(spaceDataArray1[i]+",");
                }
            }
            space+=";";
            return  space;

        }else if(spaceGroup.length==2){
                for (int i = 0; i <spaceDataArray1.length ; i++) {
                    space+=(spaceDataArray1[i]+",");
                }
                space+=";";
                for (int i = 0; i <spaceDataArray2.length ; i++) {
                     space+=(spaceDataArray2[i]+",");
                }
                space+=";";
                return  space;
        }
        return null;

    }

    public int getKeyToothAmount() {

        return spaceDataList1.size();
}

    @Override
    public ArrayList<String[]> getAllToothDepthName() {
        toothNameGroupList.clear();
        if(spaceGroup.length==1){
            toothNameGroupList.add(toothDepthNameOne);
        }else if(spaceGroup.length==2){
            toothNameGroupList.add(toothDepthNameOne);
            toothNameGroupList.add(toothDepthNameTwo);
        }
        return toothNameGroupList;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
        this.isShowArrows=isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        for (int i = 0; i < toothDepthNameOne.length ; i++) {
            toothDepthNameOne[i]="X";
        }
        for (int i = 0; i < toothDepthNameTwo.length ; i++) {
            toothDepthNameTwo[i]="X";
        }
    }

    /**
     *   获得双边钥匙读钥匙指令
     * @param locatingSlot
     * @param detectionMode
     * @param isRound
     * @return
     */

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";
        String toothQuantity;
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
              //双边齿
            if(ki.getSide()==0){
                ki.setSide(2);
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
            lastToothOrExtraCut = ki.getLastBitting();
            String order = "!DR1;!BC;"
                    + keyNorm     //钥匙规范
                    + ki.getType() + "," //钥匙类型
                    + ki.getAlign() + ","//钥匙定位方式
                    + ki.getSide() + ","// 有效边
                    + ki.getWidth() + ","// 钥匙片宽度
                    + ki.getThick() + ","// 钥匙胚厚度
                    + ki.getGuide() + ","// 表示加不加切
                    + ki.getLength() + ","//钥匙片长度
                    + ki.getCutDepth() + ","//切割深度
                    + toothQuantity// 齿的数量
                    + toothPositionData//齿位置数据
                    + toothWidth//齿顶宽数据
                    + toothDepthQuantity + ","//齿深数量
                    + toothDepthData//齿深数据
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
    public String getCutOrder(int cutDepth,int locatingSlot,String assistClamp,String cutterDiameter,int speed, int ZDepth,int detectionMode) {
        String keyNorm ="!SB";
        String toothQuantity;
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut;
        int knifeType=1;  //到类型
        int noseCut;
        String DDepth ="0.75";
        if(TextUtils.isEmpty(assistClamp)){
            assistClamp="";
        }
        //双边齿
        if(ki.getSide()==0){
            ki.setSide(2);
        }
        if(ki.getNose()!=0){  //有鼻部就切割
            noseCut=1;  //1为切割鼻部
        }else {
            noseCut=0;  //0不切割鼻部
        }
        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothCode = ki.getKeyToothCode();  //获得实际的齿号
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)
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
        lastToothOrExtraCut = ki.getLastBitting();
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide() + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getGuide() + ","// 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","//切割深度
                + toothQuantity// 齿的数量
                + toothPositionData//齿位置数据
                + toothWidth//齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData//齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码
                + ki.getNose() + ","  // 鼻部长度
                + ki.getGroove() + ","  //槽宽
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
     * 两边钥匙
     *
     * @param canvas
     */
    public void bilateralKey(Canvas canvas) {
        mPath.moveTo(30, 40);
        mPath.lineTo(80, 40);
        mPath.quadTo(92, 39, 90, 60);
        mPath.lineTo(110, 60);
        int lastToothPosition = 0;

            //绘制第一边所有齿的位子和齿深
            for (int i = 0; i < toothDistancesOne.length; i++) {
                if (toothDepthNameOne[i].equals("X")) {
                     mPath.lineTo(toothDistancesOne[i]-toothWidth, 60);
                     mPath.lineTo(toothDistancesOne[i]+toothWidth, 60);
                } else if (toothDepthNameOne[i].contains(".")) {
                    //分割齿的深度名
                    String[] str= toothDepthNameOne[i].split("\\.");
                    if(depthMap1.get(str[0])==null){
                        mPath.lineTo(toothDistancesOne[i]-toothWidth, 60);
                        mPath.lineTo(toothDistancesOne[i]+toothWidth, 60);
                    }else {
                        int depthData = depthMap1.get(str[0]);
                        float  decimals= Float.parseFloat("0."+ str[1]);
                        float  sumY=0;
                        if(depthData ==depthPositionArray1[depthPositionArray1.length-1]){
                            //等于最后一个深度不计算。
                        }else {
                            for(int j=0;j<depthPositionArray1.length;j++){
                                if(depthData ==depthPositionArray1[j]){
                                    int distance =depthPositionArray1[j+1]- depthData;
                                    sumY=distance*decimals;
                                    break;
                                }
                            }
                        }
                        mPath.lineTo(toothDistancesOne[i]-toothWidth,depthData+sumY);
                        mPath.lineTo(toothDistancesOne[i]+toothWidth,depthData+sumY);
                    }
                }else {
                    mPath.lineTo(toothDistancesOne[i]-toothWidth,depthMap1.get(toothDepthNameOne[i])==null?60:depthMap1.get(toothDepthNameOne[i]));
                    mPath.lineTo(toothDistancesOne[i]+toothWidth,depthMap1.get(toothDepthNameOne[i])==null?60:depthMap1.get(toothDepthNameOne[i]));
                }
            }
        //得到最后一个齿位长度
        lastToothPosition= toothDistancesOne[toothDistancesOne.length - 1]+toothWidth;
        //计算是不是小于第二组的长度
        if ((toothDistancesOne[toothDistancesOne.length - 1] + toothWidth) < (toothDistancesTwo[toothDistancesTwo.length - 1] + toothWidth)) {
            excessX = (toothDistancesTwo[toothDistancesTwo.length - 1] + toothWidth) - (toothDistancesOne[toothDistancesOne.length - 1] + toothWidth);
            lastToothPosition = lastToothPosition + excessX;
            mPath.lineTo(lastToothPosition, 60);
        }
            mPath.lineTo(lastToothPosition + 54, keyHalfWidthY-5);
            mPath.quadTo(lastToothPosition+58, keyHalfWidthY+2.5f,lastToothPosition + 54, keyHalfWidthY +10);
            mPath.lineTo(lastToothPosition,(int) keySumY);
        //绘制第二边所有齿的位子和齿深
        for (int i = toothDistancesTwo.length-1; i >=0; i--) {
            if (toothDepthNameTwo[i].equals("X")) {
                if(i== toothDistancesTwo.length-1){
                    mPath.lineTo(toothDistancesTwo[i]-toothWidth,keySumY);
                }else {
                    mPath.lineTo(toothDistancesTwo[i]+toothWidth,keySumY);
                    mPath.lineTo(toothDistancesTwo[i]-toothWidth,keySumY);
                }
            } else if (toothDepthNameTwo[i].contains(".")) {
                String[] str = toothDepthNameTwo[i].split("\\.");
                if(depthMap2.get(str[0])==null) {
                    if (i == toothDistancesTwo.length - 1) {
                        mPath.lineTo(toothDistancesTwo[i]-toothWidth,keySumY);
                    } else {
                        mPath.lineTo(toothDistancesTwo[i]+toothWidth,keySumY);
                        mPath.lineTo(toothDistancesTwo[i]-toothWidth,keySumY);
                    }
                }else {
                    int depthData = depthMap2.get(str[0]);
                    float  decimals= Float.parseFloat("0."+ str[1]);
                    float  sumY=0;
                    if(depthData ==depthPositionArray2[depthPositionArray1.length-1]){
                        //等于最后一个深度不计算。
                    }else {
                        for(int j=0;j<depthPositionArray2.length;j++){
                            if(depthData ==depthPositionArray2[j]){
                                int distance =depthPositionArray2[j+1]- depthData;
                                sumY=distance*decimals;
                                break;
                            }
                        }
                    }
                    //i==最后一个长度 说明 i 里面有东西
                    if(i== toothDistancesTwo.length-1){
                        mPath.setLastPoint(lastToothPosition,depthData+sumY);
                        mPath.lineTo(toothDistancesTwo[i]-toothWidth,depthData+sumY);
                    }else {
                        mPath.lineTo(toothDistancesTwo[i]+toothWidth,depthData+sumY);
                        mPath.lineTo(toothDistancesTwo[i]-toothWidth,depthData+sumY);
                    }
                }
            }else {
                if(i== toothDistancesTwo.length-1) {
                    mPath.setLastPoint(lastToothPosition,depthMap2.get(toothDepthNameTwo[i]) == null ? (int) keySumY : depthMap2.get(toothDepthNameTwo[i]));
                    mPath.lineTo(toothDistancesTwo[i] -toothWidth, depthMap2.get(toothDepthNameTwo[i]) == null ? (int) keySumY : depthMap2.get(toothDepthNameTwo[i]));
                }else {
                    mPath.lineTo(toothDistancesTwo[i] + toothWidth, depthMap2.get(toothDepthNameTwo[i]) == null ? (int) keySumY : depthMap2.get(toothDepthNameTwo[i]));
                    mPath.lineTo(toothDistancesTwo[i] - toothWidth, depthMap2.get(toothDepthNameTwo[i]) == null ? (int) keySumY : depthMap2.get(toothDepthNameTwo[i]));
                }
            }
        }
        mPath.lineTo(110,keySumY);
        mPath.lineTo(90,keySumY);
        mPath.quadTo(91,keySumY+22,80, keySumY+20);
        mPath.lineTo(29,keySumY+20);
        canvas.drawPath(mPath, mBorderPaint);
        canvas.drawPath(mPath, mKeyAppearanceColorPaint);
        //重置
        mPath.reset();
        //画深度第一组深度的值
        for (int i = 0; i < depthPositionArray1.length; i++) {
            mPath.moveTo(90, depthPositionArray1[i]);
            mPath.lineTo(lastToothPosition, depthPositionArray1[i]);
        }
        //画深度第二组深度的值
        for (int i = 0; i < depthPositionArray2.length; i++) {
            mPath.moveTo(90, depthPositionArray2[i]);
            mPath.lineTo(lastToothPosition, depthPositionArray2[i]);
        }
        canvas.drawPath(mPath, mDashedPaint);
        //画第一组齿位
        for (int i = 0; i < toothDistancesOne.length; i++) {
            mPath.moveTo(toothDistancesOne[i], depthStartY1);
            mPath.lineTo(toothDistancesOne[i], depthEndY1);
            canvas.drawPath(mPath, mDashedPaint);//画X轴方向的。
            if(toothDepthNameOne[i].contains(".")){
                if(toothDepthNameOne[i].contains("X")){
                    canvas.drawText("X", toothDistancesOne[i], 60-10, mTextColorPaint);//画红色的字
                }else {
                    String[] s = toothDepthNameOne[i].split("\\.");
                    canvas.drawText(s[0], toothDistancesOne[i],60-10, mTextColorPaint);//画红色的字
                }
            }else if(toothDepthNameOne.equals("X")){//不包涵画蓝色的字
                canvas.drawText("X", toothDistancesOne[i], 60 -10, p4);//画蓝色的字
            }else {
                canvas.drawText(toothDepthNameOne[i], toothDistancesOne[i], 60-10, p4);//画蓝色的字
            }

        }
        //画第二组齿位
        for (int i = 0; i < toothDistancesTwo.length; i++) {
            mPath.moveTo(toothDistancesTwo[i], depthStartY2);
            mPath.lineTo(toothDistancesTwo[i], depthEndY2);
            canvas.drawPath(mPath, mDashedPaint);
            if(toothDepthNameTwo[i].contains(".")){
                if(toothDepthNameTwo[i].contains("X")){
                    canvas.drawText("X", toothDistancesTwo[i], (int)keySumY+40, mTextColorPaint);//画红色的字
                }else {
                    String[] s = toothDepthNameTwo[i].split("\\.");
                    canvas.drawText(s[0], toothDistancesTwo[i],(int)keySumY+40, mTextColorPaint);//画红色的字
                }
            }else if(toothDepthNameTwo.equals("X")){//不包涵画蓝色的字
                canvas.drawText("X", toothDistancesTwo[i],(int)keySumY+40, p4);//画蓝色的字
            }else {
                canvas.drawText(toothDepthNameTwo[i], toothDistancesTwo[i],(int)keySumY+40, p4);//画蓝色的字
            }
        }
        mPath.reset();  //重置
        if(isShowArrows){
            if(ki.getAlign()==0){
                mPath.moveTo(90,20);  //100
                mPath.lineTo(120,20);//第一条  100
                mPath.lineTo(120,12);
                mPath.lineTo(130,24);//中间点  104
                mPath.lineTo(120,36);
                mPath.lineTo(120,28);
                mPath.lineTo(90,28);
                mPath.close();
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }else {
                mPath.moveTo(lastToothPosition, 30);//80
                mPath.lineTo(lastToothPosition + 10, 20);//70
                mPath.lineTo(lastToothPosition + 10, 27);//77
                mPath.lineTo(lastToothPosition + 35, 27);//77
                mPath.lineTo(lastToothPosition + 35, 33);//83
                mPath.lineTo(lastToothPosition + 10, 33);//83
                mPath.lineTo(lastToothPosition + 10, 40);//90
                mPath.lineTo(lastToothPosition, 30);//80
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }
        }

    }

    /**
     * 初始化路径和画笔
     */
  private  void initPaintAndPath() {
        mPath = new Path();//画点
        mBorderPaint = new Paint();//画点线的笔
        mDashedPaint = new Paint(); //画虚线的笔
        mTextColorPaint = new Paint();//画蓝色字体的笔
        p4 = new Paint(); //画红色字体的笔
        mArrowsPaint = new Paint();//画红色箭头的笔
        mKeyAppearanceColorPaint = new Paint();  //画钥匙身体颜色的笔
        //画钥匙形状属性
        mBorderPaint.setAntiAlias(true);//去掉抗锯齿
        mBorderPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        mBorderPaint.setStrokeWidth(2);
        //画虚线的属性
        mDashedPaint.setAntiAlias(true);//去掉抗锯齿
        mDashedPaint.setColor(Color.parseColor("#6699ff"));  //画笔的颜色  为蓝色
        mDashedPaint.setStyle(Paint.Style.STROKE);//设置画笔描边
        mDashedPaint.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        mDashedPaint.setPathEffect(effects);
        //画字体的属性
        mTextColorPaint.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        mTextColorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mTextColorPaint.setTextSize(40);
        p4.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        p4.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        p4.setTextSize(40);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //画主体颜色的笔的属性
        mKeyAppearanceColorPaint.setColor(Color.parseColor("#BABABA"));
        mKeyAppearanceColorPaint.setAntiAlias(true);
        mKeyAppearanceColorPaint.setStyle(Paint.Style.FILL);
        mKeyAppearanceColorPaint.setStrokeWidth(1);
    }
}
