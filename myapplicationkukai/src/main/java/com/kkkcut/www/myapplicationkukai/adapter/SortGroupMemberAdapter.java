package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.AngleKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.BilateralKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.ConcaveDotKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.CylinderKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.DualPathInsideGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.DualPathOuterGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.Key;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.MonorailInsideGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.MonorailOuterGrooveKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.SideToothKey;
import com.kkkcut.www.myapplicationkukai.drawKeyImg.UnilateralKey;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;
import com.kkkcut.www.myapplicationkukai.entity.Mfg;
import com.kkkcut.www.myapplicationkukai.entity.Model;
import com.kkkcut.www.myapplicationkukai.activity.KeyCategorySelectActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
    private List mList = null;
    private Context mContext;
    private int mDataLoadingType;
    private LinearLayout.LayoutParams layoutParams;//先准备好
    private HashMap<String,String> languageMap;

    public SortGroupMemberAdapter(Context mContext) {
        layoutParams=new LinearLayout.LayoutParams(144,64);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;  // 垂直居中
        this.mContext=mContext;
    }

    /**
     * 设置数据源
     */
    public void setDataSource(List list, int dataLoadingType){
        this.mList = list;
        this.mDataLoadingType =dataLoadingType;
    }



    public int getCount() {
        return mList.size();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.list_view_group_menber_item, null);
            viewHolder.mParentContainer =(LinearLayout)view.findViewById(R.id.ll_container);  //父容器
            viewHolder.mTvText = (TextView) view.findViewById(R.id.tv_title);
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
            viewHolder.mTvText.setText(mfg.getShowName());
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
            viewHolder.mTvText.setText(model.getShowName());
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
            //判断是不是他的同类   有就删除
            if(viewHolder.mParentContainer.getChildAt(1) instanceof Key){
                viewHolder.mParentContainer.removeViewAt(1);
            }
                switch (ki.getType()){
                    case KeyInfo.UNILATERAL_KEY:  //单边钥
                        UnilateralKey uk=new UnilateralKey(mContext,ki);
                        uk.setLayoutParams(layoutParams);
                        uk.setDrawDepthPatternAndToothCode(false);
                        uk.setShowArrows(false);
                        uk.setDrawPatternSize(137,60);
                        uk.setDrawToothWidth(6);
                        uk.customDrawSerrated();  //自定义
                        viewHolder.mParentContainer.addView(uk,1);
                        break;
                    case KeyInfo.BILATERAL_KEY:  //双边钥匙
                        BilateralKey bk=new BilateralKey(mContext,ki);
                        bk.setLayoutParams(layoutParams);
                        bk.setDrawDepthPatternAndToothCode(false);
                        bk.setShowArrows(false);
                        bk.setDrawPatternSize(137,58);
                        bk.setDrawToothWidth(6);
                        bk.customDrawSerrated();
                        viewHolder.mParentContainer.addView(bk,1);
                        break;
                    case KeyInfo.MONORAIL_OUTER_GROOVE_KEY:
                        MonorailOuterGrooveKey mogk=new MonorailOuterGrooveKey(mContext,ki);
                        mogk.setLayoutParams(layoutParams);
                        mogk.setDrawDepthPatternAndToothCode(false);
                        mogk.setDrawPatternSize(137,58);
                        mogk.setShowArrows(false);
                        mogk.customDrawSerrated();
                        viewHolder.mParentContainer.addView(mogk,1);
                        break;
                    case KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY:   //双轨内槽钥匙
                        DualPathInsideGrooveKey dualPathInsideGrooveKey=new DualPathInsideGrooveKey(mContext,ki);
                        dualPathInsideGrooveKey.setLayoutParams(layoutParams);
                        dualPathInsideGrooveKey.setDrawDepthPatternAndToothCode(false);
                        dualPathInsideGrooveKey.setDrawPatternSize(137,58);
                        dualPathInsideGrooveKey.setShowArrows(false);
                        dualPathInsideGrooveKey.customDrawSerrated();
                        viewHolder.mParentContainer.addView(dualPathInsideGrooveKey,1);
                        break;
                    case KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY:
                        DualPathOuterGrooveKey dualPathOuterGrooveKey=new DualPathOuterGrooveKey(mContext,ki);
                        dualPathOuterGrooveKey.setLayoutParams(layoutParams);
                        dualPathOuterGrooveKey.setDrawDepthPatternAndToothCode(false);
                        dualPathOuterGrooveKey.setDrawPatternSize(137,58);
                        dualPathOuterGrooveKey.setShowArrows(false);
                        dualPathOuterGrooveKey.customDrawSerrated();
                        viewHolder.mParentContainer.addView(dualPathOuterGrooveKey,1);
                        break;
                    case KeyInfo.ANGLE_KEY:
                        AngleKey angleKey=new AngleKey(mContext,ki);
                        angleKey.setLayoutParams(layoutParams);
                        angleKey.setShowArrows(false);
                        angleKey.setDrawDepthPatternAndToothCode(false);
                        angleKey.setDrawPatternSize(140,58);
                        angleKey.customDrawSerrated();
                        viewHolder.mParentContainer.addView(angleKey,1);
                        break;
                    case KeyInfo.CONCAVE_DOT_KEY:
                        String[] newStr=ki.getRow_pos().split(";");
                        if(ki.getRowCount()==1&&Integer.parseInt(newStr[0])<0){
                            ConcaveDotKey concaveDotKey = new ConcaveDotKey(mContext,ki);
                            concaveDotKey.setLayoutParams(layoutParams);
                            concaveDotKey.setShowArrows(false);
                            concaveDotKey.setDrawToothCode(false);
                            concaveDotKey.setOnlyDrawSidePatternSize(60,20);
                            concaveDotKey.setDrawBigCircleAndInnerCircleSize(4,3);
                            concaveDotKey.customDrawCircleGroove();
                            viewHolder.mParentContainer.addView(concaveDotKey,1);
                        }else {
                            ConcaveDotKey concaveDotKey = new ConcaveDotKey(mContext,ki);
                            concaveDotKey.setLayoutParams(layoutParams);
                            concaveDotKey.setDrawPatternSize(137,58);
                            concaveDotKey.setShowArrows(false);
                            concaveDotKey.setDrawToothCode(false);
                            concaveDotKey.setDrawBigCircleAndInnerCircleSize(4,3);
                            concaveDotKey.customDrawCircleGroove();
                            viewHolder.mParentContainer.addView(concaveDotKey,1);
                        }
                        break;
                    case KeyInfo.CYLINDER_KEY:
                        CylinderKey cylinderKey=new CylinderKey(mContext,ki);
                        LinearLayout.LayoutParams lp =new LinearLayout.LayoutParams(62,62);
                        lp.gravity=Gravity.CENTER_VERTICAL;
                        cylinderKey.setLayoutParams(lp);
                        cylinderKey.setDrawPatternSize(60,60);
                        cylinderKey.setDrawToothCodeForCirclePattern(true,5);
                        viewHolder.mParentContainer.addView(cylinderKey,1);
                        break;
                    case KeyInfo.SIDE_TOOTH_KEY:
                        SideToothKey  sideToothKey=new SideToothKey(mContext,ki);
                        sideToothKey.setLayoutParams(layoutParams);
                        sideToothKey.setDrawPatternSize(140,58);
                        sideToothKey.setShowArrows(false);
                        sideToothKey.setDrawDepthPatternAndToothCode(false);
                        sideToothKey.customDrawSerrated();
                        viewHolder.mParentContainer.addView(sideToothKey,1);
                        break;
                    case KeyInfo.MONORAIL_INSIDE_GROOVE_KEY:  //单轨内槽钥匙
                            MonorailInsideGrooveKey monorailInsideGrooveKey=new MonorailInsideGrooveKey(mContext,ki);
                            monorailInsideGrooveKey.setLayoutParams(layoutParams);
                            monorailInsideGrooveKey.setDrawPatternSize(140,58);
                            monorailInsideGrooveKey.setDrawToothWidth(6);
                            monorailInsideGrooveKey.setShowArrows(false);
                            monorailInsideGrooveKey.setDrawDepthPatternAndToothCode(false);
                             monorailInsideGrooveKey.customDrawSerrated();
                            viewHolder.mParentContainer.addView(monorailInsideGrooveKey,1);
                        break;
                }
               viewHolder.mTvCodeSeries.setText(ki.getCodeSeries());
                viewHolder.mTvText.setText(ki.getCombinationText());
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
                Mfg mfg = (Mfg)mList.get(i);
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
