package com.zoontek.rnnavigationbar

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = NavigationBarModuleImpl.NAME)
class NavigationBarModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  init {
    NavigationBarModuleImpl.initialize(reactApplicationContext)
  }

  override fun getName(): String {
    return NavigationBarModuleImpl.NAME
  }

  @ReactMethod
  fun setStyle(style: String) {
    NavigationBarModuleImpl.setStyle(reactApplicationContext, style)
  }

  @ReactMethod
  fun setHidden(hidden: Boolean) {
    NavigationBarModuleImpl.setHidden(reactApplicationContext, hidden)
  }
}
