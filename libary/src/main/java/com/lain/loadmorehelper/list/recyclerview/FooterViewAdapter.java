package com.lain.loadmorehelper.list.recyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.lain.loadmorehelper.list.IFooterViewCreator;
import com.lain.loadmorehelper.list.IListWrapper;

import static com.lain.loadmorehelper.list.IListWrapper.STATE_INITIAL;
import static com.lain.loadmorehelper.list.IListWrapper.STATE_LOAD_MORE;
import static com.lain.loadmorehelper.list.IListWrapper.STATE_LOAD_FAILED;

/**
 * Created by liyuan on 16/12/19.
 */

public class FooterViewAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter {
    private static final int VIEW_TYPE_LOADMORE = Integer.MAX_VALUE - 1;
    private static final int VIEW_TYPE_COMPLETE = Integer.MAX_VALUE - 2;
    private static final int VIEW_TYPE_LOADFAILED =  Integer.MAX_VALUE - 3;

    private @IListWrapper.ListState int mLoadMoreStatus = STATE_INITIAL;

    private SparseArray<IFooterViewCreator> footerViewCreators = new SparseArray<>();

    public FooterViewAdapter(RecyclerView.Adapter<VH> adapter) {
        super(adapter);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isFooterViewType(viewType)) {
            IFooterViewCreator creator = footerViewCreators.get(viewType);
            if (creator == null) {
                throw new IllegalArgumentException("unknow footer type:" + viewType);
            }
            View view = creator.getView(parent);
            return new RecyclerView.ViewHolder(view) {};
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isFooterViewType(holder.getItemViewType())) {
            return;
        }
        super.onBindViewHolder((VH)holder, position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isFooterPosition(position)) {
            if (hasLoadMoreView()) {
                return VIEW_TYPE_LOADMORE;
            }
            if (hasFailedView()) {
                return VIEW_TYPE_LOADFAILED;
            }
            if (hasCompleteView()) {
                return VIEW_TYPE_COMPLETE;
            }
        }
        int wrappedType = super.getItemViewType(position);
        if (isFooterViewType(wrappedType)) {
            throw new IllegalArgumentException("wrapped adapter has footer view type!");
        }
        return wrappedType;
    }

    @Override
    public int getItemCount() {
        return getInnerItemCount();
    }

    @Override
    public long getItemId(int position) {
        if (isFooterPosition(position)) {
            return -1;
        }
        return getWrappedAdapter().getItemId(position);
    }

    private int getInnerItemCount() {
        int wrapperItemCount = getWrappedAdapter().getItemCount();
        if (hasLoadMoreView() || hasFailedView() || hasCompleteView()) {
            return wrapperItemCount + 1;
        }
        return wrapperItemCount;
    }

    private boolean isFooterPosition(int pos) {
        if (pos == getInnerItemCount() - 1) {
            return true;
        }
        return false;
    }


    private boolean isFooterViewType(int viewType) {
        return viewType == VIEW_TYPE_LOADMORE || viewType == VIEW_TYPE_LOADFAILED || viewType == VIEW_TYPE_COMPLETE;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemType = getItemViewType(position);
                    return isFooterViewType(itemType)? gridLayoutManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * Set when load more state changed
     */
    public void setLoadMoreState(@IListWrapper.ListState int state) {
        if (mLoadMoreStatus == state) {
            return;
        }
        int preItemCount = getInnerItemCount();
        mLoadMoreStatus = state;
        int curItemCount = getInnerItemCount();
        if (preItemCount > curItemCount) {
            notifyItemRemoved(preItemCount -1);
        } else if (preItemCount < curItemCount) {
            notifyItemInserted(curItemCount -1);
        } else {
            notifyItemChanged(preItemCount -1);
        }
    }

    /**
     * Get the load more state
     */
    public int getLoadMoreState() {
        return mLoadMoreStatus;
    }

    public boolean isLoadMoreState() {
        return mLoadMoreStatus == STATE_LOAD_MORE;
    }

    public void setLoadMoreCreator(IFooterViewCreator creator) {
        footerViewCreators.put(VIEW_TYPE_LOADMORE, creator);
    }

    public void setLoadFailedCreator(IFooterViewCreator creator) {
        footerViewCreators.put(VIEW_TYPE_LOADFAILED, creator);
    }

    public void setLoadCompleteCreator(IFooterViewCreator creator) {
        footerViewCreators.put(VIEW_TYPE_COMPLETE, creator);
    }

    private boolean hasLoadMoreView() {
        return mLoadMoreStatus == STATE_LOAD_MORE && footerViewCreators.get(VIEW_TYPE_LOADMORE) != null;
    }

    private boolean hasCompleteView() {
        return mLoadMoreStatus == IListWrapper.STATE_LOAD_COMPLETE && footerViewCreators.get(VIEW_TYPE_COMPLETE) != null;
    }

    private boolean hasFailedView() {
        return mLoadMoreStatus == STATE_LOAD_FAILED && footerViewCreators.get(VIEW_TYPE_LOADFAILED) != null;
    }

}
