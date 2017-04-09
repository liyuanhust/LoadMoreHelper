package com.lain.loadmorehelper.list;

import android.support.annotation.IntDef;

import com.lain.loadmorehelper.LoadController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by liyuan on 17/4/4.
 */

public interface IListWrapper {
    public static final int STATE_INITIAL = 0;
    public static final int STATE_LOAD_MORE = 1;
    public static final int STATE_LOAD_FAILED = 2;
    public static final int STATE_LOAD_COMPLETE = 3;

    void init(LoadController<?> loadController);

    void setState(@ListState int curState);

    @IntDef({STATE_INITIAL, STATE_LOAD_MORE, STATE_LOAD_FAILED, STATE_LOAD_COMPLETE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ListState {

    }

}
