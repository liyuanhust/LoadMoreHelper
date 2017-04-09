# LoadMoreHelper
Android pull refresh and load more helper.  
一个android下拉刷新和上拉加载的框架。

How to use  
使用方式： 

The layout xml with `SwipeRefreshLayout` and `RecyclerView`  
布局文件里使用普通的`SwipeRefreshLayout`和`RecyclerView`即可

	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <android.support.v4.widget.SwipeRefreshLayout
        android:id="@+id/refresh"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        <android.support.v7.widget.RecyclerView
            android:id="@+id/list"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
    </android.support.v4.widget.SwipeRefreshLayout>
	</LinearLayout>	
	
	
Set data loader and data swapper into loadHelper, and start to pull data  
设置数据加载及数据填充方式，并开始下拉刷新
<pre>
        final MyAdapter2 adapter = new MyAdapter2();
        loadHelper = LoadHelper.create(swipeRefreshLayout)
                .setDataSwapper(adapter)
                .setAsyncLoader((page, lastPageData) -> doLoadData(page))
                .startPullData(true);
</pre>
