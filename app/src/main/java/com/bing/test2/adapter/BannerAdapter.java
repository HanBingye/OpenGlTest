package com.bing.test2.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager.widget.PagerAdapter;

import com.bing.test2.bean.DataBean;
import com.bumptech.glide.Glide;

import java.util.List;

public class BannerAdapter extends PagerAdapter {


    private List<DataBean> data;
    public static final int mLooperCount = 500;


    public BannerAdapter(List<DataBean> data) {
        this.data = data;
    }


    @Override
    public int getCount() {
        return getRealCount() * mLooperCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        int realPosition = position % getRealCount();
        ImageView imageView = new ImageView(container.getContext());
        Glide.with(imageView).load(data.get(realPosition).imageUrl).into(imageView);
        container.addView(imageView);
        Log.d("hanbing", "position="+realPosition);
        return imageView;
    }


    private int getRealCount() {
        return data == null ? 0 : data.size();
    }
}
