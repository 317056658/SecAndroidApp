package com.kkkcut.www.myapplicationkukai.networkRequest;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.dialogActivity.UpdateAppActivity;
import com.kkkcut.www.myapplicationkukai.entity.UpdateAppInfo;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.qiangxi.checkupdatelibrary.dialog.UpdateDialog;

import okhttp3.Call;

/**
 * Created by Administrator on 2016/11/21.
 */
public class AppUpdate {
    //异步请求类
    public static void getUpdateInfo(final String strUrl, final Context context) {
        OkHttpUtil.okHttpGet(strUrl, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {    //失败
                  Toast.makeText(context,"连接服务器失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response) {    //响应
                Gson gson=new Gson();
                UpdateAppInfo updateAppInfo=gson.fromJson(response,UpdateAppInfo.class);
                //获得现在的版本号！！
                int versionCode =getVersionCode(context);
                //大于现在的版本号！！  就更新
                if(updateAppInfo.getVer()>versionCode){
                    switch (updateAppInfo.getState()){
                        case 0: //正常更新
                            UpdateAppActivity.startItselfActivity(context,updateAppInfo);
                            break;
                        case 1:   //强制更新
                            UpdateAppActivity.startItselfActivity(context,updateAppInfo);
                            break;                    }
                }else {
                    Toast.makeText(context," 已经是最新版本咯",Toast.LENGTH_SHORT).show();;
                }
            }
        });
    }
    //非强制更新对话框:
    public static  void UpdateDialog(Context context,UpdateAppInfo updateAppInfo){
            UpdateDialog dialog = new UpdateDialog(context);
//        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
//                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            dialog .setDownloadUrl(updateAppInfo.getUrl())
                    .setTitle("有更新啦")
                    .setReleaseTime("2017/12/20")
                    .setVersionName("30")
                    .setUpdateDesc(updateAppInfo.getDesc())
                    .setFileName("myapplicationkukai-v03.apk")
                    .setFilePath(Environment.getExternalStorageDirectory().getPath() + "/checkupdatelib")
                    //该方法需设为true,才会在通知栏显示下载进度,默认为false,即不显示
                    //该方法只会控制下载进度的展示,当下载完成或下载失败时展示的通知不受该方法影响
                    //即不管该方法是置为false还是true,当下载完成或下载失败时都会在通知栏展示一个通知
                    .setShowProgress(true)
                    .setIconResId(R.mipmap.ic_launcher)
                    .setAppName("SEC").show();
        Window window= dialog.getWindow();
        View mDecorView=window.getDecorView();
        Tools.hideBottomUIMenu(mDecorView);

    }
    /**
     * 获取当前应用版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
    /**
     * 获取当前应用版本名
     */
    public static String getVersionName(Activity activity) {
        try {
            PackageManager packageManager = activity.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(activity.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "未知版本号";
        }
    }


    //主线程中
    public static void downFile(final String url,Context context){

//
//        progressDialog = new ProgressDialog(context1);    //进度条，在下载的时候实时更新进度，提高用户友好度
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置为水平进度条
//        progressDialog.setTitle("版本更新"); //标题
//        progressDialog.setMessage("正在下载,请稍候...");//设置内容
//        progressDialog.setProgress(0);
//        progressDialog.setCancelable(false);
//        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        bln=true;
//                    }
//                });
//        progressDialog.show();
//          my=new MyThread(url);
//          my.start();
    }


}
