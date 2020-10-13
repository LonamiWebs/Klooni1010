package dev.lonami.klooni.adcache;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GoogleInterstitialAdsPool {
    private static Map<String, InterstitialAd> adsIdlePool = new HashMap<>();
    private static Map<String, InterstitialAd> adsLoadingPool = new HashMap<>();
    private static ArrayList<InterstitialAd> adsLoadedPool = new ArrayList<>();
    private static Context context;
    private static final String TAG = "GoogleInterstitialAds";

    public static void init(Context ctx) {
        context = ctx;
        // 缓存一些广告
        add("ca-app-pub-3241270777052923/1648334180");
        add("ca-app-pub-3241270777052923/7752462127");
        add("ca-app-pub-3241270777052923/9626094225");
    }

    private static void add(String adKey) {
        add(adKey, true);
    }

    public static void add(String adKey, boolean bFireLoadEvent) {
        // 创建广告对象
        final InterstitialAd ad = new InterstitialAd(context);
        ad.setAdUnitId(adKey);
        ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.i(TAG, "onAdClosed: " + ad.getAdUnitId());
                add(ad.getAdUnitId());
            }

            @Override
            public void onAdLoaded() { // 广告加载完成了，放入到完成map中
                super.onAdLoaded();
                Log.i(TAG, "onAdLoaded: " + ad.getAdUnitId());
                // 从加载中map中移除
                adsLoadingPool.remove(ad.getAdUnitId());
                // 添加到加载完成队列中
                adsLoadedPool.add(ad);
            }

            @Override
            public void onAdFailedToLoad(int i) { // 加载失败
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad: " + ad.getAdUnitId());
                // 做统计
                Bundle bundleEvent = new Bundle();
                bundleEvent.putString("errormsg", ad.getAdUnitId() + " errorcode:" + i);
                bundleEvent.putString("type", "onGoogleAdFailedToLoad");
                FirebaseAnalytics.getInstance(context).logEvent("onAdFailedToLoad", bundleEvent);
                // 从加载中map中移除
                adsLoadingPool.remove(ad.getAdUnitId());
                add(ad.getAdUnitId(), false);
            }
        });
        // 加入到空闲广告map中
        adsIdlePool.put(adKey, ad);
        // 判断要不要激活加载事件
        if (bFireLoadEvent) {
            fire2LoadAd();
        }
    }

    private static void fire2LoadAd() {
        for (Map.Entry<String, InterstitialAd> entry : adsIdlePool.entrySet()) {
            // 加载广告
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("5EB85F7D497B2DF45A4F3846A535BB13")
                    .build();
            entry.getValue().loadAd(adRequest);
            adsLoadingPool.put(entry.getKey(), entry.getValue());
        }
        // 清空待加载列表
        adsIdlePool.clear();
    }

    // 显示广告
    public static boolean showAd(String fref) {
        Bundle bundleEvent = new Bundle();
        if (adsLoadedPool.isEmpty()) // 如果已加载列表为空
        {
            fire2LoadAd();
            bundleEvent.putString("type", "NoCacheGoogleAd");
            FirebaseAnalytics.getInstance(context).logEvent("NoCacheAd", bundleEvent);
            return false;
        }
        bundleEvent.putString("type", "ShowGoogleAd");
        bundleEvent.putString("fref", fref);
        FirebaseAnalytics.getInstance(context).logEvent("ShowAd", bundleEvent);
        InterstitialAd ad = adsLoadedPool.remove(0);
        Log.i(TAG, "show Ad : " + ad.getAdUnitId());
        ad.show();
        return true;
    }
}
