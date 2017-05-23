package com.leoliu.anshare.ax_edp;

/**
 * Created by Anshare_LY on 2017/5/23.
 */

import android.app.ListFragment;
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

    public static final String COLUMN_NAME_NAME = "name";
    private SimpleAdapter adapter = null;
    private List<Map<String, Object>> itemList;
    private Stack<String> pathHistory = null;
    private String curPath;

    String[] from = {COLUMN_NAME_NAME};
    int[] to = {R.id.FileTextView};

    private String TextFilePath;

    public FileManager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.file_manager, container, false);
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
                Toast.makeText(getActivity(), "显示优盘目录" + "", Toast.LENGTH_SHORT).show();
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
            curPath = SDFile.getAbsolutePath() + "/";
            itemList = getData(curPath);
            adapter = new SimpleAdapter(getActivity(), itemList, R.layout.list_item, from, to);
            setListAdapter(adapter);
        }
    }

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

            Toast toast = Toast.makeText(getActivity(), itemList.get(position).get(COLUMN_NAME_NAME) + " is a file", Toast.LENGTH_SHORT);
            toast.show();
            TextFilePath = curPath + itemList.get(position).get(COLUMN_NAME_NAME);
            Toast.makeText(getActivity(), "文件完整地址:\n" + TextFilePath, Toast.LENGTH_LONG).show();
        }
    }

    private void updateList(String path) {
        itemList.clear();
        itemList = getData(path);
        adapter = new SimpleAdapter(getActivity(), itemList, R.layout.list_item, from, to);
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "当前地址:\n" + path, Toast.LENGTH_SHORT).show();
    }
}
