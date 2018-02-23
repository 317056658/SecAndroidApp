package com.kkkcut.www.myapplicationkukai;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kkkcut.www.myapplicationkukai.Jpush.msgActivity;
import com.kkkcut.www.myapplicationkukai.NetworkRequest.AppUpdate;
import com.kkkcut.www.myapplicationkukai.application.MyApplication;
import com.kkkcut.www.myapplicationkukai.dao.KeyInfoDaoManager;
import com.kkkcut.www.myapplicationkukai.dao.LanguageDaoManager;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao;
import com.kkkcut.www.myapplicationkukai.dialogActivity.TransformProbeActivity;
import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.LanguageEvent;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.entity.Multilingual;
import com.kkkcut.www.myapplicationkukai.entity.PL2303DeviceEvent;
import com.kkkcut.www.myapplicationkukai.keyDataSelect.KeyCategorySelectActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.CreateGestureActivity;
import com.kkkcut.www.myapplicationkukai.publicActivity.LanguageChoiceActivity;
import com.kkkcut.www.myapplicationkukai.publicKeyCut.FrmKeyCutMainActivity;
import com.kkkcut.www.myapplicationkukai.saveData.KeyInformationBoxroomActivity;
import com.kkkcut.www.myapplicationkukai.search.FrmKeySearchActivity;
import com.kkkcut.www.myapplicationkukai.serialDriverCommunication.ProlificSerialDriver;
import com.kkkcut.www.myapplicationkukai.setup.FrmMaintenanceActivity;
import com.kkkcut.www.myapplicationkukai.utils.ActivityWindowUtils;
import com.kkkcut.www.myapplicationkukai.utils.CacheActivityUtils;
import com.kkkcut.www.myapplicationkukai.utils.DatabaseFileUtils;
import com.kkkcut.www.myapplicationkukai.utils.DensityUtils;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.NetWorkUtils;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.utils.logDocument.LogUtils;
import com.kkkcut.www.myapplicationkukai.view.MsgBtn;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    //公司图标
    private ImageView mFirmLogo;
    //自定义侧滑菜单
    private SlidingMenu mSlidingMenu;
    public static MsgBtn msg;
    private View mDecorView;
    private Context mContext;
    private CardView mLanguageChoice;
    private LanguageDaoManager languageDatabase;
    private GridLayout mBody;
    private LinearLayout mFoot;
    private MyHandler mHandler = new MyHandler(this);
    private ArrayList<Multilingual> languageList;
    private  ProgressDialog progressDialog;
    private  static final int DATABASE_FILE_LOAD_FINISH =51;   //数据文件加载完毕
    private  static final String TAG="MainActivity";
    private HashMap<String,String> languageMap;
    private  ProlificSerialDriver  serialDriver=null;

    private static class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MainActivity activity = (MainActivity) reference.get();
            if(activity != null){
                     switch (msg.what){
                         case DATABASE_FILE_LOAD_FINISH:
                             activity.traversalViewsSetText(activity.mBody);
                             activity.traversalViewsSetText(activity.mFoot);
                             activity.serialDriver=ProlificSerialDriver.getInstance(activity.getApplicationContext());
                             break;
                         case MicyocoEvent.CUT_KNIFE_SWITCHOVER_PROBE:
                             Intent  intentTransformProbe  =new Intent(activity,TransformProbeActivity.class);
                             activity.startActivity(intentTransformProbe);
                             break;
                     }
            }
        }
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置 Activity 永不休眠
        ActivityWindowUtils.setScreenNoDormant(getWindow());
        setContentView(R.layout.activity_main);
        CacheActivityUtils.addActivity(this);
        Log.d(TAG, "onCreate: ");
        EventBusUtils.register(this);  //注册
        initViews();//初始化按钮
        initProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseFileUtils databaseFile=new DatabaseFileUtils(MainActivity.this);
                databaseFile.addDatabases();
                KeyInfoDaoManager.getInstance();
                LanguageDaoManager.getInstanc();
                String localLanguageType = SPutils.getLocalLanguageType(MyApplication.getContext());
                languageDatabase=LanguageDaoManager.getInstanc();
                languageList =languageDatabase.queryLanguagesSelect();
                for (int i=0;i<languageList.size();i++){
                    if(localLanguageType.equals(languageList.get(i).getTableName())){
                        if(languageList.get(i).getTableName().equals("0")){ // 等于0就代表英语
                            //new 一个空集合
                                languageMap=new HashMap<>();
                        }else {
                                languageMap=languageDatabase.queryLanguageTable(localLanguageType);
                        }
                    }
                }
                mHandler.sendEmptyMessage(DATABASE_FILE_LOAD_FINISH);
            }
        }).start();
        initSlidingMenu();//侧滑菜单
        mDecorView = getWindow().getDecorView();//获得 Window 装饰类
        this.onCheckUpdate();
    }
   public  static  void startMainActivity(Context context){
       Intent intent=new Intent(context,MainActivity.class);
       context.startActivity(intent);
   }
    /**
     * 语言消息事件
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLanguageEventMain(LanguageEvent event) {
       languageMap=event.getLanguageMap();
        setContentView(R.layout.activity_main);
        initViews();//初始化按钮
        initSlidingMenu();//侧滑菜单
        traversalViewsSetText(mBody);
        traversalViewsSetText(mFoot);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPL2303DeviceEventMain(PL2303DeviceEvent event) {
        if(event.isOpen()){
            if(serialDriver!=null){
                serialDriver.write(Instruction.TWEET_SHORT_THREE_SOUND.getBytes(),Instruction.TWEET_SHORT_THREE_SOUND.length());
                serialDriver.setHandler(mHandler);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        serialDriver.read();  //开始监听下位机
                    }
                }).start();
                progressDialog.cancel();
            }
        }else {   //为false 就是没有授权
                finish();
        }
    }
    /**
     * 初始化加载框
     */
    private void initProgressDialog(){

        progressDialog = new ProgressDialog(this,R.style.progressDialog);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
//        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        progressDialog.setMessage("正在加载设备信息中,请稍后.....");
        progressDialog.show();
        Window window= progressDialog.getWindow();
        View  mDecorView=window.getDecorView();
        Tools.hideBottomUIMenu(mDecorView);
        final WindowManager.LayoutParams lp =   window.getAttributes();
        lp.width= DensityUtils.dip2px(this,500);
        window.setAttributes(lp);
    }


    @Override
    protected void onStart() {
        super.onStart();
       Log.d("类名1？", "onStart: "+MainActivity.class);
          String srt=MainActivity.class.toString();
        Log.d("类名2？", "onStart: "+srt.getClass());
        DisplayMetrics metrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.d("x方向分辨率", "onStart: "+width);
        Log.d("y方向分辨率", "onStart: "+height);
        float xdpi = getResources().getDisplayMetrics().xdpi;
        float ydpi = getResources().getDisplayMetrics().ydpi;
        Log.d("x方向密度", "onCreate: "+xdpi);
        Log.d("y方向密度", "onCreate: "+ydpi);
        if(serialDriver!=null){
            serialDriver.setHandler(mHandler);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void initSlidingMenu() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        //获得屏幕宽度
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        mSlidingMenu = new SlidingMenu(this);
        // 设置从左边侧滑
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        // 设置滑动模式
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置菜单出现时 原来的视图宽度
        int k = (int) (outMetrics.widthPixels / 1.5);
        mSlidingMenu.setBehindOffset(k);  //通过计算屏幕总宽度1/3
        // 设置菜单出现时的透明度
        mSlidingMenu.setFadeDegree(0.3f);
        // 将菜单依附到Activity
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 设置菜单的布局内容
        View contentView = LayoutInflater.from(this).inflate(R.layout.slide_menu, null);
        LinearLayout ll_layout = (LinearLayout) contentView.findViewById(R.id.ll_layout);
        msg = new MsgBtn(this);
        SQLiteDao sql = new SQLiteDao(this);
        //查询数据库 有几条未读 有就返回true
        boolean isHave = sql.queryRead();
        if (isHave) {
            msg.addMsg();
        }
        ll_layout.addView(msg);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, msgActivity.class);
                startActivity(intent);
            }
        });
        //获得sliding菜单,语言文本实例
        mLanguageChoice = (CardView) contentView.findViewById(R.id.cv_language_choice);
        //设置言语切换
        mLanguageChoice.setOnClickListener(slidingMenuItemClickListener);
        CardView mVersionUpdate = (CardView) contentView.findViewById(R.id.cv_version_update);
        mVersionUpdate.setOnClickListener(slidingMenuItemClickListener);
        CardView  mGesturePwd=(CardView) contentView.findViewById(R.id.cv_gesture_pwd);
        mGesturePwd.setOnClickListener(slidingMenuItemClickListener);
        //设置滑动出来的内容
        mSlidingMenu.setMenu(contentView);
   //dp转换为px单位
      int pixels =DensityUtils.dip2px(this,6);
        // 设置菜单与内容之间边距
        mSlidingMenu.setShadowWidth(pixels);

        // 设置菜单与内容之间边距的颜色  红色
        mSlidingMenu.setShadowDrawable(new ColorDrawable(Color.RED));
        //如果是左右滑动 需要加入第二个视图..不写报错
        //btn_menu.setSecondaryMenu(R.layout.slide_menu);
    }

    private void initViews() {
       mContext=this;
        //获得要遍历的布局
        mBody = (GridLayout) findViewById(R.id.gl_body);
        mFoot = (LinearLayout) findViewById(R.id.ll_foot);
        mFirmLogo = (ImageView) findViewById(R.id.iv_firm_logo);
        mFirmLogo.setClickable(true);
        mFirmLogo.setOnClickListener(this);
        //汽车钥匙
       Button automobileKey = (Button) findViewById(R.id.btn_automobile_key);
        automobileKey.setOnClickListener(keyCategoryClickListener);
        //摩托车钥匙
        Button motorcycleKey = (Button) findViewById(R.id.btn_motorcycle_key_cut);
        motorcycleKey.setOnClickListener(keyCategoryClickListener);
        //单边钥匙
        Button unilateralKey = (Button) findViewById(R.id.btn_civil_key_cut);
        unilateralKey.setOnClickListener(keyCategoryClickListener);
        //内槽 凹点钥匙
        Button dimpleKey = (Button) findViewById(R.id.btn_dimple_key_cut);
        dimpleKey.setOnClickListener(keyCategoryClickListener);
        //管状钥匙
        Button tubularKeyCut = (Button) findViewById(R.id.btn_tubular_key_cut);
        tubularKeyCut.setOnClickListener(keyCategoryClickListener);
        // 钥匙数据搜索
        Button keyDataSearch = (Button) findViewById(R.id.btn_search);
        keyDataSearch.setOnClickListener(this);
        // 切割记录
        Button cutHistory = (Button) findViewById(R.id.btn_cut_history);
        cutHistory.setOnClickListener(this);
        //复制钥匙
        Button copyKey = (Button) findViewById(R.id.btn_copy_key);
        copyKey.setOnClickListener(this);
        //自己添加的钥匙
        Button userDB = (Button) findViewById(R.id.btn_user_db);
        userDB.setOnClickListener(this);
        //最后切割钥匙
        Button lastCutKey = (Button) findViewById(R.id.btn_last_cut);
        lastCutKey.setOnClickListener(this);
        //收藏夹
        Button favorite = (Button) findViewById(R.id.btn_favorite);
        favorite.setOnClickListener(this);
        //钥匙打标
        Button  mBtnEngrave = (Button) findViewById(R.id.btnEngrave);
        mBtnEngrave.setOnClickListener(this);
        //设置功能
        Button mBtnMaintenance = (Button) findViewById(R.id.btn_maintenance);
        mBtnMaintenance.setOnClickListener(this);
        //关机
        Button shutdown = (Button) findViewById(R.id.btn_shutdown);
        shutdown.setOnClickListener(this);
        //使用建造者模式  创建公司网站地址链接
      TextView  mHyperlinkURL = (TextView) findViewById(R.id.tv_url);
//        SpannableStringBuilder ss = new SpannableStringBuilder(mHyperlinkURL.getText());
//        ss.setSpan(new URLSpan("http://www.kkkcut.com/"), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        //设置字体颜色
//        mHyperlinkURL.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//        mHyperlinkURL.setMovementMethod(LinkMovementMethod.initInstance());
//        mHyperlinkURL.setText(ss);
        //遍历所有View 设置test属性
    }

    /**
     * 遍历所有View 设置text内容。
     */
    private void traversalViewsSetText(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();//获得这个view下面的所以子view的数量
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof Button) { // 若是Button记录下
                Button btn = (Button) view;
                if (!TextUtils.isEmpty(languageMap.get(btn.getText().toString()))) {
                    btn.setText(languageMap.get(btn.getText().toString()));
                }
            } else if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (!TextUtils.isEmpty(languageMap.get(tv.getText().toString()))) {
                    tv.setText(languageMap.get(tv.getText().toString()));
                }
            } else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.traversalViewsSetText((ViewGroup) view);
            }
        }
    }

    /**
     * 钥匙种类点击监听器
     */
    private View.OnClickListener keyCategoryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int selectType = 0;
            switch (view.getId()) {
                case R.id.btn_automobile_key:  //汽车钥匙
                    selectType = 1;
                    break;
                case R.id.btn_motorcycle_key_cut: //摩托车钥匙
                    selectType = 2;
                    break;
                case R.id.btn_civil_key_cut:  //民用钥匙
                    selectType = 4;
                    break;
                case R.id.btn_dimple_key_cut:   //凹点钥匙
                    selectType = 3;
                    break;
                case R.id.btn_tubular_key_cut:  //  圆筒钥匙
                    selectType = 5;
                    break;
            }
            KeyCategorySelectActivity.startKeySelectActivity(MainActivity.this,languageMap,selectType);

        }
    };

    private View.OnClickListener slidingMenuItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cv_language_choice: //语言切换
                    LanguageChoiceActivity.startLanguageChoiceActivity(mContext,languageMap);
                    break;
                case R.id.cv_version_update:  //版本更新
                    //网络请求后台数据
                    if(NetWorkUtils.isNetworkAvailable(MainActivity.this)){
                        AppUpdate.getUpdateInfo("http://upgrade.kkkcut.com:8033/SECAndroidServer/AppService.ashx?Type=1",MainActivity.this);
                    }else {
                         Toast.makeText(MainActivity.this,"当前网络不可用，请连接网络",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.cv_gesture_pwd: // 手势密码
                    CreateGestureActivity.startCreateGestureActivity(mContext,languageMap);
                    break;
            }
        }
    };
    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        EventBusUtils.unregister(this);
        //关闭串口连接
        if (serialDriver!= null) {
            serialDriver.close();
            //先结束线程
            serialDriver.isEnd = true;
        }
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
        this.saveGlobalVariable();
        CacheActivityUtils.finishAllActivity();
        LogUtils.close();  //关闭日志工具
        //杀死进程 退出整个程序
    }

    /**
     * 保存application全局变量 这个类
     */
     private  void saveGlobalVariable(){
         SPutils.saveKeyInfoBean(MyApplication.getContext(),"keyInfo",MyApplication.ki);
         SPutils.saveString(MyApplication.getContext(),"step",MyApplication.stepText);
         SPutils.saveInt(MyApplication.getContext(),"startFlag",MyApplication.startFlag);
     }

    /**
     * 检测更新
     */
    private void onCheckUpdate(){
            if(NetWorkUtils.isNetworkAvailable(this)){
                AppUpdate.getUpdateInfo("http://upgrade.kkkcut.com:8033/SECAndroidServer/AppService.ashx?Type=1",this);
            }else {
                Toast.makeText(this,"当前网络不可用,请连接网络",Toast.LENGTH_SHORT).show();
            }
    }
    @Override
    public void onClick(View view) {
        Tools.btnSound(this);
        switch (view.getId()) {
            case R.id.btn_search: //搜索界面
                FrmKeySearchActivity.startFrmKeySelectActivity(this,languageMap);
                break;
            case R.id.btn_cut_history:  //跳转切割记录
                KeyInformationBoxroomActivity.startItselfActivity(this,
                                                                    languageMap,
                        KeyInformationBoxroomActivity.FLAG_CUT_HISTORY);
                break;
            case R.id.btn_copy_key:
                break;
            case R.id.btn_user_db:
                break;
            case R.id.btn_last_cut:  //最后一次切割按钮事件
                        if(MyApplication.ki==null){
                            KeyInfo ki = SPutils.getKeyInfoBean(MyApplication.getContext(),"keyInfo");
                            String stepText=SPutils.getString(MyApplication.getContext(),"step","");
                            int  startFlag=SPutils.getInt(MyApplication.getContext(),"startFlag",-1);
                            if(ki ==null){
                               Toast.makeText(this,"没有钥匙信息",Toast.LENGTH_SHORT).show();
                            }else {
                                FrmKeyCutMainActivity.startFrmKeyCutMainActivity(this,ki,stepText,languageMap,startFlag);
                            }
                        }else {
                            FrmKeyCutMainActivity.startFrmKeyCutMainActivity(this,MyApplication.ki,
                                    MyApplication.stepText,languageMap,MyApplication.startFlag);
                        }
                break;
            case R.id.btn_favorite:  //收藏夹
                KeyInformationBoxroomActivity.startItselfActivity(this,languageMap,
                        KeyInformationBoxroomActivity.FLAG_FAVORITE);
                break;
            case R.id.iv_firm_logo:   //公司图标点击事件
                //显示菜单
                    mSlidingMenu.showMenu();
                break;
            case R.id.btnEngrave:
                break;
            case R.id.btn_maintenance://设置界面
                FrmMaintenanceActivity.startFrmMaintenanceActivity(this,languageMap);
                break;
            case R.id.btn_shutdown:  //关机
//                Intent shutdown = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//                shutdown.putExtra("android.intent.extra.KEY_CONFIRM",false);
//                shutdown.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(shutdown);
//                try{
//
//                    //Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","shutdown"});  //关机
//                    Process proc =Runtime.getRuntime().exec(new String[]{"su","-c","reboot "});  //关机    //关机
//                    proc.waitFor();
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
           Tools.hideBottomUIMenu(mDecorView);//隐藏底部导航栏
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }
}
