package com.leoliu.anshare.ax_edp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Anshare_LY on 2017/5/19.
 */

public class MainPreView extends Fragment {
    private int Text_Page=1;
    private int Page_Text_Num=180;
    public MainPreView(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.main_preview, container, false);
        //初始化 控件
        final TextView PreView_Text= (TextView) rootView.findViewById(R.id.MainPreView_Text);
        InputStream inputStream = getResources().openRawResource(R.raw.a);
        final String Main_string;
        Main_string = TxtReader.getString(inputStream);
        File TxtFile=new File("Leo.txt");
        inputstreamtofile(inputStream,TxtFile);
        final ProcessText PT=new ProcessText(TxtFile,1);
        PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Page_Text_Num*Text_Page));
        Toast.makeText(getActivity(), "长度:"+Main_string.length(),Toast.LENGTH_LONG).show();

        /**
         * 页面控制部分
         * @ 功能：后退，翻页
         */
        //页面控制
        Button F_text_Up=(Button)rootView.findViewById(R.id.F_text_Up);
        F_text_Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 向上翻页
                if(Text_Page>1) {
                    Text_Page = Text_Page - 1;
                    PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Page_Text_Num*Text_Page));
                }
                else{
                    Text_Page=1;
                    PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Page_Text_Num*Text_Page));
                }
                Toast.makeText(getActivity(), "向上翻页 Page:"+Text_Page+"",Toast.LENGTH_SHORT).show();
            }
        });
        Button F_text_Down=(Button)rootView.findViewById(R.id.F_text_Down);
        F_text_Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 向下翻页
                if(Page_Text_Num*(Text_Page+1)<Main_string.length()) {
                    Text_Page = Text_Page + 1;
                    PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Page_Text_Num*Text_Page));
                }
                else{
                    Text_Page = (int) (Math.ceil(((int) Main_string.length())/Page_Text_Num)+1);
                    PreView_Text.setText(Main_string.substring(Page_Text_Num*(Text_Page-1),Main_string.length()));
                }
                Toast.makeText(getActivity(), "向下翻页 Page:"+Text_Page+"",Toast.LENGTH_SHORT).show();
            }
        });
        //预览显示
        Button F_text_Back=(Button)rootView.findViewById(R.id.F_text_Back);
        F_text_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
                getFragmentManager().beginTransaction().remove(new MainPreView()).commit();
                getFragmentManager().beginTransaction().replace(R.id.container, new MainWindow()).commit();
            }
        });
        return rootView;
    }
    public static void inputstreamtofile(InputStream ins,File file) {
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