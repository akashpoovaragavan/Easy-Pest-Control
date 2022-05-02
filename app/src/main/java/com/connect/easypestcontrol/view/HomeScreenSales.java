package com.connect.easypestcontrol.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.connect.easypestcontrol.R;
import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

public class HomeScreenSales extends AppCompatActivity{
    MeowBottomNavigation bottomNavigation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_sales);
        bottomNavigation = findViewById(R.id.bottomnavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_leads));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_converted));
        bottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_follow_up));
        bottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.ic_assign));
        bottomNavigation.add(new MeowBottomNavigation.Model(6,R.drawable.ic_payment));
        bottomNavigation.setOnShowListener(item -> {
            Fragment selectedfrag = null;
            switch (item.getId()) {
                case 1:
                    selectedfrag = new FragmentHome();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfrag).commit();
                    break;
                case 2:
                    selectedfrag = new FragmentLeads();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfrag).commit();
                    break;
                case 3:
                    selectedfrag = new FragmentConverted();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfrag).commit();
                    break;
                case 4:
                    selectedfrag = new FragmentFollow();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfrag).commit();
                    break;
                case 5:
                    selectedfrag = new FragmentAssign();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfrag).commit();
                    break;
                case 6:
                    selectedfrag = new FragmentPayment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedfrag).commit();
                    break;
            }
            loadfragment(selectedfrag);
        });
        bottomNavigation.show(1,true);
        bottomNavigation.setOnClickMenuListener(item -> {

        });
        bottomNavigation.setOnReselectListener(item -> {

        });
    }
    private void loadfragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

    }
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}