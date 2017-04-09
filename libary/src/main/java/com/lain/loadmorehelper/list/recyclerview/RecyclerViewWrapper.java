package com.lain.loadmorehelper.list.recyclerview;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.lain.loadmorehelper.ISimpleDataSwapper;
import com.lain.loadmorehelper.LoadController;
import com.lain.loadmorehelper.list.IListWrapper;

/**
 * Created by liyuan on 17/4/4.
 */

public class RecyclerViewWrapper implements IListWrapper{
    private LoadController<?> loadController;
    private RecyclerView recyclerView;
    private FooterViewAdapter<?> footerAdapter;

    private RecyclerViewWrapper(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void init(LoadController<?> loadController) {
        this.loadController = loadController;

        ISimpleDataSwapper<?> dataSwapper = loadController.getSimpleDataSwaper();
        initFooterAdapter((dataSwapper instanceof RecyclerView.Adapter)? (RecyclerView.Adapter)dataSwapper:null);

        //Always has load more view
        footerAdapter.setLoadMoreCreator(loadController.getLoadMoreFooterCreator());
        footerAdapter.setLoadFailedCreator(loadController.getLoadFailedFooterCreator());
        footerAdapter.setLoadCompleteCreator(loadController.getLoadCompleteViewCreator());

        recyclerView.addOnScrollListener(scrollListener);
    }

    public void setState(@ListState int curState) {
        footerAdapter.setLoadMoreState(curState);
    }


    private <VH extends RecyclerView.ViewHolder> void initFooterAdapter(RecyclerView.Adapter<VH> input){
        RecyclerView.Adapter<VH> originAdapter = recyclerView.getAdapter();
        if (originAdapter instanceof FooterViewAdapter) {
            footerAdapter = (FooterViewAdapter<VH>) originAdapter;
            return;
        }
        if (originAdapter != null) {
            footerAdapter = new FooterViewAdapter<>(originAdapter);
            recyclerView.setAdapter(footerAdapter);
            return;
        }
        if (input != null) {
            footerAdapter = new FooterViewAdapter<>(input);
            recyclerView.setAdapter(footerAdapter);
            return;
        }
        throw new IllegalArgumentException("recycler view has no adapter");
    }


    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        private int[] pos;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            checkAndTryLoadMore(recyclerView);
        }

        private void checkAndTryLoadMore(RecyclerView recyclerView) {
            if (isLoading()) {
                return;
            }
            if (!footerAdapter.isLoadMoreState()) {
                return;
            }
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                if (pos == null) {
                    pos = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                }
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(pos);
                lastVisibleItemPosition = findMax(pos);
            } else {
                //Unknown layout manager, do nothing;
                throw new IllegalStateException("LoadController unknown layoutManager type");
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1
                    && layoutManager.getItemCount() >= layoutManager.getChildCount()) {
                loadController.doLoadMore();
            }
        }

        private boolean isLoading() {
            return loadController.isLoading();
        }
    };

    public static RecyclerViewWrapper create(RecyclerView recyclerView) {
        return new RecyclerViewWrapper(recyclerView);
    }

    /**
     * Find max value in a int array, throw {@link IllegalArgumentException} if array is empty
     */
    private static int findMax(@NonNull int[] array) {
        if (array.length <= 0) {
            throw new IllegalArgumentException("array is empty!");
        }
        int max = array[0];
        for (int value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
