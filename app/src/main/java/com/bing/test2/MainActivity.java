package com.bing.test2;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bing.test2.adapter.BannerAdapter;
import com.bing.test2.adapter.ImageAdapter;
import com.bing.test2.bean.DataBean;
import com.bing.test2.view.BannerViewPager;
import com.youth.banner.Banner;
import com.youth.banner.listener.OnBannerListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String str = "1,2";
        String[] split = str.split(",");
        int[] array = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Integer.parseInt(split[i]);
        }
        Banner banner = findViewById(R.id.banner);
        ImageAdapter adapter = new ImageAdapter(DataBean.getTestData3());
        banner.setAdapter(adapter).addBannerLifecycleObserver(this).setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(Object data, int position) {
                Log.d("hanbing", "position=" + position);
            }
        });
        ((BannerViewPager) findViewById(R.id.bannerViewPager)).setAdapter(new BannerAdapter(DataBean.getTestData3()));
         findViewById(R.id.helloWorld).setOnClickListener(view -> {
                startActivity(new Intent(this,TestActivity.class));
         });
        Button button1 = findViewById(R.id.bt1);

        findViewById(R.id.bt2).setOnClickListener(view -> {
            button1.setVisibility(View.GONE);
         });


    }
}
