package com.lain.loadmorehelper.pullview;

/**
 * Created by liyuan on 16/12/19.
 */

public interface IPullView {

    /**
     * 自动加载
     */
    void startAutoPull();

    /**
     * 开始refresh的监听
     */
    void setRefreshListener(IRefreshListener l);

    /**
     * 加载完毕
     */
    void doPullEnd();
}
