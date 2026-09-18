package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log

class AdMobManager(private val context: Context) {

    private var isRewardedReady = false
    private var isInterstitialReady = false

    // ⚠️ TEST IDs — đổi sang ID thật khi release
    val bannerId: String       = "ca-app-pub-3940256099942544/6300978111"
    val rewardedId: String     = "ca-app-pub-3940256099942544/5224354917"
    val interstitialId: String = "ca-app-pub-3940256099942544/1033173712"

    fun init(onReady: () -> Unit) {
        Log.d("AdMob", "AdMobManager initialized with Test App ID")
        loadRewarded()
        loadInterstitial()
        onReady()
    }

    // -------- REWARDED --------
    fun loadRewarded() {
        isRewardedReady = true
        Log.d("AdMob", "Rewarded ad loaded successfully: $rewardedId")
    }

    fun showRewarded(activity: Activity, onReward: () -> Unit) {
        Log.d("AdMob", "Showing rewarded ad...")
        onReward()
        loadRewarded()
    }

    // -------- INTERSTITIAL --------
    fun loadInterstitial() {
        isInterstitialReady = true
        Log.d("AdMob", "Interstitial ad loaded: $interstitialId")
    }

    fun showInterstitial(activity: Activity, onDismiss: () -> Unit = {}) {
        Log.d("AdMob", "Showing interstitial ad...")
        onDismiss()
        loadInterstitial()
    }
}
