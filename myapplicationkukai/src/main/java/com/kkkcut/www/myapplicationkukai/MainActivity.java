package com.kkkcut.www.myapplicationkukai;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.kkkcut.www.myapplicationkukai.BluetoothManager.BltContant;
import com.kkkcut.www.myapplicationkukai.BluetoothManager.BltManager;
import com.kkkcut.www.myapplicationkukai.DataCollect.FavoriteActivity;
import com.kkkcut.www.myapplicationkukai.Jpush.msgActivity;
import com.kkkcut.www.myapplicationkukai.NetworkRequest.HttpRequest;
import com.kkkcut.www.myapplicationkukai.Search.frmKeySelect;
import com.kkkcut.www.myapplicationkukai.SortListView.frmKeySelect1;
import com.kkkcut.www.myapplicationkukai.adapter.bluetoothadapter;
import com.kkkcut.www.myapplicationkukai.custom.MsgBtn;
import com.kkkcut.www.myapplicationkukai.dao.DBManager;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao1;
import com.kkkcut.www.myapplicationkukai.setup.frmMaintenance;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.inflate;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{


    private Button MybtnAutomobileKeyCut, MybtnMotorcycleKeyCut, MybtnEdgeKeyCut, MybtnDimpleKeyCut, MybtnTubularKeyCut,
            MybtnSearch, MybtnCutHistory, MybtnCopy, MybtnUserDB, MybtnLastCutKey,
            MybtnFavorite, MybtnCutHistoryMgr, MybtnEngrave, MybtnMaintenance;

    public static SoundPool sp;//声明一个SoundPool
    public static int music;//定义一个整型用load（）；来设置suondID
    //公司图标
    private ImageView logo1;
    //自定义侧滑菜单
    private SlidingMenu menu;



    List<BluetoothDevice> bltList = new ArrayList<BluetoothDevice>();   //集合
    bluetoothadapter myAdapter;
    public static ProgressBar btl_bar;//圆形滚动条
    public static TextView blt_status_text;  //  搜索状态
    public static  Switch blue_switch;     //蓝牙开关状态
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    Toast.makeText(MainActivity.this,"连接失败,请手动连接",Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    BluetoothDevice device2 = (BluetoothDevice) message.obj;
                    Toast.makeText(MainActivity.this,"已连接"+device2.getName()+"设备",Toast.LENGTH_SHORT).show();
                    break;
                case 4://已连接某个设备
                    btl_bar.setVisibility(View.GONE);
                    BluetoothDevice device1 = (BluetoothDevice) message.obj;
                    blt_status_text.setText("已连接" + device1.getName() + "设备");
                    Toast.makeText(MainActivity.this, "已连接" + device1.getName() + "设备", Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    break;
            }
            return false;
        }
    });


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("1AAA2", "onCreate: 创建了");
        //一进来 就加载把数据库文件写进databases文件夹下面
        DBManager Date = new DBManager(this);
        Date.open();
        BtnSound();//加载声音按钮
        init();//初始化按钮
        initSideslip();//侧滑菜单
        BltManager.getInstance().initBltManager(this);//获得蓝牙管理器
        BltManager.mBluetoothAdapter.enable();//静默开启蓝牙

        blueToothRegister(); //注册蓝牙扫描广播
        //  睡眠2秒在触发   代码加载完毕后 这样连接成功率比较大
        try {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(1000);
                        BltManager.getInstance().getBltList(handler,"1");//开始连接
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 按钮的声音
     */
    private void   BtnSound(){
        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        music = sp.load(this, R.raw.glass_01, 1); //把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
    }

    /**
     * 注册蓝牙回调广播
     */
    private void blueToothRegister() {
        BltManager.getInstance().registerBltReceiver(this, new BltManager.OnRegisterBltReceiver() {

            /**搜索到新设备
             * @param device
             */
            @Override
            public void onBluetoothDevice(BluetoothDevice device) {
                if (bltList != null && !bltList.contains(device)) {
                    bltList.add(device);
                }
                if (myAdapter != null)
                    myAdapter.notifyDataSetChanged();
            }

            /**连接中
             * @param device
             */
            @Override
            public void onBltIng(BluetoothDevice device) {
                btl_bar.setVisibility(View.VISIBLE);
                blt_status_text.setText("连接" + device.getName() + "中……");
            }

            /**连接完成
             * @param device
             */
            @Override
            public void onBltEnd(BluetoothDevice device) {
                btl_bar.setVisibility(View.GONE);
                blt_status_text.setText("配对" + device.getName() + "完成");
                myAdapter.notifyDataSetChanged();
            }

            /**取消链接
             * @param device
             */
            @Override
            public void onBltNone(BluetoothDevice device) {
                btl_bar.setVisibility(View.GONE);
                blt_status_text.setText("取消了连接" + device.getName());
            }
        });
    }
    boolean a=false;
    private void checkBlueTooth() {
        if (BltManager.getInstance().getmBluetoothAdapter() == null || !BltManager.getInstance().getmBluetoothAdapter().isEnabled()) {
            blue_switch.setChecked(false);
        } else
            blue_switch.setChecked(true);
        blue_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //启用蓝牙
                    BltManager.getInstance().clickBlt(MainActivity.this, BltContant.BLUE_TOOTH_OPEN);
                    Toast.makeText(MainActivity.this, "本地蓝牙打开", Toast.LENGTH_SHORT).show();
                }else {
                    //禁用蓝牙
                    BltManager.mBluetoothAdapter.disable();
                    Toast.makeText(MainActivity.this, "本地蓝牙已断开连接", Toast.LENGTH_SHORT).show();
                    a = true;
                }
            }
        });
    }



  public static MsgBtn msg;
    private void initSideslip() {
        DisplayMetrics outMetrics = new DisplayMetrics();
        //获得屏幕宽度
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        menu = new SlidingMenu(this);
        // 设置从左边侧滑
        menu.setMode(SlidingMenu.LEFT);
        // 设置滑动模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置菜单与内容之间边距
        menu.setShadowWidth(30);
        // 设置菜单与内容之间边距的颜色  红色
        menu.setShadowDrawable(new ColorDrawable(Color.RED));
        // 设置菜单出现时 原来的视图宽度
        menu.setBehindOffset((int)(outMetrics.widthPixels / 1.5));  //通过计算屏幕总宽度1/3
        // 设置菜单出现时的透明度
        menu.setFadeDegree(0.3f);






        // 将菜单依附到Activity
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        // 设置菜单的布局内容
        View v = inflate(this, R.layout.slide_menu, null);
        LinearLayout   layoutg=  (LinearLayout)v.findViewById(R.id.layoutg);
         msg=new MsgBtn(this);
        SQLiteDao1 sql=new SQLiteDao1(this);
        //查询数据库 有几条未读 有就返回true
         boolean  have = sql.queryRead();
        if(have){
             msg.addMsg();
        }
        layoutg.addView(msg);

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,msgActivity.class);
                startActivity(intent);
            }
        });
        Button Connect = (Button) v.findViewById(R.id.Connect);
        Button Update = (Button) v.findViewById(R.id.Update);
        Update.setOnClickListener(versionUpdate);
        Connect.setOnClickListener(listenerConnect);
        //设置滑动出来的内容
        menu.setMenu(v);
        //如果是左右滑动 需要加入第二个视图..不写报错
        //menu.setSecondaryMenu(R.layout.slide_menu);
    }
    View.OnClickListener versionUpdate=new View.OnClickListener(){
        public void onClick(View view){
            //网络请求后台数据
            HttpRequest.getJson("http://upgrade.kkkcut.com:8033/SECAndroidServer/AppService.ashx?Type=1",MainActivity.this);

        }
    };
    View.OnClickListener listenerConnect = new View.OnClickListener() {

        public void onClick(View view) {
            //自定义框
            View v1 = View.inflate(MainActivity.this, R.layout.bluetooth_list, null);
            //搜素附近蓝牙
            Button searth_switch = (Button) v1.findViewById(R.id.searth_switch);
                   searth_switch.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           btl_bar.setVisibility(View.VISIBLE);
                           blt_status_text.setText("正在搜索设备");
                           blue_switch.setChecked(true);
                           BltManager.getInstance().clickBlt(MainActivity.this, BltContant.BLUE_TOOTH_SEARTH);
                       }
                   });
            btl_bar = (ProgressBar) v1.findViewById(R.id.btl_bar);
            blt_status_text = (TextView) v1.findViewById(R.id.blt_status_text);
            blue_switch = (Switch) v1.findViewById(R.id.blue_switch);
            BltManager.getInstance().clickBlt(MainActivity.this, BltContant.BLUE_TOOTH_SEARTH);//第一次进来 搜索周围蓝牙设备
            //获得自定义框的ListView
            ListView lv = (ListView) v1.findViewById(R.id.bltview);
            myAdapter = new bluetoothadapter(MainActivity.this, bltList);
            lv.setAdapter(myAdapter);
            //更新蓝牙状态

            //显示自定义对话框
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(v1)
                    .create();
                    dialog.show();
                    checkBlueTooth();

            //ListView子点击事件
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                // 使高版本API的代码在低版本SDK不报错
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if(a){
                        //这是提示开启蓝牙
                        BltManager.getInstance().checkBleDevice(MainActivity.this);
                        myAdapter.notifyDataSetChanged();
                    }
                    final BluetoothDevice bluetoothDevice = bltList.get(i);
                    btl_bar.setVisibility(View.VISIBLE);
                    blt_status_text.setText("正在连接" + bluetoothDevice.getName());
                    //链接的操作应该在子线程
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BltManager.getInstance().createBond(bluetoothDevice, handler,null);
                        }
                    }).start();

                }
            });

            // 获取对话框当前的参数
            //  WindowManager.LayoutParams params =dialog.getWindow().getAttributes();
            // params.width = 650;
            //点击外面区域不关闭自定义对话框
            // dialog.setCancelable(false);

            // 设置当前对话框当前的参数 宽度
            // dialog.getWindow().setAttributes(params);
        }
    };

    public void init() {
        logo1 =(ImageView)findViewById(R.id.logo12);
        logo1.setClickable(true);
        logo1.setOnClickListener(listenerLogo);
        MybtnAutomobileKeyCut = (Button) findViewById(R.id.btnAutomobileKeyCut);
        MybtnAutomobileKeyCut.setText("Automobile");
        MybtnAutomobileKeyCut.setOnClickListener(listener1);
        //
        MybtnMotorcycleKeyCut = (Button) findViewById(R.id.btnMotorcycleKeyCut);
        MybtnMotorcycleKeyCut.setText("Motorcycle");
        MybtnMotorcycleKeyCut.setOnClickListener(listener1);
        //
        MybtnEdgeKeyCut = (Button) findViewById(R.id.btnEdgeKeyCut);
        MybtnEdgeKeyCut.setText("Standard");
        MybtnEdgeKeyCut.setOnClickListener(listener1);
        //
        MybtnDimpleKeyCut = (Button) findViewById(R.id.btnDimpleKeyCut);
        MybtnDimpleKeyCut.setText("Dimple");
        MybtnDimpleKeyCut.setOnClickListener(listener1);
        //
        MybtnTubularKeyCut = (Button) findViewById(R.id.btnTubularKeyCut);
        MybtnTubularKeyCut.setText("Tubular");
        MybtnTubularKeyCut.setOnClickListener(listener1);
        //
        MybtnSearch = (Button) findViewById(R.id.btnSearch);
        MybtnSearch.setText("Search");
        MybtnSearch.setOnClickListener(listener6);
        //
        MybtnCutHistory = (Button) findViewById(R.id.btnCutHistory);
        MybtnCutHistory.setText("Cut History");
        MybtnCutHistory.setOnClickListener(listener7);
        //
        MybtnCopy = (Button) findViewById(R.id.btnCopy);
        MybtnCopy.setText("Duplicating Key");
        MybtnCopy.setOnClickListener(listener8);
        //
        MybtnUserDB = (Button) findViewById(R.id.btnUserDB);
        MybtnUserDB.setText("My Key Info");
        MybtnUserDB.setOnClickListener(listener9);
        //
        MybtnLastCutKey = (Button) findViewById(R.id.btnLastCutKey);
        MybtnLastCutKey.setText("Last Key");
        MybtnLastCutKey.setOnClickListener(listener10);
        //
        MybtnFavorite = (Button) findViewById(R.id.btnFavorite);
        MybtnFavorite.setText("Favorite");
        MybtnFavorite.setOnClickListener(listener11);
        //
        MybtnCutHistoryMgr = (Button) findViewById(R.id.btnCutHistoryMgr);
        MybtnCutHistoryMgr.setText("Key Marking Mgr");
        MybtnCutHistoryMgr.setOnClickListener(listener12);
        //
        MybtnEngrave = (Button) findViewById(R.id.btnEngrave);
        MybtnEngrave.setText("Key Marking");
        MybtnEngrave.setOnClickListener(listener13);
        //
        MybtnMaintenance = (Button) findViewById(R.id.btnMaintenance);
        MybtnMaintenance.setText("Setup");
        MybtnMaintenance.setOnClickListener(listener14);
    }

    private View.OnClickListener listener1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String nSelectType="0";


            switch (view.getId())
            {
                case   R.id.btnAutomobileKeyCut:
                    nSelectType="1";
                    break;
                case   R.id.btnMotorcycleKeyCut:
                    nSelectType="2";
                    break;
                case   R.id.btnEdgeKeyCut:
                    nSelectType="4";
                    break;
                case   R.id.btnDimpleKeyCut:
                    nSelectType="3";
                    break;
                case   R.id.btnTubularKeyCut:
                    nSelectType="5";
                    break;
            }

            sp.play(music, 1, 1, 0, 0, 1);
            Intent intent = new Intent(MainActivity.this,frmKeySelect1.class);
            intent.putExtra("nSelectType",nSelectType);
            startActivity(intent);

        }
    };

    private View.OnClickListener listener6 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
            Intent intent = new Intent(MainActivity.this, frmKeySelect.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener listener7 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener listener8 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
//            Intent intent = new Intent(MainActivity.this, frmKeySelect.class);
//            startActivity(intent);
        }
    };
    private View.OnClickListener listener9 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
//            Intent intent = new Intent(MainActivity.this, frmKeySelect.class);
//            startActivity(intent);
        }
    };
    private View.OnClickListener listener10 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
//            Intent intent = new Intent(MainActivity.this, frmKeySelect.class);
//            startActivity(intent);
        }
    };


    private View.OnClickListener listener11 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
//            Intent intent = new Intent(MainActivity.this, frmKeySelect.class);
//            startActivity(intent);
        }
    };
    private View.OnClickListener listener12 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
//            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
//            startActivity(intent);
        }
    };
    private View.OnClickListener listener13 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
//            Intent intent = new Intent(MainActivity.this, SetupActivity.class);
//            startActivity(intent);
        }
    };
    private View.OnClickListener listener14 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sp.play(music, 1, 1, 0, 0, 1);
            Intent intent = new Intent(MainActivity.this,frmMaintenance.class);
            startActivity(intent);
        }
    };
    private View.OnClickListener listenerLogo = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //显示菜单
            menu.showMenu();


            Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_LONG).show();
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        //页面关闭的时候要断开蓝牙
        BltManager.getInstance().unregisterReceiver(this);
        //必须强行关闭蓝牙 才能得到资源的释放，
        BltManager.mBluetoothAdapter.disable();
        finish();
    }


    @Override
    public void onClick(View view) {

    }
}
