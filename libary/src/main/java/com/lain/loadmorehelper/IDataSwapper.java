package com.lain.loadmorehelper;

import java.util.List;

/**
 * Created by liyuan on 16/12/19.
 */

public interface IDataSwapper<DT> {
    /**
     * Swap all datas
     */
    void swapData(List<? extends DT> list);
    /**
     * Append data to the end of current list
     */
    void appendData(List<? extends DT> list);
}
