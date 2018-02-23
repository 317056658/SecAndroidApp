package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.kkkcut.www.myapplicationkukai.entity.KeyBlankMfg;
import com.kkkcut.www.myapplicationkukai.entity.KeyInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class KeyBlankAdapter extends RecyclerView.Adapter<KeyBlankAdapter.ViewHolder> implements View.OnClickListener{
    public   List mList =new ArrayList<>();
    public int dataLoadingType;
    private Context mContext;
    private LinearLayout.LayoutParams layoutParams;//先准备好
    public  void setData(List list,int dataLoadingType){
        this.dataLoadingType = dataLoadingType;
         this.mList =list;
    }
    private OnItemClickListener  mOnItemClickListener=null;

    public void setClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }
    public   KeyBlankAdapter(){
        layoutParams=new LinearLayout.LayoutParams(144,64);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;  // 垂直居中
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
        LinearLayout llAddView;   //添加View
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            mTvShowTitle =(TextView)itemView.findViewById(R.id.mfg_name);
            llAddView=(LinearLayout)itemView.findViewById(R.id.ll_add_view);
        }
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyblank_item,parent,false);
        mContext =parent.getContext();
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
        Log.d("好多条数据？", "onBindViewHolder: "+position);
        holder.itemView.setTag(position);
        if(dataLoadingType ==0){    //钥匙胚查询
            holder.mTvShowTitle.setText(((KeyBlankMfg) mList.get(position)).getName());
            //判断是不是他的同类   有就删除
            if(holder.llAddView.getChildAt(1) instanceof Key){
                holder.llAddView.removeViewAt(1);
            }
        }else if(dataLoadingType ==1){  //插叙
                       if(mList.size()==0){
                           return;
                       }
                       KeyInfo ki=(KeyInfo)mList.get(position);
            holder.mTvShowTitle.setText(ki.getCombinationText());
            //判断是不是他的同类   有就删除
            if(holder.llAddView.getChildAt(1) instanceof Key){
                holder.llAddView.removeViewAt(1);
            }
            switch (ki.getType()) {
                case KeyInfo.UNILATERAL_KEY:  //单边钥
                    UnilateralKey uk = new UnilateralKey(mContext, ki);
                    uk.setLayoutParams(layoutParams);
                    uk.setDrawDepthPatternAndToothCode(false);
                    uk.setShowArrows(false);
                    uk.setDrawPatternSize(137, 60);
                    uk.setDrawToothWidth(6);
                    uk.customDrawSerrated();  //自定义
                    holder.llAddView.addView(uk, 1);
                    break;
                case KeyInfo.BILATERAL_KEY:  //双边钥匙
                    BilateralKey bk = new BilateralKey(mContext, ki);
                    bk.setLayoutParams(layoutParams);
                    bk.setDrawDepthPatternAndToothCode(false);
                    bk.setShowArrows(false);
                    bk.setDrawPatternSize(137, 58);
                    bk.setDrawToothWidth(6);
                    bk.customDrawSerrated();
                    holder.llAddView.addView(bk, 1);
                    break;
                case KeyInfo.MONORAIL_OUTER_GROOVE_KEY:
                    MonorailOuterGrooveKey mogk = new MonorailOuterGrooveKey(mContext, ki);
                    mogk.setLayoutParams(layoutParams);
                    mogk.setDrawDepthPatternAndToothCode(false);
                    mogk.setDrawPatternSize(137, 58);
                    mogk.setShowArrows(false);
                    mogk.customDrawSerrated();
                    holder.llAddView.addView(mogk, 1);
                    break;
                case KeyInfo.DUAL_PATH_INSIDE_GROOVE_KEY:   //双轨内槽钥匙
                    DualPathInsideGrooveKey dualPathInsideGrooveKey = new DualPathInsideGrooveKey(mContext, ki);
                    dualPathInsideGrooveKey.setLayoutParams(layoutParams);
                    dualPathInsideGrooveKey.setDrawDepthPatternAndToothCode(false);
                    dualPathInsideGrooveKey.setDrawPatternSize(137, 58);
                    dualPathInsideGrooveKey.setShowArrows(false);
                    dualPathInsideGrooveKey.customDrawSerrated();
                    holder.llAddView.addView(dualPathInsideGrooveKey, 1);
                    break;
                case KeyInfo.DUAL_PATH_OUTER_GROOVE_KEY:
                    DualPathOuterGrooveKey dualPathOuterGrooveKey = new DualPathOuterGrooveKey(mContext, ki);
                    dualPathOuterGrooveKey.setLayoutParams(layoutParams);
                    dualPathOuterGrooveKey.setDrawDepthPatternAndToothCode(false);
                    dualPathOuterGrooveKey.setDrawPatternSize(137, 58);
                    dualPathOuterGrooveKey.setShowArrows(false);
                    dualPathOuterGrooveKey.customDrawSerrated();
                    holder.llAddView.addView(dualPathOuterGrooveKey, 1);
                    break;
                case KeyInfo.ANGLE_KEY:
                    AngleKey angleKey = new AngleKey(mContext, ki);
                    angleKey.setLayoutParams(layoutParams);
                    angleKey.setShowArrows(false);
                    angleKey.setDrawDepthPatternAndToothCode(false);
                    angleKey.setDrawPatternSize(140, 58);
                    angleKey.customDrawSerrated();
                    holder.llAddView.addView(angleKey, 1);
                    break;
                case KeyInfo.CONCAVE_DOT_KEY:
//                    Log.d("getId", "onBindViewHolder: "+);
                    String[] newStr = ki.getRow_pos().split(";");
                    if (ki.getRowCount() == 1 && Integer.parseInt(newStr[0]) < 0) {
                        ConcaveDotKey concaveDotKey = new ConcaveDotKey(mContext, ki);
                        concaveDotKey.setLayoutParams(layoutParams);
                        concaveDotKey.setShowArrows(false);
                        concaveDotKey.setDrawToothCode(false);
                        concaveDotKey.setOnlyDrawSidePatternSize(60, 20);
                        concaveDotKey.setDrawBigCircleAndInnerCircleSize(4, 3);
                        concaveDotKey.customDrawCircleGroove();
                        holder.llAddView.addView(concaveDotKey, 1);
                    } else {
                        ConcaveDotKey concaveDotKey = new ConcaveDotKey(mContext, ki);
                        concaveDotKey.setLayoutParams(layoutParams);
                        concaveDotKey.setDrawPatternSize(137, 58);
                        concaveDotKey.setShowArrows(false);
                        concaveDotKey.setDrawToothCode(false);
                        concaveDotKey.setDrawBigCircleAndInnerCircleSize(4, 3);
                        concaveDotKey.customDrawCircleGroove();
                        holder.llAddView.addView(concaveDotKey, 1);
                    }
                    break;
                case KeyInfo.CYLINDER_KEY:
                    Log.d("进来了？", "onBindViewHolder: ");
                    CylinderKey cylinderKey = new CylinderKey(mContext, ki);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(62, 62);
                    lp.gravity = Gravity.CENTER_VERTICAL;
                    cylinderKey.setLayoutParams(lp);
                    cylinderKey.setDrawPatternSize(60, 60);
                    cylinderKey.setDrawToothCodeForCirclePattern(true, 5);
                    holder.llAddView.addView(cylinderKey, 1);
                    break;
                case KeyInfo.SIDE_TOOTH_KEY:
                    SideToothKey sideToothKey = new SideToothKey(mContext, ki);
                    sideToothKey.setLayoutParams(layoutParams);
                    sideToothKey.setDrawPatternSize(140, 58);
                    sideToothKey.setShowArrows(false);
                    sideToothKey.setDrawDepthPatternAndToothCode(false);
                    sideToothKey.customDrawSerrated();
                    holder.llAddView.addView(sideToothKey, 1);
                    break;
                case KeyInfo.MONORAIL_INSIDE_GROOVE_KEY:  //单轨内槽钥匙
                    MonorailInsideGrooveKey monorailInsideGrooveKey = new MonorailInsideGrooveKey(mContext, ki);
                    monorailInsideGrooveKey.setLayoutParams(layoutParams);
                    monorailInsideGrooveKey.setDrawPatternSize(140, 58);
                    monorailInsideGrooveKey.setDrawToothWidth(6);
                    monorailInsideGrooveKey.setShowArrows(false);
                    monorailInsideGrooveKey.setDrawDepthPatternAndToothCode(false);
                    monorailInsideGrooveKey.customDrawSerrated();
                    holder.llAddView.addView(monorailInsideGrooveKey, 1);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
