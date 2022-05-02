package com.connect.easypestcontrol.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.connect.easypestcontrol.R;
import com.connect.easypestcontrol.controller.TabSelectedAdapter;
import com.google.android.material.tabs.TabLayout;

public class FragmentServicePayment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    TextView title;
    ViewPager viewPager;
    TabLayout tabLayout;
    View view;
    SwipeRefreshLayout swipe_layout;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view=inflater.inflate(R.layout.fragment_service_payment,container,false);
        view=inflater.inflate(R.layout.fragment_task,container,false);
        title=view.findViewById(R.id.title_txt);
        title.setText("Task Completed");
        viewPager=view.findViewById(R.id.view_pager);
        tabLayout=view.findViewById(R.id.tablayout);
        swipe_layout=view.findViewById(R.id.swipelayout);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        swipe_layout.setRefreshing(false);
        TabSelectedAdapter selectedAdapter=new TabSelectedAdapter(getChildFragmentManager());
        selectedAdapter.addFragments(new FragmentTodayPayment("todayCompleted"),"Today");
        selectedAdapter.addFragments(new FragmentTodayPayment("weekCompleted"),"Week");
        selectedAdapter.addFragments(new FragmentTodayPayment("monthCompleted"),"Month");
        viewPager.setAdapter(selectedAdapter);

    }

    @Override
    public void onRefresh() {
        setUpViewPager(viewPager);
    }
}
