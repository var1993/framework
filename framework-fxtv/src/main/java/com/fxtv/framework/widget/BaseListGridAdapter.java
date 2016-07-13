package com.fxtv.framework.widget;

import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2015/12/7.
 * ListView GridView通用的适配器
 */
public abstract class BaseListGridAdapter<T> extends BaseAdapter {
    private List<T> listData;

    public BaseListGridAdapter() {
    }

    public BaseListGridAdapter(List<T> listData) {
        this.listData = listData;
    }

    public void setListData(List<T> listData) {
        //if(!this.listData.equals(listData)){
        this.listData = listData;
        notifyDataSetChanged();
        //}
    }

    public void setListDataInvalidated(List<T> listData){
        this.listData = listData;
        notifyDataSetInvalidated();
    }

    public void onDestroy() {
        this.listData = null;
    }

    public void addData(List<T> listData) {
        if (this.listData == null)
            this.listData = listData;
        else
            this.listData.addAll(listData);
        notifyDataSetChanged();
    }

    public List<T> getListData() {
        return listData;
    }

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public T getItem(int i) {
        return listData == null || i>=listData.size() ? null : listData.get(i);
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
