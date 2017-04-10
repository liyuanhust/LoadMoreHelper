package com.lain.loadmoretest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lain.loadmorehelper.ILoadViewCreator;
import com.lain.loadmorehelper.IDataSwapper;
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
 * and {@link ListView}
 */

public class Fragment3 extends Fragment{
    private List<Item> datas = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.sample3);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment3, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        PtrFrameLayout ptrFrameLayout = (PtrFrameLayout)view.findViewById(R.id.refresh);
        ListView listView = (ListView) view.findViewById(R.id.list);
        final MyAdapter3 adapter = new MyAdapter3();
        listView.setAdapter(adapter);
        final LoadMoreHelper<Item> loadHelper = LoadMoreHelper.create(ptrFrameLayout)
                .setDataSwapper(new IDataSwapper<Item>() {
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
                .setSyncDataLoader(new LoadMoreHelper.SyncDataLoader<Item>() {
                    @Override
                    public PageData<Item> startLoadData(int page, PageData<Item> lastPageData) {
                        BaseResult<Item> result = DataLoader.loadDataSync(page, null);
                        if (result == null) {
                            return PageData.createFailed(page);
                        }
                        return PageData.createSuccess(page, result.getData(), result.isPageMore());
                    }
                })
                .setLoadCompleteViewCreator(new ILoadViewCreator<Item>() {
                    @Override
                    public View createView(ViewGroup parent, LoadMoreHelper<Item> loadHelper) {
                        return LayoutInflater.from(parent.getContext()).inflate(R.layout.load_complete_layout, parent, false);
                    }
                })
                .build()
                .startPullData(true);
    }

    private class MyAdapter3 extends BaseAdapter {
        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return datas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Item item = datas.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.list_item, parent, false);
                ViewHolder3 holder = new ViewHolder3(convertView);
                convertView.setTag(holder);
            }
            ViewHolder3 holder = (ViewHolder3)convertView.getTag();
            holder.tv.setText(item.getContent());
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "This is " + item.getContent(), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }
    }

    private static class ViewHolder3{
        public View itemView;
        public TextView tv;
        public ViewHolder3(View itemView) {
            this.itemView = itemView;
            tv = (TextView)itemView.findViewById(R.id.txt);
        }
    }
}

