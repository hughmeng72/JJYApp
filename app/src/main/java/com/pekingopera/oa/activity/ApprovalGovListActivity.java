package com.pekingopera.oa.activity;

import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.ApprovalGovListFragment;

/**
 * Created by wayne on 10/7/2016.
 */
public class ApprovalGovListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new ApprovalGovListFragment();
    }
}
