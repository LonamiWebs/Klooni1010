package dev.lonami.klooni;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import dev.lonami.klooni.actors.SoftButton;
import dev.lonami.klooni.game.Board;
import dev.lonami.klooni.screens.GameScreen;

public interface IActivityRequestHandler {

    //    void showBannerAds(boolean isTop, boolean isBottom);
    void showInterstitial();

    void removeAd(Table table, SoftButton softButton);

    boolean isAdAvaliable();

    void inAppReview();

    void loadRewardAd();

    void showRewardAd(final SoftButton customButton, final Board board, final GameScreen gameScreen, final ChangeListener customChangeListener, final Klooni game, final String reason);

    void showToast(String msg);
}
