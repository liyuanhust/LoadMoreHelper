package com.lain.loadmoretest.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class SimpleActivity extends SingleFragmentActivity {
    private static final String EXTRA_INPUT_FRAGMENT_LCASS = "input_frag_class";

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        try {
            Class<? extends Fragment> fragCls = (Class<? extends Fragment>)intent.getSerializableExtra(EXTRA_INPUT_FRAGMENT_LCASS);
            return fragCls.newInstance();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Intent getStartIntent(Context ctx, Class<? extends Fragment> cls) {
        Intent intent = new Intent(ctx, SimpleActivity.class);
        intent.putExtra(EXTRA_INPUT_FRAGMENT_LCASS, cls);
        return intent;
    }


    public static Intent getStartIntent(Context ctx, Class<? extends Fragment> cls, Bundle bundle) {
        Intent intent = new Intent(ctx, SimpleActivity.class);
        intent.putExtra(EXTRA_INPUT_FRAGMENT_LCASS, cls);
        intent.putExtras(bundle);
        return intent;
    }
}
