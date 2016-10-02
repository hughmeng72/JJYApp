package com.pekingopera.oa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.pekingopera.oa.common.IPager;
import com.pekingopera.oa.common.PagerItemLab;

import java.util.List;

public class WebPagerActivity<T extends IPager> extends AppCompatActivity {

    private static final String EXTRA_ITEM_ID = "com.pekingopera.oa.item_id";

    private ViewPager mViewPager;
    private List<T> mItems;

    public static Intent newIntent(Context packageContext, int itemId) {
        Intent intent = new Intent(packageContext, WebPagerActivity.class);
        intent.putExtra(EXTRA_ITEM_ID, itemId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_pager);

        int itemId = getIntent().getIntExtra(EXTRA_ITEM_ID, -1);

        mViewPager = (ViewPager) findViewById(R.id.activity_web_pager_view_pager);

        mItems = PagerItemLab.get(this).getItems();

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                T item = mItems.get(position);
                return WebPageFragment.newInstance(item.getUri());
            }

            @Override
            public int getCount() {
                return mItems.size();
            }
        });

        for (int i = 0; i < mItems.size(); i++) {
            if (mItems.get(i).getId() == itemId) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
