package dev.lonami.klooni.adcache;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.lonami.klooni.Klooni;


public class GoogleInterstitialAdsPool {
    private static final Map<String, InterstitialAd> adsIdlePool = new HashMap<>();
    private static final Map<String, InterstitialAd> adsLoadingPool = new HashMap<>();
    private static final ArrayList<InterstitialAd> adsLoadedPool = new ArrayList<>();
    private static Context context;
    private static final String TAG = "GoogleInterstitialAds";

    public static void init(Context ctx) {
        context = ctx;
        if (!Klooni.getIsRemove()) {
            add("ca-app-pub-3940256099942544/1033173712");
            add("ca-app-pub-3940256099942544/1033173712");
        }
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
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.i(TAG, "onAdLoaded: " + ad.getAdUnitId());
                adsLoadingPool.remove(ad.getAdUnitId());
                adsLoadedPool.add(ad);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError i) { // 加载失败
                super.onAdFailedToLoad(i);
                Log.e(TAG, "onAdFailedToLoad: " + ad.getAdUnitId());
                Bundle bundleEvent = new Bundle();
                bundleEvent.putString("errormsg", ad.getAdUnitId() + " errorcode:" + i);
                bundleEvent.putString("type", "onGoogleAdFailedToLoad");
                FirebaseAnalytics.getInstance(context).logEvent("onAdFailedToLoad", bundleEvent);
                adsLoadingPool.remove(ad.getAdUnitId());
                add(ad.getAdUnitId(), false);
            }
        });
        adsIdlePool.put(adKey, ad);
        if (bFireLoadEvent) {
            fire2LoadAd();
        }
    }

    private static void fire2LoadAd() {
        for (Map.Entry<String, InterstitialAd> entry : adsIdlePool.entrySet()) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("5EB85F7D497B2DF45A4F3846A535BB13")
                    .build();
            entry.getValue().loadAd(adRequest);
            adsLoadingPool.put(entry.getKey(), entry.getValue());
        }
        adsIdlePool.clear();
    }

    public static boolean showAd(String fref) {
        Bundle bundleEvent = new Bundle();
        if (adsLoadedPool.isEmpty()) {
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
