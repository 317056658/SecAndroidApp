package com.kkkcut.www.myapplicationkukai.custom;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.kkkcut.www.myapplicationkukai.R;

/**
 * Created by Administrator on 2016/12/9.
 */

public class mPopupWindow extends PopupWindow {
    public mPopupWindow(Context context) {
            View v1= View.inflate(context, R.layout.decoder_dialog,null);
              /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(v1);

    //  设置窗口获得焦点
        this.setFocusable(true);

        // 设置弹出窗体的宽和高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(650);
        Button btn=  (Button)v1.findViewById(R.id.btn_close);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
