package com.kkkcut.www.myapplicationkukai.drawKeyImg;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/17.
 */

public abstract class Key extends View  {

    public Key(Context context) {
        super(context);
    }
    public Key(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Key(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置绘制图案的宽度和高度  不能大于View本身宽度和高度
     * @param width
     * @param height
     */
    public abstract void setDrawPatternSize(int width,int height);

    /**
     *  设置绘制的深度和深度名是否显示
     * @param isDraw
     */
    public abstract void setShowDrawDepthAndDepthName(boolean isDraw);
    public abstract void setToothDepthName(String depthName);
    public abstract void redrawKey();
    public abstract void setKeyToothAmount(int toothAmount);
    public abstract String getSpace();
    public abstract  int getKeyToothAmount();
    public abstract ArrayList<String[]> getAllToothDepthName();
    public abstract void setShowArrows(boolean isShowArrows);

    /**
     * 设置齿码为默认
     * 默认是“X”
     */
    public abstract void setToothCodeDefault();
    public abstract String getReadOrder(int locatingSlot,int detectionMode,int isRound);
    public abstract String  getCutOrder(int cutDepth,int locatingSlot,String assistClamp,String cutterDiameter,int speed, int ZDepth,int detectionMode);

    public void setNeededDrawAttribute(KeyInfo p){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }
    @Override
    protected void onWindowVisibilityChanged( int visibility) {
        super.onWindowVisibilityChanged(visibility);

    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }
}
