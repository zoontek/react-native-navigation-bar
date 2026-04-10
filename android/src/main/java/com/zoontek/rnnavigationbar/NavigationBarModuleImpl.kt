package com.zoontek.rnnavigationbar

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.TypedValue

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import com.facebook.common.logging.FLog
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.ReactConstants

// The light scrim color used in the platform API 29+
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/com/android/internal/policy/DecorView.java;drc=6ef0f022c333385dba2c294e35b8de544455bf19;l=142
internal val LightNavigationBarColor = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

// The dark scrim color used in the platform.
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/res/color/system_bar_background_semi_transparent.xml
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/remote_color_resources_res/values/colors.xml;l=67
internal val DarkNavigationBarColor = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

object NavigationBarModuleImpl {
  const val NAME = "RNNavigationBar"

  private const val NO_ACTIVITY_ERROR =
    "$NAME: Ignored navigation bar change, current activity is null."

  private fun resolveBoolAttribute(activity: Activity, resId: Int): Boolean? {
    val value = TypedValue()

    return if (activity.theme.resolveAttribute(resId, value, true)) {
      value.data != 0
    } else {
      null
    }
  }

  private fun isLightMode(activity: Activity): Boolean =
    activity.resources.configuration.uiMode and
      Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES

  private fun getInitialStyle(activity: Activity): String {
    val windowLightNavigationBar = if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
      resolveBoolAttribute(activity, android.R.attr.windowLightNavigationBar)
    } else null

    return when (windowLightNavigationBar ?: isLightMode(activity)) {
      true -> "dark-content"
      false -> "light-content"
    }
  }

  private fun isTransparent(activity: Activity): Boolean =
    !(resolveBoolAttribute(activity, R.attr.enforceNavigationBarContrast) ?: false)

  @Suppress("DEPRECATION")
  fun setStyle(reactContext: ReactApplicationContext?, style: String) {
    val activity = reactContext?.currentActivity
      ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      activity.runOnUiThread {
        val light = style == "dark-content" // dark-content = light background
        val transparent = isTransparent(activity)
        val window = activity.window

        if (VERSION.SDK_INT >= VERSION_CODES.Q) {
          window.isNavigationBarContrastEnforced = !transparent
        }

        window.navigationBarColor = when {
          transparent -> Color.TRANSPARENT
          light -> LightNavigationBarColor
          else -> DarkNavigationBarColor
        }

        WindowInsetsControllerCompat(window, window.decorView).run {
          isAppearanceLightNavigationBars = light
        }
      }
    }
  }

  fun initialize(reactContext: ReactApplicationContext?) {
    val activity = reactContext?.currentActivity
      ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    setStyle(reactContext, getInitialStyle(activity))
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
