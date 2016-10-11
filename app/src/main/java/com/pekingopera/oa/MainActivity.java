package com.pekingopera.oa;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.dou361.update.UpdateHelper;

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
        Intent intent;

        switch (v.getTag().toString()) {
            case "flow":
                intent = new Intent(this, ApprovalFlowListActivity.class);
                startActivity(intent);
                break;
            case "flow2Inquiry":
                intent = new Intent(this, FinancialFlowListActivity.class);
                startActivity(intent);
                break;
            case "flowInquiry":
                intent = new Intent(this, GeneralFlowListActivity.class);
                startActivity(intent);
                break;
            case "doc":
                intent = new Intent(this, ApprovalGovListActivity.class);
                startActivity(intent);
                break;
            case "docInquiry":
                intent = new Intent(this, GovListActivity.class);
                startActivity(intent);
                break;
            case "calendar":
                intent = new Intent(this, CalendarListActivity.class);
                startActivity(intent);
                break;
            case "notice":
                intent = new Intent(this, NoticeListActivity.class);
                startActivity(intent);
                break;
            case "mail":
                intent = new Intent(this, MailListActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, v.getTag().toString() + " clicked.", Toast.LENGTH_SHORT).show();
        }
    }
}