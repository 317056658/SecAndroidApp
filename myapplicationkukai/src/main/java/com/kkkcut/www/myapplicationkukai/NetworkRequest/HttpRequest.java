package com.kkkcut.www.myapplicationkukai.NetworkRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kkkcut.www.myapplicationkukai.entity.Version;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2016/11/21.
 */
public class HttpRequest {
    static   Context context1;
    public  static ProgressDialog  progressDialog;
   static FileOutputStream fileOutputStream;
   static   MyThread  my;

    //异步请求类
    public static void getJson(final String urlstring, final Context context) {
        context1=context;

        new AsyncTask(){
            //用于执行较为费时的操作，此方法将接收输入参数和返回计算结果
            protected Object doInBackground(Object[] params) {
                try {
                    URL url=new URL(urlstring);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if(code==200){
                        //获得字符串
                        InputStream is = conn.getInputStream();
                        byte [] b=new byte[1024*50];
                        StringBuffer sb=new StringBuffer();
                        int len=0;
                        while((len=is.read(b))>0){
                            String str=new String(b,0,len);
                            sb.append(str);
                        }
                        return sb.toString();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            @Override
            //当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上。
            protected void onPostExecute(Object o) {
                if(o==null){
                    Toast.makeText(context1,"连接服务器失败",Toast.LENGTH_SHORT).show();
                    return;
                }
                String json=(String)o;
                Log.d(TAG, "onPostExecute"+json);
                //把json字符串转为了对象
                Gson gson=new Gson();
                Version vs=gson.fromJson(json,Version.class);
                String Version=getVersion();
                  if(Version.equals(vs.getVer())){
                       handler.sendEmptyMessage(1);
                  }else if(!Version.equals(vs.getVer())){
                      Message  msg=new Message();
                       msg.obj=vs;
                        msg.what=2;
                       handler.sendMessage(msg);
                  }
            }
        }.execute();
    }
   private static Version vs;
    static InputStream is;
    private static Handler  handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    new AlertDialog.Builder(context1).setTitle("提示:").setMessage("您的版本已经是最新版本。").setPositiveButton("确定", null).show();
                    break;
                case 2:
                    vs = (Version) msg.obj;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context1);
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                    builder.setTitle("发现新版本：" + vs.getVer() + ",是否更新");
                    builder.setMessage(vs.getDesc());
                    builder.setCancelable(false);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                                downFile(vs.getUrl(),context1); //下载新版本方法（下载地址）    //在下面的代码段
                            } else {
                                Toast.makeText(context1, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.create().show();
                    break;
                case 3:
                    new AlertDialog.Builder(context1).setTitle("提示:").setMessage("连接服务器失败,请重试。").setPositiveButton("确定", null).show();
                    break;
            }
        }
    };
    //获得当前版本号
    private static String  getVersion(){
        PackageInfo packageInfo = null;
        try {
            PackageManager packageManager = context1.getPackageManager();
            packageInfo = packageManager.getPackageInfo(
                    context1.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return packageInfo.versionName;
    }
    static boolean   bln=false;
    //主线程中
    public static void downFile(final String url,Context context){
        context1=context;

        progressDialog = new ProgressDialog(context1);    //进度条，在下载的时候实时更新进度，提高用户友好度
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置为水平进度条
        progressDialog.setTitle("版本更新"); //标题
        progressDialog.setMessage("正在下载,请稍候...");//设置内容
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bln=true;
                    }

                });
        progressDialog.show();
          my=new MyThread(url);
          my.start();
    }
    //安装
    public static void down() {
        handler.post(new Runnable() {
            public void run() {
                progressDialog.cancel();
                update();
            }
        });
    }

    //安装文件，一般固定写法
    public static void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "Test.apk")), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context1.startActivity(intent);
    }
    static class MyThread extends Thread{
        String url=null;
        public  MyThread(String url){
            this.url=url;
        }
        @Override
        public void run() {
            OkHttpClient okHttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                                                      @Override
                                                      public void onFailure(Call call, IOException e) {
                                                          handler.sendEmptyMessage(3);
                                                      }

                                                      @Override
                                                      public void onResponse(Call call, Response response) throws IOException {

                                                          try {

                                                              int length = (int) response.body().contentLength();
                                                              progressDialog.setMax(length);                            //设置进度条的总长度
                                                              is = response.body().byteStream();  //获得输入流
                                                              if (is != null) {
                                                                  File file = new File(Environment.getExternalStorageDirectory(), "Test.apk");
                                                                  fileOutputStream = new FileOutputStream(file);
                                                                  byte[] buf = new byte[1024*2];   //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一 下就下载完了，看不出progressbar的效果。
                                                                  int ch = -1;
                                                                  int process = 0;
                                                                  while ((ch = is.read(buf)) != -1) {
                                                                      fileOutputStream.write(buf, 0, ch);
                                                                      process += ch;
                                                                      progressDialog.setProgress(process);       //这里就是关键的实时更新进度了！
                                                                      if(bln){
                                                                          Log.d(TAG, "onResponse: "+"终结循环");
                                                                          fileOutputStream.close();
                                                                          bln=false;
                                                                          return;
                                                                      }

                                                                  }
                                                              }
                                                              fileOutputStream.flush();
                                                              if (fileOutputStream != null) {
                                                                  fileOutputStream.close();
                                                              }
                                                              down();//安装
                                                          } catch (Exception e) {
                                                              e.printStackTrace();
                                                          }
                                                      }
                                                  }
            );
        }
    }
}
