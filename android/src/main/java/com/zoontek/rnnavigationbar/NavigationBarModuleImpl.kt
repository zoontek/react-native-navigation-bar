package com.zoontek.rnnavigationbar

import android.app.Activity
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

    activity.runOnUiThread {
      val window = activity.window

      if (VERSION.SDK_INT >= VERSION_CODES.O) {
        val light = style == "dark-content" // dark-content = light background
        val transparent = isNavigationBarTransparent(activity)

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
