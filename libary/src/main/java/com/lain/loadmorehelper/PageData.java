package com.lain.loadmorehelper;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by liyuan on 16/11/23.
 */

public final class PageData<DT> {
    public static final int FIRST_PAGE_INDEX = 1;
    private boolean success = true;
    private List<DT> data;
    private boolean pageMore;
    private int pageIndex;
    private Map<String, Object> objectMap;
    private Object tag;

    public PageData(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<DT> getData() {
        return data;
    }

    private void setData(List<DT> data) {
        this.data = data;
    }

    public boolean isPageMore() {
        return pageMore;
    }

    private void setPageMore(boolean pageMore) {
        this.pageMore = pageMore;
    }

    private void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public Object getObject(String key) {
        return objectMap == null?null:objectMap.get(key);
    }

    public void putObject(String key, Object object) {
        if (objectMap == null) {
            objectMap = new ArrayMap<>();
        }
        objectMap.put(key, object);
    }

    public void setTag(Object object) {
        this.tag = object;
    }

    public Object getTag() {
        return tag;
    }

    public boolean isDataEmpty() {
        return data == null || data.isEmpty();
    }


    public static <DT> PageData<DT> createSuccess(int pageIndex, @NonNull List<DT> list, boolean pageMore) {
        PageData<DT> pageData = new PageData<>(true);
        pageData.setPageIndex(pageIndex);
        pageData.setData(list);
        pageData.setPageMore(pageMore);
        return pageData;
    }

    public static <DT> PageData<DT> createFailed(int pageIndex) {
        PageData<DT> pageData = new PageData<>(false);
        pageData.setPageIndex(pageIndex);
        pageData.setData(new ArrayList<>());
        return pageData;
    }
}
