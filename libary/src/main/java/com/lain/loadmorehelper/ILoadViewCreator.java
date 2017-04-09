package com.lain.loadmorehelper;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liyuan on 16/12/20.
 */

public interface ILoadViewCreator<VM> {

    View createView(ViewGroup parent, LoadMoreHelper<VM> loadHelper);
}
