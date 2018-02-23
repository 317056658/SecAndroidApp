package com.kkkcut.www.myapplicationkukai.keyDataSelect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.SortGroupMemberAdapter;
import com.kkkcut.www.myapplicationkukai.dao.KeyInfoDaoManager;

import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Mfg;
import com.kkkcut.www.myapplicationkukai.entity.Model;
import com.kkkcut.www.myapplicationkukai.publicKeyCut.FrmKeyCutMainActivity;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class KeyCategorySelectActivity extends AppCompatActivity implements SectionIndexer, View.OnClickListener {
    private ListView sortListView;
    private SideBar sideBar;
    private TextView dialog;
    private SortGroupMemberAdapter adapter;
    private ClearEditText mClearEditText;
    private RelativeLayout mHeadTitle;
    private LinearLayout titleLayout;
    private TextView title, mTvStepText;
    public  static final int KEY_DATA_LOAD_TYPE_MFG=0;  //钥匙数据类型 制造商
    public  static final int KEY_DATA_LOAD_TYPE_MODEL=1; //钥匙数据类型 型号
    public  static final int  KEY_DATA_LOAD_TYPE_KEY_INFO=2;
    private ImageView img;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    /**
     * 根据拼音来排列ListView里面的品牌数据类
     */
    private MfgPinyinComparator mfgComparator;
    private ModelPinyinComparator modelPinyinComparato;
    private KeyInfoPinyinComparator keyInfoPinyinComparato;

    //存放钥匙制造商的数据
    private List<Mfg> mMfgList;
    //存放钥匙型号的数据
    private List<Model> mModelList;
    //存放钥匙信息的数据
    private List<KeyInfo> mKeyInfoList;
    // 型号标记
    private boolean backMfgData;
    //年份 标记
    private boolean backModelData;

    InputMethodManager imm;
    //装饰类
    private View mDecorView;
    private Button mBtnBack;
    private int selectType;
    private int mDataLoadingType;  //数据加载类型
    private HashMap<String,String> languageMap;  // 保存传过来的语言类型数据
    private KeyInfoDaoManager  keyInfoDaoManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_select);
        //获得装饰类
        mDecorView = getWindow().getDecorView();
        this.getIntentData();
        //获得数据库对象
        keyInfoDaoManage=KeyInfoDaoManager.getInstance();
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        //初始化view
        initViews();
        // 默认软键盘不主动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //获得系统软键盘
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 获得传过来的意图数据
     */
    private  void  getIntentData(){
        //获得传过来的意图数据
        Intent intent=getIntent();
        selectType = intent.getIntExtra("selectType", -1);
        languageMap= (HashMap<String, String>) intent.getSerializableExtra("language");
    }

    /**
     * 启动当前Activity
     * @param context
     * @param languageMap
     * @param selectType
     */
    public  static void  startKeySelectActivity(Context context,
                                               HashMap<String,String> languageMap,int selectType){
        Intent  intent=new Intent(context,KeyCategorySelectActivity.class);
        intent.putExtra("selectType",selectType);
        intent.putExtra("language",languageMap);
        context.startActivity(intent);
    }
    /**
     * 根据类型查询钥匙类型
     * 查询钥匙制造商数据
     *
     * @param type
     */
    private List<Mfg> queryManufacturerData(int type) {
        return  keyInfoDaoManage.mfgByCategoryQuery(type);
    }

    /**
     * 根据id(CardNumber)
     * 查询钥匙型号数据
     */
    private List<Model> queryModelData(int id) {
        return  keyInfoDaoManage.modelByMfgCardNumberQuery(id);
    }

    /**
     * 根据型号id和钥匙种类
     * 查询系列表和基础数据表的数据
     * @param id
     */
    private List<KeyInfo> querySeriesAndProfileData(int id, int keyCategory) {
       return  keyInfoDaoManage.QuerySeriesAndProfileByModelID(id, keyCategory);
    }

    //返回到主界面
    public void backHome(View v) {
        this.finish();
    }

    private void initViews() {
        // 获得 头部标题线性布局
        mHeadTitle = (RelativeLayout) findViewById(R.id.ll_title);
        traversalViewsSetText(mHeadTitle);
        //1。获得线性布局
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) this.findViewById(R.id.title_layout_catalog);
        mTvStepText = (TextView) findViewById(R.id.tv_name);
        // 获得 返回按钮的实例
        mBtnBack = (Button) findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(this);
        //获得图片
        img = (ImageView) this.findViewById(R.id.img);
        mfgComparator = new MfgPinyinComparator();
        modelPinyinComparato= new ModelPinyinComparator();
        keyInfoPinyinComparato =new KeyInfoPinyinComparator();
        sideBar = (SideBar) findViewById(R.id.sidebar);
        dialog = (TextView) findViewById(R.id.tv_dialog);
        sideBar.setTextView(dialog);
        //获得自定义EditText
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        //禁止EditText的剪切板
        Tools.disableShearPlate(mClearEditText);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置

                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }
            }
        });

        //存储名字的ListView
        sortListView = (ListView) findViewById(R.id.lv_store);
        //ListView  item点击事件
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (mDataLoadingType == KEY_DATA_LOAD_TYPE_MFG) {  // listViewItem动作等于0 代表钥匙型号数据加载
                    mDataLoadingType =KEY_DATA_LOAD_TYPE_MODEL;
                    mModelList= queryModelData(mMfgList.get(position).getId());
                    formLanguageSetListViewShowData();
                    // 根据a-z进行排序源数据
                    Collections.sort(mModelList,modelPinyinComparato);
                    lastFirstVisibleItem = -1;
                    //刷新ListView
                    adapter.setDataSource(mModelList, mDataLoadingType);
                    adapter.notifyDataSetChanged();
                    sortListView.setSelectionAfterHeaderView();
                    //显示返回键
                    mBtnBack.setVisibility(View.VISIBLE);
                    backMfgData = true;
                    mTvStepText.setText(mMfgList.get(position).getShowName());
                } else if (mDataLoadingType == KEY_DATA_LOAD_TYPE_MODEL) {  // 代表查询系列
                    mKeyInfoList= querySeriesAndProfileData(mModelList.get(position).getId(), selectType);
                    mDataLoadingType =KEY_DATA_LOAD_TYPE_KEY_INFO;
                    formLanguageSetListViewShowData();
                    lastFirstVisibleItem = -1;
                    //准备好数据源
                    // 根据a-z进行排序源数据
                    Collections.sort(mKeyInfoList,keyInfoPinyinComparato);
                    //刷新ListView
                    adapter.setDataSource(mKeyInfoList, mDataLoadingType);
                    adapter.notifyDataSetChanged();
                    sortListView.setSelectionAfterHeaderView();
                    backMfgData = false;
                    backModelData = true;
                    mTvStepText.setText(mTvStepText.getText().toString() + ">" +mModelList.get(position).getShowName());
                } else if (mDataLoadingType == KEY_DATA_LOAD_TYPE_KEY_INFO) {
                    String step=mTvStepText.getText().toString() + ">" + mKeyInfoList.get(position).getCombinationText();
                    FrmKeyCutMainActivity.startFrmKeyCutMainActivity(KeyCategorySelectActivity.this,mKeyInfoList.get(position),
                            step,languageMap,1);
                }
            }

        });

        mMfgList=queryManufacturerData(selectType);
        this.formLanguageSetListViewShowData();
        mDataLoadingType =0;
        // 根据a-z进行排序源数据
        Collections.sort(mMfgList, mfgComparator);
        adapter = new SortGroupMemberAdapter(this);
        adapter.setDataSource(mMfgList, mDataLoadingType);
        sortListView.setAdapter(adapter);

        sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            //用于监听ListView滑动状态的变化
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //ListView一滑动就隐藏软键盘
                  if(isSoftShowing()){
                      imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(),0);
                      Tools.hideBottomUIMenu(mDecorView);
                  }
            }

            /**
             * firstVisibleItem: 表示在屏幕中第一条显示的数据在adapter中的位置
             * visibleItemCount：则表示屏幕中最后一条数据在adapter中的数据，
             * totalItemCount则是在adapter中的总条数
             *  这个方法只要绑定适配器后，加载第一个显示的view  就会回调该方法
             * */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if(mDataLoadingType ==KEY_DATA_LOAD_TYPE_MFG){
                    if (mMfgList.size() >= 2) {
                        int section = getSectionForPosition(firstVisibleItem);
                        int nextSection = getSectionForPosition(firstVisibleItem + 1);//下一位item
                        int nextSecPosition = getPositionForSection(nextSection);//根据ascii值获取item Position
                        if (firstVisibleItem != lastFirstVisibleItem) {
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                    .getLayoutParams();
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                            title.setText(mMfgList.get(
                                    getPositionForSection(section)).getSortLetters());  //设置首字母
                        }
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = titleLayout.getHeight();
                                int bottom = childView.getBottom(); //获得子view的底部距离
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                        .getLayoutParams();
                                if (bottom < titleHeight) {
                                    float pushedDistance = bottom - titleHeight;  //计算得到推开多少距离
                                    params.topMargin = (int) pushedDistance;
                                    titleLayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        titleLayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                        lastFirstVisibleItem = firstVisibleItem;
                    }

                }else if(mDataLoadingType ==KEY_DATA_LOAD_TYPE_MODEL){
                    if (mModelList.size() >= 2) {
                        int section = getSectionForPosition(firstVisibleItem);
                        int nextSection = getSectionForPosition(firstVisibleItem + 1);//下一位item
                        int nextSecPosition = getPositionForSection(nextSection);//根据ascii值获取item Position
                        if (firstVisibleItem != lastFirstVisibleItem) {
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                    .getLayoutParams();
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                            title.setText(mModelList.get(
                                    getPositionForSection(section)).getSortLetters());  //设置首字母
                        }
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = titleLayout.getHeight();
                                int bottom = childView.getBottom(); //获得子view的底部距离
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                        .getLayoutParams();
                                if (bottom < titleHeight) {
                                    float pushedDistance = bottom - titleHeight;  //计算得到推开多少距离
                                    params.topMargin = (int) pushedDistance;
                                    titleLayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        titleLayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                        lastFirstVisibleItem = firstVisibleItem;
                    }
                }else {
                    if (mKeyInfoList.size() >= 2) {
                        int section = getSectionForPosition(firstVisibleItem);
                        int nextSection = getSectionForPosition(firstVisibleItem + 1);//下一位item
                        int nextSecPosition = getPositionForSection(nextSection);//根据ascii值获取item Position
                        if (firstVisibleItem != lastFirstVisibleItem) {
                            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                    .getLayoutParams();
                            params.topMargin = 0;
                            titleLayout.setLayoutParams(params);
                            title.setText(mKeyInfoList.get(
                                    getPositionForSection(section)).getSortLetters());  //设置首字母
                        }
                        if (nextSecPosition == firstVisibleItem + 1) {
                            View childView = view.getChildAt(0);
                            if (childView != null) {
                                int titleHeight = titleLayout.getHeight();
                                int bottom = childView.getBottom(); //获得子view的底部距离
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                        .getLayoutParams();
                                if (bottom < titleHeight) {

                                    float pushedDistance = bottom - titleHeight;  //计算得到推开多少距离
                                    params.topMargin = (int) pushedDistance;
                                    titleLayout.setLayoutParams(params);
                                } else {
                                    if (params.topMargin != 0) {
                                        params.topMargin = 0;
                                        titleLayout.setLayoutParams(params);
                                    }
                                }
                            }
                        }
                        lastFirstVisibleItem = firstVisibleItem;
                    }
                }
            }
        });

        // 根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 这个时候不需要挤压效果 就把他隐藏掉
                titleLayout.setVisibility(View.GONE);
                // 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }
    private boolean isSoftShowing() {
        //获取当前屏幕内容的高度
        int screenHeight = getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        mDecorView.getWindowVisibleDisplayFrame(rect);

        return (screenHeight - rect.bottom)!= 0;
    }
    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        this.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        if(mDataLoadingType ==KEY_DATA_LOAD_TYPE_MFG){
            List<Mfg> filterDataList = new ArrayList();
            if (TextUtils.isEmpty(filterStr)) {
                filterDataList = mMfgList;
            } else {
                for (Mfg mfg : mMfgList) {
                    String name = mfg.getName();
                    if (name.indexOf(filterStr.toString()) != -1
                            || characterParser.getSelling(name).startsWith(
                            filterStr.toString())) {
                        filterDataList.add(mfg);
                    }
                }
            }
            mMfgList = filterDataList;
            // 根据a-z进行排序
            Collections.sort(mMfgList, mfgComparator);
            adapter.setDataSource(mMfgList, mDataLoadingType);
            adapter.notifyDataSetChanged();
            if (filterDataList.size() == 0) {
                Toast.makeText(this, "No data was found", Toast.LENGTH_SHORT).show();
            }
        }else if(mDataLoadingType ==KEY_DATA_LOAD_TYPE_MODEL){
            List<Model> filterDateList = new ArrayList();
            if (TextUtils.isEmpty(filterStr)) {
                filterDateList = mModelList;
            } else {
                for (Model model : mModelList) {
                    String name = model.getName();
                    if (name.indexOf(filterStr.toString()) != -1
                            || characterParser.getSelling(name).startsWith(
                            filterStr.toString())) {
                        filterDateList.add(model);
                    }
                }
            }
            mModelList = filterDateList;
            // 根据a-z进行排序
            Collections.sort(mModelList, modelPinyinComparato);
            adapter.setDataSource(mModelList, mDataLoadingType);
            adapter.notifyDataSetChanged();
            if (filterDateList.size() == 0) {
                Toast.makeText(this, "No data was found", Toast.LENGTH_SHORT).show();
            }
        }else {
            List<KeyInfo> filterDateList = new ArrayList();
            if (TextUtils.isEmpty(filterStr)) {
                filterDateList = mKeyInfoList;
            } else {
                for (KeyInfo ki : mKeyInfoList) {
                    String name = ki.getCombinationText();
                    if (name.indexOf(filterStr.toString()) != -1
                            || characterParser.getSelling(name).startsWith(
                            filterStr.toString())) {
                        filterDateList.add(ki);
                    }
                }
            }
            mKeyInfoList = filterDateList;
            // 根据a-z进行排序
            Collections.sort(mKeyInfoList,keyInfoPinyinComparato);
            adapter.setDataSource(mKeyInfoList, mDataLoadingType);
            adapter.notifyDataSetChanged();
            if (filterDateList.size() == 0) {
                Toast.makeText(this, "No data was found", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(mDataLoadingType ==0){
            return mMfgList.get(position).getSortLetters().charAt(0);
        }else if(mDataLoadingType ==1){
            return mModelList.get(position).getSortLetters().charAt(0);
        }else {
            return mKeyInfoList.get(position).getSortLetters().charAt(0);
        }
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        if(mDataLoadingType ==0){
            for (int i = 0; i < mMfgList.size(); i++) {
                String sortStr = mMfgList.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }else if(mDataLoadingType ==1){
            for (int i = 0; i < mModelList.size(); i++) {
                String sortStr = mModelList.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }else {
            for (int i = 0; i < mKeyInfoList.size(); i++) {
                String sortStr = mKeyInfoList.get(i).getSortLetters();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 根据语言类型  设置ListView数据源 显示的名字
     */

    private void formLanguageSetListViewShowData(){
            if(mDataLoadingType ==KEY_DATA_LOAD_TYPE_MFG){  //品牌的名字
                String str;
               for (int i = 0; i <mMfgList.size() ; i++) {
                    Mfg mfg= mMfgList.get(i);
                   str=languageMap.get(mfg.getName());
                   if(TextUtils.isEmpty(str)){
                       mfg.setShowName(mfg.getName());
                   }else {
                       mfg.setShowName(str);
                   }
                   // 汉字转换成拼音
                   String pinyin = characterParser.getSelling(mfg.getShowName());
                   // 截取第一个字符转为大写
                   String sortString = pinyin.substring(0, 1).toUpperCase();
                   // 正则表达式，判断首字母是否是英文字母
                   if (sortString.matches("[A-Z]")) {
                       mfg.setSortLetters(sortString.toUpperCase());
                   } else {
                       mfg.setSortLetters("#");
                   }
               }
            }else if(mDataLoadingType==KEY_DATA_LOAD_TYPE_MODEL){  //型号的名字
                String str;
                for (int i = 0; i <mModelList.size() ; i++) {
                    Model model= mModelList.get(i);
                    str=languageMap.get(model.getName());
                    if(TextUtils.isEmpty(str)){
                        model.setShowName(model.getName());
                    }else {
                        model.setShowName(str);
                    }
                    String pinyin = characterParser.getSelling(model.getShowName());
                    Log.d("显示的名字123", "formLanguage: "+model.getShowName());
                    // 截取第一个字符转为大写
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        model.setSortLetters(sortString.toUpperCase());
                    } else {
                        model.setSortLetters("#");
                    }
                }

            }else if(mDataLoadingType==KEY_DATA_LOAD_TYPE_KEY_INFO){   //这个不用判断
                for (int i = 0; i <mKeyInfoList.size() ; i++) {
                    KeyInfo ki=mKeyInfoList.get(i);
                    // 汉字转换成拼音
                    String pinyin = characterParser.getSelling(ki.getCombinationText());
                    // 截取第一个字符转为大写
                    String sortString = pinyin.substring(0, 1).toUpperCase();
                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        ki.setSortLetters(sortString.toUpperCase());
                    } else {
                        ki.setSortLetters("#");
                    }
                }
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);

    }


    /**
     * 遍历 View ，设置setText;
     *
     * @param viewGroup
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
                    //等于空就是默认值
                    tv.setText(languageMap.get(tv.getText().toString()));
                }
            } else if (view instanceof ViewGroup) {
                // 若是布局控件（LinearLayout或RelativeLayout）,继续查询子View
                this.traversalViewsSetText((ViewGroup) view);
            }
        }
    }

    /**
     * 当前活动加载完毕后  窗口就获得焦点，自动调用此方法
     *
     * @param hasFocus
     */

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (backMfgData) {
                    mTvStepText.setText("");
                    mDataLoadingType = 0;
                    //刷新ListView
                    adapter.setDataSource(mMfgList, mDataLoadingType);
                    adapter.notifyDataSetChanged();  //刷新
                    mBtnBack.setVisibility(View.INVISIBLE);
                    backMfgData = false;
                    lastFirstVisibleItem = -1;
                } else if (backModelData) {
                    int index = mTvStepText.getText().toString().indexOf(">");
                    mTvStepText.setText(mTvStepText.getText().toString().substring(0, index));
                    mDataLoadingType = 1;
                    adapter.setDataSource(mModelList, mDataLoadingType);
                    sortListView.setAdapter(adapter);
                    backModelData = false;
                    backMfgData = true;
                    lastFirstVisibleItem = -1;
                }
                break;
        }
    }
}
