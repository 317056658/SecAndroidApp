package com.kkkcut.www.myapplicationkukai.utils;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/12/14.
 */

public class DatabaseFileUtils {
   private Context context;
    public DatabaseFileUtils(Context context) {
        this.context=context;
    }

    /**
     * 添加数据库
     */
    public  void addDatabases() {
        //添加钥匙数据库文件到databases下面
        addDatabaseFile("SEC1.db");
        //添加语言数据库文件到databases下面
        addDatabaseFile("LanguageFile.db");
    }

    /**
     *   把assets下面的数据库文件写到databases下面
     *  存储钥匙数据库
     * @return
     */
      public void addDatabaseFile(String database)
    {
        String pathFile = "data/data/com.kkkcut.www.myapplicationkukai/databases/"+database;//数据库存储路径；
        String pathMdkir= "data/data/com.kkkcut.www.myapplicationkukai/databases/";//数据库存储文件夹；
        File path = new File(pathFile);

        //判断文件是否已经生成，如果存在就return；
        if(path.exists())
        {
            return ;
        }
        else
        {
            File path1 = new File(pathMdkir);  //创建一个路径
            if(!path1.exists()){  //如果不在存就创建一个路径
               path1.mkdirs();
            }else {
            }
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(context.getAssets().open(database));
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
    }
}
