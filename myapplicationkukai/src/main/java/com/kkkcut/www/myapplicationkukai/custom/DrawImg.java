package com.kkkcut.www.myapplicationkukai.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/12/15.
 */

public class DrawImg extends View {
    public DrawImg(Context context) {
        super(context);
    }

    public DrawImg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawImg(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        Paint p=new Paint();
        p.setAntiAlias(true);//去掉抗锯齿
        p.setColor(Color.BLACK);  //画笔的颜色
        p.setStyle(Paint.Style.STROKE);//设置画笔风格为实心
        p.setStrokeWidth(4);
       // canvas.drawRect(100, 170, 500,300, p)          ;
         Path pa=new Path();
        pa.moveTo(200,100);

        pa.cubicTo(1000, 50, 200, 300, 400, 220);

        canvas.drawPath(pa,p);
    }
}
