package com.kkkcut.www.myapplicationkukai.setup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.kkkcut.www.myapplicationkukai.ClampCalibration.frmCalibration;
import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.custom.mPopupWindow;

public class frmMaintenance extends AppCompatActivity implements View.OnClickListener{
    @Override
    public void onClick(View view) {
        MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
         Intent intent= new Intent(this,frmCalibration.class);
        startActivity(intent);
    }

    mPopupWindow mpop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        initBtn();
    }

    /**
     * 初始化按钮
     */
    private void initBtn(){
        //关于我们按钮
        Button setup_btn= (Button)findViewById(R.id.btnAbout);
        setup_btn.setOnClickListener(listener);
        //decoder按钮
        Button btn_d_Dialog= (Button)findViewById(R.id.btnStartDecoderMeasure);
        btn_d_Dialog.setOnClickListener(listener1);
        //cutter按钮
        Button btn_cutter= (Button)findViewById(R.id.btnStartToolMeasure);
        btn_cutter.setOnClickListener(listener2);
        //关闭按钮
        Button btn_close=(Button)findViewById(R.id.btn_close);
        btn_close.setOnClickListener(listenerClose);
    }
    View.OnClickListener  listenerClose=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
             finish();
        }
    };
    View.OnClickListener  listener2=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            mpop=new mPopupWindow(frmMaintenance.this);
            mpop.showAtLocation(findViewById(R.id.llyout), Gravity.CENTER,0,0);

        }
    };

    View.OnClickListener listener1=new View.OnClickListener(){

        public void onClick(View view) {
                    //获得子布局
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
                    View v1 =View.inflate(frmMaintenance.this,R.layout.decoder_dialog,null);
            final AlertDialog dialog   =new AlertDialog.Builder(frmMaintenance.this)
                      .setView(v1)
                      .create();
            // 获取对话框当前的参数
            WindowManager.LayoutParams params =
                    dialog.getWindow().getAttributes();
            params.width = 650;
            //点击外面区域不关闭自定义对话框
            //dialog.setCancelable(false);

            // 设置当前对话框当前的参数 宽度
            dialog.getWindow().setAttributes(params);
            dialog.show();

            Button btn=  (Button)v1.findViewById(R.id.btn_close);
                  btn.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                             dialog.dismiss();
                      }
                  });

        }
    };

    View.OnClickListener  listener=new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            MainActivity.sp.play(MainActivity.music, 1, 1, 0, 0, 1);
            Intent  intent=new Intent(frmMaintenance.this,frmAbout.class);
              startActivity(intent);
        }
    };
}
