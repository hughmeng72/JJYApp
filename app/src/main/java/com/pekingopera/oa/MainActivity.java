package com.pekingopera.oa;

import android.support.v4.app.Fragment;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements LoginFragment.OnFragmentInteractionListener {

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    @Override
    public void onLoginValiated() {
        Toast.makeText(this, "主程序收到登录请求", Toast.LENGTH_SHORT).show();
    }
}
