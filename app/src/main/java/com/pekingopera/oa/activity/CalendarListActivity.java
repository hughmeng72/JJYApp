package com.pekingopera.oa.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pekingopera.oa.R;
import com.pekingopera.oa.common.PagerItemLab;
import com.pekingopera.oa.fragment.CalendarListFragment;
import com.pekingopera.oa.fragment.NoticeListFragment;
import com.pekingopera.oa.fragment.ShowPlanListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wayne on 10/3/2016.
 */
public class CalendarListActivity extends AppCompatActivity implements ShowPlanListFragment.OnFragmentInteractionListener {
    private ViewPager viewPager;
    private ShowPlanListFragment mPlanListFragment = new ShowPlanListFragment();
    private CalendarListFragment mCalendarListFragment = new CalendarListFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        setTitle("上海京剧院");

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.tab_viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:
                        PagerItemLab.get(CalendarListActivity.this).setItems(mPlanListFragment.getCalendars());
                        break;
                    case 1:
                        PagerItemLab.get(CalendarListActivity.this).setItems(mCalendarListFragment.getCalendars());
                        break;
                    default:
                        // Do nothing yet.
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(mPlanListFragment, "演出计划");
        adapter.addFrag(mCalendarListFragment, "日程表");

//        PagerItemLab.get(this).setItems(mCalendars);

        viewPager.setAdapter(adapter);
    }

    protected Fragment createFragment() {
        return new CalendarListFragment();
    }

    @Override
    public void inAction() {
        PagerItemLab.get(this).setItems(mPlanListFragment.getCalendars());
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
