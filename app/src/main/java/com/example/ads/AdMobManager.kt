package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

/** Owns the loaded full-screen ads. All IDs are Google's official test IDs. */
class AdMobManager(private val context: Context) {
    private var rewarded: RewardedAd? = null
    private var interstitial: InterstitialAd? = null

    val bannerId = "ca-app-pub-3940256099942544/6300978111"
    private val rewardedId = "ca-app-pub-3940256099942544/5224354917"
    private val interstitialId = "ca-app-pub-3940256099942544/1033173712"

    fun init() {
        MobileAds.initialize(context) {
            loadRewarded()
            loadInterstitial()
        }
    }

    fun loadRewarded() {
        RewardedAd.load(context, rewardedId, AdRequest.Builder().build(),
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) { rewarded = ad }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Rewarded ad failed to load: ${error.message}")
                }
            })
    }

    /** Calls [onReward] only after the user earns the reward. */
    fun showRewarded(activity: Activity, onReward: () -> Unit) {
        val ad = rewarded ?: run { loadRewarded(); return }
        rewarded = null
        ad.fullScreenContentCallback = reloadCallback { loadRewarded() }
        ad.show(activity) { onReward() }
    }

    fun loadInterstitial() {
        InterstitialAd.load(context, interstitialId, AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) { interstitial = ad }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.w(TAG, "Interstitial ad failed to load: ${error.message}")
                }
            })
    }

    fun showInterstitial(activity: Activity, onDismiss: () -> Unit = {}) {
        val ad = interstitial ?: run { loadInterstitial(); onDismiss(); return }
        interstitial = null
        ad.fullScreenContentCallback = reloadCallback {
            loadInterstitial()
            onDismiss()
        }
        ad.show(activity)
    }

    private fun reloadCallback(onClosed: () -> Unit) = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() = onClosed()
        override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
            Log.w(TAG, "Full-screen ad failed to show: ${error.message}")
            onClosed()
        }
    }

    private companion object { const val TAG = "TurboPurrCatAds" }
}
