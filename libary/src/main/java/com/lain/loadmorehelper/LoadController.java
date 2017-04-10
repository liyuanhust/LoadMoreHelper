package com.lain.loadmorehelper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.MainThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.lain.loadmorehelper.list.IFooterViewCreator;
import com.lain.loadmorehelper.list.IListWrapper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * Created by liyuan on 16/12/20.
 */

public class LoadController<DT> {
    private static final String TAG = "LoadController";
    private static final int FIRST_PAGE_INDEX = PageData.FIRST_PAGE_INDEX;
    private static Handler sUiHandler = new Handler(Looper.getMainLooper());
    private static Executor sDefaultExecutor;

    private LoadMoreHelper.ParamBuilder<DT> paramBuilder;

    /**
     * Indicate current page index
     */
    private int mCurrentPageIndex = 0;

    /**
     * Indicate current is loading data
     */
    private boolean isPullingData;

    /**
     * Indicate current is loading more
     */
    private boolean isLoadingMore;

    /**
     * Flag if current loadcontroller is destroyed;
     */
    private boolean isDestoryed;


    /**
     * The lastest page data;
     */
    private PageData<DT> lastPageData;


    LoadController(LoadMoreHelper.ParamBuilder<DT> builder) {
        init(builder);
    }

    private void init(LoadMoreHelper.ParamBuilder<DT> builder) {
        this.paramBuilder = builder;

        //Set pull data listener
        if (builder.pullView != null) {
            builder.pullView.setRefreshListener(this::doPullData);
        }

        if (builder.asyncLoader != null) {
            //Do nothing
        } else if (builder.syncDataLoader != null) {
            final Executor executor = builder.executor == null? getDefaultExecutor():builder.executor;
            builder.asyncLoader = (pageIndex, lastPageData)-> {
                executor.execute(()->{
                    PageData<DT> pageData = builder.syncDataLoader.startLoadData(pageIndex, lastPageData);
                    sUiHandler.post(()->onLoadDataEnd(pageData));
                });
            };
        } else {
            throw new IllegalArgumentException("Neither asyncload or syncloader is set");
        }
        builder.listWrapper.init(this);
    }


    /**
     * Called when user pull data. it will load the 1st page of data
     * When doing pull set {@link #isPullingData} true and load the 1st page
     */
    @MainThread
    private void doPullData() {
        if (isPullingData || isDestoryed) {
            return;
        }
        isPullingData = true;
        mCurrentPageIndex = 0;
        startLoadData(FIRST_PAGE_INDEX);
    }

    /**
     * Call when 1st page data loaded
     */
    @MainThread
    private void onPullDataEnd() {
        isPullingData = false;
        if (isDestoryed) {
            return;
        }
        if (paramBuilder.pullView != null) {
            paramBuilder.pullView.doPullEnd();
        }
    }

    /**
     * Call when recyclerview scroll to the end
     */
    @MainThread
    public void doLoadMore() {
        if (isPullingData || isLoadingMore || isDestoryed) {
            return;
        }
        isLoadingMore = true;
        paramBuilder.listWrapper.setState(IListWrapper.STATE_LOAD_MORE);
        startLoadData(mCurrentPageIndex + 1);
    }


    public boolean isLoading() {
        return isPullingData || isLoadingMore;
    }

    /**
     * Call when loadmore_vertical page end
     */
    @MainThread
    private void onLoadMoreEnd(PageData<DT> pageData) {
        isLoadingMore = false;
        if (isDestoryed) {
            return;
        }
        if (pageData.isSuccess()) {
            mCurrentPageIndex = pageData.getPageIndex();
            //Load data success
            boolean pageMore = pageData.isPageMore();
            paramBuilder.listWrapper.setState(pageMore ? IListWrapper.STATE_LOAD_MORE : IListWrapper.STATE_LOAD_COMPLETE);
        } else {
            //Load data failed
            paramBuilder.listWrapper.setState(IListWrapper.STATE_LOAD_FAILED);
        }
        fillData(pageData);
    }

    private void fillData(PageData<DT> pageData) {
        if (pageData.getPageIndex() == FIRST_PAGE_INDEX) {
            paramBuilder.simpleDataSwaper.swapData(pageData.getData());
        } else {
            paramBuilder.simpleDataSwaper.appendData(pageData.getData());
        }
    }

    @MainThread
    private void startLoadData(final int pageIndex) {
        if (isDestoryed) {
            return;
        }
        paramBuilder.asyncLoader.startLoadData(pageIndex, (pageIndex == PageData.FIRST_PAGE_INDEX)? null:lastPageData);
    }

    /**
     * If is async loader, user should call this method self when load data end
     * @param pageData
     */
    @MainThread
    void onLoadDataEnd(PageData<DT> pageData) {
        if (isDestoryed) {
            return;
        }
        if (!isLoading()) {
            return;
        }
        if (paramBuilder.onLoadEndHandler != null) {
            paramBuilder.onLoadEndHandler.doOnLoadEnd(pageData);
        }
        if (pageData.getPageIndex() != (mCurrentPageIndex + 1)) {
            //Page index not correct, ignore the result
            Log.w(TAG, "onLoadDataEnd pageIndex not correct, current pageIndex:" + mCurrentPageIndex
                + ", receive pageIndex:" + pageData.getPageIndex());
            return;
        }
        onLoadMoreEnd(pageData);
        if (pageData.getPageIndex() == FIRST_PAGE_INDEX && isPullingData) {
            onPullDataEnd();
        }
        lastPageData = pageData;
    }

    /**
     * Start load data auto
     */
    @MainThread
    public void startLoadAuto(boolean anima) {
        if (isDestoryed) {
            return;
        }
        if (paramBuilder.pullView != null && anima) {
            paramBuilder.pullView.startAutoPull();
        } else {
            doPullData();
        }
    }


    public void release() {
        isDestoryed = true;
    }

    public IDataSwapper<DT> getSimpleDataSwaper() {
        return paramBuilder.simpleDataSwaper;
    }

    public IFooterViewCreator getLoadMoreFooterCreator() {
        if (paramBuilder.loadmoreCreator == null && LoadMoreHelper.sLoadMoreLayoutRes != 0) {
            paramBuilder.loadmoreCreator = ((parent, loadHelper) ->
                LayoutInflater.from(parent.getContext()).inflate(LoadMoreHelper.sLoadMoreLayoutRes, parent, false));
        }
        if (paramBuilder.loadmoreCreator != null) {
            return parent-> paramBuilder.loadmoreCreator.createView(parent, this.loadHelper);
        }
        return null;
    }

    public IFooterViewCreator getLoadFailedFooterCreator() {
        if (paramBuilder.loadFailedViewCreator == null && LoadMoreHelper.sLoadMoreLayoutRes != 0) {
            paramBuilder.loadFailedViewCreator = ((parent, loadHelper) -> {
                View view = LayoutInflater.from(parent.getContext()).inflate(LoadMoreHelper.sLoadFaileLayoutRes, parent, false);
                view.setOnClickListener(v->loadHelper.doLoadMore());
                return view;
            });
        }
        if (paramBuilder.loadFailedViewCreator != null) {
            return parent-> paramBuilder.loadFailedViewCreator.createView(parent, this.loadHelper);
        }
        return null;
    }

    public IFooterViewCreator getLoadCompleteViewCreator() {
        if (paramBuilder.loadcompleteViewCreator == null && LoadMoreHelper.sLoadCompleteLayoutRes != 0) {
            paramBuilder.loadcompleteViewCreator = ((parent, loadHelper) ->
                LayoutInflater.from(parent.getContext()).inflate(LoadMoreHelper.sLoadCompleteLayoutRes, parent, false));
        }
        if (paramBuilder.loadcompleteViewCreator != null) {
            return parent -> paramBuilder.loadcompleteViewCreator.createView(parent, this.loadHelper);
        }
        return null;
    }

    public LoadMoreHelper<DT> getLoadHelper() {
        return loadHelper;
    }

    private LoadMoreHelper<DT> loadHelper = new LoadMoreHelper<DT>() {
        @MainThread
        public LoadMoreHelper<DT> startPullData(boolean anima) {
            startLoadAuto(anima);
            return this;
        }

        public LoadMoreHelper<DT> onLoadEnd(PageData<DT> pageData) {
            if (isOnMainThread()) {
                onLoadDataEnd(pageData);
            } else {
                sUiHandler.post(()->onLoadDataEnd(pageData));
            }
            return this;
        }

        @Override
        public LoadMoreHelper<DT> doLoadMore() {
            if (isOnMainThread()) {
                LoadController.this.doLoadMore();
            } else {
                sUiHandler.post(LoadController.this::doLoadMore);
            }
            return null;
        }

    };

    private static Executor getDefaultExecutor() {
        if (sDefaultExecutor == null) {
            sDefaultExecutor = Executors.newSingleThreadExecutor();
        }
        return sDefaultExecutor;
    }

    /**
     * Returns {@code true} if called on the main thread, {@code false} otherwise.
     */
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
