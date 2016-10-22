package com.pekingopera.oa.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.pekingopera.oa.fragment.CalendarListFragment;

/**
 * Created by Gordon Wong on 7/17/2015.
 *
 * Pager adapter for main activity.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

	public static final int NUM_ITEMS = 2;
	public static final int ALL_POS = 0;
	public static final int SHARED_POS = 1;

	private Context context;

	public MainPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
		case ALL_POS:
			return new CalendarListFragment();
		case SHARED_POS:
			return new CalendarListFragment();
		default:
			return null;
		}
	}


	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
		case ALL_POS:
			return "测试1";
		case SHARED_POS:
			return "测试2";
		default:
			return "";
		}
	}

	@Override
	public int getCount() {
		return NUM_ITEMS;
	}
}
