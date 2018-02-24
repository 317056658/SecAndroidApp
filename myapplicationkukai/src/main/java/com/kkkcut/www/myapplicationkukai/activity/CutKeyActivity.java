package com.kkkcut.www.myapplicationkukai.activity;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.kkkcut.www.myapplicationkukai.activity.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.ActivityWindowUtils;
import com.kkkcut.www.myapplicationkukai.utils.ArithUtils;
import com.kkkcut.www.myapplicationkukai.utils.DensityUtils;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kkkcut.www.myapplicationkukai.R.id.btn_stop_cut;
import static com.kkkcut.www.myapplicationkukai.utils.Tools.removeUnnecessaryZero;

public class CutKeyActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBack,mCutterDiameter,mMenu, mCutKey,mStopCut,mBtnPassCut;
    private TextView mQueryName, mClampTextHint;
    private View mDecorView;
    private PopupWindow mSpeedSelectWindow;
    private View mRootView;
    private ImageView mSpeed, mIvClampLocationSelect, mIvLocateMode;
    private KeyInfo ki;
    private Button mSteelSpeed, mSilverSpeed, mBrassSpeed, mHighSpeed, mMeasureCutter;
    private ImageView mAddDiameter, mSubtractDiameter;
    private PopupWindow mCutterDiameterChangeWindow;
    private PopupWindow mMenuWindow;
    private PopupWindow mCutDepthSwitchoverWindow;
    private boolean isShowCutterWindow = true;
    private boolean isShowCutterDiameterWindow;
    private boolean isShowSpeedWindow = false;
    private boolean isShowCutDepthWindow;
    private TextView mCutterDiameterText,mCutterSpecification;
    //刀具直径的初始数据
    private double diameterData = 2;
    private String cutterDiameterValue ="2";
    private float deepestDepthValue;
    private int keyClamp,index, clampAvailable;
    private boolean isShowMeun=false;
    private int locatingSlot;
    private String cutOrder;
    private Key mKey;
    private LinearLayout mPutKey;
    private TextView mTvProgress,mTvTextHint1, mTvTextGuideHint,mTvTextCutHint;
    private List<Clamp> clampList;
    private Clamp cl;
    private PopupWindow mCutLocationMode;
    private ImageView mIvNoDetect,mIvUpDownLocation,mIvBilateralLocation,mIvCuspLocation,mIvThreeTerminalLocation;
    private int cutDetectionMode;  //切割检测方式
    private  boolean isShowLocationModeWindow =false;
    private int cutSpeed =3;  //默认速度是3;
    private ProgressBar mPbLoading;
    private RelativeLayout mRlProgressBar;
    private String assistClamp="";
    private int startFlag;
    private Button mBtnCutDepth,mBtnFirstCut,mBtnSecondCut;
    private int cutDepth;
    private TextView mTvCutDepthValue;
    private int cutToken;  //切割记号
    private Button mBtnZCutHierarchy;
    private Button  mBtnZCutHierarchy1;
    private Button  mBtnZCutHierarchy2;
    private Button   mBtnZCutHierarchy3;
    private LinearLayout mLlZCutHierarchySelect;
    private int ZCutHierarchyValue=1;   //默认为1
    private  int defaultLength=1300;;




    private MyHandler mHandler = new MyHandler(this);
    private HashMap<String, String> languageMap;
    private String step;

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        Intent exceptionIntent=new Intent();
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }
        /**
         * 计算 进度值
         * @param c1
         * @param c2
         */
        private int  calculateProgress(char c1,char c2){
            //得到百分比
            float percent =Float.parseFloat(""+c1)/Float.parseFloat(""+c2);
            return  (int)(percent*100);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            CutKeyActivity activity=(CutKeyActivity)reference.get();
            String str ="";
            if(msg.obj!=null){
                str =msg.obj.toString();
            }
            switch (msg.what){
                case MicyocoEvent.OPERATION_FINISH://操作完成
                    if(!activity.mMeasureCutter.isClickable()){
                        activity.mMeasureCutter.setTextColor(Color.parseColor("#020701"));
                        activity.mMeasureCutter.setClickable(true);
                        activity.isShowMeun=false;
                        activity.mMenuWindow.dismiss();
                        Log.d("mMeasureCutter", "onMessageEventMain: ");
                    }else if(activity.mCutKey.getVisibility()==View.INVISIBLE){
                        if(activity.cutToken==1){
                            activity.mKey.setVisibility(View.GONE);  //隐藏钥匙
                            activity.mIvClampLocationSelect.setVisibility(View.VISIBLE);  //显示这个夹具类型选择ImageView
                            activity.mTvTextHint1.setVisibility(View.VISIBLE);
                            activity.mTvTextGuideHint.setVisibility(View.VISIBLE);
                            activity.mTvTextCutHint.setVisibility(View.INVISIBLE);
                            activity.mMenu.setVisibility(View.VISIBLE);
                            activity.mRlProgressBar.setVisibility(View.INVISIBLE);
                            activity.mTvProgress.setText("0");
                            activity.mPbLoading.setProgress(0);
                            activity.mStopCut.setVisibility(View.INVISIBLE);
                            activity.mCutKey.setVisibility(View.VISIBLE);
                            activity.mCutKey.setBackgroundResource(R.drawable.btncutimage1_frmkeycut_docutres);
                            activity.mBack.setVisibility(View.VISIBLE);
                            if(activity.ki.getType()!=KeyType.UNILATERAL_KEY&&activity.ki.getType()!=KeyType.BILATERAL_KEY){
                                activity.mBtnFirstCut.setBackgroundResource(R.drawable.btntopimage_frmkeycut_docutres);
                                activity.mBtnFirstCut.setTextColor(Color.BLACK);
                                activity.mBtnSecondCut.setVisibility(View.VISIBLE);
                                activity.mBtnSecondCut.setBackgroundResource(R.drawable.btnbottomdisabledimage_frmkeycut_docutres);
                                activity.mBtnSecondCut.setTextColor(Color.parseColor("#B5B5B5"));
                            }
                            activity.cutToken=2;
                        }else if(activity.cutToken==2){
                            if(activity.mBtnFirstCut.getVisibility()==View.INVISIBLE){
                                activity.finish();
                            }
                        }else {
                            activity.finish();
                        }
                    }
                    break;
                case MicyocoEvent.USER_CANCEL_OPERATE://用户操作取消
                    if(activity.mCutKey.getVisibility()==View.INVISIBLE){
                        activity.mKey.setVisibility(View.GONE);  //隐藏钥匙
                        activity.mIvClampLocationSelect.setVisibility(View.VISIBLE);  //显示这个夹具类型选择ImageView
                        activity.mTvTextHint1.setVisibility(View.VISIBLE);
                        activity.mTvTextGuideHint.setVisibility(View.VISIBLE);
                        activity.mTvTextCutHint.setVisibility(View.INVISIBLE);
                        activity.mMenu.setVisibility(View.VISIBLE);
                        activity.mRlProgressBar.setVisibility(View.INVISIBLE);
                        activity.mTvProgress.setText("0");
                        activity.mPbLoading.setProgress(0);
                        activity.mStopCut.setVisibility(View.INVISIBLE);
                        activity.mCutKey.setVisibility(View.VISIBLE);
                        activity.mCutKey.setBackgroundResource(R.drawable.btncutimage1_frmkeycut_docutres);
                        activity.mCutKey.setClickable(true);
                        activity.mBack.setVisibility(View.VISIBLE);
                        if(activity.ki.getType()!=KeyType.UNILATERAL_KEY&&activity.ki.getType()!=KeyType.BILATERAL_KEY){
                            activity.mBtnSecondCut.setVisibility(View.VISIBLE);
                            activity.mBtnFirstCut.setVisibility(View.VISIBLE);
                        }
                    } else  if(activity.mMeasureCutter.isClickable()==false){
                        activity.mMeasureCutter.setTextColor(Color.parseColor("#020701"));
                        activity.mMeasureCutter.setClickable(true);
                    }
                    exceptionIntent.setClass(activity,ExceptionActivity.class);
                    exceptionIntent.putExtra("exception", str);
                    activity.startActivity(exceptionIntent);
                    break;
                case MicyocoEvent.CUT_DATA_BACK:  //代表下位机返回的 切割齿消息
                    String data = str.replaceAll("[!PG,;]","");
                    int progress = calculateProgress(data.charAt(0), data.charAt(1));
                    activity.mTvProgress.setText(progress+"%");  //设置显示的百分比进度
                    activity.mPbLoading.setProgress(progress); //设置加载的进度
                    break;
                case MicyocoEvent.SAFETYGATE_OPEN:  //安全们打开
                    ExceptionActivity.startItselfActivity(activity, str);
                    break;
                case MicyocoEvent.MATERIAL_OR_POSITION_ERROR:  //钥匙位置不正确或者材料不正确
                    ExceptionActivity.startItselfActivity(activity, str);
                    break;
            }
        }
    }
  private ProlificSerialDriver serialDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWindowUtils.setScreenNoDormant(getWindow());
        setContentView(R.layout.activity_cut_key);
        serialDriver=ProlificSerialDriver.getInstance(getApplicationContext());
        getIntentData();
        initViews();
        initPopupWindow();
        keyTypeJudgeShowBtn();  //根据钥匙类型判断显示按钮
        clampJudge(ki);  //夹具判断
        judgeKeyCutLocationMode();  //判断钥匙的切割定位方式
        addKeyViewType();//准备好要绘制的钥匙
    }
    public  static  void  startCutKeyActivity(Context context, KeyInfo ki, HashMap<String,String> languageMap,String step,int startFlag){
          Intent  intent=new Intent(context,CutKeyActivity.class);
            intent.putExtra("keyInfo",ki);
            intent.putExtra("language",languageMap);
            intent.putExtra("step",step);
         intent.putExtra("startFlag",startFlag);
        context.startActivity(intent);
    }
    private void getIntentData(){
          Intent  intent=getIntent();
        startFlag = intent.getIntExtra("startFlag", -1);
        ki = intent.getParcelableExtra("keyInfo");
        languageMap = (HashMap<String, String>) intent.getSerializableExtra("language");
        step = intent.getStringExtra("step");
    }


    /**
     *  根据钥匙类型判断显示按钮
     */
    private void  keyTypeJudgeShowBtn(){
             if(ki.getType()==KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY){   //双轨内槽
                 //显示切割第几面的按钮
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
                 //显示Z切割层次按钮
                 mBtnZCutHierarchy.setVisibility(View.VISIBLE);
                 mBtnZCutHierarchy.setText("Z Pitch: 2");
                 ZCutHierarchyValue =2;
                 //显示切割深度按钮
                 if(ki.getCutDepth()!=0){
                     cutDepth= ki.getCutDepth();
                     String str= removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mBtnCutDepth.setVisibility(View.VISIBLE);
                     mTvCutDepthValue.setText(str+"mm");
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mBtnCutDepth.setVisibility(View.VISIBLE);
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }else if(ki.getType()==KeyInfo.MONORAIL_OUTER_GROOVE_KEY){  //单轨外槽
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
                 mBtnCutDepth.setVisibility(View.VISIBLE);  //显示切割深度按钮
                 if(ki.getCutDepth()!=0){
                     cutDepth= ki.getCutDepth();
                     String str= removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mBtnCutDepth.setVisibility(View.VISIBLE);
                     mTvCutDepthValue.setText(str+"mm");
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mBtnCutDepth.setVisibility(View.VISIBLE);
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }else if(ki.getType()==KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY){
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
                 mBtnCutDepth.setVisibility(View.VISIBLE);

                 if(ki.getCutDepth()!=0){
                     cutDepth= ki.getCutDepth();
                     String str= removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mTvCutDepthValue.setText(str+"mm");
                     cutDepth= ki.getCutDepth();
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }else if(ki.getType()==KeyInfo.MONORAIL_INSIDE_GROOVE_KEY){  //单轨内槽
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
                 mBtnZCutHierarchy.setText("Z Pitch:2");
                 mBtnZCutHierarchy.setVisibility(View.VISIBLE);
                 mBtnCutDepth.setVisibility(View.VISIBLE);  //显示切割深度按钮
                 ZCutHierarchyValue=2;  //设置为2;
                 if(ki.getCutDepth()!=0){
                     cutDepth= ki.getCutDepth();
                     String str= removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mTvCutDepthValue.setText(str+"mm");
                     cutDepth= ki.getCutDepth();
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }
             else if(ki.getType()==KeyInfo.CONCAVE_DOT_KEY){
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
             }else if(ki.getType()== KeyInfo.ANGLE_KEY){
                   mBtnPassCut.setVisibility(View.INVISIBLE);
             } else if(ki.getType()==KeyInfo.SIDE_TOOTH_KEY){  //等于侧齿钥匙
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
             }


    }
    /**
     * 根据钥匙类型判断切割定位方式。
     * 设置定位方式
     */
    private void judgeKeyCutLocationMode() {
        if (ki.getType() == KeyInfo.BILATERAL_KEY) {  //双边齿
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                cutDetectionMode = Detection.MODE_BOTHSIDE_LOCATION;
                mIvLocateMode.setImageResource( R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);//不能点击
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);  //不能点击
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                cutDetectionMode =Detection.MODE_THREETERMINAL_LOCATION;  //默认为0 不检测
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
            }
        } else if (ki.getType() == KeyInfo.UNILATERAL_KEY) {  //单边齿
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                cutDetectionMode =Detection.MODE_NO_DETECT;
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                cutDetectionMode =Detection.MODE_NO_DETECT;
            }
        }else if(ki.getType()==KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY){
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                cutDetectionMode =Detection.MODE_BOTHSIDE_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){  //前端
                cutDetectionMode =Detection.MODE_THREETERMINAL_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
            }
        }else if(ki.getType()==KeyInfo.MONORAIL_OUTER_GROOVE_KEY){  //单轨外槽
               if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                   cutDetectionMode =Detection.MODE_BOTHSIDE_LOCATION;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                   mIvCuspLocation.setClickable(false);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setClickable(false);
               }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
                   cutDetectionMode =Detection.MODE_THREETERMINAL_LOCATION;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
               }
        }else if(ki.getType()==KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY){  //双轨外槽
               if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                   cutDetectionMode =Detection.MODE_BOTHSIDE_LOCATION;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                   mIvCuspLocation.setClickable(false);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setClickable(false);
               }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
                   cutDetectionMode =Detection.MODE_THREETERMINAL_LOCATION;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
               }
        }else if(ki.getType()==KeyInfo.MONORAIL_INSIDE_GROOVE_KEY){
            if(ki.getAlign()==KeyInfo.SHOULDERS_ALIGN){
                cutDetectionMode=Detection.MODE_BOTHSIDE_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }else if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
                cutDetectionMode=Detection.MODE_THREETERMINAL_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
            }
        } if(ki.getType()==KeyInfo.CONCAVE_DOT_KEY){   //凹点钥匙
            if (ki.getAlign() ==  KeyInfo.SHOULDERS_ALIGN) {
                cutDetectionMode = Detection.MODE_NO_DETECT;
                mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenopressedimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeycut_docutres);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                cutDetectionMode = Detection.MODE_FRONTEND_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeycut_docutres);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetippressedimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }
        }else if(ki.getType()==KeyInfo.ANGLE_KEY){   //角度钥匙
            if(ki.getAlign()==KeyInfo.FRONTEND_ALIGN){
                cutDetectionMode = Detection.MODE_UP_DOWN_LOCATION;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeycut_docutres);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }
        }else if(ki.getType()==KeyInfo.CYLINDER_KEY){
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                cutDetectionMode =Detection.MODE_NO_DETECT;
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                cutDetectionMode =Detection.MODE_NO_DETECT;
            }
        }else if(ki.getType()==KeyInfo.SIDE_TOOTH_KEY){
            if (ki.getAlign() == KeyInfo.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {
                cutDetectionMode = Detection.MODE_NO_DETECT;
                mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenopressedimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterdisabledimage_frmkeycut_docutres);
                mIvBilateralLocation.setClickable(false);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }
        }
    }
    /**
     * 夹具判断
     */
    private void clampJudge(KeyInfo ki) {
        clampList =new ArrayList<>();
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
                deepestDepthValue = Float.parseFloat(depths[depths.length - 1]) * 0.01f;
                if (deepestDepthValue > Clamp.THREE_NUMBER_CLAMP_AVAILABLE_FIVE_MM) {
                    clampAvailable = Clamp.THREE_NUMBER_CLAMP_FIVE_MM_AVAILABLE;
                    mClampTextHint.setText("Available Clamp:3.5mm,5mm");
                } else if (deepestDepthValue > Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {
                    clampAvailable = Clamp.THREE_NUMBER_CLAMP_THREE_POINT_FIVE_MM_AVAILABLE;
                    mClampTextHint.setText("Available Clamp:3.5mm");
                } else if (deepestDepthValue <= Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {    //小于3.5  这钥匙数据不对
                    Intent messageHintIntent = new Intent(this, MessageTipsActivity.class);
                    messageHintIntent.putExtra("msgTips", 3);
                    messageHintIntent.putExtra("IntentFlag", startFlag);
                    startActivity(messageHintIntent);
                }
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {  //前端定位
                deepestDepthValue = Float.parseFloat(depths[depths.length - 1]) * 0.01f;
                Clamp cl = new Clamp();
                cl.setImgHint(R.drawable.a9_aux_single_std_2);
                cl.setLocationSlot(17);  //定位槽为17
                cl.setClampType(3);
                clampList.add(cl);
                if (deepestDepthValue > Clamp.THREE_NUMBER_CLAMP_AVAILABLE_FIVE_MM) {
                    clampAvailable = 2;
                    mClampTextHint.setText("Available Clamp:3.5mm,5mm");
                } else if (deepestDepthValue >Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {
                    clampAvailable = 1;
                    mClampTextHint.setText("Available Clamp:3.5mm");
                }else if (deepestDepthValue <= Clamp.THREE_NUMBER_CLAMP_AVAILABLE_THREE_POINT_FIVE_MM) {    //小于3.5  这钥匙数据不对
                    Intent messageHintIntent = new Intent(this, MessageTipsActivity.class);
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
                mTvTextGuideHint.setVisibility(View.INVISIBLE);
            } else if (ki.getAlign() == KeyInfo.FRONTEND_ALIGN) {  //尖端定位
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
        } else if (ki.getType() == KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY) {  //双轨外槽钥匙
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
            if(ki.getShape()==0){
                Clamp   clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_tibbe_stop_3);
                clamp.setLocationSlot(4);
                clamp.setClampType(1);
                clampList.add(clamp);
            }else if(ki.getShape()==1){
                Clamp   clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_clamp_no_5);
                clamp.setLocationSlot(0);
                clamp.setClampType(1);
                clampList.add(clamp);
            }
            mTvTextGuideHint.setVisibility(View.INVISIBLE);  //隐藏占位子
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
                Clamp   clamp = new Clamp();
                clamp.setImgHint(R.drawable.a9_fo19_stop_3);
                clamp.setLocationSlot(4);
                clamp.setClampType(1);
                clampList.add(clamp);
               mTvTextGuideHint.setVisibility(View.GONE);
        }
        if(clampList.size()<=1){
            mIvClampLocationSelect.setClickable(false);
            mTvTextGuideHint.setVisibility(View.INVISIBLE);  //隐藏但占位置
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


    private void initViews() {
        mPutKey = (LinearLayout) findViewById(R.id.ll_put_key);
        mBack = (Button) findViewById(R.id.btn_back_close);
        mBack.setOnClickListener(this);
        mQueryName = (TextView) findViewById(R.id.tv_query_name);
        mQueryName.setText("IC Card " + ki.getCardNumber() + " >");
        mSpeed = (ImageView) findViewById(R.id.iv_speed);
        mSpeed.setOnClickListener(this);
        mCutterDiameter = (Button) findViewById(R.id.btn_cut_knife_diameter);
        mCutterDiameter.setOnClickListener(this);
        mIvClampLocationSelect = (ImageView) findViewById(R.id.iv_clamp_location_select);
        mIvClampLocationSelect.setOnClickListener(this);
        mClampTextHint = (TextView) findViewById(R.id.tv_clamp_peculiarity);
        mClampTextHint.setOnClickListener(this);
        mIvLocateMode = (ImageView) findViewById(R.id.iv_key_cut_location);  //切割定位方式
        mIvLocateMode.setOnClickListener(this);
        mBtnPassCut = (Button)findViewById(R.id.btn_pass_cut);
        mBtnPassCut.setOnClickListener(this);
        mCutKey = (Button)findViewById(R.id.btn_cut_key);
        mCutKey.setOnClickListener(this);
        mBtnFirstCut =(Button)findViewById(R.id.btn_cut_first_face);
        mBtnFirstCut.setOnClickListener(this);
        mBtnSecondCut =(Button)findViewById(R.id.btn_cut_second_face);
        mBtnSecondCut.setOnClickListener(this);
        //停止切割按钮
        mStopCut =(Button)findViewById(btn_stop_cut);
        mStopCut.setOnClickListener(this);

        //当前界面 文本提示1
         mTvTextHint1= (TextView)findViewById(R.id.tv_hint);
        //当前界面 文本提示2
         mTvTextGuideHint =(TextView)findViewById(R.id.tv_guide_hint);
        //钥匙切割文本提示
        mTvTextCutHint=(TextView)findViewById(R.id.tv_cut_hint);
        //获得进度条父布局
        mRlProgressBar = (RelativeLayout)findViewById(R.id.rl_progress_bar);
        //获得进度
        mTvProgress = (TextView)findViewById(R.id.tv_progress);
        //获得进度条
        mPbLoading = (ProgressBar)findViewById(R.id.pb_loading);
        //获得菜单
        mMenu=(Button)findViewById(R.id.btn_frm_menu);
        mMenu.setOnClickListener(this);
        mBtnCutDepth=(Button)findViewById(R.id.btn_cut_depth);
        mBtnCutDepth.setOnClickListener(this);
        mBtnZCutHierarchy=(Button)findViewById(R.id.btn_z_cut_hierarchy);
        mBtnZCutHierarchy.setOnClickListener(ZCutHierarchyClickListener);
        mBtnZCutHierarchy1=(Button)findViewById(R.id.btn_z_cut_hierarchy1);  // 切割层次1
        mBtnZCutHierarchy1.setOnClickListener(ZCutHierarchyClickListener);
        mBtnZCutHierarchy2=(Button)findViewById(R.id.btn_z_cut_hierarchy2);// 切割层次2
        mBtnZCutHierarchy2.setOnClickListener(ZCutHierarchyClickListener);
        mBtnZCutHierarchy3=(Button)findViewById(R.id.btn_z_cut_hierarchy3);// 切割层次3
        mBtnZCutHierarchy3.setOnClickListener(ZCutHierarchyClickListener);
        mLlZCutHierarchySelect = (LinearLayout)findViewById(R.id.ll_z_cut_hierarchy_select);
        mDecorView = getWindow().getDecorView();
    }

    /**
     * 初始化PopupWindow
     */
    private void initPopupWindow() {
        mRootView = LayoutInflater.from(this).inflate(R.layout.activity_cut_key, null);
        //速度选择窗口
        View mSpeedContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_speed, null);
        mSpeedSelectWindow = new PopupWindow(this);
        mSpeedSelectWindow.setContentView(mSpeedContentView);
        mSpeedSelectWindow.setBackgroundDrawable(new ColorDrawable());
        mSpeedSelectWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSpeedSelectWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mSpeedSelectWindow.setOutsideTouchable(true);
        mSteelSpeed = (Button) mSpeedContentView.findViewById(R.id.btn_steel);
        mSteelSpeed.setOnClickListener(popupWindowItemClick);
        mSilverSpeed = (Button) mSpeedContentView.findViewById(R.id.btn_silver);
        mSilverSpeed.setOnClickListener(popupWindowItemClick);
        mBrassSpeed = (Button) mSpeedContentView.findViewById(R.id.btn_brass);
        mBrassSpeed.setOnClickListener(popupWindowItemClick);
        mHighSpeed = (Button) mSpeedContentView.findViewById(R.id.btn_high_speed);
        mHighSpeed.setOnClickListener(popupWindowItemClick);
        //切割定位方式选择窗口
       View mLocationContentView  =LayoutInflater.from(this).inflate(R.layout.popupwindow_location_select, null);
        mCutLocationMode= new PopupWindow(this);
        mCutLocationMode.setContentView(mLocationContentView);
        mCutLocationMode.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutLocationMode.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutLocationMode.setBackgroundDrawable(new ColorDrawable());  //设置窗口背景为透明
        mIvNoDetect = (ImageView) mLocationContentView.findViewById(R.id.ib_no_detect);  //不检测
        mIvNoDetect.setOnClickListener(cutLocationModeSelectListener);
        mIvUpDownLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_up_down_location);//上下定位
        mIvUpDownLocation.setOnClickListener(cutLocationModeSelectListener);
        mIvBilateralLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_bilateral_location);//两边定位
        mIvBilateralLocation.setOnClickListener(cutLocationModeSelectListener);
        mIvCuspLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_cusp_location); //尖端定位
        mIvCuspLocation.setOnClickListener(cutLocationModeSelectListener);
        mIvThreeTerminalLocation = (ImageView) mLocationContentView.findViewById(R.id.ib_three_terminal_location);//三端定位
        mIvThreeTerminalLocation.setOnClickListener(cutLocationModeSelectListener);

        //切割刀直径切换窗口
        View cutterContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_cutter_diameter_change, null);
        //获得显示刀具规格TextView
        mCutterSpecification=(TextView)cutterContentView.findViewById(R.id.tv_cutter_specification);
        mAddDiameter = (ImageView) cutterContentView.findViewById(R.id.btn_add);
        mAddDiameter.setOnClickListener(popupWindowItemClick);
        mSubtractDiameter = (ImageView) cutterContentView.findViewById(R.id.btn_subtract);
        mSubtractDiameter.setOnClickListener(popupWindowItemClick);
        mCutterDiameterText = (TextView) cutterContentView.findViewById(R.id.tv_storage_diameter_data);
        ImageView cutterImgHint =(ImageView)cutterContentView.findViewById(R.id.iv_cutter_hint);
       LinearLayout  bottomLayout= (LinearLayout)cutterContentView.findViewById(R.id.ll_bottom);
        if(ki.getType()==KeyInfo.CONCAVE_DOT_KEY){
            mCutterDiameter.setBackgroundResource(R.drawable.resource21_frmkeycut_docutres);
            mCutterDiameter.setText("");
            cutterImgHint.setImageResource(R.drawable.dimplecutter);
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) cutterImgHint.getLayoutParams();
            linearParams.width=ViewGroup.LayoutParams.WRAP_CONTENT;
            cutterImgHint.setLayoutParams(linearParams);
            mCutterSpecification.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        }
        mCutterDiameterChangeWindow = new PopupWindow(this);
        mCutterDiameterChangeWindow.setContentView(cutterContentView);
        mCutterDiameterChangeWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutterDiameterChangeWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutterDiameterChangeWindow.setBackgroundDrawable(new ColorDrawable());
        mCutterDiameterChangeWindow.setOutsideTouchable(true);

        //切割深度切换窗口
        View     mCutDepthContentView= LayoutInflater.from(this).inflate(R.layout.popupwindow_cut_depth_change,null);
        mCutDepthSwitchoverWindow=new PopupWindow(this);
        mCutDepthSwitchoverWindow.setContentView(mCutDepthContentView);
        mCutDepthSwitchoverWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutDepthSwitchoverWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutDepthSwitchoverWindow.setBackgroundDrawable(new ColorDrawable());
        Button   mBtnReduce =(Button)mCutDepthContentView.findViewById(R.id.btn_reduce); //减少
        mBtnReduce.setOnClickListener(cutDepthSwitchoverListener);
        Button  mBtnIncrease=(Button)mCutDepthContentView.findViewById(R.id.btn_increase); //增加
        mBtnIncrease.setOnClickListener(cutDepthSwitchoverListener);
        mTvCutDepthValue = (TextView)mCutDepthContentView.findViewById(R.id.tv_cut_depth_value);

        //菜单窗口
        View mMenuContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_decode_and_cut_menu, null);
        mMenuWindow = new PopupWindow(this);
        mMenuWindow.setContentView(mMenuContentView);
        mMenuWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMenuWindow.setBackgroundDrawable(new ColorDrawable());
        mMenuWindow.setAnimationStyle(R.style.PopupAnimation);
        Button mMenuHome =(Button)mMenuContentView.findViewById(R.id.menu_home);
        mMenuHome.setOnClickListener(popupWindowItemClick);
        Button mMenuCalibration =(Button)mMenuContentView.findViewById(R.id.menu_calibration);
        mMenuCalibration.setOnClickListener(popupWindowItemClick);
        mMeasureCutter = (Button) mMenuContentView.findViewById(R.id.menu_measure);
        mMeasureCutter.setOnClickListener(popupWindowItemClick);
        Button mMenuClampMove =(Button) mMenuContentView.findViewById(R.id.menu_clamp_move);
        mMenuClampMove.setOnClickListener(popupWindowItemClick);
        //停止操作按钮
        Button mMenuStop=(Button)mMenuContentView.findViewById(R.id.menu_stop);
        mMenuStop.setOnClickListener(popupWindowItemClick);

    }

    /**
     * Z切割层次按钮切换点击监听器
     */
    View.OnClickListener ZCutHierarchyClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                    case R.id.btn_z_cut_hierarchy1:
                            if(ZCutHierarchyValue==2){
                                mBtnZCutHierarchy2.setBackgroundResource(R.drawable.btnzpitch2image_frmkeycut_docutres);
                            }else if(ZCutHierarchyValue==3){
                                mBtnZCutHierarchy3.setBackgroundResource(R.drawable.btnzpitch3image_frmkeycut_docutres);
                            }
                            if(ZCutHierarchyValue!=1){
                                mBtnZCutHierarchy1.setBackgroundResource(R.drawable.btnzpitch1pressedimage_frmkeycut_docutres);
                                mBtnZCutHierarchy.setText("Z Pitch: 1");
                                ZCutHierarchyValue=1;
                            }
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch1image_frmkeycut_docutres);

                               mBtnZCutHierarchy1.setVisibility(View.INVISIBLE);
                               mBtnZCutHierarchy2.setVisibility(View.INVISIBLE);
                               mBtnZCutHierarchy3.setVisibility(View.INVISIBLE);
                               mLlZCutHierarchySelect.setVisibility(View.INVISIBLE);  //隐藏


                    break;
                case R.id.btn_z_cut_hierarchy2:
                    if(ZCutHierarchyValue==1){
                        mBtnZCutHierarchy1.setBackgroundResource(R.drawable.btnzpitch1image_frmkeycut_docutres);
                    }else if(ZCutHierarchyValue==3){
                        mBtnZCutHierarchy3.setBackgroundResource(R.drawable.btnzpitch3image_frmkeycut_docutres);
                    }
                    if(ZCutHierarchyValue!=2){
                        mBtnZCutHierarchy2.setBackgroundResource(R.drawable.btnzpitch2pressedimage_frmkeycut_docutres);
                        mBtnZCutHierarchy.setText("Z Pitch: 2");
                        ZCutHierarchyValue=2;
                    }
                    mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch2image_frmkeycut_docutres);
                    mBtnZCutHierarchy1.setVisibility(View.INVISIBLE);
                    mBtnZCutHierarchy2.setVisibility(View.INVISIBLE);
                    mBtnZCutHierarchy3.setVisibility(View.INVISIBLE);
                    mLlZCutHierarchySelect.setVisibility(View.INVISIBLE);  //隐藏

                    break;
                case R.id.btn_z_cut_hierarchy3:
                    if(ZCutHierarchyValue==1){
                        mBtnZCutHierarchy1.setBackgroundResource(R.drawable.btnzpitch1image_frmkeycut_docutres);
                    }else if(ZCutHierarchyValue==2){
                        mBtnZCutHierarchy2.setBackgroundResource(R.drawable.btnzpitch2image_frmkeycut_docutres);
                    }
                    if(ZCutHierarchyValue!=3){
                        mBtnZCutHierarchy.setText("Z Pitch: 3");
                        mBtnZCutHierarchy3.setBackgroundResource(R.drawable.btnzpitch3pressedimage_frmkeycut_docutres);
                        ZCutHierarchyValue=3;
                    }
                    mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch3image_frmkeycut_docutres);
                    mBtnZCutHierarchy1.setVisibility(View.INVISIBLE);
                    mBtnZCutHierarchy2.setVisibility(View.INVISIBLE);
                    mBtnZCutHierarchy3.setVisibility(View.INVISIBLE);
                    mLlZCutHierarchySelect.setVisibility(View.INVISIBLE);  //隐藏
                    break;
                case R.id.btn_z_cut_hierarchy:
                    judgeSpeedSelectWindowIsHide();
                    judgeMenuWindowIsHide();
                    judgeCutDepthWindowIsHide();
                    judgeLocationModeWindowIsHide();
                    judgeCutterDiameterWindowIsHide();
                    judgeCutterWindowIsHide();
                    if(mLlZCutHierarchySelect.getVisibility()==View.INVISIBLE){
                        mBtnZCutHierarchy1.setVisibility(View.VISIBLE);
                        mBtnZCutHierarchy2.setVisibility(View.VISIBLE);
                        mBtnZCutHierarchy3.setVisibility(View.VISIBLE);
                        mLlZCutHierarchySelect.setVisibility(View.VISIBLE);
                        if(ZCutHierarchyValue==1){
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch1pressedimage_frmkeycut_docutres);
                        }else if(ZCutHierarchyValue==2){
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch2pressedimage_frmkeycut_docutres);
                        }else if(ZCutHierarchyValue==3){
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch3pressedimage_frmkeycut_docutres);
                        }
                    }else if(mLlZCutHierarchySelect.getVisibility()==View.VISIBLE){
                        mLlZCutHierarchySelect.setVisibility(View.INVISIBLE);
                        if(ZCutHierarchyValue==1){
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch1image_frmkeycut_docutres);
                        }else if(ZCutHierarchyValue==2){
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch2image_frmkeycut_docutres);
                        }else if(ZCutHierarchyValue==3){
                            mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch3image_frmkeycut_docutres);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 钥匙切割定位方式选择监听器
     */
    View.OnClickListener cutLocationModeSelectListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ib_no_detect:   //不检测
                    if (cutDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (cutDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }
                    mIvNoDetect.setImageResource(R.drawable.btnautosensenopressedimage_frmkeycut_docutres);
                    mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    cutDetectionMode = 0;
                    mCutLocationMode.dismiss();
                    isShowLocationModeWindow =false;
                    break;
                case R.id.ib_up_down_location:  //上为定位
                    if (cutDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    }else if (cutDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }
                    mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    cutDetectionMode = 1;
                    isShowLocationModeWindow =false;
                    mCutLocationMode.dismiss();
                    break;
                case R.id.ib_bilateral_location: //两边定位
                    if (cutDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                    } else  if (cutDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }
                    mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    cutDetectionMode = 2;
                    isShowLocationModeWindow =false;
                    mCutLocationMode.dismiss();
                    break;
                case R.id.ib_cusp_location:   //尖端定位
                    if (cutDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    }  else if (cutDetectionMode == 4) {
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }
                    mIvCuspLocation.setImageResource(R.drawable.btnautosensetippressedimage_frmkeycut_docutres);
                    mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    cutDetectionMode = 3;
                    isShowLocationModeWindow =false;
                    mCutLocationMode.dismiss();
                    break;
                case R.id.ib_three_terminal_location:   // 三端定位
                    if (cutDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    }
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
                    cutDetectionMode = 4;
                    isShowLocationModeWindow =false;
                    mCutLocationMode.dismiss();
                    break;
            }

        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            judgePopupWindowDismiss();
            judgeZCutHierarchySelectIsHide();
        }
        return super.onTouchEvent(event);
    }

    View.OnClickListener popupWindowItemClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_steel:  //钢
                     if(cutSpeed ==2){
                         mSilverSpeed.setBackgroundResource(R.drawable.btnspeed2image_frmkeycut_docutres);
                    }else if(cutSpeed ==3){
                         mBrassSpeed.setBackgroundResource(R.drawable.btnspeed3image_frmkeycut_docutres);
                    }else if(cutSpeed ==4){
                         mHighSpeed.setBackgroundResource(R.drawable.btnspeed4image_frmkeycut_docutres);
                    }
                    if(cutSpeed !=1){
                        mSteelSpeed.setBackgroundResource(R.drawable.btnspeed1pressedimage_frmkeycut_docutres);
                        cutSpeed =1;
                    }
                    mSpeed.setImageResource(R.drawable.btnspeed1image_frmkeycut_docutres);
                    mSpeedSelectWindow.dismiss();
                    isShowSpeedWindow = true;
                    break;
                case R.id.btn_silver:  //镍银
                    if(cutSpeed ==1){
                        mSteelSpeed.setBackgroundResource(R.drawable.btnspeed1image_frmkeycut_docutres);
                    }else if(cutSpeed ==3){
                        mBrassSpeed.setBackgroundResource(R.drawable.btnspeed3image_frmkeycut_docutres);
                    }else if(cutSpeed ==4){
                        mHighSpeed.setBackgroundResource(R.drawable.btnspeed4image_frmkeycut_docutres);
                    }
                    if(cutSpeed !=2){
                        mSilverSpeed.setBackgroundResource(R.drawable.btnspeed2pressedimage_frmkeycut_docutres);
                        cutSpeed =2;
                    }
                    mSpeed.setImageResource(R.drawable.btnspeed2image_frmkeycut_docutres);
                    mSpeedSelectWindow.dismiss();
                    isShowSpeedWindow = true;
                    break;
                case R.id.btn_brass:   //铜
                    if(cutSpeed ==1){
                        mSteelSpeed.setBackgroundResource(R.drawable.btnspeed1image_frmkeycut_docutres);
                    }else if(cutSpeed ==2){
                        mSilverSpeed.setBackgroundResource(R.drawable.btnspeed2image_frmkeycut_docutres);
                    }else if(cutSpeed ==4){
                        mHighSpeed.setBackgroundResource(R.drawable.btnspeed4image_frmkeycut_docutres);
                    }
                    if(cutSpeed !=3){
                        mBrassSpeed.setBackgroundResource(R.drawable.btnspeed3pressedimage_frmkeycut_docutres);
                        cutSpeed =3;
                    }
                    mSpeed.setImageResource(R.drawable.btnspeed3image_frmkeycut_docutres);
                    mSpeedSelectWindow.dismiss();
                    isShowSpeedWindow = true;
                    break;
                case R.id.btn_high_speed:  //最高速度
                    if(cutSpeed ==1){
                        mSteelSpeed.setBackgroundResource(R.drawable.btnspeed1image_frmkeycut_docutres);
                    }else if(cutSpeed ==2){
                        mSilverSpeed.setBackgroundResource(R.drawable.btnspeed2image_frmkeycut_docutres);
                    }else if(cutSpeed ==3){
                        mBrassSpeed.setBackgroundResource(R.drawable.btnspeed3image_frmkeycut_docutres);
                    }
                    if(cutSpeed !=4){
                         mHighSpeed.setBackgroundResource(R.drawable.btnspeed4pressedimage_frmkeycut_docutres);
                        cutSpeed =4;
                    }
                    mSpeed.setImageResource(R.drawable.btnspeed4image_frmkeycut_docutres);
                    mSpeedSelectWindow.dismiss();
                    isShowSpeedWindow = true;
                    break;
                case R.id.btn_add:  //添加切割刀的直径
                    diameterData = ArithUtils.add(diameterData,0.1);
                    //去掉后面不要的0  转为String
                    cutterDiameterValue  = removeUnnecessaryZero(diameterData);
                    if(diameterData>2.5){
                        diameterData=1;
                        cutterDiameterValue="1";
                        mCutterDiameterText.setText(cutterDiameterValue + "mm");
                        mCutterDiameter.setText(cutterDiameterValue + "mm");
                        mCutterSpecification.setText("ref.T60-E10-P");
                    }else {
                         if(diameterData==2){
                            mCutterSpecification.setText("ref.T60-E20-P");
                             mCutterDiameterText.setText(cutterDiameterValue + "mm");
                             mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else if(diameterData==1.7){
                            mCutterSpecification.setText("ref.T60-E17-P");
                             mCutterDiameterText.setText(cutterDiameterValue + "mm");
                             mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else if(diameterData==1.5) {
                            mCutterSpecification.setText("ref.T60-E15-P");
                             mCutterDiameterText.setText(cutterDiameterValue + "mm");
                             mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else {
                             mCutterDiameterText.setText(cutterDiameterValue + "mm");
                             mCutterDiameter.setText(cutterDiameterValue + "mm");
                            mCutterSpecification.setText("");
                        }
                    }
                    timer.cancel();
                    break;
                case R.id.btn_subtract:
                    diameterData = ArithUtils.sub(diameterData, 0.1);
                    //去掉后面不要的0  转为String
                    cutterDiameterValue  = removeUnnecessaryZero(diameterData);
                    if(diameterData<1){
                        diameterData=2.5;
                        cutterDiameterValue="2.5";
                        mCutterDiameterText.setText(diameterData + "mm");
                        mCutterDiameter.setText(diameterData + "mm");
                    }else {
                        if(diameterData==1){
                            mCutterSpecification.setText("ref.T60-E10-P");
                            mCutterDiameterText.setText(cutterDiameterValue + "mm");
                            mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else if(diameterData==2){
                            mCutterSpecification.setText("ref.T60-E20-P");
                            mCutterDiameterText.setText(cutterDiameterValue + "mm");
                            mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else if(diameterData==1.7){
                            mCutterSpecification.setText("ref.T60-E17-P");
                            mCutterDiameterText.setText(cutterDiameterValue + "mm");
                            mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else if(diameterData==1.5){
                            mCutterSpecification.setText("ref.T60-E15-P");
                            mCutterDiameterText.setText(cutterDiameterValue + "mm");
                            mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }else {
                            mCutterSpecification.setText("");
                            mCutterDiameterText.setText(cutterDiameterValue + "mm");
                            mCutterDiameter.setText(cutterDiameterValue + "mm");
                        }
                    }
                    timer.cancel();
                    break;
                case R.id.menu_home:   //回到主界面
                    Intent intentHome = new Intent(CutKeyActivity.this, MainActivity.class);
                    intentHome.setFlags(intentHome.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentHome);
                    break;
                case R.id.menu_calibration:  //打开校准界面
                    FrmMaintenanceActivity.startFrmMaintenanceActivity(CutKeyActivity.this,languageMap);
                    break;
                case R.id.menu_clamp_move:  //发指令夹具移动
                    serialDriver.write(Instruction.FIXTURE_MOVE.getBytes(),Instruction.FIXTURE_MOVE.length());
                    break;
                case R.id.menu_measure:
                    ProbeAndCutterMeasurementActivity.
                            startItselfActivity(CutKeyActivity.this,ProbeAndCutterMeasurementActivity.FlAG_CUTTER_MEASUREMENT);
                    break;
                case R.id.menu_stop:  //停止操作指令
                    serialDriver.write(Instruction.STOP_OPERATE.getBytes(),Instruction.STOP_OPERATE.length());
                    break;

            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
       if(serialDriver!=null){
           serialDriver.setHandler(mHandler);
       }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("停止了？", "onPause: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!mMeasureCutter.isClickable()){
            mMeasureCutter.setTextColor(Color.parseColor("#020701"));
            mMeasureCutter.setClickable(true);
            isShowMeun=false;
            mMenuWindow.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
        if(!mCutKey.isClickable()){
            mCutKey.setBackgroundResource(R.drawable.btncutimage1_frmkeycut_docutres);
            mCutKey.setClickable(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        judgePopupWindowDismiss();
        mHandler.removeCallbacksAndMessages(null);

    }

    /**
     *  当前Activity  所有View点击事件
     * @param v
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back_close:  // 关闭当前窗口
                finish();
                break;
            case R.id.iv_speed:  //速度切换
                judgeZCutHierarchySelectIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();
                if (!isShowSpeedWindow) {  //速度窗口显示
                    if(cutSpeed ==1){
                        mSpeed.setImageResource(R.drawable.btnspeed1pressedimage_frmkeycut_docutres);
                    }else if(cutSpeed ==2){
                        mSpeed.setImageResource(R.drawable.btnspeed2pressedimage_frmkeycut_docutres);
                    }else if(cutSpeed ==3){
                        mSpeed.setImageResource(R.drawable.btnspeed3pressedimage_frmkeycut_docutres);
                    }else if(cutSpeed ==4){
                        mSpeed.setImageResource(R.drawable.btnspeed4pressedimage_frmkeycut_docutres);
                    }
                    mSpeedSelectWindow.showAsDropDown(mSpeed, 100, -95);
                    isShowSpeedWindow = true;
                } else {
                    if(cutSpeed ==1){
                        mSpeed.setImageResource(R.drawable.btnspeed1image_frmkeycut_docutres);
                    }else if(cutSpeed ==2){
                        mSpeed.setImageResource(R.drawable.btnspeed2image_frmkeycut_docutres);
                    }else if(cutSpeed ==3){
                        mSpeed.setImageResource(R.drawable.btnspeed3image_frmkeycut_docutres);
                    }else if(cutSpeed ==4){
                        mSpeed.setImageResource(R.drawable.btnspeed4image_frmkeycut_docutres);
                    }
                    mSpeedSelectWindow.dismiss();
                    isShowSpeedWindow = false;
                }
                break;
            case R.id.btn_cut_knife_diameter:   //切割刀直径
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutDepthWindowIsHide();

                if (!isShowCutterDiameterWindow) {
                    mCutterDiameterChangeWindow.showAsDropDown(mCutterDiameter, 100, -95);
                    isShowCutterDiameterWindow = true;
                } else {  //为不显示状态
                    mCutterDiameterChangeWindow.dismiss();
                    isShowCutterDiameterWindow = false;
                }
                break;
            case R.id.iv_clamp_location_select:
                clampLocationSlotImgSelect();  //夹具定位槽和图片选择方法
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();
                break;
            case R.id.btn_frm_menu:  // 界面的菜单
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();

                if(!isShowMeun){
                    mMenuWindow.showAtLocation(mRootView, Gravity.BOTTOM,0,0);
                    isShowMeun=true;
                }else {
                    mMenuWindow.dismiss();
                    isShowMeun=false;
                }

                break;
            case R.id.btn_cut_key: //切割钥匙
                 SaveDataDaoManager.saveDataToCutHistory(ki);//保存数据切割记录
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();
                EventBusUtils.post(new MessageEvent(MessageEvent.CLAMP_STATE_INDEX,index));
                cutOrder = mKey.getCutOrder(cutDepth,  //切割深度
                                          locatingSlot, // 定位槽
                                          assistClamp,  //辅助夹具
                                          cutterDiameterValue, //刀具直径
                                          cutSpeed,         //速度
                                          ZCutHierarchyValue,  //Z切割层次
                                          cutDetectionMode);   //定位方式
                Log.d("切割指令", "onClick: "+cutOrder);
                switch (ki.getType()){
                    case KeyInfo.BILATERAL_KEY:
                        if(cutDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||cutDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                            mTvTextHint1.setVisibility(View.INVISIBLE);
                            mTvTextGuideHint.setVisibility(View.INVISIBLE);
                            mTvTextCutHint.setVisibility(View.VISIBLE);
                            mMenu.setVisibility(View.INVISIBLE);
                            mRlProgressBar.setVisibility(View.VISIBLE);
                            mStopCut.setVisibility(View.VISIBLE);
                            mCutKey.setVisibility(View.INVISIBLE);
                            mBack.setVisibility(View.INVISIBLE);
                            mKey.setVisibility(View.VISIBLE);  //显示钥匙
                        }
                        break;
                    case  KeyInfo.UNILATERAL_KEY:  //单边钥匙
                        OperateTipsActivity.startItselfActivityForResult(this,ki.getType(),cutOrder, clampAvailable);
                        break;
                    case  KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY:
                        if(cutDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||cutDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        } else if(cutDetectionMode==Detection.MODE_NO_DETECT){
                                MessageTipsActivity.startMessageTipsActivityForResult(this,MessageTipsActivity.CUT_NO_DETECTION);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                            mTvTextHint1.setVisibility(View.INVISIBLE);
                            mTvTextGuideHint.setVisibility(View.INVISIBLE);
                            mTvTextCutHint.setVisibility(View.VISIBLE);
                            mMenu.setVisibility(View.INVISIBLE);
                            mRlProgressBar.setVisibility(View.VISIBLE);
                            mStopCut.setVisibility(View.VISIBLE);
                            mCutKey.setVisibility(View.INVISIBLE);
                            mBack.setVisibility(View.INVISIBLE);
                            mKey.setVisibility(View.VISIBLE);  //显示钥匙
                            if(cutToken==1){
                                mBtnSecondCut.setVisibility(View.INVISIBLE);
                            }else if(cutToken==2){
                                mBtnFirstCut.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    case KeyInfo.MONORAIL_OUTER_GROOVE_KEY:  //双轨外槽钥匙
                        if(cutDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||cutDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        } else if(cutDetectionMode==Detection.MODE_NO_DETECT){
                            MessageTipsActivity.startMessageTipsActivityForResult(this,MessageTipsActivity.CUT_NO_DETECTION);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                            mTvTextHint1.setVisibility(View.INVISIBLE);
                            mTvTextGuideHint.setVisibility(View.INVISIBLE);
                            mTvTextCutHint.setVisibility(View.VISIBLE);
                            mMenu.setVisibility(View.INVISIBLE);
                            mRlProgressBar.setVisibility(View.VISIBLE);
                            mStopCut.setVisibility(View.VISIBLE);
                            mCutKey.setVisibility(View.INVISIBLE);
                            mBack.setVisibility(View.INVISIBLE);
                            mKey.setVisibility(View.VISIBLE);  //显示钥匙
                            if(cutToken==1){
                                mBtnSecondCut.setVisibility(View.INVISIBLE);
                            }else if(cutToken==2){
                                mBtnFirstCut.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    case KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY:  //双轨外槽
                        if(cutDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||cutDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        } else if(cutDetectionMode==Detection.MODE_NO_DETECT){
                            MessageTipsActivity.startMessageTipsActivityForResult(this,MessageTipsActivity.CUT_NO_DETECTION);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                            mTvTextHint1.setVisibility(View.INVISIBLE);
                            mTvTextGuideHint.setVisibility(View.INVISIBLE);
                            mTvTextCutHint.setVisibility(View.VISIBLE);
                            mMenu.setVisibility(View.INVISIBLE);
                            mRlProgressBar.setVisibility(View.VISIBLE);
                            mStopCut.setVisibility(View.VISIBLE);
                            mCutKey.setVisibility(View.INVISIBLE);
                            mBack.setVisibility(View.INVISIBLE);
                            mKey.setVisibility(View.VISIBLE);  //显示钥匙
                            if(cutToken==1){
                                mBtnSecondCut.setVisibility(View.INVISIBLE);
                            }else if(cutToken==2){
                                mBtnFirstCut.setVisibility(View.INVISIBLE);
                            }
                        }
                        break;
                    case KeyInfo.MONORAIL_INSIDE_GROOVE_KEY:
                        if(cutDetectionMode==Detection.MODE_BOTHSIDE_LOCATION||cutDetectionMode==Detection.MODE_THREETERMINAL_LOCATION){
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,Clamp.CLAMP_GROOVE_CHIPS);
                        } else if(cutDetectionMode==Detection.MODE_NO_DETECT){
                            MessageTipsActivity.startMessageTipsActivityForResult(this,MessageTipsActivity.CUT_NO_DETECTION);
                        }else {
                            if (serialDriver != null) {
                                serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                            }
                            mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                            mTvTextHint1.setVisibility(View.INVISIBLE);
                            mTvTextGuideHint.setVisibility(View.INVISIBLE);
                            mTvTextCutHint.setVisibility(View.VISIBLE);
                            mMenu.setVisibility(View.INVISIBLE);
                            mRlProgressBar.setVisibility(View.VISIBLE);
                            mStopCut.setVisibility(View.VISIBLE);
                            mCutKey.setVisibility(View.INVISIBLE);
                            mBack.setVisibility(View.INVISIBLE);
                            mKey.setVisibility(View.VISIBLE);  //显示钥匙
                            if(cutToken==1){
                                mBtnSecondCut.setVisibility(View.INVISIBLE);
                            }else if(cutToken==2){
                                mBtnFirstCut.setVisibility(View.INVISIBLE);
                            }
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
                                    //属于特殊钥匙的逻辑
                            }else {
                                OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,ConstantValue.B_TYPE_CUTTER);
                            }
                        }else {
                            OperateTipsActivity.startItselfActivityForResult(this, ki.getType(), cutOrder,ConstantValue.B_TYPE_CUTTER);
                        }

                        break;
                    case KeyInfo.ANGLE_KEY:   //角度钥匙
                        if (serialDriver != null) {
                            serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                        }
                        mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                        mTvTextHint1.setVisibility(View.INVISIBLE);
                        mTvTextGuideHint.setVisibility(View.INVISIBLE);
                        mTvTextCutHint.setVisibility(View.VISIBLE);
                        mMenu.setVisibility(View.INVISIBLE);
                        mRlProgressBar.setVisibility(View.VISIBLE);
                        mStopCut.setVisibility(View.VISIBLE);
                        mCutKey.setVisibility(View.INVISIBLE);
                        mBack.setVisibility(View.INVISIBLE);
                        mKey.setVisibility(View.VISIBLE);  //显示钥匙
                        break;
                    case KeyInfo.CYLINDER_KEY:   //圆筒钥匙
                        if (serialDriver != null) {
                            serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                        }
                        mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                        mTvTextHint1.setVisibility(View.INVISIBLE);
                        mTvTextGuideHint.setVisibility(View.INVISIBLE);
                        mTvTextCutHint.setVisibility(View.VISIBLE);
                        mMenu.setVisibility(View.INVISIBLE);
                        mRlProgressBar.setVisibility(View.VISIBLE);
                        mStopCut.setVisibility(View.VISIBLE);
                        mCutKey.setVisibility(View.INVISIBLE);
                        mBack.setVisibility(View.INVISIBLE);
                        mKey.setVisibility(View.VISIBLE);  //显示钥匙
                        break;
                    case KeyInfo.SIDE_TOOTH_KEY:  //侧齿钥匙
                        if (serialDriver != null) {
                            serialDriver.write(cutOrder.getBytes(), cutOrder.length());
                        }
                        mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
                        mTvTextHint1.setVisibility(View.INVISIBLE);
                        mTvTextCutHint.setVisibility(View.VISIBLE);
                        mMenu.setVisibility(View.INVISIBLE);
                        mRlProgressBar.setVisibility(View.VISIBLE);
                        mStopCut.setVisibility(View.VISIBLE);
                        mCutKey.setVisibility(View.INVISIBLE);
                        mBack.setVisibility(View.INVISIBLE);
                        mKey.setVisibility(View.VISIBLE);  //显示钥匙
                        if(cutToken==1){
                            mBtnSecondCut.setVisibility(View.INVISIBLE);
                        }else if(cutToken==2){
                            mBtnFirstCut.setVisibility(View.INVISIBLE);
                        }
                        break;

                }
                break;
            case R.id.btn_stop_cut:   //停止切割
                if(serialDriver!=null){
                    serialDriver.write(Instruction.STOP_OPERATE.getBytes(),Instruction.STOP_OPERATE.length());
                }
                if(mCutKey.getVisibility()==View.INVISIBLE){
                    mKey.setVisibility(View.GONE);  //隐藏钥匙
                    mIvClampLocationSelect.setVisibility(View.VISIBLE);  //显示这个夹具类型选择ImageView
                   mTvTextHint1.setVisibility(View.VISIBLE);
                    mTvTextGuideHint.setVisibility(View.VISIBLE);
                    mTvTextCutHint.setVisibility(View.INVISIBLE);
                   mMenu.setVisibility(View.VISIBLE);
                    mRlProgressBar.setVisibility(View.INVISIBLE);
                    mTvProgress.setText("0");
                   mPbLoading.setProgress(0);
                    mStopCut.setVisibility(View.INVISIBLE);
                    mCutKey.setVisibility(View.VISIBLE);
                   mCutKey.setBackgroundResource(R.drawable.btncutimage1_frmkeycut_docutres);
                    mCutKey.setClickable(true);
                    mBack.setVisibility(View.VISIBLE);
                    if(ki.getType()!=KeyType.UNILATERAL_KEY&&ki.getType()!=KeyType.BILATERAL_KEY&&ki.getType()!=KeyInfo.ANGLE_KEY&&ki.getType()!=KeyInfo.CYLINDER_KEY){
                        mBtnSecondCut.setVisibility(View.VISIBLE);
                        mBtnFirstCut.setVisibility(View.VISIBLE);
                    }
                } else  if(mMeasureCutter.isClickable()==false){
                    mMeasureCutter.setTextColor(Color.parseColor("#020701"));
                    mMeasureCutter.setClickable(true);
                }
                break;
            case R.id.iv_key_cut_location: //打开钥匙切割定位选择窗口
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();

                if (!isShowLocationModeWindow ) {
                    if (cutDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenopressedimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 3) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensetippressedimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
                    }
                    mCutLocationMode.showAsDropDown(mIvLocateMode, 100, -97);
                    isShowLocationModeWindow = true;
                } else {
                    if (cutDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 3) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }
                    mCutLocationMode.dismiss();
                    isShowLocationModeWindow = false;
                }
                break;
            case R.id.btn_pass_cut:  //pass 切割
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();
                if (isShowSpeedWindow) {
                    if(cutSpeed ==1){
                        mSpeed.setImageResource(R.drawable.btnspeed1image_frmkeycut_docutres);
                    }else if(cutSpeed ==2){
                        mSpeed.setImageResource(R.drawable.btnspeed2image_frmkeycut_docutres);
                    }else if(cutSpeed ==3){
                        mSpeed.setImageResource(R.drawable.btnspeed3image_frmkeycut_docutres);
                    }else if(cutSpeed ==4){
                        mSpeed.setImageResource(R.drawable.btnspeed4image_frmkeycut_docutres);
                    }
                    mSpeedSelectWindow.dismiss();
                    isShowSpeedWindow = false;
                }
                if(isShowMeun){  //判断菜单窗口是不是显示状态
                    mMenuWindow.dismiss();
                    isShowMeun=false;
                }

                if(isShowCutDepthWindow){
                    mBtnCutDepth.setBackgroundResource(R.drawable.btndepthimage_frmkeycut_docutres);
                    mCutDepthSwitchoverWindow.dismiss();
                    isShowCutDepthWindow=false;
                }
                if(isShowLocationModeWindow){  //判断定位选择窗口是不是显示状态
                    if (cutDetectionMode == 0) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 1) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 2) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 3) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                    } else if (cutDetectionMode == 4) {
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }
                    mCutLocationMode.dismiss();
                    isShowLocationModeWindow = false;
                }
                break;
            case R.id.btn_cut_first_face:  //第一次切割
                if(cutToken!=1){
                    mBtnFirstCut.setBackgroundResource(R.drawable.btntopdisabledimage_frmkeycut_docutres);
                    mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                    mBtnSecondCut.setBackgroundResource(R.drawable.btnbottomimage_frmkeycut_docutres);
                    mBtnSecondCut.setTextColor(Color.BLACK);
                    cutToken=1; //切割记号为1
                }
                break;
            case R.id.btn_cut_second_face:  //第二次切割
                if(cutToken!=2){
                    mBtnFirstCut.setBackgroundResource(R.drawable.btntopimage_frmkeycut_docutres);
                    mBtnFirstCut.setTextColor(Color.BLACK);
                    mBtnSecondCut.setBackgroundResource(R.drawable.btnbottomdisabledimage_frmkeycut_docutres);
                    mBtnSecondCut.setTextColor(Color.parseColor("#B5B5B5"));
                    cutToken=2;
                }
                break;
            case R.id.btn_cut_depth:  //切割深度按钮点击事件
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                if(!isShowCutDepthWindow){
                    mCutDepthSwitchoverWindow.showAsDropDown(mBtnCutDepth,-290,-120);
                    isShowCutDepthWindow=true;
                    mBtnCutDepth.setBackgroundResource(R.drawable.btndepthpressedimage_frmkeycut_docutres);
                }else {
                    mCutDepthSwitchoverWindow.dismiss();
                    isShowCutDepthWindow=false;
                    mBtnCutDepth.setBackgroundResource(R.drawable.btndepthimage_frmkeycut_docutres);
                }
                break;
        }
    }

    /**
     *   判断切割深度窗口是否隐藏
     */
    private void judgeCutterWindowIsHide(){
        if(isShowCutDepthWindow){
            mCutDepthSwitchoverWindow.dismiss();
            isShowCutDepthWindow=false;
            mBtnCutDepth.setBackgroundResource(R.drawable.btndepthimage_frmkeycut_docutres);
        }
    }

    /**
     *   判断切割刀直径窗口是否隐藏
     */
    private void judgeCutterDiameterWindowIsHide(){
        if (isShowCutterDiameterWindow) {   //判断切割刀规格选择窗口 是不是显示状态
            mCutterDiameterChangeWindow.dismiss();
            isShowCutterDiameterWindow = false;
        }
    }

    /**
     *  判断定位方式窗口 是否隐藏
     */
    private void judgeLocationModeWindowIsHide(){
        if(isShowLocationModeWindow){  //判断定位方式窗口是不是显示状态
            if (cutDetectionMode == 0) {
                mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 1) {
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 2) {
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 3) {
                mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 4) {
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
            }
            mCutLocationMode.dismiss();
            isShowLocationModeWindow = false;
        }
    }

    /**
     * 判断切割深度窗口是否隐藏
     */
    private void judgeCutDepthWindowIsHide(){
         if(isShowCutDepthWindow){
             mCutDepthSwitchoverWindow.dismiss();
             isShowCutDepthWindow=false;
             mBtnCutDepth.setBackgroundResource(R.drawable.btndepthimage_frmkeycut_docutres);
         }
    }

    /**
     * 判断菜单窗口是否隐藏
     */
    private void judgeMenuWindowIsHide(){
        if(isShowMeun){  //判断菜单窗口 显示就隐藏
            mMenuWindow.dismiss();
            isShowMeun=false;
        }
    }

    /**
     * 判断速度选择窗口是否隐藏
     */
    private void judgeSpeedSelectWindowIsHide(){
        if (isShowSpeedWindow) {
            if(cutSpeed ==1){
                mSpeed.setImageResource(R.drawable.btnspeed1image_frmkeycut_docutres);
            }else if(cutSpeed ==2){
                mSpeed.setImageResource(R.drawable.btnspeed2image_frmkeycut_docutres);
            }else if(cutSpeed ==3){
                mSpeed.setImageResource(R.drawable.btnspeed3image_frmkeycut_docutres);
            }else if(cutSpeed ==4){
                mSpeed.setImageResource(R.drawable.btnspeed4image_frmkeycut_docutres);
            }
            mSpeedSelectWindow.dismiss();
            isShowSpeedWindow = false;
        }
    }

    /**
     *  判断Z切割层次选择布局是否隐藏
     */
    private void judgeZCutHierarchySelectIsHide(){
        if(mLlZCutHierarchySelect.getVisibility()==View.VISIBLE){ //显示就隐藏
            if(ZCutHierarchyValue==1){
                mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch1image_frmkeycut_docutres);
            }else if(ZCutHierarchyValue==2){
                mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch2image_frmkeycut_docutres);
            }else if(ZCutHierarchyValue==3){
                mBtnZCutHierarchy.setBackgroundResource(R.drawable.btnzpitch3image_frmkeycut_docutres);
            }
            mLlZCutHierarchySelect.setVisibility(View.INVISIBLE);  //隐藏
        }
    }

    /**
     *  判断本activity的PopupWindow是否关闭
     */
    private void judgePopupWindowDismiss(){
        if (isShowSpeedWindow) {  //判断速度窗口是不是为true
            if(cutSpeed ==1){
                mSpeed.setImageResource(R.drawable.btnspeed1image_frmkeycut_docutres);
            }else if(cutSpeed ==2){
                mSpeed.setImageResource(R.drawable.btnspeed2image_frmkeycut_docutres);
            }else if(cutSpeed ==3){
                mSpeed.setImageResource(R.drawable.btnspeed3image_frmkeycut_docutres);
            }else if(cutSpeed ==4){
                mSpeed.setImageResource(R.drawable.btnspeed4image_frmkeycut_docutres);
            }
            mSpeedSelectWindow.dismiss();
            isShowSpeedWindow = false;
        }
        if(isShowCutDepthWindow){  //切割深度窗口为显示状态 就为true
            mBtnCutDepth.setBackgroundResource(R.drawable.btndepthimage_frmkeycut_docutres);
            mCutDepthSwitchoverWindow.dismiss();
            isShowCutDepthWindow=false;
        }
        if(isShowMeun){  //判断菜单窗口是不是显示状态
            mMenuWindow.dismiss();
            isShowMeun=false;
        }
        if (isShowCutterDiameterWindow) {   //判断切割刀规格选择窗口 是不是显示状态
            mCutterDiameterChangeWindow.dismiss();
            isShowCutterDiameterWindow = false;
        }
        if(isShowLocationModeWindow){  //判断定位选择窗口是不是显示状态
            if (cutDetectionMode == 0) {
                mIvLocateMode.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 1) {
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 2) {
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 3) {
                mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
            } else if (cutDetectionMode == 4) {
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
            }
            mCutLocationMode.dismiss();
            isShowLocationModeWindow = false;
        }

    }
    /**
     * 根据钥匙类型设置，添加钥匙View
     */
    private void addKeyViewType() {
        if (ki.getType() == KeyType.BILATERAL_KEY) {//双边钥匙
            //准备好要绘制的数据和钥匙
            mKey =new BilateralKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(615,265));
            mKey.setDrawPatternSize(600,250);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setDrawToothWidth(20);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setShowArrows(false);
            mKey.setVisibility(View.GONE);
            //添加View
            mPutKey.addView(mKey,0);
        } else if (ki.getType() ==  KeyType.UNILATERAL_KEY) {  //单边钥匙
            mKey = new UnilateralKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(660,270));
            mKey.setDrawPatternSize(640,265);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setShowArrows(false);
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);
        }else if(ki.getType()==KeyType.DUAL_PATH_INSIDE_GROOVE_KEY){  //双轨内槽钥匙
            mKey = new DualPathInsideGrooveKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620), DensityUtils.dip2px(this,230)));
            mKey.setDrawPatternSize(DensityUtils.dip2px(this,610),DensityUtils.dip2px(this,225));
            mKey.setShowArrows(false);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);
        }else if(ki.getType()==KeyType.MONORAIL_OUTER_GROOVE_KEY){  // 单轨外槽钥匙
            mKey = new MonorailOuterGrooveKey(this,ki);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620),DensityUtils.dip2px(this,230)));
            mKey.setDrawPatternSize(DensityUtils.dip2px(this,610),DensityUtils.dip2px(this,225));
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setShowArrows(false);
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);   //添加View
        }else if(ki.getType()==KeyType.DUAL_PATH_OUTER_GROOVE_KEY){ //双轨外槽钥匙
            mKey = new DualPathOuterGrooveKey(this,ki); //双轨外槽钥匙
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,610), DensityUtils.dip2px(this,206)));
            mKey.setDrawPatternSize(DensityUtils.dip2px(this,600),DensityUtils.dip2px(this,204));
            mKey.setShowArrows(false);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);   //添加View
        }if(ki.getType()==KeyType.MONORAIL_INSIDE_GROOVE_KEY){  //单轨内槽钥匙
            mKey = new MonorailInsideGrooveKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(740, 300));
            mKey.setDrawPatternSize(736,296);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setShowArrows(false);
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);   //添加View
        }else if(ki.getType()==KeyType.CONCAVE_DOT_KEY){  //凹点钥匙
            String[] newStr=ki.getRow_pos().split(";");
            if(ki.getRowCount()==1&&Integer.parseInt(newStr[0])<0){
                mKey = new ConcaveDotKey(this,ki);
                LinearLayout.LayoutParams layoutParam=new LinearLayout.LayoutParams(DensityUtils.dip2px(this,550), DensityUtils.dip2px(this,80));
                layoutParam.setMargins(0,DensityUtils.dip2px(this,90),0,0);
                mKey.setLayoutParams(layoutParam);
                mKey.setOnlyDrawSidePatternSize(DensityUtils.dip2px(this,548),DensityUtils.dip2px(this,78));
                mKey.setDrawBigCircleAndInnerCircleSize(14,6);
                mKey.setToothCode(ki.getKeyToothCode());
                mKey.setShowArrows(false);
                mKey.setVisibility(View.GONE);
                mPutKey.addView(mKey,0);
            }else {
                mKey = new ConcaveDotKey(this,ki);
                mKey.setLayoutParams(new LinearLayout.LayoutParams(DensityUtils.dip2px(this,620), DensityUtils.dip2px(this,210)));
                mKey.setDrawPatternSize(DensityUtils.dip2px(this,618),DensityUtils.dip2px(this,208));
                mKey.setDrawBigCircleAndInnerCircleSize(14,6);
                mKey.setToothCode(ki.getKeyToothCode());
                mKey.setShowArrows(false);
                mKey.setVisibility(View.GONE);
                mPutKey.addView(mKey,0);
            }
        }else if(ki.getType()==KeyType.ANGLE_KEY){   //角度钥匙
            mKey = new AngleKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(740,240));
            mKey.setBackgroundResource(R.drawable.edit_shape);
            mKey.setDrawPatternSize(700,238);
            mKey.setShowArrows(false);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);   //添加View
        }else if(ki.getType()==KeyType.CYLINDER_KEY){  //圆筒钥匙
            mKey = new CylinderKey(this,ki);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(305,305);
            layoutParams.setMargins(DensityUtils.dip2px(this,90),0,0,0);
            layoutParams.gravity=Gravity.CENTER;
            mKey.setLayoutParams(layoutParams);
            mKey.setDrawPatternSize(300,300);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);   //添加View
        }else if(ki.getType()==KeyType.SIDE_TOOTH_KEY){  //侧齿钥匙
            mKey = new SideToothKey(this,ki);
            mKey.setLayoutParams(new LinearLayout.LayoutParams(740,250));
            mKey.setDrawPatternSize(730,248);
            mKey.setShowArrows(false);
            mKey.setToothCode(ki.getKeyToothCode());
            mKey.setVisibility(View.GONE);
            mPutKey.addView(mKey,0);   //添加View
        }
    }

    /**
     *    夹具定位槽图片选择
     */
    private  void clampLocationSlotImgSelect(){
                index++;
                if (index < clampList.size()) {
                    cl = clampList.get(index);
                    mIvClampLocationSelect.setImageResource(cl.getImgHint());
                    locatingSlot = cl.getLocationSlot();
                    keyClamp=cl.getClampType();
                } else {
                    index = 0;
                    cl = clampList.get(index);
                    mIvClampLocationSelect.setImageResource(cl.getImgHint());
                    locatingSlot = cl.getLocationSlot();
                    keyClamp=cl.getClampType();
                }
    }

    View.OnClickListener  cutDepthSwitchoverListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
//           Double  depthValue=   Double.parseDouble(mTvCutDepthValue.getText().toString().replace("mm",""));
            String  value="";
                switch (v.getId()){
                    case R.id.btn_reduce://减
                        cutDepth=cutDepth-10;

                        if(cutDepth<50){
                            mTvCutDepthValue.setText("1.5mm");
                            mBtnCutDepth.setText("1.5mm");
                            cutDepth=150;
                        }else {
                            value= Tools.removeUnnecessaryZero(ArithUtils.mul(cutDepth,0.01));
                            mBtnCutDepth.setText(value+"mm");
                            mTvCutDepthValue.setText(value+"mm");
                        }
                        Log.d("是好多?", "onClick: "+cutDepth);
                        break;
                    case R.id.btn_increase:  //增加
                        cutDepth=cutDepth+10;
                        if(cutDepth>150){
                            mBtnCutDepth.setText("0.8mm");
                            mTvCutDepthValue.setText("0.8mm");
                            cutDepth=80;
                        }else {
                            value= removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                            mBtnCutDepth.setText(value+"mm");
                            mTvCutDepthValue.setText(value+"mm");
                        }
                        Log.d("是好多?", "onClick: "+cutDepth);
                        break;
                }
        }
    };


    CountDownTimer timer = new CountDownTimer(3000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            mCutterDiameterChangeWindow.dismiss();
            isShowCutterDiameterWindow = false;
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isShowCutterWindow) {
            mCutterDiameterChangeWindow.showAsDropDown(mCutterDiameter, 100, -95);
            isShowCutterDiameterWindow = true;
            isShowCutterWindow = false;
            timer.start();//开始倒计时
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
           if(requestCode==1&&resultCode== ConstantValue.START_MEASURE_CUTTER){  //代表检测切割刀高度的返回
               mMeasureCutter.setTextColor(Color.parseColor("#636363"));
               mMeasureCutter.setClickable(false);
           }else if(resultCode==ConstantValue.START_OPERATE){   //返回码为2
               mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
               mTvTextHint1.setVisibility(View.INVISIBLE);
               mTvTextGuideHint.setVisibility(View.INVISIBLE);
               mTvTextCutHint.setVisibility(View.VISIBLE);
               mMenuWindow.dismiss();
               mMenu.setVisibility(View.INVISIBLE);
               mRlProgressBar.setVisibility(View.VISIBLE);
               mStopCut.setVisibility(View.VISIBLE);
               mCutKey.setVisibility(View.INVISIBLE);
               mBack.setVisibility(View.INVISIBLE);
               mKey.setVisibility(View.VISIBLE);  //显示钥匙
               if(cutToken==1){
                   mBtnSecondCut.setVisibility(View.INVISIBLE);
               }else if(cutToken==2){
                   mBtnFirstCut.setVisibility(View.INVISIBLE);
               }
           }else if(resultCode==MessageTipsActivity.CUT_NO_DETECTION){  //不检测  开始切割
               mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
               mTvTextHint1.setVisibility(View.INVISIBLE);
               mTvTextGuideHint.setVisibility(View.INVISIBLE);
               mTvTextCutHint.setVisibility(View.VISIBLE);
               mMenuWindow.dismiss();
               mMenu.setVisibility(View.INVISIBLE);
               mRlProgressBar.setVisibility(View.VISIBLE);
               mStopCut.setVisibility(View.VISIBLE);
               mCutKey.setVisibility(View.INVISIBLE);
               mBack.setVisibility(View.INVISIBLE);
               mKey.setVisibility(View.VISIBLE);  //显示钥匙
               if(cutToken==1){
                   mBtnSecondCut.setVisibility(View.INVISIBLE);
               }else if(cutToken==2){
                   mBtnFirstCut.setVisibility(View.INVISIBLE);
               }
               if (serialDriver != null) {
                   serialDriver.write(cutOrder.getBytes(), cutOrder.length());
               }
           }
    }
}
