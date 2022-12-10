package com.android.focusonme.Activity;

import android.annotation.SuppressLint;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.focusonme.Adapter.AppList;
import com.android.focusonme.Fragments.HomeFragment;
import com.android.focusonme.Fragments.ProfileFragment;
import com.android.focusonme.Fragments.UsageFragment;
import com.android.focusonme.R;
import com.android.focusonme.Utility.GetUsageInfo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Button b;
    HomeFragment firstFragment = new HomeFragment(this);
    UsageFragment secondFragment = new UsageFragment();
    ProfileFragment thirdFragment = new ProfileFragment();
    FragmentManager fm = getSupportFragmentManager();
    Fragment active = firstFragment;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationMenu = findViewById(R.id.nav_menu);
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm.beginTransaction().add(R.id.flFragment, thirdFragment, "3").hide(thirdFragment).commit();
        fm.beginTransaction().add(R.id.flFragment, secondFragment, "2").hide(secondFragment).commit();
        fm.beginTransaction().add(R.id.flFragment, firstFragment, "1").commit();
        b = findViewById(R.id.nav_button);
        b.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        navigationMenu.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu1:
                    startActivity(new Intent(this, SettingActivity.class));
                    break;

                case R.id.menu2:
                    startActivity(new Intent(MainActivity.this, AnalysisActivity.class));
                    break;

                case R.id.menu4:
                    startActivity(new Intent(MainActivity.this, ParentalControlActivity.class));
                    break;

                case R.id.menu5:
                    startActivity(new Intent(MainActivity.this, AboutActivity.class));
                    break;
            }
            return false;
        });
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.home:
                fm.beginTransaction().hide(active).show(firstFragment).commit();
                active = firstFragment;
                return true;

            case R.id.usage:
                fm.beginTransaction().hide(active).show(secondFragment).commit();
                active = secondFragment;
                return true;

        }
        return false;
    };

    public ArrayList<AppList> getInstalledApps() {
        ArrayList<AppList> apps = new ArrayList<>();
        @SuppressLint("QueryPermissionsNeeded") List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            PackageManager packageManager=getPackageManager();
            if (packageManager.getLaunchIntentForPackage(p.packageName)!=null) {
                String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
                Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
                String packageName=p.applicationInfo.packageName;
                apps.add(new AppList(appName, icon, packageName));
            }
        }
        Collections.sort(apps, new Comparator<AppList>() {
            @Override
            public int compare(AppList o1, AppList o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return apps;
    }

    public void showHelpUsage(View view){
        Toast.makeText(this,"Click The App", Toast.LENGTH_SHORT).show();
    }
}