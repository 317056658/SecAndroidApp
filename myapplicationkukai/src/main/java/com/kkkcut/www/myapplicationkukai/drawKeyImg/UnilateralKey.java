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
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/15.
 * 单边钥匙
 */

public class UnilateralKey extends Key {
    private Path mPath;//画路径
    private Paint mBorderPaint;//画边框笔
    private Paint mDashedPaint; //画虚线的笔
    private Paint mTextColorPaint;//画红色字体的笔
    private Paint mArrowsPaint;//画提示箭头
    private Paint mKeyAppearanceColorPaint;//钥匙外貌颜色的笔
    private KeyInfo ki;
    private String[] spaceGroup,depthGroup;
    private int extraTopY;
    private String[] spaces;  //保存解析好的齿位
    private LinkedList<Integer> spaceDataList =new LinkedList();
    private String[] toothDepthNameGroup;
    private Map<String,Integer> drawDepthPositionMap; //存放解析好的深度位子
    private int patternBodyMaxY;
    private double depthScaleValue;
    private float spacesScaleValue;
    private int[] depthPosition;
    private int depthStartY,depthEndY;  //定义一个y轴的第一点,  //定义一个y轴的最后一个点
    private int patternPercent56Y;
    private ArrayList<String[]> drawToothDepthNameList; //定义一个 保存每组齿的深度名集合
    private int maxToothDistance, halfToothWidth =14;
    private float patternBodyWidth =458f;  //定义一个钥匙身体宽度和高度
    private int[] toothDistances;
    private double patternBodyHeight =210;
    private int  patternShoulderWidth;
    private  boolean  isShowArrows=true;
    private int patternWidth,patternHeight;
    private  float patternCuspWidth;
    private   int extraWidth =20;  // 额外的20
    private boolean isDraw =true;
    private  String[] depthNames;


    public UnilateralKey(Context context,KeyInfo ki) {
         this(context,null,ki);
    }
    public UnilateralKey(Context context, AttributeSet attrs,KeyInfo ki) {
         this(context,attrs,0,ki);
    }


    public UnilateralKey(Context context, AttributeSet attrs, int defStyleAttr,KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.ki =ki;

        //初始化保存每组齿的深度名集合
        drawToothDepthNameList =new ArrayList<>();
        //保存深度位子的map
        drawDepthPositionMap =new HashMap<>();
        this.initPaintAndPath();
    }

    /**
     * 设置图案的大小
     * @param width
     * @param height
     */
    @Override
    public void setDrawPatternSize(int width, int height) {
        if(ki ==null){
            return;
        }
        patternWidth=width;
        patternHeight=height;
        patternBodyWidth= (int)(patternWidth*0.73);   //百分之75
        extraWidth=(int)(patternWidth*0.05); //百分之5
        patternShoulderWidth=(int)(patternWidth*0.1);//百分之10
        patternCuspWidth=(int)(patternWidth*0.12);//百分之10
        patternBodyHeight = (int) (patternHeight*0.82);  //百分之82
        extraTopY =(int)(patternHeight*0.14);   //上部百分之12
        this.analysisToothDistances();
        this.analysisDepthAndDepthName();
    }


    /**
     * 解析数据到绘制的齿距
     */
    private void analysisToothDistances(){
        spaceGroup = ki.getSpace().split(";");//分割齿位有几组
        for (int i = 0; i < spaceGroup.length; i++) {
            spaces = spaceGroup[i].split(",");//分割",",得到具体齿的数量
            for (int j = 0; j < spaces.length; j++) {
                //保存space数据的集合 转为int
                spaceDataList.add(Integer.parseInt(spaces[j]));
            }
        }
        //获得传过来的钥匙的最大齿位数据
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
            maxToothDistance =spaceDataList.get(spaceDataList.size()-1);
        }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
            maxToothDistance =spaceDataList.get(0);
        }
        //得到所有齿位的比例值
        spacesScaleValue=this.patternBodyWidth / maxToothDistance;
        toothDistances =new int[spaceDataList.size()];
        toothDepthNameGroup =new String[spaceDataList.size()];
        this.drawToothCode();
        //计算每个齿距的位子
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
            for (int i = 0; i < toothDistances.length ; i++) {
                toothDistances[i]=(int)((spaceDataList.get(i)* spacesScaleValue)+patternShoulderWidth)+ extraWidth;
            }
        }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN) {
            int keySumX=(int)(this.patternBodyWidth +patternShoulderWidth);
            int surplusX=keySumX- (keySumX- (int)(spaceDataList.get(spaceDataList.size()-1)* spacesScaleValue));
            for (int i = 0; i < toothDistances.length ; i++) {
                toothDistances[i]= (keySumX-(int)(spaceDataList.get(i)* spacesScaleValue))+surplusX;
            }
        }
    }

    /**
     * 绘制齿的齿码
     */
    private  void drawToothCode(){
        if(TextUtils.isEmpty(ki.getKeyToothCode())){
            for (int i = 0; i < toothDepthNameGroup.length; i++) {
                toothDepthNameGroup[i]="X";
            }
        }else {
            toothDepthNameGroup = ki.getKeyToothCode().split(",");
        }
    }

    /**
     *
     * 解析数据到 绘制的深度和深度名
     */
    private void analysisDepthAndDepthName(){
        if(depthGroup!=null){
            return;
        }
        depthScaleValue= patternBodyHeight / ki.getWidth();
        patternPercent56Y =(int)(patternBodyHeight*0.56)+ extraTopY;
        patternBodyMaxY =(int)patternBodyHeight+extraTopY;
        //分割深度名
        String[] depthNameGroup= ki.getDepth_name().split(";");
        depthNames =depthNameGroup[0].split(",");
        //分割深度组
        depthGroup= ki.getDepth().split(";");
        String[] depths =depthGroup[0].split(",");
        depthPosition =new int[depths.length];
        for (int i = 0; i < depths.length; i++) {
            depthPosition[i]= patternBodyMaxY -(int)(Integer.parseInt(depths[i])*depthScaleValue);
            //存储解析好的深度位子  以键.值.对的方式
            drawDepthPositionMap.put(depthNames[i], depthPosition[i]);
        }
        depthStartY= depthPosition[0];
        depthEndY= depthPosition[depthPosition.length-1];
    }

    /**
     * 设置绘制的深度深度
     * @param depthName
     */
    @Override
    public void setToothDepthName(String depthName) {
        if(!TextUtils.isEmpty(depthName)) {
            toothDepthNameGroup =depthName.split(",");//解析好的
        }
    }

    /**
     * 自定义绘制锯齿状
     */
    public void customDrawSerrated(){
        int  k=0;
        int j=1;
        for (int i = 0; i <toothDepthNameGroup.length ; i++) {
            if(i<depthNames.length){
                if(i==2){
                    toothDepthNameGroup[i]="X";
                }else if(i==toothDepthNameGroup.length-1) {
                    toothDepthNameGroup[i]=depthNames[k];
                }else{
                    toothDepthNameGroup[i]=depthNames[i];
                }
            }else {
                if(depthNames.length==2){
                    toothDepthNameGroup[i]=depthNames[depthNames.length-j];
                    j++;
                }else {
                    toothDepthNameGroup[i]=depthNames[0];
                }
            }
        }
    }

    /**
     * 设置绘制的齿宽度
     * @param width
     */
    public  void  setDrawToothWidth(int width){
           halfToothWidth=width/2;
    }
    /**
     * 设置需要绘制的属性
     * @param ki
     */
    @Override
    public void setNeededDrawAttribute(KeyInfo ki) {

    }

    /**
     * 重绘钥匙
     */
    @Override
    public void redrawKey() {
        this.invalidate();//重绘
    }

    /**
     * 设置齿位的数量
     * @param toothAmount
     */
    public void setKeyToothAmount(int toothAmount) {

        //-----分割线
        spaceDataList.clear();  //清除所以的space数据
        for (int i = 0; i< toothAmount; i++){
                spaceDataList.add(Integer.parseInt(spaces[i]));
        }
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
            maxToothDistance =spaceDataList.get(spaceDataList.size()-1);
        }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
            maxToothDistance =spaceDataList.get(0);
        }
        spacesScaleValue= patternBodyWidth / maxToothDistance;
        //-----分割线
        toothDistances =new int[toothAmount];
        toothDepthNameGroup =new String[toothAmount];
        //计算每个齿距的位子
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
            for (int i = 0; i <spaceDataList.size() ; i++) {
                toothDistances[i]=(int)(spaceDataList.get(i)* spacesScaleValue)+132;
                toothDepthNameGroup[i]="X";
            }
        }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN) {
            int keySumX=(int)(patternBodyWidth +132);
            int surplusX=keySumX-(keySumX- (int)(spaceDataList.get(spaceDataList.size()-1)* spacesScaleValue));
            for (int i = 0; i <spaceDataList.size(); i++) {
                toothDistances[i]= (keySumX-(int)(spaceDataList.get(i)* spacesScaleValue))+surplusX;
                toothDepthNameGroup[i]="X";
            }
        }
        this.invalidate();
    }

   @Override
    public String getSpace() {
        return TextUtils.join(",", spaceDataList);
    }

    public int getKeyToothAmount() {
        return spaceDataList.size();
    }

    /**
     *   获得全部钥匙的全部齿的深度名
     * @return
     */
    @Override
    public ArrayList<String[]> getAllToothDepthName() {
        drawToothDepthNameList.clear();
        drawToothDepthNameList.add(toothDepthNameGroup);
        return drawToothDepthNameList;
    }

    /**
     * 设置箭头是否显示
     * @param isShowArrows
     */
    @Override
    public void setShowArrows(boolean isShowArrows) {
            this.isShowArrows=isShowArrows;
    }
    /**
     *   设置全部齿的深度名为默认 默认为X；
     */
    @Override
    public void setToothCodeDefault() {
        for (int i = 0; i < toothDepthNameGroup.length; i++) {
            toothDepthNameGroup[i]="X";
        }
    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";
        String toothQuantity;
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity ;
        String toothDepthData = "";
        String toothMark = "";
        String toothDepthName = "";
        int lastToothOrExtraCut ;

        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length+","; //钥匙齿数(第一组)

        for (int i = 0; i < spaceGroup.length; i++) {
            spaces = spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothPositionData += spaces[j] + ",";//钥匙的齿位数据(如果有多组中间以,分割)
                toothMark += "0,";  //齿号
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
                + 0 + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + 0 + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿距数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothMark   //齿号代码  其实都是零
                + 0 + ","  // 鼻部长度   默认为0
                + 0 + ","  //槽宽   默认为0
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    /**
     * 单边钥匙的切割指令
     * @param cutDepth
     * @param locatingSlot
     * @param assistClamp
     * @param cutterDiameter
     * @param speed
     * @param ZDepth
     * @param detectionMode
     * @return
     */

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm ="!SB";
        String toothQuantity;
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothMark = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int knifeType=1;  //到类型
        int noseCut;
        String DDepth ="0.75";
        if(TextUtils.isEmpty(assistClamp)){
            assistClamp="";
        }

        if(ki.getNose()!=0){  //有鼻部就切割
            noseCut=1;  //1为切割鼻部
        }else {
            noseCut=0;  //0不切割鼻部
        }
        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothMark= ki.getKeyToothCode();  //获得实际的齿号
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
                + "0" + ","// 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","//切割深度
                + toothQuantity// 齿的数量
                + toothPositionData//齿位置数据
                + toothWidth//齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData//齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothMark   //齿号代码
                + ki.getNose() + ","  // 鼻部长度
                + ki.getGroove() + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                +"!AC"+assistClamp+";"  // 辅助夹具
                +"!ST"+knifeType+"," //刀类型
                +cutterDiameter+";"    //刀的规格（刀的直径）
                +"!CK"
                +speed+"," //速度
                +DDepth +","//D深度
                +ZDepth+","  //Z深度
                +noseCut+","  //鼻部切割
                +detectionMode+";";  //切割钥匙的检测方式
        return order;

    }

    /**
     * 是否绘制深度虚线和齿的深度名
     * 默认 isDraw为true
     * @param isDraw
     */
    public   void setShowDrawDepthAndDepthName(boolean isDraw){
        this.isDraw =isDraw;
    }



    /**
     * 设置齿位
     */
   @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        Log.d("固定宽度", "onMeasure: " + widthSize);
        Log.d("固定高度", "onMeasure: " + heightSize);
        setMeasuredDimension(widthSize,heightSize);  //保存测量的高度
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画单边钥匙
        drawKeyPattern(canvas);
    }
    /**
     * 绘制钥匙图像
     * @param canvas
     */
    private void drawKeyPattern(Canvas canvas) {
        if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
            mPath.moveTo(0, 3);
            int sX1=(int)(patternShoulderWidth*0.9f);
            mPath.lineTo(sX1,3);
            mPath.quadTo(patternShoulderWidth,4, patternShoulderWidth,(extraTopY-2)*0.3f);
            mPath.lineTo(patternShoulderWidth,extraTopY);
            mPath.lineTo(patternShoulderWidth+ extraWidth,extraTopY);
        }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
            mPath.moveTo(0,extraTopY);
            mPath.lineTo(patternShoulderWidth+ extraWidth,extraTopY);
        }
        for (int i = 0; i < toothDistances.length ; i++) {
            if(toothDepthNameGroup[i].equals("X")){
               mPath.lineTo(toothDistances[i]- halfToothWidth,extraTopY);
                mPath.lineTo(toothDistances[i]+ halfToothWidth,extraTopY);
            }else if(toothDepthNameGroup[i].contains(".")){
                //分割齿的深度名
                String[] newStr = toothDepthNameGroup[i].split("\\.");
                int depthValue;
                float  decimals;
                float valueY;
                int distanceY;
                if(newStr[0].equals("X")){
                    depthValue=drawDepthPositionMap.get(depthNames[0]);
                    decimals=Float.parseFloat("0."+ newStr[1]);
                    distanceY =depthValue-extraTopY;
                    valueY =distanceY*decimals;
                    mPath.lineTo(toothDistances[i]- halfToothWidth,depthValue- valueY);
                    mPath.lineTo(toothDistances[i]+ halfToothWidth,depthValue- valueY);
                }else  if(drawDepthPositionMap.get(newStr[0])==null){
                    mPath.lineTo(toothDistances[i]- halfToothWidth,extraTopY);
                    mPath.lineTo(toothDistances[i]+ halfToothWidth,extraTopY);
                }else {
                    depthValue = drawDepthPositionMap.get(newStr[0]);  //获得值
                    decimals= Float.parseFloat("0."+ newStr[1]);  //转为小数
                     valueY =0;
                    if(depthValue == depthPosition[depthPosition.length-1]){
                        //等于最后一个深度位置不计算。
                    }else {
                        for(int j = 0; j< depthPosition.length; j++){
                            if(depthValue == depthPosition[j]){
                                 distanceY = depthPosition[j+1]- depthValue;
                                valueY =distanceY*decimals;
                                break;
                            }
                        }
                    }
                    mPath.lineTo(toothDistances[i]- halfToothWidth, depthValue + valueY);
                    mPath.lineTo(toothDistances[i]+ halfToothWidth, depthValue + valueY);
                }
            }else {
                mPath.lineTo(toothDistances[i]- halfToothWidth, drawDepthPositionMap.get(toothDepthNameGroup[i])==null?extraTopY: drawDepthPositionMap.get(toothDepthNameGroup[i]));
                mPath.lineTo(toothDistances[i]+ halfToothWidth, drawDepthPositionMap.get(toothDepthNameGroup[i])==null?extraTopY: drawDepthPositionMap.get(toothDepthNameGroup[i]));
            }
        }
        Log.d("绘制开始", "drawKeyPattern: ");
        int lastToothPositionWidth = toothDistances[toothDistances.length-1]+ halfToothWidth;
        mPath.lineTo(lastToothPositionWidth +(patternCuspWidth*0.93f), patternPercent56Y);
        int patternPercent66Y =(int)(patternBodyHeight*0.66)+ extraTopY;//百分之60
        int patternPercent61Y =(int)(patternBodyHeight*0.61)+ extraTopY;
        mPath.quadTo(lastToothPositionWidth +patternCuspWidth, patternPercent61Y,lastToothPositionWidth +(patternCuspWidth*0.93f), patternPercent66Y);
        mPath.lineTo(lastToothPositionWidth, patternBodyMaxY);
        mPath.lineTo(0, patternBodyMaxY);
        //绘制钥匙模样
        canvas.drawPath(mPath, mBorderPaint);
        //填充钥匙颜色
        canvas.drawPath(mPath, mKeyAppearanceColorPaint);
        //重置
        mPath.reset();
        if(isDraw){
            //画深度
            for (int i = 0; i < depthPosition.length ; i++) {
                mPath.moveTo(patternShoulderWidth, depthPosition[i]);
                mPath.lineTo(lastToothPositionWidth, depthPosition[i]);
            }
            canvas.drawPath(mPath, mDashedPaint);
            mPath.reset();
            //画齿位
            for (int i = 0; i < toothDistances.length ; i++) {
                mPath.moveTo(toothDistances[i],depthStartY);
                mPath.lineTo(toothDistances[i],depthEndY);
            }
            canvas.drawPath(mPath, mDashedPaint);
            mPath.reset();
            //画齿名的颜色
            for (int i = 0; i < toothDepthNameGroup.length ; i++) {
                if(toothDepthNameGroup[i].equals("X")||!toothDepthNameGroup[i].contains(".")){
                    mTextColorPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                    canvas.drawText(toothDepthNameGroup[i], toothDistances[i],depthEndY+50, mTextColorPaint);
                }else if(toothDepthNameGroup[i].contains(".")){
                    mTextColorPaint.setColor(Color.parseColor("#FF3030"));//红色
                    String[] newStr = toothDepthNameGroup[i].split("\\.");
                    if(toothDepthNameGroup[i].contains("X")){
                        if(Integer.parseInt(newStr[1])>=5){
                            canvas.drawText(depthNames[0], toothDistances[i], depthEndY + 50, mTextColorPaint);//画红色的字
                        }else {
                            canvas.drawText(newStr[0], toothDistances[i], depthEndY + 50, mTextColorPaint);//画红色的字
                        }
                    }else {
                        if(Integer.parseInt(newStr[1])>=5){
                            if(newStr[0].equals(depthNames[depthNames.length-1])){
                                canvas.drawText(newStr[0], toothDistances[i], depthEndY + 50, mTextColorPaint);//画红色的字
                            }else {
                                for (int j = 0; j <depthNames.length ; j++) {
                                    if(depthNames[j].equals(newStr[0])){
                                        canvas.drawText(depthNames[j+1], toothDistances[i], depthEndY + 50, mTextColorPaint);//画红色的字
                                        break; //跳出循环
                                    }
                                }
                            }
                        }else {
                            canvas.drawText(newStr[0], toothDistances[i], depthEndY + 50, mTextColorPaint);//画红色的字
                        }
                    }
                }
            }
        }
        if(isShowArrows){   //是否显示箭头
            //画提示箭头
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                mPath.moveTo(92,20);  //100
                mPath.lineTo(120,20);//第一条  100
                mPath.lineTo(120,12);
                mPath.lineTo(130,24);//中间点  104
                mPath.lineTo(120,36);
                mPath.lineTo(120,28);
                mPath.lineTo(92,28);
                mPath.close();
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
                mPath.moveTo(lastToothPositionWidth +4,24);
                mPath.lineTo(lastToothPositionWidth +14,12);
                mPath.lineTo(lastToothPositionWidth +14,20);
                mPath.lineTo(lastToothPositionWidth +46,20);
                mPath.lineTo(lastToothPositionWidth +46,28);
                mPath.lineTo(lastToothPositionWidth +14,28);
                mPath.lineTo(lastToothPositionWidth +14,36);
                mPath.close();
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            }
        }
    }


    /**
     * 初始化路径和画笔
     */
    private  void initPaintAndPath(){
        mPath = new Path();//路径
        mBorderPaint = new Paint();//画边框线的笔
        mDashedPaint = new Paint(); //画虚线的笔
        mTextColorPaint = new Paint();//画字体颜色的笔
        mArrowsPaint = new Paint();//画箭头
        mKeyAppearanceColorPaint =new Paint(); //钥匙外貌颜色画笔
        //画钥匙边框属性
        mBorderPaint.setAntiAlias(true);//去掉抗锯齿
        mBorderPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        mBorderPaint.setStrokeWidth(2);
        //画虚线的属性
        mDashedPaint.setAntiAlias(true);//去掉抗锯齿
        mDashedPaint.setColor(Color.parseColor("#0033ff"));  //画笔的颜色  为蓝色
        mDashedPaint.setStyle(Paint.Style.STROKE);//设置画笔描边
        mDashedPaint.setStrokeWidth(1);
        PathEffect effects = new DashPathEffect(new float[]{3, 1}, 0);
        mDashedPaint.setPathEffect(effects);
        //画字体的颜色的画笔
        mTextColorPaint.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        mTextColorPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mTextColorPaint.setTextSize(40);
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
