package com.lain.loadmoretest.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.lain.loadmoretest.R;

/**
 * Created by liyuan on 16/11/9.
 * Activity contains one main fragment
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    private static final String TAG_CONTENT = "content";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        if (savedInstanceState == null) {
            Fragment frag = createFragment();
            Intent intent = getIntent();
            frag.setArguments(intent.getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, frag, TAG_CONTENT).commit();
        }
    }


    @Override
    public void onBackPressed() {
        boolean handled = false;
        Fragment fragment = getContentFragment();
        if (fragment instanceof IBackPressedListener) {
            handled = ((IBackPressedListener)fragment).handlerBackPress();
        }
        if (!handled) {
            super.onBackPressed();
        }
    }

    protected abstract Fragment createFragment();

    public Fragment getContentFragment() {
        return getSupportFragmentManager().findFragmentByTag(TAG_CONTENT);
    }

}
