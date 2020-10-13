package dev.lonami.klooni.adcache;

import android.content.Context;


public class InterstitialAdsManager {
    private static InterstitialAdsManager mInstance;
    private int mAdsShowCount = 0;
    private Context mContext;

    public static InterstitialAdsManager getInstance() {
        if (mInstance == null) {
            synchronized (InterstitialAdsManager.class) {
                if (mInstance == null) {
                    mInstance = new InterstitialAdsManager();
                }
            }
        }
        return mInstance;
    }

    public boolean showAds(String fref) {
        return showGoogleInterstitialAd(fref) ;
    }

    private boolean showGoogleInterstitialAd(String fref) {
        boolean bShow = GoogleInterstitialAdsPool.showAd(fref);
        if (bShow) {
            mAdsShowCount++;
        }
        return bShow;
    }

    public void init(Context ctx) {
        mContext = ctx;
        GoogleInterstitialAdsPool.init(ctx);
    }
}
