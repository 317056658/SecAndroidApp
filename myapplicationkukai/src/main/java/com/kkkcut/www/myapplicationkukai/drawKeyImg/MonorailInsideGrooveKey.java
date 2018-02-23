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
 * 单轨内槽
 * Created by Administrator on 2017/7/7.
 */

public class MonorailInsideGrooveKey extends Key {
    private Path mPath;
    private Paint mBorderPaint;
    private Paint mKeyAppearanceColorPaint;
    private Paint mDashedPaint;
    private Paint mColorTextPaint;
    private Paint mArrowsPaint;
    private Paint mInsideGrooveColorPaint;
    private KeyInfo ki;
    private int[] toothDistanceOne, toothDistanceTwo;
    private String[] toothDepthNameOne;
    private float spacesScaleValue;
    private String[] spaceGroup;
    private double depthScaleRatio;
    private int halfToothWidth = 14, grooveY;
    private int[] combinationToothDistance_x;   //组合齿距
    private Map<String, Integer> depthPositionMapOne, depthPositionMapTwo;
    private int[] depthPositionOne, depthPositionTwo;  //保存齿的y轴的位子
    private ArrayList<String[]> toothCodeList;
    private boolean isShowArrows = true;  //默认为true
    private int patternLeftWidth, patternRightWidth;   // 左边，右边
    private float patternMiddleWidth;
    private int extraTopY;  // 额外top
    private int patternShoulderHeight;  //
    private int patternBodyMaxY;
    private double patternBodyHeight; //图案身体高度
    private final int A_SIDE_STRESS = 0, B_SIDE_STRESS = 1, A_B_SIDE_STRESS = 3;
    private String[] depthName;
    private int depthPositionStartYOne, depthPositionEndYOne, depthPositionStartYTwo, depthPositionEndYTwo;
    private int guide_y, nose_x;
    private int insideGrooveWidth;   //默认值是300
    private boolean isDraw = true;  // 是否绘制
    private int grooveTopLine_y, grooveBottomLine_y;
    private float[] grooveBottomInitLineGroup_y, grooveTopLineGroupTwo_y;
    private float[] grooveTopInitLineGroup_y;
    private float[] drawGrooveBottomLineGroup_y;   //绘制槽底部线组
    private int flag_maxToothDistanceGroup;  //最大齿距组
    private final int ONE_GROUP_TOOTH_DISTANCE_MAX = 1, TWO_GROUP_TOOTH_DISTANCE_MAX = 2;

    public MonorailInsideGrooveKey(Context context, KeyInfo ki) {
        this(context, null, ki);
    }

    public MonorailInsideGrooveKey(Context context, AttributeSet attrs, KeyInfo ki) {
        this(context, attrs, 0, ki);
    }

    public MonorailInsideGrooveKey(Context context, AttributeSet attrs, int defStyleAttr, KeyInfo ki) {
        super(context, attrs, defStyleAttr);
        this.ki = ki;
        this.initPaintAndPath();
        toothCodeList = new ArrayList<>();
        depthPositionMapOne = new HashMap<>();
        depthPositionMapTwo = new HashMap<>();
    }

    /**
     * 设置绘制的图案大小
     *
     * @param width
     * @param height
     */
    @Override
    public void setDrawPatternSize(int width, int height) {
        patternLeftWidth = (int) (width * 0.15f);
        patternMiddleWidth = (int) (width * 0.69f);
        patternRightWidth = (int) (width * 0.16f);
        patternShoulderHeight = (int) (height * 0.11f);
        extraTopY = patternShoulderHeight + 3;
        patternBodyHeight = (int) (height * 0.78);
        patternBodyMaxY = extraTopY + (int) patternBodyHeight;
        this.analysisSpace();
        this.analysisDepthAndDepthName();
        this.calculateSpecificInfo();
    }

    /**
     * 解析space
     */
    private void analysisSpace() {
        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaceOne;
        String[] spaceTwo;
        //转为int类型
        if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
            spaceOne = spaceGroup[0].split(",");
            toothDepthNameOne = new String[spaceOne.length];
            toothDistanceOne = new int[spaceOne.length];
            grooveBottomInitLineGroup_y = new float[spaceOne.length];
            grooveTopInitLineGroup_y = new float[spaceOne.length];
            for (int i = 0; i < toothDistanceOne.length; i++) {
                toothDistanceOne[i] = Integer.parseInt(spaceOne[i]);
            }
        } else if (ki.getSide() == A_B_SIDE_STRESS) {
            spaceOne = spaceGroup[0].split(",");
            spaceTwo = spaceGroup[1].split(",");
            toothDistanceOne = new int[spaceOne.length];
            toothDistanceTwo = new int[spaceTwo.length];
            //转为int
            for (int i = 0; i < spaceOne.length; i++) {
                toothDistanceOne[i] = Integer.parseInt(spaceOne[i]);
            }
            //转为int
            for (int i = 0; i < spaceTwo.length; i++) {
                toothDistanceTwo[i] = Integer.parseInt(spaceTwo[i]);
            }
            toothDepthNameOne = new String[spaceOne.length + spaceTwo.length];
            grooveTopInitLineGroup_y =new float[spaceOne.length + spaceTwo.length];
            grooveBottomInitLineGroup_y =new float[spaceOne.length + spaceTwo.length];
            drawGrooveBottomLineGroup_y=new float[spaceOne.length+spaceTwo.length];
        }
        this.calculateDrawToothDistance();
    }

    /**
     * 计算绘制的齿距
     */
    private void calculateDrawToothDistance() {
        int value;
        int maxLength;
        if (ki.getSide() == A_B_SIDE_STRESS ) {   // 等于AB面受力
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                if (toothDistanceOne[toothDistanceOne.length - 1] > toothDistanceTwo[toothDistanceTwo.length - 1]) {
                    flag_maxToothDistanceGroup = 1;
                    if (toothDistanceOne.length > 1) {
                        maxLength = toothDistanceOne[toothDistanceOne.length - 1] + (toothDistanceOne[toothDistanceOne.length - 1] - toothDistanceOne[toothDistanceOne.length - 2]);
                    } else {
                        maxLength = toothDistanceOne[toothDistanceOne.length - 1] + 300;
                    }
                } else {
                    flag_maxToothDistanceGroup = 2;
                    if (toothDistanceTwo.length > 1) {
                        maxLength = toothDistanceTwo[toothDistanceTwo.length - 1] + (toothDistanceTwo[toothDistanceTwo.length - 1] - toothDistanceTwo[toothDistanceTwo.length - 2]);
                    } else {
                        maxLength = toothDistanceTwo[toothDistanceTwo.length - 1] + 300;
                    }
                }
                spacesScaleValue = patternMiddleWidth / maxLength;  //得到比例值
                for (int i = 0; i < toothDistanceOne.length; i++) {
                    toothDistanceOne[i] = (int) (toothDistanceOne[i] * spacesScaleValue) + patternLeftWidth;
                }
                for (int i = 0; i < toothDistanceTwo.length; i++) {
                    toothDistanceTwo[i] = (int) (toothDistanceTwo[i] * spacesScaleValue) + patternLeftWidth;
                }
                combinationToothDistance_x = new int[toothDistanceOne.length + toothDistanceTwo.length];
                spacesScaleValue = patternMiddleWidth / maxLength;  //得到比例值

                    for (int i = 0; i < toothDistanceOne.length; i++) {
                        toothDistanceOne[i] = (int) (toothDistanceOne[i] * spacesScaleValue)+patternLeftWidth;
                    }
                    for (int i = 0; i < toothDistanceTwo.length; i++) {
                        toothDistanceTwo[i] = (int) (toothDistanceTwo[i] * spacesScaleValue)+patternLeftWidth;

                    }
                    int j=0;
                    int k=0;
                    if(flag_maxToothDistanceGroup==TWO_GROUP_TOOTH_DISTANCE_MAX){
                        for (int i = 0; i < combinationToothDistance_x.length; i++) {
                            if(i%2==0){
                                combinationToothDistance_x[i]=toothDistanceTwo[j];
                                j++;
                            }else {
                                combinationToothDistance_x[i]=toothDistanceOne[k];
                                k++;
                            }
                        }
                    }else if(flag_maxToothDistanceGroup==ONE_GROUP_TOOTH_DISTANCE_MAX){
                        for (int i = 0; i < combinationToothDistance_x.length; i++) {
                            if(i%2==0){
                                combinationToothDistance_x[i]=toothDistanceOne[j];
                                j++;
                            }else {
                                combinationToothDistance_x[i]=toothDistanceTwo[k];
                                k++;
                            }
                        }
                    }
            } else {  //前端
                if (toothDistanceOne[0] > toothDistanceTwo[0]) {
                    flag_maxToothDistanceGroup = 1;
                    if (toothDistanceOne.length > 1) {
                        maxLength = toothDistanceOne[0] + (toothDistanceOne[0] - toothDistanceOne[1]);
                    } else {
                        maxLength = toothDistanceOne[0] + 300;
                    }
                } else {
                    flag_maxToothDistanceGroup = 2;
                    if (toothDistanceTwo.length > 1) {
                        maxLength = toothDistanceTwo[0] + (toothDistanceTwo[0] - toothDistanceTwo[1]);
                    } else {
                        maxLength = toothDistanceTwo[0] + 300;
                    }
                }
                combinationToothDistance_x = new int[toothDistanceOne.length + toothDistanceTwo.length];
                spacesScaleValue = patternMiddleWidth / maxLength;  //得到比例值
                int sumX = patternLeftWidth + (int) patternMiddleWidth;
                if(flag_maxToothDistanceGroup==TWO_GROUP_TOOTH_DISTANCE_MAX){
                    for (int i = 0; i < toothDistanceOne.length; i++) {
                        toothDistanceOne[i] = sumX - (int) (toothDistanceOne[i] * spacesScaleValue);
                    }
                    for (int i = 0; i < toothDistanceTwo.length; i++) {
                        toothDistanceTwo[i] = sumX - (int) (toothDistanceTwo[i] * spacesScaleValue);
                    }
                    int j=0;
                    int k=0;
                    for (int i = 0; i < combinationToothDistance_x.length; i++) {
                               if(i%2==0){
                                   combinationToothDistance_x[i]=toothDistanceTwo[j];
                                   j++;
                               }else {
                                   combinationToothDistance_x[i]=toothDistanceOne[k];
                                   k++;
                               }
                    }
                }else {
                    for (int i = 0; i < toothDistanceOne.length; i++) {
                        toothDistanceOne[i] = sumX - (int) (toothDistanceOne[i] * spacesScaleValue);
                    }
                    for (int i = 0; i < toothDistanceTwo.length; i++) {
                        toothDistanceTwo[i] = sumX - (int) (toothDistanceTwo[i] * spacesScaleValue);
                    }
                    int j=0;
                    int k=0;
                    for (int i = 0; i < combinationToothDistance_x.length; i++) {
                        if(i%2==0){
                            combinationToothDistance_x[i]=toothDistanceOne[j];
                            j++;
                        }else {
                            combinationToothDistance_x[i]=toothDistanceTwo[k];
                            k++;
                        }
                    }
                }

            }
        } else if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {  //肩部
                if (toothDistanceOne.length < 2) {
                    value = 300;
                } else {
                    value = toothDistanceOne[toothDistanceOne.length - 1] - toothDistanceOne[toothDistanceOne.length - 2];
                }
                maxLength = toothDistanceOne[toothDistanceOne.length - 1] + value;
                spacesScaleValue = patternMiddleWidth / maxLength;
                for (int i = 0; i < toothDistanceOne.length; i++) {
                    toothDistanceOne[i] = (int) (toothDistanceOne[i] * spacesScaleValue) + patternLeftWidth;
                }
            } else {  //前端
                if (toothDistanceOne.length < 2) {
                    value = 300;
                } else {
                    value = toothDistanceOne[0] - toothDistanceOne[1];
                }
                maxLength = toothDistanceOne[0] + value;
                spacesScaleValue = patternMiddleWidth / maxLength;
                int sumX = patternLeftWidth + (int) patternMiddleWidth;
                for (int i = 0; i < toothDistanceOne.length; i++) {
                    toothDistanceOne[i] = sumX - (int) (toothDistanceOne[i] * spacesScaleValue);

                }
            }
        } else {

        }

    }

    /**
     * 解析深度和深度名
     */
    private void analysisDepthAndDepthName() {
        String[] depthGroup = ki.getDepth().split(";");
        String[] depthNameGroup = ki.getDepth_name().split(";");
        String[] depth = depthGroup[0].split(",");   // 解析深度和深度名
        depthName = depthNameGroup[0].split(",");  //  解析第一组的深度名
        //转为int类型
        if (ki.getSide() == A_B_SIDE_STRESS || ki.getSide() == 6) {
            depthPositionOne = new int[depth.length];
            depthPositionTwo = new int[depth.length];
            //转为int
            for (int i = 0; i < depthPositionOne.length; i++) {
                depthPositionOne[i] = Integer.parseInt(depth[i]);
                depthPositionTwo[i] = Integer.parseInt(depth[i]);
            }
        } else {
            depthPositionOne = new int[depth.length];
            for (int i = 0; i < depth.length; i++) {
                depthPositionOne[i] = Integer.parseInt(depth[i]);
            }
        }
        this.calculateDrawDepth();
    }

    /**
     * 计算绘制的深度
     */
    private void calculateDrawDepth() {
        depthScaleRatio = patternBodyHeight / ki.getWidth();  //得到比例值
        if (ki.getSide() == A_SIDE_STRESS) {
            for (int i = 0; i < depthPositionOne.length; i++) {
                depthPositionOne[i] = extraTopY + (int) (depthPositionOne[i] * depthScaleRatio);
                depthPositionMapOne.put(depthName[i], depthPositionOne[i]);
            }
            depthPositionStartYOne = depthPositionOne[0];
            depthPositionEndYOne = depthPositionOne[depthPositionOne.length - 1];
        } else if (ki.getSide() == B_SIDE_STRESS) {
            for (int i = 0; i < depthPositionOne.length; i++) {
                depthPositionOne[i] = patternBodyMaxY - (int) (depthPositionOne[i] * depthScaleRatio);
                depthPositionMapOne.put(depthName[i], depthPositionOne[i]);
            }
            depthPositionStartYOne = depthPositionOne[0];
            depthPositionEndYOne = depthPositionOne[depthPositionOne.length - 1];
        } else if (ki.getSide() == A_B_SIDE_STRESS) {
            for (int i = 0; i < depthPositionOne.length; i++) {
                depthPositionOne[i] = extraTopY + (int) (depthPositionOne[i] * depthScaleRatio);
                depthPositionMapOne.put(depthName[i], depthPositionOne[i]);

            }
            for (int i = 0; i < depthPositionTwo.length; i++) {
                depthPositionTwo[i] = patternBodyMaxY - (int) (depthPositionTwo[i] * depthScaleRatio);
                depthPositionMapTwo.put(depthName[i], depthPositionTwo[i]);
            }
            depthPositionStartYOne = depthPositionOne[0];
            depthPositionEndYOne = depthPositionOne[depthPositionOne.length - 1];
            depthPositionStartYTwo = depthPositionTwo[0];
            depthPositionEndYTwo = depthPositionTwo[depthPositionTwo.length - 1];

        }
    }

    /**
     * 计算特殊信息
     */
    private void calculateSpecificInfo() {
        guide_y = (int) (ki.getGuide() * depthScaleRatio);
        nose_x = (int) (ki.getNose() * spacesScaleValue);
        insideGrooveWidth = (int) (ki.getGroove() * depthScaleRatio);    //内槽宽度
        if (insideGrooveWidth == 0) {
            insideGrooveWidth = (int) (300 * depthScaleRatio);
        }
        if (ki.getSide() == A_SIDE_STRESS) {
            if(depthPositionOne[0]<depthPositionOne[depthPositionOne.length-1]){
                grooveTopLine_y=depthPositionOne[0]-(int) (patternBodyHeight * 0.06f);
            }else {
                grooveTopLine_y = depthPositionOne[0] + (int) (patternBodyHeight * 0.06f);
            }

        } else if (ki.getSide() == B_SIDE_STRESS) {
            grooveBottomLine_y = depthPositionOne[0] - (int) (patternBodyHeight * 0.06f);
        } else if (ki.getSide() == A_B_SIDE_STRESS) {
            if(flag_maxToothDistanceGroup==TWO_GROUP_TOOTH_DISTANCE_MAX){   //第二组齿距最长
                for (int i = grooveTopInitLineGroup_y.length-1; i >=0 ; i--) {
                    if(i%2!=0){
                        grooveTopInitLineGroup_y[i]=depthPositionOne[0]-(int) (patternBodyHeight * 0.06f);
                    }else {
                        grooveTopInitLineGroup_y[i]=depthPositionOne[depthPositionOne.length-1]+(int) (patternBodyHeight * 0.06f);
                    }
                }
                for (int i = 0; i < grooveBottomInitLineGroup_y.length ; i++) {
                    if(i%2!=0){
                        grooveBottomInitLineGroup_y[i]=depthPositionTwo[depthPositionTwo.length-1]-(int) (patternBodyHeight * 0.06f);
                    }else {
                        grooveBottomInitLineGroup_y[i]=depthPositionTwo[0]+(int) (patternBodyHeight * 0.06f);
                    }
                }
            }else if(flag_maxToothDistanceGroup==ONE_GROUP_TOOTH_DISTANCE_MAX){   //第一组齿距最长
                for (int i = grooveTopInitLineGroup_y.length-1; i >=0 ; i--) {
                    if(i%2!=0){
                        grooveTopInitLineGroup_y[i]=depthPositionOne[depthPositionOne.length-1]+(int) (patternBodyHeight * 0.06f);
                    }else {
                        grooveTopInitLineGroup_y[i]=depthPositionOne[0]-(int) (patternBodyHeight * 0.06f);
                    }
                }
                for (int i = 0; i < grooveBottomInitLineGroup_y.length ; i++) {
                    if(i%2!=0){
                        grooveBottomInitLineGroup_y[i]=depthPositionTwo[0]+(int) (patternBodyHeight * 0.06f);
                    }else {
                        grooveBottomInitLineGroup_y[i]=depthPositionTwo[depthPositionTwo.length-1]-(int) (patternBodyHeight * 0.06f);
                    }
                }
            }
        }
    }

    @Override
    public void setDrawDepthPatternAndToothCode(boolean isDraw) {
        this.isDraw = isDraw;
    }

    @Override
    public void setToothCode(String toothCode) {
        if (!TextUtils.isEmpty(toothCode)) {
            String[] allToothDepthName = toothCode.split(",");
            if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
                for (int i = 0; i < allToothDepthName.length; i++) {
                    toothDepthNameOne[i] = allToothDepthName[i];
                }
            } else if (ki.getSide() == A_B_SIDE_STRESS) {
                for (int i = 0; i < allToothDepthName.length; i++) {
                    toothDepthNameOne[i] = allToothDepthName[i];
                }
            }
        } else {
            this.setToothCodeDefault();
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
        if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
            toothCodeList.add(toothDepthNameOne);
            return toothCodeList;
        }else if(ki.getSide()==A_B_SIDE_STRESS){
            toothCodeList.add(toothDepthNameOne);
            return toothCodeList;
        }
        return null;
    }

    @Override
    public void setShowArrows(boolean isShowArrows) {
        this.isShowArrows = isShowArrows;
    }

    @Override
    public void setToothCodeDefault() {
        String toothCode = "";
        if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                toothDepthNameOne[i] = "X";
                toothCode += "X,";
            }
        } else if (ki.getSide() == A_B_SIDE_STRESS) {
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                toothDepthNameOne[i] = "X";
                toothCode += "X,";
            }
        }
        ki.setKeyToothCode(toothCode);
    }

    @Override
    public String getReadOrder(int locatingSlot, int detectionMode, int isRound) {
        String keyNorm = "!SB";   //bi
        String toothQuantity = "";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";  //齿号代码
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int cutDepth = 110;
        int groove = 320;
        if (ki.getCutDepth() != 0) {
            cutDepth = ki.getCutDepth();
        }
        if (ki.getGroove() != 0) {
            groove = ki.getGroove();
        }
        String[] spaceGroup = ki.getSpace().split(";");
        toothQuantity = spaceGroup[0].split(",").length + ","; //钥匙齿数(第一组)
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
        String[] depthNames = depthNameGroup[0].split(",");
        for (int i = 0; i < depthNames.length; i++) {//齿深名称(第一组)
            //获得齿的深度名
            toothDepthName += (depthNames[i] + ",");
        }
        lastToothOrExtraCut = (ki.getAlign() == 0 ? ki.getExtraCut() : ki.getLastBitting());//定位方式是0启用extra_cut参数否则启用last_bitting参数
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide() + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getGuide() + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码  其实都是零
                + ki.getNose() + ","  // 鼻部长度
                + groove + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;

    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm = "!SB";   //bi
        String toothQuantity = "";
        String toothPositionData = "";
        String toothWidth = "";
        int toothDepthQuantity = 0;
        String toothDepthData = "";
        String toothCode = "";
        String toothDepthName = "";
        int lastToothOrExtraCut = 0;
        int knifeType = 1;  //到类型
        int noseCut = 0;
        String DDepth = "";
        int groove = 320;
        if (ki.getGroove() != 0) {
            groove = ki.getGroove();
        }
        //鼻部参数不等于0  就为1 表示鼻部切割
        if (ki.getNose() != 0) {
            noseCut = 1;
        }
        if (speed == 1 || speed == 2) {
            DDepth = "0.75";
        } else if (speed == 3 || speed == 4) {
            DDepth = "1";
        }

        String[] spaceGroup = ki.getSpace().split(";");
        String[] spaces;
        toothQuantity = spaceGroup[0].split(",").length + ","; //钥匙齿数(第一组)
        if (ki.getSide() == 4) {//当参数为4的时候才会有下面参数
            toothQuantity = toothQuantity + spaceGroup[1].split(",").length + ",";
        }
        toothCode = ki.getKeyToothCode();
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
        String[] depthNames = depthNameGroup[0].split(",");
        for (int i = 0; i < depthNames.length; i++) {//齿深名称(第一组)
            //获得齿的深度名
            toothDepthName += (depthNames[i] + ",");
        }
        lastToothOrExtraCut = (ki.getAlign() == 0 ? ki.getExtraCut() : ki.getLastBitting());//定位方式是0启用extra_cut参数否则启用last_bitting参数
        String order = "!DR1;!BC;"
                + keyNorm     //钥匙规范
                + ki.getType() + "," //钥匙类型
                + ki.getAlign() + ","//钥匙定位方式
                + ki.getSide() + ","// 有效边
                + ki.getWidth() + ","// 钥匙片宽度
                + ki.getThick() + ","// 钥匙胚厚度
                + ki.getGuide() + ","       // 表示加不加切
                + ki.getLength() + ","//钥匙片长度
                + cutDepth + ","   //切割深度
                + toothQuantity   // 齿的数量
                + toothPositionData //齿位置数据
                + toothWidth   //齿顶宽数据
                + toothDepthQuantity + ","//齿深数量
                + toothDepthData      //齿深数据
                + locatingSlot + ","  //钥匙的定位位置
                + toothCode   //齿号代码
                + ki.getNose() + ","  // 鼻部长度
                + groove + ","  //槽宽
                + toothDepthName
                + lastToothOrExtraCut + ";"//最后齿或扩展切割
                + "!AC" + assistClamp + ";"  // 辅助夹具
                + "!ST" + knifeType + "," //刀类型
                + cutterDiameter + ";"    //刀的规格（刀的直径）
                + "!CK"
                + speed + "," //速度
                + DDepth + ","//D深度
                + ZDepth + ","  //Z深度
                + noseCut + ","  //鼻部切割
                + detectionMode + ";";  //切割钥匙的检测方式
        return order;
    }

    /**
     * 设置绘制的齿宽
     *
     * @param width
     */
    public void setDrawToothWidth(int width) {
        halfToothWidth = width / 2;
    }

    /**
     * 自定义齿形
     */
    public void customDrawSerrated() {
        int j = 0;
        if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                if (depthName.length >= 2) {
                    toothDepthNameOne[i] = depthName[j];
                    j++;
                    if (j == 2) {
                        j = 0;
                    }
                } else if (depthName.length == 1) {
                    toothDepthNameOne[i] = depthName[0];
                }
            }
        } else if (ki.getSide() == A_B_SIDE_STRESS) {
            for (int i = 0; i <toothDepthNameOne.length ; i++) {
                toothDepthNameOne[i]="X";
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    /**
     * 绘制钥匙图案
     *
     * @param canvas
     */
    private void drawKeyPattern(Canvas canvas) {
        int patternWidth = patternLeftWidth + (int) (patternMiddleWidth + patternRightWidth);
        int arc = (int) (patternShoulderHeight * 0.4f);
        // 肩部定位
        if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
            mPath.moveTo(0, 3);
            //弧度
            mPath.lineTo(patternLeftWidth - arc, 3);
            mPath.quadTo(patternLeftWidth, 3, patternLeftWidth, 3 + arc);
            mPath.lineTo(patternLeftWidth, extraTopY);
            mPath.lineTo(patternWidth - arc, extraTopY);
            mPath.quadTo(patternWidth, extraTopY, patternWidth, extraTopY + arc);
            mPath.lineTo(patternWidth, patternBodyMaxY - arc);
            mPath.quadTo(patternWidth, patternBodyMaxY, patternWidth - arc, patternBodyMaxY);
            mPath.lineTo(patternLeftWidth, patternBodyMaxY);
            mPath.lineTo(patternLeftWidth, (patternBodyMaxY + patternShoulderHeight) - arc);
            mPath.quadTo(patternLeftWidth, patternBodyMaxY + patternShoulderHeight, patternLeftWidth - arc, patternBodyMaxY + patternShoulderHeight);
            mPath.lineTo(0, patternBodyMaxY + patternShoulderHeight);
            canvas.drawPath(mPath, mBorderPaint);   //画边框
            canvas.drawPath(mPath, mKeyAppearanceColorPaint);
            mPath.reset();
        } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) { //前端定位
            mPath.moveTo(0, extraTopY);
            mPath.lineTo(patternWidth - arc, extraTopY);
            mPath.quadTo(patternWidth, extraTopY, patternWidth, extraTopY + arc);
            mPath.lineTo(patternWidth, patternBodyMaxY - arc);
            mPath.quadTo(patternWidth, patternBodyMaxY, patternWidth - arc, patternBodyMaxY);
            mPath.lineTo(0, patternBodyMaxY);
            canvas.drawPath(mPath, mBorderPaint);   //画边框
            canvas.drawPath(mPath, mKeyAppearanceColorPaint);
            mPath.reset();
        }
        if (ki.getSide() == A_SIDE_STRESS) {  //A面
            float semicircleControl_y = 0;  //半圆的控制点y坐标
            mPath.moveTo(patternWidth, extraTopY + arc);
            mPath.quadTo(patternWidth, extraTopY, patternWidth - arc, extraTopY);
            if (nose_x != 0) {
                mPath.lineTo((patternWidth - arc) - nose_x, extraTopY);
            }
            for (int i = toothDepthNameOne.length - 1; i >= 0; i--) {
                if (toothDepthNameOne[i].contains(".")) {
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    Integer depthValue = depthPositionMapOne.get(newStr[0]);
                    float decimals = Float.parseFloat("0." + newStr[1]);
                    float valueY = 0;
                    int distanceY;
                    if (depthValue == null) {
                        if (newStr[0].equals("X") || newStr.equals("0")) {
                            depthValue = depthPositionMapOne.get(depthName[0]);  //获得深度值
                            distanceY = grooveTopLine_y - depthValue;
                            valueY = distanceY * decimals;
                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, grooveTopLine_y - valueY);
                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, grooveTopLine_y - valueY);
                            grooveBottomInitLineGroup_y[i] = (grooveTopLine_y - valueY) + insideGrooveWidth;
                            if (i == 0) {
                                semicircleControl_y = (grooveTopLine_y - valueY) + (insideGrooveWidth / 2);
                            }
                        } else {
                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, grooveTopLine_y);
                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, grooveTopLine_y);
                            grooveBottomInitLineGroup_y[i] = grooveTopLine_y + insideGrooveWidth;
                            if (i == 0) {
                                semicircleControl_y = grooveTopLine_y + (insideGrooveWidth / 2);
                            }
                        }
                    } else {
                        if (depthValue != depthPositionOne[depthPositionOne.length - 1]) {
                            for (int k = 0; k < depthPositionOne.length; k++) {
                                if (depthValue == depthPositionOne[k]) {
                                    distanceY = depthValue - depthPositionOne[k + 1];
                                    valueY = distanceY * decimals;
                                    break;
                                }
                            }
                        }
                        grooveBottomInitLineGroup_y[i] = (depthValue - valueY) + insideGrooveWidth;
                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthValue - valueY);
                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthValue - valueY);
                        if (i == 0) {
                            semicircleControl_y = (depthValue - valueY) + (insideGrooveWidth / 2);
                        }
                    }
                } else {  // 没有小数
                    Integer depthValue = depthPositionMapOne.get(toothDepthNameOne[i]);
                    if (depthValue == null) {
                        depthValue = grooveTopLine_y;
                        grooveBottomInitLineGroup_y[i] = grooveTopLine_y + insideGrooveWidth;
                    } else {
                        grooveBottomInitLineGroup_y[i] = depthValue + insideGrooveWidth;
                    }
                    mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthValue);
                    mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthValue);
                    if (i == 0) {
                        semicircleControl_y = depthValue + (insideGrooveWidth / 2);
                    }
                }

            }
            //画圆弧
            mPath.quadTo((toothDistanceOne[0] - halfToothWidth) - (patternMiddleWidth * 0.1f), semicircleControl_y, toothDistanceOne[0] - halfToothWidth, grooveBottomInitLineGroup_y[0]);
            for (int i = 0; i < grooveBottomInitLineGroup_y.length; i++) {
                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, grooveBottomInitLineGroup_y[i]);
                mPath.lineTo(toothDistanceOne[i] + halfToothWidth, grooveBottomInitLineGroup_y[i]);
            }
            if (guide_y != 0) {
                mPath.lineTo(patternWidth, patternBodyMaxY - guide_y);
            } else {
                mPath.lineTo(patternWidth - arc, patternBodyMaxY);
                mPath.quadTo(patternWidth, patternBodyMaxY, patternWidth, patternBodyMaxY - arc);
            }
            canvas.drawPath(mPath, mBorderPaint);
            canvas.drawPath(mPath, mInsideGrooveColorPaint);
            mPath.reset();
        } else if (ki.getSide() == B_SIDE_STRESS) {  //B面
            float semicircleControl_y = 0;  //半圆的控制点y坐标
            //画内槽
            mPath.moveTo(patternWidth, patternBodyMaxY - arc);
            mPath.quadTo(patternWidth, patternBodyMaxY, patternWidth - arc, patternBodyMaxY);
            for (int i = toothDepthNameOne.length - 1; i >= 0; i--) {
                if (toothDepthNameOne[i].contains(".")) {
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    Integer depthValue = depthPositionMapOne.get(newStr[0]);
                    float decimals = Float.parseFloat("0." + newStr[1]);
                    float valueY = 0;
                    int distanceY;
                    if (depthValue == null) {
                        if (newStr[0].equals("X") || newStr.equals("0")) {
                            depthValue = depthPositionMapOne.get(depthName[0]);  //获得深度值
                            distanceY = depthValue - grooveBottomLine_y;
                            valueY = distanceY * decimals;
                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, grooveBottomLine_y + valueY);
                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, grooveBottomLine_y + valueY);
                            grooveTopInitLineGroup_y[i] = (grooveBottomLine_y + valueY) - insideGrooveWidth;
                            if (i == 0) {
                                semicircleControl_y = (grooveBottomLine_y + valueY) - (insideGrooveWidth / 2);
                            }
                        } else {
                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, grooveBottomLine_y);
                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, grooveBottomLine_y);
                            grooveTopInitLineGroup_y[i] = grooveBottomLine_y - insideGrooveWidth;
                            if (i == 0) {
                                semicircleControl_y = grooveTopLine_y + (insideGrooveWidth / 2);
                            }
                        }
                    } else {
                        if (depthValue != depthPositionOne[depthPositionOne.length - 1]) {
                            for (int k = 0; k < depthPositionOne.length; k++) {
                                if (depthValue == depthPositionOne[k]) {
                                    distanceY = depthPositionOne[k + 1] - depthValue;
                                    valueY = distanceY * decimals;
                                    break;
                                }
                            }
                        }
                        grooveTopInitLineGroup_y[i] = (depthValue + valueY) - insideGrooveWidth;
                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthValue + valueY);
                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthValue + valueY);
                        if (i == 0) {
                            semicircleControl_y = (depthValue + valueY) - (insideGrooveWidth / 2);
                        }
                    }
                } else {  // 没有小数
                    Integer depthValue = depthPositionMapOne.get(toothDepthNameOne[i]);
                    if (depthValue == null) {
                        depthValue = grooveBottomLine_y;
                        grooveTopInitLineGroup_y[i] = grooveBottomLine_y - insideGrooveWidth;
                    } else {
                        grooveTopInitLineGroup_y[i] = depthValue - insideGrooveWidth;
                    }
                    mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthValue);
                    mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthValue);
                    if (i == 0) {
                        semicircleControl_y = depthValue - (insideGrooveWidth / 2);
                    }
                }
            }
            //画圆弧
            mPath.quadTo((toothDistanceOne[0] - halfToothWidth) - (patternMiddleWidth * 0.1f), semicircleControl_y, toothDistanceOne[0] - halfToothWidth, grooveTopInitLineGroup_y[0]);
            //画上部线的点
            for (int i = 0; i < grooveTopInitLineGroup_y.length; i++) {
                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, grooveTopInitLineGroup_y[i]);
                mPath.lineTo(toothDistanceOne[i] + halfToothWidth, grooveTopInitLineGroup_y[i]);
            }
            if (guide_y != 0) {
                mPath.lineTo(patternWidth, extraTopY + guide_y);
            } else {
                mPath.lineTo(patternWidth - arc, extraTopY);
                mPath.quadTo(patternWidth, extraTopY, patternWidth, extraTopY + arc);
            }
            canvas.drawPath(mPath, mBorderPaint);
            canvas.drawPath(mPath, mInsideGrooveColorPaint);
            mPath.reset();
        } else if (ki.getSide() == A_B_SIDE_STRESS) {
            mPath.moveTo(patternWidth, extraTopY + arc);
            mPath.quadTo(patternWidth, extraTopY, patternWidth - arc, extraTopY);
            if (nose_x != 0) {
                mPath.lineTo((patternWidth - arc) - nose_x, extraTopY);
            }
            if (guide_y != 0) {
                mPath.lineTo((patternWidth - arc) - nose_x, extraTopY + guide_y);
            }
           float semicircleControl_y=0;
            // 第二组齿距最长
            if (flag_maxToothDistanceGroup == TWO_GROUP_TOOTH_DISTANCE_MAX) {
                for (int i = combinationToothDistance_x.length-1; i >=0; i--) {
                    if(i%2!=0){
                        if (toothDepthNameOne[i].contains(".")) {
                            String[] newStr = toothDepthNameOne[i].split("\\.");
                            Integer depthValue = depthPositionMapOne.get(newStr[0]);
                            float decimals = Float.parseFloat("0." + newStr[1]);
                            float valueY = 0;
                            float distanceY;
                            if (depthValue == null) {
                                if (newStr[0].equals("X") || newStr.equals("0")) {
                                    depthValue = depthPositionMapOne.get(depthName[0]);  //获得深度值
                                    distanceY = depthValue-grooveTopInitLineGroup_y[i];
                                    valueY = distanceY * decimals;
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, grooveTopInitLineGroup_y[i]+ valueY);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, grooveTopInitLineGroup_y[i] + valueY);
                                    drawGrooveBottomLineGroup_y[i] = (grooveTopInitLineGroup_y[i] + valueY) + insideGrooveWidth;
                                } else {
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    drawGrooveBottomLineGroup_y[i] = grooveTopInitLineGroup_y[i] + insideGrooveWidth;
                                }
                            } else {
                                if (depthValue != depthPositionOne[depthPositionOne.length - 1]) {
                                    for (int k = 0; k < depthPositionOne.length; k++) {
                                        if (depthValue == depthPositionOne[k]) {
                                            distanceY = depthPositionOne[k + 1]-depthValue;
                                            valueY = distanceY * decimals;
                                            break;
                                        }
                                    }
                                }
                                drawGrooveBottomLineGroup_y[i] =(depthValue+valueY)+insideGrooveWidth;
                                mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, depthValue+valueY);
                                mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, depthValue+valueY);
                            }
                        } else {  // 没有小数
                            Integer depthValue = depthPositionMapOne.get(toothDepthNameOne[i]);
                            if (depthValue == null) {
                                depthValue = (int) grooveTopInitLineGroup_y[i];
                                drawGrooveBottomLineGroup_y[i] = grooveTopInitLineGroup_y[i] + insideGrooveWidth;
                            } else {
                                drawGrooveBottomLineGroup_y[i] = depthValue + insideGrooveWidth;
                            }
                            mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, depthValue);
                            mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, depthValue);
                        }
                    }else {  //%2等于0的
                        if (toothDepthNameOne[i].contains(".")) {
                            String[] newStr = toothDepthNameOne[i].split("\\.");
                            Integer depthValue = depthPositionMapTwo.get(newStr[0]);
                            float decimals = Float.parseFloat("0." + newStr[1]);
                            float valueY = 0;
                            float distanceY;
                            if (depthValue == null) {
                                if (newStr[0].equals("X") || newStr.equals("0")) {
                                    depthValue = depthPositionMapTwo.get(depthName[0]);  //获得深度值
                                    distanceY = grooveBottomInitLineGroup_y[i]- depthValue;
                                    valueY = distanceY * decimals;
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, (grooveBottomInitLineGroup_y[i]- valueY)-insideGrooveWidth);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth,(grooveBottomInitLineGroup_y[i] - valueY)-insideGrooveWidth);
                                    drawGrooveBottomLineGroup_y[i] = grooveBottomInitLineGroup_y[i]- valueY;
                                    if(i==0){
                                        semicircleControl_y=(grooveBottomInitLineGroup_y[i]- valueY)-(insideGrooveWidth/2f);
                                    }
                                } else {
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    drawGrooveBottomLineGroup_y[i] = grooveTopInitLineGroup_y[i] + insideGrooveWidth;
                                    if(i==0){
                                        semicircleControl_y=grooveTopInitLineGroup_y[i]+(insideGrooveWidth/2f);
                                    }
                                }
                            } else {
                                if (depthValue != depthPositionTwo[depthPositionTwo.length - 1]) {
                                    for (int k = 0; k < depthPositionTwo.length; k++) {
                                        if (depthValue == depthPositionTwo[k]) {
                                            distanceY =depthValue- depthPositionTwo[k + 1];
                                            valueY = distanceY * decimals;
                                            break;
                                        }
                                    }
                                }
                                drawGrooveBottomLineGroup_y[i] = depthValue-valueY;
                                mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth,(depthValue-valueY)-insideGrooveWidth);
                                mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth,(depthValue-valueY)-insideGrooveWidth);
                                if(i==0){
                                    semicircleControl_y=(depthValue-valueY)-(insideGrooveWidth/2f);
                                }
                            }
                        } else {  // 没有小数
                            Integer depthValue = depthPositionMapTwo.get(toothDepthNameOne[i]);
                            if (depthValue == null) {
                                depthValue=(int)grooveTopInitLineGroup_y[i];
                                drawGrooveBottomLineGroup_y[i] = grooveTopInitLineGroup_y[i] + insideGrooveWidth;
                                if(i==0){
                                    semicircleControl_y=grooveTopInitLineGroup_y[i] + (insideGrooveWidth/2f);
                                }
                            } else {
                                drawGrooveBottomLineGroup_y[i] = depthValue;
                                if(i==0){
                                    semicircleControl_y=depthValue+(insideGrooveWidth/2);
                                }
                                depthValue=depthValue-insideGrooveWidth;
                            }
                            mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, depthValue);
                            mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, depthValue);
                        }
                    }
                }
            }else if(flag_maxToothDistanceGroup==ONE_GROUP_TOOTH_DISTANCE_MAX){   //第一组齿距最长
                for (int i = combinationToothDistance_x.length-1; i >=0; i--) {
                    if(i%2!=0){
                        if (toothDepthNameOne[i].contains(".")) {
                            String[] newStr = toothDepthNameOne[i].split("\\.");
                            Integer depthValue = depthPositionMapTwo.get(newStr[0]);
                            float decimals = Float.parseFloat("0." + newStr[1]);
                            float valueY = 0;
                            float distanceY;
                            if (depthValue == null) {
                                if (newStr[0].equals("X") || newStr.equals("0")) {
                                    depthValue = depthPositionMapTwo.get(depthName[0]);  //获得深度值
                                    distanceY = grooveBottomInitLineGroup_y[i]-depthValue;
                                    valueY = distanceY * decimals;
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, (grooveBottomInitLineGroup_y[i]-valueY)-insideGrooveWidth);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, (grooveBottomInitLineGroup_y[i]-valueY)-insideGrooveWidth);
                                    drawGrooveBottomLineGroup_y[i] = grooveBottomInitLineGroup_y[i]-valueY;
                                } else {
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    drawGrooveBottomLineGroup_y[i] = grooveBottomInitLineGroup_y[i];
                                }
                            } else {
                                if (depthValue != depthPositionTwo[depthPositionTwo.length - 1]) {
                                    for (int k = 0; k < depthPositionTwo.length; k++) {
                                        if (depthValue == depthPositionTwo[k]) {
                                            distanceY = depthValue-depthPositionTwo[k + 1];
                                            valueY = distanceY * decimals;
                                            break;
                                        }
                                    }
                                }
                                drawGrooveBottomLineGroup_y[i] =(depthValue-valueY);
                                mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, (depthValue-valueY)-insideGrooveWidth);
                                mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, (depthValue-valueY)-insideGrooveWidth);
                            }
                        } else {  // 没有小数
                            Integer depthValue = depthPositionMapTwo.get(toothDepthNameOne[i]);
                            if (depthValue == null) {
                                depthValue = (int)grooveTopInitLineGroup_y[i];
                                drawGrooveBottomLineGroup_y[i] = grooveBottomInitLineGroup_y[i];
                            } else {
                                drawGrooveBottomLineGroup_y[i] = depthValue;
                            }
                            mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, depthValue - insideGrooveWidth);
                            mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, depthValue-insideGrooveWidth);
                        }
                    }else {  //%2等于0的
                        if (toothDepthNameOne[i].contains(".")) {
                            String[] newStr = toothDepthNameOne[i].split("\\.");
                            Integer depthValue = depthPositionMapOne.get(newStr[0]);
                            float decimals = Float.parseFloat("0." + newStr[1]);
                            float valueY = 0;
                            float distanceY;
                            if (depthValue == null) {
                                if (newStr[0].equals("X") || newStr.equals("0")) {
                                    depthValue = depthPositionMapOne.get(depthName[0]);  //获得深度值
                                    distanceY = grooveTopInitLineGroup_y[i]- depthValue;
                                    valueY = distanceY * decimals;
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, grooveTopInitLineGroup_y[i]+ valueY);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth,grooveTopInitLineGroup_y[i] + valueY);
                                    drawGrooveBottomLineGroup_y[i] = (grooveTopInitLineGroup_y[i]+ valueY)+insideGrooveWidth;
                                    if(i==0){
                                        semicircleControl_y=(grooveTopInitLineGroup_y[i]+ valueY)+(insideGrooveWidth/2f);
                                    }
                                } else {
                                    mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, grooveTopInitLineGroup_y[i]);
                                    drawGrooveBottomLineGroup_y[i] = grooveTopInitLineGroup_y[i] + insideGrooveWidth;
                                    if(i==0){
                                        semicircleControl_y=grooveTopInitLineGroup_y[i]+(insideGrooveWidth/2f);
                                    }
                                }
                            } else {
                                if (depthValue != depthPositionOne[depthPositionOne.length - 1]) {
                                    for (int k = 0; k < depthPositionOne.length; k++) {
                                        if (depthValue == depthPositionOne[k]) {
                                            distanceY =depthPositionOne[k + 1]-depthValue;
                                            valueY = distanceY * decimals;
                                            break;
                                        }
                                    }
                                }
                                drawGrooveBottomLineGroup_y[i] = (depthValue+valueY)+insideGrooveWidth;
                                mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth,(depthValue+valueY));
                                mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth,(depthValue+valueY));
                                if(i==0){
                                    semicircleControl_y=(depthValue+valueY)+(insideGrooveWidth/2f);
                                }
                            }
                        } else {  // 没有小数
                            Integer depthValue = depthPositionMapOne.get(toothDepthNameOne[i]);
                            if (depthValue == null) {
                                depthValue=(int)grooveTopInitLineGroup_y[i];
                                drawGrooveBottomLineGroup_y[i] = grooveTopInitLineGroup_y[i] + insideGrooveWidth;
                                if(i==0){
                                    semicircleControl_y=grooveTopInitLineGroup_y[i] + (insideGrooveWidth/2f);
                                }
                            } else {
                                if(i==0){
                                    semicircleControl_y=depthValue+(insideGrooveWidth/2);
                                }
                                drawGrooveBottomLineGroup_y[i] = depthValue+insideGrooveWidth;
                            }
                            mPath.lineTo(combinationToothDistance_x[i] + halfToothWidth, depthValue);
                            mPath.lineTo(combinationToothDistance_x[i] - halfToothWidth, depthValue);
                        }
                    }
                }
            }
            //画半圆
            mPath.quadTo((combinationToothDistance_x[0] - halfToothWidth) - (patternMiddleWidth * 0.13f),semicircleControl_y,combinationToothDistance_x[0] - halfToothWidth,drawGrooveBottomLineGroup_y[0]);
            for (int i = 0; i < drawGrooveBottomLineGroup_y.length;i++) {
                       mPath.lineTo(combinationToothDistance_x[i]- halfToothWidth,drawGrooveBottomLineGroup_y[i]);
                      mPath.lineTo(combinationToothDistance_x[i]+ halfToothWidth,drawGrooveBottomLineGroup_y[i]);
            }
            if(nose_x!=0){
                mPath.lineTo((patternWidth-arc)-nose_x,patternBodyMaxY);
                mPath.lineTo(patternWidth-arc,patternBodyMaxY);
                mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth,patternBodyMaxY-arc);
            }else {
                mPath.lineTo(patternWidth-arc,patternBodyMaxY);
                mPath.quadTo(patternWidth,patternBodyMaxY,patternWidth,patternBodyMaxY-arc);
            }
            canvas.drawPath(mPath, mBorderPaint);
            canvas.drawPath(mPath, mInsideGrooveColorPaint);
            mPath.reset();

        }
        if (isDraw) {
            this.drawDepthPattern(canvas);
            this.drawToothCodePattern(canvas);
        }

    }

    /**
     * 绘制深度图案
     *
     * @param canvas
     */
    private void drawDepthPattern(Canvas canvas) {
        int endDepthPosition_x = (int) (patternLeftWidth + patternMiddleWidth);
        if (ki.getSide() == A_SIDE_STRESS || ki.getSide() == B_SIDE_STRESS) {
            for (int i = 0; i < depthPositionOne.length; i++) {
                mPath.moveTo(patternLeftWidth, depthPositionOne[i]);
                mPath.lineTo(endDepthPosition_x, depthPositionOne[i]);
            }
            canvas.drawPath(mPath, mDashedPaint);
            mPath.reset();
        } else  if(ki.getSide()==A_B_SIDE_STRESS){
            for (int i = 0; i <depthPositionOne.length ; i++) {
                mPath.moveTo(patternLeftWidth, depthPositionOne[i]);
                mPath.lineTo(endDepthPosition_x, depthPositionOne[i]);
            }
            for (int i = 0; i <depthPositionTwo.length ; i++) {
                mPath.moveTo(patternLeftWidth, depthPositionTwo[i]);
                mPath.lineTo(endDepthPosition_x, depthPositionTwo[i]);
            }
            canvas.drawPath(mPath, mDashedPaint);
            mPath.reset();
        }
    }

    /**
     * 绘制齿代码图案
     *
     * @param canvas
     */
    private void drawToothCodePattern(Canvas canvas) {
        int value_y = 0;
        if (ki.getSide() == A_SIDE_STRESS) {
            value_y = (int) (patternBodyHeight * 0.22f);
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                mPath.moveTo(toothDistanceOne[i], depthPositionStartYOne);
                mPath.lineTo(toothDistanceOne[i], depthPositionEndYOne);
                if (toothDepthNameOne[i].contains(".")) {
                    mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    int num = Integer.parseInt(newStr[1]);
                    if (depthPositionMapOne.get(newStr[0]) == null) {
                        if (newStr[0].equals("X") || newStr[0].equals("0")) {
                            if (num >= 5) {
                                canvas.drawText(depthName[0], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);//画红色的字
                            } else {
                                canvas.drawText(newStr[0], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);//画红色的字
                            }
                        } else {
                            canvas.drawText(newStr[0], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);
                        }
                    } else {
                        if (num >= 5) {
                            if (newStr[0].equals(depthName[depthName.length - 1])) {
                                canvas.drawText(newStr[0], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);//画红色的字
                            } else {
                                for (int j = 0; j < depthName.length; j++) {
                                    if (depthName[j].equals(newStr[0])) {
                                        canvas.drawText(depthName[j + 1], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);//画红色的字
                                        break; //跳出循环
                                    }
                                }
                            }
                        } else {
                            canvas.drawText(newStr[0], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);//画红色的字
                        }
                    }
                } else {//不包涵画蓝色的字
                    mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                    canvas.drawText(toothDepthNameOne[i], toothDistanceOne[i], grooveTopLine_y + value_y, mColorTextPaint);//画蓝色的字
                }
            }
        } else if (ki.getSide() == B_SIDE_STRESS) {
            value_y = (int) (patternBodyHeight * 0.15f);
            for (int i = 0; i < toothDepthNameOne.length; i++) {
                mPath.moveTo(toothDistanceOne[i], depthPositionStartYOne);
                mPath.lineTo(toothDistanceOne[i], depthPositionEndYOne);
                if (toothDepthNameOne[i].contains(".")) {
                    mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                    String[] newStr = toothDepthNameOne[i].split("\\.");
                    int num = Integer.parseInt(newStr[1]);
                    if (depthPositionMapOne.get(newStr[0]) == null) {
                        if (newStr[0].equals("X") || newStr[0].equals("0")) {
                            if (num >= 5) {
                                canvas.drawText(depthName[0], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);//画红色的字
                            } else {
                                canvas.drawText(newStr[0], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);//画红色的字
                            }
                        } else {
                            canvas.drawText(newStr[0], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);
                        }
                    } else {
                        if (num >= 5) {
                            if (newStr[0].equals(depthName[depthName.length - 1])) {
                                canvas.drawText(newStr[0], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);//画红色的字
                            } else {
                                for (int j = 0; j < depthName.length; j++) {
                                    if (depthName[j].equals(newStr[0])) {
                                        canvas.drawText(depthName[j + 1], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);//画红色的字
                                        break; //跳出循环
                                    }
                                }
                            }
                        } else {
                            canvas.drawText(newStr[0], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);//画红色的字
                        }
                    }
                } else {//不包涵画蓝色的字
                    mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                    canvas.drawText(toothDepthNameOne[i], toothDistanceOne[i], grooveBottomLine_y - value_y, mColorTextPaint);//画蓝色的字
                }
            }
        }else if(ki.getSide()==A_B_SIDE_STRESS){
            value_y = (int) (patternBodyHeight * 0.15f);
            if(flag_maxToothDistanceGroup==TWO_GROUP_TOOTH_DISTANCE_MAX){
                for (int i = 0; i <combinationToothDistance_x.length; i++) {
                    if(i%2==0){
                        mPath.moveTo(combinationToothDistance_x[i],depthPositionStartYTwo);
                        mPath.lineTo(combinationToothDistance_x[i],depthPositionEndYTwo);
                    }else {
                        mPath.moveTo(combinationToothDistance_x[i],depthPositionStartYOne);
                        mPath.lineTo(combinationToothDistance_x[i],depthPositionEndYOne);
                    }
                }
                canvas.drawPath(mPath,mDashedPaint);
                mPath.reset();
            }else if(flag_maxToothDistanceGroup==ONE_GROUP_TOOTH_DISTANCE_MAX){
                for (int i = 0; i <combinationToothDistance_x.length; i++) {
                    if(i%2==0){
                        mPath.moveTo(combinationToothDistance_x[i],depthPositionStartYOne);
                        mPath.lineTo(combinationToothDistance_x[i],depthPositionEndYOne);
                    }else {
                        mPath.moveTo(combinationToothDistance_x[i],depthPositionStartYTwo);
                        mPath.lineTo(combinationToothDistance_x[i],depthPositionEndYTwo);
                    }
                }
                canvas.drawPath(mPath,mDashedPaint);
                mPath.reset();
            }
                for (int i = 0; i <toothDepthNameOne.length ; i++) {
                    if(i%2==0){
                        if (toothDepthNameOne[i].contains(".")) {
                            mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                            String[] newStr = toothDepthNameOne[i].split("\\.");
                            int num = Integer.parseInt(newStr[1]);
                            if (depthPositionMapOne.get(newStr[0]) == null) {
                                if (newStr[0].equals("X") || newStr[0].equals("0")) {
                                    if (num >= 5) {
                                        canvas.drawText(depthName[0], combinationToothDistance_x[i], patternBodyMaxY + value_y, mColorTextPaint);//画红色的字
                                    } else {
                                        canvas.drawText(newStr[0], combinationToothDistance_x[i], patternBodyMaxY +value_y, mColorTextPaint);//画红色的字
                                    }
                                } else {
                                    canvas.drawText(newStr[0], combinationToothDistance_x[i], patternBodyMaxY + value_y, mColorTextPaint);
                                }
                            } else {
                                if (num >= 5) {
                                    if (newStr[0].equals(depthName[depthName.length - 1])) {
                                        canvas.drawText(newStr[0], combinationToothDistance_x[i], patternBodyMaxY + value_y, mColorTextPaint);//画红色的字
                                    } else {
                                        for (int j = 0; j < depthName.length; j++) {
                                            if (depthName[j].equals(newStr[0])) {
                                                canvas.drawText(depthName[j + 1], combinationToothDistance_x[i], patternBodyMaxY + value_y, mColorTextPaint);//画红色的字
                                                break; //跳出循环
                                            }
                                        }
                                    }
                                } else {
                                    canvas.drawText(newStr[0], combinationToothDistance_x[i], patternBodyMaxY + value_y, mColorTextPaint);//画红色的字
                                }
                            }
                        } else {//不包涵画蓝色的字
                            mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                            canvas.drawText(toothDepthNameOne[i], combinationToothDistance_x[i], patternBodyMaxY+ value_y, mColorTextPaint);//画蓝色的字
                        }
                    }else {
                        if (toothDepthNameOne[i].contains(".")) {
                            mColorTextPaint.setColor(Color.parseColor("#FF3030"));//红色
                            String[] newStr = toothDepthNameOne[i].split("\\.");
                            int num = Integer.parseInt(newStr[1]);
                            if (depthPositionMapOne.get(newStr[0]) == null) {
                                if (newStr[0].equals("X") || newStr[0].equals("0")) {
                                    if (num >= 5) {
                                        canvas.drawText(depthName[0], combinationToothDistance_x[i], extraTopY-5 , mColorTextPaint);//画红色的字
                                    } else {
                                        canvas.drawText(newStr[0], combinationToothDistance_x[i], extraTopY-5, mColorTextPaint);//画红色的字
                                    }
                                } else {
                                    canvas.drawText(newStr[0], combinationToothDistance_x[i], extraTopY-5 , mColorTextPaint);
                                }
                            } else {
                                if (num >= 5) {
                                    if (newStr[0].equals(depthName[depthName.length - 1])) {
                                        canvas.drawText(newStr[0], combinationToothDistance_x[i], extraTopY -5, mColorTextPaint);//画红色的字
                                    } else {
                                        for (int j = 0; j < depthName.length; j++) {
                                            if (depthName[j].equals(newStr[0])) {
                                                canvas.drawText(depthName[j + 1], combinationToothDistance_x[i], extraTopY -5, mColorTextPaint);//画红色的字
                                                break; //跳出循环
                                            }
                                        }
                                    }
                                } else {
                                    canvas.drawText(newStr[0], combinationToothDistance_x[i], extraTopY -5, mColorTextPaint);//画红色的字
                                }
                            }
                        } else {//不包涵画蓝色的字
                            mColorTextPaint.setColor(Color.parseColor("#FF000080"));//蓝色
                            canvas.drawText(toothDepthNameOne[i], combinationToothDistance_x[i], extraTopY-5, mColorTextPaint);//画蓝色的字
                        }
                    }
                }
        }
        canvas.drawPath(mPath, mDashedPaint);
        mPath.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawKeyPattern(canvas);
//        if (ki.getAlign() == 0) {
//            //画肩膀定位钥匙形状
//            mPath.moveTo(10, 20);
//            mPath.lineTo(120, 20);
//            mPath.lineTo(120, 50);
//            //分割线-------------------------------
//            mPath.lineTo(710 + 10, 50);
//            mPath.quadTo(722 + 10, 50, 720 + 10, 60);
//            mPath.lineTo(720 + 10, keyWidthY - 10);
//            mPath.quadTo(722 + 10, keyWidthY, 710 + 10, keyWidthY);
//            mPath.lineTo(120, keyWidthY);
//            mPath.lineTo(120, keyWidthY + 30);
//            mPath.lineTo(10, keyWidthY + 30);
//            canvas.drawPath(mPath, mBorderPaint);
//            canvas.drawPath(mPath, mKeyAppearanceColorPaint);
//            mPath.reset();
//            if (ki.getSide() == 6) {
//                //画第一条内槽样式
//                mPath.moveTo(730, 60);
//                mPath.quadTo(722 + 10, 50, 720, 50);
//                int lastToothPosition = toothDistanceOne[toothDistanceOne.length - 1] + halfToothWidth;
//                for (int i = toothDistanceOne.length - 1; i >= 0; i--) {
//                if (toothDepthNameOne[i].contains(".")) {   //有小数的
//                        //分割齿的深度名
//                        String[] str = toothDepthNameOne[i].split("\\.");
//                        if (depthPositionMapOne.get(str[0]) == null) {
//                            if (i == toothDistanceOne.length - 1) {
//                                mPath.lineTo(lastToothPosition, depthPositionArray1[0] - 6);
//                                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionArray1[0] - 6);
//                                depthPositionOne[i] = depthPositionArray1[0] - 6;
//                            } else {
//                                mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionArray1[0] - 6);
//                                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionArray1[0] - 6);
//                                depthPositionOne[i] = depthPositionArray1[0] - 6;
//                            }
//                        } else {
//                            //获得深度的位子  y轴方向
//                            int depthPosition = depthPositionMapOne.get(str[0]);
//                            float decimals = Float.parseFloat("0." + str[1]);
//                            int sumY = 0;
//                            if (depthPosition == depthPositionArray1[depthPositionArray1.length - 1]) {
//                                //等于最后一个深度位置不计算。
//                            } else {
//                                for (int j = 0; j < depthPositionArray1.length; j++) {
//                                    if (depthPosition == depthPositionArray1[j]) {
//                                        int distance = depthPositionArray1[j + 1] - depthPosition;
//                                        sumY = (int) (distance * decimals);
//                                        break;
//                                    }
//                                }
//                            }
//                            if (i == toothDistanceOne.length - 1) {
//                                mPath.lineTo(lastToothPosition, depthPosition + sumY);
//                                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPosition + sumY);
//                                depthPositionOne[i] = depthPosition + sumY;
//                            } else {
//                                mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPosition + sumY);
//                                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPosition + sumY);
//                                depthPositionOne[i] = depthPosition + sumY;
//                            }
//                        }
//
//                    } else {   //判断除去X的整数
//                        int toothDepth = depthPositionMapOne.get(toothDepthNameOne[i]) == null ? depthPositionArray1[0] - 6 : depthPositionMapOne.get(toothDepthNameOne[i]);
//                        if (i == toothDistanceOne.length - 1) {
//                            mPath.lineTo(lastToothPosition, toothDepth);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, toothDepth);
//                            depthPositionOne[i] = toothDepth;
//                        } else {
//                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, toothDepth);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, toothDepth);
//                            depthPositionOne[i] = toothDepth;
//                        }
//                    }
//                }
//                //画第一组圆弧
//                int grooveHalfHeight = grooveY / 2;
//                int firstToothPosition = toothDistanceOne[0] - halfToothWidth;
//                mPath.quadTo(firstToothPosition - 34, depthPositionOne[0] + grooveHalfHeight, firstToothPosition, depthPositionOne[0] + grooveY);
//                for (int i = 0; i < toothDistanceOne.length; i++) {
//                    if (i == 0) {
//                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionOne[i] + grooveY);
//                    } else {
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionOne[i] + grooveY);
//                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionOne[i] + grooveY);
//                    }
//                }
//                mPath.lineTo(730, 100);
//                canvas.drawPath(mPath, mBorderPaint);
//                canvas.drawPath(mPath, mInsideGrooveColorPaint);
//                mPath.reset();
//                //画第二条内槽样式
//                mPath.moveTo(730, keyWidthY - 10);
//                mPath.quadTo(722 + 10, keyWidthY, 710 + 10, keyWidthY);
//
//                lastToothPosition = toothDistanceTwo[toothDistanceTwo.length - 1] + halfToothWidth;
//                for (int i = toothDistanceTwo.length - 1; i >= 0; i--) {
//                    if (toothDepthNameTwo[i].equals("X")) {
//                        if (i == toothDistanceTwo.length - 1) {
//                            mPath.lineTo(lastToothPosition, depthPositionArray2[0] + 6);
//                            mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPositionArray2[0] + 6);
//                            depthPositionTwo[i] = depthPositionArray2[0] + 6;
//                        } else {
//                            mPath.lineTo(toothDistanceTwo[i] + halfToothWidth, depthPositionArray2[0] + 6);
//                            mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPositionArray2[0] + 6);
//                            depthPositionTwo[i] = depthPositionArray2[0] + 6;
//                        }
//                    } else if (toothDepthNameTwo[i].contains(".")) {   //有小数的
//                        //分割齿的深度名
//                        String[] str = toothDepthNameTwo[i].split("\\.");
//                        if (depthPositionMapTwo.get(str[0]) == null) {
//                            if (i == toothDistanceTwo.length - 1) {
//                                mPath.lineTo(lastToothPosition, depthPositionArray2[0] + 6);
//                                mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPositionArray2[0] + 6);
//                                depthPositionTwo[i] = depthPositionArray2[0] + 6;
//                            } else {
//                                mPath.lineTo(toothDistanceTwo[i] + halfToothWidth, depthPositionArray2[0] + 6);
//                                mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPositionArray2[0] + 6);
//                                depthPositionTwo[i] = depthPositionArray2[0] + 6;
//                            }
//                        } else {
//                            //获得深度的位子  y轴方向
//                            int depthPosition = depthPositionMapTwo.get(str[0]);
//                            float decimals = Float.parseFloat("0." + str[1]);
//                            int sumY = 0;
//                            if (depthPosition == depthPositionArray2[depthPositionArray2.length - 1]) {
//                                //等于最后一个深度位置不计算。
//                            } else {
//                                for (int j = 0; j < depthPositionArray2.length; j++) {
//                                    if (depthPosition == depthPositionArray2[j]) {
//                                        int distance = depthPositionArray2[j + 1] - depthPosition;
//                                        sumY = (int) (distance * decimals);
//                                        break;
//                                    }
//                                }
//                            }
//                            if (i == toothDistanceTwo.length - 1) {
//                                mPath.lineTo(lastToothPosition, depthPosition + sumY);
//                                mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPosition + sumY);
//                                depthPositionTwo[i] = depthPosition + sumY;
//                            } else {
//                                mPath.lineTo(toothDistanceTwo[i] + halfToothWidth, depthPosition + sumY);
//                                mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPosition + sumY);
//                                depthPositionTwo[i] = depthPosition + sumY;
//                            }
//                        }
//                    } else {   //判断除去X的整数
//                        int toothDepth = depthPositionMapTwo.get(toothDepthNameTwo[i]) == null ? depthPositionArray2[0] + 6 : depthPositionMapTwo.get(toothDepthNameTwo[i]);
//                        if (i == toothDistanceTwo.length - 1) {
//                            mPath.lineTo(lastToothPosition, toothDepth);
//                            mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, toothDepth);
//                            depthPositionTwo[i] = toothDepth;
//                        } else {
//                            mPath.lineTo(toothDistanceTwo[i] + halfToothWidth, toothDepth);
//                            mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, toothDepth);
//                            depthPositionTwo[i] = toothDepth;
//                        }
//                    }
//                }
//                //画第二组圆弧
//                grooveHalfHeight = grooveY / 2;
//                firstToothPosition = toothDistanceTwo[0] - halfToothWidth;
//                mPath.quadTo(firstToothPosition - 34, depthPositionTwo[0] - grooveHalfHeight, firstToothPosition, depthPositionTwo[0] - grooveY);
//                for (int i = 0; i < toothDistanceTwo.length; i++) {
//                    if (i == 0) {
//                        mPath.lineTo(toothDistanceTwo[i] + halfToothWidth, depthPositionTwo[i] - grooveY);
//                    } else {
//                        mPath.lineTo(toothDistanceTwo[i] - halfToothWidth, depthPositionTwo[i] - grooveY);
//                        mPath.lineTo(toothDistanceTwo[i] + halfToothWidth, depthPositionTwo[i] - grooveY);
//                    }
//                }
//                mPath.lineTo(730, keyWidthY - 52);
//
//                canvas.drawPath(mPath, mBorderPaint);
//                canvas.drawPath(mPath, mInsideGrooveColorPaint);
//                mPath.reset();
//                //画第一组齿的深度名
//                for (int i = 0; i < toothDistanceOne.length; i++) {
//                    if (toothDepthNameOne[i].equals("X")) {
//                        canvas.drawText("X", toothDistanceOne[i], 50 - 10, mColorTextPaint);  //画蓝色的字
//                    } else if (toothDepthNameOne[i].contains(".")) {
//                        if (toothDepthNameOne[i].contains("X")) {
//                            canvas.drawText("X", toothDistanceOne[i], 50 - 10, p6);//画红色的字
//                        } else {
//                            String[] s = toothDepthNameOne[i].split("\\.");
//                            canvas.drawText(s[0], toothDistanceOne[i], 50 - 10, p6);//画红色的字
//                        }
//                    } else {
//                        canvas.drawText(toothDepthNameOne[i], toothDistanceOne[i], 50 - 10, mColorTextPaint);//画蓝色的字
//                    }
//                }
//                //画第二组齿的深度名
//                for (int i = 0; i < toothDistanceTwo.length; i++) {
//                    if (toothDepthNameTwo[i].equals("X")) {
//                        canvas.drawText("X", toothDistanceTwo[i], depthPositionArray2[0] + 50, mColorTextPaint);  //画蓝色的字
//                    } else if (toothDepthNameTwo[i].contains(".")) {
//                        if (toothDepthNameTwo[i].contains("X")) {
//                            canvas.drawText("X", toothDistanceTwo[i], depthPositionArray2[0] + 50, p6);//画红色的字
//                        } else {
//                            String[] s = toothDepthNameTwo[i].split("\\.");
//                            Log.d("这是几", "bilateralKey: " + s[0]);
//                            canvas.drawText(s[0], toothDistanceTwo[i], depthPositionArray2[0] + 50, p6);//画红色的字
//                        }
//                    } else {
//                        canvas.drawText(toothDepthNameTwo[i], toothDistanceTwo[i], depthPositionArray2[0] + 50, mColorTextPaint);//画蓝色的字
//                    }
//                }
//                //画第一组深度
//                for (int i = 0; i < depthPositionArray1.length; i++) {
//                    mPath.moveTo(140, depthPositionArray1[i]);
//                    mPath.lineTo(644, depthPositionArray1[i]);
//                }
//                //画第二组深度
//                for (int i = 0; i < depthPositionArray2.length; i++) {
//                    mPath.moveTo(140, depthPositionArray2[i]);
//                    mPath.lineTo(644, depthPositionArray2[i]);
//                }
//                canvas.drawPath(mPath, mDashedPaint);
//                mPath.reset();
//                //画第一组齿位的虚线
//                for (int i = 0; i < toothDistanceOne.length; i++) {
//                    mPath.moveTo(toothDistanceOne[i], depthStartY1);
//                    mPath.lineTo(toothDistanceOne[i], depthEndY1);
//                }
//                //画第二组齿位的虚线
//                for (int i = 0; i < toothDistanceTwo.length; i++) {
//                    mPath.moveTo(toothDistanceTwo[i], depthStartY2);
//                    mPath.lineTo(toothDistanceTwo[i], depthEndY2);
//                }
//                canvas.drawPath(mPath, mDashedPaint);
//                mPath.reset();
//            }
//        } else if (ki.getAlign() == 1) {
//            //画尖端定位钥匙形状
//            mPath.moveTo(10, 50);
//            mPath.lineTo(720, 50);
//            mPath.quadTo(732, 50, 730, 60);
//            mPath.lineTo(730, keyWidthY - 10);
//            mPath.quadTo(732, keyWidthY, 720, keyWidthY);
//            mPath.lineTo(10, keyWidthY);
//            canvas.drawPath(mPath, mBorderPaint);
//            canvas.drawPath(mPath, mKeyAppearanceColorPaint);
//            mPath.reset();
//
//        }
//        //等于0 就是A侧面
//        if (ki.getSide() == 0) {
//            int lastToothPosition = toothDistanceOne[toothDistanceOne.length - 1] + halfToothWidth;
//            //画内槽样式
//            mPath.moveTo(730, 60);
//            mPath.quadTo(722 + 10, 50, 720, 50);
//            for (int i = toothDistanceOne.length - 1; i >= 0; i--) {
//                if (toothDepthNameOne[i].equals("X")) {
//                    if (i == toothDistanceOne.length - 1) {
//                        mPath.lineTo(lastToothPosition, depthPositionArray1[0] + 10);
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionArray1[0] + 10);
//                        depthPositionOne[i] = depthPositionArray1[0] + 10;
//                    } else {
//                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionArray1[0] + 10);
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionArray1[0] + 10);
//                        depthPositionOne[i] = depthPositionArray1[0] + 10;
//                    }
//                } else if (toothDepthNameOne[i].contains(".")) {   //有小数的
//                    //分割齿的深度名
//                    String[] str = toothDepthNameOne[i].split("\\.");
//                    if (depthPositionMapOne.get(str[0]) == null) {
//                        if (i == toothDistanceOne.length - 1) {
//                            mPath.lineTo(lastToothPosition, depthPositionArray1[0] + 10);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionArray1[0] + 10);
//                            depthPositionOne[i] = depthPositionArray1[0] + 10;
//                        } else {
//                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionArray1[0] + 10);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionArray1[0] + 10);
//                            depthPositionOne[i] = depthPositionArray1[0] + 10;
//                        }
//                    } else {
//                        //获得深度的位子  y轴方向
//                        int depthPosition = depthPositionMapOne.get(str[0]);
//                        float decimals = Float.parseFloat("0." + str[1]);
//                        int sumY = 0;
//                        if (depthPosition == depthPositionArray1[depthPositionArray1.length - 1]) {
//                            //等于最后一个深度位置不计算。
//                        } else {
//                            for (int j = 0; j < depthPositionArray1.length; j++) {
//                                if (depthPosition == depthPositionArray1[j]) {
//                                    int distance = depthPositionArray1[j + 1] - depthPosition;
//                                    sumY = (int) (distance * decimals);
//                                    break;
//                                }
//                            }
//                        }
//                        if (i == toothDistanceOne.length - 1) {
//                            mPath.lineTo(lastToothPosition, depthPosition + sumY);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPosition + sumY);
//                            depthPositionOne[i] = depthPosition + sumY;
//                        } else {
//                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPosition + sumY);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPosition + sumY);
//                            depthPositionOne[i] = depthPosition + sumY;
//                        }
//                    }
//                } else {   //判断除去X的整数
//                    int toothDepth = depthPositionMapOne.get(toothDepthNameOne[i]) == null ? depthPositionArray1[0] + 10 : depthPositionMapOne.get(toothDepthNameOne[i]);
//                    if (i == toothDistanceOne.length - 1) {
//                        mPath.lineTo(lastToothPosition, toothDepth);
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, toothDepth);
//                        depthPositionOne[i] = toothDepth;
//                    } else {
//                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, toothDepth);
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, toothDepth);
//                        depthPositionOne[i] = toothDepth;
//                    }
//                }
//            }
//            int firstToothPosition = toothDistanceOne[0] - halfToothWidth;
//            int grooveHalfHeight = grooveY / 2;
//            //画圆弧
//            if (ki.getExtraCut() == 1) {  //  钥匙柄切割一刀
//                mPath.lineTo(140, depthPositionOne[0]);
//                mPath.lineTo(90, 64);
//                mPath.quadTo(32, 30, 30, 100);
//                mPath.lineTo(120, depthPositionOne[0] + grooveY);
//            } else {
//                mPath.quadTo(firstToothPosition - 40, depthPositionOne[0] + grooveHalfHeight, firstToothPosition, depthPositionOne[0] + grooveY);
//            }
//
//            for (int i = 0; i < toothDistanceOne.length; i++) {
//                if (i == 0) {
//                    mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionOne[i] + grooveY);
//                } else {
//                    mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionOne[i] + grooveY);
//                    mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionOne[i] + grooveY);
//                }
//            }
//            if (guide_y == 0) {
//                mPath.lineTo(720, keyWidthY);
//                mPath.quadTo(722 + 10, keyWidthY, 730, keyWidthY - 10);
//            } else if (guide_y != 0) {
//                mPath.lineTo(730, keyWidthY - guide_y);
//            }
//            canvas.drawPath(mPath, mBorderPaint);
//            canvas.drawPath(mPath, mInsideGrooveColorPaint);
//            mPath.reset();
//            //画每个齿的深度名
//            for (int i = 0; i < toothDistanceOne.length; i++) {
//                if (toothDepthNameOne[i].equals("X")) {
//                    canvas.drawText("X", toothDistanceOne[i], depthPositionArray1[0] + 50, mColorTextPaint);  //画蓝色的字
//                } else if (toothDepthNameOne[i].contains(".")) {
//                    if (toothDepthNameOne[i].contains("X")) {
//                        canvas.drawText("X", toothDistanceOne[i], depthPositionArray1[0] + 50, p6);//画红色的字
//                    } else {
//                        String[] s = toothDepthNameOne[i].split("\\.");
//                        canvas.drawText(s[0], toothDistanceOne[i], depthPositionArray1[0] + 50, p6);//画红色的字
//                    }
//                } else {
//                    canvas.drawText(toothDepthNameOne[i], toothDistanceOne[i], depthPositionArray1[0] + 50, mColorTextPaint);//画蓝色的字
//                }
//            }
//            //画深度
//            for (int i = 0; i < depthPositionArray1.length; i++) {
//                mPath.moveTo(140, depthPositionArray1[i]);
//                mPath.lineTo(644, depthPositionArray1[i]);
//            }
//            canvas.drawPath(mPath, mDashedPaint);
//            mPath.reset();
//            //画齿位的虚线
//            for (int i = 0; i < toothDistanceOne.length; i++) {
//                mPath.moveTo(toothDistanceOne[i], depthStartY1);
//                mPath.lineTo(toothDistanceOne[i], depthEndY1);
//            }
//            canvas.drawPath(mPath, mDashedPaint);
//            mPath.reset();
//            //等于1 就代表B面
//        } else if (ki.getSide() == 1) {
//            //画钥匙的内槽
//            mPath.moveTo(730, keyWidthY - 10);
//            mPath.quadTo(722 + 10, keyWidthY, 710 + 10, keyWidthY);
//            int lastToothPosition = toothDistanceOne[toothDistanceOne.length - 1] + halfToothWidth;
//            for (int i = toothDistanceOne.length - 1; i >= 0; i--) {
//            if (toothDepthNameOne[i].contains(".")) {   //有小数的
//                    //分割齿的深度名
//                    String[] str = toothDepthNameOne[i].split("\\.");
//                  Integer depthValue=depthPositionMapOne.get(str[0]);
//                  float   decimals=Float.parseFloat("0."+ str[1]);
//                int distanceY;
//              int valueY;
//                    if (depthValue == null) {
//                        if(str[0].equals("X")||str[0].equals("0")){
//                            depthValue=depthPositionMapOne.get(depthName[0]);
//                            distanceY=depthValue-(depthPositionArray1[0] - 10);
//                            valueY=(int)(distanceY*decimals);
//                            mPath.lineTo(lastToothPosition, depthPositionArray1[0] - 10);
//                            if (i == toothDistanceOne.length - 1) {
//                                mPath.lineTo(lastToothPosition, (depthPositionArray1[0] - 10)+valueY);
//                                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, (depthPositionArray1[0] - 10)+valueY);
//                                depthPositionOne[i] = (depthPositionArray1[0] - 10)+valueY;
//                            } else {
//                                mPath.lineTo(toothDistanceOne[i] + halfToothWidth, (depthPositionArray1[0] - 10)+valueY);
//                                mPath.lineTo(toothDistanceOne[i] - halfToothWidth, (depthPositionArray1[0] - 10)+valueY);
//                                depthPositionOne[i] =(depthPositionArray1[0] - 10)+valueY;
//                            }
//                        }
//
//                    } else {
//                        int sumY = 0;
//                        if (depthValue == depthPositionArray1[depthPositionArray1.length - 1]) {
//                            //等于最后一个深度位置不计算。
//                        } else {
//                            for (int j = 0; j < depthPositionArray1.length; j++) {
//                                if (depthValue == depthPositionArray1[j]) {
//                                    int distance = depthPositionArray1[j + 1] - depthValue;
//                                    sumY = (int) (distance * decimals);
//                                    break;
//                                }
//                            }
//                        }
//                        if (i == toothDistanceOne.length - 1) {
//                            mPath.lineTo(lastToothPosition, depthValue + sumY);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthValue + sumY);
//                            depthPositionOne[i] = depthValue + sumY;
//                        } else {
//                            mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthValue + sumY);
//                            mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthValue + sumY);
//                            depthPositionOne[i] = depthValue + sumY;
//                        }
//                    }
//                } else {   //判断除去X的整数
//                    int toothDepth = depthPositionMapOne.get(toothDepthNameOne[i]) == null ? depthPositionArray1[0] - 10 : depthPositionMapOne.get(toothDepthNameOne[i]);
//                    if (i == toothDistanceOne.length - 1) {
//                        mPath.lineTo(lastToothPosition, toothDepth);
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, toothDepth);
//                        depthPositionOne[i] = toothDepth;
//                    } else {
//                        mPath.lineTo(toothDistanceOne[i] + halfToothWidth, toothDepth);
//                        mPath.lineTo(toothDistanceOne[i] - halfToothWidth, toothDepth);
//                        depthPositionOne[i] = toothDepth;
//                    }
//                }
//            }
//            //画圆弧
//            int grooveHalfHeight = grooveY / 2;
//            int firstToothPosition = toothDistanceOne[0] - halfToothWidth;
//            //画圆弧
//            if (ki.getExtraCut() == 1) {  //  钥匙柄切割一刀
//                mPath.lineTo(140, depthPositionOne[0]);
//                mPath.lineTo(90, 64);
//                mPath.quadTo(32, 30, 30, 100);
//                mPath.lineTo(120, depthPositionOne[0] + grooveY);
//            } else {
//                mPath.quadTo(firstToothPosition - 40, depthPositionOne[0] - grooveHalfHeight, firstToothPosition, depthPositionOne[0] - grooveY);
//            }
//            for (int i = 0; i < toothDistanceOne.length; i++) {
//                if (i == 0) {
//                    mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionOne[i] - grooveY);
//                } else {
//                    mPath.lineTo(toothDistanceOne[i] - halfToothWidth, depthPositionOne[i] - grooveY);
//                    mPath.lineTo(toothDistanceOne[i] + halfToothWidth, depthPositionOne[i] - grooveY);
//                }
//            }
//            if (guide_y != 0) {
//                mPath.lineTo(730, guide_y + excessY);
//            } else {
//                mPath.lineTo(720, excessY);
//                mPath.quadTo(732, 50, 730, 60);
//            }
//            canvas.drawPath(mPath, mBorderPaint);
//            //画填充内槽的颜色
//            canvas.drawPath(mPath, mInsideGrooveColorPaint);
//            //重置
//            mPath.reset();
//            //画深度虚线
//            for (int i = 0; i < depthPositionArray1.length; i++) {
//                mPath.moveTo(140, depthPositionArray1[i]);
//                mPath.lineTo(644, depthPositionArray1[i]);
//            }
//            canvas.drawPath(mPath, mDashedPaint);
//            mPath.reset();
//            //画齿位虚线
//            for (int i = 0; i < toothDistanceOne.length; i++) {
//                mPath.moveTo(toothDistanceOne[i], depthStartY1);
//                mPath.lineTo(toothDistanceOne[i], depthEndY1);
//            }
//            canvas.drawPath(mPath, mDashedPaint);
//            mPath.reset();
//            //画每个齿的深度名
//            for (int i = 0; i < toothDepthNameOne.length; i++) {
//              if (toothDepthNameOne[i].contains(".")) {
//                  String[] newStr=toothDepthNameOne[i].split("\\.");
//                  int num=Integer.parseInt(newStr[1]);
//                    if (depthPositionMapOne.get(newStr[0])==null){
//                        if(num>=5){
//                            if(newStr[0].equals("X")||newStr[1].equals("0")){
//                                canvas.drawText(depthName[0], toothDistanceOne[i], depthPositionArray1[0] - 30, p6);//画红色的字
//                            }
//                        }else {
//                            canvas.drawText(newStr[0] ,toothDistanceOne[i], depthPositionArray1[0] - 30, p6);//画红色的字
//                        }
//                    } else {
//                        canvas.drawText(newStr[0], toothDistanceOne[i], depthPositionArray1[0] - 30, p6);//画红色的字
//                    }
//                } else {
//                    canvas.drawText(toothDepthNameOne[i], toothDistanceOne[i], depthPositionArray1[0] - 30, mColorTextPaint);//画蓝色的字
//                }
//            }
//        }
        if (isShowArrows) {
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {   //肩膀
                //画红色箭头
                mPath.reset();
                mPath.moveTo(90, 20);  //100
                mPath.lineTo(120, 20);//第一条  100
                mPath.lineTo(120, 12);
                mPath.lineTo(130, 24);//中间点  104
                mPath.lineTo(120, 36);
                mPath.lineTo(120, 28);
                mPath.lineTo(90, 28);
                mPath.close();
                canvas.drawPath(mPath, mArrowsPaint);
                mPath.reset();
            } else {
                mPath.reset();
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

        mInsideGrooveColorPaint = new Paint();
        //画钥匙形状属性
        mBorderPaint.setAntiAlias(true);//去掉抗锯齿
        mBorderPaint.setColor(Color.parseColor("#000000"));  //画笔的颜色  为黑色
        mBorderPaint.setStyle(Paint.Style.STROKE);//设置画笔风格为描边
        mBorderPaint.setStrokeWidth(2);
        //填充钥匙外貌颜色的笔
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
        mColorTextPaint.setColor(Color.parseColor("#FF000080"));//设置字体颜色  蓝色
        mColorTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        mColorTextPaint.setTextSize(40);
        //画红色提示箭头的属性
        mArrowsPaint.setColor(Color.RED);
        mArrowsPaint.setAntiAlias(true);//去掉锯齿
        mArrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        mArrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //填充钥匙内槽颜色的笔
        mInsideGrooveColorPaint.setAntiAlias(true);//去掉锯齿
        mInsideGrooveColorPaint.setColor(Color.parseColor("#cc9900")); //黄色
        mInsideGrooveColorPaint.setStyle(Paint.Style.FILL);
        mInsideGrooveColorPaint.setStrokeWidth(1);
    }
}
