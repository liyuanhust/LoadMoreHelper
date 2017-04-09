package com.lain.loadmorehelper;

import java.util.List;

/**
 * Created by liyuan on 16/12/19.
 */

public interface ISimpleDataSwapper<VM> {
    /**
     * Swap all datas
     */
    void swapData(List<? extends VM> list);


    /**
     * Append data to the end of current list
     */
    void appendData(List<? extends VM> list);
}
