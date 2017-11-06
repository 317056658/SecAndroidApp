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
 * 凹点钥匙
 * Created by Administrator on 2017/7/19.
 */

public class ConcaveDotKey extends Key {
    private Path path;
    private Paint blackPaint;
    private Paint grayPaint;
    private Paint dashedPaint;
    private Paint bluePaint;
    private Paint arrowsPaint;
    private Paint redPaint;
    private Paint largeCirclePaint;
    private Paint smallCirclePaint;
    private KeyInfo p;
    private double keyBodyHeight = 240, keyBodyWidth = 540;
    private int[] toothDistance1, toothDistance2;
    private String[] toothDepthNameGroup1, toothDepthNameGroup2;
    private int maxToothDistance, lastToothDistance;
    private int surplusX;
    private int keyWidthY, excessY;
    //定义2个hasaMap集合  保存每组齿的深度位子
    private Map<String, Integer> largeCircleRadiusMap1, smallCircleRadiusMap1;
    private Map<String, Integer> largeCircleRadiusMap2, smallCircleRadiusMap2;
    private List<String[]>  depthNameList;
    private  ArrayList<String[]> toothDepthNameList;
    private double spacesScaleValue, depthScaleRatio;
    private String[] allToothDepthName;
    private int[] track;
    private String[] depthName1, depthName2;
    private int number, keySumX;
    private String[] rowPosGroup;
    private List<int[]> toothDistanceXList;
    private List<Map<String, Integer>> largeCircleList;
    private List<Map<String, Integer>> smallCircleList;
    private boolean isSideGroove, isRowEquality;
   private String[] toothDepthName;
    int[] toothDistanceX;
    String[] depthNameGroup;
    String[] depthName;
    private boolean isShowArrows=true;  //默认为true


    private Map<String, Integer> largeCircleRadiusMap, smallCircleRadiusMap;

    public ConcaveDotKey(Context context) {
        super(context);
        toothDepthNameList = new ArrayList<>();
        toothDistanceXList = new ArrayList<>();

    }


    public ConcaveDotKey(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConcaveDotKey(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setDrawPatternSize(int width, int height) {

    }

    @Override
    public void setShowDrawDepthAndDepthName(boolean isDraw) {

    }


    /**
     *  设置钥匙齿的深度名
     * @param depthName
     */

    @Override
    public void setToothDepthName(String depthName) {
        if(!TextUtils.isEmpty(depthName)) {
            allToothDepthName = depthName.split(",");
            if (p.getRowCount() == 1) {
                for (int i = 0; i < allToothDepthName.length; i++) {
                    toothDepthNameGroup1[i] = allToothDepthName[i];
                }
            } else if (p.getRowCount() == 2) {
                int j = -1;
                for (int i = 0; i < allToothDepthName.length; i++) {
                    if (i < toothDepthNameGroup1.length) {
                        toothDepthNameGroup1[i] = allToothDepthName[i];
                    } else {
                        j++;
                        toothDepthNameGroup2[j] = allToothDepthName[i];
                    }
                }
            } else if (p.getRowCount() == 3 || p.getRowCount() == 4 || p.getRowCount() == 5) {
                String[] toothDepthName;
                int k = -1;
                for (int i = 0; i < toothDepthNameList.size(); i++) {
                    toothDepthName = toothDepthNameList.get(i);
                    for (int j = 0; j < toothDepthName.length; j++) {
                        k++;
                        toothDepthNameList.get(i)[j] = allToothDepthName[k];
                    }
                }
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
        if (p.getRowCount() == 1) {
            toothDepthNameList.clear();
            toothDepthNameList.add(toothDepthNameGroup1);
        } else if (p.getRowCount() == 2) {
            toothDepthNameList.clear();
            toothDepthNameList.add(toothDepthNameGroup1);
            toothDepthNameList.add(toothDepthNameGroup2);
        } else if (p.getRowCount() == 3 || p.getRowCount() == 4 || p.getRowCount() == 5) {
            return toothDepthNameList;
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
        String toothCode = "";
        String[]  rowPosition = p.getRow_pos().split(";");
        int rowCount=rowPosition.length;
        String[] spaceGroup = p.getSpace().split(";");
        for (int i = 0; i < spaceGroup.length; i++) {   //齿号代码
               String[] spaces=spaceGroup[i].split(",");
            for (int j = 0; j < spaces.length; j++) {
                toothCode+="0,";
            }
        }
        String[] spaceWidthGroup = p.getSpace_width().split(";");
        String[] depthGroup = p.getDepth().split(";");
        String[] depthNameGroup = p.getDepth_name().split(";");


        String order = "!DR1;!BC;" + keyNorm; //钥匙规范

                order=order + p.getType() + "," ;//钥匙类型
                order=order + 10+",";//默认值
                order=order + p.getAlign() + ","; //钥匙定位方式
                order=order + rowCount + ",";    //轴的的数量
                order=order + p.getWidth() + ",";//钥匙宽度
                order=order + p.getThick() + ",";//钥匙厚度
                order=order + 0+ ",";  //表示加不加切
                order=order + p.getLength()  + ",";
                order=order + p.getCutDepth() + ",";
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
                        order=order + p.getExtraCut() + ",";
                    }
                    String[]  depths=depthGroup[i].split(",");
                      order=order + depths.length + ",";     //齿深数量
                    for (int z = 0; z <depths.length ; z++) {
                        order=order + depths[z] + ",";   //齿深数据
                    }
                    String[]  depthNames=depthNameGroup[i].split(",");
                    for (int n = 0; n < depths.length; n++) {  //齿深名称
                        order=order + (depthNames[n].charAt(0)+",");
                    }
                 }
                order=order + locatingSlot+ ",";
                order=order + toothCode;
                order=order + "!AC;!DE" + detectionMode + "," + isRound + ";";
        return order;
    }

    @Override
    public String getCutOrder(int cutDepth, int locatingSlot, String assistClamp, String cutterDiameter, int speed, int ZDepth, int detectionMode) {
        String keyNorm = "!SB";
        String toothCode;
        int knifeType=1;  //到类型
        int noseCut=0;
        String DDepth="";
        if(speed==1||speed==2){
            DDepth ="0.25";
        }else if(speed==3||speed==4){
            DDepth="0.4";
        }
        String[]  rowPosition = p.getRow_pos().split(";");
        int rowCount=rowPosition.length;
        String[] spaceGroup = p.getSpace().split(";");
        toothCode=p.getKeyToothCode();  //获得实际齿号
        String[] spaceWidthGroup = p.getSpace_width().split(";");
        String[] depthGroup = p.getDepth().split(";");
        String[] depthNameGroup = p.getDepth_name().split(";");


        String order = "!DR1;!BC;" + keyNorm; //钥匙规范

        order=order + p.getType() + "," ;//钥匙类型
        order=order + 10+",";//默认值
        order=order + p.getAlign() + ","; //钥匙定位方式
        order=order + rowCount + ",";    //轴的的数量
        order=order + p.getWidth() + ",";//钥匙宽度
        order=order + p.getThick() + ",";//钥匙厚度
        order=order + 0+ ",";  //表示加不加切
        order=order + p.getLength()  + ",";
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
                order=order + p.getExtraCut() + ",";
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
        order=order + toothCode;  //中间最后一段  第20段
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
        Log.d("View的宽度", "onMeasure: " + MeasureSpec.getSize(widthMeasureSpec));
        Log.d("View的高度", "onMeasure: " + MeasureSpec.getSize(heightMeasureSpec));
        if (p.getRowCount()==2||p.getRowCount() == 3 || p.getRowCount() == 4 || p.getRowCount() == 5) {
            int count = 0;
            for (int i = 0; i < rowPosGroup.length; i++) {
                if (Integer.parseInt(rowPosGroup[i]) < 0) {
                    count++;
                }
            }
            if (count > 0) {  //有负数
                setMeasuredDimension(740, 400);
                isSideGroove = true;
            } else {   //  没有负数
                setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
                isSideGroove = false;
            }
        }
    }

    public void setNeededDrawAttribute(KeyInfo p) {
        this.p = p;
        //初始化画笔的属性和路径
        initPaintAndPathAttribute();
        //额外Y长度是40
        excessY = 40;
        String[] spaceGroup = p.getSpace().split(";");
        rowPosGroup = p.getRow_pos().split(";");
        depthNameGroup = p.getDepth_name().split(";");
        if (p.getAlign() == 0) {
            if (p.getRowCount() == 1) {
                //new 2个hashaMap存放大圆 小圆的 半径
                largeCircleRadiusMap1 = new HashMap<>();
                smallCircleRadiusMap1 = new HashMap<>();
                track = new int[rowPosGroup.length];
                track[0] = Integer.parseInt(rowPosGroup[0]);
                String[] spaces = spaceGroup[0].split(",");
                depthName1 = depthNameGroup[0].split(",");
                toothDistance1 = new int[spaces.length];
                toothDepthNameGroup1 = new String[toothDistance1.length];
                //把齿距转为int
                for (int i = 0; i < spaces.length; i++) {
                    toothDistance1[i] = Integer.parseInt(spaces[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                //得到齿距的比例值
                spacesScaleValue = keyBodyWidth / toothDistance1[toothDistance1.length - 1];
                depthScaleRatio = keyBodyHeight / p.getWidth();
                //得到第一条轨道的位子
                track[0] = (int) (track[0] * depthScaleRatio) + excessY;

                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (int) (toothDistance1[i] * spacesScaleValue) + 132;
                    Log.d("齿距是多少", "setNeededDrawAttribute: " + toothDistance1[i]);
                }
                keyWidthY = (int) (keyBodyHeight + excessY);

                //根据深度名保存圆的半径大小
                int largeCircleRadius = 16;
                int smallCircleRadius = 6;
                for (int i = 0; i < depthName1.length; i++) {
                    largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius);
                    largeCircleRadius = largeCircleRadius + 5;
                    smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius);
                    smallCircleRadius = smallCircleRadius + 3;
                    Log.d("" + i, "setNeededDrawAttribute: " + largeCircleRadius);
                }
            } else if (p.getRowCount() == 2) {
                //new 二组hashaMap存放大圆 小圆的 半径
                largeCircleRadiusMap1 = new HashMap<>();
                smallCircleRadiusMap1 = new HashMap<>();

                largeCircleRadiusMap2 = new HashMap<>();
                smallCircleRadiusMap2 = new HashMap<>();
                //获得轨道1
                track = new int[rowPosGroup.length];
                track[0] = Integer.parseInt(rowPosGroup[0]);
                //获得轨道2
                track[1] = Integer.parseInt(rowPosGroup[1]);

                String[] spaces1;
                String[] spaces2;
                //获得spaces
                if (spaceGroup.length == 1) {
                    spaces1 = spaceGroup[0].split(",");
                    spaces2 = spaceGroup[0].split(",");
                } else {
                    spaces1 = spaceGroup[0].split(",");
                    spaces2 = spaceGroup[1].split(",");
                }

                toothDistance1 = new int[spaces1.length];
                toothDistance2 = new int[spaces2.length];
                toothDepthNameGroup1 = new String[toothDistance1.length];
                toothDepthNameGroup2 = new String[toothDistance2.length];
                //把第一组齿距转为int 初始化第一组齿的深度名字
                for (int i = 0; i < spaces1.length; i++) {
                    toothDistance1[i] = Integer.parseInt(spaces1[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                //把第二组齿距转为int 初始化第二组齿的深度名字
                for (int i = 0; i < spaces2.length; i++) {
                    toothDistance2[i] = Integer.parseInt(spaces2[i]);
                    toothDepthNameGroup2[i] = "X";
                }
                if (toothDistance1[toothDistance1.length - 1] > toothDistance2[toothDistance2.length - 1]) {
                    maxToothDistance = toothDistance1[toothDistance1.length - 1];
                } else {
                    maxToothDistance = toothDistance2[toothDistance2.length - 1];
                }
                //得到齿距的比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                //得到深度比例值
                depthScaleRatio = keyBodyHeight / p.getWidth();
                //换算轨道的位置
                for (int i = 0; i < track.length; i++) {
                    if (track[i] > 0) {
                        track[i] = (int) (track[i] * depthScaleRatio) + excessY;
                    } else {
                        track[i] = 359;
                    }
                }
              if(p.getId()==5545||p.getId()==1353){
                  track[1] = 370;
              }
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (int) (toothDistance1[i] * spacesScaleValue) + 132;
                    Log.d("齿距是多少", "setNeededDrawAttribute: " + toothDistance1[i]);
                }
                for (int i = 0; i < toothDistance2.length; i++) {
                    toothDistance2[i] = (int) (toothDistance2[i] * spacesScaleValue) + 132;
                    Log.d("齿距是多少", "setNeededDrawAttribute: " + toothDistance2[i]);
                }
                keyWidthY = (int) (keyBodyHeight + excessY);


                int largeCircleRadius1 = 0;
                int smallCircleRadius1 = 0;

                //根据齿的数量保存第一组圆的半径大小
                depthName1 = depthNameGroup[0].split(",");

                if (toothDistance1.length > 8) {
                    largeCircleRadius1 = 10;
                    smallCircleRadius1 = 5;
                    for (int i = 0; i < depthName1.length; i++) {
                        largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius1);
                        largeCircleRadius1 = largeCircleRadius1 + 2;
                        smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius1);
                        smallCircleRadius1 = smallCircleRadius1 + 1;
                    }
                } else {
                    //根据深度名保存第一组圆的半径大小
                    largeCircleRadius1 = 16;
                    smallCircleRadius1 = 6;
                    for (int i = 0; i < depthName1.length; i++) {
                        largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius1);
                        largeCircleRadius1 = largeCircleRadius1 + 5;
                        smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius1);
                        smallCircleRadius1 = smallCircleRadius1 + 3;
                    }

                    int largeCircleRadius2 = 0;
                    int smallCircleRadius2 = 0;

                    //根据齿的数量保存第二组圆的半径大小
                    depthName2 = depthNameGroup[1].split(",");
                    if (toothDistance2.length > 8) {
                        largeCircleRadius2 = 10;
                        smallCircleRadius2 = 5;
                        for (int i = 0; i < depthName2.length; i++) {
                            largeCircleRadiusMap2.put(depthName2[i], largeCircleRadius2);
                            largeCircleRadius2 = largeCircleRadius2 + 2;
                            smallCircleRadiusMap2.put(depthName2[i], smallCircleRadius2);
                            smallCircleRadius2 = smallCircleRadius2 + 1;
                        }
                    } else {
                        largeCircleRadius2 = 16;
                        smallCircleRadius2 = 6;
                        for (int i = 0; i < depthName2.length; i++) {
                            largeCircleRadiusMap2.put(depthName2[i], largeCircleRadius2);
                            largeCircleRadius2 = largeCircleRadius2 + 5;
                            smallCircleRadiusMap2.put(depthName2[i], smallCircleRadius2);
                            smallCircleRadius2 = smallCircleRadius2 + 3;
                        }
                    }
                }
            } else if (p.getRowCount() == 3 || p.getRowCount() == 4 || p.getRowCount() == 5) {
                String[] spaces;
                int[] toothDistance;
                String[] toothDepthNameGroup;
                for (int i = 0; i < spaceGroup.length; i++) {
                    spaces = spaceGroup[i].split(",");
                    toothDistance = new int[spaces.length];
                    toothDepthNameGroup = new String[spaces.length];
                    for (int j = 0; j < spaces.length; j++) {
                        //转为int类型
                        toothDistance[j] = Integer.parseInt(spaces[j]);
                        //初始化所有组的深度名
                        toothDepthNameGroup[j] = "X";
                    }
                    toothDistanceXList.add(toothDistance);
                    toothDepthNameList.add(toothDepthNameGroup);
                }
                //获得最大齿距
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    if (maxToothDistance < toothDistance[toothDistance.length - 1]) {
                        maxToothDistance = toothDistance[toothDistance.length - 1];
                    }
                }
                //得到space的比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                //换算成合适的x方向的齿距
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    for (int j = 0; j < toothDistanceXList.get(i).length; j++) {
                        toothDistanceXList.get(i)[j] = (int) (toothDistance[j] * spacesScaleValue) + 132;
                    }
                }
                //钥匙宽度
                keyWidthY = (int) (keyBodyHeight + excessY);
                //得到深度比例值
                depthScaleRatio = keyBodyHeight / p.getWidth();
                //把轨道位子转为int
                track = new int[rowPosGroup.length];
                for (int i = 0; i < track.length; i++) {
                    if (Integer.parseInt(rowPosGroup[i]) < 0) {
                        track[i] = 359;
                    } else {
                        track[i] = (int) (Integer.parseInt(rowPosGroup[i]) * depthScaleRatio) + excessY;
                    }
                }
                int largeCircleRadius;
                int smallCircleRadius;
                depthNameList = new ArrayList<>();
                largeCircleList = new ArrayList<>();
                smallCircleList = new ArrayList<>();
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    //大圆HashMap
                    Map<String, Integer> largeCircleRadiusMap = new HashMap<>();
                    //小圆HashMap
                    Map<String, Integer> smallCircleRadiusMap = new HashMap<>();
                    if (toothDistance.length > 8) {
                        largeCircleRadius = 10;
                        smallCircleRadius = 5;
                        String[] depthName = depthNameGroup[i].split(",");
                        //保存每组的深度名
                        depthNameList.add(depthName);
                        for (int j = 0; j < depthName.length; j++) {
                            //大圆
                            largeCircleRadiusMap.put(depthName[j], largeCircleRadius);
                            //小圆
                            smallCircleRadiusMap.put(depthName[j], smallCircleRadius);
                            largeCircleRadius = largeCircleRadius + 2;
                            smallCircleRadius = smallCircleRadius + 1;
                        }
                        largeCircleList.add(largeCircleRadiusMap);
                        smallCircleList.add(smallCircleRadiusMap);
                    } else {
                        largeCircleRadius = 16;
                        smallCircleRadius = 6;
                        String[] depthName = depthNameGroup[i].split(",");
                        //保存每组的深度名
                        depthNameList.add(depthName);
                        for (int j = 0; j < depthName.length; j++) {
                            //大圆
                            largeCircleRadiusMap.put(depthName[j], largeCircleRadius);
                            //小圆
                            smallCircleRadiusMap.put(depthName[j], smallCircleRadius);
                            largeCircleRadius = largeCircleRadius + 4;
                            smallCircleRadius = smallCircleRadius + 3;
                        }
                        largeCircleList.add(largeCircleRadiusMap);
                        smallCircleList.add(smallCircleRadiusMap);
                    }
                }

            }
        } else if (p.getAlign() == 1) {  //尖端定位
            if (p.getRowCount() == 1) {
                //new 2个hashaMap存放大圆 小圆的 半径
                largeCircleRadiusMap1 = new HashMap<>();
                smallCircleRadiusMap1 = new HashMap<>();
                track = new int[rowPosGroup.length];
                track[0] = Integer.parseInt(rowPosGroup[0]);
                String[] spaces = spaceGroup[0].split(",");
                depthName1 = depthNameGroup[0].split(",");
                toothDistance1 = new int[spaces.length];
                toothDepthNameGroup1 = new String[toothDistance1.length];
                //把齿距转为int
                for (int i = 0; i < spaces.length; i++) {
                    toothDistance1[i] = Integer.parseInt(spaces[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                //得到齿距的比例值
                spacesScaleValue = keyBodyWidth / toothDistance1[0];
                depthScaleRatio = keyBodyHeight / p.getWidth();
                //得到第一条轨道的位子
                track[0] = (int) (track[0] * depthScaleRatio) + excessY;
                keySumX = (int) keyBodyWidth + 132;
                surplusX = keySumX - (keySumX - (int) (toothDistance1[toothDistance1.length - 1] * spacesScaleValue));
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (keySumX - (int) (toothDistance1[i] * spacesScaleValue)) + surplusX;
                    Log.d("齿距是多少", "setNeededDrawAttribute: " + toothDistance1[i]);
                }
                keyWidthY = (int) (keyBodyHeight + excessY);

                //根据深度名保存圆的半径大小
                int largeCircleRadius = 16;
                int smallCircleRadius = 6;
                for (int i = 0; i < depthName1.length; i++) {
                    largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius);
                    largeCircleRadius = largeCircleRadius + 5;
                    smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius);
                    smallCircleRadius = smallCircleRadius + 3;
                    Log.d("" + i, "setNeededDrawAttribute: " + largeCircleRadius);
                }
            } else if (p.getRowCount() == 2) {
                //new 二组hashaMap存放大圆 小圆的 半径
                largeCircleRadiusMap1 = new HashMap<>();
                smallCircleRadiusMap1 = new HashMap<>();

                largeCircleRadiusMap2 = new HashMap<>();
                smallCircleRadiusMap2 = new HashMap<>();
                //获得轨道1
                track = new int[rowPosGroup.length];
                track[0] = Integer.parseInt(rowPosGroup[0]);
                //获得轨道2
                track[1] = Integer.parseInt(rowPosGroup[1]);
                String[] spaces1;
                String[] spaces2;
                //获得spaces
                if (spaceGroup.length == 1) {
                    spaces1 = spaceGroup[0].split(",");
                    spaces2 = spaceGroup[0].split(",");
                } else {
                    spaces1 = spaceGroup[0].split(",");
                    spaces2 = spaceGroup[1].split(",");
                }

                toothDistance1 = new int[spaces1.length];
                toothDistance2 = new int[spaces2.length];
                toothDepthNameGroup1 = new String[toothDistance1.length];
                toothDepthNameGroup2 = new String[toothDistance2.length];
                //把第一组齿距转为int 初始化第一组齿的深度名字
                for (int i = 0; i < spaces1.length; i++) {
                    toothDistance1[i] = Integer.parseInt(spaces1[i]);
                    toothDepthNameGroup1[i] = "X";
                }
                //把第二组齿距转为int 初始化第二组齿的深度名字
                for (int i = 0; i < spaces2.length; i++) {
                    toothDistance2[i] = Integer.parseInt(spaces2[i]);
                    toothDepthNameGroup2[i] = "X";
                }
                if (toothDistance1[0] > toothDistance2[0]) {
                    maxToothDistance = toothDistance1[0];
                } else {
                    maxToothDistance = toothDistance2[0];
                }
                //得到齿距的比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                //得到深度比例值
                depthScaleRatio = keyBodyHeight / p.getWidth();
                //换算第一个轨道的位置
                track[0] = (int) (track[0] * depthScaleRatio) + excessY;
                //换算第二个轨道的位置
                track[1] = (int) (track[1] * depthScaleRatio) + excessY;
                if (toothDistance1[toothDistance1.length - 1] < toothDistance2[toothDistance2.length - 1]) {
                    lastToothDistance = toothDistance1[toothDistance1.length - 1];
                } else {
                    lastToothDistance = toothDistance2[toothDistance2.length - 1];
                }
                keySumX = (int) keyBodyWidth + 132;
                surplusX = keySumX - (keySumX - (int) (spacesScaleValue * lastToothDistance));
                for (int i = 0; i < toothDistance1.length; i++) {
                    toothDistance1[i] = (keySumX - (int) (toothDistance1[i] * spacesScaleValue)) + surplusX;
                    Log.d("齿距是多少", "setNeededDrawAttribute: " + toothDistance1[i]);
                }
                for (int i = 0; i < toothDistance2.length; i++) {
                    toothDistance2[i] = (keySumX - (int) (toothDistance2[i] * spacesScaleValue)) + surplusX;
                    Log.d("齿距是多少", "setNeededDrawAttribute: " + toothDistance2[i]);
                }
                keyWidthY = (int) (keyBodyHeight + excessY);
                int largeCircleRadius1 = 0;
                int smallCircleRadius1 = 0;
                int largeCircleRadius2 = 0;
                int smallCircleRadius2 = 0;
                if (depthNameGroup.length == 1) {
                    depthName1 = depthNameGroup[0].split(",");
                    depthName2 = depthNameGroup[0].split(",");
                } else if (depthNameGroup.length == 2) {
                    depthName1 = depthNameGroup[0].split(",");
                    depthName2 = depthNameGroup[1].split(",");
                }
                //第一行和第二行相等
                if (Integer.parseInt(rowPosGroup[0]) == Integer.parseInt(rowPosGroup[1])) {
                    isRowEquality = true;
                    //根据齿的数量保存第一组圆的半径大小
                    if (toothDistance1.length > 8) {  //超过8个长度大圆小圆
                        largeCircleRadius1 = 12;
                        smallCircleRadius1 = 7;
                        for (int i = 0; i < depthName1.length; i++) {
                            largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius1);
                            largeCircleRadius1 = largeCircleRadius1 + 2;
                            smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius1);
                            smallCircleRadius1 = smallCircleRadius1 + 1;
                        }
                    } else {  //没有超过8个长度大圆小圆
                        largeCircleRadius1 = 18;
                        smallCircleRadius1 = 8;
                        for (int i = 0; i < depthName1.length; i++) {
                            largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius1);
                            largeCircleRadius1 = largeCircleRadius1 + 5;
                            smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius1);
                            smallCircleRadius1 = smallCircleRadius1 + 3;
                        }
                    }
                    //根据齿的数量保存第二组圆的半径大小
                    if (toothDistance2.length > 8) {
                        largeCircleRadius2 = 10;
                        smallCircleRadius2 = 5;
                        for (int i = 0; i < depthName2.length; i++) {
                            largeCircleRadiusMap2.put(depthName2[i], largeCircleRadius2);
                            largeCircleRadius2 = largeCircleRadius2 + 2;
                            smallCircleRadiusMap2.put(depthName2[i], smallCircleRadius2);
                            smallCircleRadius2 = smallCircleRadius2 + 1;
                        }
                    } else {
                        largeCircleRadius2 = 15;
                        smallCircleRadius2 = 6;
                        for (int i = 0; i < depthName2.length; i++) {
                            largeCircleRadiusMap2.put(depthName2[i], largeCircleRadius2);
                            largeCircleRadius2 = largeCircleRadius2 + 5;
                            smallCircleRadiusMap2.put(depthName2[i], smallCircleRadius2);
                            smallCircleRadius2 = smallCircleRadius2 + 3;
                        }
                    }
                } else {
                    if (toothDistance1.length > 8) {
                        largeCircleRadius1 = 10;
                        smallCircleRadius1 = 5;
                        for (int i = 0; i < depthName1.length; i++) {
                            largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius1);
                            largeCircleRadius1 = largeCircleRadius1 + 2;
                            smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius1);
                            smallCircleRadius1 = smallCircleRadius1 + 1;
                        }
                    } else {
                        //根据深度名保存第一组圆的半径大小
                        largeCircleRadius1 = 16;
                        smallCircleRadius1 = 6;
                        for (int i = 0; i < depthName1.length; i++) {
                            largeCircleRadiusMap1.put(depthName1[i], largeCircleRadius1);
                            largeCircleRadius1 = largeCircleRadius1 + 5;
                            smallCircleRadiusMap1.put(depthName1[i], smallCircleRadius1);
                            smallCircleRadius1 = smallCircleRadius1 + 3;
                        }
                    }
                    //根据齿的数量保存第二组圆的半径大小
                    if (toothDistance2.length > 8) {
                        largeCircleRadius2 = 12;
                        smallCircleRadius2 = 7;
                        for (int i = 0; i < depthName2.length; i++) {
                            largeCircleRadiusMap2.put(depthName2[i], largeCircleRadius2);
                            largeCircleRadius2 = largeCircleRadius2 + 2;
                            smallCircleRadiusMap2.put(depthName2[i], smallCircleRadius2);
                            smallCircleRadius2 = smallCircleRadius2 + 1;
                        }
                    } else {
                        largeCircleRadius2 = 16;
                        smallCircleRadius2 = 6;
                        for (int i = 0; i < depthName2.length; i++) {
                            largeCircleRadiusMap2.put(depthName2[i], largeCircleRadius2);
                            largeCircleRadius2 = largeCircleRadius2 + 5;
                            smallCircleRadiusMap2.put(depthName2[i], smallCircleRadius2);
                            smallCircleRadius2 = smallCircleRadius2 + 3;
                        }
                    }
                }
            } else if (p.getRowCount() == 3 || p.getRowCount() == 4 || p.getRowCount() == 5) {
                String[] spaces;
                int[] toothDistance;
                String[] toothDepthNameGroup;
                for (int i = 0; i < spaceGroup.length; i++) {
                    spaces = spaceGroup[i].split(",");
                    toothDistance = new int[spaces.length];
                    toothDepthNameGroup = new String[spaces.length];
                    for (int j = 0; j < spaces.length; j++) {
                        //转为int类型
                        toothDistance[j] = Integer.parseInt(spaces[j]);
                        //初始化所有组的深度名
                        toothDepthNameGroup[j] = "X";
                    }
                    toothDistanceXList.add(toothDistance);
                    toothDepthNameList.add(toothDepthNameGroup);
                }
                //获得最大齿距
                toothDistance = toothDistanceXList.get(0);
                maxToothDistance = toothDistance[0];
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    if (maxToothDistance < toothDistance[0]) {
                        maxToothDistance = toothDistance[0];
                    }
                }
                //得到space的比例值
                spacesScaleValue = keyBodyWidth / maxToothDistance;
                //钥匙的X长度
                keySumX = (int) keyBodyWidth + 132;
                //拿出每组的最后一个齿距，比谁最小
                toothDistance = toothDistanceXList.get(0);
                lastToothDistance = toothDistance[toothDistance.length - 1];
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    if (lastToothDistance > toothDistance[toothDistance.length - 1]) {
                        lastToothDistance = toothDistance[toothDistance.length - 1];
                    }
                }
                surplusX = keySumX - (keySumX - (int) (spacesScaleValue * lastToothDistance));
                //换算成合适的x方向的齿距
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    for (int j = 0; j < toothDistanceXList.get(i).length; j++) {
                        toothDistanceXList.get(i)[j] = (keySumX - (int) (toothDistance[j] * spacesScaleValue)) + surplusX;
                    }
                }
                //钥匙宽度
                keyWidthY = (int) (keyBodyHeight + excessY);
                //得到深度比例值
                depthScaleRatio = keyBodyHeight / p.getWidth();
                //把轨道位子转为int
                track = new int[rowPosGroup.length];
                for (int i = 0; i < track.length; i++) {
                    if (Integer.parseInt(rowPosGroup[i]) < 0) {
                        track[i] = 359;
                    } else {
                        track[i] = (int) (Integer.parseInt(rowPosGroup[i]) * depthScaleRatio) + excessY;
                    }
                }
                int largeCircleRadius;
                int smallCircleRadius;
                depthNameList = new ArrayList<>();
                largeCircleList = new ArrayList<>();
                smallCircleList = new ArrayList<>();
                for (int i = 0; i < toothDistanceXList.size(); i++) {
                    toothDistance = toothDistanceXList.get(i);
                    //大圆HashMap
                    Map<String, Integer> largeCircleRadiusMap = new HashMap<>();
                    //小圆HashMap
                    Map<String, Integer> smallCircleRadiusMap = new HashMap<>();
                    if (toothDistance.length > 8) {
                        largeCircleRadius = 10;
                        smallCircleRadius = 5;
                        String[] depthName = depthNameGroup[i].split(",");
                        //保存每组的深度名
                        depthNameList.add(depthName);
                        for (int j = 0; j < depthName.length; j++) {
                            //大圆
                            largeCircleRadiusMap.put(depthName[j], largeCircleRadius);
                            //小圆
                            smallCircleRadiusMap.put(depthName[j], smallCircleRadius);
                            largeCircleRadius = largeCircleRadius + 2;
                            smallCircleRadius = smallCircleRadius + 1;
                        }
                        largeCircleList.add(largeCircleRadiusMap);
                        smallCircleList.add(smallCircleRadiusMap);
                    } else {
                        largeCircleRadius = 16;
                        smallCircleRadius = 6;
                        String[] depthName = depthNameGroup[i].split(",");
                        //保存每组的深度名
                        depthNameList.add(depthName);
                        for (int j = 0; j < depthName.length; j++) {
                            //大圆
                            largeCircleRadiusMap.put(depthName[j], largeCircleRadius);
                            //小圆
                            smallCircleRadiusMap.put(depthName[j], smallCircleRadius);
                            largeCircleRadius = largeCircleRadius + 4;
                            smallCircleRadius = smallCircleRadius + 3;
                        }
                        largeCircleList.add(largeCircleRadiusMap);
                        smallCircleList.add(smallCircleRadiusMap);
                    }
                }

            }
        }
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (p.getAlign() == 0) {
            //画肩部定位钥匙坯
            path.moveTo(10, 10);
            path.lineTo(80, 10);
            path.quadTo(92, 8, 90, excessY);
            path.lineTo(720, excessY);
            path.quadTo(732, excessY, 730, excessY + 10);
            path.lineTo(730, keyWidthY - 10);
            path.quadTo(730, keyWidthY + 2, 720, keyWidthY);
            path.lineTo(90, keyWidthY);
            path.quadTo(92, keyWidthY + 34, 80, keyWidthY + 30);
            path.lineTo(10, keyWidthY + 30);
            canvas.drawPath(path, blackPaint);
            canvas.drawPath(path, grayPaint);
            path.reset();
            //画红色箭头  肩部
            path.moveTo(90, 20);  //100
            path.lineTo(120, 20);//第一条  100
            path.lineTo(120, 12);
            path.lineTo(130, 24);//中间点  104
            path.lineTo(120, 36);
            path.lineTo(120, 28);
            path.lineTo(90, 28);
            canvas.drawPath(path, arrowsPaint);
            path.reset();
        } else if (p.getAlign() == 1) {
            //画尖端定位的钥匙形状
            path.moveTo(10, excessY);
            path.lineTo(720, excessY);
            path.quadTo(732, excessY, 730, excessY + 10);
            path.lineTo(730, keyWidthY - 10);
            path.quadTo(730, keyWidthY + 2, 720, keyWidthY);
            path.lineTo(10, keyWidthY);
            canvas.drawPath(path, blackPaint);
            canvas.drawPath(path, grayPaint);
            path.reset();
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
        if (p.getRowCount() == 1) {
            for (int i = 0; i < toothDepthNameGroup1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i], track[0] + 10, bluePaint);  //等于X画蓝色的字 不画圆。
                } else if (toothDepthNameGroup1[i].contains(".")) {   //有小数的全部画红色的字
                    String[] str = toothDepthNameGroup1[i].split("\\.");
                    String toothDepthName = "";
                    int number = Integer.parseInt(str[1]);
                    if (toothDepthNameGroup1[i].contains("X")) {
                        if (number >= 5) {
                            toothDepthName = depthName1[0];
                            int largeRadius = largeCircleRadiusMap1.get(toothDepthName) == null ? 0 : largeCircleRadiusMap1.get(toothDepthName);
                            int smallRadius = smallCircleRadiusMap1.get(toothDepthName) == null ? 0 : smallCircleRadiusMap1.get(toothDepthName);
                            //大圆
                            canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                        } else {
                            int largeRadius = largeCircleRadiusMap1.get(str[0]) == null ? 0 : largeCircleRadiusMap1.get(str[0]);
                            int smallRadius = smallCircleRadiusMap1.get(str[0]) == null ? 0 : smallCircleRadiusMap1.get(str[0]);
                            //大圆
                            canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(str[0], toothDistance1[i], track[0] + 10, redPaint);
                        }
                    } else { //不包含X
                        if (number >= 5) {
                            if (!str[0].equals(depthName1[depthName1.length - 1])) {
                                for (int j = 0; j < depthName1.length; j++) {
                                    if (str[0].equals(depthName1[j])) {
                                        toothDepthName = depthName1[j + 1];
                                        break;
                                    }
                                }
                            } else {
                                toothDepthName = str[0];
                            }
                            int largeRadius = largeCircleRadiusMap1.get(toothDepthName) == null ? 0 : largeCircleRadiusMap1.get(toothDepthName);
                            int smallRadius = smallCircleRadiusMap1.get(toothDepthName) == null ? 0 : smallCircleRadiusMap1.get(toothDepthName);
                            //大圆
                            canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                        } else {  //不大于5
                            int largeRadius = largeCircleRadiusMap1.get(str[0]) == null ? 0 : largeCircleRadiusMap1.get(str[0]);
                            int smallRadius = smallCircleRadiusMap1.get(str[0]) == null ? 0 : smallCircleRadiusMap1.get(str[0]);
                            //大圆
                            canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(str[0], toothDistance1[i], track[0] + 10, redPaint);
                        }
                    }
                } else {
                    int largeRadius = largeCircleRadiusMap1.get(toothDepthNameGroup1[i]) == null ? 0 : largeCircleRadiusMap1.get(toothDepthNameGroup1[i]);
                    int smallRadius = smallCircleRadiusMap1.get(toothDepthNameGroup1[i]) == null ? 0 : smallCircleRadiusMap1.get(toothDepthNameGroup1[i]);
                    //大圆
                    canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                    //小圆
                    canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                    //画字
                    canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], track[0] + 10, bluePaint);
                }
            }
            //画轨道虚线
            path.moveTo(120, track[0]);
            path.lineTo(730, track[0]);
            canvas.drawPath(path, dashedPaint);
            path.reset();
        } else if (p.getRowCount() == 2) {
            if (isSideGroove) {
                path.moveTo(10, 330);
                path.lineTo(720, 330);
                path.quadTo(732, 330, 730, 330 + 10);
                path.lineTo(730, 378);
                path.quadTo(730, 390, 720, 388);
                path.lineTo(10, 388);
                canvas.drawPath(path, blackPaint);
                canvas.drawPath(path, grayPaint);
                path.reset();
                path.moveTo(90, 330);
                path.lineTo(90, 388);
                canvas.drawPath(path, blackPaint);
                path.reset();
            }
            //绘制第一组齿的凹槽
            for (int i = 0; i < toothDepthNameGroup1.length; i++) {
                if (toothDepthNameGroup1[i].equals("X")) {
                    canvas.drawText("X", toothDistance1[i], track[0] + 10, bluePaint);  //等于X画蓝色的字 不画圆。
                } else if (toothDepthNameGroup1[i].contains(".")) {   //有小数的全部画红色的字
                    String[] str = toothDepthNameGroup1[i].split("\\.");
                    String toothDepthName = "";
                    int number = Integer.parseInt(str[1]);
                    if (toothDepthNameGroup1[i].contains("X")) {
                        if (number >= 5) {
                            toothDepthName = depthName1[0];
                            int largeRadius = largeCircleRadiusMap1.get(toothDepthName) == null ? 0 : largeCircleRadiusMap1.get(toothDepthName);
                            int smallRadius = smallCircleRadiusMap1.get(toothDepthName) == null ? 0 : smallCircleRadiusMap1.get(toothDepthName);
                            if (isRowEquality) {
                                //大圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], largeRadius, largeCirclePaint);
                                //大圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], largeRadius, largeCirclePaint);
                                //小圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], smallRadius, smallCirclePaint);
                                //小圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);

                            } else {
                                //大圆
                                canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                            }
                        } else {
                            int largeRadius = largeCircleRadiusMap1.get(str[0]) == null ? 0 : largeCircleRadiusMap1.get(str[0]);
                            int smallRadius = smallCircleRadiusMap1.get(str[0]) == null ? 0 : smallCircleRadiusMap1.get(str[0]);
                            if (isRowEquality) {
                                //大圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], largeRadius, largeCirclePaint);
                                //大圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], largeRadius, largeCirclePaint);
                                //小圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], smallRadius, smallCirclePaint);
                                //小圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);

                            } else {
                                //大圆
                                canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                            }
                        }
                    } else { //不包含X
                        if (number >= 5) {
                            Log.d("是多少1？", "onDraw: " + str[0]);
                            if (!str[0].equals(depthName1[depthName1.length - 1])) {
                                for (int j = 0; j < depthName1.length; j++) {
                                    if (str[0].equals(depthName1[j])) {
                                        toothDepthName = depthName1[j + 1];
                                        Log.d("是多少2？", "onDraw: " + depthName1[j + 1]);
                                        break;
                                    }
                                }
                            } else {
                                toothDepthName = str[0];
                            }
                            int largeRadius = largeCircleRadiusMap1.get(toothDepthName) == null ? 0 : largeCircleRadiusMap1.get(toothDepthName);
                            int smallRadius = smallCircleRadiusMap1.get(toothDepthName) == null ? 0 : smallCircleRadiusMap1.get(toothDepthName);
                            if (isRowEquality) {
                                //大圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], largeRadius, largeCirclePaint);
                                //大圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], largeRadius, largeCirclePaint);
                                //小圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], smallRadius, smallCirclePaint);
                                //小圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);

                            } else {
                                //大圆
                                canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                            }
                        } else {  //不大于5
                            int largeRadius = largeCircleRadiusMap1.get(str[0]) == null ? 0 : largeCircleRadiusMap1.get(str[0]);
                            int smallRadius = smallCircleRadiusMap1.get(str[0]) == null ? 0 : smallCircleRadiusMap1.get(str[0]);
                            if (isRowEquality) {
                                //大圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], largeRadius, largeCirclePaint);
                                //大圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], largeRadius, largeCirclePaint);
                                //小圆左边
                                canvas.drawCircle(toothDistance1[i] - largeRadius, track[0], smallRadius, smallCirclePaint);
                                //小圆右边
                                canvas.drawCircle(toothDistance1[i] + largeRadius, track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                            } else {
                                //大圆
                                canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(toothDepthName, toothDistance1[i], track[0] + 10, redPaint);
                            }
                        }
                    }
                } else {
                    int largeRadius = largeCircleRadiusMap1.get(toothDepthNameGroup1[i]) == null ? 0 : largeCircleRadiusMap1.get(toothDepthNameGroup1[i]);
                    int smallRadius = smallCircleRadiusMap1.get(toothDepthNameGroup1[i]) == null ? 0 : smallCircleRadiusMap1.get(toothDepthNameGroup1[i]);
                    if (isRowEquality) {
                        //大圆左边
                        canvas.drawCircle(toothDistance1[i] - smallRadius, track[0], largeRadius, largeCirclePaint);
                        //大圆右边
                        canvas.drawCircle(toothDistance1[i] + smallRadius, track[0], largeRadius, largeCirclePaint);
                        //小圆左边
                        canvas.drawCircle(toothDistance1[i] - smallRadius, track[0], smallRadius, smallCirclePaint);
                        //小圆右边
                        canvas.drawCircle(toothDistance1[i] + smallRadius, track[0], smallRadius, smallCirclePaint);
                        //画字
                        canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], track[0] + 10, bluePaint);

                    } else {
                        //大圆
                        canvas.drawCircle(toothDistance1[i], track[0], largeRadius, largeCirclePaint);
                        //小圆
                        canvas.drawCircle(toothDistance1[i], track[0], smallRadius, smallCirclePaint);
                        //画字
                        canvas.drawText(toothDepthNameGroup1[i], toothDistance1[i], track[0] + 10, bluePaint);
                    }
                }
            }
            //绘制第二组齿的凹槽
            for (int i = 0; i < toothDepthNameGroup2.length; i++) {
                if (toothDepthNameGroup2[i].equals("X")) {
                    canvas.drawText("X", toothDistance2[i], track[1] + 10, bluePaint);  //等于X画蓝色的字 不画圆。
                } else if (toothDepthNameGroup2[i].contains(".")) {   //有小数的全部画红色的字
                    String[] str = toothDepthNameGroup2[i].split("\\.");
                    String toothDepthName = "";
                    int number = Integer.parseInt(str[1]);
                    if (toothDepthNameGroup2[i].contains("X")) {
                        if (number >= 5) {
                            toothDepthName = depthName2[0];
                            int largeRadius = largeCircleRadiusMap2.get(toothDepthName) == null ? 0 : largeCircleRadiusMap2.get(toothDepthName);
                            int smallRadius = smallCircleRadiusMap2.get(toothDepthName) == null ? 0 : smallCircleRadiusMap2.get(toothDepthName);
                            //大圆
                            canvas.drawCircle(toothDistance2[i], track[1], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance2[i], track[1], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(toothDepthName, toothDistance2[i], track[1] + 10, redPaint);
                        } else {
                            int largeRadius = largeCircleRadiusMap2.get(str[0]) == null ? 0 : largeCircleRadiusMap2.get(str[0]);
                            int smallRadius = smallCircleRadiusMap2.get(str[0]) == null ? 0 : smallCircleRadiusMap2.get(str[0]);
                            //大圆
                            canvas.drawCircle(toothDistance2[i], track[1], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance2[i], track[1], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(str[0], toothDistance2[i], track[1] + 10, redPaint);
                        }
                    } else { //不包含X
                        if (number >= 5) {
                            if (!str[0].equals(depthName2[depthName2.length - 1])) {
                                for (int j = 0; j < depthName2.length; j++) {
                                    if (str[0].equals(depthName2[j])) {
                                        toothDepthName = depthName2[j + 1];
                                        break;
                                    }
                                }
                            } else {
                                toothDepthName = str[0];
                            }
                            int largeRadius = largeCircleRadiusMap2.get(toothDepthName) == null ? 0 : largeCircleRadiusMap2.get(toothDepthName);
                            int smallRadius = smallCircleRadiusMap2.get(toothDepthName) == null ? 0 : smallCircleRadiusMap2.get(toothDepthName);
                            //大圆
                            canvas.drawCircle(toothDistance2[i], track[1], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance2[i], track[1], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(toothDepthName, toothDistance2[i], track[1] + 10, redPaint);
                        } else {  //不大于5
                            int largeRadius = largeCircleRadiusMap2.get(str[0]) == null ? 0 : largeCircleRadiusMap2.get(str[0]);
                            int smallRadius = smallCircleRadiusMap2.get(str[0]) == null ? 0 : smallCircleRadiusMap2.get(str[0]);
                            //大圆
                            canvas.drawCircle(toothDistance2[i], track[1], largeRadius, largeCirclePaint);
                            //小圆
                            canvas.drawCircle(toothDistance2[i], track[1], smallRadius, smallCirclePaint);
                            //画字
                            canvas.drawText(str[0], toothDistance2[i], track[1] + 10, redPaint);
                        }
                    }
                } else {
                    int largeRadius = largeCircleRadiusMap2.get(toothDepthNameGroup2[i]) == null ? 0 : largeCircleRadiusMap2.get(toothDepthNameGroup2[i]);
                    int smallRadius = smallCircleRadiusMap2.get(toothDepthNameGroup2[i]) == null ? 0 : smallCircleRadiusMap2.get(toothDepthNameGroup2[i]);
                    //大圆
                    canvas.drawCircle(toothDistance2[i], track[1], largeRadius, largeCirclePaint);
                    //小圆
                    canvas.drawCircle(toothDistance2[i], track[1], smallRadius, smallCirclePaint);
                    //画字
                    canvas.drawText(toothDepthNameGroup2[i], toothDistance2[i], track[1] + 10, bluePaint);
                }
            }
            //画轨道虚线
            path.moveTo(120, track[0]);
            path.lineTo(730, track[0]);
            path.moveTo(120, track[1]);
            path.lineTo(730, track[1]);
            canvas.drawPath(path, dashedPaint);
            path.reset();

        } else if (p.getRowCount() == 3 || p.getRowCount() == 4 || p.getRowCount() == 5) {
            if (isSideGroove) {
                path.moveTo(10, 330);
                path.lineTo(720, 330);
                path.quadTo(732, 330, 730, 330 + 10);
                path.lineTo(730, 378);
                path.quadTo(730, 390, 720, 388);
                path.lineTo(10, 388);
                canvas.drawPath(path, blackPaint);
                canvas.drawPath(path, grayPaint);
                path.reset();
                path.moveTo(90, 330);
                path.lineTo(90, 388);
                canvas.drawPath(path, blackPaint);
                path.reset();
            }
            //画轨道虚线
            for (int i = 0; i < track.length; i++) {
                if (track[i] == 359) {
                    path.moveTo(120, track[i]);
                    path.lineTo(730, track[i]);
                } else {
                    path.moveTo(120, track[i]);
                    path.lineTo(700, track[i]);
                }
            }
            canvas.drawPath(path, dashedPaint);
            path.reset();
            //根据每组的齿的深度名画凹槽
            for (int i = 0; i < toothDistanceXList.size(); i++) {
                //获得齿深度名组
                toothDepthName = toothDepthNameList.get(i);
                //获得齿距组
                toothDistanceX = toothDistanceXList.get(i);
                //获得大圆hashMap集合
                largeCircleRadiusMap = largeCircleList.get(i);
                //获得小圆hashMap集合
                smallCircleRadiusMap = smallCircleList.get(i);
                for (int j = 0; j < toothDepthName.length; j++) {
                    if (toothDepthName[j].equals("X")) {
                        canvas.drawText("X", toothDistanceX[j], track[i] + 10, bluePaint);  //等于X画蓝色的字 不画圆。
                    } else if (toothDepthName[j].contains(".")) {   //有小数的全部画红色的字
                        String[] str = toothDepthName[j].split("\\.");
                        String s = "";
                        int number = Integer.parseInt(str[1]);
                        if (toothDepthName[j].contains("X")) {
                            if (number >= 5) {
                                s = depthNameList.get(i)[0];  //第一组的第一个
                                int largeRadius = largeCircleRadiusMap.get(s) == null ? 0 : largeCircleRadiusMap.get(s);
                                int smallRadius = smallCircleRadiusMap.get(s) == null ? 0 : smallCircleRadiusMap.get(s);
                                //大圆
                                canvas.drawCircle(toothDistanceX[j], track[i], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistanceX[j], track[i], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(s, toothDistance2[j], track[i] + 10, redPaint);
                            } else {
                                int largeRadius = largeCircleRadiusMap.get(str[0]) == null ? 0 : largeCircleRadiusMap.get(str[0]);
                                int smallRadius = smallCircleRadiusMap.get(str[0]) == null ? 0 : smallCircleRadiusMap.get(str[0]);
                                //大圆
                                canvas.drawCircle(toothDistanceX[j], track[i], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistanceX[j], track[i], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(str[0], toothDistanceX[j], track[i] + 10, redPaint);
                            }
                        } else { //不包含X有小数的
                            depthName = depthNameList.get(i);
                            if (number >= 5) {
                                if (!str[0].equals(depthName[depthName.length - 1])) {
                                    for (int k = 0; k < depthName.length; k++) {
                                        if (str[0].equals(depthName[k])) {
                                            s = depthName[k + 1];
                                            break;
                                        }
                                    }
                                } else {
                                    s = str[0];
                                }
                                int largeRadius = largeCircleRadiusMap.get(s) == null ? 0 : largeCircleRadiusMap.get(s);
                                int smallRadius = smallCircleRadiusMap.get(s) == null ? 0 : smallCircleRadiusMap.get(s);
                                //大圆
                                canvas.drawCircle(toothDistanceX[j], track[i], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistanceX[j], track[i], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(s, toothDistanceX[j], track[i] + 10, redPaint);
                            } else {  //不大于5
                                int largeRadius = largeCircleRadiusMap.get(str[0]) == null ? 0 : largeCircleRadiusMap.get(str[0]);
                                int smallRadius = smallCircleRadiusMap.get(str[0]) == null ? 0 : smallCircleRadiusMap.get(str[0]);
                                //大圆
                                canvas.drawCircle(toothDistanceX[j], track[i], largeRadius, largeCirclePaint);
                                //小圆
                                canvas.drawCircle(toothDistanceX[j], track[i], smallRadius, smallCirclePaint);
                                //画字
                                canvas.drawText(str[0], toothDistanceX[j], track[i] + 10, redPaint);
                            }
                        }
                    } else {
                        int largeRadius = largeCircleRadiusMap.get(toothDepthName[j]) == null ? 0 : largeCircleRadiusMap.get(toothDepthName[j]);
                        int smallRadius = smallCircleRadiusMap.get(toothDepthName[j]) == null ? 0 : smallCircleRadiusMap.get(toothDepthName[j]);
                        //大圆
                        canvas.drawCircle(toothDistanceX[j], track[i], largeRadius, largeCirclePaint);
                        //小圆
                        canvas.drawCircle(toothDistanceX[j], track[i], smallRadius, smallCirclePaint);
                        //画字
                        canvas.drawText(toothDepthName[j], toothDistanceX[j], track[i] + 10, bluePaint);
                    }
                }
            }

        } else if (p.getRowCount() == 5) {

        }

    }

    private void initPaintAndPathAttribute() {
        path = new Path();
        blackPaint = new Paint();
        grayPaint = new Paint();
        dashedPaint = new Paint();
        bluePaint = new Paint();
        arrowsPaint = new Paint();
        redPaint = new Paint();
        largeCirclePaint = new Paint();
        smallCirclePaint = new Paint();
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
        bluePaint.setTextSize(26);
        //画红色提示箭头的属性
        arrowsPaint.setColor(Color.RED);
        arrowsPaint.setAntiAlias(true);//去掉锯齿
        arrowsPaint.setStyle(Paint.Style.FILL);//设置画笔风格为实心充满
        arrowsPaint.setStrokeWidth(2); //设置画线的宽度
        //画红色字体的笔
        redPaint.setColor(Color.parseColor("#FF3030"));  //设置字体颜色  红色
        redPaint.setFlags(Paint.ANTI_ALIAS_FLAG);  //消除字体的抗锯齿
        redPaint.setFakeBoldText(true);  //设置为粗体
        redPaint.setTextAlign(Paint.Align.CENTER);
        redPaint.setTextSize(30);
        //画大圆的笔
        largeCirclePaint.setAntiAlias(true);//去掉锯齿
        largeCirclePaint.setColor(Color.parseColor("#FFD700")); //黄色
        largeCirclePaint.setStyle(Paint.Style.FILL);
        largeCirclePaint.setStrokeWidth(1);
        //画小圆的笔
        smallCirclePaint.setAntiAlias(true);//去掉锯齿
        smallCirclePaint.setColor(Color.parseColor("#cc9900")); //黄色
        smallCirclePaint.setStyle(Paint.Style.FILL);
        smallCirclePaint.setStrokeWidth(1);

    }
}
