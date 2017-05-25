package com.leoliu.anshare.ax_edp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Calendar;

public class MainActivity extends Activity {

    private final int msgKey1 = 1;                  // Handle 标识符
    private static boolean TimeFlag = true;         // 时间更新 标识符
    private TimeThread TimeT = new TimeThread();      // 开始时间更新线程
    //final Thread ConTrol_Thread = new Control(TimeT);
    static  String TextFilePath="";                 // 文件地址
    static  String Thread_Name="Time-Thread";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new MainWindow()).commit();
        }
        Thread_Start();
    }
    /**
     * 线程初始化
     */
    private void Thread_Start(){
        /*
         * 初始化 电子席卡的数值
		 * 用一个线程不断更新时间
		 */
        TimeFlag = true;
        TimeT.setName(Thread_Name);
        //ConTrol_Thread.setDaemon(true);
        TimeT.start();
        //ConTrol_Thread.start();
    }
    /**
     * 设置线程控制符
     */
    public void Set_Time_Flag(boolean Flag) {
        TimeFlag = Flag;
        System.out.println("-->收到的设置:"+TimeFlag+"");
        System.out.println("-->TimeT ID:"+TimeT.getId()+"\n-->TimeT 名称: "+TimeT.getName()+"\n-->线程状态:"+TimeT.getState());
        //System.out.println("-->ConTrol_Thread ID:"+ConTrol_Thread.getId()+"\n-->ConTrol_Thread 名称: "+ConTrol_Thread.getName()+"\n-->线程状态:"+ConTrol_Thread.getState());
        if(Flag && TimeT.getName().equals(Thread_Name)){
            TimeT.setRun();
        }
        else{
            TimeT.setStop();
        }
        /*
        if(Flag){
            // 重启线程
            TimeFlag = true;
            if(TimeT.isRun!=true)
            {
                TimeT.start();
                ConTrol_Thread.start();
            }
            else if(TimeT.getState()==Thread.State.NEW){
                System.out.println("-->要启动线程");
                TimeT.setRun();
                ConTrol_Thread.run();
            }
            System.out.println("-->当前线程:"+Thread.currentThread().getName()+" 状态:"+Thread.currentThread().getState());
        }*/
    }
    /**
     * 获取文件完整路径
     */
    public void Set_Text_File_Path(String FilePath){
        TextFilePath=FilePath;
        System.out.println("-->收到的文件地址:"+FilePath+"");
    }
    public  String Get_Text_File_Path(){
        System.out.println("-->回传的文件地址:"+TextFilePath+"");
        return TextFilePath;
    }

    /**
     * 退出关闭线程
     */
    protected void stop() {
        if (TimeFlag) {
            TimeFlag = false;
        }
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
        super.onPause();
        unregisterReceiver(mUsbReceiver);
    }

    /**
     * 销毁Activity时再次确认关闭线程
     */
    protected void onDestroy() {
        if (TimeFlag) {
            TimeFlag = false;
        }
        super.onDestroy();
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                Toast.makeText(context, "BroadcastReceiver in \n" + "ACTION_USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Toast.makeText(context, "BroadcastReceiver in \n" + "ACTION_USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /*
     * 时间更新
     */
    class TimeThread extends Thread {
        private boolean isRun=true;

        @Override
        public void run() {
            while (isRun) {
                try {
                    Thread.sleep(1000);// 一秒的时间间隔
                    System.out.println("-->Time_Flag: " + TimeFlag+"\n-->TimeT: "+TimeT.getName()+"\n-->TimeT ID: "+TimeT.getId()+"\n-->TimeT State: "+TimeT.getState());
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
     * 控制线程
     */
    /*
    class Control extends Thread {
        private TimeThread t;

        public Control(TimeThread t) {
            this.t = t;
        }

        public void run() {
            while (true) {
                if (TimeFlag) {
                    t.setRun();
                } else {
                    t.setStop();
                }
            }
        }
    }
    */

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
