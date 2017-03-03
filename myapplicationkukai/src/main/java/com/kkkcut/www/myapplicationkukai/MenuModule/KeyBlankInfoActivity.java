package com.kkkcut.www.myapplicationkukai.MenuModule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.keyBlankInfo;

import java.util.ArrayList;

public class KeyBlankInfoActivity extends AppCompatActivity {
    LinearLayout ll;
    LinearLayout mSum_ll;
    LinearLayout depthssum_ll;
    LinearLayout depthssum_ll1;
    ArrayList<String[]> mlist = new ArrayList();
    keyBlankInfo info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_blank_info);
        RadioButton radio1 = (RadioButton) findViewById(R.id.btn_Shoulder);
        RadioButton radio2 = (RadioButton) findViewById(R.id.btn_Tip);
        TextView text_width = (TextView) findViewById(R.id.text_width);
        TextView text_thick = (TextView) findViewById(R.id.text_thick);
        //得到Spaces总线性布局
        mSum_ll = (LinearLayout) findViewById(R.id.sum_ll);
        //获得子线性布局1
        ll = (LinearLayout) findViewById(R.id.ll_1);
        //得到Depths总线性布局
        depthssum_ll = (LinearLayout) findViewById(R.id.Depthssum_ll);
        //获得子线性布局2
        depthssum_ll1 = (LinearLayout) findViewById(R.id.Depthssum_ll1);
        //获得传过来的数据
        info = getIntent().getParcelableExtra("blank");
        if (info.getAlign() == 0) {
            radio1.setChecked(true);
        } else {
            radio2.setChecked(true);
        }
        //钥匙宽
        text_width.setText(info.getWidth() + "");
        //钥匙厚度
        text_thick.setText(info.getThick() + "");
        //得到钥匙的打印位置
        String[] array1 = info.getSpace().split(";");
        //判断打印位置数据有几组
        int length = 0;
        for (int i = 0; i < array1.length; i++) {
            //得到谁的齿位最多
            if (length < array1[i].split(",").length) {
                length = array1[i].split(",").length;
            }
            mlist.add(array1[i].split(","));
            TextView tv = new TextView(this);
            //设置 宽，高 都为自适应
            tv.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this, 20)));
            tv.setText((i + 1) + "");
            tv.setBackgroundResource(R.drawable.edit_shape);
            //字体颜色为黑色
            tv.setTextColor(Color.parseColor("#030401"));
            ll.addView(tv);
        }
        Log.d("数据有几条", "onCreate: " + array1.length);
        int i;
        for (i = 0; i < length; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置布局方式
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(this);
            //子类告诉父类 我要变成什么样。
            //dp 转px
            tv.setLayoutParams(new LinearLayout.LayoutParams(dip2px(this, 30), dip2px(this, 20)));
            tv.setTextColor(Color.parseColor("#030401"));
            tv.setText((i + 1) + "");
            layout.addView(tv);
            //得到当前几条数据 做判断
            for (int j = 0; j < array1.length; j++) {
                TextView tv1 = new TextView(this);
                tv1.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this, 20)));
                String[] array3 = mlist.get(j);
                tv1.setTextColor(Color.parseColor("#030401"));
                if (array3.length < i) {
                    tv1.setText("");
                } else {
                    tv1.setText(array3[i]);
                }
                layout.addView(tv1);
            }
            mSum_ll.addView(layout);
        }
        //加载depths的数据
        setDepths();
      TextView tvdesc=  (TextView)findViewById(R.id.tv_desc);
        tvdesc.setText(info.getDesc());
          //关闭按钮
       Button closeUI= (Button)findViewById(R.id.closeUI);
        closeUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    // 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setDepths() {
        //清空内存 重新复用
        mlist.clear();
        String[] array1 = info.getDepth().split(";");
        //判断打印位置数据有几组
        int length = 0;
        for (int i = 0; i < array1.length; i++) {
            //得到谁的齿位最多
            if (length < array1[i].split(",").length) {
                length = array1[i].split(",").length;
            }
            mlist.add(array1[i].split(","));
            TextView tv = new TextView(this);
            //设置 宽，高 都为自适应
            tv.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this, 20)));
            tv.setText((i + 1) + "");
            tv.setBackgroundResource(R.drawable.edit_shape);
            //字体颜色为黑色
            tv.setTextColor(Color.parseColor("#030401"));
            depthssum_ll1.addView(tv);
        }
        Log.d("数据有几条", "onCreate: " + array1.length);
        int i;
        for (i = 0; i < length; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置布局方式
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(this);
            //子类告诉父类 我要变成什么样。
            //dp 转px
            tv.setLayoutParams(new LinearLayout.LayoutParams(dip2px(this, 30), dip2px(this, 20)));
            tv.setTextColor(Color.parseColor("#030401"));
            tv.setText((i + 1) + "");
            layout.addView(tv);
            //得到当前几条数据 做判断
            for (int j = 0; j < array1.length; j++) {
                Log.d("aa", "setDepths:      "+i);
                TextView tv1 = new TextView(this);
                tv1.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this, 20)));
                String[] array3 = mlist.get(j);
                tv1.setTextColor(Color.parseColor("#030401"));
                if (array3.length <= i) {
                    tv1.setText("");
                } else {
                    tv1.setText(array3[i]);
                }
                layout.addView(tv1);
            }
            depthssum_ll.addView(layout);
        }

    }
}
