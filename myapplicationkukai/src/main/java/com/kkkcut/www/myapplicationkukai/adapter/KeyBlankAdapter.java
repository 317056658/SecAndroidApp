package com.kkkcut.www.myapplicationkukai.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.KeyBlankMfg;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class KeyBlankAdapter extends RecyclerView.Adapter<KeyBlankAdapter.ViewHolder> implements View.OnClickListener{
    public   List list =new ArrayList<>();
    public int dataLoadingType;
    public  void setData(List list,int dataLoadingType){
        this.dataLoadingType = dataLoadingType;
         this.list =list;
    }
    private OnItemClickListener  mOnItemClickListener=null;

    public void setClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    /**
     * 本adapter  item点击事件
     * @param v
     */

    @Override
    public void onClick(View v) {
       if ( mOnItemClickListener != null) {

            //注意这里使用getTag方法获取position
            mOnItemClickListener.onClick(v,(int)v.getTag());
        }

    }


    public  interface OnItemClickListener {
        void onClick(View view, int position);
    }

    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView mTvShowTitle;
        View   itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            mTvShowTitle =(TextView)itemView.findViewById(R.id.mfg_name);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyblank_item,parent,false);
      final ViewHolder vh =new ViewHolder(view);
        //设置点击事件
        view.setOnClickListener(this);
        return vh;
    }

    /**
     * 设置数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);
        if(dataLoadingType ==0){
            holder.mTvShowTitle.setText(((KeyBlankMfg) list.get(position)).getName());
        }else if(dataLoadingType ==1){
                       if(list.size()==0){
                           return;
                       }
            holder.mTvShowTitle.setText(((KeyInfo) list.get(position)).getCombinationName());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
