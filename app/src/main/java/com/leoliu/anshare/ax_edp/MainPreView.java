package com.leoliu.anshare.ax_edp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Anshare_LY on 2017/5/19.
 */

public class MainPreView extends Fragment {
    private int Text_Page = 1;
    private int Text_Page_Max;
    private int Page_Text_Num = 160;
    private float TextSize = 25;
    String Main_string="文件读取失败";

    public MainPreView() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_preview, container, false);
        // 关闭时间自动更新
        MainActivity MA = (MainActivity) new MainActivity();
        MA.Set_Time_Flag(false);
        //初始化 控件
        final TextView PreView_Text = (TextView) rootView.findViewById(R.id.MainPreView_Text);
        String Txt_File_Path = MA.Get_Text_File_Path();
        System.out.println("--> 文件目录:"+Txt_File_Path);
        if (Txt_File_Path == null) {
            // 开始文件读取
            InputStream inputStream = getResources().openRawResource(R.raw.readme);
            Main_string = TxtReader.getString(inputStream, "UTF-8");
        }else if(Txt_File_Path.equals("")){
            // 开始文件读取
            InputStream inputStream = getResources().openRawResource(R.raw.readme);
            Main_string = TxtReader.getString(inputStream, "UTF-8");
        }
        else {
            Toast.makeText(getActivity(), "文件: " + Txt_File_Path + " 已读取", Toast.LENGTH_SHORT).show();
            try {
                Main_string = TxtReader.getString(Txt_File_Path);
            }catch (Exception E){

                Toast.makeText(getActivity(), "请检查文件是否正确!", Toast.LENGTH_SHORT).show();
            }
        }
        //File TxtFile = new File("Leo.txt");
        //inputstreamtofile(inputStream, TxtFile);
        //final ProcessText PT = new ProcessText(TxtFile, 1);
        PreView_Text.setTextSize(TextSize);
        Text_Page_Max = (int) (Math.ceil(((int) Main_string.length()) / Page_Text_Num) + 1);
        if(Main_string.length()>Page_Text_Num) {
            PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Page_Text_Num * Text_Page));
        }
        else{
            PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Main_string.length()));
        }
        Toast.makeText(getActivity(), "长度: " + Main_string.length() + " 字\n每页字数: " + Page_Text_Num + " 字\n最大页面数: " + Text_Page_Max + " 页", Toast.LENGTH_LONG).show();

        /**
         * 页面控制部分
         * @ 功能：后退，翻页
         */
        //页面控制
        final SeekBar F_text_SeekBar = (SeekBar) rootView.findViewById(R.id.MainPreView_SeekBar);
        final TextView F_Page=(TextView) rootView.findViewById(R.id.MainPreView_Page);
        F_Page.setText(""+Text_Page+"");
        F_text_SeekBar.setMax(Text_Page_Max);
        F_text_SeekBar.setProgress(1);
        Button F_text_Up = (Button) rootView.findViewById(R.id.F_text_Up);
        F_text_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 向上翻页
                if (Text_Page > 1) {
                    Text_Page = Text_Page - 1;
                    PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Page_Text_Num * Text_Page));
                } else {
                    Text_Page = 1;
                    if(Main_string.length()>Page_Text_Num) {
                        PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Page_Text_Num * Text_Page));
                    }
                    else{
                        PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Main_string.length()));
                    }
                }
                F_text_SeekBar.setProgress(Text_Page);
                F_Page.setText(""+Text_Page+"");
                //Toast.makeText(getActivity(), "向上翻页 Page:" + Text_Page + "", Toast.LENGTH_SHORT).show();
            }
        });
        Button F_text_Down = (Button) rootView.findViewById(R.id.F_text_Down);
        F_text_Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 向下翻页
                if (Page_Text_Num * (Text_Page + 1) < Main_string.length()) {
                    Text_Page = Text_Page + 1;
                    PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Page_Text_Num * Text_Page));
                } else {
                    Text_Page = Text_Page_Max;
                    PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Main_string.length()));
                }
                F_text_SeekBar.setProgress(Text_Page);
                F_Page.setText(""+Text_Page+"");
                //Toast.makeText(getActivity(), "向下翻页 Page:" + Text_Page + "", Toast.LENGTH_SHORT).show();
            }
        });
        // 返回上一页面
        Button F_text_Back = (Button) rootView.findViewById(R.id.F_text_Back);
        F_text_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                getFragmentManager().beginTransaction().replace(R.id.container, new FileManager()).commit();
            }
        });
        F_text_SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(Text_Page!=F_text_SeekBar.getProgress()) {
                    Text_Page = F_text_SeekBar.getProgress();
                    if (Text_Page > 0) {
                        try {
                            if (Page_Text_Num * (Text_Page) < Main_string.length()) {
                                Text_Page = Text_Page + 1;
                                PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Page_Text_Num * Text_Page));
                            } else {
                                Text_Page = Text_Page_Max;
                                PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Main_string.length()));
                            }

                        } catch (Exception E) {
                            System.out.println("--> SeekBar" + E.toString());
                        }
                    } else {
                        Text_Page = 1;
                        if(Main_string.length()>Page_Text_Num) {
                            PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Page_Text_Num * Text_Page));
                        }
                        else{
                            PreView_Text.setText(Main_string.substring(Page_Text_Num * (Text_Page - 1), Main_string.length()));
                        }
                    }
                    F_Page.setText(""+Text_Page+"");
                }
                System.out.println("--> SeekBar 进度" + Text_Page + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getActivity(), "滚动条定位 Page:" + Text_Page + "", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}