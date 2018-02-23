package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.dao.SaveDataDaoManager;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.OperateTipsActivity;
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
import com.kkkcut.www.myapplicationkukai.entity.Clamp;
import com.kkkcut.www.myapplicationkukai.entity.ConstantValue;
import com.kkkcut.www.myapplicationkukai.entity.Detection;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.KeyType;
import com.kkkcut.www.myapplicationkukai.entity.MessageEvent;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.DensityUtils;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.utils.logDocument.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kkkcut.www.myapplicationkukai.R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes;

public class DecodeKeyActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtnBack, mBtnProbe, mDecode, mMeasure;
    private Button mBtnMenu;
    private TextView mStepTitle, mMenuName, mClampTextHint, mTvGuide, mTvHint;
    private PopupWindow mProbeHintWindow;
    private boolean isShowWindow = true; //加个阀值做判断
    private ImageView mIvClampLocationSelect;
    private LinearLayout mLlAddKey;
    private View mDecorView;
    boolean isProbeWindowShow = true;
    private PopupWindow mMenuWindow;
    private boolean isShowMenu = false, isShowLocationSelectWindow = false;
    private View rootView;
    private CheckBox mIsRound;
    private Button   mStopDecode;
    private int startFlag;
    private Intent menuChildViewIntent = new Intent();
    private int keyClamp, locatingSlot;  //定位槽和 夹具
    private KeyInfo ki;
    private LinearLayout.LayoutParams layoutParams;
    private Key mKey;
    private float deepestDepth;
    private int clampAvailable;
    private int isRound = 0;
    private int index = 0;
    private  String step;
    private List<Clamp> clampList;
    private String readOrder;  //读齿命令
    private Clamp cl;
    private PopupWindow mDetectionLocationMode;
    private ImageView mIvLocateMode, mIvNoDetect, mIvUpDownLocation, mIvBilateralLocation, mIvCuspLocation, mIvThreeTerminalLocation;
    private int readDetectionMode = 0;  //定位方式默认为0,  读齿的定位方式
    private HashMap<String,String>  languageMap;
    private ProlificSerialDriver  serialDriver;
    private MyHandler mHandler = new MyHandler(this);
    private  int defaultLength=1300;

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        private String readData;
        Intent exceptionIntent = new Intent();
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DecodeKeyActivity activity = (DecodeKeyActivity) reference.get();
            String data="";
            if(msg.obj!=null){
                data=msg.obj.toString();
            }
            switch (msg.what){
                case MicyocoEvent.OPERATION_FINISH://操作完成
                    if (!activity.mMeasure.isClickable()) {
                        Log.d("进来？", "handleMessage: ");
                        activity.mMeasure.setTextColor(Color.parseColor("#020701"));
                        activity.mMeasure.setClickable(true);
                        activity.mMenuWindow.dismiss();
                    }else  if (activity.mDecode.getVisibility() == View.INVISIBLE) {
                        readData=readData+",";
                        EventBusUtils.post(new MessageEvent(readData,MessageEvent.CHANGE_KEY_TOOTH_CODE));
                        activity.finish();
                    }
                    break;
                case MicyocoEvent.USER_CANCEL_OPERATE: //用户操作取消
                    exceptionIntent.setClass(activity, ExceptionActivity.class);
                    exceptionIntent.putExtra("exception",data);
                    activity.startActivity(exceptionIntent);
                    break;
                case MicyocoEvent.SAFETYGATE_OPEN://安全门打开
                    exceptionIntent.setClass(activity, ExceptionActivity.class);
                    exceptionIntent.putExtra("exception",data);
                    activity.startActivity(exceptionIntent);
                    break;
                case MicyocoEvent.READ_TOOTH_DATA_BACK:  //读齿数据返回
                    readData=data;
                    activity.mKey.setToothCode(readData);
                    activity.mKey.redrawKey();
                    break;
                case MicyocoEvent.MATERIAL_OR_POSITION_ERROR:   //异常错误
                    if (activity.mDecode.getVisibility() == View.INVISIBLE) {
                        activity.mKey.setToothCodeDefault();//设置齿的深度名全部为默认X
                        activity.mKey.setVisibility(View.GONE);
                        activity.mKey.setToothCodeDefault();
                        activity.mIvClampLocationSelect.setVisibility(View.VISIBLE);
                        activity.mBtnMenu.setVisibility(View.VISIBLE);
                        activity. mDecode.setVisibility(View.VISIBLE);
                        activity.mStopDecode.setVisibility(View.INVISIBLE);
                        activity.mBtnBack.setVisibility(View.VISIBLE);
                        //显示指南
                        activity.mTvGuide.setVisibility(View.VISIBLE);
                        activity.mTvHint.setText("Place a mKey blank as shown in the below figure.");
                    }else {
                        activity.mMeasure.setTextColor(Color.parseColor("#636363"));
                        activity.mMeasure.setClickable(false);
                        activity.judgeMenuWindowIsHide();
                    }
                    ExceptionActivity.startItselfActivity(activity,data);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_layout);
      serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
        getIntentData();
        initViews();//初始化按钮
        initPopupWindow();//初始化PopupWindow窗口
        clampJudge(ki);  //根据传过来的数据做夹具判断
        judgeKeyDetectLocationMode();  //判断钥匙的检测定位方式
        addKeyViewType();// 添加钥匙View的类型
    }

    /**
     * 启动本Activity
     * @param context
     * @param ki
     * @param languageMap
     */
    public static void startDecodeActivity(Context context, KeyInfo ki, HashMap<String, String> languageMap,String step,int  startFlag){
        Intent intent=new Intent(context,DecodeKeyActivity.class);
        intent.putExtra("keyInfo",ki);
        intent.putExtra("language",languageMap);
        intent.putExtra("step",step);
        intent.putExtra("startFlag",startFlag);
        context.startActivity(intent);
    }
    private  void getIntentData(){
        Intent intent=getIntent();
        startFlag = intent.getIntExtra("startFlag", -1);
        ki = intent.getParcelableExtra("keyInfo");
        languageMap= (HashMap<String, String>) intent.getSerializableExtra("language");
        step=intent.getStringExtra("step");
    }

    /**
     * 夹具判断
     */
    private void clampJudge(KeyInfo ki) {
        clampList = new ArrayList<>();
        String[] spaceGroup = ki.getSpace().split(";"); //分割space
        String[] depthGroup = ki.getDepth().split(";"); //分割深度
        if (ki.getType() == KeyInfo.BILATERAL_KEY) {   // 等于0为双边齿
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_standard_stop_1);
                cl.setLocationSlot(1);
                cl.setClampType(1);
                clampList.add(cl);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                int availableExtent = 0;  //有效范围
                int quondamSpace=0;
                if (spaceGroup.length == 1) {
                    String[] spaceDataArray = spaceGroup[0].split(",");
                    availableExtent = Integer.parseInt(spaceDataArray[0])+defaultLength;
                    quondamSpace=Integer.parseInt(spaceDataArray[0]);
                } else if (spaceGroup.length == 2) {
                    String[] spaceDataArrayOne = spaceGroup[0].split(",");
                    String[] spaceDataArrayTwo = spaceGroup[1].split(",");
                    int spaceOne = Integer.parseInt(spaceDataArrayOne[0]);
                    int spaceTwo = Integer.parseInt(spaceDataArrayTwo[0]);
                    if (spaceOne > spaceTwo) {
                        availableExtent = spaceOne +defaultLength;
                        quondamSpace=spaceOne;
                    } else {
                        availableExtent = spaceTwo +defaultLength;
                        quondamSpace=spaceTwo;
                    }
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC&&quondamSpace<Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC ){
                    Clamp cl = new Clamp();
                    cl.setImgHint(R.drawable.a9_standard_stop_4);
                    cl.setLocationSlot(4);
                    cl.setClampType(1);
                    clampList.add(cl);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC&&quondamSpace<Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC ){
                    Clamp cl = new Clamp();
                    cl.setImgHint(R.drawable.a9_standard_stop_3);
                    cl.setLocationSlot(3);
                    cl.setClampType(1);
                    clampList.add(cl);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE&&quondamSpace<Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE) {
                    Clamp cl = new Clamp();
                    cl.setImgHint(R.drawable.a9_standard_stop_2);
                    cl.setLocationSlot(2);
                    cl.setClampType(1);
                    clampList.add(cl);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC&&quondamSpace<Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC) {
                    Clamp cl = new Clamp();
                    cl.setImgHint(R.drawable.a9_standard_stop_5);
                    cl.setLocationSlot(5);
                    cl.setClampType(1);
                    clampList.add(cl);
                }
            }
            mTvGuide.setVisibility(View.VISIBLE);   //显示
        } else if (ki.getType() == KeyInfo.FRONTEND_ALIGN) {  //单边齿
            String[] depths = depthGroup[0].split(",");
            //肩部定位
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                Clamp clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_aux_single_std_1);
                clamp.setLocationSlot(16);  //定位槽为16
                clamp.setClampType(3);
                clampList.add(clamp);
                //获得最深深度
                deepestDepth = Float.parseFloat(depths[depths.length - 1]) * 0.01f;
                if (deepestDepth > Clamp.THREE_NUMBER_CLAMP_AVAILABLE_FIVE_MM) {
                    clampAvailable = Clamp.THREE_NUMBER_CLAMP_FIVE_MM_AVAILABLE;
                    mClampTextHint.setText("Available Clamp:3.5mm,5mm");
                } else if (deepestDepth > Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {
                    clampAvailable = Clamp.THREE_NUMBER_CLAMP_THREE_POINT_FIVE_MM_AVAILABLE;
                    mClampTextHint.setText("Available Clamp:3.5mm");
                } else if (deepestDepth <= Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {    //小于3.5  这钥匙数据不对
                    Intent messageHintIntent = new Intent(DecodeKeyActivity.this, MessageTipsActivity.class);
                    messageHintIntent.putExtra("msgTips", 3);
                    messageHintIntent.putExtra("IntentFlag", startFlag);
                    startActivity(messageHintIntent);
                }
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {  //前端定位
                deepestDepth = Float.parseFloat(depths[depths.length - 1]) * 0.01f;
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_aux_single_std_2);
                cl.setLocationSlot(17);  //定位槽为17
                cl.setClampType(3);
                clampList.add(cl);
                if (deepestDepth > Clamp.THREE_NUMBER_CLAMP_AVAILABLE_FIVE_MM) {
                    clampAvailable = 2;
                    mClampTextHint.setText("Available Clamp:3.5mm,5mm");
                } else if (deepestDepth >Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {
                    clampAvailable = 1;
                    mClampTextHint.setText("Available Clamp:3.5mm");
                }else if (deepestDepth <= Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {    //小于3.5  这钥匙数据不对
                    Intent messageHintIntent = new Intent(DecodeKeyActivity.this, MessageTipsActivity.class);
                    messageHintIntent.putExtra("msgTips", 3);
                    messageHintIntent.putExtra("IntentFlag", startFlag);
                    startActivity(messageHintIntent);
                }
            }
        } else if (ki.getType() == KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY) {  //双轨内槽钥匙
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {   //前端定位
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClampType(1);
                clampList.add(cl);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {  //尖端定位
                String[] spaceDataArrayOne = spaceGroup[0].split(",");
                String[] spaceDataArrayTwo = spaceGroup[1].split(",");
                int availableExtent;
                int quondamSpace;
                int spaceOne = Integer.parseInt(spaceDataArrayOne[0]);
                int spaceTwo = Integer.parseInt(spaceDataArrayTwo[0]);
                if (spaceOne > spaceTwo) {
                    availableExtent = spaceOne+defaultLength;
                    quondamSpace=spaceOne;
                } else {
                    availableExtent = spaceTwo+defaultLength;
                    quondamSpace=spaceTwo;
                }
                Clamp  clamp;
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC ){
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_4);
                    clamp.setLocationSlot(4);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC ){
                    clamp = new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_3);
                    clamp.setLocationSlot(3);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_2);
                    clamp.setLocationSlot(2);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                //这个是加了辅助夹具定位档片
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_laser_stop_15);
                clamp.setLocationSlot(15);  //辅助夹具的定位槽
                clamp.setClampType(1);
                clampList.add(clamp);
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_5);
                    clamp.setLocationSlot(5);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
            }
        } else if (ki.getType()==KeyInfo.MONORAIL_OUTER_GROOVE_KEY) {  //单轨外槽
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClampType(1);
                clampList.add(cl);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                String[] spaceDataArrayOne = spaceGroup[0].split(",");
                int availableExtent ;
                int quondamSpace;
                int spaceOne = Integer.parseInt(spaceDataArrayOne[0]);
                availableExtent = spaceOne+defaultLength;
                quondamSpace=spaceOne;
                Clamp  clamp;
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC ){
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_4);
                    clamp.setLocationSlot(4);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC ){
                    clamp = new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_3);
                    clamp.setLocationSlot(3);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_2);
                    clamp.setLocationSlot(2);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                //这个是加了辅助夹具定位档片
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_laser_stop_15);
                clamp.setLocationSlot(15);  //辅助夹具的定位槽
                clamp.setClampType(1);
                clampList.add(clamp);
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_5);
                    clamp.setLocationSlot(5);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
            }
        } else if (ki.getType()== KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY) {  //双轨外槽钥匙
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {  //肩部定位
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_laser_stop_1_1);
                cl.setLocationSlot(1);
                cl.setClampType(1);
                clampList.add(cl);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {  //前端定位
                String[] spaceDataArrayOne = spaceGroup[0].split(",");
                String[] spaceDataArrayTwo = spaceGroup[1].split(",");
                int availableExtent ;
                int quondamSpace;
                int spaceOne = Integer.parseInt(spaceDataArrayOne[0]);
                int spaceTwo = Integer.parseInt(spaceDataArrayTwo[0]);
                if (spaceOne > spaceTwo) {
                    availableExtent = spaceOne+defaultLength;
                    quondamSpace=spaceOne;
                } else {
                    availableExtent = spaceTwo+defaultLength;
                    quondamSpace=spaceTwo;
                }
                Clamp  clamp;
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC ){
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_4);
                    clamp.setLocationSlot(4);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC ){
                    clamp = new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_3);
                    clamp.setLocationSlot(3);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_2);
                    clamp.setLocationSlot(2);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                //这个是加了辅助夹具定位档片
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_laser_stop_15);
                clamp.setLocationSlot(15);  //辅助夹具的定位槽
                clamp.setClampType(1);
                clampList.add(clamp);
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_5);
                    clamp.setLocationSlot(5);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
            }
        } else if (ki.getType() == KeyInfo.MONORAIL_INSIDE_GROOVE_KEY) {  //单轨内槽钥匙
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClampType(1);
                clampList.add(cl);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                int availableExtent = 0;  //有效范围
                int quondamSpace=0;
                if (spaceGroup.length == 1) {
                    String[] spaceDataArray = spaceGroup[0].split(",");
                    availableExtent = Integer.parseInt(spaceDataArray[0])+defaultLength;
                    quondamSpace=Integer.parseInt(spaceDataArray[0]);
                } else if (spaceGroup.length == 2) {
                    String[] spaceDataArrayOne = spaceGroup[0].split(",");
                    String[] spaceDataArrayTwo = spaceGroup[1].split(",");
                    int spaceOne = Integer.parseInt(spaceDataArrayOne[0]);
                    int spaceTwo = Integer.parseInt(spaceDataArrayTwo[0]);
                    if (spaceOne > spaceTwo) {
                        availableExtent = spaceOne +defaultLength;
                        quondamSpace=spaceOne;
                    } else {
                        availableExtent = spaceTwo +defaultLength;
                        quondamSpace=spaceTwo;
                    }
                }
                Clamp  clamp;
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FOUR_DISTANC ){
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_4);
                    clamp.setLocationSlot(4);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_THREE_DISTANC ){
                    clamp = new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_3);
                    clamp.setLocationSlot(3);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_TWO_DISTANCE) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_2);
                    clamp.setLocationSlot(2);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
                //这个是加了辅助夹具定位档片
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_laser_stop_15);
                clamp.setLocationSlot(15);  //辅助夹具的定位槽
                clamp.setClampType(1);
                clampList.add(clamp);
                if (availableExtent >= Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC&& quondamSpace <Clamp.ONE_NUMBER_CLAMP_LOCATING_GROOVE_FIVE_DISTANC) {
                    clamp= new Clamp();
                    clamp.setImgHint(R.drawable.a9_laser_stop_5);
                    clamp.setLocationSlot(5);
                    clamp.setClampType(1);
                    clampList.add(clamp);
                }
            }
        }else if(ki.getType()==KeyInfo.CONCAVE_DOT_KEY){   //凹点钥匙
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_dimple_stop_1);
                cl.setLocationSlot(6);
                cl.setClampType(2);
                clampList.add(cl);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                Clamp clamp;
                clamp= new Clamp();
                clamp.setImgHint(R.drawable.a9_dimple_stop_5);
                clamp.setLocationSlot(10);
                clamp.setClampType(2);
                clampList.add(clamp);
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_dimple_stop_4);
                clamp.setLocationSlot(9);
                clamp.setClampType(2);
                clampList.add(clamp);
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_dimple_stop_3);
                clamp.setLocationSlot(8);
                clamp.setClampType(2);
                clampList.add(clamp);
                clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_dimple_stop_2);
                clamp.setLocationSlot(7);
                clamp.setClampType(2);
                clampList.add(clamp);
            }
        }else if(ki.getType()==KeyInfo.ANGLE_KEY){   //角度钥匙
        }else if(ki.getType()==KeyInfo.CYLINDER_KEY){  //圆筒钥匙
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                Clamp    clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_tubular_stop);
                clamp.setClampType(4);
                clampList.add(clamp);
            }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
                Clamp    clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_tubular_stop);
                clamp.setClampType(4);
                clampList.add(clamp);
            }
        }else if(ki.getType()==KeyInfo.SIDE_TOOTH_KEY){  //侧齿钥匙
             //侧齿钥匙没有夹具判断  在读齿里面
        }

             if(clampList.size()<=1){
                 mIvClampLocationSelect.setClickable(false);
                 mTvGuide.setVisibility(View.INVISIBLE);
             }

             if(clampList.size()>=1){
                 //根据索引设置夹具图片和定位槽
                 cl = clampList.get(ki.getClampStateIndex());
                 mIvClampLocationSelect.setImageResource(cl.getImgHint());
                 locatingSlot = cl.getLocationSlot();
                 keyClamp = cl.getClampType();
                 index=ki.getClampStateIndex();
             }else {
                 mIvClampLocationSelect.setImageDrawable(null);
             }

        if (keyClamp == 3) {  //夹具等于3  就显示夹具技巧提示
            mClampTextHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (serialDriver != null) {
            serialDriver.setHandler(mHandler);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mMeasure.isClickable()) {
            mMeasure.setTextColor(Color.parseColor("#020701"));
            mMeasure.setClickable(true);
            mMenuWindow.dismiss();
        }
    }


    private void initViews() {
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        mBtnProbe = (Button) findViewById(R.id.btn_probe);
        mBtnProbe.setOnClickListener(this);
        mDecode = (Button) findViewById(R.id.btn_decode);
        mDecode.setOnClickListener(this);
        mStepTitle = (TextView) findViewById(R.id.tv_step_title);
        mStepTitle.setText(step);  //显示id
        mIvLocateMode = (ImageView) findViewById(R.id.btn_locate_mode);
        mIvLocateMode.setOnClickListener(this);
        //添加 自定义view画图 的  ViewGroup
        mLlAddKey = (LinearLayout) findViewById(R.id.ll_img_add);
        //获得存放夹具类型图片的view
        mIvClampLocationSelect = (ImageView) findViewById(R.id.lv_clamp_location_select);
        mIvClampLocationSelect.setOnClickListener(this);
        mBtnMenu = (Button) findViewById(R.id.rl_menu);
        mBtnMenu.setOnClickListener(this);
        mMenuName = (TextView) findViewById(R.id.tv_name);

        mIsRound = (CheckBox) findViewById(R.id.cb_round);
        mIsRound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isRound = 1; //1代表下位机四舍五入
                } else {
                    isRound = 0;  //0代表有小数
                }
                if (isProbeWindowShow) {
                    mProbeHintWindow.dismiss();
                    isProbeWindowShow = false;
                }
                judgeMenuWindowIsHide();
                if (isShowLocationSelectWindow) {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.dismiss();
                    isShowLocationSelectWindow = false;
                }

            }
        });
        mStopDecode = (Button) findViewById(R.id.btn_stop_decode);
        mStopDecode.setOnClickListener(this);
        mClampTextHint = (TextView) findViewById(R.id.tv_clamp_hint);
        //获得文本指南
        mTvGuide = (TextView) findViewById(R.id.tv_guide);
        //文本提示
        mTvHint = (TextView) findViewById(R.id.tv_hint);
        mDecorView = getWindow().getDecorView();
    }

    private void initPopupWindow() {
        rootView = LayoutInflater.from(this).inflate(R.layout.decode_layout, null);
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_probe_hint, null);

        if(ki.getType()==KeyInfo.CONCAVE_DOT_KEY){
            // 是凹点钥匙类型就替换图片
            ImageView  probeHintImg= (ImageView)contentView.findViewById(R.id.iv_probe_img);
            probeHintImg.setImageResource(R.drawable.dimpledecoder);
            TextView probeSize=(TextView)contentView.findViewById(R.id.tv_probe_size);
            probeSize.setVisibility(View.GONE);
        }
        //探针提示窗口
        mProbeHintWindow = new PopupWindow(this);
        mProbeHintWindow.setContentView(contentView);
        mProbeHintWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置宽度
        mProbeHintWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置高度
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable cd = new ColorDrawable(Color.parseColor("#00000000"));
        // 设置外部可点击 点击PopupWindow就消失
        mProbeHintWindow.setOutsideTouchable(true);
        // 设置弹出窗体的背景 要配合 setOutsideTouchable 使用 不然无意义
        mProbeHintWindow.setBackgroundDrawable(cd);
        //菜单窗口
        mMenuWindow = new PopupWindow(this);
        View mMenuContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_decode_and_cut_menu, null);
        Button mBtnHome=(Button) mMenuContentView.findViewById(R.id.menu_home);
        mBtnHome.setOnClickListener(menuChildViewClickListener);
        Button mBtnCalibration=(Button)mMenuContentView.findViewById(R.id.menu_calibration);
        mBtnCalibration.setOnClickListener(menuChildViewClickListener);
        mMeasure = (Button) mMenuContentView.findViewById(R.id.menu_measure);
        mMeasure.setOnClickListener(menuChildViewClickListener);
        Button mMenuClampMove = (Button) mMenuContentView.findViewById(R.id.menu_clamp_move);
        mMenuClampMove.setOnClickListener(menuChildViewClickListener);
        Button mMenuStop = (Button) mMenuContentView.findViewById(R.id.menu_stop);
        mMenuStop.setOnClickListener(menuChildViewClickListener);
        mMenuWindow.setContentView(mMenuContentView);
        mMenuWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuWindow.setBackgroundDrawable(cd);
        mMenuWindow.setAnimationStyle(R.style.PopupAnimation);
        //检测定位方式，选择窗口
        View mLocationContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_location_select, null);
        mDetectionLocationMode = new PopupWindow(this);
        mDetectionLocationMode.setContentView(mLocationContentView);
        mDetectionLocationMode.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置宽度
        mDetectionLocationMode.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置高度
        mDetectionLocationMode.setBackgroundDrawable(cd);  //设置背景为透明
        mIvNoDetect = (ImageView) mLocationContentView.findViewById(R.id.ib_no_detect);  //不检测
        mIvNoDetect.setOnClickListener(detectionLocationModeListener);
        mIvUpDownLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_up_down_location);//上下定位
        mIvUpDownLocation.setOnClickListener(detectionLocationModeListener);
        mIvBilateralLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_bilateral_location);//两边定位
        mIvBilateralLocation.setOnClickListener(detectionLocationModeListener);
        mIvCuspLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_cusp_location); //尖端定位
        mIvCuspLocation.setOnClickListener(detectionLocationModeListener);
        mIvThreeTerminalLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_three_terminal_location);//三端定位
        mIvThreeTerminalLocation.setOnClickListener(detectionLocationModeListener);
    }

    /**
     * 钥匙检测定位方式,选择监听器
     */
    View.OnClickListener detectionLocationModeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ib_no_detect://不检测
                    if (readDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mIvNoDetect.setImageResource(R.drawable.no_detect);
                    mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    readDetectionMode = 0;
                    isShowLocationSelectWindow = false;
                    mDetectionLocationMode.dismiss();
                    break;
                case R.id.ib_up_down_location://上下定位
                    if (readDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    readDetectionMode = 1;
                    isShowLocationSelectWindow = false;
                    mDetectionLocationMode.dismiss();
                    break;
                case R.id.ib_bilateral_location://两边定位
                    if (readDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    readDetectionMode = 2;
                    isShowLocationSelectWindow = false;
                    mDetectionLocationMode.dismiss();
                    break;
                case R.id.ib_cusp_location://尖端定位
                    if (readDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mIvCuspLocation.setImageResource(R.drawable.btnautosensetippressedimage_frmkeydecode_dodecoderes);
                    mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                    readDetectionMode = 3;
                    isShowLocationSelectWindow = false;
                    mDetectionLocationMode.dismiss();
                    break;
                case R.id.ib_three_terminal_location://三端定位

                        if (readDetectionMode == Detection.MODE_NO_DETECT) {
                            mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                        } else if (readDetectionMode == Detection.MODE_UP_DOWN_LOCATION) {
                            mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                        } else if (readDetectionMode == Detection.MODE_BOTHSIDE_LOCATION) {
                            mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                        } else if (readDetectionMode == Detection.MODE_FRONTEND_LOCATION) {
                            mIvCuspLocation.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                        }
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    if(readDetectionMode!=Detection.MODE_THREETERMINAL_LOCATION){
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
                        readDetectionMode = Detection.MODE_THREETERMINAL_LOCATION;
                    }
                    isShowLocationSelectWindow = false;
                    mDetectionLocationMode.dismiss();
                    break;
            }
        }
    };

    /**
     * 根据钥匙类型判断检测定位方式。
     * 设置定位方式
     */
    private void judgeKeyDetectLocationMode() {
        if (ki.getType() == KeyInfo.BILATERAL_KEY) {  //双边齿
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode = Detection.MODE_NO_DETECT;
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                //夹具判断
                readDetectionMode = Detection.MODE_FRONTEND_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetippressedimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            }
        } else if (ki.getType() == KeyInfo.UNILATERAL_KEY) {  //单边齿
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode =Detection.MODE_NO_DETECT;
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode =Detection.MODE_NO_DETECT;
            }
        } else if (ki.getType() == KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY) {  //  双轨内槽钥匙
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                readDetectionMode =Detection.MODE_BOTHSIDE_LOCATION ;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                readDetectionMode =Detection.MODE_THREETERMINAL_LOCATION ;  //三端定位
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
            }
        } else if (ki.getType() == KeyInfo.MONORAIL_OUTER_GROOVE_KEY) {  //单轨外槽
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                readDetectionMode = Detection.MODE_BOTHSIDE_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                readDetectionMode = Detection.MODE_THREETERMINAL_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
            }
        } else if (ki.getType() == KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY) {  //双轨外槽钥匙
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                readDetectionMode = Detection.MODE_BOTHSIDE_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                readDetectionMode = Detection.MODE_THREETERMINAL_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
            }
        } else if (ki.getType() == KeyInfo.MONORAIL_INSIDE_GROOVE_KEY) {
            if (ki.getAlign() ==  KeyInfo.SHOULDERS_ALIGN) {
                readDetectionMode = Detection.MODE_BOTHSIDE_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                readDetectionMode =Detection.MODE_THREETERMINAL_LOCATION ;  //三端定位
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
            }
        }else if(ki.getType()==KeyInfo.CONCAVE_DOT_KEY){   //凹点钥匙
            if (ki.getAlign() ==  KeyInfo.SHOULDERS_ALIGN) {
                readDetectionMode = Detection.MODE_NO_DETECT;
                mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.no_detect);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                readDetectionMode = Detection.MODE_FRONTEND_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetippressedimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            }
        }else if(ki.getType()==KeyInfo.ANGLE_KEY){
           //角度钥匙没有
        }else if(ki.getType()==KeyInfo.CYLINDER_KEY){
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode =Detection.MODE_NO_DETECT;
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode =Detection.MODE_NO_DETECT;
            }
        }else if(ki.getType()==KeyInfo.SIDE_TOOTH_KEY){

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_probe:
                if (isShowMenu) {
                    mMenuWindow.dismiss();
                    isShowMenu = false;
                }
                if (isProbeWindowShow) {
                    mProbeHintWindow.dismiss();
                    isProbeWindowShow = false;
                } else {
                    mProbeHintWindow.showAsDropDown(mBtnProbe, 130, -116);
                    isProbeWindowShow = true;
                }
                if (isShowLocationSelectWindow) {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.dismiss();
                    isShowLocationSelectWindow = false;
                }
                break;
            case R.id.btn_decode:   //解读钥匙
                //保存钥匙信息
                SaveDataDaoManager.saveDataToCutHistory(ki);
                //定位槽      //定位方式   //是否四舍五入
                readOrder = mKey.getReadOrder(locatingSlot, readDetectionMode, isRound);
                EventBusUtils.post(new MessageEvent(MessageEvent.CLAMP_STATE_INDEX,index));
                switch (ki.getType()) {
                    case KeyInfo.BILATERAL_KEY:
                        if (serialDriver != null) {
                            serialDriver.write(readOrder.getBytes(), readOrder.length());
                        }
                        mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                        mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                        mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                        mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                        mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                        mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                        mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                        mKey.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                        break;
                    case KeyInfo.UNILATERAL_KEY:
                        OperateTipsActivity.startItselfActivityForResult(this,ki.getType(),readOrder, clampAvailable);
                        break;
                    case KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY:    //双轨内槽钥匙
                        if(readDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||readDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), readOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(readOrder.getBytes(), readOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                            mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                            mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                            mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                            mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                            mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                            mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                            mKey.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                        }
                        break;
                    case KeyInfo.MONORAIL_OUTER_GROOVE_KEY:
                        if(readDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||readDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), readOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(readOrder.getBytes(), readOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                            mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                            mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                            mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                            mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                            mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                            mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                            mKey.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                        }
                        break;
                    case KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY:
                        if(readDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||readDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), readOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(readOrder.getBytes(), readOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                            mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                            mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                            mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                            mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                            mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                            mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                            mKey.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                        }
                        break;
                    case KeyInfo.MONORAIL_INSIDE_GROOVE_KEY:
                        if(readDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||readDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), readOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(readOrder.getBytes(), readOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                            mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                            mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                            mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                            mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                            mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                            mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                            mKey.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                        }
                        break;
                    case KeyInfo.CONCAVE_DOT_KEY:   //凹点钥匙
                        if(ki.getRowCount()>1){
                            String[]  rowPos =ki.getRow_pos().split(";");
                            int  valueSide;
                            boolean isHaveSide = false;
                            boolean isPathwayEquality=false;
                            //数
                            int num=0;
                            for (int i = 0; i <rowPos.length ; i++) {
                                valueSide  =Integer.parseInt(rowPos[i]);
                                if(valueSide<=0){
                                    isHaveSide=true;
                                    break;
                                }
                                if(valueSide==num){
                                    isPathwayEquality=true;
                                    break;
                                }else {
                                    num=valueSide;
                                }
                            }
                            if(isHaveSide==true||isPathwayEquality==true){

                            }else {
                                OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), readOrder,ConstantValue.PROBE_CUSP);
                            }
                        }else {
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), readOrder,ConstantValue.PROBE_CUSP);
                        }
                        break;
                    case KeyInfo.ANGLE_KEY:  //角度钥匙
                        break;
                    case KeyInfo.CYLINDER_KEY:  //圆筒钥匙
                        if (serialDriver != null) {
                            serialDriver.write(readOrder.getBytes(),readOrder.length());
                        }
                        mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                        mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                        mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                        mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                        mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                        mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                        mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                        mKey.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                        break;
                    case KeyInfo.SIDE_TOOTH_KEY:  //侧齿钥匙
                        break;
                }
                LogUtils.d("读钥匙指令", readOrder);
                judgeMenuWindowIsHide();
                if (isShowLocationSelectWindow) {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.dismiss();
                    isShowLocationSelectWindow = false;
                }
                isProbeWindowShow = false;
                break;
            case R.id.lv_clamp_location_select:   //夹具定位选择
                clampLocationSlotImgSelect();  //夹具选择切换
                if (isProbeWindowShow) {
                    mProbeHintWindow.dismiss();
                    isProbeWindowShow = false;
                }
                if (isShowLocationSelectWindow) {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.dismiss();
                    isShowLocationSelectWindow = false;

                }
                judgeMenuWindowIsHide();
                break;
            case R.id.rl_menu:
                if (isProbeWindowShow) {
                    mProbeHintWindow.dismiss();
                    isProbeWindowShow = false;
                }
                if (isShowLocationSelectWindow) {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.dismiss();
                    isShowLocationSelectWindow = false;
                }
                if (isShowMenu) {
                    mMenuWindow.dismiss();
                    isShowMenu = false;
                } else {
                    mMenuWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
                    isShowMenu = true;
                }
                break;
            case R.id.btn_stop_decode:
                //停止读齿
                if (serialDriver != null) {
                    serialDriver.write(Instruction.STOP_OPERATE.getBytes(), Instruction.STOP_OPERATE.length());
                }
                if (mDecode.getVisibility() == View.INVISIBLE) {
                    mKey.setToothCodeDefault();//设置齿的深度名全部为默认X
                    mKey.setVisibility(View.GONE);
                    mKey.setToothCodeDefault();
                   mIvClampLocationSelect.setVisibility(View.VISIBLE);
                    mBtnMenu.setVisibility(View.VISIBLE);
                    mDecode.setVisibility(View.VISIBLE);
                    mStopDecode.setVisibility(View.INVISIBLE);
                    mBtnBack.setVisibility(View.VISIBLE);
                    //显示指南
                    mTvGuide.setVisibility(View.VISIBLE);
                    mTvHint.setText("Place a mKey blank as shown in the below figure.");
                }else {
                    mMeasure.setTextColor(Color.parseColor("#636363"));
                   mMeasure.setClickable(false);
                    judgeMenuWindowIsHide();
                }
                break;
            case R.id.btn_locate_mode:  //钥匙检测方式选择
                if (isProbeWindowShow) {
                    mProbeHintWindow.dismiss();
                    isProbeWindowShow = false;
                }
                judgeMenuWindowIsHide();
                if (isShowLocationSelectWindow == false) {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.no_detect);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensetippressedimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.showAsDropDown(mIvLocateMode, 100, -99);
                    Log.d("进来了？", "onClick: ");
                    isShowLocationSelectWindow = true;
                } else {
                    if (readDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    }
                    mDetectionLocationMode.dismiss();
                    isShowLocationSelectWindow = false;
                }
                break;
        }
    }

    /**
     * 判断菜单窗口是否隐藏
     */
    private void  judgeMenuWindowIsHide(){
        if (isShowMenu) {
            mMenuWindow.dismiss();
            isShowMenu = false;
        }
    }
    /**
     * 夹具定位槽图片选择
     */
    private void clampLocationSlotImgSelect() {

            index++;
        if(index>clampList.size()-1){
            index=0;
        }
            cl = clampList.get(index);
            mIvClampLocationSelect.setImageResource(cl.getImgHint());
            locatingSlot = cl.getLocationSlot();
            keyClamp = cl.getClampType();
    }

    /**
     * 菜单View点击监听器
     */

    View.OnClickListener menuChildViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_home://回到主界面
                    menuChildViewIntent.setClass(DecodeKeyActivity.this, MainActivity.class);
                    menuChildViewIntent.setFlags(menuChildViewIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(menuChildViewIntent);
                    break;
                case R.id.menu_calibration://打开校准界面
                    mMenuWindow.dismiss();
                    FrmMaintenanceActivity.startFrmMaintenanceActivity(DecodeKeyActivity.this,languageMap);
                    break;
                case R.id.menu_measure:  //打开校准探针和切割刀高度界面
                    ProbeAndCutterMeasurementActivity.
                            startItselfActivity(DecodeKeyActivity.this,ProbeAndCutterMeasurementActivity.FlAG_PROBE_MEASUREMENT);
                    break;
                case R.id.menu_clamp_move:   //夹具移动
                    if (serialDriver != null) {
                    serialDriver.write(Instruction.FIXTURE_MOVE.getBytes(), Instruction.FIXTURE_MOVE.length());
                    }
                    break;
                case R.id.menu_stop:   //操作动作停止
                    if (serialDriver != null) {
                        serialDriver.write(Instruction.STOP_OPERATE.getBytes(), Instruction.STOP_OPERATE.length());
                    }
                    if (mMeasure.isClickable() == false) {
                        mMeasure.setTextColor(Color.parseColor("#020701"));
                        mMeasure.setClickable(true);
                        isShowMenu = false;
                        mMenuWindow.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if (isProbeWindowShow) {
                mProbeHintWindow.dismiss();
                isProbeWindowShow = false;
            }
            if (isShowMenu) {
                mMenuWindow.dismiss();
                isShowMenu = false;
            }
            if (isShowLocationSelectWindow) {
                if (readDetectionMode == 0) {
                    mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                } else if (readDetectionMode == 1) {
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                } else if (readDetectionMode == 2) {
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                } else if (readDetectionMode == 3) {
                    mIvLocateMode.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                } else if (readDetectionMode == 4) {
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                }
                mDetectionLocationMode.dismiss();
                isShowLocationSelectWindow = false;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }


    @Override
    /**
     * 当Activity加载完毕获得焦点的时候调用，失去焦点也会调用
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isShowWindow) {//用一个阀值做判断,不然会重复执行很多次
            mProbeHintWindow.showAsDropDown(mBtnProbe, 130, -116);
            timer.start();
            isShowWindow = false;

        }
    }


    /**
     * 倒计时类
     */
    CountDownTimer timer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mProbeHintWindow.dismiss();
            isProbeWindowShow = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (isShowMenu) {
            mMenuWindow.dismiss();
        }
        if (isProbeWindowShow) {
            mProbeHintWindow.dismiss();
        }
        if (isShowLocationSelectWindow) {
            mProbeHintWindow.dismiss();
        }
        Log.d("DecodeActivity", "onDestroy: ");
    }

    /**
     * 根据类型设置要绘制的钥匙
     */

    private void addKeyViewType() {
        if (ki.getType() == KeyInfo.BILATERAL_KEY) {//双边钥匙
            mKey = new BilateralKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(710,270));
            mKey.setDrawPatternSize(690,260);
            mKey.setToothCodeDefault();
            mKey.setDrawToothWidth(20);
            mKey.setShowArrows(false);
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.UNILATERAL_KEY) {  //单边钥匙
            mKey = new UnilateralKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(680,285));
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setDrawPatternSize(670,280);
            mKey.setDrawToothWidth(20);
            mKey.setShowArrows(false);
            mKey.setToothCodeDefault();
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.DUAL_PATH_INSIDE_GROOVE_KEY) {  //双轨内槽
            mKey = new DualPathInsideGrooveKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,640), DensityUtils.dip2px(this,250)));
            mKey.setDrawPatternSize(DensityUtils.dip2px(this,630),DensityUtils.dip2px(this,245));
            mKey.setShowArrows(false);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setToothCodeDefault();
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.MONORAIL_OUTER_GROOVE_KEY) { //单轨外槽钥匙
            mKey = new MonorailOuterGrooveKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,650),DensityUtils.dip2px(this,235)));
            mKey.setDrawPatternSize(DensityUtils.dip2px(this,645),DensityUtils.dip2px(this,230));
            mKey.setShowArrows(false);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setToothCodeDefault();
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.DUAL_PATH_OUTER_GROOVE_KEY) {  //双轨外槽钥匙
            mKey = new DualPathOuterGrooveKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620), DensityUtils.dip2px(this,216)));
            mKey.setDrawPatternSize(DensityUtils.dip2px(this,610),DensityUtils.dip2px(this,215));
            mKey.setShowArrows(false);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setToothCodeDefault();
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.MONORAIL_INSIDE_GROOVE_KEY) {  //单轨内槽钥匙
            mKey = new MonorailInsideGrooveKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 300));
            mKey.setDrawPatternSize(736,296);
            mKey.setShowArrows(false);
            mKey.setToothCodeDefault();
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey);
        } else if (ki.getType()== KeyType.CONCAVE_DOT_KEY) {  //凹点钥匙
            String[] newStr=ki.getRow_pos().split(";");
            if(ki.getRowCount()==1&&Integer.parseInt(newStr[0])<0){
                mKey = new ConcaveDotKey(this,ki);
                LinearLayout.LayoutParams layoutParam=new LinearLayout.LayoutParams(DensityUtils.dip2px(this,550), DensityUtils.dip2px(this,80));
                layoutParam.setMargins(0,DensityUtils.dip2px(this,90),0,0);
                mKey.setLayoutParams(layoutParam);
                mKey.setOnlyDrawSidePatternSize(DensityUtils.dip2px(this,548),DensityUtils.dip2px(this,78));
                mKey.setDrawBigCircleAndInnerCircleSize(14,6);
                mKey.setShowArrows(false);
                mKey.setToothCodeDefault();
                mKey.setBackgroundResource(R.drawable.edit_shape);
                mKey.setVisibility(View.GONE);
                mLlAddKey.addView(mKey,0);
            }else {
                mKey = new ConcaveDotKey(this,ki);
                mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620), DensityUtils.dip2px(this,210)));
                mKey.setDrawPatternSize(DensityUtils.dip2px(this,618),DensityUtils.dip2px(this,208));
                mKey.setShowArrows(false);
                mKey.setDrawBigCircleAndInnerCircleSize(14,6);
                mKey.setToothCodeDefault();
                mKey.setBackgroundResource(R.drawable.edit_shape);
                mKey.setVisibility(View.GONE);
                mLlAddKey.addView(mKey,0);
            }
        } else if (ki.getType() == KeyType.ANGLE_KEY) {   //角度钥匙
            mKey = new AngleKey(this,ki);
            mKey.setShowArrows(false);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(740,240));
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setDrawPatternSize(738,238);
            mKey.setToothCodeDefault();
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.CYLINDER_KEY) {  //圆筒钥匙
            mKey = new CylinderKey(this,ki);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(305,305);
            layoutParams.gravity=Gravity.CENTER;
            mKey.setLayoutParams(layoutParams);
            mKey.setDrawPatternSize(300,300);
            mKey.setToothCodeDefault();
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        } else if (ki.getType() == KeyType.SIDE_TOOTH_KEY) {  //侧齿钥匙
            mKey = new SideToothKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(740,250));
            mKey.setDrawPatternSize(730,248);
            mKey.setShowArrows(false);
            mKey.setToothCodeDefault();
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setVisibility(View.GONE);
            mLlAddKey.addView(mKey, 0);   //添加View
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == ConstantValue.START_MEASURE_PROBE) {
            mMeasure.setTextColor(Color.parseColor("#636363"));
            mMeasure.setClickable(false);
        } else if (requestCode == 1 && resultCode == ConstantValue.START_OPERATE) {  //开始读钥匙
            mStopDecode.setVisibility(View.VISIBLE);
            mDecode.setVisibility(View.INVISIBLE);
            mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
            mKey.setVisibility(View.VISIBLE);
            mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
            mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
            mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
            if (isShowMenu) {   //菜单显示就隐藏
                mMenuWindow.dismiss();
                isShowMenu = false;
            }
        }
    }


}
