package com.android.focusonme.Activity;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.focusonme.Adapter.ViewPagerAdapter;
import com.android.focusonme.Fragments.AppAnalysisFragment;
import com.android.focusonme.Fragments.WebAnalysisFragment;
import com.android.focusonme.R;
import com.google.android.material.tabs.TabLayout;

public class AnalysisActivity extends AppCompatActivity {

    Button b;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        b=findViewById(R.id.b12350);

        tabLayout = findViewById(R.id.tablayout_ana);
        viewPager = findViewById(R.id.viewPager_ana);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new AppAnalysisFragment(),"APPS");
        viewPagerAdapter.addFragment(new WebAnalysisFragment(),"WEBSITES");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        b.setOnClickListener(view -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}