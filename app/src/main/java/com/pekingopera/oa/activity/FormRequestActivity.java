package com.pekingopera.oa.activity;

import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.FormRequestFragment;
import com.pekingopera.oa.fragment.GovListFragment;

/**
 * Created by wayne on 10/7/2016.
 */
public class FormRequestActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new FormRequestFragment();
    }
}