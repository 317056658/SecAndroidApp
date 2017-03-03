package com.kkkcut.www.myapplicationkukai.Search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.keyAdapter;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao;
import com.kkkcut.www.myapplicationkukai.entity.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class frmKeySelect extends AppCompatActivity {
    Button btn_key_blank, btn_clear, btn_move, btn_remove;
    Button btn_A, btn_B, btn_C, btn_D, btn_E, btn_F, btn_G, btn_H, btn_I, btn_J, btn_K, btn_L, btn_M, btn_N, btn_O, btn_P, btn_Q, btn_R, btn_S, btn_T,
            btn_U, btn_V, btn_W, btn_X, btn_Y, btn_Z, btn_minus, btn_point, btn_, btn_comma;
    Button btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_0;
    StringBuilder str = new StringBuilder();
    private List mlist = new ArrayList<>();
    EditText etext;
    Button ic_card;
    String[] num = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "_", ",","0","1"
            ,"2","3","4","5","6","7","8","9",".","-"};
    // 子线程 发送消息 在主线程更新IU
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            int num = message.what;
            //发送消息是1 就变为灰色 按钮不能点击
            if (num == 1) {
                btn_A.setBackgroundColor(Color.parseColor("#50587C"));
                btn_A.setTextColor(Color.parseColor("#818285"));
                btn_A.setEnabled(false);
                btn_B.setBackgroundColor(Color.parseColor("#50587C"));
                btn_B.setTextColor(Color.parseColor("#818285"));
                btn_B.setEnabled(false);
                btn_C.setBackgroundColor(Color.parseColor("#50587C"));
                btn_C.setTextColor(Color.parseColor("#818285"));
                btn_C.setEnabled(false);
                btn_D.setBackgroundColor(Color.parseColor("#50587C"));
                btn_D.setTextColor(Color.parseColor("#818285"));
                btn_C.setEnabled(false);
                btn_E.setBackgroundColor(Color.parseColor("#50587C"));
                btn_E.setTextColor(Color.parseColor("#818285"));
                btn_E.setEnabled(false);
                btn_F.setBackgroundColor(Color.parseColor("#50587C"));
                btn_F.setTextColor(Color.parseColor("#818285"));
                btn_F.setEnabled(false);
                btn_G.setBackgroundColor(Color.parseColor("#50587C"));
                btn_G.setTextColor(Color.parseColor("#818285"));
                btn_G.setEnabled(false);
                btn_H.setBackgroundColor(Color.parseColor("#50587C"));
                btn_H.setTextColor(Color.parseColor("#818285"));
                btn_H.setEnabled(false);
                btn_I.setBackgroundColor(Color.parseColor("#50587C"));
                btn_I.setTextColor(Color.parseColor("#818285"));
                btn_I.setEnabled(false);
                btn_J.setBackgroundColor(Color.parseColor("#50587C"));
                btn_J.setTextColor(Color.parseColor("#818285"));
                btn_J.setEnabled(false);
                btn_K.setBackgroundColor(Color.parseColor("#50587C"));
                btn_K.setTextColor(Color.parseColor("#818285"));
                btn_K.setEnabled(false);
                btn_L.setBackgroundColor(Color.parseColor("#50587C"));
                btn_L.setTextColor(Color.parseColor("#818285"));
                btn_L.setEnabled(false);
                btn_M.setBackgroundColor(Color.parseColor("#50587C"));
                btn_M.setTextColor(Color.parseColor("#818285"));
                btn_M.setEnabled(false);
                btn_N.setBackgroundColor(Color.parseColor("#50587C"));
                btn_N.setTextColor(Color.parseColor("#818285"));
                btn_N.setEnabled(false);
                btn_O.setBackgroundColor(Color.parseColor("#50587C"));
                btn_O.setTextColor(Color.parseColor("#818285"));
                btn_O.setEnabled(false);
                btn_P.setBackgroundColor(Color.parseColor("#50587C"));
                btn_P.setTextColor(Color.parseColor("#818285"));
                btn_P.setEnabled(false);
                btn_Q.setBackgroundColor(Color.parseColor("#50587C"));
                btn_Q.setTextColor(Color.parseColor("#818285"));
                btn_Q.setEnabled(false);
                btn_R.setBackgroundColor(Color.parseColor("#50587C"));
                btn_R.setTextColor(Color.parseColor("#818285"));
                btn_R.setEnabled(false);
                btn_S.setBackgroundColor(Color.parseColor("#50587C"));
                btn_S.setTextColor(Color.parseColor("#818285"));
                btn_S.setEnabled(false);
                btn_T.setBackgroundColor(Color.parseColor("#50587C"));
                btn_T.setTextColor(Color.parseColor("#818285"));
                btn_T.setEnabled(false);
                btn_U.setBackgroundColor(Color.parseColor("#50587C"));
                btn_U.setTextColor(Color.parseColor("#818285"));
                btn_U.setEnabled(false);
                btn_V.setBackgroundColor(Color.parseColor("#50587C"));
                btn_V.setTextColor(Color.parseColor("#818285"));
                btn_V.setEnabled(false);
                btn_W.setBackgroundColor(Color.parseColor("#50587C"));
                btn_W.setTextColor(Color.parseColor("#818285"));
                btn_W.setEnabled(false);
                btn_X.setBackgroundColor(Color.parseColor("#50587C"));
                btn_X.setTextColor(Color.parseColor("#818285"));
                btn_X.setEnabled(false);
                btn_Y.setBackgroundColor(Color.parseColor("#50587C"));
                btn_Y.setTextColor(Color.parseColor("#818285"));
                btn_Y.setEnabled(false);
                btn_Z.setBackgroundColor(Color.parseColor("#50587C"));
                btn_Z.setTextColor(Color.parseColor("#818285"));
                btn_Z.setEnabled(false);
                btn_.setBackgroundColor(Color.parseColor("#50587C"));
                btn_.setTextColor(Color.parseColor("#818285"));
                btn_.setEnabled(false);

                btn_minus.setBackgroundColor(Color.parseColor("#50587C"));
                btn_minus.setTextColor(Color.parseColor("#818285"));
                btn_minus.setEnabled(false);
                btn_point.setBackgroundColor(Color.parseColor("#50587C"));
                btn_point.setTextColor(Color.parseColor("#818285"));
                btn_point.setEnabled(false);
                btn_comma.setBackgroundColor(Color.parseColor("#50587C"));
                btn_comma.setTextColor(Color.parseColor("#818285"));
                btn_comma.setEnabled(false);

            }
            //发送消息是2 就变回来颜色  按钮点击
            if (num == 2) {
                Btninit(grid_layout);
                etext.setText("");
            }
            return false;
        }
    });
    public static TextView inputText,SearchMode;
   private String strTempList;
   public static keyAdapter adapter;
   private GridLayout grid_layout;
   private RecyclerView search_recycler_view;
   private List list_blank_mfg;
    private int SEARCH_STATUS=0;
    public static Button btn_back;
    private  String  condition_name;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getViewId();
        search_recycler_view = (RecyclerView) findViewById(R.id.search_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为线性布局
        search_recycler_view.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度或者宽度是固定的，设置这个选项可以提高性能
        search_recycler_view.setHasFixedSize(true);
        adapter=new keyAdapter();
         search_recycler_view.setAdapter(adapter);
        grid_layout= (GridLayout)findViewById(R.id.grid_layout);
        //设置RecyclerView的分割线
        search_recycler_view.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));


    }


    /**
     * 获得这个布局所以Button ID
     */
    private void getViewId() {
        Button b2 = (Button) findViewById(R.id.b1);
        b2.setOnClickListener(listener);
        ic_card = (Button) findViewById(R.id.ic_card);
        ic_card.setOnClickListener(key_id_query);
        btn_key_blank = (Button) findViewById(R.id.btn_key_blank);
        btn_key_blank.setOnClickListener(Key_Blank_Seach);
        etext = (EditText) findViewById(R.id.etext);
        etext.setOnClickListener(listener4);
        // A到Z的id
        btn_A = (Button) findViewById(R.id.btn_A);
        btn_A.setOnClickListener(listenerA);
        btn_B = (Button) findViewById(R.id.btn_B);
        btn_B.setOnClickListener(listenerB);
        btn_C = (Button) findViewById(R.id.btn_C);
        btn_C.setOnClickListener(listenerC);
        btn_D = (Button) findViewById(R.id.btn_D);
        btn_D.setOnClickListener(listenerD);
        btn_E = (Button) findViewById(R.id.btn_E);
        btn_E.setOnClickListener(listenerE);
        btn_F = (Button) findViewById(R.id.btn_F);
        btn_F.setOnClickListener(listenerF);
        btn_G = (Button) findViewById(R.id.btn_G);
        btn_G.setOnClickListener(listenerG);
        btn_H = (Button) findViewById(R.id.btn_H);
        btn_H.setOnClickListener(listenerH);
        btn_I = (Button) findViewById(R.id.btn_I);
        btn_I.setOnClickListener(listenerI);
        btn_J = (Button) findViewById(R.id.btn_J);
        btn_J.setOnClickListener(listenerJ);
        btn_K = (Button) findViewById(R.id.btn_K);
        btn_K.setOnClickListener(listenerK);
        btn_L = (Button) findViewById(R.id.btn_L);
        btn_L.setOnClickListener(listenerL);
        btn_M = (Button) findViewById(R.id.btn_M);
        btn_M.setOnClickListener(listenerM);
        btn_N = (Button) findViewById(R.id.btn_N);
        btn_N.setOnClickListener(listenerN);
        btn_O = (Button) findViewById(R.id.btn_O);
        btn_O.setOnClickListener(listenerO);
        btn_P = (Button) findViewById(R.id.btn_P);
        btn_P.setOnClickListener(listenerP);
        btn_Q = (Button) findViewById(R.id.btn_Q);
        btn_Q.setOnClickListener(listenerQ);
        btn_R = (Button) findViewById(R.id.btn_R);
        btn_R.setOnClickListener(listenerR);
        btn_S = (Button) findViewById(R.id.btn_S);
        btn_S.setOnClickListener(listenerS);
        btn_T = (Button) findViewById(R.id.btn_T);
        btn_T.setOnClickListener(listenerT);
        btn_U = (Button) findViewById(R.id.btn_U);
        btn_U.setOnClickListener(listenerU);
        btn_V = (Button) findViewById(R.id.btn_V);
        btn_V.setOnClickListener(listenerV);
        btn_W = (Button) findViewById(R.id.btn_W);
        btn_W.setOnClickListener(listenerW);
        btn_X = (Button) findViewById(R.id.btn_X);
        btn_X.setOnClickListener(listenerX);

        btn_Y = (Button) findViewById(R.id.btn_Y);
        btn_Y.setOnClickListener(listenerY);
        btn_Z = (Button) findViewById(R.id.btn_Z);
        btn_Z.setOnClickListener(listenerZ);
        //- _ , .
        btn_point = (Button) findViewById(R.id.btn_point);
        btn_point.setOnClickListener(listenerPoint);
        btn_ = (Button) findViewById(R.id.btn_);
        btn_.setOnClickListener(listener_);
        btn_minus = (Button) findViewById(R.id.btn_minus);
        btn_minus.setOnClickListener(listenerMinus);

        btn_comma = (Button) findViewById(R.id.btn_comma);
        btn_comma.setOnClickListener(listenerComma);

        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(ClearContent);

        btn_move = (Button) findViewById(R.id.btn_move);
        btn_move.setOnClickListener(listenerMove);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        btn_remove.setOnClickListener(listenerRemove);
        //数字id
        btn_1 = (Button) findViewById(R.id.btn_1);
        btn_1.setOnClickListener(listener001);
        btn_2 = (Button) findViewById(R.id.btn_2);
        btn_2.setOnClickListener(listener002);
        btn_3 = (Button) findViewById(R.id.btn_3);
        btn_3.setOnClickListener(listener003);
        btn_4 = (Button) findViewById(R.id.btn_4);
        btn_4.setOnClickListener(listener004);
        btn_5 = (Button) findViewById(R.id.btn_5);
        btn_5.setOnClickListener(listener005);
        btn_6 = (Button) findViewById(R.id.btn_6);
        btn_6.setOnClickListener(listener006);
        btn_7 = (Button) findViewById(R.id.btn_7);
        btn_7.setOnClickListener(listener007);
        btn_8 = (Button) findViewById(R.id.btn_8);
        btn_8.setOnClickListener(listener008);
        btn_9 = (Button) findViewById(R.id.btn_9);
        btn_9.setOnClickListener(listener009);
        btn_0 = (Button) findViewById(R.id.btn_0);
        btn_0.setOnClickListener(listener000);
        //得到文本的实例
        inputText= (TextView)findViewById(R.id.inputText);
        SearchMode=(TextView)findViewById(R.id.searchMode);
        Button Search=(Button)findViewById(R.id.search);
        //搜索数据
         Search.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {


                 if(str.toString().length()==0){
                     return;
                 }else if(SEARCH_STATUS==0){

                     //获得文本框的值
                     inputText.setText(etext.getText().toString());
                     SQLiteDao dao = new SQLiteDao(frmKeySelect.this);
                     condition_name=str.toString();
                     adapter.sendData(dao.KeyBlankMfgByKeyBlankName(str.toString()),inputText.getText().toString(),frmKeySelect.this,0);
                     adapter.notifyDataSetChanged();
                 }else  if(SEARCH_STATUS==1){


                 }



             }
         });

        //返回按钮
       btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDao dao = new SQLiteDao(frmKeySelect.this);
                inputText.setText(condition_name);
                adapter.sendData(dao.KeyBlankMfgByKeyBlankName(condition_name),condition_name,frmKeySelect.this,0);
                adapter.notifyDataSetChanged();
                 btn_back.setVisibility(View.GONE);

            }
        });


    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            finish();
        }
    };
    View.OnClickListener key_id_query= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            btn_key_blank.setBackgroundResource(R.drawable.keysearch_searchiccardimage);
            etext.setHint("IC Card");
            SearchMode.setText("IC Card Search");
            ic_card.setBackgroundResource(R.drawable.keysearch_searchkeyblankpressedimage);
            SEARCH_STATUS=1;
            str.delete(0,str.length());
            etext.setText("");
            Idquery(grid_layout);
        }
    };
    View.OnClickListener Key_Blank_Seach = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            btn_key_blank.setBackgroundResource(R.drawable.keysearch_searchkeyblankpressedimage);
            ic_card.setBackgroundResource(R.drawable.keysearch_searchiccardimage);
            //0 代表 钥匙坯搜索
            SEARCH_STATUS=0;
            etext.setHint("Key Blank");
            SearchMode.setText("Key Blank Search");
            handler.sendEmptyMessage(2);
        }
    };
    //清空所以item
    View.OnClickListener ClearContent= new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            etext.setText("");
            int sb_length = str.length();
            str.delete(0, sb_length);
            Btninit(grid_layout);

                //删除Recyclerview里面的所以view
                search_recycler_view.removeAllViews();
                //清空list集合所以数据 释放内存
               // list_blank_mfg.clear();
                adapter.mlist.clear();
                //重新加载Recyclerview
                adapter.notifyDataSetChanged();
                //隐藏返回按钮
                btn_back.setVisibility(View.GONE);
                inputText.setText("");






        }
    };
    View.OnClickListener listener4 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            //etext.clearFocus();
            //强制隐藏EditText自带的输入法键盘
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };
    View.OnClickListener listenerA = new View.OnClickListener() {

        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_A.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);

        }
    };
    private  void Btninit(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button newDtv = (Button) view;

                if (newDtv.getId() == R.id.btn_clear || newDtv.getId() == R.id.btn_remove || newDtv.getId() == R.id.btn_move || newDtv.getId() == R.id.search) {

                } else if(newDtv.getId() != R.id.btn_clear && newDtv.getId() != R.id.btn_remove && newDtv.getId() != R.id.btn_move && newDtv.getId() != R.id.search)  {
                    newDtv.setBackgroundColor(Color.parseColor("#16A4FA"));
                    newDtv.setTextColor(Color.parseColor("#ffffff"));
                    newDtv.setEnabled(true);
                }
            } else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.TraverseBtn((ViewGroup) view);
            }
        }
    }
    private  void  Idquery(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button newDtv = (Button) view;
                if(newDtv.getText().toString().matches("[0-9]")){
                    newDtv.setBackgroundColor(Color.parseColor("#16A4FA"));
                    newDtv.setTextColor(Color.parseColor("#ffffff"));
                    newDtv.setEnabled(true);
                }else if(newDtv.getText().toString().matches("[A-Z.-_,]")){
                    newDtv.setBackgroundColor(Color.parseColor("#50587C"));
                    newDtv.setTextColor(Color.parseColor("#818285"));
                    newDtv.setEnabled(false);
                }
            } else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.TraverseBtn((ViewGroup) view);
            }
        }
    }
    private  void  TraverseBtn(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button newDtv = (Button) view;
                if(str.toString().length()==0){
                    if(newDtv.getId()==R.id.btn_clear||newDtv.getId()==R.id.btn_remove||newDtv.getId()==R.id.btn_move||newDtv.getId()==R.id.search){

                    }else {
                        newDtv.setBackgroundColor(Color.parseColor("#16A4FA"));
                        newDtv.setTextColor(Color.parseColor("#ffffff"));
                        newDtv.setEnabled(true);
                        continue;
                    }
                }
                if(newDtv.getId()==R.id.btn_clear||newDtv.getId()==R.id.btn_remove||newDtv.getId()==R.id.btn_move||newDtv.getId()==R.id.search){

                }else {
                   if(!strTempList.contains(newDtv.getText().toString())){
                       newDtv.setBackgroundColor(Color.parseColor("#50587C"));
                       newDtv.setTextColor(Color.parseColor("#818285"));
                       newDtv.setEnabled(false);
                    } else{
                       newDtv.setBackgroundColor(Color.parseColor("#16A4FA"));
                       newDtv.setTextColor(Color.parseColor("#ffffff"));
                       newDtv.setEnabled(true);
                   }

                }

            }else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.TraverseBtn((ViewGroup) view);
            }

        }
        strTempList="";
    }

    private  void  DataSearch(){
        if(str.toString().length()==0){
            return;
        }

        SQLiteDao dao = new SQLiteDao(frmKeySelect.this);
        mlist = dao.firstQuery(str.toString());
        if(mlist==null){
            return;
        }
        strTempList ="";
        for (int i = 0; i < mlist.size(); i++) {
            String name = (String) mlist.get(i);
            if (str.toString().length() <name.length())
            {
                Log.d("名字", "DataSearch: "+name);
                String s2 = name.charAt(str.toString().length()) + "";//把char型转为字符。
                strTempList+=s2;
            }
        }
        Log.d("数据", "DataSearch: "+strTempList);
        //清空内存
        mlist.clear();

    }


    View.OnClickListener listenerB = new View.OnClickListener() {

        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_B.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };

    View.OnClickListener listenerC = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_C.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerD = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_D.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };

    View.OnClickListener listenerE = new View.OnClickListener() {

        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_E.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerF = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_F.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerG = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_G.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerH = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_H.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerI = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_I.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerJ = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_J.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerK = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_K.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerL = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_L.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerM = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_M.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerN = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_N.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerO = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_O.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerP = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_P.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerQ = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_Q.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerR = new View.OnClickListener() {
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_R.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerS = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_S.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerT = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_T.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerU = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_U.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerV = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_V.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerW = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_W.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerX = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_X.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerY = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_Y.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerZ = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_Z.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerMinus = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_minus.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerPoint = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_point.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerComma = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_comma.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener_ = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listenerMove = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            if (etext.getSelectionStart() < str.length()) {
                etext.setSelection(etext.getSelectionStart() + 1);
            } else {
                Toast.makeText(frmKeySelect.this, "光标越界", Toast.LENGTH_SHORT).show();
            }


        }
    };
    View.OnClickListener listenerRemove = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            String ete = etext.getText().toString();
            if (ete.length() > 0) {
                //删除StringBuffer最后一个字符
                str.deleteCharAt(str.length() - 1);
                Log.d("到底有多少？", "onClick: "+str.toString());

                etext.setText(ete.substring(0, ete.length() - 1));
                etext.setSelection(etext.getText().length());
                Log.d("数据", "onClick: "+strTempList );
                //搜索数据
                DataSearch();
                //循环所以btn按钮
                TraverseBtn(grid_layout);

            }

        }
    };
    View.OnClickListener listener001 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_1.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener002 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_2.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener003 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_3.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener004 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_4.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener005 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_5.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener006 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_6.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener007 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_7.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener008 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_8.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener009 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_9.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };
    View.OnClickListener listener000 = new View.OnClickListener() {

        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            str.append(btn_0.getText().toString());
            etext.setText(str);
            etext.setSelection(str.toString().length());
            DataSearch();
            TraverseBtn(grid_layout);
        }
    };

}




