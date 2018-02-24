package com.kkkcut.www.myapplicationkukai.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.LanguageAdapter;
import com.kkkcut.www.myapplicationkukai.dao.LanguageDaoManager;
import com.kkkcut.www.myapplicationkukai.entity.LanguageEvent;
import com.kkkcut.www.myapplicationkukai.entity.Multilingual;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.SPutils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;

public class LanguageChoiceActivity extends AppCompatActivity implements View.OnClickListener{
    private ArrayList<Multilingual> languageList;
    private HashMap<String,String> languageMap;
    private Context mContext;
    private View DecorView;
    private String mCurrentLanguage;
    private LanguageAdapter mLanguageAdapter;
    private LanguageDaoManager languageDaoManager;
    private  String  TAG="LanguageChoiceActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_choice);
        getIntentData();
        initViews();
        languageDaoManager=LanguageDaoManager.getInstanc();
    }

    private void initViews() {
        mContext = this;
        DecorView = getWindow().getDecorView();
        TextView mTitle = (TextView) findViewById(R.id.tv_title);
        Button mBtnOK = (Button) findViewById(R.id.btn_ok);
        mBtnOK.setOnClickListener(this);
        Button mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        mBtnCancel.setOnClickListener(this);
        String text =languageMap.get(mTitle.getText().toString());
        if (text != null) {
            mTitle.setText(text);
        }
        text=languageMap.get(mBtnOK.getText().toString());
        if (text != null) {
            mBtnOK.setText(text);
        }
        text=languageMap.get(mBtnCancel.getText().toString());
        if (text != null) {
            mBtnCancel.setText(text);
        }

        ListView mLanguageList = (ListView) findViewById(R.id.language_list);
        languageList = languageDaoManager.queryLanguagesSelect();
        mCurrentLanguage = SPutils.getLocalLanguageType(mContext);
        for (int i = 0; i < languageList.size(); i++) {
            if (languageList.get(i).getTableName().equals(mCurrentLanguage)) {
                languageList.get(i).setChecked(true);
                break;
            }
        }
        mLanguageAdapter = new LanguageAdapter(mContext, languageList);
        mLanguageList.setAdapter(mLanguageAdapter);
        mLanguageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                languageList.get(position).setChecked(true);
                mLanguageAdapter.cancelByCheckedState();
                mLanguageAdapter.notifyDataSetChanged();
            }
        });
    }

    public  static  void startLanguageChoiceActivity(Context context, HashMap<String,String> languageMap){
        Intent intent=new Intent(context,LanguageChoiceActivity.class);
        intent.putExtra("language",languageMap);
        context.startActivity(intent);
    }
    private  void  getIntentData(){
          Intent  intent=getIntent();
        languageMap = (HashMap) intent.getSerializableExtra("language");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_ok:
                String languageTable=mLanguageAdapter.getCheckedLanguageTable();
                if(languageTable.equals(mCurrentLanguage)){
                        finish();
                }else {
                    languageMap = languageDaoManager.queryLanguageTable(languageTable);
                    SPutils.setLocalLanguageType(getApplication(),languageTable);
                    EventBusUtils.post(new LanguageEvent(languageMap));
                    finish();
                }
                break;
            case R.id.btn_cancel:
                finish();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(DecorView);
    }
}
