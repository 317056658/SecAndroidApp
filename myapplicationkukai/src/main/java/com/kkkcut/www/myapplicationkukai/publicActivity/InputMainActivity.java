package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.MessageEvent;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;

import static com.kkkcut.www.myapplicationkukai.R.id.btn_change_decimal;
import static com.kkkcut.www.myapplicationkukai.R.id.btn_clear_data;


public class InputMainActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout mStorageInteger;
    private LinearLayout mStorageDecimal;
    private String   depths;
    private String  depthNames;
    private String[] depthNameArray;
    private GridLayout mStorageIntegerButton, mDecimalskeyboard;
    String[] depthArray;
    private Button mChangeDecimal, mBackWantData, mCancelWindow, mLeftMove, mRightMove, mClearData, mRoundDecimals;
    private CheckBox mIsChangeAllDecimal;
    private View mDecorView;
    private boolean isShowDecimalKeyboard = true;
    private String dataLength;
    private LinearLayout.LayoutParams layoutParams1;
    private LinearLayout.LayoutParams layoutParams2;
    private int index = 0;
    private int position = -1;
    private int group = 0;
    private int depthNameIndex;
    private ScrollView mScrollView;
    private HorizontalScrollView mIntegerHorizontalScroll;
    private HorizontalScrollView mDecimalsHorizontalScroll;
    private HashMap<String,String> languageMap;
    private ArrayList<String[]> keyToothCodeList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_main);
        getIntentData();
        initViews();
        initLoadBtnView();
        initDecimalsKeyboardClickEvent();    //初始化小数键盘的点击事件
    }

    /**
     * 初始化本Activity控件
     */
    private void initViews(){
        mDecorView = getWindow().getDecorView();
        //获得布局参数实例类
        layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 获得存储小数布局
        mStorageInteger = (LinearLayout) findViewById(R.id.ll_storage_integer);
        //获得存储小数布局
        mStorageDecimal = (LinearLayout) findViewById(R.id.ll_storage_decimals);
        //获得改变小数的布局模块
        mChangeDecimal = (Button) findViewById(btn_change_decimal);
        mChangeDecimal.setOnClickListener(this);
        mIsChangeAllDecimal = (CheckBox) findViewById(R.id.cb_decimals_all_change);
        //获得包裹整数布局的横向滚动条
        mIntegerHorizontalScroll = (HorizontalScrollView) findViewById(R.id.integer_hs);
        //获得包裹小数布局的横向滚动条
        mDecimalsHorizontalScroll = (HorizontalScrollView) findViewById(R.id.decimals_hs);
        //获得滚动条
        mScrollView = (ScrollView) findViewById(R.id.scroll);
        mBackWantData = (Button) findViewById(R.id.btn_ok);
        mBackWantData.setOnClickListener(this);
        mCancelWindow = (Button) findViewById(R.id.btn_cancel);
        mCancelWindow.setOnClickListener(this);
        mRoundDecimals = (Button) findViewById(R.id.btn_round);
        mRoundDecimals.setOnClickListener(this);
        //保存数据长度 用来判断有组数
        dataLength = "";
        for (int i = 0; i < keyToothCodeList.size(); i++) {
            String[] s = keyToothCodeList.get(i);
            for (int j = 0; j < s.length; j++) {
                position++;
                if (s[j].contains(".")) {
                    String[] s1 = s[j].split("\\.");
                    dataLength += s1[0];
                    //存储整数
                    TextView tv1 = new TextView(this);
                    tv1.setText(s1[0]);
                    tv1.setClickable(true);
                    tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                    tv1.setTag(position);
                    tv1.setOnClickListener(integerClickListener);
                    mStorageInteger.addView(tv1, layoutParams1);
                    //存储小数
                    TextView tv2 = new TextView(this);
                    tv2.setText("." + s1[1]);
                    tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    mStorageDecimal.addView(tv2, layoutParams2);
                } else {
                    dataLength += s[j];
                    //存储整数
                    TextView tv1 = new TextView(this);
                    tv1.setClickable(true);
                    tv1.setText(s[j]);
                    tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                    tv1.setOnClickListener(integerClickListener);
                    tv1.setTag(position);
                    mStorageInteger.addView(tv1, layoutParams1);
                    //存储小数
                    TextView tv2 = new TextView(this);
                    tv2.setText(".0");
                    tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    mStorageDecimal.addView(tv2, layoutParams2);
                    Log.d("数量是好多？", "onCreate: " + mStorageDecimal.getChildCount());
                }
            }
            dataLength += "-";
            position++;
            //整数
            Log.d("位置是好多？", "onCreate: " + position);
            TextView tv1 = new TextView(this);
            tv1.setText("-");
            tv1.setClickable(true);
            tv1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            tv1.setOnClickListener(integerClickListener);
            tv1.setTag(position);
            mStorageInteger.addView(tv1, layoutParams1);
            //小数
            TextView tv2 = new TextView(this);
            tv2.setText("-");
            tv2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
            mStorageDecimal.addView(tv2, layoutParams2);
        }
        dataLength = dataLength.substring(0, dataLength.length() - 1);
        //删除整数最后裔个view
        mStorageInteger.removeViewAt(mStorageInteger.getChildCount() - 1);
        //删除小数最后一个View
        mStorageDecimal.removeViewAt(mStorageDecimal.getChildCount() - 1);

        for (int j = 0; j < keyToothCodeList.size(); j++) {
            String[] s = keyToothCodeList.get(j);
            for (int q = 0; q < s.length; q++) {
                if (s[j].contains(".")) {
                    //包涵小数点就显示 表示有小数
                    mStorageDecimal.setVisibility(View.VISIBLE);
                    //显示四舍五入
                    mRoundDecimals.setVisibility(View.VISIBLE);
                    break;
                }
            }
            break;
        }
        TextView tv1 = (TextView) mStorageInteger.getChildAt(index);
        tv1.setBackgroundColor(Color.BLUE);
        TextView tv2 = (TextView) mStorageDecimal.getChildAt(index);
        tv2.setBackgroundColor(Color.BLUE);
        mLeftMove = (Button) findViewById(R.id.btn_left_move);
        mLeftMove.setOnClickListener(this);
        mRightMove = (Button) findViewById(R.id.btn_right_move);
        mRightMove.setOnClickListener(this);
        mClearData = (Button) findViewById(btn_clear_data);
        mClearData.setOnClickListener(this);
        //获得网格布局
        mStorageIntegerButton = (GridLayout) findViewById(R.id.gl_integer_keyboard);
        mDecimalskeyboard = (GridLayout) findViewById(R.id.gl_decimals_keyboard);
    }

    /**
     * 启动当前Activity
     * @param context
     * @param languageMap
     */
    public  static  void  startInputMainActivity(Context context, HashMap<String,String> languageMap,ArrayList<String[]> list,String depths,String depthNames){
          Intent intent=new Intent(context,InputMainActivity.class);
          intent.putExtra("language",languageMap);
          intent.putExtra("toothDepthName",list);
          intent.putExtra("depth",depths);
          intent.putExtra("depthName",depthNames);
          context.startActivity(intent);
    }

    /**
     * 获得传过来的意图数据
     */
    private  void  getIntentData(){
        Intent intent = getIntent();//获得传过的意图
        languageMap= (HashMap<String, String>) intent.getSerializableExtra("language");
        keyToothCodeList= (ArrayList<String[]>) intent.getSerializableExtra("toothDepthName");
        depths =intent.getStringExtra("depth");
        depthNames=intent.getStringExtra("depthName");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }

    View.OnClickListener integerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            int position = (int) tv.getTag();
            int current = position + 1;
            if (tv.getText().toString().equals("-")) {
                //得到１　就是第２组，得到０就是就是第一组
                if (index != current) {
                    String[] newString = dataLength.substring(0, position + 1).split("-");
                    Log.d("长度是好多？", "onClick: " + newString.length);
                    if (depthNameArray.length != 1) {
                        mStorageIntegerButton.removeAllViews();
                        loadDepthName(newString.length);
                    }
                    group = newString.length;
                    Log.d("位子是多少？", "onClick: " + position);
                    TextView integerText = (TextView) mStorageInteger.getChildAt(position + 1);
                    integerText.setBackgroundColor(Color.BLUE);
                    integerText = (TextView) mStorageInteger.getChildAt(index);
                    integerText.setBackground(null);
                    //小数的
                    TextView decimalsText = (TextView) mStorageDecimal.getChildAt(position + 1);
                    decimalsText.setBackgroundColor(Color.BLUE);
                    Log.d("索引是好多？", "onClick: " + index);
                    decimalsText = (TextView) mStorageDecimal.getChildAt(index);
                    decimalsText.setBackground(null);
                    index = position + 1;
                }
            } else {
                String[] newString = dataLength.substring(0, position + 1).split("-");
                Log.d("是第几组？", "onClick: " + newString.length);
                //等于1 是第一组
                if (newString.length == 1) {
                    depthNameIndex = 0;
                } else {
                    depthNameIndex = newString.length - 1;
                }
                if (group == depthNameIndex) {
                    Log.d("不加载", "onClick: ");
                } else {
                    group = depthNameIndex;
                    if (depthNameArray.length != 1) {
                        mStorageIntegerButton.removeAllViews();
                        loadDepthName(depthNameIndex);
                        Log.d("加载", "onClick: ");
                    }
                }
                Log.d("位子是", "onClick: " + position);
                Log.d("当前下标是？", "onClick: " +index );
                if(position!=index){
                    //整数的
                    TextView integerText = (TextView) mStorageInteger.getChildAt(position);
                    integerText.setBackgroundColor(Color.BLUE);
                    integerText = (TextView) mStorageInteger.getChildAt(index);
                    integerText.setBackground(null);
                    //小数的
                    TextView decimalsText = (TextView) mStorageDecimal.getChildAt(position);
                    decimalsText.setBackgroundColor(Color.BLUE);
                    integerText = (TextView) mStorageDecimal.getChildAt(index);
                    integerText.setBackground(null);
                    index = position;
                }
            }


        }
    };

    private void loadDepthName(int index) {
        String[] depthName = depthNameArray[index].split(",");
        for (int i = 0; i < depthNameArray[index].split(",").length; i++) {
            Button btn = new Button(this);
            btn.setBackgroundColor(Color.parseColor("#16A4FA"));
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            btn.setId(View.generateViewId());
            btn.setText(depthName[i]);
            btn.setTextColor(Color.parseColor("#030401"));//蓝色
            TextPaint paint = btn.getPaint();
            paint.setFakeBoldText(true);//设置字体加粗
            btn.setOnClickListener(depthNameClickListener);
            //                   GridLayout.Spec rowSpec = GridLayout.spec(i);     //设置它的行和列
//                   GridLayout.Spec columnSpec=GridLayout.spec(j);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.setMargins(0, 6, 6, 0);
            layoutParams.height = 80;
            layoutParams.width = 73;
            mStorageIntegerButton.addView(btn, layoutParams);

        }

    }


    /**
     * 初始化加载Btn
     */

    public void initLoadBtnView() {
        //分割;   求出深度有几组
        depthArray = depths.split(";");
        //分割;   求出深度名有几组
        depthNameArray = depthNames.split(";");
        // 默认加载第一组
        String[] depthName = depthNameArray[0].split(",");
        Log.d("长度是多少？", "initLoadBtnView: " + depthName.length);
        Log.d("depthArray[0]？", "initLoadBtnView: " + depthArray[0].length());
        for (int i = 0; i < depthArray[0].split(",").length; i++) {
            Button btn = new Button(this);
            btn.setBackgroundColor(Color.parseColor("#16A4FA"));
            btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            btn.setId(View.generateViewId());
            btn.setText(depthName[i]);
            btn.setTextColor(Color.parseColor("#030401"));//蓝色
            TextPaint paint = btn.getPaint();
            paint.setFakeBoldText(true);//设置字体加粗
            btn.setOnClickListener(depthNameClickListener);
            //                   GridLayout.Spec rowSpec = GridLayout.spec(i);     //设置它的行和列
//                   GridLayout.Spec columnSpec=GridLayout.spec(j);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.setMargins(0, 6, 6, 0);
            layoutParams.height = 80;
            layoutParams.width = 73;
            mStorageIntegerButton.addView(btn, layoutParams);
        }
    }

    View.OnClickListener depthNameClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button btn = (Button) v;
            //获得整数布局ChildView
            TextView tv = (TextView) mStorageInteger.getChildAt(index);
            tv.setText(btn.getText());
            tv.setBackground(null);
            Log.d("下标是", "onClick: " + index);
            Log.d("深度名点击书记", "onClick: " + mStorageDecimal.getChildCount());
            TextView decimalsText = (TextView) mStorageDecimal.getChildAt(index);
            decimalsText.setBackground(null);
            index++;
            if (index == mStorageInteger.getChildCount()) {
                index--;
                tv.setBackgroundColor(Color.BLUE);
                decimalsText.setBackgroundColor(Color.BLUE);
            } else {
                TextView tv1 = (TextView) mStorageInteger.getChildAt(index);
                TextView tv2 = (TextView) mStorageDecimal.getChildAt(index);
                if (tv1.getText().toString().equals("-")) {
                    index++;
                    tv1 = (TextView) mStorageInteger.getChildAt(index);
                    tv1.setBackgroundColor(Color.BLUE);
                    tv2 = (TextView) mStorageDecimal.getChildAt(index);
                    tv2.setBackgroundColor(Color.BLUE);
                    //得到１　就是第２组，
                    String[] newString = dataLength.substring(0, index).split("-");
                    Log.d("数组的长度是？", "onClick: " + newString.length);
                    if (depthNameArray.length != 1) {
                        mStorageIntegerButton.removeAllViews();
                        loadDepthName(newString.length);
                    }
                    depthNameIndex = newString.length;
                    group = newString.length;
                } else {
                    tv1.setBackgroundColor(Color.BLUE);
                    tv2.setBackgroundColor(Color.BLUE);
                }
            }
        }
    };


    /**
     * 初始化小数的点击事件
     */
    public void initDecimalsKeyboardClickEvent() {
        Button mBtnDecimal1  =(Button)findViewById(R.id.btn_decimal1);
        mBtnDecimal1.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal2  =(Button)findViewById(R.id.btn_decimal2);
        mBtnDecimal2.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal3  =(Button)findViewById(R.id.btn_decimal3);
        mBtnDecimal3.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal4  =(Button)findViewById(R.id.btn_decimal4);
        mBtnDecimal4.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal5  =(Button)findViewById(R.id.btn_decimal5);
        mBtnDecimal5.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal6  =(Button)findViewById(R.id.btn_decimal6);
        mBtnDecimal6.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal7  =(Button)findViewById(R.id.btn_decimal7);
        mBtnDecimal7.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal8  =(Button)findViewById(R.id.btn_decimal8);
        mBtnDecimal8.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal9  =(Button)findViewById(R.id.btn_decimal9);
        mBtnDecimal9.setOnClickListener(changeDecimalsClickListener);

        Button mBtnDecimal0  =(Button)findViewById(R.id.btn_decimal0);
        mBtnDecimal0.setOnClickListener(changeDecimalsClickListener);


    }

    private TextView textView;
    View.OnClickListener changeDecimalsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Log.d("32131", "onClick: ");

            Button btn = (Button) v;
            if (mIsChangeAllDecimal.isChecked()) {
                for (int i = 0; i < mStorageDecimal.getChildCount(); i++) {
                    textView = (TextView) mStorageDecimal.getChildAt(i);
                    if (!textView.getText().toString().equals("-")) {
                        textView.setText(btn.getText());
                    }
                }
            } else {
                textView = (TextView) mStorageDecimal.getChildAt(index);
                textView.setText(btn.getText());
            }
        }
    };

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_decimal:
                if (mRoundDecimals.getVisibility() != View.VISIBLE) {
                    mRoundDecimals.setVisibility(View.VISIBLE);
                }
                //显示小数
                if (isShowDecimalKeyboard) {
                    //隐藏整数键盘
                    mScrollView.setVisibility(View.GONE);
                    //显示存储小数的布局
                    mStorageDecimal.setVisibility(View.VISIBLE);
                    //显示小数的键盘
                    mDecimalskeyboard.setVisibility(View.VISIBLE);
                    //设置为黄色
                    mIntegerHorizontalScroll.setBackgroundResource(R.drawable.edit_no_background_border);
                    //设置为白色
                    mDecimalsHorizontalScroll.setBackgroundColor(Color.WHITE);


                    //显示小数全部一起改变的选择
                    mIsChangeAllDecimal.setVisibility(View.VISIBLE);
                    //设置显示为false
                    isShowDecimalKeyboard = false;
                    TextView tv = (TextView) mStorageDecimal.getChildAt(index);
                    tv.setBackgroundColor(Color.BLUE);
                } else {
                    mIntegerHorizontalScroll.setBackgroundResource(R.drawable.edit_shape);
                    //显整数键盘
                    mScrollView.setVisibility(View.VISIBLE);
                    //隐藏小数全部一起改变的选择
                    mIsChangeAllDecimal.setVisibility(View.INVISIBLE);
                    //隐藏小数的键盘
                    mDecimalskeyboard.setVisibility(View.GONE);
                    mDecimalsHorizontalScroll.setBackgroundColor(Color.parseColor("#FBBE01"));
                    isShowDecimalKeyboard = true;
                }
                break;
            case R.id.btn_ok:
                EventBusUtils.post(new MessageEvent(getAnalysisDepthNameData(),MessageEvent.CHANGE_KEY_TOOTH_CODE));
                finish();//活动销毁
                break;
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_left_move:
                if (index != 0) {
                    //整数位子为白色
                    TextView tv1 = (TextView) mStorageInteger.getChildAt(index);
                    tv1.setBackground(null);
                    //小数位子为黄色
                    TextView tv3 = (TextView) mStorageDecimal.getChildAt(index);
                    tv3.setBackground(null);
                    index--;

                    //整数
                    TextView tv2 = (TextView) mStorageInteger.getChildAt(index);
                    //小数
                    TextView tv4 = (TextView) mStorageDecimal.getChildAt(index);
                    if (tv2.getText().toString().equals("-")) {
                        index--;
                        String[] newString = dataLength.substring(0, index).split("-");
                        mStorageIntegerButton.removeAllViews();
                        if (newString.length == 1) {
                            depthNameIndex = 0;
                        } else {
                            depthNameIndex = newString.length - 1;
                        }
                        if (group == depthNameIndex) {
                            Log.d("不加载", "onClick: ");
                        } else {
                            group = depthNameIndex;
                            if (depthNameArray.length != 1) {
                                mStorageIntegerButton.removeAllViews();
                                loadDepthName(depthNameIndex);
                            }
                        }
                        //整数位子为蓝色
                        tv2 = (TextView) mStorageInteger.getChildAt(index);
                        tv2.setBackgroundColor(Color.BLUE);
                        //小数位子为蓝牙
                        tv4 = (TextView) mStorageDecimal.getChildAt(index);
                        tv4.setBackgroundColor(Color.BLUE);
                    } else {   //写完了  明天继续
                        //整数位子为蓝色
                        tv2.setBackgroundColor(Color.BLUE);
                        //小数位子为蓝蓝色
                        tv4.setBackgroundColor(Color.BLUE);
                    }
                }
                break;
            case R.id.btn_right_move:
                if (index != mStorageInteger.getChildCount() - 1) {
                    //整数位子为白色
                    TextView tv1 = (TextView) mStorageInteger.getChildAt(index);
                    tv1.setBackground(null);//设置为白色
                    //小数位子为黄色
                    TextView tv3 = (TextView) mStorageDecimal.getChildAt(index);
                    tv3.setBackground(null);
                    index++;

                    //整数位子为蓝色
                    TextView tv2 = (TextView) mStorageInteger.getChildAt(index);
                    //小数位子为蓝色
                    TextView tv4 = (TextView) mStorageDecimal.getChildAt(index);
                    if (tv2.getText().toString().equals("-")) {
                        index++;
                        String[] newString = dataLength.substring(0, index).split("-");
                        if (depthNameArray.length != 1) {
                            mStorageIntegerButton.removeAllViews();
                            loadDepthName(newString.length);
                        }
                        depthNameIndex = newString.length;
                        group = newString.length;
                        //整数位子为蓝色
                        tv2 = (TextView) mStorageInteger.getChildAt(index);
                        tv2.setBackgroundColor(Color.BLUE);
                        //小数位子为蓝色
                        tv4 = (TextView) mStorageDecimal.getChildAt(index);
                        tv4.setBackgroundColor(Color.BLUE);
                    } else {
                        //整数位子为蓝色
                        tv2.setBackgroundColor(Color.BLUE);
                        //小数位子为蓝色
                        tv4.setBackgroundColor(Color.BLUE);
                        //明天继续
                    }
                }
                break;
            case btn_clear_data:
                mIntegerHorizontalScroll.setBackgroundResource(R.drawable.edit_shape);
                mDecimalsHorizontalScroll.setBackgroundColor(Color.parseColor("#FBBE01"));
                traversalViewsToInit(mStorageInteger);
                traversalViewsToInit(mStorageDecimal);
                mStorageDecimal.setVisibility(View.INVISIBLE);
                isShowDecimalKeyboard = true;
                //显示整数键盘
                mScrollView.setVisibility(View.VISIBLE);
                //隐藏小数的键盘
                mDecimalskeyboard.setVisibility(View.GONE);
                break;
            case R.id.btn_round:   //四舍五入小数
                roundOff();
                Log.d("点击了四舍五入小数", "onClick: ");
                break;
        }
    }

    /**
     * 四舍五入方法
     */
    private void roundOff() {
        TextView integer;
        TextView decimal;
        int number;
        for (int i = 0; i < mStorageDecimal.getChildCount(); i++) {
            decimal = (TextView) mStorageDecimal.getChildAt(i);
            if (!decimal.getText().equals("-")) {
                number = Integer.parseInt(decimal.getText().charAt(1) + "");
                if (number >= 5) {
                    integer = (TextView) mStorageInteger.getChildAt(i);
                    int position = (int) integer.getTag();
                    String[] newString = dataLength.substring(0, position + 1).split("-");
                    int index = newString.length - 1;
                    if (integer.getText().toString().equals("X")) {
                        String[] depthName = depthNameArray[index].split(",");
                        for (int j = 0; j < depthName.length; j++) {
                            integer.setText(depthName[0]);
                            break;
                        }
                    } else {
                        String[] depthName = depthNameArray[index].split(",");
                        for (int j = 0; j < depthName.length; j++) {
                            if (integer.getText().toString().equals(depthName[depthName.length - 1])) {
                                break;
                            } else {
                                if (integer.getText().toString().equals(depthName[j])) {
                                    integer.setText(depthName[j + 1]);
                                    break;
                                }
                            }
                        }
                    }
                }
                decimal.setText(".0");
            }
        }
        mStorageDecimal.setVisibility(View.INVISIBLE);
        mDecimalskeyboard.setVisibility(View.GONE);
        mStorageInteger.setBackgroundResource(R.drawable.edit_shape);
        mScrollView.setVisibility(View.VISIBLE);
        mRoundDecimals.setVisibility(View.INVISIBLE);
        isShowDecimalKeyboard = true;


    }

    /**
     * 返回到初始状态
     */

    private void traversalViewsToInit(LinearLayout ll) {
        for (int i = 0; i < ll.getChildCount(); i++) {
            TextView tv = (TextView) ll.getChildAt(i);
            if (!tv.getText().toString().equals("-"))
                if (tv.getText().toString().contains(".")) {
                    tv.setText(".0");
                } else {
                    tv.setText("X");
                }
        }
    }

    /**
     * 获得解析好的深度名
     *
     * @return
     */
    private String getAnalysisDepthNameData() {
        String data = "";
        TextView integer;
        TextView decimal;
        for (int i = 0; i < mStorageInteger.getChildCount(); i++) {
            integer = (TextView) mStorageInteger.getChildAt(i);
            decimal = (TextView) mStorageDecimal.getChildAt(i);
            if (!integer.getText().toString().equals("-")) {
                if (decimal.getText().toString().equals(".0")) {
                    data += integer.getText() + ",";
                } else {
                    data += (integer.getText() + "" + decimal.getText() + ",");
                }
            }
        }
        return data;
    }

}


