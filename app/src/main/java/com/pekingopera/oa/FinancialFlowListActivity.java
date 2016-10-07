package com.pekingopera.oa;

import android.support.v4.app.Fragment;

/**
 * Created by wayne on 10/7/2016.
 */
public class FinancialFlowListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new FinancialFlowListFragment();
    }
}
