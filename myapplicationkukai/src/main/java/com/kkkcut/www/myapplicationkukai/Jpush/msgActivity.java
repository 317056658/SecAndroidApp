package com.kkkcut.www.myapplicationkukai.Jpush;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.PublicClass.JPushReceiver.TextActivity;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.adapter.msgAdapter;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao1;
import com.kkkcut.www.myapplicationkukai.entity.JMessage;
import com.kkkcut.www.myapplicationkukai.entity.TempInfo;

import java.util.List;

import static com.kkkcut.www.myapplicationkukai.MainActivity.msg;

public class msgActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static List<JMessage> list;
    public static ListView lv;
    public static msgAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    TextView tv_back;
    Handler hander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d("动了", "handleMessage:动了动了");
            //更新数据
            adapter=new msgAdapter(msgActivity.this,list);
            lv.setAdapter(adapter);
            //停止刷新
            swipeRefresh.setRefreshing(false);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        msg.conceal();
        TempInfo.start = true;
        lv = (ListView) findViewById(R.id.lv);
       tv_back=(TextView)findViewById(R.id.tv_back);
        tv_back.setOnClickListener(back);
       swipeRefresh=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
        //改变加载显示的颜色
        swipeRefresh.setColorSchemeColors(Color.RED, Color.RED);
        //设置初始时的大小
        swipeRefresh.setSize(SwipeRefreshLayout.LARGE);
        //设置监听
        swipeRefresh.setOnRefreshListener(this);
        //设置向下拉多少出现刷新
        swipeRefresh.setDistanceToTriggerSync(90);
        //设置刷新出现的位置
        swipeRefresh.setProgressViewEndTarget(false,100);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(msgActivity.this, list.get(position).getTime(), Toast.LENGTH_SHORT).show();
                SQLiteDao1 sql=new SQLiteDao1(msgActivity.this);
                sql.updateState(list.get(position).getId());
                //代表文本网络显示
                if(list.get(position).getType()==1)
                {
                    TempInfo.jump=true;
                    Intent intent=new Intent(msgActivity.this, TextActivity.class);
                    intent.putExtra("content",list.get(position).getContent());
                    intent.putExtra("title",list.get(position).getTitle());
                   startActivity(intent);

                }else if(list.get(position).getType()==0)//代表更新版本
                {

                }else if(list.get(position).getType()==2)//代表视频
                {

                }
            }
        });
        lv.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {

            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.setHeaderTitle("选择操作");
                contextMenu.add(0, 0, 0, "删除该条");
                contextMenu.add(0, 1, 0, "删除所有");
            }
        });

    }
    View.OnClickListener back=new View.OnClickListener(){

        @Override
        public void onClick(View view) {
             finish();
        }
    };



    protected void onStart() {
        super.onStart();
        SQLiteDao1 sql = new SQLiteDao1(this);
        boolean lean = sql.query();
        if (lean) {
            //查询全部
            list = sql.queryAll();
            adapter = new msgAdapter(this, list);
            lv.setAdapter(adapter);
        }

    }


    AdapterView.AdapterContextMenuInfo info;

    public boolean onContextItemSelected(MenuItem item) {
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //得到listview中选择的条目绑定的id
        switch (item.getItemId()) {
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("你要删除吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDao1 sqlDelete = new SQLiteDao1(msgActivity.this);
                        //根据时间删除
                        sqlDelete.Delete(list.get(info.position).getTime());
                        list.remove(info.position);
                        //刷新适配器
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
                return true;
            case 1:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setIcon(android.R.drawable.ic_dialog_info);
                builder1.setMessage("你要删除吗？");
                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDao1 sqlDelete1 = new SQLiteDao1(msgActivity.this);
                        sqlDelete1.DeleteAll();
                        //清空listView
                        lv.setAdapter(null);
                    }
                });
                builder1.setNegativeButton("取消", null);
                builder1.create().show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }



    protected void onDestroy() {
        super.onDestroy();
        Log.d("销毁", "onDestroy: 销毁");
        TempInfo.start = false;
        TempInfo.bool = true;
    }
    @Override
    public void onRefresh() {
        Log.d("aa", "onRefresh:动了 ");
        SQLiteDao1 sql = new SQLiteDao1(this);
        //查询数据库按时间排序 返回一个List<JMessage>  集合
        list = sql.queryAllsort();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                   // 刷新控件停留两秒后消失
                    Thread.sleep(2000);
                    hander.sendEmptyMessage(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
