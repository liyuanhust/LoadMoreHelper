package com.lain.loadmorehelper.pullview;

import android.support.v4.widget.SwipeRefreshLayout;

/**
 * Created by liyuan on 16/12/19.
 */

public class SwipeFreshPullView implements IPullView{
    private SwipeRefreshLayout swipeRefreshLayout;
    private IRefreshListener refreshListener;

    public SwipeFreshPullView(SwipeRefreshLayout swipeRefreshLayout) {
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void startAutoPull() {
        swipeRefreshLayout.post(()->{
            swipeRefreshLayout.setRefreshing(true);
            if (refreshListener != null) {
                refreshListener.doRefresh();
            }
        });
    }

    @Override
    public void setRefreshListener(IRefreshListener l) {
        refreshListener = l;
        swipeRefreshLayout.setOnRefreshListener(l::doRefresh);
    }


    @Override
    public void doPullEnd() {
        swipeRefreshLayout.setRefreshing(false);
    }


    public static SwipeFreshPullView create(SwipeRefreshLayout swipeRefreshLayout) {
        return new SwipeFreshPullView(swipeRefreshLayout);
    }
}
