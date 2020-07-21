package com.social.adpaters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.social.frgaments.Followings;
import com.social.frgaments.Home;
import com.social.frgaments.Mypost;
import com.social.frgaments.Tags;

public class Profile_tabadpater extends FragmentPagerAdapter {
    Context context;
    int totalTabs;
    public Profile_tabadpater(Context c, FragmentManager fm, int totalTabs) {
        super(fm);
        context = c;
        this.totalTabs = totalTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Mypost mypost = new Mypost();
                return mypost;
            case 1:
                Tags tags = new Tags();
                return tags;
            case 2:
                Followings followings = new Followings();
                return followings;
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return totalTabs;
    }
    @Override
    public CharSequence getPageTitle(int position) {

        // return null to display only the icon
        return null;
    }
}