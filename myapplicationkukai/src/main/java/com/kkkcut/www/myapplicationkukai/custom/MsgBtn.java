package com.kkkcut.www.myapplicationkukai.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;

/**
 * Created by Administrator on 2017/1/10.
 */

public class MsgBtn extends RelativeLayout {
    static TextView tv_content;
    public MsgBtn(Context context) {
        super(context);
        RelativeLayout rl = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_msg_hint, this, true);
        tv_content = (TextView) rl.findViewById(R.id.tv_count);
    }

    public MsgBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MsgBtn(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private static int msgCount;
    public static void setMessageCount(int count) {
        msgCount = count;
        try {
            if (count == 0) {
                tv_content.setVisibility(View.GONE);
            } else {
                tv_content.setVisibility(View.VISIBLE);
                if (count < 100) {
                    tv_content.setText(count + "");
                } else {
                    tv_content.setText("99+");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   invalidate();
    }

    public static void addMsg() {
        tv_content.setVisibility(View.VISIBLE);
    }
    public static  void conceal(){
        tv_content.setVisibility(View.GONE);
    }

}
