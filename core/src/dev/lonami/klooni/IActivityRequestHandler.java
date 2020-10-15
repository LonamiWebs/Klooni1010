package dev.lonami.klooni;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import dev.lonami.klooni.actors.SoftButton;

public interface IActivityRequestHandler {

    //    void showBannerAds(boolean isTop, boolean isBottom);
    void showInterstitial();


    void removeAd(Table table, SoftButton softButton);

    boolean isAdAvaliable();

    void inAppReview();
}
