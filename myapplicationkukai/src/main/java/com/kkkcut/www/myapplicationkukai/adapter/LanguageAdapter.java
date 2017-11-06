package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.Multilingual;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/5/26.
 */

public class LanguageAdapter extends BaseAdapter  {
    private  Context context;
    private ArrayList<Multilingual> list;
    private int  checkedPosition ;  //被选中的位子

    public  LanguageAdapter(Context context,ArrayList<Multilingual> list){
        this.context=context;
        this.list=list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  vh;
          if(convertView==null){
              vh=new ViewHolder();
              //加载子视图
              convertView  =LayoutInflater.from(context).inflate(R.layout.language_list_item_view,parent,false);
              //  获得子视图控件的实例
             vh.show_language=(TextView)convertView.findViewById(R.id.tv);
              vh.checked=(CheckBox) convertView.findViewById(R.id.cb);
              convertView.setTag(vh);
          }else {
              vh=(ViewHolder)convertView.getTag();
          }
          //给子视图控件赋值
            Multilingual  multilingual=list.get(position);
           vh.show_language.setText(multilingual.getShowLanguage());
        if(list.get(position).isChecked()){
            vh.checked.setVisibility(View.VISIBLE);
            checkedPosition=position;  //保存被选中的位置
        }else {
            vh.checked.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    /**
     * 取消被选中的状态
     */
    public  void cancelByCheckedState(){
        list.get(checkedPosition).setChecked(false);
    }

    /**
     * 获得选中的语言表
     * @return
     */
    public  String getCheckedLanguageTable(){
        return list.get(checkedPosition).getTableName();
    }

    class ViewHolder{
        TextView show_language;   //  显示语言
        CheckBox checked;   //选中的

    }
}
