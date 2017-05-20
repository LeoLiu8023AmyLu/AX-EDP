package com.leoliu.anshare.ax_edp;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends Activity {

    private final int msgKey1 = 1;
    private Boolean TimeFlag = true;                // 时间更新 标识符

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
        new TimeThread().start();   // 开始时间更新线程
    }
    /**
     *  退出关闭线程
     */
    public void stop() {
        if (TimeFlag) {
            TimeFlag = false;
        }
    }
    @Override
    protected void onResume() {
        /**
         * 设置为横屏
         */
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (TimeFlag) {}
        else{
            TimeFlag = true;
        }
        super.onResume();
    }
    /**
     *  销毁Activity时再次确认关闭线程
     */
    protected void onDestroy() {
        if (TimeFlag) {
            TimeFlag = false;
        }
        super.onDestroy();
    };
    /*
     * 时间更新
     */
    public class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Thread.sleep(1000);// 一秒的时间间隔
                    Message msg = new Message();
                    msg.what = msgKey1;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (TimeFlag);
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
                    // 获取控件
                    TextView MainTime = (TextView) findViewById(R.id.MainTime);
                    TextView MainDate = (TextView) findViewById(R.id.MainDate);
                    // 解算时间
                    Calendar c = Calendar.getInstance();
                    int week = c.get(Calendar.DAY_OF_WEEK);
                    String[] weekname = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
                    // 更新 UI
                    if(TimeFlag) { // 重要判断 防止退出时资源冲突
                        MainTime.setText(sysTimeStr); // 设置时间
                        MainDate.setText(sysDateStr + "  " + weekname[week - 1]); // 设置日期
                    }
                    break;
                default:
                    break;
            }
        }
    };
}
