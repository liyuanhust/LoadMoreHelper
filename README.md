# LoadMoreHelper
Android pull refresh and load more helper.  
一个android下拉刷新和上拉加载的框架。

### How to use  
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
	
	
Set data loader and data swapper into loadMoreHelper, and start to pull data  
设置数据加载及数据填充方式，并开始下拉刷新
<pre>
        final MyAdapter2 adapter = new MyAdapter2();
        loadHelper = LoadHelper.create(swipeRefreshLayout)
                .setDataSwapper(adapter)
                .setAsyncLoader((page, lastPageData) -> doLoadData(page))
                .startPullData(true);
</pre>


======
#### Description
说明  

`RecyclerView.Adapter` could implements the interface `IDataSwapper`   
Adapter 可以 继承`IDataSwapper` ，也可以单独实现
<pre>
public interface IDataSwapper<VM> {
    /**
     * Swap all datas
     */
    void swapData(List<? extends VM> list);
    /**
     * Append data to the end of current list
     */
    void appendData(List<? extends VM> list);
}
</pre>

<pre>
private class MyAdapter2 extends RecyclerView.Adapter<ViewHolder2> implements IDataSwapper<Item>
</pre>


`AsyncLoader` return the `PageData` inculde data list<T>, pagemore, load result success or false. Call `LoadHelper.onLoadEnd` to pass the load result data.  
数据加载接口返回`PageData` 数据，主要包含数据List, 是否有更多pageMore,以及当前加载是否成功. 数据加载完毕时调用`LoadHelper.onLoadEnd`传入数据加载结果

		public final class PageData<DT> {
   			private boolean success = true;
 			private List<DT> data;
    		private boolean pageMore;
   			private int pageIndex;

`AsyncLoader` implements without lambda  
`AsyncLoader` 使用非lambda表达式实现


		.setAsyncLoader(new LoadMoreHelper.AsyncDataLoader<Item>() {
                    @Override
                    public void startLoadData(int pageIndex, PageData<Item> lastPageData) {
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
                })