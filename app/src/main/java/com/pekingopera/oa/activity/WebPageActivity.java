package com.pekingopera.oa.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.pekingopera.oa.fragment.WebPageFragment;

/**
 * Created by wayne on 10/2/2016.
 */
public class WebPageActivity extends BaseActivity {
    public static Intent newIntent(Context context, Uri uri) {
        Intent i = new Intent(context, WebPageActivity.class);
        i.setData(uri);

        return i;
    }

    @Override
    protected Fragment createFragment() {
        return WebPageFragment.newInstance(getIntent().getData());
    }
}
