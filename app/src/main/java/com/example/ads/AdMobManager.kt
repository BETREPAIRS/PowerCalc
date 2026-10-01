package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.Slate900
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Manages Google Mobile Ads (GMA) Next-Gen SDK integration with the user's official AdMob IDs.
 * Follows GMA Next-Gen background initialization rules and safeguards against startup delays,
 * splash screen freezes, and runtime crashes.
 */
object AdMobManager {
    private const val TAG = "AdMobManager"

    // Official Google AdMob Production IDs provided by user
    const val ADMOB_APP_ID = "ca-app-pub-9538078384787942~7993332162"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-9538078384787942/9450893229"
    const val INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9538078384787942/8736577356"
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-9538078384787942/9398747328"
    const val REWARDED_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9538078384787942/7754668171"

    private val _isSdkInitialized = MutableStateFlow(false)
    val isSdkInitialized: StateFlow<Boolean> = _isSdkInitialized.asStateFlow()

    // Interstitial Ad State
    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false
    private var lastInterstitialShowTime = 0L
    private const val INTERSTITIAL_COOLDOWN_MS = 30_000L // 30 seconds cooldown between interstitials

    // Rewarded Interstitial Ad State
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var isRewardedInterstitialLoading = false

    /**
     * Initializes GMA Next-Gen SDK on a background thread as required by current documentation.
     * Prevents any UI thread stalls or splash screen freezes.
     */
    fun initialize(context: Context) {
        if (_isSdkInitialized.value) return

        val appContext = context.applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestConfiguration = RequestConfiguration.Builder()
                    .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR))
                    .build()
                MobileAds.setRequestConfiguration(requestConfiguration)

                MobileAds.initialize(appContext) { status ->
                    Log.d(TAG, "GMA Next-Gen SDK background initialization completed: $status")
                    CoroutineScope(Dispatchers.Main).launch {
                        _isSdkInitialized.value = true
                        // Preload first interstitial ad once initialization finishes
                        loadInterstitial(appContext)
                    }
                }
            } catch (e: Throwable) {
                Log.e(TAG, "GMA Next-Gen SDK background initialization error", e)
                CoroutineScope(Dispatchers.Main).launch {
                    _isSdkInitialized.value = true
                }
            }
        }
    }

    /**
     * Preloads an interstitial ad using official Ad Unit ID.
     */
    fun loadInterstitial(context: Context) {
        if (!_isSdkInitialized.value || isInterstitialLoading || interstitialAd != null) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                InterstitialAd.load(
                    context,
                    INTERSTITIAL_AD_UNIT_ID,
                    adRequest,
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            Log.d(TAG, "Interstitial ad loaded successfully.")
                            interstitialAd = ad
                            isInterstitialLoading = false
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message} (code ${loadAdError.code})")
                            interstitialAd = null
                            isInterstitialLoading = false
                        }
                    }
                )
            } catch (e: Throwable) {
                Log.e(TAG, "Exception during InterstitialAd.load", e)
                interstitialAd = null
                isInterstitialLoading = false
            }
        }
    }

    /**
     * Displays the interstitial ad at natural transition points with cooldown protection.
     * If not available or on cooldown, immediately invokes [onDismissed] so app flow is uninterrupted.
     */
    fun showInterstitial(activity: Activity?, onDismissed: () -> Unit = {}) {
        val currentAd = interstitialAd
        val now = System.currentTimeMillis()

        if (activity == null || currentAd == null || (now - lastInterstitialShowTime < INTERSTITIAL_COOLDOWN_MS)) {
            // Cannot or should not show ad now; seamlessly proceed
            onDismissed()
            if (activity != null && interstitialAd == null && !isInterstitialLoading) {
                loadInterstitial(activity.applicationContext)
            }
            return
        }

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial ad dismissed.")
                interstitialAd = null
                lastInterstitialShowTime = System.currentTimeMillis()
                loadInterstitial(activity.applicationContext)
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                interstitialAd = null
                loadInterstitial(activity.applicationContext)
                onDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial ad showed full screen.")
            }
        }

        try {
            currentAd.show(activity)
        } catch (e: Throwable) {
            Log.e(TAG, "Error presenting interstitial ad", e)
            interstitialAd = null
            onDismissed()
        }
    }

    /**
     * Preloads a rewarded interstitial ad using official Ad Unit ID.
     */
    fun loadRewardedInterstitial(context: Context) {
        if (!_isSdkInitialized.value || isRewardedInterstitialLoading || rewardedInterstitialAd != null) return
        isRewardedInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                RewardedInterstitialAd.load(
                    context,
                    REWARDED_INTERSTITIAL_AD_UNIT_ID,
                    adRequest,
                    object : RewardedInterstitialAdLoadCallback() {
                        override fun onAdLoaded(ad: RewardedInterstitialAd) {
                            Log.d(TAG, "Rewarded interstitial ad loaded successfully.")
                            rewardedInterstitialAd = ad
                            isRewardedInterstitialLoading = false
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            Log.w(TAG, "Rewarded interstitial ad failed to load: ${loadAdError.message}")
                            rewardedInterstitialAd = null
                            isRewardedInterstitialLoading = false
                        }
                    }
                )
            } catch (e: Throwable) {
                Log.e(TAG, "Exception during RewardedInterstitialAd.load", e)
                rewardedInterstitialAd = null
                isRewardedInterstitialLoading = false
            }
        }
    }

    /**
     * Displays the rewarded interstitial ad if loaded.
     */
    fun showRewardedInterstitial(
        activity: Activity?,
        onUserEarnedReward: (RewardItem) -> Unit = {},
        onDismissed: () -> Unit = {}
    ) {
        val currentAd = rewardedInterstitialAd
        if (activity == null || currentAd == null) {
            onDismissed()
            if (activity != null && rewardedInterstitialAd == null && !isRewardedInterstitialLoading) {
                loadRewardedInterstitial(activity.applicationContext)
            }
            return
        }

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded interstitial ad dismissed.")
                rewardedInterstitialAd = null
                loadRewardedInterstitial(activity.applicationContext)
                onDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.w(TAG, "Rewarded interstitial ad failed to show: ${adError.message}")
                rewardedInterstitialAd = null
                loadRewardedInterstitial(activity.applicationContext)
                onDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded interstitial ad showed full screen.")
            }
        }

        try {
            currentAd.show(activity) { rewardItem ->
                onUserEarnedReward(rewardItem)
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Error presenting rewarded interstitial ad", e)
            rewardedInterstitialAd = null
            onDismissed()
        }
    }

    /**
     * Jetpack Compose Composable for displaying the AdMob Banner Ad.
     * Uses official Banner Ad Unit ID and auto-adjusts to wrap content without blocking the UI.
     */
    @Composable
    fun BannerAd(modifier: Modifier = Modifier) {
        val isReady by isSdkInitialized.collectAsState()
        var isAdLoaded by remember { mutableStateOf(false) }

        if (!isReady) {
            // Invisible until SDK is ready to prevent UI flicker
            return
        }

        // Clean container anchored above navigation bar
        Box(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(if (isAdLoaded) 50.dp else 0.dp),
                factory = { context ->
                    try {
                        AdView(context).apply {
                            layoutParams = FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT
                            ).apply {
                                gravity = android.view.Gravity.CENTER
                            }
                            // In headless/container/emulator environments without physical DRI GPU node,
                            // software rendering layer prevents MESA OpenGL rendernode driver errors
                            try {
                                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                            } catch (_: Throwable) {}

                            setAdSize(AdSize.BANNER)
                            adUnitId = BANNER_AD_UNIT_ID
                            adListener = object : AdListener() {
                                override fun onAdLoaded() {
                                    Log.d(TAG, "Official AdMob banner ad loaded successfully.")
                                    isAdLoaded = true
                                }

                                override fun onAdFailedToLoad(error: LoadAdError) {
                                    Log.w(TAG, "Official AdMob banner failed to load: ${error.message} (code ${error.code})")
                                    isAdLoaded = false
                                }
                            }
                            loadAd(AdRequest.Builder().build())
                        }
                    } catch (e: Throwable) {
                        Log.e(TAG, "Error initializing AdView in Compose", e)
                        isAdLoaded = false
                        View(context)
                    }
                }
            )
        }
    }
}
