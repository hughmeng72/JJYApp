package com.pekingopera.oa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements LoginFragment.OnFragmentInteractionListener, MainFragment.OnFragmentInteractionListener {

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onLoginValiated() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment oldFragment = fm.findFragmentById(R.id.fragmentContainer);
        if (null != oldFragment) {
            ft.remove(oldFragment);
        }

        Fragment newFragment = new MainFragment();
        ft.add(R.id.fragmentContainer, newFragment);

        ft.commit();
    }

    @Override
    public void inAction(View v) {
        Toast.makeText(this, v.getTag().toString() + " clicked.", Toast.LENGTH_SHORT).show();
    }
}
