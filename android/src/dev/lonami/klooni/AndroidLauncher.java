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
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.PlayersClient;
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
    private View gameView;
    private Klooni game;
    private ReviewManager manager;
    private GoogleSignInClient mGoogleSignInClient;
    private LeaderboardsClient mLeaderboardsClient;
    private EventsClient mEventsClient;
    private PlayersClient mPlayersClient;
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
    private String mDisplayName = "";
    private final AccomplishmentsOutbox mOutbox = new AccomplishmentsOutbox();

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
        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
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
        Bundle bundleEvent = new Bundle();
        bundleEvent.putString("msg", "clickAdRemove");
        FirebaseAnalytics.getInstance(this).logEvent("adRemove", bundleEvent);
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
                        manager = ReviewManagerFactory.create(AndroidLauncher.this);
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
                }
        );
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

    private class AccomplishmentsOutbox {
        boolean m1Achievement = false;
        boolean m2Achievement = false;
        boolean m3Achievement = false;
        boolean m4Achievement = false;
        boolean m5Achievement = false;
        int mBoredSteps = 0;
        int mEasyModeScore = -1;

        boolean isEmpty() {
            return !m1Achievement && !m2Achievement && !m3Achievement &&
                    !m4Achievement && m5Achievement && mBoredSteps == 0 && mEasyModeScore < 0;
        }

    }
}
