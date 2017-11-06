package com.kkkcut.www.myapplicationkukai.adapter;

import android.animation.Animator;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.CollectCutHistory;

import java.util.List;
/**
 * 被装饰类要和装饰类继承自同一父类
 * Created by Administrator on 2016/11/22.
 */

public class MyDataAdapter extends BaseItemDraggableAdapter<CollectCutHistory,BaseViewHolder>{
    public MyDataAdapter(List<CollectCutHistory> dataSet) {
          super(R.layout.recycler_item__content,dataSet);
    }

    @Override
    protected void startAnim(Animator anim, int index) {
        super.startAnim(anim, index);
    }

    @Override
    protected void convert(BaseViewHolder helper, CollectCutHistory item) {
        helper.setText(R.id.tv_content_title,item.getStep());
    }


}
