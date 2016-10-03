package com.pekingopera.oa;

import android.support.v4.app.Fragment;

/**
 * Created by wayne on 10/3/2016.
 */
public class CalendarListActivity extends BaseActivity {
    @Override
    protected Fragment createFragment() {
        return new CalendarListFragment();
    }
}
