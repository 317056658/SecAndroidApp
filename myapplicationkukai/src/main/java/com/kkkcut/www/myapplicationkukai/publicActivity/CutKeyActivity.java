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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.DataCollect.KeyInformationBoxroomActivity;
import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteCollectAndCutHistoryDao;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.KeyCutOperationTipsActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MeasureToolActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.MessageTipsActivity;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.AngleKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.BilateralKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.ConcaveDotKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.CylinderKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.DualPathOuterGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.Key;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.MonorailInsideGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.MonorailOuterGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.SideToothKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.UnilateralKey;
import com.kkkcut.www.myapplicationkukai.entity.ClampLocating;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.KeyType;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.ArithUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kkkcut.www.myapplicationkukai.R.id.btn_stop_cut;

public class CutKeyActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBack,mCutterDiameter,mMenu, mCutKey,mStopCut,mBtnPassCut;
    private TextView mQueryName, mClampTextHint;
    private View mDecorView;
    private PopupWindow mSpeedSelectWindow;
    private View mRootView;
    private ImageView mSpeed, mIvClampLocationSelect, mIvLocateMode;
    private KeyInfo ki;
    private Button mSteelSpeed, mSilverSpeed, mBrassSpeed, mHighSpeed, mAddDiameter, mSubtractDiameter,mMeasureCutter;
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
    private float minDepth;
    private int keyClamp,index,imgFlag;
    private boolean isShowMeun=false;
    private int locatingSlot;
    private String cutOrder;
    private Key key;
    private LinearLayout mPutKey;
    private TextView mTvProgress,mTvTextHint1,mTvTextHint2,mTvTextCutHint;
    private List<ClampLocating> clampLocatingList;
    private ClampLocating cl;
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
            String data="";
            if(msg.obj!=null){
                data=msg.obj.toString();
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
                            activity.key.setVisibility(View.GONE);  //隐藏钥匙
                            activity.mIvClampLocationSelect.setVisibility(View.VISIBLE);  //显示这个夹具类型选择ImageView
                            activity.mTvTextHint1.setVisibility(View.VISIBLE);
                            activity.mTvTextHint2.setVisibility(View.VISIBLE);
                            activity.mTvTextCutHint.setVisibility(View.INVISIBLE);
                            activity.mMenu.setVisibility(View.VISIBLE);
                            activity.mRlProgressBar.setVisibility(View.INVISIBLE);
                            activity.mTvProgress.setText("0");
                            activity.mPbLoading.setProgress(0);
                            activity.mStopCut.setVisibility(View.INVISIBLE);
                            activity.mCutKey.setVisibility(View.VISIBLE);
                            activity.mCutKey.setBackgroundResource(R.drawable.btncutimage1_frmkeycut_docutres);
                            activity.mBack.setVisibility(View.VISIBLE);

                            activity.mBtnFirstCut.setBackgroundResource(R.drawable.btntopimage_frmkeycut_docutres);
                            activity.mBtnFirstCut.setTextColor(Color.BLACK);
                            activity.mBtnSecondCut.setVisibility(View.VISIBLE);
                            activity.mBtnSecondCut.setBackgroundResource(R.drawable.btnbottomdisabledimage_frmkeycut_docutres);
                            activity.mBtnSecondCut.setTextColor(Color.parseColor("#B5B5B5"));
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
                        activity.key.setVisibility(View.GONE);  //隐藏钥匙
                        activity.mIvClampLocationSelect.setVisibility(View.VISIBLE);  //显示这个夹具类型选择ImageView
                        activity.mTvTextHint1.setVisibility(View.VISIBLE);
                        activity.mTvTextHint2.setVisibility(View.VISIBLE);
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
                        activity.mBtnSecondCut.setVisibility(View.VISIBLE);
                        activity.mBtnFirstCut.setVisibility(View.VISIBLE);
                    } else  if(activity.mMeasureCutter.isClickable()==false){
                        activity.mMeasureCutter.setTextColor(Color.parseColor("#020701"));
                        activity.mMeasureCutter.setClickable(true);
                    }
                    exceptionIntent.setClass(activity,ExceptionActivity.class);
                    exceptionIntent.putExtra("exception",data);
                    activity.startActivity(exceptionIntent);
                    break;
                case MicyocoEvent.CUT_DATA_BACK:  //代表下位机返回的 切割齿消息
                    String   str=data.replaceAll("[!PG,;]","");
                    int progress = calculateProgress(str.charAt(0),str.charAt(1));
                    activity.mTvProgress.setText(progress+"%");  //设置显示的百分比进度
                    activity.mPbLoading.setProgress(progress); //设置加载的进度
                    break;
                case MicyocoEvent.SAFETYGATE_OPEN:  //安全们打开
                    exceptionIntent.setClass(activity,ExceptionActivity.class);
                    exceptionIntent.putExtra("exception",data);
                    activity.startActivity(exceptionIntent);
                    break;
            }
        }
    }
  private ProlificSerialDriver serialDriver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_key);
        serialDriver=ProlificSerialDriver.getInstance();
        getIntentData();
        initViews();
        initPopupWindow();
        keyTypeJudgeShowBtn();  //根据钥匙类型判断显示按钮
        clampJudge(ki);  //夹具判断
        judgeKeyCutLocationMode();  //判断钥匙的切割定位方式
        addKeyViewType();//准备好要绘制的钥匙
   // accordingKeyTypeSetParam();

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
             if(ki.getType()==2){   //双轨内槽
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
                     String str=Tools.removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mBtnCutDepth.setVisibility(View.VISIBLE);
                     mTvCutDepthValue.setText(str+"mm");
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mBtnCutDepth.setVisibility(View.VISIBLE);
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }

                 NumberFormat nf = NumberFormat.getInstance();
                 nf.format(3.300);
             }else if(ki.getType()==3){  //单轨外槽
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
                 mBtnCutDepth.setVisibility(View.VISIBLE);  //显示切割深度按钮
                 if(ki.getCutDepth()!=0){
                     cutDepth= ki.getCutDepth();
                     String str=Tools.removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mTvCutDepthValue.setText(str+"mm");
                     cutDepth= ki.getCutDepth();
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }else if(ki.getType()==4){
                 mBtnFirstCut.setVisibility(View.VISIBLE);
                 mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                 mBtnFirstCut.setText("1st Cut");
                 cutToken=1; //切割记号为1
                 mBtnSecondCut.setVisibility(View.VISIBLE);
                 mBtnSecondCut.setText("2nd Cut");
                 mBtnCutDepth.setVisibility(View.VISIBLE);

                 if(ki.getCutDepth()!=0){
                     cutDepth= ki.getCutDepth();
                     String str=Tools.removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mTvCutDepthValue.setText(str+"mm");
                     cutDepth= ki.getCutDepth();
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }else if(ki.getType()==5){  //单轨内槽
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

                     String str=Tools.removeUnnecessaryZero(ArithUtils.mul(cutDepth, 0.01));
                     mBtnCutDepth.setText(str+"mm");
                     mTvCutDepthValue.setText(str+"mm");
                     cutDepth= ki.getCutDepth();
                 }else {
                     mBtnCutDepth.setText("1mm");
                     mTvCutDepthValue.setText("1mm");
                     cutDepth=100;
                 }
             }

    }
    /**
     * 根据钥匙类型判断切割定位方式。
     * 设置定位方式
     */
    private void judgeKeyCutLocationMode() {
        if (ki.getType() == 0) {  //双边齿
            if (ki.getAlign() == 0) {
                cutDetectionMode =0; //默认为0
                mIvLocateMode.setImageResource( R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenopressedimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);//不能点击
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);  //不能点击
            } else if (ki.getAlign() == 1) {
                cutDetectionMode =4;  //默认为0 不检测
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightdisabledimage_frmkeycut_docutres);
                mIvUpDownLocation.setClickable(false);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
            }
        } else if (ki.getType() == 1) {  //单边齿
            if (ki.getAlign() == 0) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                cutDetectionMode =0;
            } else if (ki.getAlign() == 1) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                cutDetectionMode =0;
            }
        }else if(ki.getType()==2){
            if(ki.getAlign()==0){
                cutDetectionMode =1;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }else if(ki.getAlign()==1){

            }
        }else if(ki.getType()==3){  //单轨外槽
               if(ki.getAlign()==0){
                   cutDetectionMode =1;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeycut_docutres);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                   mIvCuspLocation.setClickable(false);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setClickable(false);
               }else if(ki.getAlign()==1){
                   cutDetectionMode =4;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
               }
        }else if(ki.getType()==4){  //双轨外槽
               if(ki.getAlign()==0){
                   cutDetectionMode =1;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeycut_docutres);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                   mIvCuspLocation.setClickable(false);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setClickable(false);
               }else if(ki.getAlign()==1){
                   cutDetectionMode =4;
                   mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                   mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                   mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                   mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                   mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                   mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
               }
        }else if(ki.getType()==5){
            if(ki.getAlign()==0){
                cutDetectionMode=2;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeycut_docutres);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setClickable(false);
            }else if(ki.getAlign()==1){
                cutDetectionMode=4;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
            }
        }
    }
    /**
     * 夹具判断
     */
    private void clampJudge(KeyInfo ki) {
        clampLocatingList=new ArrayList<>();
        int type = ki.getType();
        String[] spaceGroup = ki.getSpace().split(";"); //分割space
        String[] depthGroup = ki.getDepth().split(";"); //分割深度
        if (type == 0) {   // 等于0为双边齿
            if(ki.getAlign() == 0){
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_standard_stop_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                mIvClampLocationSelect.setClickable(false);
                clampLocatingList.add(cl);
            }else   if (ki.getAlign() == 1) {
                int maxSpace = 0;
                if (spaceGroup.length == 1) {
                    String[] spaceDataArray = spaceGroup[0].split(",");
                    maxSpace = Integer.parseInt(spaceDataArray[0]);
                } else if (spaceGroup.length == 2) {
                    String[] spaceDataArray1 = spaceGroup[0].split(",");
                    String[] spaceDataArray2 = spaceGroup[1].split(",");
                    int space1 = Integer.parseInt(spaceDataArray1[0]);
                    int space2 = Integer.parseInt(spaceDataArray2[0]);
                    if (space1 > space2) {
                        maxSpace = space1;
                    } else {
                        maxSpace = space2;
                    }
                }
                if (maxSpace <=2750) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_4);
                    cl.setLocationSlot(4);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 2450) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_3);
                    cl.setLocationSlot(3);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 2100) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_2);
                    cl.setLocationSlot(2);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 3250) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_5);
                    cl.setLocationSlot(5);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
            }
        } else if (type == 1) {  //单边齿
            String[] depths = depthGroup[0].split(",");
            if(ki.getAlign() == 0){
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_aux_single_std_1);
                cl.setLocationSlot(16);
                cl.setClamp(3);
                clampLocatingList.add(cl);
                mIvClampLocationSelect.setClickable(false);
                //获得最小深度
                minDepth = Float.parseFloat(depths[depths.length - 1]) * 0.01f;
                if (minDepth > 5.0) {
                    imgFlag = 2;
                    mClampTextHint.setText("Available Clamp:3.5mm,5mm");
                } else if (minDepth > 3.5) {
                    imgFlag = 1;
                    mClampTextHint.setText("Available Clamp:3.5mm");
                } else if (minDepth <= 3.5) {    //小于3.5  这钥匙数据不对
                    Intent messageHintIntent = new Intent(CutKeyActivity.this, MessageTipsActivity.class);
                    messageHintIntent.putExtra("Type", 3);
                    messageHintIntent.putExtra("IntentFlag", startFlag);
                    startActivity(messageHintIntent);
                }
            }else if(ki.getAlign() == 1){
                minDepth = Float.parseFloat(depths[depths.length - 1]) * 0.01f;
                if (minDepth > 5.0) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_aux_single_std_1);
                    cl.setLocationSlot(16);  //定位槽为16
                    cl.setClamp(3);
                    clampLocatingList.add(cl);
                    imgFlag = 2;
                    mClampTextHint.setText("Available Clamp:3.5mm,5mm");
                } else if (minDepth > 3.5) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_aux_single_std_1);
                    cl.setLocationSlot(16);  //定位槽为16
                    cl.setClamp(3);
                    clampLocatingList.add(cl);
                    imgFlag = 1;
                    mClampTextHint.setText("Available Clamp:3.5mm");
                }
                int maxSpace = 0;
                String[] spaceDataArray = spaceGroup[0].split(",");
                maxSpace = Integer.parseInt(spaceDataArray[0]);
                if (maxSpace <= 3250) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_5);
                    cl.setLocationSlot(5);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <=2750) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_4);
                    cl.setLocationSlot(4);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 2450) {
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_3);
                    cl.setLocationSlot(3);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 2100) {//本来是2100
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_standard_stop_2);
                    cl.setLocationSlot(2);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
            }
        }else if(type==2){  //双轨内槽
                if(ki.getAlign()==0){
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_1);
                    cl.setLocationSlot(1);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }else if(ki.getAlign()==1){

                }
        }else if(type==KeyType.MONORAIL_OUTER_GROOVE_KEY){  //单轨外槽
            if(ki.getAlign()==0){
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                clampLocatingList.add(cl);
            }else if(ki.getAlign()==1){
                int maxSpace = 0;
                ClampLocating cl; //定义一个 夹具定位类
                String[] spaceDataArray = spaceGroup[0].split(",");
                maxSpace = Integer.parseInt(spaceDataArray[0]);
                if (maxSpace <= 2750) {
                    cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_4);
                    cl.setLocationSlot(4);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 2450) {
                    cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_3);
                    cl.setLocationSlot(3);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <= 2000) {//本来是2100  换成2000
                    cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_2);
                    cl.setLocationSlot(2);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
                if (maxSpace <=3250) {
                    cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_5);
                    cl.setLocationSlot(5);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
            }
        }else if(type==4){  //双轨外槽
                if(ki.getAlign()==0){
                    ClampLocating cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_1_1);
                    cl.setLocationSlot(1);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                    mIvClampLocationSelect.setClickable(false);
                }else if(ki.getAlign()==1){
                    String[] spaceDataArray1 = spaceGroup[0].split(",");
                    String[] spaceDataArray2 = spaceGroup[1].split(",");
                    int maxSpace = 0;
                    ClampLocating cl; //定义一个 夹具定位类
                    if(Integer.parseInt(spaceDataArray1[0])>Integer.parseInt(spaceDataArray2[0])){
                        maxSpace=Integer.parseInt(spaceDataArray1[0]);
                    }else {
                        maxSpace=Integer.parseInt(spaceDataArray2[0]);
                    }
                    if (maxSpace <=2750) {
                        cl = new ClampLocating();
                        cl.setImgHint(R.drawable.a9_laser_stop_4);
                        cl.setLocationSlot(4);
                        cl.setClamp(1);
                        clampLocatingList.add(cl);
                    }

                    if (maxSpace <=2450) {
                        cl = new ClampLocating();
                        cl.setImgHint(R.drawable.a9_laser_stop_3);
                        cl.setLocationSlot(3);
                        cl.setClamp(1);
                        clampLocatingList.add(cl);
                    }
                    if(this.ki.getLastBitting()==0){
                        if (maxSpace <= 2000) {
                            cl = new ClampLocating();
                            cl.setImgHint(R.drawable.a9_laser_stop_2);
                            cl.setLocationSlot(2);
                            cl.setClamp(1);
                            clampLocatingList.add(cl);
                        }
                    }
                    //这个是加了辅助夹具定位档片
                    cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_15);
                    cl.setLocationSlot(10);  //辅助夹具的槽
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                    if (maxSpace <= 3250) {
                        cl = new ClampLocating();
                        cl.setImgHint(R.drawable.a9_laser_stop_5);
                        cl.setLocationSlot(5);
                        cl.setClamp(1);
                        clampLocatingList.add(cl);
                    }
                }
        }else if(type==KeyType.MONORAIL_INSIDE_GROOVE_KEY){
            if (ki.getAlign() == 0) {
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                clampLocatingList.add(cl);
                mIvClampLocationSelect.setClickable(false);
            } else if (ki.getAlign() == 1) {
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_4);
                cl.setLocationSlot(4);
                cl.setClamp(1);
                clampLocatingList.add(cl);
                mIvClampLocationSelect.setClickable(false);
            }

        }

        //根据索引设置夹具图片和定位槽
        if (MyApplication.clampLocatingIndex == 100) {
            if(clampLocatingList.size()>=1){
                cl = clampLocatingList.get(0);
                mIvClampLocationSelect.setImageResource(cl.getImgHint());
                locatingSlot = cl.getLocationSlot();
                keyClamp =cl.getClamp();
            }else {
                return;
            }
        } else {
            if(clampLocatingList.size()>=1){
                cl = clampLocatingList.get(MyApplication.clampLocatingIndex);
                mIvClampLocationSelect.setImageResource(cl.getImgHint());
                locatingSlot = cl.getLocationSlot();
                keyClamp =cl.getClamp();
            }else {
                return;
            }
        }
        if(keyClamp==3){
            mClampTextHint.setVisibility(View.VISIBLE);
        }
    }
    /**
     * 这个方式是临时的
     * 根据钥匙类型设置参数
     */
    private void accordingKeyTypeSetParam() {
        if (ki.getType() == 0) {  //双边钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                locatingSlot = 1;
                cutDetectionMode = 0;

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                locatingSlot = 4;
                cutDetectionMode = 3;

            }
        } else if (ki.getType() == 1) {  //单边钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                locatingSlot = 16;
                cutDetectionMode = 0;
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 2) {  //双轨内槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                cutDetectionMode = 2;
                locatingSlot = 1;
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                cutDetectionMode = 4;
                locatingSlot = 4;

            }
        } else if (ki.getType() == 3) {     //单轨外槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                locatingSlot = 4;
                cutDetectionMode = 4;
            }
        } else if (ki.getType() == 4) {   //双轨外槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                locatingSlot = 4;
                cutDetectionMode = 4;
                mIvClampLocationSelect.setImageResource(R.drawable.a9_laser_stop_4);
            }
        } else if (ki.getType() == 5) {   //单轨内槽

        } else if (ki.getType() == 6) {   //凹点钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                cutDetectionMode = 0;
                locatingSlot = 6;
                mIvClampLocationSelect.setImageResource(R.drawable.a9_dimple_stop_1);
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 7) {  //角度钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 8) {//圆筒钥匙/
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                cutDetectionMode = 0;
                locatingSlot = 0;
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 9) {  //侧齿
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        }
    }

    private void initViews() {
        mPutKey = (LinearLayout) findViewById(R.id.ll_put_key);
        mBack = (Button) findViewById(R.id.btn_back_close);
        mBack.setOnClickListener(this);
        mQueryName = (TextView) findViewById(R.id.tv_query_name);
        mQueryName.setText("IC Card " + ki.getId() + " >");
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
        mBtnFirstCut =(Button)findViewById(R.id.btn_first_cut);
        mBtnFirstCut.setOnClickListener(this);
        mBtnSecondCut =(Button)findViewById(R.id.btn_second_cut);
        mBtnSecondCut.setOnClickListener(this);
        //停止切割按钮
        mStopCut =(Button)findViewById(btn_stop_cut);
        mStopCut.setOnClickListener(this);

        //当前界面 文本提示1
         mTvTextHint1= (TextView)findViewById(R.id.tv_hint1);
        //当前界面 文本提示2
         mTvTextHint2=(TextView)findViewById(R.id.tv_hint2);
        //钥匙切割文本提示
        mTvTextCutHint=(TextView)findViewById(R.id.tv_cut_hint);
        //获得进度条父布局
        mRlProgressBar = (RelativeLayout)findViewById(R.id.rl_progress_bar);
        //获得进度
        mTvProgress = (TextView)findViewById(R.id.tv_progress);
        //获得进度条
        mPbLoading = (ProgressBar)findViewById(R.id.pb_loading);
        //获得菜单
        mMenu=(Button)findViewById(R.id.btn_cut_menu);
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
        mCutterDiameterChangeWindow = new PopupWindow(this);
        mCutterDiameterChangeWindow.setContentView(cutterContentView);
        mCutterDiameterChangeWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutterDiameterChangeWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mCutterDiameterChangeWindow.setBackgroundDrawable(new ColorDrawable());
        mCutterDiameterChangeWindow.setOutsideTouchable(true);
        //获得显示刀具规格TextView
        mCutterSpecification=(TextView)cutterContentView.findViewById(R.id.tv_cutter_specification);
        mAddDiameter = (Button) cutterContentView.findViewById(R.id.btn_add);
        mAddDiameter.setOnClickListener(popupWindowItemClick);
        mSubtractDiameter = (Button) cutterContentView.findViewById(R.id.btn_subtract);
        mSubtractDiameter.setOnClickListener(popupWindowItemClick);
        mCutterDiameterText = (TextView) cutterContentView.findViewById(R.id.tv_storage_diameter_data);

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
                    judgeCuttterWindowIsHide();
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
                    Log.d("是好多？", "onClick: "+cutDetectionMode);
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
        judgePopupWindowDismiss();
        judgeZCutHierarchySelectIsHide();
        Log.d("点击了", "onTouchEvent: ");
        return super.onTouchEvent(event);
    }

    View.OnClickListener popupWindowItemClick = new View.OnClickListener() {
        Button btn;

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
                    if(diameterData>2.5){
                        diameterData=1;
                        mCutterDiameterText.setText(1 + "mm");
                        mCutterDiameter.setText(1 + "mm");
                        mCutterSpecification.setText("ref.T60-E10-P");
                    }else {
                         if(diameterData==2){
                            mCutterSpecification.setText("ref.T60-E20-P");
                             mCutterDiameterText.setText(2 + "mm");
                             mCutterDiameter.setText(2 + "mm");
                        }else if(diameterData==1.7){
                            mCutterSpecification.setText("ref.T60-E17-P");
                        }else if(diameterData==1.5) {
                            mCutterSpecification.setText("ref.T60-E15-P");
                        }else {
                             mCutterDiameterText.setText(diameterData + "mm");
                             mCutterDiameter.setText(diameterData + "mm");
                            mCutterSpecification.setText("");
                        }
                    }
                    cutterDiameterValue = mCutterDiameterText.getText().toString().replace("mm","");
                    timer.cancel();
                    break;
                case R.id.btn_subtract:
                    diameterData = ArithUtils.sub(diameterData, 0.1);
                    if(diameterData<1){
                        diameterData=2.5;
                        mCutterDiameterText.setText(diameterData + "mm");
                        mCutterDiameter.setText(diameterData + "mm");
                    }else {
                        if(diameterData==1){
                            mCutterSpecification.setText("ref.T60-E10-P");
                            mCutterDiameterText.setText(1 + "mm");
                            mCutterDiameter.setText(1 + "mm");
                        }else if(diameterData==2){
                            mCutterSpecification.setText("ref.T60-E20-P");
                            mCutterDiameterText.setText(2 + "mm");
                            mCutterDiameter.setText(2 + "mm");
                        }else if(diameterData==1.7){
                            mCutterSpecification.setText("ref.T60-E17-P");
                        }else if(diameterData==1.5){
                            mCutterSpecification.setText("ref.T60-E15-P");
                        }else {
                            mCutterSpecification.setText("");
                            mCutterDiameterText.setText(diameterData + "mm");
                            mCutterDiameter.setText(diameterData + "mm");
                        }
                    }
                    cutterDiameterValue = mCutterDiameterText.getText().toString().replace("mm","");
                    timer.cancel();
                    break;
                case R.id.menu_home:   //回到主界面
                    Intent intentHome = new Intent(CutKeyActivity.this, MainActivity.class);
                    intentHome.setFlags(intentHome.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentHome);
                    break;
                case R.id.menu_calibration:  //打开校准界面
                    Intent intentCalibration=new Intent(CutKeyActivity.this,FrmMaintenanceActivity.class);
                    startActivity(intentCalibration);
                    break;
                case R.id.menu_clamp_move:  //发指令夹具移动
                    serialDriver.write(Instruction.FIXTURE_MOVE.getBytes(),Instruction.FIXTURE_MOVE.length());
                    break;
                case R.id.menu_measure:
                    Intent measureIntent =new Intent(CutKeyActivity.this,MeasureToolActivity.class);
                    measureIntent.putExtra("Flags",2);
                    startActivityForResult(measureIntent,1);
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
            case R.id.btn_back_close:
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
            case R.id.btn_cut_menu:  // 界面的菜单
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
            case R.id.btn_cut_key: //准备切割钥匙
                saveCutKeyData();//保存切割记录
                judgeZCutHierarchySelectIsHide();
                judgeSpeedSelectWindowIsHide();
                judgeMenuWindowIsHide();
                judgeLocationModeWindowIsHide();
                judgeCutterDiameterWindowIsHide();
                judgeCutDepthWindowIsHide();
                cutOrder =key.getCutOrder(cutDepth,  //切割深度
                                          locatingSlot, // 定位槽
                                          assistClamp,  //辅助夹具
                                    cutterDiameterValue, //刀具直径
                                          cutSpeed,         //速度
                                          ZCutHierarchyValue,  //Z切割层次
                        cutDetectionMode);   //定位方式
                Log.d("切割指令", "指令==="+ cutOrder);
                if(keyClamp ==3){
                    mCutKey.setBackgroundResource(R.drawable.btncutpressedimage_frmkeycut_docutres);
                    Intent cutIntent =new Intent(CutKeyActivity.this, KeyCutOperationTipsActivity.class);
                    cutIntent.putExtra("ImgFlag",imgFlag);
                    cutIntent.putExtra("Order", cutOrder);
                    startActivityForResult(cutIntent,3);  //请求码是3
               }else if(keyClamp ==1){
                    Intent cutIntent =new Intent(CutKeyActivity.this, KeyCutOperationTipsActivity.class);
                    cutIntent.putExtra("Clamp",keyClamp);
                    cutIntent.putExtra("Order", cutOrder);
                    startActivityForResult(cutIntent,3);  //请求码是3
                      //切割钥匙   做到切割界面
                    mCutKey.setBackgroundResource(R.drawable.btncutpressedimage_frmkeycut_docutres);
                    mCutKey.setClickable(false);
                }
                MyApplication.clampLocatingIndex=index;
                break;
            case R.id.btn_stop_cut:   //停止切割
                if(serialDriver!=null){
                    serialDriver.write(Instruction.STOP_OPERATE.getBytes(),Instruction.STOP_OPERATE.length());
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
            case R.id.btn_first_cut:  //第一次切割
                if(cutToken!=1){
                    mBtnFirstCut.setBackgroundResource(R.drawable.btntopdisabledimage_frmkeycut_docutres);
                    mBtnFirstCut.setTextColor(Color.parseColor("#B5B5B5"));
                    mBtnSecondCut.setBackgroundResource(R.drawable.btnbottomimage_frmkeycut_docutres);
                    mBtnSecondCut.setTextColor(Color.BLACK);
                    cutToken=1; //切割记号为1
                }
                break;
            case R.id.btn_second_cut:  //第二次切割
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
     * 保存切割钥匙的数据
     */
   private  void saveCutKeyData(){
       SQLiteCollectAndCutHistoryDao sqLiteCollectAndCutHistoryDao=
               SQLiteCollectAndCutHistoryDao.getInstance(getApplication());
       //获得表中数据有多少条
      int dataQuantity=sqLiteCollectAndCutHistoryDao.getTableDataNum(sqLiteCollectAndCutHistoryDao.TABLE_CUT_HISTORY);
       if(dataQuantity< KeyInformationBoxroomActivity.CUT_HISTORY_DATA_MAX){
           sqLiteCollectAndCutHistoryDao.insertDataToCutHistory(step,ki,startFlag);
       }else {
           MessageTipsActivity.startMessageTipsActivity(this,MessageTipsActivity.COLLECT_FAILURE);
       }
   }
    /**
     *   判断切割深度窗口是否隐藏
     */
    private void judgeCuttterWindowIsHide(){
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
            mBtnCutDepth.setBackgroundResource(R.drawable.btndepthpressedimage_frmkeycut_docutres);
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
            key=new BilateralKey(this,ki);
            key.setLayoutParams(new LinearLayout.LayoutParams(740, 330));
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setShowArrows(false);
            key.setVisibility(View.GONE);
            //添加View
            mPutKey.addView(key,0);
        } else if (ki.getType() ==  KeyType.UNILATERAL_KEY) {  //单边钥匙
            key = new UnilateralKey(this,ki);
            key.setNeededDrawAttribute(ki);
            key.setShowArrows(false);
            key.setLayoutParams(new LinearLayout.LayoutParams(720,300));
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setShowArrows(false);
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);
        }else if(ki.getType()==KeyType.DUAL_PATH_INSIDE_GROOVE_KEY){  //双轨内槽钥匙
            key = new DualPathOuterGrooveKey(this);
            key.setNeededDrawAttribute(ki);
            key.setShowArrows(false);
            key.setLayoutParams(new LinearLayout.LayoutParams(720,300));
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);
        }else if(ki.getType()==KeyType.MONORAIL_OUTER_GROOVE_KEY){  // 单轨外槽钥匙
            key = new MonorailOuterGrooveKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }else if(ki.getType()==KeyType.DUAL_PATH_OUTER_GROOVE_KEY){ //双轨外槽钥匙
            key = new DualPathOuterGrooveKey(this); //双轨外槽钥匙
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }if(ki.getType()==KeyType.MONORAIL_INSIDE_GROOVE_KEY){  //单轨内槽钥匙
            key = new MonorailInsideGrooveKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }else if(ki.getType()==KeyType.CONCAVE_DOT_KEY){  //凹点钥匙
            key = new ConcaveDotKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }else if(ki.getType()==KeyType.ANGLE_KEY){   //角度钥匙
            key = new AngleKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }else if(ki.getType()==KeyType.CYLINDER_KEY){  //圆筒钥匙
            key = new CylinderKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }else if(ki.getType()==KeyType.SIDE_TOOTH_KEY){  //侧齿钥匙
            key = new SideToothKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            key.setToothDepthName(ki.getKeyToothCode());
            key.setBackgroundResource(R.drawable.edit_shape);
            key.setLayoutParams(new LinearLayout.LayoutParams(740,300));
            key.setVisibility(View.GONE);
            mPutKey.addView(key,0);   //添加View
        }
    }

    /**
     *    夹具定位槽图片选择
     */
    private  void clampLocationSlotImgSelect(){
                index++;
                if (index < clampLocatingList.size()) {
                    cl = clampLocatingList.get(index);
                    mIvClampLocationSelect.setImageResource(cl.getImgHint());
                    locatingSlot = cl.getLocationSlot();
                    keyClamp=cl.getClamp();
                } else {
                    index = 0;
                    cl = clampLocatingList.get(index);
                    mIvClampLocationSelect.setImageResource(cl.getImgHint());
                    locatingSlot = cl.getLocationSlot();
                    keyClamp=cl.getClamp();
                }
        if(keyClamp==1){
            if(mClampTextHint.getVisibility()==View.VISIBLE){
                mClampTextHint.setVisibility(View.INVISIBLE);
            }
            if(ki.getType()==3){  //单轨外槽
                if(ki.getAlign()==0){

                }else if(ki.getAlign()==1){
                    if(locatingSlot==10){
                        cutDetectionMode =2;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }else {
                        cutDetectionMode =4;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
                    }
                }
            }else if(ki.getType()==4){  //双轨外槽
                if(ki.getAlign()==0){

                }else if(ki.getAlign()==1){
                    if(locatingSlot==10){
                        cutDetectionMode =2;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeycut_docutres);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                    }else {
                        cutDetectionMode =4;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeycut_docutres);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeycut_docutres);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeycut_docutres);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeycut_docutres);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeycut_docutres);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeycut_docutres);
                    }
                }
            }
        }else if(keyClamp==3){
            if(mClampTextHint.getVisibility()==View.INVISIBLE){
                mClampTextHint.setVisibility(View.VISIBLE);
            }
        }
    }

    View.OnClickListener  cutDepthSwitchoverListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
           Double  depthValue=   Double.parseDouble(mTvCutDepthValue.getText().toString().replace("mm",""));
            String  value="";
                switch (v.getId()){
                    case R.id.btn_reduce://减
                      value=Tools.removeUnnecessaryZero(ArithUtils.sub(depthValue, 0.1));
                        Log.d("是好多?", "onClick: "+value);
                        if(depthValue<0.5){
                            mTvCutDepthValue.setText("1.5mm");
                            mBtnCutDepth.setText("1.5mm");
                        }else {
                            mBtnCutDepth.setText(value+"mm");
                            mTvCutDepthValue.setText(value+"mm");
                        }

                        cutDepth=(int)ArithUtils.mul(Double.parseDouble(value),100);
                        break;
                    case R.id.btn_increase:  //增加
                        value=Tools.removeUnnecessaryZero(ArithUtils.add(depthValue, 0.1));
                        if(depthValue>1.5){
                            mBtnCutDepth.setText("0.8mm");
                            mTvCutDepthValue.setText("0.8mm");
                        }else {
                            mBtnCutDepth.setText(value+"mm");
                            mTvCutDepthValue.setText(value+"mm");
                        }
                        cutDepth=(int)ArithUtils.mul(Double.parseDouble(value),100);
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
           if(requestCode==1&&resultCode==2){  //代表检测切割刀高度的返回
               mMeasureCutter.setTextColor(Color.parseColor("#636363"));
               mMeasureCutter.setClickable(false);
           }else if(requestCode==3&&resultCode==2){   //返回码为2
               mIvClampLocationSelect.setVisibility(View.GONE);  //隐藏这个夹具类型选择ImageView
               mTvTextHint1.setVisibility(View.INVISIBLE);
               mTvTextHint2.setVisibility(View.INVISIBLE);
               mTvTextCutHint.setVisibility(View.VISIBLE);
               mMenuWindow.dismiss();
               mMenu.setVisibility(View.INVISIBLE);
               mRlProgressBar.setVisibility(View.VISIBLE);
               mStopCut.setVisibility(View.VISIBLE);
               mCutKey.setVisibility(View.INVISIBLE);
               mBack.setVisibility(View.INVISIBLE);
               key.setVisibility(View.VISIBLE);  //显示钥匙
               if(cutToken==1){
                   mBtnSecondCut.setVisibility(View.INVISIBLE);
               }else if(cutToken==2){
                   mBtnFirstCut.setVisibility(View.INVISIBLE);
               }
           }
    }
}
