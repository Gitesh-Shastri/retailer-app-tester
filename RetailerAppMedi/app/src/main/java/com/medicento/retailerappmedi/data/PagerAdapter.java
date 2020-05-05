package com.medicento.retailerappmedi.data;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;


import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter{
    ArrayList<Fragment> frames = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragements (Fragment fragment, String tabTitles) {
        this.frames.add(fragment);
        this.tabTitles.add(tabTitles);
    }


    @Override
    public Fragment getItem(int position) {
        return frames.get(position);
    }

    @Override
    public int getCount() {
        return frames.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }
}
