package com.pekingopera.oa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.dou361.update.UpdateHelper;
import com.dou361.update.listener.ForceListener;

public abstract class BaseActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }

        checkUpdate();
    }

    private void checkUpdate() {
        UpdateHelper.getInstance().setForceListener(new ForceListener() {
            @Override
            public void onUserCancel(boolean force) {
                if (force) {
                    finish();
                }
            }
        }).check(this);
    }
}
