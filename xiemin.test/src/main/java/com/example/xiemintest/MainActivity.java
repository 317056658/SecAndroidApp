package com.example.xiemintest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class MainActivity extends AppCompatActivity {
    private final static String HEX = "0123CDEF";
    private final static String TRANSFORMATION = "DES/CBC/PKCS5Padding";//DES是加密方式 CBC是工作模式 PKCS5Padding是填充模式
    private final static String IVPARAMETERSPEC = "01020304";////初始化向量参数，AES 为16bytes. DES 为8bytes.
    private final static String ALGORITHM = "DES";//DES是加密方式
    private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法
    private String encodeString;
    private   EditText  input;
    private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };
    String pwd;
    TextView tv1,tv2;
    String pwdss;
    DESPlus des;
    String password="";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String sss=  "dsadasab321311112313;";
      EditText et=  (EditText)findViewById(R.id.et_text);
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            et.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(et, false);
            } catch (Exception e) {
            }
            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(et, false);
            } catch (Exception e) {
            }
        }
        Log.d("字符串", "onCreate: "+sss.contains("32"));
    }
    protected void onStart() {
        super.onStart();
    }
    /**
     * 加密
     * @param datasource byte[]
     * @param password String
     * @return byte[]
     */
    public static String encrypt(byte[] datasource, String password) {
        try{
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
//创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
//Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
//用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
//现在，获取数据并加密
//正式执行加密操作
            return Base64.encodeToString(cipher.doFinal(datasource),Base64.DEFAULT);
        }catch(Throwable e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recreate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("执行了", "onDestroy: ");
    }

    /**
     * 解密
     * @param src byte[]
     * @param password String
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decrypt(byte[] src, String password) throws Exception {
// DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
// 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
// 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
// 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
// Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
// 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
// 真正开始解密操作
        return cipher.doFinal(src);
    }
}
//    public static String encryptDES(String encryptString, String encryptKey)
//            throws Exception {
//        IvParameterSpec zeroIv = new IvParameterSpec(iv);
//        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(),"DES");
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
//        byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
//      return BASE64.encode(encryptedData);
////        return Base64.encodeToString(encryptedData,Base64.URL_SAFE);
//    }




//    public static String byteArr2HexStr(byte[] data) throws Exception {
//        int iLen = data.length;
//        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
//        StringBuffer sb = new StringBuffer(iLen * 2);
//        for (int i = 0; i < iLen; i++) {
//            int intTmp = data[i];
//            // 把负数转换为正数
//            while (intTmp < 0) {
//                intTmp = intTmp + 256;
//            }
//            // 小于0F的数需要在前面补0
//            if (intTmp < 16) {
//                sb.append("0");
//            }
//            sb.append(Integer.toString(intTmp, 16));
//        }
//        return sb.toString();
//    }
//    public static String decryptDES(String decryptString, String decryptKey)
//            throws Exception {
//        byte[] byteMi = Base64.decode(decryptString, Base64.DEFAULT);
//        IvParameterSpec zeroIv = new IvParameterSpec(iv);
//        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
//        byte decryptedData[] = cipher.doFinal(byteMi);
//        return new String(decryptedData);
//    }
//}
