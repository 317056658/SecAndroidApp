package com.kkkcut.www.myapplicationkukai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.dao.CodeToothNumberDaoManager;
import com.kkkcut.www.myapplicationkukai.dao.KeyInfoDaoManager;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class CodeFindToothActivity extends AppCompatActivity implements View.OnClickListener{
    private   EditText mEtCodeInput,mEtShowBitting;
   private InputMethodManager imm;
    private   Button mBtnA, mBtnB, mBtnC, mBtnD, mBtnE, mBtnF, mBtnG, mBtnH, mBtnI, mBtnJ, mBtnK, mBtnL, mBtnM, mBtnN, mBtnO, mBtnP, mBtnQ, mBtnR, mBtnS, mBtnT,
            mBtnU, mBtnV, mBtnW, mBtnX, mBtnY, mBtnZ, mBtnBar,mbtnPoint, mBtn1, mBtn2, mBtn3, mBtn4, mBtn5, mBtn6, mBtn7, mBtn8, mBtn9, mBtn_, mBtn0, mBtnComma;
    private  Button mBtnClear, mBtnNext, mBtnRemove, mBtnSearch;
    private View mDecorView;
    private String title;  //标题
    private String cardNumber;
    private KeyInfoDaoManager keyInfoDaoManager;
    private CodeToothNumberDaoManager codeToothNumberDaoManager;
    private  String toothCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_check_tooth);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //获得传过来的意图数据
        getIntentData();
        //获得数据库管理者
        codeToothNumberDaoManager=CodeToothNumberDaoManager.getInstance();
        keyInfoDaoManager=KeyInfoDaoManager.getInstance();
        //初始化view
        initViews();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(mDecorView!=null){
            Tools.hideBottomUIMenu(mDecorView);
        }
    }

    public  void initViews(){
        TextView tvTitle=(TextView)findViewById(R.id.tv_title);
        //设置标题
        tvTitle.setText(title);
        cardNumber=title.substring(title.lastIndexOf(":")+1,title.length());
        Button closeActivity =(Button)findViewById(R.id.btn_close_activity);
        closeActivity.setOnClickListener(this);
        mEtCodeInput =(EditText)findViewById(R.id.et_code_input);
        Tools.disableShowSoftInput(mEtCodeInput);
        Tools.disableShearPlate(mEtCodeInput);
      String codeSeries  =keyInfoDaoManager.queryCodeSeriesById(cardNumber);
     TextView tvSeriesContent=  (TextView)findViewById(R.id.tv_series_content);
        //代码系列
        tvSeriesContent.setText(codeSeries);
        mDecorView=getWindow().getDecorView();
        mBtnA = (Button) findViewById(R.id.btnA);
        mBtnA.setOnClickListener(letterFigureClickListener);

        mBtnB = (Button) findViewById(R.id.btnB);
        mBtnB.setOnClickListener(letterFigureClickListener);

        mBtnC = (Button) findViewById(R.id.btnC);
        mBtnC.setOnClickListener(letterFigureClickListener);

        mBtnD = (Button) findViewById(R.id.btnD);
        mBtnD.setOnClickListener(letterFigureClickListener);

        mBtnE = (Button) findViewById(R.id.btnE);
        mBtnE.setOnClickListener(letterFigureClickListener);

        mBtnF = (Button) findViewById(R.id.btnF);
        mBtnF.setOnClickListener(letterFigureClickListener);

        mBtnG = (Button) findViewById(R.id.btnG);
        mBtnG.setOnClickListener(letterFigureClickListener);

        mBtnH = (Button) findViewById(R.id.btnH);
        mBtnH.setOnClickListener(letterFigureClickListener);

        mBtnI = (Button) findViewById(R.id.btnI);
        mBtnI.setOnClickListener(letterFigureClickListener);

        mBtnJ = (Button) findViewById(R.id.btnJ);
        mBtnJ.setOnClickListener(letterFigureClickListener);

        mBtnK = (Button) findViewById(R.id.btnK);
        mBtnK.setOnClickListener(letterFigureClickListener);

        mBtnL = (Button) findViewById(R.id.btnL);
        mBtnL.setOnClickListener(letterFigureClickListener);

        mBtnM = (Button) findViewById(R.id.btnM);
        mBtnM.setOnClickListener(letterFigureClickListener);

        mBtnN = (Button) findViewById(R.id.btnN);
        mBtnN.setOnClickListener(letterFigureClickListener);

        mBtnO = (Button) findViewById(R.id.btnO);
        mBtnO.setOnClickListener(letterFigureClickListener);

        mBtnP = (Button) findViewById(R.id.btnP);
        mBtnP.setOnClickListener(letterFigureClickListener);

        mBtnQ = (Button) findViewById(R.id.btnQ);
        mBtnQ.setOnClickListener(letterFigureClickListener);

        mBtnR = (Button) findViewById(R.id.btnR);
        mBtnR.setOnClickListener(letterFigureClickListener);

        mBtnS = (Button) findViewById(R.id.btnS);
        mBtnS.setOnClickListener(letterFigureClickListener);

        mBtnT = (Button) findViewById(R.id.btnT);
        mBtnT.setOnClickListener(letterFigureClickListener);

        mBtnU = (Button) findViewById(R.id.btnU);
        mBtnU.setOnClickListener(letterFigureClickListener);

        mBtnV = (Button) findViewById(R.id.btnV);
        mBtnV.setOnClickListener(letterFigureClickListener);

        mBtnW = (Button) findViewById(R.id.btnW);
        mBtnW.setOnClickListener(letterFigureClickListener);

        mBtnX = (Button) findViewById(R.id.btnX);
        mBtnX.setOnClickListener(letterFigureClickListener);

        mBtnY = (Button) findViewById(R.id.btnY);
        mBtnY.setOnClickListener(letterFigureClickListener);

        mBtnZ = (Button) findViewById(R.id.btnZ);
        mBtnZ.setOnClickListener(letterFigureClickListener);

        mBtnBar = (Button) findViewById(R.id.btnBar);
        mBtnBar.setOnClickListener(letterFigureClickListener);

        mbtnPoint= (Button) findViewById(R.id.btnPoint);
        mbtnPoint.setOnClickListener(letterFigureClickListener);

        mBtn1 = (Button) findViewById(R.id.btn1);
        mBtn1.setOnClickListener(letterFigureClickListener);

        mBtn2 = (Button) findViewById(R.id.btn_2);
        mBtn2.setOnClickListener(letterFigureClickListener);

        mBtn3 = (Button) findViewById(R.id.btn_3);
        mBtn3.setOnClickListener(letterFigureClickListener);

        mBtn4 = (Button) findViewById(R.id.btn_4);
        mBtn4.setOnClickListener(letterFigureClickListener);

        mBtn5 = (Button) findViewById(R.id.btn_5);
        mBtn5.setOnClickListener(letterFigureClickListener);

        mBtn6 = (Button) findViewById(R.id.btn_6);
        mBtn6.setOnClickListener(letterFigureClickListener);

        mBtn7 = (Button) findViewById(R.id.btn_7);
        mBtn7.setOnClickListener(letterFigureClickListener);

        mBtn8 = (Button) findViewById(R.id.btn_8);
        mBtn8.setOnClickListener(letterFigureClickListener);

        mBtn9 = (Button) findViewById(R.id.btn_9);
        mBtn9.setOnClickListener(letterFigureClickListener);

        mBtn_ = (Button) findViewById(R.id.btn_);
        mBtn_.setOnClickListener(letterFigureClickListener);

        mBtn0 = (Button) findViewById(R.id.btn_0);
        mBtn0.setOnClickListener(letterFigureClickListener);

        mBtnComma = (Button) findViewById(R.id.btnComma);
        mBtnComma.setOnClickListener(letterFigureClickListener);

        mBtnClear = (Button) findViewById(R.id.btnClear);
        mBtnClear.setOnClickListener(this);

        mBtnRemove = (Button) findViewById(R.id.btnRemove);
        mBtnRemove.setOnClickListener(this);

        mBtnNext = (Button) findViewById(R.id.btnNext);
        mBtnNext.setOnClickListener(this);

        mBtnSearch = (Button) findViewById(R.id.btn_search);
        mBtnSearch.setOnClickListener(this);
        mEtShowBitting=(EditText)findViewById(R.id.et_show_bitting);
       Button  backData= (Button)findViewById(R.id.btn_back_data);
        backData.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnRemove:
                if(mEtCodeInput.getSelectionStart()!=0) {
                    mEtCodeInput.getText().delete(mEtCodeInput.getSelectionStart() - 1, mEtCodeInput.getSelectionStart());
                }
                return;
            case R.id.btnClear:
                mEtCodeInput.getText().clear(); //清空所有内容
                return;
            case R.id.btnNext:
                if(mEtCodeInput.getSelectionStart()!= mEtCodeInput.getText().length()){
                    mEtCodeInput.setSelection(mEtCodeInput.getSelectionStart()+1);
                }
                return;
            case  R.id.btn_search:
             toothCode =codeToothNumberDaoManager.queryBittingByCode(mEtCodeInput.getText().toString().trim());
               if(TextUtils.isEmpty(toothCode)){
                    MessageTipsActivity.startItselfActivity(this,MessageTipsActivity.NOT_FIND_TOOTH_CODE,-1);
                   mEtCodeInput.getText().clear();
                }else {
                   mEtShowBitting.setText(toothCode);
                }
                return;
            case R.id.btn_close_activity:
                finish();
                break;
            case R.id.btn_back_data:  //返回数据
                if(!TextUtils.isEmpty(toothCode)){
                    char[]  chars  =toothCode.toCharArray();
                    for (int i = 0; i <chars.length ; i++) {
                        Log.d("输出", "onClick: "+chars[i]);
                    }
                }
//                EventBusUtils.post(new MessageEvent(getAnalysisDepthNameData(), MessageEvent.CHANGE_KEY_TOOTH_CODE));
//                finish();//活动销毁
                break;

       }
    }

    /**
     * 字母,数字按钮点击监听器
     */
    View.OnClickListener letterFigureClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn= (Button)findViewById(v.getId());
            mEtCodeInput.getText().insert(mEtCodeInput.getSelectionStart(),btn.getText());
        }
    };

    /**
     *  启动它本身Activity
     */
    public  static  void startItselfActivity(Context context,String str){
        Intent intent=new Intent(context,CodeFindToothActivity.class);
         intent.putExtra("title",str);
        context.startActivity(intent);
    }

    /**
     * 获得意图数据
     */
     private void getIntentData(){
            Intent intent=getIntent();
        title=intent.getStringExtra("title");
    }

}
