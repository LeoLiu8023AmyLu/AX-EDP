package com.leoliu.anshare.ax_edp;

/**
 * Created by Anshare_LY on 2017/5/18.
 */

import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainWindow extends Fragment {

    public MainWindow() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.mainwindow, container, false);
        //
        Button MainEnterBT = (Button) rootView.findViewById(R.id.button_MainEnter);
        MainEnterBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入下一个 Fragment
                MainActivity MA = (MainActivity) new MainActivity();
                MA.Set_Time_Flag(false);
                getFragmentManager().beginTransaction().replace(R.id.container, new FileManager()).commit();
            }
        });
        Button MainAutoSetBT = (Button) rootView.findViewById(R.id.button_MainAutoset);
        MainAutoSetBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                MainActivity MA = (MainActivity) new MainActivity();
                boolean Flag=MA.Get_Time_Flag();
                if(Flag){Flag=false;}
                else{Flag=true;}
                MA.Set_Time_Flag(Flag);
                Toast.makeText(getActivity(), "暂不支持此项功能，请等待开发完成", Toast.LENGTH_SHORT).show();
            }
        });
        //初始化 控件
        TextView MainTime = (TextView) rootView.findViewById(R.id.MainTime);
        TextView MainDate = (TextView) rootView.findViewById(R.id.MainDate);
        Calendar c = Calendar.getInstance();
        //获取时间，分别设置时间与日期
        long sysTime = System.currentTimeMillis();
        CharSequence sysTimeStr = DateFormat.format("HH:mm:ss", sysTime);
        CharSequence sysDateStr = DateFormat.format("yyyy-MM-dd", sysTime);
        //推算星期
        int week = c.get(Calendar.DAY_OF_WEEK);
        String[] weekname = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        //将计算得到的时间信息发送给UI
        MainTime.setText(sysTimeStr);
        MainDate.setText(sysDateStr + "  " + weekname[week - 1]);
        return rootView;
    }
}
