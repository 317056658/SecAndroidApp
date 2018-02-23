package com.kkkcut.www.myapplicationkukai.dialogActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.utils.Tools;

/**
 *  显示异常提示的Activity窗口
 */
public class ExceptionActivity extends AppCompatActivity {
  private View mDecorView;
    private String exception;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_layout);
        mDecorView =getWindow().getDecorView();
        //可以点击外面取消窗口
//        this.setFinishOnTouchOutside(true);
        this.getIntentData();
        this.initViews();
    }
    private  void initViews(){
        ImageView mIvTips =(ImageView)findViewById(R.id.iv_tips);
        TextView  mTvTextHint =(TextView)findViewById(R.id.tv_text_hint);
        mIvTips.setOnClickListener(closeActivityWindowListener);
        // 根据传过来的异常数据  设置View内容
        if(exception.equals("!EX0;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX1;")){
            mIvTips.setImageResource(R.drawable.eusercancelled);
            mTvTextHint.setText("Operation is canceled.");
        }else  if(exception.equals("!EX2;")){
            mIvTips.setImageResource(R.drawable.eusercancelled);
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX3;")){
            mIvTips.setImageResource(R.drawable.eusercancelled);
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX4;")){
            mIvTips.setImageResource(R.drawable.eusercancelled);
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX5;")){
            mIvTips.setImageResource(R.drawable.eusercancelled);
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX6;")){   //命令错误
            mTvTextHint.setText("Command error has occured.");
        }else  if(exception.equals("!EX7;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX8;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX9;")){
            mIvTips.setImageResource(R.drawable.ematerialposerror);
            mTvTextHint.setText("Key is too thin or position is not correct.");
        }else  if(exception.equals("!EX10;")){
            mIvTips.setImageResource(R.drawable.ecoveropen);
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX11;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX12;")){
            mIvTips.setImageResource(R.drawable.ecoveropen);
            mTvTextHint.setText("Safety cover is open");
        }else  if(exception.equals("!EX13;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX14;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX15;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX16;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX17;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX18;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX19;")){
            mTvTextHint.setText(exception);
        }else  if(exception.equals("!EX20;")){
            mTvTextHint.setText(exception);
        }
    }

    /**
     *  获得传过的意图数据
     */
    private void  getIntentData(){
        Intent  intent=getIntent();
        exception =intent.getStringExtra("exception");
    }


    /**
     * 启动当前Activity
     * @param context
     * @param exception
     */
    public  static void  startItselfActivity(Context context,String exception){
              Intent  intent=new Intent(context,ExceptionActivity.class);
                intent.putExtra("exception",exception);
                 context.startActivity(intent);
    }
    View.OnClickListener  closeActivityWindowListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            ExceptionActivity.this.finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        Tools.hideBottomUIMenu(mDecorView);
    }

}
