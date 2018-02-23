package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class ToothFindCodeActivity extends AppCompatActivity implements View.OnClickListener {
private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tooth_find_code);
        initViews();
    }

    @Override
    public void onClick(View view) {
         Button button  =(Button)findViewById(view.getId());
          switch (view.getId()){
              case R.id.Tbtn_colse:
                  finish();
                  break;
          }
    }

    /**
     * 获得意图数据
     */
  private void   getIntentData(){
             Intent intent=getIntent();
      //获得传过来的标题
      title=intent.getStringExtra("title");
  }
    /**
     * 初始化
     */
    private  void initViews(){
        TextView mTitle =(TextView)findViewById(R.id.Ttv_title);
        //设置标题
        mTitle.setText(getIntent().getStringExtra("title"));
        Button mColse  =(Button)findViewById(R.id.Tbtn_colse);
        mColse.setOnClickListener(this);
    }

    /**
     * 启动它自身
     * @param title
     */
    public  static  void startItselfActivity(Context context, String title){
        Intent intent=new Intent(context,ToothFindCodeActivity.class);
        intent.putExtra("title",title);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(getWindow().getDecorView());
    }
}
