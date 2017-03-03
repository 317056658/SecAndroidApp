package com.kkkcut.www.myapplicationkukai.adapter;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.BluetoothManager.BltManager;
import com.kkkcut.www.myapplicationkukai.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 */

public class bluetoothadapter extends BaseAdapter {
    private Context context;
    private List<BluetoothDevice> list;

    public bluetoothadapter(Context context, List<BluetoothDevice> list) {
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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        if(view==null){
              vh=new ViewHolder();
            //加载子视图
            view=View.inflate(context, R.layout.bluetooth_list_item,null);
                vh.blt_name=(TextView)view.findViewById(R.id.blt_name);
                vh.blt_address=(TextView)view.findViewById(R.id.blt_address);
                vh.blt_type=(TextView)view.findViewById(R.id.blt_type);
                vh.blt_bond_state=(TextView)view.findViewById(R.id.blt_bond_state);

            view.setTag(vh);
        }else{
            vh=(ViewHolder)view.getTag();
        }

       //vh.tv1.setText("蓝牙设备的mac地址:"+device.getAddress());
         BluetoothDevice device=list.get(i);
         vh.blt_name.setText("蓝牙名称:"+device.getName());
        vh.blt_address.setText("蓝牙地址:" + device.getAddress());
        vh.blt_type.setText("蓝牙类型:" + device.getType());
        vh.blt_bond_state.setText("蓝牙状态:" + BltManager.getInstance().bltStatus(device.getBondState()));

        return view;
    }

    class ViewHolder{
        TextView blt_name, blt_address, blt_type, blt_bond_state;
    }
}
