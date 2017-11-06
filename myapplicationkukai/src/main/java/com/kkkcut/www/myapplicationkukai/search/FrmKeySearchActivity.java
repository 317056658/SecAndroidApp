package com.kkkcut.www.myapplicationkukai.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.adapter.KeyBlankAdapter;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteKeyDao;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.TransformProbeActivity;
import com.kkkcut.www.myapplicationkukai.entity.DividerItemDecoration;
import com.kkkcut.www.myapplicationkukai.entity.KeyBlankMfg;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.publicKeyCut.FrmKeyCutMainActivity;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FrmKeySearchActivity extends AppCompatActivity {
   private Button mBtnKeyBlank, mBtnClear, mBtnMove, mBtnRemove;
   private Button mBtnA, mBtnB, mBtnC, mBtnD, mBtnE, mBtnF, mBtnG, mBtnH, mBtnI, mBtnJ, mBtnK, mBtnL, mBtnM, mBtnN, mBtnO, mBtnP, mBtnQ, mBtnR, mBtnS, mBtnT,
            mBtnU, mBtnV, mBtnW, mBtnX, mBtnY, mBtnZ, mBtnMinus, mBtnPoint, mBtn_, mBtnComma;
   private Button mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9, mBtn0;

    private List mList = new ArrayList<>();
   private EditText mEtInput;
   private Button IcCard;
    private  TextView mInputText, mSearchMode;
    private String tempStr;
    private KeyBlankAdapter adapter;
    private GridLayout mDl;
    private RecyclerView mSearchRecyclerView;
    private List kbmList;
    private List kiList;
    private int SEARCH_STATUS = 0;
    private   Button mBtnBack;
    private View mDecorView;
    private Editable editable;
    private SQLiteKeyDao database;
    private int dataLoadingType;
    private String  stepText;
    private HashMap<String,String> languageMap;
    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }
        Intent intent;
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrmKeySearchActivity activity = (FrmKeySearchActivity) reference.get();
            if(activity != null){
                String data;
                  if(msg.obj!=null){
                      data=msg.obj.toString();
                  }else {
                      data="";
                  }
                if(data.equals(MicyocoEvent.CUT_KNIFE_SWITCHOVER_PROBE)){ //切割刀换探针
                    intent =new Intent(activity,TransformProbeActivity.class);
                    activity.startActivity(intent);
                }
            }
        }
    }
    private ProlificSerialDriver serialDriver;
    private Context mContext;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext=this;
        getIntentData();
        initViews();//初始化View
//        serialDriver=ProlificSerialDriver.getInstance();
        database = new SQLiteKeyDao(this,"SEC1.db");
        initRecyclerView();
    }

    /**
     * 启动本Activity
     * @param context
     * @param languageMap
     */
    public  static  void startFrmKeySelectActivity(Context context,HashMap<String,String> languageMap){
        Intent intent=new Intent(context,FrmKeySearchActivity.class);
        intent.putExtra("language",languageMap);
        context.startActivity(intent);
    }

    /**
     * 获得传过来的意图数据
     */
    private  void getIntentData(){
        Intent intent=getIntent();
        languageMap  = (HashMap<String,String>) intent.getSerializableExtra("language");
    }

    /**
     * 初始化RecyclerView
     */
    private  void  initRecyclerView(){
        mDecorView = getWindow().getDecorView();
        mSearchRecyclerView = (RecyclerView) findViewById(R.id.search_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //设置为线性布局
        mSearchRecyclerView.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度或者宽度是固定的，设置这个选项可以提高性能
        mSearchRecyclerView.setHasFixedSize(true);
        adapter = new KeyBlankAdapter();
        mSearchRecyclerView.setAdapter(adapter);
        adapter.setClickListener(new KeyBlankAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(dataLoadingType ==1){  //等于1  加载钥匙坯钥匙信息
                    mBtnBack.setVisibility(View.VISIBLE);
                    KeyBlankMfg kbm=(KeyBlankMfg) kbmList.get(position);
                    String  manufacturerName  =kbm.getName();  //获得厂商名字
                    mInputText.setText(manufacturerName);
                    kiList =database.keyInfoByKeyBlank(editable.toString(),manufacturerName);
                    adapter.setData(kiList, dataLoadingType);
                    adapter.notifyDataSetChanged();//通知数据设置改变了，重新加载
                    dataLoadingType =2;
                    mBtnBack.setVisibility(View.VISIBLE);
                }else if(dataLoadingType ==2){  //等于2 获得钥匙基础信息 跳转Activity
                    KeyInfo ki  =(KeyInfo)kiList.get(position);
                    stepText=ki.getManufacturerName()+">"+ki.getCombinationName();
                    FrmKeyCutMainActivity.startFrmKeyCutMainActivity(FrmKeySearchActivity.this,ki,stepText,languageMap,2);
                }
            }
        });
        mDl = (GridLayout) findViewById(R.id.grid_layout);
        //设置RecyclerView的分割线
        mSearchRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
    }
    /**
     * 获得这个布局所以Button ID
     */
    private void initViews() {
        Button mHome = (Button) findViewById(R.id.btn_home);
        mHome.setOnClickListener(clickListener);
        IcCard = (Button) findViewById(R.id.ic_card);
        IcCard.setOnClickListener(clickListener);
        mBtnKeyBlank = (Button) findViewById(R.id.btn_key_blank);
        mBtnKeyBlank.setOnClickListener(clickListener);
        mEtInput = (EditText) findViewById(R.id.et_input);
        Tools.disableShowSoftInput(mEtInput);//禁止Edittext弹出软件盘，光标依然正常显示\
        Tools.disableShearPlate(mEtInput);//禁止Edittext弹出剪切板
        editable = mEtInput.getText();
        // A到Z的id
        mBtnA = (Button) findViewById(R.id.btn_A);
        mBtnA.setOnClickListener(clickListener);
        mBtnB = (Button) findViewById(R.id.btn_B);
        mBtnB.setOnClickListener(clickListener);
        mBtnC = (Button) findViewById(R.id.btn_C);
        mBtnC.setOnClickListener(clickListener);
        mBtnD = (Button) findViewById(R.id.btn_D);
        mBtnD.setOnClickListener(clickListener);
        mBtnE = (Button) findViewById(R.id.btn_E);
        mBtnE.setOnClickListener(clickListener);
        mBtnF = (Button) findViewById(R.id.btn_F);
        mBtnF.setOnClickListener(clickListener);
        mBtnG = (Button) findViewById(R.id.btn_G);
        mBtnG.setOnClickListener(clickListener);
        mBtnH = (Button) findViewById(R.id.btn_H);
        mBtnH.setOnClickListener(clickListener);
        mBtnI = (Button) findViewById(R.id.btn_I);
        mBtnI.setOnClickListener(clickListener);
        mBtnJ = (Button) findViewById(R.id.btn_J);
        mBtnJ.setOnClickListener(clickListener);
        mBtnK = (Button) findViewById(R.id.btn_K);
        mBtnK.setOnClickListener(clickListener);
        mBtnL = (Button) findViewById(R.id.btn_L);
        mBtnL.setOnClickListener(clickListener);
        mBtnM = (Button) findViewById(R.id.btn_M);
        mBtnM.setOnClickListener(clickListener);
        mBtnN = (Button) findViewById(R.id.btn_N);
        mBtnN.setOnClickListener(clickListener);
        mBtnO = (Button) findViewById(R.id.btn_O);
        mBtnO.setOnClickListener(clickListener);
        mBtnP = (Button) findViewById(R.id.btn_P);
        mBtnP.setOnClickListener(clickListener);
        mBtnQ = (Button) findViewById(R.id.btn_Q);
        mBtnQ.setOnClickListener(clickListener);
        mBtnR = (Button) findViewById(R.id.btn_R);
        mBtnR.setOnClickListener(clickListener);
        mBtnS = (Button) findViewById(R.id.btn_S);
        mBtnS.setOnClickListener(clickListener);
        mBtnT = (Button) findViewById(R.id.btn_T);
        mBtnT.setOnClickListener(clickListener);
        mBtnU = (Button) findViewById(R.id.btn_U);
        mBtnU.setOnClickListener(clickListener);
        mBtnV = (Button) findViewById(R.id.btn_V);
        mBtnV.setOnClickListener(clickListener);
        mBtnW = (Button) findViewById(R.id.btn_W);
        mBtnW.setOnClickListener(clickListener);
        mBtnX = (Button) findViewById(R.id.btn_X);
        mBtnX.setOnClickListener(clickListener);

        mBtnY = (Button) findViewById(R.id.btn_Y);
        mBtnY.setOnClickListener(clickListener);
        mBtnZ = (Button) findViewById(R.id.btn_Z);
        mBtnZ.setOnClickListener(clickListener);
        //- _ , .
        mBtnPoint = (Button) findViewById(R.id.btn_point);
        mBtnPoint.setOnClickListener(clickListener);
        mBtn_ = (Button) findViewById(R.id.btn_);
        mBtn_.setOnClickListener(clickListener);
        mBtnMinus = (Button) findViewById(R.id.btn_minus);
        mBtnMinus.setOnClickListener(clickListener);

        mBtnComma = (Button) findViewById(R.id.btn_comma);
        mBtnComma.setOnClickListener(clickListener);

        mBtnClear = (Button) findViewById(R.id.btn_clear);
        mBtnClear.setOnClickListener(clickListener);

        mBtnMove = (Button) findViewById(R.id.btn_move);
        mBtnMove.setOnClickListener(clickListener);
        mBtnRemove = (Button) findViewById(R.id.btn_remove);
        mBtnRemove.setOnClickListener(clickListener);
        //数字id
        mBtn1 = (Button) findViewById(R.id.btn_1);
        mBtn1.setOnClickListener(clickListener);
        mBtn2 = (Button) findViewById(R.id.btn_2);
        mBtn2.setOnClickListener(clickListener);
        mBtn3 = (Button) findViewById(R.id.btn_3);
        mBtn3.setOnClickListener(clickListener);
        mBtn4 = (Button) findViewById(R.id.btn_4);
        mBtn4.setOnClickListener(clickListener);
        mBtn5 = (Button) findViewById(R.id.btn_5);
        mBtn5.setOnClickListener(clickListener);
        mBtn6 = (Button) findViewById(R.id.btn_6);
        mBtn6.setOnClickListener(clickListener);
        mBtn7 = (Button) findViewById(R.id.btn_7);
        mBtn7.setOnClickListener(clickListener);
        mBtn8 = (Button) findViewById(R.id.btn_8);
        mBtn8.setOnClickListener(clickListener);
        mBtn9 = (Button) findViewById(R.id.btn_9);
        mBtn9.setOnClickListener(clickListener);
        mBtn0 = (Button) findViewById(R.id.btn_0);
        mBtn0.setOnClickListener(clickListener);
        //得到文本的实例
        mInputText = (TextView) findViewById(R.id.input_text);
        mSearchMode = (TextView) findViewById(R.id.search_mode);
        Button Search = (Button) findViewById(R.id.search);
        Search.setOnClickListener(clickListener);
        //返回按钮
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(clickListener);
        LinearLayout   mSearchLayout=(LinearLayout)findViewById(R.id.ll_search_layout);
        setAllViewTest(mSearchLayout);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(serialDriver!=null){
            serialDriver.setHandler(mHandler);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_home:
                    finish();
                    break;
                case R.id.ic_card:  //id 查询
                    mBtnKeyBlank.setBackgroundResource(R.drawable.keysearch_searchiccardimage);
                        if(languageMap.get("IC Card")!=null){
                            mEtInput.setHint(languageMap.get("IC Card"));
                            //设置搜索方式
                            mSearchMode.setText(languageMap.get("IC Card Search"));
                        }
                    IcCard.setBackgroundResource(R.drawable.keysearch_searchkeyblankpressedimage);
                    SEARCH_STATUS = 1;
                    editable.delete(0, editable.length());
                    IdQuery(mDl);
                    break;
                case R.id.btn_key_blank: //钥匙胚查询
                    mBtnKeyBlank.setBackgroundResource(R.drawable.keysearch_searchkeyblankpressedimage);
                    IcCard.setBackgroundResource(R.drawable.keysearch_searchiccardimage);
                    SEARCH_STATUS = 0;
                    if(languageMap.get("Key Blank")!=null){
                    mEtInput.setHint(languageMap.get("Key Blank"));
                    mSearchMode.setText(languageMap.get("Key Blank Search"));
                    }
                    editable.delete(0, editable.length());
                    recoverBtnClickable(mDl);
                    break;
                case R.id.btn_A:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnA.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_B:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnB.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_C:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnC.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_D:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnD.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_E:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnE.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_F:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnF.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_G:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnG.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_H:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnH.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_I:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnI.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_J:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnJ.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_K:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnK.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_L:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnL.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_M:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnM.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_N:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnN.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_O:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnO.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_P:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnP.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_Q:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnQ.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_R:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnR.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_S:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnS.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_T:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnT.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_U:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnU.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_V:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnV.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_W:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnW.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_X:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnX.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_Y:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnY.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_Z:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnZ.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn_.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_point:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnPoint.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_1:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn1.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_2:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn2.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_3:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn3.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_4:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn4.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_5:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn5.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_6:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn6.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_7:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn7.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_8:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn8.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_9:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn9.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_minus:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnMinus.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_0:
                    editable.insert(mEtInput.getSelectionEnd(), mBtn0.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_comma:
                    editable.insert(mEtInput.getSelectionEnd(), mBtnComma.getText());
                    dataSearch();
                    TraverseBtn(mDl);
                    break;
                case R.id.btn_clear:
                    editable.clear();//情况所有内容

                    if (SEARCH_STATUS == 0) {  //0 是 钥匙胚查询
                        dataLoadingType =0;
                        recoverBtnClickable(mDl);//回到 初始状态
                    }
                    //删除RecyclerView里面的所以view
                    mSearchRecyclerView.removeAllViews();
                    //清空list集合所以数据 释放内存
                    adapter.list.clear();
                    //重新加载RecyclerView
                    adapter.notifyDataSetChanged();
                    //隐藏返回按钮
                    mBtnBack.setVisibility(View.GONE);
                    mInputText.setText("");
                    break;
                case R.id.btn_remove:
                    if (mEtInput.getSelectionStart() == 0) {
                        editable.delete(0, editable.length());
                        return;
                    }
                    editable.delete(mEtInput.getSelectionStart() - 1, mEtInput.getSelectionStart());
                    if (SEARCH_STATUS == 0) { //钥匙胚插叙
                        dataSearch();
                        if (TextUtils.isEmpty(editable.toString())) {
                            recoverBtnClickable(mDl);//回到初始状态
                        } else {
                            TraverseBtn(mDl);
                        }
                    }
                    break;
                case R.id.btn_move:
                    if (editable.length() == mEtInput.getSelectionEnd()) {
                        return;
                    }
                    mEtInput.setSelection(mEtInput.getSelectionStart() + 1);
                    break;
                case R.id.search:
                    if (TextUtils.isEmpty(editable.toString())) {
                        return;
                    }
                    if (SEARCH_STATUS == 0) {   //钥匙胚查询
                        kbmList = database.KeyBlankMfgByKeyBlankName(editable.toString());  //根据钥匙胚型号查询厂商
                        if(kbmList.size()==0){
                            MessageTipsActivity.startMessageTipsActivity(mContext,MessageTipsActivity.UNFOUND_KEY_TIPS);
                        }else {
                            dataLoadingType =0;
                            mInputText.setText(editable.toString()+"～");
                            adapter.setData(kbmList, dataLoadingType);
                            dataLoadingType=1;
                            adapter.notifyDataSetChanged();//通知数据设置改变了，重新加载
                        }
                    } else if (SEARCH_STATUS == 1) {  //等于1就是  id 查询
                        //根据id查询   只会返回一条数据
                        KeyInfo ki = database.profileByID(editable.toString().trim());
                        if (ki.getSpace() != null) {
                            stepText ="IC Card "+ki.getId()+">";
                            FrmKeyCutMainActivity.startFrmKeyCutMainActivity(FrmKeySearchActivity.this,ki,stepText,languageMap,0);
                        } else {
                            //没有找到数据
                            Intent intent = new Intent(FrmKeySearchActivity.this, MessageTipsActivity.class);
                            intent.putExtra("Type",2);
                            startActivity(intent);
                        }
                    }
                    break;
                case R.id.btn_back:  //返回按钮
                    mInputText.setText(editable+"～");
                    dataLoadingType =0;
                    adapter.setData(kbmList, dataLoadingType);
                    adapter.notifyDataSetChanged();
                    mBtnBack.setVisibility(View.GONE);
                    dataLoadingType=1;
                    break;
            }
        }
    };
    /**
     * 恢复btn可以点击
     *
     * @param viewGroup
     */
    private void recoverBtnClickable(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button newDtv = (Button) view;
                if (!newDtv.isEnabled()) {
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

    /**
     * 切换成IC card 状态
     *
     * @param viewGroup
     */
    private void IdQuery(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button newDtv = (Button) view;
                if (newDtv.getText().toString().matches("[0-9]")) {
                    newDtv.setBackgroundColor(Color.parseColor("#16A4FA"));
                    newDtv.setTextColor(Color.parseColor("#ffffff"));
                    newDtv.setEnabled(true);
                } else if (newDtv.getText().toString().matches("[A-Z._,-]")) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
          if(resultCode==0&&requestCode==0){
              recoverBtnClickable(mDl);
          }
    }

    /**
     * 遍历所以 Button
     *
     * @param viewGroup
     */
    private void TraverseBtn(ViewGroup viewGroup) {
        if (SEARCH_STATUS == 1) {  //id不遍历
            return;
        }
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button newDtv = (Button) view;
                if (newDtv.getId() == R.id.btn_clear || newDtv.getId() == R.id.btn_remove || newDtv.getId() == R.id.btn_move || newDtv.getId() == R.id.search) {

                } else {
                    if (!tempStr.contains(newDtv.getText().toString())) {//将没有的字符全部设置为不可以点击
                        newDtv.setBackgroundColor(Color.parseColor("#50587C"));
                        newDtv.setTextColor(Color.parseColor("#818285"));
                        newDtv.setEnabled(false);
                    } else {                                     //将有的字符全部设置为可以编辑
                        newDtv.setBackgroundColor(Color.parseColor("#16A4FA"));
                        newDtv.setTextColor(Color.parseColor("#ffffff"));
                        newDtv.setEnabled(true);
                    }
                }
            } else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.TraverseBtn((ViewGroup) view);
            }
        }
        tempStr = "";
    }

    /**
     *  设置全部view的setTest
     * @param viewGroup
     */

    private void setAllViewTest(ViewGroup viewGroup){
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof TextView) { // 若是Button记录下
                 TextView  textView   =(TextView)view;
                if(!TextUtils.isEmpty(languageMap.get(textView.getText().toString()))){
                    textView.setText(languageMap.get(textView.getText().toString()));
                }
                if(!TextUtils.isEmpty(textView.getHint())){
                    if(!TextUtils.isEmpty(languageMap.get(textView.getHint().toString()))){
                        textView.setHint(languageMap.get(textView.getHint().toString()));
                    }
                }
            } else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.setAllViewTest((ViewGroup) view);
            }
        }
    }

    /**
     * 寻找下一个字符
     */
    private void dataSearch() {
        if (TextUtils.isEmpty(editable.toString()) || SEARCH_STATUS == 1) {
            return;
        }
        mList = database.firstQuery(editable.toString());
        if (mList == null) {
            return;
        }
        tempStr = "";
        for (int i = 0; i < mList.size(); i++) {
            String name = (String) mList.get(i);
            if (editable.length() < name.length()) {
                String s2 = name.charAt(editable.length()) + "";//把char型转为字符。
                tempStr += s2;
            }
        }
        //清空内存
        mList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}




