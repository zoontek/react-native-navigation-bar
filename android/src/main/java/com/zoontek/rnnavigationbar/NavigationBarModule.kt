package com.zoontek.rnnavigationbar

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.TypedValue
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.common.ReactConstants
import com.facebook.react.interfaces.ExtraWindowEventListener
import com.facebook.react.module.annotations.ReactModule
import java.util.Collections
import java.util.WeakHashMap

// The light scrim color used in the platform API 29+
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/com/android/internal/policy/DecorView.java;drc=6ef0f022c333385dba2c294e35b8de544455bf19;l=142
internal val LightNavigationBarColor = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

// The dark scrim color used in the platform.
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/res/color/system_bar_background_semi_transparent.xml
// https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/res/remote_color_resources_res/values/colors.xml;l=67
internal val DarkNavigationBarColor = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

@Suppress("DEPRECATION")
internal fun Window.setNavigationBarStyle(light: Boolean, transparent: Boolean) {
  if (VERSION.SDK_INT < VERSION_CODES.O) {
    return // isAppearanceLightNavigationBars is not available below Android O
  }

  if (VERSION.SDK_INT >= VERSION_CODES.Q) {
    isNavigationBarContrastEnforced = !transparent
  } else {
    navigationBarColor =
      when {
        transparent -> Color.TRANSPARENT
        light -> LightNavigationBarColor
        else -> DarkNavigationBarColor
      }
  }

  WindowInsetsControllerCompat(this, decorView).apply {
    isAppearanceLightNavigationBars = light
  }
}

internal fun Window.setNavigationBarHidden(hidden: Boolean) {
  WindowInsetsControllerCompat(this, decorView).apply {
    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    when (hidden) {
      true -> hide(WindowInsetsCompat.Type.navigationBars())
      else -> show(WindowInsetsCompat.Type.navigationBars())
    }
  }
}

@ReactModule(name = NativeNavigationBarModuleSpec.NAME)
class NavigationBarModule(reactContext: ReactApplicationContext) :
  NativeNavigationBarModuleSpec(reactContext), ExtraWindowEventListener, LifecycleEventListener {

  init {
    reactApplicationContext.addExtraWindowEventListener(this)
    reactApplicationContext.addLifecycleEventListener(this)
  }

  override fun invalidate() {
    super.invalidate()
    reactApplicationContext.removeExtraWindowEventListener(this)
    reactApplicationContext.removeLifecycleEventListener(this)
  }

  private fun resolveBoolAttribute(activity: Activity, resId: Int): Boolean? {
    val value = TypedValue()

    return if (activity.theme.resolveAttribute(resId, value, true)) {
      value.data != 0
    } else {
      null
    }
  }

  private fun isLightMode(activity: Activity): Boolean =
    activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK !=
      Configuration.UI_MODE_NIGHT_YES

  private fun isTransparent(activity: Activity): Boolean =
    !(resolveBoolAttribute(activity, R.attr.enforceNavigationBarContrast) ?: false)

  override fun setStyle(style: String) {
    val activity =
      reactApplicationContext.currentActivity
        ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    val light = style == "dark-content" // dark-content = light background
    val transparent = isTransparent(activity)

    activity.runOnUiThread {
      activity.window.setNavigationBarStyle(light, transparent)
      extraWindows.forEach { it.setNavigationBarStyle(light, transparent) }
    }
  }

  override fun setHidden(hidden: Boolean) {
    val activity =
      reactApplicationContext.currentActivity
        ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    activity.runOnUiThread {
      activity.window.setNavigationBarHidden(hidden)
      extraWindows.forEach { it.setNavigationBarHidden(hidden) }
    }
  }

  override fun onExtraWindowCreate(window: Window) {
    extraWindows.add(window)

    val activity =
      reactApplicationContext.currentActivity
        ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    activity.window?.let { activityWindow ->
      val controller = WindowCompat.getInsetsController(activityWindow, activityWindow.decorView)
      val insets = ViewCompat.getRootWindowInsets(activityWindow.decorView)

      val light = controller.isAppearanceLightNavigationBars
      val transparent = isTransparent(activity)
      val visible = insets?.isVisible(WindowInsetsCompat.Type.navigationBars()) ?: true

      window.setNavigationBarStyle(light, transparent)
      window.setNavigationBarHidden(!visible)
    }
  }

  override fun onExtraWindowDestroy(window: Window) {
    extraWindows.remove(window)
  }

  override fun onHostResume() {
    val activity =
      reactApplicationContext.currentActivity
        ?: return FLog.w(ReactConstants.TAG, NO_ACTIVITY_ERROR)

    val windowLightNavigationBar =
      if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
        resolveBoolAttribute(activity, android.R.attr.windowLightNavigationBar)
      } else null

    setStyle(
      when (windowLightNavigationBar ?: isLightMode(activity)) {
        true -> "dark-content"
        false -> "light-content"
      }
    )

    reactApplicationContext.removeLifecycleEventListener(this)
  }

  override fun onHostPause() {}

  override fun onHostDestroy() {}

  companion object {
    private val extraWindows = Collections.newSetFromMap<Window>(WeakHashMap())

    private const val NO_ACTIVITY_ERROR =
      "${NAME}: Ignored navigation bar change, current activity is null."
  }
}
