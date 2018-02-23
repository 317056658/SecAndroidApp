package com.kkkcut.www.myapplicationkukai.publicKeyCut;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.MenuModule.KeyBaseInfoActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.dao.KeyInfoDaoManager;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ProbeAndCutterMeasurementActivity;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.AngleKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.BilateralKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.ConcaveDotKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.CylinderKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.DualPathInsideGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.DualPathOuterGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.Key;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.MonorailInsideGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.MonorailOuterGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.SideToothKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.UnilateralKey;
import com.kkkcut.www.myapplicationkukai.entity.ConstantValue;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.KeyType;
import com.kkkcut.www.myapplicationkukai.entity.MessageEvent;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.publicActivity.CodeFindToothActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.CutKeyActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.DecodeKeyActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.InputMainActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.LackToothFindActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.ToothFindCodeActivity;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.CacheActivityUtils;
import com.kkkcut.www.myapplicationkukai.utils.DensityUtils;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class FrmKeyCutMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnMenu, mChangeDepth, mCodeFindKey, mDecodeKey, mToothFindCode, mLackToothFind, mKeyCut, mBack, mMeasure;
    private String title,keyBlanks;
    private TextView  mDecodeName, mHint;
    private KeyInfo ki;
    private View rootView;
    public int isShowMenu = 0;
    private PopupWindow mMenuWindow;
    private PopupWindow mSelectKeySpaceWindow;  //选择几个齿位窗口
    private View mDecorView;
    private Key mKey;
    private boolean isShowKeySpaceSelectWindow;  //只第一次进来显示
    private LinearLayout mVariableCount;
    private TextView mTvStep;
    private String[] strArray;
    private TextView mTvKeyBlanksContent;
    private MyHandler mHandler = new MyHandler(this);
    private static  final  String TAG="FrmKeyCutMainActivity";
    private  final int  KEY_CATEGORY_SEARCH=1;     //钥匙种类 比如汽车钥匙，摩托车钥匙等等。
    private  final int  KEY_BLANKS_SEARCH=2;      //钥匙胚搜索
    private String stepText;
    private HashMap<String,String> languageMap;
    private int   startFlag;
    private ProlificSerialDriver serialDriver;
    private Context mContext;
    private  Intent intent;
    private int width, height;

    /**
     *  本Activity按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

    }

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FrmKeyCutMainActivity activity=(FrmKeyCutMainActivity)reference.get();
            String data="";
            if(msg.obj!=null){
                data=msg.obj.toString();
            }
            switch (msg.what){
                case MicyocoEvent.OPERATION_FINISH: //操作完成
                    if(!activity.mMeasure.isClickable()){
                        activity.mMeasure.setClickable(true);
                        activity.mMeasure.setTextColor(Color.parseColor("#000000"));
                        activity.mMenuWindow.dismiss();
                        activity.isShowMenu = 0;
                    }
                    break;
                case MicyocoEvent.USER_CANCEL_OPERATE://用户操作取消
                    if(!activity.mMeasure.isClickable()){
                        activity.mMeasure.setClickable(true);
                        activity.mMeasure.setTextColor(Color.parseColor("#000000"));
                        activity.mMenuWindow.dismiss();
                        Intent exceptionIntent=new Intent(activity, ExceptionActivity.class);
                        exceptionIntent.putExtra("exception",data);
                        activity.startActivity(exceptionIntent);
                    }
                    break;
            }
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_key_cut);
        CacheActivityUtils.addActivity(this);
        mContext =this;
        this.getIntentData();
        this.temporaryGlobalSave();
        EventBusUtils.register(this);  //注册EventBus
        this.initPopupWindow();
        this.initViews();
        this.setKeyBlanksInformationAndStep(startFlag);  //设置钥匙胚信息和步骤
        //初始化按钮
        //获得线性布局
        LinearLayout mPutImg = (LinearLayout) findViewById(R.id.ll_put_image);
                switch (ki.getType()) {
                    case KeyType.UNILATERAL_KEY://加载单边钥匙
                        mKey = new UnilateralKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(680,285));
                        mKey.setDrawPatternSize(660,280);
                        mKey.setToothCode(ki.getKeyToothCode());
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.BILATERAL_KEY:  //加载双边钥匙
                        mKey = new BilateralKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(715,310));
                        mKey.setDrawPatternSize(700,290);
                        mKey.setToothCode(ki.getKeyToothCode());
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.DUAL_PATH_INSIDE_GROOVE_KEY:  //加载双轨内槽钥匙
                        mKey = new DualPathInsideGrooveKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,640), DensityUtils.dip2px(this,250)));
                        mKey.setDrawPatternSize(DensityUtils.dip2px(this,630),DensityUtils.dip2px(this,245));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mKey.setToothCode(ki.getKeyToothCode());
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.MONORAIL_OUTER_GROOVE_KEY:  //加载单轨外槽
                        mKey = new MonorailOuterGrooveKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,710),DensityUtils.dip2px(this,300)));
                        mKey.setDrawPatternSize(DensityUtils.dip2px(this,680),DensityUtils.dip2px(this,230));
                        mKey.setToothCode(ki.getKeyToothCode());
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.DUAL_PATH_OUTER_GROOVE_KEY:  //加载双轨外槽钥匙
                        mKey = new DualPathOuterGrooveKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620), DensityUtils.dip2px(this,216)));
                        mKey.setDrawPatternSize(DensityUtils.dip2px(this,610),DensityUtils.dip2px(this,215));
                        mKey.setToothCode(ki.getKeyToothCode());
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.MONORAIL_INSIDE_GROOVE_KEY: //加载单轨内槽钥匙
                            mKey = new MonorailInsideGrooveKey(this,ki);
                            mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 300));
                            mKey.setDrawPatternSize(736,296);
                            mKey.setToothCode(ki.getKeyToothCode());
                            mKey.setBackgroundResource(R.drawable.edit_shape);
                            mPutImg.addView(mKey);
                        break;
                    case KeyType.CONCAVE_DOT_KEY:  //加载凹点钥匙
                        String[] newStr=ki.getRow_pos().split(";");
                        if(ki.getRowCount()==1&&Integer.parseInt(newStr[0])<0){
                            mKey = new ConcaveDotKey(this,ki);
                            LinearLayout.LayoutParams layoutParam=new LinearLayout.LayoutParams(DensityUtils.dip2px(this,550), DensityUtils.dip2px(this,80));
                            layoutParam.setMargins(0,DensityUtils.dip2px(this,90),0,0);
                            mKey.setLayoutParams(layoutParam);
                            mKey.setOnlyDrawSidePatternSize(DensityUtils.dip2px(this,548),DensityUtils.dip2px(this,78));
                            mKey.setDrawBigCircleAndInnerCircleSize(14,6);
                            mKey.setToothCode(ki.getKeyToothCode());
                            mKey.setBackgroundResource(R.drawable.edit_shape);
                            mPutImg.addView(mKey);
                        }else {
                            mKey = new ConcaveDotKey(this,ki);
                            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620), DensityUtils.dip2px(this,270)));
                            mKey.setDrawPatternSize(DensityUtils.dip2px(this,618),DensityUtils.dip2px(this,266));
                            mKey.setDrawBigCircleAndInnerCircleSize(14,6);
                            mKey.setToothCode(ki.getKeyToothCode());
                            mKey.setBackgroundResource(R.drawable.edit_shape);
                            mPutImg.addView(mKey);
                        }
                        break;
                    case KeyType.ANGLE_KEY: //角度钥匙
                        LinearLayout.LayoutParams layoutParam=new LinearLayout.LayoutParams(740,240 );
                        layoutParam.setMargins(0,DensityUtils.dip2px(this,60),0,0);
                        mKey =new AngleKey(this,ki);
                        mKey.setLayoutParams(layoutParam);
                        mKey.setDrawPatternSize(738,238);
                        mKey.setToothCode(ki.getKeyToothCode());
                        mPutImg.addView(mKey);
                        mDecodeKey.setVisibility(View.INVISIBLE);  //隐藏
                        break;
                    case KeyType.CYLINDER_KEY: //圆筒钥匙
                        mKey =new CylinderKey(this,ki);
                        LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(305,305);
                        layoutParams.gravity=Gravity.CENTER;
                        mKey.setLayoutParams(layoutParams);
                        mKey.setDrawPatternSize(300,300);
                        mKey.setToothCode(ki.getKeyToothCode());
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.SIDE_TOOTH_KEY:  //侧齿钥匙
                        mKey =new SideToothKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740,250));
                        mKey.setDrawPatternSize(730,248);
                        mKey.setToothCode(ki.getKeyToothCode());
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        mDecodeKey.setVisibility(View.INVISIBLE);  //隐藏
                        break;
                }
                if(ki.getVariableSpace()!=null&&ki.isVariableSpace()==false){
                    strArray = ki.getVariableSpace().split(",");
                    isShowKeySpaceSelectWindow = true;  //只第一次进去
                }
        //判断有不有描述。有描述就打开一个窗口提示
        if (!TextUtils.isEmpty(ki.getDesc())) {
            MessageTipsActivity.startMessageTipsActivity(this,MessageTipsActivity.KEY_SPECIAL_DESCRIPTION_TIPS,ki);
        }
    }

    /**
     * 临时全局保存
     */
    private  void temporaryGlobalSave(){
        MyApplication.ki=this.ki;
        MyApplication.stepText=this.stepText;
        MyApplication.startFlag=this.startFlag;
    }
    /**
     * 根据启动标记 设置钥匙胚的信息和步骤
     * @param startFlag
     */
    private  void setKeyBlanksInformationAndStep(int startFlag){
        if(startFlag==KEY_CATEGORY_SEARCH){  //等于1 代表是种类钥匙界面
            keyBlanks="";
            String[]  newStr=ki.getKeyBlanks().split(";");
            for (int i = 0; i <newStr.length; i++) {
                keyBlanks+=newStr[i]+"\n";
            }
            // 设置钥匙胚内容
            mTvKeyBlanksContent.setText(keyBlanks);
            mTvStep.setText(stepText+"—IC Card:"+ki.getCardNumber());
        }else if(startFlag==KEY_BLANKS_SEARCH){  //等于钥匙胚搜索
            KeyInfoDaoManager keyInfoDaoManager=KeyInfoDaoManager.getInstance();
            keyBlanks= keyInfoDaoManager.queryKeyBlanks(ki.getCardNumber());
            // 设置钥匙胚内容
            mTvKeyBlanksContent.setText(keyBlanks);
            mTvStep.setText(stepText+"—IC Card:"+ki.getCardNumber());
        }else {  //最后等于id搜索
            keyBlanks="";
            // 设置钥匙胚内容
            mTvKeyBlanksContent.setText(keyBlanks);
            mTvStep.setText(stepText+"—IC Card:"+ki.getCardNumber());
        }
        ki.setStep(stepText);
    }

    /**
     * 启动当前Activity
     * @param context
     * @param ki
     * @param step
     * @param languageMap
     * @param startFlag
     */

    public  static void  startFrmKeyCutMainActivity(Context context,KeyInfo ki,String step,HashMap<String,String> languageMap,int startFlag){
        Intent intent=new Intent(context,FrmKeyCutMainActivity.class);
        intent.putExtra("keyInfo",ki);
        intent.putExtra("step",step);
        intent.putExtra("language",languageMap);
        intent.putExtra("startFlag",startFlag);
        context.startActivity(intent);
    }

    /**
     * 获得传过来的意图数据
     */
    private  void getIntentData(){
        //获得意图
        Intent intent = getIntent();
        ki = intent.getParcelableExtra("keyInfo");
        stepText = intent.getStringExtra("step");
        languageMap  = (HashMap<String, String>) intent.getSerializableExtra("language");
        startFlag=intent.getIntExtra("startFlag",-1);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(serialDriver!=null){
            serialDriver.setHandler(mHandler);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mDecorView != null) {
            Tools.hideBottomUIMenu(mDecorView);
        }
        Log.e(TAG, "onResume: " );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isShowMenu == 1) {
            isShowMenu = 0;
            mMenuWindow.dismiss();
        }
        if(!mMeasure.isClickable()){
            mMeasure.setClickable(true);
            mMeasure.setTextColor(Color.parseColor("#000000"));
            mMenuWindow.dismiss();
            isShowMenu = 0;
        }

    }
  private   LinearLayout  linearLayout;
    private void initPopupWindow() {
        //在那个窗口上显示
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_frm_key_cut, null);
        //菜单窗口
        View   menuContentView = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        mMenuWindow = new PopupWindow(this);
        mMenuWindow.setContentView(menuContentView);
        // 设置弹出窗体的宽和高
        mMenuWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置窗口背景为透明
        mMenuWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置弹出窗体显示时的动画，从底部向上弹出
        mMenuWindow.setAnimationStyle(R.style.PopupAnimation);
        linearLayout=(LinearLayout)menuContentView.findViewById(R.id.ll_menu_layout);
        Button mBackHome = (Button) menuContentView.findViewById(R.id.btn_home);
        mBackHome.setOnClickListener(menuItemClickListener);
        Button mInfo = (Button) menuContentView.findViewById(R.id.btn_info);
        mInfo.setOnClickListener(menuItemClickListener);
        Button mCollect = (Button) menuContentView.findViewById(R.id.btn_favorite);
        mCollect.setOnClickListener(menuItemClickListener);
        Button mCalibration = (Button) menuContentView.findViewById(R.id.btn_calibration);
        mCalibration.setOnClickListener(menuItemClickListener);
        mMeasure = (Button) menuContentView.findViewById(R.id.btn_measure);
        mMeasure.setOnClickListener(menuItemClickListener);
        Button mMove = (Button) menuContentView.findViewById(R.id.btn_move);
        mMove.setOnClickListener(menuItemClickListener);
        Button mSave = (Button) menuContentView.findViewById(R.id.btn_save);
        mSave.setOnClickListener(menuItemClickListener);
        Button mStop = (Button) menuContentView.findViewById(R.id.btn_stop_operate);
        mStop.setOnClickListener(menuItemClickListener);
        View selectKeySpaceContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_select_key_spaces, null);
        mSelectKeySpaceWindow = new PopupWindow(this);
        mSelectKeySpaceWindow.setContentView(selectKeySpaceContentView);
        mSelectKeySpaceWindow.setBackgroundDrawable(new ColorDrawable());
        mSelectKeySpaceWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectKeySpaceWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectKeySpaceWindow.setFocusable(false);
        mVariableCount = (LinearLayout) selectKeySpaceContentView.findViewById(R.id.ll_space_group);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isShowMenu == 1) {
            isShowMenu = 0;
            mMenuWindow.dismiss();
        }
        return  super.onTouchEvent(event);
    }

    public void initViews() {
       serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());  //获得串口驱动
        mBack = (Button) findViewById(R.id.btnBack);
        mBack.setOnClickListener(clickListener);
        //改变钥匙齿位深度
        mChangeDepth = (Button) findViewById(R.id.btnInput);
        mChangeDepth.setOnClickListener(clickListener);
        //代码查齿号
        mCodeFindKey = (Button) findViewById(R.id.btnCode);
        mCodeFindKey.setOnClickListener(clickListener);
        //钥匙形状解码
        mDecodeKey = (Button) findViewById(R.id.btn_decode);
        mDecodeKey.setOnClickListener(clickListener);
        //齿号查代码
        mToothFindCode = (Button) findViewById(R.id.btnInCode);
        mToothFindCode.setOnClickListener(clickListener);
        //缺齿查询
        mLackToothFind = (Button) findViewById(R.id.btnLackTooth);
        mLackToothFind.setOnClickListener(clickListener);
        //切割
        mKeyCut = (Button) findViewById(R.id.btn_cut);
        mKeyCut.setOnClickListener(clickListener);
        mBtnMenu = (Button) findViewById(R.id.btn_frm_menu);
        mBtnMenu.setOnClickListener(clickListener);
        //获得Window 装饰View
        mDecorView = getWindow().getDecorView();
        mDecodeName = (TextView) findViewById(R.id.tv_step_title);
        //提示语
        mHint = (TextView) findViewById(R.id.tv_hint);
        mHint.setText("<- Tap mKey on the left to switch.");
        mTvKeyBlanksContent=(TextView)findViewById(R.id.tv_key_blanks_content);
        //获得步骤文本
      mTvStep = (TextView) findViewById(R.id.tv_title);
        //注册一个ViewTreeObserver的监听回调
        ViewTreeObserver vto =mBtnMenu.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                width =mBtnMenu.getWidth();
                linearLayout.measure(0,0);
                height =linearLayout.getMeasuredHeight();
                mBtnMenu.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        Intent intent;

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnBack://退出界面
                    finish();
                    break;
                case R.id.btnInput://改变齿深
                    InputMainActivity.startInputMainActivity(mContext,
                            languageMap,
                            mKey.getToothCode(),
                            ki
                            );
                    break;
                case R.id.btnCode://代码查齿
                    CodeFindToothActivity.startItselfActivity(mContext,mTvStep.getText().toString());
                    break;
                case R.id.btn_decode://检查钥匙的形状，齿深，齿位。读齿
                    DecodeKeyActivity.startDecodeActivity(mContext,ki,languageMap,stepText,startFlag);
                    break;
                case R.id.btnInCode://齿号查钥匙的代码编号
                    ToothFindCodeActivity.startItselfActivity(mContext,mTvStep.getText().toString());
                    break;
                case R.id.btnLackTooth: //缺齿查询
                    intent = new Intent(mContext, LackToothFindActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_cut:   //切割钥匙
                    CutKeyActivity.startCutKeyActivity(mContext,ki,languageMap,stepText,startFlag);
                    break;
                case R.id.btn_frm_menu:  //打开窗口菜单
                    if (isShowMenu == 1) {
                        mMenuWindow.dismiss();
                        isShowMenu = 0;
                    } else {
                        mMenuWindow.showAsDropDown(mBtnMenu,width+1,-height,Gravity.BOTTOM);
                        isShowMenu = 1;
                    }
                    break;

            }
        }
    };
    /**
     * 本Activity菜单item点击事件
     */
    View.OnClickListener  menuItemClickListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
             switch (v.getId()) {
                 case R.id.btn_home:  //回到主界面
                     intent = new Intent(mContext, MainActivity.class);
                     // 跳到主界面 跳转到的activity若已在栈中存在，则将其上的activity都销掉。
                     intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(intent);
                     break;
                 case R.id.btn_info://跳转到钥匙信息界面 查看钥匙的基本信息
                     intent = new Intent(mContext, KeyBaseInfoActivity.class);
                     intent.putExtra("keyInfo", ki);
                     intent.putExtra("keyBlanksInfo",keyBlanks);
                     startActivity(intent);
                     break;
                 case R.id.btn_favorite:  //收藏夹功能
                     MessageTipsActivity.
                             startMessageTipsActivity(mContext,MessageTipsActivity.COLLECT_TIPS,ki);
                     break;
                 case R.id.btn_calibration:   //跳转到校准界面
                     intent = new Intent(mContext, FrmMaintenanceActivity.class);
                     startActivity(intent);
                     break;
                 case R.id.btn_measure: // 校准切割刀的高度
                     ProbeAndCutterMeasurementActivity.
                             startItselfActivity(FrmKeyCutMainActivity.this,ProbeAndCutterMeasurementActivity.FlAG_CUTTER_MEASUREMENT);
                     break;
                 case R.id.btn_move:
                     serialDriver.write(Instruction.FIXTURE_MOVE.getBytes(), Instruction.FIXTURE_MOVE.length());
                     break;
                 case R.id.btn_save:
                     break;
                 case R.id.btn_stop_operate:
                     serialDriver.write(Instruction.STOP_OPERATE.getBytes(), Instruction.STOP_OPERATE.length());
                     mMenuWindow.dismiss();
                     isShowMenu = 0;
                     break;
             }
        }
    };

    //默认线程
    @Subscribe(threadMode = ThreadMode.POSTING,priority=1)
    public void onMessageEventPost(MessageEvent event) {
            if(event.resultCode==MessageEvent.CHANGE_KEY_TOOTH_CODE){  // 改变钥匙的齿代码
                Log.d(TAG, "onMessageEventPost: "+event.message);
                ki.setKeyToothCode(event.message);
                mKey.setToothCode(event.message);
            }else if(event.resultCode==MessageEvent.MEASURE_TOOL_FINISH){  //测量工具完成，恢复按钮
            }else if(event.resultCode==MessageEvent.CONFIRM_COLLECT_KEY){  //确定收藏钥匙

            }else if(event.resultCode==MessageEvent.CLAMP_STATE_INDEX){
                ki.setClampStateIndex(event.ClampStateIndex);
            }
        // 取消
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode== ConstantValue.START_MEASURE_CUTTER){
            mMeasure.setClickable(false);
            mMeasure.setTextColor(Color.parseColor("#BFBFBF"));
        }
    }

    /**
     * 根据选择的齿的数量,重新设置Space And SpaceWidth
     *
     * @param amount
     */
    private void resetSpaceAndSpaceWidth(int amount) {
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        String[] spacesGroup=ki.getSpace().split(";");
        String spaceWidth = "";
        String space="";
        for (int i = 0; i < spacesGroup.length; i++) {
            String[] newStrSpaces=spacesGroup[i].split(",");
            String[] newStrSpacesWidth = spaceWidthGroup[i].split(",");
            for (int j = 0; j < amount; j++) {
                if(j==(amount-1)){   //  一组的最后一个 不加，
                    space+=newStrSpaces[j];
                    spaceWidth += newStrSpacesWidth[j];
                }else {
                    space+=(newStrSpaces[j]+",");
                    spaceWidth += (newStrSpacesWidth[j] + ",");
                }
            }
            space+=";";
            spaceWidth += ";";
        }
        ki.setSpace_width(spaceWidth);
        ki.setSpace(space);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheActivityUtils.finishSingleActivity(this);
        EventBusUtils.unregister(this);
        if (isShowMenu == 1) {
            isShowMenu = 0;
            mMenuWindow.dismiss();
        }
        mHandler.removeCallbacksAndMessages(null);
        mSelectKeySpaceWindow.dismiss();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isShowKeySpaceSelectWindow) {
            for (int j = 0; j < strArray.length; j++) {
                TextView tv = new TextView(this);
                tv.setBackgroundResource(R.drawable.btn1image_dlgspaceselectres);
                tv.setText(strArray[j]);
                tv.setClickable(true);
                tv.setTextSize(24);
                tv.setTextColor(Color.WHITE);
                tv.setId(View.generateViewId());
                //设置内容居中
                tv.setGravity(Gravity.CENTER);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                        public void onClick(View v) {
                        TextView tv1 = (TextView) v;
                        int amount = Integer.parseInt(tv1.getText().toString());
                        if (amount < mKey.getToothAmount()) {
                            //设置齿的数量
                            mKey.setToothAmount(amount);
                            //设置齿和齿宽
                            resetSpaceAndSpaceWidth(amount);
                            Log.d(TAG, "onClick: "+ki.getSpace());
                        }
                        mHint.setVisibility(View.INVISIBLE);
                        mSelectKeySpaceWindow.dismiss();
                        mBack.setClickable(true);
                        mChangeDepth.setClickable(true);
                        mCodeFindKey.setClickable(true);
                        mDecodeKey.setClickable(true);
                        mToothFindCode.setClickable(true);
                        mLackToothFind.setClickable(true);
                        mKeyCut.setClickable(true);
                        mBtnMenu.setClickable(true);
                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(6, 14, 6, 6);
                mVariableCount.addView(tv, layoutParams);  //添加可变的数量
            }
            mHint.setVisibility(View.VISIBLE);
            mSelectKeySpaceWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            mChangeDepth.setClickable(false);
            mCodeFindKey.setClickable(false);
            mDecodeKey.setClickable(false);
            mToothFindCode.setClickable(false);
            mLackToothFind.setClickable(false);
            mKeyCut.setClickable(false);
            mBtnMenu.setClickable(false);
            ki.setVariableSpace(true);
            isShowKeySpaceSelectWindow = false;
        }
    }

}
