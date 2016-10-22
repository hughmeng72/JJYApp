package com.pekingopera.oa.activity;

import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.MailListFragment;

/**
 * Created by wayne on 10/3/2016.
 */
public class MailListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new MailListFragment();
    }
}
