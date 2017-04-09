package com.lain.loadmoretest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lain.loadmorehelper.ILoadViewCreator;
import com.lain.loadmorehelper.ISimpleDataSwapper;
import com.lain.loadmorehelper.LoadMoreHelper;
import com.lain.loadmorehelper.PageData;
import com.lain.loadmoretest.data.BaseResult;
import com.lain.loadmoretest.data.DataLoader;
import com.lain.loadmoretest.data.Item;


import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by liyuan on 17/4/6.
 * Layout with {@link in.srain.cube.views.ptr.PtrClassicFrameLayout}
 * and {@link android.support.v7.widget.RecyclerView}
 */

public class Fragment1 extends Fragment{
    private List<Item> datas = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.sample1);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        PtrFrameLayout ptrFrameLayout = (PtrFrameLayout)view.findViewById(R.id.refresh);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final MyAdapter1 adapter = new MyAdapter1();
        recyclerView.setAdapter(adapter);
        final LoadMoreHelper<Item> loadHelper = LoadMoreHelper.create(ptrFrameLayout)
                .setDataSwapper(new ISimpleDataSwapper<Item>() {
                    @Override
                    public void swapData(List<? extends Item> list) {
                        datas.clear();
                        datas.addAll(list);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void appendData(List<? extends Item> list) {
                        datas.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                })
                .setSyncLoader(new LoadMoreHelper.SyncDataLoader<Item>() {
                    @Override
                    public PageData<Item> startLoadData(int page, PageData<Item> lastPageData) {
                        BaseResult<Item> result = DataLoader.loadDataSync(page, null);
                        if (result == null) {
                            return PageData.createFailed(page);
                        }
                        return PageData.createSuccess(page, result.getData(), result.isPageMore());
                    }
                })
                .setLoadcompleteViewCreator(new ILoadViewCreator<Item>() {
                    @Override
                    public View createView(ViewGroup parent, LoadMoreHelper<Item> loadHelper) {
                        return LayoutInflater.from(parent.getContext()).inflate(R.layout.load_complete_layout, parent, false);
                    }
                })
                .build()
                .startPullData(true);
    }



    private class MyAdapter1 extends RecyclerView.Adapter<ViewHolder1> {
        @Override
        public ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item, parent, false);
            return new ViewHolder1(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder1 holder, int position) {
            final Item item = datas.get(position);
            holder.tv.setText(item.getContent());
            holder.itemView.setOnClickListener(new View.OnClickListener(){
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
    }

    private static class ViewHolder1 extends RecyclerView.ViewHolder {
        public TextView tv;
        public ViewHolder1(View itemView) {
            super(itemView);
            tv = (TextView)itemView.findViewById(R.id.txt);
        }
    }
}

