package com.lain.loadmoretest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lain.loadmorehelper.IDataSwapper;
import com.lain.loadmorehelper.LoadMoreHelper;
import com.lain.loadmorehelper.PageData;
import com.lain.loadmoretest.data.DataLoader;
import com.lain.loadmoretest.data.Item;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liyuan on 17/4/6.
 * Layout with {@link SwipeRefreshLayout}
 * and {@link RecyclerView}
 */

public class Fragment2 extends Fragment {
    private List<Item> datas = new ArrayList<>();
    private LoadMoreHelper<Item> loadHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.sample2);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final MyAdapter2 adapter = new MyAdapter2();
        loadHelper = LoadMoreHelper.create(swipeRefreshLayout)
                .setDataSwapper(adapter)
                .setAsyncDataLoader((page, lastPageData) -> doLoadData(page))
                .startPullData(true);
    }

    private void doLoadData(final int pageIndex) {
        DataLoader.loadData(pageIndex, null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    PageData<Item> pageData = PageData.createSuccess(pageIndex, result.getData(), result.isPageMore());
                    loadHelper.onLoadEnd(pageData);
                }, e -> {
                    PageData<Item> pageData = PageData.createFailed(pageIndex);
                    loadHelper.onLoadEnd(pageData);
                });
    }


    private class MyAdapter2 extends RecyclerView.Adapter<ViewHolder2> implements IDataSwapper<Item> {
        @Override
        public ViewHolder2 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder2(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder2 holder, int position) {
            final Item item = datas.get(position);
            holder.tv.setText(item.getContent());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "This is " + item.getContent(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public void swapData(List<? extends Item> list) {
            datas.clear();
            datas.addAll(list);
            notifyDataSetChanged();
        }

        @Override
        public void appendData(List<? extends Item> list) {
            if (list == null || list.isEmpty()) {
                return;
            }
            int start = datas.size();
            datas.addAll(list);
            notifyItemRangeInserted(start, list.size());
        }
    }

    private static class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView tv;

        public ViewHolder2(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.txt);
        }
    }
}

