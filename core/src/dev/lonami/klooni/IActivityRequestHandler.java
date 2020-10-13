package dev.lonami.klooni;

public interface IActivityRequestHandler {

//    void showBannerAds(boolean isTop, boolean isBottom);

    void showInterstitial();

    void loadInterstitial();

    void showBanner();

    void hideBanner();
//    boolean showVideoAd(boolean isRewarded);

}
