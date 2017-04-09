package com.lain.loadmorehelper;

import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.lain.loadmorehelper.list.IListWrapper;
import com.lain.loadmorehelper.list.listview.ListViewWrapper;
import com.lain.loadmorehelper.list.recyclerview.RecyclerViewWrapper;
import com.lain.loadmorehelper.pullview.IPullView;
import com.lain.loadmorehelper.pullview.PtrFramePullView;
import com.lain.loadmorehelper.pullview.SwipeFreshPullView;

import java.util.concurrent.Executor;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by liyuan on 16/12/19.
 */

public abstract class LoadMoreHelper<VM> {

    /**
     * Call to load the data from page 1 without pull view animation
     * @return
     */
    public LoadMoreHelper<VM> startPullData() {
        return startPullData(false);
    }


    @MainThread
    public abstract LoadMoreHelper<VM> startPullData(boolean anima);

    /**
     * When load data end on work thread, call this method to pass the data
     * @param pageData
     * @return
     */
    public abstract LoadMoreHelper<VM> onLoadEnd(PageData<VM> pageData);

    /**
     * Load data manually, for example when load failed, call this method to retry
     * @return
     */
    public abstract LoadMoreHelper<VM> doLoadMore();

    public static BuilderStep1 create(PtrFrameLayout ptrFrameLayout) {
        IPullView pullView = PtrFramePullView.create(ptrFrameLayout);
        IListWrapper listWrapper = null;
        if ((listWrapper = createListWrapper(ptrFrameLayout.getContentView())) == null) {
            throw new IllegalArgumentException("can not find recyclerview or listview in ptrFrameLayout");
        }
        return new BuilderStep1(pullView, listWrapper);
    }

    public static BuilderStep1 create(SwipeRefreshLayout swipeRefreshLayout) {
        IPullView pullView = SwipeFreshPullView.create(swipeRefreshLayout);
        IListWrapper listWrapper = null;
        for (int i=0; i<swipeRefreshLayout.getChildCount();i++) {
            if ((listWrapper = createListWrapper(swipeRefreshLayout.getChildAt(i))) != null) {
                break;
            }
        }
        if (listWrapper == null) {
            throw new IllegalArgumentException("can not find recyclerview or listview in swipeRefreshLayout");
        }
        return new BuilderStep1(pullView, listWrapper);
    }

    /**
     * Create with a loadhelper without pull fuction. Only load more
     */
    public static BuilderStep1 create(RecyclerView recyclerView) {
        return new BuilderStep1(null, RecyclerViewWrapper.create(recyclerView));
    }

    public static BuilderStep1 create(ListView listView) {
        return new BuilderStep1(null, ListViewWrapper.create(listView));
    }

    public static BuilderStep1 create(IPullView pullView, IListWrapper listWrapper) {
        return new BuilderStep1(pullView, listWrapper);
    }

    public static IListWrapper createListWrapper(View contentView) {
        if (contentView instanceof RecyclerView) {
            return RecyclerViewWrapper.create((RecyclerView) contentView);
        } else if (contentView instanceof ListView) {
            return ListViewWrapper.create((ListView)contentView);
        }
        return null;
    }

    public static class BuilderStep1 {
        private IPullView pullView;
        private IListWrapper listWrapper;

        private BuilderStep1(@Nullable IPullView pullView, @NonNull IListWrapper listWrapper) {
            this.pullView = pullView;
            this.listWrapper = listWrapper;
        }

        /**
         * Set adapter
         * @param <VM>
         */
        public <VM> BuilderStep2<VM> setDataSwapper(IDataSwapper<VM> dataSwapper) {
            return new BuilderStep2<>(this, dataSwapper);
        }
    }

    public static class BuilderStep2<VM> {
        private final ParamBuilder<VM> paramBuilder;
        private BuilderStep2(BuilderStep1 step1, IDataSwapper<VM> dataSwapper) {
            paramBuilder = new ParamBuilder<>();
            paramBuilder.pullView = step1.pullView;
            paramBuilder.listWrapper = step1.listWrapper;
            paramBuilder.simpleDataSwaper = dataSwapper;
        }
        /**
         * Set sync data loader, return pagedata directly. It will execute on work thread
         */
        public ParamBuilder<VM> setSyncLoader(final SyncDataLoader<VM> syncLoader) {
            paramBuilder.syncDataLoader = syncLoader;
            return paramBuilder;
        }

        /**
         * Set async data loader, you must call {@link LoadMoreHelper#onLoadEnd(PageData)} by yourself
         * when load data end.
         */
        public ParamBuilder<VM> setAsyncLoader(AsyncDataLoader<VM> asyncLoader) {
            paramBuilder.asyncLoader = asyncLoader;
            return paramBuilder;
        }
    }


    public static class ParamBuilder<VM> {
        IListWrapper listWrapper;
        IPullView pullView;
        IDataSwapper<VM> simpleDataSwaper;
        OnLoadEndHandler<VM> onLoadEndHandler;
        SyncDataLoader<VM> syncDataLoader;
        AsyncDataLoader<VM> asyncLoader;
        Executor executor;

        ILoadViewCreator<VM> loadmoreCreator;
        ILoadViewCreator<VM> loadFailedViewCreator;
        ILoadViewCreator<VM> loadcompleteViewCreator ;

        private ParamBuilder() {
        }

        /**
         * It works when set syndatacload. It not set, use default executor
         * @param executor
         * @return
         */
        public ParamBuilder<VM> setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        /**
         * Give a chance to handle data before call {@link IDataSwapper}
         * if not set, do nothing
         * @param onLoadEndHandler
         * @return
         */
        public ParamBuilder<VM> setOnLoadEndHandler(OnLoadEndHandler<VM> onLoadEndHandler) {
            this.onLoadEndHandler = onLoadEndHandler;
            return this;
        }

        /**
         * Show the loading more view on bottom of listview
         * If not set, use the default sLoadmoreViewCreator
         * @param footerCreator
         * @return
         */
        public ParamBuilder<VM> setLoadmoreViewCreator(ILoadViewCreator<VM> footerCreator) {
            this.loadmoreCreator = footerCreator;
            return this;
        }

        /**
         * Show the load failed view on bottom of listview.
         * You could set it by self and do {@link LoadMoreHelper#doLoadMore()} to retry when load failed.
         * Default is null
         * @param footerCreator
         * @return
         */
        public ParamBuilder<VM> setLoadFailedViewCreator(ILoadViewCreator<VM> footerCreator) {
            this.loadFailedViewCreator = footerCreator;
            return this;
        }

        /**
         * Show the load complete view on bottom of listview when no more data
         * Default is null
         * @param footerCreator
         * @return
         */
        public ParamBuilder<VM> setLoadcompleteViewCreator(ILoadViewCreator<VM> footerCreator) {
            this.loadcompleteViewCreator = footerCreator;
            return this;
        }

        /**
         * Construct {@link LoadMoreHelper}, call {@link LoadMoreHelper#startPullData()} by yourself
         * @return
         */
        public LoadMoreHelper<VM> build() {
            LoadController<VM> loadController = new LoadController<>(this);
            return loadController.getLoadHelper();
        }

        /**
         * Construct {@link LoadMoreHelper}, and start to pull data
         * @return
         */
        public LoadMoreHelper<VM> startPullData() {
            LoadController<VM> loadController = new LoadController<>(this);
            LoadMoreHelper<VM> loadHelper =  loadController.getLoadHelper();
            loadHelper.startPullData();
            return loadHelper;
        }

        public LoadMoreHelper<VM> startPullData(boolean anima) {
            LoadController<VM> loadController = new LoadController<>(this);
            LoadMoreHelper<VM> loadHelper =  loadController.getLoadHelper();
            loadHelper.startPullData(anima);
            return loadHelper;
        }
    }


    @LayoutRes
    static int sLoadMoreLayoutRes = R.layout.default_loadmore_layout;

    @LayoutRes
    static int sLoadFaileLayoutRes = R.layout.default_loadfailed_layout;

    @LayoutRes
    static int sLoadCompleteLayoutRes;

    /**
     * Set default loadmore creater
     */
    public static void setDefaultLoadMoreLayoutRes(@LayoutRes int layoutRes) {
        sLoadMoreLayoutRes = layoutRes;
    }

    public static void setDefaultLoaderrorCreator(@LayoutRes int layoutRes) {
        sLoadFaileLayoutRes = layoutRes;
    }

    public static void setDefaultLoadcompleteCreator(@LayoutRes int layoutRes) {
        sLoadCompleteLayoutRes = layoutRes;
    }

    @LayoutRes
    public static int getsLoadMoreLayoutRes() {
        return sLoadMoreLayoutRes;
    }

    @LayoutRes
    public static int getsLoadFaileLayoutRes() {
        return sLoadFaileLayoutRes;
    }

    @LayoutRes
    public static int getsLoadCompleteLayoutRes() {
        return sLoadCompleteLayoutRes;
    }



    /**
     * Load data sync. LoadHelper will call it on work thread
     */
    @WorkerThread
    public interface SyncDataLoader<VM> {
        PageData<VM> startLoadData(int page, PageData<VM> lastPageData);
    }

    /**
     *
     * Load data int async thread
     * @param <VM>
     */
    public interface AsyncDataLoader<VM> {
        void startLoadData(int page, PageData<VM> lastPageData);
    }

    /**
     * Called when data loaded end
     * @param <VM>
     */
    @MainThread
    public interface OnLoadEndHandler<VM> {
        void doOnLoadEnd(PageData<VM> pageData);
    }
}
