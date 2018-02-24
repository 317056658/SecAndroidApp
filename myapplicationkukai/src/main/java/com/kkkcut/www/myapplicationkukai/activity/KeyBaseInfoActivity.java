package com.kkkcut.www.myapplicationkukai.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.MyExpandableAdapter;
import com.kkkcut.www.myapplicationkukai.entity.KeyBlank;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.utils.DensityUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import static com.kkkcut.www.myapplicationkukai.utils.DensityUtils.dip2px;

public class KeyBaseInfoActivity extends AppCompatActivity implements View.OnClickListener {
  private   LinearLayout mSpacesColumn;
   private LinearLayout mSpacesGroup;
  private   LinearLayout mAllDepthData;
  private   LinearLayout mLlDepthGroupNumber;
   private ArrayList<String[]> mList = new ArrayList();
   private KeyInfo ki;//定义一个基本信息类
    private View mDecorView;
    private String  keyBlanks;
    private List<KeyBlank> groupList=new ArrayList<>();
    private List<String>  childList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_blank_info);
        //获得传过来的数据
        Intent intent=getIntent();

        ki = intent.getParcelableExtra("keyInfo");  //获得钥匙信息
        keyBlanks=intent.getStringExtra("keyBlanksInfo");//获得钥匙胚信息
        initViews();
        //设置齿距
        setSpaces();
        //设置depths的数据
        setDepths();
    }

    /**
     * 初始化本活动的views
     */
    private void  initViews(){
        RadioButton mRbShoulder = (RadioButton) findViewById(R.id.btn_Shoulder);
        RadioButton mRbFrontend= (RadioButton) findViewById(R.id.btn_Tip);
        if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {  //等于0就是肩部定位
            mRbShoulder.setChecked(true);
            mRbFrontend.setClickable(false);
        } else {  //就是尖端定位
            mRbFrontend.setChecked(true);
            mRbShoulder.setClickable(false);
        }
        TextView mTvWidth = (TextView) findViewById(R.id.tv_width);
        TextView mTvThick = (TextView) findViewById(R.id.tv_thick);
        Button mBtnCloseActivity = (Button)findViewById(R.id.btn_close_activity);
        mBtnCloseActivity.setOnClickListener(this);
        //设置钥匙信息的宽度
        mTvWidth.setText(ki.getWidth()+"");
        //设置钥匙信息的厚度
        mTvThick.setText(ki.getThick()+"");
        mDecorView=getWindow().getDecorView();
        //得到Spaces总线性布局
        mSpacesGroup = (LinearLayout) findViewById(R.id.ll_spaces_group);
        //获得列的ViewGroup
        mSpacesColumn= (LinearLayout) findViewById(R.id.ll_spaces_column);
        //获得加载全部深度数据线性布局
        mAllDepthData = (LinearLayout) findViewById(R.id.ll_depth_data);
        //获得加载深度组号线性布局
        mLlDepthGroupNumber = (LinearLayout) findViewById(R.id.ll_depth_group_number);
        TextView mTvDesc =  (TextView)findViewById(R.id.tv_desc);
        mTvDesc.setText(ki.getDesc());
        TextView mTvCodeSeries=(TextView)findViewById(R.id.tv_code_series);
        if(ki.getCodeSeries()==null){
            mTvCodeSeries.setText("Code Series:");
        }else {
            mTvCodeSeries.setText("Code Series:"+ki.getCodeSeries());
        }

        ExpandableListView mExpandableListView =(ExpandableListView)findViewById(R.id.elv);
        if(!TextUtils.isEmpty(keyBlanks)){
             //准备好数据源
              String[] newStr   =keyBlanks.split("\n");
            for (int i = 0; i <newStr.length ; i++) {
                 KeyBlank kb=new KeyBlank();
                 String[] newStr1=newStr[i].split(":");
                  kb.setMfgName(newStr1[0]);
                 String[] newStr2= newStr1[1].split(",");
                    childList=new ArrayList<>();
                for (int j = 0; j <newStr2.length ; j++) {
                    childList.add(newStr2[j]);
                    kb.setModelNameList(childList);
                }
                groupList.add(kb);
            }
         MyExpandableAdapter expandableAdapter=new MyExpandableAdapter(this,groupList);
            mExpandableListView.setAdapter(expandableAdapter); //绑定数据源
            // 设置全部默认展开
            int groupCount = mExpandableListView.getCount();
            for(int i=0;i<groupCount;i++){
                mExpandableListView.expandGroup(i);
            }
            mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                    return false;
                }
            });
            mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {

                }
            });

       }

    }

    /**
     * 设置齿距数据
     * 加载齿距数据
     */
    private void setSpaces(){
        //得到钥匙的打印位置
        String[] array1 = ki.getSpace().split(";");
        //判断打印位置数据有几组
        int length = 0;
        //加载列
        for (int i = 0; i < array1.length; i++) {
            //得到谁的齿位最多
            if (length < array1[i].split(",").length) {
                length = array1[i].split(",").length;
            }
            mList.add(array1[i].split(","));
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this, 20)));
            tv.setText((i + 1) + "");
            tv.setBackgroundResource(R.drawable.edit_shape);
            //字体颜色为黑色
            tv.setTextColor(Color.parseColor("#030401"));
            mSpacesColumn.addView(tv);
        }
        //加载行
        for (int i = 0; i < length; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置布局方式
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(this);
            //dp 转px
            tv.setLayoutParams(new LinearLayout.LayoutParams(dip2px(this, 30), dip2px(this, 20)));
            tv.setTextColor(Color.parseColor("#030401"));
            tv.setText((i + 1) + "");
            layout.addView(tv);
            //得到当前几条数据 做判断
            for (int j = 0; j < array1.length; j++) {
                TextView tv1 = new TextView(this);
                tv1.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this,20)));
                String[] array3 = mList.get(j);
                tv1.setTextColor(Color.parseColor("#030401"));
                if (array3.length < i) {
                    tv1.setText("");
                } else {
                    tv1.setText(array3[i]);
                }
                layout.addView(tv1);
            }
            mSpacesGroup.addView(layout);
        }
    }

    /**
     *   解析深度数据 设置深度
     */
    public void setDepths() {
        //清空内存 重新复用
        mList.clear();
        String[] array1 = ki.getDepth().split(";");
        //判断打印位置数据有几组
        int length = 0;
        for (int i = 0; i < array1.length; i++) {
            //得到谁的齿位最多
            if (length < array1[i].split(",").length) {
                length = array1[i].split(",").length;
            }
            mList.add(array1[i].split(","));
            TextView tv = new TextView(this);
            //设置 宽，高 都为自适应
            tv.setLayoutParams(new LinearLayout.LayoutParams(60, dip2px(this, 20)));
            tv.setText((i + 1) + "");
            tv.setBackgroundResource(R.drawable.edit_shape);
            //字体颜色为黑色
            tv.setTextColor(Color.parseColor("#030401"));
            mLlDepthGroupNumber.addView(tv);
        }
        int i;
        for (i = 0; i < length; i++) {
            LinearLayout layout = new LinearLayout(this);
            layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //设置布局方式
            layout.setOrientation(LinearLayout.HORIZONTAL);
            TextView tv = new TextView(this);
            //子类告诉父类 我要变成什么样。
            //dp 转px
            tv.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this, 30), DensityUtils.dip2px(this, 20)));
            tv.setTextColor(Color.parseColor("#030401"));
            tv.setText((i + 1) + "");
            layout.addView(tv);
            //得到当前几条数据 做判断
            for (int j = 0; j < array1.length; j++) {
                TextView tv1 = new TextView(this);
                tv1.setLayoutParams(new LinearLayout.LayoutParams(60, DensityUtils.dip2px(this, 20)));
                String[] array3 = mList.get(j);
                tv1.setTextColor(Color.parseColor("#030401"));
                if (array3.length <= i) {
                    tv1.setText("");
                } else {
                    tv1.setText(array3[i]);
                }
                layout.addView(tv1);
            }
            mAllDepthData.addView(layout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_close_activity:  //关闭活动
                this.finish();
                break;
        }
    }
}
