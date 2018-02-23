package com.kkkcut.www.myapplicationkukai.publicClass.JPushReceiver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.TempInfo;

public class TextActivity extends AppCompatActivity {
    private String content;
    private String title;
    private TextView tv_back;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text); //加载布局
        Intent intent=getIntent();
        if(TempInfo.jump){
              content  =  intent.getStringExtra("content");
              title  = intent.getStringExtra("title");
        }else {
             content  =  intent.getStringExtra("content");
             title  = intent.getStringExtra("title");
        }
        TextView tv_tag=(TextView)findViewById(R.id.tv_tag);
        tv_back= (TextView)findViewById(R.id.tv_back);
        tv_back.setOnClickListener(back);
        Button btn_home=(Button)findViewById(R.id.btn_home);
        btn_home.setOnClickListener(home);
        WebView web=(WebView) findViewById(R.id.web);
        tv_tag.setText(title);
        //加载、并显示HTML代码  两个方法都行
     //  web.loadDataWithBaseURL(null,sb.toString(), "text/html", "utf-8", null);
        web.loadData(content,"text/html; charset=UTF-8", null);
//         WebSettings webSettings=web.getSettings();
//        web.getSettings().setDefaultTextEncodingName("UTF-8");//设置默认为utf-8
//        webSettings.setJavaScriptEnabled(true);
    }

//    View.OnClickListener Update =new View.OnClickListener(){
//        @Override
//        public void onClick(View view) {
//            HttpRequest.downFile(Ve.getUrl(),TextActivity.this);
//        }
//    };
    View.OnClickListener back=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
                finish();
        }
    };
    View.OnClickListener home=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent  intent=new Intent(TextActivity.this, MainActivity.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };
}
