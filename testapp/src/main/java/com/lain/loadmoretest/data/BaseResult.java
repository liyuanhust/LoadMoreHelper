package com.lain.loadmoretest.data;

import java.util.List;

/**
 * Created by liyuan on 17/4/6.
 */

public class BaseResult<T> {
    List<T> data;
    boolean pageMore;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public boolean isPageMore() {
        return pageMore;
    }

    public void setPageMore(boolean pageMore) {
        this.pageMore = pageMore;
    }
}
