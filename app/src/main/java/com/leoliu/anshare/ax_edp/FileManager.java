package com.leoliu.anshare.ax_edp;

/**
 * Created by Anshare_LY on 2017/5/23.
 */

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class FileManager extends ListFragment {

    public static final String COLUMN_NAME_NAME = "name";   // 列名称
    private static boolean OTG_Flag = false;                // USB 设备可用标识符
    private SimpleAdapter adapter = null;                   // 适配器
    private List<Map<String, Object>> itemList;             // 列表文件
    private Stack<String> pathHistory = null;               // 历史路径
    private String curPath;                                 // 当前路径
    String[] from = {COLUMN_NAME_NAME};                     // 从列中读取
    int[] to = {R.id.FileTextView};                         // 列表显示文件
    private String TextFilePath;                            // 文件地址

    public FileManager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.file_manager, container, false);
        /**
         * 页面控制部分
         * @ 功能：后退，翻页
         */
        //页面控制
        Button File_Button_U = (Button) rootView.findViewById(R.id.File_Button_U);
        File_Button_U.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示 U 盘路径
                MainActivity MA = new MainActivity();
                OTG_Flag = MA.Get_OTG_Flag();
                if (OTG_Flag) {
                    Toast.makeText(getActivity(), "已检测到设备", Toast.LENGTH_SHORT).show();
                    // 接下来需要使用另一个 Fragment 来完成 USB 的读取工作
                    goToIntent(rootView);
                } else {
                    Toast.makeText(getActivity(), "未检测到设备", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button File_Button_next = (Button) rootView.findViewById(R.id.File_Button_next);
        File_Button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new MainPreView()).commit();
            }
        });
        // 返回上一页面
        Button File_Button_back = (Button) rootView.findViewById(R.id.File_Button_back);
        File_Button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity MA = new MainActivity();
                MA.Set_Time_Flag(true);
                getFragmentManager().beginTransaction().replace(R.id.container, new MainWindow()).commit();
            }
        });
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pathHistory = new Stack<String>();
        String sDStateString = Environment.getExternalStorageState();
        if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
            File SDFile = Environment.getExternalStorageDirectory();
            curPath = SDFile.getAbsolutePath() + "/documents/";
            itemList = getData(curPath);
            adapter = new SimpleAdapter(getActivity(), itemList, R.layout.list_item, from, to);
            setListAdapter(adapter);
        }
    }

    /**
     * 列表更新
     *
     * @param path
     * @return list
     */
    private List<Map<String, Object>> getData(String path) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(COLUMN_NAME_NAME, "..");
        list.add(map);

        File file = new File(path);
        if (file.listFiles().length > 0) {
            for (File f : file.listFiles()) {
                map = new HashMap<String, Object>();
                String name = f.getName();
                if (f.isDirectory()) {
                    name += "/";
                }
                map.put(COLUMN_NAME_NAME, name);
                list.add(map);

                Log.d("Scan", " path " + path + f.getName());
            }
        }
        return list;
    }

    /**
     * 列表点击事件处理
     *
     * @param l
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String path;
        if (position > 0) {
            pathHistory.push(curPath);
            path = curPath + itemList.get(position).get(COLUMN_NAME_NAME);
        } else { // uplevel
            if (!pathHistory.empty())
                path = pathHistory.pop();
            else // root
                path = curPath;
        }
        Log.d("List View Click", " position: " + position + " name: " + path);

        File file = new File(path);
        if (file.isDirectory()) {
            updateList(path);
            curPath = path;
        } else {
            // 装载文件
            TextFilePath = curPath + itemList.get(position).get(COLUMN_NAME_NAME);
            Toast.makeText(getActivity(), "文件完整地址:\n" + TextFilePath, Toast.LENGTH_LONG).show();
            if ((TextFilePath.substring((TextFilePath.length() - 4), TextFilePath.length())).toLowerCase().equals(".txt")) {
                MainActivity MA = new MainActivity();
                MA.Set_Text_File_Path(TextFilePath);
                Toast.makeText(getActivity(), "[" + itemList.get(position).get(COLUMN_NAME_NAME) + "] 是可读取文件，已回传给MainActivity", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 更新列表
     *
     * @param path
     */
    private void updateList(String path) {
        itemList.clear();
        itemList = getData(path);
        adapter = new SimpleAdapter(getActivity(), itemList, R.layout.list_item, from, to);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "当前地址:\n" + path, Toast.LENGTH_SHORT).show();
    }

    public void goToIntent(View view) {
        Intent intent = new Intent(getActivity(), USBMainActivity.class);
        startActivity(intent);
    }
}
