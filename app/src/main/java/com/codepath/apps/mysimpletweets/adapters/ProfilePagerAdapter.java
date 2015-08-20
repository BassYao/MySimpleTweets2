package com.codepath.apps.mysimpletweets.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.codepath.apps.mysimpletweets.activities.TimelineFragment;

import java.util.ArrayList;

/**
 * Created by bass on 2015/8/20.
 */
public class ProfilePagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tweets", "Following", "Followers" };
    ArrayList<Integer> arrCount;
    SparseArray<Fragment> registeredFragments;
    long mUid = 0;
    public ProfilePagerAdapter(FragmentManager fm, ArrayList<Integer> counts, long uid) {
        super(fm);
        arrCount = counts;
        mUid = uid;
        registeredFragments = new SparseArray<>();

    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = TimelineFragment.newInstance(position, mUid);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];// + "(" + arrCount.get(position).toString()+")";
    }

    public Fragment getRegisteredFragment(int position) { return registeredFragments.get(position); }

}
