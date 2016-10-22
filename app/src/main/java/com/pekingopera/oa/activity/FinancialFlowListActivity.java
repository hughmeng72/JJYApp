package com.pekingopera.oa.activity;

import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.FinancialFlowListFragment;

/**
 * Created by wayne on 10/7/2016.
 */
public class FinancialFlowListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new FinancialFlowListFragment();
    }
}
