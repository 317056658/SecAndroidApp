package com.kkkcut.www.myapplicationkukai.AutoType;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.KeyYearParagraph.KeyYearParagraphActivity;
import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;

import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao;
import com.kkkcut.www.myapplicationkukai.entity.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class typeActivity extends AppCompatActivity implements SectionIndexer {
    private ListView sortListView;  //有关
    private SideBar1 sideBar;   //无关
    private TextView dialog;
    private SortGroupMemberAdapter1 adapter;  //适配器
    private ClearEditText1 mClearEditText;

    private LinearLayout titleLayout;
    private TextView title;
    private TextView tvNofriends;
    private ImageView img;
    private TextView  tv_name;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser1 characterParser;
    private List<GroupMemberBean1> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    /**
     * 图片数组
     *
     *
     */
    int img1[]={R.drawable.ic_launcher21};

    InputMethodManager imm;
    List<model> list1;
    String  brand_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audia1);
        // 默认软键盘不弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        //获得软键盘
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Intent  intent=getIntent();
        int brand_id=intent.getIntExtra("brand_id",-1);
         brand_name=intent.getStringExtra("brand_name");
        QueryBrand(brand_id);
        initViews();
    }
    private void QueryBrand(int id){
        SQLiteDao sql=new SQLiteDao(typeActivity.this);
        list1=sql.ModelByMfgID(id);

    }
    public  void backHomes(View v){
        Intent intent=new Intent(typeActivity.this,MainActivity.class);
        // 跳到主界面 跳转到的activity若已在栈中存在，则将其上的activity都销掉。
        intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
    public  void back(View v){
        finish();

    }

    private void initViews() {
        //1。获得线性布局
        titleLayout = (LinearLayout) findViewById(R.id.title_layou1);
        title = (TextView) this.findViewById(R.id.title_layout_catalog1);
        //2 设置传过来的品牌名字
          tv_name = (TextView)findViewById(R.id.tv_name);
          tv_name.setText(brand_name);
        //获得图片
        img=(ImageView)this.findViewById(R.id.img);
        tvNofriends = (TextView) this
                .findViewById(R.id.title_layout_no_friend1);
        // 实例化汉字转拼音类
        characterParser = CharacterParser1.getInstance();

        pinyinComparator = new PinyinComparator();

        sideBar = (SideBar1) findViewById(R.id.sidrbar1);
        dialog = (TextView) findViewById(R.id.dialog1);
        sideBar.setTextView(dialog);

        // 设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar1.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    sortListView.setSelection(position);
                }

            }
        });

        sortListView = (ListView) findViewById(R.id.country_lvcountry1);


        SourceDateList = filledData(list1,R.drawable.ic_launcher21);
        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {



                Intent intent=new Intent(typeActivity.this,KeyYearParagraphActivity.class);
                Toast.makeText(typeActivity.this,list1.get(position).get_Name(),Toast.LENGTH_SHORT).show();
                 intent.putExtra("_id",list1.get(position).get_id());
                 intent.putExtra("name",list1.get(position).get_Name());
                startActivity(intent);


            }
        });

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter =new  SortGroupMemberAdapter1(this, SourceDateList);
        sortListView.setAdapter(adapter);
        sortListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //ListView一滑动就隐藏软键盘
                imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                int section = getSectionForPosition(firstVisibleItem);
                int nextSection = getSectionForPosition(firstVisibleItem);
                int nextSecPosition = getPositionForSection(+nextSection);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                            .getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(SourceDateList.get(
                            getPositionForSection(section)).getSortLetters());
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
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
        });
        mClearEditText = (ClearEditText1) findViewById(R.id.filter_edi1);

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

    /**
     * 为ListView填充数据        数据库补充？？？？？？
     *
     * @param
     * @return
     */
    private List<GroupMemberBean1> filledData(Object ob, int img ) {
        List<model> lstmfg =(List<model>)ob;
        if(lstmfg != null) {
            List<GroupMemberBean1> mSortList = new ArrayList<GroupMemberBean1>();
            for (int i = 0; i < lstmfg.size(); i++) {
                GroupMemberBean1 sortModel = new GroupMemberBean1();
                sortModel.set_id(lstmfg.get(i).get_id());
                sortModel.setName(lstmfg.get(i).get_Name());
                sortModel.setImg(img);
                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(lstmfg.get(i).get_Name());
                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                } else {
                    sortModel.setSortLetters("#");
                }

                mSortList.add(sortModel);
            }
            return mSortList;
        }
        return  null;
    }
    private List<GroupMemberBean1> filledData(String[] date, int img ) {
        List<GroupMemberBean1> mSortList = new ArrayList<GroupMemberBean1>();

        for (int i = 0; i < date.length; i++) {
            GroupMemberBean1 sortModel = new GroupMemberBean1();
            sortModel.setName(date[i]);
            sortModel.setImg(img);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<GroupMemberBean1> filterDateList = new ArrayList<GroupMemberBean1>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
            tvNofriends.setVisibility(View.GONE);
        } else {
            filterDateList.clear();
            for (GroupMemberBean1 sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.indexOf(filterStr.toString()) != -1
                        || characterParser.getSelling(name).startsWith(
                        filterStr.toString())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
        if (filterDateList.size() == 0) {
            tvNofriends.setVisibility(View.VISIBLE);
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


        try {
            Toast.makeText(this,position+"",Toast.LENGTH_SHORT).show();
            return SourceDateList.get(position).getSortLetters().charAt(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return 0;
    }
    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

}
