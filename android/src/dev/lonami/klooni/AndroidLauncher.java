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

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.review.testing.FakeReviewManager;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;

import java.lang.reflect.Method;
import java.util.Arrays;

import dev.lonami.klooni.actors.SoftButton;
import dev.lonami.klooni.adcache.GoogleInterstitialAdsPool;
import dev.lonami.klooni.adcache.InterstitialAdsManager;

public class AndroidLauncher extends AndroidApplication implements IActivityRequestHandler {
    private InterstitialAd mInterstitialAd;
    public RelativeLayout layout;
    private static final String PRODUCT_ID = "com.vision.remove.ad";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs+M7RqVtF6DObnwCFBnW9Stb+RbL63M1zOb+UeFmbcfLSB+zy2oOUO0aiak3JIZHZ5Lr7Efg2kB2kxUEEYI8uvcf9CafMSLA/Rb6pB7yqVIK4MzsNFPbPQE/NL5shRqIRmN4PXdFGGo6UpgwbUyKjNDZKtGqZ8aqVeW5fOoBkbZPzpqpr9BA8kvU3+WLcazIDtjOWkHgI1kaH1/J+aXrm+andy9HB+EYS+z5lHmfjLaJx9AATxdsFFa9xa/GKCFDr8CBmhzey68KLELrRiDLgRxNcXsg5EbD5R54UyKPgb3u/Tua9hDnvIqJMkB6p+CSimhe7Cp0Ew1ZPj5PHGUTlQIDAQAB";
    private static final String MERCHANT_ID = "01548378342793731645";
    private Table table;
    private SoftButton softButton;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;
    View gameView;
    private AdView adView;
    AdRequest adRequest;
    Klooni game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // https://stackoverflow.com/a/42437379/
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
                game.setRemoveAd(true);
                table.removeActor(softButton);
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                Log.e("billing", "onBillingError: " + errorCode);
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
                    game.setRemoveAd(true);
                } else
                    game.setRemoveAd(false);
            }
        });

        layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        final AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        final AndroidShareChallenge shareChallenge = new AndroidShareChallenge(this);
        game = new Klooni(shareChallenge, this);
        gameView = initializeForView(game, config);
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("E189A701CB7ADBE9C09BCB9754032F2A"));
        InterstitialAdsManager.getInstance().init(this);
        RelativeLayout.LayoutParams gameViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        gameView.setLayoutParams(gameViewParams);
        setContentView(gameView);
    }

    @Override
    public void showInterstitial() {
        Log.e("billing", game.getIsRemove() + "");
        if (!game.getIsRemove())
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                        loadInterstitial();
//                        Log.e("TAG", "The interstitial wasn't loaded yet.");
//                    }
                    GoogleInterstitialAdsPool.showAd("gameover");
                }
            });
    }

    @Override
    public void removeAd(Table table, SoftButton softButton) {
        this.table = table;
        this.softButton = softButton;
        if (readyToPurchase)
            bp.purchase(this, PRODUCT_ID);

    }

    @Override
    public boolean isAdAvaliable() {
        if (bp.isPurchased(PRODUCT_ID)) {
            game.setRemoveAd(true);
            return false;
        } else {
            game.setRemoveAd(false);
            return true;
        }
    }

    @Override
    public void inAppReview() {
//        final ReviewManager manager = new FakeReviewManager(this);
        final ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
            @Override
            public void onComplete(Task<ReviewInfo> task) {
                if (task.isSuccessful()) {
                    ReviewInfo reviewInfo = task.getResult();
                    Task<Void> flow = manager.launchReviewFlow(AndroidLauncher.this, reviewInfo);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG", "onStart: ");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bp.isPurchased(PRODUCT_ID)) {
            game.setRemoveAd(true);
        } else
            game.setRemoveAd(false);
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
