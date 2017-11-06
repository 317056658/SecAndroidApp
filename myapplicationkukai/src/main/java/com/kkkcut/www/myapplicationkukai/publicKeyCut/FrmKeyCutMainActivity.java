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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.MenuModule.KeyBaseInfoActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.CodeCheckToothActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.CutKeyActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.DecodeActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.InputMainActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.LackToothActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.ToothCheckCodeActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteKeyDao;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MeasureToolActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
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
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.KeyType;
import com.kkkcut.www.myapplicationkukai.entity.MessageEvent;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

public class FrmKeyCutMainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mMenu, mChangeDepth, mCodeFindKey, mDecodeKey, mToothFindCode, mLackToothFind, mKeyCut, mBack, mMeasure;
    private   String title,keyBlanks;
    private TextView  mDecodeName, mHint;
    private KeyInfo ki;
    View rootView;
    public int isShowMenu = 0;
    private PopupWindow mMenuWindow;
    private PopupWindow mSelectKeySpaceWindow;  //选择几个齿位窗口
    private View mDecorView;
    private Key mKey;
    private boolean isShowKeySpaceSelectWindow;  //只第一次进来显示
    private LinearLayout mSpaceGroup;
    private TextView mTvStep;
    private String[] strArray;
    private SQLiteKeyDao database;
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
        mContext =this;
        this.getIntentData();
        this.temporaryGlobalSave();
        EventBusUtils.register(this);  //注册EventBus
        this.initViews();
        Log.d("是好多？", "getIntentData: "+startFlag);
        this.setKeyBlanksInformationAndStep(startFlag);  //设置钥匙胚信息和步骤
        //初始化按钮
        //获得线性布局
        LinearLayout mPutImg = (LinearLayout) findViewById(R.id.ll_put_image);
        MyApplication.clampLocatingIndex = 100;      // 夹具定位下标 默认值为100
                switch (ki.getType()) {
                    case KeyType.UNILATERAL_KEY://加载单边钥匙
                        mKey = new UnilateralKey(this,ki);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(680,320));
                        mKey.setDrawPatternSize(640,280);
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.BILATERAL_KEY:  //加载双边钥匙
                        mKey = new BilateralKey(this,ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740,330));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.DUAL_PATH_INSIDE_GROOVE_KEY:  //加载双轨内槽钥匙
                        mKey = new DualPathInsideGrooveKey(this);
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 330));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.MONORAIL_OUTER_GROOVE_KEY:  //加载单轨外槽
                        mKey = new MonorailOuterGrooveKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 300));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.DUAL_PATH_OUTER_GROOVE_KEY:  //加载双轨外槽钥匙
                        mKey = new DualPathOuterGrooveKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 310));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.MONORAIL_INSIDE_GROOVE_KEY: //加载单轨内槽钥匙
                        mKey = new MonorailInsideGrooveKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 300));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.CONCAVE_DOT_KEY:  //加载凹点钥匙
                        mKey = new ConcaveDotKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740,360));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.ANGLE_KEY: //角度钥匙
                        mKey =new AngleKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 300));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        mDecodeKey.setVisibility(View.INVISIBLE);  //隐藏
                        break;
                    case KeyType.CYLINDER_KEY: //圆筒钥匙
                        mKey =new CylinderKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 340));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        break;
                    case KeyType.SIDE_TOOTH_KEY:  //侧齿钥匙
                        mKey =new SideToothKey(this);
                        mKey.setNeededDrawAttribute(ki);
                        mKey.setToothCodeDefault();
                        mKey.setToothDepthName(ki.getKeyToothCode());
                        mKey.setLayoutParams(new LinearLayout.LayoutParams(800,300));
                        mKey.setBackgroundResource(R.drawable.edit_shape);
                        mPutImg.addView(mKey);
                        mDecodeKey.setVisibility(View.INVISIBLE);  //隐藏
                        break;
                }
        if(ki.getVariableSpace()!=null){
            strArray = ki.getVariableSpace().split(",");
            isShowKeySpaceSelectWindow = true;  //只第一次进去
        }
        //判断有不有描述。有描述就打开一个窗口提示
        if (!TextUtils.isEmpty(ki.getDesc())) {
            MessageTipsActivity.startMessageTipsActivity(this,MessageTipsActivity.KEY_SPECIAL_DESCRIPTION_TIPS,ki,null,-10);
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
            mTvStep.setText(stepText+"—IC Card:"+ki.getId());
        }else if(startFlag==KEY_BLANKS_SEARCH){  //等于钥匙胚搜索
            database =new SQLiteKeyDao(this,"SEC1.db");
            keyBlanks= database.queryKeyBlanks(ki.getId());
            // 设置钥匙胚内容
            mTvKeyBlanksContent.setText(keyBlanks);
            mTvStep.setText(stepText+"—IC Card:"+ki.getId());
        }else {  //最后等于id搜索
            keyBlanks="";
            // 设置钥匙胚内容
            mTvKeyBlanksContent.setText(keyBlanks);
            mTvStep.setText(stepText+"—IC Card:"+ki.getId());
        }
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
        Log.e(TAG, "onStart: " );
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mDecorView != null) {
            Tools.hideBottomUIMenu(mDecorView);
        }
        if (mMenuWindow == null) {
            initPopupWindow();
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

    private void initPopupWindow() {
        //在那个窗口上显示
        rootView = LayoutInflater.from(this).inflate(R.layout.activity_frm_key_cut, null);
        //菜单窗口
        View menuContentView = LayoutInflater.from(this).inflate(R.layout.layout_menu, null);
        mMenuWindow = new PopupWindow(this);
        mMenuWindow.setContentView(menuContentView);
        // 设置弹出窗体的宽和高
        mMenuWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置窗口背景为透明
        mMenuWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        // 设置弹出窗体显示时的动画，从底部向上弹出
        mMenuWindow.setAnimationStyle(R.style.PopupAnimation);
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
        Button mStop = (Button) menuContentView.findViewById(R.id.btn_stop);
        mStop.setOnClickListener(menuItemClickListener);
        View selectKeySpaceContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_select_key_spaces, null);
        mSelectKeySpaceWindow = new PopupWindow(this);
        mSelectKeySpaceWindow.setContentView(selectKeySpaceContentView);
        mSelectKeySpaceWindow.setBackgroundDrawable(new ColorDrawable());
        mSelectKeySpaceWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectKeySpaceWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSelectKeySpaceWindow.setFocusable(false);
        mSpaceGroup = (LinearLayout) selectKeySpaceContentView.findViewById(R.id.ll_space_group);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isShowMenu == 1) {
            isShowMenu = 0;
            mMenuWindow.dismiss();
        }
        Log.d("触摸了？", "onTouchEvent: ");
        return  super.onTouchEvent(event);
    }

    public void initViews() {
//        serialDriver=ProlificSerialDriver.getInstance();  //获得串口驱动
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
        mMenu = (Button) findViewById(R.id.btn_cut_menu);
        mMenu.setOnClickListener(clickListener);
        //获得Window 装饰View
        mDecorView = getWindow().getDecorView();
        mDecodeName = (TextView) findViewById(R.id.tv_step_title);
        //提示语
        mHint = (TextView) findViewById(R.id.tv_hint);
        mHint.setText("<- Tap mKey on the left to switch.");
        mTvKeyBlanksContent=(TextView)findViewById(R.id.tv_key_blanks_content);
        //获得步骤文本
      mTvStep = (TextView) findViewById(R.id.tv_text);
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
                            mKey.getAllToothDepthName(),
                            ki.getDepth(),
                            ki.getDepth_name());
                    break;
                case R.id.btnCode://代码查齿
                    intent = new Intent(mContext, CodeCheckToothActivity.class);
                    intent.putExtra("title", title);
                    startActivity(intent);
                    break;
                case R.id.btn_decode://检查钥匙的形状，齿深，齿位。读齿
                    ki.setKeyToothCode(keyToothDepthNameToString());
                    DecodeActivity.startDecodeActivity(mContext,ki,languageMap,stepText,startFlag);
                    break;
                case R.id.btnInCode://齿号查钥匙的代码编号
                    intent = new Intent(mContext,ToothCheckCodeActivity.class);
                    intent.putExtra("title", title);
                    startActivity(intent);
                    break;
                case R.id.btnLackTooth: //缺齿查询
                    intent = new Intent(mContext, LackToothActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_cut:   //切割钥匙
                    CutKeyActivity.startCutKeyActivity(mContext,ki,languageMap,stepText,startFlag);
                    break;
                case R.id.btn_cut_menu:  //打开窗口菜单
                    if (isShowMenu == 1) {
                        mMenuWindow.dismiss();
                        isShowMenu = 0;
                    } else {
                        mMenuWindow.showAtLocation(rootView, Gravity.BOTTOM, 100, 0);
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
                     MessageTipsActivity.startMessageTipsActivity(mContext,MessageTipsActivity.COLLECT_TIPS,ki,stepText,startFlag);
                     break;
                 case R.id.btn_calibration:   //跳转到校准界面
                     intent = new Intent(mContext, FrmMaintenanceActivity.class);
                     startActivity(intent);
                     break;
                 case R.id.btn_measure: // 校准切割刀的高度
                     intent = new Intent(mContext, MeasureToolActivity.class);
                     intent.putExtra("Flags", 2);
                     startActivity(intent);
                     break;
                 case R.id.btn_move:
                     serialDriver.write(Instruction.FIXTURE_MOVE.getBytes(), Instruction.FIXTURE_MOVE.length());
                     break;
                 case R.id.btn_save:
                     break;
                 case R.id.btn_stop:
                     serialDriver.write(Instruction.STOP_OPERATE.getBytes(), Instruction.STOP_OPERATE.length());
                     mMenuWindow.dismiss();
                     isShowMenu = 0;
                     break;
             }
        }
    };

    /**
     * 把钥匙齿的深度名转为字符串
     * @return
     */
    private  String keyToothDepthNameToString(){
        String str="";
      List<String[]> list = mKey.getAllToothDepthName();
        for (int i = 0; i <list.size(); i++) {
            String[] toothDepthName =list.get(i);
            str+= TextUtils.join(",",toothDepthName);
            str+=",";
        }
        return str;

    }
    //默认线程
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onMessageEventPost(MessageEvent event) {
            if(event.resultCode==MessageEvent.CHANGE_KEY_TOOTH_CODE){  // 读齿完毕数据
                ki.setKeyToothCode(event.message);
                mKey.setToothDepthName(event.message);
            }else if(event.resultCode==MessageEvent.MEASURE_TOOL_FINISH){  //测量工具完成，恢复按钮
                mMeasure.setClickable(false);
                mMeasure.setTextColor(Color.parseColor("#BFBFBF"));
            }else if(event.resultCode==MessageEvent.CONFIRM_COLLECT_KEY){  //确定收藏钥匙

            }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
    }

    /**
     * 根据选择的齿的数量,重新设置齿顶宽
     *
     * @param amount
     */
    private void setSpaceWidth(int amount) {
        String[] spaceWidthGroup = ki.getSpace_width().split(";");
        String spaceWidth = "";
        for (int i = 0; i < spaceWidthGroup.length; i++) {
            String[] toothWidth = spaceWidthGroup[i].split(",");
            for (int j = 0; j < amount; j++) {
                if(j==(amount-1)){   //  一组的最后一个 不加，
                    spaceWidth +=toothWidth[j];
                }else {
                    spaceWidth += (toothWidth[j] + ",");
                }
            }

            spaceWidth += ";";
        }
        Log.d("齿宽", "setSpaceWidth: "+spaceWidth);
        ki.setSpace_width(spaceWidth);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                        Log.d("onClick", "onClick: " + Integer.parseInt(tv1.getText().toString()));
                        int amount = Integer.parseInt(tv1.getText().toString());
                        if (amount < mKey.getKeyToothAmount()) {
                            //设置齿的数量
                            mKey.setKeyToothAmount(amount);
                            //获得齿距
                            ki.setSpace(mKey.getSpace());
                            //设置齿顶宽
                            setSpaceWidth(amount);
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
                        mMenu.setClickable(true);
                    }
                });
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(6, 14, 6, 6);
                mSpaceGroup.addView(tv, layoutParams);
            }
            mHint.setVisibility(View.VISIBLE);
            mSelectKeySpaceWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
            mChangeDepth.setClickable(false);
            mCodeFindKey.setClickable(false);
            mDecodeKey.setClickable(false);
            mToothFindCode.setClickable(false);
            mLackToothFind.setClickable(false);
            mKeyCut.setClickable(false);
            mMenu.setClickable(false);
            isShowKeySpaceSelectWindow = false;
        }
    }

}
