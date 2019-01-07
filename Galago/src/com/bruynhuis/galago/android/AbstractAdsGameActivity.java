/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bruynhuis.galago.android;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import java.util.Properties;
import com.bruynhuis.galago.app.BaseApplication;
import com.google.android.gms.ads.*;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * This class the AbstractAdsGameActivity should be extended when ever you
 * choose to use the GalagoLibrary with android and admob ads. This class will
 * give your game features admob support
 *
 * @author NideBruyn
 */
public abstract class AbstractAdsGameActivity extends AbstractGameActivity {

    protected RelativeLayout addViewLayout;
    protected AdView adView;
    protected InterstitialAd interstitialAd;
    protected RewardedVideoAd rewardedVideoAd;
    protected GoogleAnalytics analytics;
    protected Tracker tracker;

    /*
     * You have access to the properties and they can be specified in preload();
     */
    protected String ADMOB_ID = "";
    protected String ADMOB_INTERSTITIALS_ID = "";
    protected String ADMOB_REWARDS_ID = "";
    protected String ANALYTICS_TRACKER_ID = "";
    protected boolean useAdmob = false;
    protected boolean useAdmobInterstitials = false;
    protected boolean useAdmobRewards = false;
    protected boolean admobBannerBottom = true;
    protected boolean useAnalytics = false;

    /**
     * This method will send an analytics action to play services.
     *
     * @param prprts
     */
    public void doAnalyticsAction(Properties prprts) {
        if (analytics != null && tracker != null) {
            String catagoryStr = prprts.getProperty(BaseApplication.ANALYTICS_CATAGORY);
            String actionStr = prprts.getProperty(BaseApplication.ANALYTICS_ACTION);
            String labelStr = prprts.getProperty(BaseApplication.ANALYTICS_LABEL);

            if (catagoryStr != null && actionStr != null && labelStr != null) {

                tracker.send(new HitBuilders.EventBuilder(catagoryStr, actionStr)
                        .setLabel(labelStr)
                        .build());

            }
        }
    }

    /**
     * Called from the jME side when some action must be performed.
     *
     * @param prprts
     * @return
     */
    public String doAction(final Properties prprts) {
        super.doAction(prprts);        
        
        if (prprts != null) {

            String action = prprts.getProperty(BaseApplication.ACTION);
            if (action != null) {
                if (action.equals(BaseApplication.ACTION_AD)) {
                    doAdd(prprts);

                } else if (action.equals(BaseApplication.ACTION_INIT_ADS)) {
                    doInitAds();

                } else if (action.equals(BaseApplication.ACTION_ANALYTICS)) {
                    doAnalyticsAction(prprts);

                }

            }
        }

        return null;
    }

    /**
     * Show an add on the screen.
     *
     * @param prprts
     */
    protected void doAdd(Properties prprts) {
        if (prprts != null) {
            String type = prprts.getProperty(BaseApplication.TYPE);
            String showStr = prprts.getProperty(BaseApplication.SHOW);
            boolean show = Boolean.parseBoolean(showStr);

            if (BaseApplication.ADMOB.equals(type)) {
                if (show) {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (adView != null) {
                                adView.setVisibility(adView.VISIBLE);

                            }
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (adView != null) {
                                adView.setVisibility(adView.GONE);
                            }
                        }
                    });
                }
            }

            if (BaseApplication.ADMOB_INTERSTITIALS.equals(type)) {
                if (show) {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (interstitialAd != null) {
                                if (interstitialAd.isLoaded()) {
                                    interstitialAd.show();
                                }
                            }
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (interstitialAd != null) {
                                if (interstitialAd.isLoaded()) {
//                                    interstitialAd.show();
                                }
                            }
                        }
                    });
                }
            }

            if (BaseApplication.ADMOB_REWARDS.equals(type)) {
                if (show) {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (rewardedVideoAd != null) {
                                if (rewardedVideoAd.isLoaded()) {
                                    rewardedVideoAd.show();
                                }
                            }
                        }
                    });
                } else {
                    this.runOnUiThread(new Runnable() {
                        public void run() {
                            if (rewardedVideoAd != null) {
                                if (rewardedVideoAd.isLoaded()) {
//                                    rewardedVideoAd.show();
                                }
                            }
                        }
                    });
                }
            }

        } else {
            this.runOnUiThread(new Runnable() {
                public void run() {
                    if (adView != null) {
                        adView.setVisibility(adView.GONE);
                    }
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (useAnalytics) {
            initAnalytics();
        }

    }

    protected void initAnalytics() {

        if (analytics == null) {
            analytics = GoogleAnalytics.getInstance(this);
        }

        if (tracker == null) {
            tracker = analytics.newTracker(ANALYTICS_TRACKER_ID);
            tracker.enableExceptionReporting(true);
            tracker.enableAdvertisingIdCollection(true);
            tracker.enableAutoActivityTracking(true);

        }

    }

    /**
     * Called after everything is loaded and we are running the app.
     */
    protected void doInitAds() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                if (useAdmob) {
                    initAdMobAds();
                }

                if (useAdmobInterstitials) {
                    initAdMobInterstitialsAds();
                }

                if (useAdmobRewards) {
                    initAdMobRewardsAds();
                }

            }
        });

    }

    protected void initAdMobInterstitialsAds() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(ADMOB_INTERSTITIALS_ID);

        // Set the AdListener.
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
//                showAlert("AdLoadded");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
//                String message = String.format("onAdFailedToLoad (%s)", getInterstitialsAdErrorReason(errorCode));
//                showAlert(message);
            }

            @Override
            public void onAdClosed() {
                //Load a new ad with the request
                AdRequest adRequest = new AdRequest.Builder().build();
                interstitialAd.loadAd(adRequest);
//                showAlert("Load the next admob ad");
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
//        adView.loadAd(adRequest);

        interstitialAd.loadAd(adRequest);
    }

    /**
     * This will initialize the admob service.
     */
    protected void initAdMobAds() {

        /*
         adView = new AdView(this, AdSize.IAB_BANNER, ADMOB_ID);
         */
        addViewLayout = new RelativeLayout(this);

        adView = new AdView(this);
        adView.setAdUnitId(ADMOB_ID);
        adView.setAdSize(AdSize.SMART_BANNER);

        adView.setAdListener(new AdListener() {
            public void onAdLoaded() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (adView != null) {
                            adView.setVisibility(adView.GONE);
                            adView.bringToFront();

                        }
                    }
                });

            }
        });

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.addContentView(addViewLayout, params);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        addViewLayout.addView(adView);
        if (admobBannerBottom) {
            addViewLayout.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        } else {
            addViewLayout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        }

        addViewLayout.bringToFront();
        adView.bringToFront();

    }

    protected void initAdMobRewardsAds() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).setRewardAdLoaded(true);
                    ((BaseApplication) getJmeApplication()).fireRewardAdLoadedListener();
                }

            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).fireRewardAdClosedListener();
                }
                loadAdmobRewardsAd();

            }

            @Override
            public void onRewarded(RewardItem ri) {
                //Call back to the game and fire the reward ad listener
                if (getJmeApplication() != null) {
                    ((BaseApplication) getJmeApplication()).fireRewardAdRewardListener(ri.getAmount(), ri.getType());
                }

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                //TODO: Call back to the game and fire the reward ad listener

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
//                String message = String.format("onRewardAdFailedToLoad (%s)", getInterstitialsAdErrorReason(errorCode));
//                showAlert(message);
                ((BaseApplication) getJmeApplication()).setRewardAdLoaded(false);
            }
        });

        loadAdmobRewardsAd();

    }

    protected void loadAdmobRewardsAd() {
        if (rewardedVideoAd != null && !rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(ADMOB_REWARDS_ID, new AdRequest.Builder().build());
        }
    }

    @Override
    protected void onDestroy() {

        if (adView != null) {
            adView.destroy();
        }

        super.onDestroy();

    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }

        super.onPause();

    }

    @Override
    protected void onResume() {

        if (adView != null) {
            adView.resume();
        }

        super.onResume();

    }

    /**
     * Gets a string error reason from an error code.
     */
    private String getInterstitialsAdErrorReason(int errorCode) {
        String errorReason = "";
        switch (errorCode) {
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                errorReason = "Internal error";
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
                errorReason = "Invalid request";
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                errorReason = "Network Error";
                break;
            case AdRequest.ERROR_CODE_NO_FILL:
                errorReason = "No fill";
                break;
        }
        return errorReason;
    }

}
