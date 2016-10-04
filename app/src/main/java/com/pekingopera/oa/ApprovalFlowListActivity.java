package com.pekingopera.oa;

import android.support.v4.app.Fragment;

/**
 * Created by wayne on 10/5/2016.
 */
public class ApprovalFlowListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new ApprovalFlowListFragment();
    }
}
