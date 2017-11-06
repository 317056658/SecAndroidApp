package com.kkkcut.www.myapplicationkukai.serialDriverCommunication;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.kkkcut.www.myapplicationkukai.entity.Instruction;
import com.kkkcut.www.myapplicationkukai.entity.MicyocoEvent;
import com.kkkcut.www.myapplicationkukai.entity.PL2303DeviceEvent;
import com.kkkcut.www.myapplicationkukai.utils.EventBusUtils;
import com.kkkcut.www.myapplicationkukai.utils.Tools;
import com.kkkcut.www.myapplicationkukai.utils.logDocument.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;


/**
 * PL2303串口驱动
 */
public class ProlificSerialDriver {
    private static final int USB_READ_TIMEOUT_MILLIS = 5000;
    private static final int USB_WRITE_TIMEOUT_MILLIS = 3000;//设置发送数据超时时间为3秒
    private final Object mReadBufferLock = new Object();
    private final Object mWriteBufferLock = new Object();
    private byte[] buffer = new byte[4096];
    private ByteArrayOutputStream bufferOS = new ByteArrayOutputStream();
    private  Context context;
    private UsbEndpoint mReadEndpoint;
    private UsbEndpoint mWriteEndpoint;
    private UsbDeviceConnection connection;
    private  UsbDevice usbDevice;
    public boolean isEnd = false;//是否结束掉持续读取
    public int timeout = 0;//设置超时时间
    private final static String ACTION_DEVICE_PERMISSION="com.prolific.pl2303hxdsimpletest.USB_PERMISSION";
    private Handler mHandler;
    private static ProlificSerialDriver pl2303SerialDriver =null;
    private int  baudRate=19200;  //默认的波特率
    private UsbManager usbManager;
     static String TAG="ProlificSerialDriver";

    //私有的
    private ProlificSerialDriver(Context context, UsbDevice usbDevice) {
        this.context = context;
        this.usbDevice = usbDevice;
        openDevice();
    }


    public static  synchronized ProlificSerialDriver initInstance(Context context, UsbDevice usbDevice) {
        if(pl2303SerialDriver ==null){
            pl2303SerialDriver = new ProlificSerialDriver(context,usbDevice);
        }
        return pl2303SerialDriver;
    }
    //获得单例
    public static final  ProlificSerialDriver getInstance(){
        if(pl2303SerialDriver ==null){
            throw new NullPointerException("class ProlificSerialDriver must not null");
        }else {
            return pl2303SerialDriver;
        }
    }
    /**
     * 设置handler 接收消息
     * @param mHandler
     */
    public void  setHandler(Handler mHandler){
       this.mHandler=mHandler;
    }

    /**
     * 打开可以连接的设备
     */
    private void openDevice(){
        //申请权限
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_DEVICE_PERMISSION), 0);
        IntentFilter  permissionFilter=new IntentFilter(ACTION_DEVICE_PERMISSION);
        context.registerReceiver(usbPermissionReceiver,permissionFilter);  //注册广播
        usbManager.requestPermission(usbDevice, mPermissionIntent);  //显示对话框,询问用户允许打开权限
    }

    /**
     * usb权限许可广播
     */
    private  final BroadcastReceiver usbPermissionReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                 String  action   =intent.getAction();
            if(action.equals(ACTION_DEVICE_PERMISSION)){
                synchronized(this){
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                      if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                          if(device!=null){
                              //授权成功,在这里进行打开设备操作
                              UsbInterface usbInterface = usbDevice.getInterface(0);  //获得一个接口
                              for (int i = 0; i < usbInterface.getEndpointCount(); ++i) {
                                  UsbEndpoint currentEndpoint = usbInterface.getEndpoint(i);
                                  switch (currentEndpoint.getAddress()) {
                                      case 0x83:
                                          mReadEndpoint = currentEndpoint;
                                          break;
                                      case 0x02:
                                          mWriteEndpoint = currentEndpoint;
                                          break;
                                  }
                              }
                              //表示连接到该设备的连接，该设备在端点上传输数据。这个类允许你来回发送数据同步或异步。
                              connection = usbManager.openDevice(usbDevice);
                              //声称ubs接口
                              connection.claimInterface(usbInterface, true);
                              try {
                                  initPL2303Chip();
                                  ctrlOut(baudRate);
                                  EventBusUtils.post(new PL2303DeviceEvent(true));
                              } catch (IOException e) {
                                  e.printStackTrace();
                              }
                          }
                      }else {
                                EventBusUtils.post(new PL2303DeviceEvent(false));
                      }
                }
            }
        }
    };

    /**
     * 设置传输控制
     * @param baudRate
     * @throws IOException
     */
    private final void ctrlOut(int baudRate) throws IOException {
        byte[] lineRequestData = new byte[7];

        lineRequestData[0] = (byte) (baudRate & 0xff);
        lineRequestData[1] = (byte) ((baudRate >> 8) & 0xff);
        lineRequestData[2] = (byte) ((baudRate >> 16) & 0xff);
        lineRequestData[3] = (byte) ((baudRate >> 24) & 0xff);

        lineRequestData[4] = 0;
        lineRequestData[5] = 0;
        lineRequestData[6] = (byte) 8;
        outControlTransfer(UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_CLASS | 0x01, 0x20, 0, 0, lineRequestData);
    }

    /**
     * 初始化芯片
     *
     * @throws IOException
     */
    private void initPL2303Chip() throws IOException {
        int mDeviceType = getDeviceType();
        vendorIn(0x8484, 0, 1);
        vendorOut(0x0404, 0, null);

        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);
        vendorIn(0x8484, 0, 1);

        vendorOut(0x0404, 1, null);
        vendorIn(0x8484, 0, 1);
        vendorIn(0x8383, 0, 1);

        vendorOut(0, 1, null);
        vendorOut(1, 0, null);
        vendorOut(2, (mDeviceType == 0) ? 0x44 : 0x24, null);
    }

    /**
     * 获得设备类型
     */
    private int getDeviceType() {
        int mDeviceType = 0;
        try {
            if (usbDevice.getDeviceClass() == 0x02) {
                mDeviceType = 1;
            } else {
                Method getRawDescriptorsMethod = connection.getClass().getMethod("getRawDescriptors");
                byte[] rawDescriptors = (byte[]) getRawDescriptorsMethod.invoke(connection);
                byte maxPacketSize0 = rawDescriptors[7];
                if (maxPacketSize0 == 64) {
                    mDeviceType = 0;
                } else if ((usbDevice.getDeviceClass() == 0x00) || (usbDevice.getDeviceClass() == 0xff)) {
                    mDeviceType = 2;
                } else {
                    mDeviceType = 0;
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return mDeviceType;
    }

    /**
     * 传数据方法
     *
     * @param buf
     * @param wlength
     * @return
     */
    public int write(byte[] buf, int wlength) {
        int offset = 0;
        int actual_length = 0;
        byte[] write_buf = new byte[4096];
        synchronized (mWriteBufferLock) {
            System.arraycopy(buf, offset, write_buf, 0, wlength);
            actual_length = connection.bulkTransfer(mWriteEndpoint, write_buf, wlength, USB_WRITE_TIMEOUT_MILLIS);
            if (actual_length < 0) {
                Toast.makeText(context, "发送数据失败", Toast.LENGTH_SHORT).show();
                return -1;
            }
        }
        return actual_length;
    }

    /**
     * 读取返回数据方法
     *
     * @throws IOException
     */

    public void read() {
        synchronized (mReadBufferLock) {
            try {
                int sum = 0;//总共读了多少字节
                int number;//一次读取多少个字节
                while (!isEnd) {
                    number = connection.bulkTransfer(mReadEndpoint, buffer, buffer.length, timeout);
                    if (number > 0) {
                        sum += number;
                        if (timeout == 0) {
                            timeout = 200;//如果来数据了 就变为300ms
                        }
                        bufferOS.write(buffer, 0, number);
                    } else if (number == -1) {
                        timeout = 0;
                        Log.d("读到多少字节", "多少: " + sum);
                        sum = 0;
                        String s = new String(bufferOS.toByteArray());
                        bufferOS.reset();
                        String data = Tools.clearNewline(s);
                        Log.d("read", "读取的数据是好多？:" + data);
                        if (data.contains(Instruction.OPERATION_FINISH)) {//操作完成
                                 int  beginIndex =data.lastIndexOf("!");
                               String str  =data.substring(beginIndex,beginIndex+3);
                                if(str.equals(Instruction.OPERATION_FINISH)){
                                    mHandler.sendEmptyMessage(MicyocoEvent.OPERATION_FINISH);
                                }
                        }else  if(data.contains(Instruction.READ_TOOTH_PROCEED)){     //属于读齿进行中的返回数据
                            if(data.substring(0,3).equals(Instruction.READ_TOOTH_PROCEED)){
                                data=data.replaceAll("[!RB;]","");
                                Message  msg=mHandler.obtainMessage();
                                msg.obj=data;
                                msg.what=MicyocoEvent.READ_TOOTH_DATA_BACK;
                                mHandler.sendMessage(msg);
                            }
                        }else if(data.contains(Instruction.CUT_CONDUCT)) {   //切割进行中的返回数据
                            LogUtils.d("下位机返回的数据", data);
                            if (data.substring(0, 3).equals(Instruction.CUT_CONDUCT)) {
                                Message msg = mHandler.obtainMessage();
                                msg.obj = data;
                                msg.what=MicyocoEvent.CUT_DATA_BACK;
                                mHandler.sendMessage(msg);
                            }
                        }else if (data.equals(Instruction.CUT_KNIFE_SWITCHOVER_PROBE)) { //探针和切割刀的距离 切割刀换探针
                            mHandler.sendEmptyMessage(MicyocoEvent.CUT_KNIFE_SWITCHOVER_PROBE);
                        } else if (data.equals(Instruction.ExceptionError.eUserCancelled.toString())) {//操作取消
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.USER_CANCEL_OPERATE;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eXLimitOver.toString())){ //X轴超限
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.X_LIMIT_OVER;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eYLimitOver.toString())){//Y轴超限
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.Y_LIMIT_OVER;
                            mHandler.sendMessage(msg);
                        }else if(Instruction.ExceptionError.eZLimitOver.toString().equals(data)){//Z轴超限
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.Z_LIMIT_OVER;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eProbeError.toString())){  //探测错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.PROBE_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eCommandError.toString())){ //命令错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.COMMAND_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eNoTool)){//没有安装工具
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.NO_TOOL;
                            mHandler.sendMessage(msg);
                        }else if(Instruction.ExceptionError.eRS232Error.toString().equals(data)){ //232连接错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.RS232_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(Instruction.ExceptionError.eMaterialPosError.toString().equals(data)){//钥匙太薄或者是位置不正确
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.MATERIAL_OR_POSITION_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eToolError.toString())){//工具错误（工具太长或者太短）
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.TOOL_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eSystemReset)){  //系统重置
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.SYSTEM_RESET;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eCoverOpen.toString())){//安全门打开
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.SAFETYGATE_OPEN;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eUnsupportedProfile.toString())){//选择了不支持的钥匙类型
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.UNSUPPORTED_PROFILE;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eWrongStopper.toString())){   //选择了错误的档块位置
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.WRONG_STOPPER;
                            mHandler.sendMessage(msg);
                        }else if(Instruction.ExceptionError.eIncomingDataError.toString().equals(data)){ //数据传输丢失
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.INCOMING_DATA_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eXSensorError.toString())){//X轴传感器错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.X_SENSOR_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eYSensorError.toString())){//Y轴传感器错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.Y_SENSOR_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eZSensorError.toString())){ //Z轴传感器错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.Z_SENSOR_ERROR;
                            mHandler.sendMessage(msg);
                        }else if(data.equals(Instruction.ExceptionError.eClampNotCalibrated.toString())){//请设定夹具的原点
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.CLAMP_NOT_CALIBRATED;
                            mHandler.sendMessage(msg);
                        }else if(Instruction.ExceptionError.eVersionNotMatched.toString().equals(data)){  //版本不匹配
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.VERSION_NOT_MATCHED;
                            mHandler.sendMessage(msg);
                        }else if(Instruction.ExceptionError.eNoError.toString().equals(data)){   //没有错误
                            LogUtils.d("下位机返回的数据", data);
                            Message  msg=mHandler.obtainMessage();
                            msg.obj=data;
                            msg.what=MicyocoEvent.NO_ERROR;
                            mHandler.sendMessage(msg);
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHandler=null;
        context=null;
        if(context==null){
            Log.d("线程结束", "read: ");
        }
    }
    /**
     * 关闭资源
     */
    public void close() {
        context.unregisterReceiver(usbPermissionReceiver);  //移除广播
        try {
            if (connection == null) {
                return;
            }

            Log.d("关闭连接", "close: ");
            connection.releaseInterface(this.usbDevice.getInterface(0));
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 输出的传输
     *
     * @throws IOException
     */
    private final void outControlTransfer(int requestType, int request, int value, int index, byte[] data) throws IOException {
        int length = (data == null) ? 0 : data.length;
        int result = connection.controlTransfer(requestType, request, value, index, data, length, USB_WRITE_TIMEOUT_MILLIS);
        if (result != length) {
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", value, result));
        }
    }

    /**
     * 输入的传输
     *
     * @throws IOException
     */
    private final byte[] inControlTransfer(int requestType, int request, int value, int index, int length) throws IOException {
        byte[] buffer = new byte[length];
        int result = connection.controlTransfer(requestType, request, value, index, buffer, length, USB_READ_TIMEOUT_MILLIS);
        if (result != length) {
            throw new IOException(String.format("ControlTransfer with value 0x%x failed: %d", value, result));
        }
        return buffer;
    }
    /**
     * 写设备控制
     */
    private final byte[] vendorIn(int value, int index, int length) throws IOException {
        return inControlTransfer(UsbConstants.USB_DIR_IN | UsbConstants.USB_TYPE_VENDOR, 0x01, value, index, length);
    }

    /**
     * 读设备控制
     */
    private final void vendorOut(int value, int index, byte[] data) throws IOException {
        outControlTransfer(UsbConstants.USB_DIR_OUT | UsbConstants.USB_TYPE_VENDOR, 0x01, value, index, data);
    }

}
