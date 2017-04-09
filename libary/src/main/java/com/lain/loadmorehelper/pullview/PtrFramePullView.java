package com.lain.loadmorehelper.pullview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by liyuan on 16/12/19.
 */

public class PtrFramePullView implements IPullView {
    private PtrFrameLayout ptrFrameLayout;

    public PtrFramePullView(PtrFrameLayout layout) {
        this.ptrFrameLayout = layout;
    }

    @Override
    public void startAutoPull() {
        ptrFrameLayout.post(()-> ptrFrameLayout.autoRefresh(true));
    }

    @Override
    public void setRefreshListener(IRefreshListener l) {
        setPtrFresher(ptrFrameLayout, l);
    }

    @Override
    public void doPullEnd() {
        ptrFrameLayout.refreshComplete();
    }


    private static boolean checkRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            if (linearLayoutManager.getItemCount() == 0) {
                return true;
            }
            int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
            if (firstVisiblePosition == 0) {
                View firstVisibleView = linearLayoutManager.findViewByPosition(firstVisiblePosition);
                int top = firstVisibleView.getTop();
                return top >= 0;
            } else {
                return false;
            }
        }
        return false;
    }

    public static void setPtrFresher(PtrFrameLayout ptrFrameLayout, IRefreshListener refreshListener) {
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshListener.doRefresh();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if (content instanceof RecyclerView) {
                    return checkRecyclerView((RecyclerView) content);
                }
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }


    public static PtrFramePullView create(PtrFrameLayout ptrFrameLayout) {
        return new PtrFramePullView(ptrFrameLayout);
    }
}
