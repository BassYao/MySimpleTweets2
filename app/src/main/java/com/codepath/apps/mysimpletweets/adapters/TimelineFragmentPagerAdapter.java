package com.codepath.apps.mysimpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.codepath.apps.mysimpletweets.activities.TimelineFragment;

/**
 * Created by bass on 2015/8/20.
 */
public class TimelineFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Home", "Mentions" };
    SparseArray<Fragment> registeredFragments;

    public TimelineFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        registeredFragments = new SparseArray<>();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = TimelineFragment.newInstance(position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    public Fragment getRegisteredFragment(int position) { return registeredFragments.get(position); }


}