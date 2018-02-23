package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class LackToothFindActivity extends AppCompatActivity implements View.OnClickListener {
    private View mDecorView;
    private KeyInfo ki;
    private String  title;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lack_tooth);
        mDecorView=getWindow().getDecorView();
        iniViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
    public static  void startItselfActivity(Context context, String  title, KeyInfo ki){
        Intent intent=new Intent(context,LackToothFindActivity.class);
        intent.putExtra("title",title);
        intent.putExtra("keyInfo",ki);
        context.startActivity(intent);
    }

    /**
     * 获得意图数据
     */
    private void  getIntentData(){
            Intent  intent=getIntent();
        ki = intent.getParcelableExtra("keyInfo");
        title=intent.getStringExtra("title");
    }
    /**
     * 初始化Views
     */
    private  void iniViews(){
        TextView  tvTitle =(TextView)findViewById(R.id.tv_title);
        tvTitle.setText(title);
        initRecyclerView();
    }

    /**
     * 初始化RecyclerView
     */
    private  void  initRecyclerView(){
        mRecyclerView= (RecyclerView)findViewById(R.id.recycler_view);
        // new 一个 线性布局管理者
        LinearLayoutManager  layoutManager=new LinearLayoutManager(this);
        // 设置布局方向为横向
         layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //设置布局管理者 为线性布局
        mRecyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度或者宽度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);

    }
    @Override
    public void onClick(View v) {

    }
}
