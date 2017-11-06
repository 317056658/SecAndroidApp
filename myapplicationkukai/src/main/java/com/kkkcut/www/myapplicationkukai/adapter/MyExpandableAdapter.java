package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.entity.KeyBlank;

import java.util.List;

/**
 * Expandable的适配器
 * Created by Administrator on 2017/9/13.
 */

public class MyExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<KeyBlank> groupList;

    public MyExpandableAdapter(Context context,List<KeyBlank> groupList){
        this.context=context;
        this.groupList=groupList;
    }

    // 获取分组的个数
    @Override
    public int getGroupCount() {
        return groupList.size();
    }
    //  获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getModelNameList().size();
    }
    //        获取组元素对象
    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }
    //        获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int groupPosition, int childPosition) {
                return groupList.get(groupPosition).getModelNameList().get(childPosition);
    }
    //        获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    //        获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
    //        分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }
    //加载并显示组元素的视图
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.mTvName = (TextView) convertView.findViewById(R.id.tv_group_name);
            groupViewHolder.mIvIndicator=  (ImageView) convertView.findViewById(R.id.iv_indicator);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        if(isExpanded){
            groupViewHolder.mIvIndicator.setImageResource(R.drawable.ic_expand_more);
        }else {
            groupViewHolder.mIvIndicator.setImageResource(R.drawable.ic_expand_less);
        }
        groupViewHolder.mTvName.setText(groupList.get(groupPosition).getMfgName());
        return convertView;
    }
    //        获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
          if(convertView==null){
              convertView = LayoutInflater.from(context).inflate(R.layout.expandable_child, parent, false);
              childViewHolder=new ChildViewHolder();
              childViewHolder.mTvName=(TextView) convertView.findViewById(R.id.tv_child_name);
              convertView.setTag(childViewHolder);
          }else {
              childViewHolder=(ChildViewHolder)convertView.getTag();
          }

        childViewHolder.mTvName.setText(groupList.get(groupPosition).getModelNameList().get(childPosition));
        return convertView;
    }
    //        指定位置上的子元素是否可选中 才会把分割线显示出来
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    static class GroupViewHolder {
        TextView mTvName;
        ImageView mIvIndicator;
    }
    static class ChildViewHolder {
        TextView mTvName;
    }
}
