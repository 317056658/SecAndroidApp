package com.kkkcut.www.myapplicationkukai.publicActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

public class LackToothActivity extends AppCompatActivity {
    private View mDecorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lack_tooth);
        mDecorView=getWindow().getDecorView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }
}
