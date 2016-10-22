package com.pekingopera.oa.activity;

import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.NoticeListFragment;

/**
 * Created by wayne on 10/1/2016.
 */

public class NoticeListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new NoticeListFragment();
    }
}
