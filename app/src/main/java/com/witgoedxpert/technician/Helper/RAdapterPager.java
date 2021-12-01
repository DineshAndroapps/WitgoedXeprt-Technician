package com.witgoedxpert.technician.Helper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import android.content.res.Resources;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.witgoedxpert.technician.Fragments.Pending_F;

public class RAdapterPager  extends FragmentPagerAdapter {
    private final Resources resources;

    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


    public RAdapterPager(final Resources resources, FragmentManager fm) {
        super(fm);
        this.resources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fm=null;
        switch (position) {

            case 0:
                fm = new Pending_F();
                break;
            case 1:
                fm = new Pending_F();
                break;
            case 2:
                fm = new Pending_F();
                break;

        }
        return fm;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(final int position) {

        return null;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }


    /**
     * Get the Fragment by position
     *
     * @param position tab position of the fragment
     * @return
     */
    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

}


