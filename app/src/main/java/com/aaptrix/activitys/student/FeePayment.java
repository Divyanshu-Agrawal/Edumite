package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aaptrix.R;
import com.aaptrix.fragments.FeeStructure;
import com.aaptrix.fragments.PaymentHistory;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class FeePayment extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    ViewPager viewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        viewPager = findViewById(R.id.contacts_viewpager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Fee Structure"));
        tabLayout.addTab(tabLayout.newTab().setText("Payment History"));

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        tabLayout.setBackgroundColor(Color.parseColor(selToolColor));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(selToolColor));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor(selToolColor));

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        tabLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        tabLayout.setTabTextColors(Color.parseColor(selToolColor), Color.parseColor(selStatusColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new FeeStructure();
                case 1:
                    return new PaymentHistory();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "Fee Structure";
            } else if (position == 1) {
                title = "Payment History";
            }
            return title;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
