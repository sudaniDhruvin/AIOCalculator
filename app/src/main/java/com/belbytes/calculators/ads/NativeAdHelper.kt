package com.belbytes.calculators.ads

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.NonNull
import com.belbytes.calculators.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

object NativeAdHelper {
    private const val AD_UNIT_ID = "ca-app-pub-2192933586407526/3387898470" // Test Ad ID
    private var nativeAd: NativeAd? = null

    fun loadNativeAd(context: Context, adContainer: FrameLayout, shimmerView: ShimmerFrameLayout) {
        // Start shimmer effect and hide ad container initially
        shimmerView.startShimmer()
        shimmerView.visibility = View.VISIBLE
        adContainer.visibility = View.GONE

        val adLoader = AdLoader.Builder(context, AD_UNIT_ID)
            .forNativeAd { ad ->
                // Store the loaded ad
                nativeAd?.destroy() // Destroy old ad if exists to prevent memory leaks
                nativeAd = ad

                showLoadedAd(adContainer, shimmerView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdFailedToLoad(@NonNull loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    shimmerView.stopShimmer()
                    shimmerView.visibility = View.GONE
                    adContainer.visibility = View.GONE
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    // Function to display already loaded ad
    private fun showLoadedAd(adContainer: FrameLayout, shimmerView: ShimmerFrameLayout) {
        val ad = nativeAd ?: return

        // Hide shimmer and show ad
        shimmerView.stopShimmer()
        shimmerView.visibility = View.GONE
        adContainer.visibility = View.VISIBLE

        // Inflate and populate ad layout
        val inflater = LayoutInflater.from(adContainer.context)
        val adView = inflater.inflate(R.layout.native_ad_layout, null) as NativeAdView
        populateNativeAdView(ad, adView)

        // Remove old views and add new ad
        adContainer.removeAllViews()
        adContainer.addView(adView)
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Headline (Required)
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        adView.headlineView = headlineView
        headlineView?.text = nativeAd.headline

        // Body Text
        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        adView.bodyView = bodyView
        if (nativeAd.body != null) {
            bodyView?.text = nativeAd.body
            bodyView?.visibility = View.VISIBLE
        } else {
            bodyView?.visibility = View.GONE
        }

        // Call-to-Action Button
        val ctaButton = adView.findViewById<Button>(R.id.ad_call_to_action)
        adView.callToActionView = ctaButton
        if (nativeAd.callToAction != null) {
            ctaButton?.text = nativeAd.callToAction
            ctaButton?.visibility = View.VISIBLE
        } else {
            ctaButton?.visibility = View.GONE
        }

        // Ad Icon (App Logo)
        val adIcon = adView.findViewById<ImageView>(R.id.ad_app_icon)
        adView.iconView = adIcon
        if (nativeAd.icon != null) {
            adIcon?.setImageDrawable(nativeAd.icon?.drawable)
            adIcon?.visibility = View.VISIBLE
        } else {
            adIcon?.visibility = View.GONE
        }

        // Star Rating (Optional)
        val ratingBar = adView.findViewById<RatingBar>(R.id.ad_stars)
        adView.starRatingView = ratingBar
        if (nativeAd.starRating != null) {
            ratingBar?.rating = nativeAd.starRating!!.toFloat()
            ratingBar?.visibility = View.VISIBLE
        } else {
            ratingBar?.visibility = View.GONE
        }

        // Bind the native ad object to the view
        adView.setNativeAd(nativeAd)
    }
}

