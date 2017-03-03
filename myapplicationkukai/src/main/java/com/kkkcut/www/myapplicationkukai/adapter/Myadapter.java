package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.series;

import java.util.List;

/**
 * Created by Administrator on 2016/11/22.
 */

public class Myadapter extends BaseAdapter {
    Context context;
    List<series> list;
    public Myadapter(Context context, List<series> list) {
        this.context=context;
        this.list=list;

    }


    public int getCount() {
        return list.size();
    }


    public Object getItem(int i) {
        return list.get(i);
    }


    public long getItemId(int i) {
        return i;
    }


    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view==null){
            vh= new ViewHolder();
            //加载子视图
            view  =View.inflate(context, R.layout.activity_key_item_view,null);
            //给子视图的控件赋值
            vh.tv1= (TextView)view.findViewById(R.id.tv01);
            vh.tv2= (TextView)view.findViewById(R.id.tv02);
            vh.img=(ImageView)view.findViewById(R.id.img);
            view.setTag(vh);

        }else{
            vh=(ViewHolder)view.getTag();

        }
        //给子视图中的控件赋值
        series s=list.get(i);
        vh.tv1.setText(s.get_year_from()+"");
        vh.tv2.setText(s.get_code_series());
        vh.img.setImageResource(s.ge_img());
        return view;
    }

    class ViewHolder{
        TextView tv1,tv2;
        ImageView img;

    }
  }
