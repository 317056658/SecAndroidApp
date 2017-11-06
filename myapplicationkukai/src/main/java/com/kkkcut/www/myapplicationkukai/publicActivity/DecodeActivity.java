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
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.dialogActivity.ExceptionActivity;
import com.kkkcut.www.myapplicationkukai.dialogActivity.KeyDecodeOperationTipsActivity;
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
import com.kkkcut.www.myapplicationkukai.entity.ClampLocating;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.KeyType;
import com.kkkcut.www.myapplicationkukai.entity.MessageEvent;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.utils.logDocument.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.kkkcut.www.myapplicationkukai.R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes;

public class DecodeActivity extends AppCompatActivity implements View.OnClickListener {
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
    private Key key;
    private float minDepth;
    private int imgFlag;
    private int isRound = 0;
    private int index = 0;
    private  String step;
    private List<ClampLocating> clampLocatingList;
    private String readToothOrder;  //读齿命令
    private ClampLocating cl;
    private PopupWindow mDetectionLocationMode;
    private ImageView mIvLocateMode, mIvNoDetect, mIvUpDownLocation, mIvBilateralLocation, mIvCuspLocation, mIvThreeTerminalLocation;
    private int readDetectionMode = 0;  //定位方式默认为0,  读齿的定位方式
    private HashMap<String,String>  languageMap;
    private ProlificSerialDriver  serialDriver;
    private MyHandler mHandler = new MyHandler(this);

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
            DecodeActivity activity = (DecodeActivity) reference.get();
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
                        EventBusUtils.post(new MessageEvent(readData,MessageEvent.CHANGE_KEY_TOOTH_CODE));
                        activity.finish();
                    }
                    break;
                case MicyocoEvent.USER_CANCEL_OPERATE: //用户操作取消
                    if (activity.mDecode.getVisibility() == View.INVISIBLE) {
                        activity.key.setToothCodeDefault();//设置齿的深度名全部为默认X
                        activity.key.setVisibility(View.GONE);
                        activity.key.setToothCodeDefault();
                        activity.mIvClampLocationSelect.setVisibility(View.VISIBLE);
                        activity.mBtnMenu.setVisibility(View.VISIBLE);
                        activity.mDecode.setVisibility(View.VISIBLE);
                        activity.mStopDecode.setVisibility(View.INVISIBLE);
                        activity.mBtnBack.setVisibility(View.VISIBLE);
                        //显示指南
                        activity.mTvGuide.setVisibility(View.VISIBLE);
                        activity.mTvHint.setText("Place a key blank as shown in the below figure.");
                    }else {
                        activity.mMeasure.setTextColor(Color.parseColor("#636363"));
                        activity.mMeasure.setClickable(false);
                        activity.judgeMenuWindowIsHide();
                    }
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
                    activity.key.setToothDepthName(readData);
                    activity.key.redrawKey();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_layout);
        serialDriver=ProlificSerialDriver.getInstance();
        getIntentData();
        initViews();//初始化按钮
        initPopupWindow();//初始化PopupWindow窗口
        clampJudge(ki);  //根据传过来的数据做夹具判断
        judgeKeyDetectLocationMode();  //判断钥匙的检测定位方式
        addKeyViewType();// 添加钥匙View的类型
       // accordingKeyTypeSetParam();
    }

    /**
     * 启动本Activity
     * @param context
     * @param ki
     * @param languageMap
     */
    public static void startDecodeActivity(Context context, KeyInfo ki, HashMap<String, String> languageMap,String step,int  startFlag){
        Intent intent=new Intent(context,DecodeActivity.class);
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
     * 这个方式是临时的
     * 根据钥匙类型设置参数
     */
    private void accordingKeyTypeSetParam() {
         if (ki.getType() == 1) {  //单边钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                locatingSlot=16;
                readDetectionMode=0;
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 2) {  //双轨内槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                readDetectionMode= 2;
                locatingSlot=1;
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                readDetectionMode= 4;
                locatingSlot=4;

            }
        } else if (ki.getType() == 3) {     //单轨外槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                    locatingSlot=4;
                   readDetectionMode=4;
            }
        } else if (ki.getType() == 4) {   //双轨外槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
                locatingSlot=4;
               readDetectionMode=4;
                mIvClampLocationSelect.setImageResource(R.drawable.a9_laser_stop_4);
            }
        } else if (ki.getType() == 5) {   //单轨内槽
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                mIvClampLocationSelect.setImageResource(R.drawable.a9_laser_stop_1);
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {
            }
        } else if (ki.getType() == 6) {   //凹点钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                readDetectionMode=0;
                locatingSlot=6;
                mIvClampLocationSelect.setImageResource(R.drawable.a9_dimple_stop_1);
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 7) {  //角度钥匙
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 8) {//圆筒钥匙/
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {
                readDetectionMode=0;
                locatingSlot=0;
            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        } else if (ki.getType() == 9) {  //侧齿
            if (ki.getAlign() == KeyType.SHOULDERS_ALIGN) {

            } else if (ki.getAlign() == KeyType.FRONTEND_ALIGN) {

            }
        }
    }

    /**
     * 夹具判断
     */
    private void clampJudge(KeyInfo ki) {
        clampLocatingList = new ArrayList<>();
        int type = ki.getType();
        String[] spaceGroup = ki.getSpace().split(";"); //分割space
        String[] depthGroup = ki.getDepth().split(";"); //分割深度
        if (type == 0) {   // 等于0为双边齿
            if (ki.getAlign() == 0) {
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_standard_stop_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                mIvClampLocationSelect.setClickable(false);
                clampLocatingList.add(cl);
            } else if (ki.getAlign() == 1) {
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
                if (maxSpace <= 2750) {
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
            if (ki.getAlign() == 0) {  //肩部定位
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_aux_single_std_1);
                cl.setLocationSlot(16);  //定位槽为16
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
                    Intent messageHintIntent = new Intent(DecodeActivity.this, MessageTipsActivity.class);
                    messageHintIntent.putExtra("Type", 3);
                    messageHintIntent.putExtra("IntentFlag", startFlag);
                    startActivity(messageHintIntent);
                }
            } else if (ki.getAlign() == 1) {  //尖端定位
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
                if (maxSpace <= 2750) {
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
        } else if (type == 2) {
            if (ki.getAlign() == 0) {
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                clampLocatingList.add(cl);
                mIvClampLocationSelect.setClickable(false);
            } else if (ki.getAlign() == 1) {  //尖端定位
                String[] spaceDataArray1 = spaceGroup[0].split(",");
                String[] spaceDataArray2 = spaceGroup[1].split(",");
                int maxSpace = 0;
                int space1 = Integer.parseInt(spaceDataArray1[0]);
                int space2 = Integer.parseInt(spaceDataArray2[0]);
                if (space1 > space2) {
                    maxSpace = space1;
                } else {
                    maxSpace = space2;
                }

                if (maxSpace <= 2750) {
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
                //这个是加了辅助夹具定位档片
                cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_15);
                cl.setLocationSlot(10);  //辅助夹具的槽
                cl.setClamp(1);
                clampLocatingList.add(cl);
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
        } else if (type == 3) {  //单轨外槽
            if (ki.getAlign() == 0) {
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                clampLocatingList.add(cl);
                mIvClampLocationSelect.setClickable(false);
            } else if (ki.getAlign() == 1) {
                mIvClampLocationSelect.setClickable(false);
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
                //这个是加了辅助夹具定位档片
                cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_15);
                cl.setLocationSlot(10);  //辅助夹具的槽
                cl.setClamp(1);
                clampLocatingList.add(cl);

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
                if (maxSpace <= 3250) {
                    cl = new ClampLocating();
                    cl.setImgHint(R.drawable.a9_laser_stop_5);
                    cl.setLocationSlot(5);
                    cl.setClamp(1);
                    clampLocatingList.add(cl);
                }
            }
        } else if (type == 4) {
            if (ki.getAlign() == 0) {
                ClampLocating cl = new ClampLocating();
                cl.setImgHint(R.drawable.a9_laser_stop_1_1);
                cl.setLocationSlot(1);
                cl.setClamp(1);
                clampLocatingList.add(cl);
                mIvClampLocationSelect.setClickable(false);
            } else if (ki.getAlign() == 1) {
                String[] spaceDataArray1 = spaceGroup[0].split(",");
                String[] spaceDataArray2 = spaceGroup[1].split(",");
                int maxSpace = 0;
                ClampLocating cl; //定义一个 夹具定位类
                if (Integer.parseInt(spaceDataArray1[0]) > Integer.parseInt(spaceDataArray2[0])) {
                    maxSpace = Integer.parseInt(spaceDataArray1[0]);
                } else {
                    maxSpace = Integer.parseInt(spaceDataArray2[0]);
                }
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
                if (this.ki.getLastBitting() == 0) {
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
        } else if (ki.getType() == 5) {  //单轨内槽钥匙
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
            if (clampLocatingList.size() >= 1) {
                cl = clampLocatingList.get(0);
                mIvClampLocationSelect.setImageResource(cl.getImgHint());
                locatingSlot = cl.getLocationSlot();
                keyClamp = cl.getClamp();
                mIvClampLocationSelect.setClickable(false);
            } else {
                return;
            }
        } else {
            if (clampLocatingList.size() >= 1) {
                cl = clampLocatingList.get(MyApplication.clampLocatingIndex);
                mIvClampLocationSelect.setImageResource(cl.getImgHint());
                locatingSlot = cl.getLocationSlot();
                keyClamp = cl.getClamp();
            } else {
                return;
            }
        }
        if (keyClamp == 3) {
            mClampTextHint.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (serialDriver != null) {
            serialDriver.setHandler(mHandler);
        }
        Log.e("DecodeActivity", "onStart: ");
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
        View contentView = LayoutInflater.from(this).inflate(R.layout.flattop_probe_pop, null);
        mProbeHintWindow = new PopupWindow(this);
        mProbeHintWindow.setContentView(contentView);
        mProbeHintWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置宽度
        mProbeHintWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置高度
        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(Color.parseColor("#00000000"));
        // 设置外部可点击 点击PopupWindow就消失
        mProbeHintWindow.setOutsideTouchable(true);
        // 设置弹出窗体的背景 要配合 setOutsideTouchable 使用 不然无意义
        mProbeHintWindow.setBackgroundDrawable(dw);
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
        mMenuWindow.setBackgroundDrawable(dw);
        mMenuWindow.setAnimationStyle(R.style.PopupAnimation);
        //检测定位方式，选择窗口
        View mLocationContentView = LayoutInflater.from(this).inflate(R.layout.popupwindow_location_select, null);
        mDetectionLocationMode = new PopupWindow(this);
        mDetectionLocationMode.setContentView(mLocationContentView);
        mDetectionLocationMode.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置宽度
        mDetectionLocationMode.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);  //设置高度
        mDetectionLocationMode.setBackgroundDrawable(dw);  //设置背景为透明
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
                    if (readDetectionMode == 0) {
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 1) {
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 2) {
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                    } else if (readDetectionMode == 3) {
                        mIvCuspLocation.setImageResource(btnautosensetipimage_frmkeydecode_dodecoderes);
                    }
                    mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    readDetectionMode = 4;
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
        if (ki.getType() == 0) {  //双边齿
            mIsRound.setChecked(true);  //选中
            if (ki.getAlign() == 0) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode = 0;
            } else if (ki.getAlign() == 1) {
                //夹具判断
                readDetectionMode = 3;  //默认为0 不检测
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
        } else if (ki.getType() == 1) {  //单边齿
            if (ki.getAlign() == 0) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode = 0;
            } else if (ki.getAlign() == 1) {
                mIvLocateMode.setVisibility(View.INVISIBLE);  //根据钥匙的特性屏蔽定位方式
                readDetectionMode = 0;
            }
        } else if (ki.getType() == 2) {  //  双轨内槽钥匙
            if (ki.getAlign() == 0) {
                readDetectionMode = 1;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == 1) {

            }

        } else if (ki.getType() == 3) {  //单轨外槽
            if (ki.getAlign() == 0) {
                readDetectionMode = 1;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == 1) {
                readDetectionMode = 4;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
            }
        } else if (ki.getType() == 4) {
            if (ki.getAlign() == 0) {
                readDetectionMode = 1;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == 1) {
                readDetectionMode = 4;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
            }
        } else if (ki.getType() == 5) {
            mIsRound.setChecked(true);  //选中
            if (ki.getAlign() == 0) {
                readDetectionMode = 1;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightpressedimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipdisabledimage_frmkeydecode_dodecoderes16);
                mIvCuspLocation.setClickable(false);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosensealldisabledimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setClickable(false);
            } else if (ki.getAlign() == 1) {
                readDetectionMode = 2;
                mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                Log.d("执行了？", "onClick: ");
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
                //定位槽      //定位方式   //是否四舍五入
                readToothOrder = key.getReadOrder(locatingSlot, readDetectionMode, isRound);
                if (keyClamp == 3) {   //选择了 3号夹具
                    Intent intent = new Intent(DecodeActivity.this, KeyDecodeOperationTipsActivity.class);
                    intent.putExtra("ImgFlag", imgFlag);
                    intent.putExtra("Order", readToothOrder);
                    startActivityForResult(intent, 1);  //请求码 是1
                } else if (keyClamp == 1) {  //1号夹具直接发送
                    serialDriver.write(readToothOrder.getBytes(), readToothOrder.length());
                    mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
                    mTvHint.setText("Decoding is now in process. Please\nwait...\t     The result of decoding will be displayed on the screen below.");
                    mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
                    mBtnBack.setVisibility(View.INVISIBLE); //隐藏返回
                    mDecode.setVisibility(View.INVISIBLE);  //隐藏读齿
                    mStopDecode.setVisibility(View.VISIBLE);  //显示停止读齿
                    mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
                    key.setVisibility(View.VISIBLE);  //显示要绘制的钥匙
                }
                //只要点了 decode就保存索引
                MyApplication.clampLocatingIndex = index;
                LogUtils.d("读钥匙指令", readToothOrder);
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
                clampLocationSlotImgSelect();
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
        if (index < clampLocatingList.size()) {
            cl = clampLocatingList.get(index);
            mIvClampLocationSelect.setImageResource(cl.getImgHint());
            locatingSlot = cl.getLocationSlot();
            keyClamp = cl.getClamp();
        } else {
            index = 0;
            cl = clampLocatingList.get(index);
            mIvClampLocationSelect.setImageResource(cl.getImgHint());
            locatingSlot = cl.getLocationSlot();
            keyClamp = cl.getClamp();
        }
        if (keyClamp == 1) {
            if (mClampTextHint.getVisibility() == View.VISIBLE) {
                mClampTextHint.setVisibility(View.INVISIBLE);
            }
            if (ki.getType() == 3) {
                if (ki.getAlign() == 0) {
                } else if (ki.getAlign() == 1) {
                    if (locatingSlot == 10) {
                        readDetectionMode = 2;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    } else {
                        readDetectionMode = 4;
                        mIvLocateMode.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
                    }
                }
            } else if (ki.getType() == 4) {
                if (ki.getAlign() == 1) {
                    if (locatingSlot == 10) {
                        readDetectionMode = 2;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterpressedimage_frmkeydecode_dodecoderes);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                    } else {
                        readDetectionMode = 4;
                        mIvLocateMode.setImageResource(R.drawable.btnautosenseallimage_frmkeydecode_dodecoderes);
                        mIvNoDetect.setImageResource(R.drawable.btnautosensenoimage_frmkeydecode_dodecoderes);
                        mIvUpDownLocation.setImageResource(R.drawable.btnautosenseheightimage_frmkeydecode_dodecoderes);
                        mIvBilateralLocation.setImageResource(R.drawable.btnautosenseycenterimage_frmkeydecode_dodecoderes);
                        mIvCuspLocation.setImageResource(R.drawable.btnautosensetipimage_frmkeydecode_dodecoderes);
                        mIvThreeTerminalLocation.setImageResource(R.drawable.btnautosenseallpressedimage_frmkeydecode_dodecoderes);
                    }
                }
            }
        } else if (keyClamp == 3) {
            if (mClampTextHint.getVisibility() == View.INVISIBLE) {
                mClampTextHint.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 菜单View点击监听器
     */

    View.OnClickListener menuChildViewClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.menu_home://回到主界面
                    menuChildViewIntent.setClass(DecodeActivity.this, MainActivity.class);
                    menuChildViewIntent.setFlags(menuChildViewIntent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(menuChildViewIntent);
                    break;
                case R.id.menu_calibration://打开校准界面
                    mMenuWindow.dismiss();
                    menuChildViewIntent.setClass(DecodeActivity.this, FrmMaintenanceActivity.class);
                    startActivity(menuChildViewIntent);
                    break;
                case R.id.menu_measure:  //打开校准探针和切割刀高度界面
                    menuChildViewIntent.setClass(DecodeActivity.this, MeasureToolActivity.class);
                    menuChildViewIntent.putExtra("Flags", 1);
                    startActivityForResult(menuChildViewIntent, 2);
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
        Log.d("触发咯", "onTouchEvent: ");
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
        if (ki.getType() == KeyType.BILATERAL_KEY) {//双边钥匙
            key = new BilateralKey(this,ki);
            layoutParams = new LinearLayout.LayoutParams(740, 330);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            key.setShowArrows(false);
            mLlAddKey.addView(key, 0);   //添加View
        } else if (ki.getType() == KeyType.UNILATERAL_KEY) {  //单边钥匙
            key = new UnilateralKey(this,ki);

            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(720, 330);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View
        } else if (ki.getType() == KeyType.DUAL_PATH_INSIDE_GROOVE_KEY) {  //双轨内槽
            key = new DualPathInsideGrooveKey(this);
            key.setNeededDrawAttribute(ki);
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740,330);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View
        } else if (ki.getType() == KeyType.MONORAIL_OUTER_GROOVE_KEY) {
            key = new MonorailOuterGrooveKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740, 300);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View
        } else if (ki.getType() == KeyType.DUAL_PATH_OUTER_GROOVE_KEY) {  //双轨外槽钥匙
            key = new DualPathOuterGrooveKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740, 310);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View
        } else if (ki.getType() == KeyType.MONORAIL_INSIDE_GROOVE_KEY) {  //单轨内槽钥匙
            key = new MonorailInsideGrooveKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740, 310);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View

        } else if (ki.getType() == KeyType.CONCAVE_DOT_KEY) {  //凹点钥匙
            key = new ConcaveDotKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740,360);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View

        } else if (ki.getType() == KeyType.ANGLE_KEY) {   //角度钥匙
            key = new AngleKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740, 3000);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View

        } else if (ki.getType() == KeyType.CYLINDER_KEY) {  //圆筒钥匙
            key = new CylinderKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(740, 340);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View
        } else if (ki.getType() == KeyType.SIDE_TOOTH_KEY) {  //侧齿钥匙
            key = new SideToothKey(this);
            key.setNeededDrawAttribute(ki);  //设置需要绘制的属性
            key.setShowArrows(false);
            layoutParams = new LinearLayout.LayoutParams(800,300);
            key.setLayoutParams(layoutParams);
            key.setVisibility(View.GONE);
            mLlAddKey.addView(key, 0);   //添加View

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == 2) {
            mMeasure.setTextColor(Color.parseColor("#636363"));
            mMeasure.setClickable(false);
        } else if (requestCode == 1 && resultCode == 1) {  //开始读钥匙
            mStopDecode.setVisibility(View.VISIBLE);
            mDecode.setVisibility(View.INVISIBLE);
            mIvClampLocationSelect.setVisibility(View.GONE);  //彻底隐藏图片
            key.setVisibility(View.VISIBLE);
            mBtnMenu.setVisibility(View.INVISIBLE);//隐藏菜单
            mTvGuide.setVisibility(View.INVISIBLE); //隐藏指南
            if (isShowMenu) {   //菜单显示就隐藏
                mMenuWindow.dismiss();
                isShowMenu = false;
            }
        }
    }


}
