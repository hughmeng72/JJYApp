package com.pekingopera.oa;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by wayne on 10/5/2016.
 */
public class GovItemActivity extends BaseActivity {
    private static final String EXTRA_GOV_ID = "com.pekingopera.oa.gov_id";

    public static Intent newIntent(Context context, int govId) {
        Intent intent = new Intent(context, GovItemActivity.class);
        intent.putExtra(EXTRA_GOV_ID, govId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        int flowId = getIntent().getIntExtra(EXTRA_GOV_ID, -1);

        return GovItemFragment.newInstance(flowId);
    }
}
