package com.zoontek.rnnavigationbar

import android.app.Activity
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

import android.graphics.Color
import android.util.TypedValue

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import com.facebook.common.logging.FLog
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.ReactConstants

object NavigationBarModuleImpl {
  const val NAME = "RNNavigationBar"

  private const val NO_ACTIVITY_ERROR = "$NAME: Ignored navigation bar change, current activity is null."
  private val boolAttributes = mutableMapOf<Int, Boolean>()

  private fun resolveBoolAttribute(activity: Activity, resId: Int): Boolean =
    boolAttributes.getOrPut(resId) {
      val value = TypedValue()
      activity.theme.resolveAttribute(resId, value, true) && value.data != 0
    }

  private fun isNavigationBarTransparent(activity: Activity): Boolean =
    !resolveBoolAttribute(activity, R.attr.enforceNavigationBarContrast)

  @Suppress("DEPRECATION")
  fun setStyle(reactContext: ReactApplicationContext?, style: String) {
    val activity = reactContext?.currentActivity
      ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      activity.runOnUiThread {
        val window = activity.window

        if (VERSION.SDK_INT >= VERSION_CODES.Q && isNavigationBarTransparent(activity)) {
          window.isNavigationBarContrastEnforced = false
        }

        window.navigationBarColor = Color.TRANSPARENT

        WindowInsetsControllerCompat(window, window.decorView).run {
          isAppearanceLightNavigationBars = style == "dark-content"
        }
      }
    }
  }

  // copy StatusBar behavior (default is light-content)
  // https://github.com/facebook/react-native/blob/v0.81.1/packages/react-native/ReactAndroid/src/main/java/com/facebook/react/modules/statusbar/StatusBarModule.kt#L131
  fun initialize(reactContext: ReactApplicationContext?) {
    setStyle(reactContext, "light-content")
  }

  fun setHidden(reactContext: ReactApplicationContext?, hidden: Boolean) {
    val activity = reactContext?.currentActivity
      ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    activity.runOnUiThread {
      val window = activity.window

      WindowInsetsControllerCompat(window, window.decorView).run {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        when (hidden) {
          true -> hide(WindowInsetsCompat.Type.navigationBars())
          else -> show(WindowInsetsCompat.Type.navigationBars())
        }
      }
    }
  }
}
