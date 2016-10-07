
package com.android.gis.huapp;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class AllBuilds extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String SELECTED_ITEM_ID = "selected_item_id";
    private static final String FIRST_TIME = "first_time";
    // Need this to link with the Snackbar
    private CoordinatorLayout mCoordinator;
    //Need this to set the title of the app bar
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private FloatingActionButton mFab;
    private Toolbar mToolbar;
    private LinearLayout mRoot;
    private DrawerLayout mDrawerLayout;
    private NavigationView mDrawer;
    private ActionBarDrawerToggle mDrawerToggle;
    private ViewPager mPager;
    private int mSelectedId;
    private ViewPagerAdapter mAdapter;
    private TabLayout mTabLayout;
    private ImageView imgHeader;
    private boolean mUserSawDrawer = false;
    private Animator mCurrentAnimator;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;

    private View.OnClickListener mSnackBarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private View.OnClickListener mFabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Snackbar.make(mRoot, "FAB Clicked", Snackbar.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allbuilds);
     /*   imgHeader= (ImageView) findViewById(R.id.imgHeader);
        imgHeader.setImageResource(R.drawable.logo3);
        final int[] bgImgs= new int[5];
        int id1=R.drawable.hu_logo;
        int id2=R.drawable.admins;
        int id3=R.drawable.pharmarcy;
        int id4=R.drawable.edma;
        int id5=R.drawable.hu_img;
        bgImgs[0]=id1;
        bgImgs[1]=id2;
        bgImgs[2]=id3;
        bgImgs[3]=id4;
        bgImgs[4]=id5;

        new Runnable() {
            int currentIndex = 0;
            int updateInterval = 3000; //=one second

            @Override
            public void run() {

                //currentIndex += 1;
                int Size=bgImgs.length;
                if(currentIndex == Size){
                    currentIndex = 0;
                }
                imgHeader.postDelayed(this, updateInterval);
                currentIndex += 1;
            }
        }.run();*/





        mCoordinator = (CoordinatorLayout) findViewById(R.id.root_coordinator);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mDrawer = (NavigationView) findViewById(R.id.navigation_drawer);
        mDrawer.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        if (!didUserSeeDrawer()) {
            showDrawer();
            markDrawerSeen();
        } else {
            hideDrawer();
        }
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);


        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 1"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 2"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 3"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab 4"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Tab5"));
        mPager = (ViewPager) findViewById(R.id.view_pager);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(),mTabLayout.getTabCount());

        mPager.setAdapter(mAdapter);
        //Notice how the Tab Layout links with the Pager Adapter
       // mTabLayout.setTabsFromPagerAdapter(mAdapter);

        //Notice how The Tab Layout adn View Pager object are linked
        mTabLayout.setupWithViewPager(mPager);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));


        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        //Notice how the title is set on the Collapsing Toolbar Layout instead of the Toolbar
        //mCollapsingToolbarLayout.setTitle("Univeristy Builds");

    }








    private boolean didUserSeeDrawer() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = sharedPreferences.getBoolean(FIRST_TIME, false);
        return mUserSawDrawer;
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        // TODO Display the navigation drawer icon on action bar when there state has changed
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    private void markDrawerSeen() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mUserSawDrawer = true;
        sharedPreferences.edit().putBoolean(FIRST_TIME, mUserSawDrawer).apply();
    }

    private void showDrawer() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void navigate(int mSelectedId) {
        Intent intent = null;

        if (mSelectedId == R.id.navigation_item_1) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, SearchMap.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_2) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AllBuilds.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_3) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, WeatherToday.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_4) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, News.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_5) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, Notifications.class);
            startActivity(intent);
        }
        if (mSelectedId == R.id.navigation_item_6) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            intent = new Intent(this, AboutUs.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fourth, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        menuItem.setChecked(true);
        mSelectedId = menuItem.getItemId();

        navigate(mSelectedId);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_ITEM_ID, mSelectedId);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



}


