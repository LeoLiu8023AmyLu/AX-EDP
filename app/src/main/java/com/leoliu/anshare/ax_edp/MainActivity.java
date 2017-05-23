package com.leoliu.anshare.ax_edp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity {

    private final int msgKey1 = 1;      // Handle 标识符
    private boolean TimeFlag = false;    // 时间更新 标识符
    TimeThread TimeT = new TimeThread();   // 开始时间更新线程
    Thread ConTrol_Thread = new Control(TimeT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new MainWindow()).commit();
        }
        /*
         * 初始化 电子席卡的数值
		 * 用一个线程不断更新时间
		 */
        Set_Time_Flag(true);
        TimeT.setRun();
        ConTrol_Thread.setDaemon(true);
        TimeT.start();
        ConTrol_Thread.start();
    }

    /**
     * 设置线程控制符
     */
    public void Set_Time_Flag(boolean Flag) {
        this.TimeFlag = Flag;
        if (!Flag) {
            TimeT.setStop();
        } else {
            TimeT.setRun();
        }
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

    /*
     * 时间更新
     */
    class TimeThread extends Thread {
        private boolean isRun;

        @Override
        public void run() {
            while (isRun && TimeFlag) {
                System.out.println("Thread: " + TimeT.currentThread().getName() + " 开始运行");
                try {
                    Thread.sleep(1000);// 一秒的时间间隔
                    System.out.println("--> Time_Flag: " + TimeFlag);
                    if (TimeFlag) {
                        Message msg = new Message();
                        msg.what = msgKey1;
                        mHandler.sendMessage(msg);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread: " + TimeT.currentThread().getName() + " 结束");
            }
            System.out.println("Thread: " + TimeT.currentThread().getName() + " 运行中....");
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

    /*
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
                        System.out.println("Error :" + ex.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
