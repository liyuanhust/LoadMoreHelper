package com.lain.loadmorehelper;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liyuan on 16/12/20.
 */

public interface ILoadViewCreator<DT> {

    View createView(ViewGroup parent, LoadMoreHelper<DT> loadHelper);
}
