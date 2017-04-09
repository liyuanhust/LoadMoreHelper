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
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lain.loadmorehelper.ISimpleDataSwapper;
import com.lain.loadmorehelper.LoadHelper;
import com.lain.loadmorehelper.PageData;
import com.lain.loadmoretest.data.BaseResult;
import com.lain.loadmoretest.data.DataLoader;
import com.lain.loadmoretest.data.Item;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by liyuan on 17/4/6.
 * Layout with {@link SwipeRefreshLayout}
 * and {@link ListView}
 */

public class Fragment4 extends Fragment {
    private List<Item> datas = new ArrayList<>();
    private LoadHelper<Item> loadHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.sample4);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment4, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        final MyAdapter4 adapter = new MyAdapter4();
        loadHelper = LoadHelper.create(swipeRefreshLayout)
                .setDataSwapper(adapter)
                .setAsyncLoader((page, lastPageData) -> doLoadData(page))
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


    private class MyAdapter4 extends BaseAdapter implements ISimpleDataSwapper<Item> {
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
                ViewHolder4 holder = new ViewHolder4(convertView);
                convertView.setTag(holder);
            }
            ViewHolder4 holder = (ViewHolder4) convertView.getTag();
            holder.tv.setText(item.getContent());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "This is " + item.getContent(), Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
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
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder4 extends RecyclerView.ViewHolder {
        public TextView tv;

        public ViewHolder4(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.txt);
        }
    }
}

