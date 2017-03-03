package com.kkkcut.www.myapplicationkukai.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kkkcut.www.myapplicationkukai.AutoType.frmKeyCut_Main;
import com.kkkcut.www.myapplicationkukai.R;
import com.kkkcut.www.myapplicationkukai.Search.frmKeySelect;
import com.kkkcut.www.myapplicationkukai.dao.SQLiteDao;
import com.kkkcut.www.myapplicationkukai.entity.keyBlankInfo;
import com.kkkcut.www.myapplicationkukai.entity.key_blank_mfg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/17.
 */

public class keyAdapter extends RecyclerView.Adapter<keyAdapter.ViewHolder>{
  public   List mlist=new ArrayList<>();
    String ModelName;
    public int data_loading;
    private Context context;
    public  void sendData(List list, String ModelName, Context context,int data_loading){
        this.data_loading=data_loading;
        this.context=context;
         this.mlist=list;
        this.ModelName=ModelName;
    }
    static  class ViewHolder extends RecyclerView.ViewHolder{
        TextView mfg_name;
        View   itemView;
        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView=itemView;
            mfg_name=(TextView)itemView.findViewById(R.id.mfg_name);
        }
    }
    String  firmName;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.keyblank_item,parent,false);
      final ViewHolder holder=new ViewHolder(view);
          holder.itemView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  int position=holder.getAdapterPosition();
                if(data_loading==0)
                {
                    data_loading=1;
                    frmKeySelect.btn_back.setVisibility(View.VISIBLE);
                  SQLiteDao sql=new SQLiteDao(context);
                  key_blank_mfg mg=(key_blank_mfg)mlist.get(position);
                    firmName =mg.get_Name();
                    //点击 获得钥匙坯的厂商名字
                    frmKeySelect.inputText.setText(firmName);
                    //释放内存
                     mlist.clear();
                  mlist=sql.queryBlank(ModelName,firmName);
                  Log.d("型号名：", "onClick: "+ModelName);
                  //重新绘制RecyclerView
                  frmKeySelect.adapter.notifyDataSetChanged();

                }else if(data_loading==1){
                    Intent intent=new Intent(context, frmKeyCut_Main.class);
                    keyBlankInfo blank  =(keyBlankInfo)mlist.get(position);

                    intent.putExtra("mfg_name",holder.mfg_name.getText().toString());
                    intent.putExtra("blank", blank);
                    context.startActivity(intent);
                }
              }
          });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(data_loading==0){
           // Log.d("name", "onBindViewHolder: "+((key_blank_mfg) mlist.get(position)).get_Name());

            holder.mfg_name.setText(((key_blank_mfg) mlist.get(position)).get_Name());

        }else if(data_loading==1){
                       if(mlist.size()==0){
                           return;
                       }
            keyBlankInfo  kb=(keyBlankInfo)mlist.get(position);
              String strnum="[";
              String[] group =kb.getSpace().split(";");
            for (int i=0;i<group .length;i++){
                strnum+=group [i].split(",").length+"-";
            }
               strnum=strnum.substring(0,strnum.length()-1);
            holder.mfg_name.setText( ((keyBlankInfo)mlist.get(position)).getModelName()+"("+((keyBlankInfo)mlist.get(position)).getIC_card()+")"+strnum+"]");
        }


    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
