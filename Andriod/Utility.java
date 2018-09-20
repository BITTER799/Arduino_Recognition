package com.example.lenovo.arduinoforandroid;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class Utility {
    public static void setListViewHeightBasedOnChildren(ListView listView, int mode) {
        //获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0,count=0;
        int type=0;
        switch (mode) {
            case 1:
                type=10;break;
            case 2:
                type=8;break;
            case 3:
                type=7;break;
            case 4:
                type=11;break;
            default:
                type=10;break;
        }
        for (int i = 0, len = listAdapter.getCount(); i < type&&i<len; i++) {   //listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);  //计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight();  //统计所有子项的总高度
            count++;
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height =  totalHeight + (listView.getDividerHeight() * (count-1));
        //listView.getDividerHeight()获取子项间分隔符占用的高度
        //params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}
