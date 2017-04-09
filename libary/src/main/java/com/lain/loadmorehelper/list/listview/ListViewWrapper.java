package com.lain.loadmorehelper.list.listview;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.lain.loadmorehelper.ISimpleDataSwapper;
import com.lain.loadmorehelper.LoadController;
import com.lain.loadmorehelper.R;
import com.lain.loadmorehelper.list.IFooterViewCreator;
import com.lain.loadmorehelper.list.IListWrapper;

/**
 * Created by liyuan on 17/4/4.
 */

public class ListViewWrapper implements IListWrapper{
    private ListView listView;
    private LoadController<?> loadController;
    private @IListWrapper.ListState int curState = IListWrapper.STATE_INITIAL;
    private ViewGroup footerViewContainer;
    private SparseArray<FooterViewInfo> footerViewInfos = new SparseArray<>();

    public static class FooterViewInfo{
        int state;
        IFooterViewCreator creator;
        View view;
    }

    private ListViewWrapper(ListView listView) {
        this.listView = listView;
    }

    @Override
    public void init(LoadController<?> loadController) {
        this.loadController = loadController;
        ISimpleDataSwapper<?> dataSwapper = loadController.getSimpleDataSwaper();
        if (listView.getAdapter() == null && (dataSwapper instanceof ListAdapter)) {
            listView.setAdapter((ListAdapter) dataSwapper);
        }
        addFooterViewInfo(STATE_LOAD_MORE, loadController.getLoadMoreFooterCreator());
        addFooterViewInfo(STATE_LOAD_FAILED, loadController.getLoadFailedFooterCreator());
        addFooterViewInfo(STATE_LOAD_COMPLETE, loadController.getLoadCompleteViewCreator());
        MultiScrollListener.addScrollListener(listView, scrollListener);
    }

    private void addFooterViewInfo(int state, IFooterViewCreator footerViewCreator) {
        FooterViewInfo info = new FooterViewInfo();
        info.state = state;
        info.creator = footerViewCreator;
        footerViewInfos.put(state, info);
    }

    public void setState(@ListState int state) {
        if (this.curState == state) {
            return;
        }
        FooterViewInfo preInfo = footerViewInfos.get(this.curState);
        if (preInfo != null && preInfo.view != null) {
            preInfo.view.setVisibility(View.GONE);
        }
        this.curState = state;
        FooterViewInfo curInfo = footerViewInfos.get(this.curState);
        if (curInfo != null) {
            if (curInfo.view != null) {
                curInfo.view.setVisibility(View.VISIBLE);
            } else if (curInfo.creator != null){
                if (footerViewContainer == null) {
                    footerViewContainer = new FrameLayout(listView.getContext());
                    listView.addFooterView(footerViewContainer);
                }
                curInfo.view = curInfo.creator.getView(footerViewContainer);
                footerViewContainer.addView(curInfo.view);
            }
        }
    }


    private ListView.OnScrollListener scrollListener = new ListView.OnScrollListener(){
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {}

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (loadController.isLoading() || curState != IListWrapper.STATE_LOAD_MORE) {
                return;
            }
            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;
            if (loadMore) {
                loadController.doLoadMore();
            }
        }
    };

    public static ListViewWrapper create(ListView listView) {
        return new ListViewWrapper(listView);
    }
}
