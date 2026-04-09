package com.zoontek.rnnavigationbar

import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = NavigationBarModuleImpl.NAME)
class NavigationBarModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), LifecycleEventListener {

  init {
    reactApplicationContext.addLifecycleEventListener(this)
  }

  override fun invalidate() {
    reactApplicationContext.removeLifecycleEventListener(this)
  }

  override fun getName(): String {
    return NavigationBarModuleImpl.NAME
  }

  override fun onHostResume() {
    NavigationBarModuleImpl.initialize(reactApplicationContext)
    reactApplicationContext.removeLifecycleEventListener(this)
  }

  override fun onHostPause() {}

  override fun onHostDestroy() {}

  @ReactMethod
  fun setStyle(style: String) {
    NavigationBarModuleImpl.setStyle(reactApplicationContext, style)
  }

  @ReactMethod
  fun setHidden(hidden: Boolean) {
    NavigationBarModuleImpl.setHidden(reactApplicationContext, hidden)
  }
}
