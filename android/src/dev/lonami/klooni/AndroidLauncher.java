/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017-2019  Lonami Exo @ lonami.dev

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package dev.lonami.klooni;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.reflect.Method;
import java.util.Arrays;

import dev.lonami.klooni.actors.SoftButton;
import dev.lonami.klooni.adcache.GoogleInterstitialAdsPool;
import dev.lonami.klooni.adcache.InterstitialAdsManager;
import dev.lonami.klooni.game.Board;
import dev.lonami.klooni.screens.GameScreen;
import es.dmoral.toasty.Toasty;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
    public RelativeLayout layout;
    private static final String PRODUCT_ID = "com.vision.remove.ad";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs+M7RqVtF6DObnwCFBnW9Stb+RbL63M1zOb+UeFmbcfLSB+zy2oOUO0aiak3JIZHZ5Lr7Efg2kB2kxUEEYI8uvcf9CafMSLA/Rb6pB7yqVIK4MzsNFPbPQE/NL5shRqIRmN4PXdFGGo6UpgwbUyKjNDZKtGqZ8aqVeW5fOoBkbZPzpqpr9BA8kvU3+WLcazIDtjOWkHgI1kaH1/J+aXrm+andy9HB+EYS+z5lHmfjLaJx9AATxdsFFa9xa/GKCFDr8CBmhzey68KLELrRiDLgRxNcXsg5EbD5R54UyKPgb3u/Tua9hDnvIqJMkB6p+CSimhe7Cp0Ew1ZPj5PHGUTlQIDAQAB";
    private static final String MERCHANT_ID = "01548378342793731645";
    private Table table;
    private SoftButton softButton;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    private ReviewManager manager;
    private RewardedAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadRewardAd();
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!BillingProcessor.isIabServiceAvailable(this)) {
            Log.e("billing", "onCreate: serviceisAvailable");
        }
        bp = new BillingProcessor(this, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
//                Log.e("billing", "onProductPurchased: "+details.purchaseInfo.responseData.toString() );
                Klooni.setRemoveAd(true);
                table.removeActor(softButton);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                Log.e("billing", "onBillingError: " + errorCode);
                Bundle bundleEvent = new Bundle();
                bundleEvent.putString("billingerror", errorCode + "");
                FirebaseAnalytics.getInstance(AndroidLauncher.this).logEvent("billingerror", bundleEvent);
            }

            @Override
            public void onBillingInitialized() {
                readyToPurchase = true;
                Log.e("billing", "onBillingInitialized: ");
            }

            @Override
            public void onPurchaseHistoryRestored() {
                Log.e("billing", bp.listOwnedProducts().size() + "");
                for (String sku : bp.listOwnedProducts()) {
                    Log.e("billing", sku);
                }
                if (bp.isPurchased(PRODUCT_ID)) {
                    Klooni.setRemoveAd(true);
                } else
                    Klooni.setRemoveAd(false);
            }
        });
        layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final AndroidShareChallenge shareChallenge = new AndroidShareChallenge(this);
        Klooni game = new Klooni(shareChallenge, this);
        View gameView = initializeForView(game, config);
        InterstitialAdsManager.getInstance().init(this);
        RelativeLayout.LayoutParams gameViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        gameView.setLayoutParams(gameViewParams);
        setContentView(gameView);
    }

    @Override
    public void showInterstitial() {
        Log.e("billing", Klooni.getIsRemove() + "");
        if (!Klooni.getIsRemove())
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    GoogleInterstitialAdsPool.showAd("gameover");
                }
            });
    }

    @Override
    public void removeAd(Table table, SoftButton softButton) {
        this.table = table;
        this.softButton = softButton;
        Log.e("billing", "removeAd: ");
        if (readyToPurchase)
            bp.purchase(this, PRODUCT_ID);

    }

    @Override
    public boolean isAdAvaliable() {
        if (bp.isPurchased(PRODUCT_ID)) {
            Klooni.setRemoveAd(true);
            return false;
        } else {
            Klooni.setRemoveAd(false);
            return true;
        }
    }

    @Override
    public void inAppReview() {
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        if (Klooni.getInAppReview()) {
                            manager = ReviewManagerFactory.create(AndroidLauncher.this);
                            Task<ReviewInfo> request = manager.requestReviewFlow();
                            request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                                @Override
                                public void onComplete(Task<ReviewInfo> task) {
                                    if (task.isSuccessful()) {
                                        ReviewInfo reviewInfo = task.getResult();
                                        Task<Void> flow = manager.launchReviewFlow(AndroidLauncher.this, reviewInfo);
                                        Log.e("review", "onComplete: " + flow.isComplete());
                                        flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(Task<Void> task) {
                                                if (task != null && task.getResult() != null)
                                                    Log.e("inAppReview", "onComplete: " + task.getResult().toString());
                                            }
                                        });
                                    } else {
                                        Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.vision.elimination");
                                        Log.e("inAppReview", "onComplete: error");
                                    }
                                }
                            });
                        } else
                            Gdx.net.openURI("https://play.google.com/store/apps/details?id=com.vision.elimination");
                    }
                }
        );
    }

    @Override
    public void loadRewardAd() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRewardedVideoAd = new RewardedAd(AndroidLauncher.this,
//ca-app-pub-3241270777052923/7659770111
// test ca-app-pub-3940256099942544/5224354917
                        "ca-app-pub-3241270777052923/7659770111");
                loadRewardedVideoAd();
            }
        });
    }

    @Override
    public void showRewardAd(final SoftButton customButton, final Board board, final GameScreen gameScreen, final ChangeListener customChangeListener, final Klooni game, final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mRewardedVideoAd.isLoaded()) {
                    Activity activityContext = AndroidLauncher.this;
                    RewardedAdCallback adCallback = new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                            mRewardedVideoAd = new RewardedAd(AndroidLauncher.this,
                                    "ca-app-pub-3241270777052923/7659770111");
                            loadRewardedVideoAd();
                        }

                        @Override
                        public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
//                            customButton.updateImage("palette_texture");
//                            customButton.addListener(customChangeListener);
                            board.clearCompleteToRandom(game.effect);
                            gameScreen.gameOverDone = false;
                            gameScreen.holder.enabled = true;
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display.
                            Log.e("ads", "onRewardedAdFailedToShow: " + adError.toString());
                            gameScreen.doRealGameOver(reason);
                        }
                    };
                    mRewardedVideoAd.show(activityContext, adCallback);
                } else {
                    Log.d("TAG", "The rewarded ad wasn't loaded yet.");
                }
            }
        });
    }

    @Override
    public void showToast(String msg) {
        Toasty.error(this, msg, Toast.LENGTH_LONG, true).show();
    }

    private void loadRewardedVideoAd() {
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                Log.e("ads", "onRewardedAdFailedToLoad: " + adError.toString());
                showToast(adError.getMessage());
//                gameScreen.doRealGameOver(reason);

            }
        };
        mRewardedVideoAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG", "onStart: ");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("billing", "onResume: " + bp.isPurchased(PRODUCT_ID));
        if (bp.isPurchased(PRODUCT_ID)) {
            Klooni.setRemoveAd(true);
        } else
            Klooni.setRemoveAd(false);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();
        super.onDestroy();
    }


}
