package com.thisisswitch.storelove.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewFragmentAdapter extends FragmentPagerAdapter {
	
    //protected static final String[] CONTENT = new String[] { "This", "Is", "A", "Test", };
    //private int mCount = CONTENT.length;
    
    List<Fragment> fragments = null;
    public ViewFragmentAdapter(FragmentManager fm,List<Fragment>  fragmentsss) {
        super(fm);
        this.fragments = fragmentsss;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            //mCount = count;
            notifyDataSetChanged();
        }
    }
}