package com.kkkcut.www.myapplicationkukai.PublicClass.JPushReceiver;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.kkkcut.www.myapplicationkukai.Jpush.msgActivity;
import com.kkkcut.www.myapplicationkukai.MainActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.msgAdapter;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao1;
import com.kkkcut.www.myapplicationkukai.entity.JMessage;
import com.kkkcut.www.myapplicationkukai.entity.TempInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static android.content.ContentValues.TAG;
import static android.content.Context.NOTIFICATION_SERVICE;
import static java.lang.System.currentTimeMillis;

/**
 * Created by Administrator on 2017/1/3.
 */


public class MyReceiver extends BroadcastReceiver {
    private String title;  //标题
    String   introduce;    //大概介绍
    String   id;
    public static boolean boon;
    public static boolean boln;
    public void onReceive(Context context, Intent intent) {
        //获得传过来的消息内容
        Bundle bundle=intent.getExtras();
          //判断是不是自定义消息广播动作  是就接收
        if(intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)){
            //获得附加字段
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Log.d("aaa", "onReceive:+++++1 "+extras);
            String textMessage= bundle.getString(JPushInterface.EXTRA_MESSAGE);
            Log.d("aaa", "onReceive:+++++3 "+textMessage);
            try {
                JSONObject extrasJson = new JSONObject(extras);
                String myKey= extrasJson.optString("ID");
                int  key=Integer.parseInt(myKey);
                TempInfo.key=key;
                TempInfo.textMessage1=textMessage;
            } catch (Exception e) {
                e.printStackTrace();
            }
            String file = bundle.getString(JPushInterface.EXTRA_MSG_ID);



        }
        if(intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)){
            //获得通知标题
           title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
            Log.d(TAG, "onReceive:来了"+title);
            //获得通知内容介绍JPushInterface.EXTRA_ALERT
            introduce = bundle.getString(JPushInterface.EXTRA_ALERT);
              id      = bundle.getString(JPushInterface.EXTRA_MSG_ID);
            Log.d(TAG, "onReceive:来了"+introduce);
            String file = bundle.getString(JPushInterface.EXTRA_MSG_ID);

        }
        if(title!=null&&introduce!=null){
            Log.d("2", "onReceive: 成功！");
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss");
            long time=System.currentTimeMillis();
            String str = formatter.format(time); //获取当前时间
            Log.d(TAG, "onReceive: 来了111111"+title);
            Log.d(TAG, "onReceive:"+ TempInfo.key);
            Log.d(TAG, "onReceive: 来了333333"+TempInfo.textMessage1);
            Log.d(TAG, "onReceive: 来了444444"+introduce);
            SQLiteDao1 sql=new SQLiteDao1(context);
            sql.insertObject(id,title,introduce,0,TempInfo.key,TempInfo.textMessage1,str);
            title=null;
            introduce=null;
            if(TempInfo.start){
                SQLiteDao1 sqll=new SQLiteDao1(context);
                msgActivity.list  =sqll.queryAllsort();
                msgActivity.adapter=new msgAdapter(context,msgActivity.list);
                msgActivity.lv.setAdapter(msgActivity.adapter);
                }else  if(TempInfo.bool){
                MainActivity.msg.addMsg();
                Log.d(TAG, "onReceive: 动了");
            }
        }

        if(intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            //用户点击了通知栏 就打开一个新的Activity
            String file = bundle.getString(JPushInterface.EXTRA_MSG_ID);

            SQLiteDao1 sql=new SQLiteDao1(context);
               List<JMessage> list=sql.queryId(file);
            if(list.get(0).getType()==1){
                Intent intent1 = new Intent(context,TextActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent1.putExtra("content",list.get(0).getContent());
                intent1.putExtra("title",list.get(0).getTitle());
                context.startActivity(intent1);
            }else if(list.get(0).getType()==2){
                Intent intent1 = new Intent(context,MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent1);
            }



        }
    }
    //获得完整的消息
    private  String getMessage(String s){
        int right = s.indexOf("}")+1;
        Log.d("来了", "[MyReceiver] 接收到推送下来的通知::::"+s.substring(right));
        return s.substring(right);
    }
    //获得类型
    private String getType(String  s){
        // 获取第一个 '{' 下标
        int left = s.indexOf("{") + 1;// 为什么+1？因为下标是从0开始
        // 获取第一个 '}' 下标
        int right = s.indexOf("}");
        return s.substring(left, right);
    }
    @SuppressLint("NewApi")
    public void showNotification(Context context,Bundle bundle){

        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String title = bundle.getString(JPushInterface.EXTRA_TITLE);

        try {

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Intent intent = new Intent(context,TextActivity.class);


            PendingIntent contentIndent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(currentTimeMillis())
                    .setContentIntent(contentIndent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .build();

            notification.defaults = Notification.DEFAULT_ALL;
            //点击之后通知栏消失
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            manager.notify(1, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
