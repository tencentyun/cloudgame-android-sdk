package com.tencent.tcrdemo.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class SimplePagerAdapter extends PagerAdapter {
    private final List<View> mViews = new ArrayList<>();

    public SimplePagerAdapter(ViewPager viewPager) {
        View child;
        do {
            child = viewPager.getChildAt(0);
            if (child != null) {
                mViews.add(child);
                viewPager.removeViewAt(0);
            }
        } while (child != null);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    public View getView(int pos) {
        return mViews.get(pos);
    }
    
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return object == view;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View child = mViews.get(position);
        container.addView(child);
        return child;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}
