package com.lain.loadmoretest.data;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.WorkerThread;

import com.lain.loadmorehelper.PageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by liyuan on 17/4/6.
 */

public class DataLoader {
    private static Executor sExecutor = Executors.newSingleThreadExecutor();
    private static Handler sUiHandler = new Handler(Looper.getMainLooper());

    /**
     * Load data sync
     *
     * @param page
     * @param param
     * @return
     */
    @WorkerThread
    public static BaseResult<Item> loadDataSync(int page, Object param) {
        Random r = new Random();
        //load data cost time
        try {
            Thread.sleep((r.nextInt(2) +1) * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //load data failed
        if (r.nextInt(5) == 0) {
            return null;
        }

        int pageSize = 10;
        List<Item> list = new ArrayList<>(pageSize);
        for (int i = 0; i < pageSize; i++) {
            Item item = new Item("Item:" + String.valueOf(pageSize * (page - 1) + i));
            list.add(item);
        }

        boolean more = page <= 5;
        BaseResult<Item> result = new BaseResult<>();
        result.setData(list);
        result.setPageMore(more);


        return result;
    }


    @AnyThread
    public static void loadData(final int page, final Object param, final LoadDataCallback cb) {
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final BaseResult<Item> result = loadDataSync(page, param);
                sUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (cb != null) {
                            if (result != null) {
                                cb.onLoadSuccess(page, result.getData(), result.isPageMore());
                            } else {
                                cb.onLoadFailed(page);
                            }
                        }
                    }
                });
            }
        });
    }


    public static Observable<BaseResult<Item>> loadData(final int page, final Object param) {
        return Observable.just(null).map(new Func1<Object, BaseResult<Item>>() {
            @Override
            public BaseResult<Item> call(Object o) {
                BaseResult<Item> result =  loadDataSync(page, param);
                if (result == null) {
                    throw new RuntimeException("load error");
                }
                return result;
            }
        });
    }

    public interface LoadDataCallback {
        void onLoadSuccess(int page, List<Item> list, boolean pageMore);

        void onLoadFailed(int page);
    }
}
