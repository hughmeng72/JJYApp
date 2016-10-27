package com.pekingopera.oa;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import com.pekingopera.oa.activity.ApprovalFlowListActivity;
import com.pekingopera.oa.activity.ApprovalGovListActivity;
import com.pekingopera.oa.activity.BaseActivity;
import com.pekingopera.oa.activity.CalendarListActivity;
import com.pekingopera.oa.activity.FinancialFlowListActivity;
import com.pekingopera.oa.activity.FormRequestActivity;
import com.pekingopera.oa.activity.GeneralFlowListActivity;
import com.pekingopera.oa.activity.GovListActivity;
import com.pekingopera.oa.activity.MailListActivity;
import com.pekingopera.oa.activity.MainFragment;
import com.pekingopera.oa.activity.NoticeListActivity;
import com.pekingopera.oa.fragment.LoginFragment;
import com.pekingopera.oa.R;

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
            case "others":
                intent = new Intent(this, FormRequestActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, v.getTag().toString() + " clicked.", Toast.LENGTH_SHORT).show();
        }
    }
}