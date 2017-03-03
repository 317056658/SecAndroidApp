package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.JMessage;

import java.util.List;

/**
 * Created by Administrator on 2017/1/12.
 */

public class msgAdapter extends BaseAdapter {
    Context context;
    List<JMessage>  list;
    public msgAdapter(Context context,List<JMessage>  list) {
        this.context=context;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       ViewHolder vh;
        if(view==null){
            vh=new ViewHolder();
            //加载子视图
            view  =View.inflate(context, R.layout.activity_msg_item,null);
            //给子视图的控件赋值
            vh.tv1= (TextView)view.findViewById(R.id.tv01);
            vh.tv2= (TextView)view.findViewById(R.id.tv02);
            vh.tv3=(TextView)view.findViewById(R.id.tv03);
            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();

        }
        //给子视图中的控件赋值
        JMessage jm =list.get(i);


        if(jm.getState()==0){
            vh.tv1.setText(jm.getTitle());
            vh.tv2.setText(jm.getIntroduce());
            vh.tv3.setText(jm.getTime());
        }else {
            vh.tv1.setText(jm.getTitle());
            vh.tv1.setTextColor(Color.parseColor("#000000"));
            vh.tv1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            vh.tv2.setText(jm.getIntroduce());
            vh.tv2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            vh.tv2.setTextColor(Color.parseColor("#000000"));
            vh.tv3.setText(jm.getTime());
            vh.tv3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            vh.tv3.setTextColor(Color.parseColor("#000000"));
        }
        return view;
    }

    class ViewHolder{
        TextView tv1,tv2,tv3;
        ImageView img;

    }




}
