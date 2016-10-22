package com.pekingopera.oa.activity;

import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.CalendarFragment;
import com.pekingopera.oa.fragment.CalendarListFragment;

/**
 * Created by wayne on 10/3/2016.
 */
public class CalendarListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new CalendarListFragment();
//        return new CalendarFragment();
    }
}
