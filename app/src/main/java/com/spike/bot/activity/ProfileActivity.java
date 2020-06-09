package com.spike.bot.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.spike.bot.ChatApplication;
import com.spike.bot.R;
import com.spike.bot.core.Common;
import com.spike.bot.core.Constants;
import com.spike.bot.fragments.UserChildListFragment;
import com.spike.bot.fragments.UserProfileFragment;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    String TAG = "ProfileActivity";
    boolean isFlagUser = false;
    Activity activity;
    TabLayout tabLayout;
    public ViewPager mViewPager;
    public TextView txtVersionCode;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    ArrayList<String> strList = new ArrayList<>();
    MenuItem menuAdd, menuAddSave, menuaddtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity = this;

        mViewPager = (ViewPager) findViewById(R.id.profile_container);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        txtVersionCode = findViewById(R.id.txtVersionCode);
        tabLayout.setupWithViewPager(mViewPager);

        if (Common.getPrefValue(this, Constants.USER_ADMIN_TYPE).equalsIgnoreCase("1")) {
            isFlagUser = true;
        }

        /*add fragment*/
        fragmentList.add(UserProfileFragment.newInstance());
        if (isFlagUser) {
            fragmentList.add(UserChildListFragment.newInstance());
        } else {
            fragmentList.add(UserProfileFragment.newInstance());
        }
        setCustomFont();

        strList.add("User Details");
        strList.add("Child Users");


        SpannableString s = new SpannableString("User Details");
        s.setSpan(new TypefaceSpan( "Axiforma.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString s1 = new SpannableString("Child Users");
        s1.setSpan(new TypefaceSpan( "Axiforma.ttf"), 0, s1.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                //lock second
                if (isFlagUser && menuAdd!=null) {
                    if (position == 1) {
                        menuaddtext.setVisible(true);
                    } else {
                        menuaddtext.setVisible(false);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            txtVersionCode.setText("Version code : " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void setCustomFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();

        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);

            int tabChildsCount = vgTab.getChildCount();

            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "Axiforma.ttf"));
                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_room_edit, menu);
        menuAdd = menu.findItem(R.id.action_add);
        menuAddSave = menu.findItem(R.id.action_save);
        menuaddtext = menu.findItem(R.id.action_add_text);
        menuAddSave.setVisible(false);
        menuAdd.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add_text) {
            Intent intent = new Intent(this, UserChildActivity.class);
            intent.putExtra("modeType", "add");
            startActivityForResult(intent, 1001);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new UserProfileFragment();
            } else if (position == 1) {
                if (isFlagUser) {
                    fragment = new UserChildListFragment();
                } else {
                    fragment = new UserProfileFragment();
                }

            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return strList.get(position);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ChatApplication.logDisplay("activity is " + requestCode + " " + requestCode);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
