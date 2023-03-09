package com.bing.test2.view;

import static androidx.lifecycle.Lifecycle.Event.ON_DESTROY;
import static androidx.lifecycle.Lifecycle.Event.ON_PAUSE;
import static androidx.lifecycle.Lifecycle.Event.ON_RESUME;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class BannerViewPager extends ViewPager implements LifecycleObserver {
    private Handler handler = new Handler();
    private boolean autoPlay = true;
    private int currentItem = 0;
    private int duration = 5000;

    public BannerViewPager(@NonNull Context context) {
        this(context, null);
    }

    public BannerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ((LifecycleOwner) context).getLifecycle().addObserver(this);
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (autoPlay) {
                PagerAdapter pagerAdapter = getAdapter();
                if (pagerAdapter == null) {
                    return;
                }
                currentItem = getCurrentItem();
                if (currentItem == pagerAdapter.getCount() - 1) {
                    currentItem = 0;   //为了循环所以变为0
                }
                currentItem++;
                setCurrentItem(currentItem);
                handler.postDelayed(this, duration);
            } else {
                handler.postDelayed(this, duration);
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            startPlay();
        } else if (action == MotionEvent.ACTION_DOWN) {
            stopPlay();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void startPlay() {
        handler.postDelayed(runnable, duration);
    }

    private void stopPlay() {
        handler.removeCallbacks(runnable);
    }

    @OnLifecycleEvent(ON_RESUME)
    void onResume() {
        startPlay();
    }

    @OnLifecycleEvent(ON_PAUSE)
    void onPause() {
        stopPlay();
    }

    @OnLifecycleEvent(ON_DESTROY)
    void onDestroy() {
        stopPlay();
        handler.removeCallbacksAndMessages(null);
    }
}
