package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.Key;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.UnilateralKey;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Mfg;
import com.kkkcut.www.myapplicationkukai.entity.Model;
import com.kkkcut.www.myapplicationkukai.keyDataSelect.KeyCategorySelectActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List mList = null;

    private Context mContext;

    private int mDataLoadingType;
    private Key mKey;

    public SortGroupMemberAdapter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 设置数据源
     * @param list
     * @param dataLoadingType
     */
    public void setDataSource(List list, int dataLoadingType){
        this.mList = list;
        this.mDataLoadingType =dataLoadingType;
    }



    public int getCount() {
        return this.mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        Log.d("跑一次", "getView: ");
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_group_menber_item, null);
            viewHolder.mParentContainer =(LinearLayout)view.findViewById(R.id.ll_container);  //父容器
            viewHolder.mTvText = (TextView) view.findViewById(R.id.tv_text);
            viewHolder.mTvLetter = (TextView) view.findViewById(R.id.tv_catalog);
            viewHolder.mTvCodeSeries =(TextView)view.findViewById(R.id.tv_code_series);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if(mDataLoadingType == KeyCategorySelectActivity.KEY_DATA_LOAD_TYPE_MFG){  //品牌
           Mfg  mfg   =(Mfg) mList.get(position);
            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                viewHolder.mTvLetter.setVisibility(View.VISIBLE);
                viewHolder.mTvLetter.setText(mfg.getSortLetters());
            } else {
                viewHolder.mTvLetter.setVisibility(View.GONE);
            }
            viewHolder.mTvText.setText(mfg.getName());
        }else if(mDataLoadingType ==KeyCategorySelectActivity.KEY_DATA_LOAD_TYPE_MODEL){  //型号
            Model  model   =(Model) mList.get(position);
            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                viewHolder.mTvLetter.setVisibility(View.VISIBLE);
                viewHolder.mTvLetter.setText(model.getSortLetters());
            } else {
                viewHolder.mTvLetter.setVisibility(View.GONE);
            }
            viewHolder.mTvText.setText(model.getName());
            if(viewHolder.mParentContainer.getChildAt(1) instanceof Key){   //判断是不是key的子类
                viewHolder.mParentContainer.removeViewAt(1);
            }
        }else{   //钥匙信息
           KeyInfo ki =(KeyInfo) mList.get(position);
            // 根据position获取分类的首字母的Char ascii值
            int section = getSectionForPosition(position);
            if (position == getPositionForSection(section)) {
                viewHolder.mTvLetter.setVisibility(View.VISIBLE);
                viewHolder.mTvLetter.setText(ki.getSortLetters());
            } else {
                viewHolder.mTvLetter.setVisibility(View.GONE);
            }
           switch (ki.getType()){
               case KeyInfo.UNILATERAL_KEY:  //单边钥
                   Log.d("进来几次？", "getView: ");
                   UnilateralKey uk=new UnilateralKey(mContext,ki);
                   LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(140,62);
                   layoutParams.gravity= Gravity.CENTER_VERTICAL;
                   uk.setLayoutParams(layoutParams);
                   uk.setBackgroundResource(R.drawable.edit_shape);
                   uk.setShowDrawDepthAndDepthName(false);
                   uk.setShowArrows(false);
                   uk.setDrawPatternSize(137,60);
                   uk.setDrawToothWidth(6);
                   uk.customDrawSerrated();  //自定义
                   viewHolder.mParentContainer.addView(uk,1);
                   break;
               case 2:

                   break;
               case 3:
                   break;
           }
            viewHolder.mTvText.setText(ki.getCombinationName());

        }

        return view;
    }

   static class ViewHolder {
        TextView mTvLetter;
        LinearLayout mParentContainer;
        TextView mTvText;
        TextView mTvCodeSeries;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        if(mDataLoadingType ==KeyCategorySelectActivity.KEY_DATA_LOAD_TYPE_MFG){
            return ((Mfg) mList.get(position)).getSortLetters().charAt(0);
        }else if(mDataLoadingType ==KeyCategorySelectActivity.KEY_DATA_LOAD_TYPE_MODEL){
            return ((Model) mList.get(position)).getSortLetters().charAt(0);
        }else {
             return ((KeyInfo) mList.get(position)).getSortLetters().charAt(0);
        }
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        if(mDataLoadingType ==KeyCategorySelectActivity.KEY_DATA_LOAD_TYPE_MFG){
            for (int i = 0; i < getCount(); i++) {
                Mfg mfg = (Mfg) mList.get(i);
                char firstChar = mfg.getSortLetters().toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }else if(mDataLoadingType ==KeyCategorySelectActivity.KEY_DATA_LOAD_TYPE_MODEL){
            for (int i = 0; i < getCount(); i++) {
                Model model= (Model) mList.get(i);
                char firstChar = model.getSortLetters().toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }else {
            for (int i = 0; i < getCount(); i++) {
                KeyInfo ki= (KeyInfo) mList.get(i);
                char firstChar = ki.getSortLetters().toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * 提取英文的首字母，非英文字母用#代替。
     *
     * @param str
     * @return
     */
    private String getAlpha(String str) {
        String sortStr = str.trim().substring(0, 1).toUpperCase();
        // 正则表达式，判断首字母是否是英文字母
        if (sortStr.matches("[A-Z]")) {
            return sortStr;
        } else {
            return "#";
        }
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
