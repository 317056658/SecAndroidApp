package com.kkkcut.www.myapplicationkukai.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/14.
 */

public class DBManager {
    Context context;
    SQLiteDatabase db;
    public DBManager(Context context) {
        this.context=context;
    }
     public void open(){
         db=openDatabase();
         db.close();
     }

    public SQLiteDatabase openDatabase()
    {
        String pathFile = "data/data/com.kkkcut.www.myapplicationkukai/databases/SEC1.db";//数据库存储路径；
        String pathMdkir = "data/data/com.kkkcut.www.myapplicationkukai/databases";//数据库存储文件夹；
        File path = new File(pathFile);
        //判断文件是否已经生成，如果存在就直接返回数据；
        if(path.exists())
        {
            SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(path, null);
            return db;
        }
        else
        {
            File path1 = new File(pathMdkir);
            if(path1.mkdir())
            {
                System.out.println("创建成功");
            }
            else
            {
                System.out.println("创建失败");
            }
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(context.getAssets().open("SEC1.db"));
            bos = new BufferedOutputStream(new FileOutputStream(pathFile));
            int len;
            byte[] b = new byte[1024];
            while((len = bis.read(b)) != -1)
            {
                bos.write(b, 0, len);
                bos.flush();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally
        {
            if(bis != null)
            {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(bos != null)
            {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return openDatabase();
    }
}
