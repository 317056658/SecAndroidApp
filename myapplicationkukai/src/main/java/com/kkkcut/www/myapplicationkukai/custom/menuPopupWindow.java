package com.kkkcut.www.myapplicationkukai.custom;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.MenuModule.KeyBlankInfoActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.keyBlankInfo;
import com.kkkcut.www.myapplicationkukai.setup.frmMaintenance;

/**
 * Created by Administrator on 2016/12/7.
 */

public class menuPopupWindow extends PopupWindow {
    private View view;
    private Context context;
    keyBlankInfo blank;

    public menuPopupWindow(Context context, keyBlankInfo blank) {
        //获得子布局
        this.view = LayoutInflater.from(context).inflate(R.layout.layout_menu,null);
        // 设置外部可点击 点击PopupWindow就消失
        this.setOutsideTouchable(true);
        this.context = context;
        this.blank=blank;
        // View添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        this.view.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int height = view.findViewById(R.id.id_pop_layout).getTop();
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                           dismiss();
//                    }
//                }
//                return true;
//            }
//        });
    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);

        // 设置弹出窗体可点击
      this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为透明
      ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        // 设置弹出窗体的背景 要配合 setOutsideTouchable 使用 不然无意义
       this.setBackgroundDrawable(dw);
        // 设置弹出窗体显示时的动画，从底部向上弹出
       this.setAnimationStyle(R.style.PopupAnimation);
            Button   btn_info1 =(Button)view.findViewById(R.id.btn_info);
            btn_info1.setOnClickListener(info);
        Button  WindowHome = (Button)view.findViewById(R.id.window_home);
        WindowHome.setOnClickListener(backMain);
        Button  Calibration = (Button) view.findViewById(R.id.btn_calibration);
         Calibration.setOnClickListener(CalibrateListener);
        Button  Measure = (Button) view.findViewById(R.id.pop_Measure);
        Measure.setOnClickListener(MeasureListener);
        Button  Stop = (Button)view.findViewById(R.id.pop_stop);
        Stop.setOnClickListener(StopListener);
    }
    View.OnClickListener info= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(context, KeyBlankInfoActivity.class);
            intent.putExtra("blank",blank);
            context.startActivity(intent);
        }
    };
    View.OnClickListener backMain= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent=new Intent(context,MainActivity.class);
            // 跳到主界面 跳转到的activity若已在栈中存在，则将其上的activity都销掉。
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            mClose.onCloseAcivity();
        }
    };
    View.OnClickListener CalibrateListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent1=new Intent(context,frmMaintenance.class);
            context.startActivity(intent1);
        }
    };

    View.OnClickListener MeasureListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mPopupWindow   popupWindow=new mPopupWindow(context);
            View rootview = LayoutInflater.from(context).inflate(R.layout.activity_audia2, null);
            popupWindow.showAtLocation(rootview, Gravity.CENTER,0,0);
        }
    };

    View.OnClickListener StopListener= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
              dismiss();
        }
    };

    View.OnClickListener backMain3= new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private OnClickCloseListener mClose=null;
    public interface  OnClickCloseListener{
        void onCloseAcivity();
    }
    //对话暴露一个方法
    public void setOnClickClose(OnClickCloseListener mClose){
            this.mClose=mClose;
    }

}




