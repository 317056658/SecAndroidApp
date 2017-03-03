package com.kkkcut.www.myapplicationkukai.KeyYearParagraph;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.AutoType.frmKeyCut_Main;
import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.Myadapter;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao;
import com.kkkcut.www.myapplicationkukai.entity.series;

import java.util.List;

public class KeyYearParagraphActivity extends AppCompatActivity {
    private Button btn_home,btn_back;
    String  name;
    private  List<series> list_series;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_year_paragraph);
        btn_home=(Button)findViewById(R.id.btn_home);
        btn_back=(Button)findViewById(R.id.btn_back);
        btn_home.setOnClickListener(listener0);
        btn_back.setOnClickListener(listener1);
               Intent intent=getIntent();
               int i =intent.getIntExtra("_id",-1);
              name=intent.getStringExtra("name");
        Log.d("aaaa", "onCreate: "+i);
        getData(i);
    }
    private void getData(int i){
        //在UI里更新名字
         TextView tv_name  =(TextView)findViewById(R.id.tv_name);
        tv_name.setText(name);
        SQLiteDao  sql=new SQLiteDao(this);
        list_series = sql.SeriesByModelID(i);
        //获得listView
        ListView  lv= (ListView) findViewById(R.id.lv);
        Myadapter  adapter=new Myadapter(this,list_series);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(item);

    }
    AdapterView.OnItemClickListener  item=new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                  Intent intent=new Intent(KeyYearParagraphActivity.this,frmKeyCut_Main.class);
                  intent.putExtra("_id",list_series.get(position).get_id());
                  startActivity(intent);
        }
    };

      View.OnClickListener listener0=new View.OnClickListener(){
          @Override
          public void onClick(View view) {
              Intent  intent=new Intent(KeyYearParagraphActivity.this, MainActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
              startActivity(intent);
              finish();
          }
      };
      View.OnClickListener listener1=new View.OnClickListener(){
          @Override
          public void onClick(View view) {
                    finish();
          }
      };
}
