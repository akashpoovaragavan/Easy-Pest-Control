package com.connect.easypestcontrol.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class TabSelectedAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList=new ArrayList<>();
    private List<String> titlelist=new ArrayList<>();
    public TabSelectedAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }
    public void addFragments(Fragment fragment,String title){
        fragmentList.add(fragment);
        titlelist.add(title);
    }
}
