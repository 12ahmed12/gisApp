package com.android.gis.huapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                com.android.gis.huapp.FragmentCollege tab1 = new com.android.gis.huapp.FragmentCollege();
                return tab1;
            case 1:
                com.android.gis.huapp.FragmentShops tab2 = new com.android.gis.huapp.FragmentShops();
                return tab2;
            case 2:
                com.android.gis.huapp.FragmentLibrary tab3 = new com.android.gis.huapp.FragmentLibrary();
                return tab3;
            case 3:
                com.android.gis.huapp.FragmentTheatre tab4 = new com.android.gis.huapp.FragmentTheatre();
                return tab4;
            case 4:
                com.android.gis.huapp.FragmentAdminstrations tab5 = new com.android.gis.huapp.FragmentAdminstrations();
                return tab5;

            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        com.android.gis.huapp.FragmentCollege f = (com.android.gis.huapp.FragmentCollege) object;
        com.android.gis.huapp.FragmentShops f2 = (com.android.gis.huapp.FragmentShops) object;
        com.android.gis.huapp.FragmentLibrary f3 = (com.android.gis.huapp.FragmentLibrary) object;
        com.android.gis.huapp.FragmentTheatre f4=(com.android.gis.huapp.FragmentTheatre) object;
        FragmentAdminstrations f5=(FragmentAdminstrations) object;
        if (f != null) {
            f.update();
        }
        if (f2 != null) {
            f2.update();
        }
        if (f3 != null) {
            f3.update();
        }
        if (f4 != null) {
            f4.update();
        }
        if (f5 != null) {
            f5.update();
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0)
        {
            return "كليات";
        }
        if(position==1)
        {
            return "محلات";
        }
        if(position==2)
        {
            return "مكتبات";
        }
        else if(position==3)
        {
            return "مدرجات";
        }
        else
        {
            return "ادارة";
        }

    }

}