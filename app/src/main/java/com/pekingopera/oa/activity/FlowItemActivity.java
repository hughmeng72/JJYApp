package com.pekingopera.oa.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.FlowItemFragment;

/**
 * Created by wayne on 10/5/2016.
 */
public class FlowItemActivity extends BaseActivity {
    private static final String EXTRA_FLOW_ID = "com.pekingopera.oa.flow_id";

    public static Intent newIntent(Context context, int flowId) {
        Intent intent = new Intent(context, FlowItemActivity.class);
        intent.putExtra(EXTRA_FLOW_ID, flowId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        int flowId = getIntent().getIntExtra(EXTRA_FLOW_ID, -1);

        return FlowItemFragment.newInstance(flowId);
    }
}
