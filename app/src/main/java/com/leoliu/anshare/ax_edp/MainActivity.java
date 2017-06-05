package com.leoliu.anshare.ax_edp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends Activity {

    private final int msgKey1 = 1;                  // Handle 标识符
    private static boolean TimeFlag = true;         // 时间更新 标识符
    private static boolean OTG_Flag = false;        // OTG 设备 标识符
    private TimeThread TimeT = new TimeThread();    // 开始时间更新线程
    static String TextFilePath = "";                // 文件地址
    static String Thread_Name = "Time-Thread";      // 线程名字
    private static final String ACTION_USB_PERMISSION = "com.Android.example.USB_PERMISSION"; // 权限字段

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new MainWindow()).commit();
        }
        Intent intent = getIntent();
        TextFilePath = intent.getStringExtra("FilePath");
        Start_OTG_IntentFilter();   // 监听 OTG USB 的插入事件
        Thread_Start();             // 开始 时间自动更新 线程
    }

    /**
     * 线程初始化
     */
    private void Thread_Start() {
        /*
         * 初始化 电子席卡的数值
		 * 用一个线程不断更新时间
		 */
        TimeFlag = true;
        TimeT.setName(Thread_Name);
        TimeT.start();
    }

    /**
     * 设置OTG监听事件
     */
    private void Start_OTG_IntentFilter() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        //otg插入 播出广播
        registerReceiver(mUsbReceiver, usbDeviceStateFilter);//这里我用的碎片
    }

    /**
     * 设置及获取 时间状态标识符
     */
    public void Set_Time_Flag(boolean Flag) {
        TimeFlag = Flag;
        System.out.println("-->收到的设置:" + TimeFlag + "");
        System.out.println("-->TimeT ID:" + TimeT.getId() + "\n-->TimeT 名称: " + TimeT.getName() + "\n-->线程状态:" + TimeT.getState());
        if (Flag && TimeT.getName().equals(Thread_Name)) {
            TimeT.setRun();
        } else {
            TimeT.setStop();
        }
    }

    public boolean Get_Time_Flag() {
        return TimeFlag;
    }

    /**
     * 获取 OTG 状态符
     */
    public boolean Get_OTG_Flag() {
        return OTG_Flag;
    }

    /**
     * 获取文件完整路径
     */
    public void Set_Text_File_Path(String FilePath) {
        TextFilePath = FilePath;
        System.out.println("-->收到的文件地址:" + FilePath + "");
    }

    public String Get_Text_File_Path() {
        System.out.println("-->回传的文件地址:" + TextFilePath + "");
        return TextFilePath;
    }

    /**
     * 退出关闭线程
     */
    protected void stop() {
        if (TimeFlag) {
            TimeFlag = false;
        }
        Set_Time_Flag(TimeFlag);
    }

    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        TimeFlag = true;
        Set_Time_Flag(TimeFlag);
        super.onResume();
        // USB  状态监测
        IntentFilter usbFilter = new IntentFilter();
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mUsbReceiver, usbFilter);
    }

    @Override
    protected void onPause() {
        if (TimeFlag) {
            TimeFlag = false;
        }
        Set_Time_Flag(TimeFlag);
        super.onPause();
        //unregisterReceiver(mUsbReceiver);
    }

    /**
     * 销毁Activity时再次确认关闭线程
     */
    protected void onDestroy() {
        if (TimeFlag) {
            TimeFlag = false;
        }
        Set_Time_Flag(TimeFlag);
        super.onDestroy();
    }

    /**
     * OTG USB 事件接收程序
     */
    //mUsbReceiver只是一个普通的广播，根据action，去分别处理对应的事件。
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION://接受到自定义广播
                    synchronized (this) {
                        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {  //允许权限申请
                            if (usbDevice != null) {
                                //Do something
                                Toast.makeText(context, "检测到USB设备" + usbDevice.toString() + "", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "用户未授权，读取失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_ATTACHED://接收到存储设备插入广播
                    UsbDevice device_add = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_add != null) {
                        OTG_Flag = true;
                        Toast.makeText(context, "接收到存储设备插入广播\nUSB 设备已插入", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UsbManager.ACTION_USB_DEVICE_DETACHED://接收到存储设备拔出广播
                    UsbDevice device_remove = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device_remove != null) {
                        OTG_Flag = false;
                        Toast.makeText(context, "接收到存储设备拔出广播\nUSB 设备已拔出", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            Set_Time_Flag(true);
        }
    };

    /*
     * 时间更新
     */
    class TimeThread extends Thread {
        private boolean isRun = true;

        @Override
        public void run() {
            while (isRun) {
                try {
                    Thread.sleep(1000);// 一秒的时间间隔
                    System.out.println("-->Time_Flag: " + TimeFlag + "\n-->TimeT: " + TimeT.getName() + "\n-->TimeT ID: " + TimeT.getId() + "\n-->TimeT State: " + TimeT.getState());
                    if (TimeFlag) {
                        Message msg = new Message();
                        msg.what = msgKey1;
                        mHandler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("-->Thread Error :" + e.getMessage());
                }
            }
        }

        /*
        控制线程
         */
        public void setRun() {
            this.isRun = true;
        }

        public void setStop() {
            this.isRun = false;
        }
    }

    /**
     * 读取时间，将时间反馈到界面
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msgKey1:
                    // 获取时间
                    long sysTime = System.currentTimeMillis();
                    CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime); // 获取时间
                    CharSequence sysDateStr = DateFormat.format("yyyy-MM-dd", sysTime); // 获取日期
                    // 解算时间
                    Calendar c = Calendar.getInstance();
                    int week = c.get(Calendar.DAY_OF_WEEK);
                    String[] weekname = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
                    try {

                        if (TimeFlag) { // 重要判断 防止退出时资源冲突
                            // 获取控件
                            TextView MainTime = (TextView) findViewById(R.id.MainTime);
                            TextView MainDate = (TextView) findViewById(R.id.MainDate);
                            // 更新 UI
                            MainTime.setText(sysTimeStr); // 设置时间
                            MainDate.setText(sysDateStr + "  " + weekname[week - 1]); // 设置日期
                        }
                    } catch (Exception ex) {
                        System.out.println("-->Handle Error :" + ex.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
